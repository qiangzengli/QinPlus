package com.weiyin.qinplus.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.commontool.CityUtil;
import com.weiyin.qinplus.commontool.Conver;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.rest.BizException;
import com.weiyin.qinplus.rest.DataService;
import com.weiyin.qinplus.rest.HttpRequestListener;
import com.weiyin.qinplus.rest.RequestConfigs;
import com.weiyin.qinplus.rest.RetrofitClient;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app  启动用户个人信息页面Activity
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class StartUserActivity extends BaseActivity implements View.OnClickListener, HttpRequestListener<ResponseBody> {
    public static final String TAG = StartUserActivity.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    public static StartUserActivity startUserActivity;
    private EditText userNameEditText, userEmailEditText, userWeiBoEditText;
    private TextView userCityTextView, userBirthdayTextView, userSexTextView;
    private List<String> options1Items;
    private List<List<String>> options1Items1;
    private List<List<List<String>>> options1Items2;
    private int age;
    private Bitmap bitmap;

    private String birthday;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (startUserActivity != null) {
            startUserActivity = null;
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_user);
        startUserActivity = this;
        LayoutHelper layoutHelper = new LayoutHelper(this);
        layoutHelper.scaleView(findViewById(R.id.activityStartUserLayout));
        initView();
    }

    private void initView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activityStartUserLayout);
        bitmap = Constant.readBitMap(this, R.drawable.opern_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        relativeLayout.setBackground(bitmapDrawable);

        ImageView back = (ImageView) findViewById(R.id.back);
        userNameEditText = (EditText) findViewById(R.id.userNameEditText);
        userEmailEditText = (EditText) findViewById(R.id.userEmailEditText);
        userWeiBoEditText = (EditText) findViewById(R.id.userWeiBoEditText);

        userCityTextView = (TextView) findViewById(R.id.userCityTextView);
        userBirthdayTextView = (TextView) findViewById(R.id.userBirthdayTextView);
        userSexTextView = (TextView) findViewById(R.id.userSexTextView);
        TextView activityStartLoginSubject = (TextView) findViewById(R.id.activityStartLoginSubject);

        userCityTextView.setOnClickListener(this);
        userSexTextView.setOnClickListener(this);
        userBirthdayTextView.setOnClickListener(this);
        activityStartLoginSubject.setOnClickListener(this);
        back.setOnClickListener(this);

        options1Items = new ArrayList<>();
        options1Items1 = new ArrayList<>();
        options1Items2 = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.back) {
            // 返回按钮
            finish();
        }
        else if (viewId == R.id.userCityTextView) {
            // 城市选择
            CityUtil c = new CityUtil();
            c.getCityList();
            options1Items = c.getCity();
            options1Items1 = c.getCity1();
            options1Items2 = c.getCity2();
            showPickerView(Constant.CITY);
        }
        else if (viewId == R.id.userBirthdayTextView) {
            // 生日选择
            initTimePicker();
        }
        else if (viewId == R.id.userSexTextView) {
            // 性别选择
            showPickerView(Constant.SEX);
        }
        else if (viewId == R.id.activityStartLoginSubject) {
            // 提交表单
            String name = userNameEditText.getText().toString();
            String city = userCityTextView.getText().toString();
            String sex = userSexTextView.getText().toString();
            String email = userEmailEditText.getText().toString();
            String weibo = userWeiBoEditText.getText().toString();

            // 表单验证
            if (StringUtils.isEmpty(name)) {
                ToastUtil.showTextToast(StartUserActivity.this, "姓名不能为空");
                return;
            }
            if (StringUtils.isEmpty(city)) {
                ToastUtil.showTextToast(StartUserActivity.this, "城市不能为空");
                return;
            }
            if (StringUtils.isEmpty(birthday + "")) {
                ToastUtil.showTextToast(StartUserActivity.this, "生日不能为空");
                return;
            }
            if (StringUtils.isEmpty(sex)) {
                ToastUtil.showTextToast(StartUserActivity.this, "性别不能为空");
                return;
            }

            // 提交数据
            subject(name, sex, birthday, city, weibo, email);
        }
    }

    private void subject(String name, String sex, String birthday, String city, String weibo, String email) {
        name = name.replace(" ", "");
        if (!StringUtils.isEmpty(weibo)) {
            weibo = weibo.replace(" ", "");
        }
        if (!StringUtils.isEmpty(email)) {
            email = email.replace(" ", "");
        }
        city = city.replace(" ", "");
        DataService dataService = new DataService(RetrofitClient.getInstance().getService());
        Map<String, String> map = new HashMap<>();
        map.put("appVersion", Constant.getVersionName(this));
        map.put("platformType", Constant.CODE);
        map.put("userId", WinYinPianoApplication.strUid);
        map.put("nickname", name);
        map.put("sex", sex);
        map.put("birthday", birthday);
        map.put("city", city);
        map.put("email", email);
        map.put("mobile", "");
        map.put("weixinNum", "");
        map.put("qqNum", "");
        map.put("weibo", weibo);
        map.put("otherNum", "");
        map.put("hobby", "");
        map.put("address", "");
        map.put("regfrom", Constant.REGROW);
        map.put("comment", "");
        LogUtil.i(TAG,Constant.getVersionName(this)+"   "+ Constant.CODE+ "  "+  WinYinPianoApplication.strUid+" " + name+ "  "+sex+" "+age+" "+city  +"  "+ Constant.REGROW);
        dataService.updateUserInfo(map, this);
    }

    private void isDeviceActState() {
        startActivity(new Intent(StartUserActivity.this, StartingActivity.class));
        StartLoginActivity.startLoginActivity.finish();
        StartVerificationActivity.startVerificationActivity.finish();
        finish();
    }

    private void showPickerView(final String type) {
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                switch (type) {
                    case Constant.AGE:

                        break;
                    case Constant.CITY:
                        userCityTextView.setText(options1Items.get(options1) + " " + options1Items1.get(options1).get(option2) + " " + options1Items2.get(options1).get(option2).get(options3));
                        break;
                    case Constant.SEX:
                        userSexTextView.setText(options1Items.get(options1));
                        break;
                    default:
                        break;
                }
            }
        }).build();
        switch (type) {
            case Constant.AGE:

                break;
            case Constant.CITY:
                pvOptions.setPicker(options1Items, options1Items1, options1Items2);
                break;
            case Constant.SEX:
                options1Items.clear();
                options1Items.add("男");
                options1Items.add("女");
                pvOptions.setPicker(options1Items);
                break;
            default:

                break;
        }
        pvOptions.show();
    }

    private void initTimePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(1970, 0, 01);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2019, 11, 28);
        //时间选择器
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
//                age = Integer.valueOf(Conver.transferLongToDate("yyyy", System.currentTimeMillis() - date.getTime())) - 1970;
//
//                if (age < 0) {
//                    age = 0;
//                }
//                userAgeTextView.setText(age + "岁" + "(" + Conver.transferLongToDate("yyyy-MM-dd", date.getTime()) + ")");

                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                birthday=simpleDateFormat.format(date);
                userBirthdayTextView.setText(birthday);


            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(false)
                .setDividerColor(Color.DKGRAY)
//                .setContentSize(21)
                .setContentTextSize(21)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setBackgroundId(0x00FFFFFF)
                .setDecorView(null)
                .build();
        pvTime.show();
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
            case RequestConfigs.UPDATE_USER_INFO:
                try {
                    String json = baseResponse.string();
                    LogUtil.i(TAG,json);
                    JSONObject jsonObject = new JSONObject(json);
                    int msgString = jsonObject.getInt(Constant.RESP_CODE);
                    if (0 == msgString) {
                        isDeviceActState();
                    } else {
                        ToastUtil.showTextToast(StartUserActivity.this, "用户信息提交失败");
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
