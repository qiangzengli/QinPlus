package com.weiyin.qinplus.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.RelativeLayout;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.bluetooth.BlueToothControl;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.listener.InterfaceBlueConnect;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;
import com.weiyin.qinplus.usb.UsbController;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app  启动页面Activity
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class StartingActivity extends BaseActivity implements InterfaceBlueConnect {
    public final String TAG = "StartingActivity";
    private final int REQUEST_FINE_LOCATION = 0;

    AnimationDrawable reanimation;
    private static final int SHOW_TIME_MIN = 5000;
    long mStartTime;
    BlueToothControl blueToothControl;

    RelativeLayout relativeLayout;
    Bitmap bitmap;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            /* 计算一下总共花费的时间 */
            long loadingTime = System.currentTimeMillis() - mStartTime;
            /* 如果比最小显示时间还短，就延时进入MainActivity，否则直接进入 */
            if (loadingTime < SHOW_TIME_MIN) {
                mHandler.postDelayed(goToMainActivity, SHOW_TIME_MIN
                        - loadingTime);
            } else {
                mHandler.post(goToMainActivity);
            }
        }
    };
    /**
     * 进入下一个Activity
     */
    Runnable goToMainActivity = new Runnable() {

        @Override
        public void run() {
            LogUtil.e(TAG + "off", String.valueOf(SystemClock.currentThreadTimeMillis()));
            StartingActivity.this.startActivity(new Intent(StartingActivity.this,
                    MainActivity.class));
            BlueToothControl blueToothControl = BlueToothControl.getBlueToothInstance();
            blueToothControl.removeConnectCallback(StartingActivity.this);
            mHandler.removeCallbacksAndMessages(null);
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BlueToothControl blueToothControl = BlueToothControl.getBlueToothInstance();
        blueToothControl.removeConnectCallback(StartingActivity.this);
        mHandler.removeCallbacksAndMessages(null);
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        LayoutHelper layoutHelper = new LayoutHelper(this);
        layoutHelper.scaleView(findViewById(R.id.activity_starting));

        relativeLayout = (RelativeLayout) findViewById(R.id.activity_starting);
        bitmap = Constant.readBitMap(this, R.drawable.opern_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        relativeLayout.setBackground(bitmapDrawable);

        blueToothControl = BlueToothControl.getBlueToothInstance();
        blueToothControl.addConnectedCallback(StartingActivity.this);
/* mayRequestLocation(); */
        checkBlueData();
        LogUtil.e(TAG + "on", String.valueOf(SystemClock.currentThreadTimeMillis()));

    }

    private void checkBlueData() {
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        final String strAddress = sp.getString("bluetoothdataaddress", "");
        final String strName = sp.getString("bluetoothdataname", "");
        UsbController usbController = UsbController.getUsbController();
        if (usbController == null || !usbController.ismConnected()) {
            if (!StringUtils.isEmpty(strAddress)) {
/*Log.i("StartActivity", "ble地址=" + strAddress);*/
/*Log.i("StartActivity", "ble名字=" + strName);*/
                blueToothControl.RegisterBlueReceiver(getApplicationContext());
                blueToothControl.setDeviceInfoConnect(getApplicationContext(), strName, strAddress);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!blueToothControl.getConnectFlag()) {
                            mHandler.sendEmptyMessage(0);
                        }
                    }
                }, 5000);
            } else {
                //记录开始时间，
                mStartTime = System.currentTimeMillis();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(0);
                    }
                }, 3000);
            }
        } else {
            //记录开始时间，
            mStartTime = System.currentTimeMillis();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(0);
                }
            }, 3000);
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

        if (reanimation != null) {
            reanimation.stop();
            reanimation = null;
        }
    }

    @Override
    public void onConnectStatusChanged(final String name, final String address, boolean bConnect) {
        if (bConnect) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    blueToothControl.sendData(BlueToothControl.OPEN_A2DP);
                    mHandler.sendEmptyMessage(0);
                    mHandler.removeCallbacks(this);
                }
            }, 2000);
        }
    }
}
