package com.weiyin.qinplus.activity.course;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.dialog.PracticeDialog;
import com.weiyin.qinplus.entity.CourseEntity;
import com.weiyin.qinplus.entity.FrameEntity;
import com.weiyin.qinplus.entity.ImgAFraEntity;
import com.weiyin.qinplus.entity.PracticeEntity;
import com.weiyin.qinplus.listener.CourseInterface;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author njf
 * @date 2017/8/31
 */

public class Practice5 extends AbstractCourse {
    private View rootView;
    private TextView practice1LayoutContentTextView, practice1LayoutPracticeTextView, practice1LayoutOkTextView, practice1TextView;
    private ImageView practice1Rl1ImageView, practice1Rl2ImageView;
    private RelativeLayout practice1LayoutPracticeRl, practice1Rl1, practice1Rl2, practiceCancelRl, practice5LayoutRl;
    private ClickAble clickAble;

    private Activity activity;
    private CourseEntity courseEntity;
    private int index;
    private String answer, PracticeType, nextType;

    private PracticeDialog practiceDialog;
    private PracticeEntity practiceEntity;

    private List<ImageView> firstImageViewList, twoImageViewList;

    @Override
    public void onCreate(Activity activity, ViewFlipper contain, CourseEntity courseEntities, int index) {
        rootView = LayoutInflater.from(activity).inflate(R.layout.practice5_layout, null);
        this.activity = activity;
        this.index = index;
        this.courseEntity = courseEntities;
        Log.i(TAG, "index" + index);
        LayoutHelper layoutHelper = new LayoutHelper(activity);
        layoutHelper.scaleView(rootView);
        contain.addView(rootView);
        if (firstImageViewList == null) {
            firstImageViewList = new ArrayList<>();
        }
        if (twoImageViewList == null) {
            twoImageViewList = new ArrayList<>();
        }
        clickAble = new ClickAble();
        initView();
    }

    @Override
    public void initView() {
        practice5LayoutRl = (RelativeLayout) rootView.findViewById(R.id.practice5LayoutRl);
    }

    private void initViewData() {
        int practiceIndex = index - courseEntity.getKnowledge().size();
        if (practiceIndex < courseEntity.getPractice().size()) {
            PracticeEntity practiceEntity = courseEntity.getPractice().get(practiceIndex);
            List<ImgAFraEntity> imgAFraEntities = practiceEntity.getFristCol();
            if (imgAFraEntities != null) {
                for (int i = 0; i < imgAFraEntities.size(); i++) {
                    ImgAFraEntity imgAFraEntity = imgAFraEntities.get(i);
                    ImageView imageView = new ImageView(activity);
                    FrameEntity frameEntity = imgAFraEntity.getFrame();
                    if (frameEntity != null) {
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Integer.valueOf(frameEntity.getW()), Integer.valueOf(frameEntity.getH()));
                        layoutParams.topMargin = Integer.parseInt(frameEntity.getY());
                        layoutParams.leftMargin = Integer.parseInt(frameEntity.getX());
                        practice5LayoutRl.addView(imageView);
                        firstImageViewList.add(imageView);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }
                }
            }

            List<ImgAFraEntity> imgAFraEntitieList = practiceEntity.getSecondCol();
            if (imgAFraEntitieList != null) {
                for (int i = 0; i < imgAFraEntitieList.size(); i++) {
                    ImgAFraEntity imgAFraEntity = imgAFraEntitieList.get(i);
                    FrameEntity frameEntity = imgAFraEntity.getFrame();
                    if (frameEntity != null) {
                        ImageView imageView = new ImageView(activity);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Integer.valueOf(frameEntity.getW()), Integer.valueOf(frameEntity.getH()));
                        layoutParams.topMargin = Integer.parseInt(frameEntity.getY());
                        layoutParams.leftMargin = Integer.parseInt(frameEntity.getX());
                        imageView.setLayoutParams(layoutParams);
                        twoImageViewList.add(imageView);
                        practice5LayoutRl.addView(imageView);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }
                }
            }

        }
    }

    private class ClickAble implements View.OnClickListener {

        @Override
        public void onClick(View v) {

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
