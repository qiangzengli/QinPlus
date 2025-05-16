package com.weiyin.qinplus.ui.tv.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.ui.tv.utils.AnimationUtil;


/**
 * 获得焦点后凸显特效
 * @author chailiwei
 * @since 2015.01.08 13:40
 */
@SuppressLint("NewApi") public class AdapterMetroView extends RelativeLayout {
	
	private Rect mBound;
	private Drawable mDrawable;
	private Rect mRect;
	private AnimatorSet scaleAnimation;
	private int shadowWidth=43;
	private int topShadowWidth;
	private int bottomShadowWidth;
	private int leftShadowWidth;
	private int rightShadowWidth;
	private Context context;
	private static final int refresh = 40;
	private boolean isRefresh1 = false;
	private boolean hasAnim = true;
	private boolean isChildSelected=false;
	private String imagetype = "false";

	@SuppressLint("HandlerLeak")
	private Handler mStartHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case refresh:
				if(isRefresh1){
					getRootView().invalidate();
					mStartHandler.sendEmptyMessageDelayed(refresh, 10);
				}
				break;
				default:
					break;
			}
		};
	};

	public AdapterMetroView(Context context) {
		super(context);
		init(context, null);
	}

	public AdapterMetroView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public AdapterMetroView(Context context, AttributeSet attrs) {
		super(context, attrs);
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
			shadowWidth = a.getDimensionPixelSize(R.styleable.MetroView_shadowingWidth, getResources().getDimensionPixelOffset(R.dimen.dp_2));
			hasAnim = a.getBoolean(R.styleable.MetroView_hasAnim, true);
			isChildSelected=a.getBoolean(R.styleable.MetroView_isChildSelected, false);
			imagetype = a.getString(R.styleable.MetroView_imagetype);
			if(a.getDimensionPixelSize(R.styleable.MetroView_shadowingWidth, getResources().getDimensionPixelOffset(R.dimen.dp_2)) == getResources().getDimensionPixelOffset(R.dimen.dp_2)){
				topShadowWidth = a.getDimensionPixelSize(R.styleable.MetroView_topShadowingWidth, getResources().getDimensionPixelOffset(R.dimen.dp_2));
				bottomShadowWidth = a.getDimensionPixelSize(R.styleable.MetroView_bottomShadowingWidth, getResources().getDimensionPixelOffset(R.dimen.dp_2));
				leftShadowWidth = a.getDimensionPixelSize(R.styleable.MetroView_leftShadowingWidth, getResources().getDimensionPixelOffset(R.dimen.dp_2));
				rightShadowWidth = a.getDimensionPixelSize(R.styleable.MetroView_rightShadowingWidth, getResources().getDimensionPixelOffset(R.dimen.dp_2));
			}else{
				topShadowWidth = shadowWidth;
				bottomShadowWidth = shadowWidth;
				leftShadowWidth = shadowWidth;
				rightShadowWidth = shadowWidth;
			}
			a.recycle(); 
		}
		if(mDrawable==null){
			if (imagetype != null)
			{
				if (imagetype.equals("fillet"))
				{
//					mDrawable = getResources().getDrawable(R.drawable.mainlayoutfocus);
				}else{
//					mDrawable = getResources().getDrawable(R.drawable.bookselectborder);// nav_focused_2,poster_shadow_4
				}
			}else{
//				mDrawable = getResources().getDrawable(R.drawable.bookselectborder);// nav_focused_2,poster_shadow_4
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
		super.draw(canvas);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (hasFocus()&&mDrawable!=null) {
			super.getDrawingRect(mRect);
			mBound.set(-leftShadowWidth+mRect.left, -topShadowWidth+mRect.top, rightShadowWidth+mRect.right, bottomShadowWidth+mRect.bottom);
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
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		if(isChildSelected){
			if(gainFocus){
				for(int i=0;i<this.getChildCount();i++){
					this.getChildAt(i).setSelected(true);
				}
			}else{
				for(int i=0;i<this.getChildCount();i++){
					this.getChildAt(i).setSelected(false);
				}
			}
		}
		if (null != scaleAnimation) {
			scaleAnimation.cancel();
			scaleAnimation = null;
		}
		
		if (gainFocus) {
			//bringToFront();
			getRootView().requestLayout();
			getRootView().invalidate();
			if(hasAnim){
				scaleAnimation = AnimationUtil.startScaleToBigAnimation(this, Constant
						.SCALE_RATE, new AnimatorListener() {
					
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
			
			
		} else {
			if(hasAnim){
				scaleAnimation = AnimationUtil.startScaleToSmallAnimation(this, Constant.SCALE_RATE, null);
			}
		}
	}
	
}