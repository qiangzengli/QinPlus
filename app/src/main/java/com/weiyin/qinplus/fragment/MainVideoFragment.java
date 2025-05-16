package com.weiyin.qinplus.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.activity.VideoActivity;
import com.weiyin.qinplus.adapter.MainVideoFragmentAdapter;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.entity.VideoEntity;
import com.weiyin.qinplus.rest.BizException;
import com.weiyin.qinplus.rest.DataService;
import com.weiyin.qinplus.rest.HttpRequestListener;
import com.weiyin.qinplus.rest.RequestConfigs;
import com.weiyin.qinplus.rest.RetrofitClient;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 视频
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class MainVideoFragment extends BaseFragment implements HttpRequestListener<ResponseBody>, MainVideoFragmentAdapter.OnItemClickInterface {
    private static final String TAG = MainVideoFragment.class.getSimpleName();

    private View rootView;
    private MainVideoFragmentAdapter mainVideoFragmentAdapter;
    private List<VideoEntity> videoEntityList;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.INTERNET_GET_VIDEO_LIST:

                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.main_video_fragment_layout, null);
            LayoutHelper layoutHelper = new LayoutHelper(getActivity());
            layoutHelper.scaleView(rootView);
            if (videoEntityList == null) {
                videoEntityList = new ArrayList<>();
                initView();
                getVideo();
            }
        }
        return rootView;
    }

    private void getVideo() {

        DataService dataService = new DataService(RetrofitClient.getInstance().getService());
        dataService.getVideo(Constant.getVersionName(getContext()), Constant.CODE, this);
    }

    private void initView() {
        RecyclerView recycleView = (RecyclerView) rootView.findViewById(R.id.mainVideoFragmentRecycleView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.HORIZONTAL, false);
        recycleView.setLayoutManager(gridLayoutManager);
        mainVideoFragmentAdapter = new MainVideoFragmentAdapter(getActivity(), videoEntityList);
        recycleView.setAdapter(mainVideoFragmentAdapter);
        mainVideoFragmentAdapter.setOnItemClickInterface(this);
    }


    @Override
    public void onItemClick(View view, int position) {
        if (videoEntityList != null) {
            if (videoEntityList.size() > position) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), VideoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("VideoEntity", videoEntityList.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onCompleted(String url) {

    }

    @Override
    public void onError(BizException e) {
        ToastUtil.showTextToast(getContext(), e.getMsg());
        LogUtil.i(TAG, e.getUrl()+" "+e.getMsg());
    }

    @Override
    public void onNext(ResponseBody baseResponse, String url) {
        switch (url) {
            case RequestConfigs.GET_VIDEO:
                try {
                    String json = baseResponse.string();
                    LogUtil.i(TAG, json);
                    JSONObject jsonObject = new JSONObject(json);
                    if (0 == (jsonObject.getInt(Constant.RESP_CODE))) {
                        Gson gson = new Gson();
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            VideoEntity videoEntity = gson.fromJson(jsonArray.get(i).toString(), VideoEntity.class);
                            videoEntityList.add(videoEntity);
                        }
                        if (mainVideoFragmentAdapter != null) {
                            Log.i(TAG, "size= " + videoEntityList.size());
                            mainVideoFragmentAdapter.notifyDataSetChanged();
                        }
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
