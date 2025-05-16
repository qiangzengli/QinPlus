package com.weiyin.qinplus.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.entity.MusicCollectionEntity;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import java.util.List;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app  收藏Adapter
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class MyCollectionAdapter extends BaseQuickAdapter<MusicCollectionEntity, BaseViewHolder> {
    private Activity activity;

    public MyCollectionAdapter(Activity activity, int layoutResId, @Nullable List<MusicCollectionEntity> data) {
        super(layoutResId, data);
        this.activity = activity;
    }

    private OnCollectionItemClickListener onHistoryItemClickListener;

    public void setOnHistoryItemClickListener(OnCollectionItemClickListener onHistoryItemClickListener) {
        this.onHistoryItemClickListener = onHistoryItemClickListener;
    }

    public interface OnCollectionItemClickListener {
        /**
         * 最近弹奏Item点击
         *
         * @param baseViewHolder viewHolder
         * @param position       下标
         */
        void collectionItemClick(BaseViewHolder baseViewHolder, int position);
    }

    @Override
    protected BaseViewHolder createBaseViewHolder(View view) {
        LayoutHelper layoutHelper = new LayoutHelper(activity);
        layoutHelper.scaleView(view);
        return super.createBaseViewHolder(view);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MusicCollectionEntity item) {
        if (item != null) {
            Glide.with(activity).
                    load(WinYinPianoApplication.strUrl + item.getCoverUrl()).
                    into((ImageView) helper.getView(R.id.mycollection_item_imageview));
            LogUtil.i(TAG, WinYinPianoApplication.strUrl + item.getCoverUrl());
            String musicName = item.getMusicName();
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
            helper.setText(R.id.mycollection_item_textview, musicName);

            helper.getView(R.id.mycollection_item_rl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onHistoryItemClickListener != null) {
                        onHistoryItemClickListener.collectionItemClick(helper, helper.getAdapterPosition());
                    }
                }
            });
        }
    }
}
