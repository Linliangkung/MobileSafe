package com.jsako.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class AutoCleanService extends Service {
	private ScreenOffReceiver receiver;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		receiver=new ScreenOffReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver=null;
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("屏幕锁屏啦!");
			//清理所有进程
//			List<TaskInfo> taskInfos=TaskInfoProvider.getTaskInfos(AutoCleanService.this);
//			
//			for(TaskInfo info:taskInfos){
//				am.killBackgroundProcesses(info.getPackageName());
//			}
			
			ActivityManager am=(ActivityManager) getSystemService(ACTIVITY_SERVICE);
			 List<RunningAppProcessInfo> processInfos=am.getRunningAppProcesses();
			 for(RunningAppProcessInfo processInfo:processInfos){
				 am.killBackgroundProcesses(processInfo.processName);
			 }
		}
		
	}
}
