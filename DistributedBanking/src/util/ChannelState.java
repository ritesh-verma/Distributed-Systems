package util;

public class ChannelState {
    // if amount = -1, channel is empty
    private volatile int amount;

    public ChannelState(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
