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
		// ���ж��û����������Ƿ�����
		boolean protecting = sp.getBoolean("protecting", false);
		if (!protecting) {
			Log.i(TAG, "�û�û�п���������������");
			return;
		}
		// ��ȡ�û����õİ�ȫ����
		String safenumber = sp.getString("safenumber", "");
		// �жϰ�ȫ�����Ƿ�Ϊ��,���Ϊ��,�򷵻�
		if (TextUtils.isEmpty(safenumber)) {
			Log.i(TAG, "��ȫ����Ϊ��");
			return;
		}
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for (Object obj : objs) {
			SmsMessage sm = SmsMessage.createFromPdu((byte[]) obj);
			String sender = sm.getOriginatingAddress();
			String body = sm.getMessageBody();
			// �жϷ������Ƿ�Ϊ��ȫ����,�������ֱ�ӷ���
			if (!sender.equals(safenumber)) {
				Log.i(TAG, "�����߲��ǰ�ȫ����");
				return;
			}
			if ("#*location*#".equals(body)) {
				// GPS׷��
				Log.i(TAG, "GPS׷��");
				// �����һ�ξ�γ����Ϣȡ��
				String lastLocation = sp.getString("lastlocation", null);
				if (TextUtils.isEmpty(lastLocation)) {
					// ���lastLocationΪ��˵����û�л�ȡ��gpsλ��
					lastLocation = "getLocation.................";
				}
				SmsManager manager = SmsManager.getDefault();
				Log.i(TAG, "��������Ϊ:" + lastLocation);
				manager.sendTextMessage(safenumber, null, lastLocation, null,
						null);
				Intent service = new Intent(context, GPSService.class);
				context.startService(service);
				abortBroadcast();
			} else if ("#*alarm*#".equals(body)) {
				abortBroadcast();
				// ���ű�������
				Log.i(TAG, "���ű�������");
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
					// ����豸�������Ѿ�����Ļ�
					if ("#*wipedata*#".equals(body)) {
						// Զ����������
						Log.i(TAG, "Զ����������");
					} else {
						// ����
						Log.i(TAG, "Զ������");
						dpm.lockNow();
					}
				} else {
					// ����豸������û�м���,���¼���,��Ҫ�ٷ�һ�����Ų���������
					//��������FLAG_ACTIVITY_NEW_TASK��������ͼ����һ��ϵͳ��activity,����򿪼����������ҳ��Ͳ�����
					//��ʱ��Ҫͨ������һ���µ�activity,��ͨ���µ�activityȥ���������������ϵͳ����
					Intent ii=new Intent(context,OpenDeviceActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(ii);
				}
				abortBroadcast();
			}
		}
	}

}
