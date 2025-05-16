package com.weiyin.qinplus.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnLayoutInflatedListener;
import com.app.hubert.guide.model.GuidePage;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.adapter.MainFragmentPagerAdapter;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.bluetooth.BlueToothControl;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.dialog.ConnectDialog;
import com.weiyin.qinplus.dialog.ConnectDialogMain;
import com.weiyin.qinplus.dialog.UpdateAppDialog;
import com.weiyin.qinplus.fragment.MainMusicClassicalFragment;
import com.weiyin.qinplus.fragment.MainMusicFashionFragment;
import com.weiyin.qinplus.fragment.MainMusicLevelFragment;
import com.weiyin.qinplus.fragment.MainPersonalCenterFragment;
import com.weiyin.qinplus.fragment.MainPracticeFragment;
import com.weiyin.qinplus.fragment.MainSearchFragment;
import com.weiyin.qinplus.fragment.MainVideoFragment;
import com.weiyin.qinplus.listener.InterfaceBlueConnect;
import com.weiyin.qinplus.rest.BizException;
import com.weiyin.qinplus.rest.DataService;
import com.weiyin.qinplus.rest.HttpRequestListener;
import com.weiyin.qinplus.rest.RequestConfigs;
import com.weiyin.qinplus.rest.RetrofitClient;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;
import com.weiyin.qinplus.usb.UsbController;
import com.weiyin.qinplus.usb.listener.UsbConnectListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.xml.transform.SourceLocator;

import okhttp3.ResponseBody;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 入口activity
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class MainActivity extends BaseFragmentActivity implements HttpRequestListener<ResponseBody>, View.OnClickListener, View.OnFocusChangeListener, InterfaceBlueConnect,UsbConnectListener{
    public final String TAG = MainActivity.class.getSimpleName();
    private final int REQUEST_FINE_LOCATION=0;
    private View mOldView;

    private BlueToothControl blueToothControl;

    private MainMusicClassicalFragment classicalFragment;

    private CannotSlidingViewpager mainActivityViewpager;

    private RelativeLayout fragmentClassicalButtonRl;
    private String httpResult = "";
    private ImageView mainClassicalMusicImageView, mainPopMusicImageView, mainTestAreaImageView,
            mainSearchImageView, mainPersonalCenterImageView, mainImageView;

    private TextView mainDropDownTextView1, mainDropDownTextView2, mainHistoryTextView, mainTitleTextView;

    private ImageView loadingImageView;
    private ImageView loadingFaultImageView;
    private TextView loadingTextView;
    private RelativeLayout loadingFaultTextView;
    private AnimationDrawable blackTapAnimation;

    private RelativeLayout mainDropDownRl;
    private Bitmap bitmap;
    private String titleString;

    private boolean isClick = false;
    private String appContent;
    private int resCode;
    public static MainActivity mainActivity;
    private UsbController usbController;
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler();

    ImageView imgConnect;

    boolean blueState;
    int usbState=Integer.MAX_VALUE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        LayoutHelper layoutHelper = new LayoutHelper(this);
        layoutHelper.scaleView(findViewById(R.id.activity_main_layout));

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_main_layout);
        bitmap = Constant.readBitMap(this, R.drawable.opern_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        relativeLayout.setBackground(bitmapDrawable);

        blueToothControl = BlueToothControl.getBlueToothInstance();

        usbController = UsbController.getUsbController();
        usbController.init(this);

        blueToothControl.addConnectedCallback(this);
        usbController.addUsbConnectListener(this);

        Log.e(TAG + "on", String.valueOf(SystemClock.currentThreadTimeMillis()));

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        Log.i(TAG, "vmHeapSize=" + activityManager.getMemoryClass());

        initView();
        getUpdateApp();


        //获取蓝牙和usb的连接状态来更新首页的连接图标
        if (blueToothControl.getConnectFlag()||usbController.ismConnected()) {
            openConnect();

        } else {
            closeConnect();
        }


        View view=LayoutInflater.from(this).inflate(R.layout.view_guide_simple,null,false);
        layoutHelper.scaleView(view);
        NewbieGuide.with(this)
                //.alwaysShow(true)
                .setLabel("guide1")
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(imgConnect)
                        .setEverywhereCancelable(false)//是否点击任意位置消失引导页，默认true
                        .setLayoutRes(R.layout.view_guide_simple,R.id.img_guide)).show();


    }




    void getUpdateApp() {
        DataService dataService = new DataService(RetrofitClient.getInstance().getService());
        dataService.updateApp(Constant.getVersionName(this), Constant.CODE, this);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "销毁");
        super.onDestroy();
        if (mainActivity != null) {
            mainActivity = null;
        }
        WinYinPianoApplication.getInstance().exit();
        if (blueToothControl != null) {
            if (blueToothControl.getConnectFlag()) {
                blueToothControl.sendData(BlueToothControl.CLOSE_A2DP);
                blueToothControl.destroy();
            }
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        if (!isStart) {
            blueToothControl.removeConnectCallback(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WinYinPianoApplication.httpData = new WinYinPianoApplication.HttpData() {
            @Override
            public void httpDataResult(String result) {
                if (fragmentClassicalButtonRl != null) {
                    httpResult = result;
                    mHandler.sendEmptyMessage(0);
                }
            }
        };


        //每次恢复的时候要添加回调
        blueToothControl.addConnectedCallback(this);
        usbController.addUsbConnectListener(this);
        //每次回到首页的时候看是否有连接

        if (blueToothControl.getConnectFlag()||usbController.ismConnected()) {
            openConnect();

        } else {
            closeConnect();
        }

    }

    boolean isStart = false;

    private void stopLoading() {
        blackTapAnimation.stop();
        loadingImageView.setVisibility(View.GONE);
        loadingFaultImageView.setVisibility(View.VISIBLE);
        loadingTextView.setVisibility(View.GONE);
        loadingFaultTextView.setVisibility(View.VISIBLE);
    }

    private void showLoadDialog() {
        loadingImageView.setVisibility(View.VISIBLE);
        loadingFaultImageView.setVisibility(View.GONE);
        loadingTextView.setVisibility(View.VISIBLE);
        loadingFaultTextView.setVisibility(View.GONE);
        blackTapAnimation.start();
    }

    private void initView() {
        RelativeLayout mainClassicalMusicRl = (RelativeLayout) findViewById(R.id.main_classical_music_Rl);
        RelativeLayout mainPopMusicRl = (RelativeLayout) findViewById(R.id.main_pop_music_Rl);
        RelativeLayout mainTestAreaRl = (RelativeLayout) findViewById(R.id.main_test_area_Rl);
        RelativeLayout mainSearchRl = (RelativeLayout) findViewById(R.id.main_search_Rl);
        RelativeLayout mainPersonalCenterRl = (RelativeLayout) findViewById(R.id.main_personal_center_Rl);

        //连接蓝牙的按钮
        imgConnect= (ImageView) findViewById(R.id.img_connect);
        mainDropDownTextView1 = (TextView) findViewById(R.id.main_drop_down_textView1);
        mainDropDownTextView2 = (TextView) findViewById(R.id.main_drop_down_textView2);
        mainDropDownRl = (RelativeLayout) findViewById(R.id.main_drop_down_rl);
        mainImageView = (ImageView) findViewById(R.id.main_imageView);
        loadingImageView = (ImageView) findViewById(R.id.fragment_classical_imageView_loading);
        loadingFaultImageView = (ImageView) findViewById(R.id.fragment_classical_imageView_loadfault);
        loadingTextView = (TextView) findViewById(R.id.fragment_classical_textView_loading);
        loadingFaultTextView = (RelativeLayout) findViewById(R.id.fragment_classical_imageView_loadFault_rl);

        loadingImageView.setImageResource(R.drawable.imageview_rotate);
        blackTapAnimation = new AnimationDrawable();
        blackTapAnimation = (AnimationDrawable) loadingImageView.getDrawable();

        fragmentClassicalButtonRl = (RelativeLayout) findViewById(R.id.fragment_classical_button_rl);

        mainActivityViewpager = (CannotSlidingViewpager) findViewById(R.id.activity_main_viewpager_fragment);

        mainHistoryTextView = (TextView) findViewById(R.id.main_history_textView);
        mainTitleTextView = (TextView) findViewById(R.id.main_title_textView);

        mainClassicalMusicImageView = (ImageView) findViewById(R.id.main_classical_music_imageView);
        mainPopMusicImageView = (ImageView) findViewById(R.id.main_pop_music_imageView);
        mainTestAreaImageView = (ImageView) findViewById(R.id.main_test_area_imageView);
        mainSearchImageView = (ImageView) findViewById(R.id.main_search_imageView);
        mainPersonalCenterImageView = (ImageView) findViewById(R.id.main_personal_center_imageView);

        mainTitleTextView.setOnClickListener(this);
        mainClassicalMusicRl.setOnClickListener(this);
        mainPopMusicRl.setOnClickListener(this);
        mainTestAreaRl.setOnClickListener(this);
        mainSearchRl.setOnClickListener(this);
        mainPersonalCenterRl.setOnClickListener(this);

        mainHistoryTextView.setOnClickListener(this);

        fragmentClassicalButtonRl.setOnClickListener(this);
        mainImageView.setOnClickListener(this);
        mainDropDownTextView1.setOnClickListener(this);
        mainDropDownTextView2.setOnClickListener(this);
        imgConnect.setOnClickListener(this);


        ArrayList<Fragment> fragmentList = new ArrayList<>();
        classicalFragment = new MainMusicClassicalFragment();
        MainMusicFashionFragment fashionFragment = new MainMusicFashionFragment();
        MainMusicLevelFragment levelFragment = new MainMusicLevelFragment();
        MainSearchFragment searchFragment = new MainSearchFragment();
        MainPersonalCenterFragment personalCenterFragment = new MainPersonalCenterFragment();
        MainPracticeFragment practiceFragment = new MainPracticeFragment();
        MainVideoFragment mainVideoFragment = new MainVideoFragment();

        fragmentList.add(classicalFragment);
        fragmentList.add(fashionFragment);
        fragmentList.add(levelFragment);
        fragmentList.add(searchFragment);
        fragmentList.add(personalCenterFragment);
        fragmentList.add(practiceFragment);
        fragmentList.add(mainVideoFragment);

        FragmentManager fm = getSupportFragmentManager();
        MainFragmentPagerAdapter fragmentAdapter = new MainFragmentPagerAdapter(fm, fragmentList);
        mainActivityViewpager.setAdapter(fragmentAdapter);

        mOldView = mainClassicalMusicImageView;

        titleString = "经典曲谱";



    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_classical_music_Rl ||
                v.getId() == R.id.main_pop_music_Rl ||
                v.getId() == R.id.main_test_area_Rl ||
                v.getId() == R.id.main_search_Rl ||
                v.getId() == R.id.main_personal_center_Rl) {
            mainClassicalMusicImageView.setImageResource(R.drawable.musicinput);
            mainPopMusicImageView.setImageResource(R.drawable.main_video_o);
            mainTestAreaImageView.setImageResource(R.drawable.main_practice_o);
            mainSearchImageView.setImageResource(R.drawable.main_seach_o);
            mainPersonalCenterImageView.setImageResource(R.drawable.main_personal_center_o);
        }
        int viewId = v.getId();

// 标题栏点击处理（两个控件共用同一逻辑）
        if (viewId == R.id.main_title_textView || viewId == R.id.main_imageView) {
            if (!isClick) {
                isClick = true;
                mainImageView.setImageResource(R.drawable.pull);
                mainDropDownRl.setVisibility(View.VISIBLE);
                setTextView();
            } else {
                isClick = false;
                mainImageView.setImageResource(R.drawable.drop_down);
                mainDropDownRl.setVisibility(View.GONE);
            }
        }
// 下拉菜单选项1
        else if (viewId == R.id.main_drop_down_textView1) {
            Log.i(TAG, mainDropDownTextView1.getText().toString());
            mainTitleTextView.setText(mainDropDownTextView1.getText());
            setTextView();
            setOnclic();
        }
// 下拉菜单选项2
        else if (viewId == R.id.main_drop_down_textView2) {
            mainTitleTextView.setText(mainDropDownTextView2.getText());
            setTextView();
            setOnclic();
        }
// 古典音乐按钮
        else if (viewId == R.id.fragment_classical_button_rl) {
            WinYinPianoApplication.getInstance().getJson();
            showLoadDialog();
        }
// 历史记录
        else if (viewId == R.id.main_history_textView) {
            startActivity(new Intent(this, MainHistoryActivity.class));
        }
// 古典音乐模块
        else if (viewId == R.id.main_classical_music_Rl) {
            mainImageView.setVisibility(View.VISIBLE);
            mainHistoryTextView.setVisibility(View.VISIBLE);
            mainTitleTextView.setText(titleString);
            mainTitleTextView.setClickable(true);
            mOldView = mainClassicalMusicImageView;

            if (httpResult.equals("error")) {
                fragmentClassicalButtonRl.setVisibility(View.VISIBLE);
            }
            classicalFragment.handlerUI.sendEmptyMessage(Constant.INTERNET_CALLBACK_REQUEST_CABLE_MUSIC_CLASS_IDEALIST);

            mainClassicalMusicImageView.setImageResource(R.drawable.musicinput_f);
            setOnclic();
        }
// 流行音乐模块
        else if (viewId == R.id.main_pop_music_Rl) {
            if (mainImageView.getVisibility() == View.VISIBLE) {
                mainImageView.setVisibility(View.GONE);
                mainDropDownRl.setVisibility(View.GONE);
                isClick = false;
                mainImageView.setImageResource(R.drawable.drop_down);
            }
            mOldView = mainPopMusicImageView;
            if (httpResult.equals("error")) {
                fragmentClassicalButtonRl.setVisibility(View.VISIBLE);
            }
            mainHistoryTextView.setVisibility(View.VISIBLE);
            mainPopMusicImageView.setImageResource(R.drawable.main_video_f);
            mainTitleTextView.setText("视频教学");
            mainTitleTextView.setClickable(false);
            mainActivityViewpager.setCurrentItem(6);
        }
// 测试区域模块
        else if (viewId == R.id.main_test_area_Rl) {
            if (mainImageView.getVisibility() == View.VISIBLE) {
                mainImageView.setVisibility(View.GONE);
                mainDropDownRl.setVisibility(View.GONE);
                isClick = false;
                mainImageView.setImageResource(R.drawable.drop_down);
            }
            mOldView = mainTestAreaImageView;
            if (httpResult.equals("error")) {
                fragmentClassicalButtonRl.setVisibility(View.VISIBLE);
            }
            mainHistoryTextView.setVisibility(View.VISIBLE);
            mainTestAreaImageView.setImageResource(R.drawable.main_practice_f);
            mainTitleTextView.setText("音乐教室");
            mainTitleTextView.setClickable(false);
            mainActivityViewpager.setCurrentItem(5);
        }
// 搜索模块
        else if (viewId == R.id.main_search_Rl) {
            if (mainImageView.getVisibility() == View.VISIBLE) {
                mainImageView.setVisibility(View.GONE);
                mainDropDownRl.setVisibility(View.GONE);
                isClick = false;
                mainImageView.setImageResource(R.drawable.drop_down);
            }
            mOldView = mainSearchImageView;
            if (httpResult.equals("error")) {
                fragmentClassicalButtonRl.setVisibility(View.GONE);
            }
            mainHistoryTextView.setVisibility(View.GONE);
            mainSearchImageView.setImageResource(R.drawable.main_seach_f);
            mainTitleTextView.setText("搜索");
            mainTitleTextView.setClickable(false);
            mainActivityViewpager.setCurrentItem(3);
        }
// 个人中心模块
        else if (viewId == R.id.main_personal_center_Rl) {
            if (mainImageView.getVisibility() == View.VISIBLE) {
                mainImageView.setVisibility(View.GONE);
                mainDropDownRl.setVisibility(View.GONE);
                isClick = false;
                mainImageView.setImageResource(R.drawable.drop_down);
            }
            mOldView = mainPersonalCenterImageView;
            if (httpResult.equals("error")) {
                fragmentClassicalButtonRl.setVisibility(View.GONE);
            }
            mainHistoryTextView.setVisibility(View.VISIBLE);
            mainPersonalCenterImageView.setImageResource(R.drawable.main_personal_center_f);
            mainTitleTextView.setText("个人中心");
            mainTitleTextView.setClickable(false);
            mainActivityViewpager.setCurrentItem(4);
        }
// 蓝牙连接
        else if (viewId == R.id.img_connect) {
            mayRequestLocation();
            ConnectDialogMain bluetoothDialog = new ConnectDialogMain(this, R.style.ConnectDialogStyle, imgConnect);
            Window dialogWindow = bluetoothDialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
            WindowManager wm = getWindowManager();
            Display display = wm.getDefaultDisplay();

            int[] location = new int[2];
            imgConnect.getLocationOnScreen(location);
            /* 新位置X坐标 */
            lp.x = imgConnect.getWidth()/2;
            /* 新位置Y坐标 */
            lp.y = (location[1] + imgConnect.getHeight());

            bluetoothDialog.show();
        }
    }

    private void setTextView() {
        if ("经典曲谱".equals(mainTitleTextView.getText().toString())) {
            mainDropDownTextView1.setText("流行音乐");
            mainDropDownTextView2.setText("考级专区");
        } else if ("流行音乐".equals(mainTitleTextView.getText().toString())) {
            mainDropDownTextView1.setText("经典曲谱");
            mainDropDownTextView2.setText("考级专区");
        } else if ("考级专区".equals(mainTitleTextView.getText().toString())) {
            mainDropDownTextView1.setText("经典曲谱");
            mainDropDownTextView2.setText("流行音乐");
        }
        titleString = mainTitleTextView.getText().toString();
    }

    private void setOnclic() {
        if ("经典曲谱".equals(mainTitleTextView.getText().toString())) {
            classicalFragment.handlerUI.sendEmptyMessage(Constant.INTERNET_CALLBACK_REQUEST_CABLE_MUSIC_CLASS_IDEALIST);
            mainActivityViewpager.setCurrentItem(0);
        } else if ("流行音乐".equals(mainTitleTextView.getText().toString())) {
            classicalFragment.handlerUI.sendEmptyMessage(Constant.INTERNET_CALLBACK_REQUEST_CABLE_MUSIC_CLASS_IDEALIST);
            mainActivityViewpager.setCurrentItem(1);
        } else if ("考级专区".equals(mainTitleTextView.getText().toString())) {
            classicalFragment.handlerUI.sendEmptyMessage(Constant.INTERNET_CALLBACK_REQUEST_CABLE_MUSIC_CLASS_IDEALIST);
            mainActivityViewpager.setCurrentItem(2);
        }
        mainHistoryTextView.setVisibility(View.VISIBLE);
        mOldView = mainClassicalMusicImageView;
        if (httpResult.equals("error")) {
            fragmentClassicalButtonRl.setVisibility(View.VISIBLE);
        }
        mainClassicalMusicImageView.setImageResource(R.drawable.musicinput_f);

        isClick = false;
        mainImageView.setImageResource(R.drawable.drop_down);
        mainDropDownRl.setVisibility(View.GONE);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void onConnectStatusChanged(String name, String address, boolean bConnect) {
        LogUtil.i("MainActivity", "连接状态=" + bConnect);

        blueState=bConnect;
        if (blueState||usbState==0) {
            openConnect();

        } else {
            closeConnect();
        }
    }

    @Override
    public void onCompleted(String url) {
        stopLoading();
    }

    @Override
    public void onError(BizException e) {
        ToastUtil.showTextToast(this, e.toString());
        LogUtil.i(TAG, e.getUrl() + " " + e.getMsg());
        stopLoading();
    }

    @Override
    public void onNext(ResponseBody baseResponse, String url) {
        stopLoading();
        switch (url) {
            case RequestConfigs.UPDATE_APP:
                try {
                    JSONObject jsonObject = new JSONObject(baseResponse.string());
                    resCode = jsonObject.getInt(Constant.RESP_CODE);
                    if (resCode == 0) {
                        appContent = jsonObject.getString("updateInfo");
                        appContent = appContent.replace("\\n", "\n");
                    }

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (resCode == 0) {
                                UpdateAppDialog loginDialog = new UpdateAppDialog(MainActivity.this, R.style.BlueToothDialogStyle, appContent);
                                if (hasWindowFocus()) {
                                    loginDialog.show();
                                }
                            }
                        }
                    }, 500);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }


    private void mayRequestLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要 向用户解释，为什么要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    ToastUtil.showTextToast(getApplicationContext(), "请允许开启定位，否则无法连接钢琴");
                }
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_FINE_LOCATION);
            } else {
            }
        } else {
        }
    }


    private void openConnect() {

        imgConnect.setImageDrawable(getResources().getDrawable(R.drawable.connected));

    }

    private void closeConnect() {
        imgConnect.setImageDrawable(getResources().getDrawable(R.drawable.disconnected));


    }



    @Override
    public void result(int openClose) {
        usbState=openClose;
        if (blueState||usbState==0) {
            openConnect();

        } else {
            closeConnect();
        }
    }

    @Override
    public void resultString(String result) {

    }






}
