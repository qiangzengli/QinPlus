package com.weiyin.qinplus.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.entity.MusicAboutEntity;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app  曲谱相关Adapter
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class MusicRelaterAdapter extends RecyclerView.Adapter<MusicRelaterAdapter.ViewHolder> {

    private List<MusicAboutEntity> musicListItemEntities;
    private Activity mContext;
    private RequestOptions options;

    public MusicRelaterAdapter(Activity context, ArrayList<MusicAboutEntity> musicListItemEntities) {
        this.mContext = context;
        this.musicListItemEntities = musicListItemEntities;

        options = new RequestOptions();
        options.centerCrop()
                .placeholder(R.drawable.loadingfailure)
                .error(R.drawable.loadingfault)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fallback(R.drawable.loadingfailure);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mycollection_item_layout, parent, false);
        LayoutHelper layoutHelper = new LayoutHelper(mContext);
        layoutHelper.scaleView(v);
        return new MusicRelaterAdapter.ViewHolder(v);
    }

    public interface ItemClickListener {
        public void onItemClick(View view, int postion);
    }

    public interface ItemFcousListener {
        public void onItemFcous(View view, int postion);
    }

    private ItemClickListener mItemClickListener;

    public void setOnItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void searchUpdateList(List<MusicAboutEntity> list) {
        this.musicListItemEntities = list;
        Log.i("adapter", "list.size=" + list.size() + " labels.size=" + musicListItemEntities.size());
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(MusicRelaterAdapter.ViewHolder holder, final int position) {
        if (holder != null) {
            MusicAboutEntity musicListItemEntity = musicListItemEntities.get(position);
            if (musicListItemEntity != null) {
                Glide.with(mContext).load(WinYinPianoApplication.strUrl + musicListItemEntity.getMusicPicUrl()).apply(options).into(holder.imageView);
                String musicName = musicListItemEntity.getMusicName();
                if (musicName.contains("（")) {
                    musicName = musicName.replace("（", "|");
                }
                if (musicName.contains("）")) {
                    musicName = musicName.replace("）", "");
                }
                if (musicName.contains("(")) {
                    musicName = musicName.replace("(", "|");
                }
                if (musicName.contains(")")) {
                    musicName = musicName.replace(")", "");
                }
                holder.textView.setText(musicName);

                holder.myCollectionItemRl.setOnClickListener(new View.OnClickListener() {
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
        return musicListItemEntities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;
        RelativeLayout myCollectionItemRl;

        public ViewHolder(View itemView) {
            super(itemView);
            myCollectionItemRl = (RelativeLayout) itemView.findViewById(R.id.mycollection_item_rl);
            imageView = (ImageView) itemView.findViewById(R.id.mycollection_item_imageview);
            textView = (TextView) itemView.findViewById(R.id.mycollection_item_textview);
        }
    }
}
