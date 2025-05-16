package com.weiyin.qinplus.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.application.WinYinPianoApplication;
import com.weiyin.qinplus.dialog.LoadingDialog;

import java.lang.ref.PhantomReference;
import java.lang.reflect.Method;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   :Activity基类，继承Activity
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
@SuppressLint("Registered")
public class BaseActivity extends Activity {
    public static final String TAG = BaseActivity.class.getSimpleName();
    public LoadingDialog getLoadingDialog() {
        return loadingDialog;
    }

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        WinYinPianoApplication.getInstance().addActivity2Stack(this);
//		//透明状态栏
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//		//透明导航栏
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * show加载弹框
     */
    public void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this, R.style.BlueToothDialogStyle, "加载中 · · · ");
        }
        if (hasWindowFocus()) {
            if (!loadingDialog.isShowing()) {
                loadingDialog.show();
                loadingDialog.setCanceledOnTouchOutside(false);
                loadingDialog.start();
            }
        }
    }

    public void dismiss() {
        if (loadingDialog != null) {
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.i("BaseActivity", "销毁");
        WinYinPianoApplication.getInstance().removeActivityFromStack(this);
        super.onDestroy();
        if (loadingDialog != null) {
            loadingDialog.cancel();
            loadingDialog = null;
        }
    }





}
