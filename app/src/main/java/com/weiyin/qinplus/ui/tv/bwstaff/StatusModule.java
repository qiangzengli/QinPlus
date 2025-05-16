package com.weiyin.qinplus.ui.tv.bwstaff;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 走谱界面状态控制
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class StatusModule {
    private static StatusModule statusModule = null;

    public static StatusModule getStatusModule() {
        if (statusModule == null) {
            statusModule = new StatusModule();
        }
        return statusModule;
    }

    public void clean() {
        statusModule = null;
    }

    /**
     * 伴奏状态
     */
    private boolean accompaniment = false;
    /**
     * 节拍器状态
     */
    private boolean metronome = false;
    /**
     * 示范
     */
    private boolean demonstration = false;
    /**
     * 录制
     */
    private boolean practice = false;
    /**
     * 瀑布流
     */
    private boolean waterfall = false;
    /**
     * 重新开始
     */
    private boolean restart = false;
    /**
     * 退出
     */
    private boolean out = false;
    /**
     * 是否有瀑布流
     */
    private boolean watefallview = false;
    /**
     * 视频界面
     */
    private boolean video = false;

    /**
     * 左右手模式
     */
    private int type = 0;

    /**
     * 弹奏前进
     */
    private boolean bounceForward = false;

    public boolean isBounceForward() {
        return bounceForward;
    }

    public void setBounceForward(boolean bounceForward) {
        this.bounceForward = bounceForward;
    }

    public boolean isOut() {
        return out;
    }

    public void setOut(boolean out) {
        this.out = out;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public boolean isAccompaniment() {
        return accompaniment;
    }

    public void setAccompaniment(boolean accompaniment) {
        this.accompaniment = accompaniment;
    }

    public boolean isMetronome() {
        return metronome;
    }

    public void setMetronome(boolean metronome) {
        this.metronome = metronome;
    }


    public boolean isDemonstration() {
        return demonstration;
    }

    public void setDemonstration(boolean play) {
        this.demonstration = play;
    }

    public boolean isPractice() {
        return practice;
    }

    public void setPractice(boolean recording) {
        this.practice = recording;
    }

    public boolean isWaterfall() {
        return waterfall;
    }

    public void setWaterfall(boolean waterfall) {
        this.waterfall = waterfall;
    }

    public boolean isRestart() {
        return restart;
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }

    public boolean isWatefallview() {
        return watefallview;
    }

    public void setWatefallview(boolean watefallview) {
        this.watefallview = watefallview;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
