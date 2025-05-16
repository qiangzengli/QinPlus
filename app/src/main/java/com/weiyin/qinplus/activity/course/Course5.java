package com.weiyin.qinplus.activity.course;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.thinkcool.circletextimageview.CircleTextImageView;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.entity.CourseEntity;
import com.weiyin.qinplus.entity.FrameEntity;
import com.weiyin.qinplus.entity.ImgAFraEntity;
import com.weiyin.qinplus.entity.ImgAutoEntity;
import com.weiyin.qinplus.entity.KnowledgeEntity;
import com.weiyin.qinplus.listener.CourseInterface;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lenovo
 * @date 2017/8/28
 */

public class Course5 extends AbstractCourse {
    private View rootView;
    private ImageView imageBackground;
    private List<ImageView> imageViewList;

    private RelativeLayout course1LayoutLastKnowledgeRl, course1LayoutNextKnowledgeRl, course1LayoutPracticeRl, course5LayoutRl;
    private CircleTextImageView course1LayoutAllPractice;

    private ClickAble clickAble;

    private Activity activity;

    private MediaPlayer mediaPlayer;

    private CourseEntity courseEntity;
    private int index;

    @Override
    public void onCreate(Activity activity, ViewFlipper contain, CourseEntity courseEntities, int index) {
        rootView = LayoutInflater.from(activity).inflate(R.layout.course5_layout, null);
        LayoutHelper layoutHelper = new LayoutHelper(activity);
        layoutHelper.scaleView(rootView);
        this.activity = activity;
        this.courseEntity = courseEntities;
        this.index = index;
        Log.i(TAG, "index" + index);
        contain.addView(rootView);
        mediaPlayer = new MediaPlayer();
        clickAble = new ClickAble();
        if (imageViewList == null) {
            imageViewList = new ArrayList<>();
        }
        initView();
        initViewData();
    }

    @Override
    public void initView() {
        course1LayoutLastKnowledgeRl = (RelativeLayout) rootView.findViewById(R.id.course1LayoutLastKnowledgeRl);
        course1LayoutNextKnowledgeRl = (RelativeLayout) rootView.findViewById(R.id.course1LayoutNextKnowledgeRl);
        course1LayoutPracticeRl = (RelativeLayout) rootView.findViewById(R.id.course1LayoutPracticeRl);
        course5LayoutRl = (RelativeLayout) rootView.findViewById(R.id.course5LayoutRl);
        course1LayoutAllPractice = (CircleTextImageView) rootView.findViewById(R.id.course1LayoutAllPractice);

        course1LayoutPracticeRl.setOnClickListener(clickAble);
        course1LayoutNextKnowledgeRl.setOnClickListener(clickAble);
        course1LayoutLastKnowledgeRl.setOnClickListener(clickAble);
        course1LayoutAllPractice.setOnClickListener(clickAble);
    }

    private void initViewData() {
        if (imageBackground == null) {
            if (courseEntity.getKnowledge().size() > index) {
                course1LayoutLastKnowledgeRl.setVisibility(index == 0 ? View.GONE : View.VISIBLE);
                course1LayoutNextKnowledgeRl.setVisibility(index == courseEntity.getKnowledge().size() - 1 ? View.GONE : View.VISIBLE);
                KnowledgeEntity knowledgeEntity = courseEntity.getKnowledge().get(index);
                imageBackground = new ImageView(activity);
                ImgAFraEntity imgAFraEntity = knowledgeEntity.getBackGudImg();
                if (imgAFraEntity != null) {
                    FrameEntity frameEntity = imgAFraEntity.getFrame();
                    if (frameEntity != null) {
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Integer.valueOf(frameEntity.getW()), Integer.valueOf(frameEntity.getH()));
                        layoutParams.topMargin = Integer.parseInt(frameEntity.getY());
                        layoutParams.leftMargin = Integer.parseInt(frameEntity.getX());
                        imageBackground.setLayoutParams(layoutParams);
                        course5LayoutRl.addView(imageBackground);
                        Glide.with(activity).load(WinYinPianoApplication.strUrl + Constant.PRACTICE_PATH + imgAFraEntity.getImgUrl()).into(imageBackground);
                    }
                }
                final List<ImgAutoEntity> imgAutoEntityList = knowledgeEntity.getImgAuto();
                if (imgAutoEntityList != null) {
                    for (int i = 0; i < imgAutoEntityList.size(); i++) {
                        ImgAutoEntity imgAutoEntity = imgAutoEntityList.get(i);
                        ImageView imageView = new ImageView(activity);
                        FrameEntity frameEntity = imgAutoEntity.getFrame();
                        if (frameEntity != null) {
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Integer.valueOf(frameEntity.getW()), Integer.valueOf(frameEntity.getH()));
                            layoutParams.topMargin = Integer.parseInt(frameEntity.getY());
                            layoutParams.leftMargin = Integer.parseInt(frameEntity.getX());
                            imageView.setLayoutParams(layoutParams);
                            course5LayoutRl.addView(imageView);
                            imageViewList.add(imageView);
                            Glide.with(activity).load(WinYinPianoApplication.strUrl + Constant.PRACTICE_PATH + imgAutoEntity.getImg()).into(imageView);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for (int i = 0; i < imageViewList.size(); i++) {
                                        if (v == imageViewList.get(i)) {
                                            play(WinYinPianoApplication.strUrl + Constant.PRACTICE_PATH + imgAutoEntityList.get(i).getAudio());
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    private void play(String path) {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClickAble implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            if (viewId == R.id.course1LayoutAllPractice) {
                courseInterface.courseInterfaceResult(Constant.TOTAL_PRACTICE, courseEntity.getPractice().size());
            }
            else if (viewId == R.id.course1LayoutLastKnowledgeRl) {
                courseInterface.courseInterfaceResult(Constant.LAST_KNOWLEDGE, index - 1);
            }
            else if (viewId == R.id.course1LayoutNextKnowledgeRl) {
                courseInterface.courseInterfaceResult(Constant.NEXT_KNOWLEDGE, index + 1);
            }
            else if (viewId == R.id.course1LayoutPracticeRl) {
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
            }
// No need for default case as it was empty in the switch statement
        }
    }

    @Override
    public void setCourseInterface(CourseInterface courseInterface) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void show(CourseEntity courseEntities, int index, String type) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {

    }
}
