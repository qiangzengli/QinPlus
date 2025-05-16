package com.weiyin.qinplus.ui.tv.utils;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 *
 * @author hailongqiu 356752238@qq.com
 *
 */
public class Utils {

	/**
	 * 获取SDK版本
	 */
	public static int getSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
		}
		return version;
	}

	public static String getMyUUID(Activity activity) {
//		final TelephonyManager tm = (TelephonyManager) activity.getBaseContext().getSystemService(TELEPHONY_SERVICE);
//		final String tmDevice, tmSerial, tmPhone ,androidId;
//		try {
//			tmDevice = "" + tm.getDeviceId();
//			tmSerial=""+tm.getSimSerialNumber();
//			androidId = "" + android.provider.Settings.Secure.getString(activity.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
//			UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32 ) | tmSerial.hashCode());
//			String uniqueId = deviceUuid.toString();
//			Log.i("debug","uuid="+uniqueId);
//			return uniqueId;
//		}catch (Exception e)
//		{
//			e.printStackTrace();
//		}
		String serial = null;

		String m_szDevIDShort = "17" +
				Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

				Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

				Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

				Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

				Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

				Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

				Build.USER.length() % 10; //13 位

		try {
			serial = android.os.Build.class.getField("SERIAL").get(null).toString();
			//API>=9 使用serial号
			return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
		} catch (Exception exception) {
			//serial需要一个初始化
			serial = "weiyin"; // 随便一个初始化
		}
		//使用硬件信息拼凑出来的15位号码
		return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
	}

	/**
	 * Pseudo-Unique ID, 这个在任何Android手机中都有效
	 * 有一些特殊的情况，一些如平板电脑的设置没有通话功能，或者你不愿加入READ_PHONE_STATE许可。而你仍然想获得唯
	 * 一序列号之类的东西。这时你可以通过取出ROM版本、制造商、CPU型号、以及其他硬件信息来实现这一点。这样计算出
	 * 来的ID不是唯一的（因为如果两个手机应用了同样的硬件以及Rom 镜像）。但应当明白的是，出现类似情况的可能性基
	 * 本可以忽略。大多数的Build成员都是字符串形式的，我们只取他们的长度信息。我们取到13个数字，并在前面加上“35
	 * ”。这样这个ID看起来就和15位IMEI一样了。
	 *
	 * @return PesudoUniqueID
	 */
	public static String getPesudoUniqueID() {
		String m_szDevIDShort = "35" + //we make this look like a valid IMEI
				Build.BOARD.length() % 10 +
				Build.BRAND.length() % 10 +
				Build.CPU_ABI.length() % 10 +
				Build.DEVICE.length() % 10 +
				Build.DISPLAY.length() % 10 +
				Build.HOST.length() % 10 +
				Build.ID.length() % 10 +
				Build.MANUFACTURER.length() % 10 +
				Build.MODEL.length() % 10 +
				Build.PRODUCT.length() % 10 +
				Build.TAGS.length() % 10 +
				Build.TYPE.length() % 10 +
				Build.USER.length() % 10; //13 digits
		return m_szDevIDShort;
	}
}
