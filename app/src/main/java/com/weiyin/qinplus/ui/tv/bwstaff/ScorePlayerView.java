package com.weiyin.qinplus.ui.tv.bwstaff;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

//import com.squareup.haha.trove.THash;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.commontool.ToastUtil;

import java.text.BreakIterator;
import java.util.ArrayList;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 控制曲谱界面移动
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class ScorePlayerView {

    public final String TAG = "ScorePlayerView";
    private ScoreController scoreClockController;

    private InterfaceScoreControllerData interfaceScoreControllerData;

    private ScoreDataCoordinator.Score szScore;
    private ArrayList<ScoreDataCoordinator.Note> keyBordPitch;

    private int peleViewIndex = 1;
    private int cumulativeTick = 0;
    private boolean updateMeasure = false;

    private boolean backwardMeasure = false;
    private boolean whiteLamp = true;

    private boolean fine = false;
    private int fineMeasureIdx = 0;
    private int fineTick = 0;

    private int startEndingIdx = 0;
    private int endEndingIdx = 0;
    private int endTick = 0;

    private int currentMeasureNoIdx;

    private int dsCodeMeasureTick = 0;
    private int toCodeIdx = 0;
    private int codeIdx = 0;
    private int sagoIda = 0;
    private boolean dsCode = false;

    private int currentPlayingTick;
    private Handler mHandler = new Handler();

    public ScorePlayerView(ScoreDataCoordinator.Score szScore, ScoreController scoreClockController) {
        this.scoreClockController = scoreClockController;
        this.szScore = szScore;
        keyBordPitch = new ArrayList<>();
    }

    public void clean() {
        stopLe();
        if (scoreClockController != null) {
            scoreClockController = null;
        }
        if (interfaceScoreControllerData != null) {
            interfaceScoreControllerData = null;
        }
        if (szScore != null) {
            szScore = null;
        }
    }

    /**
     * RESET=========================================================
     */
    public void toBeginAndReset() {
        scoreClockController.scoreModule.setRhythm(0);
        scoreClockController.scoreModule.setIntensity(0);
        scoreClockController.scoreModule.setIntonation(0);

        scoreClockController.scoreJudge.init();
        if (!scoreClockController.isFirst()) {
            if (scoreClockController.scoreWaterfall != null) {
                if (scoreClockController.getStatusModule().isWaterfall()) {
                    scoreClockController.isCleanWater = true;
                    scoreClockController.scoreWaterfall.waterFallSurfaceView.cleann();
                }
            }
        }
        if (scoreClockController.peleView != null) {
            scoreClockController.activityOpernRl.removeView(scoreClockController.peleView);
        }
        if (scoreClockController.keyboard != null && !scoreClockController.isFirst()) {
            scoreClockController.keyboard.draw();
        }
        stopLe();
        if (!scoreClockController.reduction) {
            if (interfaceScoreControllerData != null) {
                interfaceScoreControllerData.result("true");
            }
        }
        if (scoreClockController.scoreWaterfall != null) {
            scoreClockController.midiPlay.setCurrentPulseTime(0);
        }
        if (!scoreClockController.reduction) {
            for (int i = 0; i < scoreClockController.szScore.measure.size(); i++) {
                if (szScore.measure.get(i).bpm != 0) {
                    scoreClockController.setMidiSpeed(szScore.measure.get(i).bpm);
                    break;
                }
            }
            scoreClockController.midiPlay.setCurrentTempo(scoreClockController.getMidiSpeed(), false);
        }
        whiteLamp = true;
        isSend = false;
        scoreClockController.pauseData = 0;
        scoreClockController.playData = 0;
        scoreClockController.startY = 0;
        scoreClockController.setDraw(false);
        scoreClockController.reduction = false;
        scoreClockController.setFirst(true);
        scoreClockController.startScoreActivity = false;
        scoreClockController.setIdNumForNoteOn(0);
        scoreClockController.getStatusModule().setBounceForward(false);
        scoreClockController.tuck = -100;

        scoreClockController.currentRowNo = 1;
        scoreClockController.idNumForNoteOff = 1;
        peleViewIndex = 1;


        if (szScore.slot.size() > 0) {
            int tempoStaff1OffTick = szScore.slot.get(0).tick;
            int tempoStaff2OffTick = szScore.slot.get(0).tick;
            if (tempoStaff1OffTick < tempoStaff2OffTick) {
                scoreClockController.tempoPlayTickNoteOff = tempoStaff1OffTick;
            } else {
                scoreClockController.tempoPlayTickNoteOff = tempoStaff2OffTick;
            }
        }

        cumulativeTick = 0;
        updateMeasure = false;
        fine = false;
        fineMeasureIdx = 0;
        startEndingIdx = 0;
        endEndingIdx = 0;
        endTick = 0;
        fineTick = 0;

        currentPlayingTick = 0;

        dsCodeMeasureTick = 0;
        toCodeIdx = 0;
        codeIdx = 0;
        sagoIda = 0;
        dsCode = false;
        if (scoreClockController.getDrawKeyBoardList() != null) {
            scoreClockController.getDrawKeyBoardList().clear();
        }
        scoreClockController.tempoPlayTickNoteOn = 0;
        scoreClockController.tempoPlayTickWindowOpen = 0;
        scoreClockController.currentPitchArrayFromKeyboard = new ArrayList<>();
        /* 第一小节节拍值 mea_no =1 */
        scoreClockController.currentBeats = szScore.measure.get(0).beats;
        scoreClockController.currentBeatType = szScore.measure.get(0).beatType;
        scoreClockController.currentMeasureTickAmount = 256 * 4 * scoreClockController.currentBeats / scoreClockController.currentBeatType;

/* Log.i(TAG,"hasZeroMeasure="+scoreClockController.szScore.hasZeroMeasure); */
        if (scoreClockController.szScore.hasZeroMeasure) {
            scoreClockController.midiPlay.setCurrentTick(0);
            scoreClockController.midiPlay.setStartTick(0);
            scoreClockController.tickNumber = 0;
        } else {
            /* 同步时间轴 加上一拍的提前拍 */
            scoreClockController.midiPlay.setCurrentTick(-scoreClockController.currentBeats * 256 * 4 / scoreClockController.currentBeatType - 96);
            scoreClockController.midiPlay.setStartTick(-scoreClockController.currentBeats * 256 * 4 / scoreClockController.currentBeatType - 96);
            scoreClockController.tickNumber = -scoreClockController.currentBeats * 256 * 4 / scoreClockController.currentBeatType - 96;
        }
        scoreClockController.date = 0;
        /* 如果播放一半就临时停止了，则将当前半段收集的结果标记数组添加到总数组 */
        if (scoreClockController.tempoCorrectAndFaultViewArrayInPage.size() != 0) {
            scoreClockController.correctAndFaultViewArray.add(scoreClockController.tempoCorrectAndFaultViewArrayInPage);
            scoreClockController.correctAndFaultStringArray.add(scoreClockController.tempoCorrectAndFaultStringArrayInPage);
        }
        scoreClockController.setCurrentMeasureNo(0);
        seekToMeasure(scoreClockController.getCurrentMeasureNo());

        /* slot阴影归位 */
        seekToSlotBlock(1);
/* scoreClockController.img_slot.setVisibility(View.GONE); */
        if (scoreClockController.correctAndFaultViewArray.size() > 0) {
            /* 去除所有正误符号 */
            for (int i = 0; i < scoreClockController.correctAndFaultViewArray.size(); i++) {
                for (int j = 0; j < scoreClockController.correctAndFaultViewArray.get(i).size(); j++) {
                    scoreClockController.scoreMarkView.removeView(scoreClockController.correctAndFaultViewArray.get(i).get(j));
                }
            }
            scoreClockController.correctAndFaultViewArray.clear();
        }
        if (scoreClockController.correctAndFaultStringArray.size() > 0) {
            scoreClockController.correctAndFaultStringArray.clear();
        }
    }

    private void stopLe() {
        if (scoreClockController != null) {
            if (scoreClockController.blueToothcontrol != null) {
                if (scoreClockController.blueToothcontrol.getConnectFlag()) {
                    String strLedLeft = led1(1, 1, -1);
/* scoreClockController.blueToothControl.sendData(strLed); */
                    String strLed1Right = led1(2, 1, -1);
                    String strLed2White = led1(0, 1, -1);
                    strLed1Right = strLed1Right.substring(2);
                    strLed2White = strLed2White.substring(2);
                    scoreClockController.blueToothcontrol.sendData(strLedLeft + strLed1Right + strLed2White);
                }
            }
        }
    }

    private String led1(int channel, int state, int note) {
        String result = "";
        String thisChannel;
        String thisState;
        String thisNote;
        if (note == -1) {
            thisNote = "7F";
        } else {
            thisNote = Integer.toHexString(note);
        }
        if (channel == 1) {
            thisChannel = "B2";
        } else if (channel == 0) {
            thisChannel = "B1";
        } else {
            thisChannel = "B0";
        }
        if (state == 0) {
            thisState = "69";
        } else {
            thisState = "68";
        }
        result = "8080" + thisChannel + thisState + thisNote;
        Log.i(TAG, "this_state=" + thisChannel + " this_state=" + thisState + " note=" + thisNote + "result=" + result);
        return result;
    }

    /**
     * 弹对前进
     */
    public void bounceForward() {
        if (scoreClockController.getStatusModule().isBounceForward()) {
            if (scoreClockController.isFirst() && scoreClockController.getIdNumForNoteOn() < szScore.slot.size()) {
                /*音符开*/
                scoreClockController.handler.post(moveSlotBounceForward);
            }
            scoreClockController.setFirst(false);
            mHandler.postDelayed(bounceForwardRunnable, 30);

        }
    }

    private Runnable bounceForwardRunnable = new Runnable() {
        @Override
        public void run() {
                    /* 判断对错 */
            if (scoreClockController != null && scoreClockController.getStatusModule() != null)
                if (scoreClockController.getStatusModule().isBounceForward()) {
                    boolean result = scoreClockController.scoreJudge.judge();
//                    result = true;
                    if (result) {
                        scoreClockController.currentPitchArrayFromKeyboard.clear();
                        Log.i(TAG, "scoreClockController.getIdNumForNoteOn()=" + scoreClockController.getIdNumForNoteOn());
                        if (scoreClockController.getIdNumForNoteOn() < szScore.slot.size()) {
                            /*音符开*/
                            scoreClockController.handler.post(moveSlotBounceForward);
                        } else {
                            toBeginAndReset();
                        }
                    } else {
//                        scoreClockController.handler.post(moveSlotBounceForward);
                        if (!isSend) {
                            isSend = true;

                            mHandler.postDelayed(runnable, 2000);
                        }

                    }
                    mHandler.postDelayed(bounceForwardRunnable, 30);
                }
        }
    };


    public volatile boolean isSend = false;


    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            Log.e(TAG, System.currentTimeMillis() + "");
            for (int i = 0; i < keyBordPitch.size(); i++) {

                if (isSend) {
                    downKey(keyBordPitch.get(i));
                }

            }
            Log.e(TAG, System.currentTimeMillis() + "");
            isSend = false;
        }
    };

    public void backCurrentTick(int currentTick, PeleView peleView) {
        this.currentPlayingTick = currentTick - cumulativeTick + endTick - fineTick + dsCodeMeasureTick;
        /* 判断是否停止播放的条件 */
        if (fine) {
            if (currentPlayingTick >= szScore.measure.get(fineMeasureIdx).tick + 256 * 4 * scoreClockController.currentBeats / scoreClockController.currentBeatType) {
                end();
            }
        } else {
            if (currentPlayingTick >= szScore.measure.get(szScore.measure.size() - 1).tick + 256 * 4 * scoreClockController.currentBeats / scoreClockController.currentBeatType) {
                if (szScore.measure.get(szScore.measure.size() - 1).dCFineMeasureIdx < Integer.MAX_VALUE) {
                    fine = true;
                    for (int i = 0; i < szScore.measure.size(); i++) {
                        if (szScore.measure.get(i).fineMeasureIdx < Integer.MAX_VALUE) {
                            fineMeasureIdx = szScore.measure.get(i).fineMeasureIdx - 1;
                            fineTick = currentPlayingTick;

                            scoreClockController.setCurrentMeasureNo(0);
                            scoreClockController.setIdNumForNoteOn(0);
                            currentPlayingTick = szScore.measure.get(0).tick;
                        }
                    }
                } else {
                    if (dsCode) {
                        if (currentPlayingTick >= szScore.measure.get(szScore.measure.size() - 1).tick + 256 * 4 * scoreClockController.currentBeats / scoreClockController.currentBeatType) {
                            end();
                        }
                    } else {
                        if (szScore.measure.get(szScore.measure.size() - 1).dSCodeMeasureIdx < Integer.MAX_VALUE) {
                            for (int i = 0; i < szScore.measure.size(); i++) {
                                if (szScore.measure.get(i).code < Integer.MAX_VALUE) {
                                    codeIdx = i + 1;
                                }
                                if (szScore.measure.get(i).toCode < Integer.MAX_VALUE) {
                                    toCodeIdx = i;
                                }
                                if (szScore.measure.get(i).sago < Integer.MAX_VALUE) {
                                    sagoIda = i;
                                }

                                if (sagoIda != 0 && codeIdx != 0 && toCodeIdx != 0) {
                                    break;
                                }
                            }

                            dsCodeMeasureTick = szScore.measure.get(sagoIda).tick;
                            fineTick = currentPlayingTick;
                            currentPlayingTick = dsCodeMeasureTick;

                            for (int i = 0; i < szScore.slot.size(); i++) {
                                if (szScore.slot.get(i).tick == dsCodeMeasureTick) {
                                    scoreClockController.setIdNumForNoteOn(i);
                                    break;
                                }
                            }
                            scoreClockController.setCurrentMeasureNo(sagoIda);
                            dsCode = true;
                        } else {
                            if (szScore.measure.get(szScore.measure.size() - 1).backwardMeasureIdx < Integer.MAX_VALUE) {
                                if (!backwardMeasure) {
                                    backwardMeasure = true;
                                    backward(szScore.measure.size(), currentPlayingTick);
                                } else {
                                    backwardMeasure = false;
                                    end();
                                }
                            } else {
                                end();
                            }
                        }
                    }
                }
            }
        }
        if (!scoreClockController.startScoreActivity) {

            if (peleViewIndex > scoreClockController.currentBeats) {
                showHidePeople(peleView);
            }
            //判断当前进行的哪一个measure=========================================
            //匹配并移动小节阴影 并且当其大于0时才生效（避开预备拍）
            //if (currentPlayingTick>=0 && currentPlayingTick == scoreClockController.currentMeasureTickAmount == 0)
            if (scoreClockController.getCurrentMeasureNo() < szScore.measure.size()) {
//                Log.i(TAG,"getCurrentMeasureNo="+scoreClockController.getCurrentMeasureNo()+" currentPlayingTick="+currentPlayingTick);
                /*修改音节，节拍，*/
                updateMeasure(currentPlayingTick, false);
                if (currentPlayingTick > szScore.measure.get(scoreClockController.getCurrentMeasureNo()).tick - 16 &&
                        currentPlayingTick < szScore.measure.get(scoreClockController.getCurrentMeasureNo()).tick + 16) {
                    //移动小节
                    if (!scoreClockController.getStatusModule().isWaterfall()) {
                        scoreClockController.handler.post(moveMeasure);
                    } else {
                        scoreClockController.setCurrentMeasureNo(scoreClockController.getCurrentMeasureNo() + 1);
                    }
                }
            }
            if (scoreClockController.getStatusModule().isPractice()) {
                //判断对错
                scoreClockController.scoreJudge.judge(currentPlayingTick);
            }
            if (scoreClockController.getIdNumForNoteOn() < szScore.slot.size()) {
//                Log.i(TAG,"currentPlayingTick="+currentPlayingTick+" idNumForNoteOn="+scoreClockController.idNumForNoteOn+" tick="+szScore.slot.get(scoreClockController.idNumForNoteOn).tick);
                /*音符开*/
                if (scoreClockController.getIdNumForNoteOn() != szScore.slotAmount &&
                        currentPlayingTick > szScore.slot.get(scoreClockController.getIdNumForNoteOn()).tick - 16 - (256 * 4 / scoreClockController.currentBeatType) && whiteLamp) {
                    whiteLamp = false;
                    ScoreDataCoordinator.SlotBlock thisSlotBlock = szScore.slot.get(scoreClockController.getIdNumForNoteOn());
                    for (int i = 0; i < thisSlotBlock.note.size(); i++) {
                        ScoreDataCoordinator.Note szNote = thisSlotBlock.note.get(i);
                        LogUtil.i(TAG, "szNote.x=" + szNote.x + " szNote.y=" + szNote.y);
                        if (szNote.x != 0 && szNote.y != 0) {
                            if (scoreClockController.getStatusModule().getType() == Constant.TWO_HAND) {
                                downWhite(szNote);
                            } else if (scoreClockController.getStatusModule().getType() == Constant.RIGHT_HAND) {
                                if (szNote.hand == 1) {
                                    downWhite(szNote);
                                }
                            } else if (scoreClockController.getStatusModule().getType() == Constant.LEFT_HAND) {
                                if (szNote.hand == 0) {
                                    downWhite(szNote);
                                }
                            }
                        }
                    }
                }
                if (scoreClockController.getIdNumForNoteOn() != szScore.slotAmount &&
                        currentPlayingTick > szScore.slot.get(scoreClockController.getIdNumForNoteOn()).tick - 16 &&
                        currentPlayingTick < szScore.slot.get(scoreClockController.getIdNumForNoteOn()).tick + 16) {
                    scoreClockController.handler.post(moveSlot);
                }
            }

            if (currentPlayingTick < szScore.measure.get(szScore.measure.size() - 1).tick + scoreClockController.currentMeasureTickAmount) {
                //发送节拍器码
                sendMetronome(currentPlayingTick, peleView);
            }
        }
    }

    /**
     * 显示隐藏预备拍
     */
    private void showHidePeople(PeleView peleView) {
        if (peleView != null) {
            if (peleView.getVisibility() == View.VISIBLE) {
                scoreClockController.peleView.setVisibility(View.GONE);
                scoreClockController.peleView = null;
//                Log.i(TAG,"开启瀑布流");
            }
        }
    }

    /**
     * 发送节拍器码
     */
    private void sendMetronome(int currentPlayingTick, PeleView peleView) {
        int remainder;
        //节拍器
        if (szScore.measure.size() > scoreClockController.getCurrentMeasureNo()) {
            remainder = (currentPlayingTick - szScore.measure.get(scoreClockController.getCurrentMeasureNo()).tick) % (256 * 4 * scoreClockController.currentBeats / scoreClockController.currentBeatType);
        } else {
            remainder = (currentPlayingTick - szScore.measure.get(szScore.measure.size() - 1).tick) % (256 * 4 * scoreClockController.currentBeats / scoreClockController.currentBeatType);
        }
        if (remainder == 0) {
            if (scoreClockController.getStatusModule().isMetronome()) {
                scoreClockController.midiPlay.playMetronome("first");
            }
            if (peleView != null) {
                if (peleView.getVisibility() == View.VISIBLE) {
                    peleView.removeView();
                    ++peleViewIndex;
                }
            }

        }
        if (remainder != 0 && remainder % ((256 * 4 * scoreClockController.currentBeats / scoreClockController.currentBeatType) / scoreClockController.currentBeats) == 0) {
            if (scoreClockController.getStatusModule().isMetronome()) {
                scoreClockController.midiPlay.playMetronome("others");
            }
            if (peleView != null) {
                if (peleView.getVisibility() == View.VISIBLE) {
                    peleView.removeView();
                    ++peleViewIndex;
                }
            }
        }
    }

    /**
     * 修改小节信息
     */
    public void updateMeasure(int current_playing_tick, boolean update) {
        int measureNo;
        if (update) {
            measureNo = scoreClockController.getCurrentMeasureNo() - 1;
            if (measureNo == -1) {
                measureNo = 0;
            }
        } else {
            measureNo = scoreClockController.getCurrentMeasureNo();
        }
        if (current_playing_tick > szScore.measure.get(measureNo).tick - 16
                && current_playing_tick < szScore.measure.get(measureNo).tick + 16) {
            /* 反复记号 */
            backward(measureNo, current_playing_tick);
            if (toCodeIdx != 0) {
                if (measureNo == toCodeIdx) {
                    int measureTick = szScore.measure.get(codeIdx).tick;

                    dsCodeMeasureTick = dsCodeMeasureTick + (measureTick - (szScore.measure.get(toCodeIdx).tick));

                    for (int i = 0; i < szScore.slot.size(); i++) {
                        if (szScore.slot.get(i).tick < measureTick + 16 && szScore.slot.get(i).tick > measureTick - 16) {
                            scoreClockController.setIdNumForNoteOn(i);
                            break;
                        }
                    }
                    current_playing_tick = measureTick;
                    scoreClockController.setCurrentMeasureNo(codeIdx);
                }
            }
            /* 遇到曲谱坊间情况 */
            if (startEndingIdx != 0) {
                if (measureNo == startEndingIdx) {
                    if (endEndingIdx != 0) {
                        endTick = endTick + szScore.measure.get(endEndingIdx).tick - szScore.measure.get(startEndingIdx).tick - 16;

                        int endingTick = szScore.measure.get(endEndingIdx).tick;

                        for (int i = 0; i < szScore.slot.size(); i++) {
                            if (endingTick > szScore.slot.get(i).tick - 16 && endingTick < szScore.slot.get(i).tick + 16) {
                                scoreClockController.setIdNumForNoteOn(i + 1);
                                break;
                            }
                        }
                        scoreClockController.setCurrentMeasureNo(endEndingIdx);
                    }
                }
                if (measureNo > endEndingIdx) {
                    startEndingIdx = 0;
                    endEndingIdx = 0;
                }
            } else {
                if (szScore.measure.get(measureNo).ending < Integer.MAX_VALUE) {
                    startEndingIdx = measureNo;
                    for (int i = measureNo + 1; i < szScore.measure.size(); i++) {
                        if (szScore.measure.get(i).ending < Integer.MAX_VALUE) {
                            endEndingIdx = i;
                            break;
                        }
                    }
                }
            }

            if (measureNo > 0) {
                /* 修改节拍 */
                if (szScore.measure.get(measureNo).beats != 0
                        && szScore.measure.get(measureNo).beatType != 0) {
                    scoreClockController.currentBeats = szScore.measure.get(measureNo).beats;
                    scoreClockController.currentBeatType = szScore.measure.get(measureNo).beatType;
                }
/* Log.i(TAG,"进入改速度"); */
                if (szScore.measure.get(measureNo).bpm != 0) {
                    if (szScore.measure.get(measureNo).bpm != scoreClockController.getMidiSpeed() / scoreClockController.getSpeed()) {
                        float currentTempo = szScore.measure.get(measureNo).bpm * scoreClockController.getSpeed();
                        scoreClockController.setMidiSpeed(currentTempo);
                        scoreClockController.midiPlay.setCurrentTempo(currentTempo, true);
                    }
                }
            }

        }
    }

    private void backward(int measureNo, int currentTick) {
        if (!updateMeasure) {
            //反复记号
            currentMeasureNoIdx = measureNo;
            if (currentMeasureNoIdx > 0) {
                if (szScore.measure.get(currentMeasureNoIdx - 1).backwardMeasureIdx < Integer.MAX_VALUE) {
                    int measureIndex = szScore.measure.get(currentMeasureNoIdx - 1).backwardMeasureIdx;
                    if (measureIndex > 0) {
                        measureIndex = measureIndex - 1;
                    }
                    cumulativeTick = cumulativeTick + ((currentTick - szScore.measure.get(measureIndex).tick)) + 16;
                    int thisTick = szScore.measure.get(measureIndex).tick;
                    for (int i = 0; i < szScore.slot.size(); i++) {
                        if (thisTick > szScore.slot.get(i).tick - 16 && thisTick < szScore.slot.get(i).tick + 16) {
                            scoreClockController.setIdNumForNoteOn(i);
                            break;
                        }
                    }
                    scoreClockController.setCurrentMeasureNo(measureIndex);
                    updateMeasure = true;
                    currentPlayingTick = currentTick - cumulativeTick + endTick - fineTick + dsCodeMeasureTick;
                    /* 同步slot数 */
                }
            }
        } else {
            if (measureNo > currentMeasureNoIdx) {
                updateMeasure = false;
            }
        }
    }

    /**
     * 结束
     */
    public void end() {
        //将最后一页上的结果暂存数组存于总数组
        if (!scoreClockController.startScoreActivity) {
            scoreClockController.correctAndFaultViewArray.add(scoreClockController.tempoCorrectAndFaultViewArrayInPage);
            scoreClockController.correctAndFaultStringArray.add(scoreClockController.tempoCorrectAndFaultStringArrayInPage);
            scoreClockController.startScoreActivity = true;
            scoreClockController.getStatusModule().setRestart(false);
            scoreClockController.stop();

        }
    }

    /**
     * 移动slot
     */
    public void setMoveSlot(boolean isDelete) {
        if (scoreClockController.getCurrentMeasureNo() - 1 < szScore.measure.size()) {
            int slotIndex;
            if (isDelete && scoreClockController.getIdNumForNoteOn() > 1) {
                slotIndex = scoreClockController.getIdNumForNoteOn() - 1;
            } else {
                slotIndex = scoreClockController.getIdNumForNoteOn();
            }
            if (keyBordPitch != null) {
                for (int i = 0; i < keyBordPitch.size(); i++) {
                    upKey(keyBordPitch.get(i));
                }
                keyBordPitch.clear();
            }

            ScoreDataCoordinator.SlotBlock thisSlotBlock = szScore.slot.get(slotIndex);
            ImageView imgSlot = scoreClockController.slotShadowArray.get(0);
            LogUtil.i(TAG, "slotIndex=" + slotIndex);
            whiteLamp = true;
            //移动块
            for (int i = 0; i < thisSlotBlock.note.size(); i++) {
                ScoreDataCoordinator.Note szNote = thisSlotBlock.note.get(i);
                LogUtil.i(TAG, "szNote.x=" + szNote.x + " szNote.y=" + szNote.y + "pitch" + szNote.pitch);
                if (szNote.x != 0 && szNote.y != 0) {
                    ScoreDataCoordinator.Measure thisMeasureSlotBlock;
                    if (scoreClockController.getCurrentMeasureNo() > 0) {
                        thisMeasureSlotBlock = szScore.measure.get(scoreClockController.getCurrentMeasureNo() - 1);
                    } else {
                        thisMeasureSlotBlock = szScore.measure.get(scoreClockController.getCurrentMeasureNo());
                    }

                    double height = (int) ((thisMeasureSlotBlock.staffTopDistance.get(1) - thisMeasureSlotBlock.staffTopDistance.get(0) + 40) * scoreClockController.scoreMarkViewRatioWidth);
//                    double height = 200;
                    double width = 5;
                    LogUtil.i(TAG, "height" + height);
                    imgSlot.setImageResource(R.color.red);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (width),
                            (int) (height));
                    imgSlot.setLayoutParams(layoutParams);
                    if (scoreClockController.getStatusModule().getType() == Constant.TWO_HAND) {
                        imgSlot.setX((int) ((szNote.x + 1) * scoreClockController.scoreMarkViewRatioWidth));
                        imgSlot.setY((int) ((thisMeasureSlotBlock.staffTopDistance.get(0) - 20) * scoreClockController.scoreMarkViewRatioWidth));
                        LogUtil.i(TAG, "(int) ((thisMeasureSlotBlock.staffTopDistance.get(0) - 20) * scoreClockController.scoreMarkViewRatioWidth)" + ((int) ((thisMeasureSlotBlock.staffTopDistance.get(0) - 20) * scoreClockController.scoreMarkViewRatioWidth)));
                        LogUtil.i(TAG, "(int) ((szNote.x + 1) * scoreClockController.scoreMarkViewRatioWidth)=" + ((int) ((szNote.x + 1) * scoreClockController.scoreMarkViewRatioWidth)));


                        downKey(szNote);


                    } else if (scoreClockController.getStatusModule().getType() == Constant.RIGHT_HAND) {
                        if (scoreClockController.getStatusModule().isPractice()) {
                            imgSlot.setX((int) ((szNote.x + 1) * scoreClockController.scoreMarkViewRatioWidth));
                            imgSlot.setY((int) ((thisMeasureSlotBlock.staffTopDistance.get(0) - 20) * scoreClockController.scoreMarkViewRatioWidth));
                            if (szNote.hand == 0) {

                                downKey(szNote);
                            }
                        } else {

                            if (szNote.hand == 1) {
                                imgSlot.setX((int) ((szNote.x + 1) * scoreClockController.scoreMarkViewRatioWidth));
                                imgSlot.setY((int) ((thisMeasureSlotBlock.staffTopDistance.get(0) - 20) * scoreClockController.scoreMarkViewRatioWidth));
                                downKey(szNote);
                            }
                        }
                    } else if (scoreClockController.getStatusModule().getType() == Constant.LEFT_HAND) {

                        if (scoreClockController.getStatusModule().isPractice()) {

                            imgSlot.setX((int) ((szNote.x + 1) * scoreClockController.scoreMarkViewRatioWidth));
                            imgSlot.setY((int) ((thisMeasureSlotBlock.staffTopDistance.get(0) - 20) * scoreClockController.scoreMarkViewRatioWidth));
                            if (szNote.hand == 1) {
                                downKey(szNote);
                            }
                        } else {


                            if (szNote.hand == 0) {
                                imgSlot.setX((int) ((szNote.x + 1) * scoreClockController.scoreMarkViewRatioWidth));
                                imgSlot.setY((int) ((thisMeasureSlotBlock.staffTopDistance.get(0) - 20) * scoreClockController.scoreMarkViewRatioWidth));
                                downKey(szNote);
                            }
                        }

                    }
                }

            }
        }
    }

    public void upKey(final ScoreDataCoordinator.Note szNote) {

        Runnable upRunnable = new Runnable() {
            @Override
            public void run() {
                up(szNote);
            }
        };
        mHandler.post(upRunnable);
    }


    private void downKey(final ScoreDataCoordinator.Note szNote) {
        Runnable downRunnable = new Runnable() {
            @Override
            public void run() {
                down(szNote);
            }
        };
        mHandler.post(downRunnable);
    }

    private void downWhite(final ScoreDataCoordinator.Note szNote) {
        Runnable downRunnable = new Runnable() {
            @Override
            public void run() {
                if (scoreClockController.getUsbControl().getMidiOutputDeviceFromSpinner() != null) {
                    //sendB 方法代表 usb
                    scoreClockController.getUsbControl().sendB(0, 0x69, szNote.pitch);
                }

                if (scoreClockController.blueToothcontrol.getConnectFlag()) {
                    boolean ledWhiteResult = false;
                    String strLedWhite = led1(2, 0, szNote.pitch);
                    int flag=0;
                    while (!ledWhiteResult&&flag<3) {
                        flag++;
                        ledWhiteResult = scoreClockController.blueToothcontrol.sendData(strLedWhite);

                        if (!ledWhiteResult) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    LogUtil.i(TAG, "downWhite=" + ledWhiteResult);
                }
            }
        };
        mHandler.post(downRunnable);
    }

    public void down(ScoreDataCoordinator.Note szNote) {
        if (szNote != null && scoreClockController != null) {

            if (scoreClockController.getStatusModule().isBounceForward()) {
                scoreClockController.keyboard.poinaLamp(szNote.pitch, Constant.DOWN, szNote.hand);
            }

            int velocity;
            int channel;
            if (szNote.hand == 1) {
                Log.e("TAG", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                channel = 0;
                velocity = 80;
            } else {
                Log.e("TAG", "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
                channel = 1;
                velocity = 60;
            }
            if (scoreClockController.getUsbControl().getMidiOutputDeviceFromSpinner() != null) {


                if (scoreClockController.getStatusModule().isPractice() && scoreClockController.getStatusModule().getType() == Constant.TWO_HAND || scoreClockController.getStatusModule().isBounceForward()) {

                    scoreClockController.getUsbControl().sendB(channel + 1, 0x68, szNote.pitch);
                    scoreClockController.getUsbControl().sendB(0, 0x69, szNote.pitch);

                } else {
                    scoreClockController.getUsbControl().sendB(channel + 1, 0x68, szNote.pitch);
                    scoreClockController.getUsbControl().sendB(0, 0x69, szNote.pitch);
                    scoreClockController.getUsbControl().sendMidiOn(0, channel, szNote.pitch, velocity);
                }


            }
            if (scoreClockController.blueToothcontrol.getConnectFlag()) {

                String str = scoreClockController.getMidiPlay().musicOpen(channel, szNote.pitch, velocity);
                boolean result = false;
                String strLed = led1(channel, 0, szNote.pitch);
                str = str.substring(2);
                String strLedWhite = led1(2, 1, szNote.pitch);
                strLed = strLed.substring(2);

                //退出循环的标记    如果发送了三次还是失败就跳出循环（因为可能蓝牙已经断开了连接）
                int flag=0;

                while (!result&&flag<3) {
                        flag++;
                    //如果蓝牙名字中包含“RealPiano”  不发送  str（midi码   让钢琴发声）
                    if (scoreClockController.blueToothcontrol.getmDeviceName().contains("RealPiano")) {

                        result = scoreClockController.blueToothcontrol.sendData(strLedWhite + strLed);

                        if (!result) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //如果是练习并且是双手  不发声
                        if (scoreClockController.getStatusModule().isPractice() && scoreClockController.getStatusModule().getType() == Constant.TWO_HAND || scoreClockController.getStatusModule().isBounceForward()) {

                            result = scoreClockController.blueToothcontrol.sendData(strLedWhite + strLed);


                            if (!result) {
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        } else {

                            result = scoreClockController.blueToothcontrol.sendData(strLedWhite + strLed + str);

                            if (!result) {
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }


                }
                //   LogUtil.i(TAG, "down=" + strLedWhite + strLed + str);

            }

            //如果集合中不存在这个键值，再把键值加进去

            if (!keyBordPitch.contains(szNote)) {
                keyBordPitch.add(szNote);
            }

        }
    }

    public void up(ScoreDataCoordinator.Note szNote) {

        if (scoreClockController.getStatusModule().isBounceForward()) {
            scoreClockController.keyboard.poinaLamp(szNote.pitch, Constant.UP, szNote.hand);
        }
        int velocity;
        int channel;
        if (szNote.hand == 1) {
            channel = 0;
            velocity = 80;
        } else {
            velocity = 60;
            channel = 1;
        }
        if (scoreClockController.getUsbControl().getMidiOutputDeviceFromSpinner() != null) {
            if (scoreClockController.getStatusModule().isPractice() && scoreClockController.getStatusModule().getType() == Constant.TWO_HAND || scoreClockController.getStatusModule().isBounceForward()) {

                scoreClockController.getUsbControl().sendB(channel + 1, 0x68, szNote.pitch);
                scoreClockController.getUsbControl().sendB(0, 0x69, szNote.pitch);

            } else {
                scoreClockController.getUsbControl().sendB(channel + 1, 0x68, szNote.pitch);
                scoreClockController.getUsbControl().sendB(0, 0x69, szNote.pitch);
                scoreClockController.getUsbControl().sendMidiOff(0, channel, szNote.pitch, velocity);
            }


        }
        if (scoreClockController.blueToothcontrol.getConnectFlag()) {
            String str = scoreClockController.getMidiPlay().musicClose(channel, szNote.pitch, velocity);
            boolean ledResult = false;
            String strLed = led1(channel, 1, szNote.pitch);
            str = str.substring(2);
            int flag=0;
            while (!ledResult&&flag<3) {
                flag++;
                //如果蓝牙名字中包含“RealPiano”  不发送  str（midi码   让pad发声）
                if (scoreClockController.blueToothcontrol.getmDeviceName().contains("RealPiano")) {
                    ledResult = scoreClockController.blueToothcontrol.sendData(strLed);

                    if (!ledResult) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                } else {

                    if (scoreClockController.getStatusModule().isPractice() && scoreClockController.getStatusModule().getType() == Constant.TWO_HAND || scoreClockController.getStatusModule().isBounceForward()) {

                        ledResult = scoreClockController.blueToothcontrol.sendData(strLed);

                        if (!ledResult) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    } else {
                        Log.e(TAG, "发命令发命令发命令发命令发命令发命令发命令发命令发命令发命令发命令发命令发命令");




                        ledResult = scoreClockController.blueToothcontrol.sendData(str + strLed);

                        if (!ledResult) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                }
            }
            // LogUtil.i(TAG, "up=" + strLed + str);
        }

    }

    private Runnable moveMeasure = new Runnable() {
        @Override
        public void run() {
            seekToMeasure(scoreClockController.getCurrentMeasureNo());
            scoreClockController.setCurrentMeasureNo(scoreClockController.getCurrentMeasureNo() + 1);
        }
    };

    private Runnable moveSlotBounceForward = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "进入moveSlotBounceForward " + scoreClockController.getCurrentMeasureNo() + "========" + scoreClockController.szScore.slot.get(scoreClockController.getIdNumForNoteOn()).note.get(0).measure);
            int measureIndex;
            if (scoreClockController.szScore.hasZeroMeasure) {
                measureIndex = scoreClockController.szScore.slot.get(scoreClockController.getIdNumForNoteOn()).note.get(0).measure + 1;
            } else {
                measureIndex = scoreClockController.szScore.slot.get(scoreClockController.getIdNumForNoteOn()).note.get(0).measure;
            }
            if (scoreClockController.getIdNumForNoteOn() < scoreClockController.szScore.slot.size() &&
                    scoreClockController.getCurrentMeasureNo() != measureIndex) {
                seekToMeasure(scoreClockController.getCurrentMeasureNo());
                scoreClockController.setCurrentMeasureNo(scoreClockController.getCurrentMeasureNo() + 1);
                LogUtil.i(TAG, "进入moveSlotBounceForward   scoreClockController.szScore.slot.get(scoreClockController.getIdNumForNoteOn()).note.get(0).measure=" + scoreClockController.szScore.slot.get(scoreClockController.getIdNumForNoteOn()).note.get(0).measure);
                LogUtil.i(TAG, "进入moveSlotBounceForward  scoreClockController.getCurrentMeasureNo()=" + scoreClockController.getCurrentMeasureNo());
            }
            if (!scoreClockController.getStatusModule().isWaterfall()) {
                setMoveSlot(false);
            }
            scoreClockController.setIdNumForNoteOn(scoreClockController.getIdNumForNoteOn() + 1);
        }
    };

    private Runnable moveSlot = new Runnable() {
        @Override
        public void run() {
            if (!scoreClockController.getStatusModule().isWaterfall()) {
                setMoveSlot(false);
            }
            scoreClockController.setIdNumForNoteOn(scoreClockController.getIdNumForNoteOn() + 1);
        }
    };

    /**
     * 跳到指定小节=========================================================
     *
     * @param measureNo 小节数
     */
    public void seekToMeasure(int measureNo) {
        if (!scoreClockController.isFirst()) {
            if (scoreClockController.getStatusModule().isWaterfall()) {
                if (scoreClockController.scoreMarkView.getVisibility() == View.VISIBLE) {
                    scoreClockController.scoreMarkView.setVisibility(View.GONE);
                }
            } else {
                if (scoreClockController.scoreMarkView.getVisibility() == View.GONE) {
                    scoreClockController.scoreMarkView.setVisibility(View.VISIBLE);
                }
            }
        }
        if (measureNo < szScore.measure.size()) {
            //是否要翻页
            boolean newMeasureNoStatus = szScore.measure.get(measureNo).newPage;
            if (newMeasureNoStatus) {
                //当为第一页时
                if (measureNo == 0) {
                    scoreClockController.tempoCorrectAndFaultViewArrayInPage = null;
                    scoreClockController.tempoCorrectAndFaultViewArrayInPage = new ArrayList<>();
                    scoreClockController.tempoCorrectAndFaultStringArrayInPage = null;
                    scoreClockController.tempoCorrectAndFaultStringArrayInPage = new ArrayList<>();
                }
                //不为第一页时
                if (measureNo != 0) {
                    //将上一个结果暂存数组存于总数组
                    scoreClockController.correctAndFaultViewArray.add(scoreClockController.tempoCorrectAndFaultViewArrayInPage);
                    scoreClockController.correctAndFaultStringArray.add(scoreClockController.tempoCorrectAndFaultStringArrayInPage);
                    scoreClockController.currentPageNo++;
                    scoreClockController.pageUpAndDown(scoreClockController.currentPageNo);
                }
                //移除之前一页的结果记号
                if (scoreClockController.tempoCorrectAndFaultViewArrayInPage.size() > 0) {
                    for (int i = 0; i < scoreClockController.tempoCorrectAndFaultViewArrayInPage.size(); i++) {
                        scoreClockController.scoreMarkView.removeView(scoreClockController.tempoCorrectAndFaultViewArrayInPage.get(i));
                    }
                    //初始化每一页的正误符号暂存数组
                    scoreClockController.tempoCorrectAndFaultViewArrayInPage = new ArrayList<>();
                    scoreClockController.tempoCorrectAndFaultStringArrayInPage = new ArrayList<>();
                }
            }
        }
        View tempoMeasureShadow = scoreClockController.measureShadowArray.get(0);
        if (measureNo < szScore.measure.size()) {
            ScoreDataCoordinator.Measure thisMeasureSlotBlock = szScore.measure.get(measureNo);
            int width = (int) (thisMeasureSlotBlock.width * scoreClockController.scoreMarkViewRatioWidth);
            int height = (int) ((thisMeasureSlotBlock.staffTopDistance.get(1) - thisMeasureSlotBlock.staffTopDistance.get(0) + 40) * scoreClockController.scoreMarkViewRatioWidth);
            int x = (int) (thisMeasureSlotBlock.left * scoreClockController.scoreMarkViewRatioWidth);
            int y = (int) ((thisMeasureSlotBlock.staffTopDistance.get(0) - 20) * scoreClockController.scoreMarkViewRatioWidth);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,
                    height);
            tempoMeasureShadow.setLayoutParams(layoutParams);
            tempoMeasureShadow.setX(x);
            tempoMeasureShadow.setY(y);
            scoreClockController.getPdfView().moveTo(0, -((int) ((tempoMeasureShadow.getY() + tempoMeasureShadow.getHeight() / 2) - scoreClockController.self.getHeight() * 0.25)));
            scoreClockController.scoreMarkView.scrollTo(0, (int) ((tempoMeasureShadow.getY() + tempoMeasureShadow.getHeight() / 2) - scoreClockController.self.getHeight() * 0.25));
        }
    }

    /**
     * 指到指定slotBlock=============================================
     */

    private void seekToSlotBlock(int slotBlockNo) {
        if (szScore.slot.size() > 0) {
            ScoreDataCoordinator.SlotBlock szNote = szScore.slot.get(slotBlockNo - 1);
            ScoreDataCoordinator.Measure thisMeasureSlotBlock = szScore.measure.get(slotBlockNo - 1);
            int x = szNote.note.get(0).x;
            int y = szNote.note.get(0).y;
            if (x != 0 && y != 0) {
                if (scoreClockController.slotShadowArray.size() > 0) {
                    View block = scoreClockController.slotShadowArray.get(0);
                    block.setX((int) ((szNote.note.get(0).x + 1) * scoreClockController.scoreMarkViewRatioWidth));
                    block.setY((int) ((thisMeasureSlotBlock.staffTopDistance.get(0) - 20) * scoreClockController.scoreMarkViewRatioWidth));
                }
            }
        }
    }

    public void setListener(InterfaceScoreControllerData interfaceScoreControllerData) {
        this.interfaceScoreControllerData = interfaceScoreControllerData;
    }

    public interface InterfaceScoreControllerData {
        void result(String s);
    }

}
