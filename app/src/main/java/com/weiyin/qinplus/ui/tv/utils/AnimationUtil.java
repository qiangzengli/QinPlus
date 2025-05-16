package com.weiyin.qinplus.ui.tv.utils;

import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.WinYinPianoApplication;


/**
 * @author linan1 2013年11月15日 下午7:02:58
 */
public class AnimationUtil {

	/**
	 * 播放放大动画
	 * 
	 * @param view
	 */
	public static AnimatorSet startScaleToBigAnimation(final View view, float rate, AnimatorListener animatorListener) {
		if (null != view.getAnimation()) {
			
			view.getAnimation().cancel();
		}
		view.clearAnimation();
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator oa = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, rate);
		oa.setDuration(240);
		oa.setInterpolator(new DecelerateInterpolator());

		ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, rate);
		oa1.setDuration(240);
		oa1.setInterpolator(new DecelerateInterpolator());
		set.playTogether(oa, oa1);
		if (null != animatorListener) {
			set.addListener(animatorListener);
		}

		set.start();

		return set;

	}
	
	public static AnimatorSet startScaleToBigAnimation(final View view, float rate, AnimatorListener animatorListener , long duration) {
		if (null != view.getAnimation()) {
			
			view.getAnimation().cancel();
		}
		view.clearAnimation();
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator oa = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, rate);
		oa.setDuration(duration);
		oa.setInterpolator(new DecelerateInterpolator());

		ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, rate);
		oa1.setDuration(duration);
		oa1.setInterpolator(new DecelerateInterpolator());
		set.playTogether(oa, oa1);
		if (null != animatorListener) {
			set.addListener(animatorListener);
		}

		set.start();
		return set;
	}

	/**
	 * 播放缩小动画
	 * 
	 * @param view
	 */
	public static AnimatorSet startScaleToSmallAnimation(View view, float rate, AnimatorListener animatorListener) {
		if (null != view.getAnimation()) {
			view.getAnimation().cancel();
		}
		view.clearAnimation();
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator oa = ObjectAnimator.ofFloat(view, "scaleY", rate, 1.0f);
		oa.setDuration(140);
		oa.setInterpolator(new DecelerateInterpolator());

		ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", rate, 1.0f);
		oa1.setDuration(140);
		oa1.setInterpolator(new DecelerateInterpolator());

		set.playTogether(oa, oa1);
		set.start();
		return set;
	}
	
	public static AnimatorSet startScaleToSmallAnimation(View view, float rate, AnimatorListener animatorListener, long duration) {
		if (null != view.getAnimation()) {
			view.getAnimation().cancel();
		}
		view.clearAnimation();
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator oa = ObjectAnimator.ofFloat(view, "scaleY", rate, 1.0f);
		oa.setDuration(duration);
		oa.setInterpolator(new DecelerateInterpolator());

		ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", rate, 1.0f);
		oa1.setDuration(duration);
		oa1.setInterpolator(new DecelerateInterpolator());

		set.playTogether(oa, oa1);
		set.start();
		return set;
	}

	public static void startScaleBigAnimation(View v) {
		startScaleToBigAnimation(v, 1.05f, null);
	}
	public static void startScaleSmallAnimation(View v) {
		startScaleToSmallAnimation(v, 1.05f, null);
	}

	public static void startRotateAnimation(View v, int duration) {
		ObjectAnimator oa = ObjectAnimator.ofFloat(v, "rotation", 0f, 360f);
		oa.setDuration(duration);
		oa.setInterpolator(null);
		oa.setRepeatCount(ValueAnimator.INFINITE);
		oa.start();
	}

	public static void clearAnimation(View v) {
		if (null == v) {
			return;
		}

		if (null != v.getAnimation()) {
			v.getAnimation().cancel();
			v.clearAnimation();
		}
	}

	/**
	 * 向上移动动画
	 * 
	 * @param view
	 * @return
	 */
	public static AnimatorSet startTranlateUpAnimation(final View view) {
		if (null != view.getAnimation()) {
			view.getAnimation().cancel();
		}
		view.clearAnimation();
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator oa = ObjectAnimator.ofFloat(view, "translationY", 1.0f, -80.0f);
		oa.setDuration(240);
		oa.setInterpolator(new DecelerateInterpolator());

		set.play(oa);
		set.start();

		return set;
	}

	/**
	 * 向下移动动画
	 * 
	 * @param view
	 * @return
	 */
	public static AnimatorSet startTranlateDownAnimation(final View view) {
		if (null != view.getAnimation()) {
			view.getAnimation().cancel();
		}
		view.clearAnimation();
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator oa = ObjectAnimator.ofFloat(view, "translationY", -80.0f, 1.0f);
		oa.setDuration(240);
		oa.setInterpolator(new DecelerateInterpolator());

		set.play(oa);
		set.start();

		return set;
	}
	
	/**
	 * X轴移动动画
	 * 
	 */
	public static AnimatorSet startTranlateRightAnimation(final View view, final float start, final float end, final int duration) {
		if (null != view.getAnimation()) {
			view.getAnimation().cancel();
		}
		view.clearAnimation();
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator oa = ObjectAnimator.ofFloat(view, "translationX", start, end);
		oa.setDuration(duration);
		oa.setInterpolator(new DecelerateInterpolator());
		set.play(oa);
		set.start();
		return set;
	}

	public static void fadeIn(View view) {
		if (view.getAnimation() != null) {
			view.getAnimation().cancel();
		}
		view.clearAnimation();
		Animation fadeIn = AnimationUtils.loadAnimation(WinYinPianoApplication.getInstance(), R.anim
				.fade_in);
		view.setVisibility(View.VISIBLE);
		view.startAnimation(fadeIn);
	}
	public static void fadeOut(View view) {
		if (view.getAnimation() != null) {
			view.getAnimation().cancel();
		}
		view.clearAnimation();
		Animation fadeOut = AnimationUtils.loadAnimation(WinYinPianoApplication.getInstance(), R.anim
				.fade_out);
		view.setVisibility(View.GONE);
		view.startAnimation(fadeOut);
	}
}
