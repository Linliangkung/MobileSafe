package com.jsako.mobilesafe.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KillAllReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("���յ��㲥��Ϣ,�������");
		ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		 List<RunningAppProcessInfo> processInfos=am.getRunningAppProcesses();
		 for(RunningAppProcessInfo processInfo:processInfos){
			 am.killBackgroundProcesses(processInfo.processName);
		 }
	}

}
