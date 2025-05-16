package com.weiyin.qinplus.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiyin.qinplus.R;


/**
 *
 * @author njf
 * @date 2016/7/26
 */
public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    TextView textName;
    TextView textType;
    RelativeLayout linearResultSearchLayout;


    private SearchResultItemAdapter.MyItemClickListener mListener;
    private SearchResultItemAdapter.MyItemLongClickListener mLongClickListener;

    public SearchViewHolder(View itemView, SearchResultItemAdapter.MyItemClickListener listener,
                            SearchResultItemAdapter.MyItemLongClickListener longClickListener) {
        super(itemView);

        textName = (TextView) itemView.findViewById(R.id.item_searchresult_nametext);
        textType = (TextView) itemView.findViewById(R.id.item_searchresult_sorttext);
        linearResultSearchLayout = (RelativeLayout) itemView.findViewById(R.id.linear_result_search_layout);
        this.mListener = listener;
        this.mLongClickListener = longClickListener;

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (mListener != null) {
            mListener.onItemClick(v, getPosition());
        }

    }

    @Override
    public boolean onLongClick(View v) {

        if (mLongClickListener != null) {
            mLongClickListener.onItemLongClick(v, getPosition());
        }
        return true;
    }
}
