package com.weiyin.qinplus.ui.tv.bwstaff;

/**
 * @author njf
 * @date 2017/3/2
 */

public class KeyBoardModule {
    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        this.mNumber = number;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public int getVelocity() {
        return velocity;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    private int mNumber;
    private int mStatus;
    private int velocity;
    private int channel;

    public KeyBoardModule(int number, int status, int velocity, int channel) {
        this.mNumber = number;
        this.mStatus = status;
        this.channel = channel;
        this.velocity = velocity;
    }
}
