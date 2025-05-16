package com.weiyin.qinplus.ui.tv.waterfall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.weiyin.qinplus.R;

import java.util.ArrayList;


/*

WaterFallSurfaceView
瀑布流实现
 */

public class WaterFallSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    int STEP = 8;
    int DISTANCE = 2;
    float KEY_WIDTH;
    float WhiteKeyWidth;

    public final int KeysPerOctave = 7;
    public final int MaxOctave = 7;

    private final String TAG = "WaterFallSurfaceView";

    private boolean surfaceReady;            // 是否能画
    private Bitmap bufferBitmap;
    private Canvas bufferCanvas;            // 双缓存
    private Paint paint;
    int width;
    int height;

    ArrayList<WaterfallInfo> shutters = new ArrayList<WaterfallInfo>();

    //	Bitmap bitmapbackground;
    Bitmap bitmapBlackLeft;
    Bitmap bitmapBlackRight;
    Bitmap bitmapWhiteLeft;
    Bitmap bitmapWhiteRight;
    Bitmap bitmapwhiteLeftClick;
    Bitmap bitmapwhiteLeftClickNone;
    Bitmap bitmapwhiteRightClick;
    Bitmap bitmapwhiteRightClickNone;

    Bitmap bitmapblackLeftClick;
    Bitmap bitmapblackLeftClickNone;
    Bitmap bitmapblackRightClick;
    Bitmap bitmapblackRightClickNone;

    Bitmap bitmapblackleftcircle;
    Bitmap bitmapblackrightcircle;
    Bitmap bitmapwhiteleftcircle;
    Bitmap bitmapwhiterightcircle;

    Bitmap bitmapBlackLeftShort;
    Bitmap bitmapBlackRightShort;
    Bitmap bitmapWhiteLeftShort;
    Bitmap bitmapWhiteRightShort;


    public Handler handler = new Handler() {
        public synchronized void handleMessage(Message msg) {
            draw();
        }
    };

    public void recycle() {
        bitmapBlackLeft.recycle();
        bitmapBlackRight.recycle();
        bitmapWhiteLeft.recycle();
        bitmapWhiteRight.recycle();
        bitmapwhiteLeftClick.recycle();
        bitmapwhiteLeftClickNone.recycle();
        bitmapwhiteRightClick.recycle();
        bitmapwhiteRightClickNone.recycle();

        bitmapblackLeftClick.recycle();
        bitmapblackLeftClickNone.recycle();
        bitmapblackRightClick.recycle();
        bitmapblackRightClickNone.recycle();

        bitmapblackleftcircle.recycle();
        bitmapblackrightcircle.recycle();
        bitmapwhiteleftcircle.recycle();
        bitmapwhiterightcircle.recycle();

        bitmapBlackLeftShort.recycle();
        bitmapBlackRightShort.recycle();
        bitmapWhiteLeftShort.recycle();
        bitmapWhiteRightShort.recycle();
        if (bufferBitmap != null) {
            bufferBitmap.recycle();
        }

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (shutters != null) {
            shutters = null;
        }
    }

    public WaterFallSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        paint = new Paint();
        paint.setAntiAlias(false);
        paint.setTextSize(9.0f);

        Log.i(TAG, "WaterFallSurfaceView");

        //bitmapbackground = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallbackground);;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 4;    //这个的值压缩的倍数（2的整数倍），数值越小，压缩率越小，图片越清晰

        //返回原图解码之后的bitmap对象
        bitmapBlackLeft = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackleft, opts);
        bitmapBlackRight = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackright, opts);
        bitmapWhiteLeft = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiteleft, opts);
        bitmapWhiteRight = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiteright, opts);

        bitmapwhiteLeftClick = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiteleftclick, opts);
        bitmapwhiteLeftClickNone = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiteleftclicknone, opts);
        bitmapwhiteRightClick = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiterightclick, opts);
        bitmapwhiteRightClickNone = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiterightclicknone, opts);
        bitmapblackLeftClick = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackleftclick, opts);
        bitmapblackLeftClickNone = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackleftclicknone, opts);
        bitmapblackRightClick = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackrightclick, opts);
        bitmapblackRightClickNone = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackrightclicknone, opts);

        bitmapblackleftcircle = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackleftcircle, opts);
        bitmapblackrightcircle = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackrightcircle, opts);
        bitmapwhiteleftcircle = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiteleftcircle, opts);
        bitmapwhiterightcircle = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiterightcircle, opts);

        bitmapBlackLeftShort = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackleftshort, opts);
        bitmapBlackRightShort = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackrightshort, opts);
        bitmapWhiteLeftShort = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiteleftshort, opts);
        bitmapWhiteRightShort = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiterightshort, opts);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
    }

    public WaterFallSurfaceView(Context context) {
        // TODO Auto-generated constructor stub
        super(context);
        // TODO Auto-generated constructor stub
        paint = new Paint();
        paint.setAntiAlias(false);
        paint.setTextSize(9.0f);
        Log.i(TAG, "WaterFallSurfaceView");

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 4;    //这个的值压缩的倍数（2的整数倍），数值越小，压缩率越小，图片越清晰

        //返回原图解码之后的bitmap对象
        //bitmapbackground = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallbackground);
        bitmapBlackLeft = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackleft, opts);
        bitmapBlackRight = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackright, opts);
        bitmapWhiteLeft = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiteleft, opts);
        bitmapWhiteRight = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiteright, opts);

        bitmapwhiteLeftClick = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiteleftclick, opts);
        bitmapwhiteLeftClickNone = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiteleftclicknone, opts);
        bitmapwhiteRightClick = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiterightclick, opts);
        bitmapwhiteRightClickNone = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiterightclicknone, opts);
        bitmapblackLeftClick = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackleftclick, opts);
        bitmapblackLeftClickNone = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackleftclicknone, opts);
        bitmapblackRightClick = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackrightclick, opts);
        bitmapblackRightClickNone = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackrightclicknone, opts);

        bitmapblackleftcircle = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackleftcircle, opts);
        bitmapblackrightcircle = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackrightcircle, opts);
        bitmapwhiteleftcircle = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiteleftcircle, opts);
        bitmapwhiterightcircle = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiterightcircle, opts);

        bitmapBlackLeftShort = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackleftshort, opts);
        bitmapBlackRightShort = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallblackrightshort, opts);
        bitmapWhiteLeftShort = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiteleftshort, opts);
        bitmapWhiteRightShort = BitmapFactory.decodeResource(getResources(), R.drawable.waterfallwhiterightshort, opts);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        int W = mDisplayMetrics.widthPixels;
        int H = mDisplayMetrics.heightPixels;
        if (H <= 1000)
            STEP = 8;
    }

    @SuppressLint("WrongCall")
    synchronized void draw() {

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

    public void cleann() {
        // 清除cacheCanvas图像
        // 绘制背景
        shutters.clear();
        draw();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG, "onMeasure");
        int widthnew = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        STEP = height / 15;

        int screenwidth = MeasureSpec.getSize(widthMeasureSpec);
        int screenheight = MeasureSpec.getSize(heightMeasureSpec);
        WhiteKeyWidth = (float) (screenwidth / (3.0 + KeysPerOctave * MaxOctave));

        if (width != widthnew) {
            Log.i(TAG, "jinru");
            width = widthnew;
            KEY_WIDTH = WhiteKeyWidth;
            setMeasuredDimension(width, height);
            if (bufferBitmap != null) {
                bufferBitmap.recycle();
                bufferBitmap = null;
            }
            bufferBitmap = Bitmap
                    .createBitmap(width, height, Bitmap.Config.RGB_565);
            bufferCanvas = new Canvas(bufferBitmap);
        }
    }

    @Override
    protected void onSizeChanged(int newwidth, int newheight, int oldwidth,
                                 int oldheight) {
        super.onSizeChanged(newwidth, newheight, oldwidth, oldheight);
    }

    Bitmap bmptemp;
    RectF rectfnew;
    Rect srctemp;

    protected synchronized void onDraw(Canvas canvas) {
        long starttime = SystemClock.uptimeMillis();
        if (!surfaceReady || bufferBitmap == null) {
            return;
        }

        float bottom_line_y = height - 3 * STEP;

        // 绘制背景
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
//		if(bitmapbackground != null) {
//			Rect dst = new Rect(0, 0, width, height);
//			Rect src = getBitmapSize(bitmapbackground);
//			bufferCanvas.drawBitmap(bitmapbackground, src, dst, paint);
//		}
        // 绘制背景
        paint.setColor(0xff1c2a3c);
        Rect dst = new Rect(0, 0, width, height);
        bufferCanvas.drawRect(dst, paint);
        // 绘制线条
        paint.setColor(0xff294260);
        paint.setStyle(Paint.Style.STROKE);
        float xextra = WhiteKeyWidth * 2;
        float ystart = 0;
        float yend = height;
        paint.setStrokeWidth(1.0f);
        for (int i = 0; i < 7; i++) {
            float xstart = i * 7 * WhiteKeyWidth + xextra;
            bufferCanvas.drawLine(xstart, ystart, xstart, yend, paint);
            xstart = xstart + 3 * WhiteKeyWidth;
            bufferCanvas.drawLine(xstart, ystart, xstart, yend, paint);
            if (i == 6) {
                xstart = xstart + 4 * WhiteKeyWidth;
                bufferCanvas.drawLine(xstart, ystart, xstart, yend, paint);
            }
        }

        // 测试代码
//		for(int i = 0; i < 52; i ++)
//		{
//			paint.setColor(0xffcccccc);
//			paint.setStrokeWidth(1.0f);
//			bufferCanvas.drawLine(i * WhiteKeyWidth, 0, i*WhiteKeyWidth + 1, height, paint);
//		}

        paint.setColor(0xffcccccc);
        paint.setStrokeWidth((float) (KEY_WIDTH * 0.4));
        paint.setStyle(Paint.Style.STROKE);
        if (shutters.size() > 0) {
            for (int i = 0; i < shutters.size(); i++) {
                if (i < shutters.size()) {
                    WaterfallInfo dict = shutters.get(i);
                    if (dict != null) {
                        if (dict.pos != null) {
                            Log.i(TAG, "绘制瀑布流");
                            RectF pos = dict.pos;

                            //int color = dict.color;
                            boolean blackkey = dict.blackkey;
                            //paint.setColor(color);
                            paint.setStyle(Paint.Style.FILL);
                            rectfnew = new RectF(pos);
                            bmptemp = bitmapBlackLeft;
                            //srctemp = new Rect();
                            if (pos.bottom - pos.top <= WhiteKeyWidth) {
                                if (dict.staff == 1) {
                                    if (blackkey) {
                                        rectfnew.top = rectfnew.bottom - 0.7f * WhiteKeyWidth;
                                        bmptemp = bitmapblackrightcircle;
                                    } else
                                        bmptemp = bitmapwhiterightcircle;
                                } else {
                                    if (blackkey) {
                                        rectfnew.top = rectfnew.bottom - 0.7f * WhiteKeyWidth;
                                        bmptemp = bitmapblackleftcircle;
                                    } else
                                        bmptemp = bitmapwhiteleftcircle;
                                }
                            } else {
                                boolean bflagshort = false;
                                if (pos.bottom - pos.top >= 100)
                                    bflagshort = false;
                                else
                                    bflagshort = true;
                                if (dict.staff == 1) {
                                    if (blackkey) {
                                        if (bflagshort)
                                            bmptemp = bitmapBlackRightShort;
                                        else
                                            bmptemp = bitmapBlackRight;
                                    } else {
                                        if (bflagshort)
                                            bmptemp = bitmapWhiteRightShort;
                                        else
                                            bmptemp = bitmapWhiteRight;
                                    }
                                } else {
                                    if (blackkey) {
                                        if (bflagshort)
                                            bmptemp = bitmapBlackLeftShort;
                                        else
                                            bmptemp = bitmapBlackLeft;
                                    } else {
                                        if (bflagshort)
                                            bmptemp = bitmapWhiteLeftShort;
                                        else
                                            bmptemp = bitmapWhiteLeft;
                                    }
                                }
                            }
                            srctemp = getBitmapSize(bmptemp);
                            //				bufferCanvas.drawBitmap(bmptemp, srctemp, rectfnew, paint);
                            //				if(blackkey)
                            //					bufferCanvas.drawRoundRect(rectfnew, WhiteKeyWidth / 4, WhiteKeyWidth / 4, paint);
                            //				else
                            //					bufferCanvas.drawRoundRect(rectfnew, WhiteKeyWidth / 2, WhiteKeyWidth/2, paint);
                            //bufferCanvas.drawRoundRect(rectfnew, 10, 10, paint);
                            if (dict.staff == 1)//right
                            {
                                Shader mShader = new LinearGradient(rectfnew.left, rectfnew.top, rectfnew.right, rectfnew.bottom,
                                        new int[]{0xff27949a, 0xff79fed2}, null, Shader.TileMode.REPEAT); // 一个材质,打造出一个线性梯度沿著一条线。
                                paint.setShader(mShader);
                                bufferCanvas.drawRoundRect(rectfnew, WhiteKeyWidth / 2, WhiteKeyWidth / 2, paint);
                            } else if (dict.staff == 2) {
                                Shader mShader = new LinearGradient(rectfnew.left, rectfnew.top, rectfnew.right, rectfnew.bottom,
                                        new int[]{0xff2a92cb, 0xff8c7bfe}, null, Shader.TileMode.REPEAT); // 一个材质,打造出一个线性梯度沿著一条线。
                                paint.setShader(mShader);
                                bufferCanvas.drawRoundRect(rectfnew, WhiteKeyWidth / 2, WhiteKeyWidth / 2, paint);
                            }

                            paint.setShader(null);
                            paint.setStyle(Paint.Style.STROKE);

                            //	    		if(blackkey)
                            //	    		{
                            //	    			paint.setStyle(Paint.Style.FILL);
                            //	    			paint.setColor(Color.GRAY);
                            //	    			RectF rectnew  = new RectF();
                            //	    			rectnew.left = pos.left + 3;
                            //	    			rectnew.top = pos.top + 3;
                            //	    			rectnew.right = pos.right - 3;
                            //	    			rectnew.bottom = pos.bottom - 3;
                            //	    			RectF outerrect = new RectF(rectnew);
                            //	    			bufferCanvas.drawRoundRect(outerrect, 10, 10, paint);
                            //	    			paint.setStyle(Paint.Style.STROKE);
                            //	    		}

                            if (pos.bottom >= height) {
                                float length = 0;
                                paint.setStyle(Paint.Style.STROKE);
                                rectfnew = new RectF(pos);
                                rectfnew.top = height * 17 / 20;
                                rectfnew.bottom = height;
                                if (blackkey) {
                                    length = 0.75f * WhiteKeyWidth * 0.5f;
                                } else {
                                    length = WhiteKeyWidth;
                                }
                                rectfnew.left -= length;
                                rectfnew.right += length;
                                bmptemp = bitmapblackLeftClickNone;
                                //Rect src = new Rect();
                                if (dict.staff == 1) {
                                    if (pos.top > height * 17 / 20) {
                                        if (blackkey)
                                            bmptemp = bitmapblackRightClickNone;
                                        else
                                            bmptemp = bitmapwhiteRightClickNone;
                                    } else {
                                        if (blackkey)
                                            bmptemp = bitmapblackRightClick;
                                        else
                                            bmptemp = bitmapwhiteRightClick;
                                    }
                                } else {
                                    if (pos.top > height * 17 / 20) {
                                        if (blackkey) {
                                            bmptemp = bitmapblackLeftClickNone;
                                        } else {
                                            bmptemp = bitmapwhiteLeftClickNone;
                                        }
                                    } else {
                                        if (blackkey)
                                            bmptemp = bitmapblackLeftClick;
                                        else
                                            bmptemp = bitmapwhiteLeftClick;
                                    }
                                }
                                srctemp = getBitmapSize(bmptemp);
                                bufferCanvas.drawBitmap(bmptemp, srctemp, rectfnew, paint);
                            }
                        }
                    }
                }
            }
        }
        canvas.drawBitmap(bufferBitmap, 0, 0, paint);
        long endtime = SystemClock.uptimeMillis();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        surfaceReady = true;
        draw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        draw();
        Log.i(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        surfaceReady = false;
    }

    public synchronized void setShutters(ArrayList<WaterfallInfo> shuttersnew) {
        //this.shutters = shutters;
        if (shutters != null) {
            shutters.clear();
        }
        shutters = shuttersnew;
        handler.sendEmptyMessage(0);
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
}
