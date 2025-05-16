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
import com.weiyin.qinplus.entity.PracticeEntity;
import com.weiyin.qinplus.listener.CourseInterface;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author njf
 * @date 2017/8/31
 */

public class Practice6 extends AbstractCourse {
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
        rootView = LayoutInflater.from(activity).inflate(R.layout.practice6_layout, null);
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
