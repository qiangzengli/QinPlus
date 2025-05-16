package com.weiyin.qinplus.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.adapter.ClassicalRecyclerAdapter;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.dialog.MusicDetailDialog;
import com.weiyin.qinplus.entity.MusicBookEntity;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;
import com.weiyin.qinplus.ui.tv.view.recyclerview.CustomRecyclerView;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 经典教程
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
@SuppressLint("ValidFragment")
public class MainMusicClassicalFragment extends BaseFragment implements ClassicalRecyclerAdapter.ItemClickListener,
        ClassicalRecyclerAdapter.ItemFocusListener, View.OnClickListener {
    public final String TAG = "MainMusicClassicalFragment";

    private ArrayList<MusicBookEntity> musicList;
    private String strMusicLevel;

    private View rootView;
    private ClassicalRecyclerAdapter mAdapter;


    private ImageView ivSBoardDetailTabLine;
    /**
     * 屏幕寬度
     */
    private int screenWidth;
    /**
     * 坐标
     */
    private int sreenIndex = 1;


    private MusicDetailDialog musicDetailDialog;

    public MainMusicClassicalFragment() {

    }

    @SuppressLint("HandlerLeak")
    public Handler handlerUI = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.INTERNET_CALLBACK_REQUEST_CABLE_MUSIC_CLASS_IDEALIST:
                    if (musicList != null) {
                        musicList.clear();
                    }
                    getCableData(strMusicLevel);
                    if(mAdapter!=null){
                        mAdapter.notifyDataSetChanged();
                    }

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_classicalmusic_viewpager, null);
            LayoutHelper layoutHelper = new LayoutHelper(getActivity());
            layoutHelper.scaleView(rootView);

            strMusicLevel = "11";
            if (musicList == null) {
                musicList = new ArrayList<>();
                initView();
                getCableData(strMusicLevel);
                mAdapter.notifyDataSetChanged();
            }
        }

        initTabLineWidth();
        return rootView;
    }






    /**
     * 设置tabLine属性
     */
    private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
//


        //这个判断是为了处理强制竖屏的时候  屏幕宽度变小  tab位置错乱

            if(dpMetrics.widthPixels>dpMetrics.heightPixels){
                screenWidth = dpMetrics.widthPixels;
            }else {
                screenWidth=dpMetrics.heightPixels;
            }




        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivSBoardDetailTabLine
                .getLayoutParams();
        lp.width = screenWidth / 8;
        lp.height = 5;
        lp.leftMargin = screenWidth / 4 * sreenIndex - (screenWidth / 4 - screenWidth / 8) / 2 - screenWidth / 8;
        ivSBoardDetailTabLine.setLayoutParams(lp);
    }

    public void initView() {
        RelativeLayout mOneLevelRl = (RelativeLayout) rootView.findViewById(R.id.classical_onetab_rl);
        RelativeLayout mTwoLevelRl = (RelativeLayout) rootView.findViewById(R.id.classical_twotab_rl);
        RelativeLayout mThreeLevelRl = (RelativeLayout) rootView.findViewById(R.id.classical_threetab_rl);
        RelativeLayout mFourLevelRl = (RelativeLayout) rootView.findViewById(R.id.classical_fourtab_rl);

        ivSBoardDetailTabLine = (ImageView) rootView.findViewById(R.id.iv_sboard_detail_tabline);

        CustomRecyclerView customRecyclerView = (CustomRecyclerView) rootView.findViewById(R.id.fragment_classical_customrecyclerview);

/* gridLayoutManager = new android.support.v7.widget.GridLayoutManager(getActivity(), 2, GridLayoutManager.HORIZONTAL,false); */
        customRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.HORIZONTAL, false));
        customRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new ClassicalRecyclerAdapter(getActivity(), musicList, false);
        customRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemFocusListener(this);


        mOneLevelRl.setOnClickListener(this);
        mTwoLevelRl.setOnClickListener(this);
        mThreeLevelRl.setOnClickListener(this);
        mFourLevelRl.setOnClickListener(this);
    }


    /**
     * 请求缓存数据
     *
     * @param strLevel String
     */
    public void getCableData(String strLevel) {
        if (!StringUtils.isEmpty(strLevel)) {
            Log.i(TAG, strLevel);
            if (WinYinPianoApplication.musicBookEntity03.size() == 0) {
                WinYinPianoApplication.instance.getJson();
            }
            for (int i = 0; i < WinYinPianoApplication.musicBookEntity03.size(); i++) {
                MusicBookEntity musicListItemEntity = WinYinPianoApplication.musicBookEntity03.get(i);
                if (musicListItemEntity.getMusicLevel().equals(strLevel)) {
                    musicList.add(musicListItemEntity);
                }
            }
            Log.i(TAG, "大小=" + musicList.size());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) rootView.getParent()).removeView(rootView);
        Log.e("ClassicalFragment", "onDestroyView");
    }

    @Override
    public void onItemClick(View view, int position) {
        showMusicDetailDialog(position);
    }

    @SuppressLint("RtlHardcoded")
    public void showMusicDetailDialog(int position) {
        if (musicDetailDialog == null) {
            musicDetailDialog = new MusicDetailDialog(getActivity(), R.style.BlueToothDialogStyle, musicList.get(position));
            Window dialogWindow = musicDetailDialog.getWindow();
            assert dialogWindow != null;
            dialogWindow.setGravity(Gravity.RIGHT);
        }
        if (musicDetailDialog != null) {
            if (getActivity().hasWindowFocus()) {
                if (!musicDetailDialog.isShowing()) {
                    musicDetailDialog.show();
                    musicDetailDialog.update(musicList.get(position));
                }
            }
        }
    }

    @Override
    public void onItemFcous(View view, int position) {

//        mOldView = view;
    }

    public void updateLp(RelativeLayout.LayoutParams lp) {
        lp.leftMargin = screenWidth / 4 * sreenIndex - (screenWidth / 4 - ivSBoardDetailTabLine.getWidth()) / 2 - ivSBoardDetailTabLine.getWidth();
    }

    @Override
    public void onClick(View v) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivSBoardDetailTabLine
                .getLayoutParams();
        Message msg = new Message();
        int viewId = v.getId();

        if (viewId == R.id.classical_onetab_rl) {
            sreenIndex = 1;
            updateLp(lp);
            strMusicLevel = "11";
            msg.what = Constant.INTERNET_CALLBACK_REQUEST_CABLE_MUSIC_CLASS_IDEALIST;
            handlerUI.sendMessage(msg);
        }
        else if (viewId == R.id.classical_twotab_rl) {
            sreenIndex = 2;
            updateLp(lp);
            strMusicLevel = "12";
            msg.what = Constant.INTERNET_CALLBACK_REQUEST_CABLE_MUSIC_CLASS_IDEALIST;
            handlerUI.sendMessage(msg);
        }
        else if (viewId == R.id.classical_threetab_rl) {
            sreenIndex = 3;
            updateLp(lp);
            strMusicLevel = "13";
            msg.what = Constant.INTERNET_CALLBACK_REQUEST_CABLE_MUSIC_CLASS_IDEALIST;
            handlerUI.sendMessage(msg);
        }
        else if (viewId == R.id.classical_fourtab_rl) {
            sreenIndex = 4;
            updateLp(lp);
            strMusicLevel = "14";
            msg.what = Constant.INTERNET_CALLBACK_REQUEST_CABLE_MUSIC_CLASS_IDEALIST;
            handlerUI.sendMessage(msg);
        }
        ivSBoardDetailTabLine.setLayoutParams(lp);
    }







}
