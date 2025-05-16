package com.weiyin.qinplus.rest;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   :  返回请求接口类
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public interface HttpRequestListener<T> {

    /**
     * 请求队列完成回调
     *
     * @param url 请求的url地址
     */
    void onCompleted(String url);

    /**
     * 请求错误回调
     *
     * @param e 异常
     */
    void onError(BizException e);

    /**
     * 单个请求完成回调
     *
     * @param t   返回值
     * @param url 请求的url地址
     */
    void onNext(T t, String url);

}
