package com.weiyin.qinplus.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weiyin.qinplus.R;
import com.weiyin.qinplus.bluetooth.BlueToothControl;
import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.entity.BluetoothEntity;
import com.weiyin.qinplus.listener.InterfaceBlueConnect;
import com.weiyin.qinplus.ui.tv.utils.LayoutHelper;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : 蓝牙弹窗
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class BluetoothDialog extends Dialog implements InterfaceBlueConnect, View.OnClickListener {
    private final String TAG = "BluetoothDialog";

    private BlueToothControl blueToothControl;

    private Activity mContext;
    private BluetoothAdapter mBluetoothAdapterBle;
    private LeDeviceListAdapter mLeDeviceListAdapter;

    private ArrayList<BluetoothEntity> mLeDevices;
    private int indexConnect = 0;

    private String bleName, bleAddress, blueConnectName, blueConnectAddress;

    private Animation operatingAnim;

    private ImageView oldImageView;

    private TextView bluetoothTextViewName;
    private TextView bluetoothTextViewStatus;

    private int connectIndex = 0;

    // 10秒后停止查找搜索.
    private final long SCAN_PERIOD = 10000;

    private Handler mHandler = new Handler();
    private boolean bluetoothConnect = false;

    public BluetoothDialog(Activity context) {
        super(context);
        this.mContext = context;
    }

    public BluetoothDialog(Activity context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    protected BluetoothDialog(Activity context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.bluetooth_textview_status) {
            // Handle Bluetooth connection status toggle
            if (blueToothControl.getConnectFlag()) {
                blueToothControl.disConnect();
            }
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = space;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bluetoothlistdialog_layout);
        LayoutHelper layoutHelper = new LayoutHelper(mContext);
        layoutHelper.scaleView(findViewById(R.id.bluetoothlistdialog_layout));

        blueToothControl = BlueToothControl.getBlueToothInstance();
        blueToothControl.addConnectedCallback(this);
        initData();
        initView();
        /* 检查当前手机是否支持ble bluetooth_disconnected,如果不支持退出程序 */
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            ToastUtil.showTextToast(mContext, mContext.getResources().getString(R.string.ble_not_supported));
            dismiss();
        }

        /* 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本) */
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager!=null){
            mBluetoothAdapterBle = bluetoothManager.getAdapter();
        }

        /* 检查设备上是否支持蓝牙 */
        if (mBluetoothAdapterBle == null) {
            ToastUtil.showTextToast(mContext, mContext.getResources().getString(R.string.error_bluetooth_not_supported));
            return;
        }
        if (!mBluetoothAdapterBle.isEnabled()) {
/* Constant.showTextToast(mContext,"蓝牙未打开"); */
            mBluetoothAdapterBle.enable();
        }
        /* 设置广播信息过滤 */
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);

        mContext.registerReceiver(bluesReceiver, intentFilter);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scanLeDevice(true);
            }
        }, 500);

    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mBluetoothAdapterBle != null) {
            mBluetoothAdapterBle.cancelDiscovery();
            if (blueToothControl != null) {
                blueToothControl.removeConnectCallback(this);
                if (bluesReceiver != null) {
                    mContext.unregisterReceiver(bluesReceiver);
                }
            }
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    public void initView() {
        RecyclerView listView = (RecyclerView) findViewById(R.id.bluetoothlistdialog_recyclerview);

        bluetoothTextViewName = (TextView) findViewById(R.id.bluetooth_textview_name);
        bluetoothTextViewStatus = (TextView) findViewById(R.id.bluetooth_textview_status);


        if (blueConnectName != null && blueConnectAddress != null) {
            textVisible(blueConnectName);
        } else {
            textGone();
        }

        operatingAnim = AnimationUtils.loadAnimation(mContext, R.anim.imageview_rotate);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        listView.setLayoutManager(mLayoutManager);
        listView.setHasFixedSize(true);

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        mLeDeviceListAdapter.setAutoFocusable(true);
        listView.addItemDecoration(new SpacesItemDecoration(40));

        listView.setAdapter(mLeDeviceListAdapter);


        bluetoothTextViewStatus.setOnClickListener(this);
        setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int index) {
                itemClick(view, index);
            }
        });
    }

    private void itemClick(View view, int index) {
        mHandler.removeCallbacksAndMessages(null);
        String name = "";
        String address = "";
        connectIndex = index;
        if (mLeDeviceListAdapter != null) {
            name = mLeDevices.get(index).getName();
            address = mLeDevices.get(index).getAddress();
        }
        Log.i(TAG, "点击蓝牙的名字=" + name);
        Log.i(TAG, "点击蓝牙的MAC地址=" + address);
        Log.i(TAG, "index=" + index);
        if (StringUtils.isEmpty(address) || StringUtils.isEmpty(name)) {
            return;
        }
        bleAddress = address;
        bleName = name;
        if (blueToothControl.getConnectFlag()) {
            bluetoothConnect = !address.equals(blueToothControl.getConnectAddress());
            blueToothControl.disConnect();
        } else {
            mLeDeviceListAdapter.setConnectIndex(index);
            blueToothControl.RegisterBlueReceiver(mContext.getApplicationContext());
            blueToothControl.setDeviceInfoConnect(mContext.getApplicationContext(), name, address);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!blueToothControl.getConnectFlag()) {
                        ToastUtil.showTextToast(mContext, "蓝牙未连接");
                    }
                }
            }, 8000);
        }
    }

    private void initData() {
        mLeDevices = new ArrayList<>();
        SharedPreferences sp = mContext.getSharedPreferences("config", mContext.MODE_PRIVATE);
        String strAddress = sp.getString("bluetoothdataaddress", "");
        String strName = sp.getString("bluetoothdataname", "");

        if (!StringUtils.isEmpty(strAddress) && !StringUtils.isEmpty(strName) && blueToothControl.getConnectFlag()) {
            BluetoothEntity bluetoothEntity = new BluetoothEntity();
            bluetoothEntity.setName(strName);
            bluetoothEntity.setAddress(strAddress);
            if (strAddress.equals(blueToothControl.getConnectAddress()) && strName.equals(blueToothControl.getmDeviceName()) && blueToothControl.getConnectFlag()) {
                blueConnectName = strName;
                blueConnectAddress = strAddress;
            }
            Log.i(TAG, "strAddress=" + strAddress);
            Log.i(TAG, "strName=" + strName);
        }
    }

    private void textVisible(String name) {
        bluetoothTextViewName.setText(name);
        bluetoothTextViewStatus.setText("已连接");

        bluetoothTextViewName.setVisibility(View.VISIBLE);
        bluetoothTextViewStatus.setVisibility(View.VISIBLE);
    }

    private void textGone() {
        bluetoothTextViewName.setVisibility(View.GONE);
        bluetoothTextViewStatus.setVisibility(View.GONE);
    }

    public void stop() {
        if (oldImageView != null) {
            oldImageView.clearAnimation();
            oldImageView.setImageResource(0);
        }
    }

    public void start(ImageView imageView) {
        oldImageView = imageView;
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if (operatingAnim != null) {
            imageView.startAnimation(operatingAnim);
        }
    }

    private void scanLeDevice(boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            boolean isStartDiscovery = mBluetoothAdapterBle.startDiscovery();
            Log.i(TAG, "isStart=" + isStartDiscovery);
//            mBluetoothAdapterBle.startLeScan(mLeScanCallback);
        } else {
/* mBluetoothAdapter_ble.stopLeScan(mLeScanCallback); */
            mBluetoothAdapterBle.cancelDiscovery();
//            mBluetoothAdapterBle.stopLeScan(mLeScanCallback);
        }
    }


    @Override
    public void onConnectStatusChanged(String name, String address, boolean bConnect) {
        stop();
        if (blueToothControl.getInterfaceBlueSet() != null) {
            for (int i = 0; i < blueToothControl.getInterfaceBlueSet().size(); i++) {
                blueToothControl.getInterfaceBlueSet().get(i).onBlueSet("true");
            }
        }
        if (bConnect) {
            Log.i(TAG, "connect");
            SharedPreferences sp = mContext.getSharedPreferences("config", Context.MODE_PRIVATE);
            SharedPreferences.Editor editorBle = sp.edit();
            editorBle.putString("bluetoothdataaddress", address);
            editorBle.putString("bluetoothdataname", name);
            editorBle.apply();
            editorBle.commit();
            blueConnectName = name;
            blueConnectAddress = address;
            textVisible(name);
            mLeDevices.remove(connectIndex);
            mLeDeviceListAdapter.notifyDataSetChanged();
            Log.i(TAG, name);
            bleAddress = "";
        } else {
            Log.i(TAG, "not connect");

            textGone();
            if (blueConnectAddress != null && blueConnectName != null) {
                mLeDevices.add(new BluetoothEntity(blueConnectName, blueConnectAddress));
            }
            mLeDeviceListAdapter.notifyDataSetChanged();
            if (bluetoothConnect) {
                Log.i(TAG, "进入" + " bleName=" + bleName + " bleAddress=" + bleAddress);
                if (bleName != null && bleAddress != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            blueToothControl.RegisterBlueReceiver(mContext.getApplicationContext());
                            blueToothControl.setDeviceInfoConnect(mContext.getApplicationContext(), bleName, bleAddress);
                            mLeDeviceListAdapter.setConnectIndex(indexConnect);
                            bluetoothConnect = false;
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!blueToothControl.getConnectFlag()) {
                                        ToastUtil.showTextToast(mContext, "蓝牙未连接");
                                    }
                                }
                            }, 8000);
                        }
                    }, 2000);

                }
            } else {
                bleAddress = "";
            }
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int index);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends RecyclerView.Adapter<LeDeviceListAdapter.ViewHolder> {

        private int focusPosition = 0;
        private boolean autoFocusable = false;

        private TextView textView;


        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView deviceName;
            TextView deviceStatus;
            ImageView deviceImage;
            RelativeLayout relativeLayout;

            public ViewHolder(View view) {
                super(view);
                relativeLayout = (RelativeLayout) view.findViewById(R.id.bluetoothRl);
                deviceName = (TextView) view.findViewById(R.id.bluetooth_name);
                deviceStatus = (TextView) view.findViewById(R.id.bluetooth_status);
                deviceImage = (ImageView) view.findViewById(R.id.bluetooth_imageview);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_bluetooth_recycler, null);
            ViewHolder viewHolder = new ViewHolder(view);

            LayoutHelper layoutHelper = new LayoutHelper(mContext);
            layoutHelper.scaleView(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            if (position >= 0) {
                if (viewHolder != null) {
                    final BluetoothEntity bluetoothEntity = mLeDevices.get(position);
                    viewHolder.itemView.setTag(bluetoothEntity);

                    viewHolder.deviceStatus.setText("选择连接");
                    viewHolder.deviceImage.setImageResource(0);

                    if (position == indexConnect) {
                        if (bluetoothEntity.getAddress().equals(bleAddress)) {
                            viewHolder.deviceImage.setImageResource(R.drawable.bluetooth_connect);
                            viewHolder.deviceStatus.setText("已连接");
                            start(viewHolder.deviceImage);
                            Log.i(TAG, "进入连接动画");
                        }
                    }
                    String deviceName = bluetoothEntity.getName();
                    if (deviceName != null && deviceName.length() > 0) {
                        viewHolder.deviceName.setText(deviceName);
                    }

                    viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnItemClickListener != null) {
                                mOnItemClickListener.onItemClick(viewHolder.deviceStatus, position);
                            }
                        }
                    });
                }
            }
        }

        void setAutoFocusable(boolean autoFocusable) {
            this.autoFocusable = autoFocusable;
        }

        void setConnectIndex(int i) {
            indexConnect = i;
            this.notifyItemChanged(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getItemCount() {
            return mLeDevices.size();
        }
    }

    private void addItem(BluetoothDevice device) {
        BluetoothEntity bluetoothEntity1 = new BluetoothEntity();
        bluetoothEntity1.setName(device.getName());
        bluetoothEntity1.setAddress(device.getAddress());
        mLeDevices.add(bluetoothEntity1);

        mLeDeviceListAdapter.notifyItemInserted(mLeDevices.size());
    }

    private void addDevice(BluetoothDevice device) {
        if (mLeDevices.size() == 0) {
            if (device.getName() != null && device.getAddress() != null) {
                addItem(device);
            }
        } else {
            boolean result = false;
            for (int i = 0; i < mLeDevices.size(); i++) {
                BluetoothEntity bluetoothEntity = mLeDevices.get(i);
                if (bluetoothEntity.getAddress().equals(device.getAddress())) {
                    result = true;
                    mLeDeviceListAdapter.notifyItemChanged(i);
                    break;
                }
            }
            if (!result) {
                if (device.getName() != null && device.getAddress() != null) {
                    addItem(device);
                }
            }
        }
    }

    private BroadcastReceiver bluesReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "进入a2dp蓝牙广播=" + action);
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device != null) {
                Log.i(TAG, "蓝牙名字=" + device.getName());
                Log.i(TAG, "蓝牙地址=" + device.getAddress());
                Log.i(TAG, "蓝牙状态=" + device.getBondState());
                Log.i(TAG, "蓝牙样式=" + device.getType());
                Log.i(TAG, "蓝牙类=" + device.getBluetoothClass());
                Log.i(TAG, "UUID=" + Arrays.toString(device.getUuids()));

                if (device.getType() == 2 || device.getType() == 3) {
                    addDevice(device);
                }
            }
        }
    };


}
