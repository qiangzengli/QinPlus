package com.weiyin.qinplus.rest;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.dialog.LoadingDialog;
import com.weiyin.qinplus.rest.api.ApiService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   :  接口服务类，主要用于封装http请求以及结果回调
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class DataService {

    private ApiService coolService;

    public DataService(ApiService coolService) {
        this.coolService = coolService;
    }

    /**
     * 获取指定图书曲谱信息
     *
     * @param map        appVersion(app版本)  platformType(平台类型)  musicBookName(书名)
     *                   musicSort(大类) musicLevel(小类)
     * @param subscriber 回调监听
     */
    public void getMusicOfBook(Map<String, String> map, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.getMusicOfBook(map);
        onSubscript(observable, subscriber, RequestConfigs.GET_MUSIC_OF_BOOK);
    }

    /**
     * 获取所有曲谱信息
     *
     * @param appVersion   app版本
     * @param platformType 平台类型
     */
    public void getAll(String appVersion,
                       String platformType, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.getAll(appVersion, platformType);
        onSubscript(observable, subscriber, RequestConfigs.GET_ALL);
    }

    /**
     * 获取所有图书信息
     *
     * @param appVersion   app版本
     * @param platformType 平台类型
     * @return 回调监听
     */
    public void getBookAll(String appVersion, String platformType, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.getBookAll(appVersion, platformType);
        onSubscript(observable, subscriber, RequestConfigs.GET_BOOK_ALL);
    }

    /**
     * 获取指定曲谱信息
     *
     * @param map appVersion(app版本)  platformType(平台类型) userId(用户名) id(曲谱ID)
     * @return 回调监听
     */
    public void getMusicItem(Map<String, String> map, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.getMusicItem(map);
        onSubscript(observable, subscriber, RequestConfigs.GET_MUSIC_ITEM);
    }

    /**
     * 获取音乐课堂信息
     *
     * @param appVersion   app版本
     * @param platformType 平台类型
     * @return 回调监听
     */
    public void getMusicClass(String appVersion, String platformType, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.getMusicClass(appVersion, platformType);
        onSubscript(observable, subscriber, RequestConfigs.GET_MUSIC_CLASS);
    }

    /**
     * 获取视频教学信息
     *
     * @param appVersion   app版本
     * @param platformType 平台类型
     * @return 回调监听
     */
    public void getVideo(String appVersion, String platformType, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.getVideo(appVersion, platformType);
        onSubscript(observable, subscriber, RequestConfigs.GET_VIDEO);
    }

    /**
     * 用户注册
     *
     * @param map appVersion(app版本)  platformType(平台类型) deviceId(设备ID)
     *            regMode(用户注册类别) logName(登录名) password(密码)
     * @return 回调监听
     */
    public void userRegister(Map<String, String> map, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.userRegister(map);
        onSubscript(observable, subscriber, RequestConfigs.USER_REGISTER);
    }

    /**
     * 用户登录退出
     *
     * @param map appVersion(app版本)  platformType(平台类型) deviceId(设备ID)
     *            regMode(用户注册类别) logName(登录名) password(密码)
     *            deviceModel(设备类型) macAddress(Mac地址) userId(userId)
     *            userState(用户状态)
     * @return 回调监听
     */
    public void userLogInfo(Map<String, String> map, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.userLogInfo(map);
        onSubscript(observable, subscriber, RequestConfigs.USER_LOG_INFO);
    }

    /**
     * 更新用户信息
     *
     * @param map appVersion(app版本)  platformType(平台类型) userId(用户id)
     *            nickname(昵称) sex(性别) age(年龄)
     *            city(城市) email(Email地址) mobile(手机号码)
     *            weixinNum(微信号) qqNum(qqNum) weiboNum(微博号)
     *            otherNum(其他号) hobby(兴趣爱好) address(地址)
     *            regfrom(注册来源) comment(备注)
     * @return 回调监听
     */
    public void updateUserInfo(Map<String, String> map, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.updateUserInfo(map);
        onSubscript(observable, subscriber, RequestConfigs.UPDATE_USER_INFO);
    }

    /**
     * APP版本更新
     *
     * @param appVersion   app版本
     * @param platformType 平台类型
     * @return 回调监听
     */
    public void updateApp(String appVersion, String platformType, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.updateApp(appVersion, platformType);
        onSubscript(observable, subscriber, RequestConfigs.UPDATE_APP);
    }

    /**
     * 设备激活
     *
     * @param map appVersion(app版本)  platformType(平台类型) actCode(激活码)
     *            deviceId(设备ID)
     * @return 回调监听
     */
    public void actDevice(Map<String, String> map, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.actDevice(map);
        onSubscript(observable, subscriber, RequestConfigs.ACT_DEVICE);
    }

    /**
     * 记录用户练习信息
     *
     * @param map appVersion(app版本)  platformType(平台类型)  userId(用户ID)
     *            musicId(曲谱ID)  score(总成绩)  intonation(音准成绩)
     *            rhythm(节奏成绩)  intensity(力度成绩)  startDate(弹奏开始日期)
     *            startTime(开始时间)  playLong(弹奏时长)  deviceId(设备ID)
     *            deviceModel(设备类型)
     * @return 回调监听
     */
    public void putPlayRecord(Map<String, String> map, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.putPlayRecord(map);
        onSubscript(observable, subscriber, RequestConfigs.PUT_PLAY_RECORD);
    }

    /**
     * 获取最近练习曲谱
     *
     * @param map appVersion(app版本)  platformType(平台类型)  userId(用户ID)
     * @return 回调监听
     */
    public void getPlayList(Map<String, String> map, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.getPlayList(map);
        onSubscript(observable, subscriber, RequestConfigs.GET_PLAY_LIST);
    }

    /**
     * 获取曲谱练习记录
     *
     * @param map appVersion(app版本)  platformType(平台类型)  userId(用户ID)
     *            musicId(曲谱ID)  startDate(弹奏开始日期)
     * @return 回调监听
     */
    public void getPlayRecord(Map<String, String> map, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.getPlayRecord(map);
        onSubscript(observable, subscriber, RequestConfigs.GET_PLAY_RECORD);
    }

    /**
     * 曲谱收藏
     *
     * @param map appVersion(app版本)  platformType(平台类型)  userId(用户ID)
     *            musicId(曲谱ID)  collectTime(收藏时间)
     * @return 回调监听
     */
    public void putMusicCollect(Map<String, String> map, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.putMusicCollect(map);
        onSubscript(observable, subscriber, RequestConfigs.PUT_MUSIC_COLLECT);
    }

    /**
     * 取消曲谱收藏
     *
     * @param map appVersion(app版本)  platformType(平台类型)  userId(用户ID)
     *            musicId(曲谱ID)
     * @return 回调监听
     */
    public void delMusicCollect(Map<String, String> map, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.delMusicCollect(map);
        onSubscript(observable, subscriber, RequestConfigs.DEL_MUSIC_COLLECT);
    }

    /**
     * 获取收藏信息
     *
     * @param map appVersion(app版本)  platformType(平台类型)  userId(用户ID)
     * @return 回调监听
     */
    public void getCollectList(Map<String, String> map, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.getCollectList(map);
        onSubscript(observable, subscriber, RequestConfigs.GET_COLLECT_LIST);
    }

    /**
     * 检查设备是否激活
     *
     * @param map appVersion(app版本)  platformType(平台类型)  deviceId(设备ID)
     * @return 回调监听
     */
    public void getDeviceActState(Map<String, String> map, HttpRequestListener<ResponseBody> subscriber) {
        Observable<ResponseBody> observable = coolService.getDeviceActState(map);
        onSubscript(observable, subscriber, RequestConfigs.GET_DEVICE_ACT_STATE);
    }

    /**
     * 下载
     *
     * @param fileUrl  文件路径
     * @param callback 回调监听
     */
    public void down(String fileUrl, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = coolService.downloadFileWithDynamicUrlSync(WinYinPianoApplication.strUrl + fileUrl);
        LogUtil.i(TAG, WinYinPianoApplication.strUrl + fileUrl);
        call.enqueue(callback);
    }

    public void down(String fileUrl, final Context mContext, final DataService dataService, final LoadingDialog loadingDialog, final Handler handler) {
        final File futureStudioIconFile = new File(mContext.getApplicationContext().getExternalFilesDir(null) + fileUrl.replace("/", ""));
        LogUtil.i(TAG, "filePath=" + (mContext.getApplicationContext().getExternalFilesDir(null) + fileUrl.replace("/", "")) + " futureStudioIconFile.exists()=" + futureStudioIconFile.exists());
        if (!futureStudioIconFile.exists()) {
            dataService.down(fileUrl, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        LogUtil.i(TAG, "server contacted and has file");
                        boolean writtenToDisk = dataService.writeResponseBodyToDisk(response.body(), futureStudioIconFile);
                        LogUtil.i(TAG, "file download was a success? " + writtenToDisk);
                        if (!writtenToDisk) {
                            ToastUtil.showTextToast(mContext.getApplicationContext(), "文件下载失败");
                            if (loadingDialog != null) {
                                loadingDialog.dismiss();
                            }
                        } else {
                            Message message = new Message();
                            message.obj = futureStudioIconFile;
                            message.what = Constant.INTERNET_CALLBACK_FILE_DOWN;
                            handler.sendMessage(message);

                        }
                    } else {
                        LogUtil.i(TAG, "server contact failed");
                        if (loadingDialog != null) {
                            loadingDialog.dismiss();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    ToastUtil.showTextToast(mContext.getApplicationContext(), "文件下载失败");
                    if (loadingDialog != null) {
                        loadingDialog.dismiss();
                    }
                }
            });
        } else {
            Message message = new Message();
            message.obj = futureStudioIconFile;
            message.what = Constant.INTERNET_CALLBACK_FILE_DOWN;
            handler.sendMessage(message);
        }
    }

    public boolean writeResponseBodyToDisk(ResponseBody body, File futureStudioIconFile) {
        try {
            // todo change the file location/name according to your needs

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    LogUtil.i(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    private void onSubscriptArray(Observable<BaseResponseArray> observable,
                                  final HttpRequestListener<BaseResponseArray> listener,
                                  final String tag) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResponseArray>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseResponseArray value) {
                        listener.onNext(value, tag);
                    }

                    @Override
                    public void onError(Throwable e) {
                        BizException exception = handleException(e, tag);
                        listener.onError(exception);
                    }

                    @Override
                    public void onComplete() {
                        listener.onCompleted(tag);
                    }
                });
    }

    private void onSubscript(Observable<ResponseBody> observable, final HttpRequestListener<ResponseBody> listener, final String tag) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        listener.onNext(value, tag);
                    }

                    @Override
                    public void onError(Throwable e) {
                        BizException exception = handleException(e, tag);
                        listener.onError(exception);
                    }

                    @Override
                    public void onComplete() {
                        listener.onCompleted(tag);
                    }
                });
    }

    /**
     * 处理异常
     *
     * @param e   异常
     * @param url 请求url
     * @return 转换业务异常返回
     */
    private BizException handleException(Throwable e, String url) {
        BizException exception;
        if (e instanceof ConnectException) {
            exception = new BizException(404, "网络连接错误", url);
            //网络连接错误异常
        } else if (e instanceof BizException) {
            //系统返回错误异常
            exception = (BizException) e;
            exception.setUrl(url);
        } else {
            exception = new BizException(400, "未知错误", url);
        }
        return exception;
    }

}
