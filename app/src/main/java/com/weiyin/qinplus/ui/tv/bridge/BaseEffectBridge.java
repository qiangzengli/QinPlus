package com.weiyin.qinplus.ui.tv.bridge;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.weiyin.qinplus.ui.tv.view.MainUpView;


/**
 * Anim Bridge 抽象类. ----桥接模式.
 * 
 * @author hailongqiu 356752238@qq.com
 *
 */
public abstract class BaseEffectBridge {

	public abstract void onInitBridge(MainUpView view);

	/**
	 * 需要绘制的东西.
	 */
	public abstract boolean onDrawMainUpView(Canvas canvas);

	/**
	 * 老的焦点View.
	 */
	public abstract void onOldFocusView(View oldFocusView, float scaleX, float scaleY);

	/**
	 * 新的焦点View.
	 */
	public abstract void onFocusView(View focusView, float scaleX, float scaleY);

	/**
	 * 最上层移动的view.
	 */
	public void setMainUpView(MainUpView view) {
		
	}

	public MainUpView getMainUpView() {
		return null;
	}
	
	/**
	 * 设置边框图片
	 */
	
	public void setUpRectResource(int resId) {
		
	}
	
	public void setUpRectDrawable(Drawable upRectDrawable) {
		
	}
	
	public Drawable getUpRectDrawable() {
		return null;
	}
	
	public void setDrawUpRectPadding(Rect rect) {
		
	}
	
	public Rect getDrawUpRect() {
		return null;
	}
	
	/**
	 * 设置边框阴影
	 */
	
	public void setShadowResource(int resId) {
	}

	public Drawable getShadowDrawable() {
		return null;
	}

	public void setShadowDrawable(Drawable shadowDrawable) {
	}
	
	public void setDrawShadowRectPadding(Rect rect) {
	}
	
	public Rect getDrawShadowRect() {
		return null;
	}
	
}
