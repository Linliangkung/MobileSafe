package com.jsako.mobilesafe.service;

import java.util.List;

import com.jsako.mobilesafe.EnterPSWActivity;
import com.jsako.mobilesafe.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.EditText;

public class WatchDogService extends Service {
	private boolean flag;
	private ActivityManager am;
	private AppLockDao dao;
	private String tempStopProtectPackname;
	private InnerRececiver innerReceiver;
	private OffScreenReceiver offReceiver;
	private OnScreenReceiver onReceiver;
	private DataChangeReceiver dataChangeReceiver;
	private List<String> packageNameInfos;
	private Intent intent;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		innerReceiver = new InnerRececiver();
		registerReceiver(innerReceiver, new IntentFilter(
				"com.jsako.mobilesafe.tempstop"));
		offReceiver = new OffScreenReceiver();
		registerReceiver(offReceiver,
				new IntentFilter(Intent.ACTION_SCREEN_OFF));
		onReceiver = new OnScreenReceiver();
		registerReceiver(onReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		dataChangeReceiver =new DataChangeReceiver();
		registerReceiver(dataChangeReceiver,new IntentFilter("com.jsako.mobilesafe.datachange"));
		flag = true;
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new AppLockDao(this);
		packageNameInfos = dao.findAll();
		intent = new Intent(WatchDogService.this, EnterPSWActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		new Thread() {
			public void run() {
				while (flag) {
					List<RunningTaskInfo> taskInfos = am.getRunningTasks(1);
					String packageName = taskInfos.get(0).topActivity
							.getPackageName();
					if (packageNameInfos.contains(packageName)) {
						if (packageName.equals(tempStopProtectPackname)) {
							// 说明需要临时停止保护
						} else {
							// 说明加了程序锁
							intent.putExtra("packagename", packageName);
							startActivity(intent);
						}
					}
					SystemClock.sleep(10);
				}
			}
		}.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(innerReceiver);
		innerReceiver = null;
		unregisterReceiver(offReceiver);
		offReceiver = null;
		unregisterReceiver(onReceiver);
		onReceiver = null;
		unregisterReceiver(dataChangeReceiver);
		dataChangeReceiver=null;
		flag = false;
	}

	private class InnerRececiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("接收到了临时停止保护的广播事件");
			tempStopProtectPackname = intent.getStringExtra("packagename");
		}
	}

	private class DataChangeReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("数据库内容发生了变化");
			packageNameInfos=dao.findAll();
		}
		
	}
	private class OffScreenReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("锁屏啦!!!!!");
			tempStopProtectPackname = null;
			flag = false;
		}
	}

	private class OnScreenReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("解锁啦!!!!!");
			flag = true;
			new Thread() {
				public void run() {
					while (flag) {
						List<RunningTaskInfo> taskInfos = am.getRunningTasks(1);
						String packageName = taskInfos.get(0).topActivity
								.getPackageName();
						if (packageNameInfos.contains(packageName)) {
							if (packageName.equals(tempStopProtectPackname)) {
								// 说明需要临时停止保护
							} else {
								// 说明加了程序锁
								WatchDogService.this.intent.putExtra("packagename", packageName);
								startActivity(WatchDogService.this.intent);
							}
						}
						SystemClock.sleep(10);
					}
				}
			}.start();
		}

	}
}
