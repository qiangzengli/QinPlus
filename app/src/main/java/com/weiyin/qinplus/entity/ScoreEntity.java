package com.weiyin.qinplus.entity;

/**
 * Created by lenovo on 2016/11/4.
 */

public class ScoreEntity extends BaseEntity{
    public String musicid;                         // 曲谱id号
    public String musicname;                        //曲谱名称
    public long startTime;                          //弹奏开始时间戳
    public long musictime;                          //弹奏完的时间戳
    public double dynamics;                        //力度分
    public double rhythm;                           //节奏分
    public double intonation;                       //音准
    public double totalscore;                       //总分


}
