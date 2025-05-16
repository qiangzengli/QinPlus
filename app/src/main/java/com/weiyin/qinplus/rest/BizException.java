package com.weiyin.qinplus.rest;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   :
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class BizException extends RuntimeException {

    private int code;

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String msg;
    private String url;

    public BizException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public BizException(int code, String msg, String url) {
        super(msg);
        this.code = code;
        this.url = url;
    }

    public int getCode() {
        return code;
    }
}
