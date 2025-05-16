package com.weiyin.qinplus.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.commontool.Conver;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.entity.ScoreEntity;
import com.weiyin.qinplus.rest.BizException;
import com.weiyin.qinplus.rest.DataService;
import com.weiyin.qinplus.rest.HttpRequestListener;
import com.weiyin.qinplus.rest.RequestConfigs;
import com.weiyin.qinplus.rest.RetrofitClient;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;
import com.weiyin.qinplus.ui.tv.utils.Utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app  成绩界面Activity
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class ScoreActivity extends BaseActivity implements View.OnClickListener, HttpRequestListener<ResponseBody> {
    public final String TAG = "ScoreActivity";

    private RelativeLayout leftLayout;

    private Animation operatingAnim;

    private String state = "", userId;

    private double intonationScoreHeight, intensityScoreHeight, rhythmScoreHeight;

    private double intonationScore;
    private double intensityScore;
    private double rhythmScore;
    private double totalScore;
    private ScoreEntity scoreEntity;

    private PowerManager.WakeLock wakeLock = null;

    private LayoutHelper layoutHelper;

    private Bitmap bitmap;
    private DataService dataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_layout);
        layoutHelper = new LayoutHelper(this);
        layoutHelper.scaleView(findViewById(R.id.score_layout));
        dataService = new DataService(RetrofitClient.getInstance().getService());
        PowerManager powerManager = (PowerManager) this.getSystemService(POWER_SERVICE);
        assert powerManager != null;
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        Log.i(TAG, "进入了");
        initData();
        int ioldPagenum = 0;
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*10 minutes*/
        wakeLock.acquire(10 * 60 * 1000L);
    }

    @Override
    protected void onStop() {
        super.onStop();
        wakeLock.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
        }

        if (operatingAnim != null) {
            operatingAnim = null;
        }
        if (scoreEntity != null) {
            scoreEntity = null;
        }

        if (leftLayout != null) {
            leftLayout.removeAllViews();
            leftLayout = null;
        }
        if (userId != null) {
            userId = null;
        }
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            state = intent.getStringExtra(Constant.STATE);
        }
        if (state == null) {
            finish();
        }
        assert intent != null;
        String path = intent.getStringExtra(Constant.PATH);

        Log.i(TAG, "路徑=" + path);
        scoreEntity = (ScoreEntity) intent.getSerializableExtra(Constant.SCORE_ENTITY);
        intensityScore = scoreEntity.dynamics;
        intonationScore = scoreEntity.intonation;
        rhythmScore = scoreEntity.rhythm;
        totalScore = intensityScore * 0.2 + intonationScore * 0.5 + rhythmScore * 0.3;
        scoreEntity.totalscore = totalScore;
        if (intensityScore != 0) {
            intensityScoreHeight = (intensityScore / 100) * layoutHelper.scaleDimension(153, "y");
        } else {
            intensityScoreHeight = 1;
        }

        if (intonationScore != 0) {
            intonationScoreHeight = (intonationScore / 100) * layoutHelper.scaleDimension(153, "y");
        } else {
            intonationScoreHeight = 1;
        }

        if (rhythmScore != 0) {
            rhythmScoreHeight = (rhythmScore / 100) * layoutHelper.scaleDimension(153, "y");
        } else {
            rhythmScoreHeight = 1;
        }
        DecimalFormat df = new DecimalFormat("0.0");
        if (intensityScore != 0) {
            if (intensityScore != 100) {
                intensityScore = Double.parseDouble(df.format(intensityScore));
            }
        }
        if (rhythmScore != 0) {
            if (rhythmScore != 100) {
                rhythmScore = Double.parseDouble(df.format(rhythmScore));
            }
        }
        if (totalScore != 0) {
            if (totalScore != 100) {
                totalScore = Double.parseDouble(df.format(totalScore));
            }
        }
        if (intonationScore != 0) {
            if (intonationScore != 100) {
                intonationScore = Double.parseDouble(df.format(intonationScore));
            }
        }
        putPlayRecord(scoreEntity.musicid, String.valueOf(totalScore), String.valueOf(intonationScore),
                String.valueOf(rhythmScore), String.valueOf(intensityScore), scoreEntity.startTime,
                scoreEntity.musictime);
    }

    /**
     * @param musicId    曲谱id
     * @param score      总成绩
     * @param intonation 音准成绩
     * @param rhythm     节奏成绩
     * @param intensity  力度成绩
     * @param startDate  开始时间戳
     * @param endTime    结束时间戳
     */
    private void putPlayRecord(String musicId, String score, String intonation, String rhythm,
                               String intensity, long startDate, long endTime) {
        Map<String, String> map = new HashMap<>();
        String uuid = Utils.getMyUUID(this);
        String startTimeDate = Conver.transferLongToDate("yyyy-MM-dd", startDate);
        String startTime = Conver.transferLongToDate("HH:mm:ss", startDate);
        long playLong = (endTime - startDate) / 1000;
        LogUtil.i(TAG, "putPlayRecord startTimeDate=" + startTimeDate + " startTime=" + startTime + " playLong=" + playLong + " musicId=" + musicId + " WinYinPianoApplication.strUid=" + WinYinPianoApplication.strUid);
        map.put("appVersion", Constant.getVersionName(this.getApplicationContext()));
        map.put("platformType", Constant.CODE);
        map.put("userId", WinYinPianoApplication.strUid);
        map.put("musicId", musicId);
        map.put("score", score);
        map.put("intonation", intonation);
        map.put("rhythm", rhythm);
        map.put("intensity", intensity);
        map.put("startDate", startTimeDate);
        map.put("startTime", startTime);
        map.put("playLong", String.valueOf(playLong));
        map.put("deviceId", uuid);
        map.put("deviceModel", "");
        dataService.putPlayRecord(map, this);
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.score_layout);
        bitmap = Constant.readBitMap(this, R.drawable.opern_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        relativeLayout.setBackground(bitmapDrawable);

        /* 力度得分 */
        TextView scoreLayoutTextIntensity = (TextView) findViewById(R.id.score_layout_text_intensity);
        TextView scoreLayoutTextIntonation = (TextView) findViewById(R.id.score_layout_text_intonation);
        TextView scoreLayoutTextRhythm = (TextView) findViewById(R.id.score_layout_text_rhythm);
        /* 总分 */
        TextView scoreLayoutTextTotal = (TextView) findViewById(R.id.score_total_score);

        RelativeLayout scorePlayAgainRl = (RelativeLayout) findViewById(R.id.score_play_again_rl);
        RelativeLayout scoreReselectionRepertoireRl = (RelativeLayout) findViewById(R.id.score_reselection_repertoire_rl);
        ImageView back = (ImageView) findViewById(R.id.score_back);

        leftLayout = (RelativeLayout) findViewById(R.id.score_layout_left_rl);

        scorePlayAgainRl.setOnClickListener(this);
        scoreReselectionRepertoireRl.setOnClickListener(this);
        back.setOnClickListener(this);

        if (intensityScoreHeight != 0) {
            ImageView imageViewIntensity = new ImageView(ScoreActivity.this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(40, (int) intensityScoreHeight);
            layoutParams.topMargin = (int) (layoutHelper.scaleDimension(305, "y") - intensityScoreHeight);
            layoutParams.leftMargin = layoutHelper.scaleDimension(315, "x");
            Log.i(TAG, "力度=" + layoutParams.leftMargin);
            imageViewIntensity.setLayoutParams(layoutParams);
            imageViewIntensity.setBackgroundResource(R.drawable.error_score);
            leftLayout.addView(imageViewIntensity);
        }

        scoreLayoutTextIntensity.setText("" + ((int) intensityScore));
        if (intonationScoreHeight != 0) {
            ImageView imageViewIntonation = new ImageView(ScoreActivity.this);
            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(40, (int) intonationScoreHeight);
            layoutParams1.topMargin = layoutHelper.scaleDimension(305, "y") - (int) intonationScoreHeight;
            layoutParams1.leftMargin = layoutHelper.scaleDimension(455, "x");
            Log.i(TAG, "音准=" + layoutParams1.leftMargin);
            imageViewIntonation.setLayoutParams(layoutParams1);
            imageViewIntonation.setBackgroundResource(R.drawable.error_score);
            leftLayout.addView(imageViewIntonation);
        }
        scoreLayoutTextIntonation.setText("" + ((int) intonationScore));
        if (rhythmScoreHeight != 0) {
            ImageView imageViewRhythm = new ImageView(ScoreActivity.this);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(40, (int) rhythmScoreHeight);
            layoutParams2.topMargin = layoutHelper.scaleDimension(305, "y") - (int) rhythmScoreHeight;
            layoutParams2.leftMargin = layoutHelper.scaleDimension(600, "x");
            Log.i(TAG, "节奏=" + layoutParams2.leftMargin);
            imageViewRhythm.setLayoutParams(layoutParams2);
            imageViewRhythm.setBackgroundResource(R.drawable.error_score);
            leftLayout.addView(imageViewRhythm);
        }
        scoreLayoutTextRhythm.setText("" + ((int) rhythmScore));

        scoreLayoutTextTotal.setText("总分：" + ((int) totalScore));

        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.activity_diy_blacktap_loading);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick");
        if (v.getId() == R.id.score_play_again_rl) {
            finish();
        } else if (v.getId() == R.id.score_reselection_repertoire_rl) {
            if (OpernActivity.instance != null) {
                OpernActivity.instance.finish();
            }
            if (MusicDetailActivity.instance != null) {
                MusicDetailActivity.instance.finish();
            }
            finish();
        } else if (v.getId() == R.id.score_back) {
            finish();
        }
    }

    @Override
    public void onCompleted(String url) {

    }

    @Override
    public void onError(BizException e) {

    }

    @Override
    public void onNext(ResponseBody responseBody, String url) {
        switch (url) {
            case RequestConfigs.PUT_PLAY_RECORD:
                try {
                    LogUtil.i(TAG, "onNext=" + responseBody.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
