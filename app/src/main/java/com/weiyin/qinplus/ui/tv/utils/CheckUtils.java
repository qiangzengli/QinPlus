package com.weiyin.qinplus.ui.tv.utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtils {

    //判断是否手机号码
    public static PhoneType isPhone(String phoneNumber) {
        if (isChinaPhone(phoneNumber)) {
            return PhoneType.CHINA;
        } else if (isAmericanPhone(phoneNumber)) {
            return PhoneType.AMERICAN;
        } else {
            return PhoneType.NOTHING;
        }
    }

    public static boolean isChinaPhone(String phoneNumber) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(phoneNumber);
        System.out.println(m.matches() + "---");
        return m.matches();
    }

    public static boolean isAmericanPhone(String phoneNumber) {
        if (phoneNumber.length() == 10) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }


    public enum PhoneType {
        CHINA, AMERICAN, NOTHING
    }

    public static String string2Time(String duration){
        int m;
        int s;
        int time;
        try{
            time = Integer.parseInt(duration);
        }catch (RuntimeException re) {
            re.printStackTrace();
            return "00:00";
        }
        m = time / 60;
        s = time % 60;
        StringBuffer sb =new StringBuffer();
        sb.append(m >= 10 ? m : "0" + m ).append( ":" ).append( s >= 10 ? s : "0" + s);
        return sb.toString();
    }


}
