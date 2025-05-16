package com.weiyin.qinplus.usb.listener;

/**
 * Created by lenovo on 2017/10/30.
 */

public interface UsbConnectListener {
    /**
     * 连接状态
     * @param openClose 0:开 1:断
     */
    void result(int openClose);

    void resultString(String result);
}
