package com.weiyin.qinplus.entity;

/**
 * Created by lenovo on 2017/5/24.
 */

public class MusicAboutEntity extends BaseEntity{
    private String id;
    private String musicName;
    private String musicPdfUrl;
    private String musicDbUrl;

    private String musicMidiUrl;

    private String musicMp3Url;
    private String musicPicUrl;
    public String getMusicPicUrl() {
        return musicPicUrl;
    }

    public void setMusicPicUrl(String musicPicUrl) {
        this.musicPicUrl = musicPicUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicPdfUrl() {
        return musicPdfUrl;
    }

    public void setMusicPdfUrl(String musicPdfUrl) {
        this.musicPdfUrl = musicPdfUrl;
    }

    public String getMusicDbUrl() {
        return musicDbUrl;
    }

    public void setMusicDbUrl(String musicDbUrl) {
        this.musicDbUrl = musicDbUrl;
    }

    public String getMusicMidiUrl() {
        return musicMidiUrl;
    }

    public void setMusicMidiUrl(String musicMidiUrl) {
        this.musicMidiUrl = musicMidiUrl;
    }

    public String getMusicMp3Url() {
        return musicMp3Url;
    }

    public void setMusicMp3Url(String musicMp3Url) {
        this.musicMp3Url = musicMp3Url;
    }
}
