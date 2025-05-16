package com.weiyin.qinplus.rest.api;


import com.weiyin.qinplus.rest.BaseResponse;
import com.weiyin.qinplus.rest.RequestConfigs;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : 请求接口类，定义接口路径、请求参数以及回调
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public interface ApiService {
    /**
     * 获取指定曲谱信息
     *
     * @param fieldMap Map<String, String>
     * @return 回调监听
     */
    @FormUrlEncoded
    @POST(RequestConfigs.GET_MUSIC_ITEM)
    Observable<ResponseBody> getMusicItem(@FieldMap Map<String, String> fieldMap);

    /**
     * 获取所有曲谱信息
     *
     * @param appVersion   app版本
     * @param platformType 平台类型
     * @return 回调监听
     */
    @FormUrlEncoded
    @POST(RequestConfigs.GET_ALL)
    Observable<ResponseBody> getAll(@Field("appVersion") String appVersion,
                                    @Field("platformType") String platformType);

    /**
     * 获取所有图书信息
     *
     * @param appVersion   app版本
     * @param platformType 平台类型
     * @return 回调监听
     */
    @FormUrlEncoded
    @POST(RequestConfigs.GET_BOOK_ALL)
    Observable<ResponseBody> getBookAll(@Field("appVersion") String appVersion,
                                        @Field("platformType") String platformType);

    /**
     * 获取指定图书曲谱信息
     *
     * @param fieldMap Map<String, String>
     * @return 回调监听
     */
    @GET(RequestConfigs.GET_MUSIC_OF_BOOK)
    Observable<ResponseBody> getMusicOfBook(@QueryMap Map<String, String> fieldMap);

    /**
     * 获取音乐课堂信息
     *
     * @param appVersion   app版本
     * @param platformType 平台类型
     * @return 回调监听
     */
    @FormUrlEncoded
    @POST(RequestConfigs.GET_MUSIC_CLASS)
    Observable<ResponseBody> getMusicClass(@Field("appVersion") String appVersion,
                                           @Field("platformType") String platformType);

    /**
     * 获取视频教学信息
     *
     * @param appVersion   app版本
     * @param platformType 平台类型
     * @return 回调监听
     */
    @FormUrlEncoded
    @POST(RequestConfigs.GET_VIDEO)
    Observable<ResponseBody> getVideo(@Field("appVersion") String appVersion,
                                      @Field("platformType") String platformType);

    /**
     * 用户注册
     *
     * @param fieldMap Map<String, String>
     * @return 回调监听
     */
    @FormUrlEncoded
    @POST(RequestConfigs.USER_REGISTER)
    Observable<ResponseBody> userRegister(@FieldMap Map<String, String> fieldMap);

    /**
     * 用户登录退出
     *
     * @param fieldMap Map<String, String>
     * @return 回调监听
     */
    @FormUrlEncoded
    @POST(RequestConfigs.USER_LOG_INFO)
    Observable<ResponseBody> userLogInfo(@FieldMap Map<String, String> fieldMap);

    /**
     * 更新用户信息
     *
     * @param fieldMap Map<String, String>
     * @return 回调监听
     */
    @GET(RequestConfigs.UPDATE_USER_INFO)
    Observable<ResponseBody> updateUserInfo(@QueryMap Map<String, String> fieldMap);

    /**
     * APP版本更新
     *
     * @param appVersion   app版本
     * @param platformType 平台类型
     * @return 回调监听
     */
    @FormUrlEncoded
    @POST(RequestConfigs.UPDATE_APP)
    Observable<ResponseBody> updateApp(@Field("appVersion") String appVersion,
                                       @Field("platformType") String platformType);

    /**
     * 设备激活
     *
     * @param fieldMap Map<String, String>
     * @return 回调监听
     */
    @FormUrlEncoded
    @POST(RequestConfigs.ACT_DEVICE)
    Observable<ResponseBody> actDevice(@FieldMap Map<String, String> fieldMap);

    /**
     * 记录用户练习信息
     *
     * @param fieldMap Map<String, String>
     * @return 回调监听
     */
    @GET(RequestConfigs.PUT_PLAY_RECORD)
    Observable<ResponseBody> putPlayRecord(@QueryMap Map<String, String> fieldMap);

    /**
     * 获取最近练习曲谱
     *
     * @param fieldMap Map<String, String>
     * @return 回调监听
     */
    @FormUrlEncoded
    @POST(RequestConfigs.GET_PLAY_LIST)
    Observable<ResponseBody> getPlayList(@FieldMap Map<String, String> fieldMap);

    /**
     * 获取曲谱练习记录
     *
     * @param fieldMap Map<String, String>
     * @return 回调监听
     */
    @FormUrlEncoded
    @POST(RequestConfigs.GET_PLAY_RECORD)
    Observable<ResponseBody> getPlayRecord(@FieldMap Map<String, String> fieldMap);

    /**
     * 曲谱收藏
     *
     * @param fieldMap Map<String, String>
     * @return 回调监听
     */
    @FormUrlEncoded
    @POST(RequestConfigs.PUT_MUSIC_COLLECT)
    Observable<ResponseBody> putMusicCollect(@FieldMap Map<String, String> fieldMap);

    /**
     * 取消曲谱收藏
     *
     * @param fieldMap Map<String, String>
     * @return 回调监听
     */
    @FormUrlEncoded
    @POST(RequestConfigs.DEL_MUSIC_COLLECT)
    Observable<ResponseBody> delMusicCollect(@FieldMap Map<String, String> fieldMap);

    /**
     * 获取收藏信息
     *
     * @param fieldMap Map<String, String>
     * @return 回调监听
     */
    @FormUrlEncoded
    @POST(RequestConfigs.GET_COLLECT_LIST)
    Observable<ResponseBody> getCollectList(@FieldMap Map<String, String> fieldMap);

    /**
     * 检查设备是否激活
     *
     * @param fieldMap Map<String, String>
     * @return 回调监听
     */
    @FormUrlEncoded
    @POST(RequestConfigs.GET_DEVICE_ACT_STATE)
    Observable<ResponseBody> getDeviceActState(@FieldMap Map<String, String> fieldMap);

    /**
     * 下载文件
     *
     * @param fileUrl 文件路径
     * @return 回调监听
     */
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);

}
