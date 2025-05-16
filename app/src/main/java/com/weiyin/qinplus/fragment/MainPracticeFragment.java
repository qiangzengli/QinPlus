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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.activity.PracticeActivity;
import com.weiyin.qinplus.adapter.MainPracticeAdapter;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.entity.MainPracticeEntity;
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
 *     desc   : app 音乐教室
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class MainPracticeFragment extends BaseFragment implements View.OnClickListener, HttpRequestListener<ResponseBody>, MainPracticeAdapter.OnItemClickInterface {

    private View view;

    private MainPracticeAdapter adapter;
    private List<MainPracticeEntity> entityList;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.INTERNET_GET_PRACTICE_LIST:

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
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main_practice_layout, null);
            LayoutHelper layoutHelper = new LayoutHelper(getActivity());
            layoutHelper.scaleView(view);

            if (entityList == null) {
                entityList = new ArrayList<>();
                initView();
                getPractice();
            }
        }
        return view;
    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.mainPracticeRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3, LinearLayoutManager.HORIZONTAL, false));
        adapter = new MainPracticeAdapter(getActivity(), entityList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickInterface(this);
    }

    private void getPractice() {
        DataService dataService = new DataService(RetrofitClient.getInstance().getService());
        dataService.getMusicClass(Constant.getVersionName(getContext()), Constant.CODE, this);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onItemClick(View view, int position) {
        if (entityList != null) {
            if (entityList.size() > position) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), PracticeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("MainPracticeEntity", entityList.get(position));
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        }
    }

    @Override
    public void onCompleted(String url) {

    }

    @Override
    public void onError(BizException e) {
        ToastUtil.showTextToast(getContext(), e.getMsg());
    }

    @Override
    public void onNext(ResponseBody baseResponse, String url) {
        switch (url) {
            case RequestConfigs.GET_MUSIC_CLASS:
                try {
                    JSONObject jsonObject = new JSONObject(baseResponse.string());
                    if ("0".equals(jsonObject.getString("respCode"))) {
                        Gson gson = new Gson();
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            MainPracticeEntity mainPracticeEntity = gson.fromJson(jsonArray.get(i).toString(), MainPracticeEntity.class);
                            entityList.add(mainPracticeEntity);
                        }
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
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
