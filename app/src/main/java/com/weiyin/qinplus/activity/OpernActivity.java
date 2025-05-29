package com.weiyin.qinplus.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.bluetooth.BlueToothControl;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.dialog.ConnectDialog;
import com.weiyin.qinplus.entity.OpernEntity;
import com.weiyin.qinplus.listener.InterfaceBlueConnect;
import com.weiyin.qinplus.listener.InterfaceBlueSet;
import com.weiyin.qinplus.rest.DataService;
import com.weiyin.qinplus.rest.RetrofitClient;
import com.weiyin.qinplus.ui.tv.bwstaff.DB.MidiJni;
import com.weiyin.qinplus.ui.tv.bwstaff.DB.TagWaterFall;
import com.weiyin.qinplus.ui.tv.bwstaff.FileUtil;
import com.weiyin.qinplus.ui.tv.bwstaff.ScoreController;
import com.weiyin.qinplus.ui.tv.bwstaff.ScorePlayerView;
import com.weiyin.qinplus.ui.tv.bwstaff.StatusModule;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;
import com.weiyin.qinplus.ui.tv.waterfall.KeyBoard;
import com.weiyin.qinplus.ui.tv.waterfall.MeasureNote;
import com.weiyin.qinplus.ui.tv.waterfall.WaterFallSurfaceView;
import com.weiyin.qinplus.usb.UsbController;
import com.weiyin.qinplus.usb.listener.UsbConnectListener;

import java.io.File;
import java.util.ArrayList;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app  曲谱的界面Activity
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class OpernActivity extends BaseActivity implements OnClickListener, UsbConnectListener, InterfaceBlueConnect {
    public final String TAG = OpernActivity.class.getSimpleName();
    private final int REQUEST_FINE_LOCATION = 0;

    /**
     * 播放, 录制, 线, 重新开始 , 伴奏, 节拍器, 瀑布流, 退出, 连接
     */

    private ImageView demonstrationImg, practiceImg, accompanimentImg, metronomeImg, waterfallFlowImg, projectileImg;

    private TextView demonstrationText;
    private TextView practiceText;
    private TextView accompanimentText;
    private TextView metronomeText;
    private TextView waterfallFlowText;
    private TextView playSpeedText;
    private TextView speedSizeText;
    private TextView projectileText;
    private DataService dataService;
    private RelativeLayout guiDeRl, rl, practiceRl, projectileRl, demonstrationRl,
            restartRl, speedSizeRl, metronomeRl, waterfallFlowRl, accompanimentRl, connectRl, opernOntouchRl;

    /**
     * 曲谱
     */
    private RelativeLayout frameLayoutQuPu, opern_activity_frame;

    /**
     * 钢琴键
     */
    private KeyBoard keyBoard;

    private String[] speedText = new String[3];

    private ScoreController bwStaff;
    private StatusModule statusModule;
    /**
     * 本地的数据库文件名字
     */
    private String midiPath;
    /**
     * PDF文件名字
     */
    private String pdfPath;
    /**
     * 数据库db文件
     */
    private String dbPath;
    /**
     * bluetooth_disconnected
     */
    private BlueToothControl blueToothControl;
    private UsbController usbController;
    private ArrayList<MeasureNote> standMeasureNotes;
    private ArrayList<TagWaterFall> tagWaterFallArrayList;
    private MidiJni midiJni;

    private WaterFallSurfaceView waterFallSurfaceView;

    private OpernEntity opernEntity;
    private int type;
    private FileUtil fileUtil;
    public static BaseActivity instance = null;

    private PowerManager.WakeLock wakeLock = null;

    private Bitmap bitmap;
    int speedTextSize = 2;


    private boolean keyboardOnclick = true;
    private LayoutHelper layoutHelper;

    RelativeLayout activityRl;

    private HorizontalScrollView keyboardPlaySongScrollView;

    private String musicBookName;

    private ImageView imageViewBack, imgConnect;

    private boolean bluetoothState;
    private int usbState = Integer.MAX_VALUE;


    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismiss();
            switch (msg.what) {
                case Constant.INTERNET_CALLBACK_FILE_DOWN:
                    try {
                        File file = (File) msg.obj;
                        if (accompanimentImg != null && accompanimentText != null && statusModule != null) {
                            accompanimentImg.setImageResource(R.drawable.accompaniment_opening_o);
                            setColor(accompanimentText);
                            statusModule.setAccompaniment(true);
                        }
                        if (file.getPath().contains(".mp3")) {
                            opernEntity.getMp3Path()[speedTextSize] = file.getPath();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showTextToast(OpernActivity.this, "文件出现错误");
                    }

                    break;
                case Constant.INTERNET_CALLBACK_FILE_DOWN_FAULT:
                    if (accompanimentImg != null && accompanimentText != null && statusModule != null) {
                        accompanimentImg.setImageResource(R.drawable.accompaniment_closure_o);
                        setColor(accompanimentText);
                        statusModule.setAccompaniment(false);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opern);
        layoutHelper = new LayoutHelper(this);
        layoutHelper.scaleView(findViewById(R.id.activity_opern_rl));

        PowerManager powerManager = (PowerManager) this.getSystemService(POWER_SERVICE);
        if(powerManager!=null){
            wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        }


        instance = this;
        midiJni = new MidiJni();
        Intent intent = getIntent();
        opernEntity = (OpernEntity) intent.getSerializableExtra("opernEntity");
        musicBookName = intent.getStringExtra("musicBookName");
//        opernEntity = new OpernEntity();
        dbPath = opernEntity.getDbPath();
        midiPath = opernEntity.getMidiPath();
        pdfPath = opernEntity.getPdfPath();
        usbController = UsbController.getUsbController();
        usbController.addUsbConnectListener(this);


        type = intent.getIntExtra("type", 0);
        if (dbPath == null) {
            finish();
            ToastUtil.showTextToast(this, "DB文件为空");
        }
        if (midiPath == null) {
            finish();
            ToastUtil.showTextToast(this, "midi文件为空");
        }
        if (pdfPath == null) {
            finish();
            ToastUtil.showTextToast(this, "pdf文件为空");
        }
        Log.i(TAG, pdfPath + "====" + midiPath + "=====" + dbPath);
        initView();
        CheckBlueData();
        InitData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //友盟统计计时
        wakeLock.acquire();
        if (bwStaff != null) {
            if (bwStaff.getDrawKeyBoardList() != null) {
                bwStaff.getDrawKeyBoardList().clear();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bwStaff != null) {
            bwStaff.pause();
        }
        if (statusModule.isDemonstration()) {
            statusModule.setDemonstration(false);
            demonstrationText.setText("继续");
            demonstrationImg.setImageResource(R.drawable.oper_play_o);
        }
        if (statusModule.isPractice()) {
            statusModule.setPractice(false);
            practiceImg.setImageResource(R.drawable.opern_practice_play);
            practiceText.setText("继续");
        }
        wakeLock.release();
    }

    @Override
    protected void onDestroy() {
        if (opernEntity != null) {
            boolean dbResult = FileUtil.deleteFile(opernEntity.getDbPath());
            boolean pdfResult = FileUtil.deleteFile(opernEntity.getPdfPath());
            boolean txtResult = FileUtil.deleteFile(opernEntity.getTxtPath());
            boolean midResult = FileUtil.deleteFile(opernEntity.getMidiPath() + ".mid");
            Log.i(TAG, "db=" + dbResult + " pdf=" + pdfResult + " txt=" + txtResult + " mid=" + midResult);
        }
        if (instance != null) {
            instance = null;
        }
        if (dbPath != null && midiPath != null && pdfPath != null) {
            if (bwStaff != null) {
                bwStaff.pause();
                bwStaff.removeDataCallBack();
            }
            statusModule.setOut(true);
            if (bwStaff != null) {
                bwStaff.stop();
            }
            if (statusModule != null) {
                statusModule.clean();
            }
            if (standMeasureNotes != null) {
                standMeasureNotes = null;
            }
            if (bwStaff != null) {
                bwStaff.clean();
                bwStaff = null;
            }
            if (keyBoard != null) {
                keyBoard.clean();
                keyBoard.surfaceDestroyed(keyBoard.getHolder());
                keyBoard = null;
            }
            if (waterFallSurfaceView != null) {
                waterFallSurfaceView.surfaceDestroyed(waterFallSurfaceView.getHolder());
                waterFallSurfaceView.recycle();
                waterFallSurfaceView = null;
            }
            if (midiJni != null) {
                midiJni = null;
            }
            if (opernEntity != null) {
                opernEntity = null;
            }
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
            }
            if (frameLayoutQuPu != null) {
                frameLayoutQuPu.removeAllViews();
            }
            if (rl != null) {
                rl = null;
            }

        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        WinYinPianoApplication.getInstance().removeActivityFromStack(this);
        System.gc();
        super.onDestroy();
    }

    private void initView() {
        dataService = new DataService(RetrofitClient.getInstance().getService());
        speedText[2] = getResources().getString(R.string.yuansu);
        speedText[1] = getResources().getString(R.string.mansu);
        speedText[0] = getResources().getString(R.string.chaomansu);

        statusModule = StatusModule.getStatusModule();
        statusModule.setAccompaniment(true);
        statusModule.setMetronome(true);
        statusModule.setDemonstration(false);
        statusModule.setPractice(false);
        statusModule.setWaterfall(false);
        statusModule.setRestart(false);
        statusModule.setVideo(false);
        statusModule.setWatefallview(true);
        statusModule.setType(type);
        statusModule.setOut(false);

        keyboardPlaySongScrollView = (HorizontalScrollView) findViewById(R.id.opern_keyboard_playsong_scrollew);
        rl = (RelativeLayout) findViewById(R.id.activity_opern_relativelayout);
        guiDeRl = (RelativeLayout) findViewById(R.id.opern_guiDeRl);
        practiceRl = (RelativeLayout) findViewById(R.id.opern_practice_Rl);
        projectileRl = (RelativeLayout) findViewById(R.id.opern_projectile_Rl);
        demonstrationRl = (RelativeLayout) findViewById(R.id.opern_demonstration_Rl);
        restartRl = (RelativeLayout) findViewById(R.id.opern_restart_Rl);
        speedSizeRl = (RelativeLayout) findViewById(R.id.opern_speed_size_Rl);
        metronomeRl = (RelativeLayout) findViewById(R.id.opern_metronome_Rl);
        waterfallFlowRl = (RelativeLayout) findViewById(R.id.opern_waterfall_flow_Rl);
        accompanimentRl = (RelativeLayout) findViewById(R.id.opern_accompaniment_Rl);
        connectRl = (RelativeLayout) findViewById(R.id.opern_connect_Rl);


        frameLayoutQuPu = (RelativeLayout) findViewById(R.id.opern_activity_main_frameLayout);
        opernOntouchRl = (RelativeLayout) findViewById(R.id.opernOntouchRl);
        activityRl = (RelativeLayout) findViewById(R.id.activity_opern_rl);
        keyBoard = (KeyBoard) findViewById(R.id.opern_keyboard_playsong);
        imageViewBack = (ImageView) findViewById(R.id.opern_back);
        demonstrationImg = (ImageView) findViewById(R.id.opern_demonstration_img);
        projectileImg = (ImageView) findViewById(R.id.opern_projectile_img);
        practiceImg = (ImageView) findViewById(R.id.opern_practice_img);
        accompanimentImg = (ImageView) findViewById(R.id.opern_accompaniment_img);
        metronomeImg = (ImageView) findViewById(R.id.opern_metronome_img);
        waterfallFlowImg = (ImageView) findViewById(R.id.opern_waterfall_flow_img);
        projectileText = (TextView) findViewById(R.id.opern_projectile_text);
        demonstrationText = (TextView) findViewById(R.id.opern_play_text);
        practiceText = (TextView) findViewById(R.id.opern_practice_text);
        accompanimentText = (TextView) findViewById(R.id.opern_accompaniment_text);
        metronomeText = (TextView) findViewById(R.id.opern_metronome_text);
        waterfallFlowText = (TextView) findViewById(R.id.opern_waterfall_flow_text);
        speedSizeText = (TextView) findViewById(R.id.opern_speed_size_text);
        playSpeedText = (TextView) findViewById(R.id.opern_play_speed_text);


        imgConnect = (ImageView) findViewById(R.id.opern_connect_img);

        bitmap = Constant.readBitMap(this, R.drawable.opern_title_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        rl.setBackground(bitmapDrawable);
        imageViewBack.setImageDrawable(getResources().getDrawable(R.drawable.opern_back));


        waterFallSurfaceView = (WaterFallSurfaceView) findViewById(R.id.opern_waterfall);
        waterFallSurfaceView.setVisibility(View.GONE);
        fileUtil = new FileUtil();
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        String strAddress = sp.getString("opernfrist", "");
        if (strAddress.equals("")) {
            SharedPreferences.Editor editorBle = sp.edit();
            editorBle.putString("opernfrist", "frist");


            editorBle.apply();
            editorBle.commit();
            guiDeRl.setVisibility(View.VISIBLE);
            guiDeRl.requestFocus();
        } else {
            guiDeRl.setVisibility(View.GONE);
            try {
                createBwStaff();
            } catch (Exception e) {
                ToastUtil.showTextToast(this, "加载错误，将返回上一个界面");
                e.printStackTrace();
                finish();
            }
        }

        imageViewBack.setOnClickListener(this);
        practiceRl.setOnClickListener(this);
        projectileRl.setOnClickListener(this);
        demonstrationRl.setOnClickListener(this);
        restartRl.setOnClickListener(this);
        speedSizeRl.setOnClickListener(this);
        metronomeRl.setOnClickListener(this);
        waterfallFlowRl.setOnClickListener(this);
        accompanimentRl.setOnClickListener(this);
        connectRl.setOnClickListener(this);

        guiDeRl.setOnClickListener(this);
        keyBoard.setOnClickListener(this);


        if (WinYinPianoApplication.isWaterfall) {
            waterfallFlowImg.setImageResource(R.drawable.music_mode_o);
            waterfallFlowText.setText("曲谱");
        } else {
            waterfallFlowImg.setImageResource(R.drawable.waterfall_flow_o);
            waterfallFlowText.setText("瀑布流");
        }


    }

    public void InitData() {
        if (opernEntity.getMp3Path() != null) {
            if (opernEntity.getMp3Path()[0] == null) {
                accompanimentImg.setImageResource(R.drawable.accompaniment_closure_h);
                accompanimentText.setTextColor(getResources().getColor(R.color.white_50));
                accompanimentRl.setEnabled(false);
            }
        }

        if (blueToothControl != null) {
            blueToothControl.setInterfaceBlueSet(new InterfaceBlueSet() {
                @Override
                public void onBlueSet(String value) {
                    if (value.equals("true")) {
                        if (usbController == null || !usbController.ismConnected()) {
                            if (blueToothControl != null) {
                                if (!blueToothControl.getConnectFlag()) {
                                    metronomeImg.setImageResource(R.drawable.close_metronome_h);
                                    metronomeText.setTextColor(getResources().getColor(R.color.white_50));
                                    metronomeRl.setEnabled(false);
                                } else {
                                    metronomeText.setTextColor(getResources().getColor(R.color.colorWhite));
                                    metronomeImg.setImageResource(R.drawable.open_metronome_o);
                                    metronomeRl.setEnabled(true);
                                }
                            }
                        }
                    }
                }
            });
            if (!blueToothControl.getConnectFlag()) {
                metronomeImg.setImageResource(R.drawable.close_metronome_h);
                metronomeText.setTextColor(getResources().getColor(R.color.white_50));
                metronomeRl.setEnabled(false);
            }
            if (usbController.ismConnected()) {
                metronomeRl.setEnabled(true);
                metronomeText.setTextColor(getResources().getColor(R.color.white));
            }
        }

        blueToothControl.addConnectedCallback(this);
        usbController.addUsbConnectListener(this);

        if (blueToothControl.getConnectFlag() || usbController.ismConnected()) {
            imgConnect.setImageDrawable(getResources().getDrawable(R.drawable.connect));

        } else {
            imgConnect.setImageDrawable(getResources().getDrawable(R.drawable.disconnect));
        }
    }

    /**
     * 得到蓝牙实例化对象
     */
    private void CheckBlueData() {
        blueToothControl = BlueToothControl.getBlueToothInstance();
        blueToothControl.addConnectedCallback(this);
    }

    public void setColor(TextView textView) {
        textView.setTextColor(getResources().getColor(R.color.colorWhite));
    }

    public void setGreyColor(TextView textView) {
        textView.setTextColor(getResources().getColor(R.color.white_50));
    }

    public void setImage(ImageView image, int drawable) {
        image.setImageResource(drawable);
    }

    public void startMediaPlay() {
        if (bwStaff.isFirst()) {
            mHandler.postDelayed(tempoChange, 0);
            speedSizeRl.setEnabled(false);

            setGreyColor(speedSizeText);
            setGreyColor(playSpeedText);

            accompanimentRl.setEnabled(false);

            setGreyColor(accompanimentText);
            if (statusModule.isAccompaniment()) {
                setImage(accompanimentImg, R.drawable.accompaniment_opening_h);
            } else {
                setImage(accompanimentImg, R.drawable.accompaniment_closure_h);
            }
            if (statusModule.isMetronome()) {
                setImage(metronomeImg, R.drawable.open_metronome_h);
            } else {
                setImage(metronomeImg, R.drawable.close_metronome_h);
            }
            metronomeRl.setEnabled(false);
            setGreyColor(metronomeText);

            if (!statusModule.isPractice()) {
                practiceRl.setEnabled(false);
                setImage(practiceImg, R.drawable.opern_practice_play_h);
                setGreyColor(practiceText);
            }

            if (!statusModule.isBounceForward()) {
                projectileRl.setEnabled(false);
                setImage(projectileImg, R.drawable.projectile_h);
                setGreyColor(projectileText);
            } else {
                waterfallFlowRl.setEnabled(false);
                waterfallFlowImg.setImageResource(R.drawable.waterfall_flow_h);
                setGreyColor(waterfallFlowText);
                waterfallFlowText.setText("瀑布流");
                statusModule.setWaterfall(false);
                if (bwStaff.getScoreMarkView() != null) {
                    if (bwStaff.isReduction() || !bwStaff.isFirst()) {
                        bwStaff.getScoreMarkView().setVisibility(View.VISIBLE);
                    }
                }
                WinYinPianoApplication.isWaterfall = false;
                waterFallSurfaceView.setVisibility(View.GONE);
                frameLayoutQuPu.setVisibility(View.VISIBLE);
            }
            if (!statusModule.isDemonstration()) {
                demonstrationRl.setEnabled(false);
                setGreyColor(demonstrationText);
                setImage(demonstrationImg, R.drawable.oper_play_h);
            }
        } else {
            if (bwStaff != null) {
                if (statusModule.isBounceForward()) {
                    bwStaff.getScorePlayerView().bounceForward();
                } else {
                    bwStaff.play();
                }

            }
        }
    }

    Runnable tempoChange = new Runnable() {
        @Override
        public void run() {
            if (speedSizeText.getText().toString().equals(getResources().getString(R.string.mansu))) {
                if (type == Constant.TWO_HAND) {
                    FileUtil.writeFile(midiPath + ".mid", midiPath + "_m8.mid", 0.8);
                    if (opernEntity.getMp3Path() != null && opernEntity.getMp3Path().length > 2 && opernEntity.getMp3Path()[2] != null) {
                        bwStaff.speed(0.8f, midiPath + "_m8.mid", opernEntity.getMp3Path()[2]);
                    } else {
                        bwStaff.speed(0.8f, midiPath + "_m8.mid", null);
                    }
                } else if (type == Constant.RIGHT_HAND) {
                    FileUtil.writeFile(midiPath + "_right.mid", midiPath + "_right_m8.mid", 0.8);
                    if (opernEntity.getMp3Path() != null && opernEntity.getMp3Path().length > 2 && opernEntity.getMp3Path()[2] != null) {
                        if (statusModule.isPractice()) {
                            bwStaff.speed(0.8f, midiPath + "_left_m8.mid", opernEntity.getMp3Path()[2]);
                        } else {
                            bwStaff.speed(0.8f, midiPath + "_right_m8.mid", opernEntity.getMp3Path()[2]);
                        }
                    } else {
                        if (statusModule.isPractice()) {
                            bwStaff.speed(0.8f, midiPath + "_left_m8.mid", null);
                        } else {
                            bwStaff.speed(0.8f, midiPath + "_right_m8.mid", null);
                        }
                    }
                } else if (type == Constant.LEFT_HAND) {
                    FileUtil.writeFile(midiPath + "_left.mid", midiPath + "_left_m8.mid", 0.8);

                    if (opernEntity.getMp3Path() != null && opernEntity.getMp3Path().length > 2 && opernEntity.getMp3Path()[2] != null) {
                        if (statusModule.isPractice()) {
                            bwStaff.speed(0.8f, midiPath + "_right_m8.mid", opernEntity.getMp3Path()[2]);
                        } else {
                            bwStaff.speed(0.8f, midiPath + "_left_m8.mid", opernEntity.getMp3Path()[2]);
                        }
                    } else {
                        if (statusModule.isPractice()) {
                            bwStaff.speed(0.8f, midiPath + "_right_m8.mid", null);
                        } else {
                            bwStaff.speed(0.8f, midiPath + "_left_m8.mid", null);
                        }
                    }
                }
                bwStaff.initWaterFallSurfaceViewData(fileUtil.updateSpeed(tagWaterFallArrayList, 0.8));
            } else if (speedSizeText.getText().toString().equals(getResources().getString(R.string.chaomansu))) {
                if (type == Constant.TWO_HAND) {
                    FileUtil.writeFile(midiPath + ".mid", midiPath + "_m6.mid", 0.6);
                    if (opernEntity.getMp3Path() != null && opernEntity.getMp3Path().length > 1 && opernEntity.getMp3Path()[1] != null) {
                        bwStaff.speed(0.6f, midiPath + "_m6.mid", opernEntity.getMp3Path()[1]);
                    } else {
                        bwStaff.speed(0.6f, midiPath + "_m6.mid", null);
                    }
                } else if (type == Constant.RIGHT_HAND) {
                    FileUtil.writeFile(midiPath + "_right.mid", midiPath + "_right_m6.mid", 0.6);
                    if (opernEntity.getMp3Path() != null && opernEntity.getMp3Path().length > 1 && opernEntity.getMp3Path()[1] != null) {
                        if (statusModule.isPractice()) {
                            bwStaff.speed(0.6f, midiPath + "_left_m6.mid", opernEntity.getMp3Path()[1]);
                        } else {
                            bwStaff.speed(0.6f, midiPath + "_right_m6.mid", opernEntity.getMp3Path()[1]);
                        }
                    } else {
                        if (statusModule.isPractice()) {
                            bwStaff.speed(0.6f, midiPath + "_left_m6.mid", null);
                        } else {
                            bwStaff.speed(0.6f, midiPath + "_right_m6.mid", null);
                        }
                    }
                } else if (type == Constant.LEFT_HAND) {
                    FileUtil.writeFile(midiPath + "_left.mid", midiPath + "_left_m6.mid", 0.6);
                    if (opernEntity.getMp3Path() != null && opernEntity.getMp3Path().length > 1 && opernEntity.getMp3Path()[1] != null) {
                        if (statusModule.isPractice()) {
                            bwStaff.speed(0.6f, midiPath + "_right_m6.mid", opernEntity.getMp3Path()[1]);
                        } else {
                            bwStaff.speed(0.6f, midiPath + "_left_m6.mid", opernEntity.getMp3Path()[1]);
                        }
                    } else {
                        if (statusModule.isPractice()) {
                            bwStaff.speed(0.6f, midiPath + "_right_m6.mid", null);
                        } else {
                            bwStaff.speed(0.6f, midiPath + "_left_m6.mid", null);
                        }
                    }
                }
                bwStaff.initWaterFallSurfaceViewData(fileUtil.updateSpeed(tagWaterFallArrayList, 0.6));
            } else if (speedSizeText.getText().toString().equals(getResources().getString(R.string.yuansu))) {
                if (type == Constant.TWO_HAND) {
                    if (opernEntity.getMp3Path() != null && opernEntity.getMp3Path().length > 0 && opernEntity.getMp3Path()[0] != null) {
                        bwStaff.speed(1f, midiPath + ".mid", opernEntity.getMp3Path()[0]);
                    } else {
                        bwStaff.speed(1f, midiPath + ".mid", null);
                    }
                } else if (type == Constant.RIGHT_HAND) {
                    if (opernEntity.getMp3Path() != null && opernEntity.getMp3Path().length > 0 && opernEntity.getMp3Path()[0] != null) {
                        if (statusModule.isPractice()) {
                            bwStaff.speed(1f, midiPath + "_left.mid", opernEntity.getMp3Path()[0]);
                        } else {
                            bwStaff.speed(1f, midiPath + "_right.mid", opernEntity.getMp3Path()[0]);
                        }
                    } else {
                        if (statusModule.isPractice()) {
                            bwStaff.speed(1f, midiPath + "_left.mid", null);
                        } else {
                            bwStaff.speed(1f, midiPath + "_right.mid", null);
                        }
                    }
                } else if (type == Constant.LEFT_HAND) {
                    if (opernEntity.getMp3Path() != null && opernEntity.getMp3Path().length > 0 && opernEntity.getMp3Path()[0] != null) {
                        if (statusModule.isPractice()) {
                            bwStaff.speed(1f, midiPath + "_right.mid", opernEntity.getMp3Path()[0]);
                        } else {
                            bwStaff.speed(1f, midiPath + "_left.mid", opernEntity.getMp3Path()[0]);
                        }
                    } else {
                        if (statusModule.isPractice()) {
                            bwStaff.speed(1f, midiPath + "_right.mid", null);
                        } else {
                            bwStaff.speed(1f, midiPath + "_left.mid", null);
                        }
                    }
                }
                bwStaff.initWaterFallSurfaceViewData(fileUtil.updateSpeed(tagWaterFallArrayList, 1));
            }
            if (bwStaff != null) {
                if (statusModule.isBounceForward()) {
                    bwStaff.getScorePlayerView().bounceForward();
                } else {
                    bwStaff.play();
                }
            }
        }
    };

    private void createBwStaff() {
        bwStaff = new ScoreController(this, dbPath, pdfPath, frameLayoutQuPu, keyBoard, waterFallSurfaceView, activityRl, opernOntouchRl);
        // 这里的midiJni 没有用到
        bwStaff.setMidiJni(midiJni);
        // 设置video 路径
        bwStaff.setRecodePath(midiPath + "_recorde");
        // 设置曲谱id
        bwStaff.setMusicId(opernEntity.musicid);
        // 从txt 文件中读取 瀑布流数据
        tagWaterFallArrayList = fileUtil.readFileByLinesAudio(opernEntity.getTxtPath());
        bwStaff.initWaterFallSurfaceViewData(tagWaterFallArrayList);
        waterfall();
        bwStaff.addDataCallBack();
        bwStaff.getScorePlayerView().setListener(new ScorePlayerView.InterfaceScoreControllerData() {
            @Override
            public void result(String s) {
                // 页面元素，状态初始化
                if (opernEntity != null) {
                    if (s.equals("true")) {
                        statusModule.setPractice(false);
                        practiceText.setText("练习");
                        practiceRl.setEnabled(true);
                        practiceImg.setImageResource(R.drawable.opern_practice_play);
                        setColor(practiceText);

                        statusModule.setBounceForward(false);
                        projectileImg.setImageResource(R.drawable.projectile_o);
                        setColor(projectileText);
                        projectileRl.setEnabled(true);
                        projectileText.setText("弹对前进");

                        statusModule.setDemonstration(false);
                        demonstrationText.setText("示范");
                        demonstrationRl.setEnabled(true);
                        demonstrationImg.setImageResource(R.drawable.oper_play_o);
                        setColor(demonstrationText);

                        speedSizeRl.setEnabled(true);
                        setColor(speedSizeText);
                        setColor(playSpeedText);

                        waterfallFlowRl.setEnabled(true);
                        if (statusModule.isWaterfall()) {
                            waterfallFlowImg.setImageResource(R.drawable.waterfall_flow_o);
                        } else {
                            waterfallFlowImg.setImageResource(R.drawable.music_mode_o);
                        }
                        setColor(waterfallFlowText);
                        if (blueToothControl.getConnectFlag() || usbController.ismConnected()) {
                            metronomeRl.setEnabled(true);
                            setColor(metronomeText);
                            if (statusModule.isMetronome()) {
                                setImage(metronomeImg, R.drawable.open_metronome_o);
                            } else {
                                setImage(metronomeImg, R.drawable.close_metronome_o);
                            }
                        } else {
                            metronomeRl.setEnabled(false);
                            setGreyColor(metronomeText);
                            setImage(metronomeImg, R.drawable.close_metronome_h);
                        }

                        if (opernEntity.getMp3Path() != null) {
                            if (opernEntity.getMp3Path()[0] != null) {
                                accompanimentRl.setEnabled(true);
                                setColor(accompanimentText);

                                if (statusModule.isAccompaniment()) {
                                    setImage(accompanimentImg, R.drawable.accompaniment_opening_o);
                                } else {
                                    setImage(accompanimentImg, R.drawable.accompaniment_closure_o);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void waterfall() {
        if (!statusModule.isWaterfall()) {
            if (!bwStaff.isFirst()) {
                bwStaff.getScoreMarkView().setVisibility(View.VISIBLE);
                if (!statusModule.isPractice() || !statusModule.isDemonstration()) {
                    if (bwStaff.getCurrentMeasureNo() > 0) {
                        bwStaff.getScorePlayerView().seekToMeasure(bwStaff.getCurrentMeasureNo() - 1);
                    } else {
                        bwStaff.getScorePlayerView().seekToMeasure(bwStaff.getCurrentMeasureNo());
                    }


                    bwStaff.getScorePlayerView().setMoveSlot(true);
                }
            }
        } else {
            if (bwStaff.isFirst() && !bwStaff.isCleanWater()) {
                waterFallSurfaceView.cleann();
            }
            if (!bwStaff.isFirst()) {
                bwStaff.getScoreWaterfall().setShutter((int) bwStaff.getMidiPlay().getCurrentPulseTime(), 0);
            }
        }
    }

    public void speed(int index) {
        if (opernEntity.getMp3Path() != null) {
            if (opernEntity.getMp3Path().length > 0 && opernEntity.getMp3Path()[0] != null) {
                String downMp3Path = opernEntity.getDownMp3Path()[index];
                showLoadingDialog();
                dataService.down(downMp3Path, OpernActivity.this, dataService, getLoadingDialog(), mHandler);


            }
        }
    }

    private void mayRequestLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要 向用户解释，为什么要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    ToastUtil.showTextToast(getApplicationContext(), "请允许开启定位，否则无法连接钢琴");
                }
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_FINE_LOCATION);
            } else {
            }
        } else {
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                /* If request is cancelled, the result arrays are empty. */
                /* The user disallowed the requested permission. */
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请手动前往设置允许应用获取位置信息，否则将无法连接蓝牙", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
    // 键盘切换处理
    private void handleKeyboardToggle(boolean expand) {
        keyboardOnclick = expand;
        mHandler.postDelayed(new Runnable() {
            @Override
            synchronized public void run() {
                keyBoard.setStarting(true);
                int width = expand ? 1024 : 2048;
                keyBoard.setMultiple(expand ? 1 : 2);

                waterFallSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(
                        layoutHelper.scaleDimension(width, "x"),
                        layoutHelper.scaleDimension(590, "y")));

                RelativeLayout.LayoutParams keyboardParams = new RelativeLayout.LayoutParams(
                        layoutHelper.scaleDimension(width, "x"),
                        layoutHelper.scaleDimension(108, "y"));
                keyboardParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                keyBoard.setLayoutParams(keyboardParams);

                mHandler.removeCallbacks(this);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        keyboardPlaySongScrollView.smoothScrollTo(expand ? 0 : 600, 0);
                        waterfall();
                        mHandler.removeCallbacks(this);
                        keyBoard.setEnabled(true);
                    }
                }, 50);
            }
        }, 0);
    }
    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.opern_activity_main_frameLayout) {
            LogUtil.i(TAG, "点击了");
        }
        else if (viewId == R.id.opern_connect_Rl) {
            // 蓝牙连接对话框
            mayRequestLocation();
            ConnectDialog bluetoothDialog = new ConnectDialog(this, R.style.ConnectDialogStyle);
            Window dialogWindow = bluetoothDialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            dialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP);

            int[] location = new int[2];
            connectRl.getLocationOnScreen(location);
            lp.x = (getWindowManager().getDefaultDisplay().getWidth() - location[0] - connectRl.getWidth()) * 2 / 10;
            lp.y = (location[1] + connectRl.getHeight());
            bluetoothDialog.show();
        }
        else if (viewId == R.id.opern_guiDeRl) {
            // 隐藏引导界面
            guiDeRl.setVisibility(View.GONE);
            guiDeRl = null;
            try {
                createBwStaff();
            } catch (Exception e) {
                ToastUtil.showTextToast(OpernActivity.this, "加载错误，将返回上一个界面");
                e.printStackTrace();
                finish();
            }
        }
        else if (viewId == R.id.opern_practice_Rl) {
            // 练习模式切换
            if (bwStaff != null) {
                if (statusModule.isPractice()) {
                    statusModule.setPractice(false);
                    practiceImg.setImageResource(R.drawable.opern_practice_play);
                    practiceText.setText("继续");
                    bwStaff.pause();
                } else {
                    // 练习模式需要 连接蓝牙，或者USB
                    if (blueToothControl.getConnectFlag() || usbController.ismConnected()) {
                        practiceImg.setImageResource(R.drawable.opern_practice_pause_o);
                        practiceText.setText("暂停");
                        statusModule.setPractice(true);
                        statusModule.setRestart(false);
                        startMediaPlay();
                    } else {
                        ToastUtil.showTextToast(this, "钢琴未连接,请用USB线或点击蓝牙进行连接钢琴");
                    }
                }
            }
        }
        else if (viewId == R.id.opern_demonstration_Rl) {
            // 示范模式切换
            if (bwStaff != null) {
                if (statusModule.isDemonstration()) {
                    statusModule.setDemonstration(false);
                    demonstrationText.setText("继续");
                    demonstrationImg.setImageResource(R.drawable.oper_play_o);
                    bwStaff.pause();
                } else {
                    statusModule.setDemonstration(true);
                    demonstrationText.setText("暂停");
                    demonstrationImg.setImageResource(R.drawable.oper_pause_o);
                    startMediaPlay();
                }
            }
        }
        else if (viewId == R.id.opern_projectile_Rl) {
            // 弹奏前进模式
            if (statusModule.isBounceForward()) {
                statusModule.setBounceForward(false);
                projectileImg.setImageResource(R.drawable.projectile_o);
                projectileText.setText("弹奏前进");
                bwStaff.getScorePlayerView().isSend = false;
            } else {
                if (blueToothControl.getConnectFlag() || usbController.ismConnected()) {
                    statusModule.setBounceForward(true);
                    projectileImg.setImageResource(R.drawable.opern_practice_pause_o);
                    projectileText.setText("暂停");
                    startMediaPlay();
                } else {
                    ToastUtil.showTextToast(this, "钢琴未连接,请用USB线或点击蓝牙进行连接钢琴");
                }
            }
        }
        else if (viewId == R.id.opern_metronome_Rl) {
            // 节拍器开关
            if (statusModule.isMetronome()) {
                metronomeImg.setImageResource(R.drawable.close_metronome_o);
                statusModule.setMetronome(false);
            } else {
                metronomeImg.setImageResource(R.drawable.open_metronome_o);
                statusModule.setMetronome(true);
            }
            metronomeText.setText("节拍器");
        }
        else if (viewId == R.id.opern_accompaniment_Rl) {
            // 伴奏开关
            if (statusModule.isAccompaniment()) {
                accompanimentImg.setImageResource(R.drawable.accompaniment_closure_o);
                statusModule.setAccompaniment(false);
            } else {
                accompanimentImg.setImageResource(R.drawable.accompaniment_opening_o);
                statusModule.setAccompaniment(true);
                speed(speedTextSize);
            }
        }
        else if (viewId == R.id.opern_speed_size_Rl) {
            // 速度调整
            if (--speedTextSize == -1) {
                speedTextSize = 2;
            }
            speedSizeText.setText(speedText[speedTextSize]);
            speed(speedTextSize);
        }
        else if (viewId == R.id.opern_restart_Rl) {
            // 重新开始
            if (bwStaff != null) {
                statusModule.setRestart(true);
                bwStaff.stop();
                ToastUtil.showTextToast(this, "准备就绪,再来一遍");
            }
        }
        else if (viewId == R.id.opern_waterfall_flow_Rl) {
            // 瀑布流模式切换
            if (bwStaff != null) {
                if (statusModule.isWaterfall()) {
                    // 关闭瀑布流
                    waterfallFlowImg.setImageResource(R.drawable.waterfall_flow_o);
                    waterfallFlowText.setText("瀑布流");
                    statusModule.setWaterfall(false);
                    if (bwStaff.getScoreMarkView() != null && (bwStaff.isReduction() || !bwStaff.isFirst())) {
                        bwStaff.getScoreMarkView().setVisibility(View.VISIBLE);
                    }
                    WinYinPianoApplication.isWaterfall = false;
                    waterFallSurfaceView.setVisibility(View.GONE);
                    frameLayoutQuPu.setVisibility(View.VISIBLE);
                } else {
                    // 开启瀑布流
                    waterfallFlowImg.setImageResource(R.drawable.music_mode_o);
                    waterfallFlowText.setText("曲谱");
                    if (bwStaff.getScoreMarkView() != null) {
                        bwStaff.getScoreMarkView().setVisibility(View.GONE);
                    }
                    waterFallSurfaceView.setVisibility(View.VISIBLE);
                    frameLayoutQuPu.setVisibility(View.GONE);
                    statusModule.setWaterfall(true);
                }
                waterfall();
            }
        }
        else if (viewId == R.id.opern_back) {
            // 返回
            finish();
        }
        else if (viewId == R.id.opern_keyboard_playsong) {
            // 键盘播放歌曲
            keyBoard.setEnabled(false);
            if (keyboardOnclick) {
                handleKeyboardToggle(false);
            } else {
                handleKeyboardToggle(true);
            }
        }


    }

    @Override
    public void result(int openClose) {
        usbState = openClose;


        if (bwStaff != null) {

            if(bwStaff.getMidiPlay()!=null){
                bwStaff.getMidiPlay().setPadVolumeConnect(bluetoothState, usbState);
            }

        }
        if (openClose == 0) {

            metronomeImg.setEnabled(true);
            metronomeText.setEnabled(true);


        } else {
            if (bwStaff != null) {
                if (statusModule != null) {
                    if (statusModule.isPractice()) {
                        statusModule.setPractice(false);
                        practiceImg.setImageResource(R.drawable.opern_practice_play);
                        practiceText.setText("继续");
                        bwStaff.pause();
                    }
                }
            }
        }
        //改变  首页连接图标的图片
        if (bluetoothState || usbState == 0) {
            imgConnect.setImageDrawable(getResources().getDrawable(R.drawable.connect));

        } else {
            imgConnect.setImageDrawable(getResources().getDrawable(R.drawable.disconnect));
        }
    }


    @Override
    public void resultString(String result) {

    }

    @Override
    public void onConnectStatusChanged(String name, String address, boolean bConnect) {

        bluetoothState = bConnect;

        //当蓝牙连接状态发声改变  改变pad的发声大小
        if (bwStaff != null) {
            if(bwStaff.getMidiPlay()!=null){
                bwStaff.getMidiPlay().setPadVolumeConnect(bluetoothState, usbState);
            }

        }
        if (!bConnect) {
            if (bwStaff != null) {
                if (statusModule != null) {
                    if (statusModule.isPractice()) {
                        statusModule.setPractice(false);
                        practiceImg.setImageResource(R.drawable.opern_practice_play);
                        practiceText.setText("继续");
                        bwStaff.pause();
                    }
                }
            }


        }

        if (bluetoothState || usbState == 0) {
            imgConnect.setImageDrawable(getResources().getDrawable(R.drawable.connect));

        } else {
            imgConnect.setImageDrawable(getResources().getDrawable(R.drawable.disconnect));
        }

    }

}