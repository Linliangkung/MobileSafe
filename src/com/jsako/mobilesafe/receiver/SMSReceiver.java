package com.jsako.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.jsako.mobilesafe.OpenDeviceActivity;
import com.jsako.mobilesafe.R;
import com.jsako.mobilesafe.service.GPSService;
import com.jsako.mobilesafe.receiver.MyDeviceAdminReceiver;

public class SMSReceiver extends BroadcastReceiver {
	private static final String TAG = "SMSReceiver";
	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		// 先判断用户防盗保护是否开启了
		boolean protecting = sp.getBoolean("protecting", false);
		if (!protecting) {
			Log.i(TAG, "用户没有开启防盗保护功能");
			return;
		}
		// 获取用户设置的安全号码
		String safenumber = sp.getString("safenumber", "");
		// 判断安全号码是否为空,如果为空,则返回
		if (TextUtils.isEmpty(safenumber)) {
			Log.i(TAG, "安全号码为空");
			return;
		}
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for (Object obj : objs) {
			SmsMessage sm = SmsMessage.createFromPdu((byte[]) obj);
			String sender = sm.getOriginatingAddress();
			String body = sm.getMessageBody();
			// 判断发送者是否为安全号码,如果不是直接返回
			if (!sender.equals(safenumber)) {
				Log.i(TAG, "发送者不是安全号码");
				return;
			}
			if ("#*location*#".equals(body)) {
				// GPS追踪
				Log.i(TAG, "GPS追踪");
				// 将最后一次经纬度信息取出
				String lastLocation = sp.getString("lastlocation", null);
				if (TextUtils.isEmpty(lastLocation)) {
					// 如果lastLocation为空说明还没有获取到gps位置
					lastLocation = "getLocation.................";
				}
				SmsManager manager = SmsManager.getDefault();
				Log.i(TAG, "短信内容为:" + lastLocation);
				manager.sendTextMessage(safenumber, null, lastLocation, null,
						null);
				Intent service = new Intent(context, GPSService.class);
				context.startService(service);
				abortBroadcast();
			} else if ("#*alarm*#".equals(body)) {
				abortBroadcast();
				// 播放报警音乐
				Log.i(TAG, "播放报警音乐");
				MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
				player.setLooping(true);
				player.setVolume(1.0f, 1.0f);
				player.start();
			} else if ("#*wipedata*#".equals(body)
					|| "#*lockscreen*#".equals(body)) {
				DevicePolicyManager dpm = (DevicePolicyManager) context
						.getSystemService(Context.DEVICE_POLICY_SERVICE);
				ComponentName cn = new ComponentName(context,
						MyDeviceAdminReceiver.class);
				if (dpm.isAdminActive(cn)) {
					// 如果设备管理器已经激活的话
					if ("#*wipedata*#".equals(body)) {
						// 远程销毁数据
						Log.i(TAG, "远程销毁数据");
					} else {
						// 锁屏
						Log.i(TAG, "远程锁屏");
						dpm.lockNow();
					}
				} else {
					// 如果设备管理器没有激活,重新激活,需要再发一条短信才能起作用
					//不可以用FLAG_ACTIVITY_NEW_TASK这样的意图出打开一个系统的activity,例如打开激活管理器的页面就不行了
					//这时就要通过开启一个新的activity,再通过新的activity去开启激活管理器的系统界面
					Intent ii=new Intent(context,OpenDeviceActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(ii);
				}
				abortBroadcast();
			}
		}
	}

}
