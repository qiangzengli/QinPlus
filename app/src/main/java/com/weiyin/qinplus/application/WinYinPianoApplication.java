package com.weiyin.qinplus.application;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

import com.google.gson.Gson;
import com.mob.MobApplication;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.entity.MusicBookEntity;
import com.weiyin.qinplus.entity.MusicListItemEntity;
import com.weiyin.qinplus.rest.BizException;
import com.weiyin.qinplus.rest.DataService;
import com.weiyin.qinplus.rest.HttpRequestListener;
import com.weiyin.qinplus.rest.RequestConfigs;
import com.weiyin.qinplus.rest.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app入口Application
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class WinYinPianoApplication extends MobApplication {

    /**
     * 实例化一个app
     */
    public static WinYinPianoApplication instance;
    private ArrayList<Activity> activityTackList;

    public static String strUid = "";
    public static String strAccountUrl = "";
    public static String strNickName = "";
    public static String strUrl = "";

    public static boolean isWaterfall = false;
    public static ArrayList<MusicListItemEntity> musicListItemEntityArrayList = new ArrayList<>();

    public static ArrayList<MusicBookEntity> musicBookEntity03 = new ArrayList<>();
    public static ArrayList<MusicBookEntity> musicBookEntity02 = new ArrayList<>();
    public static ArrayList<MusicBookEntity> musicBookEntity01 = new ArrayList<>();

    public static IWXAPI api;

    public static HttpData httpData;

    @Override
    public void onCreate() {
        super.onCreate();


        MultiDex.install(this);
        getHttpBookCacheUrl();
        getHttpAllCacheUrl();

        instance = this;
        activityTackList = new ArrayList<Activity>();
        WinYinPianoCrashHandler.getInstance().init(this);

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID, false);

        // 将该app注册到微信
        api.registerApp(Constant.APP_ID);
    }

    public void getHttpAllCacheUrl() {
        DataService dataService = new DataService(RetrofitClient.getInstance().getService());
        dataService.getAll(Constant.getVersionName(this), Constant.CODE, cachecallback);
    }

    public void getHttpBookCacheUrl() {
        DataService dataService = new DataService(RetrofitClient.getInstance().getService());
        dataService.getBookAll(Constant.getVersionName(this), Constant.CODE, cachecallback);
    }

    HttpRequestListener<ResponseBody> cachecallback = new HttpRequestListener<ResponseBody>() {
        @Override
        public void onCompleted(String url) {
            LogUtil.i("WinYinPianoApplication", "onCompleted=" + url);
        }

        @Override
        public void onError(BizException e) {
            if (httpData != null) {
                httpData.httpDataResult("error");
            }
            LogUtil.i("WinYinPianoApplication", "onError=" + e.getMsg() + "=----" + e.getUrl());
        }

        @Override
        public void onNext(ResponseBody baseResponse, String url) {
            SharedPreferences settings = getSharedPreferences("CacheUrl", 0);
            SharedPreferences.Editor local = settings.edit();
            switch (url) {
                case RequestConfigs.GET_ALL:
                    try {
                        analysisAllJson(baseResponse.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (httpData != null) {
                        httpData.httpDataResult("ok");
                    }
                    break;
                case RequestConfigs.GET_BOOK_ALL:
                    try {
                        analysisBookJson(baseResponse.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (httpData != null) {
                        httpData.httpDataResult("ok");
                    }
                    break;
                default:

                    break;
            }
            local.apply();
            local.commit();
        }
    };

    public void getJson() {
        if (musicBookEntity01.size() == 0 || musicBookEntity02.size() == 0 || musicBookEntity03.size() == 0) {
            getHttpBookCacheUrl();
        }
        if (musicListItemEntityArrayList.size() == 0) {
            getHttpAllCacheUrl();
        }
    }

    public void analysisAllJson(String respondsStr) {
        try {
            LogUtil.i("APP ALL", respondsStr);
            JSONObject jsonObject = new JSONObject(respondsStr);
            if (jsonObject.getInt("respCode") == 0) {
                musicListItemEntityArrayList.clear();
                JSONArray musicArray = jsonObject.getJSONArray("list");
                int ilength = musicArray.length();
                for (int i = 0; i < ilength; i++) {
                    Gson gson = new Gson();
                    MusicListItemEntity item = gson.fromJson(musicArray.get(i).toString(), MusicListItemEntity.class);
                    musicListItemEntityArrayList.add(item);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void analysisBookJson(String respondsStr) {
        try {
            LogUtil.i("APP book", respondsStr);
            JSONObject jsonObject = new JSONObject(respondsStr);
            if (jsonObject.getInt("respCode") == 0) {
                strUrl = jsonObject.getString("url");
                musicBookEntity03.clear();
                musicBookEntity02.clear();
                musicBookEntity01.clear();
                JSONArray musicArray = jsonObject.getJSONArray("list");
                int ilength = musicArray.length();
                for (int i = 0; i < ilength; i++) {
                    Gson gson = new Gson();
                    MusicBookEntity item = gson.fromJson(musicArray.get(i).toString(), MusicBookEntity.class);
                    if (item.getMusicSort().equals("01")) {
                        musicBookEntity01.add(item);
                    } else if (item.getMusicSort().equals("02")) {
                        musicBookEntity02.add(item);
                    } else if (item.getMusicSort().equals("03")) {
                        musicBookEntity03.add(item);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将应用程序的任务栈中的一activity实例添加到activitystack中
     *
     * @param activity
     */
    public void addActivity2Stack(Activity activity) {
        activityTackList.add(activity);
    }

    /**
     * 经activity实例从activitystack中移除
     *
     * @param activity
     */
    public void removeActivityFromStack(Activity activity) {
        activityTackList.remove(activity);
    }

    public static WinYinPianoApplication getInstance() {
        return instance;
    }

    public void exit() {
        try {
            for (Activity activity : activityTackList) {
                if (activity != null) {
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public interface HttpData {
        /**
         * http返回
         *
         * @param result 返回结果
         */
        void httpDataResult(String result);
    }
}
