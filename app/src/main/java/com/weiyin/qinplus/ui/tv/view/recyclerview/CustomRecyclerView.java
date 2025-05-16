package com.weiyin.qinplus.ui.tv.view.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 横向滚动RecyclerView
 * 
 * @author chailiwei
 * 
 */
public class CustomRecyclerView extends RecyclerView {

	public CustomRecyclerView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CustomRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void smoothScrollBy(int dx, int dy) {
		if (dx > 0) {
			super.scrollBy(dx + 100, dy);// jjj125 0
		} else if (dx < 0) {
			super.scrollBy(dx - 100, dy);// jjj125 0
		} else {
			super.scrollBy(dx, dy);// jjj125 0
		}
	}
}
