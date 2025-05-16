package com.weiyin.qinplus.entity;

/**
 * Created by lenovo on 2017/4/12.
 */

public class BluetoothEntity extends BaseEntity{


    String name;

    String address;

    public BluetoothEntity(String name,String address){
        this.name = name;
        this.address = address;
    }

    public BluetoothEntity(){};


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
