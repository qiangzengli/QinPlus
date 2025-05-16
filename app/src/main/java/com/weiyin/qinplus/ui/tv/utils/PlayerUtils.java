package com.weiyin.qinplus.ui.tv.utils;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.weiyin.qinplus.R;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import fm.jiecao.jcvideoplayer_lib.JCUserAction;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;


/**
 * 音视频播放工具
 */
public class PlayerUtils implements MediaPlayer.OnCompletionListener {


    public static final int PICK_VIDEO = 2001;
    public static final int PICK_VOICE = 2002;
    public static final int PLAYING = 2003;
    public static final int PAUSE = 2004;
    public static final int STOP = 2005;

    private MediaPlayer mp;
    public int currentPosition;
    public int playState = STOP;
    private ImageView playView;
    private PublicWorker publicWorker;

    public static PlayerUtils getInstance() {
        return new PlayerUtils();
    }

    public void playVoice(final String path, final ImageView playView) {
        this.playView = playView;
        publicWorker = new PublicWorker();
        publicWorker.pushTask(new Runnable() {
            @Override
            public void run() {
                try {
                    killMediaPlayer();// 播放前，先kill原来的mediaPlayer
                    Log.d("status", "playVoice");
                    playState = PLAYING;
                    mp = new MediaPlayer();
                    mp.setDataSource(path);
                    mp.prepare();
                    mp.setOnCompletionListener(PlayerUtils.this);
                    mp.start();
                    playView.setImageResource(R.drawable.com_ic_pause_voice);
                } catch (IllegalStateException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }


    public void restart(ImageView playView) {
        if (mp != null && !mp.isPlaying()) {
            playState = PLAYING;
            playView.setImageResource(R.drawable.com_ic_pause_voice);
            mp.seekTo(currentPosition);
            mp.start();
        }
    }

    public void killMediaPlayer() {
        // TODO Auto-generated method stub
        if (null != mp) {
            mp.release();
        }
    }

    public long getVoiceTimeLength(String path) {
        try {
            killMediaPlayer();// 播放前，先kill原来的mediaPlayer
            mp = new MediaPlayer();
            mp.setDataSource(path);
            mp.prepare();
            long duration = mp.getDuration();
            return duration;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void cloasVoice() {
        mp.release();
    }


    public void setVideo(JCVideoPlayerStandard jcVideoPlayerStandard,
                         String url,
                         String title,
                         Bitmap bitmap) {
        jcVideoPlayerStandard.setUp(url, JCUserAction.ON_CLICK_START_ICON, title);
        jcVideoPlayerStandard.thumbImageView.setImageBitmap(bitmap);
    }

    /**
     * @param jcVideoPlayerStandard 播放控件
     * @param url                   播放路径 本地或者网络
     * @param title                 视频标题
     */
    public void setVideo(JCVideoPlayerStandard jcVideoPlayerStandard,
                         String url,
                         String title,
                         String imgUrl) {
        jcVideoPlayerStandard.setUp(url, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, title);
        if (StringUtils.isEmpty(imgUrl)) {
            return;
        }
        Glide.with(jcVideoPlayerStandard.getContext()).load(imgUrl).into(jcVideoPlayerStandard.thumbImageView);
    }


    /**
     * 释放视频播放
     */
    public void releaseVideo() {
        JCVideoPlayer.releaseAllVideos();
    }


    public void pickVoice(Activity activity) {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, PICK_VOICE);
    }

    public void pickVideo(Activity activity) {
        Intent intent = new Intent();
        intent.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, PICK_VIDEO);
    }

    public String formatTv(long timeLenght) {
        int s = (int) (timeLenght / 1000);
        int m = s / 60;
        s = s % 60;
        return m + ":" + s;
    }

    public String formatTv(int timeLength) {
        int m = timeLength / 60;
        int s = timeLength % 60;
        return m + ":" + s;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.release();
        playState = STOP;
    }
}
