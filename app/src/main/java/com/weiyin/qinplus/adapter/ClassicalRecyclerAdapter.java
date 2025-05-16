package com.weiyin.qinplus.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.entity.MusicBookEntity;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import java.util.List;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app  经典曲谱Adapter
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class ClassicalRecyclerAdapter extends RecyclerView.Adapter<ClassicalRecyclerAdapter.ViewHolder> {
    public final String TAG = "ClassicalRecyclerAdapter";
    private List<MusicBookEntity> itemList;
    private Activity context;
    private static final int FOUCS = -111;
    private int focusPosition = 0;
    private boolean autoFocusable = false;
    private ViewHolder mOldView;
    RequestOptions options;

    public interface ItemClickListener {
        public void onItemClick(View view, int position);
    }

    public interface ItemFocusListener {
        public void onItemFcous(View view, int position);
    }

    public interface ItemOrientationListener {
        public void onItemOrientation(View view, int position, String strOrientation);
    }


    private ItemClickListener mItemClickListener;
    private ItemFocusListener mItemFcousListener;
    private ItemOrientationListener mItemOrientationListener;

    public ClassicalRecyclerAdapter(Activity context, List<MusicBookEntity> items, boolean autoFocusable) {
        this.itemList = items;
        this.context = context;
        this.autoFocusable = autoFocusable;
        options = new RequestOptions();
        options.centerCrop()
                .placeholder(R.drawable.loadingfailure)
                .error(R.drawable.loadingfault)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fallback(R.drawable.loadingfailure);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_classicalmusic_item, parent, false);
        LayoutHelper layoutHelper = new LayoutHelper(context);
        layoutHelper.scaleView(v);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        MusicBookEntity item = itemList.get(position);
        if (item != null) {
            Glide.with(context).load(WinYinPianoApplication.strUrl + item.getMusicPicUrl()).apply(options).into(holder.musicImage);
        }

        if (holder != null) {
            holder.musicImage.setOnClickListener(new View.OnClickListener() {
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

    public void setOnItemFocusListener(ItemFocusListener listener) {
        this.mItemFcousListener = listener;
    }

    public void setOnItemOrientationListener(ItemOrientationListener listener) {
        this.mItemOrientationListener = listener;
    }


    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public void setAutoFocusable(boolean autoFocusable) {
        this.autoFocusable = autoFocusable;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView musicImage;

        public ViewHolder(View itemView) {
            super(itemView);

            musicImage = (ImageView) itemView.findViewById(R.id.item_classicalmusic_imageview);

        }
    }
}
