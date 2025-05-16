package com.weiyin.qinplus.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.entity.VideoEntity;
import com.weiyin.qinplus.ui.tv.utils.PlayerUtils;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app  视频页面Activity
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class VideoActivity extends AppCompatActivity {

    private JCVideoPlayerStandard jcVideoPlayerStandard;
    private VideoEntity videoEntity;
    private PlayerUtils playerUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        playerUtils = PlayerUtils.getInstance();
        videoEntity = (VideoEntity) getIntent().getSerializableExtra("VideoEntity");
        initView();
        initData();
    }

    private void initView() {
        jcVideoPlayerStandard = (JCVideoPlayerStandard) findViewById(R.id.videoplayer);
    }

    private void initData() {
        if (videoEntity != null) {
            jcVideoPlayerStandard.titleTextView.setTextSize(20);
            playerUtils.setVideo(jcVideoPlayerStandard, WinYinPianoApplication.strUrl + videoEntity.getVideoUrl(), videoEntity.getName(), WinYinPianoApplication.strUrl + videoEntity.getCoverUrl());
            jcVideoPlayerStandard.backButton.setVisibility(View.VISIBLE);
            jcVideoPlayerStandard.backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }
}
