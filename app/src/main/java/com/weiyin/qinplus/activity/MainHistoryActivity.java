package com.weiyin.qinplus.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.adapter.MusicHistoryAdapter;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.entity.MusicHistoryEntity;
import com.weiyin.qinplus.rest.BizException;
import com.weiyin.qinplus.rest.DataService;
import com.weiyin.qinplus.rest.HttpRequestListener;
import com.weiyin.qinplus.rest.RequestConfigs;
import com.weiyin.qinplus.rest.RetrofitClient;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;
import com.weiyin.qinplus.ui.tv.view.recyclerview.CustomRecyclerView;
import com.weiyin.qinplus.ui.tv.view.recyclerview.GridLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : 弹奏记录Activity
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class MainHistoryActivity extends BaseActivity implements MusicHistoryAdapter.OnHistoryItemClickListener, View.OnClickListener, HttpRequestListener<ResponseBody> {
    public final String TAG = MainHistoryActivity.class.getSimpleName();
    private ArrayList<MusicHistoryEntity> arrayList, oneList, twoList, threeList;
    private ImageView detailTabLine, mainHistoryNo;
    private MusicHistoryAdapter musicHistoryAdapter;

    /**
     * 屏幕寬度,坐标
     */
    private int screenWidth, screenIndex = 1;

    private Bitmap bitmap;

    private DataService dataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_history);
        LayoutHelper layoutHelper = new LayoutHelper(this);
        layoutHelper.scaleView(findViewById(R.id.activity_main_history));
        dataService = new DataService(RetrofitClient.getInstance().getService());
        arrayList = new ArrayList<>();
        oneList = new ArrayList<>();
        twoList = new ArrayList<>();
        threeList = new ArrayList<>();
        getData();
        initView();
        initTabLineWidth();
        arrayList = oneList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    /**
     * 设置tabLine属性
     */
    private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);



        if(dpMetrics.widthPixels>dpMetrics.heightPixels){
            screenWidth = dpMetrics.widthPixels;
        }else {
            screenWidth = dpMetrics.heightPixels;
        }

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) detailTabLine
                .getLayoutParams();
        lp.width = screenWidth / 6;
        lp.height = 5;
        lp.leftMargin = screenWidth / 3 * screenIndex - (screenWidth / 3 - screenWidth / 6) / 2 - screenWidth / 6;
        detailTabLine.setLayoutParams(lp);
    }

    public void updateLp(RelativeLayout.LayoutParams lp) {
        lp.leftMargin = screenWidth / 3 * screenIndex - (screenWidth / 3 - detailTabLine.getWidth()) / 2
                - detailTabLine.getWidth();
    }

    void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("appVersion", Constant.getVersionName(this.getApplicationContext()));
        map.put("platformType", Constant.CODE);
        map.put("userId", WinYinPianoApplication.strUid);
        dataService.getPlayList(map, this);
    }

    public void initView() {
        mainHistoryNo = (ImageView) findViewById(R.id.mainHistoryNo);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_main_history);
        bitmap = Constant.readBitMap(this, R.drawable.opern_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        relativeLayout.setBackground(bitmapDrawable);

        RelativeLayout oneTabRl = (RelativeLayout) findViewById(R.id.activity_main_history_onetab_rl);
        RelativeLayout twoTabRl = (RelativeLayout) findViewById(R.id.activity_main_history_twotab_rl);
        RelativeLayout threeTabRl = (RelativeLayout) findViewById(R.id.activity_main_history_threetab_rl);

        detailTabLine = (ImageView) findViewById(R.id.activity_main_history_iv_sboard_detail_tabline);
        RelativeLayout backImageView = (RelativeLayout) findViewById(R.id.main_history_activity_back_Rl);

        CustomRecyclerView customRecyclerView = (CustomRecyclerView) findViewById(R.id.activity_main_history_customrecyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL);

        musicHistoryAdapter = new MusicHistoryAdapter(this, R.layout.history_item_layout, oneList);

        customRecyclerView.setLayoutManager(gridLayoutManager);
        customRecyclerView.setItemAnimator(new DefaultItemAnimator());
        customRecyclerView.setAdapter(musicHistoryAdapter);

        musicHistoryAdapter.setOnHistoryItemClickListener(this);

        oneTabRl.setOnClickListener(this);
        twoTabRl.setOnClickListener(this);
        threeTabRl.setOnClickListener(this);
        backImageView.setOnClickListener(this);
    }
    // 处理标签页选择的公共方法
    private void handleTabSelection(int index, ArrayList<MusicHistoryEntity> dataList, RelativeLayout.LayoutParams lp) {
        screenIndex = index;
        updateLp(lp);
        arrayList = dataList;
        musicHistoryAdapter.setNewData(dataList);
        mainHistoryNo.setVisibility(dataList.size() == 0 ? View.VISIBLE : View.GONE);
    }
    @Override
    public void onClick(View v) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) detailTabLine
                .getLayoutParams();
        int viewId = v.getId();

        if (viewId == R.id.activity_main_history_onetab_rl) {
            handleTabSelection(1, oneList,lp);
        }
        else if (viewId == R.id.activity_main_history_twotab_rl) {
            handleTabSelection(2, twoList,lp);
        }
        else if (viewId == R.id.activity_main_history_threetab_rl) {
            handleTabSelection(3, threeList,lp);
        }
        else if (viewId == R.id.main_history_activity_back_Rl) {
            // 返回按钮
            finish();
        }


        detailTabLine.setLayoutParams(lp);
    }


    @Override
    public void onCompleted(String url) {

    }

    @Override
    public void onError(BizException e) {
        mainHistoryNo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNext(ResponseBody responseBody, String url) {
        switch (url) {
            case RequestConfigs.GET_PLAY_LIST:
                try {
                    String json = responseBody.string();
                    LogUtil.i(TAG, json);
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getInt(Constant.RESP_CODE) == 0) {
                        LogUtil.i(TAG, "true");
                        JSONArray jsonArray = jsonObject.getJSONArray("musicList");
                        List<MusicHistoryEntity> musicHistoryEntityList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Gson gson = new Gson();
                            MusicHistoryEntity item = gson.fromJson(jsonArray.get(i).toString(), MusicHistoryEntity.class);
                            LogUtil.i(TAG, item.getMusicId() + "" + item.getCoverUrl() + " " + item.getSort() + " " + item.getTestNum() + " " + item.getMusicName());
                            musicHistoryEntityList.add(item);
                        }
                        LogUtil.i(TAG, musicHistoryEntityList.size());
                        for (int i = 0; i < musicHistoryEntityList.size(); i++) {
                            MusicHistoryEntity musicListItemEntity = musicHistoryEntityList.get(i);
                            String sort = musicListItemEntity.getSort();
                            if (sort.equals("01")) {
                                threeList.add(musicListItemEntity);
                            } else if (sort.equals("02")) {
                                twoList.add(musicListItemEntity);
                            } else if (sort.equals("03")) {
                                oneList.add(musicListItemEntity);
                            }
                        }
                        musicHistoryAdapter.setNewData(oneList);
                        LogUtil.i(TAG, "oneList.size=" + oneList.size() + " twoList.size=" + twoList.size() + " threeList.size=" + threeList.size());
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        if (oneList.size() == 0) {
            mainHistoryNo.setVisibility(View.VISIBLE);
        } else {
            mainHistoryNo.setVisibility(View.GONE);
        }
    }

    @Override
    public void historyItemClick(BaseViewHolder baseViewHolder, int position) {
        Intent intent = new Intent();
        intent.putExtra("musicId", arrayList.get(position).getMusicId());
        intent.putExtra("musicName", arrayList.get(position).getMusicName());
        intent.putExtra("musicUrl", arrayList.get(position).getCoverUrl());
        intent.setClass(this, MusicHistoryDetailActivity.class);
        startActivity(intent);
    }
}
