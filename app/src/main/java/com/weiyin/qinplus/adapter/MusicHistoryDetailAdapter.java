package com.weiyin.qinplus.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.weiyin.qinplus.R;
import com.weiyin.qinplus.commontool.LogUtil;
import com.weiyin.qinplus.entity.MusicHistoryDetailEntity;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import java.util.List;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2018/02/06
 *     desc   :
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class MusicHistoryDetailAdapter extends BaseQuickAdapter<MusicHistoryDetailEntity, BaseViewHolder> {
    private Activity activity;

    public MusicHistoryDetailAdapter(Activity activity, int layoutResId, @Nullable List<MusicHistoryDetailEntity> data) {
        super(layoutResId, data);
        this.activity = activity;
    }

    @Override
    protected BaseViewHolder createBaseViewHolder(View view) {
        LayoutHelper layoutHelper = new LayoutHelper(activity);
        layoutHelper.scaleView(view);
        return super.createBaseViewHolder(view);
    }

    private OnHistoryDetailItemClickListener onHistoryItemClickListener;

    public void setOnHistoryDetailItemClickListener(OnHistoryDetailItemClickListener onHistoryItemClickListener) {
        this.onHistoryItemClickListener = onHistoryItemClickListener;
    }

    public interface OnHistoryDetailItemClickListener {
        /**
         * 最近弹奏Item点击
         *
         * @param baseViewHolder viewHolder
         * @param position       下标
         */
        void historyDetailItemClick(BaseViewHolder baseViewHolder, int position);
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicHistoryDetailEntity item) {
        if (item != null) {
            if (helper.getAdapterPosition() % 2 == 0) {
                helper.getView(R.id.musicHistoryDetailItemLayout).setSelected(true);
            } else {
                helper.getView(R.id.musicHistoryDetailItemLayout).setSelected(false);
            }
            helper.setText(R.id.historyItemScore, item.getScore());
            helper.setText(R.id.historyItemIntensity, item.getIntensity());
            helper.setText(R.id.historyItemIntonation, item.getIntonation());
            LogUtil.i(TAG, "item.getPlayLong()=" + item.getPlayLong());
            if (item.getPlayLong() != null) {
                Double playDouble = Double.parseDouble(item.getPlayLong());
                int playLong = playDouble.intValue();

                helper.setText(R.id.historyItemPlayLong, (playLong / 60) + "min" + (playLong % 60) + "s");
            }
            helper.setText(R.id.historyItemRhythm, item.getRhythm());
            helper.setText(R.id.historyItemStartTime, "开始时间：" + item.getStartTime());
            helper.setText(R.id.historyItemStartDate, item.getStartDate());
        }
    }
}
