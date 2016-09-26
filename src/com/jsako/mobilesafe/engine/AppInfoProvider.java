package com.jsako.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.jsako.mobilesafe.domain.AppInfo;

/**
 * 业务类,提供手机里安装的所有应用程序信息
 * 
 * @author Administrator
 * 
 */
public class AppInfoProvider {
	/**
	 * 返回按app信息
	 * @param context
	 * @return 返回app信息的集合
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		AppInfo info;
		for (PackageInfo packageInfo : packageInfos) {
			// packageInfo相当于清单文件.xml
			info = new AppInfo();
			String packageName = packageInfo.packageName;
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
			String name = (String) packageInfo.applicationInfo.loadLabel(pm);
			int flag=packageInfo.applicationInfo.flags;
			if((flag&ApplicationInfo.FLAG_SYSTEM)!=0){
				//是系统应用
				info.setUserApp(false);
			}else{
				//不是系统应用
				info.setUserApp(true);
			}
			
			if((flag&ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0){
				//说明装在sd卡上
				info.setRom(false);
			}else{
				//说明装在内部存储里
				info.setRom(true);
			}
			info.setPackageName(packageName);
			info.setIcon(icon);
			info.setName(name);
			appInfos.add(info);
		}
		return appInfos;
	}
}
