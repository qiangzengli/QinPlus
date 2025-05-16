package com.weiyin.qinplus.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.activity.course.AbstractCourse;
import com.weiyin.qinplus.activity.course.Course1;
import com.weiyin.qinplus.activity.course.Course2;
import com.weiyin.qinplus.activity.course.Course3;
import com.weiyin.qinplus.activity.course.Practice1;
import com.weiyin.qinplus.activity.course.Practice2;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.dialog.LoadingDialog;
import com.weiyin.qinplus.entity.CourseEntity;
import com.weiyin.qinplus.entity.KnowledgeEntity;
import com.weiyin.qinplus.entity.MainPracticeEntity;
import com.weiyin.qinplus.entity.PracticeEntity;
import com.weiyin.qinplus.listener.CourseInterface;
import com.weiyin.qinplus.rest.DataService;
import com.weiyin.qinplus.rest.RetrofitClient;
import com.weiyin.qinplus.ui.tv.bwstaff.FileUtil;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app  音乐教室Activity
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class PracticeActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, View.OnClickListener, CourseInterface {
    public static final String TAG = PracticeActivity.class.getSimpleName();
    private ViewFlipper practiceViewFlipper;

    private List<AbstractCourse> courseList;

    private CourseEntity courseEntity;

    private String jsonPath, filePath;

    private int currentStep, currentIndex, midIndex, old = -1, oldKnowledge = -1;
    private List<String> midList, downMidList;
    private Map<Integer, Integer> indexList;
    private TextView textView;
    private MainPracticeEntity mainPracticeEntity;

    private LoadingDialog loadingDialog;
    private Bitmap bitmap;
    private DataService dataService;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.INTERNET_CALLBACK_FILE_DOWN:
                    try {
                        File file = (File) msg.obj;
                        if (file.getName().contains(".mid")) {
                            downMidList.add(file.getPath());
                            midIndex++;
                            if (midList.size() > midIndex) {
                                Log.i(TAG, "midIndex=" + midIndex);
                                dataService.down(midList.get(midIndex), PracticeActivity.this, dataService, loadingDialog, mHandler);
                            }
                        } else {
                            filePath = file.getPath();
                            readFile(file.getPath());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        /* Constant.showTextToast(MusicDetailActivity.this,"文件出现错误"); */
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void readFile(String fullFilename) {
        String readOutStr;
        try {
            long len;
            try (DataInputStream dis = new DataInputStream(new FileInputStream(fullFilename))) {
                len = new File(fullFilename).length();
                if (len > Integer.MAX_VALUE) {
                    Log.i(TAG, "File " + fullFilename + " too large, was " + len + " bytes.");
                }
                byte[] bytes = new byte[(int) len];
                dis.readFully(bytes);
                readOutStr = new String(bytes, "UTF-8");
                Gson gson = new Gson();
                JSONObject jsonObject = new JSONObject(readOutStr);
                courseEntity = gson.fromJson(jsonObject.toString(), CourseEntity.class);
                mHandler.post(downMidiFile);
                initSetData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("readFileContentStr", "Successfully to read out string from file " + fullFilename);
        } catch (IOException e) {
            Log.d("readFileContentStr", "Fail to read out string from file " + fullFilename);
        }
    }

    Runnable downMidiFile = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < courseEntity.getKnowledge().size(); i++) {
                KnowledgeEntity knowledgeEntity = courseEntity.getKnowledge().get(i);
                if (knowledgeEntity.getAudio() != null) {
                    midList.add(Constant.PRACTICE_PATH + knowledgeEntity.getAudio());
                }
                if (knowledgeEntity.getAudio1() != null) {
                    midList.add(Constant.PRACTICE_PATH + knowledgeEntity.getAudio1());
                }
                if (knowledgeEntity.getAudio2() != null) {
                    midList.add(Constant.PRACTICE_PATH + knowledgeEntity.getAudio2());
                }
            }
            if (midList.size() > 0) {
                midIndex = 0;
                dataService.down(midList.get(midIndex), PracticeActivity.this, dataService, loadingDialog, mHandler);
            }
            mHandler.removeCallbacks(this);
        }
    };

    private void initSetData() {
        if (courseEntity != null) {
            boolean isBreak = false;
            List<KnowledgeEntity> knowledgeEntities = courseEntity.getKnowledge();
            int index = -1;
            for (int i = 0; i < knowledgeEntities.size(); i++) {
                KnowledgeEntity knowledgeEntity = knowledgeEntities.get(i);
                switch (knowledgeEntity.getType()) {
                    case "1":
                        index++;
                        indexList.put(index, index);
                        courseList.add(new Course1());
                        break;
                    case "2":
                        index++;
                        indexList.put(index, index);
                        courseList.add(new Course2());
                        break;
                    case "3":
                        index++;
                        indexList.put(index, index);
                        courseList.add(new Course3());
                        break;
                    default:
                        ToastUtil.showTextToast(this, "敬请期待");
                        isBreak = true;
                        break;
                }
            }
            List<PracticeEntity> practiceEntities = courseEntity.getPractice();
            boolean start = false;
            int oldIndex = 0;
            for (int i = 0; i < practiceEntities.size(); i++) {
                PracticeEntity practiceEntity = courseEntity.getPractice().get(i);
                switch (practiceEntity.getType()) {
                    case "1":
                        index++;
                        indexList.put(index, index);
                        courseList.add(new Practice1());
                        break;
                    case "2":
                        index++;
                        if (!start) {
                            oldIndex = index;
                            courseList.add(new Practice2());
                            indexList.put(index, index);
                            start = true;
                        } else {
                            indexList.put(index, oldIndex);
                        }

                        break;
                    default:
                        ToastUtil.showTextToast(this, "敬请期待");
                        isBreak = true;
                        break;
                }
            }
            if (!isBreak) {
                for (int i = 0; i < courseList.size(); i++) {
                    AbstractCourse step = courseList.get(i);
                    step.onCreate(this, practiceViewFlipper, courseEntity, i);
                    step.setCourseInterface(this);
                }
                currentStep = 0;
                currentIndex = 0;
                showNext(Constant.NEXT_KNOWLEDGE);
            }
        }
        setTitle();
    }

    @SuppressLint("SetTextI18n")
    private void setTitle() {
        textView = (TextView) findViewById(R.id.titleContent);
        ImageView imageView = (ImageView) findViewById(R.id.titleBack);

        textView.setText(mainPracticeEntity.getName() + "   " + mainPracticeEntity.getTitle() + "（知识点）");
        imageView.setOnClickListener(this);
    }

    @SuppressLint("UseSparseArrays")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        dataService = new DataService(RetrofitClient.getInstance().getService());
        if (courseList == null) {
            courseList = new ArrayList<>();
        }
        if (midList == null) {
            midList = new ArrayList<>();
        }
        if (downMidList == null) {
            downMidList = new ArrayList<>();
        }
        if (indexList == null) {
            indexList = new HashMap<>();
        }
        mainPracticeEntity = (MainPracticeEntity) getIntent().getSerializableExtra("MainPracticeEntity");
        Constant.PRACTICE_PATH = mainPracticeEntity.getConfigUrl().substring(0, mainPracticeEntity.getConfigUrl().lastIndexOf("/") + 1);
        jsonPath = mainPracticeEntity.getConfigUrl();
        LayoutHelper layoutHelper = new LayoutHelper(this);
        layoutHelper.scaleView(findViewById(R.id.practice_layout));
        initView();
    }

    private void initView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.practice_layout);
        bitmap = Constant.readBitMap(this, R.drawable.opern_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        relativeLayout.setBackground(bitmapDrawable);

        practiceViewFlipper = (ViewFlipper) findViewById(R.id.practiceViewFlipper);
        loadingDialog = new LoadingDialog(this, R.style.BlueToothDialogStyle, "加载中 · · · ");

        mHandler.post(downJson);
    }

    Runnable downJson = new Runnable() {
        @Override
        public void run() {
            dataService.down(jsonPath, PracticeActivity.this, dataService, loadingDialog, mHandler);
            mHandler.removeCallbacks(this);
        }
    };

    @Override
    protected void onDestroy() {
        if (filePath != null) {
            boolean result = FileUtil.deleteFile(filePath);
            Log.i(TAG, "result=" + result);
        }
        if (downMidList.size() > 0) {
            for (int i = 0; i < downMidList.size(); i++) {
                boolean result = FileUtil.deleteFile(downMidList.get(i));
                Log.i(TAG, "result=" + result);
            }
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        for (int i = 0; i < courseList.size(); i++) {
            courseList.get(i).destroy();
        }
        super.onDestroy();
    }

//    @Override
//    public void onBackPressed() {
//        if (JCVideoPlayer.backPress()) {
//            return;
//        }
//        super.onBackPressed();
//    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
        if (courseList != null) {

            if (courseList.size() > currentStep) {
                courseList.get(currentStep).stop();
            }
        }
//        courseList.get(currentStep).stop();
    }

    private void showNext(String type) {
        setAnimation();
        if (currentStep == 0 || practiceViewFlipper.getDisplayedChild() != currentStep) {
            Log.i(TAG, "currentStep=" + currentStep);
            practiceViewFlipper.setDisplayedChild(currentStep);
        }
        Log.i(TAG, "type=" + type);
        if (courseList.size() > currentStep) {
            courseList.get(currentStep).show(courseEntity, currentIndex, type);
        }
        if (Constant.TOTAL_PRACTICE.equals(type)) {
            oldKnowledge = old;
            Log.i(TAG, "oldKnowledge=" + oldKnowledge);
        }
        old = currentStep;
    }

    private void showBack(String type) {
        setBackAnimation();
        if (practiceViewFlipper.getDisplayedChild() != currentStep) {
            practiceViewFlipper.setDisplayedChild(currentStep);
        }
        courseList.get(currentStep).show(courseEntity, currentIndex, type);
        if (Constant.TOTAL_PRACTICE.equals(type)) {
            oldKnowledge = old;
            Log.i(TAG, "oldKnowledge=" + oldKnowledge);
        }
        old = currentStep;
    }

    private void setAnimation() {
        practiceViewFlipper.setInAnimation(this, R.anim.general_right_in);
        practiceViewFlipper.setOutAnimation(this, R.anim.general_left_out);
    }

    private void setBackAnimation() {
        practiceViewFlipper.setInAnimation(this, R.anim.general_left_in);
        practiceViewFlipper.setOutAnimation(this, R.anim.general_right_out);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        if (e1.getX() - e2.getX() > 120) {
//            if (currentStep < 3) {
//                showNext();
//            }
//            return true;
//        } else if (e1.getX() - e2.getX() < -120) {
//            if (currentStep > 0) {
//                showBack();
//            }
//            return true;
//        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.titleBack) {
            finish();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void courseInterfaceResult(String type, int index) {
        if (textView != null) {
            if (index >= courseEntity.getKnowledge().size()) {
                textView.setText(mainPracticeEntity.getName() + "   " + mainPracticeEntity.getTitle() + "（练习）");
            } else {
                textView.setText(mainPracticeEntity.getName() + "   " + mainPracticeEntity.getTitle() + "（知识点）");
            }
        }
        switch (type) {
            case Constant.NEXT_KNOWLEDGE:
                Log.i(TAG, "index=" + index);
                currentIndex = index;
                currentStep = indexList.get(index);
                showNext(Constant.NEXT_KNOWLEDGE);
                break;
            case Constant.LAST_KNOWLEDGE:
                Log.i(TAG, "index=" + index);
                currentIndex = index;
                currentStep = indexList.get(index);
                showBack(Constant.LAST_KNOWLEDGE);
                break;
            case Constant.TOTAL_PRACTICE:
                Log.i(TAG, "index=" + index);
                currentIndex = index;
                currentStep = indexList.get(index);
                showNext(Constant.TOTAL_PRACTICE);
                break;
            case Constant.OLD_PRACTICE:
                currentIndex = index;
                currentStep = indexList.get(oldKnowledge);
                showNext(Constant.OLD_PRACTICE);
                break;
            case Constant.NEXT_PRACTICE:
                Log.i(TAG, "index=" + index);
                currentIndex = index;
                currentStep = indexList.get(index);
                showNext(Constant.NEXT_PRACTICE);
                break;
            default:
                break;
        }
    }
}