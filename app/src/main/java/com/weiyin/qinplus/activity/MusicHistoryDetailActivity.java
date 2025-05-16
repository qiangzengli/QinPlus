package com.weiyin.qinplus.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.adapter.MusicHistoryDetailAdapter;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.entity.MusicHistoryDetailEntity;
import com.weiyin.qinplus.rest.BizException;
import com.weiyin.qinplus.rest.DataService;
import com.weiyin.qinplus.rest.HttpRequestListener;
import com.weiyin.qinplus.rest.RequestConfigs;
import com.weiyin.qinplus.rest.RetrofitClient;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;
import com.weiyin.qinplus.ui.tv.view.WheelView;

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
 *     time   : 2018/02/06
 *     desc   :
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
@SuppressLint("Registered")
public class MusicHistoryDetailActivity extends BaseActivity implements HttpRequestListener<ResponseBody>, View.OnClickListener {
    private List<MusicHistoryDetailEntity> musicDetailEntities, musicDetailEntitieAll;
    private DataService dataService;
    private String musicId, musicName, musicUrl;
    private MusicHistoryDetailAdapter musicHistoryDetailAdapter;
    private WheelView wheelView;
    private List<String> list;
    private List<List<String>> list1;
    private List<List<List<String>>> list2;
    private Bitmap bitmap;
    private RelativeLayout historyDetailLayoutRl;
    private TextView historyDetailLayoutRlYear, historyDetailLayoutRlMonth, historyDetailLayoutRlDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_history_detail_layout);
        LayoutHelper layoutHelper = new LayoutHelper(this);
        layoutHelper.scaleView(findViewById(R.id.historyDetailLayout));
        musicId = getIntent().getStringExtra("musicId");
        musicName = getIntent().getStringExtra("musicName");
        musicUrl = getIntent().getStringExtra("musicUrl");

        if (list == null) {
            list = new ArrayList<>();
        }
        if (list1 == null) {
            list1 = new ArrayList<>();
        }
        if (list2 == null) {
            list2 = new ArrayList<>();
        }

        if (musicDetailEntities == null) {
            musicDetailEntities = new ArrayList<>();
        }
        if (musicDetailEntitieAll == null) {
            musicDetailEntitieAll = new ArrayList<>();
        }
        initView();
        getPlayRecord(musicId, "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    private void initView() {
        historyDetailLayoutRlYear = (TextView) findViewById(R.id.historyDetailLayoutRlYear);
        historyDetailLayoutRlMonth = (TextView) findViewById(R.id.historyDetailLayoutRlMonth);
        historyDetailLayoutRlDay = (TextView) findViewById(R.id.historyDetailLayoutRlDay);
        historyDetailLayoutRl = (RelativeLayout) findViewById(R.id.historyDetailLayoutRl);
        dataService = new DataService(RetrofitClient.getInstance().getService());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.historyDetailRecyclerView);
        musicHistoryDetailAdapter = new MusicHistoryDetailAdapter(this,
                R.layout.history_detail_item_layout, musicDetailEntities);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(musicHistoryDetailAdapter);

        ImageView historyDetailLinearLayoutImageView = (ImageView) findViewById(R.id.historyDetailLinearLayoutImageView);
        RelativeLayout titleBack = (RelativeLayout) findViewById(R.id.titleBackRl);
        TextView historyDetailLinearLayoutTextView = (TextView) findViewById(R.id.historyDetailLinearLayoutTextView);
        TextView titleContent = (TextView) findViewById(R.id.titleContent);
        LinearLayout historyDetailLinearLayout = (LinearLayout) findViewById(R.id.historyDetailLinearLayout);

        historyDetailLinearLayout.setOnClickListener(this);
        titleBack.setOnClickListener(this);

        RelativeLayout historyDetailLayout = (RelativeLayout) findViewById(R.id.historyDetailLayout);
        bitmap = Constant.readBitMap(this, R.drawable.opern_background);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
        historyDetailLayout.setBackground(bitmapDrawable);

        Glide.with(this).load(WinYinPianoApplication.strUrl + musicUrl).into(historyDetailLinearLayoutImageView);
        titleContent.setText(musicName);
        historyDetailLinearLayoutTextView.setText(musicName);

        historyDetailLayoutRl.setOnClickListener(this);
    }

    void getPlayRecord(String musicId, String startDate) {
        Map<String, String> map = new HashMap<>();
        map.put("appVersion", Constant.getVersionName(this.getApplicationContext()));
        map.put("platformType", Constant.CODE);
        map.put("userId", WinYinPianoApplication.strUid);
        map.put("musicId", musicId);
        map.put("startDate", startDate);
        dataService.getPlayRecord(map, this);
    }

    @Override
    public void onCompleted(String url) {

    }

    @Override
    public void onError(BizException e) {

    }

    @Override
    public void onNext(ResponseBody responseBody, String url) {
        switch (url) {
            case RequestConfigs.GET_PLAY_RECORD:
                try {
                    String json = responseBody.string();
                    LogUtil.i(TAG, json);
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getInt(Constant.RESP_CODE) == 0) {
                        JSONArray jsonArray = jsonObject.getJSONArray("playList");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Gson gson = new Gson();
                            MusicHistoryDetailEntity musicHistoryDetailEntity = gson.fromJson(jsonArray.getString(i).toString(), MusicHistoryDetailEntity.class);
                            musicHistoryDetailAdapter.addData(musicHistoryDetailEntity);
                            musicDetailEntitieAll.add(musicHistoryDetailEntity);
                            String date = musicHistoryDetailEntity.getStartDate();
                            String[] dates = date.split("-");
                            if (!list.contains("全部")) {
                                list.add("全部");
                                List<String> strings1 = new ArrayList<>();
                                List<List<String>> lists = new ArrayList<>();
                                List<String> list3 = new ArrayList<>();

                                strings1.add("全部");
                                list1.add(strings1);

                                list3.add("全部");
                                lists.add(list3);
                                list2.add(lists);
                            }
                            if (!list.contains(dates[0])) {
                                list.add(dates[0]);
                                List<List<String>> lists = new ArrayList<>();
                                List<String> list3 = new ArrayList<>();
                                List<String> strings1 = new ArrayList<>();
                                strings1.add("全部");
                                list1.add(strings1);
                                list3.add("全部");
                                lists.add(list3);
                                list2.add(lists);
                            }
                            int inde = list.indexOf(dates[0]);
                            if (!list1.get(inde).contains("全部")) {
                                list1.get(inde).add("全部");
                                List<List<String>> lists = new ArrayList<>();
                                List<String> list3 = new ArrayList<>();
                                list3.add("全部");
                                lists.add(list3);
                                list2.add(lists);
                            }
                            if (!list1.get(inde).contains(dates[1])) {
                                list1.get(inde).add(dates[1]);
                                List<String> list3 = new ArrayList<>();
                                list3.add("全部");
                                list2.get(inde).add(list3);
                            }
                            int index = list1.get(inde).indexOf(dates[1]);
                            if (!list2.get(inde).get(index).contains(dates[2])) {
                                list2.get(inde).get(index).add(dates[2]);
                            }
                        }
                        Log.i(TAG, "list.size=" + list.size() + " list1.size=" + list1.size() + " list2.size=" + list2.size());
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void initData(String date, List<String> strings1, List<String> list3, List<List<String>> lists) {
        Log.i(TAG, date);
    }

    private void showPickerView() {
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                String year = list.get(options1);
                String month = list1.get(options1).get(option2);
                String day = list2.get(options1).get(option2).get(options3);
                //返回的分别是三个级别的选中位置
                historyDetailLayoutRlYear.setText(year);
                historyDetailLayoutRlMonth.setText(month);
                historyDetailLayoutRlDay.setText(day);
                StringBuilder startDate = new StringBuilder();
                if ("全部".equals(year)) {
                    startDate.append("1971");
                } else {
                    startDate.append(year);
                }
                if ("全部".equals(month)) {
                    startDate.append("1");
                } else {
                    startDate.append(month);
                }
                if ("全部".equals(day)) {
                    startDate.append("1");
                } else {
                    startDate.append(day);
                }
                setData(startDate.toString());
            }
        }).build();
        pvOptions.setPicker(list, list1, list2);
        pvOptions.show();
    }

    private void setData(String startDate) {
        if (musicDetailEntities != null) {
            musicDetailEntities.clear();
        }
        for (int i = 0; i < musicDetailEntitieAll.size(); i++) {
            MusicHistoryDetailEntity musicHistoryDetailEntity = musicDetailEntitieAll.get(i);
            String musicDate = musicHistoryDetailEntity.getStartDate().replace("-", "");
            if (Integer.parseInt(musicDate) >= Integer.parseInt(startDate)) {
                musicHistoryDetailAdapter.addData(musicHistoryDetailEntity);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.historyDetailLayoutRl) {
            // 显示选择器视图
            showPickerView();
        }
        else if (viewId == R.id.titleBackRl) {
            // 返回按钮 - 结束当前Activity
            finish();
        }
        else if (viewId == R.id.historyDetailLinearLayout) {
            // 跳转到音乐详情Activity
            Intent intent = new Intent();
            intent.putExtra("musicId", musicId);
            intent.putExtra("musicName", musicName);
            intent.putExtra("musicUrl", musicUrl);
            intent.setClass(this, MusicDetailActivity.class);
            this.startActivity(intent);
        }
    }
}
