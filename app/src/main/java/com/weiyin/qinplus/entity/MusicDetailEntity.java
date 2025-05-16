package com.weiyin.qinplus.entity;

import java.util.ArrayList;

/**
 * Created by win on 2016/10/19.
 * 一个曲谱的详细信息item
 */

public class MusicDetailEntity extends BaseEntity {

    /**
     * 曲谱ID
     */
    private String id;
    /**
     * 书名
     */
    private String musicBookName;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 曲谱作者
     */
    private String musicAuthor;
    /**
     * 曲谱地区
     */
    private String musicAuthorDiatrict;
    /**
     * 曲谱难度
     */
    private String musicDifficultLevel;
    /**
     * 作者简介
     */
    private String musicAuthorIntroduce;
    /**
     * 曲谱简介
     */
    private String musicDetailIntroduce;
    /**
     * 曲谱风格
     */
    private String musicStyle;
    /**
     * 曲谱PDF
     */
    private String musicPdfUrl;
    /**
     * 曲谱DB
     */
    private String musicDbUrl;
    /**
     * 曲谱MIDI
     */
    private String musicMidiUrl;
    /**
     * 曲谱MP3
     */
    private String musicMp3Url;
    /**
     * 相关曲目
     */
    private ArrayList<MusicAboutEntity> relationMusicId;
    /**
     * 是否收藏 1:收藏 0：未收藏
     */
    private String isCollect;


    public void setIsCollect(String isCollect) {
        this.isCollect = isCollect;
    }

    public String getIsCollect() {
        return isCollect;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMusicBookName() {
        return musicBookName;
    }

    public void setMusicBookName(String musicBookName) {
        this.musicBookName = musicBookName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMusicAuthor() {
        return musicAuthor;
    }

    public void setMusicAuthor(String musicAuthor) {
        this.musicAuthor = musicAuthor;
    }

    public String getMusicAuthorDiatrict() {
        return musicAuthorDiatrict;
    }

    public void setMusicAuthorDiatrict(String musicAuthorDiatrict) {
        this.musicAuthorDiatrict = musicAuthorDiatrict;
    }

    public String getMusicDifficultLevel() {
        return musicDifficultLevel;
    }

    public void setMusicDifficultLevel(String musicDifficultLevel) {
        this.musicDifficultLevel = musicDifficultLevel;
    }

    public String getMusicAuthorIntroduce() {
        return musicAuthorIntroduce;
    }

    public void setMusicAuthorIntroduce(String musicAuthorIntroduce) {
        this.musicAuthorIntroduce = musicAuthorIntroduce;
    }

    public String getMusicDetailIntroduce() {
        return musicDetailIntroduce;
    }

    public void setMusicDetailIntroduce(String musicDetailIntroduce) {
        this.musicDetailIntroduce = musicDetailIntroduce;
    }

    public String getMusicStyle() {
        return musicStyle;
    }

    public void setMusicStyle(String musicStyle) {
        this.musicStyle = musicStyle;
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

    public ArrayList<MusicAboutEntity> getRelationMusicId() {
        return relationMusicId;
    }

    public void setRelationMusicId(ArrayList<MusicAboutEntity> relationMusicId) {
        this.relationMusicId = relationMusicId;
    }
}
