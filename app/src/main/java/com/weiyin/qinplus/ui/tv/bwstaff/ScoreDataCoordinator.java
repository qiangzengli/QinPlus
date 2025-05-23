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
    /**
     * 第一小节的id 值，0,或者1
     */
    private int firstMeasureIdx;
    private static final String TAG = ScoreDataCoordinator.class.getSimpleName();

    /**
     * 记录当前音符的所有信息实体
     */
    class Note {
        /**
         * 音符的序列
         */
        int idx;
        /**
         * 音符的X，通常是时间轴的位置，越靠右越晚
         */
        int x;
        /**
         * 音符的Y，通常是音高的位置，越靠上，越晚
         */
        int y;
        /**
         * 音符对应的小节数
         */
        int measure;
        /**
         * note值  音符在键盘上对应的值，
         * 字面意思是音高
         */
        int pitch;
        /**
         * 音乐中表示声部
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
         * tick值，在音乐和MIDI 中，“tick” (时钟脉冲) 表示音乐时间的基本单位，通常用作衡量音符的长度和节奏的单位。它相当于一个很小的拍点，可以用于控制音符何时开始和结束，以及各种音符和节奏的细微调整。
         * 举个例子，在MIDI 标准中，一个四分音符通常被划分为480 个tick，这意味着1 个四分音符的长度可以被精确地分割成480 个tick。每个tick 都可以被单独控制，使得乐曲的节奏和音符的长度可以更加精细地控制。
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
        //每小节有多少拍 4 表示四拍
        int beats;
        // 以什么音符为一拍（拍值）4 表示四分音符为一拍，跟beats 合起来表示: 每小节几拍 + 每拍是什么时值,拍号（Time Signature）
        int beatType;
        Region region;
        /**
         * Speed
         * 在音乐中，BPM 是 Beats Per Minute（每分钟节拍数） 的缩写，用来表示音乐的速度（tempo）。
         */
        int bpm;
        ArrayList<Double> staffTopDistance = new ArrayList<>();
        /**
         * 反复记号值 在音乐中，反复记号（repeat sign）是用来指示乐曲某段需要重复演奏的标记。
         */
        int backwardMeasureIdx;

        /**
         * fine记号值,在音乐中，Fine（菲内） 是一个表示“结束”的术语，用来告诉演奏者在重复或返回之后，在此处停止演奏。
         */
        int fineMeasureIdx;
        /**
         * DCFine记号值,在音乐中，DC Fine 是一个结束或重复指示的术语，出现在乐谱中，用于指示演奏者返回曲首（D.C. = Da Capo）并在特定位置“Fine”结束。
         */
        int dCFineMeasureIdx;

        /**
         * 坊间记号
         *
         * 在音乐中，ending（结尾）通常指乐曲的终止部分，但在乐谱结构中，它还有更具体的含义，特别是配合重复（repeat）使用时。
         *
         * 🎼 1. 一般含义：Ending = 结尾段
         * 指乐曲的收尾部分，用于结束整首曲子。
         *
         * 常见于任何曲式结构，如 A-B-A（再现部）、Coda 等。
         *
         * 🎵 2. 特殊用法：1st / 2nd Ending（第1/2结尾）
         * 在有反复播放的乐谱中，ending 表示不同的结尾路线，配合反复记号使用。
         *
         * 📖 举例说明：
         * less
         * 复制
         * 编辑
         * |: A - A - A :|1. 结尾1
         *              |2. 结尾2
         * 首次演奏时，从第1结尾结束，再回到反复开始。
         *
         * 第二次演奏时跳过第1结尾，走第2结尾结束。
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
         * 弱起，什么是弱起？
         * 乐句或小节开始前的不完整起拍，即小节之前的几拍，通常用来引入旋律或节奏。
         * <p>
         * 这些起拍的音符不构成完整的小节，而是连接到下一小节。
         */
        Boolean hasZeroMeasure;
        ArrayList<Measure> measure;

        /**
         * Slot
         */
        // slot 的数量
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

        // Measure 从DB 中读取小节数
        int measureAmount = Integer.valueOf(myDBManager.getRecordsWhere("measure-amount"));
        score.measure = new ArrayList<>();
        // 判断是否有弱起小节，并把所有的小节读取到
        for (int i = 0, offset = 0, l = measureAmount; i < l; i++) {
            Measure measure1 = new Measure();
            measure1 = measureIdx(i);
            // 如果当前小节width ==0
            if (measure1.width == 0) {
                // 第一小节索引从1 开始
                firstMeasureIdx = 1;

                offset -= 1;
                l += 1;
                continue;
            }
            // 将当前的小节实体添加到measure List 中
            score.measure.add(measure1);
        }

        // 是否有弱起
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
        /**
         * 表示该小节（measure）在页面上起始位置的横坐标（X 轴位置），单位是“tenths”（1/10 音符头的宽度，MusicXML 的默认单位）
         */
        measure.left = Integer.valueOf(myDBManager.getRecordsWhere("measure-left-x", 0, measure.idx, 0, 0, 0, "0"));
        /**
         * width 是排版信息，控制在软件中小节显示的横向空间。
         * 对于自动换行、对齐歌词、定位小节线等视觉布局非常重要。
         * 不影响音符节拍、节奏播放等“逻辑”内容。
         */
        measure.width = Integer.valueOf(myDBManager.getRecordsWhere("measure-width", 0, measure.idx, 0, 0, 0, "0"));

        if (measure.width == 0) {
            return new Measure();
        }

        // Position: staffTop
        // 在音乐相关的上下文中，staffNum（staff number）通常指的是：
        //
        //staffNum 的含义
        //staff：五线谱中的一组五条平行线，承载乐谱的音符和符号。
        //
        //staffNum：表示当前乐谱或乐曲中某个五线谱的编号（序号）。
        int staffNum = 2;
        for (int idx = 0; idx < staffNum; idx++) {
//            在音乐中，instrument 指的是“乐器”，也就是演奏某段音乐的具体声音来源。
//
//            instrument 的含义
//            表示用来演奏乐曲中某个声部或音轨的具体乐器，比如钢琴、小提琴、长笛、鼓等。
//
//            在多乐器合奏中，instrument 用来区分不同的声音来源和演奏者。
            int instrument = 1;
            double top = Double.valueOf(myDBManager.getRecordsWhere("measure-height", instrument, measure.idx, idx + 1, 0, 0, "0"));
            measure.staffTopDistance.add(top);
        }
        // NewLine & NewPage
        /**
         * 查询 “new-page—measure-no” 为 YES 的字段，标记当前小节是要换页的小节， 将当前的小节号记录到beginMeasureNoPerPageArray List 中
         */
        String np = myDBManager.getRecordsWhere("new-page—measure-no", 0, measure.idx, 0, 0, 0, "0");
        String kYesStr = "YES";
        if (np.equalsIgnoreCase(kYesStr)) {
            measure.newPage = true;
            score.beginMeasureNoPerPageArray.add(measure.idx);
        }
        /**
         * 查询 “new-row—measure-no” 为 YES 的字段，标记当前小节是要换行的小节， 将当前的小节号记录到beginMeasureNoPerRowArray List 中
         */
        String nr = myDBManager.getRecordsWhere("new-row—measure-no", 0, measure.idx, 0, 0, 0, "0");
        if (nr.equalsIgnoreCase(kYesStr)) {
            measure.newLine = true;
            score.beginMeasureNoPerRowArray.add(measure.idx);
        }

        /* Tick ，读取小节 tick*/
        measure.tick = Integer.valueOf(myDBManager.getRecordsWhere("tick-for-measure", 0, measure.idx, 0, 0, 0, "0"));

        String bKey = "backward";
        String fKey = "forward";
        /*在音乐中，bar line（小节线）是乐谱上的一条竖线，用来划分乐曲中的小节（measure），帮助演奏者理解节拍结构和节奏。

        1. 什么是 Bar Line？
        Bar line 是竖直画在五线谱上的线条，标记小节的开始和结束。

        通过小节线，乐谱被划分为一个个小节，每个小节包含一定数量的拍子（由拍号决定）。*/
        // 这个字段不是所哟db文件中都有
        String status = myDBManager.getRecordsWhere("bar-line", 0, measure.idx, 0, 0, 0, "0");
        if (status.equalsIgnoreCase(bKey)) {
            measure.backwardMeasureIdx = forewarnMeasureIda;
        } else {
            measure.backwardMeasureIdx = Integer.MAX_VALUE;
            if (status.equalsIgnoreCase(fKey)) {
                forewarnMeasureIda = measure.idx;
            }
        }


        /**
         * ChatGpt 释义
         * String fine = "fine";              // 乐曲结束于此处（终止记号）
         * String dCFine = "D.C. al Fine";    // 从头反复至 Fine（Da Capo al Fine）
         * String dSCode = "D.S. al Coda";    // 从 ∆ 开始，跳转到 Coda（Dal Segno al Coda）
         * String sago = "sago";              // 可能是写错了，应该是 "Segno"（记号）
         * String toCode = "To Coda";         // 从当前位置跳转到 Coda 部分
         * String code = "CODA";              // 表示“尾声”部分的起点
         *
         * 术语	意思	作用
         * Fine	终止	乐曲在标有 Fine 的地方结束
         * D.C. al Fine	从头反复至 Fine	从乐曲开头回到 Fine 所在位置
         * D.S. al Coda	从 Segno 记号处回到，并跳到 Coda	从 Segno 记号跳转，然后在 To Coda 处跳到 Coda
         * Segno	记号（标记某个位置）	用于 D.S. 跳转的起点
         * To Coda	跳转到 Coda 的提示	在此处跳转到 Coda
         * Coda	尾声段落	表示从 To Coda 跳转后的目标段落
         */
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
//每小节有多少拍 4 表示四拍
        measure.beats = Integer.valueOf(myDBManager.getRecordsWhere("beats", 0, measure.idx, 0, 0, 0, "0"));
        // 以什么音符为一拍（拍值）4 表示四分音符为一拍，跟beats 合起来表示: 每小节几拍 + 每拍是什么时值,拍号（Time Signature）
        measure.beatType = Integer.valueOf(myDBManager.getRecordsWhere("beat-type", 0, measure.idx, 0, 0, 0, "0"));
        /**
         * Speed
         * 在音乐中，BPM 是 Beats Per Minute（每分钟节拍数） 的缩写，用来表示音乐的速度（tempo）。
         */
        int bpm = Integer.valueOf(myDBManager.getRecordsWhere("per-minute", 0, measure.idx, 0, 0, 0, "0"));
        if (bpm == 0) {
            // 小节数不为空
            if (!score.measure.isEmpty()) {
                // bpm = 最后一个小节的bpm
                measure.bpm = score.measure.get(score.measure.size() - 1).bpm;
            } else {
                // 否则的话就==0
                measure.bpm = bpm;
            }
        } else {
            // 等于读取到的bpm
            measure.bpm = bpm;
            // 将小节添加到 variableSpeed list 中
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
