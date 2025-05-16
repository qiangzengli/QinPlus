package com.weiyin.qinplus.entity;

/**
 * Created by lenovo on 2017/8/29.
 */

public class ImagesPointEntity extends BaseEntity{
    private String x;
    private String y;
    private String imgNeedType;

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getImgNeedType() {
        return imgNeedType;
    }

    public void setImgNeedType(String imgNeedType) {
        this.imgNeedType = imgNeedType;
    }

}
