package com.weiyin.qinplus.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.activity.MusicDetailActivity;
import com.weiyin.qinplus.adapter.MusicDetailDialogAdapter;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.entity.MusicBookEntity;
import com.weiyin.qinplus.entity.MusicListItemEntity;
import com.weiyin.qinplus.rest.BizException;
import com.weiyin.qinplus.rest.DataService;
import com.weiyin.qinplus.rest.HttpRequestListener;
import com.weiyin.qinplus.rest.RequestConfigs;
import com.weiyin.qinplus.rest.RetrofitClient;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;
import com.weiyin.qinplus.ui.tv.view.recyclerview.DividerItemDecoration;
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
 *     desc   : 曲谱详情介绍弹窗
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class MusicDetailDialog extends Dialog implements MusicDetailDialogAdapter.ItemClickListener,
        MusicDetailDialogAdapter.ItemFocusListener, HttpRequestListener<ResponseBody> {
    public final String TAG = MusicDetailDialog.class.getSimpleName();

    private Activity mContext;

    private MusicDetailDialogAdapter listAdapter;
    private List<MusicListItemEntity> items = new ArrayList<>();

    private ImageView imageview;
    private TextView titleTextView;


    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.INTERNET_CALLBACK_GET_BOOK_LIST:

                    break;
                default:
                    break;
            }
        }
    };

    public MusicDetailDialog(Activity context) {
        super(context);
        this.mContext = context;
    }

    public MusicDetailDialog(Activity context, int themeResId, MusicBookEntity musicListItemEntities) {
        super(context, themeResId);
        this.mContext = context;
    }

    protected MusicDetailDialog(Activity context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musicdetaildialog_layout);

        LayoutHelper layoutHelper = new LayoutHelper(mContext);
        layoutHelper.scaleView(findViewById(R.id.musicdetaildialog_layout));

        initView();
    }

    public void update(MusicBookEntity musicBookEntity) {
        if (musicBookEntity != null) {
            Glide.with(mContext).load(WinYinPianoApplication.strUrl + musicBookEntity.getMusicPicUrl()).into(imageview);
            titleTextView.setText(musicBookEntity.getMusicBookName());
            items.clear();
            listAdapter.notifyDataSetChanged();
            getMusicListItemEntity(musicBookEntity.getMusicBookName(), musicBookEntity.getMusicSort(), musicBookEntity.getMusicLevel());
        }
    }

    private void initView() {
        imageview = (ImageView) findViewById(R.id.musicdetial_dialog_music_imageview);

        titleTextView = (TextView) findViewById(R.id.musicdetial_dialog_title_textview);

        RecyclerView verticalCustomRecyclerView = (RecyclerView) findViewById(R.id.musicdetial_dialog_verticalcustomRecyclerView);
        GridLayoutManager verGridLayoutManager = new GridLayoutManager(mContext, 1, GridLayoutManager.VERTICAL);

        verticalCustomRecyclerView.setLayoutManager(verGridLayoutManager);
        verticalCustomRecyclerView.setItemAnimator(new DefaultItemAnimator());
        listAdapter = new MusicDetailDialogAdapter(mContext, items, false);
        verticalCustomRecyclerView.setAdapter(listAdapter);
        verticalCustomRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,
                DividerItemDecoration.VERTICAL_LIST));
        listAdapter.setOnItemClickListener(this);
        listAdapter.setOnItemFcousListener(this);
    }

    public void getMusicListItemEntity(String musicBookName, String musicSort, String musicLevel) {
        musicBookName = musicBookName.replace(" ", "");
        musicSort = musicSort.replace(" ", "");
        musicLevel = musicLevel.replace(" ", "");

        DataService dataService = new DataService(RetrofitClient.getInstance().getService());
        Map<String, String> map = new HashMap<>();
        map.put("musicBookName", musicBookName);
        map.put("musicSort", musicSort);
        map.put("musicLevel", musicLevel);
        map.put("appVersion", Constant.getVersionName(mContext));
        map.put("platformType", Constant.CODE);
        LogUtil.i(TAG, musicBookName + "----" + musicSort + "-----" + musicLevel + "----" + (Constant.getVersionName(mContext)) + "----" + Constant.CODE);
        dataService.getMusicOfBook(map, this);
    }

    @Override
    public void onItemClick(View view, int postion) {
        Intent intent = new Intent();
        intent.putExtra("musicId", items.get(postion).getId());
        intent.putExtra("musicName", items.get(postion).getMusicName());
        intent.putExtra("musicUrl", items.get(postion).getMusicPicUrl());
        intent.setClass(mContext, MusicDetailActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public void onItemFcous(View view, int position) {

    }

    @Override
    public void onCompleted(String url) {
        LogUtil.i(TAG, "onCompleted url=" + url);
    }

    @Override
    public void onError(BizException e) {

        LogUtil.i(TAG, "onCompleted url=" + e.getUrl() + " msg=" + e.getMsg());
    }

    @Override
    public void onNext(ResponseBody baseResponse, String url) {
        switch (url) {
            case RequestConfigs.GET_MUSIC_OF_BOOK:
                try {
                    String json = baseResponse.string();
                    LogUtil.i(TAG, "json=" + json);
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getInt(Constant.RESP_CODE) == 0) {
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Gson gson = new Gson();
                            MusicListItemEntity item = gson.fromJson(jsonArray.get(i).toString(), MusicListItemEntity.class);
                            items.add(item);
                        }
                        listAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
