package com.weiyin.qinplus.activity.course;

import android.app.Activity;
import android.content.Intent;
import android.widget.ViewFlipper;

import com.weiyin.qinplus.entity.CourseEntity;
import com.weiyin.qinplus.listener.CourseInterface;

/**
 * @author njf
 * @date 2017/7/24
 */

public abstract class AbstractCourse {
    public static final String TAG = AbstractCourse.class.getSimpleName();
    public CourseInterface courseInterface;

    /**
     * onCreate
     *
     * @param activity       Activity
     * @param contain        ViewFlipper
     * @param courseEntities CourseEntity
     * @param index          int
     */
    public abstract void onCreate(Activity activity, ViewFlipper contain, CourseEntity courseEntities, int index);

    /**
     * initView
     */
    public abstract void initView();

    /**
     * setCourseInterface
     *
     * @param courseInterface CourseInterface
     */
    public abstract void setCourseInterface(CourseInterface courseInterface);

    /**
     * onActivityResult
     *
     * @param requestCode int
     * @param resultCode  int
     * @param data        Intent
     */
    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * show
     *
     * @param courseEntities CourseEntity
     * @param index          int
     * @param type           String
     */
    public abstract void show(CourseEntity courseEntities, int index, String type);

    /**
     * stop
     */
    public abstract void stop();

    /**
     * destroy
     */
    public abstract void destroy();
}
