package com.weiyin.qinplus.entity;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2018/02/07
 *     desc   :
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class MusicCollectionEntity extends BaseEntity{
    /**
     * 曲谱ID
     */
    private String musicId;
    /**
     * 封面url
     */
    private String coverUrl;
    /**
     * 曲谱名称
     */
    private String musicName;
    /**
     * 曲谱分类
     */
    private String sort;
    /**
     * 收藏时间
     */
    private String collectTime;

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }
}
