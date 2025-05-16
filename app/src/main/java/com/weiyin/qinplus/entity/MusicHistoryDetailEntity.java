package com.weiyin.qinplus.entity;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2018/02/06
 *     desc   :
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class MusicHistoryDetailEntity extends BaseEntity {
    /**
     * 总成绩
     */
    private String score;
    /**
     * 音准成绩
     */
    private String intonation;
    /**
     * 节奏成绩
     */
    private String rhythm;
    /**
     * 力度成绩
     */
    private String intensity;
    /**
     * 弹奏开始日期
     */
    private String startDate;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 弹奏时长
     */
    private String playLong;

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getIntonation() {
        return intonation;
    }

    public void setIntonation(String intonation) {
        this.intonation = intonation;
    }

    public String getRhythm() {
        return rhythm;
    }

    public void setRhythm(String rhythm) {
        this.rhythm = rhythm;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPlayLong() {
        return playLong;
    }

    public void setPlayLong(String playLong) {
        this.playLong = playLong;
    }
}
