package com.weiyin.qinplus.commontool;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : 日志类
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class LogUtil {
    /**
     * 日志文件总开关
     */
    private static Boolean MY_LOG_SWITCH = true;
    /**
     * 日志写入文件开关
     */
    private static Boolean MY_LOG_WRITE_TO_FILE = false;

    /**
     * 输入日志类型，w代表只输出告警信息等，v代表输出所有信息
     */
    private static char MY_LOG_TYPE = 'v';
    /**
     * 日志文件在sdcard中的路径
     */
    private static String MY_LOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * sd卡中日志文件的最多保存天数
     */
    private static int SDCARD_LOG_FILE_SAVE_DAYS = 0;
    /**
     * 本类输出的日志文件名称
     */
    private static String MYLOGFILEName = "galileoLog.txt";
    /**
     * 日志的输出格式
     */
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat myLogSdf = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    /**
     * 日志文件格式
     */
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");

    public static void w(String tag, Object msg) { // 警告信息
        log(tag, msg.toString(), 'w');
    }

    public static void e(String tag, Object msg) { // 错误信息
        log(tag, msg.toString(), 'e');
    }

    public static void d(String tag, Object msg) {// 调试信息
        log(tag, msg.toString(), 'd');
    }

    public static void i(String tag, Object msg) {//
        log(tag, msg.toString(), 'i');
    }

    public static void v(String tag, Object msg) {
        log(tag, msg.toString(), 'v');
    }

    public static void w(String tag, String text) {
        log(tag, text, 'w');
    }

    public static void e(String tag, String text) {
        log(tag, text, 'e');
    }

    public static void d(String tag, String text) {
        log(tag, text, 'd');
    }

    public static void i(String tag, String text) {
        log(tag, text, 'i');
    }

    public static void v(String tag, String text) {
        log(tag, text, 'v');
    }

    public static void setMyLogWriteToFile(Boolean myLogWriteToFile) {
        MY_LOG_WRITE_TO_FILE = myLogWriteToFile;
    }

    public static void setMyLogSwitch(Boolean myLogSwitch) {
        MY_LOG_SWITCH = myLogSwitch;
    }

    /**
     * 根据tag, msg和等级，输出日志
     *
     * @param tag   String
     * @param msg   String
     * @param level char
     * @return void
     * @since v 1.0
     */
    private static void log(String tag, String msg, char level) {
        if (MY_LOG_SWITCH) {
            if (('e' == level) && (('e' == MY_LOG_TYPE) || ('v' == MY_LOG_TYPE))) {
                //输出错误信息
                Log.e(tag, msg);
            } else if ('w' == level && ('w' == MY_LOG_TYPE || 'v' == MY_LOG_TYPE)) {
                Log.w(tag, msg);
            } else if ('d' == level && ('d' == MY_LOG_TYPE || 'v' == MY_LOG_TYPE)) {
                Log.d(tag, msg);
            } else if ('i' == level && ('d' == MY_LOG_TYPE || 'v' == MY_LOG_TYPE)) {
                Log.i(tag, msg);
            } else {
                Log.v(tag, msg);
            }
            if (MY_LOG_WRITE_TO_FILE) {
                writeLogToFile(String.valueOf(level), tag, msg);
            }
        }
    }

    /**
     * 打开日志文件并写入日志
     * 新建或打开日志文件
     *
     * @return void
     **/
    private static void writeLogToFile(String myLogType, String tag, String text) {
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);

        if (sdCardExist) {

            Date nowTime = new Date();
            String needWriteField = logfile.format(nowTime);
            String needWriteMessage = myLogSdf.format(nowTime) + "    " + myLogType
                    + "    " + tag + "    " + text;
            File file = new File(MY_LOG_PATH_SDCARD_DIR, needWriteField
                    + MYLOGFILEName);
            try {
                /* 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖 */
                FileWriter filerWriter = new FileWriter(file, true);
                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                bufWriter.write(needWriteMessage);
                bufWriter.newLine();
                bufWriter.close();
                filerWriter.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除制定的日志文件
     * 删除日志文件
     */
    public static void delFile() {
        String needDelField = logfile.format(getDateBefore());
        File file = new File(MY_LOG_PATH_SDCARD_DIR, needDelField + MYLOGFILEName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
     */
    private static Date getDateBefore() {
        Date nowTime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowTime);
        now.set(Calendar.DATE, now.get(Calendar.DATE)
                - SDCARD_LOG_FILE_SAVE_DAYS);
        return now.getTime();
    }
}
