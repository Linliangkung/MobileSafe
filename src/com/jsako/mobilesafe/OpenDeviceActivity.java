package com.jsako.mobilesafe;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.jsako.mobilesafe.receiver.MyDeviceAdminReceiver;

public class OpenDeviceActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ComponentName cn = new ComponentName(this, MyDeviceAdminReceiver.class);
		//����һ����ͼ�������ǿ����豸�ĳ�������Ա
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
		//Ȱ˵�û���������Ա
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				"�����Ұѡ������ҾͿ��������ˣ������ͻ���");
		startActivity(intent);
		finish();
	}
}
