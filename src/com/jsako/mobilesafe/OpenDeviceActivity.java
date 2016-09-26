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
		//声明一个意图，作用是开启设备的超级管理员
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
		//劝说用户开启管理员
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				"开启我把。开启我就可以锁屏了，开启送积分");
		startActivity(intent);
		finish();
	}
}
