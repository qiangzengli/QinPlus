package com.weiyin.qinplus.entity;

/**
 * Created by lenovo on 2017/5/10.
 */

public class MusicBookEntity {
    private String musicSort;   //曲谱分类
    private String musicLevel;  //曲谱水平
    private String musicBookName;   //曲谱名字
    private String musicPicUrl; //图片地址



    public String getMusicPicUrl() {
        return musicPicUrl;
    }

    public void setMusicPicUrl(String musicPicUrl) {
        this.musicPicUrl = musicPicUrl;
    }

    public String getMusicSort() {
        return musicSort;
    }

    public void setMusicSort(String musicSort) {
        this.musicSort = musicSort;
    }

    public String getMusicLevel() {
        return musicLevel;
    }

    public void setMusicLevel(String musicLevel) {
        this.musicLevel = musicLevel;
    }

    public String getMusicBookName() {
        return musicBookName;
    }

    public void setMusicBookName(String musicBookName) {
        this.musicBookName = musicBookName;
    }
}
