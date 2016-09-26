package com.jsako.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

import com.jsako.mobilesafe.R;
import com.jsako.mobilesafe.domain.TrafficInfo;

public class TrafficInfoProvider {
	
	public static List<TrafficInfo> getTrafficInfos(Context context){
		PackageManager pm=context.getPackageManager();
		List<ApplicationInfo> appInfos=pm.getInstalledApplications(0);
		List<TrafficInfo> trafficInfos=new ArrayList<TrafficInfo>();
		long systemRx=0;
		long systemTx=0;
		for(ApplicationInfo appInfo:appInfos){
			int uid=appInfo.uid;
			long rx=TrafficStats.getUidRxBytes(uid);
			long tx=TrafficStats.getUidTxBytes(uid);
			if((appInfo.flags&ApplicationInfo.FLAG_SYSTEM)!=0){
				//如果是系统应用则跳过
				systemRx+=rx;
				systemTx+=tx;
				continue;
			}
			if(rx!=0||tx!=0){
				TrafficInfo trafficInfo=new TrafficInfo();
				trafficInfo.setTx(tx);
				trafficInfo.setRx(rx);
				trafficInfo.setIcon(appInfo.loadIcon(pm));
				trafficInfo.setName(appInfo.loadLabel(pm).toString());
				trafficInfos.add(trafficInfo);
			}
		}
		TrafficInfo trafficInfo=new TrafficInfo();
		trafficInfo.setTx(systemTx);
		trafficInfo.setRx(systemRx);
		trafficInfo.setIcon(context.getResources().getDrawable(R.drawable.xiaotubiao));
		trafficInfo.setName("系统应用");
		trafficInfos.add(trafficInfo);
		return trafficInfos;
	}
}
