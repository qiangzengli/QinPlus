package com.weiyin.qinplus.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.entity.MusicListItemEntity;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app  搜索相关Adapter
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class SearchResultItemAdapter extends RecyclerView.Adapter<SearchViewHolder> {

    private Activity context;

    public interface MyItemClickListener {
         void onItemClick(View view, int postion);
    }

    public interface MyItemLongClickListener {
        void onItemLongClick(View view, int postion);
    }

    private MyItemClickListener mItemClickListener;
    private MyItemLongClickListener mItemLongClickListener;

    private List<MusicListItemEntity> labels;

    public SearchResultItemAdapter(Activity context) {
        this.labels = new ArrayList<>();
        this.context = context;
    }

    public void searchUpdateList(List<MusicListItemEntity> list) {
        this.labels = list;
        Log.i("adapter", "list.size=" + list.size() + " labels.size=" + labels.size());
        notifyDataSetChanged();
    }


    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchresult_layout, parent, false);
        LayoutHelper layoutHelper = new LayoutHelper(context);
        layoutHelper.scaleView(view);
        return new SearchViewHolder(view, mItemClickListener, mItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (holder != null) {
            if (labels.get(position) != null) {
                String str = labels.get(position).getMusicName().replace(" ", "");
                holder.textName.setText(str);
                holder.textType.setText(labels.get(position).getMusicBookName());
                holder.linearResultSearchLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onItemClick(v, position);
                    }
                });
            }
        }
    }


    @Override
    public int getItemCount() {
        return labels.size();
    }


    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(MyItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }
}
