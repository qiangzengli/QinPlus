package com.weiyin.qinplus.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.weiyin.qinplus.ui.tv.view.recyclerview.GridLayoutManager;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;


/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 流行音乐
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
@SuppressLint("ValidFragment")
public class MainMusicFashionFragment extends BaseFragment implements View.OnFocusChangeListener, ClassicalRecyclerAdapter.ItemClickListener, ClassicalRecyclerAdapter.ItemFocusListener, View.OnClickListener {
    public final String TAG = MainMusicFashionFragment.class.getSimpleName();

    private ArrayList<MusicBookEntity> musicList;
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
    private int screenIndex = 1;

    private String strMusicLevel;

    private MusicDetailDialog musicDetailDialog;

    public MainMusicFashionFragment() {

    }

    @SuppressLint("HandlerLeak")
    public Handler handlerUI = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.INTERNET_CALLBACK_REQUEST_CABLE_MUSIC_FASCIST:
                    if (musicList != null) {
                        musicList.clear();
                    }
                    getCableData(strMusicLevel);
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_fashionmusic_viewpager, null);
            LayoutHelper layoutHelper = new LayoutHelper(getActivity());
            layoutHelper.scaleView(rootView);

            if (musicList == null) {
                musicList = new ArrayList<>();
                initView();

                strMusicLevel = "21";
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
        //这个判断是为了处理强制竖屏的时候  屏幕宽度变小  tab位置错乱

        if (dpMetrics.widthPixels > dpMetrics.heightPixels) {
            screenWidth = dpMetrics.widthPixels;
        } else {
            screenWidth = dpMetrics.heightPixels;
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivSBoardDetailTabLine
                .getLayoutParams();
        lp.width = screenWidth / 6;
        lp.height = 5;
        lp.leftMargin = screenWidth / 3 * screenIndex - (screenWidth / 3 - screenWidth / 6) / 2 - screenWidth / 6;
        ivSBoardDetailTabLine.setLayoutParams(lp);
    }

    public void updateLp(RelativeLayout.LayoutParams lp) {
        lp.leftMargin = screenWidth / 3 * screenIndex - (screenWidth / 3 - ivSBoardDetailTabLine.getWidth()) / 2 - ivSBoardDetailTabLine.getWidth();
    }


    public void initView() {
        ivSBoardDetailTabLine = (ImageView) rootView.findViewById(R.id.fragment_fashion_iv_sboard_detail_tabline);

        RelativeLayout mOneLevelRl = (RelativeLayout) rootView.findViewById(R.id.fashion_onetab_rl);
        RelativeLayout mTwoLevelRl = (RelativeLayout) rootView.findViewById(R.id.fashion_twotab_rl);
        RelativeLayout mThreeLevelRl = (RelativeLayout) rootView.findViewById(R.id.fashion_threetab_rl);


        CustomRecyclerView customRecyclerView = (CustomRecyclerView) rootView.findViewById(R.id.fragment_fashion_customrecyclerview);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.HORIZONTAL);
        customRecyclerView.setLayoutManager(gridLayoutManager);
        customRecyclerView.setItemAnimator(new DefaultItemAnimator());


        mAdapter = new ClassicalRecyclerAdapter(getActivity(), musicList, false);
        customRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemFocusListener(this);

        mOneLevelRl.setOnClickListener(this);
        mTwoLevelRl.setOnClickListener(this);
        mThreeLevelRl.setOnClickListener(this);

    }


    /**
     * 请求缓存数据
     *
     * @param level String
     */
    public void getCableData(String level) {
        if (!StringUtils.isEmpty(level)) {
            if (WinYinPianoApplication.musicBookEntity02.size() == 0) {
                WinYinPianoApplication.instance.getJson();
            }
            for (int i = 0; i < WinYinPianoApplication.musicBookEntity02.size(); i++) {
                MusicBookEntity musicListItemEntity = WinYinPianoApplication.musicBookEntity02.get(i);
                if (musicListItemEntity.getMusicLevel().equals(level)) {
                    musicList.add(musicListItemEntity);
                }
            }
            Log.i(TAG, "大小=" + musicList.size());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("FashionFragment", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("FashionFragment", "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ((ViewGroup) rootView.getParent()).removeView(rootView);
        Log.e("FashionFragment", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("FashionFragment", "onDestroy");
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

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

    }

    // 提取公共逻辑方法
    private void handleTabSelection(int index, String level, RelativeLayout.LayoutParams lp) {
        Message msg = new Message();
        screenIndex = index;
        updateLp(lp);
        strMusicLevel = level;
        msg.what = Constant.INTERNET_CALLBACK_REQUEST_CABLE_MUSIC_FASCIST;
        handlerUI.sendMessage(msg);
    }

    @Override
    public void onClick(View v) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivSBoardDetailTabLine
                .getLayoutParams();

        int viewId = v.getId();

        if (viewId == R.id.fashion_onetab_rl) {
            handleTabSelection(1, "21", lp);
        } else if (viewId == R.id.fashion_twotab_rl) {
            handleTabSelection(2, "22", lp);
        } else if (viewId == R.id.fashion_threetab_rl) {
            handleTabSelection(3, "23", lp);
        }
        ivSBoardDetailTabLine.setLayoutParams(lp);
    }
}
