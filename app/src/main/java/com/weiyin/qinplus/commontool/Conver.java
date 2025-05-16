package com.weiyin.qinplus.commontool;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : 时间帮助类
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class Conver {

    /**
     * 把字符串转化为日期
     *
     * @param strDate String
     * @return Date
     * @throws ParseException
     */
    public static Date ConverToDateTime(String strDate) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf1.parse(strDate);
    }


    /**
     * 把字符串转化为日期
     *
     * @param strDate String
     * @return Date
     * @throws ParseException
     */
    public static Date ConverToDateUK(String strDate) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
        return sdf1.parse(strDate);
    }

    /**
     * 把毫秒转化成日期
     *
     * @param dateFormat (日期格式，例如：MM/ dd/yyyy HH:mm:ss)
     * @param millSec    (毫秒数)
     * @return String
     *     
     */
    public static String transferLongToDate(String dateFormat, Long millSec) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }

    /**
     * 讲毫秒转换为字符串时间用于显示
     */
    public static String datetimeStamp(int date) {
        date = date / 1000;
        int s = (date % 60);
        int m =     (date / 60 % 60);
        if (s < 10) {
            return m + ":0" + s;
        } else {
            return m + ":" + s;
        }

    }

    /**
     * 把字符串转化为日期
     *
     * @param strDate String
     * @return Date
     * @throws Exception
     */
    public static Date ConverToDate(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
        return df.parse(strDate);
    }

    /**
     * 把字符串转化为日期2
     *
     * @param strDate String
     * @return Date
     * @throws Exception
     */
    public static Date ConverToDate2(String strDate) throws Exception {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.parse(strDate);
    }

    /**
     * 把日期转化为字符串
     *
     * @param date Date
     * @return String
     */
    public static String ConverToString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
        return df.format(date);
    }


}
