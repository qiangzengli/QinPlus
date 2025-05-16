package com.weiyin.qinplus.entity;

import java.util.List;

/**
 * Created by lenovo on 2017/8/1.
 */

public class KnowledgeEntity extends BaseEntity{
    private String id;
    private String img;
    private String text;
    private String title;
    private String audio;
    private String type;
    private String video;
    private String audio2;
    private String audio1;
    private List<ImgAutoEntity> imgAuto;
    private ImgAFraEntity backGudImg;

    public ImgAFraEntity getBackGudImg() {
        return backGudImg;
    }

    public void setBackGudImg(ImgAFraEntity backGudImg) {
        this.backGudImg = backGudImg;
    }
    public List<ImgAutoEntity> getImgAuto() {
        return imgAuto;
    }

    public void setImgAuto(List<ImgAutoEntity> imgAuto) {
        this.imgAuto = imgAuto;
    }

    public String getAudio2() {
        return audio2;
    }

    public void setAudio2(String audio2) {
        this.audio2 = audio2;
    }

    public String getAudio1() {
        return audio1;
    }

    public void setAudio1(String audio1) {
        this.audio1 = audio1;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
