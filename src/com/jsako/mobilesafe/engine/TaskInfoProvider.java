package com.jsako.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.jsako.mobilesafe.R;
import com.jsako.mobilesafe.domain.TaskInfo;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

/**
 * 进程信息业务类,提供当前系统正在运行的进程信息
 * @author Administrator
 *
 */
public class TaskInfoProvider {
	/**
	 * 返回系统正在运行的进程信息
	 * @param context
	 * @return
	 */
	public static List<TaskInfo> getTaskInfos(Context context){
		ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm=context.getPackageManager();
		List<RunningAppProcessInfo> processInfos=am.getRunningAppProcesses();
		TaskInfo info;
		List<TaskInfo> list=new ArrayList<TaskInfo>();
		for(RunningAppProcessInfo processInfo:processInfos){
			info=new TaskInfo();
			String packageName=processInfo.processName;
			info.setPackageName(packageName);
			MemoryInfo[] memoryInfo=am.getProcessMemoryInfo(new int[]{processInfo.pid});
			long memsize=memoryInfo[0].getTotalPrivateDirty()*1024;
			info.setMemsize(memsize);
			try {
				ApplicationInfo applicationInfo=pm.getApplicationInfo(packageName, 0);
				String name=applicationInfo.loadLabel(pm).toString();
				Drawable icon=applicationInfo.loadIcon(pm);
				info.setName(name);
				info.setIcon(icon);
				if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==0){
					//用户应用
					info.setUserTask(true);
				}else{
					//系统应用
					info.setUserTask(false);
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				info.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
				info.setName(packageName);
			}
			list.add(info);
	}
		return list;
	}
}
