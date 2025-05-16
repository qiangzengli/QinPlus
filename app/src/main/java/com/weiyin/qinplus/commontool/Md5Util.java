package com.weiyin.qinplus.commontool;

import java.security.MessageDigest;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : MD5帮助类
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class Md5Util {
    /**
     * 调用md5算法进行加密
     *
     * @param password 原文
     * @return 密文
     * @author lhz
     */
    public static String md5Diagest(String password) {

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] result = digest.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < result.length; i++) {
                String res = Integer.toHexString(result[i] & 0xFF);
                if (res.length() == 1) {
                    /* 0~F */
                    sb.append("0").append(res);
                } else {
                    sb.append(res);
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String md5Diagest(String str, int bit) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] result = digest.digest(str.getBytes("UTF-8"));
            for (byte aResult : result) {
                String res = Integer.toHexString(aResult & 0xFF);
                if (res.length() == 1) {
                    /* 0~F */
                    sb.append("0").append(res);
                } else {
                    sb.append(res);
                }
            }
            if (bit == 16) {
                return sb.toString().substring(8, 24);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
