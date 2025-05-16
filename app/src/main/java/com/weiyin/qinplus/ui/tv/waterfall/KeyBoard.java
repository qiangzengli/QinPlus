package com.weiyin.qinplus.ui.tv.waterfall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.ui.tv.bwstaff.KeyBoardModule;
import com.weiyin.qinplus.ui.tv.bwstaff.ScoreController;

import java.util.ArrayList;

/**
 * @author njf
 *         Created by njf on 2016/6/29.
 *         <p/>
 *         键盘钢琴类
 */
public class KeyBoard extends SurfaceView implements SurfaceHolder.Callback {
    private String TAG = "KeyBoard";
    private static final int DOWN = 90;

    private static final int LEFT_STAFF = 2;
    private static final int RIGHT_STAFF = 1;

    public int iShadeLeft = 2;
    public int iShadeRight = 1;
    public int iShadeNone = 0;

    private int keysPerOctave = 7;
    private int maxOctave = 7;
    /**
     * 记录黑键的偏移位置
     */
    private float[] blackKeyOffsets;
    /**
     * 白键宽度
     */
    private float fWhiteKeyWidth;
    /**
     * 白键高度
     */
    private float fWhiteKeyHeight;
    /**
     * 黑键宽度
     */
    private float fBlackKeyWidth;
    /**
     * 黑键高度
     */
    private float fBlackKeyHeight;
    /**
     * 键盘上方空的高度
     */
    private float fSpaceAbove;
    /**
     * 指示灯高度
     */
    private float fLampHeight;
    /**
     * 指示灯宽度
     */
    private float fLampWidth;
    /**
     * 键盘背景
     */
    private Bitmap bmpBackGround;
    /**
     * 白键图片
     */
    private Bitmap bmpWhite;
    /**
     * 黑键图片
     */
    private Bitmap bmpBlack;
    /**
     * 左手点击白键 左边有一个黑键
     */
    private Bitmap bmpWhiteClickLeftBlackLeft;
    /**
     * 左手点击白键 右边有一个黑键
     */
    private Bitmap bmpWhiteClickLeftBlackRight;
    /**
     * 右手点击白键 左边有一个黑键
     */
    private Bitmap bmpWhiteClickRightBlackLeft;
    /**
     * 右手几点白键 右边有一个黑键
     */
    private Bitmap bmpWhiteClickRightBlackRight;
    /**
     * 左手点击黑键
     */
    private Bitmap bmpBlackClickLeft;
    /**
     * 右手点击黑键
     */
    private Bitmap bmpBlackClickRight;
    /**
     * 右手灯
     */
    private Bitmap bmpRightLamp;
    /**
     * 左手灯
     */
    private Bitmap bmpLeftLamp;
    /**
     * 无显示灯
     */
    private Bitmap bmpLamp;

    boolean isVideo;

    public boolean isKeyNote() {
        return isKeyNote;
    }

    public void setIsKeyNote(boolean isKeyNote) {
        this.isKeyNote = isKeyNote;
    }

    private boolean isKeyNote = true;
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

    private ScoreController scoreController;

    public boolean isStarting() {
        return starting;
    }

    public void setStarting(boolean starting) {
        this.starting = starting;
    }

    private boolean starting = false;

    public int getMultiple() {
        return multiple;
    }

    public void setMultiple(int multiple) {
        this.multiple = multiple;
        init();
    }

    public KeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        fWhiteKeyWidth = 0;
        fSpaceAbove = 30;
        paint = new Paint();
        GetBitmap();

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
    }

    public void clean() {
        if (bmpBackGround != null) {
            bmpBackGround.recycle();
        }
        if (bmpWhite != null) {
            bmpWhite.recycle();
        }
        if (bmpBlack != null) {
            bmpBlack.recycle();
        }
        if (bmpWhiteClickLeftBlackLeft != null) {
            bmpWhiteClickLeftBlackLeft.recycle();
        }
        if (bmpWhiteClickLeftBlackRight != null) {
            bmpWhiteClickLeftBlackRight.recycle();
        }
        if (bmpWhiteClickRightBlackLeft != null) {
            bmpWhiteClickRightBlackLeft.recycle();
        }
        if (bmpWhiteClickRightBlackRight != null) {
            bmpWhiteClickRightBlackRight.recycle();
        }
        if (bmpBlackClickLeft != null) {
            bmpBlackClickLeft.recycle();
        }
        if (bmpBlackClickRight != null) {
            bmpBlackClickRight.recycle();
        }
        if (bmpWhiteClickLeftBlackRight != null) {
            bmpLeftLamp.recycle();
        }
        if (bmpLamp != null) {
            bmpLamp.recycle();
        }
        if (bufferBitmap != null) {
            bufferBitmap.recycle();
        }

        if (bmpRightLamp != null) {
            bmpRightLamp.recycle();
        }
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

    public void setMeasureNotes(ArrayList<Integer> timeArrayList, ArrayList<MeasureNote> notes, boolean isVideo, ScoreController scoreController) {
        this.timeArrayList = timeArrayList;
        this.measureNotes = notes;
        this.isVideo = isVideo;
        this.scoreController = scoreController;
        if (notes != null) {
            for (MeasureNote note : notes) {
                if (note.duration > maxDuration) {
                    maxDuration = note.duration;
                }
            }
        }
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
            int noteNumber = measureNotes.get(i).notenumber;
            int nextStart = NextStartTime(i);
            int nextStartTrack = NextStartTimeSameTrack(i);
            int iFinger = measureNotes.get(i).iFinger;
            end -= 30;
            if (end <= start) {
                end += 30;
            }

			/* If we've past the previous and current times, we're done. */
            if ((start > prevPulseTime) && (start > currentPulseTime)) {
                break;
            }

			/* If the note is in the current time, shade it */
            if ((start <= currentPulseTime) && (currentPulseTime < end) && (start >= startPulseTime - 64)) {
                int iBlackStatus = getLeftBlackBlackStatus(noteNumber, currentPulseTime);
                if (useTwoColors) {
                    if (measureNotes.get(i).staff == 1) {
                        if (isKeyNote) {
                            shadeOneNote(bufferCanvas, noteNumber, iShadeRight, iFinger, measureNotes.get(i).staff, iBlackStatus);
                        }
                        shadeOneLamp(bufferCanvas, noteNumber, Constant.DOWN, measureNotes.get(i).staff);
                    } else {
                        if (isKeyNote) {
                            shadeOneNote(bufferCanvas, noteNumber, iShadeLeft, iFinger, measureNotes.get(i).staff, iBlackStatus);
                        }
                        shadeOneLamp(bufferCanvas, noteNumber, Constant.DOWN, measureNotes.get(i).staff);
                    }
                } else {
                    if (isKeyNote) {
                        shadeOneNote(bufferCanvas, noteNumber, iShadeLeft, iFinger, measureNotes.get(i).staff, iBlackStatus);
                    }
                    shadeOneLamp(bufferCanvas, noteNumber, Constant.DOWN, measureNotes.get(i).staff);
                }
            }

			/* If the note is in the previous time, un-shade it, draw it white. */
            else if ((start <= prevPulseTime) && (prevPulseTime < end)) {
                int num = noteNumber % 12;
                int iBlackStatus = getLeftBlackBlackStatus(noteNumber, currentPulseTime);
                if (num == 1 || num == 3 || num == 6 || num == 8 || num == 10) {
                    if (isKeyNote) {
                        shadeOneNote(bufferCanvas, noteNumber, iShadeNone, 0, 0, iBlackStatus);
                    }
                    shadeOneLamp(bufferCanvas, noteNumber, Constant.UP, measureNotes.get(i).staff);
                } else {
                    if (isKeyNote) {
                        shadeOneNote(bufferCanvas, noteNumber, iShadeNone, 0, 0, iBlackStatus);
                    }
                    shadeOneLamp(bufferCanvas, noteNumber, Constant.UP, measureNotes.get(i).staff);
                }
            }
        }
        bufferCanvas.translate(0, -(fSpaceAbove - 5));
        canvas.drawBitmap(bufferBitmap, 0, 0, paint);
        holder.unlockCanvasAndPost(canvas);
    }

    /**
     * 如果当前是白健，需要判断黑键的状态
     * 返回值为黑键的状态 0 代表没有按下， 1,代表左边黑键 : 11右手弹奏  12左手弹奏，2代表右边黑键，3,代表两边黑键都按下
     **/
    int getLeftBlackBlackStatus(int noteNumber, int currentTime) {
        int iType = 0;
        int noteScale = noteNumber % 12;
        int bFlagLeft = 0;
        int bFlagRight = 0;
        switch (noteScale) {
            case 1:
            case 3:
            case 6:
            case 8:
            case 10:
                iType = 0;
                break;
            case 0:
                bFlagRight = judgeBlackStatus(noteNumber + 1, currentTime);
                break;
            case 2:
                bFlagLeft = judgeBlackStatus(noteNumber - 1, currentTime);
                bFlagRight = judgeBlackStatus(noteNumber + 1, currentTime);
                break;
            case 4:
                bFlagLeft = judgeBlackStatus(noteNumber - 1, currentTime);
                break;
            case 5:
                bFlagRight = judgeBlackStatus(noteNumber + 1, currentTime);
                break;
            case 7:
                bFlagLeft = judgeBlackStatus(noteNumber - 1, currentTime);
                bFlagRight = judgeBlackStatus(noteNumber + 1, currentTime);
                break;
            case 9:
                bFlagLeft = judgeBlackStatus(noteNumber - 1, currentTime);
                bFlagRight = judgeBlackStatus(noteNumber + 1, currentTime);
                break;
            case 11:
                bFlagLeft = judgeBlackStatus(noteNumber - 1, currentTime);
                break;
            default:

                break;
        }
        if (bFlagLeft == 1 && bFlagRight == 1) {
            // 两边按下 右手
            return 311;
        } else if (bFlagLeft == 1 && bFlagRight == 2) {
            return 312;
        } else if (bFlagLeft == 2 && bFlagRight == 1) {
            return 321;
        } else if (bFlagLeft == 2 && bFlagRight == 2) {
            return 322;
        } else if (bFlagLeft == 1 && bFlagRight == 0) {
            //左边按下
            return 11;
        } else if (bFlagLeft == 2 && bFlagRight == 0) {
            return 12;
        } else if (bFlagLeft == 0 && bFlagRight == 1) {
            // 右边按下
            return 21;
        } else if (bFlagLeft == 0 && bFlagRight == 2) {
            return 22;
        } else {
            //都没按下
            return iType;
        }
    }

    int judgeBlackStatus(int noteNumber, int currentTime) {
        int lastShadedIndex = FindClosestStartTime(currentTime
                - 1920 - 480);
        for (int i = lastShadedIndex; i < measureNotes.size(); i++) {
            int noteNumberTemp = measureNotes.get(i).notenumber;
            int start = measureNotes.get(i).time;
            int end = measureNotes.get(i).time + measureNotes.get(i).duration;
            int staff = measureNotes.get(i).staff;
            end -= 30;
            if (end <= start) {
                end += 30;
            }

            if (start > currentTime) {
                break;
            }

            if (noteNumberTemp == noteNumber) {
                if (start <= currentTime && end > currentTime) {
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
            if (measureNotes.get(left).time == pulseTime) {
                break;
            } else if (measureNotes.get(i).time <= pulseTime) {
                left = i;
            } else {
                right = i;
            }
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
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 4;    //这个的值压缩的倍数（2的整数倍），数值越小，压缩率越小，图片越清晰

        bmpBackGround = BitmapFactory.decodeResource(getResources(), R.drawable.keyboardbackgroud, opts);
        bmpWhite = BitmapFactory.decodeResource(getResources(), R.drawable.keyboardwhite, opts);
        bmpBlack = BitmapFactory.decodeResource(getResources(), R.drawable.keyboardblack, opts);
        bmpWhiteClickLeftBlackLeft = BitmapFactory.decodeResource(getResources(), R.drawable.whiteclickleftblackleft, opts);
        bmpWhiteClickLeftBlackRight = BitmapFactory.decodeResource(getResources(), R.drawable.whiteclickleftblackright, opts);
        bmpWhiteClickRightBlackLeft = BitmapFactory.decodeResource(getResources(), R.drawable.whiteclickrightblackleft, opts);
        bmpWhiteClickRightBlackRight = BitmapFactory.decodeResource(getResources(), R.drawable.whiteclickrightright, opts);
        bmpBlackClickLeft = BitmapFactory.decodeResource(getResources(), R.drawable.blackclickleft, opts);
        bmpBlackClickRight = BitmapFactory.decodeResource(getResources(), R.drawable.blackclickright, opts);

        bmpRightLamp = BitmapFactory.decodeResource(getResources(), R.drawable.bmprightlamp);
        bmpLeftLamp = BitmapFactory.decodeResource(getResources(), R.drawable.bmpleftlamp);
        bmpLamp = BitmapFactory.decodeResource(getResources(), R.drawable.bmplamp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.keyWidthMeasureSpec = widthMeasureSpec;
        this.keyHeightMeasureSpec = heightMeasureSpec;

        super.onMeasure(keyWidthMeasureSpec, keyHeightMeasureSpec);
        if (widthMeasureSpec < 0 || heightMeasureSpec < 0) {
            return;
        }
        init();
    }

    private void init() {
        fLampWidth = 8;
        fLampHeight = 16;

        int screenWidth = MeasureSpec.getSize(keyWidthMeasureSpec);
        int screenHeight = MeasureSpec.getSize(keyHeightMeasureSpec);

        fWhiteKeyWidth = (float) (screenWidth / (3.0 + keysPerOctave * maxOctave));
        fWhiteKeyHeight = fWhiteKeyWidth * 5 / multiple;
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
        //(int)fWhiteKeyWidth * (keysPerOctave * maxOctave + 3 );
        int height = (int) (fWhiteKeyHeight + fSpaceAbove);

        if (screenWidth <= 0 || height <= 0) {
            Log.d(TAG, "width or height error!");
            return;
        }
        setMeasuredDimension(screenWidth, height);
        bufferBitmap = Bitmap.createBitmap(screenWidth, height, Bitmap.Config.RGB_565);
        bufferCanvas = new Canvas(bufferBitmap);
        if (!starting) {
            this.invalidate();
            draw();
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
        onDrawKeyBoard(canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    /**
     * onDrawKeyBoard
     *
     * @param canvas
     */
    protected void onDrawKeyBoard(Canvas canvas) {
        if (!surfaceReady || bufferBitmap == null) {
            return;
        }

        if (fWhiteKeyWidth == 0) {
            return;
        }
        paint.setAntiAlias(false);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        // 先画背景
        drawBackGround(bufferCanvas);
        // 偏移一点位置开始绘制键盘按键
        bufferCanvas.translate(0, fSpaceAbove - 5);
        // 绘制白键
        DrawWhiteKey(bufferCanvas);
        // 绘制黑键
        DrawBlackKey(bufferCanvas);

        bufferCanvas.translate(0, -(fSpaceAbove - 5));

        canvas.drawBitmap(bufferBitmap, 0, 0, paint);
    }

    private void drawBackGround(Canvas canvas) {
        RectF dst = new RectF();
        dst.left = 0;
        dst.right = fWhiteKeyWidth * (keysPerOctave * maxOctave + 3);
        dst.top = 0;
        dst.bottom = fWhiteKeyHeight + fSpaceAbove;
        Rect rect = getBitmapSize(bmpBackGround);
        canvas.drawBitmap(bmpBackGround, rect, dst, paint);
    }

    private void DrawWhiteKey(Canvas canvas) {
        Rect src = getBitmapSize(bmpWhite);
        RectF rectf = new RectF();
        Rect srcLamp = getBitmapSize(bmpLamp);
        RectF rectFLamp = new RectF();

        for (int i = 0; i < 52; i++) {
            rectFLamp.top = -15;
            rectFLamp.bottom = 1;
            rectFLamp.left = i * fWhiteKeyWidth + fWhiteKeyWidth / 2 - 4f;
            rectFLamp.right = rectFLamp.left + 8;
            canvas.drawBitmap(bmpLamp, srcLamp, rectFLamp, paint);

            rectf.top = 10;
            rectf.bottom = fWhiteKeyHeight;
            rectf.left = i * fWhiteKeyWidth;
            rectf.right = rectf.left + fWhiteKeyWidth;
            canvas.drawBitmap(bmpWhite, src, rectf, paint);

            if (i == 23) {
                paint.setTextSize(30);
                canvas.drawText("C", rectf.left + fWhiteKeyWidth / 3, fWhiteKeyHeight / 8 * 7, paint);
            }
        }

    }

    private void DrawBlackKey(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        Rect srcLamp = getBitmapSize(bmpLamp);
        RectF rectFLamp = new RectF();


        RectF dsttemp = new RectF();
        // 最左边有一个黑色的按钮
        float x1temp = blackKeyOffsets[0];
        rectFLamp.top = -15;
        rectFLamp.bottom = 1;
        rectFLamp.left = x1temp + fBlackKeyWidth / 2 - 4f;
        rectFLamp.right = rectFLamp.left + 8;
        canvas.drawBitmap(bmpLamp, srcLamp, rectFLamp, paint);

        dsttemp.top = 10;
        dsttemp.left = x1temp;
        dsttemp.right = x1temp + fBlackKeyWidth;
        dsttemp.bottom = fBlackKeyHeight;
        Rect srcTemp = getBitmapSize(bmpBlack);
        canvas.drawBitmap(bmpBlack, srcTemp, dsttemp, paint);

        // 后面的黑色按钮
        for (int octave = 0; octave < maxOctave; octave++) {
            canvas.translate(octave * fWhiteKeyWidth * keysPerOctave + 2
                    * fWhiteKeyWidth, 0);
            for (int i = 0; i < 10; i += 2) {
                float x1 = blackKeyOffsets[i];
                float x2 = blackKeyOffsets[i + 1];
                RectF dst = new RectF();

//                if(isVideo) {
                rectFLamp.top = -15;
                rectFLamp.bottom = 1;
                rectFLamp.left = x1 + fBlackKeyWidth / 2 - 4f;
                rectFLamp.right = rectFLamp.left + 8;
                canvas.drawBitmap(bmpLamp, srcLamp, rectFLamp, paint);
                dst.top = 10;
//                }else{
//                    dst.top = 0;
//                }
                dst.left = x1;
                dst.right = x1 + fBlackKeyWidth;

                dst.bottom = fBlackKeyHeight;
                Rect src = getBitmapSize(bmpBlack);
                canvas.drawBitmap(bmpBlack, src, dst, paint);
            }
            canvas.translate(
                    -(octave * fWhiteKeyWidth * keysPerOctave + 2 * fWhiteKeyWidth),
                    0);
        }
        paint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 区分左右手的显示
     *
     * @param keyBoardModuleArrayList
     * @param staff
     */
    public void poina(ArrayList<KeyBoardModule> keyBoardModuleArrayList, int staff) {
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

    public void drawKeyBoard(ArrayList<KeyBoardModule> keyBoardModuleArrayList, int staff) {
        int i = 0;
        if (scoreController != null && scoreController.getDrawKeyBoardList() != null) {
            while (scoreController.getDrawKeyBoardList().size() != 0) {
                shadeOneNot(bufferCanvas, scoreController.getDrawKeyBoardList().get(i).getNumber(), scoreController.getDrawKeyBoardList().get(i).getStatus(), staff);
                scoreController.getDrawKeyBoardList().remove(i);
            }
            LogUtil.i(TAG, "drawKeyBoardList.size=" + scoreController.getDrawKeyBoardList().size());
            if (scoreController.getDrawKeyBoardList().size() != 0) {
                drawKeyBoard(scoreController.getDrawKeyBoardList(), staff);
            } else {
                LogUtil.i(TAG, "设置为false");
            }
        }
    }


    public void poinaLamp(int noteNumber, int status, int staff) {
        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
            return;
        }

        bufferCanvas.translate(0, fSpaceAbove - 5);
        shadeOneLamp(bufferCanvas, noteNumber, status, staff);
        bufferCanvas.translate(0, -(fSpaceAbove - 5));
        canvas.drawBitmap(bufferBitmap, 0, 0, paint);
        holder.unlockCanvasAndPost(canvas);
    }


    /**
     * 更新黑键按下的数组
     *
     * @param noteNumber
     * @param status
     * @param staff
     */
    public void updateBlackClick(int noteNumber, int status, int staff) {
        int noteScale = noteNumber % 12;
        if (noteScale == 1 || noteScale == 3 || noteScale == 6 || noteScale == 8 || noteScale == 10) {
            if (status == DOWN) {
                boolean bFlag = false;
                for (int i = 0; i < measureNotesBlackClick.size(); i++) {
                    MeasureNote note = measureNotesBlackClick.get(i);
                    if (note.notenumber == noteNumber) {
                        bFlag = true;
                        break;
                    }
                }
                if (!bFlag) {
                    MeasureNote note = new MeasureNote();
                    note.staff = staff;
                    note.notenumber = noteNumber;
                    measureNotesBlackClick.add(note);
                }
            } else {
                for (int i = 0; i < measureNotesBlackClick.size(); i++) {
                    MeasureNote note = measureNotesBlackClick.get(i);
                    if (note.notenumber == noteNumber) {
                        measureNotesBlackClick.remove(i);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 返回值为黑键的状态 0 代表没有按下， 1,代表左边黑键 ，2代表右边黑键，3,代表两边黑键都按下
     *
     * @param noteNumber
     * @return
     */
    int getClickBlackStatus(int noteNumber) {
        int iType = 0;
        int noteScale = noteNumber % 12;
        if (noteScale == 0 || noteScale == 2 || noteScale == 4 || noteScale == 5 || noteScale == 7 || noteScale == 9 || noteScale == 11) {
            boolean bFlagLeft = false;
            boolean bFlagRight = false;
            switch (noteScale) {
                case 1:
                case 3:
                case 6:
                case 8:
                case 10:
                    iType = 0;
                    break;
                case 0:
                    bFlagRight = hasBlackClicked(noteNumber + 1);
                    break;
                case 2:
                    bFlagLeft = hasBlackClicked(noteNumber - 1);
                    bFlagRight = hasBlackClicked(noteNumber + 1);
                    break;
                case 4:
                    bFlagLeft = hasBlackClicked(noteNumber - 1);
                    break;
                case 5:
                    bFlagRight = hasBlackClicked(noteNumber + 1);
                    break;
                case 7:
                    bFlagLeft = hasBlackClicked(noteNumber - 1);
                    bFlagRight = hasBlackClicked(noteNumber + 1);
                    break;
                case 9:
                    bFlagLeft = hasBlackClicked(noteNumber - 1);
                    bFlagRight = hasBlackClicked(noteNumber + 1);
                    break;
                case 11:
                    bFlagLeft = hasBlackClicked(noteNumber - 1);
                    break;
                default:
                    break;
            }
            if (bFlagLeft && bFlagRight) {
                // 两边按下 右手
                return 3;
            } else if (bFlagLeft && !bFlagRight) {
                //左边按下
                return 1;
            } else if (!bFlagLeft && bFlagRight) {
                // 右边按下
                return 2;
            } else {
                //都没按下
                return iType;
            }
        }
        return iType;
    }

    boolean hasBlackClicked(int noteNumber) {
        for (int i = 0; i < measureNotesBlackClick.size(); i++) {
            MeasureNote note = measureNotesBlackClick.get(i);
            if (noteNumber == note.notenumber) {
                return true;
            }
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
    public void shadeOneLamp(Canvas canvas, int notenumber, int status, int staff) {
        if (staff == 0) {
            staff = 2;
        }
        int octave = notenumber / 12;
        int notescale = notenumber % 12;
        octave -= 2;
        if (octave < -1 || octave > maxOctave) {
            return;
        }
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.translate(octave * fWhiteKeyWidth * keysPerOctave + 2 * fWhiteKeyWidth, 0);
        float x1, x2, x3;


        Bitmap bmp;
        Rect rect;

        RectF rectF = new RectF();


        switch (notescale) {
            case 0:
                /*C*/
                x1 = fWhiteKeyWidth * 0.5f - fLampWidth * 0.5f;
                x2 = x1 + fLampWidth;

                if (staff == LEFT_STAFF && status == DOWN) {
                    bmp = bmpLeftLamp;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpRightLamp;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpLamp;
                }


                rectF.top = -15;
                rectF.left = x1;
                rectF.bottom = 1;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);
                break;
            case 1:
                /*C#*/
                x1 = fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth * 0.5f - fLampWidth * 0.5f;
                x2 = x1 + fLampWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpLeftLamp;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpRightLamp;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpLamp;
                }

                rectF.top = -15;
                rectF.left = x1;
                rectF.bottom = 1;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);
                break;
            case 2:
                /*D*/
                x1 = fWhiteKeyWidth + fWhiteKeyWidth * 0.5f - fLampWidth * 0.5f;
                x2 = x1 + fLampWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpLeftLamp;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpRightLamp;
                } else {// if (status.equalsIgnoreCase("up"))
                    bmp = bmpLamp;
                }

                rectF.top = -15;
                rectF.left = x1;
                rectF.bottom = 1;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 3:
                /*D#*/
                x1 = fWhiteKeyWidth * 2 - fLampWidth * 0.5f;
                x2 = x1 + fLampWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpLeftLamp;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpRightLamp;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpLamp;
                }

                rectF.top = -15;
                rectF.left = x1;
                rectF.bottom = 1;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);
                break;
            case 4:
                /*E */
                x1 = fWhiteKeyWidth * 2 + fWhiteKeyWidth * 0.5f - fLampWidth * 0.5f;
                x2 = x1 + fLampWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpLeftLamp;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpRightLamp;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpLamp;
                }

                rectF.top = -15;
                rectF.left = x1;
                rectF.bottom = 1;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);


                break;
            case 5:
                /*F*/
                x1 = fWhiteKeyWidth * 3 + fWhiteKeyWidth * 0.5f - fLampWidth * 0.5f;
                x2 = x1 + fLampWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpLeftLamp;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpRightLamp;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpLamp;
                }

                rectF.top = -15;
                rectF.left = x1;
                rectF.bottom = 1;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);


                break;
            case 6:
                /*F#*/
                x1 = fWhiteKeyWidth * 4 - fLampWidth * 0.5f;
                x2 = x1 + fLampWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpLeftLamp;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpRightLamp;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpLamp;
                }

                rectF.top = -15;
                rectF.left = x1;
                rectF.bottom = 1;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 7:
                /*G */
                x1 = fWhiteKeyWidth * 4 + fWhiteKeyWidth * 0.5f - fLampWidth * 0.5f;
                x2 = x1 + fLampWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpLeftLamp;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpRightLamp;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpLamp;
                }

                rectF.top = -15;
                rectF.left = x1;
                rectF.bottom = 1;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 8:
                /*G# */
                x1 = fWhiteKeyWidth * 5 - fLampWidth * 0.5f;
                x2 = x1 + fLampWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpLeftLamp;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpRightLamp;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpLamp;
                }

                rectF.top = -15;
                rectF.left = x1;
                rectF.bottom = 1;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 9:
                /*A*/
                x1 = fWhiteKeyWidth * 5 + fWhiteKeyWidth * 0.5f - fLampWidth * 0.5f;
                x2 = x1 + fLampWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpLeftLamp;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpRightLamp;
                } else {// if (status.equalsIgnoreCase("up"))
                    bmp = bmpLamp;
                }

                rectF.top = -15;
                rectF.left = x1;
                rectF.bottom = 1;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 10:
                /*A# */
                x1 = fWhiteKeyWidth * 6 - fLampWidth * 0.5f;
                x2 = x1 + fLampWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpLeftLamp;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpRightLamp;
                } else {// if (status.equalsIgnoreCase("up"))
                    bmp = bmpLamp;
                }
                rectF.top = -15;
                rectF.left = x1;
                rectF.bottom = 1;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 11:
                /*B*/
                x1 = fWhiteKeyWidth * 6 + fWhiteKeyWidth * 0.5f - fLampWidth * 0.5f;
                x2 = x1 + fLampWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpLeftLamp;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpRightLamp;
                } else {// if (status.equalsIgnoreCase("up"))
                    bmp = bmpLamp;
                }

                rectF.top = -15;
                rectF.left = x1;
                rectF.bottom = 1;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            default:
                break;
        }
        canvas.translate(-(octave * fWhiteKeyWidth * keysPerOctave + 2 * fWhiteKeyWidth), 0);
    }

    /**
     * 根据音高和接收到的指令绘制钢琴的按下和抬起
     * 21~109音值得钢琴绘制
     *
     * @param noteNumber
     * @param status
     */
    public void shadeOneNot(Canvas canvas, int noteNumber, int status, int staff) {
        updateBlackClick(noteNumber, status, staff);
        int octave = noteNumber / 12;
        int noteScale = noteNumber % 12;
        octave -= 2;
        if (octave < -1 || octave > maxOctave) {
            return;
        }
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.translate(octave * fWhiteKeyWidth * keysPerOctave + 2 * fWhiteKeyWidth, 0);
        float x1, x2, x3;

        float bottomHalfHeight = (fWhiteKeyHeight - (fBlackKeyHeight + 3));

        Bitmap bmp;
        Bitmap bmpBlackTemp1 = bmpBlack;
        Bitmap bmpBlackTemp2 = bmpBlack;
        Rect rect;
        Rect rectBlack = getBitmapSize(bmpBlack);

        RectF rectF = new RectF();
        RectF rectLeft = new RectF();
        RectF rectRight = new RectF();

        int blackStatus = getClickBlackStatus(noteNumber);

        switch (noteScale) {
            case 0:
                /*C*/
                x1 = 0;
                x2 = fWhiteKeyWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpWhiteClickLeftBlackRight;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpWhiteClickRightBlackRight;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpWhite;
                }


                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                if (noteNumber == 60) {
                    paint.setTextSize(30);
                    canvas.drawText("C", rectF.left + fWhiteKeyWidth / 3, fWhiteKeyHeight / 8 * 7, paint);
                }
                if (noteNumber == 108) {

                } else {
                    rectRight.top = 10;
                    rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                    rectRight.right = rectRight.left + fBlackKeyWidth;
                    rectRight.bottom = fBlackKeyHeight;
                    rectBlack = getBitmapSize(bmpBlack);
                    if (blackStatus == 2) {
                        canvas.drawBitmap(bmpBlackClickLeft, rectBlack, rectRight, paint);
                    } else {

                        canvas.drawBitmap(bmpBlack, rectBlack, rectRight, paint);
                    }
                }
                break;
            case 1:
                /*C#*/
                x1 = fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = x1 + fBlackKeyWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpBlackClickLeft;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpBlackClickRight;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpBlack;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);
                break;
            case 2:
                /*D*/
                x1 = fWhiteKeyWidth;
                x2 = fWhiteKeyWidth + fWhiteKeyWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpWhiteClickLeftBlackRight;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpWhiteClickRightBlackRight;
                } else {// if (status.equalsIgnoreCase("up"))
                    bmp = bmpWhite;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                if (blackStatus == 3) {
                    bmpBlackTemp1 = bmpBlackClickLeft;
                    bmpBlackTemp2 = bmpBlackClickLeft;
                } else if (blackStatus == 2) {
                    bmpBlackTemp2 = bmpBlackClickLeft;
                } else if (blackStatus == 1) {
                    bmpBlackTemp1 = bmpBlackClickLeft;
                }
                rectRight.top = 10;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlack = getBitmapSize(bmpBlack);
                canvas.drawBitmap(bmpBlackTemp2, rectBlack, rectRight, paint);

                rectLeft.top = 10;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                canvas.drawBitmap(bmpBlackTemp1, rectBlack, rectLeft, paint);
                break;
            case 3:
                /*D#*/
                x1 = fWhiteKeyWidth + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = fWhiteKeyWidth + fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpBlackClickLeft;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpBlackClickRight;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpBlack;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);
                break;
            case 4:
                /*E */
                x1 = fWhiteKeyWidth * 2;
                x2 = fWhiteKeyWidth * 2 + fWhiteKeyWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpWhiteClickLeftBlackRight;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpWhiteClickRightBlackRight;
                } else {// if (status.equalsIgnoreCase("up"))
                    bmp = bmpWhite;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);


                rectLeft.top = 10;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                if (blackStatus == 1) {
                    canvas.drawBitmap(bmpBlackClickLeft, rectBlack, rectLeft, paint);
                } else {

                    canvas.drawBitmap(bmpBlack, rectBlack, rectLeft, paint);
                }

                break;
            case 5:
                /*F*/
                x1 = fWhiteKeyWidth * 3;
                x2 = fWhiteKeyWidth * 3 + fWhiteKeyWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpWhiteClickLeftBlackRight;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpWhiteClickRightBlackRight;
                } else {// if (status.equalsIgnoreCase("up"))
                    bmp = bmpWhite;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                rectRight.top = 10;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlack = getBitmapSize(bmpBlack);
                if (blackStatus == 2) {
                    canvas.drawBitmap(bmpBlackClickLeft, rectBlack, rectRight, paint);
                } else {
                    canvas.drawBitmap(bmpBlack, rectBlack, rectRight, paint);
                }

                break;
            case 6:
                /*F#*/
                x1 = fWhiteKeyWidth * 3 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = fWhiteKeyWidth * 3 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpBlackClickLeft;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpBlackClickRight;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpBlack;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 7:
                /*G */
                x1 = fWhiteKeyWidth * 4;
                x2 = fWhiteKeyWidth * 4 + fWhiteKeyWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpWhiteClickLeftBlackRight;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpWhiteClickRightBlackRight;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpWhite;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                if (blackStatus == 3) {
                    bmpBlackTemp1 = bmpBlackClickLeft;
                    bmpBlackTemp2 = bmpBlackClickLeft;
                } else if (blackStatus == 2) {
                    bmpBlackTemp2 = bmpBlackClickLeft;
                } else if (blackStatus == 1) {
                    bmpBlackTemp1 = bmpBlackClickLeft;
                }
                rectRight.top = 10;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlack = getBitmapSize(bmpBlack);
                canvas.drawBitmap(bmpBlackTemp2, rectBlack, rectRight, paint);

                rectLeft.top = 10;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                canvas.drawBitmap(bmpBlackTemp1, rectBlack, rectLeft, paint);
                break;
            case 8:
                /*G# */
                x1 = fWhiteKeyWidth * 4 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = fWhiteKeyWidth * 4 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpBlackClickLeft;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpBlackClickRight;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpBlack;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 9:
                /*A*/
                x1 = fWhiteKeyWidth * 5;
                x2 = fWhiteKeyWidth * 5 + fWhiteKeyWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpWhiteClickLeftBlackRight;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpWhiteClickRightBlackRight;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpWhite;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                if (blackStatus == 3) {
                    bmpBlackTemp1 = bmpBlackClickLeft;
                    bmpBlackTemp2 = bmpBlackClickLeft;
                } else if (blackStatus == 2) {
                    bmpBlackTemp2 = bmpBlackClickLeft;
                } else if (blackStatus == 1) {
                    bmpBlackTemp1 = bmpBlackClickLeft;
                }

                rectRight.top = 10;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlack = getBitmapSize(bmpBlack);
                canvas.drawBitmap(bmpBlackTemp2, rectBlack, rectRight, paint);

                if (noteNumber == 21) {

                } else {
                    rectLeft.top = 10;
                    rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                    rectLeft.right = rectLeft.left + fBlackKeyWidth;
                    rectLeft.bottom = fBlackKeyHeight;
                    canvas.drawBitmap(bmpBlackTemp1, rectBlack, rectLeft, paint);
                }
                break;
            case 10:
                /*A# */
                x1 = fWhiteKeyWidth * 5 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = fWhiteKeyWidth * 5 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpBlackClickLeft;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpBlackClickRight;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpBlack;
                }
                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 11:
                /*B*/
                x1 = fWhiteKeyWidth * 6;
                x2 = fWhiteKeyWidth * 6 + fWhiteKeyWidth;

                if (staff == LEFT_STAFF && status == (DOWN)) {
                    bmp = bmpWhiteClickLeftBlackRight;
                } else if (staff == RIGHT_STAFF && status == (DOWN)) {
                    bmp = bmpWhiteClickRightBlackRight;
                } else {//if (status.equalsIgnoreCase("up"))
                    bmp = bmpWhite;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                rectLeft.top = 10;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                if (blackStatus == 1) {
                    canvas.drawBitmap(bmpBlackClickLeft, rectBlack, rectLeft, paint);
                } else {
                    canvas.drawBitmap(bmpBlack, rectBlack, rectLeft, paint);
                }
                break;
            default:
                break;
        }
        canvas.translate(-(octave * fWhiteKeyWidth * keysPerOctave + 2 * fWhiteKeyWidth), 0);
    }

    /**
     * 黑键的状态 0 代表没有按下， 1,代表左边黑键，2代表右边黑键，3,代表两边黑键都按下
     * Shade the given note with the given brush. We only draw notes from
     *
     * @param noteNumber 24 to 96. (Middle-C is 60).
     */
    private void shadeOneNote(Canvas canvas, int noteNumber, int color, int iFinger, int iChannel, int iBlackStatus) {
        int blackStatus = iBlackStatus;
        int octave = noteNumber / 12;
        int noteScale = noteNumber % 12;

        octave -= 2;
        if (octave < 0 || octave >= maxOctave) {
            return;
        }
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.translate(octave * fWhiteKeyWidth * keysPerOctave + 2 * fWhiteKeyWidth, 0);
        float x1, x2, x3;

        Bitmap bmp;
        Rect rect;
        Rect rectBlack = getBitmapSize(bmpBlack);

        RectF rectF = new RectF();
        RectF rectLeft = new RectF();
        RectF rectRight = new RectF();

        Bitmap bmpBlackTemp1 = bmpBlack;
        Bitmap bmpBlackTemp2 = bmpBlack;

		/* noteScale goes from 0 to 11, from C to B. */
        switch (noteScale) {
            case 0:
                /* C */
                x1 = 0;
                x2 = fWhiteKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpWhiteClickLeftBlackRight;
                } else if (color == iShadeRight) {
                    bmp = bmpWhiteClickRightBlackRight;
                } else {// if (color == iShadeNone)
                    bmp = bmpWhite;
                }
                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                rectRight.top = 10;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlack = getBitmapSize(bmpBlack);

                if (noteNumber == 60) {
                    paint.setTextSize(30);
                    canvas.drawText("C", rectF.left + fWhiteKeyWidth / 3, fWhiteKeyHeight / 8 * 7, paint);
                }

                if (blackStatus == 21) {
                    // 右手
                    canvas.drawBitmap(bmpBlackClickRight, rectBlack, rectRight, paint);
                } else if (blackStatus == 22) {
                    // 左手
                    canvas.drawBitmap(bmpBlackClickLeft, rectBlack, rectRight, paint);
                } else {
                    canvas.drawBitmap(bmpBlack, rectBlack, rectRight, paint);
                }
                break;
            case 1:
                /* C# */
                x1 = fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = x1 + fBlackKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpBlackClickLeft;
                } else if (color == iShadeRight) {
                    bmp = bmpBlackClickRight;
                } else {
                    //if (color == iShadeNone)
                    bmp = bmpBlack;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);
                break;
            case 2:
                /* D */
                x1 = fWhiteKeyWidth;
                x2 = fWhiteKeyWidth + fWhiteKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpWhiteClickLeftBlackRight;
                } else if (color == iShadeRight) {
                    bmp = bmpWhiteClickRightBlackRight;
                } else {
                    //if (color == iShadeNone)
                    bmp = bmpWhite;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                if (blackStatus == 321) {
                    // 左手 右手
                    bmpBlackTemp1 = bmpBlackClickLeft;
                    bmpBlackTemp2 = bmpBlackClickRight;
                } else if (blackStatus == 312) {
                    bmpBlackTemp1 = bmpBlackClickRight;
                    bmpBlackTemp2 = bmpBlackClickLeft;
                } else if (blackStatus == 311) {
                    bmpBlackTemp1 = bmpBlackClickRight;
                    bmpBlackTemp2 = bmpBlackClickRight;
                } else if (blackStatus == 322) {
                    bmpBlackTemp1 = bmpBlackClickLeft;
                    bmpBlackTemp2 = bmpBlackClickLeft;
                }

                rectRight.top = 10;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlack = getBitmapSize(bmpBlack);
                canvas.drawBitmap(bmpBlackTemp2, rectBlack, rectRight, paint);

                rectLeft.top = 10;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                canvas.drawBitmap(bmpBlackTemp1, rectBlack, rectLeft, paint);
                break;
            case 3:
                /* D# */
                x1 = fWhiteKeyWidth + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = fWhiteKeyWidth + fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpBlackClickLeft;
                } else if (color == iShadeRight) {
                    bmp = bmpBlackClickRight;
                } else {
                    //if (color == iShadeNone)
                    bmp = bmpBlack;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);
                break;
            case 4:
                /* E */
                x1 = fWhiteKeyWidth * 2;
                x2 = fWhiteKeyWidth * 2 + fWhiteKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpWhiteClickLeftBlackRight;
                } else if (color == iShadeRight) {
                    bmp = bmpWhiteClickRightBlackRight;
                } else {
                    //if (color == iShadeNone)
                    bmp = bmpWhite;
                }
                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                rectLeft.top = 10;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                if (blackStatus == 11) {
                    // 右手
                    canvas.drawBitmap(bmpBlackClickRight, rectBlack, rectLeft, paint);
                } else if (blackStatus == 12) {
                    canvas.drawBitmap(bmpBlackClickLeft, rectBlack, rectLeft, paint);
                } else {
                    canvas.drawBitmap(bmpBlack, rectBlack, rectLeft, paint);
                }

                break;
            case 5:
                /* F */
                x1 = fWhiteKeyWidth * 3;
                x2 = fWhiteKeyWidth * 3 + fWhiteKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpWhiteClickLeftBlackRight;
                } else if (color == iShadeRight) {
                    bmp = bmpWhiteClickRightBlackRight;
                } else {
                    // if (color == iShadeNone)
                    bmp = bmpWhite;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                rectRight.top = 10;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlack = getBitmapSize(bmpBlack);
                if (blackStatus == 21) {
                    // 右手
                    canvas.drawBitmap(bmpBlackClickRight, rectBlack, rectRight, paint);
                } else if (blackStatus == 22) {
                    // 左手
                    canvas.drawBitmap(bmpBlackClickLeft, rectBlack, rectRight, paint);
                } else {
                    canvas.drawBitmap(bmpBlack, rectBlack, rectRight, paint);
                }

                break;
            case 6:
                /* F# */
                x1 = fWhiteKeyWidth * 3 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = fWhiteKeyWidth * 3 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpBlackClickLeft;
                } else if (color == iShadeRight) {
                    bmp = bmpBlackClickRight;
                } else {
                    // if (color == iShadeNone)
                    bmp = bmpBlack;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 7:
                /* G */
                x1 = fWhiteKeyWidth * 4;
                x2 = fWhiteKeyWidth * 4 + fWhiteKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpWhiteClickLeftBlackRight;
                } else if (color == iShadeRight) {
                    bmp = bmpWhiteClickRightBlackRight;
                } else {
                    //if (color == iShadeNone)
                    bmp = bmpWhite;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                if (blackStatus == 321) {
                    // 左手 右手
                    bmpBlackTemp1 = bmpBlackClickLeft;
                    bmpBlackTemp2 = bmpBlackClickRight;
                } else if (blackStatus == 312) {
                    bmpBlackTemp1 = bmpBlackClickRight;
                    bmpBlackTemp2 = bmpBlackClickLeft;
                } else if (blackStatus == 311) {
                    bmpBlackTemp1 = bmpBlackClickRight;
                    bmpBlackTemp2 = bmpBlackClickRight;
                } else if (blackStatus == 322) {
                    bmpBlackTemp1 = bmpBlackClickLeft;
                    bmpBlackTemp2 = bmpBlackClickLeft;
                }

                rectRight.top = 10;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlack = getBitmapSize(bmpBlack);
                canvas.drawBitmap(bmpBlackTemp2, rectBlack, rectRight, paint);

                rectLeft.top = 10;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                canvas.drawBitmap(bmpBlackTemp1, rectBlack, rectLeft, paint);

                break;
            case 8:
                /* G# */
                x1 = fWhiteKeyWidth * 4 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = fWhiteKeyWidth * 4 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpBlackClickLeft;
                } else if (color == iShadeRight) {
                    bmp = bmpBlackClickRight;
                } else {// if (color == iShadeNone)
                    bmp = bmpBlack;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 9:
                /* A */
                x1 = fWhiteKeyWidth * 5;
                x2 = fWhiteKeyWidth * 5 + fWhiteKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpWhiteClickLeftBlackRight;
                } else if (color == iShadeRight) {
                    bmp = bmpWhiteClickRightBlackRight;
                } else {// if (color == iShadeNone)
                    bmp = bmpWhite;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);


                if (blackStatus == 321) {
                    // 左手 右手
                    bmpBlackTemp1 = bmpBlackClickLeft;
                    bmpBlackTemp2 = bmpBlackClickRight;
                } else if (blackStatus == 312) {
                    bmpBlackTemp1 = bmpBlackClickRight;
                    bmpBlackTemp2 = bmpBlackClickLeft;
                } else if (blackStatus == 311) {
                    bmpBlackTemp1 = bmpBlackClickRight;
                    bmpBlackTemp2 = bmpBlackClickRight;
                } else if (blackStatus == 322) {
                    bmpBlackTemp1 = bmpBlackClickLeft;
                    bmpBlackTemp2 = bmpBlackClickLeft;
                }

                rectRight.top = 10;
                rectRight.left = x1 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                rectRight.right = rectRight.left + fBlackKeyWidth;
                rectRight.bottom = fBlackKeyHeight;
                rectBlack = getBitmapSize(bmpBlack);
                canvas.drawBitmap(bmpBlackTemp2, rectBlack, rectRight, paint);

                rectLeft.top = 10;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                canvas.drawBitmap(bmpBlackTemp1, rectBlack, rectLeft, paint);

                break;
            case 10:
                /* A# */
                x1 = fWhiteKeyWidth * 5 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f;
                x2 = fWhiteKeyWidth * 5 + fWhiteKeyWidth - fBlackKeyWidth * 0.5f + fBlackKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpBlackClickLeft;
                } else if (color == iShadeRight) {
                    bmp = bmpBlackClickRight;
                } else {//if (color == iShadeNone)
                    bmp = bmpBlack;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fBlackKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                break;
            case 11:
                /* B */
                x1 = fWhiteKeyWidth * 6;
                x2 = fWhiteKeyWidth * 6 + fWhiteKeyWidth;

                if (color == iShadeLeft) {
                    bmp = bmpWhiteClickLeftBlackRight;
                } else if (color == iShadeRight) {
                    bmp = bmpWhiteClickRightBlackRight;
                } else {//if (color == iShadeNone)
                    bmp = bmpWhite;
                }

                rectF.top = 10;
                rectF.left = x1;
                rectF.bottom = fWhiteKeyHeight;
                rectF.right = x2;
                rect = getBitmapSize(bmp);
                canvas.drawBitmap(bmp, rect, rectF, paint);

                rectLeft.top = 10;
                rectLeft.left = x1 - 0.5f * fBlackKeyWidth;
                rectLeft.right = rectLeft.left + fBlackKeyWidth;
                rectLeft.bottom = fBlackKeyHeight;
                if (blackStatus == 11) {
                    // 右手
                    canvas.drawBitmap(bmpBlackClickRight, rectBlack, rectLeft, paint);
                } else if (blackStatus == 12) {
                    canvas.drawBitmap(bmpBlackClickLeft, rectBlack, rectLeft, paint);
                } else {
                    canvas.drawBitmap(bmpBlack, rectBlack, rectLeft, paint);
                }

                break;
            default:
                break;
        }
        canvas.translate(-(octave * fWhiteKeyWidth * keysPerOctave + 2 * fWhiteKeyWidth), 0);
    }

    /**
     * 获取bitmap的大小
     *
     * @param bmp
     */
    private Rect getBitmapSize(Bitmap bmp) {
        Rect rectF = new Rect();
        if (bmp != null) {
            rectF.left = 0;
            rectF.top = 0;
            rectF.right = bmp.getWidth();
            rectF.bottom = bmp.getHeight();
        } else {
            rectF.left = 0;
            rectF.top = 0;
            rectF.right = 0;
            rectF.bottom = 0;
        }
        return rectF;
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
        draw();
        setStarting(false);
        Log.i(TAG, "调用surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        surfaceReady = false;
    }
}
