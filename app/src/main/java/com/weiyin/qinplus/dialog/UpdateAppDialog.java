package com.weiyin.qinplus.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.TextView;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.activity.AboutAppActivity;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : APP更新弹窗
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class UpdateAppDialog extends Dialog {
    private Activity activity;
    private String textContent;

    public UpdateAppDialog(@NonNull Activity context) {
        super(context);
    }

    public UpdateAppDialog(@NonNull Activity context, @StyleRes int themeResId, String content) {
        super(context, themeResId);
        this.activity = context;
        this.textContent = content;
    }

    protected UpdateAppDialog(@NonNull Activity context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog_layout);
        LayoutHelper layoutHelper = new LayoutHelper(activity);
        layoutHelper.scaleView(findViewById(R.id.login_dialog_rl));
        initView();
    }

    private void initView() {
        TextView textViewCancel = (TextView) findViewById(R.id.loginDialogCancel);
        TextView textViewContent = (TextView) findViewById(R.id.loginDialogContent);
        TextView textViewOk = (TextView) findViewById(R.id.loginDialogOk);
        if (textContent != null) {
            textViewContent.setText(textContent);
        }
        textViewOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                activity.startActivity(new Intent(activity, AboutAppActivity.class));
            }
        });
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
