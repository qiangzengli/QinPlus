package com.weiyin.qinplus.application;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.weiyin.qinplus.changemiditrack.MidiFile;
import com.weiyin.qinplus.changemiditrack.MidiOptions;
import com.weiyin.qinplus.commontool.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app  常量
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class Constant {

    public static final float SCALE_RATE = 1.1f;

    public static final String APP_ID = "wx9f01b270b29d38b9";
    public static final String SECRET = "35863bb05cbc6487c3a4fcf2a6478072";
    public static final int DOWN = 90;
    public static final int UP = 80;
    // http请求状态码标志
    /**
     * 请求OK
     */
    public final static int STATAS_OK = 200;
    /**
     * 请求无响应找不到响应资源
     */
    public final static int NO_RESPONSE = 400;
    /**
     * 服务器出错
     */
    public final static int S_EXCEPTION = 500;
    /**
     * 响应异常
     */
    public final static int RESPONESE_EXCEPTION = 160;
    /**
     * 请求超时
     */
    public final static int TIMEOUT = 101;
    /**
     * 没用可用网络
     */
    public final static int NO_NETWORK = 102;
    /**
     * 参数为空异常
     */
    public final static int NULLPARAMEXCEPTION = 103;

    public static final String STATE = "state";
    public static final String HAVE_RECORDING = "haveRecording";
    public static final String VIDEO = "video";
    public static final String SCORE_ENTITY = "scoreEntity";
    public static final String PATH = "path";

    public static final String NEXT_KNOWLEDGE = "nextKnowledge";
    public static final String LAST_KNOWLEDGE = "lastKnowledge";
    public static final String TOTAL_PRACTICE = "totalPractice";
    public static final String NEXT_PRACTICE = "nextPractice";
    public static final String OLD_PRACTICE = "oldPractice";

    public final static String NICKNAME = "nickName";

    public final static String USER = "user";
    public final static String UNION_ID = "unionId";
    public final static String ACT_DEVICE = "actDevice";
    public final static String USER_ID = "userId";
    public final static String REGED = "reged";
    /**
     * 头像
     */
    public final static String HEAD_IMAGE_URL = "headImageUrl";
    public final static String CITY = "city";
    public final static String SEX = "sex";
    public final static String AGE = "age";
    public final static String LOGIN_ACTIVITY = "LoginActivity";
    public static final int TWO_HAND = 0;
    public static final int RIGHT_HAND = 1;
    public static final int LEFT_HAND = 2;

    public final static String CODE = "2";
    public final static String REGROW = "1";
    public static String PRACTICE_PATH = "/piano/class/1/";
    public static String DOWN_PATH = "";

    public static final String RESP_CODE = "respCode";


    /**
     * 获取单个曲谱
     */
    public final static int INTERNET_CALLBACK_MUSIC_ITEM_DETAIL = 0x02;
    /**
     * 文件下载
     */
    public final static int INTERNET_CALLBACK_FILE_DOWN = 0x03;
    /**
     * 获取文件书列表信息
     */
    public final static int INTERNET_CALLBACK_REQUEST_CABLE_MUSIC_CLASS_IDEALIST = 0x04;
    /**
     * 获取文件书列表信息
     */
    public final static int INTERNET_CALLBACK_REQUEST_CABLE_MUSIC_FASCIST = 0x06;
    /**
     * 获取文件书列表信息
     */
    public final static int INTERNET_CALLBACK_REQUEST_CABLE_MUSIC_LEVEL_LIST = 0x07;

    /**
     * 获取书本详情信息
     */
    public final static int INTERNET_CALLBACK_GET_BOOK_LIST = 0x05;
    /**
     * 微信
     */
    public final static int INTERNET_WX = 0x08;
    /**
     * 微信用户信息
     */
    public final static int INTERNET_WX_USER = 0x09;
    /**
     * 注册
     */
    public final static int INTERNET_REGISTER = 0x10;
    /**
     * 登录
     */
    public final static int INTERNET_LOGIN = 0x11;
    /**
     * 更新APP
     */
    public final static int INTERNET_UPDATE_APP = 0x12;
    /**
     * 退出
     */
    public final static int INTERNET_OUT_LOGIN = 0x13;
    /**
     * 文件下载失败
     */
    public final static int INTERNET_CALLBACK_FILE_DOWN_FAULT = 0x14;
    public final static int INTERNET_GET_VIDEO_LIST = 0x15;
    public final static int INTERNET_GET_PRACTICE_LIST = 0x16;
    public final static int INTERNET_GET_UPDATE_USER = 0x17;


    public static boolean compare(String name, String addres, ArrayList<BluetoothDevice> bluetooth_bles) {
        if (name != null && addres != null) {
            if (bluetooth_bles.size() > 0) {
                int length = bluetooth_bles.size();
                for (int i = 0; i < length; i++) {
                    BluetoothDevice bluetoothDevice = bluetooth_bles.get(i);
                    if (bluetoothDevice.getName() != null && bluetoothDevice.getAddress() != null) {
                        Log.i("constant", "找到的blename=" + bluetoothDevice.getName());
                        Log.i("constant", "找到的bleaddress=" + bluetoothDevice.getAddress());
                        if (bluetoothDevice.getName().equals(name) && bluetoothDevice.getAddress().equals(addres)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }


    /**
     *  Java文件操作 获取不带扩展名的文件名 
     *  
     */
    public static String getFileNameNoEx(String filename, String string) {
        if (filename.contains(string)) {
            if (filename.length() > 0) {
                int dot = filename.lastIndexOf(string);
                if ((dot > -1) && (dot < (filename.length()))) {
                    return filename.substring(0, dot);
                }
            }
            return filename;
        }
        return null;
    }

    public static void createMidiFile(MidiFile midiFile, String path, MidiOptions options, Context mContext) {
        // TODO Auto-generated method stub
        FileOutputStream dest = null;
        try {
            /* FileOutputStream dest = this.openFileOutput("testMidi.mid", Context.MODE_PRIVATE); */
            dest = new FileOutputStream(new File(path));
            midiFile.ChangeSound(dest, options);
            /* checkFile(tempSoundFile); */
        } catch (IOException e) {
            ToastUtil.showTextToast(mContext, "Error: Unable to create MIDI file for playing.");
        } finally {
            try {
                assert dest != null;
                dest.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static char ascii2Char(int ascii) {
        return (char) ascii;
    }

    public static String ascii2String(int[] asciis) {
        StringBuilder sb = new StringBuilder();
        for (int ascii : asciis) {
            sb.append(ascii2Char(ascii));
        }
        return sb.toString();
    }


    //获取版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}
