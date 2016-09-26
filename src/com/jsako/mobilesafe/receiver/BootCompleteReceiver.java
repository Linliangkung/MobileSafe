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
		// �ж��û��Ƿ����˷�������
		boolean protecting = sp.getBoolean("protecting", false);
		if (!protecting) {
			// �û�û�п�����������
			Log.i(TAG, "�û���δ������������");
			return;
		}
		// ��ȡ��ǰ�ֻ�sim��������
		tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String sim = tm.getSimSerialNumber();
		// ��ȡ�ֻ��󶨵�sim�����к�
		String savesim = sp.getString("simserialnumber", "");
		// �û��Ѿ���sim����
		if (savesim.equals(sim)) {
			// ��ǰsim�����󶨵�sim��һ��
			Log.i(TAG, "sim��һ��");
		} else {
			String safenumber = sp.getString("safenumber", "");
			Log.i(TAG, "sim����һ��,���Ͷ��Ÿ���ȫ����" + safenumber);
			SmsManager manager = SmsManager.getDefault();
			manager.sendTextMessage(safenumber, null, "sim�������仯", null, null);
		}
	}

}
