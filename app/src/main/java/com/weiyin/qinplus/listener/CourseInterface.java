package com.weiyin.qinplus.listener;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 跳转题目
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public interface CourseInterface {
    /**
     *  跳转题目
     * @param type 样式
     * @param index 下标
     */
    void courseInterfaceResult(String type, int index);
}
