package com.weiyin.qinplus.ui.tv.bwstaff;

/**
 * @author njf
 * @date 2016/10/25
 */

public class ScoreModule {
    //声调（语调）
    private double intonation;
    //亮度（强烈，紧张）
    private double intensity;
    //节奏、韵律
    private double rhythm;

    public double getIntonation() {
        return intonation;
    }

    public void setIntonation(double intonation) {
        this.intonation = intonation;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public double getRhythm() {
        return rhythm;
    }

    public void setRhythm(double rhythm) {
        this.rhythm = rhythm;
    }
}
