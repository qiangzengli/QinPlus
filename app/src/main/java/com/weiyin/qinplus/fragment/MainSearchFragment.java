package com.weiyin.qinplus.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.activity.MusicDetailActivity;
import com.weiyin.qinplus.adapter.SearchHistoryAdapter;
import com.weiyin.qinplus.adapter.SearchResultItemAdapter;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.entity.MusicListItemEntity;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;
import com.weiyin.qinplus.ui.tv.view.recyclerview.CustomRecyclerView;
import com.weiyin.qinplus.ui.tv.view.recyclerview.GridLayoutManager;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 搜索
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class MainSearchFragment extends BaseFragment implements View.OnClickListener, SearchHistoryAdapter.ItemClickListener, SearchResultItemAdapter.MyItemClickListener {
    public final String TAG = MainSearchFragment.class.getSimpleName();

    private ArrayList<MusicListItemEntity> musicList;
    private View rootView;
    private SearchResultItemAdapter mAdapter;

    private SearchHistoryAdapter searchHistoryAdapter;
    private EditText editText;
    private String strKey = "";

    private String oldKey = "";
    private ArrayList<String> items;

    private TextView searchTextView;

    private boolean search = false;

    @SuppressLint("HandlerLeak")
    private Handler handlerUI = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_main_search_layout, null);
            LayoutHelper layoutHelper = new LayoutHelper(getActivity());
            layoutHelper.scaleView(rootView);

            if (musicList == null) {
                musicList = new ArrayList<>();
                items = new ArrayList<>();
                getSet();
                initData();
                initView();

            }
        }
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        getSet();
    }


    public void initData() {
        Log.i(TAG, "size=" + musicList.size());
        if (musicList.size() == 0) {
            if (WinYinPianoApplication.musicBookEntity01.size() > 0) {
                if (WinYinPianoApplication.musicListItemEntityArrayList.size() > 3) {
                    musicList.add(WinYinPianoApplication.musicListItemEntityArrayList.get(0));
                    musicList.add(WinYinPianoApplication.musicListItemEntityArrayList.get(1));
                    musicList.add(WinYinPianoApplication.musicListItemEntityArrayList.get(2));
                }
                Log.i(TAG, "size=" + musicList.size());
            }
        }
    }

    public void getSet() {
        if (items != null) {
            items.clear();
            SharedPreferences settings = getActivity().getSharedPreferences("search", 0);
            int count = settings.getInt("seachcount", 0);
            Log.i(TAG, "count=" + count);
            for (int i = count; i > -1; i--) {
                if (count - i < 4) {
                    String s = settings.getString("searchkey" + i, "");
                    Log.i(TAG, s);
                    if (!s.equals("")) {
                        s = s.replace(" ", "");
                        if (!items.contains(s)) {
                            items.add(s);
                        } else {
                            count--;
                        }
                    }
                }
            }
        }
        if (searchHistoryAdapter != null) {
            searchHistoryAdapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        editText = (EditText) rootView.findViewById(R.id.fragment_search_edit_text_layout_search);
        searchTextView = (TextView) rootView.findViewById(R.id.fragment_main_search_textview);
        TextView historyCleanTextView = (TextView) rootView.findViewById(R.id.fragment_history_clean_textview);

        CustomRecyclerView customRecyclerView = (CustomRecyclerView) rootView.findViewById(R.id.fragment_search_customrecyclerview);

        CustomRecyclerView customRecyclerView1 = (CustomRecyclerView) rootView.findViewById(R.id.fragment_history_search_customrecyclerview);

        GridLayoutManager historyGridLayoutManager = new GridLayoutManager(getActivity(), 4, GridLayoutManager.VERTICAL);
        customRecyclerView1.setLayoutManager(historyGridLayoutManager);
        customRecyclerView1.setItemAnimator(new DefaultItemAnimator());
        searchHistoryAdapter = new SearchHistoryAdapter(getActivity(), items);
        customRecyclerView1.setAdapter(searchHistoryAdapter);


        historyCleanTextView.setOnClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL);
        customRecyclerView.setLayoutManager(gridLayoutManager);
        customRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new SearchResultItemAdapter(getActivity());
        customRecyclerView.setAdapter(mAdapter);

        searchHistoryAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.searchUpdateList(musicList);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.i(TAG, "id=" + actionId);
                if ((actionId == 0 || actionId == 3) && event != null) {
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }
                    search();
                    return true;
                }
                return false;
            }
        });
        editText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = editText.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > editText.getWidth()
                        - editText.getPaddingRight()
                        - drawable.getIntrinsicWidth() && search) {
                    editText.setText("");
                }
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                strKey = s.toString();
                setDrawAble();
            }
        });
    }

    public void setDrawAble() {
        search();
        if (StringUtils.isEmpty(strKey)) {
            search = false;
            Drawable drawable = getResources().getDrawable(R.drawable.activity_main_search_editicon);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            editText.setCompoundDrawables(null, null, drawable, null);
        } else {
            search = true;
            Drawable drawable = getResources().getDrawable(R.drawable.fragment_delect);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            editText.setCompoundDrawables(null, null, drawable, null);
        }
    }

    public void cleanText() {
        if (strKey != null) {
            Log.i(TAG, "cleanText=" + strKey);
            if (strKey.indexOf(" ") == 0) {
                strKey = strKey.substring(strKey.indexOf(" ") + 1);
                Log.i(TAG, "clean=" + strKey);
                cleanText();
            }
        } else {
            Log.i(TAG, "return=     ");
        }
    }

    public void search() {
        if (strKey != null) {
            if (!oldKey.equals(strKey)) {
                musicList.clear();
                if (strKey.length() != 0) {
                    searchHistoryAdapter.notifyDataSetChanged();
                }
                searchText();
                Log.i(TAG, "music.size=" + musicList.size());
                if (musicList.size() > 0) {
                    searchTextView.setText("您是否在找……");
                } else {
                    searchTextView.setText("热门搜索");
                    initData();
                }
                oldKey = strKey;
                mAdapter.searchUpdateList(musicList);
            }
        }

    }

    public void searchText() {
        cleanText();
        if (strKey != null) {
            if (strKey.contains(" ")) {
                String seach = strKey.substring(0, strKey.indexOf(" "));
                addItem(seach);
                strKey = strKey.substring(strKey.indexOf(" ") + 1);
                searchText();
            } else {
                if (strKey.length() != 0) {
                    addItem(strKey);
                }
            }
        }
    }

    public void addItem(String search) {
        for (int i = 0; i < WinYinPianoApplication.musicListItemEntityArrayList.size(); i++) {
            MusicListItemEntity musicListItemEntity = WinYinPianoApplication.musicListItemEntityArrayList.get(i);
            if (musicListItemEntity.getMusicName() != null) {
                if (musicListItemEntity.getMusicName().contains(search)) {
                    if (!musicList.contains(musicListItemEntity)) {
                        musicList.add(musicListItemEntity);
                    }
                }
                if (musicListItemEntity.getMnQp().contains(search)) {
                    if (!musicList.contains(musicListItemEntity)) {
                        musicList.add(musicListItemEntity);
                    }
                }
                if (musicListItemEntity.getMnSzm().contains(search)) {
                    if (!musicList.contains(musicListItemEntity)) {
                        musicList.add(musicListItemEntity);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fragment_history_clean_textview) {

            SharedPreferences settings = getActivity().getSharedPreferences("search", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.apply();
            editor.commit();

            getSet();
            searchHistoryAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        SharedPreferences settings = getActivity().getSharedPreferences("search", 0);
        int count = settings.getInt("seachcount", -1);
        SharedPreferences.Editor edit = settings.edit();
        edit.putString("searchkey" + (count + 1), musicList.get(position).getMusicName());
        edit.putInt("seachcount", count + 1);
        edit.apply();
        edit.commit();
        Log.i(TAG, "commit" + musicList.get(position).getMusicName());

        Intent intent = new Intent();
        intent.putExtra("musicId", musicList.get(position).getId());
        intent.putExtra("musicName", musicList.get(position).getMusicName());
        intent.putExtra("musicUrl", musicList.get(position).getMusicPicUrl());
        intent.setClass(getActivity(), MusicDetailActivity.class);
        getActivity().startActivity(intent);
    }

    @Override
    public void onSearchHistoryAdapterItemClick(View view, int postion) {
        strKey = items.get(postion);
        editText.setText(strKey);
        setDrawAble();
        search();
    }
}
