package com.weiyin.qinplus.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : 曲谱相关弹窗
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class MusicDetailAbstractDialog extends Dialog {

    private TextView authorText;
    private TextView musicDetailText;

    private Activity context;

    public MusicDetailAbstractDialog(Activity context) {
        super(context);
        this.context = context;
    }

    public MusicDetailAbstractDialog(Activity context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected MusicDetailAbstractDialog(Activity context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    public void update(String author, String musicDetail) {
        if (author != null) {
            if (authorText != null) {
                authorText.setText(author);
            }
        }
        if (musicDetailText != null) {
            if (musicDetail != null) {
                musicDetailText.setText(musicDetail);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_detailabstract);

        LayoutHelper layoutHelper = new LayoutHelper(context);
        layoutHelper.scaleView(findViewById(R.id.activity_music_detailabstract));

        initView();
    }

    private void initView() {
        authorText = (TextView) findViewById(R.id.activity_musicdetailabstract_author);
        musicDetailText = (TextView) findViewById(R.id.activity_musicdetailabstract_musicintroduce);
    }
}
