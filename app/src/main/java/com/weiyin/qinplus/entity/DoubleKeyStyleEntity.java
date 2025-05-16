package com.weiyin.qinplus.entity;

import java.util.List;

/**
 * Created by lenovo on 2017/8/1.
 */
public class DoubleKeyStyleEntity extends BaseEntity{
    private String type;
    private String index;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}