package com.weiyin.qinplus.rest;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   :  接口路径配置
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class RequestConfigs {
    public static final String BASE_URL = "http://121.41.128.49:9017";
    public final static String ROOT_PATH = "/qin_plus/";
    public static final String HTTPS_BASE_URL = "https://sslcs.amso.com.cn";

    public static final String PATH_APP = "shuangk/";
    public static final String PATH_IMAGE = "image/";
    public static final String PATH_APK = "apk/";

    /**
     * 获取所有曲谱信息
     */
    public static final String GET_ALL = "main/getAll";

    /**
     * 获取指定曲谱信息
     */
    public static final String GET_MUSIC_ITEM = "main/getMusicItem";
    /**
     * 获取所有图书信息
     */
    public static final String GET_BOOK_ALL = "main/getBookAll";
    /**
     * 获取指定图书曲谱信息
     */
    public static final String GET_MUSIC_OF_BOOK = "main/getMusicOfBook";
    /**
     * 获取音乐课堂信息
     */
    public static final String GET_MUSIC_CLASS = "main/getMusicClass";
    /**
     * 获取视频教学信息
     */
    public static final String GET_VIDEO = "main/getVideo";
    /**
     * 用户注册
     */
    public static final String USER_REGISTER = "pc/userRegister";
    /**
     * 用户登录退出
     */
    public static final String USER_LOG_INFO = "pc/userLogInfo";
    /**
     * 更新用户信息
     */
    public static final String UPDATE_USER_INFO = "pc/updateUserInfo";
    /**
     * APP版本更新
     */
    public static final String UPDATE_APP = "pc/updateApp";
    /**
     * 设备激活
     */
    public static final String ACT_DEVICE = "pc/actDevice";
    /**
     * 检查设备是否激活
     */
    public static final String GET_DEVICE_ACT_STATE = "pc/getDeviceActState";
    /**
     * 记录用户练习信息
     */
    public static final String PUT_PLAY_RECORD = "pc/putPlayRecord";
    /**
     * 3.2.8获取最近练习曲谱
     */
    public static final String GET_PLAY_LIST = "pc/getPlayList";
    /**
     * 3.2.9获取曲谱练习记录
     */
    public static final String GET_PLAY_RECORD = "pc/getPlayRecord";
    /**
     * 曲谱收藏
     */
    public static final String PUT_MUSIC_COLLECT = "pc/putMusicCollect";
    /**
     * 取消曲谱收藏
     */
    public static final String DEL_MUSIC_COLLECT = "pc/delMusicCollect";
    /**
     * 获取收藏信息
     */
    public static final String GET_COLLECT_LIST = "pc/getCollectList";

}
