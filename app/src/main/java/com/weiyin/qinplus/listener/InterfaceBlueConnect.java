package com.weiyin.qinplus.listener;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 蓝牙回调连接状态的改变
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public interface InterfaceBlueConnect {
    /**
     * 连接回调
     *
     * @param name     蓝牙名
     * @param address  蓝牙地址
     * @param bConnect 连接状态
     */
    void onConnectStatusChanged(String name, String address, boolean bConnect);
}
