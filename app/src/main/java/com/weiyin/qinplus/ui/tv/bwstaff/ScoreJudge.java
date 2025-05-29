package com.weiyin.qinplus.ui.tv.bwstaff;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.commontool.LogUtil;

import java.util.ArrayList;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 比对弹奏信息
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class ScoreJudge {
    private final int CORRECT_DURATION_IN_TICK = 50;

    public final String TAG = ScoreJudge.class.getSimpleName();
    private ScoreController scoreClockController;

    private int slotIdx = 0;

    private boolean in = false;
    private ScoreDataCoordinator.Score szScore;

    public int getSlotIdx() {
        return slotIdx;
    }

    public void setSlotIdx(int slotIdx) {
        this.slotIdx = slotIdx;
    }

    public ScoreJudge(ScoreController scoreController, ScoreDataCoordinator.Score szScore) {
        this.scoreClockController = scoreController;
        this.szScore = szScore;
    }

    public void init() {
        slotIdx = 0;
    }

    /**
     * 弹对前进的judge 方法
     */
    public boolean judge() {
        boolean result = inspectResult(slotIdx, true)[0].equalsIgnoreCase("correct");
        if (result) {
            slotIdx++;
        }
        return result;
    }

    /**
     * 练习模式的judge 方法
     * @param tick 当前节奏的tick
     */
    public void judge(int tick) {
        if (in && (tick >= szScore.slot.get(slotIdx).tick + CORRECT_DURATION_IN_TICK)) {
            //当当前的tick值大于当前的音符的tick值 + 50后和进入计算
            inspectResult(slotIdx, false);
            //计算完后切换下一个音符
            slotIdx++;

            //设置是否可进入（用户在钢琴上输入的内容可记录）
            if (slotIdx == szScore.slot.size() || tick < szScore.slot.get(slotIdx).tick - CORRECT_DURATION_IN_TICK) {
                in = false;
            }
        }
        if (slotIdx != szScore.slot.size()
                && (tick >= szScore.slot.get(slotIdx).tick - CORRECT_DURATION_IN_TICK)) {
            //当前的tick值 大于当前音符的对应的tick值-50 可进入（用户在钢琴上输入的内容可记录）
            in = true;
        }
    }


    /**
     * 判断每个slot演奏的正误=============================================
     *
     * @param slotBlockNo   小节数
     * @param bounceForward 是否为弹奏前进
     * @return 返回结果
     */
    private String[] inspectResult(int slotBlockNo, boolean bounceForward) {
/* Log.i(TAG,"slotBlockNo="+slotBlockNo); */
        // 右手音高实体
        ArrayList<PianoModule> currentSlotPitchArray1 = new ArrayList<>();
        // 左手
        ArrayList<PianoModule> currentSlotPitchArray2 = new ArrayList<>();
        // 右手
        ArrayList<PianoModule> nextCurrentSlotPitchArray1 = new ArrayList<>();
        // 左手
        ArrayList<PianoModule> nextCurrentSlotPitchArray2 = new ArrayList<>();

        ArrayList<Integer> currentSlotPitch1 = new ArrayList<>();
        ArrayList<Integer> nextCurrentSlotPitch1 = new ArrayList<>();
        for (int valueNumI = 0; valueNumI < szScore.slot.get(slotBlockNo).note.size(); valueNumI++) {
            int tempoPitch = szScore.slot.get(slotBlockNo).note.get(valueNumI).pitch;
            int tick = szScore.slot.get(slotBlockNo).tick;
            if (tempoPitch > 0 && tempoPitch <= 127) {
                //当前音符的数组
                if (scoreClockController.getStatusModule().getType()== Constant.LEFT_HAND) {

                    if (szScore.slot.get(slotBlockNo).note.get(valueNumI).hand == 0) {
                        PianoModule pianoModule = new PianoModule();
                        pianoModule.setTick(tick);
                        pianoModule.setNumber(tempoPitch);
                        if (currentSlotPitch1.size() == 0) {
                            currentSlotPitch1.add(tempoPitch);
                            currentSlotPitchArray2.add(pianoModule);
                        } else {
                            if (!currentSlotPitch1.contains(tempoPitch)) {
                                currentSlotPitch1.add(tempoPitch);
                                currentSlotPitchArray2.add(pianoModule);
                            }
                        }
/* Log.i(TAG,"tempo_pitch1="+tempo_pitch); */
                    }
                }else if (scoreClockController.getStatusModule().getType()== Constant.RIGHT_HAND) {
                    if (szScore.slot.get(slotBlockNo).note.get(valueNumI).hand == 1) {
                        PianoModule pianoModule = new PianoModule();
                        pianoModule.setTick(tick);
                        pianoModule.setNumber(tempoPitch);

                        if (currentSlotPitch1.size() == 0) {
                            currentSlotPitch1.add(tempoPitch);
                            currentSlotPitchArray1.add(pianoModule);
                        } else {
                            if (!currentSlotPitch1.contains(tempoPitch)) {
                                currentSlotPitch1.add(tempoPitch);
                                currentSlotPitchArray1.add(pianoModule);
                            }
                        }
/* Log.i(TAG,"tempo_pitch2="+tempo_pitch); */
                    }
                }else {
                    if (szScore.slot.get(slotBlockNo).note.get(valueNumI).hand == 0) {
                        PianoModule pianoModule = new PianoModule();
                        pianoModule.setTick(tick);
                        pianoModule.setNumber(tempoPitch);
                        if (currentSlotPitch1.size() == 0) {
                            currentSlotPitch1.add(tempoPitch);
                            currentSlotPitchArray2.add(pianoModule);
                        } else {
                            if (!currentSlotPitch1.contains(tempoPitch)) {
                                currentSlotPitch1.add(tempoPitch);
                                currentSlotPitchArray2.add(pianoModule);
                            }
                        }
/* Log.i(TAG,"tempo_pitch1="+tempo_pitch); */
                    }

                    if (szScore.slot.get(slotBlockNo).note.get(valueNumI).hand == 1) {
                        PianoModule pianoModule = new PianoModule();
                        pianoModule.setTick(tick);
                        pianoModule.setNumber(tempoPitch);

                        if (currentSlotPitch1.size() == 0) {
                            currentSlotPitch1.add(tempoPitch);
                            currentSlotPitchArray1.add(pianoModule);
                        } else {
                            if (!currentSlotPitch1.contains(tempoPitch)) {
                                currentSlotPitch1.add(tempoPitch);
                                currentSlotPitchArray1.add(pianoModule);
                            }
                        }
/* Log.i(TAG,"tempo_pitch2="+tempo_pitch); */
                    }
                }
            }
        }
        if (slotBlockNo < szScore.slot.size() - 1) {
            int nextSlotBlockNo = slotBlockNo + 1;
            for (int valueNumI = 0; valueNumI < szScore.slot.get(nextSlotBlockNo).note.size(); valueNumI++) {
                int tempoPitch = szScore.slot.get(nextSlotBlockNo).note.get(valueNumI).pitch;
                int tick = szScore.slot.get(nextSlotBlockNo).tick;
                if (tempoPitch > 0 && tempoPitch <= 127) {
                    /* 当前音符的数组 */
                    if (szScore.slot.get(nextSlotBlockNo).note.get(valueNumI).hand == 1) {
                        PianoModule pianoModule = new PianoModule();
                        pianoModule.setTick(tick);
                        pianoModule.setNumber(tempoPitch);
/* Log.i(TAG,"tempo_pitch1="+tempo_pitch); */
                        if (nextCurrentSlotPitch1.size() == 0) {
                            nextCurrentSlotPitch1.add(tempoPitch);
                            nextCurrentSlotPitchArray1.add(pianoModule);
                        } else {
                            if (!nextCurrentSlotPitch1.contains(tempoPitch)) {
                                nextCurrentSlotPitch1.add(tempoPitch);
                                nextCurrentSlotPitchArray1.add(pianoModule);
                            }
                        }
                    } else if (szScore.slot.get(nextSlotBlockNo).note.get(valueNumI).hand == 0) {
                        PianoModule pianoModule = new PianoModule();
                        pianoModule.setTick(tick);
                        pianoModule.setNumber(tempoPitch);

                        if (nextCurrentSlotPitch1.size() == 0) {
                            nextCurrentSlotPitch1.add(tempoPitch);
                            nextCurrentSlotPitchArray2.add(pianoModule);
                        } else {
                            if (!nextCurrentSlotPitch1.contains(tempoPitch)) {
                                nextCurrentSlotPitch1.add(tempoPitch);
                                nextCurrentSlotPitchArray2.add(pianoModule);
                            }
                        }

                    }
                }
            }
        }
        String[] result = new String[2];
        boolean right = false;
        boolean left = false;
        if (currentSlotPitchArray1.size() == 0) {
            left = true;
        }
        if (currentSlotPitchArray2.size() == 0) {
            right = true;
        }

        if (currentSlotPitchArray1.size() != 0 || currentSlotPitchArray2.size() != 0) {
            if (bounceForward) {
                result = arrayJude(currentSlotPitchArray1, currentSlotPitchArray2,
                        scoreClockController.currentPitchArrayFromKeyboard);
            } else {
                result = arrayJuge1(currentSlotPitchArray1, currentSlotPitchArray2,
                        nextCurrentSlotPitchArray1, nextCurrentSlotPitchArray2,
                        scoreClockController.currentPitchArrayFromKeyboard);
            }
        } else {
            result[0] = "correct";
        }
        if (!bounceForward) {

            ImageView result_pic1 = new ImageView(scoreClockController.mContext);
            ImageView result_pic2 = new ImageView(scoreClockController.mContext);

            if ("correct".equalsIgnoreCase(result[0])) {
                result_pic1.setBackgroundResource(R.drawable.correct);
            }

            if ("fault".equalsIgnoreCase(result[0])) {
                result_pic1.setBackgroundResource(R.drawable.fault);
            }

            if ("leak".equalsIgnoreCase(result[0])) {
                //没弹的图片
                result_pic1.setBackgroundResource(R.drawable.leak);
            }

            if ("overflow".equalsIgnoreCase(result[0])) {
                //多弹的图片
                result_pic1.setBackgroundResource(R.drawable.overflow);
            }


            if ("correct".equalsIgnoreCase(result[1])) {
                result_pic2.setBackgroundResource(R.drawable.correct);
            }


            if ("fault".equalsIgnoreCase(result[1])) {
                result_pic2.setBackgroundResource(R.drawable.fault);
            }

            if ("leak".equalsIgnoreCase(result[1])) {
                //没弹的图片
                result_pic2.setBackgroundResource(R.drawable.leak);
            }
            if ("overflow".equalsIgnoreCase(result[1])) {
                //多弹的图片
                result_pic2.setBackgroundResource(R.drawable.overflow);
            }
//        Log.i("scoreplayerview","slotBlock_no="+slotBlock_no);
            int indexNumber = szScore.slot.get(slotBlockNo).note.get(0).measure;

            int tempo_pichtX1 = szScore.slot.get(slotBlockNo).note.get(0).x;
//        Log.i("scoreplayerview","indexNumber="+indexNumber);
            if (indexNumber == 0) {
                indexNumber = 1;
            }
//        Log.i("scoreplayerview","indexNumber="+indexNumber);
            double tempo_pichtY1;
            double tempo_pichtY2;
            if (szScore.hasZeroMeasure) {
                tempo_pichtY1 = szScore.measure.get(indexNumber).staffTopDistance.get(0);
                tempo_pichtY2 = szScore.measure.get(indexNumber).staffTopDistance.get(1);
            } else {
                tempo_pichtY1 = szScore.measure.get(indexNumber - 1).staffTopDistance.get(0);
                tempo_pichtY2 = szScore.measure.get(indexNumber - 1).staffTopDistance.get(1);
            }

//        Log.i("scoreplayerview","tempo_pichtY1="+tempo_pichtY1);

//        int height =(int)(szScore.score_size/4/scoreClockController.pdfView.getPdfWidth()*1920);
            int height = (int) (szScore.scoreSize / 2 / scoreClockController.getPdfView().getPdfWidth() * scoreClockController.self.getWidth());
            RelativeLayout.LayoutParams layoutParams_pic = new RelativeLayout.LayoutParams(height, height);
            result_pic1.setLayoutParams(layoutParams_pic);

            result_pic1.setX((int) ((tempo_pichtX1 - 5) * scoreClockController.scoreMarkViewRatioWidth));
            result_pic1.setY((int) ((tempo_pichtY1 - height * 2) * scoreClockController.scoreMarkViewRatioWidth));

            RelativeLayout.LayoutParams layoutParams_pic1 = new RelativeLayout.LayoutParams(height, height);
            result_pic2.setLayoutParams(layoutParams_pic1);

            result_pic2.setX((int) ((tempo_pichtX1 - 5) * scoreClockController.scoreMarkViewRatioWidth));
            result_pic2.setY((int) ((tempo_pichtY2 - height * 2) * scoreClockController.scoreMarkViewRatioWidth));



            if (!left) {
                scoreClockController.getScoreMarkView().addView(result_pic1);
                scoreClockController.tempoCorrectAndFaultViewArrayInPage.add(result_pic1);
            }
            if (!right) {
                scoreClockController.getScoreMarkView().addView(result_pic2);
                scoreClockController.tempoCorrectAndFaultViewArrayInPage.add(result_pic2);
            }
            scoreClockController.tempoCorrectAndFaultStringArrayInPage.add(result);
        }
        return result;
    }

    /**
     * @param staff1Array  右手键
     * @param staff2Array  左手键
     * @param keyBardArray 钢琴弹奏的键
     * @return 返回对错结果 （correct为对 fault为错 leak为漏 overflow为多弹）
     */
    private String[] arrayJude(ArrayList<PianoModule> staff1Array, ArrayList<PianoModule> staff2Array,
                               ArrayList<PianoModule> keyBardArray) {
        String result = "leak";

        StringBuilder play = new StringBuilder();
        String play2 = "";
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < staff1Array.size(); i++) {
            play.append(" ").append(staff1Array.get(i).getNumber());
            for (int j = 0; j < staff2Array.size(); j++) {
                if (staff1Array.get(i).getNumber() == staff2Array.get(j).getNumber()) {
                    staff1Array.remove(i);
                    i--;
                    break;
                }
            }
        }
        if (keyBardArray.size() != staff1Array.size() + staff2Array.size()) {
            result = "fault";
        }

        for (int j = 0; j < staff2Array.size(); j++) {
            play.append(" ").append(staff2Array.get(j).getNumber());
        }
        for (int i = 0; i < keyBardArray.size(); i++) {
            key.append(" ").append(keyBardArray.get(i).getNumber());
            PianoModule keyBardPianoModule = keyBardArray.get(i);
            boolean updateLeft = false;
            boolean updateRight = false;
            for (PianoModule p : staff1Array) {
                if (p.getNumber() == keyBardPianoModule.getNumber()) {
                    updateLeft = true;
                }
            }
            for (PianoModule p : staff2Array) {
                if (p.getNumber() == keyBardPianoModule.getNumber()) {
                    updateRight = true;
                }
            }
            if (!updateRight && !updateLeft) {
                keyBardArray.remove(keyBardPianoModule);
                i--;
            }
        }
        if (keyBardArray.size() == staff1Array.size() + staff2Array.size() && !"fault".equals(result)) {
            result = "correct";
        }
        LogUtil.i(TAG, "staff1.size=" + staff1Array.size() + " staff2.size=" + staff2Array.size() + " keysize=" + keyBardArray.size() + " tan1=" + play.toString() + " tan2=" + play2 + " key=" + key);
        return new String[]{
                result, ""
        };
    }

    /**
     * @param staff1Array     右手键
     * @param staff2Array     左手键
     * @param nextStaff1Array 下一个小节的右手
     * @param nextStaff2Array 下一个小节的左手
     * @param keyBardArray    钢琴弹奏的键
     * @return 返回对错结果 （correct为对 fault为错 leak为漏 overflow为多弹）
     */
    public String[] arrayJuge1(ArrayList<PianoModule> staff1Array, ArrayList<PianoModule> staff2Array,
                               ArrayList<PianoModule> nextStaff1Array, ArrayList<PianoModule> nextStaff2Array,
                               ArrayList<PianoModule> keyBardArray) {
        String result = "leak";
        String result1 = "leak";
        // 记录左手键分值
        double jiezou1 = 0;
        // 记录右手键分值
        double jiezou2 = 0;
        double jiezou = 0;
        double lidu = 0;
        double lidu1 = 0;
        double lidu2 = 0;

        String tanzou = "";
        String tanzou2 = "";
        String key = "";

        // 左手键值和右手键值相同的元素从右手键值集合中移除
        for (int i = 0; i < staff1Array.size(); i++) {
            // 遍历左手键
            for (int j = 0; j < staff2Array.size(); j++) {
                if (staff1Array.get(i).getNumber() == staff2Array.get(j).getNumber()) {
                    staff1Array.remove(i);
                    i--;
                    break;
                }
            }
        }

        // 将键盘数组拼接成字符串
        for (int i = 0; i < keyBardArray.size(); i++) {
            key = key + " " + (keyBardArray.get(i).getNumber());
        }
        //右手键，将所有右手键 弹奏出来的分值累加到 jiezou1 变量中
        for (int i = 0; i < staff1Array.size(); i++) {
            PianoModule pianoModule = staff1Array.get(i);

            tanzou = tanzou + " " + pianoModule.getNumber();
            for (int j = 0; j < keyBardArray.size(); j++) {
                PianoModule pianoModule1 = keyBardArray.get(j);
                float abs = (Math.abs(pianoModule.getTick() - pianoModule1.getTick()));
                if (abs >= CORRECT_DURATION_IN_TICK * 2) {
                    abs = CORRECT_DURATION_IN_TICK * 2;
                }
                if (abs < 40) {
                    abs = 40;
                }
                abs = (-1 / 3600) * (abs - 40) * (abs - 40) + 1;
                // 将
                jiezou1 = jiezou1 + abs;
            }
        }

        for (int i = 0; i < staff2Array.size(); i++) {
            // 左手键，将所有左手键 弹奏出来的分值累加到 jiezou2 变量中
            PianoModule pianoModule = staff2Array.get(i);
            tanzou2 = tanzou2 + " " + pianoModule.getNumber();
            for (int j = 0; j < keyBardArray.size(); j++) {
                // 弹奏的键值，tick和力度
                PianoModule pianoModule1 = keyBardArray.get(j);
                float abs = (Math.abs(pianoModule.getTick() - pianoModule1.getTick()));
                if (abs >= (CORRECT_DURATION_IN_TICK * 2)) {
                    abs = (CORRECT_DURATION_IN_TICK * 2);
                }
                if (abs < 40) {
                    abs = 40;
                }
                abs = (-1 / 3600) * (abs - 40) * (abs - 40) + 1;
                jiezou2 = jiezou2 + abs;
            }
        }

        if (staff1Array.size() == 0) {
            jiezou1 = keyBardArray.size();
        }

        if (staff2Array.size() == 0) {
            jiezou2 = keyBardArray.size();
        }

        if (keyBardArray.size() != 0) {
            jiezou = (jiezou1 + jiezou2) / (keyBardArray.size() * 2);
            scoreClockController.scoreModule.setRhythm(scoreClockController.scoreModule.getRhythm() + jiezou);
        }

        LogUtil.i(TAG, "staff1.size=" + staff1Array.size() + " staff2.size=" + staff2Array.size() + " keysize=" + keyBardArray.size() + " tan1=" + tanzou + " tan2=" + tanzou2 + " key=" + key);
        //漏弹===============================================
        // 右手键盘+左手键盘 数量> 键盘总数量
        if (staff1Array.size() + staff2Array.size() > keyBardArray.size()) {
            int correctIndex = 0;
            int correctIndex1 = 0;
            boolean staff_1_contains = false;
            boolean staff_2_contains = false;
            ArrayList<PianoModule> pianoModuleArrayList = new ArrayList<>();
            //正确和错误============================================
            for (int i = 0; i < keyBardArray.size(); i++) {
                PianoModule pianoModule = keyBardArray.get(i);
                // 循环右手键
                for (int j = 0; j < staff1Array.size(); j++) {
                    PianoModule pianoModule1 = staff1Array.get(j);
                    if (pianoModule1.getNumber() == pianoModule.getNumber()) {
                        staff_1_contains = true;

                        if (pianoModule.getIntensity() > 20 && pianoModule.getIntensity() < 127) {
                            lidu1 = 1;
                        }
                        pianoModuleArrayList.add(pianoModule);
                        correctIndex++;
                        break;
                    }
                }

                // 循环左手键
                for (int j = 0; j < staff2Array.size(); j++) {
                    PianoModule pianoModule1 = staff2Array.get(j);
                    if (pianoModule1.getNumber() == pianoModule.getNumber()) {
                        staff_2_contains = true;

                        if (pianoModule.getIntensity() > 20 && pianoModule.getIntensity() < 127) {
                            lidu2 = 1;
                        }
                        pianoModuleArrayList.add(pianoModule);
                        correctIndex1++;
                        break;
                    }
                }
            }
            for (int i = 0; i < pianoModuleArrayList.size(); i++) {
                for (int j = 0; j < keyBardArray.size(); j++) {
                    if (pianoModuleArrayList.get(i).getNumber() == keyBardArray.get(j).getNumber()) {
                        keyBardArray.remove(j);
                        j--;
                    }
                }
            }
            if (staff1Array.size() == 0) {
                staff_1_contains = true;
                lidu1 = 1;
            }
            if (staff2Array.size() == 0) {
                staff_2_contains = true;
                lidu2 = 1;
            }

            //正确  两个都谈对
            if (staff_1_contains && correctIndex == staff1Array.size()) {
                result = "correct";
            } else {
                result = "leak";
            }
            if (staff_2_contains && correctIndex1 == staff2Array.size()) {
                result1 = "correct";
            } else {
                result1 = "leak";
            }
            keyBardArray.clear();
        } else {
            int correctIndex = 0;
            int correctIndex1 = 0;
            boolean staff_1_contains = false;
            boolean staff_2_contains = false;
            ArrayList<PianoModule> pianoModuleArrayList = new ArrayList<>();
            //正确和错误============================================
            for (int i = 0; i < keyBardArray.size(); i++) {
                PianoModule pianoModule = keyBardArray.get(i);
                for (int j = 0; j < staff1Array.size(); j++) {
                    PianoModule pianoModule1 = staff1Array.get(j);
                    if (pianoModule1.getNumber() == pianoModule.getNumber()) {
                        staff_1_contains = true;

                        if (pianoModule.getIntensity() > 20 && pianoModule.getIntensity() < 127) {
                            lidu1 = 1;
                        }
                        pianoModuleArrayList.add(pianoModule);
                        correctIndex++;
                        break;
                    }
                }
                for (int j = 0; j < staff2Array.size(); j++) {
                    PianoModule pianoModule1 = staff2Array.get(j);
                    if (pianoModule1.getNumber() == pianoModule.getNumber()) {
                        staff_2_contains = true;

                        if (pianoModule.getIntensity() > 20 && pianoModule.getIntensity() < 127) {
                            lidu2 = 1;
                        }
                        pianoModuleArrayList.add(pianoModule);
                        correctIndex1++;
                        break;
                    }
                }
            }
            for (int i = 0; i < pianoModuleArrayList.size(); i++) {
                for (int j = 0; j < keyBardArray.size(); j++) {
                    if (pianoModuleArrayList.get(i).getNumber() == keyBardArray.get(j).getNumber()) {
                        keyBardArray.remove(j);
                        j--;
                    }
                }
            }
            if (staff1Array.size() == 0) {
                staff_1_contains = true;
                lidu1 = 1;
            }
            if (staff2Array.size() == 0) {
                staff_2_contains = true;
                lidu2 = 1;
            }

            //正确  两个都谈对
            if (staff_1_contains) {
                result = "correct";
            }
            if (staff_2_contains) {
                result1 = "correct";
            }

            if (keyBardArray.size() > 0) {
                int nextTick;
                if (slotIdx + 1 < szScore.slot.size()) {
                    nextTick = szScore.slot.get(slotIdx + 1).tick - CORRECT_DURATION_IN_TICK;
                } else {
                    nextTick = szScore.slot.get(slotIdx).tick + CORRECT_DURATION_IN_TICK;
                }
                int faultIndex = 0;
                int faultIndex1 = 0;
                for (int i = 0; i < keyBardArray.size(); i++) {
                    PianoModule pianoModule = keyBardArray.get(i);
                    if (pianoModule.getTick() < nextTick) {
                        boolean rome = true;
                        boolean rome1 = true;
                        for (int j = 0; j < nextStaff1Array.size(); j++) {
                            PianoModule pianoModuleNext1 = nextStaff1Array.get(j);
                            if (pianoModule.getNumber() == pianoModuleNext1.getNumber()) {
                                if (pianoModule.getTick() > pianoModuleNext1.getTick() - CORRECT_DURATION_IN_TICK) {
                                    rome = false;
                                }
                            }
                        }
                        for (int j = 0; j < nextStaff2Array.size(); j++) {
                            PianoModule pianoModuleNext2 = nextStaff2Array.get(j);
                            if (pianoModule.getNumber() == pianoModuleNext2.getNumber()) {
                                if (pianoModule.getTick() > pianoModuleNext2.getTick() - CORRECT_DURATION_IN_TICK) {
                                    rome1 = false;
                                }
                            }
                        }
                        if (rome && !rome1) {
                            //错
                            faultIndex++;
                            if (result.equals("correct")) {
                                result1 = "fault";
                            } else {
                                result = "fault";
                            }
                            keyBardArray.remove(i);
                            i--;
                        }
                        if (!rome && rome1) {
                            //错
                            faultIndex1++;
                            if (result1.equals("correct")) {
                                result = "fault";
                            } else {
                                result1 = "fault";
                            }
                            keyBardArray.remove(i);
                            i--;
                        }
                        if (rome && rome1) {
                            if (staff1Array.size() == 0) {
                                if (correctIndex1 != 0) {
                                    faultIndex1++;
                                }
                                result1 = "fault";
                            } else {
                                if (correctIndex != 0) {
                                    faultIndex++;
                                }
                                result = "fault";
                            }
                            keyBardArray.remove(i);
                            i--;
                        }
                    } else {
                        keyBardArray.remove(i);
                        i--;
                        if (correctIndex != 0) {
                            result = "overflow";
                            scoreClockController.scoreModule.setIntonation(scoreClockController.scoreModule.getIntonation() + 0.5);
                            scoreClockController.scoreModule.setIntensity(scoreClockController.scoreModule.getIntensity() + 0.5);
                        } else {
                            result1 = "overflow";
                            if (correctIndex1 != 0) {
                                scoreClockController.scoreModule.setIntonation(scoreClockController.scoreModule.getIntonation() + 0.5);
                                scoreClockController.scoreModule.setIntensity(scoreClockController.scoreModule.getIntensity() + 0.5);
                            }
                        }
                    }
                }
                if (faultIndex + correctIndex > staff1Array.size()) {
                    result = "overflow";
                    if (correctIndex != 0) {
                        scoreClockController.scoreModule.setIntonation(scoreClockController.scoreModule.getIntonation() + 0.5);
                        scoreClockController.scoreModule.setIntensity(scoreClockController.scoreModule.getIntensity() + 0.5);
                    }
                }
                if (faultIndex1 + correctIndex1 > staff2Array.size()) {
                    result1 = "overflow";
                    if (correctIndex1 != 0) {
                        scoreClockController.scoreModule.setIntonation(scoreClockController.scoreModule.getIntonation() + 0.5);
                        scoreClockController.scoreModule.setIntensity(scoreClockController.scoreModule.getIntensity() + 0.5);
                    }
                }
            }
        }
        if (result.equals("correct") && result1.equals("correct")) {
            lidu = (lidu1 + lidu2) / 2;

            scoreClockController.scoreModule.setIntonation(scoreClockController.scoreModule.getIntonation() + 1);
            scoreClockController.scoreModule.setIntensity(scoreClockController.scoreModule.getIntensity() + lidu);
        }
//        Log.i(TAG,"连弹判断力度="+scoreClockController.scoreMoudle.getLiDu());
//        Log.i(TAG,"连弹判断节奏="+scoreClockController.scoreMoudle.getRhythm());
//        Log.i(TAG,"连弹判断音准="+scoreClockController.scoreMoudle.getIntonation());
        String[] strings = {result, result1};
        return strings;
    }

}
