package com.weiyin.qinplus.entity;

/**
 * Created by Lee on 2016/10/5.
 */
public class MusicListItemEntity extends BaseEntity{
    private String id;          //曲谱ID
    private String musicSort;   //曲谱分类
    private String musicLevel;  //曲谱水平
    private String musicName;   //曲谱名字
    private String musicAuthor;      //作者
    private String musicBookName;    //书名
    private String musicStyle;       //曲目风格
    private String musicPicUrl; //图片地址
    private String mnQp;        //曲谱全拼
    private String mnSzm;       //曲谱首字母

    public String getMnQp() {
        return mnQp;
    }

    public void setMnQp(String mnQp) {
        this.mnQp = mnQp;
    }

    public String getMnSzm() {
        return mnSzm;
    }

    public void setMnSzm(String mnSzm) {
        this.mnSzm = mnSzm;
    }
    public String getMusicStyle() {
        return musicStyle;
    }

    public void setMusicStyle(String musicStyle) {
        this.musicStyle = musicStyle;
    }

    public String getMusicAuthor() {
        return musicAuthor;
    }

    public void setMusicAuthor(String musicAuthor) {
        this.musicAuthor = musicAuthor;
    }

    public String getMusicBookName() {
        return musicBookName;
    }

    public void setMusicBookName(String musicBookName) {
        this.musicBookName = musicBookName;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicPicUrl() {
        return musicPicUrl;
    }

    public void setMusicPicUrl(String musicPicUrl) {
        this.musicPicUrl = musicPicUrl;
    }
}
