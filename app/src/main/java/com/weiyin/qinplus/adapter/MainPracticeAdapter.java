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
import com.weiyin.qinplus.entity.MainPracticeEntity;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import java.util.List;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app  音乐教室Adapter
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class MainPracticeAdapter extends RecyclerView.Adapter<MainPracticeAdapter.ViewHolder> {

    private List<MainPracticeEntity> mainPracticeEntityList;
    private Activity activity;
    RequestOptions options;

    public MainPracticeAdapter(Activity activity, List<MainPracticeEntity> mainPracticeEntities) {
        this.activity = activity;
        this.mainPracticeEntityList = mainPracticeEntities;
        options = new RequestOptions();
        options.centerCrop()
                .placeholder(R.drawable.loadingfailure)
                .error(R.drawable.loadingfault)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fallback(R.drawable.loadingfailure);
    }

    private OnItemClickInterface onItemClickInterface;

    public void setOnItemClickInterface(OnItemClickInterface onItemClickInterface) {
        this.onItemClickInterface = onItemClickInterface;
    }

    public interface OnItemClickInterface {
        void onItemClick(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_practice_fragment_item, parent, false);
        LayoutHelper layoutHelper = new LayoutHelper(activity);
        layoutHelper.scaleView(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        MainPracticeEntity mainPracticeEntity = mainPracticeEntityList.get(position);
        if (mainPracticeEntity != null) {
            Glide.with(activity).load(WinYinPianoApplication.strUrl + mainPracticeEntity.getCoverUrl()).apply(options).into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickInterface != null) {
                        onItemClickInterface.onItemClick(v, position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mainPracticeEntityList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.mainPracticeItemImageView);
        }
    }
}
