package com.weiyin.qinplus.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.activity.AboutAppActivity;
import com.weiyin.qinplus.activity.HelpActivity;
import com.weiyin.qinplus.activity.LoginActivity;
import com.weiyin.qinplus.activity.MyCollectionActivity;
import com.weiyin.qinplus.application.Constant;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;
import com.weiyin.qinplus.ui.tv.view.CircleImageView;

import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 个人中心
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class MainPersonalCenterFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;

    private CircleImageView userHeadImage;

    private SharedPreferences sharedPreferences;
    private RelativeLayout fragmentPersonalCenterRl;

    private TextView nameTextView;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_personalcenter_layout, null);
            LayoutHelper layoutHelper = new LayoutHelper(getActivity());
            layoutHelper.scaleView(rootView);

            sharedPreferences = getActivity().getSharedPreferences(Constant.USER, 0);
            initView();

        }

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        String imageUrl = sharedPreferences.getString(Constant.HEAD_IMAGE_URL, "");
        String strName = sharedPreferences.getString(Constant.NICKNAME, "");
        if (!StringUtils.isEmpty(imageUrl)) {
            Glide.with(getActivity()).load(imageUrl).into(userHeadImage);
        } else {
            userHeadImage.setImageResource(R.drawable.activity_main_personcenter_head);
        }
        if (!StringUtils.isEmpty(strName)) {
            nameTextView.setText(strName);
        } else {
            nameTextView.setText("请登录");
        }

        Log.i("aa", "height=" + fragmentPersonalCenterRl.getHeight() + " width=" + fragmentPersonalCenterRl.getWidth());
    }

    public void initView() {
        RelativeLayout myCollection = (RelativeLayout) rootView.findViewById(R.id.fragment_personalcenter_my_collection);
        RelativeLayout about = (RelativeLayout) rootView.findViewById(R.id.fragment_personalcenter_about);
        RelativeLayout help = (RelativeLayout) rootView.findViewById(R.id.fragment_personalcenter_help);
        userHeadImage = (CircleImageView) rootView.findViewById(R.id.fragment_main_personcenter_usehead_image);
        nameTextView = (TextView) rootView.findViewById(R.id.fragment_main_personcenter_name_textview);
        fragmentPersonalCenterRl = (RelativeLayout) rootView.findViewById(R.id.fragment_personalcenter_rl);


        userHeadImage.setOnClickListener(this);
        help.setOnClickListener(this);
        about.setOnClickListener(this);
        myCollection.setOnClickListener(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) rootView.getParent()).removeView(rootView);
        Log.e("ClassicalFragment", "onDestroyView");
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.fragment_personalcenter_my_collection) {
            // 我的收藏
            startActivity(new Intent(getActivity(), MyCollectionActivity.class));
        }
        else if (viewId == R.id.fragment_personalcenter_about) {
            // 关于应用
            startActivity(new Intent(getActivity(), AboutAppActivity.class));
        }
        else if (viewId == R.id.fragment_personalcenter_help) {
            // 帮助中心
            startActivity(new Intent(getActivity(), HelpActivity.class));
        }
        else if (viewId == R.id.fragment_main_personcenter_usehead_image) {
            // 用户头像点击跳转登录
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }
}
