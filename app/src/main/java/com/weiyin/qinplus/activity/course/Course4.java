package com.weiyin.qinplus.activity.course;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewFlipper;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.entity.CourseEntity;
import com.weiyin.qinplus.listener.CourseInterface;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

/**
 *
 * @author njf
 * @date 2017/8/25
 */

public class Course4 extends AbstractCourse {
    @Override
    public void onCreate(Activity activity, ViewFlipper contain, CourseEntity courseEntities, int index) {
        View rootView = LayoutInflater.from(activity).inflate(R.layout.course4_layout, null);
        LayoutHelper layoutHelper = new LayoutHelper(activity);
        layoutHelper.scaleView(rootView);
        Activity activity1 = activity;
        CourseEntity courseEntity = courseEntities;
        int index1 = index;
        Log.i(TAG,"index"+index);
        contain.addView(rootView);
        MediaPlayer mediaPlayer1 = new MediaPlayer();
        MediaPlayer mediaPlayer2 = new MediaPlayer();
        ClickAble clickAble = new ClickAble();
        initView();
        initViewData();
    }

    @Override
    public void initView() {

    }

    private void initViewData(){

    }

    private class ClickAble implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            }
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
