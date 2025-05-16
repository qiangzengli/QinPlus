package com.weiyin.qinplus.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.changemiditrack.FileUri;
import com.weiyin.qinplus.changemiditrack.MidiFile;
import com.weiyin.qinplus.changemiditrack.MidiOptions;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.dialog.MusicDetailAbstractDialog;
import com.weiyin.qinplus.entity.MusicAboutEntity;
import com.weiyin.qinplus.entity.MusicDetailEntity;
import com.weiyin.qinplus.entity.OpernEntity;
import com.weiyin.qinplus.rest.BizException;
import com.weiyin.qinplus.rest.DataService;
import com.weiyin.qinplus.rest.HttpRequestListener;
import com.weiyin.qinplus.rest.RequestConfigs;
import com.weiyin.qinplus.rest.RetrofitClient;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

import static com.weiyin.qinplus.application.Constant.LEFT_HAND;
import static com.weiyin.qinplus.application.Constant.RIGHT_HAND;
import static com.weiyin.qinplus.application.Constant.TWO_HAND;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 曲目详情
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class MusicDetailActivity extends BaseActivity implements HttpRequestListener<ResponseBody>, View.OnClickListener {
    public final String TAG = MusicDetailActivity.class.getSimpleName();
    public static BaseActivity instance = null;
    private int leftRightIndex = 0;
    private String musicId, musicName, musicUrl;

    private File fileDb, fileMidi, filePdf, fileTxt;
    private String[] fileMp3 = new String[3];
    private String[] downFileMp3 = new String[3];

    private MidiFile midifile;
    private MidiOptions options;

    private MusicDetailEntity detailEntity;
    private boolean isStart = true, downMp3 = false;
    private String musicCollection = "0";
    private DataService dataService;
    private String musicBookName;
    /**
     * 曲谱名,Title曲谱名, 书名, 作者 ,地区
     */
    private TextView bookNameText, bookTitleName, musicBookNameText, musicAuthorText, musicAuthorDistractText,
            musicCollectionText, musicTwoHandText, loadingTextView;

    /**
     * 难度
     */
    private RatingBar ratingbar;

    private ImageView imageview, musicWwoHandImage, musicCollectionImage, loadingImageView, loadingFaultImageView;

    private MusicDetailAbstractDialog musicDetailAbstractDialog;

    private RelativeLayout loadingFaultRl, classicalButtonRl, backRl;
    private AnimationDrawable animationDrawable;
    private Bitmap bitmap;

    @SuppressLint("HandlerLeak")
    Handler handlerUI = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (classicalButtonRl.getVisibility() == View.VISIBLE) {
                stopLoading();
                classicalButtonRl.setVisibility(View.GONE);
            }
            switch (msg.what) {
                case Constant.INTERNET_CALLBACK_MUSIC_ITEM_DETAIL:
                    setData();
                    break;
                case Constant.INTERNET_CALLBACK_FILE_DOWN:
                    try {
                        File file = (File) msg.obj;
                        skip(file);
                    } catch (Exception e) {
                        e.printStackTrace();
/* Constant.showTextToast(MusicDetailActivity.this,"文件出现错误"); */
                    }
                    break;
                case Constant.RESPONESE_EXCEPTION:

                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("SetTextI18n")
    public void setData() {
        Glide.with(this).load(WinYinPianoApplication.strUrl + musicUrl).into(imageview);
        bookTitleName.setText(musicName);
        if (musicName.contains("（")) {
            musicName = musicName.replace("（", "|");
        }
        if (musicName.contains("）")) {
            musicName = musicName.replace("）", "");
        }
        if (musicName.contains("(")) {
            musicName = musicName.replace("(", "|");
        }
        if (musicName.contains(")")) {
            musicName = musicName.replace(")", "");


        }

        musicName=spaceAt1(musicName);
        Log.e(TAG,musicName);
        //bookNameText.setText("我\n和\n你\n微\nf\nl\nl\nl\nl\nl\nd\nk\nj");


        bookNameText.setText(musicName);

        musicBookNameText.setText("书名：" + detailEntity.getMusicBookName());
        musicBookName = detailEntity.getMusicBookName();
        musicAuthorText.setText("作者：" + detailEntity.getMusicAuthor());
        musicAuthorDistractText.setText("地区：" + detailEntity.getMusicAuthorDiatrict());

        Log.i(TAG, detailEntity.getMusicDifficultLevel());

        if (StringUtils.isEmpty(detailEntity.getMusicDifficultLevel())) {
            detailEntity.setMusicDifficultLevel("0");
        }
        ratingbar.setRating(Float.valueOf(detailEntity.getMusicDifficultLevel()));


        musicCollection = detailEntity.getIsCollect();
        LogUtil.i(TAG, "musicCollection=" + musicCollection);
        if ("1".equals(musicCollection)) {
            musicCollectionImage.setImageResource(R.drawable.collection_f);
            musicCollectionText.setText("取消收藏");
        } else {
            musicCollectionImage.setImageResource(R.drawable.collection_o);
            musicCollectionText.setText("收藏");
        }
    }

    private void stopLoading() {
        animationDrawable.stop();
        loadingImageView.setVisibility(View.GONE);
        loadingFaultImageView.setVisibility(View.VISIBLE);
        loadingTextView.setVisibility(View.GONE);
        loadingFaultRl.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        loadingImageView.setVisibility(View.VISIBLE);
        loadingFaultImageView.setVisibility(View.GONE);
        loadingTextView.setVisibility(View.VISIBLE);
        loadingFaultRl.setVisibility(View.GONE);
        animationDrawable.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("MusicDetailActivity", "stop");
        fileMp3 = new String[3];
        downFileMp3 = new String[3];
        fileMidi = null;
        fileDb = null;
        filePdf = null;
    }

    @Override
    protected void onDestroy() {
        LogUtil.e("MusicDetailActivity", "onDestroy");
        if (handlerUI != null) {
            handlerUI.removeCallbacksAndMessages(null);
            handlerUI = null;
        }

        if (bitmap != null) {
            bitmap.recycle();
        }
        instance = null;
        if (detailEntity != null) {
            detailEntity = null;
        }
        if (midifile != null) {
            midifile = null;
        }
        if (options != null) {
            options = null;
        }
        WinYinPianoApplication.getInstance().removeActivityFromStack(this);
        System.gc();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_detailmusic);
        LayoutHelper layoutHelper = new LayoutHelper(this);
        layoutHelper.scaleView(findViewById(R.id.activity_music_detailmusic));
        dataService = new DataService(RetrofitClient.getInstance().getService());
        instance = this;
//        midiJni = new MidiJni();

        musicId = getIntent().getStringExtra("musicId");
        musicName = getIntent().getStringExtra("musicName");
        musicUrl = getIntent().getStringExtra("musicUrl");
        initView();
        if (!StringUtils.isEmpty(musicId)) {
            musicItemOneDetail(musicId);
            Log.i("MusicDetailActivity", "曲子id=" + musicId + " userId=" + WinYinPianoApplication.strUid);
        }
    }

    /**
     * 一首曲子的详细
     */
    private void musicItemOneDetail(String strMusicId) {
        Map<String, String> map = new HashMap<>(4);
        map.put("appVersion", Constant.getVersionName(this));
        map.put("platformType", Constant.CODE);
        map.put("userId", WinYinPianoApplication.strUid);
        map.put("id", strMusicId);
        dataService.getMusicItem(map, this);
    }

    private void initView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_music_detailAbstract_rl);
        bitmap = Constant.readBitMap(this, R.drawable.opern_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        relativeLayout.setBackground(bitmapDrawable);

        classicalButtonRl = (RelativeLayout) findViewById(R.id.activity_music_detailabstract_button_rl);
        loadingImageView = (ImageView) findViewById(R.id.activity_music_detailabstract_imageview_loading);
        loadingFaultImageView = (ImageView) findViewById(R.id.activity_music_detailabstract_imageview_loadfault);
        loadingTextView = (TextView) findViewById(R.id.activity_music_detailabstract_textview_loading);
        loadingFaultRl = (RelativeLayout) findViewById(R.id.activity_music_detailabstract_imageview_loadfault_rl);

        loadingImageView.setImageResource(R.drawable.imageview_rotate);
        animationDrawable = new AnimationDrawable();
        animationDrawable = (AnimationDrawable) loadingImageView.getDrawable();


        imageview = (ImageView) findViewById(R.id.activity_music_detailabstract_imageview);
        ImageView back = (ImageView) findViewById(R.id.activity_music_detailabstract_back_imageview);

        ImageView detailAbstractRelatedImageView = (ImageView) findViewById(R.id.activity_music_detailabstract_related_imageview);

        bookTitleName = (TextView) findViewById(R.id.activity_music_detailabstract_title_name_textview);
        bookNameText = (TextView) findViewById(R.id.activity_music_detailabstract_name_textview);
        musicBookNameText = (TextView) findViewById(R.id.activity_music_detailabstract_bookname_textview);
        musicAuthorText = (TextView) findViewById(R.id.activity_music_detailabstract_author_textview);
        musicAuthorDistractText = (TextView) findViewById(R.id.activity_music_detailabstract_region_textview);
        ratingbar = (RatingBar) findViewById(R.id.activity_music_detailabstract_difficulty_ratingBar);

        /*收藏按钮, 进入曲谱, 左右手, 简介*/
        RelativeLayout musicCollectionRl = (RelativeLayout) findViewById(R.id.activity_music_detailabstract_collection_rl);
        RelativeLayout musicInputRl = (RelativeLayout) findViewById(R.id.activity_music_detailabstract_musicinput_rl);
        RelativeLayout musicTwoHandModelRl = (RelativeLayout) findViewById(R.id.activity_music_detailabstract_handplayer_rl);
        RelativeLayout musicDetailIntroduce = (RelativeLayout) findViewById(R.id.activity_music_detailabstract_brief_introduction_rl);

        musicWwoHandImage = (ImageView) findViewById(R.id.activity_music_detailabstract_handplayer_imageview);
        musicCollectionImage = (ImageView) findViewById(R.id.activity_music_detailabstract_collection_imageview);

        musicCollectionText = (TextView) findViewById(R.id.activity_music_detailabstract_collection_textview);
        musicTwoHandText = (TextView) findViewById(R.id.activity_music_detailabstract_handplayer_textview);
        backRl = (RelativeLayout) findViewById(R.id.activity_music_detailabstract_back_Rl);

        backRl.setOnClickListener(this);
        musicCollectionRl.setOnClickListener(this);
        musicInputRl.setOnClickListener(this);
        musicTwoHandModelRl.setOnClickListener(this);
        musicDetailIntroduce.setOnClickListener(this);
        detailAbstractRelatedImageView.setOnClickListener(this);
        imageview.setOnClickListener(this);
        classicalButtonRl.setOnClickListener(this);
    }

    private void showMusicDetailAbstractDialog(String author, String musicDetail) {

        if (musicDetailAbstractDialog == null) {
            musicDetailAbstractDialog = new MusicDetailAbstractDialog(MusicDetailActivity.this, R.style.BlueToothDialogStyle);
        }
        if (hasWindowFocus()) {
            if (!musicDetailAbstractDialog.isShowing()) {
                musicDetailAbstractDialog.show();
                musicDetailAbstractDialog.update(author, musicDetail);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("MusicDetailActivity", "onBackPressed");
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.activity_music_detailabstract_button_rl) {
            // 处理按钮点击
            if (!StringUtils.isEmpty(musicId)) {
                musicItemOneDetail(musicId);
                Log.i("MusicDetailActivity", "曲子id=" + musicId + " userId=" + WinYinPianoApplication.strUid);
            }
            showLoading();
        }
        else if (viewId == R.id.activity_music_detailabstract_related_imageview) {
            // 处理相关曲目
            if (detailEntity != null) {
                Intent intent = new Intent();
                intent.putExtra("MusicDetailEntity", detailEntity);
                intent.setClass(this, MusicRelatedActivity.class);
                startActivity(intent);
            }
        }
        else if (viewId == R.id.activity_music_detailabstract_back_Rl) {
            // 返回按钮
            finish();
        }
        else if (viewId == R.id.activity_music_detailabstract_collection_rl) {
            // 收藏功能
            if (detailEntity != null) {
                collection();
            }
        }
        else if (viewId == R.id.activity_music_detailabstract_imageview ||
                viewId == R.id.activity_music_detailabstract_musicinput_rl) {
            // 进入曲谱（两个控件共用同一逻辑）
            if (detailEntity != null) {
                inputMusic();
            }
        }
        else if (viewId == R.id.activity_music_detailabstract_handplayer_rl) {
            // 左右手设置
            if (detailEntity != null) {
                leftRightHand();
            }
        }
        else if (viewId == R.id.activity_music_detailabstract_brief_introduction_rl) {
            // 显示简介
            if (detailEntity != null) {
                if (detailEntity.getMusicAuthorIntroduce() != null &&
                        detailEntity.getMusicDetailIntroduce() != null) {
                    showMusicDetailAbstractDialog(
                            detailEntity.getMusicAuthorIntroduce(),
                            detailEntity.getMusicDetailIntroduce()
                    );
                }
            }
        }
// 默认情况无需处理
    }

    void collection() {
        if ("0".equals(musicCollection)) {
            putMusicCollect();
        } else {
            delMusicCollect();
        }
    }

    void putMusicCollect() {
        Map<String, String> map = new HashMap<>();
        map.put("appVersion", Constant.getVersionName(this.getApplicationContext()));
        map.put("platformType", Constant.CODE);
        map.put("userId", WinYinPianoApplication.strUid);
        map.put("musicId", musicId);
        LogUtil.i(TAG, "shoucang=" + System.currentTimeMillis() + "");
        map.put("collectTime", System.currentTimeMillis() + "");
        dataService.putMusicCollect(map, this);
    }

    void delMusicCollect() {
        Map<String, String> map = new HashMap<>();
        map.put("appVersion", Constant.getVersionName(this.getApplicationContext()));
        map.put("platformType", Constant.CODE);
        map.put("userId", WinYinPianoApplication.strUid);
        map.put("musicId", musicId);
        dataService.delMusicCollect(map, this);
    }

    void inputMusic() {
        isStart = true;
        if (detailEntity != null) {
            Log.i("MusicDetailActivity", "detailEntity!=null");
            if (handlerUI != null) {
                handlerUI.postDelayed(thread, 0);
            }
            showLoadingDialog();
        } else {
            Log.i("MusicDetailActivity", "detailEntity=null");
        }
    }

    void leftRightHand() {
        ++leftRightIndex;
        if (leftRightIndex == 3) {
            leftRightIndex = 0;
        }
        if (leftRightIndex == TWO_HAND) {
            musicWwoHandImage.setImageResource(R.drawable.two_hand_player);
            musicTwoHandText.setText("双手模式");
        } else if (leftRightIndex == RIGHT_HAND) {
            musicWwoHandImage.setImageResource(R.drawable.right_hand_player);
            musicTwoHandText.setText("右手模式");
        } else if (leftRightIndex == LEFT_HAND) {
            musicWwoHandImage.setImageResource(R.drawable.left_hand_player);
            musicTwoHandText.setText("左手模式");
        }
    }

    Runnable thread = new Runnable() {
        @Override
        public void run() {
            if (detailEntity.getMusicMp3Url() != null) {
                if (detailEntity.getMusicMp3Url().length() > 0) {
                    Log.i("MusicDetailActivity", "mp3=====" + detailEntity.getMusicMp3Url());
                    if (handlerUI != null) {
                        downMp3 = false;
                        dataService.down(detailEntity.getMusicMp3Url(), MusicDetailActivity.this, dataService, getLoadingDialog(), handlerUI);
                    }
                    if (detailEntity.getFileName() != null) {
                        if (detailEntity.getMusicMp3Url().contains(detailEntity.getFileName() + "10.mp3")) {
                            downFileMp3[0] = detailEntity.getMusicMp3Url();
                            downFileMp3[1] = detailEntity.getMusicMp3Url().replace(detailEntity.getFileName() + "10.mp3", detailEntity.getFileName() + "6.mp3");
                            downFileMp3[2] = detailEntity.getMusicMp3Url().replace(detailEntity.getFileName() + "10.mp3", detailEntity.getFileName() + "8.mp3");
                        }
                    }

                } else {
                    downMp3 = true;
                    Log.i("MusicDetailActivity", "mp3=====null");
                }
            } else {
                downMp3 = true;
                Log.i("MusicDetailActivity", "mp3=====null");
            }
            if (detailEntity.getMusicDbUrl() != null) {
                Log.i("MusicDetailActivity", "db=====" + detailEntity.getMusicDbUrl());
                if (handlerUI != null) {
                    dataService.down(detailEntity.getMusicDbUrl(), MusicDetailActivity.this, dataService, getLoadingDialog(), handlerUI);
                }
            } else {
                Log.i("MusicDetailActivity", "db=====null");
                ToastUtil.showTextToast(MusicDetailActivity.this, "文件缺失无法进入");
                dismiss();
                return;
            }
            if (detailEntity.getMusicMidiUrl() != null) {
                Log.i("MusicDetailActivity", "midi=====" + detailEntity.getMusicMidiUrl());
                if (handlerUI != null) {
                    dataService.down(detailEntity.getMusicMidiUrl(), MusicDetailActivity.this, dataService, getLoadingDialog(), handlerUI);
                }

            } else {
                Log.i("MusicDetailActivity", "midi======null");
                ToastUtil.showTextToast(MusicDetailActivity.this, "文件缺失无法进入");
                dismiss();
                return;
            }
            if (detailEntity.getMusicPdfUrl() != null) {
                Log.i("MusicDetailActivity", "pdf=======" + detailEntity.getMusicPdfUrl());
                String textUrl = detailEntity.getMusicPdfUrl().replace("pdf", "txt");
                //"/piano/class/1/le1.json"
                if (textUrl != null) {
                    Log.i("MusicDetailActivity", "txt=" + textUrl);
                    if (handlerUI != null) {
                        dataService.down(textUrl, MusicDetailActivity.this, dataService, getLoadingDialog(), handlerUI);
                    }
                }
                if (handlerUI != null) {
                    dataService.down(detailEntity.getMusicPdfUrl(), MusicDetailActivity.this, dataService, getLoadingDialog(), handlerUI);
                }
            } else {
                Log.i("MusicDetailActivity", "pdf========null");
                ToastUtil.showTextToast(MusicDetailActivity.this, "文件缺失无法进入");
                dismiss();
            }
        }
    };

    void skip(File file) {
        String filePath = file.getPath();
        LogUtil.i(TAG, filePath);
        if (filePath.contains(".mid") || filePath.contains(".MID")) {
            fileMidi = file;
            Uri uri = Uri.parse(file.getPath());
            String title = Constant.getFileNameNoEx(file.getName(), ".mid");
            FileUri fileuri = new FileUri(uri, title);
            byte[] data;

            try {
                data = fileuri.getData(MusicDetailActivity.this);
                midifile = new MidiFile(data, title);
                if (!midifile.bFlagXmlRight) {
                    throw new Exception("Xml Error");
                }
            } catch (Exception e) {
//                                ToastUtil.makeText(MusicDetailActivity.this, "文件解析错误", ToastUtil.LENGTH_SHORT).show();
//                                MusicDetailActivity.this.finish();
//                                return;
            }

            options = new MidiOptions(midifile);
            if (leftRightIndex == LEFT_HAND || leftRightIndex == RIGHT_HAND) {
                String rightPath = Constant.getFileNameNoEx(file.getPath(), ".mid");
                writeFileLeft(2, rightPath + "_right.mid");


                String leftPath = Constant.getFileNameNoEx(file.getPath(), ".mid");
                writeFileLeft(1, leftPath + "_left.mid");
            }
        } else if (filePath.contains(".pdf")) {
            filePdf = file;
        } else if (filePath.contains(".db")) {
            fileDb = file;
        } else if (filePath.contains(".txt") || filePath.contains(".TXT")) {
            fileTxt = file;
        } else if (filePath.contains(".mp3") || filePath.contains(".MP3")) {
            downMp3 = true;
            fileMp3[0] = filePath;
        }

        if (fileDb != null && filePdf != null && fileMidi != null && fileTxt != null && isStart && downMp3) {
            isStart = false;
            if (getLoadingDialog() != null) {
                getLoadingDialog().dismiss();
                Intent intent = new Intent();

                OpernEntity opernEntity = new OpernEntity();
                opernEntity.musicid = musicId;
                if (fileTxt.exists()) {
                    opernEntity.setTxtPath(fileTxt.getPath());
                } else {
/* Log.i("MusicDetailActivity","TXT文件不存在"); */
                }
                if (fileDb.exists()) {
                    opernEntity.setDbPath(fileDb.getPath());
                }
                if (fileMidi.exists()) {
                    opernEntity.setMidiPath(Constant.getFileNameNoEx(fileMidi.getPath(), ".mid"));
                }
                if (filePdf.exists()) {
                    opernEntity.setPdfPath(filePdf.getPath());
                }
                if (fileMp3 != null) {
                    if (fileMp3.length > 0) {
                        opernEntity.setMp3Path(fileMp3);
                        opernEntity.setDownMp3Path(downFileMp3);
                    }
                }
                intent.putExtra("type", leftRightIndex);
                intent.putExtra("opernEntity", opernEntity);
                intent.putExtra("musicBookName", musicBookName);
                Log.i("MusicDetailActivity", "跳转db路径=" + fileDb.getPath());
                Log.i("MusicDetailActivity", "跳转pdf路径=" + filePdf.getPath());
                Log.i("MusicDetailActivity", "跳转midi路径=" + fileMidi.getPath());
                Log.i("MusicDetailActivity", "跳转index=" + leftRightIndex);
                Log.i("MusicDetailActivity", "跳转txt路径" + fileTxt.getPath());
                intent.setClass(MusicDetailActivity.this, OpernActivity.class);
                startActivity(intent);
            }
        }
    }

    public void writeFileLeft(int index, String path) {
        if (index == 1) {
                    /* left */
            int length = options.tracks.length;
            for (int i = 0; i < length; i++) {
                if (i == 0) {
                    options.tracks[i] = false;
                    options.mute[i] = true;
                } else {
                    options.tracks[i] = true;
                    options.mute[i] = false;
                }
                options.HandType = 0;
            }
        } else if (index == 2) {
            /* right */
            int length;
            length = options.tracks.length;
            for (int i = 0; i < length; i++) {
                if (i == 1) {
                    options.tracks[i] = false;
                    options.mute[i] = true;
                } else {
                    options.tracks[i] = true;
                    options.mute[i] = false;
                }
                options.HandType = 1;
            }
        }
        Constant.createMidiFile(midifile, path, options, this.getApplicationContext());
    }

    @Override
    public void onCompleted(String url) {

    }

    @Override
    public void onError(BizException e) {
        ToastUtil.showTextToast(this, e.getMsg());
        if (classicalButtonRl.getVisibility() == View.GONE) {
            classicalButtonRl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNext(ResponseBody baseResponse, String url) {
        switch (url) {
            case RequestConfigs.GET_MUSIC_ITEM:
                try {
                    String json = baseResponse.string();
                    Log.i(TAG, json);
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getInt(Constant.RESP_CODE) == 0) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("item");
                        JSONArray jsonArray = jsonObject1.getJSONArray("relationMusic");
                        Gson gson = new Gson();
                        detailEntity = gson.fromJson(jsonObject.getJSONObject("item").toString(), MusicDetailEntity.class);

                        ArrayList<MusicAboutEntity> musicAboutEntities = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            MusicAboutEntity musicAboutEntity = gson.fromJson(jsonArray.get(i).toString(), MusicAboutEntity.class);
                            musicAboutEntities.add(musicAboutEntity);
                        }
                        detailEntity.setRelationMusicId(musicAboutEntities);
                        setData();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                break;
            case RequestConfigs.PUT_MUSIC_COLLECT:
                try {
                    String json = baseResponse.string();
                    Log.i(TAG, "PUT_MUSIC_COLLECT=" + json);
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getInt(Constant.RESP_CODE) == 0) {
                        musicCollection = "1";
                        musicCollectionImage.setImageResource(R.drawable.collection_f);
                        musicCollectionText.setText("取消收藏");
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                break;
            case RequestConfigs.DEL_MUSIC_COLLECT:
                try {
                    String json = baseResponse.string();
                    Log.i(TAG, "DEL_MUSIC_COLLECT=" + json);
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getInt(Constant.RESP_CODE) == 0) {
                        musicCollection = "0";
                        musicCollectionImage.setImageResource(R.drawable.collection_o);
                        musicCollectionText.setText("收藏");
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                break;
            default:
                break;
        }

    }


    //字符串换行
    public static String spaceAt1(String str) {

        StringBuilder sb = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            //防止ArrayIndexOutOfBoundsException
            sb.append(str.substring(i, i + 1)).append("\n");



        }

        return sb.toString();
    }


}
