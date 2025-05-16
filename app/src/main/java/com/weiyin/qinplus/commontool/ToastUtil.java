package com.weiyin.qinplus.commontool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2018/02/04
 *     desc   :
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class ToastUtil {
    private static Toast mToast = null;

    @SuppressLint("ShowToast")
    public static void showTextToast(Context mContext, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}
