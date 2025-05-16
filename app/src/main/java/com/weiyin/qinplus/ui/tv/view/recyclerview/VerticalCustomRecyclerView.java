package com.weiyin.qinplus.ui.tv.view.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class VerticalCustomRecyclerView extends RecyclerView {

	public VerticalCustomRecyclerView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public VerticalCustomRecyclerView(Context context, AttributeSet attrs,
									  int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public VerticalCustomRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void smoothScrollBy(int dx, int dy) {
		// TODO Auto-generated method stub
		if (dy > 0) {
			super.scrollBy(dx, dy + 100);// jjj125 0
		} else if (dy < 0) {
			super.scrollBy(dx, dy - 100);// jjj125 0
		} else {
			super.scrollBy(dx, dy);// jjj125 0
		}

	}

}
