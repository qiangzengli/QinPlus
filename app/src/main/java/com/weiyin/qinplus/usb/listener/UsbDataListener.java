package com.weiyin.qinplus.usb.listener;

/**
 * Created by lenovo on 2017/10/30.
 */

public interface UsbDataListener {
    /**
     *  usb琴键消息
     * @param status 80 90
     * @param channel 通道
     * @param note note值
     * @param velocity 力度
     */
    void usbData(int status, int channel, int note, int velocity);
}
