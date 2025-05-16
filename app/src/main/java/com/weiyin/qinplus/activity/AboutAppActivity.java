package com.weiyin.qinplus.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.commontool.AppHelperUtil;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.rest.BizException;
import com.weiyin.qinplus.rest.DataService;
import com.weiyin.qinplus.rest.HttpRequestListener;
import com.weiyin.qinplus.rest.RequestConfigs;
import com.weiyin.qinplus.rest.RetrofitClient;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import okhttp3.ResponseBody;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : 关于APP
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class AboutAppActivity extends BaseActivity implements View.OnClickListener, HttpRequestListener<ResponseBody> {
    private ImageView detailTabLine, logoImageView, loadingImageView, loadingFaultImageView;
    private String resCode, appUrl, appContent, newCode;

    /**
     * 开始更新按钮, 提示, 进度条大小, 下载进度, 旧版本, 新版本, 重点推荐, 推荐内容, logo版本, logo
     */
    private TextView aboutAppOnClick, aboutAppHint, dataTextView, downloadTextView,
            oldCodeTextView, newCodeTextView, zingDianTextView,
            contentTextView, newCodeLogoTextView, newCodeLogoTextView1,
            loadingTextView;
    /**
     * 进度条布局, 线
     */
    private RelativeLayout downloadRl, xianRl, loadingFaultTextView, classicalButtonRl;
    /**
     * 进度条
     */
    private SeekBar progressBar;
    /**
     * 屏幕寬度, 坐标
     */
    private int screenWidth, screenIndex = 1, max = 0;

    private boolean textOnClick = false, out = false;

    private AnimationDrawable animationDrawable;

    private Bitmap bitmap;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (classicalButtonRl.getVisibility() == View.VISIBLE) {
                stopLoading();
                classicalButtonRl.setVisibility(View.GONE);
            }
            switch (msg.what) {
                case 1:
                    max = (int) msg.obj;
                    progressBar.setMax(max);
                    break;
                case 2:
                    int length = (int) msg.obj;
                    if (max != 0) {
                        DecimalFormat df = new DecimalFormat("0.00");

                        downloadTextView.setText("已下载" + (df.format(((double) length * 100 / (double) max))) + "%");
                    }
                    if (max != 0) {
                        dataTextView.setText(format(length) + "/" + format(max));
                    }
                    progressBar.setProgress(length);
                    break;
                case 3:
                    File file = (File) msg.obj;
                    if (file.exists()) {
                        aboutAppOnClick.setText("开始更新");
                        textOnClick = false;
                        AppHelperUtil.installApk(AboutAppActivity.this, file);
                    }
                    break;
                case Constant.INTERNET_UPDATE_APP:

                    break;
                case Constant.RESPONESE_EXCEPTION:
                    if (classicalButtonRl.getVisibility() == View.GONE) {
                        classicalButtonRl.setVisibility(View.VISIBLE);
                    }

                    break;
                default:
                    break;
            }
        }

    };

    private String format(int size) {
        return size / 1000000 + "M";
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String cachePath = (getExternalFilesDir("upgrade_apk") + File.separator + getPackageName() + ".apk");
            fileDownLoad(appUrl, cachePath, null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        LayoutHelper layoutHelper = new LayoutHelper(this);
        layoutHelper.scaleView(findViewById(R.id.activity_about_app));
        initView();
        initTabLineWidth();
        getUpdateApp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    private void stopLoading() {
        animationDrawable.stop();
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
        animationDrawable.start();
    }

    public void initView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_about_app);
        bitmap = Constant.readBitMap(this, R.drawable.opern_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        relativeLayout.setBackground(bitmapDrawable);

        classicalButtonRl = (RelativeLayout) findViewById(R.id.about_app_classical_button_rl);
        loadingImageView = (ImageView) findViewById(R.id.about_app_classical_imageview_loading);
        loadingFaultImageView = (ImageView) findViewById(R.id.about_app_classical_imageview_loadfault);
        loadingTextView = (TextView) findViewById(R.id.about_app_classical_textview_loading);
        loadingFaultTextView = (RelativeLayout) findViewById(R.id.about_app_classical_imageview_loadfault_rl);

        loadingImageView.setImageResource(R.drawable.imageview_rotate);
        animationDrawable = new AnimationDrawable();
        animationDrawable = (AnimationDrawable) loadingImageView.getDrawable();


        RelativeLayout oneTabRl = (RelativeLayout) findViewById(R.id.activity_about_app_onetab_rl);
        RelativeLayout twoTabRl = (RelativeLayout) findViewById(R.id.activity_about_app_twotab_rl);
        RelativeLayout threeTabRl = (RelativeLayout) findViewById(R.id.activity_about_app_threetab_rl);

        detailTabLine = (ImageView) findViewById(R.id.activity_about_app_iv_sboard_detail_tabline);
        ImageView backImageView = (ImageView) findViewById(R.id.about_app_activity_back_imageview);

        aboutAppOnClick = (TextView) findViewById(R.id.about_app_onclic);
        aboutAppHint = (TextView) findViewById(R.id.about_app_hint);
        downloadRl = (RelativeLayout) findViewById(R.id.about_app_download_rl);
        dataTextView = (TextView) findViewById(R.id.about_app_data_textview);
        downloadTextView = (TextView) findViewById(R.id.about_app_download_textview);
        progressBar = (SeekBar) findViewById(R.id.about_app_progressBar);
        oldCodeTextView = (TextView) findViewById(R.id.about_app_old_code_textview);
        newCodeTextView = (TextView) findViewById(R.id.about_app_new_code_textview);
        xianRl = (RelativeLayout) findViewById(R.id.about_app_xian_rl);
        zingDianTextView = (TextView) findViewById(R.id.about_app_zhongdian_textview);
        contentTextView = (TextView) findViewById(R.id.about_app_content_textview);

        logoImageView = (ImageView) findViewById(R.id.about_app_logo_imageview);
        newCodeLogoTextView = (TextView) findViewById(R.id.about_app_new_code_logo_textview);
        newCodeLogoTextView1 = (TextView) findViewById(R.id.about_app_new_code_logo_textview1);


        aboutAppOnClick.setOnClickListener(this);
        oneTabRl.setOnClickListener(this);
        twoTabRl.setOnClickListener(this);
        threeTabRl.setOnClickListener(this);
        backImageView.setOnClickListener(this);

        classicalButtonRl.setOnClickListener(this);
    }

    /**
     * 设置tabLine属性
     */
    private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) detailTabLine
                .getLayoutParams();
        lp.width = screenWidth / 6;
        lp.height = 5;
        lp.leftMargin = screenWidth / 3 * screenIndex - (screenWidth / 3 - screenWidth / 6) / 2 - screenWidth / 6;
        detailTabLine.setLayoutParams(lp);
    }

    public void updateLp(RelativeLayout.LayoutParams lp) {
        lp.leftMargin = screenWidth / 3 * screenIndex - (screenWidth / 3 - detailTabLine.getWidth()) / 2
                - detailTabLine.getWidth();
    }

    void getUpdateApp() {
        DataService dataService = new DataService(RetrofitClient.getInstance().getService());
        dataService.updateApp(Constant.getVersionName(this), Constant.CODE, this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) detailTabLine
                .getLayoutParams();
        int id = v.getId();

        if (id == R.id.about_app_classical_button_rl) {
            getUpdateApp();
            showLoadDialog();
        } else if (id == R.id.about_app_onclic) {
            if (textOnClick) {
                if (screenIndex == 1) {
                    aboutAppOnClick.setText("开始更新");
                    dataTextView.setText(format(0) + "/" + format(max));
                    DecimalFormat df = new DecimalFormat("0.00");
                    out = true;
                    downloadTextView.setText("已下载" + (df.format(((double) 0 * 100 / (double) max))) + "%");
                    progressBar.setProgress(0);
                    mHandler.removeCallbacksAndMessages(null);
                }
                textOnClick = false;
            } else {
                if (screenIndex == 1) {
                    aboutAppOnClick.setText("取消更新");
                    out = false;
                    new Thread(runnable).start();
                }
                textOnClick = true;
            }
        } else if (id == R.id.activity_about_app_onetab_rl) {
            screenIndex = 1;
            updateLp(lp);
            getUpdateApp();
        } else if (id == R.id.activity_about_app_twotab_rl || id == R.id.activity_about_app_threetab_rl) {
            ToastUtil.showTextToast(AboutAppActivity.this, "暂无此功能");
        } else if (id == R.id.about_app_activity_back_imageview) {
            finish();
        }

        detailTabLine.setLayoutParams(lp);
    }

    /**
     * 更新apk版本更新
     *
     * @param uri        链接地址
     * @param fileDirect 存放的目录
     * @param fileName   文件的名称
     * @return file
     */
    public void fileDownLoad(String uri, String fileDirect, String fileName) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpParams httParams = client.getParams();
        HttpConnectionParams.setConnectionTimeout(httParams, 20000);
        HttpConnectionParams.setSoTimeout(httParams, 20000);

        HttpProtocolParams.setContentCharset(httParams, HTTP.UTF_8);
        HttpProtocolParams.setUseExpectContinue(httParams, false);

        HttpGet get = new HttpGet(uri);
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            HttpResponse response = client.execute(get);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == HttpStatus.SC_OK) {
                HttpEntity httpentity = response.getEntity();
                int max = (int) httpentity.getContentLength();
                Message message = new Message();
                message.obj = max;
                message.what = 1;
                if (mHandler != null) {
                    mHandler.sendMessage(message);
                }

                if (fileDirect != null) {
                    File dire = new File(fileDirect);
                    if (!dire.exists()) {
                        boolean newFile = dire.createNewFile();
                        LogUtil.i(TAG,"newFile="+newFile);
                    }
                    if (dire.exists()) {
                        is = httpentity.getContent();
                        fos = new FileOutputStream(dire);
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        int total = 0;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                            total += len;
                            Message msg = new Message();
                            msg.obj = total;
                            msg.what = 2;
                            if (!out) {
                                mHandler.sendMessage(msg);
                            } else {
                                return;
                            }
                        }
                        fos.flush();
                        fos.close();
                        is.close();
                        if (total == max) {
                            Message message1 = new Message();
                            message1.what = 3;
                            message1.obj = dire;
                            if (mHandler != null) {
                                mHandler.sendMessage(message1);
                            }
                        }
                    }

                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setViewVisibilty() {
        if (resCode.equals("0")) {
            aboutAppOnClick.setVisibility(View.VISIBLE);
            aboutAppHint.setVisibility(View.VISIBLE);
            downloadRl.setVisibility(View.VISIBLE);
            oldCodeTextView.setVisibility(View.VISIBLE);
            newCodeTextView.setVisibility(View.VISIBLE);
            xianRl.setVisibility(View.VISIBLE);
            zingDianTextView.setVisibility(View.VISIBLE);
            contentTextView.setVisibility(View.VISIBLE);

            logoImageView.setVisibility(View.GONE);
            newCodeLogoTextView.setVisibility(View.GONE);
            newCodeLogoTextView1.setVisibility(View.GONE);


            contentTextView.setText(appContent);
            oldCodeTextView.setText("当前版本：" + Constant.getVersionName(AboutAppActivity.this) + "版本");
            newCodeTextView.setText("更新版本：" + newCode + "版本");
        } else {
            aboutAppOnClick.setVisibility(View.GONE);
            aboutAppHint.setVisibility(View.GONE);
            downloadRl.setVisibility(View.GONE);
            oldCodeTextView.setVisibility(View.GONE);
            newCodeTextView.setVisibility(View.GONE);
            xianRl.setVisibility(View.GONE);
            zingDianTextView.setVisibility(View.GONE);
            contentTextView.setVisibility(View.GONE);

            logoImageView.setVisibility(View.VISIBLE);
            newCodeLogoTextView.setVisibility(View.VISIBLE);
            newCodeLogoTextView1.setVisibility(View.VISIBLE);

            newCodeLogoTextView.setText(Constant.getVersionName(AboutAppActivity.this) + "版本");
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
            case RequestConfigs.UPDATE_APP:
                try {
                    JSONObject jsonObject = new JSONObject(baseResponse.string());
                    resCode = jsonObject.getString("respCode");
                    if (jsonObject.getInt(Constant.RESP_CODE) == 0) {
                        appUrl = jsonObject.getString("appBagUrl");
                        appUrl = WinYinPianoApplication.strUrl + appUrl;
                        Log.i("aa", appUrl);
                        appContent = jsonObject.getString("updateInfo");
                        appContent = appContent.replace("\\n", "\n");
                        newCode = jsonObject.getString("appVersion");
                    }
                    setViewVisibilty();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
