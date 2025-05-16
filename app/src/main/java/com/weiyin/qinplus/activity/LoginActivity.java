package com.weiyin.qinplus.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.weiyin.qinplus.ui.tv.view.CircleImageView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : 退出登录
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, HttpRequestListener<ResponseBody> {
    public final String TAG = LoginActivity.class.getSimpleName();
    private RelativeLayout outLoginRl, headImageViewRl;
    private CircleImageView personCenterUserHeadImage;
    private TextView personCenterNameTextView;

    private Bitmap bitmap;

    private SharedPreferences sharedPreferences;

    @SuppressLint("HandlerLeak")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LayoutHelper layoutHelper = new LayoutHelper(this);
        layoutHelper.scaleView(findViewById(R.id.activity_login));
        initView();
        initViewData();
    }

    private void initView() {
        ImageView backImageView = (ImageView) findViewById(R.id.main_login_activity_back_imageview);
        outLoginRl = (RelativeLayout) findViewById(R.id.login_activity_out_login_rl);
        headImageViewRl = (RelativeLayout) findViewById(R.id.login_activity_head_imageview_rl);
        personCenterUserHeadImage = (CircleImageView) findViewById(R.id.login_activity_personcenter_usehead_image);
        personCenterNameTextView = (TextView) findViewById(R.id.login_activity_personcenter_name_textview);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_login);
        bitmap = Constant.readBitMap(this, R.drawable.opern_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        relativeLayout.setBackground(bitmapDrawable);

        outLoginRl.setOnClickListener(this);
        backImageView.setOnClickListener(this);
    }

    private void initViewData() {
        sharedPreferences = getSharedPreferences(Constant.USER, 0);
        String imageUrl = sharedPreferences.getString(Constant.HEAD_IMAGE_URL, "");
        String strName = sharedPreferences.getString(Constant.NICKNAME, "");
        if (!StringUtils.isEmpty(imageUrl) || !StringUtils.isEmpty(strName)) {
            /* imageView.setVisibility(View.GONE); */
            outLoginRl.setVisibility(View.VISIBLE);
            headImageViewRl.setVisibility(View.VISIBLE);
            if (!StringUtils.isEmpty(imageUrl)) {
                Glide.with(this).load(imageUrl).into(personCenterUserHeadImage);
            }
            if (!StringUtils.isEmpty(strName)) {
                personCenterNameTextView.setText(strName);
            }
        } else {
            /* imageView.setVisibility(View.VISIBLE); */
            outLoginRl.setVisibility(View.GONE);
            headImageViewRl.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
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

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.main_login_activity_back_imageview) {
            // 返回按钮 - 结束当前Activity
            finish();
        } else if (viewId == R.id.login_activity_out_login_rl) {
            // 退出登录操作
            login(sharedPreferences.getString(Constant.UNION_ID, ""), "2", "2");
        }
    }

    @Override
    public void onCompleted(String url) {

    }

    @Override
    public void onError(BizException e) {
        ToastUtil.showTextToast(this, e.getMsg());
    }

    @Override
    public void onNext(ResponseBody baseResponse, String url) {
        switch (url) {
            case RequestConfigs.USER_LOG_INFO:
                try {
                    JSONObject jsonObject = new JSONObject(baseResponse.string());
                    if (0 == jsonObject.getInt(Constant.RESP_CODE)) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        editor.commit();
                        outLoginRl.setVisibility(View.GONE);
                        headImageViewRl.setVisibility(View.GONE);
                        WinYinPianoApplication.strUid = "";
                        Intent intent = new Intent(LoginActivity.this, StartLoginActivity.class);
                        intent.putExtra(Constant.LOGIN_ACTIVITY, "1");
                        startActivity(intent);
                        MainActivity.mainActivity.finish();
                        finish();
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
