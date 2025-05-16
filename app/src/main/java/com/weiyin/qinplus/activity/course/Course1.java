package com.weiyin.qinplus.activity.course;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.thinkcool.circletextimageview.CircleTextImageView;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.entity.CourseEntity;
import com.weiyin.qinplus.entity.KnowledgeEntity;
import com.weiyin.qinplus.listener.CourseInterface;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;
import com.weiyin.qinplus.ui.tv.view.RoundProgressBar;

import java.io.IOException;

/**
 * @author njf
 * @date 2017/7/24
 */

public class Course1 extends AbstractCourse implements View.OnTouchListener {
    private View rootView;

    private ClickAble clickAble;

    private Activity activity;

    private TextView course1TabNumberTextView, course1LayoutTitleTextView, course1LayoutContentTextView;
    private ImageView course1LayoutImageView, course1LayoutPlay;

    private RelativeLayout course1LayoutLastKnowledgeRl, course1LayoutNextKnowledgeRl, course1LayoutPracticeRl;

    private CourseEntity courseEntity;
    private int index;

    private MediaPlayer mediaPlayer;

    private RoundProgressBar course1LayoutProgressBar;

    private boolean playIng = false, start = false;

    private Handler mHandler = new Handler();
    private AnimationDrawable blacktopAnimation;

    @SuppressLint("InflateParams")
    @Override
    public void onCreate(Activity activity, ViewFlipper contain, CourseEntity courseEntities, int index) {
        rootView = LayoutInflater.from(activity).inflate(R.layout.course1_layout, null);
        LayoutHelper layoutHelper = new LayoutHelper(activity);
        layoutHelper.scaleView(rootView);
        this.activity = activity;
        this.courseEntity = courseEntities;
        this.index = index;
        Log.i(TAG, "index" + index);
        start = false;
        mediaPlayer = new MediaPlayer();
        contain.addView(rootView);
        clickAble = new ClickAble();
        initView();
    }

    @Override
    public void initView() {
        course1TabNumberTextView = (TextView) rootView.findViewById(R.id.course1TabNumberTextView);
        course1LayoutTitleTextView = (TextView) rootView.findViewById(R.id.course1LayoutTitleTextView);
        course1LayoutContentTextView = (TextView) rootView.findViewById(R.id.course1LayoutContentTextView);
        CircleTextImageView course1LayoutAllPractice = (CircleTextImageView) rootView.findViewById(R.id.course1LayoutAllPractice);

        course1LayoutImageView = (ImageView) rootView.findViewById(R.id.course1LayoutImageView);
        course1LayoutPlay = (ImageView) rootView.findViewById(R.id.course1LayoutPlay);

        course1LayoutProgressBar = (RoundProgressBar) rootView.findViewById(R.id.course1LayoutProgressBar);

        course1LayoutLastKnowledgeRl = (RelativeLayout) rootView.findViewById(R.id.course1LayoutLastKnowledgeRl);
        course1LayoutNextKnowledgeRl = (RelativeLayout) rootView.findViewById(R.id.course1LayoutNextKnowledgeRl);
        course1LayoutPracticeRl = (RelativeLayout) rootView.findViewById(R.id.course1LayoutPracticeRl);

        course1LayoutLastKnowledgeRl.setOnClickListener(clickAble);
        course1LayoutNextKnowledgeRl.setOnClickListener(clickAble);
        course1LayoutPracticeRl.setOnClickListener(clickAble);
        course1LayoutPlay.setOnClickListener(clickAble);
        course1LayoutAllPractice.setOnClickListener(clickAble);
    }

    @SuppressLint("SetTextI18n")
    private void initViewData() {
        if (courseEntity != null) {
            course1LayoutLastKnowledgeRl.setVisibility(index == 0 ? View.GONE : View.VISIBLE);
            course1LayoutNextKnowledgeRl.setVisibility(index == courseEntity.getKnowledge().size() - 1 ? View.GONE : View.VISIBLE);
            if (index < courseEntity.getKnowledge().size()) {
                KnowledgeEntity knowledgeEntity = courseEntity.getKnowledge().get(index);
                int position = -1;
                for (int i = 0; i < courseEntity.getPractice().size(); i++) {
                    if (knowledgeEntity.getId().equals(courseEntity.getPractice().get(i).getKnowPointId())) {
                        position = courseEntity.getKnowledge().size() + i;
                    }
                }
                Log.i(TAG, "position" + position);
                if (position == -1) {
                    course1LayoutPracticeRl.setVisibility(View.GONE);
                } else {
                    course1LayoutPracticeRl.setVisibility(View.VISIBLE);
                }
                course1TabNumberTextView.setText(knowledgeEntity.getId() + "/" + courseEntity.getKnowledge().size());
                course1LayoutTitleTextView.setText(knowledgeEntity.getTitle());
                course1LayoutContentTextView.setText(knowledgeEntity.getText());
                Log.i(TAG, WinYinPianoApplication.strUrl + Constant.PRACTICE_PATH + knowledgeEntity.getImg() + "   index=" + index);
                Glide.with(activity).load(WinYinPianoApplication.strUrl + Constant.PRACTICE_PATH + knowledgeEntity.getImg()).into(course1LayoutImageView);
            }
        }
    }

    @Override
    public void setCourseInterface(CourseInterface courseInterface) {
        this.courseInterface = courseInterface;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    /**
     * update进度条
     */
    private Runnable updateThread = new Runnable() {
        @Override
        public void run() {
            try {
                if (mediaPlayer != null) {
                    if (mediaPlayer.getCurrentPosition() == mediaPlayer.getDuration()) {
                        return;
                    }
                    course1LayoutProgressBar.setProgress(mediaPlayer.getCurrentPosition());
/* Log.i(TAG,"线程时间="+mediaPlayer.getCurrentPosition()); */
                }
                mHandler.removeCallbacks(updateThread);
                mHandler.postDelayed(updateThread, 50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    class ClickAble implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();

            if (id == R.id.course1LayoutAllPractice) {
                pause();
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                courseInterface.courseInterfaceResult(Constant.TOTAL_PRACTICE, courseEntity.getPractice().size());
            } else if (id == R.id.course1LayoutLastKnowledgeRl) {
                pause();
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                courseInterface.courseInterfaceResult(Constant.LAST_KNOWLEDGE, index - 1);
            } else if (id == R.id.course1LayoutNextKnowledgeRl) {
                pause();
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                courseInterface.courseInterfaceResult(Constant.NEXT_KNOWLEDGE, index + 1);
            } else if (id == R.id.course1LayoutPracticeRl) {
                pause();
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                KnowledgeEntity knowledgeEntity = courseEntity.getKnowledge().get(index);
                int position = -1;
                for (int i = 0; i < courseEntity.getPractice().size(); i++) {
                    if (knowledgeEntity.getId().equals(courseEntity.getPractice().get(i).getKnowPointId())) {
                        position = courseEntity.getKnowledge().size() + i;
                    }
                }
                Log.i(TAG, "position" + position);
                if (position == -1) {
                    ToastUtil.showTextToast(activity, "本知识点没有练习题");
                } else {
                    rootView.setVisibility(View.GONE);
                    courseInterface.courseInterfaceResult(Constant.NEXT_KNOWLEDGE, position);
                }
            } else if (id == R.id.course1LayoutPlay) {
                play();
            }

        }
    }

    private void play() {
        if (playIng) {
            pause();
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } else {
            if (!start) {
                try {
                    mediaPlayer.reset();
                    Log.i(TAG, Constant.DOWN_PATH + Constant.PRACTICE_PATH + courseEntity.getKnowledge().get(index).getAudio());
                    mediaPlayer.setDataSource(Constant.DOWN_PATH + Constant.PRACTICE_PATH + courseEntity.getKnowledge().get(index).getAudio());
                    mediaPlayer.prepare();

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            pause();
                            course1LayoutProgressBar.setProgress(0);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "getDuration=" + mediaPlayer.getDuration());

                course1LayoutProgressBar.setMax(mediaPlayer.getDuration());
                start = true;
            }

            course1LayoutPlay.setImageResource(R.drawable.audio_pause_bg);
            blacktopAnimation = new AnimationDrawable();
            blacktopAnimation = (AnimationDrawable) course1LayoutPlay.getDrawable();
            blacktopAnimation.start();
            playIng = true;
            mediaPlayer.start();
            mHandler.post(updateThread);
        }
    }

    private void pause() {
        Log.i(TAG, "pause");
        playIng = false;
        if (blacktopAnimation != null) {
            blacktopAnimation.stop();
        }
        course1LayoutPlay.setImageResource(R.drawable.audio_play);
        mHandler.removeCallbacks(updateThread);
    }

    @Override
    public void show(CourseEntity courseEntities, int index, String type) {
        rootView.setVisibility(View.VISIBLE);
        initViewData();
    }

    @Override
    public void stop() {
        pause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void destroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (blacktopAnimation != null) {
            blacktopAnimation = null;
        }
    }
}
