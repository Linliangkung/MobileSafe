package com.jsako.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {
	private static final String TAG = "BootCompleteReceiver";
	private TelephonyManager tm;
	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		// 判断用户是否开启了防盗保护
		boolean protecting = sp.getBoolean("protecting", false);
		if (!protecting) {
			// 用户没有开启防盗保护
			Log.i(TAG, "用户尚未开启防盗保护");
			return;
		}
		// 获取当前手机sim卡序列码
		tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String sim = tm.getSimSerialNumber();
		// 获取手机绑定的sim卡序列号
		String savesim = sp.getString("simserialnumber", "");
		// 用户已经绑定sim卡了
		if (savesim.equals(sim)) {
			// 当前sim卡跟绑定的sim卡一致
			Log.i(TAG, "sim卡一致");
		} else {
			String safenumber = sp.getString("safenumber", "");
			Log.i(TAG, "sim卡不一致,发送短信给安全号码" + safenumber);
			SmsManager manager = SmsManager.getDefault();
			manager.sendTextMessage(safenumber, null, "sim卡发生变化", null, null);
		}
	}

}
