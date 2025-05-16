package com.weiyin.qinplus.activity.course;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.dialog.PracticeDialog;
import com.weiyin.qinplus.entity.CourseEntity;
import com.weiyin.qinplus.entity.FrameEntity;
import com.weiyin.qinplus.entity.ImageSizeEntity;
import com.weiyin.qinplus.entity.ImagesEntity;
import com.weiyin.qinplus.entity.ImagesPointEntity;
import com.weiyin.qinplus.entity.ImgAFraEntity;
import com.weiyin.qinplus.entity.PracticeEntity;
import com.weiyin.qinplus.listener.CourseInterface;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author njf
 * @date 2017/8/28
 */

public class Practice3 extends AbstractCourse {
    private View rootView;
    private TextView practice1LayoutContentTextView, practice1LayoutPracticeTextView, practice1LayoutOkTextView, practice1TextView;
    private RelativeLayout practice3LayoutRl, practice1LayoutPracticeRl, practiceCancelRl;
    private ClickAble clickAble;

    private Activity activity;
    private CourseEntity courseEntity;
    private int index, imageIndex;
    private String answer, PracticeType, nextType;

    private PracticeDialog practiceDialog;
    private PracticeEntity practiceEntity;
    private List<ImageView> imageViewList;
    private List<String> imageIndexList;

    Handler mHandler = new Handler();

    @Override
    public void onCreate(Activity activity, ViewFlipper contain, CourseEntity courseEntities, int index) {
        rootView = LayoutInflater.from(activity).inflate(R.layout.practice3_layout, null);
        this.activity = activity;
        this.index = index;
        this.courseEntity = courseEntities;
        imageIndex = 0;
        Log.i(TAG, "index" + index);
        LayoutHelper layoutHelper = new LayoutHelper(activity);
        layoutHelper.scaleView(rootView);
        contain.addView(rootView);
        clickAble = new ClickAble();
        if (imageIndexList == null) {
            imageIndexList = new ArrayList<>();
        }
        initView();
    }

    @Override
    public void initView() {
        practice1LayoutContentTextView = (TextView) rootView.findViewById(R.id.practice1LayoutContentTextView);
        practice1LayoutPracticeTextView = (TextView) rootView.findViewById(R.id.practice1LayoutPracticeTextView);
        practice1TextView = (TextView) rootView.findViewById(R.id.practice1TextView);
        practice1LayoutOkTextView = (TextView) rootView.findViewById(R.id.practice1LayoutOkTextView);
        practice3LayoutRl = (RelativeLayout) rootView.findViewById(R.id.practice3LayoutRl);
        practice1LayoutPracticeRl = (RelativeLayout) rootView.findViewById(R.id.practice1LayoutPracticeRl);
        practiceCancelRl = (RelativeLayout) rootView.findViewById(R.id.practiceCancelRl);

        practice1LayoutOkTextView.setOnClickListener(clickAble);
        practice1LayoutPracticeTextView.setOnClickListener(clickAble);
        practice1LayoutPracticeRl.setOnClickListener(clickAble);
        practiceCancelRl.setOnClickListener(clickAble);
    }

    private void initViewData() {
        int practiceIndex = index - courseEntity.getKnowledge().size();
        practiceEntity = courseEntity.getPractice().get(practiceIndex);
        if (practiceEntity != null) {
            initImageView();
            practice1LayoutContentTextView.setText(practiceEntity.getText());
            if (Constant.TOTAL_PRACTICE.equals(PracticeType) || Constant.NEXT_PRACTICE.equals(PracticeType)) {
                practice1TextView.setText((practiceIndex + 1) + "/" + courseEntity.getPractice().size());
            } else {
                int count = 0;
                int index = 0;
                for (int i = 0; i < courseEntity.getPractice().size(); i++) {
                    if (courseEntity.getPractice().get(i).getKnowPointId().equals(practiceEntity.getKnowPointId())) {
                        count++;
                        if (courseEntity.getPractice().get(i).getText().equals(practiceEntity.getText())) {
                            index = count;
                        }
                    }
                }
                practice1TextView.setText(index + "/" + count);
            }
        }
    }

    private void initImageView() {
        ImgAFraEntity imgAFraEntity = practiceEntity.getImgAFra();
        if (imgAFraEntity != null) {
            FrameEntity frameEntity = imgAFraEntity.getFrame();
            if (frameEntity != null) {
                int width = Integer.parseInt(frameEntity.getW());
                int height = Integer.parseInt(frameEntity.getH());
                ImageView imageView = new ImageView(activity);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
                layoutParams.topMargin = Integer.parseInt(frameEntity.getY());
                layoutParams.leftMargin = Integer.parseInt(frameEntity.getX());
                imageView.setLayoutParams(layoutParams);
                Glide.with(activity).load(WinYinPianoApplication.strUrl + Constant.PRACTICE_PATH + imgAFraEntity.getImgUrl()).into(imageView);
                practice3LayoutRl.addView(imageView);
            }
        }
        final List<ImagesEntity> imagesEntities = practiceEntity.getImgs();
        ImageSizeEntity imageSize = practiceEntity.getImgSize();
        int width = Integer.parseInt(imageSize.getW());
        int height = Integer.parseInt(imageSize.getH());
        if (imagesEntities != null) {
            for (int i = 0; i < imagesEntities.size(); i++) {
                ImageView imageView = new ImageView(activity);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
                layoutParams.topMargin = Integer.parseInt(imagesEntities.get(i).getImgPoint().getY());
                layoutParams.leftMargin = Integer.parseInt(imagesEntities.get(i).getImgPoint().getX());
                imageView.setLayoutParams(layoutParams);
                Glide.with(activity).load(WinYinPianoApplication.strUrl + Constant.PRACTICE_PATH + imagesEntities.get(i).getImgUrl()).into(imageView);
                practice3LayoutRl.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imageViewList != null) {
                            v.setDrawingCacheEnabled(true);
                            if (imageIndexList != null && imageIndexList.size() > 0) {
                                imageViewList.get(Integer.parseInt(imageIndexList.get(imageIndexList.size() - 1))).setImageBitmap(v.getDrawingCache());
                                imageIndexList.remove(imageIndexList.size() - 1);
                            } else {
                                imageViewList.get(imageIndex).setImageBitmap(v.getDrawingCache());
                                imageIndex++;
                            }
                            v.setDrawingCacheEnabled(false);
                        }
                    }
                });
            }
        }
        List<ImagesPointEntity> imagesPointEntities = practiceEntity.getImgsPoint();
        if (imagesPointEntities != null) {
            imageViewList = new ArrayList<>();
            for (int i = 0; i < imagesPointEntities.size(); i++) {
                ImageView imageView = new ImageView(activity);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
                layoutParams.topMargin = Integer.parseInt(imagesPointEntities.get(i).getY());
                layoutParams.leftMargin = Integer.parseInt(imagesPointEntities.get(i).getX());
                imageView.setLayoutParams(layoutParams);
                Glide.with(activity).load(WinYinPianoApplication.strUrl + Constant.PRACTICE_PATH + practiceEntity.getPointDefaultImg()).into(imageView);
                practice3LayoutRl.addView(imageView);
                imageViewList.add(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imageViewList != null) {
                            for (int i = 0; i < imageViewList.size(); i++) {
                                if (imageViewList.get(i) == v) {
                                    Log.i(TAG, "i=" + i);
                                    imageIndexList.add(String.valueOf(i));
                                }
                            }
                        }
                        Glide.with(activity).load(WinYinPianoApplication.strUrl + Constant.PRACTICE_PATH + practiceEntity.getPointDefaultImg()).into((ImageView) v);
                    }
                });
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
        this.index = index;
        initViewData();
    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {

    }
}
