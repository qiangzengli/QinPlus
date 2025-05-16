package com.weiyin.qinplus.entity;

/**
 * Created by lenovo on 2017/8/29.
 */

public class ImgAutoEntity extends BaseEntity {
    private String img;
    private String audio;
    private FrameEntity frame;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public FrameEntity getFrame() {
        return frame;
    }

    public void setFrame(FrameEntity frame) {
        this.frame = frame;
    }

}
