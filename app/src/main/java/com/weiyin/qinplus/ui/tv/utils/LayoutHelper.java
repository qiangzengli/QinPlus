package com.weiyin.qinplus.ui.tv.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.TextView;


/**
 * @author njf
 */
public class LayoutHelper {
    private final String TAG = LayoutHelper.class.getSimpleName();
    private float scaleX;
    private Activity activity;
    private float scaleY;

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public LayoutHelper(Activity activity) {
        this.activity = activity;
        init();
    }

    public void init() {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int orientation = activity.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            scaleX = size.x / 1024f;
            scaleY = size.y / 768f;
        } else {
            scaleX = size.x / 768f;
            scaleY = size.y / 1024f;
        }
    }

    public void scaleView(View view) {

        view.setPadding(scaleDimension(view.getPaddingLeft(), "x"), scaleDimension(view.getPaddingTop(), "y"),
                scaleDimension(view.getPaddingRight(), "x"), scaleDimension(view.getPaddingBottom(), "y"));

        ViewGroup.LayoutParams ll = view.getLayoutParams();

        if (ll != null) {
            ll.width = scaleDimension(ll.width, "x");
            ll.height = scaleDimension(ll.height, "y");

            if (ll instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) ll).leftMargin = scaleDimension(((ViewGroup.MarginLayoutParams) ll).leftMargin, "x");
                ((ViewGroup.MarginLayoutParams) ll).rightMargin = scaleDimension(((ViewGroup.MarginLayoutParams) ll).rightMargin, "x");
                ((ViewGroup.MarginLayoutParams) ll).topMargin = scaleDimension(((ViewGroup.MarginLayoutParams) ll).topMargin, "y");
                ((ViewGroup.MarginLayoutParams) ll).bottomMargin = scaleDimension(((ViewGroup.MarginLayoutParams) ll).bottomMargin, "y");
            }

            if (ll instanceof AbsoluteLayout.LayoutParams) {
                ((AbsoluteLayout.LayoutParams) ll).x = scaleDimension(((AbsoluteLayout.LayoutParams) ll).x, "x");
                ((AbsoluteLayout.LayoutParams) ll).y = scaleDimension(((AbsoluteLayout.LayoutParams) ll).y, "y");
            }
        }

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                scaleView(viewGroup.getChildAt(i));
            }
        }

        if (view instanceof TextView) {
            float fontsize = ((TextView) view).getTextSize();
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, scaleFontSize(fontsize));
        }
    }

    public int scaleDimension(int dim, String type) {
        if (dim == ViewGroup.LayoutParams.MATCH_PARENT || dim == ViewGroup.LayoutParams.WRAP_CONTENT) {
            return dim;
        } else {
            if (type.equals("x")) {
                return (int) (dim * scaleX);
            } else {
                return (int) (dim * scaleY);
            }
        }
    }

    public float scaleFontSize(float dim)

    {
        return dim * scaleX;
    }

    public View inflate(int id, LayoutInflater layoutInflater)

    {
        return layoutInflater.inflate(id, null, false);
    }

}
