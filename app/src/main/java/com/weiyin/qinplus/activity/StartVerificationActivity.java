package com.weiyin.qinplus.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.rest.BizException;
import com.weiyin.qinplus.rest.DataService;
import com.weiyin.qinplus.rest.HttpRequestListener;
import com.weiyin.qinplus.rest.RequestConfigs;
import com.weiyin.qinplus.rest.RetrofitClient;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;
import com.weiyin.qinplus.ui.tv.utils.Utils;
import com.weiyin.qinplus.ui.tv.utils.VerifyCodeUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app  启动注册登录页面Activity
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class StartVerificationActivity extends BaseActivity implements View.OnClickListener, HttpRequestListener<ResponseBody> {
    private static final String TAG = StartVerificationActivity.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    public static StartVerificationActivity startVerificationActivity;
    private TextView activityStartVerificationSendCode;
    private EditText activityStartVerificationEdPhone;
    private String emailOrPho, countries;
    private TextView activityStartLoginSubject;
    /**
     * 是否填写用户个人信息
     */
    private boolean raged;

    private String mark;

    private SharedPreferences sharedPreferences;
    private EventHandler eh;

    private Bitmap bitmap;
    // 短信注册，随机产生头像
    private static final String[] AVATARS = {
            "http://tupian.qqjay.com/u/2011/0729/e755c434c91fed9f6f73152731788cb3.jpg",
            "http://99touxiang.com/public/upload/nvsheng/125/27-011820_433.jpg",
            "http://img1.touxiang.cn/uploads/allimg/111029/2330264224-36.png",
            "http://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339485237265.jpg",
            "http://diy.qqjay.com/u/files/2012/0523/f466c38e1c6c99ee2d6cd7746207a97a.jpg",
            "http://img1.touxiang.cn/uploads/20121224/24-054837_708.jpg",
            "http://img1.touxiang.cn/uploads/20121212/12-060125_658.jpg",
            "http://img1.touxiang.cn/uploads/20130608/08-054059_703.jpg",
            "http://diy.qqjay.com/u2/2013/0422/fadc08459b1ef5fc1ea6b5b8d22e44b4.jpg",
            "http://img1.2345.com/duoteimg/qqTxImg/2012/04/09/13339510584349.jpg",
            "http://img1.touxiang.cn/uploads/20130515/15-080722_514.jpg",
            "http://diy.qqjay.com/u2/2013/0401/4355c29b30d295b26da6f242a65bcaad.jpg"
    };
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    ToastUtil.showTextToast(StartVerificationActivity.this, "已发送验证码");
                    break;
                case -1:
                    ToastUtil.showTextToast(StartVerificationActivity.this, (String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_verification);
        startVerificationActivity = this;
        LayoutHelper layoutHelper = new LayoutHelper(this);
        layoutHelper.scaleView(findViewById(R.id.activityStartVerificationLayout));
        emailOrPho = getIntent().getStringExtra("phone");
        countries = getIntent().getStringExtra("countries");
        mark = getIntent().getStringExtra(Constant.LOGIN_ACTIVITY);
        LogUtil.i(TAG, emailOrPho);
        initView();
        initMob();
    }

    private void initView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activityStartVerificationLayout);
        bitmap = Constant.readBitMap(this, R.drawable.opern_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        relativeLayout.setBackground(bitmapDrawable);

        sharedPreferences = getSharedPreferences(Constant.USER, 0);
        ImageView back = (ImageView) findViewById(R.id.back);
        activityStartVerificationEdPhone = (EditText) findViewById(R.id.activityStartVerificationEdPhone);
        activityStartVerificationSendCode = (TextView) findViewById(R.id.activityStartVerificationSendCode);
        activityStartLoginSubject = (TextView) findViewById(R.id.activityStartLoginSubject);

        activityStartVerificationSendCode.setOnClickListener(this);
      //  activityStartLoginSubject.setOnClickListener(this);


        //禁用登陆按钮的 重复点击事件
        RxView.clicks(activityStartLoginSubject).throttleFirst(5, TimeUnit.SECONDS).subscribe(new Consumer<Object>() {

            @Override
            public void accept(Object o) throws Exception {

                String code = activityStartVerificationEdPhone.getText().toString();
                if (StringUtils.isEmpty(code)) {
                    ToastUtil.showTextToast(StartVerificationActivity.this, getString(R.string.notCode));
                    return;
                }
                registerUser("+86", emailOrPho);

            }
        });




        back.setOnClickListener(this);
        activityStartVerificationEdPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isEmpty(s.toString())) {
                    activityStartLoginSubject.setSelected(false);
                } else {
                    activityStartLoginSubject.setSelected(true);
                }
            }
        });
    }

    private void initMob() {
        eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {
                if (event == SMSSDK.EVENT_SUBMIT_USER_INFO) {
                    // 短信注册成功后，返回MainActivity,然后提示新好友
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        LogUtil.i(TAG, "资料已提交");
                        String code = activityStartVerificationEdPhone.getText().toString();
                        SMSSDK.submitVerificationCode(countries, emailOrPho, code);
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                }
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        LogUtil.i(TAG, "提交验证码成功");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constant.NICKNAME, emailOrPho);
                        editor.putString(Constant.UNION_ID, emailOrPho);
                        editor.apply();
                        editor.commit();
                        register("2", emailOrPho);
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        Message message = new Message();
                        message.what = 100;
                        mHandler.sendMessage(message);
                        LogUtil.i(TAG, "获取验证码成功");
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                    try {
                        Message message = new Message();

                        JSONObject jsonObject = new JSONObject(((Throwable) data).getMessage());
                        message.what = -1;
                        message.obj = jsonObject.getString("detail");
                        mHandler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        /* 注册短信回调 */
        SMSSDK.registerEventHandler(eh);
    }

    private void register(String type, String logName) {
        WinYinPianoApplication.strUid = sharedPreferences.getString(Constant.USER_ID, "");
        try {
            if ("".equals(WinYinPianoApplication.strUid)) {
                String uuid = Utils.getMyUUID(this);
                register(uuid, type, logName);
            } else {
                login(logName, type, "1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void login(String logName, String type, String userState) {
        LogUtil.i(TAG, logName);
        String uuid = Utils.getMyUUID(this);

        DataService dataService = new DataService(RetrofitClient.getInstance().getService());
        Map<String, String> map = new HashMap<>();
        map.put("appVersion", Constant.getVersionName(this));
        map.put("platformType", Constant.CODE);
        map.put("deviceModel", "");
        map.put("macAddress", "");
        map.put("deviceId", uuid);
        map.put("userId", WinYinPianoApplication.strUid);
        map.put("regMode", type);
        map.put("logName", logName);
        map.put("userState", userState);
        map.put("password", "");
        dataService.userLogInfo(map, this);
    }

    void register(String uuid, String type, String logName) {
        DataService dataService = new DataService(RetrofitClient.getInstance().getService());
        Map<String, String> map = new HashMap<>();
        map.put("appVersion", Constant.getVersionName(this));
        map.put("platformType", Constant.CODE);
        map.put("deviceId", uuid);
        map.put("regMode", type);
        map.put("logName", logName);
        map.put("password", "");
        dataService.userRegister(map, this);
    }

    /**
     * 提交用户信息
     *
     * @param country 城市
     * @param phone   手机号
     */
    private void registerUser(String country, String phone) {
        Random rnd = new Random();
        int id = Math.abs(rnd.nextInt());
        String uid = String.valueOf(id);
        String nickName = "SmsSDK_User_" + uid;
        String avatar = AVATARS[id % 12];
        SMSSDK.submitUserInfo(uid, nickName, avatar, country, phone);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
        if (startVerificationActivity != null) {
            startVerificationActivity = null;
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.back) {
            // Handle back button click
            finish();
        }
        else if (viewId == R.id.activityStartVerificationSendCode) {
            // Handle verification code sending
            if (StringUtils.isEmpty(emailOrPho)) {
                ToastUtil.showTextToast(this, getString(R.string.errorEnterEmailOrPhoneNumber));
                return;
            }

            VerifyCodeUtils.sendCode(this, activityStartVerificationSendCode);
            SMSSDK.getVerificationCode(countries, emailOrPho);
        }
    }


    @Override
    public void onCompleted(String url) {

    }

    @Override
    public void onError(BizException e) {

    }

    @Override
    public void onNext(ResponseBody baseResponse, String url) {
        switch (url) {
            case RequestConfigs.USER_REGISTER:
                try {
                    JSONObject jsonObject = new JSONObject(baseResponse.string());
                    if (jsonObject.getInt(Constant.RESP_CODE) == 0) {
                        WinYinPianoApplication.strUid = jsonObject.getString("userId");
                        raged = jsonObject.getBoolean(Constant.REGED);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constant.USER_ID, WinYinPianoApplication.strUid);
                        editor.apply();
                        editor.commit();
                        LogUtil.i(TAG, Constant.USER_ID + "=" + WinYinPianoApplication.strUid);
                    }
                    login(sharedPreferences.getString(Constant.UNION_ID, ""), "2", "1");
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                break;
            case RequestConfigs.USER_LOG_INFO:
                try {
                    JSONObject jsonObject = new JSONObject(baseResponse.string());
                    if (jsonObject.getInt(Constant.RESP_CODE) == 0) {
                        //登录成功
                        if (StringUtils.isEmpty(mark)) {
                            if (raged) {
                                startActivity(new Intent(StartVerificationActivity.this, StartingActivity.class));
                                StartLoginActivity.startLoginActivity.finish();
                                finish();
                            } else {
                                startActivity(new Intent(StartVerificationActivity.this, StartUserActivity.class));
                            }
                        } else {
                            startActivity(new Intent(StartVerificationActivity.this, MainActivity.class));
                            StartLoginActivity.startLoginActivity.finish();
                            finish();
                        }
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
