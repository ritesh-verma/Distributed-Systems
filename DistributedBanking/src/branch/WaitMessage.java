package branch;

import thrift.TransferMessage;

public class WaitMessage {
    private int messageId;
    private TransferMessage message;

    public WaitMessage(int messageId, TransferMessage message) {
        this.messageId = messageId;
        this.message = message;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public TransferMessage getMessage() {
        return message;
    }

    public void setMessage(TransferMessage message) {
        this.message = message;
    }
}
