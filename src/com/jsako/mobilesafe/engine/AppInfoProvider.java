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
 * ҵ����,�ṩ�ֻ��ﰲװ������Ӧ�ó�����Ϣ
 * 
 * @author Administrator
 * 
 */
public class AppInfoProvider {
	/**
	 * ���ذ�app��Ϣ
	 * @param context
	 * @return ����app��Ϣ�ļ���
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		AppInfo info;
		for (PackageInfo packageInfo : packageInfos) {
			// packageInfo�൱���嵥�ļ�.xml
			info = new AppInfo();
			String packageName = packageInfo.packageName;
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
			String name = (String) packageInfo.applicationInfo.loadLabel(pm);
			int flag=packageInfo.applicationInfo.flags;
			if((flag&ApplicationInfo.FLAG_SYSTEM)!=0){
				//��ϵͳӦ��
				info.setUserApp(false);
			}else{
				//����ϵͳӦ��
				info.setUserApp(true);
			}
			
			if((flag&ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0){
				//˵��װ��sd����
				info.setRom(false);
			}else{
				//˵��װ���ڲ��洢��
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
