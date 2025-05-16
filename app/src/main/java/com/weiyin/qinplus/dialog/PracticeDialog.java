package com.weiyin.qinplus.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : 音乐教室提交答案弹窗
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class PracticeDialog extends Dialog implements View.OnClickListener {
    private Activity mContext;
    private ImageView imageView;
    private TextView practiceDialogLayoutTextView1, practiceDialogLayoutTextView2;
    private String content;

    private PracticeInterface practiceInterface;

    public String getContent() {
        return content;
    }

    public PracticeInterface getPracticeInterface() {
        return practiceInterface;
    }

    public void setPracticeInterface(PracticeInterface practiceInterface) {
        this.practiceInterface = practiceInterface;
    }

    public void setContent(String type, String content) {
        this.content = content;
        if (content != null) {
            if (type.equals("yes")) {
                imageView.setImageResource(R.drawable.practice_yes);
            } else {
                imageView.setImageResource(R.drawable.practice_no);
            }
            practiceDialogLayoutTextView1.setText("重做这一题");
            practiceDialogLayoutTextView2.setText(content);
        }
    }

    public PracticeDialog(Activity context) {
        super(context);
        this.mContext = context;
    }

    public PracticeDialog(Activity context, int themeResId, String strContent) {
        super(context, themeResId);
        this.mContext = context;
        if (strContent != null) {
            this.content = strContent;
        }
    }

    protected PracticeDialog(Activity context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practice_dialog_layout);
        LayoutHelper layoutHelper = new LayoutHelper(mContext);
        layoutHelper.scaleView(findViewById(R.id.practiceDialogLayout));

        initView();
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.practiceDialogLayoutImageView);
        practiceDialogLayoutTextView1 = (TextView) findViewById(R.id.practiceDialogLayoutTextView1);
        practiceDialogLayoutTextView2 = (TextView) findViewById(R.id.practiceDialogLayoutTextView2);

        practiceDialogLayoutTextView1.setOnClickListener(this);
        practiceDialogLayoutTextView2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.practiceDialogLayoutTextView1) {
            practiceInterface.result("1");
            dismiss();
        } else if (id == R.id.practiceDialogLayoutTextView2) {
            dismiss();
            practiceInterface.result("2");
        }

    }

    public interface PracticeInterface {
        void result(String type);
    }
}