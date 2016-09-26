package com.jsako.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {
	private ServiceUtils(){}
	public static boolean isRunningService(Context context,String serviceName){
		ActivityManager manager=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list=manager.getRunningServices(100);
		if(list==null){
			System.out.println("list==null");
			return false;
		}
		for(RunningServiceInfo info:list){
			String name=info.service.getClassName();
			if(serviceName.equals(name)){
				return true;
			}
		}
		return false;
	}
}
