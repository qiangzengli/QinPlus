package com.weiyin.qinplus.entity;

/**
 * Created by lenovo on 2016/10/31.
 */

public class OpernEntity extends BaseEntity {

    public String musicid;                         // 曲谱id号
    String dbPath;                          //db路径
    String midiPath;                        // midi路径
    String[] mp3Path;                       //MP3路径
    String pdfPath;                         //pdf路径
    String[] downMp3Path;                   //下载的mp3路径

    String txtPath;                         //txt路径

    public String getTxtPath() {
        return txtPath;
    }

    public void setTxtPath(String txtPath) {
        this.txtPath = txtPath;
    }

    public String getDbPath() {
        return dbPath;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public String getMidiPath() {
        return midiPath;
    }

    public void setMidiPath(String midiPath) {
        this.midiPath = midiPath;
    }

    public String[] getMp3Path() {
        return mp3Path;
    }

    public void setMp3Path(String[] mp3Path) {
        this.mp3Path = mp3Path;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public String[] getDownMp3Path() {
        return downMp3Path;
    }

    public void setDownMp3Path(String[] downMp3Path) {
        this.downMp3Path = downMp3Path;
    }

    public String getMusicid() {
        return musicid;
    }

    public void setMusicid(String musicid) {
        this.musicid = musicid;
    }

}
