package com.weiyin.qinplus.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app  启动填写手机号码页面Activity
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class StartLoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = StartLoginActivity.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    public static StartLoginActivity startLoginActivity;
    private EditText editTextPhone;

    private String mark;

    private Bitmap bitmap;
    private TextView supportedCountriesTv;
    private EventHandler eh;
    List<String> countyList;
    private LinearLayout supportedCountriesTvLinearLayout;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    ToastUtil.showTextToast(StartLoginActivity.this, "已发送验证码");
                    break;
                case -1:
                    ToastUtil.showTextToast(StartLoginActivity.this, (String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_login);
        startLoginActivity = this;
        LayoutHelper layoutHelper = new LayoutHelper(this);
        mark = getIntent().getStringExtra(Constant.LOGIN_ACTIVITY);
        layoutHelper.scaleView(findViewById(R.id.activityStartLoginLayout));
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.USER, 0);
        String strName = sharedPreferences.getString(Constant.NICKNAME, "");
        if ("".equals(strName)) {
            initView();
        } else {
            WinYinPianoApplication.strUid = sharedPreferences.getString(Constant.USER_ID,"");
            toStart();
        }
        initMob();
    }

    private void initMob() {
        countyList = new ArrayList<>();
        eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        if (countyList != null && countyList.size() == 0) {
                            //返回支持发送验证码的国家列表
                            Map<Character, ArrayList<String[]>> first = SMSSDK.getGroupedCountryList();
//							System.out.println("第一层数组："+first);
                            Set set = first.keySet();
                            for (Object obj : set) {
                                //				System.out.println("键:"+obj+"  值:"+first.get(obj));
                                List<String[]> second = first.get(obj);
//								System.out.println("第二层数组："+second);
                                for (int i = 0; i < second.toArray().length; i++) {
                                    String[] thirst = second.get(i);
                                    String str = thirst[0] + " " + thirst[1];
                                    countyList.add(str);
//									System.out.println("----"+countyList);
                                }
                            }
                            LogUtil.i(TAG, countyList);
                        }

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
        SMSSDK.getSupportedCountries();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
        if (startLoginActivity != null) {
            startLoginActivity = null;
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    private void toStart() {
        startActivity(new Intent(StartLoginActivity.this, StartingActivity.class));
        finish();
    }

    private void toVerification() {
        Intent intent = new Intent();
        intent.setClass(StartLoginActivity.this, StartVerificationActivity.class);
        LogUtil.i(TAG, editTextPhone.getText() + "");
        if (!StringUtils.isEmpty(mark)) {
            intent.putExtra(Constant.LOGIN_ACTIVITY, mark);
        }
        intent.putExtra("countries",supportedCountriesTv.getText().toString());
        intent.putExtra("phone", editTextPhone.getText().toString());
        startActivity(intent);
    }

    private void initView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activityStartLoginLayout);
        bitmap = Constant.readBitMap(this, R.drawable.opern_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        relativeLayout.setBackground(bitmapDrawable);

        supportedCountriesTv = (TextView) findViewById(R.id.supportedCountriesTv);
        supportedCountriesTvLinearLayout = (LinearLayout) findViewById(R.id.supportedCountriesTvLinearLayout);
        editTextPhone = (EditText) findViewById(R.id.activityStartLoginEdPhone);
        TextView textViewSubject = (TextView) findViewById(R.id.activityStartLoginSubject);

        textViewSubject.setOnClickListener(this);
        supportedCountriesTvLinearLayout.setOnClickListener(this);
    }
    private void showPickerView() {
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String county = countyList.get(options1);
                supportedCountriesTv.setText("+" + county.substring(county.indexOf(" ") + 1));
            }
        }).build();
        pvOptions.setPicker(countyList);
        pvOptions.show();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.supportedCountriesTvLinearLayout) {
            // 显示国家选择器
            showPickerView();
        }
        else if (viewId == R.id.activityStartLoginSubject) {
            // 登录提交处理
            if (editTextPhone != null && !StringUtils.isEmpty(editTextPhone.getText())) {
                toVerification();  // 执行验证流程
            } else {
                ToastUtil.showTextToast(StartLoginActivity.this, "请输入正确的手机号");
            }
        }
    }
}
