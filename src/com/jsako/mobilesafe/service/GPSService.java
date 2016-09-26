package com.jsako.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSService extends Service {
	public static final String TAG = "GPSService";
	private SharedPreferences sp;
	private LocationManager lm;
	private MyLocationListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 获取位置管理者
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		// 设置精确度最高
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(criteria, true);
		listener = new MyLocationListener();
		// 注册监听位置变化服务,10秒更新一次
		lm.requestLocationUpdates(provider, 0, 0, listener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(listener);
		listener = null;
	}

	private class MyLocationListener implements LocationListener {
		// 位置发生改变的时候调用
		@Override
		public void onLocationChanged(Location location) {
			// 获取经度
			double longitude = location.getLongitude();
			// 获取纬度
			double latitude = location.getLatitude();
			// 获取精确度
			float accuracy = location.getAccuracy();
			Log.i(TAG, "longitude:" + longitude);
			Log.i(TAG, "latitude:" + latitude);

			// 将经纬度存入config
			Editor editor = sp.edit();
			String data = "j:" + longitude + ",w:" + latitude + ",a:"
					+ accuracy;
			editor.putString("lastlocation", data);
			editor.commit();
		}

		// 服务状态发生变化的时候调用
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}
	}
}
