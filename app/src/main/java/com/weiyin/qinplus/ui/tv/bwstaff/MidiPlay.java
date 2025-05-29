package com.weiyin.qinplus.ui.tv.bwstaff;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.bluetooth.BlueToothControl;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newScheduledThreadPool;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 播放器类
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class MidiPlay {


    public final String TAG = "MidiPlay";

    private Handler mHandler;

    private ScheduledExecutorService service;

    /**
     * midi播放器
     */
    private MediaPlayer midiPlayer;
    private MediaPlayer mp3Player;

    private boolean midiPlaying;
    private boolean mp3Playing;
    private boolean waterFalling;

    private ScoreController scoreController;

    /**
     * 开始时间
     */
    private double startPulseTime = 0;
    /**
     * 前一个音符的时间
     */
    private double prevPulseTime;
    private double currentPulseTime = 0;
    private long startTime = 0;
    /**
     * 1s钟多少tick
     */
    private float unit;
    private int startDate = 0;
    private float currentTempo = 0;
    private float tickInterval;
    private long date;
    private int currentTickI;
    private int startTick;
    private String mp3Id, midiId;

    private boolean mp3Start = false;

    private boolean stop = true;

    private int midiPlayerDate = 0;
    private int midiDate = 0;

    private int startDateTick;

    /**
     * /当前播放速度
     *
     * @property(nonatomic, assign)
     */

    public double getCurrentPulseTime() {
        return currentPulseTime;
    }

    public void setCurrentPulseTime(double currentPulseTime) {
        this.currentPulseTime = currentPulseTime;
    }

    public MidiPlay(Handler handler, ScoreController scoreController) {
        this.mHandler = handler;
        waterFalling = false;
        StatusModule statusModule = StatusModule.getStatusModule();
        if (!statusModule.isVideo()) {
            midiPlayer = new MediaPlayer();
            mp3Player = new MediaPlayer();
        }
        this.scoreController = scoreController;
        midiPlaying = false;
        mp3Playing = false;

    }

    private TimerTask tick = new TimerTask() {
        @Override
        public void run() {

            Message msg = new Message();
            msg.what = 1;
            // 传过来之前是measure.tick/16,然后*16后传递给msg.obj,所以最终传递的还是measure.tick
            msg.obj = (currentTickI) * 16;
            if (!mp3Start) {
                if (!scoreController.szScore.hasZeroMeasure) {
                    if (currentTickI * 16 >= startDateTick * 16 + scoreController.currentMeasureTickAmount) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mp3Play();
                                mp3Start = true;
                            }
                        }, 100);

                    }
                }
            }

            if (currentTickI == 0) {
                midiPlayerDate = midiPlayer.getCurrentPosition();
            }
            /* Log.i(TAG,"伴奏="+mp3Player.getCurrentPosition()+" midi="+midiPlayer.getCurrentPosition()+" currentTickI="+currentTickI); */
            currentTickI = currentTickI + 1;
            mHandler.sendMessage(msg);
            if (!scoreController.szScore.hasZeroMeasure) {
                if (scoreController.isFirst()) {
                    /* 0 */
                    if (scoreController.getStatusModule().isWatefallview()) {
                        waterFalling = true;
                        startDate = (int) (0 - date / 1000 / 1000 * startTick);
                        /* Log.i(TAG, "startDate=" + startDate); */
                        scoreController.setFirst(false);
                    }
                }
            }
            if (waterFalling) {
                if (scoreController.getStatusModule().isPractice() || scoreController.getStatusModule().isDemonstration()) {
                    long timerTask = SystemClock.uptimeMillis() - startTime;
                    prevPulseTime = currentPulseTime;
                    currentPulseTime = startPulseTime + timerTask * unit / 1000;
                    if (midiPlayer.getCurrentPosition() != 0) {
                        currentPulseTime = midiPlayer.getCurrentPosition() - date / 1000 / 1000 * startTick;
                    }
//                Log.i(TAG,"currentPulseTime= "+ currentPulseTime+" msec="+msec+ " startPulseTime="+startPulseTime +
//                        "currentTickI="+currentTickI+" midi="+midiPlayer.getCurrentPosition()+"data="+date +" prevPulseTime="+prevPulseTime);
//                    Log.i(TAG,"currentPulseTime="+currentPulseTime);
                    if (scoreController.getStatusModule().isWaterfall()) {
                        scoreController.getScoreWaterfall().setShutter((int) currentPulseTime, (int) (midiDate - startTick * date / 1000 / 1000));
                    }
                    scoreController.keyboard.ShadeNotes((int) currentPulseTime, (int) prevPulseTime, (int) (midiDate - startTick * date / 1000 / 1000));
                }
            }

        }
    };

    public void clean() {
        if (scoreController != null) {
            scoreController = null;
        }
        if (midiPlayer != null) {
            midiPlayer.release();
            midiPlayer = null;
        }
        if (mp3Player != null) {
            mp3Player.release();
            mp3Player = null;
        }

        if (service != null) {
            service = null;
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }


    //这个方法用来处理播放过程中了蓝牙状态改变   pad是否发声
    public void setPadVolumeConnect(boolean connect, int usbState) {


        //蓝牙如果连接  名字包括RealPiano的  pad发声，否则 pad不发生
        if (connect || usbState == 0) {

            if (scoreController.blueToothcontrol.getmDeviceName().contains("RealPiano") || usbState == 0) {

                midiPlayer.setVolume(1.0f, 1.0f);
            } else {
                midiPlayer.setVolume(0f, 0f);
            }

        } else {

            //蓝牙没有连接    设置pad发声
            midiPlayer.setVolume(1.0f, 1.0f);
        }

    }

    public void play() {
        /*currentTempo = 80f;//45f  最小值为64分音符的时间间隔  16 */
        mp3Start = false;
        midiDate = 0;
        boolean add = true;
        /* Log.i(TAG,"currentTempo===="+currentTempo); */
//        计算 16 个 tick 的持续时间（秒），假设 256 tick = 1 个四分音符。
        tickInterval = (float) (60.0 / currentTempo / 256.0 * 16);
        long playDate;
        if (stop) {
            for (int i = 0; i < scoreController.szScore.measure.size(); i++) {
                if (scoreController.szScore.measure.get(i).bpm != 0) {
                    /*
                     * scoreController.szScore.measure.get(i).bpm
                     * 获取第 i 个小节的 BPM（节奏）。
                     * 60.0 / bpm
                     * 表示 1 个四分音符的时长（单位：秒）。
                     * / 256.0
                     * 假设 1 个四分音符有 256 tick，求出 1 tick 的时长（秒）。
                     * * 16
                     * 表示 16 个 tick 的总时长（秒）。
                     * * 1000 * 1000 * 1000
                     * 把秒换算成 纳秒（1 秒 = 10⁹ 纳秒）。
                     * (long)
                     * 最终结果强制转换为 long 类型（因为纳秒是整数时间戳
                     */
                    date = (long) ((float) (60.0 / (scoreController.szScore.measure.get(i).bpm * scoreController.getSpeed()) / 256.0 * 16) * 1000 * 1000 * 1000);
                    stop = false;
                    break;
                }
            }
        }
        int currentMeasureTickAmount = 0;
        if (!scoreController.szScore.hasZeroMeasure) {
            currentMeasureTickAmount = scoreController.currentMeasureTickAmount / 16;
        }
        /* Log.i(TAG,"size="+scoreController.szScore.variableSpeed.size()); */
        if (scoreController.szScore.variableSpeed.size() > 1) {
            for (int i = 0; i < scoreController.szScore.variableSpeed.size() - 1; i++) {
                ScoreDataCoordinator.Measure szMeasure = scoreController.szScore.variableSpeed.get(i);
                /* Log.i(TAG,"szMeasure.tick="+szMeasure.tick+"szMeasure.bpm="+szMeasure.bpm); */
                if (currentTickI * 16 >= szMeasure.tick) {
                    if (currentTickI * 16 < scoreController.szScore.variableSpeed.get(i + 1).tick) {
                        if (!add) {
                            float tickInterval = (float) (60.0 / szMeasure.bpm / 256.0 * 16) * 1000;
                            int midiDat = (int) (midiDate + (currentTickI - (szMeasure.tick / 16) + currentMeasureTickAmount) * tickInterval);

                            startPulseTime = midiDat - startTick * date / 1000 / 1000 - currentMeasureTickAmount * tickInterval;
                            prevPulseTime = midiDat - startTick * date / 1000 / 1000 - currentMeasureTickAmount * tickInterval;
                            if (currentTickI * 16 >= startDateTick * 16 + scoreController.currentMeasureTickAmount) {
                                midiDate = (int) (midiDate + (currentTickI - (szMeasure.tick / 16) + currentMeasureTickAmount) * tickInterval);
                            } else {
                                midiDate = (int) (midiDate + (startDateTick - (szMeasure.tick / 16) + currentMeasureTickAmount) * tickInterval);
                            }
                            if (!scoreController.isFirst()) {
                                if (scoreController.peleView == null) {
                                    midiDate = (int) (midiDate - currentMeasureTickAmount * tickInterval);
                                }
                            }
//                            Log.i(TAG,"!<add="+midiDate +" startTick="+startTick+" scoreController.currentMeasureTickAmount="+currentMeasureTickAmount
//                                    +" startWaterDate=" +"  ====="+( startTick * date / 1000 / 1000)
//                                    +" ======"+(currentMeasureTickAmount * tickInterval)
//                                    +" -------"+(midiDate -  startTick * date / 1000 / 1000 -currentMeasureTickAmount * tickInterval)
//                                    +" szMeasure.tick="+((szMeasure.tick/16))+"-------"+(currentTickI)+" tickInterval="+tickInterval
//                            );
                        } else {
                            float tickInterval = (float) (60.0 / szMeasure.bpm / 256.0 * 16) * 1000;
                            int midiDat = (int) (midiDate + (currentTickI + currentMeasureTickAmount) * tickInterval);
                            if (currentTickI * 16 + currentMeasureTickAmount >= scoreController.szScore.variableSpeed.get(i + 1).tick) {
                                tickInterval = (float) (60.0 / scoreController.szScore.variableSpeed.get(i + 1).bpm / 256.0 * 16) * 1000;
                            }
                            startPulseTime = midiDat - startTick * date / 1000 / 1000 - currentMeasureTickAmount * tickInterval;
                            prevPulseTime = midiDat - startTick * date / 1000 / 1000 - currentMeasureTickAmount * tickInterval;
                            if (currentTickI * 16 >= startDateTick * 16 + scoreController.currentMeasureTickAmount) {
                                midiDate = (int) (midiDate + (currentTickI + currentMeasureTickAmount) * tickInterval);
                            } else {
                                midiDate = (int) (midiDate + (startDateTick + currentMeasureTickAmount) * tickInterval);
                            }
                            if (!scoreController.isFirst()) {
                                if (scoreController.peleView == null) {
                                    midiDate = (int) (midiDate - currentMeasureTickAmount * tickInterval);
                                }
                            }
                            /* Log.i(TAG,"<add="+midiDate +" startTick="+startTick+" scoreController.currentMeasureTickAmount="+currentMeasureTickAmount +" startWaterDate=startWaterDate   ====="+( startTick * date / 1000 / 1000)+" ======"+(currentMeasureTickAmount * tickInterval) ); */
                        }
                    } else {
                        add = false;
                        float tickInterval = (float) (60.0 / szMeasure.bpm / 256.0 * 16) * 1000;
                        midiDate = (int) (midiDate + (scoreController.szScore.variableSpeed.get(i + 1).tick / 16) * tickInterval);
//                        Log.i(TAG,"<else="+midiDate);
                    }
                }
            }
        } else {
            /* Log.i(TAG,"midiDate="+midiDate); */
            int midiDat = (int) (midiDate + (currentTickI + currentMeasureTickAmount) * 1000 * tickInterval);
            /* Log.i(TAG,"midiDate="+midiDate); */
            startPulseTime = midiDat - startTick * date / 1000 / 1000 - currentMeasureTickAmount * 1000 * tickInterval;
            prevPulseTime = midiDat - startTick * date / 1000 / 1000 - currentMeasureTickAmount * 1000 * tickInterval;
            if (currentTickI * 16 >= startDateTick * 16 + scoreController.currentMeasureTickAmount) {
                midiDate = (int) (midiDate + (currentTickI + currentMeasureTickAmount) * 1000 * tickInterval);
            } else {
                midiDate = (int) (midiDate + (startDateTick + currentMeasureTickAmount) * 1000 * tickInterval);
            }
            if (!scoreController.isFirst()) {
                /* Log.i(TAG, "midiDate=" + midiDate); */
                if (scoreController.peleView == null) {
                    midiDate = (int) (midiDate - currentMeasureTickAmount * 1000 * tickInterval);
                }
                /* Log.i(TAG,"midiDate="+midiDate); */
            }
            /* Log.i(TAG,"if<1  "+midiDate +" startTick="+startTick+" scoreController.currentMeasureTickAmount="+currentMeasureTickAmount +" startWaterDate=" +"  ====="+( startTick * date / 1000 / 1000)+" ======" ); */
        }
        playDate = (long) (tickInterval * 1000 * 1000 * 1000);

        /* float tickInterval = (float) (60.0 / waterSudu / 256.0 * 16); */
        unit = (float) (currentTempo * ((60.0 / currentTempo / 256.0 * 16) * 1000) * 16 / 60);


        startTime = SystemClock.uptimeMillis();

        Log.i(TAG, "unit=" + unit);
        Log.i(TAG, "date=" + date + " playDate=" + playDate);
        Log.i(TAG, "currentTickI=" + currentTickI);
        Log.i(TAG, "弱起=" + scoreController.szScore.hasZeroMeasure);
        Log.i(TAG, "startTime=" + startTime + " startPulseTime=" + startPulseTime + " startWaterDate=");
        if (scoreController.szScore.hasZeroMeasure != null) {
            if (scoreController.szScore.hasZeroMeasure) {
                if (service == null || (service != null && service.isShutdown())) {
                    service = newScheduledThreadPool(3);
                    /* date */
                    service.scheduleAtFixedRate(tick, 0, playDate, TimeUnit.NANOSECONDS);
                } else {
                    try {
                        synchronized (service) {
                            service.notify();
                        }
                    } catch (IllegalMonitorStateException e) {
                        e.printStackTrace();
                    }
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mp3Play();

                    }
                }, 100);
                scoreController.setFirst(false);
                if (scoreController.scoreWaterfall != null) {
//                    scoreController.scoreWaterfall.PlayWaterfall(12);//300
                    waterFalling = true;
                }
            } else {
                if (service == null || (service != null && service.isShutdown())) {
                    service = newScheduledThreadPool(3);
                    service.scheduleAtFixedRate(tick, 0, playDate, TimeUnit.NANOSECONDS);
                } else {
                    try {
                        synchronized (service) {
                            service.notify();
                        }
                    } catch (IllegalMonitorStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }

    private void mp3Play() {

        StatusModule statusModule = StatusModule.getStatusModule();
        if (mp3Player != null) {
            if (statusModule.isAccompaniment()) {
                if (!StringUtils.isEmpty(mp3Id)) {
                    Log.i(TAG, "伴奏播放时间=" + mp3Player.getDuration());
                    if (mp3Player.getDuration() > 0) {
                        if (midiDate > 0) {
                            mp3Player.seekTo(midiPlayerDate + midiDate);
                        } else {
                            mp3Player.seekTo((int) ((currentTickI * date / 1000 / 1000) + midiPlayerDate));
                        }
                        mp3Player.start();
                        if (statusModule.isAccompaniment()) {
                            mp3Player.setVolume(0.8f, 0.8f);
                        }
                        Log.i(TAG, "伴奏播放进入=" + mp3Player.getCurrentPosition());
                    }
                }
            }
        }
        if (midiPlayer != null) {
            if (statusModule.isDemonstration() || statusModule.isPractice()) {
                if (!StringUtils.isEmpty(midiId)) {
                    Log.i("midiplay", "MIDI播放时间=" + midiPlayer.getDuration());
                    if (midiPlayer.getDuration() > 0) {
                        if (midiDate > 0) {
                            midiPlayer.seekTo(midiPlayerDate + midiDate);
                        } else {
                            midiPlayer.seekTo((int) ((currentTickI * date / 1000 / 1000) + midiPlayerDate));
                        }
                        midiPlayer.start();
                        if (statusModule.isPractice()) {

                            if (scoreController.blueToothcontrol.getConnectFlag()) {


                                if (!scoreController.blueToothcontrol.getmDeviceName().contains("RealPiano")) {

//                                    if (statusModule.getType() != Constant.TWO_HAND) {
//                                        midiPlayer.setVolume(0.5f, 0.5f);
//                                    } else {
//                                        midiPlayer.setVolume(0f, 0f);
//                                    }

                                    midiPlayer.setVolume(0f, 0f);
                                } else {
                                    midiPlayer.setVolume(1.0f, 1.0f);
                                }

                            } else if (scoreController.getUsbControl().ismConnected()) {


                                midiPlayer.setVolume(0f, 0f);


                            } else {
                                midiPlayer.setVolume(1.0f, 1.0f);
                            }

                        }

                        //如果是选中了示范曲
                        if (statusModule.isDemonstration()) {

                            //如果蓝牙没连接  设置pad  发声
                            if (scoreController.blueToothcontrol.getConnectFlag()) {


                                //如果蓝牙连接了 真钢琴  设置pad发声  否则 pad不发声
                                if (scoreController.blueToothcontrol.getmDeviceName().contains("RealPiano")) {
                                    midiPlayer.setVolume(1.0f, 1.0f);
                                } else {
                                    midiPlayer.setVolume(0f, 0f);
                                }

                            } else if (scoreController.getUsbControl().ismConnected()) {


                                midiPlayer.setVolume(0f, 0f);


                            } else {


                                midiPlayer.setVolume(1.0f, 1.0f);
                            }

                        }
//                    Log.i(TAG,"midi播放="+((int) ((currentTickI * date/1000/1000) + midiPlayerDate) + midiDate)+"currentTickI="+currentTickI +" (date/1000/1000)="+(1000 * date/1000/1000)+" midiDate="+midiDate);
//                    Log.i(TAG, "midi播放jinru=" + midiPlayer.getCurrentPosition());
                    }
                }
            }
        }
    }

    public void stop() {
        stop = true;
        if (service != null && !service.isShutdown()) {
            service.shutdown();
        }
        if (tick != null) {
            tick.cancel();
        }
        waterFalling = false;
        if (midiPlayer != null) {
            if (midiPlayer.isPlaying()) {
                midiPlayer.stop();
            }

            if (mp3Player != null) {
                if (mp3Player.isPlaying()) {
                    mp3Player.stop();
                }
            }
            try {
                if (midiPlayer != null) {
                    if (midiPlaying) {
                        midiPlayer.prepare();
                    }
                }
                if (mp3Player != null) {
                    if (mp3Playing) {
                        mp3Player.prepare();
                    }
                }
            } catch (IOException | IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        if (service != null && !service.isShutdown()) {
            service.shutdown();
        }
        if (tick != null) {
            tick.cancel();
        }

        if (midiPlayer != null) {
            if (midiPlayer.isPlaying()) {
                midiPlayer.pause();
            }
        }
        if (mp3Player != null) {
            if (mp3Player.isPlaying()) {
                mp3Player.pause();
            }
        }

    }


    /**
     * 播放节拍器
     *
     * @param type 样式
     */
    public void playMetronome(String type) {
        final BlueToothControl blueControl = BlueToothControl.getBlueToothInstance();
        if ("first".equalsIgnoreCase(type)) {
            //强拍
            final String strong = musicOpen(9, 34, 127);
            scoreController.getUsbControl().sendMidiOn(0, 9, 34, 127);
            if (blueControl != null) {
                if (blueControl.getConnectFlag()) {
                    blueControl.sendData(strong);
                }
            }
        }
        if ("others".equalsIgnoreCase(type)) {
            //弱拍
            final String weak = musicOpen(9, 33, 90);
            scoreController.getUsbControl().sendMidiOn(0, 9, 33, 90);
            if (blueControl != null) {
                if (blueControl.getConnectFlag()) {
                    blueControl.sendData(weak);
                }
            }
        }
    }

    public String musicOpen(int channel, int note, int duration) {
        String result = "";
        String thisChannel = Integer.toHexString(channel);
        String thisNote = Integer.toHexString(note);
        String thisDuration = Integer.toHexString(duration);
        if (thisDuration.equals("1")) {
            thisDuration = "01";
        }
        result = "80809" + thisChannel + thisNote + thisDuration;
        return result;
    }

    public String musicClose(int channel, int note, int duration) {
        String result = "";
        String thisChannel = Integer.toHexString(channel);
        String thisNote = Integer.toHexString(note);
        String thisDuration = Integer.toHexString(duration);
        if (thisDuration.equals("1")) {
            thisDuration = "01";
        }
        result = "80808" + thisChannel + thisNote + thisDuration;
        return result;
    }

    /**
     * #
     * pragma mark
     * -播放音关闭音
     */

    public void NoteOn(int pitch, int velocity) {

    }

    public void NoteOff(int pitch) {

    }

    public float getCurrentTempo() {
        return currentTempo;
    }

    public void setCurrentTempo(float currentTempo, boolean playing) {
        this.currentTempo = currentTempo;
        tickInterval = (float) (60.0 / currentTempo / 256.0 * 16);
        long aaa = (long) (tickInterval * 1000 * 1000 * 1000);
        unit = (float) (currentTempo * ((60.0 / currentTempo / 256.0 * 16) * 1000) * 16 / 60);

        prevPulseTime = currentPulseTime;
        startPulseTime = currentPulseTime;

        startTime = SystemClock.uptimeMillis();
        if (playing) {
            if (service != null && !service.isShutdown()) {
                service.shutdown();
            }
            if (tick != null) {
                tick.cancel();
            }

            if (service == null || (service != null && service.isShutdown())) {
                service = newScheduledThreadPool(3);
                service.scheduleAtFixedRate(tick, 0, aaa, TimeUnit.NANOSECONDS);//date
            } else {

                try {
                    synchronized (service) {
                        service.notify();
                    }
                } catch (IllegalMonitorStateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setCurrentTempo(float currentTempo, String midiId, String mp3Id) {
        this.currentTempo = currentTempo;
        this.mp3Id = mp3Id;
        this.midiId = midiId;
        StatusModule statusModule = StatusModule.getStatusModule();
        Log.i("MidiPlay", "midi=" + midiId + "  banzou" + mp3Id);
        // 是否是示范
        if (statusModule.isDemonstration()) {
            midiPlayer.reset();
            try {
                midiPlayer.setDataSource(midiId);
                midiPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 是否是练习
        if (statusModule.isPractice()) {
            midiPlayer.reset();
            try {
                midiPlayer.setDataSource(midiId);
                midiPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mp3Id != null) {
            if (statusModule.isAccompaniment()) {
                mp3Player.reset();
                try {
                    Log.i(TAG, "伴奏=" + mp3Id);
                    mp3Player.setDataSource(mp3Id);
                    mp3Player.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setStartTick(int currentTick) {
        startTick = currentTick / 16;
    }


    public int getStartDateTick() {
        return startDateTick;
    }

    public void setStartDateTick(int startDateTick) {
        this.startDateTick = startDateTick;
    }

    /**
     * 设置当前的tick值
     *
     * @param currentTick int
     */
    public void setCurrentTick(int currentTick) {
        startDateTick = currentTick / 16;
        currentTickI = currentTick / 16;
    }


}