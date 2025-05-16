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
 * @date 2017/8/1
 */

public class Course3 extends AbstractCourse implements View.OnTouchListener {


    private View rootView;

    private ClickAble clickAble;

    private Activity activity;

    private TextView course1TabNumberTextView, course1LayoutTitleTextView, course1LayoutContentTextView;
    private ImageView course3LayoutImageView, course3LayoutPlay1, course3LayoutPlay2;

    private RelativeLayout course1LayoutLastKnowledgeRl, course1LayoutNextKnowledgeRl;
    private RoundProgressBar course3LayoutPlay1ProgressBar, course3LayoutPlay2ProgressBar;
    private CourseEntity courseEntity;
    private int index;

    private MediaPlayer mediaPlayer1, mediaPlayer2;

    private boolean playIng1, playIng2, start1, start2;
    private AnimationDrawable blacktopAnimation1, blacktopAnimation2;
    private Handler mHandler = new Handler();


    @SuppressLint("InflateParams")
    @Override
    public void onCreate(Activity activity, ViewFlipper contain, CourseEntity courseEntities, int index) {
        rootView = LayoutInflater.from(activity).inflate(R.layout.course3_layout, null);
        LayoutHelper layoutHelper = new LayoutHelper(activity);
        layoutHelper.scaleView(rootView);
        this.activity = activity;
        this.courseEntity = courseEntities;
        this.index = index;
        Log.i(TAG, "index" + index);
        contain.addView(rootView);
        mediaPlayer1 = new MediaPlayer();
        mediaPlayer2 = new MediaPlayer();
        clickAble = new ClickAble();
        initView();
    }

    @Override
    public void initView() {
        course1TabNumberTextView = (TextView) rootView.findViewById(R.id.course1TabNumberTextView);
        course1LayoutTitleTextView = (TextView) rootView.findViewById(R.id.course1LayoutTitleTextView);
        course1LayoutContentTextView = (TextView) rootView.findViewById(R.id.course1LayoutContentTextView);
        CircleTextImageView course1LayoutAllPractice = (CircleTextImageView) rootView.findViewById(R.id.course1LayoutAllPractice);

        course3LayoutImageView = (ImageView) rootView.findViewById(R.id.course3LayoutImageView);
        course3LayoutPlay1 = (ImageView) rootView.findViewById(R.id.course3LayoutPlay1);
        course3LayoutPlay2 = (ImageView) rootView.findViewById(R.id.course3LayoutPlay2);
        course3LayoutPlay1ProgressBar = (RoundProgressBar) rootView.findViewById(R.id.course3LayoutPlay1ProgressBar);
        course3LayoutPlay2ProgressBar = (RoundProgressBar) rootView.findViewById(R.id.course3LayoutPlay2ProgressBar);
        course1LayoutLastKnowledgeRl = (RelativeLayout) rootView.findViewById(R.id.course1LayoutLastKnowledgeRl);
        course1LayoutNextKnowledgeRl = (RelativeLayout) rootView.findViewById(R.id.course1LayoutNextKnowledgeRl);
        RelativeLayout course1LayoutPracticeRl = (RelativeLayout) rootView.findViewById(R.id.course1LayoutPracticeRl);

        course1LayoutLastKnowledgeRl.setOnClickListener(clickAble);
        course1LayoutNextKnowledgeRl.setOnClickListener(clickAble);
        course1LayoutPracticeRl.setOnClickListener(clickAble);
        course3LayoutPlay1.setOnClickListener(clickAble);
        course3LayoutPlay2.setOnClickListener(clickAble);
        course1LayoutAllPractice.setOnClickListener(clickAble);
    }

    @SuppressLint("SetTextI18n")
    private void initViewData() {
        if (courseEntity != null) {
            course1LayoutLastKnowledgeRl.setVisibility(index == 0 ? View.GONE : View.VISIBLE);
            course1LayoutNextKnowledgeRl.setVisibility(index == courseEntity.getKnowledge().size() - 1 ? View.GONE : View.VISIBLE);
            if (index < courseEntity.getKnowledge().size()) {
                KnowledgeEntity knowledgeEntity = courseEntity.getKnowledge().get(index);
                course1TabNumberTextView.setText(knowledgeEntity.getId() + "/" + courseEntity.getKnowledge().size());
                course1LayoutTitleTextView.setText(knowledgeEntity.getTitle());
                course1LayoutContentTextView.setText(knowledgeEntity.getText());
                Log.i(TAG, knowledgeEntity.getImg());
                Glide.with(activity).load(WinYinPianoApplication.strUrl + Constant.PRACTICE_PATH + knowledgeEntity.getImg()).into(course3LayoutImageView);
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private class ClickAble implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.course1LayoutAllPractice) {
                pause1();
                pause2();
                courseInterface.courseInterfaceResult(Constant.TOTAL_PRACTICE, courseEntity.getPractice().size());
            } else if (id == R.id.course1LayoutLastKnowledgeRl) {
                pause1();
                pause2();
                courseInterface.courseInterfaceResult(Constant.LAST_KNOWLEDGE, index - 1);
            } else if (id == R.id.course1LayoutNextKnowledgeRl) {
                pause1();
                pause2();
                courseInterface.courseInterfaceResult(Constant.NEXT_KNOWLEDGE, index + 1);
            } else if (id == R.id.course1LayoutPracticeRl) {
                KnowledgeEntity knowledgeEntity = courseEntity.getKnowledge().get(index);
                int position = -1;
                for (int i = 0; i < courseEntity.getPractice().size(); i++) {
                    if (knowledgeEntity.getId().equals(courseEntity.getPractice().get(i).getKnowPointId())) {
                        position = courseEntity.getKnowledge().size() + i;
                        break;
                    }
                }
                Log.i(TAG, "position" + position);
                if (position == -1) {
                    ToastUtil.showTextToast(activity, "本知识点没有练习题");
                } else {
                    pause1();
                    pause2();
                    courseInterface.courseInterfaceResult(Constant.NEXT_KNOWLEDGE, position);
                }
            } else if (id == R.id.course3LayoutPlay1) {
                pause2();
                play1();
            } else if (id == R.id.course3LayoutPlay2) {
                pause1();
                play2();
            }
// default 情况无需处理，原switch中的default是空语句
        }
    }

    private void play1() {
        if (playIng1) {
            pause1();
        } else {
            if (!start1) {
                try {
                    mediaPlayer1.reset();
                    Log.i(TAG, Constant.DOWN_PATH + Constant.PRACTICE_PATH + courseEntity.getKnowledge().get(index).getAudio());
                    mediaPlayer1.setDataSource(Constant.DOWN_PATH + Constant.PRACTICE_PATH + courseEntity.getKnowledge().get(index).getAudio1());
                    mediaPlayer1.prepare();

                    mediaPlayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            playIng1 = false;
                            if (blacktopAnimation1 != null) {
                                blacktopAnimation1.stop();
                            }
                            course3LayoutPlay1.setImageResource(R.drawable.audio_play);
                            mHandler.removeCallbacks(updateThread1);
                            course3LayoutPlay1ProgressBar.setProgress(0);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "getDuration=" + mediaPlayer1.getDuration());
                course3LayoutPlay1ProgressBar.setMax(mediaPlayer1.getDuration());
                start1 = true;
            }

            course3LayoutPlay1.setImageResource(R.drawable.audio_pause_bg);
            blacktopAnimation1 = new AnimationDrawable();
            blacktopAnimation1 = (AnimationDrawable) course3LayoutPlay1.getDrawable();
            blacktopAnimation1.start();
            playIng1 = true;
            mediaPlayer1.start();
            mHandler.post(updateThread1);
        }
    }

    private void pause1() {
        playIng1 = false;
        if (blacktopAnimation1 != null) {
            blacktopAnimation1.stop();
        }
        course3LayoutPlay1.setImageResource(R.drawable.audio_play);
        if (mediaPlayer1.isPlaying()) {
            mediaPlayer1.pause();
        }
        mHandler.removeCallbacks(updateThread1);
    }

    private void play2() {
        if (playIng2) {
            pause2();
        } else {
            if (!start2) {
                try {
                    mediaPlayer2.reset();
                    Log.i(TAG, Constant.DOWN_PATH + Constant.PRACTICE_PATH + courseEntity.getKnowledge().get(index).getAudio());
                    mediaPlayer2.setDataSource(Constant.DOWN_PATH + Constant.PRACTICE_PATH + courseEntity.getKnowledge().get(index).getAudio2());
                    mediaPlayer2.prepare();

                    mediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            playIng2 = false;
                            if (blacktopAnimation2 != null) {
                                blacktopAnimation2.stop();
                            }
                            course3LayoutPlay2.setImageResource(R.drawable.audio_play);
                            course3LayoutPlay2ProgressBar.setProgress(0);
                            mHandler.removeCallbacks(updateThread2);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "getDuration=" + mediaPlayer2.getDuration());
                course3LayoutPlay2ProgressBar.setMax(mediaPlayer2.getDuration());
                start2 = true;
            }

            course3LayoutPlay2.setImageResource(R.drawable.audio_pause_bg);
            blacktopAnimation2 = new AnimationDrawable();
            blacktopAnimation2 = (AnimationDrawable) course3LayoutPlay2.getDrawable();
            blacktopAnimation2.start();
            playIng2 = true;
            mediaPlayer2.start();
            mHandler.post(updateThread2);
        }
    }

    private void pause2() {
        playIng2 = false;
        if (blacktopAnimation2 != null) {
            blacktopAnimation2.stop();
        }
        course3LayoutPlay2.setImageResource(R.drawable.audio_play);
        if (mediaPlayer2.isPlaying()) {
            mediaPlayer2.pause();
        }
        mHandler.removeCallbacks(updateThread2);
    }

    /**
     * update进度条
     */
    private Runnable updateThread1 = new Runnable() {
        @Override
        public void run() {
            try {
                if (mediaPlayer1 != null) {
                    if (mediaPlayer1.getCurrentPosition() == mediaPlayer1.getDuration()) {
                        return;
                    }
                    course3LayoutPlay1ProgressBar.setProgress(mediaPlayer1.getCurrentPosition());
                }
                mHandler.removeCallbacks(updateThread1);
                mHandler.postDelayed(updateThread1, 50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    /**
     * update进度条
     */
    private Runnable updateThread2 = new Runnable() {
        @Override
        public void run() {
            try {
                if (mediaPlayer2 != null) {
                    if (mediaPlayer2.getCurrentPosition() == mediaPlayer2.getDuration()) {
                        return;
                    }
                    course3LayoutPlay2ProgressBar.setProgress(mediaPlayer2.getCurrentPosition());
                }
                mHandler.removeCallbacks(updateThread2);
                mHandler.postDelayed(updateThread2, 50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void show(CourseEntity courseEntities, int index, String type) {
        rootView.setVisibility(View.VISIBLE);
        initViewData();
    }

    @Override
    public void stop() {
        pause1();
        pause2();
    }

    @Override
    public void destroy() {
        if (mediaPlayer1 != null) {
            mediaPlayer1.release();
            mediaPlayer1 = null;
        }
        if (mediaPlayer2 != null) {
            mediaPlayer2.release();
            mediaPlayer2 = null;
        }
        if (blacktopAnimation1 != null) {
            blacktopAnimation1 = null;
        }
        if (blacktopAnimation2 != null) {
            blacktopAnimation2 = null;
        }
    }
}

