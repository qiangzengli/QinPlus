package com.weiyin.qinplus.ui.tv.bwstaff;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Region;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.activity.ScoreActivity;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.bluetooth.BlueToothControl;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.entity.ScoreEntity;
import com.weiyin.qinplus.listener.InterfaceBlueData;
import com.weiyin.qinplus.ui.tv.bwstaff.DB.MidiJni;
import com.weiyin.qinplus.ui.tv.bwstaff.DB.TagEvent;
import com.weiyin.qinplus.ui.tv.bwstaff.DB.TagWaterFall;
import com.weiyin.qinplus.ui.tv.waterfall.KeyBoard;
import com.weiyin.qinplus.ui.tv.waterfall.WaterFallSurfaceView;
import com.weiyin.qinplus.usb.UsbController;
import com.weiyin.qinplus.usb.listener.UsbDataListener;

import java.util.ArrayList;
import java.util.Timer;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 曲谱总的控制
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class ScoreController implements OnDrawListener, OnPageChangeListener, InterfaceBlueData, UsbDataListener {
    public final String TAG = "ScoreController";

    private ScorePlayerView scorePlayerView;

    private String pdfName;

    ScoreJudge scoreJudge;
    MidiPlay midiPlay;

    Context mContext;

    private PDFView pdfView;
    private ImageView imgMeasure;
    private ImageView imgSlot;

    private WaterFallSurfaceView waterFallSurfaceView;
    PeleView peleView;

    KeyBoard keyboard;

    BlueToothControl blueToothcontrol;
    private UsbController usbControl;
    private StatusModule statusModule;

    /**
     * 各个声部小节阴影的数组
     */
    ArrayList<ImageView> measureShadowArray;
    /**
     * 各个声部slot阴影的数组
     */
    ArrayList<ImageView> slotShadowArray;

    /**
     * 标记所在的view
     */
    RelativeLayout scoreMarkView;
    /**
     * 所有正误符号的view数组(二维，第一层为页面数）
     */
    ArrayList<ArrayList<ImageView>> correctAndFaultViewArray;
    /**
     * 每页上临时存的正误符号数组
     */
    ArrayList<ImageView> tempoCorrectAndFaultViewArrayInPage;

    ArrayList<ArrayList<String[]>> correctAndFaultStringArray;

    volatile ArrayList<KeyBoardModule> drawKeyBoardList = new ArrayList<>();

    /**
     * 成绩
     */
    ScoreModule scoreModule;

    ArrayList<String[]> tempoCorrectAndFaultStringArrayInPage;
    private ArrayList<TagEvent> tagEvents;
    int startY = 0;
    /**
     * 整个页面的缩放比例
     */
    double scoreMarkViewRatioWidth;

    /**
     * 当前处于的页面
     */
    int currentPageNo;
    /**
     * 当前播放处于的row
     */
    int currentRowNo;


    /**
     * 当前播放处于的小节
     */
    private int currentMeasureNo;

    /**
     * 当前小节的ticks总量
     */
    int currentMeasureTickAmount;

    /**
     * 当前的节拍
     */
    int currentBeats;
    int currentBeatType;

    private String recodePath;

    private long startData;
    long pauseData;
    long playData;
    /**
     * 当前的用于音高判断的数组
     */
    ArrayList<PianoModule> currentPitchArrayFromKeyboard;
    boolean reduction = false;
    /**
     * 播放时的id_num
     */
    private int idNumForNoteOn;
    int idNumForNoteOff;
    int tempoPlayTickNoteOn;
    int tempoPlayTickNoteOff;
    int tempoPlayTickWindowOpen;

    RelativeLayout self;
    RelativeLayout activityOpernRl, opernOntouchRl;
    private MidiJni midiJni;
    /**
     * 曲谱id
     */
    private String musicId;

    int tickNumber;
    private float midiSpeed = 1;
    private float speed;

    boolean startScoreActivity = false;
    private boolean isFirst = true;
    boolean isCleanWater = false;
    private boolean isDraw = false;
    private ScoreDataCoordinator scoreDataCoordinator;
    ScoreDataCoordinator.Score szScore;
    ScoreWaterfall scoreWaterfall;
    int date = 0;
    int tuck;

    public void setDraw(boolean draw) {
        this.isDraw = draw;
    }

    public boolean isDraw() {
        return isDraw;
    }

    public UsbController getUsbControl() {
        return usbControl;
    }

    public PDFView getPdfView() {
        return pdfView;
    }

    public int getIdNumForNoteOn() {
        return idNumForNoteOn;
    }

    public void setIdNumForNoteOn(int idNumForNoteOn) {
        this.idNumForNoteOn = idNumForNoteOn;
    }

    public StatusModule getStatusModule() {
        return statusModule;
    }

    public void setStatusModule(StatusModule statusModule) {
        this.statusModule = statusModule;
    }

    public ArrayList<KeyBoardModule> getDrawKeyBoardList() {
        return drawKeyBoardList;
    }

    public void setDrawKeyBoardList(ArrayList<KeyBoardModule> drawKeyBoardList) {
        this.drawKeyBoardList = drawKeyBoardList;
    }

    public boolean isReduction() {
        return reduction;
    }

    public void setReduction(boolean reduction) {
        this.reduction = reduction;
    }

    public float getSpeed() {
        return speed;
    }

    public MidiPlay getMidiPlay() {
        return midiPlay;
    }

    public void setMidiPlay(MidiPlay midiPlay) {
        this.midiPlay = midiPlay;
    }

    public int getCurrentMeasureNo() {
        return currentMeasureNo;
    }

    public void setCurrentMeasureNo(int currentMeasureNo) {
        this.currentMeasureNo = currentMeasureNo;
    }

    public boolean isCleanWater() {
        return isCleanWater;
    }

    public void setStartScoreActivity(boolean startScoreActivity) {
        this.startScoreActivity = startScoreActivity;
    }

    public RelativeLayout getScoreMarkView() {
        return scoreMarkView;
    }

    public ScoreWaterfall getScoreWaterfall() {
        return scoreWaterfall;
    }

    public void setFirst(boolean isfrist) {
        isFirst = isfrist;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public ScorePlayerView getScorePlayerView() {
        return scorePlayerView;
    }

    public void setScorePlayerView(ScorePlayerView scorePlayerView) {
        this.scorePlayerView = scorePlayerView;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public int getTickNumber() {
        return tickNumber;
    }

    public void setTickNumber(int tickNumber) {
        this.tickNumber = tickNumber;
    }

    public float getMidiSpeed() {
        return midiSpeed;
    }

    public void setMidiSpeed(float midiSpeed) {
        this.midiSpeed = midiSpeed;
    }

    public MidiJni getMidiJni() {
        return midiJni;
    }

    public void setMidiJni(MidiJni midiJni) {
        this.midiJni = midiJni;
    }

    public String getRecodePath() {
        return recodePath;
    }

    public void setRecodePath(String recodePath) {
        this.recodePath = recodePath;
    }

    public void clean() {
        if (scorePlayerView != null) {
            scorePlayerView.clean();
            scorePlayerView = null;
        }
        if (peleView != null) {
            peleView = null;
        }
        if (szScore != null) {
            szScore = null;
        }
        if (self != null) {
            self.removeAllViews();
            self = null;
        }
        if (keyboard != null) {
            keyboard.clean();
            keyboard.surfaceDestroyed(keyboard.getHolder());
            keyboard = null;
        }
        if (blueToothcontrol != null) {
            blueToothcontrol.removeDataCallback(this);
            blueToothcontrol = null;
        }
        if (scoreDataCoordinator != null) {
            scoreDataCoordinator.clean();
            scoreDataCoordinator = null;
        }
        if (scoreWaterfall != null) {
            scoreWaterfall.clean();
            scoreWaterfall = null;
        }
        if (pdfView != null) {
            pdfView.recycle();
            pdfView = null;
        }
        if (midiPlay != null) {
            midiPlay.clean();
            midiPlay = null;
        }
        if (midiJni != null) {
            midiJni = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (statusModule != null) {
            statusModule = null;
        }
        if (scoreModule != null) {
            scoreModule = null;
        }
        if (tagEvents != null) {
            tagEvents = null;
        }
        if (currentPitchArrayFromKeyboard != null) {
            currentPitchArrayFromKeyboard = null;
        }
        if (usbControl != null) {
            usbControl.removeUsbDataListener();
        }
    }

    /**
     * 初始化函数==============================================
     */
    public ScoreController(Context context, String dbPath, String pdfName, RelativeLayout self1, KeyBoard keyBoard, final WaterFallSurfaceView waterFallSurfaceView, RelativeLayout activityOpernRl, RelativeLayout opernOntouchRl) {
        this.activityOpernRl = activityOpernRl;
        this.opernOntouchRl = opernOntouchRl;
        this.mContext = context;
        this.pdfName = pdfName;
        this.self = self1;
        this.keyboard = keyBoard;
        this.waterFallSurfaceView = waterFallSurfaceView;
        Log.i(TAG, "初始化BWStaff");
        usbControl = UsbController.getUsbController();
        usbControl.setUsbDataListener(this);
        pdfView = new PDFView(context.getApplicationContext());
        ViewGroup.LayoutParams pdfViewLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pdfView.setLayoutParams(pdfViewLayoutParams);
        self.addView(pdfView);
        //首次执行导入.db文件
        scoreDataCoordinator = ScoreDataCoordinator.sharedCoordinator();
        szScore = scoreDataCoordinator.loadFile(context.getApplicationContext(), dbPath);


        scorePlayerView = new ScorePlayerView(szScore, this);
        //连接蓝牙
        checkBlueData();
        scoreJudge = new ScoreJudge(this, szScore);
        scoreModule = new ScoreModule();
        statusModule = StatusModule.getStatusModule();
        correctAndFaultViewArray = new ArrayList<>();
        tempoCorrectAndFaultViewArrayInPage = new ArrayList<>();
        correctAndFaultStringArray = new ArrayList<>();
        tempoCorrectAndFaultStringArrayInPage = new ArrayList<>();
        currentPitchArrayFromKeyboard = new ArrayList<>();
        if (waterFallSurfaceView != null) {
            openWaterfall(waterFallSurfaceView);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                openPDF(0);
                /* 播放器初始化 */
                midiPlay = new MidiPlay(handler, ScoreController.this);
                /* 更新页面大小 */
                double pageWidth = szScore.pageWidth;
                self.measure(0, 0);
                /* PDF实际宽度除以XML中的宽度 */
                scoreMarkViewRatioWidth = self.getWidth() / pageWidth;
                setRegion(scoreMarkViewRatioWidth);
                Log.i(TAG, "width=" + self.getWidth() + " height=" + self.getHeight());
/*取出起始点位置 play_begin_left_x = szScore.measure.get(0).left;*/
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (szScore.pageWidth * scoreMarkViewRatioWidth),
                        (int) (szScore.pageHeight * scoreMarkViewRatioWidth));
                scoreMarkView = new RelativeLayout(mContext.getApplicationContext());
                scoreMarkView.setLayoutParams(layoutParams);
                self.addView(scoreMarkView);
                scoreMarkView.setVisibility(View.GONE);
                /* 建立每个声部的小节阴影 */
                measureShadowArray = new ArrayList<>();
                slotShadowArray = new ArrayList<>();
                int insI, staffI;
                //初始化measure-view
                imgMeasure = new ImageView(mContext);
                imgMeasure.setBackgroundResource(R.color.main_background_blue);
                imgMeasure.setAlpha(0.15f);
                imgSlot = new ImageView(mContext);
                slotShadowArray.add(imgSlot);
                scoreMarkView.addView(imgSlot);
                scoreMarkView.addView(imgMeasure);
                measureShadowArray.add(imgMeasure);
                if (waterFallSurfaceView != null) {
                    if (WinYinPianoApplication.isWaterfall) {
                        statusModule.setWaterfall(true);
                        scoreMarkView.setVisibility(View.GONE);
                        waterFallSurfaceView.setVisibility(View.VISIBLE);
                    } else {
                        statusModule.setWaterfall(false);
                        waterFallSurfaceView.setVisibility(View.GONE);
                    }

                }
                //整体清零
                scorePlayerView.toBeginAndReset();
            }
        }, 1000);

    }

    /***
     * pdf文件=========================================================
     *
     * */
    @SuppressLint("ClickableViewAccessibility")
    private void openPDF(int thisPageNo) {
        pdfView.fromAsset(pdfName).defaultPage(thisPageNo).onPageChange(this).onDraw(this).swipeVertical(false).load();
        pdfView.setEnabled(true);
        opernOntouchRl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                LogUtil.i(TAG, "motionEvent=" + motionEvent.getAction());
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = (int) motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
//                        pdfView.moveTo(0, -motionEvent.getY());

//                        movePdf(motionEvent, false);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isFirst()) {
                            if (motionEvent.getY() - startY > 100) {
                                seekToPreviousRow();
                            } else if(motionEvent.getY()-startY<-100){
                                seekToNextRow();
                            }
                            movePdf(motionEvent, true);
                        }
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void movePdf(MotionEvent motionEvent, boolean isRow) {
        int y = (int) pdfView.getCurrentYOffset();
        for (ScoreDataCoordinator.Measure measure : szScore.measure) {
//            LogUtil.i(TAG, (int) (measure.left * scoreMarkViewRatioWidth) + "==" +
//                    (int) (measure.staffTopDistance.get(0).intValue() * scoreMarkViewRatioWidth) + "==" +
//                    (int) ((measure.left + measure.width) * scoreMarkViewRatioWidth) + "==" +
//                    (int) (measure.staffTopDistance.get(1).intValue() * scoreMarkViewRatioWidth));
//            LogUtil.i(TAG, motionEvent.getX() + "===========" + motionEvent.getY() + "     " + pdfView.getCurrentYOffset());
            int x;
            if (isRow) {
                x = (int) motionEvent.getX();
            } else {
                x = (int) (measure.left * scoreMarkViewRatioWidth);
            }
            if (measure.region.contains(x, (int) ((int) (motionEvent.getY()) - y))) {
                currentMeasureNo = measure.idx;
                if (szScore.hasZeroMeasure) {
                    currentMeasureNo++;
                }
                LogUtil.i(TAG, "currentMeasureNo=" + currentMeasureNo);
                reduction = true;
                if (statusModule.isWaterfall()) {
                    scoreMarkView.setVisibility(View.GONE);
                } else {
                    scoreMarkView.setVisibility(View.VISIBLE);
                }
                scorePlayerView.seekToMeasure(currentMeasureNo - 1);
            }
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        currentPageNo = page;
    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

    }

    private void setRegion(double thizScoreMarkViewRatioWidth) {
        for (ScoreDataCoordinator.Measure measure : szScore.measure) {
            measure.region = new Region((int) (measure.left * thizScoreMarkViewRatioWidth),
                    (int) (measure.staffTopDistance.get(0).intValue() * thizScoreMarkViewRatioWidth),
                    (int) ((measure.left + measure.width) * thizScoreMarkViewRatioWidth),
                    (int) (measure.staffTopDistance.get(1).intValue() * thizScoreMarkViewRatioWidth));
        }
    }

    /**
     * page控制=========================================================
     * 上翻页
     */
    public void pageUp() {
        currentPageNo--;
        pageUpAndDown(currentPageNo);
    }

    /**
     * 下翻页
     */
    public void pageDown() {
        currentPageNo++;
        pageUpAndDown(currentPageNo);
    }

    /**
     * 指到指定的页面
     *
     * @param pageNo 页面数
     */
    public void pageUpAndDown(int pageNo) {
        //先去除所有正误符号
        for (int i = 0; i < correctAndFaultViewArray.size(); i++) {
            for (int j = 0; j < correctAndFaultViewArray.get(i).size(); j++) {
                scoreMarkView.removeView(correctAndFaultViewArray.get(i).get(j));
            }
        }
        //再把当前页的正误符号添加回来
        if (pageNo > 0 && pageNo <= correctAndFaultViewArray.size()) {
            for (int j = 0; j < correctAndFaultViewArray.get(pageNo - 1).size(); j++) {
                scoreMarkView.addView(correctAndFaultViewArray.get(pageNo - 1).get(j));
            }
        }
        currentPageNo = pageNo;
        openPDF(currentPageNo);
    }

    /**
     * 行控制=========================================================
     */
    public void seekToPreviousRow() {
        reduction = true;
        if (currentRowNo > 1) {
            --currentRowNo;
            seekToRow(currentRowNo);
        } else {
            ToastUtil.showTextToast(mContext, mContext.getResources().getString(R.string.start_toprow));
        }
    }

    public void seekToNextRow() {
        reduction = true;
        if (currentRowNo < szScore.beginMeasureNoPerRowArray.size()) {
            ++currentRowNo;
            seekToRow(currentRowNo);
        } else {
            ToastUtil.showTextToast(mContext, mContext.getResources().getString(R.string.start_bottomrow));
        }
    }

    public void seekToRow(int rowNo) {
        if (statusModule.isWaterfall()) {
            scoreMarkView.setVisibility(View.GONE);
        } else {
            scoreMarkView.setVisibility(View.VISIBLE);
        }
        if (rowNo > 0) {
            --rowNo;
        }
        currentMeasureNo = szScore.beginMeasureNoPerRowArray.get(rowNo);
        if (szScore.hasZeroMeasure) {
            currentMeasureNo++;
        }
        scorePlayerView.seekToMeasure(currentMeasureNo - 1);
    }


    /**
     * 更改同步时间轴=========================================================
     *
     * @param thisMeaNo 小节数
     */
    public void synToTickTimeLine(int thisMeaNo) {
        if (statusModule.isWaterfall()) {
            scoreMarkView.setVisibility(View.GONE);
        } else {
            scoreMarkView.setVisibility(View.VISIBLE);
        }
        LogUtil.i(TAG, thisMeaNo + " " + szScore.slot.size());
        if (thisMeaNo < szScore.slot.size()) {
            int thisMeaTick;
            if (thisMeaNo < szScore.slot.size() - 2) {
                thisMeaTick = szScore.measure.get(thisMeaNo + 2).tick;
            } else {
                thisMeaTick = szScore.measure.get(thisMeaNo + 1).tick + 1024 / currentBeatType * currentBeats;
            }
            int thisTick = szScore.measure.get(thisMeaNo + 1).tick;
            for (int i = 0; i < szScore.slot.size(); i++) {
                if (thisTick == szScore.slot.get(i).tick) {
                    idNumForNoteOn = i;
                }
            }
            thisMeaTick = thisTick - (thisMeaTick - thisTick);
            tickNumber = thisMeaTick;
            scorePlayerView.updateMeasure(thisTick, true);
            midiPlay.setCurrentTick(thisMeaTick);
        }
    }


    /**
     * 小节控制=========================================================
     */
    public void seekToPreviousMeasure() {
        reduction = true;
        if (currentMeasureNo > 1) {
            currentMeasureNo--;
            updateRowNo();
            scorePlayerView.seekToMeasure(currentMeasureNo - 1);
/* synToTickTimeLine(currentMeasureNo); */
        } else {
            ToastUtil.showTextToast(mContext, mContext.getResources().getString(R.string.start_topmeasure));
        }
    }

    public void seekToNextMeasure() {
        reduction = true;
        if (currentMeasureNo < szScore.measure.size()) {
            currentMeasureNo++;
            updateRowNo();
            scorePlayerView.seekToMeasure(currentMeasureNo - 1);
        } else {
            ToastUtil.showTextToast(mContext, mContext.getResources().getString(R.string.start_bottommeasure));
        }
    }


    private void updateRowNo() {
        if (statusModule.isWaterfall()) {
            scoreMarkView.setVisibility(View.GONE);
        } else {
            scoreMarkView.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < szScore.beginMeasureNoPerRowArray.size() - 1; i++) {
            if (szScore.beginMeasureNoPerRowArray.get(szScore.beginMeasureNoPerRowArray.size() - 1) <= currentMeasureNo) {
                currentRowNo = szScore.beginMeasureNoPerRowArray.size();
                break;
            }
            if (szScore.beginMeasureNoPerRowArray.get(i) <= currentMeasureNo
                    && currentMeasureNo < szScore.beginMeasureNoPerRowArray.get(i + 1)) {
                currentRowNo = i + 1;
                break;
            }
        }
    }


    /**
     * 用返回的tick追踪位置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
     * 首先接收计时器回来的值
     */
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    date = (int) msg.obj;
/* Log.i(TAG,"date线程="+date); */
                    tuck = date / 16;
/* Log.i(TAG,"data"+date); */
                    if (date == ((tickNumber))) {
                        peleView = new PeleView(mContext.getApplicationContext());
                        peleView.addView(currentBeats);
                        activityOpernRl.addView(peleView);
                        peleView.setVisibility(View.VISIBLE);
                    }
                    if (scorePlayerView != null) {
                        scorePlayerView.backCurrentTick(date, peleView);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 按下键盘
     */
    public void pressKeyboard(ArrayList<KeyBoardModule> keyBoardModuleArrayList, int staff) {
        keyboard.poina(keyBoardModuleArrayList, staff);
    }

    /**
     * 设置速度
     *
     * @param speed  速度
     * @param midiId midiID
     * @param mp3Id  MP3 ID
     */
    public void speed(float speed, String midiId, String mp3Id) {
        this.speed = speed;
        midiSpeed = midiSpeed * speed;
        if (midiPlay != null) {
            midiPlay.setCurrentTempo(midiSpeed, midiId, mp3Id);
        }
    }


    /**
     * 播放控制
     */


    public void play() {
        if (isFirst) {
            keyboard.setIsKeyNote(!statusModule.isPractice());
        }
        if (statusModule.isPractice()) {
            if (isFirst) {
                tagEvents = new ArrayList<>();
                startData = System.currentTimeMillis();
            } else {
                playData = System.currentTimeMillis() + playData;
            }
        }
        if (reduction) {
            if (currentMeasureNo > 1) {
                synToTickTimeLine(currentMeasureNo - 2);
            }
            reduction = false;
        }
        if (midiPlay != null) {
            midiPlay.play();
            scoreJudge.setSlotIdx(idNumForNoteOn);
            isCleanWater = false;
        }
    }

    /**
     * 录音调用jni处代码
     */
    private Runnable thread = new Runnable() {
        @Override
        public void run() {
            skip();
        }
    };

    private void skip() {
        double intonation = scoreModule.getIntonation();
        double intensity = scoreModule.getIntensity();
        double rhythm = scoreModule.getRhythm();
        Intent intent = new Intent();
        ScoreEntity scoreEntity = new ScoreEntity();
        int slotBlockAmountNoteOn = szScore.slotSize;
        if (intonation != 0) {
            intonation = Math.abs(intonation / slotBlockAmountNoteOn * 100);
        }
        if (intensity != 0) {
            intensity = Math.abs(intensity / slotBlockAmountNoteOn * 100);
        }
        if (rhythm != 0) {
            rhythm = Math.abs(rhythm / slotBlockAmountNoteOn * 100);
        }
        Log.i(TAG, "力度=" + intensity);
        Log.i(TAG, "音准=" + intonation);
        Log.i(TAG, "节奏=" + rhythm);
        if (intensity > 100) {
            intensity = 100;
        }
        if (intonation > 100) {
            intonation = 100;
        }
        if (rhythm > 100) {
            rhythm = 100;
        }

        if (!statusModule.isVideo()) {
            intent.putExtra(Constant.STATE, Constant.HAVE_RECORDING);
            intent.putExtra(Constant.PATH, recodePath);
        } else {
            intent.putExtra(Constant.STATE, Constant.VIDEO);
        }
        scoreEntity.musicid = musicId;
        scoreEntity.musictime = System.currentTimeMillis();
        scoreEntity.startTime = startData;
        scoreEntity.dynamics = intensity;
        scoreEntity.rhythm = rhythm;
        scoreEntity.intonation = intonation;
        scoreEntity.musicname = "";
        intent.putExtra(Constant.SCORE_ENTITY, scoreEntity);
        intent.setClass(mContext, ScoreActivity.class);
        mContext.startActivity(intent);
    }

    public void stop() {
        if (!statusModule.isOut()) {
            if (statusModule != null) {
                if (!statusModule.isRestart()) {
                    if (tempoCorrectAndFaultStringArrayInPage.size() > 0) {
                        if (tagEvents.size() > 0 && !statusModule.isVideo()) {
                            handler.postDelayed(thread, 0);
                        } else {
                            skip();
                        }
                    }
                }
            }
        }
        if (statusModule != null) {
            if (!statusModule.isOut()) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (scorePlayerView != null) {
                            scorePlayerView.toBeginAndReset();
                        }
                        if (midiPlay != null) {
                            midiPlay.stop();
                        }
                    }
                }, 200);
            }
        }
    }

    public void pause() {
        pauseData = System.currentTimeMillis() + pauseData;
        if (midiPlay != null) {
            midiPlay.pause();
        }
    }

    public void addDataCallBack() {
        if (blueToothcontrol != null) {
            blueToothcontrol.addDataCallback(this);
        }
    }

    public void removeDataCallBack() {
        if (blueToothcontrol != null) {
            blueToothcontrol.removeDataCallback(this);
        }
    }

    public void initWaterFallSurfaceViewData(ArrayList<TagWaterFall> tagWaterFalls) {
        if (szScore != null && scoreWaterfall != null) {
            /* /////////////////////////gai */
            LogUtil.i(TAG, "szScore.measure.get(0).tick=" + szScore.measure.get(0).tick + " szScore.beats=" + szScore.measure.get(0).beats + " szScore.beatType=" + szScore.measure.get(0).beatType);
            scoreWaterfall.initData(tagWaterFalls, szScore.hasZeroMeasure, (int) ((szScore.measure.get(0).tick - (-szScore.measure.get(0).beats * 256 * 4 / szScore.measure.get(0).beatType - 64)) / 16 * ((60.0 / midiSpeed / 256.0 * 16) * 1000)));
        } else {
            Log.i(TAG, "szScore==null||scoreWaterfall==null");
        }
    }


    /**
     * 得到蓝牙实例化对象
     */
    private void checkBlueData() {
        blueToothcontrol = BlueToothControl.getBlueToothInstance();
        blueToothcontrol.addDataCallback(this);
    }

    private void openWaterfall(WaterFallSurfaceView waterFallSurfaceView) {
        scoreWaterfall = new ScoreWaterfall(this, keyboard, mContext, waterFallSurfaceView);
    }

    @Override
    public void onDataReceive(String value) {
        long data = System.currentTimeMillis();
        value = value.replace(" ", "");
        Log.i(TAG, value);
        if (value.length() > 2) {
            if (!value.substring(value.length() - 2).equals("F7")) {
                if (value.length() > 5) {
                    String numberStr = value.substring(2, 4);
                    if (statusModule.isPractice()) {
                        //录音的数据     startData  是点击开始录音时保存下来的系统时间 然后时间需要微秒
                        TagEvent tagEvent = new TagEvent();
                        if (playData != 0) {
                            tagEvent.setTimeStamp((int) ((data - startData - (playData - pauseData))) * 1000);
                        } else {
                            tagEvent.setTimeStamp((int) ((data - startData - pauseData)) * 1000);
                        }
                        int[] tagEventData = new int[3];
                        tagEventData[0] = Integer.parseInt(value.substring(0, 2), 16);
                        tagEventData[1] = Integer.parseInt(value.substring(2, 4), 16);
                        tagEventData[2] = Integer.parseInt(value.substring(4, 6), 16);

                        tagEvent.setLen(tagEventData.length);
                        tagEvent.setData(tagEventData);
                        tagEvents.add(tagEvent);
                    }
                    int note = Integer.parseInt(numberStr, 16);
                    int j = Integer.parseInt(value.substring(4, 6), 16);
                    String str = value.substring(0, 2);
                    if (!isDraw) {
                        isDraw = true;
                        handler.postDelayed(drawKey, 30);
                    }
                    Log.i(TAG, "音节=" + note + "      value=" + value + "  j=" + j + " str= " + str);
                    int status = Integer.parseInt(value.substring(0, 2), 16);
                    if (status >= 128 && status < 144) {
                        KeyBoardModule keyBoardModule = new KeyBoardModule(note, 80, j, 0);
                        drawKeyBoardList.add(keyBoardModule);
                    } else if (status >= 144 && Integer.valueOf(str) < 160) {
                        if (j == 0) {
                            KeyBoardModule keyBoardModule = new KeyBoardModule(note, 80, j, 0);
                            drawKeyBoardList.add(keyBoardModule);
                        } else {
                            KeyBoardModule keyBoardModule = new KeyBoardModule(note, 90, j, 0);
                            drawKeyBoardList.add(keyBoardModule);
                            if (statusModule.isPractice() || statusModule.isBounceForward()) {
                                boolean contain = false;
                                for (int i = 0; i < currentPitchArrayFromKeyboard.size(); i++) {
                                    if (currentPitchArrayFromKeyboard.get(i).getNumber() == note) {
                                        contain = true;
                                    }
                                }
                                if (!contain) {
                                    PianoModule pianoModule = new PianoModule();
                                    pianoModule.setNumber(note);
                                    pianoModule.setTick(date);
                                    pianoModule.setIntensity(Integer.parseInt(value.substring(2, 4), 16));
                                    currentPitchArrayFromKeyboard.add(pianoModule);
                                }
                            }
//                    }
                        }
                    }
                }
            }
        }
    }

    Runnable drawKey = new Runnable() {
        @Override
        public void run() {
            isDraw = false;
            LogUtil.i(TAG, "drawKey drawKeyBoardList.size=" + drawKeyBoardList.size());
//                pressKeyboard(drawKeyBoardList, 2);
            keyboard.poina(drawKeyBoardList, 2);
        }
    };

    @Override
    public void usbData(int status, int channel, int note, int velocity) {
        LogUtil.i(TAG, " size=" + drawKeyBoardList.size());
        LogUtil.i(TAG, "音节=" + note + " velocity" + velocity + " status=" + status);
        if (!isDraw) {
            isDraw = true;
            handler.postDelayed(drawKey, 30);
        }
        if (status == 80) {
            KeyBoardModule keyBoardModule = new KeyBoardModule(note, 80, velocity, channel);
            drawKeyBoardList.add(keyBoardModule);
        } else if (status == 90) {
            if (velocity == 0) {
                KeyBoardModule keyBoardModule = new KeyBoardModule(note, 80, velocity, channel);
                drawKeyBoardList.add(keyBoardModule);
            } else {
                KeyBoardModule keyBoardModule = new KeyBoardModule(note, 90, velocity, channel);
                drawKeyBoardList.add(keyBoardModule);
                if (statusModule != null) {
                    if (statusModule.isPractice() || statusModule.isBounceForward()) {
                        boolean contain = false;
                        for (int i = 0; i < currentPitchArrayFromKeyboard.size(); i++) {
                            if (currentPitchArrayFromKeyboard.get(i).getNumber() == note) {
                                contain = true;
                            }
                        }
                        if (!contain) {
                            PianoModule pianoModule = new PianoModule();
                            pianoModule.setNumber(note);
                            pianoModule.setTick(date);
                            pianoModule.setIntensity(velocity);
                            currentPitchArrayFromKeyboard.add(pianoModule);
                        }
                    }
                }
            }
        }
    }
}