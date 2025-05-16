package com.weiyin.qinplus.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.entity.MusicListItemEntity;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import java.util.List;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app  书籍相关曲谱Adapter
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class MusicDetailDialogAdapter extends RecyclerView.Adapter<MusicDetailDialogAdapter.ViewHolder> {

    private List<MusicListItemEntity> itemlist;
    private Activity context;
    private static final int FOUCS = -111;
    private int focusPosition = 0;
    private boolean autoFocusable = false;

    public interface ItemClickListener {
        public void onItemClick(View view, int postion);
    }

    public interface ItemFocusListener {
        public void onItemFcous(View view, int postion);
    }

    public interface ItemOrientationListener {
        public void onItemOrientation(View view, int postion, String strOrientation);
    }

    private ItemClickListener mItemClickListener;
    private ItemFocusListener mItemFcousListener;
    private ItemOrientationListener mItemOrientationListener;

    public MusicDetailDialogAdapter(Activity context, List<MusicListItemEntity> items, boolean autoFocusable) {
        this.itemlist = items;
        this.context = context;
        this.autoFocusable = autoFocusable;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.musicdetaildialog_item, parent, false);
        LayoutHelper layoutHelper = new LayoutHelper(context);
        layoutHelper.scaleView(v);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final MusicListItemEntity item = itemlist.get(position);
        if (item != null) {
            if (position < 9) {
                holder.musicTextName.setText("0" + (position + 1) + "." + item.getMusicName());
            } else {
                holder.musicTextName.setText((position + 1) + "." + item.getMusicName());
            }
        }

        if (holder != null) {
            holder.musicDetailDialogItemRl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        holder.musicTextName.setTextColor(context.getResources().getColor(R.color.main_text_green));
                    } else {
                        holder.musicTextName.setTextColor(context.getResources().getColor(R.color.white));
                    }

                }
            });

            holder.musicDetailDialogItemRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mItemClickListener.onItemClick(view, position);
                }
            });
        }
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOnItemFcousListener(ItemFocusListener listener) {
        this.mItemFcousListener = listener;
    }

    public void setOnItemOrientationListener(ItemOrientationListener listener) {
        this.mItemOrientationListener = listener;
    }

    @Override
    public int getItemCount() {
        return itemlist != null ? itemlist.size() : 0;
    }

    public void setAutoFocusable(boolean autoFocusable) {
        this.autoFocusable = autoFocusable;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView musicTextName;
        RelativeLayout musicDetailDialogItemRl;

        public ViewHolder(View itemView) {
            super(itemView);
            musicDetailDialogItemRl = (RelativeLayout) itemView.findViewById(R.id.musicdetial_dialog_item_rl);
            musicTextName = (TextView) itemView.findViewById(R.id.musicdetial_dialog_item_textview);

        }
    }
}
