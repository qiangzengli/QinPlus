package com.weiyin.qinplus.ui.tv.bwstaff;

import android.content.Context;
import android.graphics.RectF;
import android.util.Log;

import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.ui.tv.bwstaff.DB.TagWaterFall;
import com.weiyin.qinplus.ui.tv.waterfall.KeyBoard;
import com.weiyin.qinplus.ui.tv.waterfall.MeasureNote;
import com.weiyin.qinplus.ui.tv.waterfall.WaterFallSurfaceView;
import com.weiyin.qinplus.ui.tv.waterfall.WaterfallInfo;

import java.util.ArrayList;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 控制瀑布流
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class ScoreWaterfall {

    private ArrayList<Integer> standTimeArrayList = new ArrayList<Integer>();
    private ArrayList<MeasureNote> standMeasureNotes = new ArrayList<MeasureNote>();
    private boolean bFlagBlackKey = false;

    private ScoreController bwStaff;
    private KeyBoard keyBoard;
    WaterFallSurfaceView waterFallSurfaceView;

    public void clean() {
        if (bwStaff != null) {
            bwStaff = null;
        }
        if (waterFallSurfaceView != null) {
            waterFallSurfaceView.recycle();
            waterFallSurfaceView = null;
        }
        if (keyBoard != null) {
            keyBoard = null;
        }

        if (standMeasureNotes != null) {
            standMeasureNotes = null;
        }
    }

    public ScoreWaterfall(ScoreController scoreController, KeyBoard keyBoard, Context context, WaterFallSurfaceView waterFallSurfaceView) {
        this.bwStaff = scoreController;
        this.keyBoard = keyBoard;
        this.waterFallSurfaceView = waterFallSurfaceView;
    }

    public void setShutter(int iCentime, int startTime) {
        ArrayList<WaterfallInfo> shutters = new ArrayList<WaterfallInfo>();
        float shutterHeight;
        float shutterWidth;
        ArrayList<MeasureNote> testList;

        shutterHeight = waterFallSurfaceView.getHeight();
        shutterWidth = waterFallSurfaceView.getWidth() / 52;
        testList = standMeasureNotes;

        for (MeasureNote event : testList) {
            long len = event.duration;
            float shutterSpeed = 0.20f;
            float height = len * shutterSpeed;
            if (height < shutterWidth) {
                height = shutterWidth;
            }
            if (event.time >= startTime - 64) {
                float y = shutterHeight - (event.time - iCentime) * shutterSpeed - height;
                if (y < shutterHeight && y + height > 0 && len > 0) {
                    int staff = event.staff;
                    //int line = event.line;
                    boolean isBlackKey = false;
                    float x = pianoKeyBoardOffset(event.notenumber, staff);
                    isBlackKey = bFlagBlackKey;
                    bFlagBlackKey = false;
                    float width = shutterWidth;
                    if (isBlackKey) {
                        width = shutterWidth * 0.7f;
                    } else {
                        width = shutterWidth;
                    }
                    RectF rectF = new RectF();
                    rectF.left = x + 1;
                    rectF.top = y;
                    rectF.right = rectF.left + width;
                    rectF.bottom = (rectF.top + height);


                    WaterfallInfo testInfo = new WaterfallInfo();
                    testInfo.blackkey = isBlackKey;
                    testInfo.pos = rectF;
                    testInfo.staff = staff;
                    shutters.add(testInfo);
                }
            }
        }
        waterFallSurfaceView.setShutters(shutters);
    }

    private float pianoKeyBoardOffset(int noteNumber, int staff) {
        float shuterWidth = (float) waterFallSurfaceView.getWidth() / 52;

        int octave = noteNumber / 12;
        int step = noteNumber % 12;
        float[] aligns = {0, 0.65f, 1, 1.65f, 2, 3, 3.65f, 4, 4.65f, 5, 5.65f, 6};
        bFlagBlackKey = step == 1 || step == 3 || step == 6 || step == 8 || step == 10;
        float x = (octave - 2) * 7 * shuterWidth + 2 * shuterWidth + aligns[step] * shuterWidth;
        //float x = shuter_width * ((octave-2) * 7 + aligns[step] + 3) - shuter_width / 2;
        return x;
    }

    public void initData(ArrayList<TagWaterFall> tagWaterFalls, boolean isHasZeroMeasure, int date) {
        Log.i("isHasZeroMeasure", "isHasZeroMeasure=" + isHasZeroMeasure + " date=" + date);
        if (tagWaterFalls.size() > 0) {
            standMeasureNotes.clear();
            for (int i = 0; i < tagWaterFalls.size(); i++) {
                TagWaterFall tagWaterFall = tagWaterFalls.get(i);
                MeasureNote mmnn = new MeasureNote();
                if (bwStaff.getStatusModule().getType() == Constant.LEFT_HAND) {
                    if (tagWaterFall.leftRight != Constant.LEFT_HAND) {
                        continue;
                    }
                } else if (bwStaff.getStatusModule().getType() == Constant.RIGHT_HAND) {
                    if (tagWaterFall.leftRight != Constant.RIGHT_HAND) {
                        continue;
                    }
                }
                mmnn.staff = tagWaterFall.leftRight;
                mmnn.notenumber = tagWaterFall.note;
                mmnn.duration = tagWaterFall.duration;
                if (isHasZeroMeasure) {
                    /* 改动 */
                    mmnn.time = tagWaterFall.startTime;
                } else {
                    mmnn.time = tagWaterFall.startTime + date;
                }
                standTimeArrayList.add(mmnn.time);
                standMeasureNotes.add(mmnn);
            }
            keyBoard.setMeasureNotes(standTimeArrayList, standMeasureNotes, false, bwStaff);
        } else {
            Log.i("ScoreWaterfall", "tagWaterFalls = null");
        }
    }
}
