package com.weiyin.qinplus.application;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 程序crash
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.weiyin.qinplus.activity.MainActivity;
import com.weiyin.qinplus.commontool.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Date;

public class WinYinPianoCrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "WinYinPianoCrashHandler";
    @SuppressLint("StaticFieldLeak")
    private static WinYinPianoCrashHandler leapFrogCrashHandler;
    /**
     * 日志文件在sdcard中的路径
     */
    @SuppressLint("SdCardPath")
    private static final String CRASH_PATH_SDCARD_DIR = "/sdcard/LetvPiano/crash/";

    private WinYinPianoCrashHandler() {
    }


    private Context context;

    public synchronized static WinYinPianoCrashHandler getInstance() {
        if (leapFrogCrashHandler == null) {
            leapFrogCrashHandler = new WinYinPianoCrashHandler();
            return leapFrogCrashHandler;
        } else {
            return leapFrogCrashHandler;
        }
    }

    public void init(Context context) {
        this.context = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            // 在throwable的参数里面保存的有程序的异常信息
            StringBuilder sb = new StringBuilder();
            //1.得到手机的版本信息 硬件信息
            Field[] fields = Build.class.getDeclaredFields();
            for (Field filed : fields) {
                /* 暴力反射 */
                filed.setAccessible(true);
                String name = filed.getName();
                String value = filed.get(null).toString();
                sb.append(name);
                sb.append("=");
                sb.append(value);
                sb.append("\n");
            }


            //2.得到当前程序的版本号
            PackageInfo info =
                    context.getPackageManager().getPackageInfo(context.getPackageName(),
                            0);
            sb.append(info.versionName);
            sb.append("\n");
            //3.得到当前程序的异常信息
            Writer writer = new StringWriter();
            PrintWriter printwriter = new PrintWriter(writer);

            ex.printStackTrace(printwriter);
            printwriter.flush();
            printwriter.close();

            sb.append(writer.toString());

            // 4.提交异常信息到服务器
            Log.e(TAG, sb.toString());
            // System.out.println(sb.toString());
        } catch (Exception e1) {
            /* TODO Auto-generated catch block */
            e1.printStackTrace();
        }

        writeToSDCard(ex);
        ToastUtil.showTextToast(context, "程序跑去弹琴了，请重新打开下");
        WinYinPianoApplication.getInstance().exit();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    public void restartApp() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        /* 结束进程之前可以把你程序的注销或者退出代码放在这段代码之前 */
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void writeToSDCard(Throwable ex) {
        boolean isDealing = false;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            RandomAccessFile randomAccessFile = null;
            try {
                String fileName = CRASH_PATH_SDCARD_DIR;
                File file = new File(fileName);
                if (!file.exists()) {
                    file.mkdirs();
                }
                randomAccessFile = new RandomAccessFile(fileName
                        + pacerTime(System.currentTimeMillis()) + ".log", "rw");
                long fileLength = randomAccessFile.length();
                randomAccessFile.seek(fileLength);
                randomAccessFile.writeBytes(getThrowableInfo(ex));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (randomAccessFile != null) {
                    try {
                        randomAccessFile.close();
                        isDealing = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String pacerTime(long currentTimeMillis) {
        // TODO Auto-generated method stub
        Date date = new Date(currentTimeMillis);
        return date.toString();
    }

    private static String getThrowableInfo(Throwable ex) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);
        return stringWriter.toString();
    }

}
