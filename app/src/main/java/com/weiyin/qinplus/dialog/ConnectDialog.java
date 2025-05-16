package com.weiyin.qinplus.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.bluetooth.BlueToothControl;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.listener.InterfaceBlueConnect;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;
import com.weiyin.qinplus.usb.UsbController;
import com.weiyin.qinplus.usb.listener.UsbConnectListener;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : 选择蓝牙USB连接弹窗
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class ConnectDialog extends Dialog implements InterfaceBlueConnect, UsbConnectListener, View.OnClickListener {
    private Activity activity;
    private ImageView blueImageView, usbImageView;
    private TextView blueTextView, usbTextView;
    private BlueToothControl blueToothControl;
    private UsbController usbController;

    public ConnectDialog(@NonNull Activity context) {
        super(context);
        this.activity = context;
    }

    public ConnectDialog(@NonNull Activity context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.activity = context;
    }

    protected ConnectDialog(@NonNull Activity context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.activity = context;
    }

    @Override
    public void dismiss() {
        if (blueToothControl != null) {
            blueToothControl.removeConnectCallback(this);
            blueToothControl = null;
        }
        if (usbController != null) {
            usbController.removeUsbConnectListener(this);
            usbController = null;
        }
        super.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_dialog_layout);
        LayoutHelper layoutHelper = new LayoutHelper(activity);
        layoutHelper.scaleView(findViewById(R.id.connectDialogLayout));

        initControl();
        initView();
    }

    private void initControl() {
        blueToothControl = BlueToothControl.getBlueToothInstance();
        usbController = UsbController.getUsbController();

        blueToothControl.addConnectedCallback(this);
        usbController.addUsbConnectListener(this);
    }

    private void initView() {
        blueImageView = (ImageView) findViewById(R.id.connectBlueImageView);
        blueTextView = (TextView) findViewById(R.id.connectBlueTextView);
        usbImageView = (ImageView) findViewById(R.id.connectUsbImageView);
        usbTextView = (TextView) findViewById(R.id.connectUsbTextView);
        RelativeLayout connectBlueRl = (RelativeLayout) findViewById(R.id.connectBlueRl);

        connectBlueRl.setOnClickListener(this);

        if (blueToothControl != null) {
            if (blueToothControl.getConnectFlag()) {
                openBlue();
            } else {
                closeBlue();
            }
        }

        if (usbController != null) {
            if (usbController.ismConnected()) {
                openUsb();
                closeBlue();
            } else {
                closeUsb();
            }
        }
    }

    private void openBlue() {
        blueImageView.setImageResource(R.drawable.oper_bluetooth_open);
        blueTextView.setText(activity.getResources().getString(R.string.blueConnect));
    }

    private void closeBlue() {
        blueImageView.setImageResource(R.drawable.oper_bluetooth_close);
        blueTextView.setText(activity.getResources().getString(R.string.blueDisConnect));
    }

    private void openUsb() {
        usbImageView.setImageResource(R.drawable.usb_connect);
        usbTextView.setText(activity.getResources().getString(R.string.usbConnect));
    }

    private void closeUsb() {
        usbImageView.setImageResource(R.drawable.usb_disconnect);
        usbTextView.setText(activity.getResources().getString(R.string.usbDisConnect));
    }

    @Override
    public void result(int openClose) {
        if (openClose == 0) {
            openUsb();
            closeBlue();
        } else {
            closeUsb();
        }
    }

    @Override
    public void resultString(String result) {

    }

    @Override
    public void onConnectStatusChanged(String name, String address, boolean bConnect) {
        if (bConnect) {
            openBlue();
            closeUsb();
        } else {
            closeBlue();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.connectBlueRl) {
                if (usbController == null || !usbController.ismConnected()) {
                    BluetoothDialog bluetoothDialog = new BluetoothDialog(activity, R.style.BlueToothDialogStyle);
                    bluetoothDialog.show();
                    dismiss();
                } else {
                    ToastUtil.showTextToast(activity.getApplicationContext(), activity.getResources().getString(R.string.usbStatusConnect));
                }
        }
    }
}
