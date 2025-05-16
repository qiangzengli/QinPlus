package com.weiyin.qinplus.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.adapter.MusicRelaterAdapter;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.entity.MusicAboutEntity;
import com.weiyin.qinplus.entity.MusicDetailEntity;
import com.weiyin.qinplus.entity.OpernEntity;
import com.weiyin.qinplus.rest.DataService;
import com.weiyin.qinplus.rest.RetrofitClient;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;
import com.weiyin.qinplus.ui.tv.view.recyclerview.CustomRecyclerView;
import com.weiyin.qinplus.ui.tv.view.recyclerview.GridLayoutManager;

import java.io.File;
import java.util.ArrayList;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 曲目相关详情
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
    public class MusicRelatedActivity extends BaseActivity implements MusicRelaterAdapter.ItemClickListener, View.OnClickListener {

    private ArrayList<MusicAboutEntity> arrayList = new ArrayList<>();

    private MusicDetailEntity musicDetailEntity;

    private boolean isStart = true;
    private String musicId;
    private String[] fileMp3 = new String[3];
    private File fileDb, fileMidi, filePdf, fileTxt;

    private MusicAboutEntity detailEntity;
    private DataService dataService;

    private Bitmap bitmap;

    @SuppressLint("HandlerLeak")
    Handler handlerUI = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.INTERNET_CALLBACK_FILE_DOWN:
                    try {
                        File file = (File) msg.obj;
                        skip(file);
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

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("MusicDetailActivity", "stop");
        fileMp3 = new String[3];
        fileMidi = null;
        fileDb = null;
        filePdf = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_related);
        LayoutHelper layoutHelper = new LayoutHelper(this);
        layoutHelper.scaleView(findViewById(R.id.activity_music_related));

        musicDetailEntity = (MusicDetailEntity) getIntent().getSerializableExtra("MusicDetailEntity");
        initView();
    }


    public void initView() {
        dataService = new DataService(RetrofitClient.getInstance().getService());
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_music_related);
        bitmap = Constant.readBitMap(this, R.drawable.opern_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        relativeLayout.setBackground(bitmapDrawable);

        ImageView mainRelatedActivityBack = (ImageView) findViewById(R.id.main_related_activity_back);
        TextView mainRelatedActivityNameText = (TextView) findViewById(R.id.main_related_activity_name_textview);

        CustomRecyclerView mainRelatedActivityCustomRecyclerView = (CustomRecyclerView) findViewById(R.id.main_related_activity_customrecyclerview);

        if (musicDetailEntity != null) {
            arrayList = musicDetailEntity.getRelationMusicId();
            if (musicDetailEntity.getMusicBookName() != null) {
                mainRelatedActivityNameText.setText(musicDetailEntity.getMusicBookName());
            }
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL);

        MusicRelaterAdapter myCollectionAdapter = new MusicRelaterAdapter(this, arrayList);

        mainRelatedActivityCustomRecyclerView.setLayoutManager(gridLayoutManager);
        mainRelatedActivityCustomRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mainRelatedActivityCustomRecyclerView.setAdapter(myCollectionAdapter);

        mainRelatedActivityBack.setOnClickListener(this);
        myCollectionAdapter.setOnItemClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.main_related_activity_back) {
                finish();
        }
    }


    void inputMusic(MusicAboutEntity musicAboutEntity) {
        isStart = true;
        this.detailEntity = musicAboutEntity;
        if (musicAboutEntity != null) {
            musicId = musicAboutEntity.getId();
            Log.i("MusciDetailActivity", "detailentity!=null");
            if (handlerUI != null) {
                handlerUI.postDelayed(thread, 0);
            }
            showLoadingDialog();
        } else {
            Log.i("MusciDetailActivity", "detailentity=null");
        }
    }

    Runnable thread = new Runnable() {
        @Override
        public void run() {
            if (detailEntity.getMusicDbUrl() != null) {
                Log.i("MusicDetailActivity", "db=====" + detailEntity.getMusicDbUrl());
                if (handlerUI != null) {
                    dataService.down(detailEntity.getMusicDbUrl(), MusicRelatedActivity.this, dataService, getLoadingDialog(), handlerUI);
                }
            } else {
                Log.i("MusicDetailActivity", "db=====null");
                ToastUtil.showTextToast(MusicRelatedActivity.this, "文件缺失无法进入");
                dismiss();
                return;
            }
            if (detailEntity.getMusicMidiUrl() != null) {
                Log.i("MusicDetailActivity", "midi=====" + detailEntity.getMusicMidiUrl());
                if (handlerUI != null) {
                    dataService.down(detailEntity.getMusicMidiUrl(), MusicRelatedActivity.this, dataService, getLoadingDialog(), handlerUI);
                }

            } else {
                Log.i("MusicDetailActivity", "midi======null");
                ToastUtil.showTextToast(MusicRelatedActivity.this, "文件缺失无法进入");
                dismiss();
                return;
            }
            if (detailEntity.getMusicPdfUrl() != null) {
                Log.i("MusicDetailActivity", "pdf=======" + detailEntity.getMusicPdfUrl());
                String textUrl = detailEntity.getMusicPdfUrl().replace("pdf", "txt");
                if (textUrl != null) {
                    Log.i("MusicDetailActivity", "txt=" + textUrl);
                    if (handlerUI != null) {
                        dataService.down(textUrl, MusicRelatedActivity.this, dataService, getLoadingDialog(), handlerUI);
                    }
                }
                if (handlerUI != null) {
                    dataService.down(detailEntity.getMusicPdfUrl(), MusicRelatedActivity.this, dataService, getLoadingDialog(), handlerUI);
                }
            } else {
                Log.i("MusicDetailActivity", "pdf========null");
                ToastUtil.showTextToast(MusicRelatedActivity.this, "文件缺失无法进入");
                dismiss();
            }
        }
    };

    void skip(File file) {
        String filePath = file.getPath();
        if (filePath.contains(".mid")) {
            fileMidi = file;
        } else if (filePath.contains(".pdf")) {
            filePdf = file;
        } else if (filePath.contains(".db")) {
            fileDb = file;
        } else if (filePath.contains(".txt")) {
            fileTxt = file;
        }

        if (fileDb != null && filePdf != null && fileMidi != null && fileTxt != null && isStart) {
            isStart = false;
            if (getLoadingDialog() != null) {
                getLoadingDialog().dismiss();
                Intent intent = new Intent();

                OpernEntity opernEntity = new OpernEntity();
                opernEntity.musicid = musicId;
                if (fileTxt.exists()) {
                    opernEntity.setTxtPath(fileTxt.getPath());
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
                    }
                }
                intent.putExtra("type", 0);
                intent.putExtra("opernEntity", opernEntity);
                Log.i("MusicDetailActivity", "跳转db路径=" + fileDb.getPath());
                Log.i("MusicDetailActivity", "跳转pdf路径=" + filePdf.getPath());
                Log.i("MusicDetailActivity", "跳转midi路径=" + fileMidi.getPath());
                Log.i("MusicDetailActivity", "跳转index=" + 2);
                Log.i("MusicDetailActivity", "跳转txt路径" + fileTxt.getPath());
                intent.setClass(MusicRelatedActivity.this, OpernActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onItemClick(View view, int postion) {
        inputMusic(musicDetailEntity.getRelationMusicId().get(postion));
    }
}
