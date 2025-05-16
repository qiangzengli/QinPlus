package com.weiyin.qinplus.entity;

/**
 * Created by lenovo on 2017/8/29.
 */

public class ImagesEntity extends BaseEntity{
    private String imgUrl;
    private String imgType;
    private ImagesPointEntity imgPoint;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgType() {
        return imgType;
    }

    public void setImgType(String imgType) {
        this.imgType = imgType;
    }

    public ImagesPointEntity getImgPoint() {
        return imgPoint;
    }

    public void setImgPoint(ImagesPointEntity imgPoint) {
        this.imgPoint = imgPoint;
    }
}
