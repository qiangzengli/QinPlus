package com.weiyin.qinplus.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import java.util.List;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app  最近弹奏Adapter
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    private List<String> itemList;
    private Activity context;


    public interface ItemClickListener {
        public void onSearchHistoryAdapterItemClick(View view, int postion);
    }

    public interface OnKeyListener {
        public void onKey();
    }


    private ItemClickListener mItemClickListener;
    private OnKeyListener mOnkeyListener;


    public SearchHistoryAdapter(Activity context, List<String> items) {
        this.itemList = items;
        this.context = context;
    }

    public void SearchUpdateList(List<String> list) {
        if (list != null) {
            this.itemList = list;
            notifyDataSetChanged();
        }
    }


    @Override
    public SearchHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchhistory_item, parent, false);
        LayoutHelper layoutHelper = new LayoutHelper(context);
        layoutHelper.scaleView(v);
        return new SearchHistoryAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SearchHistoryAdapter.ViewHolder holder, final int position) {

        final String item = itemList.get(position);
        if (item != null) {
            holder.musicTextName.setText(item);

        }

        holder.musicTextName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    holder.musicTextName.setTextColor(context.getResources().getColor(R.color.Unchecked));

                } else {
                    holder.musicTextName.setTextColor(context.getResources().getColor(R.color.white));
                }
            }
        });

        holder.musicTextName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mItemClickListener.onSearchHistoryAdapterItemClick(view, position);
            }
        });
    }

    public void setOnItemClickListener(SearchHistoryAdapter.ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOnkeyListener(SearchHistoryAdapter.OnKeyListener listener) {
        this.mOnkeyListener = listener;
    }


    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView musicTextName;


        public ViewHolder(View itemView) {
            super(itemView);

            musicTextName = (TextView) itemView.findViewById(R.id.searchhistory_item_textview);


        }
    }

}
