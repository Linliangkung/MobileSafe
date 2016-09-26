package com.jsako.mobilesafe.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.jsako.mobilesafe.R;
import com.jsako.mobilesafe.receiver.MyWidget;
import com.jsako.mobilesafe.utils.SystemInfoUtils;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ComponentInfo;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {
	private Timer timer;
	private TimerTask task;
	private AppWidgetManager awm;
	private ScreenOffReceiver off;
	private ScreenOnReceiver on;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		off=new ScreenOffReceiver();
		on=new ScreenOnReceiver();
		registerReceiver(off,new IntentFilter(Intent.ACTION_SCREEN_OFF));
		registerReceiver(on, new IntentFilter(Intent.ACTION_SCREEN_ON));
		startTimer();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(off);
		unregisterReceiver(on);
		off=null;
		on=null;
		stopTimer();
	}

	private void stopTimer() {
		if (timer != null && task != null) {
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
	}

	private void startTimer() {
		if(timer==null&&task==null){
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				System.out.println("更新widget");
				awm = AppWidgetManager.getInstance(UpdateWidgetService.this);
				ComponentName provider = new ComponentName(
						UpdateWidgetService.this, MyWidget.class);
				RemoteViews views = new RemoteViews(getPackageName(),
						R.layout.process_widget);
				views.setTextViewText(
						R.id.process_count,
						"正在运行的软件:"
								+ SystemInfoUtils
										.getRuningProcessCount(UpdateWidgetService.this)
								+ "个");
				long size = SystemInfoUtils
						.getAvailMem(UpdateWidgetService.this);
				views.setTextViewText(
						R.id.process_memory,
						"可用内存:"
								+ Formatter.formatFileSize(
										UpdateWidgetService.this, size));
				Intent intent = new Intent();
				intent.setAction("com.jsako.mobilesafe.killall");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						UpdateWidgetService.this, 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				awm.updateAppWidget(provider, views);
			}
		};
		timer.schedule(task, 0, 3000);
		}
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
				System.out.println("屏幕锁屏啦!");
				stopTimer();
			 }
		}
		
	private class ScreenOnReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
				System.out.println("屏幕解锁啦!");
				startTimer();
			 }
		}
}
