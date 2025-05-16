package com.weiyin.qinplus.activity.course;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
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

/**
 * @author njf
 * @date 2017/8/28
 */

public class Course7 extends AbstractCourse {
    private View rootView;
    private ImageView course7LayoutImageView;

    private ClickAble clickAble;

    private Activity activity;

    private RelativeLayout course1LayoutLastKnowledgeRl, course1LayoutNextKnowledgeRl, course1LayoutPracticeRl;
    private CircleTextImageView course1LayoutAllPractice;
    private TextView course1TabNumberTextView, course1LayoutTitleTextView, course1LayoutContentTextView;

    private CourseEntity courseEntity;
    private int index;

    @SuppressLint("InflateParams")
    @Override
    public void onCreate(Activity activity, ViewFlipper contain, CourseEntity courseEntities, int index) {
        rootView = LayoutInflater.from(activity).inflate(R.layout.course7_layout, null);
        LayoutHelper layoutHelper = new LayoutHelper(activity);
        layoutHelper.scaleView(rootView);
        this.activity = activity;
        this.courseEntity = courseEntities;
        this.index = index;
        Log.i(TAG, "index" + index);
        contain.addView(rootView);
        clickAble = new ClickAble();
        initView();
    }

    @Override
    public void initView() {
        course7LayoutImageView = (ImageView) rootView.findViewById(R.id.course7LayoutImageView);
        course1LayoutLastKnowledgeRl = (RelativeLayout) rootView.findViewById(R.id.course1LayoutLastKnowledgeRl);
        course1LayoutNextKnowledgeRl = (RelativeLayout) rootView.findViewById(R.id.course1LayoutNextKnowledgeRl);
        course1LayoutPracticeRl = (RelativeLayout) rootView.findViewById(R.id.course1LayoutPracticeRl);
        course1TabNumberTextView = (TextView) rootView.findViewById(R.id.course1TabNumberTextView);
        course1LayoutTitleTextView = (TextView) rootView.findViewById(R.id.course1LayoutTitleTextView);
        course1LayoutContentTextView = (TextView) rootView.findViewById(R.id.course1LayoutContentTextView);
        course1LayoutAllPractice = (CircleTextImageView) rootView.findViewById(R.id.course1LayoutAllPractice);

        course1LayoutLastKnowledgeRl.setOnClickListener(clickAble);
        course1LayoutNextKnowledgeRl.setOnClickListener(clickAble);
        course1LayoutPracticeRl.setOnClickListener(clickAble);
        course1LayoutAllPractice.setOnClickListener(clickAble);
    }

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
                Glide.with(activity).load(WinYinPianoApplication.strUrl + Constant.PRACTICE_PATH + knowledgeEntity.getImg()).into(course7LayoutImageView);
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

    private class ClickAble implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();

            if (id == R.id.course1LayoutAllPractice) {
                courseInterface.courseInterfaceResult(Constant.TOTAL_PRACTICE, courseEntity.getPractice().size());
            } else if (id == R.id.course1LayoutLastKnowledgeRl) {
                courseInterface.courseInterfaceResult(Constant.LAST_KNOWLEDGE, index - 1);
            } else if (id == R.id.course1LayoutNextKnowledgeRl) {
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
                    rootView.setVisibility(View.GONE);
                    courseInterface.courseInterfaceResult(Constant.NEXT_KNOWLEDGE, position);
                }
            }

        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {

    }
}
