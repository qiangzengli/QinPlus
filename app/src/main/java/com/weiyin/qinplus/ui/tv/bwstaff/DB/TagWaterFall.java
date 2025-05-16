package com.weiyin.qinplus.ui.tv.bwstaff.DB;
public class TagWaterFall{
    public  int note;
    public int startTime;
    public int duration;
    public int leftRight;

    public TagWaterFall(){

    }
    public TagWaterFall(int note,int startTime,int duration,int leftRight){
        this.note =note;
        this.startTime = startTime;
        this.duration = duration;
        this.leftRight = leftRight;
    }
}