package com.weiyin.qinplus.entity;

/**
 * Created by lenovo on 2016/11/30.
 */

public class FirmwareversionEntity extends BaseEntity {

    String mainboardversionurl;           //主板版本url
    String mainboardversioncode;          //主板版本号
    String mainboardupdateinfo;           //主板升级信息
    String bluetoothversionurl;           //蓝牙版本url
    String bluetoothversioncode;          //蓝牙版本号
    String bluetoothupdateinfo;           //蓝牙升级信息

    public String getMainboardversionurl() {
        return mainboardversionurl;
    }

    public void setMainboardversionurl(String mainboardversionurl) {
        this.mainboardversionurl = mainboardversionurl;
    }

    public String getMainboardversioncode() {
        return mainboardversioncode;
    }

    public void setMainboardversioncode(String mainboardversioncode) {
        this.mainboardversioncode = mainboardversioncode;
    }

    public String getMainboardupdateinfo() {
        return mainboardupdateinfo;
    }

    public void setMainboardupdateinfo(String mainboardupdateinfo) {
        this.mainboardupdateinfo = mainboardupdateinfo;
    }

    public String getBluetoothversionurl() {
        return bluetoothversionurl;
    }

    public void setBluetoothversionurl(String bluetoothversionurl) {
        this.bluetoothversionurl = bluetoothversionurl;
    }

    public String getBluetoothversioncode() {
        return bluetoothversioncode;
    }

    public void setBluetoothversioncode(String bluetoothversioncode) {
        this.bluetoothversioncode = bluetoothversioncode;
    }

    public String getBluetoothupdateinfo() {
        return bluetoothupdateinfo;
    }

    public void setBluetoothupdateinfo(String bluetoothupdateinfo) {
        this.bluetoothupdateinfo = bluetoothupdateinfo;
    }

}
