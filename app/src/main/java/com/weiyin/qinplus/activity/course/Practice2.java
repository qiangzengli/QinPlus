package com.weiyin.qinplus.activity.course;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.dialog.PracticeDialog;
import com.weiyin.qinplus.entity.CourseEntity;
import com.weiyin.qinplus.entity.PracticeEntity;
import com.weiyin.qinplus.listener.CourseInterface;
import com.weiyin.qinplus.ui.tv.bwstaff.KeyBoardModule;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;
import com.weiyin.qinplus.ui.tv.waterfall.PracticeKeyBoard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author njf
 * @date 2017/7/24
 */

public class Practice2 extends AbstractCourse {
    public static final String TAG = Practice2.class.getSimpleName();
    private View rootView;

    private ClickAble clickAble;
    private TouchAble touchAble;

    private Activity activity;

    private static final int START_NOTE = 21;
    private static final int ANIMATOR_DURATION = 100;
    private static final int KEY_NUMBER = 12;

    private HorizontalScrollView horizontalScrollView;
    private PracticeKeyBoard scrollViewKeyBoard, practiceKeyBoard;
    private float oldX, moveX;
    private List<KeyBoardModule> keyBoardModuleList;
    private List<String> keyBoard;

    private List<KeyBoardModule> practiceKeyBoardModuleList;
    private TextView practice2TextView, practice2LayoutContentTextView, practice1LayoutPracticeTextView, practice1LayoutOkTextView;
    private RelativeLayout relativeLayout, practice1LayoutPracticeRl, relativeLayout1, relativeLayout2, practice2RelativeLayout;

    Handler mHandler = new Handler();
    private CourseEntity courseEntity;
    private int index;

    private MediaPlayer mediaPlayer;

    private PracticeDialog practiceDialog;

    private String PracticeType, nextType;
    LayoutHelper layoutHelper;

    @Override
    public void onCreate(Activity activity, ViewFlipper contain, CourseEntity courseEntities, int index) {
        rootView = LayoutInflater.from(activity).inflate(R.layout.practice2_layout, null);
        this.activity = activity;
        layoutHelper = new LayoutHelper(activity);
        layoutHelper.scaleView(rootView);
        contain.addView(rootView);
        this.index = index;
        this.courseEntity = courseEntities;
        clickAble = new ClickAble();
        touchAble = new TouchAble();
        initView();
    }

    @Override
    public void initView() {
        mediaPlayer = new MediaPlayer();
        relativeLayout1 = (RelativeLayout) rootView.findViewById(R.id.relativeLayout1);
        relativeLayout2 = (RelativeLayout) rootView.findViewById(R.id.relativeLayout2);
        practice2RelativeLayout = (RelativeLayout) rootView.findViewById(R.id.practice2RelativeLayout);
        horizontalScrollView = (HorizontalScrollView) rootView.findViewById(R.id.practiceHorizontalScrollView);
        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout);
        scrollViewKeyBoard = (PracticeKeyBoard) rootView.findViewById(R.id.scrollViewKeyBoard);
        practiceKeyBoard = (PracticeKeyBoard) rootView.findViewById(R.id.practiceKeyBoard);
        practice1LayoutPracticeRl = (RelativeLayout) rootView.findViewById(R.id.practice1LayoutPracticeRl);
        practice2TextView = (TextView) rootView.findViewById(R.id.practice1TextView);
        practice2LayoutContentTextView = (TextView) rootView.findViewById(R.id.practice1LayoutContentTextView);
        practice1LayoutPracticeTextView = (TextView) rootView.findViewById(R.id.practice1LayoutPracticeTextView);
        practice1LayoutOkTextView = (TextView) rootView.findViewById(R.id.practice1LayoutOkTextView);
        if (keyBoardModuleList == null) {
            keyBoardModuleList = new ArrayList<>();
        }
        if (practiceKeyBoardModuleList == null) {
            practiceKeyBoardModuleList = new ArrayList<>();
        }
        if (keyBoard == null) {
            keyBoard = new ArrayList<>();
        }
        scrollViewKeyBoard.setOnTouchListener(touchAble);
        relativeLayout.setOnTouchListener(touchAble);

        practice1LayoutPracticeRl.setOnClickListener(clickAble);
        practice1LayoutPracticeTextView.setOnClickListener(clickAble);
        practice1LayoutOkTextView.setOnClickListener(clickAble);
    }

    private void initViewData() {
        if (scrollViewKeyBoard.getVisibility() == View.GONE) {
            scrollViewKeyBoard.setVisibility(View.VISIBLE);
        }
        if (practice2RelativeLayout.getVisibility() == View.GONE) {
            practice2RelativeLayout.setVisibility(View.VISIBLE);
        }
        if (practiceKeyBoard.getVisibility() == View.GONE) {
            practiceKeyBoard.setVisibility(View.VISIBLE);
        }
        if (courseEntity != null) {
            int position = index - courseEntity.getKnowledge().size();
            final PracticeEntity practiceEntity = courseEntity.getPractice().get(position);
            practice2LayoutContentTextView.setText(practiceEntity.getText());
            if (Constant.TOTAL_PRACTICE.equals(PracticeType) || Constant.NEXT_PRACTICE.equals(PracticeType)) {
                practice2TextView.setText((position + 1) + "/" + courseEntity.getPractice().size());
            } else {
                int cout = 0;
                int index = 0;
                for (int i = 0; i < courseEntity.getPractice().size(); i++) {
                    if (courseEntity.getPractice().get(i).getKnowPointId().equals(practiceEntity.getKnowPointId())) {
                        Log.i(TAG, "count=" + cout);
                        cout++;
                        if (courseEntity.getPractice().get(i).getText().equals(practiceEntity.getText())) {
                            index = cout;
                        }
                    }
                }
                practice2TextView.setText(index + "/" + cout);
            }

            if ("1".equals(practiceEntity.getDoubleKeyStyle().getType())) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String note = practiceEntity.getDoubleKeyStyle().getIndex();
                        int index = (Integer.valueOf(note) - 21) / 12 * 7 + 2;
                        int x = (int) (practiceKeyBoard.getfWhiteKeyWidth() * index);

                        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(relativeLayout, "translationX", 0, x);
                        objectAnimator.setDuration(ANIMATOR_DURATION);
                        objectAnimator.start();

                        Log.i(TAG, "left" + relativeLayout.getLeft() + "  " + relativeLayout.getWidth() + " " + scrollViewKeyBoard.getHeight() + "" + relativeLayout.getTop() + "  " + (practiceKeyBoard.getWidth() - (x + relativeLayout.getWidth())) + " " + (x + relativeLayout.getWidth()) + " " + practiceKeyBoard.getWidth() + " " + x);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (x), relativeLayout.getHeight());
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        layoutParams.topMargin = relativeLayout.getTop();
                        relativeLayout1.setLayoutParams(layoutParams);

                        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((int) ((practiceKeyBoard.getWidth() - (x + relativeLayout.getWidth()))), relativeLayout.getHeight());
                        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        Log.i(TAG, "aaa=" + (int) ((practiceKeyBoard.getWidth() - (x + relativeLayout.getWidth())) / layoutHelper.getScaleX()) + "  " + (int) (x / layoutHelper.getScaleX()));
                        layoutParams1.topMargin = relativeLayout.getTop();
                        relativeLayout2.setLayoutParams(layoutParams1);

                        float keyWidth = scrollViewKeyBoard.getWidth();
                        float praWidth = practiceKeyBoard.getWidth();
                        float bili = keyWidth / praWidth;
                        moveX = x * bili;
                        Log.i(TAG, "bili=" + bili + " movex=" + moveX + " keywidth=" + keyWidth + " prawidth=" + praWidth);
                        horizontalScrollView.smoothScrollTo((int) (moveX), 0);
                    }
                }, 200);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void setCourseInterface(CourseInterface courseInterface) {
        this.courseInterface = courseInterface;
    }

    private class TouchAble implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.i(TAG, "进入onTouch=" + event.getActionMasked() + "         " + event.getAction());
            Log.i(TAG, "r1=" + relativeLayout1.getWidth() + " r2=" + relativeLayout2.getWidth() + " " + layoutHelper.getScaleX());
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_MASK:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    if (v.getId() == R.id.scrollViewKeyBoard) {
                        getNote(event.getRawX(), event.getRawY());
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    if (v.getId() == R.id.scrollViewKeyBoard) {
                        getNote(event.getRawX(), event.getRawY());
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    getNote(event.getRawX(), event.getRawY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    Log.i(TAG, "进入");
                    if (v.getId() == R.id.scrollViewKeyBoard) {
//                    getNote(event.getRawX(),event.getRawY());
                        return true;
                    }
                    float keywidth = scrollViewKeyBoard.getWidth();
                    float prawidth = practiceKeyBoard.getWidth();
                    float bili = keywidth / prawidth;
                    Log.i(TAG, "进入1");
                    if (event.getRawX() < relativeLayout.getWidth() / 2) {
                        return true;
                    }
                    float x = event.getRawX() - relativeLayout.getWidth() / 2;
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(relativeLayout, "translationX", oldX, x);
                    oldX = x;
                    objectAnimator.setDuration(ANIMATOR_DURATION);
                    objectAnimator.start();

                    moveX = x * bili;
                    horizontalScrollView.smoothScrollTo((int) (moveX), 0);
                    Log.i(TAG, "进入2");
                    break;
                case MotionEvent.ACTION_UP:
                    mHandler.removeCallbacks(stopRunnable);
                    mHandler.postDelayed(stopRunnable, 1000);
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    private Runnable stopRunnable = new Runnable() {
        @Override
        public void run() {
            mediaPlayer.stop();
            mHandler.removeCallbacks(this);
        }
    };

    private void getNote(float x, float y) {
        float fWhiteKeyWidth = scrollViewKeyBoard.getfWhiteKeyWidth();
//        float fWhiteKeyHeight = scrollViewKeyBoard.getfWhiteKeyHeight();
//        float fBlackKeyHeight = scrollViewKeyBoard.getfBlackKeyHeight();
        float fBlackKeyWidth = scrollViewKeyBoard.getfBlackKeyWidth();
        float bottom = horizontalScrollView.getBottom();
        int note = (int) ((moveX + x) / fWhiteKeyWidth);
        int noteX = (int) (fWhiteKeyWidth * (note));
        int octave = (note - 2) / 7;
        int noteScale = (note - 2) % 7;
//        Log.i(TAG,"a="+note+" x="+x+" y="+y+" moveX="+moveX+" practiceKeyBoard.getWidth()="+practiceKeyBoard.getWidth()+" fWhiteKeyWidth="+fWhiteKeyWidth);
//        Log.i(TAG," octave="+octave+" noteX="+noteX+" fWhiteKeyHeight="+fWhiteKeyHeight+" fBlackKeyHeight="+fBlackKeyHeight+" fBlackKeyWidth="+fBlackKeyWidth+" y="+scrollViewKeyBoard.getBottom());
//        Log.i(TAG,"octave="+octave+"noteScale="+noteScale);
        int keyNote = 0;
        switch (noteScale) {
            case -2:
                keyNote = 21;
                if (noteX + (fWhiteKeyWidth - fBlackKeyWidth / 2) < x + moveX && bottom > y) {
                    keyNote++;
                }
                break;
            case -1:
                keyNote = 23;
                if (noteX + fBlackKeyWidth / 2 > x + moveX && bottom > y) {
                    keyNote--;
                }
                break;
            case 0:
                keyNote = START_NOTE + ((octave)) * KEY_NUMBER + 3;
                if (noteX + (fWhiteKeyWidth - fBlackKeyWidth / 2) < x + moveX && bottom > y) {
                    keyNote++;
                }
                break;
            case 1:
                keyNote = START_NOTE + ((octave)) * KEY_NUMBER + 3 + 2;
                if (noteX + fBlackKeyWidth / 2 > x + moveX && bottom > y) {
                    keyNote--;
                }
                if (noteX + (fWhiteKeyWidth - fBlackKeyWidth / 2) < x + moveX && bottom > y) {
                    keyNote++;
                }
                break;
            case 2:
                keyNote = START_NOTE + ((octave)) * KEY_NUMBER + 3 + 4;
                if (noteX + fBlackKeyWidth / 2 > x + moveX && bottom > y) {
                    keyNote--;
                }
                break;
            case 3:
                keyNote = START_NOTE + ((octave)) * KEY_NUMBER + 3 + 5;
                if (noteX + (fWhiteKeyWidth - fBlackKeyWidth / 2) < x + moveX && bottom > y) {
                    keyNote++;
                }
                break;
            case 4:
                keyNote = START_NOTE + ((octave)) * KEY_NUMBER + 3 + 7;
                if (noteX + fBlackKeyWidth / 2 > x + moveX && bottom > y) {
                    keyNote--;
                }
                if (noteX + (fWhiteKeyWidth - fBlackKeyWidth / 2) < x + moveX && bottom > y) {
                    keyNote++;
                }
                break;
            case 5:
                keyNote = START_NOTE + ((octave)) * KEY_NUMBER + 3 + 9;
                if (noteX + fBlackKeyWidth / 2 > x + moveX && bottom > y) {
                    keyNote--;
                }
                if (noteX + (fWhiteKeyWidth - fBlackKeyWidth / 2) < x + moveX && bottom > y) {
                    keyNote++;
                }
                break;
            case 6:
                keyNote = START_NOTE + ((octave)) * KEY_NUMBER + 3 + 11;
                if (noteX + fBlackKeyWidth / 2 > x + moveX && bottom > y) {
                    keyNote--;
                }
                break;
            default:
                break;
        }
        KeyBoardModule keyBoardModule = new KeyBoardModule(keyNote, 90, 127, 0);
        if (!keyBoard.contains(keyNote + "")) {
            keyBoard.add(keyNote + "");
            keyBoardModule.setStatus(90);
        } else {
            keyBoardModule.setStatus(80);
            keyBoard.remove(keyNote + "");
        }
        play(keyNote);

        keyBoardModuleList.add(keyBoardModule);
        practiceKeyBoardModuleList.add(keyBoardModule);
        scrollViewKeyBoard.poina(keyBoardModuleList, 2);
        practiceKeyBoard.poina(practiceKeyBoardModuleList, 2);
//        Constant.showTextToast(activity,"点击的值="+keyNote);
    }

    private void play(int keyNote) {
        mediaPlayer.reset();
        try {
            AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("m" + keyNote + ".midi");
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class ClickAble implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.practice1LayoutPracticeRl) {
                returnKnowledge();
            } else if (id == R.id.practice1LayoutPracticeTextView) {
                mHandler.removeCallbacks(runnable);
                PracticeEntity practiceEntity1 = courseEntity.getPractice().get(index - courseEntity.getKnowledge().size());
                String okString = practiceEntity1.getRocResType().getIndex();

                KeyBoardModule keyBoardModule = new KeyBoardModule(Integer.valueOf(okString), Constant.DOWN, 127, 0);
                keyBoardModuleList.add(keyBoardModule);
                practiceKeyBoardModuleList.add(keyBoardModule);
                scrollViewKeyBoard.poina(keyBoardModuleList, 2);
                practiceKeyBoard.poina(practiceKeyBoardModuleList, 2);

                mHandler.postDelayed(runnable, 1000);
            } else if (id == R.id.practice1LayoutOkTextView) {
                if (keyBoard == null || keyBoard.size() == 0) {
                    ToastUtil.showTextToast(activity, "请点击按键以选择答案");
                    return;
                }
                PracticeEntity practiceEntity2 = courseEntity.getPractice().get(index - courseEntity.getKnowledge().size());
                String okString1 = practiceEntity2.getRocResType().getIndex();
                boolean ok = false;
                for (int i = 0; i < keyBoard.size(); i++) {
                    if (!keyBoard.get(i).equals(okString1)) {
                        ok = false;
                        break;
                    } else {
                        ok = true;
                    }
                }
                String content;
                if (index == courseEntity.getKnowledge().size() + courseEntity.getPractice().size() - 1) {
                    Log.i(TAG, "index=size-1");
                    nextType = "no";
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
                if (ok) {
                    showPracticeDialog("yes", content);
                } else {
                    showPracticeDialog("no", content);
                }
            }
        }
    }

    private void returnKnowledge() {
        setGone();
        int practiceIndex = index - courseEntity.getKnowledge().size();
        Log.i(TAG, "practiceIndex=" + practiceIndex + " index=" + index);
        PracticeEntity practiceEntity = courseEntity.getPractice().get(practiceIndex);
        int position = Integer.valueOf(practiceEntity.getKnowPointId());
        Log.i(TAG, "position=" + position);
        keyBoard.clear();
        courseInterface.courseInterfaceResult(Constant.LAST_KNOWLEDGE, position - 1);
    }

    private void setGone() {
        if (scrollViewKeyBoard.getVisibility() == View.VISIBLE) {
            scrollViewKeyBoard.setVisibility(View.GONE);
        }
        if (practice2RelativeLayout.getVisibility() == View.VISIBLE) {
            practice2RelativeLayout.setVisibility(View.GONE);
        }
        if (practiceKeyBoard.getVisibility() == View.VISIBLE) {
            practiceKeyBoard.setVisibility(View.GONE);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            PracticeEntity practiceEntity1 = courseEntity.getPractice().get(index - courseEntity.getKnowledge().size());
            String okString = practiceEntity1.getRocResType().getIndex();

            KeyBoardModule keyBoardModule = new KeyBoardModule(Integer.valueOf(okString), Constant.UP, 127, 0);
            keyBoardModuleList.add(keyBoardModule);
            practiceKeyBoardModuleList.add(keyBoardModule);
            scrollViewKeyBoard.poina(keyBoardModuleList, 2);
            practiceKeyBoard.poina(practiceKeyBoardModuleList, 2);
            mHandler.removeCallbacks(runnable);

            if (keyBoard.contains(okString)) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PracticeEntity practiceEntity1 = courseEntity.getPractice().get(index - courseEntity.getKnowledge().size());
                        String okString = practiceEntity1.getRocResType().getIndex();

                        KeyBoardModule keyBoardModule = new KeyBoardModule(Integer.valueOf(okString), Constant.DOWN, 127, 0);
                        keyBoardModuleList.add(keyBoardModule);
                        practiceKeyBoardModuleList.add(keyBoardModule);
                        scrollViewKeyBoard.poina(keyBoardModuleList, 2);
                        practiceKeyBoard.poina(practiceKeyBoardModuleList, 2);
                    }
                }, 500);
            }
        }
    };

    @Override
    public void show(CourseEntity courseEntities, int index, String type) {
        this.PracticeType = type;
        this.index = index;
        Log.i(TAG, PracticeType);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initViewData();
            }
        }, 0);
    }

    private void showPracticeDialog(String type, String content) {
        if (practiceDialog == null) {
            practiceDialog = new PracticeDialog(activity, R.style.BlueToothDialogStyle, null);
            practiceDialog.setPracticeInterface(new PracticeDialog.PracticeInterface() {
                @Override
                public void result(String type) {
                    for (int i = 0; i < keyBoard.size(); i++) {
                        KeyBoardModule keyBoardModule = new KeyBoardModule(Integer.valueOf(keyBoard.get(i)), Constant.UP, 127, 0);
                        keyBoardModuleList.add(keyBoardModule);
                        practiceKeyBoardModuleList.add(keyBoardModule);
                    }
                    scrollViewKeyBoard.poina(keyBoardModuleList, 2);
                    practiceKeyBoard.poina(practiceKeyBoardModuleList, 2);
                    keyBoard.clear();
                    if ("2".equals(type)) {
                        Log.i(TAG, "nextType=" + nextType);
//                        setGone();

                        if (Constant.TOTAL_PRACTICE.equals(PracticeType) || Constant.NEXT_PRACTICE.equals(PracticeType)) {
                            if (index == courseEntity.getKnowledge().size() + courseEntity.getPractice().size() - 1) {
                                courseInterface.courseInterfaceResult(Constant.OLD_PRACTICE, -1);
                            } else {
                                courseInterface.courseInterfaceResult(Constant.NEXT_PRACTICE, index + 1);
                            }
                        } else {
                            if ("yes".equals(nextType)) {
                                courseInterface.courseInterfaceResult(Constant.NEXT_KNOWLEDGE, index + 1);
                            } else {
                                returnKnowledge();
                            }
                        }

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
        clean();
    }

    private void clean() {
        if (scrollViewKeyBoard != null) {
            scrollViewKeyBoard.clean();
            scrollViewKeyBoard.surfaceDestroyed(scrollViewKeyBoard.getHolder());
            scrollViewKeyBoard = null;
        }
        if (practiceKeyBoard != null) {
            practiceKeyBoard.clean();
            practiceKeyBoard.surfaceDestroyed(practiceKeyBoard.getHolder());
            practiceKeyBoard = null;
        }
    }
}
