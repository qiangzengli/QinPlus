package com.weiyin.qinplus.entity;

/**
 * Created by lenovo on 2017/8/29.
 */

public class ImgAFraEntity extends BaseEntity{
    private String imgUrl;
    private FrameEntity frame;
    private String rightLinkType;
    private String linkType;

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }
    public String getRightLinkType() {
        return rightLinkType;
    }

    public void setRightLinkType(String rightLinkType) {
        this.rightLinkType = rightLinkType;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public FrameEntity getFrame() {
        return frame;
    }

    public void setFrame(FrameEntity frame) {
        this.frame = frame;
    }
}
