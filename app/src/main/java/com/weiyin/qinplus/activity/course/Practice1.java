package com.weiyin.qinplus.activity.course;

import android.app.Activity;
import android.content.Intent;
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
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.dialog.PracticeDialog;
import com.weiyin.qinplus.entity.CourseEntity;
import com.weiyin.qinplus.entity.PracticeEntity;
import com.weiyin.qinplus.listener.CourseInterface;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import org.apache.commons.lang3.StringUtils;

/**
 * @author njf
 * @date 2017/7/28
 */

public class Practice1 extends AbstractCourse implements View.OnTouchListener {

    private View rootView;
    private TextView practice1LayoutContentTextView, practice1LayoutPracticeTextView, practice1LayoutOkTextView, practice1TextView;
    private ImageView practice1Rl1ImageView, practice1Rl2ImageView;
    private RelativeLayout practice1LayoutPracticeRl, practice1Rl1, practice1Rl2, practiceCancelRl;
    private ClickAble clickAble;

    private Activity activity;
    private CourseEntity courseEntity;
    private int index;
    private String answer, PracticeType, nextType;

    private PracticeDialog practiceDialog;
    private PracticeEntity practiceEntity;

    Handler mHandler = new Handler();

    @Override
    public void onCreate(Activity activity, ViewFlipper contain, CourseEntity courseEntities, int index) {
        rootView = LayoutInflater.from(activity).inflate(R.layout.practice1_layout, null);
        this.activity = activity;
        this.index = index;
        this.courseEntity = courseEntities;
        Log.i(TAG, "index" + index);
        LayoutHelper layoutHelper = new LayoutHelper(activity);
        layoutHelper.scaleView(rootView);
        contain.addView(rootView);
        clickAble = new ClickAble();
        initView();
    }

    @Override
    public void initView() {
        practice1LayoutContentTextView = (TextView) rootView.findViewById(R.id.practice1LayoutContentTextView);
        practice1LayoutPracticeTextView = (TextView) rootView.findViewById(R.id.practice1LayoutPracticeTextView);
        practice1LayoutOkTextView = (TextView) rootView.findViewById(R.id.practice1LayoutOkTextView);
        practice1Rl1ImageView = (ImageView) rootView.findViewById(R.id.practice1Rl1ImageView);
        practice1Rl2ImageView = (ImageView) rootView.findViewById(R.id.practice1Rl2ImageView);
        practice1LayoutPracticeRl = (RelativeLayout) rootView.findViewById(R.id.practice1LayoutPracticeRl);
        practice1Rl1 = (RelativeLayout) rootView.findViewById(R.id.practice1Rl1);
        practice1Rl2 = (RelativeLayout) rootView.findViewById(R.id.practice1Rl2);
        practice1TextView = (TextView) rootView.findViewById(R.id.practice1TextView);
        practiceCancelRl = (RelativeLayout) rootView.findViewById(R.id.practiceCancelRl);

        practiceCancelRl.setVisibility(View.GONE);
        practice1Rl1.setOnClickListener(clickAble);
        practice1Rl2.setOnClickListener(clickAble);
        practice1LayoutPracticeRl.setOnClickListener(clickAble);
        practice1LayoutPracticeTextView.setOnClickListener(clickAble);
        practice1LayoutOkTextView.setOnClickListener(clickAble);
    }

    private void initViewData() {
        int practiceIndex = index - courseEntity.getKnowledge().size();
        practiceEntity = courseEntity.getPractice().get(practiceIndex);
        if (practiceEntity != null) {
            practice1Rl1.setSelected(false);
            practice1Rl2.setSelected(false);
            if (Constant.TOTAL_PRACTICE.equals(PracticeType) || Constant.NEXT_PRACTICE.equals(PracticeType)) {
                practice1TextView.setText((practiceIndex + 1) + "/" + courseEntity.getPractice().size());
            } else {
                int cout = 0;
                int index = 0;
                for (int i = 0; i < courseEntity.getPractice().size(); i++) {
                    if (courseEntity.getPractice().get(i).getKnowPointId().equals(practiceEntity.getKnowPointId())) {
                        cout++;
                        if (courseEntity.getPractice().get(i).getText().equals(practiceEntity.getText())) {
                            index = cout;
                        }
                    }
                }
                practice1TextView.setText(index + "/" + cout);
            }
            practice1LayoutContentTextView.setText(practiceEntity.getText());
            Glide.with(activity).load(WinYinPianoApplication.strUrl + Constant.PRACTICE_PATH + practiceEntity.getImg().get(0)).into(practice1Rl1ImageView);
            Glide.with(activity).load(WinYinPianoApplication.strUrl + Constant.PRACTICE_PATH + practiceEntity.getImg().get(1)).into(practice1Rl2ImageView);
        }
    }

    @Override
    public void setCourseInterface(CourseInterface courseInterface) {
        this.courseInterface = courseInterface;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private class ClickAble implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.practice1Rl1) {
                if (practiceEntity != null) {
                    if (practiceEntity.getImg().get(0).contains("zhengque")) {
                        answer = "ok";
                    } else {
                        answer = "no";
                    }
                } else {
                    answer = "no";
                }
                practice1Rl1.setSelected(true);
                practice1Rl2.setSelected(false);
            } else if (id == R.id.practice1Rl2) {
                if (practiceEntity != null) {
                    if (practiceEntity.getImg().get(1).contains("zhengque")) {
                        answer = "ok";
                    } else {
                        answer = "no";
                    }
                } else {
                    answer = "no";
                }
                practice1Rl1.setSelected(false);
                practice1Rl2.setSelected(true);
            } else if (id == R.id.practice1LayoutPracticeRl) {
                returnKnowledge();
            } else if (id == R.id.practice1LayoutPracticeTextView) {
                mHandler.removeCallbacksAndMessages(runnable);
                practice1Rl1.setSelected(true);
                practice1Rl2.setSelected(false);
                mHandler.postDelayed(runnable, 1000);
            } else if (id == R.id.practice1LayoutOkTextView) {
                if (StringUtils.isEmpty(answer)) {
                    ToastUtil.showTextToast(activity, "请点击图片以选择答案");
                    return;
                }
                String content = "";
                if (index == courseEntity.getKnowledge().size() + courseEntity.getPractice().size() - 1) {
                    content = "返回知识点";
                } else {
                    if (Constant.TOTAL_PRACTICE.equals(PracticeType) || Constant.NEXT_PRACTICE.equals(PracticeType)) {
                        content = "下一题";
                    } else {
                        if (courseEntity.getPractice().get(index + 1 - courseEntity.getKnowledge().size()).getKnowPointId().equals(courseEntity.getPractice().get(index - courseEntity.getKnowledge().size()).getKnowPointId())) {
                            nextType = "yes";
                            content = "下一题";
                        } else {
                            nextType = "no";
                            content = "返回知识点";
                        }
                    }
                }
                if ("ok".equals(answer)) {
                    showPracticeDialog("yes", content);
                } else {
                    showPracticeDialog("no", content);
                }
            }
// default 情况无需处理，原switch中的default是空语句
        }
    }

    private void returnKnowledge() {
        answer = "";
        int practiceIndex = index - courseEntity.getKnowledge().size();
        PracticeEntity practiceEntity = courseEntity.getPractice().get(practiceIndex);
        int position = Integer.valueOf(practiceEntity.getKnowPointId());
        courseInterface.courseInterfaceResult(Constant.LAST_KNOWLEDGE, position - 1);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            practice1Rl1.setSelected(false);
            mHandler.removeCallbacks(this);
            if ("ok".equals(answer)) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        practice1Rl1.setSelected(true);
                    }
                }, 500);
            }
        }
    };

    @Override
    public void show(CourseEntity courseEntities, int index, String type) {
        this.PracticeType = type;
        rootView.setVisibility(View.VISIBLE);
        initViewData();
    }

    private void showPracticeDialog(String type, String content) {
        if (practiceDialog == null) {
            practiceDialog = new PracticeDialog(activity, R.style.BlueToothDialogStyle, null);
            practiceDialog.setPracticeInterface(new PracticeDialog.PracticeInterface() {
                @Override
                public void result(String type) {
                    if ("2".equals(type)) {
                        answer = "";
                        if (Constant.TOTAL_PRACTICE.equals(PracticeType) || Constant.NEXT_PRACTICE.equals(PracticeType)) {
                            if (index == courseEntity.getKnowledge().size() + courseEntity.getPractice().size() - 1) {
                                courseInterface.courseInterfaceResult(Constant.OLD_PRACTICE, -1);
                            } else {
                                courseInterface.courseInterfaceResult(Constant.NEXT_PRACTICE, index + 1);
                            }
                        } else {
                            if ("yes".equals(nextType)) {
                                courseInterface.courseInterfaceResult(Constant.NEXT_PRACTICE, index + 1);
                            } else {
                                returnKnowledge();
                            }
//                            content = "下一题";
                        }
                    } else if ("1".equals(type)) {
                        answer = "";
                        practice1Rl1.setSelected(false);
                        practice1Rl2.setSelected(false);
                    }
                }
            });
        }
        if (!practiceDialog.isShowing()) {
            if (activity.hasWindowFocus()) {
                Log.i(TAG, type + "      " + content);
                practiceDialog.show();
                practiceDialog.setContent(type, content);
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
