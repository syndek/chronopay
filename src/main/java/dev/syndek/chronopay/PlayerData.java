package dev.syndek.chronopay;

public class PlayerData {
    private int   onlineTime;
    private float payedMoney;

    public int getOnlineTime() {
        return onlineTime;
    }

    public void incrementOnlineTime() {
        this.onlineTime++;
    }

    public float getPayedMoney() {
        return payedMoney;
    }

    public void addPayedMoney(final float payedMoney) {
        this.payedMoney += payedMoney;
    }

    public void resetPayedMoney() {
        this.payedMoney = 0;
    }
}