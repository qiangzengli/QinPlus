package com.weiyin.qinplus.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;


/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : 加载弹窗
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class LoadingDialog extends Dialog {
    private Activity mContext;
    private TextView textViewContent;
    private AnimationDrawable bastardisation;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        if (content != null) {
            textViewContent.setText(content);
        }
    }

    public LoadingDialog(Activity context) {
        super(context);
        this.mContext = context;
    }

    public LoadingDialog(Activity context, int themeResId, String strContent) {
        super(context, themeResId);
        this.mContext = context;
        if (strContent != null) {
            this.content = strContent;
        }
    }

    protected LoadingDialog(Activity context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onStop() {
        super.onStop();
        bastardisation.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadingdialog_layout);
        LayoutHelper layoutHelper = new LayoutHelper(mContext);
        layoutHelper.scaleView(findViewById(R.id.loadingdialog_layout));

        initView();
    }

    private void initView() {
        ImageView imageView = (ImageView) findViewById(R.id.loadingdialog_imageview_loading);
        textViewContent = (TextView) findViewById(R.id.loadingdialog_textiview_content);

        if (content != null) {
            textViewContent.setText(content);
        }
        imageView.setImageResource(R.drawable.imageview_rotate);
        bastardisation = new AnimationDrawable();
        bastardisation = (AnimationDrawable) imageView.getDrawable();
    }

    public void start() {
        bastardisation.start();
    }
}
