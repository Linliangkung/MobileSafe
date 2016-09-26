package com.jsako.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

/**
 * ϵͳ��Ϣ������
 * 
 * @author Administrator
 * 
 */
public class SystemInfoUtils {
	private SystemInfoUtils() {
	}

	/**
	 * ��ȡ��ǰϵͳ��������
	 * 
	 * @param context
	 *            ������
	 * @return ��ǰϵͳ��������
	 */
	public static int getRuningProcessCount(Context context) {
		// PackageManager�������� ActivityManager���̹�����
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		return am.getRunningAppProcesses().size();
	}

	/**
	 * ��õ�ǰϵͳ�����ڴ�
	 * 
	 * @param context
	 *            ������
	 * @return ��ǰϵͳ�����ڴ�
	 */
	public static long getAvailMem(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	/**
	 * ��õ�ǰϵͳ���ڴ�
	 * @param context ������
	 * @return ��ǰϵͳ���ڴ� ��λ��byte
	 */
	public static long getTotalMem(Context context) {
		/*
		 * ���apiֻ����4.0ϵͳ����,���Բ�֧�� ActivityManager am=(ActivityManager)
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
