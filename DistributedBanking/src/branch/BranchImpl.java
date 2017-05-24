package branch;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import thrift.*;
import util.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Thread.sleep;

public class BranchImpl implements Branch.Iface, Runnable {

    private volatile int balance;
    private int initialBalance;
    private String branchName;
    private String IP;
    private int port;
    private boolean isInitialized = false;
    private boolean isInitiator = false;
    private final int EMPTY = -1;
    private static int startRecMessageId;

    private List<BranchID> allBranches;
    private Map<String, Branch.Client> connectionsMap;
    private static volatile Map<String, Integer> sendMessageId; // contains all id generated when money is sent
    private Map<String, Integer> recvMessageId;    // contains all message id when money is received
    private Map<String, List> waitingMessageMap;
    private Map<BranchID, ChannelState> channelStateMap;    // contains channels of a branch
    private static volatile Map<BranchID, LocalState> localStateMap = new Hashtable<>();    // contains local state of a branch
    private static volatile Map<Integer, LocalState> snapshotMap = new Hashtable<>();
    private static volatile Map<String, Integer> recordMessageId = new Hashtable<>();
    private List<Integer> channelMessageList;   // stores list of amounts when recording starts

    public BranchImpl(String nameIn, String IPIn, int portIn) {
        branchName = nameIn;
        IP = IPIn;
        port = portIn;
    }

    @Override
    public void initBranch(int balance, List<BranchID> all_branches) throws SystemException, TException {
        if (balance > 0) {
            initialBalance = balance;
            // TODO: 10/29/16 initialize balance
            updateBalance(balance);
        }

        allBranches = all_branches;
        connectionsMap = new HashMap<>();
        sendMessageId = new HashMap<>();
        waitingMessageMap = new HashMap<>();
        channelStateMap = new Hashtable<>();
        channelMessageList = new ArrayList<>();

        for (BranchID branchID : allBranches) {
            if (!branchID.getName().equalsIgnoreCase(branchName)) {
                connectionsMap.put(branchID.getName(), connectToBranch(branchID));
            }
        }
        isInitialized = true;
        System.out.println("Initialized");
    }

    @Override
    public void transferMoney(TransferMessage message, int messageId) throws SystemException, TException {
        BranchID branchID = message.getOrig_branchId();
        int receivedAmount = message.getAmount();
        int previousMessageId = recvMessageId.get(branchID.getName());
        List waitQueueList = null;

        // If recording is turned on, messages are in channel
        if (messageId > recordMessageId.get(this.branchName)) {
            // put messages to a list
            channelMessageList.add(message.getAmount());
        }

        if (messageId > (previousMessageId + 1)) {
            if (waitingMessageMap.containsKey(branchID.getName())) {
                waitQueueList = waitingMessageMap.get(branchID.getName());
                waitingMessageMap.put(branchID.getName(), putWaitMessageToList(waitQueueList, new WaitMessage(messageId, message)));
            } else {
                waitQueueList = new ArrayList();
                waitingMessageMap.put(branchID.getName(), putWaitMessageToList(waitQueueList, new WaitMessage(messageId, message)));
            }
        } else {
            if (receivedAmount >= 0) {
                synchronized(this) {
                    updateBalance(receivedAmount, recvMessageId, branchID.getName(), messageId);
                    previousMessageId = messageId;
                }
            }

            // run until all money is transferred
            for (int i = 0; i < waitQueueList.size(); i++) {
                waitQueueList = waitingMessageMap.get(branchID.getName());
                WaitMessage waitMessage = getFirstWaitMessage(waitQueueList);
                if (waitMessage.getMessageId() == (previousMessageId + 1)) {
                    synchronized (this) {
                        updateBalance(receivedAmount, recvMessageId, branchID.getName(), messageId);
                        previousMessageId = waitMessage.getMessageId();
                    }
                }
            }
        }
    }

    private WaitMessage getFirstWaitMessage(List waitQueueList) {
        int min = Integer.MAX_VALUE;
        WaitMessage message = null;
        int min_index = -1;

        for (int i = 0; i < waitQueueList.size(); i++) {
            WaitMessage waitMessage = (WaitMessage) waitQueueList.get(i);
            if (waitMessage.getMessageId() < min) {
                min = waitMessage.getMessageId();
                message = waitMessage;
                min_index = i;
            }
        }
        waitQueueList.remove(min_index);
        return message;
    }

    private List putWaitMessageToList(List waitQueueList, WaitMessage waitMessage) {
        waitQueueList.add(waitMessage);
        return waitQueueList;
    }

    @Override
    public void initSnapshot(int snapshotId) throws SystemException, TException {
        // record own local state
        // call Marker method on all other branches

        System.out.println(this.balance);
        this.isInitiator = true;

        BranchID thisBranchId = getCurrentBranchId();

        LocalState localState = new LocalState(this.balance);
        localStateMap.put(thisBranchId, localState);

        for (int i = 0; i < allBranches.size(); i++) {
            // Check if the same branch is pulled from the list
            BranchID branchID = allBranches.get(i);
            if (!branchID.getName().equalsIgnoreCase(this.branchName)) {
                // call Marker method
                Branch.Client client = connectionsMap.get(branchID.getName());
                client.Marker(thisBranchId, snapshotId, generateMessageID(branchID));
            }
        }
    }

    @Override
    public void Marker(BranchID branchId, int snapshotId, int messageId) throws SystemException, TException {
        // first marker message with snapshotId
        // set channel state to empty
        if (!snapshotMap.containsKey(snapshotId)) { // checks if this is the first marker message for corresponding snapshotId

            //localStateMap.put(getCurrentBranchId(), new LocalState(this.balance));

            snapshotMap.put(snapshotId, new LocalState(this.balance));

            ChannelState channelState = new ChannelState(EMPTY);
            channelStateMap.put(branchId, channelState);
            recordMessageId.put(this.branchName, messageId);
            // start recording for other channels
            for (int i = 0; i < allBranches.size(); i++) {
                BranchID bID = allBranches.get(i);
                // Start recording on other channels
                if (!bID.equals(branchId) || !bID.getName().equalsIgnoreCase(this.branchName)) {
                    channelStateMap.put(bID, null);     // null implies empty channel
                }
            }

            BranchID thisBranchId = null;

            // send marker message to all other channels
            for (int i = 0; i < allBranches.size(); i++) {
                BranchID bID = allBranches.get(i);
                if (bID.getName().equalsIgnoreCase(this.branchName)) {
                    thisBranchId = bID;
                }
            }

            for (int i = 0; i < allBranches.size(); i++) {
                BranchID branchID = allBranches.get(i);
                if (!branchID.getName().equalsIgnoreCase(this.branchName)) {
                    Branch.Client client = connectionsMap.get(branchID.getName());
                    client.Marker(thisBranchId, snapshotId, messageId);
                }
            }
        } else {
            // turn off recording for the channel

        }
    }

    @Override
    public LocalSnapshot retrieveSnapshot(int snapshotId) throws SystemException, TException {
        return new LocalSnapshot(snapshotId, this.balance, channelMessageList);
    }

    @Override
    // runs for infinite loop
    // decrement self balance before sending money
    // send money to other branch
    // wait for 0-5 sec before sending money again
    public void run() {
        while (isInitialized) {
            int amount = generateAmount();
            BranchID branchID = getRandomBranch(allBranches);
            Branch.Client client;

            TransferMessage transferMessage = new TransferMessage();
            try {
                updateBalance((-1) * amount);   // update new balance
                transferMessage.setOrig_branchId(getCurrentBranchId());
                transferMessage.setAmount(amount);
                client = connectionsMap.get(branchID.getName());
                System.out.println("Sending money to " + branchID.getName());
                client.transferMoney(transferMessage, generateMessageID(branchID));
                sleep(1000);
            } catch (InterruptedException | TException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private synchronized int generateMessageID(BranchID branchID) {
        int messageId;

        if (sendMessageId.containsKey(branchID.getName())) {
            messageId = sendMessageId.get(branchID.getName()) + 1;
            sendMessageId.put(branchID.getName(), messageId);
        } else {
            messageId = 1;
            sendMessageId.put(branchID.getName(), messageId);
        }

        return messageId;
    }

    /**
     * Get a random branch from the list of all branches
     * @param allBranches List of all branches
     * @return BranchID from the list
     */
    private BranchID getRandomBranch(List<BranchID> allBranches) {
        BranchID branchID = allBranches.get((int) (generateRandomValue(0, allBranches.size() - 1)));

        while (branchName.equalsIgnoreCase(branchID.getName())) {   // if the current branch is returned, get other from list
            branchID = allBranches.get((int) (generateRandomValue(0, allBranches.size() - 1)));
        }

        return branchID;
    }

    /**
     * Updates the balance of the branch
     * @param value the amount to be added to the current balance
     */
    private synchronized boolean updateBalance(int value, Map map, String name, int messageId) {
        /* Check if the updated balance is non-negative */
        if ((this.balance + value) >= 0) {
            this.balance += value;
            map.put(name, messageId);
            return true;
        }
        return false;
    }

    private synchronized boolean updateBalance(int value) {
        if ((this.balance + value) >= 0) {
            this.balance += value;
            return true;
        }
        return false;
    }

    /**
     * Generates a random number between two given numbers
     * @param min Min number
     * @param max Max number
     * @return random generated number
     */
    private double generateRandomValue(int min, int max) {
        return ThreadLocalRandom.current().nextDouble(min, max - 1);
    }

    private int generateAmount() {
        return (int) (initialBalance * generateRandomValue(1, 5));
    }

    private Branch.Client connectToBranch(BranchID branchID) {
        TTransport tTransport;
        Branch.Client clientBranch = null;

        try {
            tTransport = new TSocket("127.0.0.1", branchID.getPort());
            tTransport.open();

            TProtocol protocol = new TBinaryProtocol(tTransport);
            clientBranch = new Branch.Client(protocol);

        } catch (TException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return clientBranch;
    }

    /**
     * Returns the BranchID of the current branch
     * @return current BranchID
     */
    public BranchID getCurrentBranchId() {
        BranchID branchID = null;

        for (int i = 0; i < allBranches.size(); i++) {
            BranchID bID = allBranches.get(i);
            if (bID.getName().equalsIgnoreCase(this.branchName)) {
                branchID = bID;
            }
        }

        return branchID;
    }
}
