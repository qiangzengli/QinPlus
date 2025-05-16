package com.weiyin.qinplus.rest;


import com.weiyin.qinplus.rest.api.ApiService;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   :  Retrofit客户端
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class RetrofitClient {

    private static RetrofitClient retrofitClient;

    private static final int CONNECTION_TIME_OUT = 15;
    private static final int READ_TIME_OUT = 15;
    private static final int WRITE_TIME_OUT = 15;

    private ApiService apiService;
    private String baseUrl;

    private RetrofitClient(String baseUrl) {
        if (StringUtils.isEmpty(baseUrl)) {
            this.baseUrl = RequestConfigs.BASE_URL + RequestConfigs.ROOT_PATH;
        } else {
            this.baseUrl = baseUrl;
        }
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS).setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static RetrofitClient getInstance() {
        if (retrofitClient == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitClient == null) {
                    retrofitClient = new RetrofitClient("");
                }
            }
        }
        return retrofitClient;
    }

    public static RetrofitClient getInstance(String baseUrl) {
        return new RetrofitClient(baseUrl);
    }

    public ApiService getService() {
        return apiService;
    }

}
