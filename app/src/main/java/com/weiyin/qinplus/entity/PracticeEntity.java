package com.weiyin.qinplus.entity;

import java.util.List;

/**
 * Created by lenovo on 2017/8/1.
 */

public class PracticeEntity extends BaseEntity {
    private List<String> img;
    private String text;
    private String type;
    private String knowPointId;
    private RocResTypeEntity rocResType;
    private DoubleKeyStyleEntity doubleKeyStyle;
    private ImgAFraEntity imgAFra;
    private List<ImagesEntity> imgs;
    private List<ImagesPointEntity> imgsPoint;
    private String pointDefaultImg;
    private ImageSizeEntity imgSize;
    private String backImg;
    private String rectImg;
    private String resultImg;
    private List<ImgAFraEntity> firstCol;
    private List<ImgAFraEntity> secondCol;
    private String dbUrl;
    private String midiUrl;
    private String pdfUrl;


    public List<String> getImg() {
        return img;
    }

    public void setImg(List<String> img) {
        this.img = img;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKnowPointId() {
        return knowPointId;
    }

    public void setKnowPointId(String knowPointId) {
        this.knowPointId = knowPointId;
    }

    public RocResTypeEntity getRocResType() {
        return rocResType;
    }

    public void setRocResType(RocResTypeEntity rocResType) {
        this.rocResType = rocResType;
    }

    public DoubleKeyStyleEntity getDoubleKeyStyle() {
        return doubleKeyStyle;
    }

    public void setDoubleKeyStyle(DoubleKeyStyleEntity doubleKeyStyle) {
        this.doubleKeyStyle = doubleKeyStyle;
    }

    public ImgAFraEntity getImgAFra() {
        return imgAFra;
    }

    public void setImgAFra(ImgAFraEntity imgAFra) {
        this.imgAFra = imgAFra;
    }

    public List<ImagesEntity> getImgs() {
        return imgs;
    }

    public void setImgs(List<ImagesEntity> imgs) {
        this.imgs = imgs;
    }

    public List<ImagesPointEntity> getImgsPoint() {
        return imgsPoint;
    }

    public void setImgsPoint(List<ImagesPointEntity> imgsPoint) {
        this.imgsPoint = imgsPoint;
    }

    public String getPointDefaultImg() {
        return pointDefaultImg;
    }

    public void setPointDefaultImg(String pointDefaultImg) {
        this.pointDefaultImg = pointDefaultImg;
    }

    public ImageSizeEntity getImgSize() {
        return imgSize;
    }

    public void setImgSize(ImageSizeEntity imgSize) {
        this.imgSize = imgSize;
    }

    public String getBackImg() {
        return backImg;
    }

    public void setBackImg(String backImg) {
        this.backImg = backImg;
    }

    public String getRectImg() {
        return rectImg;
    }

    public void setRectImg(String rectImg) {
        this.rectImg = rectImg;
    }

    public String getResultImg() {
        return resultImg;
    }

    public void setResultImg(String resultImg) {
        this.resultImg = resultImg;
    }

    public List<ImgAFraEntity> getFristCol() {
        return firstCol;
    }

    public void setFristCol(List<ImgAFraEntity> firstCol) {
        this.firstCol = firstCol;
    }

    public List<ImgAFraEntity> getSecondCol() {
        return secondCol;
    }

    public void setSecondCol(List<ImgAFraEntity> secondCol) {
        this.secondCol = secondCol;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getMidiUrl() {
        return midiUrl;
    }

    public void setMidiUrl(String midiUrl) {
        this.midiUrl = midiUrl;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }
}
