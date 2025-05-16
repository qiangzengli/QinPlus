package com.weiyin.qinplus.activity.course;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.weiyin.qinplus.ui.tv.utils.PlayerUtils;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;


/**
 * @author njf
 * @date 2017/8/1
 */

public class Course2 extends AbstractCourse implements View.OnTouchListener {

    private PlayerUtils playerUtils;
    private JCVideoPlayerStandard mPic;
    private View rootView;

    private ClickAble clickAble;

    private Activity activity;

    private TextView course1TabNumberTextView, course1LayoutTitleTextView, course1LayoutContentTextView;
    private ImageView course1LayoutImageView;

    private RelativeLayout course1LayoutLastKnowledgeRl, course1LayoutNextKnowledgeRl, resultLayout;

    private CourseEntity courseEntity;
    private int index;

    @SuppressLint("InflateParams")
    @Override
    public void onCreate(Activity activity, ViewFlipper contain, CourseEntity courseEntities, int index) {
        rootView = LayoutInflater.from(activity).inflate(R.layout.course2_layout, null);
        LayoutHelper layoutHelper = new LayoutHelper(activity);
        layoutHelper.scaleView(rootView);
        this.activity = activity;
        this.courseEntity = courseEntities;
        this.index = index;
        Log.i(TAG, "index" + index);
        playerUtils = PlayerUtils.getInstance();
        contain.addView(rootView);
        clickAble = new ClickAble();
        initView();
    }

    @Override
    public void initView() {
        mPic = (JCVideoPlayerStandard) rootView.findViewById(R.id.videoplayer);
        course1TabNumberTextView = (TextView) rootView.findViewById(R.id.course1TabNumberTextView);
        course1LayoutTitleTextView = (TextView) rootView.findViewById(R.id.course1LayoutTitleTextView);
        course1LayoutContentTextView = (TextView) rootView.findViewById(R.id.course1LayoutContentTextView);
        CircleTextImageView course1LayoutAllPractice = (CircleTextImageView) rootView.findViewById(R.id.course1LayoutAllPractice);

        course1LayoutImageView = (ImageView) rootView.findViewById(R.id.course1LayoutImageView);
        ImageView course1LayoutPlay = (ImageView) rootView.findViewById(R.id.course1LayoutPlay);

        course1LayoutLastKnowledgeRl = (RelativeLayout) rootView.findViewById(R.id.course1LayoutLastKnowledgeRl);
        course1LayoutNextKnowledgeRl = (RelativeLayout) rootView.findViewById(R.id.course1LayoutNextKnowledgeRl);
        RelativeLayout course1LayoutPracticeRl = (RelativeLayout) rootView.findViewById(R.id.course1LayoutPracticeRl);
        resultLayout = (RelativeLayout) rootView.findViewById(R.id.resultLayout);

        course1LayoutLastKnowledgeRl.setOnClickListener(clickAble);
        course1LayoutNextKnowledgeRl.setOnClickListener(clickAble);
        course1LayoutPracticeRl.setOnClickListener(clickAble);
        course1LayoutPlay.setOnClickListener(clickAble);
        course1LayoutAllPractice.setOnClickListener(clickAble);
    }

    @SuppressLint("SetTextI18n")
    private void initViewData() {
        mPic.setVisibility(View.VISIBLE);
        if (courseEntity != null) {
            course1LayoutLastKnowledgeRl.setVisibility(index == 0 ? View.GONE : View.VISIBLE);
            course1LayoutNextKnowledgeRl.setVisibility(index == courseEntity.getKnowledge().size() - 1 ? View.GONE : View.VISIBLE);
            if (index < courseEntity.getKnowledge().size()) {
                KnowledgeEntity knowledgeEntity = courseEntity.getKnowledge().get(index);
                course1TabNumberTextView.setText(knowledgeEntity.getId() + "/" + courseEntity.getKnowledge().size());
                course1LayoutTitleTextView.setText(knowledgeEntity.getTitle());
                course1LayoutContentTextView.setText(knowledgeEntity.getText());
                Log.i(TAG, knowledgeEntity.getImg());
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
                JCVideoPlayer.releaseAllVideos();
                resultLayout.setVisibility(View.GONE);
                courseInterface.courseInterfaceResult(Constant.TOTAL_PRACTICE, courseEntity.getPractice().size());

            } else if (id == R.id.course1LayoutLastKnowledgeRl) {
                JCVideoPlayer.releaseAllVideos();
                resultLayout.setVisibility(View.GONE);
                courseInterface.courseInterfaceResult(Constant.LAST_KNOWLEDGE, index - 1);

            } else if (id == R.id.course1LayoutNextKnowledgeRl) {
                JCVideoPlayer.releaseAllVideos();
                resultLayout.setVisibility(View.GONE);
                courseInterface.courseInterfaceResult(Constant.NEXT_KNOWLEDGE, index + 1);

            } else if (id == R.id.course1LayoutPracticeRl) {
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
                    JCVideoPlayer.releaseAllVideos();
                    resultLayout.setVisibility(View.GONE);
                    courseInterface.courseInterfaceResult(Constant.NEXT_KNOWLEDGE, position);
                }

            } else if (id == R.id.course1LayoutPlay) {
                if (mPic.thumbImageView != null && !TextUtils.isEmpty(courseEntity.getKnowledge().get(index).getVideo())) {
                    resultLayout.setVisibility(View.VISIBLE);
                    mPic.titleTextView.setTextSize(20);

                    playerUtils.setVideo(
                            mPic,
                            WinYinPianoApplication.strUrl + Constant.PRACTICE_PATH + courseEntity.getKnowledge().get(index).getVideo(),
                            "",
                            WinYinPianoApplication.strUrl + Constant.PRACTICE_PATH + courseEntity.getKnowledge().get(index).getImg()
                    );
                    mPic.backButton.setVisibility(View.VISIBLE);

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    mPic.backButton.setLayoutParams(layoutParams);

                    mPic.backButton.setOnClickListener(v1 -> {
                        JCVideoPlayer.releaseAllVideos();
                        resultLayout.setVisibility(View.GONE);
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    });
                }
            }

        }
    }


    @Override
    public void show(CourseEntity courseEntities, int index, String type) {
        rootView.setVisibility(View.VISIBLE);
        initViewData();
    }

    @Override
    public void stop() {
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public void destroy() {
        if (playerUtils != null) {
            playerUtils.releaseVideo();
            playerUtils = null;
        }
        if (mPic != null) {
            mPic.release();
            mPic = null;
        }
    }
}
