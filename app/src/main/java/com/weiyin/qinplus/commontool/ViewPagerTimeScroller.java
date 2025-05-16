package com.weiyin.qinplus.commontool;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : ViewPagerTimeScroller
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class ViewPagerTimeScroller extends Scroller {

    private int mDuration = 500;

    public ViewPagerTimeScroller(Context context) {
        super(context);
    }

    public ViewPagerTimeScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }


    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }


    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    public void setmDuration(int itime) {
        mDuration = itime;
    }

}
