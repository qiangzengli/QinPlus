package com.weiyin.qinplus.ui.tv.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.ui.tv.utils.AnimationUtil;


/**
 * 获得焦点后凸显特效
 * 
 * @author chailiwei
 * @since 2015.01.08 13:40
 */
@SuppressLint("NewApi")
public class MetroView extends RelativeLayout {

	private Rect mBound;
	private Drawable mDrawable;
	private Rect mRect;
	private int bgColor;
	private AnimatorSet scaleAnimation;
	private int shadowWidth = 43;
	private int topShadowWidth;
	private int bottomShadowWidth;
	private int leftShadowWidth;
	private int rightShadowWidth;
	private static final int refresh = 40;
	private boolean hasAnim = true;
	private boolean isRefresh1 = false;
	private OnItemFocusChangedListener onItemFocusChangedListener;
	private Context context;
	private String imagetype = "false";
	
	private Handler mStartHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case refresh:
				if(isRefresh1){
					//Log.i("isRefresh", "refresh");
					getRootView().invalidate();
					mStartHandler.sendEmptyMessageDelayed(refresh, 10);
				}
				break;
			}
		};
	};

	public void setOnItemFocusChangedListener(
			OnItemFocusChangedListener onItemFocusChangedListener) {
		this.onItemFocusChangedListener = onItemFocusChangedListener;
	}

	public MetroView(Context context) {
		super(context);
		init(context, null);
	}

	public MetroView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public MetroView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(isInEditMode()){
			return;
		}
		init(context, attrs);
	}

	protected void init(Context context, AttributeSet attrs) {
		this.context = context;
		setWillNotDraw(false);
		mRect = new Rect();
		mBound = new Rect();
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MetroView);
			// Get the provided drawable from the XML
			mDrawable = a.getDrawable(R.styleable.MetroView_android_src);
			shadowWidth = a.getDimensionPixelSize(R.styleable.MetroView_shadowingWidth, getResources().getDimensionPixelOffset(R.dimen.dp_4));
			bgColor = a.getColor(R.styleable.MetroView_bgcolor, -1);
			hasAnim = a.getBoolean(R.styleable.MetroView_hasAnim, true);
			imagetype = a.getString(R.styleable.MetroView_imagetype);
			if(a.getDimensionPixelSize(R.styleable.MetroView_shadowingWidth, getResources().getDimensionPixelOffset(R.dimen.dp_4)) == getResources().getDimensionPixelOffset(R.dimen.dp_4)){
				topShadowWidth = a.getDimensionPixelSize(R.styleable.MetroView_topShadowingWidth, getResources().getDimensionPixelOffset(R.dimen.dp_4));
				bottomShadowWidth = a.getDimensionPixelSize(R.styleable.MetroView_bottomShadowingWidth, getResources().getDimensionPixelOffset(R.dimen.dp_4));
				leftShadowWidth = a.getDimensionPixelSize(R.styleable.MetroView_leftShadowingWidth, getResources().getDimensionPixelOffset(R.dimen.dp_4));
				rightShadowWidth = a.getDimensionPixelSize(R.styleable.MetroView_rightShadowingWidth, getResources().getDimensionPixelOffset(R.dimen.dp_4));
			}else{
				topShadowWidth = shadowWidth;
				bottomShadowWidth = shadowWidth;
				leftShadowWidth = shadowWidth;
				rightShadowWidth = shadowWidth;
			}
			a.recycle();
		}
		if (mDrawable == null) {
			if (imagetype != null)
			{
				if (imagetype.equals("fillet"))
				{
//					mDrawable = getResources().getDrawable(R.drawable.mainyoutcircular);
				}else{
//					mDrawable = getResources().getDrawable(R.drawable.mainlayoutfocus);// nav_focused_2,poster_shadow_4
				}
			}else{
//				mDrawable = getResources().getDrawable(R.drawable.mainlayoutfocus);// nav_focused_2,poster_shadow_4
			}

		}
		setChildrenDrawingOrderEnabled(true);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	public void draw(Canvas canvas) {
		if(!isInEditMode()){
			super.draw(canvas);
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		if(bgColor != -1){
			//获取了设置的背景颜色
			//画圆角矩形  
			super.getDrawingRect(mRect);
			Paint p = new Paint();
	        p.setStyle(Paint.Style.FILL);//充满
	        p.setColor(bgColor);  
	        p.setAntiAlias(true);// 设置画笔的锯齿效果  
	        RectF oval = new RectF(mRect.left, mRect.top, mRect.right, mRect.bottom);// 设置个新的长方形
	        canvas.drawRoundRect(oval, getResources().getDimension(R.dimen.dp_2), getResources().getDimension(R.dimen.dp_2), p);//第二个参数是x半径，第三个参数是y半径
		}
		if (hasFocus() && mDrawable != null) {
			//Log.i("isRefresh", "onDrawKeyBoard");
			super.getDrawingRect(mRect);
			mBound.set(-leftShadowWidth + mRect.left, -topShadowWidth + mRect.top,
					rightShadowWidth + mRect.right, bottomShadowWidth + mRect.bottom);
			mDrawable.setBounds(mBound);
			canvas.save();
			mDrawable.draw(canvas);
			canvas.restore();
			if(isRefresh1){
				mStartHandler.sendEmptyMessageDelayed(refresh, 10);
			}
		}
		super.onDraw(canvas);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		if (null != scaleAnimation) {
			scaleAnimation.cancel();
			scaleAnimation = null;
		}
		if (onItemFocusChangedListener != null) {
			onItemFocusChangedListener.onItemFocusChanged(this, gainFocus);
		}

		if (gainFocus) {
				bringToFront();
				getRootView().requestLayout();
				getRootView().invalidate();
				if(hasAnim){
				scaleAnimation = AnimationUtil.startScaleToBigAnimation(this,
						Constant.SCALE_RATE, new AnimatorListener() {
							
							@Override
							public void onAnimationStart(Animator arg0) {
								isRefresh1=true;
							}
							
							@Override
							public void onAnimationRepeat(Animator arg0) {
							}
							
							@Override
							public void onAnimationEnd(Animator arg0) {
								getRootView().invalidate();
								isRefresh1=false;
							}
							
							@Override
							public void onAnimationCancel(Animator arg0) {
							}
						});
				}
				View v = null;
				if (this.getParent() instanceof View) {
					v = (View) this.getParent();
				}
				while (v != null) {
					v.bringToFront();
					v.invalidate();
					if (v.getParent() instanceof View) {
						v = (View) v.getParent();
					} else {
						v = null;
					}
				}
		} else {
			if(hasAnim){
				scaleAnimation = AnimationUtil.startScaleToSmallAnimation(this,
						Constant.SCALE_RATE, null);
			}
		}
	}
	
	public void setbgColor(int bgColor){
		this.bgColor =  bgColor;
//		invalidate();
	}
	
	public void setHasAnim(boolean hasAnim){
		this.hasAnim = hasAnim;
	}

	public interface OnItemFocusChangedListener {
		public void onItemFocusChanged(View view, boolean hasFocus);
	}
}