package com.jsako.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

/**
 * 系统信息工具类
 * 
 * @author Administrator
 * 
 */
public class SystemInfoUtils {
	private SystemInfoUtils() {
	}

	/**
	 * 获取当前系统进程数量
	 * 
	 * @param context
	 *            上下文
	 * @return 当前系统进程数量
	 */
	public static int getRuningProcessCount(Context context) {
		// PackageManager包管理器 ActivityManager进程管理器
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		return am.getRunningAppProcesses().size();
	}

	/**
	 * 获得当前系统可用内存
	 * 
	 * @param context
	 *            上下文
	 * @return 当前系统可用内存
	 */
	public static long getAvailMem(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	/**
	 * 获得当前系统总内存
	 * @param context 上下文
	 * @return 当前系统总内存 单位是byte
	 */
	public static long getTotalMem(Context context) {
		/*
		 * 这个api只能在4.0系统后用,所以不支持 ActivityManager am=(ActivityManager)
		 * context.getSystemService(Context.ACTIVITY_SERVICE); MemoryInfo
		 * outInfo=new MemoryInfo(); am.getMemoryInfo(outInfo); return
		 * outInfo.totalMem;
		 */
		try {
			File file = new File("/proc/meminfo");
			FileReader fr=new FileReader(file);
			BufferedReader br=new BufferedReader(fr);
			//MemTotal:         516452 kB
			String line= br.readLine();
			StringBuilder sb=new StringBuilder();
			for(char c:line.toCharArray()){
				if(c>='0'&&c<='9'){
					sb.append(c);
				}
			}
			long totalMem=Long.parseLong(sb.toString())*1024;
			return totalMem;
		
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
