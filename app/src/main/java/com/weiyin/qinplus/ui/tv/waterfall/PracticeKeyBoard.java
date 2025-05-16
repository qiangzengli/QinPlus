package com.weiyin.qinplus.ui.tv.waterfall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.ui.tv.bwstaff.KeyBoardModule;
import com.weiyin.qinplus.ui.tv.bwstaff.ScoreController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/7/19.
 */

public class PracticeKeyBoard extends SurfaceView implements SurfaceHolder.Callback {

    private String TAG = "KeyBoard";

    public int iShadeLeft = 2;
    public int iShadeRight = 1;
    public int iShadeNone = 0;

    private int KeysPerOctave = 7;          //
    private int MaxOctave = 7;              //
    private float[] blackKeyOffsets;          // 记录黑键的偏移位置
    private float fWhiteKeyWidth;             // 白键宽度
    private float fWhiteKeyHeight;            // 白键高度
    private float fBlackKeyWidth;             // 黑键宽度
    private float fBlackKeyHeight;            // 黑键高度
    private float fSpaceAbove;                // 键盘上方空的高度

    //    private Bitmap bmpBackGround;                   //  键盘背景
    private Bitmap bmpWhite;                        //  白键图片
    private Bitmap bmpBlack;                        //  黑键图片
    private Bitmap bmpWhiteClickLeftBlackLeft;      //  左手点击白键 左边有一个黑键
    //    private Bitmap bmpWhiteClickLeftBlackRight;     //  左手点击白键 右边有一个黑键
//    private Bitmap bmpWhiteClickRihgtBlackLeft;     //  右手点击白键 左边有一个黑键
//    private Bitmap bmpWhiteClickRihgtBlackRight;    //  右手几点白键 右边有一个黑键
//    private Bitmap bmpBlackClickLeft;               //  左手点击黑键
    private Bitmap bmpBlackClickRihgt;              //  右手点击黑键

    boolean isvideo;

    public boolean iskeyNote() {
        return iskeyNote;
    }

    public void setIskeyNote(boolean iskeyNote) {
        this.iskeyNote = iskeyNote;
    }

    private boolean iskeyNote = true;
    /**
     * The paint options for drawing
     */
    private Paint paint;
    /**
     * True if we can draw on the surface
     */
    private boolean surfaceReady;
    /**
     * The bitmap for double-buffering
     */
    private Bitmap bufferBitmap;
    /**
     * The canvas for double-buffering
     */
    private Canvas bufferCanvas;

    public ArrayList<Integer> timeArrayList = new ArrayList<Integer>();
    public ArrayList<MeasureNote> measureNotes = new ArrayList<MeasureNote>();

    public ArrayList<MeasureNote> measureNotesBlackClick = new ArrayList<MeasureNote>();

    boolean useTwoColors = true;

    boolean bFlagLoop = true;

    int maxDuration = 1920;

    private int multiple = 1;
    private int keyWidthMeasureSpec;
    private int keyHeightMeasureSpec;

    public boolean isStarting() {
        return starting;
    }

    public void setStarting(boolean starting) {
        this.starting = starting;
    }

    private boolean starting = false;

    private String[] textData = {"A2", "B2", "C1", "D1", "E1", "F1", "G1", "A1", "B1",
            "C", "D", "E", "F", "G", "A", "B",
            "c", "d", "e", "f", "g", "a", "b",
            "c1", "d1", "e1", "f1", "g1", "a1", "b1",
            "c2", "d2", "e2", "f2", "g2", "a2", "b2",
            "c3", "d3", "e3", "f3", "g3", "a3", "b3",
            "c4", "d4", "e4", "f4", "g4", "a4", "b4", "c5"};

    public float getfWhiteKeyWidth() {
        return fWhiteKeyWidth;
    }

    public void setfWhiteKeyWidth(float fWhiteKeyWidth) {
        this.fWhiteKeyWidth = fWhiteKeyWidth;
    }

    public float getfWhiteKeyHeight() {
        return fWhiteKeyHeight;
    }

    public void setfWhiteKeyHeight(float fWhiteKeyHeight) {
        this.fWhiteKeyHeight = fWhiteKeyHeight;
    }

    public float getfBlackKeyWidth() {
        return fBlackKeyWidth;
    }

    public void setfBlackKeyWidth(float fBlackKeyWidth) {
        this.fBlackKeyWidth = fBlackKeyWidth;
    }

    public float getfBlackKeyHeight() {
        return fBlackKeyHeight;
    }

    public void setfBlackKeyHeight(float fBlackKeyHeight) {
        this.fBlackKeyHeight = fBlackKeyHeight;
    }

    public int getMultiple() {
        return multiple;
    }

    public void setMultiple(int multiple) {
        this.multiple = multiple;

        init();
    }


    public PracticeKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);

        fWhiteKeyWidth = 0;
        fSpaceAbove = 0;
        paint = new Paint();

        GetBitmap();


        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

    }

    public void clean() {
//        bmpBackGround.recycle();
        bmpWhite.recycle();
        bmpBlack.recycle();
        bmpWhiteClickLeftBlackLeft.recycle();
//        bmpWhiteClickLeftBlackRight.recycle();
//        bmpWhiteClickRihgtBlackLeft.recycle();
//        bmpWhiteClickRihgtBlackRight.recycle();
//        bmpBlackClickLeft.recycle();
        bmpBlackClickRihgt.recycle();
        bufferBitmap.recycle();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(200);
            } catch (Exception e) {

            }
            draw();
        }
    };

    ScoreController scoreController;

    public void setMeasureNotes(ArrayList<Integer> timeArrayList, ArrayList<MeasureNote> notes, boolean isvideo, ScoreController scoreController) {
        this.timeArrayList = timeArrayList;
        this.measureNotes = notes;
        this.isvideo = isvideo;
        this.scoreController = scoreController;
        if (notes != null) {
            for (MeasureNote note : notes) {
                if (note.duration > maxDuration)
                    maxDuration = note.duration;
            }
        }
        Log.i("keyboradmaxduration", String.valueOf(maxDuration));
    }

    public void ShadeNotes(int currentPulseTime, int prevPulseTime, int startPulseTime) {
        if (starting) {
            return;
        }
        if (measureNotes == null || measureNotes.size() == 0 || !surfaceReady
                || bufferBitmap == null) {
            return;
        }
        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
            return;
        }
        bufferCanvas.translate(0, fSpaceAbove - 5);

        int lastShadedIndex = FindClosestStartTime(prevPulseTime
                - maxDuration);
        for (int i = lastShadedIndex; i < measureNotes.size(); i++) {
            int start = measureNotes.get(i).time;
            int end = measureNotes.get(i).time + measureNotes.get(i).duration;
            int notenumber = measureNotes.get(i).notenumber;
            int nextStart = NextStartTime(i);
            int nextStartTrack = NextStartTimeSameTrack(i);
            int ifinger = measureNotes.get(i).iFinger;
            end -= 30;
            if (end <= start)
                end += 30;
//			end = Math.max(end, nextStartTrack);
//			end = Math.min(end, start + maxShadeDuration - 1);

			/* If we've past the previous and current times, we're done. */
            if ((start > prevPulseTime) && (start > currentPulseTime)) {
                break;
            }

			/* If shaded notes are the same, we're done */
//			if ((start <= currentPulseTime) && (currentPulseTime < nextStart)
//					&& (currentPulseTime < end) && (start <= prevPulseTime)
//					&& (prevPulseTime < nextStart) && (prevPulseTime < end)) {
//				break;
//			}

			/* If the note is in the current time, shade it */
            if ((start <= currentPulseTime) && (currentPulseTime < end) && (start >= startPulseTime - 64)) {
                int iblackstatus = getLeftBlackBlackStatus(notenumber, currentPulseTime);
                if (useTwoColors) {
                    if (measureNotes.get(i).staff == 1) {
                        if (iskeyNote) {
                            shadeOneNote(bufferCanvas, notenumber, iShadeRight, ifinger, measureNotes.get(i).staff, iblackstatus);
                        }
                    } else {
                        if (iskeyNote) {
                            shadeOneNote(bufferCanvas, notenumber, iShadeLeft, ifinger, measureNotes.get(i).staff, iblackstatus);
                        }
                    }
                } else {
                    if (iskeyNote) {
                        shadeOneNote(bufferCanvas, notenumber, iShadeLeft, ifinger, measureNotes.get(i).staff, iblackstatus);
                    }
                }
            }

			/* If the note is in the previous time, un-shade it, draw it white. */
            else if ((start <= prevPulseTime) && (prevPulseTime < end)) {
                int num = notenumber % 12;
                int iblackstatus = getLeftBlackBlackStatus(notenumber, currentPulseTime);
                if (num == 1 || num == 3 || num == 6 || num == 8 || num == 10) {
                    if (iskeyNote) {
                        shadeOneNote(bufferCanvas, notenumber, iShadeNone, 0, 0, iblackstatus);
                    }
                } else {
                    if (iskeyNote) {
                        shadeOneNote(bufferCanvas, notenumber, iShadeNone, 0, 0, iblackstatus);
                    }
                }
            }
        }
        bufferCanvas.translate(0, -(fSpaceAbove - 5));
        canvas.drawBitmap(bufferBitmap, 0, 0, paint);
        holder.unlockCanvasAndPost(canvas);
    }

    // 如果当前是白健，需要判断黑键的状态
    // 返回值为黑键的状态 0 代表没有按下， 1,代表左边黑键 : 11右手弹奏  12左手弹奏，2代表右边黑键，3,代表两边黑键都按下
    int getLeftBlackBlackStatus(int notenumber, int currenttime) {
        int itype = 0;
        int notescale = notenumber % 12;
        int bflagleft = 0;
        int bflagright = 0;
        switch (notescale) {
            case 1:
            case 3:
            case 6:
            case 8:
            case 10:
                itype = 0;
                break;
            case 0:
                bflagright = judgeBlackStatus(notenumber + 1, currenttime);
                break;
            case 2:
                bflagleft = judgeBlackStatus(notenumber - 1, currenttime);
                bflagright = judgeBlackStatus(notenumber + 1, currenttime);
                break;
            case 4:
                bflagleft = judgeBlackStatus(notenumber - 1, currenttime);
                break;
            case 5:
                bflagright = judgeBlackStatus(notenumber + 1, currenttime);
                break;
            case 7:
                bflagleft = judgeBlackStatus(notenumber - 1, currenttime);
                bflagright = judgeBlackStatus(notenumber + 1, currenttime);
                break;
            case 9:
                bflagleft = judgeBlackStatus(notenumber - 1, currenttime);
                bflagright = judgeBlackStatus(notenumber + 1, currenttime);
                break;
            case 11:
                bflagleft = judgeBlackStatus(notenumber - 1, currenttime);
                break;

        }
        if (bflagleft == 1 && bflagright == 1) // 两边按下 右手
            return 311;
        else if (bflagleft == 1 && bflagright == 2)
            return 312;
        else if (bflagleft == 2 && bflagright == 1)
            return 321;
        else if (bflagleft == 2 && bflagright == 2)
            return 322;
        else if (bflagleft == 1 && bflagright == 0)//左边按下
            return 11;
        else if (bflagleft == 2 && bflagright == 0)
            return 12;
        else if (bflagleft == 0 && bflagright == 1)// 右边按下
            return 21;
        else if (bflagleft == 0 && bflagright == 2)
            return 22;
        else//都没按下
            return itype;
    }

    int judgeBlackStatus(int notenumber, int currenttime) {
        int lastShadedIndex = FindClosestStartTime(currenttime
                - 1920 - 480);
        for (int i = lastShadedIndex; i < measureNotes.size(); i++) {
            int notenumertemp = measureNotes.get(i).notenumber;
            int start = measureNotes.get(i).time;
            int end = measureNotes.get(i).time + measureNotes.get(i).duration;
            int staff = measureNotes.get(i).staff;
            end -= 30;
            if (end <= start)
                end += 30;

            if (start > currenttime)
                break;

            if (notenumertemp == notenumber) {
                if (start <= currenttime && end > currenttime) {
                    return staff;
                }
            }
        }
        return 0;
    }


    /**
     * Find the MidiNote with the startTime closest to the given time. Return
     * the index of the note. Use a binary search method.
     */
    private int FindClosestStartTime(int pulseTime) {
        int left = 0;
        int right = measureNotes.size() - 1;

        while (right - left > 1) {
            int i = (right + left) / 2;
            if (measureNotes.get(left).time == pulseTime)
                break;
            else if (measureNotes.get(i).time <= pulseTime)
                left = i;
            else
                right = i;
        }
        while (left >= 1
                && (measureNotes.get(left - 1).time == measureNotes.get(left).time)) {
            left--;
        }
        return left;
    }


    /**
     * Return the next StartTime that occurs after the MidiNote at offset i,
     * that is also in the same track/channel.
     */
    private int NextStartTimeSameTrack(int i) {
        int start = measureNotes.get(i).time;
        int end = measureNotes.get(i).time + measureNotes.get(i).duration;
        int track = measureNotes.get(i).staff;

        while (i < measureNotes.size()) {
            if (measureNotes.get(i).staff != track) {
                i++;
                continue;
            }
            if (measureNotes.get(i).time > start) {
                return measureNotes.get(i).time;
            }
            end = Math.max(end, measureNotes.get(i).time + measureNotes.get(i).duration);
            i++;
        }
        return end;
    }

    /**
     * Return the next StartTime that occurs after the MidiNote at offset i. If
     * all the subsequent notes have the same StartTime, then return the largest
     * EndTime.
     */
    private int NextStartTime(int i) {
        int start = measureNotes.get(i).time;
        int end = measureNotes.get(i).time + measureNotes.get(i).duration;

        while (i < measureNotes.size()) {
            if (measureNotes.get(i).time > start) {
                return measureNotes.get(i).time;
            }
            end = Math.max(end, measureNotes.get(i).time + measureNotes.get(i).duration);
            i++;
        }
        return end;
    }

    private void GetBitmap() {
        Log.i(TAG, "GetBitmap");
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 4;    //这个的值压缩的倍数（2的整数倍），数值越小，压缩率越小，图片越清晰

//        bmpBackGround = BitmapFactory.decodeResource(getResources(), R.drawable.keyboardbackgroud,opts);
        bmpWhite = BitmapFactory.decodeResource(getResources(), R.drawable.practicekeyboardwhite, opts);
        bmpBlack = BitmapFactory.decodeResource(getResources(), R.drawable.practicekeyboardblack, opts);
        bmpWhiteClickLeftBlackLeft = BitmapFactory.decodeResource(getResources(), R.drawable.practicewhiteclickleftblackleft, opts);
//        bmpWhiteClickLeftBlackRight = BitmapFactory.decodeResource(getResources(), R.drawable.practicewhiteclickleftblackleft,opts);
//        bmpWhiteClickRihgtBlackLeft = BitmapFactory.decodeResource(getResources(), R.drawable.practicewhiteclickleftblackleft,opts);
//        bmpWhiteClickRihgtBlackRight = BitmapFactory.decodeResource(getResources(), R.drawable.practicewhiteclickleftblackleft,opts);
//        bmpBlackClickLeft = BitmapFactory.decodeResource(getResources(), R.drawable.practiceblackclickleft,opts);
        bmpBlackClickRihgt = BitmapFactory.decodeResource(getResources(), R.drawable.practiceblackclickleft, opts);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.keyWidthMeasureSpec = widthMeasureSpec;
        this.keyHeightMeasureSpec = heightMeasureSpec;

        super.onMeasure(keyWidthMeasureSpec, keyHeightMeasureSpec);
        if (widthMeasureSpec < 0 || heightMeasureSpec < 0)
            return;

        init();
    }

    private void init() {
        int screenwidth = MeasureSpec.getSize(keyWidthMeasureSpec);
        int screenheight = MeasureSpec.getSize(keyHeightMeasureSpec);

        fWhiteKeyWidth = (float) (screenwidth / (3.0 + KeysPerOctave * MaxOctave));
        fWhiteKeyHeight = screenheight;
        fBlackKeyWidth = fWhiteKeyWidth * 0.7f;
        fBlackKeyHeight = fWhiteKeyHeight * 2 / 3;

        blackKeyOffsets = new float[]{fWhiteKeyWidth - fBlackKeyWidth / 2,
                fWhiteKeyWidth + fBlackKeyWidth / 2,
                2 * fWhiteKeyWidth - fBlackKeyWidth / 2,
                2 * fWhiteKeyWidth + fBlackKeyWidth / 2,
                4 * fWhiteKeyWidth - fBlackKeyWidth / 2,
                4 * fWhiteKeyWidth + fBlackKeyWidth / 2,
                5 * fWhiteKeyWidth - fBlackKeyWidth / 2,
                5 * fWhiteKeyWidth + fBlackKeyWidth / 2,
                6 * fWhiteKeyWidth - fBlackKeyWidth / 2,
                6 * fWhiteKeyWidth + fBlackKeyWidth / 2

        };
        int width = screenwidth;//(int)fWhiteKeyWidth * (KeysPerOctave * MaxOctave + 3 );
        int height = (int) (fWhiteKeyHeight + fSpaceAbove);

        if (width <= 0 || height <= 0) {
            Log.d(TAG, "width or height error!");
            return;
        }
        setMeasuredDimension(width, height);
        bufferBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bufferCanvas = new Canvas(bufferBitmap);
        if (!starting) {
            this.invalidate();
            draw();
            Log.i(TAG, "onMeasuredraw()");
        }
    }

    /**
     * Obtain the drawing canvas and call onDrawKeyBoard()
     */
    @SuppressLint("WrongCall")
    public void draw() {
        if (!surfaceReady) {
            return;
        }
        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
            return;
        }
        onDraw(canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    protected void onDraw(Canvas canvas) {
        if (!surfaceReady || bufferBitmap == null) {
            return;
        }

        if (fWhiteKeyWidth == 0f) {
            return;
        }
        paint.setAntiAlias(false);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFF9B9B9B);
        Log.i(TAG, "draw");
        // 先画背景
//        DrawBackgroud(bufferCanvas);
        // 偏移一点位置开始绘制键盘按键
        bufferCanvas.translate(0, fSpaceAbove - 5);
        // 绘制白键
        DrawWhiteKey(bufferCanvas);
        // 绘制黑键
        DrawBlackKey(bufferCanvas);

        bufferCanvas.translate(0, -(fSpaceAbove - 5));
//        DrawLamp(bufferCanvas);

        canvas.drawBitmap(bufferBitmap, 0, 0, paint);
    }
//    private void DrawLamp(Canvas canvas){
//        Rect src = getBitmapSize(bmplamp);
//        RectF rectF = new RectF();
//        if(isVideo)
//            Log.i(TAG,"调用了灯");
//        for (int i = 0;i < 88 ; i++)
//        {
//            rectF.top = 10;
//            rectF.bottom = 25;
//            rectF.left = i * 15+((i+1)*6.8f);
//            rectF.right = rectF.left +15;
//            canvas.drawBitmap(bmplamp, src, rectF, paint);
//        }
//    }

    private void DrawBackgroud(Canvas canvas) {
        RectF dst = new RectF();
        dst.left = 0;
        dst.right = fWhiteKeyWidth * (KeysPerOctave * MaxOctave + 3);
        dst.top = 0;
        dst.bottom = fWhiteKeyHeight + fSpaceAbove;
//        Rect rect = getBitmapSize(bmpBackGround);
//        canvas.drawBitmap(bmpBackGround, rect, dst, paint);
    }

    private void DrawWhiteKey(Canvas canvas) {
        Rect src = getBitmapSize(bmpWhite);
        RectF rectf = new RectF();
//
//
        for (int i = 0; i < 52; i++) {
            rectf.top = 0;
            rectf.bottom = fWhiteKeyHeight;
            rectf.left = i * fWhiteKeyWidth;
            rectf.right = rectf.left + fWhiteKeyWidth;
            canvas.drawBitmap(bmpWhite, src, rectf, paint);

//            Log.i(TAG,"width="+fWhiteKeyWidth+" height="+fBlackKeyHeight);
            paint.setTextSize(fWhiteKeyWidth / 2);
            if (i == 23) {
                canvas.drawText("中", rectf.left + fWhiteKeyWidth / 7, fWhiteKeyHeight / 8 * 2, paint);
                canvas.drawText("央", rectf.left + fWhiteKeyWidth / 7, (float) (fWhiteKeyHeight / 8 * 3.5), paint);
                canvas.drawText("C", rectf.left + fWhiteKeyWidth / 7, fWhiteKeyHeight / 8 * 5, paint);
            }
            canvas.drawText(textData[i], rectf.left + fWhiteKeyWidth / 4, fWhiteKeyHeight / 8 * 7, paint);
        }
    }

    private void DrawBlackKey(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        RectF dsttemp = new RectF();
        // 最左边有一个黑色的按钮
        float x1temp = blackKeyOffsets[0];
        dsttemp.top = 0;
        dsttemp.left = x1temp;
        dsttemp.right = x1temp + fBlackKeyWidth;
        dsttemp.bottom = fBlackKeyHeight;
        Rect srctemp = getBitmapSize(bmpBlack);
        canvas.drawBitmap(bmpBlack, srctemp, dsttemp, paint);

        // 后面的黑色按钮
        for (int octave = 0; octave < MaxOctave; octave++) {
            canvas.translate(octave * fWhiteKeyWidth * KeysPerOctave + 2
                    * fWhiteKeyWidth, 0);
            for (int i = 0; i < 10; i += 2) {
                float x1 = blackKeyOffsets[i];
                float x2 = blackKeyOffsets[i + 1];
                RectF dst = new RectF();
                dst.top = 0;
                dst.left = x1;
                dst.right = x1 + fBlackKeyWidth;
                dst.bottom = fBlackKeyHeight;
                Rect src = getBitmapSize(bmpBlack);
                canvas.drawBitmap(bmpBlack, src, dst, paint);
            }
            canvas.translate(
                    -(octave * fWhiteKeyWidth * KeysPerOctave + 2 * fWhiteKeyWidth),
                    0);
        }
        paint.setStyle(Paint.Style.STROKE);
    }

    //区分左右手的显示
    public void poina(List<KeyBoardModule> keyBoardModuleArrayList, int staff) {
        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
            return;
        }
        bufferCanvas.translate(0, fSpaceAbove - 5);
        drawKeyBoard(keyBoardModuleArrayList, staff);
        bufferCanvas.translate(0, -(fSpaceAbove - 5));
        canvas.drawBitmap(bufferBitmap, 0, 0, paint);
        holder.unlockCanvasAndPost(canvas);
    }

    public void drawKeyBoard(List<KeyBoardModule> keyBoardModuleArrayList, int staff) {
        int i = 0;
        while (keyBoardModuleArrayList.size() != 0) {
            shadeOneNot(bufferCanvas, keyBoardModuleArrayList.get(i).getNumber(), keyBoardModuleArrayList.get(i).getStatus(), staff);
            keyBoardModuleArrayList.remove(i);
//            Log.i(TAG,"remove");
        }
        if (keyBoardModuleArrayList.size() != 0) {
            Log.i(TAG, "drawKeyBoard");
            drawKeyBoard(keyBoardModuleArrayList, staff);
        }
    }


    public void poinaLamp(int notenumber, String status, int staff) {
        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
//        canvas.translate(0,  -(fSpaceAbove - 5));
        if (canvas == null) {
            return;
        }

        bufferCanvas.translate(0, fSpaceAbove - 5);
        bufferCanvas.translate(0, -(fSpaceAbove - 5));
        canvas.drawBitmap(bufferBitmap, 0, 0, paint);
        holder.unlockCanvasAndPost(canvas);
    }


    // 更新黑键按下的数组
    public void updateBlackClick(int notenumber, int status, int staff) {
        int notescale = notenumber % 12;
        if (notescale == 1 || notescale == 3 || notescale == 6 || notescale == 8 || notescale == 10) {
            if (status==(90)) {
                boolean bflag = false;
                for (int i = 0; i < measureNotesBlackClick.size(); i++) {
                    MeasureNote note = measureNotesBlackClick.get(i);
                    if (note.notenumber == notenumber) {
                        bflag = true;
                        break;
                    }
                }
                if (!bflag) {
                    MeasureNote note = new MeasureNote();
                    note.staff = staff;
                    note.notenumber = notenumber;
                    measureNotesBlackClick.add(note);
                }
            } else {
                for (int i = 0; i < measureNotesBlackClick.size(); i++) {
                    MeasureNote note = measureNotesBlackClick.get(i);
                    if (note.notenumber == notenumber) {
                        measureNotesBlackClick.remove(i);
                        break;
                    }
                }
            }
        }
    }

    // 返回值为黑键的状态 0 代表没有按下， 1,代表左边黑键 ，2代表右边黑键，3,代表两边黑键都按下
    int getClickBlackStatus(int notenumber) {
        int itype = 0;
        int notescale = notenumber % 12;
        if (notescale == 0 || notescale == 2 || notescale == 4 || notescale == 5 || notescale == 7 || notescale == 9 || notescale == 11) {
            boolean bflagleft = false;
            boolean bflagright = false;
            switch (notescale) {
                case 1:
                case 3:
                case 6:
                case 8:
                case 10:
                    itype = 0;
                    break;
                case 0:
                    bflagright = hasBlackClicked(notenumber + 1);
                    break;
                case 2:
                    bflagleft = hasBlackClicked(notenumber - 1);
                    bflagright = hasBlackClicked(notenumber + 1);
                    break;
                case 4:
                    bflagleft = hasBlackClicked(notenumber - 1);
                    break;
                case 5:
                    bflagright = hasBlackClicked(notenumber + 1);
                    break;
                case 7:
                    bflagleft = hasBlackClicked(notenumber - 1);
                    bflagright = hasBlackClicked(notenumber + 1);
                    break;
                case 9:
                    bflagleft = hasBlackClicked(notenumber - 1);
                    bflagright = hasBlackClicked(notenumber + 1);
                    break;
                case 11:
                    bflagleft = hasBlackClicked(notenumber - 1);
                    break;

            }
            if (bflagleft && bflagright) // 两边按下 右手
                return 3;
            else if (bflagleft && !bflagright)//左边按下
                return 1;
            else if (!bflagleft && bflagright)// 右边按下
                return 2;
            else//都没按下
                return itype;
        }
        return itype;
    }

    boolean hasBlackClicked(int notenumber) {
        for (int i = 0; i < measureNotesBlackClick.size(); i++) {
            MeasureNote note = measureNotesBlackClick.get(i);
            if (notenumber == note.notenumber)
                return true;
        }
        return false;
    }

    /**
     * 根据音高和接收到的指令绘制钢琴的按下和抬起
     * 21~109音值得钢琴绘制
     *
     * @param notenumber
     * @param status
     */
    public void shadeOneNot(Canvas canvas, int notenumber, int status, int staff) {
        updateBlackClick(notenumber, status, staff);
        int octave = notenumber / 12;
        int notescale = notenumber % 12;
        octave -= 2;
        if (octave < -1 || octave > MaxOctave)
            return;
        paint.setColor(0xFF9B9B9B);
        paint.setStyle(Paint.Style.FILL);
        canvas.translate(octave * fWhiteKeyWidth * KeysPerOctave + 2 * fWhiteKeyWidth, 0);
        float x1, x2, x3;

        float bottomHalfHeight = (fWhiteKeyHeight - (fBlackKeyHeight + 3));

        Bitmap bmp;
        Bitmap bmpblacktemp1 = bmpBlack;
        Bitmap bmpblacktemp2 = bmpBlack;
        Rect rect;
        Rect rectBlcak = getBitmapSize(bmpBlack);

        RectF rectF = new RectF();
        RectF rectLeft = new RectF();
        RectF rectRight = new RectF();

        int blackstatus = getClickBlackStatus(notenumber);

        switch (notescale) {
            case 0:  /*C*/

                x1 = 0;
                x2 = fWhiteKeyWidth;

                if (staff == 2 && status==(90)) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else if (staff == 1 && status==(90)) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpWhite;
                }


                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);


                if (notenumber == 108) {

                } else {
                    rectRight.top = 0;
                    rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                    rectRight.right = rectRight.left + fBlackKeyWidth;
                    rectRight.bottom = fBlackKeyHeight;
                    rectBlcak = getBitmapSize(bmpBlack);
                    if (blackstatus == 2) {
                        canvas.drawBitmap(bmpBlackClickRihgt, rectBlcak, rectRight, paint);
                    } else {

                        canvas.drawBitmap(bmpBlack, rectBlcak, rectRight, paint);
                    }
                }
                paint.setTextSize(fWhiteKeyWidth / 2);
                if (notenumber == 60) {
                    canvas.drawText("中", rectF.left + fWhiteKeyWidth / 7, fWhiteKeyHeight / 8 * 2, paint);
                    canvas.drawText("央", rectF.left + fWhiteKeyWidth / 7, (float) (fWhiteKeyHeight / 8 * 3.5), paint);
                    canvas.drawText("C", rectF.left + fWhiteKeyWidth / 7, fWhiteKeyHeight / 8 * 5, paint);
                }
                canvas.drawText(textData[octave * 7 + 2 + 0], rectF.left + fWhiteKeyWidth / 4, fWhiteKeyHeight / 8 * 7, paint);
                break;
            case 1:  /*C#*/
                x1 = fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = x1 + fBlackKeyWidth;

                if (staff == 2 && status==(90)) {
                    bmp = bmpBlackClickRihgt;
                } else if (staff == 1 && status==(90)) {
                    bmp = bmpBlackClickRihgt;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpBlack;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);
                break;
            case 2:  /*D*/
                x1 = fWhiteKeyWidth;
                x2 = fWhiteKeyWidth + fWhiteKeyWidth;

                if (staff == 2 && status==(90)) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else if (staff == 1 && status==(90)) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else {// if (status.equalsIgnoreCase("up"))
                    bmp = bmpWhite;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                if (blackstatus == 3) {
                    bmpblacktemp1 = bmpBlackClickRihgt;
                    bmpblacktemp2 = bmpBlackClickRihgt;
                } else if (blackstatus == 2) {
                    bmpblacktemp2 = bmpBlackClickRihgt;
                } else if (blackstatus == 1) {
                    bmpblacktemp1 = bmpBlackClickRihgt;
                }
                rectRight.top = 0;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlcak = getBitmapSize(bmpBlack);
                canvas.drawBitmap(bmpblacktemp2, rectBlcak, rectRight, paint);

                rectLeft.top = 0;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                canvas.drawBitmap(bmpblacktemp1, rectBlcak, rectLeft, paint);

                paint.setTextSize(fWhiteKeyWidth / 2);
                canvas.drawText(textData[octave * 7 + 2 + 1], rectF.left + fWhiteKeyWidth / 4, fWhiteKeyHeight / 8 * 7, paint);
                break;
            case 3:  /*D#*/
                x1 = fWhiteKeyWidth + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = fWhiteKeyWidth + fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth;

                if (staff == 2 && status==(90)) {
                    bmp = bmpBlackClickRihgt;
                } else if (staff == 1 && status==(90)) {
                    bmp = bmpBlackClickRihgt;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpBlack;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);
                break;
            case 4:  /*E */
                x1 = fWhiteKeyWidth * 2;
                x2 = fWhiteKeyWidth * 2 + fWhiteKeyWidth;

                if (staff == 2 &&status==(90)) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else if (staff == 1 && status==(90)) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else {// if (status.equalsIgnoreCase("up"))
                    bmp = bmpWhite;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);


                rectLeft.top = 0;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                if (blackstatus == 1) {
                    canvas.drawBitmap(bmpBlackClickRihgt, rectBlcak, rectLeft, paint);
                } else {

                    canvas.drawBitmap(bmpBlack, rectBlcak, rectLeft, paint);
                }
                paint.setTextSize(fWhiteKeyWidth / 2);
                canvas.drawText(textData[octave * 7 + 2 + 2], rectF.left + fWhiteKeyWidth / 4, fWhiteKeyHeight / 8 * 7, paint);
                break;
            case 5:  /*F*/
                x1 = fWhiteKeyWidth * 3;
                x2 = fWhiteKeyWidth * 3 + fWhiteKeyWidth;

                if (staff == 2 && status==(90)) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else if (staff == 1 && status==(90)) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else {// if (status.equalsIgnoreCase("up"))
                    bmp = bmpWhite;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                rectRight.top = 0;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlcak = getBitmapSize(bmpBlack);
                if (blackstatus == 2) {
                    canvas.drawBitmap(bmpBlackClickRihgt, rectBlcak, rectRight, paint);
                } else {
                    canvas.drawBitmap(bmpBlack, rectBlcak, rectRight, paint);
                }
                paint.setTextSize(fWhiteKeyWidth / 2);
                canvas.drawText(textData[octave * 7 + 2 + 3], rectF.left + fWhiteKeyWidth / 4, fWhiteKeyHeight / 8 * 7, paint);
                break;
            case 6:  /*F#*/
                x1 = fWhiteKeyWidth * 3 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = fWhiteKeyWidth * 3 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth;

                if (staff == 2 && status==(90)) {
                    bmp = bmpBlackClickRihgt;
                } else if (staff == 1 && status==(90)) {
                    bmp = bmpBlackClickRihgt;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpBlack;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 7:  /*G */
                x1 = fWhiteKeyWidth * 4;
                x2 = fWhiteKeyWidth * 4 + fWhiteKeyWidth;

                if (staff == 2 && status==(90)) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else if (staff == 1 &&status==(90)) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpWhite;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                if (blackstatus == 3) {
                    bmpblacktemp1 = bmpBlackClickRihgt;
                    bmpblacktemp2 = bmpBlackClickRihgt;
                } else if (blackstatus == 2) {
                    bmpblacktemp2 = bmpBlackClickRihgt;
                } else if (blackstatus == 1) {
                    bmpblacktemp1 = bmpBlackClickRihgt;
                }
                rectRight.top = 0;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlcak = getBitmapSize(bmpBlack);
                canvas.drawBitmap(bmpblacktemp2, rectBlcak, rectRight, paint);

                rectLeft.top = 0;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                canvas.drawBitmap(bmpblacktemp1, rectBlcak, rectLeft, paint);

                paint.setTextSize(fWhiteKeyWidth / 2);
                canvas.drawText(textData[octave * 7 + 2 + 4], rectF.left + fWhiteKeyWidth / 4, fWhiteKeyHeight / 8 * 7, paint);
                break;
            case 8:  /*G# */
                x1 = fWhiteKeyWidth * 4 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = fWhiteKeyWidth * 4 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth;

                if (staff == 2 && status==(90)) {
                    bmp = bmpBlackClickRihgt;
                } else if (staff == 1 && status==(90)) {
                    bmp = bmpBlackClickRihgt;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpBlack;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 9:  /*A*/
                x1 = fWhiteKeyWidth * 5;
                x2 = fWhiteKeyWidth * 5 + fWhiteKeyWidth;

                if (staff == 2 && status==(90)) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else if (staff == 1 && status==(90)) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpWhite;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                if (blackstatus == 3) {
                    bmpblacktemp1 = bmpBlackClickRihgt;
                    bmpblacktemp2 = bmpBlackClickRihgt;
                } else if (blackstatus == 2) {
                    bmpblacktemp2 = bmpBlackClickRihgt;
                } else if (blackstatus == 1) {
                    bmpblacktemp1 = bmpBlackClickRihgt;
                }

                rectRight.top = 0;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlcak = getBitmapSize(bmpBlack);
                canvas.drawBitmap(bmpblacktemp2, rectBlcak, rectRight, paint);


                if (notenumber == 21) {

                } else {
                    rectLeft.top = 0;
                    rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                    rectLeft.right = rectLeft.left + fBlackKeyWidth;
                    rectLeft.bottom = fBlackKeyHeight;
                    canvas.drawBitmap(bmpblacktemp1, rectBlcak, rectLeft, paint);
                }
                paint.setTextSize(fWhiteKeyWidth / 2);
                canvas.drawText(textData[octave * 7 + 2 + 5], rectF.left + fWhiteKeyWidth / 4, fWhiteKeyHeight / 8 * 7, paint);
                break;
            case 10:  /*A# */
                x1 = fWhiteKeyWidth * 5 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = fWhiteKeyWidth * 5 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth;

                if (staff == 2 && status==(90)) {
                    bmp = bmpBlackClickRihgt;
                } else if (staff == 1 && status==(90)) {
                    bmp = bmpBlackClickRihgt;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpBlack;
                }
                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 11:  /*B*/
                x1 = fWhiteKeyWidth * 6;
                x2 = fWhiteKeyWidth * 6 + fWhiteKeyWidth;

                if (staff == 2 && status==(90)) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else if (staff == 1 && status==(90)) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpWhite;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                rectLeft.top = 0;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                if (blackstatus == 1) {
                    canvas.drawBitmap(bmpBlackClickRihgt, rectBlcak, rectLeft, paint);
                } else {
                    canvas.drawBitmap(bmpBlack, rectBlcak, rectLeft, paint);
                }
                paint.setTextSize(fWhiteKeyWidth / 2);
                canvas.drawText(textData[octave * 7 + 2 + 6], rectF.left + fWhiteKeyWidth / 4, fWhiteKeyHeight / 8 * 7, paint);
                break;
            default:
                break;
        }
        canvas.translate(-(octave * fWhiteKeyWidth * KeysPerOctave + 2 * fWhiteKeyWidth), 0);
    }

    /*
     * Shade the given note with the given brush. We only draw notes from
	 * notenumber 24 to 96. (Middle-C is 60).
	 */
    //黑键的状态 0 代表没有按下， 1,代表左边黑键，2代表右边黑键，3,代表两边黑键都按下
    private void shadeOneNote(Canvas canvas, int notenumber, int color, int ifinger, int iChannel, int iblackstatus) {
        int blackstatus = iblackstatus;
        int octave = notenumber / 12;
        int notescale = notenumber % 12;

        octave -= 2;
        if (octave < 0 || octave >= MaxOctave)
            return;

        paint.setColor(0xFF9B9B9B);
        paint.setStyle(Paint.Style.FILL);
        canvas.translate(octave * fWhiteKeyWidth * KeysPerOctave + 2 * fWhiteKeyWidth, 0);
        float x1, x2, x3;


        Bitmap bmp;
        Rect rect;
        Rect rectBlcak = getBitmapSize(bmpBlack);

        RectF rectF = new RectF();
        RectF rectLeft = new RectF();
        RectF rectRight = new RectF();

        Bitmap bmpBlackTemp1 = bmpBlack;
        Bitmap bmpBlackTemp2 = bmpBlack;

		/* notescale goes from 0 to 11, from C to B. */
        switch (notescale) {
            case 0: /* C */
                x1 = 0;
                x2 = fWhiteKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else if (color == iShadeRight) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else {// if (color == iShadeNone)
                    bmp = bmpWhite;
                }


                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                rectRight.top = 0;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlcak = getBitmapSize(bmpBlack);

                if (notenumber == 60) {
                    paint.setTextSize(fWhiteKeyWidth / 2);
                    canvas.drawText("C", rectF.left + fWhiteKeyWidth / 3, fWhiteKeyHeight / 8 * 7, paint);
                }

                if (blackstatus == 21) // 右手
                {
                    canvas.drawBitmap(bmpBlackClickRihgt, rectBlcak, rectRight, paint);
                } else if (blackstatus == 22) // 左手
                {
                    canvas.drawBitmap(bmpBlackClickRihgt, rectBlcak, rectRight, paint);
                } else
                    canvas.drawBitmap(bmpBlack, rectBlcak, rectRight, paint);


                break;
            case 1: /* C# */
                x1 = fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = x1 + fBlackKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpBlackClickRihgt;
                } else if (color == iShadeRight) {
                    bmp = bmpBlackClickRihgt;
                } else {//if (color == iShadeNone)
                    bmp = bmpBlack;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);
                break;
            case 2: /* D */
                x1 = fWhiteKeyWidth;
                x2 = fWhiteKeyWidth + fWhiteKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else if (color == iShadeRight) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else {//if (color == iShadeNone)
                    bmp = bmpWhite;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);


                if (blackstatus == 321)// 左手 右手
                {
                    bmpBlackTemp1 = bmpBlackClickRihgt;
                    bmpBlackTemp2 = bmpBlackClickRihgt;
                } else if (blackstatus == 312) {
                    bmpBlackTemp1 = bmpBlackClickRihgt;
                    bmpBlackTemp2 = bmpBlackClickRihgt;
                } else if (blackstatus == 311) {
                    bmpBlackTemp1 = bmpBlackClickRihgt;
                    bmpBlackTemp2 = bmpBlackClickRihgt;
                } else if (blackstatus == 322) {
                    bmpBlackTemp1 = bmpBlackClickRihgt;
                    bmpBlackTemp2 = bmpBlackClickRihgt;
                }

                rectRight.top = 0;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlcak = getBitmapSize(bmpBlack);
                canvas.drawBitmap(bmpBlackTemp2, rectBlcak, rectRight, paint);

                rectLeft.top = 0;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                canvas.drawBitmap(bmpBlackTemp1, rectBlcak, rectLeft, paint);


                break;
            case 3: /* D# */
                x1 = fWhiteKeyWidth + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = fWhiteKeyWidth + fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpBlackClickRihgt;
                } else if (color == iShadeRight) {
                    bmp = bmpBlackClickRihgt;
                } else {//if (color == iShadeNone)
                    bmp = bmpBlack;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);
                break;
            case 4: /* E */
                x1 = fWhiteKeyWidth * 2;
                x2 = fWhiteKeyWidth * 2 + fWhiteKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else if (color == iShadeRight) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else {//if (color == iShadeNone)
                    bmp = bmpWhite;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);


                rectLeft.top = 0;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                if (blackstatus == 11) // 右手
                {
                    canvas.drawBitmap(bmpBlackClickRihgt, rectBlcak, rectLeft, paint);
                } else if (blackstatus == 12) {
                    canvas.drawBitmap(bmpBlackClickRihgt, rectBlcak, rectLeft, paint);
                } else {
                    canvas.drawBitmap(bmpBlack, rectBlcak, rectLeft, paint);
                }

                break;
            case 5: /* F */
                x1 = fWhiteKeyWidth * 3;
                x2 = fWhiteKeyWidth * 3 + fWhiteKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else if (color == iShadeRight) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else {// if (color == iShadeNone)
                    bmp = bmpWhite;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                rectRight.top = 0;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlcak = getBitmapSize(bmpBlack);
                if (blackstatus == 21) // 右手
                {
                    canvas.drawBitmap(bmpBlackClickRihgt, rectBlcak, rectRight, paint);
                } else if (blackstatus == 22) // 左手
                {
                    canvas.drawBitmap(bmpBlackClickRihgt, rectBlcak, rectRight, paint);
                } else {
                    canvas.drawBitmap(bmpBlack, rectBlcak, rectRight, paint);
                }

                break;
            case 6: /* F# */
                x1 = fWhiteKeyWidth * 3 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = fWhiteKeyWidth * 3 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpBlackClickRihgt;
                } else if (color == iShadeRight) {
                    bmp = bmpBlackClickRihgt;
                } else {// if (color == iShadeNone)
                    bmp = bmpBlack;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 7: /* G */
                x1 = fWhiteKeyWidth * 4;
                x2 = fWhiteKeyWidth * 4 + fWhiteKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else if (color == iShadeRight) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else {//if (color == iShadeNone)
                    bmp = bmpWhite;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                if (blackstatus == 321)// 左手 右手
                {
                    bmpBlackTemp1 = bmpBlackClickRihgt;
                    bmpBlackTemp2 = bmpBlackClickRihgt;
                } else if (blackstatus == 312) {
                    bmpBlackTemp1 = bmpBlackClickRihgt;
                    bmpBlackTemp2 = bmpBlackClickRihgt;
                } else if (blackstatus == 311) {
                    bmpBlackTemp1 = bmpBlackClickRihgt;
                    bmpBlackTemp2 = bmpBlackClickRihgt;
                } else if (blackstatus == 322) {
                    bmpBlackTemp1 = bmpBlackClickRihgt;
                    bmpBlackTemp2 = bmpBlackClickRihgt;
                }

                rectRight.top = 0;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlcak = getBitmapSize(bmpBlack);
                canvas.drawBitmap(bmpBlackTemp2, rectBlcak, rectRight, paint);

                rectLeft.top = 0;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                canvas.drawBitmap(bmpBlackTemp1, rectBlcak, rectLeft, paint);

                break;
            case 8: /* G# */
                x1 = fWhiteKeyWidth * 4 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = fWhiteKeyWidth * 4 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpBlackClickRihgt;
                } else if (color == iShadeRight) {
                    bmp = bmpBlackClickRihgt;
                } else {// if (color == iShadeNone)
                    bmp = bmpBlack;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 9: /* A */
                x1 = fWhiteKeyWidth * 5;
                x2 = fWhiteKeyWidth * 5 + fWhiteKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else if (color == iShadeRight) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else {// if (color == iShadeNone)
                    bmp = bmpWhite;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);


                if (blackstatus == 321)// 左手 右手
                {
                    bmpBlackTemp1 = bmpBlackClickRihgt;
                    bmpBlackTemp2 = bmpBlackClickRihgt;
                } else if (blackstatus == 312) {
                    bmpBlackTemp1 = bmpBlackClickRihgt;
                    bmpBlackTemp2 = bmpBlackClickRihgt;
                } else if (blackstatus == 311) {
                    bmpBlackTemp1 = bmpBlackClickRihgt;
                    bmpBlackTemp2 = bmpBlackClickRihgt;
                } else if (blackstatus == 322) {
                    bmpBlackTemp1 = bmpBlackClickRihgt;
                    bmpBlackTemp2 = bmpBlackClickRihgt;
                }

                rectRight.top = 0;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlcak = getBitmapSize(bmpBlack);
                canvas.drawBitmap(bmpBlackTemp2, rectBlcak, rectRight, paint);

                rectLeft.top = 10;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                canvas.drawBitmap(bmpBlackTemp1, rectBlcak, rectLeft, paint);

                break;
            case 10: /* A# */
                x1 = fWhiteKeyWidth * 5 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = fWhiteKeyWidth * 5 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpBlackClickRihgt;
                } else if (color == iShadeRight) {
                    bmp = bmpBlackClickRihgt;
                } else {//if (color == iShadeNone)
                    bmp = bmpBlack;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 11: /* B */
                x1 = fWhiteKeyWidth * 6;
                x2 = fWhiteKeyWidth * 6 + fWhiteKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else if (color == iShadeRight) {
                    bmp = bmpWhiteClickLeftBlackLeft;
                } else {//if (color == iShadeNone)
                    bmp = bmpWhite;
                }

                rectF.top = 0;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                rectLeft.top = 0;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                if (blackstatus == 11) // 右手
                {
                    canvas.drawBitmap(bmpBlackClickRihgt, rectBlcak, rectLeft, paint);
                } else if (blackstatus == 12) {
                    canvas.drawBitmap(bmpBlackClickRihgt, rectBlcak, rectLeft, paint);
                } else {
                    canvas.drawBitmap(bmpBlack, rectBlcak, rectLeft, paint);
                }

                break;
            default:
                break;
        }
        canvas.translate(-(octave * fWhiteKeyWidth * KeysPerOctave + 2 * fWhiteKeyWidth), 0);
    }

    /*
        获取bitmap的大小
    */
    private Rect getBitmapSize(Bitmap bmp) {
        Rect rectf = new Rect();
        if (bmp != null) {
            rectf.left = 0;
            rectf.top = 0;
            rectf.right = bmp.getWidth();
            rectf.bottom = bmp.getHeight();
        } else {
            rectf.left = 0;
            rectf.top = 0;
            rectf.right = 0;
            rectf.bottom = 0;
        }
        return rectf;
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        surfaceReady = true;
//        draw();
        Log.i(TAG, "调用surfaceCreted");
        //new Thread(runnable).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (!starting) {
            draw();
            Log.i(TAG, "调用surfaceChanged");
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        surfaceReady = false;
    }
}