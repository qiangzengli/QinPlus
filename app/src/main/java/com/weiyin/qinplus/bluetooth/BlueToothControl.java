package com.weiyin.qinplus.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.weiyin.qinplus.commontool.ToastUtil;
import com.weiyin.qinplus.listener.InterfaceBlueConnect;
import com.weiyin.qinplus.listener.InterfaceBlueData;
import com.weiyin.qinplus.listener.InterfaceBlueSet;
import com.weiyin.qinplus.listener.InterfaceLogin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 蓝牙连接收发数据控制类  单例实现
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class BlueToothControl {
    public static final String OPEN_A2DP = "8080F0451055F7";
    public static final String CLOSE_A2DP = "8080F0451155F7";
    public static final String SELECT_A2DP = "8080F0451255F7";

    public static final String SELECT_BLUE_BANBEN = "8080F0450D55F7";
    public static final String SELECT_BLUE_MOSHI = "8080F0450655F7";
    public static final String CHONGQI_BLUE_BOOT = "8080F0450555F7";
    public static final String CHONGQI_BLUE_APP = "8080F0450455F7";

    public static final String SELECT_ZHUBAN_MOSHI = "8080F0455D55F7";
    public static final String CHONGQI_ZHUBAN_BOOT = "8080F0455655F7";
    public static final String CHONGQI_ZHUBAN_APP = "8080F0455E55F7";

    public static final String OPEN_MUSIC = "8080F0455055F7";
    public static final String CLOSE_MUSIC = "8080F0455155F7";


    private final static String TAG = BlueToothControl.class.getSimpleName();
    private static BlueToothControl instance = null;

    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic;
    private Context context;

    private boolean bFlagRegistered = false;
    public String senData = "";

    private InterfaceLogin interfaceLogin;

    /**
     * 连接状态回调
     */
    private ArrayList<InterfaceBlueConnect> interfacesConnect = new ArrayList<InterfaceBlueConnect>();
    /**
     * 数据回调
     */
    private ArrayList<InterfaceBlueData> interfacedata = new ArrayList<>();

    public void setInterfaceBlueSet(InterfaceBlueSet interfaceBlueSet) {
        if (interfaceBlueSets != null) {
            if (!interfaceBlueSets.contains(interfaceBlueSet)) {
                interfaceBlueSets.add(interfaceBlueSet);
            }
        }
    }

    public void setInterFaceLogin(InterfaceLogin interfaceLogin) {
        this.interfaceLogin = interfaceLogin;
    }

    public InterfaceLogin getInterfaceLogin() {
        return interfaceLogin;
    }

    public ArrayList<InterfaceBlueSet> getInterfaceBlueSet() {
        return interfaceBlueSets;
    }

    private ArrayList<InterfaceBlueSet> interfaceBlueSets = new ArrayList<>();

    public static BlueToothControl getBlueToothInstance() {
        if (instance == null) {
            instance = new BlueToothControl();
        }

        return instance;
    }

    private BlueToothControl() {

    }

    public String getmDeviceName() {
        return mDeviceName;
    }

    public void setmDeviceName(String mDeviceName) {
        this.mDeviceName = mDeviceName;
    }

    public void RegisterBlueReceiver(Context context) {
        try {
            if (bFlagRegistered) {
                context.unbindService(mServiceConnection);
                context.unregisterReceiver(mGattUpdateReceiver);
                bFlagRegistered = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setDeviceInfoConnect(Context context, String deviceName, String deviceAddress) {

        this.context = context;
        this.mDeviceAddress = deviceAddress;
        this.mDeviceName = deviceName;
        if (!bFlagRegistered) {
            context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            bFlagRegistered = true;
        }
        Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
        this.context.bindService(gattServiceIntent, mServiceConnection, context.BIND_AUTO_CREATE);
    }

    public void addConnectedCallback(InterfaceBlueConnect callback) {
        if (interfacesConnect != null) {
            if (!interfacesConnect.contains(callback)) {
                interfacesConnect.add(callback);
            }
        }
    }

    public void removeConnectCallback(InterfaceBlueConnect callback) {
        if (interfacesConnect != null) {
            if (interfacesConnect.contains(callback)) {
                interfacesConnect.remove(callback);
            }
        }
    }

    public void addDataCallback(InterfaceBlueData dataCallBack) {
        if (interfacedata != null) {
            if (!interfacedata.contains(dataCallBack)) {
                interfacedata.add(dataCallBack);
            }
        }
    }

    public void removeDataCallback(InterfaceBlueData dataCallBack) {
        if (interfacedata != null) {
            if (interfacedata.contains(dataCallBack)) {
                interfacedata.remove(dataCallBack);
            }
        }
    }

    public String getConnectAddress() {
        return mDeviceAddress;
    }

    public boolean getConnectFlag() {
        return mConnected;
    }


    public void disConnect() {
        mBluetoothLeService.disconnect();
        Log.i(TAG, "Bluetooth.status===" + getConnectFlag());
    }

    public void destroy() {
        Log.i(TAG, "bluetooth.destroy()");
        mBluetoothLeService.disconnect();
        try {
            this.context.unbindService(mServiceConnection);
            this.context.unregisterReceiver(mGattUpdateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean sendData(String str) {

        str = str.toUpperCase();
        byte[] byteArray = hexStringToByte(str);
        if (str.contains("F7")) {
            senData = str;
            int f = senData.indexOf("F7");
            senData = senData.substring(6, f);
            Log.i(TAG, "发下去的数据长度====" + byteArray.length);
        }
        if (mWriteCharacteristic != null && mBluetoothLeService.mConnectionState == BluetoothLeService.STATE_CONNECTED) {
            mWriteCharacteristic.setValue(byteArray);

            return mBluetoothLeService.writeCharacteristic(mWriteCharacteristic);
        }
        return false;
    }

    public void sendDataUpgrade(String str) {
        byte[] byteArray = hexStringToBytes(str);
        mWriteCharacteristic.setValue(byteArray);
        mBluetoothLeService.writeCharacteristic(mWriteCharacteristic);
    }

    /**
     * Code to manage Service lifecycle.
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    /**
     * Handles various events fired by the Service.
     * ACTION_GATT_CONNECTED: connected to a GATT server.
     * ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
     * ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
     * ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
     * or notification operations.
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.i(TAG, "进入Ble蓝牙广播=" + action);

                if (instance.getInterfaceBlueSet() != null) {
                    for (int i = 0; i < instance.getInterfaceBlueSet().size(); i++) {
                        instance.getInterfaceBlueSet().get(i).onBlueSet("true");
                    }
                }
                mConnected = true;
                for (InterfaceBlueConnect callback : interfacesConnect) {
                    callback.onConnectStatusChanged(mDeviceName, mDeviceAddress, mConnected);
                }
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Log.i(TAG, "进入Ble蓝牙广播=" + action);
                ToastUtil.showTextToast(context, mDeviceName + "断开连接");

                if (instance.getInterfaceBlueSet() != null) {
                    for (int i = 0; i < instance.getInterfaceBlueSet().size(); i++) {
                        instance.getInterfaceBlueSet().get(i).onBlueSet("true");
                    }
                }
                for (InterfaceBlueConnect callback : interfacesConnect) {
                    callback.onConnectStatusChanged(mDeviceName, mDeviceAddress, mConnected);
                }
                if (bFlagRegistered) {
                    context.unbindService(mServiceConnection);
                    context.unregisterReceiver(mGattUpdateReceiver);
                    bFlagRegistered = false;
                }
                mDeviceAddress = null;
                mDeviceName = null;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                /* Show all the supported services and characteristics on the user interface. */
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
/*displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));//// TODO: 2016/7/18  获取蓝牙数据，处理*/
                String value = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if (value != null) {
                    if (interfacedata != null) {
                        if (interfacedata.size() > 0) {
                            interfacedata.get(interfacedata.size() - 1).onDataReceive(value);
                        }
                    }
                }
            }
        }
    };

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        Log.i(TAG, gattServices.size() + "大小");
        String uuid = null;
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();
            Log.i(TAG, gattCharacteristics.size() + "大小");
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                final int charaProp = gattCharacteristic.getProperties();
                gattCharacteristicGroupData.add(currentCharaData);
                if (BluetoothLeService.UUID_CLIENT_CHARACTERISTIC_CONFIG.equals(gattCharacteristic.getUuid())) {
                    mWriteCharacteristic = gattCharacteristic;
                    mNotifyCharacteristic = gattCharacteristic;
                    mBluetoothLeService.setCharacteristicNotification(
                            gattCharacteristic, true);
                }
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
    }


    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] aChar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(aChar[pos]) << 4 | toByte(aChar[pos + 1]));
        }
        return result;
    }

    public static byte[] hexStringToBytes(String hex) {
        return asciiStringToString(hex);
    }

    /**
     * 十进制转换为十六进制字符串
     *
     * @param algorithm int 十进制的数字
     * @return String 对应的十六进制字符串
     */
    public static String algorithmToHEXString(int algorithm) {
        String result = "";
        result = Integer.toHexString(algorithm);

        if (result.length() % 2 == 1) {
            result = "0" + result;

        }
        result = result.toUpperCase();

        return result;
    }

    /**
     * 十六进制字符串转为Byte数组,每两个十六进制字符转为一个Byte
     *
     * @param hex 十六进制字符串
     * @return byte 转换结果
     */
    public static byte[] hexStringToBytess(String hex) {
        int max = hex.length() / 2;
        byte[] bytes = new byte[max];
        String binary = hexStringToBinary(hex);
        for (int i = 0; i < max; i++) {
            bytes[i] = (byte) binaryToAlgorithm(binary.substring(
                    i * 8 + 1, (i + 1) * 8));
            if (binary.charAt(8 * i) == '1') {
                bytes[i] = (byte) (0 - bytes[i]);
            }
        }
        return bytes;
    }

    /**
     * 二进制字符串转十进制
     *
     * @param binary 二进制字符串
     * @return 十进制数值
     */
    public static int binaryToAlgorithm(String binary) {
        int max = binary.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = binary.charAt(i - 1);
            int algorithm = c - '0';
            result += Math.pow(2, max - i) * algorithm;
        }
        return result;
    }

    /**
     * 十六转二进制
     *
     * @param hex 十六进制字符串
     * @return 二进制字符串
     */
    public static String hexStringToBinary(String hex) {
        hex = hex.toUpperCase();
        StringBuilder result = new StringBuilder();
        int max = hex.length();
        for (int i = 0; i < max; i++) {
            char c = hex.charAt(i);
            switch (c) {
                case '0':
                    result.append("0000");
                    break;
                case '1':
                    result.append("0001");
                    break;
                case '2':
                    result.append("0010");
                    break;
                case '3':
                    result.append("0011");
                    break;
                case '4':
                    result.append("0100");
                    break;
                case '5':
                    result.append("0101");
                    break;
                case '6':
                    result.append("0110");
                    break;
                case '7':
                    result.append("0111");
                    break;
                case '8':
                    result.append("1000");
                    break;
                case '9':
                    result.append("1001");
                    break;
                case 'A':
                    result.append("1010");
                    break;
                case 'B':
                    result.append("1011");
                    break;
                case 'C':
                    result.append("1100");
                    break;
                case 'D':
                    result.append("1101");
                    break;
                case 'E':
                    result.append("1110");
                    break;
                case 'F':
                    result.append("1111");
                    break;
                default:

                    break;
            }
        }
        return result.toString();
    }

    /**
     * ASCII码字符串转数字字符串
     *
     * @return 字符串
     * @paramString ASCII字符串
     */
    public static byte[] asciiStringToString(String content) {
        int length = content.length() / 2;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            String c = content.substring(i * 2, i * 2 + 2);
            int a = hexStringToAlgorithm(c);
            bytes[i] = (byte) a;
        }
        return bytes;
    }

    /**
     * 十六进制字符串装十进制
     *
     * @param hex 十六进制字符串
     * @return 十进制数值
     */
    public static int hexStringToAlgorithm(String hex) {
        hex = hex.toUpperCase();
        int max = hex.length();
        int result = 0;
        for (int i = max; i > 0; i--) {
            char c = hex.charAt(i - 1);
            int algorithm;
            if (c >= '0' && c <= '9') {
                algorithm = c - '0';
            } else {
                algorithm = c - 55;
            }
            result += Math.pow(16, max - i) * algorithm;
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
