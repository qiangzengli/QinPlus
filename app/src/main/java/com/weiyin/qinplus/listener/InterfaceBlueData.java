package com.weiyin.qinplus.listener;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 接口类，数据回调接口
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public interface InterfaceBlueData {
    /**
     * 蓝牙数据回调接口
     * @param value 回调内容
     */
    void onDataReceive(String value);
}
