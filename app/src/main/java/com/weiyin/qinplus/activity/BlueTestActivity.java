package com.weiyin.qinplus.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.bluetooth.BlueToothControl;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.entity.BlueTestEntity;
import com.weiyin.qinplus.listener.InterfaceBlueConnect;
import com.weiyin.qinplus.listener.InterfaceBlueData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : 升级测试
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class BlueTestActivity extends Activity implements InterfaceBlueData, InterfaceBlueConnect {
    public final String TAG = "BlueTestActivity";

    SeekBar progressBar;
    int line = 1;
    ArrayList<BlueTestEntity> blueTestMoudles = new ArrayList<>();
    BlueToothControl blueToothControl;
    int select_ble = 0;
    String select_ble_result = "";

    int restart_blue_booth = 0;
    String restart_blue_booth_result = "";

    int restart_blue_app = 0;
    String restart_blue_app_result = "";

    int select_zhuban = 1;
    String select_zhuban_result = "";

    int restart_zhuban_booth = 0;
    String restart_zhuban_booth_result = "";

    int restart_zhuban_app = 0;
    String restart_zhuban_app_result = "";

    boolean upgrade = false;

    TextView blue_test_content;

    PowerManager.WakeLock wakeLock = null;

    BlueTestActivity mcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_test);
        mcontext = this;
        PowerManager powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

        progressBar = (SeekBar) findViewById(R.id.blue_test_progressBar);
        blue_test_content = (TextView) findViewById(R.id.blue_test_content);

        blueToothControl = BlueToothControl.getBlueToothInstance();
        blueToothControl.addDataCallback(this);
        blueToothControl.addConnectedCallback(this);

//        String blue_file_name = getIntent().getStringExtra("bluepath");
//        String blue_file_name = "/mnt/usb_connected/DEC61BE5/updateFiles/bluetooth.txt";
//        if (blue_file_name != null)
//        {
//            if(!blue_file_name.equals(""))
//            {
//                ReadFile(blue_file_name);
//            }
//        }
//        String zhuban_file_name = getIntent().getStringExtra("mainboardpath");
        String zhuban_file_name = Environment.getExternalStorageDirectory().getPath() + "/musicPCB.txt";
        if (zhuban_file_name != null) {
            if (!zhuban_file_name.equals("")) {
                ReadFile(zhuban_file_name);
            }
        }
        if (blueToothControl != null) {
            if (blueToothControl.getConnectFlag()) {
                blueToothControl.sendData(BlueToothControl.SELECT_ZHUBAN_MOSHI);
                handler.postDelayed(select_zhuban_runnable, 1000);
//                ToastUtil.makeText(BlueTestActivity.this,"发送了",ToastUtil.LENGTH_SHORT).show();
            }
        }

        progressBar.setMax(line);
        progressBar.setProgress(0);
//        //设置标题进度条风格
//        requestWindowFeature(Window.FEATURE_PROGRESS);
//        //显示标题进度
//        setProgressBarVisibility(true);
//        //设置标题当前进度值为5000（标题进度最大值默认为10000）
//        setProgress(5000);
    }

    Handler handler = new Handler();


    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        blueToothControl.removeDataCallback(this);
        blueToothControl.removeConnectCallback(this);
        handler.removeCallbacksAndMessages(null);
    }

    public void ReadFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return;
        }
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));

            String tempString;
            String strTempo = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {

                if (line % 2 == 1) {
                    int i = tempString.indexOf(":");
                    if (i < 0) {
                        Log.i(TAG, "解析错误");
                    }
                    strTempo = tempString.substring(i + 1);

                } else {
                    int i = tempString.indexOf("R");
                    int j = tempString.indexOf(":", i + 1);

                    String str1 = tempString.substring(i + 1, j);
                    String str2 = tempString.substring(j + 1);

                    BlueTestEntity blueTestMoudle = new BlueTestEntity();
                    int f = str2.indexOf("F7");
                    String s = str2.substring(0, f);
                    str2 = s + "80F7";
                    blueTestMoudle.setShuju(strTempo);
                    blueTestMoudle.setData(Long.valueOf(str1));
                    blueTestMoudle.setResult(str2);

                    blueTestMoudles.add(blueTestMoudle);
                }
                line++;
            }
            Log.i(TAG, "读取完成=" + blueTestMoudles.size());
        } catch (FileNotFoundException e) {
            Log.i(TAG, "找不到文件");
            e.printStackTrace();
        } catch (IOException ioException) {
            Log.i(TAG, "IO异常");
            ioException.printStackTrace();
        }
    }

    /**
     * 查询主板状态
     */
    Runnable select_zhuban_runnable = new Runnable() {
        @Override
        public void run() {
            if (select_zhuban_result.equals("")) {
                if (select_zhuban < 3) {
                    select_zhuban++;
                    blueToothControl.sendData(BlueToothControl.SELECT_ZHUBAN_MOSHI);
                    handler.removeCallbacks(select_zhuban_runnable);
                    handler.postDelayed(select_zhuban_runnable, 1000);
                } else {
                    select_zhuban = 0;
                    handler.removeCallbacks(select_zhuban_runnable);
                    ToastUtil.showTextToast(BlueTestActivity.this, "固件升级失败，请重新尝试。");
                }
            } else {
                select_zhuban_result = "";
                handler.removeCallbacks(select_zhuban_runnable);
            }
        }
    };

    /**
     * 重启主板进入app
     **/
    Runnable restart_zhuban_app_runnable = new Runnable() {
        @Override
        public void run() {
            if (restart_zhuban_app_result.equals("")) {
                if (restart_zhuban_app < 3) {
                    restart_zhuban_app++;
                    blueToothControl.sendData(BlueToothControl.CHONGQI_ZHUBAN_APP);
                    handler.removeCallbacks(restart_zhuban_app_runnable);
                    handler.postDelayed(restart_zhuban_app_runnable, 3000);
                } else {
                    handler.removeCallbacks(restart_zhuban_app_runnable);
                    restart_zhuban_app = 0;
                    ToastUtil.showTextToast(BlueTestActivity.this, "固件升级失败，请重新尝试。");
                }
            } else {
                restart_zhuban_app_result = "";
                handler.removeCallbacks(restart_zhuban_app_runnable);
            }
        }
    };

    /**
     * 重启主板进入booth
     **/
    Runnable restart_zhuban_booth_runnable = new Runnable() {
        @Override
        public void run() {
            if (restart_zhuban_booth_result.equals("")) {
                if (restart_zhuban_booth < 3) {
                    restart_zhuban_booth++;
                    blueToothControl.sendData(BlueToothControl.CHONGQI_ZHUBAN_BOOT);
                    handler.removeCallbacks(restart_zhuban_booth_runnable);
                    handler.postDelayed(restart_zhuban_booth_runnable, 3000);
                } else {
                    restart_zhuban_booth = 0;
                    handler.removeCallbacks(restart_zhuban_booth_runnable);
                    ToastUtil.showTextToast(BlueTestActivity.this, "固件升级失败，请重新尝试。");
                }
            } else {
                restart_zhuban_booth_result = "";
                handler.removeCallbacks(restart_zhuban_booth_runnable);
            }
        }
    };


    /**
     * 查询蓝牙状态
     **/
    Runnable select_Blue_runnable = new Runnable() {
        @Override
        public void run() {
            if (select_ble_result.equals("")) {
                if (select_ble < 3) {
                    select_ble++;
                    blueToothControl.sendData(BlueToothControl.SELECT_BLUE_MOSHI);
                    handler.removeCallbacks(select_Blue_runnable);
                    handler.postDelayed(select_Blue_runnable, 1000);
                } else {
                    select_ble = 0;
                    handler.removeCallbacks(select_Blue_runnable);
                    ToastUtil.showTextToast(BlueTestActivity.this, "固件升级失败，请重新尝试。");
                }
            } else {
                select_ble_result = "";
                handler.removeCallbacks(select_Blue_runnable);
            }
        }
    };
    /**
     * 重启蓝牙进入app
     **/
    Runnable restart_Blue_App_runnable = new Runnable() {
        @Override
        public void run() {
            if (restart_blue_app_result.equals("")) {
                if (restart_blue_app < 3) {
                    restart_blue_app++;
                    blueToothControl.sendData(BlueToothControl.CHONGQI_BLUE_APP);
                    handler.removeCallbacks(restart_Blue_App_runnable);
                    handler.postDelayed(restart_Blue_App_runnable, 1000);
                } else {
                    restart_blue_app = 0;
                    handler.removeCallbacks(restart_Blue_App_runnable);
                    ToastUtil.showTextToast(BlueTestActivity.this, "固件升级失败，请重新尝试。");
                }
            } else {
                restart_blue_app_result = "";
                handler.removeCallbacks(restart_Blue_App_runnable);
            }
        }
    };

    /**
     * 重启蓝牙进入booth
     **/
    Runnable restart_Blue_Booth_runnable = new Runnable() {
        @Override
        public void run() {
            if (restart_blue_booth_result.equals("")) {
                if (restart_blue_booth < 3) {
                    restart_blue_booth++;
                    blueToothControl.sendData(BlueToothControl.CHONGQI_BLUE_BOOT);
                    handler.removeCallbacks(restart_Blue_Booth_runnable);
                    handler.postDelayed(restart_Blue_Booth_runnable, 1000);
                } else {
                    restart_blue_booth = 0;
                }
            } else {
                restart_blue_booth_result = "";
                handler.removeCallbacks(restart_Blue_Booth_runnable);
            }


        }
    };


    int sendataindex = 0;
    Runnable SenData = new Runnable() {
        @Override
        public void run() {
            if (getStrresult.equals("")) {
                if (sendataindex < 3) {
                    sendataindex++;
                    BlueTestEntity blueTestMoudle = blueTestMoudles.get(blueindex);
                    int length = blueTestMoudle.getShuju().length();
                    for (int i = 0; i < length; i += 30) {
                        try {
                            StringBuffer stringBuffer1 = new StringBuffer();
                            if (i == 0) {
                                stringBuffer1.append("8080");
                            } else {
                                stringBuffer1.append("80");
                            }
                            if (i + 30 > blueTestMoudle.getShuju().length()) {
                                stringBuffer1.append(blueTestMoudle.getShuju().substring(i, blueTestMoudle.getShuju().length()));
                                Thread.sleep(10);
                                Log.i(TAG, stringBuffer1.toString());
                                blueToothControl.sendDataUpgrade(stringBuffer1.toString());
                            } else {
                                stringBuffer1.append(blueTestMoudle.getShuju().substring(i, i + 30));
                                Thread.sleep(10);
                                Log.i(TAG, stringBuffer1.toString());
                                blueToothControl.sendDataUpgrade(stringBuffer1.toString());
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.removeCallbacks(SenData);
                    handler.postDelayed(SenData, blueTestMoudle.getData() * 10);
                    Log.i(TAG, "时间=" + blueTestMoudle.getData());
                } else {
                    sendataindex = 0;
                    handler.removeCallbacks(SenData);
                    ToastUtil.showTextToast(BlueTestActivity.this, "固件升级失败，请重新尝试。");
                }
            } else {
                handler.removeCallbacks(SenData);
                getStrresult = "";
            }
        }
    };
    Runnable SenNextData = new Runnable() {
        @Override
        public void run() {
            progressBar.setProgress(blueindex * 2);
            DecimalFormat df = new DecimalFormat("0.00");
            blue_test_content.setText("已升级" + (df.format(((double) blueindex * 2 * 100 / (double) line))) + "%");
            if (blueindex < blueTestMoudles.size()) {

                BlueTestEntity blueTestMoudle = blueTestMoudles.get(blueindex);
                int length = blueTestMoudle.getShuju().length();
                for (int i = 0; i < length; i += 30) {
                    try {
                        StringBuffer stringBuffer1 = new StringBuffer();
                        if (i == 0) {
                            stringBuffer1.append("8080");
                        } else {
                            stringBuffer1.append("80");
                        }
                        if (i + 30 > blueTestMoudle.getShuju().length()) {
                            stringBuffer1.append(blueTestMoudle.getShuju().substring(i, blueTestMoudle.getShuju().length()));
                            Thread.sleep(10);
                            Log.i(TAG, stringBuffer1.toString());
                            blueToothControl.sendDataUpgrade(stringBuffer1.toString());
                            strresult = "";
//                            getStrresult = "";
                        } else {
                            stringBuffer1.append(blueTestMoudle.getShuju().substring(i, i + 30));
                            Thread.sleep(10);
                            Log.i(TAG, stringBuffer1.toString());
                            blueToothControl.sendDataUpgrade(stringBuffer1.toString());
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.postDelayed(SenData, blueTestMoudle.getData() * 10);

            } else {
                upgrade = false;
                blueToothControl.sendData(BlueToothControl.CHONGQI_ZHUBAN_APP);
            }
        }
    };

    int blueindex = 0;
    int a2dpindex = 0;
    boolean isblue = false;
    boolean isA2dp = false;
    String strresult = "";
    String getStrresult = "";

    @Override
    public void onDataReceive(String value) {
        String regex = "\\s+";
        strresult = value.replaceAll(regex, "");
        Log.i(TAG, "进入" + blueToothControl.senData);
        BlueTestEntity blueTestMoudle = new BlueTestEntity();
        if (blueTestMoudles.size() > 0) {
            if (blueindex >= blueTestMoudles.size()) {
                blueTestMoudle = blueTestMoudles.get(blueTestMoudles.size() - 1);
            } else {
                blueTestMoudle = blueTestMoudles.get(blueindex);
            }
        }
        if (!upgrade) {
            if (strresult.contains(blueToothControl.senData)) {
                Log.i(TAG, strresult);
                if (strresult.contains("455D5529")) {
                    select_zhuban_result = "result";
                    handler.postDelayed(restart_zhuban_booth_runnable, 3000);
                } else if (strresult.contains("455D5501")) {
                    isA2dp = true;
                    select_zhuban_result = "result";
                    handler.postDelayed(select_Blue_runnable, 1000);
                } else if (strresult.contains("45565501")) {
                    restart_zhuban_booth_result = "result";
                    isA2dp = true;
                    handler.postDelayed(select_Blue_runnable, 1000);
                } else if (strresult.contains("455E5501")) {
                    restart_zhuban_app_result = "result";
                    handler.postDelayed(restart_Blue_App_runnable, 1000);
                } else if (strresult.contains("45045501")) {
                    restart_blue_app_result = "result";
                    ToastUtil.showTextToast(BlueTestActivity.this, "固件升级成功");
                    blue_test_content.setText("升级成功");
                } else if (strresult.contains("45065500")) {
                    select_ble_result = "result";
                    isblue = true;
                } else if (strresult.contains("45065501")) {
                    select_ble_result = "result";
                    handler.postDelayed(restart_Blue_Booth_runnable, 1000);
                } else if (strresult.contains("45055501")) {
                    restart_blue_booth_result = "result";
                    isblue = true;
                }
                Log.i(TAG, "isblue=" + isblue + " isa2dp=" + isA2dp);
                Log.i(TAG, "getStrresult=" + getStrresult);
                if (isblue && isA2dp) {
                    upgrade = true;
                    isblue = false;
                    isA2dp = false;
                    handler.postDelayed(SenData, 5000);
                }
            }
        } else {
            if (strresult.contains(blueTestMoudle.getResult())) {
                getStrresult = strresult;
                sendataindex = 0;
                handler.postDelayed(SenNextData, 0);
                blueindex++;
                Log.i(TAG, blueindex + "");
            } else {
                getStrresult = "";
                handler.postDelayed(SenData, 0);
            }
        }
    }

    @Override
    public void onConnectStatusChanged(String name, String address, boolean bConnect) {
        if (!bConnect) {
            ToastUtil.showTextToast(BlueTestActivity.this, "蓝牙断开，请重新尝试。");
        }
    }
}
