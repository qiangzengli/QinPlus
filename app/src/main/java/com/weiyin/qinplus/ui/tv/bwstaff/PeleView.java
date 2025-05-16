package com.weiyin.qinplus.ui.tv.bwstaff;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.weiyin.qinplus.R;

import java.util.ArrayList;
/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 前奏的View
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class PeleView extends View {
    private static final String TAG = PeleView.class.getSimpleName();
    private int count = 0;
    private int width = 560;
    private int i = 0;
    private Context mContext;

    public ArrayList<Integer> list;

    public PeleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        list = new ArrayList<>();
    }

    public PeleView(Context context) {
        super(context);
        list = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        Log.i(TAG, "width=" + getWidth() + " height=" + getHeight());
        RectF daft = new RectF(getWidth() / 4, getHeight() / 4, getWidth() / 4 * 3, getHeight() / 4 * 3);
        p.setColor(getResources().getColor(R.color.peludeview_bg));
        canvas.drawRoundRect(daft, 10, 10, p);
        Bitmap bitmap;

        for (int i = 0; i < count; i++) {
            if (i == count) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.purple);
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.yellow);
            }
            if (list.size() > 0) {
                for (int j = 0; j < list.size(); j++) {
                    if (i == list.get(j)) {
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.frame);

                    }
                }
            }
            /* 第一个是图片,第二个是距离顶部距离,第三个是距离左边距离 */
            canvas.drawBitmap(bitmap, (getWidth() - 106 * count) / 2 + (i * 106), getHeight() / 2, p);
        }
    }

    public void addView(int count) {
        list.clear();
        this.count = count;
        i = count;
        width = 560;
        invalidate();
    }

    public void removeView() {
        i = i - 1;
        list.add(i);
        invalidate();
    }
}


