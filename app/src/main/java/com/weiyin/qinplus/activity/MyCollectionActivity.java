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
import com.weiyin.qinplus.adapter.MyCollectionAdapter;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.entity.MusicCollectionEntity;
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
 *     desc   : app 我的收藏Activity
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class MyCollectionActivity extends BaseActivity implements HttpRequestListener<ResponseBody>, View.OnClickListener, MyCollectionAdapter.OnCollectionItemClickListener {
    private ArrayList<MusicCollectionEntity> arrayList, oneList, twoList, threeList;
    private ArrayList<String> idList;

    private ImageView tabLineIv, mainHistoryNo;
    private MyCollectionAdapter myCollectionAdapter;

    /**
     * 屏幕寬度
     */
    private int screenWidth;
    /**
     * 坐标
     */
    private int screenIndex;
    private Bitmap bitmap;
    private DataService dataService;

    public MyCollectionActivity() {
        screenIndex = 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);

        LayoutHelper layoutHelper = new LayoutHelper(this);
        layoutHelper.scaleView(findViewById(R.id.activity_my_collection));
        dataService = new DataService(RetrofitClient.getInstance().getService());
        arrayList = new ArrayList<>();
        idList = new ArrayList<>();
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
        screenWidth = dpMetrics.widthPixels;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tabLineIv
                .getLayoutParams();
        lp.width = screenWidth / 6;
        lp.height = 5;
        lp.leftMargin = screenWidth / 3 * screenIndex - (screenWidth / 3 - screenWidth / 6) / 2 - screenWidth / 6;
        tabLineIv.setLayoutParams(lp);
    }

    public void updateLp(RelativeLayout.LayoutParams lp) {
        lp.leftMargin = screenWidth / 3 * screenIndex - (screenWidth / 3 - tabLineIv.getWidth()) / 2 - tabLineIv.getWidth();
    }

    void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("appVersion", Constant.getVersionName(this.getApplicationContext()));
        map.put("platformType", Constant.CODE);
        map.put("userId", WinYinPianoApplication.strUid);
        dataService.getCollectList(map, this);
    }


    /**/
    void initView() {
        mainHistoryNo = (ImageView) findViewById(R.id.mainHistoryNo);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_my_collection);
        bitmap = Constant.readBitMap(this, R.drawable.opern_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        relativeLayout.setBackground(bitmapDrawable);

        RelativeLayout oneTabRl = (RelativeLayout) findViewById(R.id.activity_my_collection_onetab_rl);
        RelativeLayout twoTabRl = (RelativeLayout) findViewById(R.id.activity_my_collection_twotab_rl);
        RelativeLayout threeTabRl = (RelativeLayout) findViewById(R.id.activity_my_collection_threetab_rl);

        RelativeLayout backImageView = (RelativeLayout) findViewById(R.id.my_collection_activity_back_Rl);

        tabLineIv = (ImageView) findViewById(R.id.activity_my_collection_iv_sboard_detail_tabline);

        CustomRecyclerView customrecyclerview = (CustomRecyclerView) findViewById(R.id.activity_my_collection_customrecyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL);
        myCollectionAdapter = new MyCollectionAdapter(this, R.layout.mycollection_item_layout, arrayList);

        customrecyclerview.setLayoutManager(gridLayoutManager);
        customrecyclerview.setItemAnimator(new DefaultItemAnimator());

        customrecyclerview.setAdapter(myCollectionAdapter);
        myCollectionAdapter.setOnHistoryItemClickListener(this);

        oneTabRl.setOnClickListener(this);
        twoTabRl.setOnClickListener(this);
        threeTabRl.setOnClickListener(this);
        backImageView.setOnClickListener(this);
    }
    /**
     * 处理收藏标签页切换的公共方法
     * @param index 标签页索引
     * @param dataList 对应标签页的数据列表
     */
    private void handleCollectionTab(int index, ArrayList<MusicCollectionEntity> dataList,RelativeLayout.LayoutParams lp) {
        screenIndex = index;
        updateLp(lp);
        arrayList = dataList;
        myCollectionAdapter.setNewData(dataList);
        mainHistoryNo.setVisibility(dataList.isEmpty() ? View.VISIBLE : View.GONE);
    }
    @Override
    public void onClick(View v) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tabLineIv
                .getLayoutParams();
        int viewId = v.getId();

        if (viewId == R.id.activity_my_collection_onetab_rl) {
            handleCollectionTab(1, oneList,lp);
        }
        else if (viewId == R.id.activity_my_collection_twotab_rl) {
            handleCollectionTab(2, twoList,lp);
        }
        else if (viewId == R.id.activity_my_collection_threetab_rl) {
            handleCollectionTab(3, threeList,lp);
        }
        else if (viewId == R.id.my_collection_activity_back_Rl) {
            // 返回按钮处理
            finish();
        }
        tabLineIv.setLayoutParams(lp);
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
            case RequestConfigs.GET_COLLECT_LIST:
                try {
                    String json = responseBody.string();
                    LogUtil.i(TAG, json);
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getInt(Constant.RESP_CODE) == 0) {
                        LogUtil.i(TAG, "true");
                        JSONArray jsonArray = jsonObject.getJSONArray("collectList");
                        List<MusicCollectionEntity> musicHistoryEntityList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Gson gson = new Gson();
                            MusicCollectionEntity item = gson.fromJson(jsonArray.get(i).toString(), MusicCollectionEntity.class);
                            musicHistoryEntityList.add(item);
                        }
                        LogUtil.i(TAG, musicHistoryEntityList.size());
                        for (int i = 0; i < musicHistoryEntityList.size(); i++) {
                            MusicCollectionEntity musicListItemEntity = musicHistoryEntityList.get(i);
                            String sort = musicListItemEntity.getSort();
                            if (sort.equals("01")) {
                                threeList.add(musicListItemEntity);
                            } else if (sort.equals("02")) {
                                twoList.add(musicListItemEntity);
                            } else if (sort.equals("03")) {
                                oneList.add(musicListItemEntity);
                            }
                        }
                        myCollectionAdapter.setNewData(oneList);
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
    public void collectionItemClick(BaseViewHolder baseViewHolder, int position) {
        Intent intent = new Intent();
        intent.putExtra("musicId", arrayList.get(position).getMusicId());
        intent.putExtra("musicName", arrayList.get(position).getMusicName());
        intent.putExtra("musicUrl", arrayList.get(position).getCoverUrl());
        intent.setClass(this, MusicDetailActivity.class);
        startActivity(intent);
    }
}
