package controller;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import thrift.Branch;
import thrift.BranchID;
import util.FileProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Controller {
    private static List<BranchID> allBranches;
    private static Map<BranchID, Branch.Client> connectionsMap;
    private static int snapshotId;


    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Please enter two arguments.");
            System.out.println("Usage: ./controller <total_amount> <input_file_name>");
            System.exit(1);
        }

        int totalAmount;
        FileProcessor fp;

        try {
            totalAmount = Integer.parseInt(args[0]);
            allBranches = new ArrayList<>();
            connectionsMap = new HashMap<>();
            fp = new FileProcessor(args[1]);
            generateBranchList(fp);

            initializeBranches(totalAmount);
        } catch (NumberFormatException | IOException | TException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {

        }

        // start snapshot
        while (true) {
            int i = 1;
            BranchID branchID = getRandomBranch(allBranches);
            setSnapshotId(i++);
            try {
                initializeSnapshot(branchID, getSnapshotId());
            } catch (TException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void initializeSnapshot(BranchID branchID, int snapshotId) throws TException {
        Branch.Client client = connectionsMap.get(branchID);
        client.initSnapshot(snapshotId);
        setSnapshotId(getSnapshotId() + 1);
    }

    private static void initializeBranches(int totalAmount) throws TException {
        TTransport tTransport;
        Branch.Client clientBranch;
        BranchID branchID;
        int amount = totalAmount / allBranches.size();

        for (int i = 0; i < allBranches.size(); i++) {
            branchID = allBranches.get(i);
            tTransport = new TSocket(branchID.getIp(), branchID.getPort()); // branchID.getIp()
            tTransport.open();

            TProtocol protocol = new TBinaryProtocol(tTransport);
            clientBranch = new Branch.Client(protocol);

            clientBranch.initBranch(amount, allBranches);
            connectionsMap.put(branchID, clientBranch);
        }
    }

    private static void generateBranchList(FileProcessor fp) throws NumberFormatException {
        String line;
        BranchID branchID;
        while ((line = fp.readLineFromFile()) != null) {
            System.out.println(line);
            String[] tokens = line.split(" ");
            branchID = new BranchID(tokens[0], tokens[1], Integer.parseInt(tokens[2]));
            allBranches.add(branchID);
        }
    }

    private static BranchID getRandomBranch(List<BranchID> allBranches) {
        return allBranches.get((int) generateRandomValue(0, allBranches.size() - 1));
    }

    private static double generateRandomValue(int min, int max) {
        return ThreadLocalRandom.current().nextDouble(min, max - 1);
    }

    private static int getSnapshotId() {
        return snapshotId;
    }

    private static void setSnapshotId(int snapshotId) {
        Controller.snapshotId = snapshotId;
    }

}
