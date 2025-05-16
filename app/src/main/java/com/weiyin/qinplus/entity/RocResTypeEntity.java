package com.weiyin.qinplus.entity;

import java.util.List;

/**
 * Created by lenovo on 2017/8/1.
 */

public class RocResTypeEntity extends BaseEntity{
    private String type;
    private String index;
    private List<String> index1;
    private List<List<String>> index2;

    public List<String> getIndex1() {
        return index1;
    }

    public void setIndex1(List<String> index1) {
        this.index1 = index1;
    }

    public List<List<String>> getIndex2() {
        return index2;
    }

    public void setIndex2(List<List<String>> index2) {
        this.index2 = index2;
    }
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