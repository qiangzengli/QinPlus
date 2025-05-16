package com.weiyin.qinplus.ui.tv.bwstaff;


import android.content.Context;
import android.graphics.Region;
import android.util.Log;

import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.ui.tv.bwstaff.DB.DBManager;

import java.io.File;
import java.util.ArrayList;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 解析数据库
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class ScoreDataCoordinator {
    private DBManager myDBManager;

    private Score score;
    private int forewarnMeasureIda;
    private int firstMeasureIdx;
    private static final String TAG = ScoreDataCoordinator.class.getSimpleName();

    class Note {
        /**
         * 音符的序列
         */
        int idx;
        /**
         * 音符的X
         */
        int x;
        /**
         * 音符的Y
         */
        int y;
        /**
         * 音符对应的小节数
         */
        int measure;
        /**
         * note值  音符在键盘上对应的值
         */
        int pitch;
        /**
         * 音符的左右手
         */
        int voice;
        /**
         * 音符的左右手
         */
        int hand;
    }

    class SlotBlock {
        /**
         * 同一tick的音节序号
         */
        int idx;
        /**
         * tick值
         */
        int tick;
        /**
         * 同一tick的音节数
         */
        int noteAmount;
        /**
         * 存储音节的信息数组
         */
        ArrayList<Note> note = new ArrayList<>();
        ArrayList<Note> notePractice = new ArrayList<>();

    }

    class Measure {
        int idx;
        int tick;
        int backNoteIdx;
        boolean newLine;
        boolean newPage;
        double left;
        double width;
        int beats;
        int beatType;
        Region region;
        /**
         * Speed
         */
        int bpm;
        ArrayList<Double> staffTopDistance = new ArrayList<>();
        /**
         * 反复记号值
         */
        int backwardMeasureIdx;

        /**
         * fine记号值
         */
        int fineMeasureIdx;
        /**
         * DCFine记号值
         */
        int dCFineMeasureIdx;

        /**
         * 坊间记号
         */
        int ending;

        int dSCodeMeasureIdx;
        int sago;
        int toCode;
        int code;
    }

    class Score {

        /**
         * Page
         */
        double pageWidth;
        double pageHeight;
        double scoreSize;


        ArrayList<Integer> beginMeasureNoPerRowArray = new ArrayList<>();
        ArrayList<Integer> beginMeasureNoPerPageArray = new ArrayList<>();

        /**
         * 弱起
         */
        Boolean hasZeroMeasure;
        ArrayList<Measure> measure;

        /**
         * Slot
         */
        int slotAmount;
        ArrayList<SlotBlock> slot;

        int slotSize = 0;

        ArrayList<Measure> variableSpeed = new ArrayList<>();
    }

    public static ScoreDataCoordinator coordinator = null;

    public static ScoreDataCoordinator sharedCoordinator() {
        if (coordinator == null) {
            coordinator = new ScoreDataCoordinator();
        }
        return coordinator;
    }

    public void clean() {
        score = null;
        myDBManager = null;
        coordinator = null;
    }

    private void ready() {
        forewarnMeasureIda = 0;
        firstMeasureIdx = 0;
        score = new Score();
    }

    public void closeSQLData() {
        myDBManager.closeDatabase();
    }

    Score loadFile(Context context, String fileName) {
        ready();
        File file = new File(fileName);
        myDBManager = new DBManager(context, file.getName());
        myDBManager.openDatabase(file.getPath());

        if (myDBManager == null) {
            return null;
        }

        // Global data: pageSize\bpm\beat\beatType\scoreHeight
        // Global data: pageSize\scoreHeight
        score.pageWidth = Integer.valueOf(myDBManager.getRecordsWhere("page-width"));
        score.pageHeight = Integer.valueOf(myDBManager.getRecordsWhere("page-height"));
        score.scoreSize = Double.valueOf(myDBManager.getRecordsWhere("font-size"));

        // Measure
        int measureAmount = Integer.valueOf(myDBManager.getRecordsWhere("measure-amount"));
        score.measure = new ArrayList<>();
        for (int i = 0, offset = 0, l = measureAmount; i < l; i++) {
            Measure measure1 = new Measure();
            measure1 = measureIdx(i);
            if (measure1.width == 0) {
                firstMeasureIdx = 1;
                offset -= 1;
                l += 1;
                continue;
            }
            score.measure.add(measure1);
        }


        score.hasZeroMeasure = (firstMeasureIdx == 0);

        // Note
        myDBManager.getNoteOnMessage();
        myDBManager.closeDatabase();
        return score;
    }

    private Measure measureIdx(int i) {
        Measure measure = new Measure();
        measure.idx = firstMeasureIdx == 1 ? i - 1 : i;
        measure.backNoteIdx = Integer.MAX_VALUE;

        // Position: left & width
        measure.left = Integer.valueOf(myDBManager.getRecordsWhere("measure-left-x", 0, measure.idx, 0, 0, 0, "0"));
        measure.width = Integer.valueOf(myDBManager.getRecordsWhere("measure-width", 0, measure.idx, 0, 0, 0, "0"));

        if (measure.width == 0) {
            return new Measure();
        }

        // Position: staffTop
        int staffNum = 2;
        for (int idx = 0; idx < staffNum; idx++) {
            int instrument = 1;
            double top = Double.valueOf(myDBManager.getRecordsWhere("measure-height", instrument, measure.idx, idx + 1, 0, 0, "0"));
            measure.staffTopDistance.add(top);
        }
        // NewLine & NewPage
        String np = myDBManager.getRecordsWhere("new-page—measure-no", 0, measure.idx, 0, 0, 0, "0");
        String kYesStr = "YES";
        if (np.equalsIgnoreCase(kYesStr)) {
            measure.newPage = true;
            score.beginMeasureNoPerPageArray.add(measure.idx);
        }
        String nr = myDBManager.getRecordsWhere("new-row—measure-no", 0, measure.idx, 0, 0, 0, "0");
        if (nr.equalsIgnoreCase(kYesStr)) {
            measure.newLine = true;
            score.beginMeasureNoPerRowArray.add(measure.idx);
        }

        /* Tick */
        measure.tick = Integer.valueOf(myDBManager.getRecordsWhere("tick-for-measure", 0, measure.idx, 0, 0, 0, "0"));

        String bKey = "backward";
        String fKey = "forward";
        String status = myDBManager.getRecordsWhere("bar-line", 0, measure.idx, 0, 0, 0, "0");
        if (status.equalsIgnoreCase(bKey)) {
            measure.backwardMeasureIdx = forewarnMeasureIda;
        } else {
            measure.backwardMeasureIdx = Integer.MAX_VALUE;
            if (status.equalsIgnoreCase(fKey)) {
                forewarnMeasureIda = measure.idx;
            }
        }


        /* DC. al fine */
        String fine = "fine";
        String dCFine = "D.C. al Fine";
        String dSCode = "D.S. al Coda";
        String sago = "sago";
        String toCode = "To Coda";
        String code = "CODA";
        String fineStatus = myDBManager.getRecordsWhere("words", 0, measure.idx, 0, 0, 0, "0");
        if (fineStatus.equalsIgnoreCase(fine)) {
            measure.fineMeasureIdx = measure.idx;
        } else {
            measure.fineMeasureIdx = Integer.MAX_VALUE;
        }
        if (fineStatus.equalsIgnoreCase(dCFine)) {
            measure.dCFineMeasureIdx = measure.idx;
        } else {
            measure.dCFineMeasureIdx = Integer.MAX_VALUE;
        }
        if (fineStatus.equalsIgnoreCase(dSCode)) {
            measure.dSCodeMeasureIdx = measure.idx;
        } else {
            measure.dSCodeMeasureIdx = Integer.MAX_VALUE;
        }
        if (fineStatus.equalsIgnoreCase(sago)) {
            measure.sago = measure.idx;
        } else {
            measure.sago = Integer.MAX_VALUE;
        }
        if (fineStatus.equalsIgnoreCase(toCode)) {
            measure.toCode = measure.idx;
        } else {
            measure.toCode = Integer.MAX_VALUE;
        }
        if (fineStatus.equalsIgnoreCase(code)) {
            measure.code = measure.idx;
        } else {
            measure.code = Integer.MAX_VALUE;
        }

        /* ending */
        String endingStatus = myDBManager.getRecordsWhere("ending", 0, measure.idx, 0, 0, 0, "0");
        if (endingStatus.equalsIgnoreCase("start")) {
            measure.ending = measure.idx;
            Log.i(TAG, "ending=" + measure.ending);
        } else {
            measure.ending = Integer.MAX_VALUE;
        }

        measure.beats = Integer.valueOf(myDBManager.getRecordsWhere("beats", 0, measure.idx, 0, 0, 0, "0"));
        measure.beatType = Integer.valueOf(myDBManager.getRecordsWhere("beat-type", 0, measure.idx, 0, 0, 0, "0"));

        int bpm = Integer.valueOf(myDBManager.getRecordsWhere("per-minute", 0, measure.idx, 0, 0, 0, "0"));
        if (bpm == 0) {
            if (score.measure.size() - 1 >= 0) {
                measure.bpm = score.measure.get(score.measure.size() - 1).bpm;
            } else {
                measure.bpm = bpm;
            }
        } else {
            measure.bpm = bpm;
            score.variableSpeed.add(measure);
        }
        return measure;
    }

    public void slotBlockAmount(int slotBlockAmount) {
        score.slotAmount = slotBlockAmount;

        score.slot = new ArrayList<>();
    }

    private int index = 0;

    public void noteIdNum(int i, int tick, int measure, int staff, int voice, int numInVoice, int pitch, int x, int y, int hand) {

        Note note = new Note();
        note.idx = numInVoice;
        note.voice = voice;
        note.pitch = pitch;
        note.x = x;
        note.y = y;
        note.hand = hand;
        note.measure = measure;
        StatusModule statusModule = StatusModule.getStatusModule();

//        if (statusModule.getType() == Constant.RIGHT_HAND) {
//            if (hand != 1) {
//                return;
//            }
//        } else if (statusModule.getType() == Constant.LEFT_HAND) {
//            if (hand != 0) {
//                return;
//            }
//        }

        if (i != index) {
            SlotBlock szSlotBlock = new SlotBlock();
            szSlotBlock.idx = score.slot.size() - 1;
            szSlotBlock.tick = tick;
            szSlotBlock.noteAmount = 0;
            score.slot.add(szSlotBlock);
            if (pitch != 0) {
                score.slotSize++;
            }
        }
        int measureNo = firstMeasureIdx == 1 ? measure - 1 : measure;
        if (score.measure.get(measureNo).backNoteIdx == Integer.MAX_VALUE) {
            score.measure.get(measureNo).backNoteIdx = score.slot.size() - 1;
        }
        if (score.slot.size() > 0) {
            int a = score.slot.get(score.slot.size() - 1).noteAmount;
            score.slot.get(score.slot.size() - 1).note.add(note);
            //score.slot.get(score.slot.size()-1).notePractice.add(note);
            score.slot.get(score.slot.size() - 1).noteAmount++;
        }
        index = i;
    }

}
