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
		// ��ȡλ�ù�����
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		// ���þ�ȷ�����
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(criteria, true);
		listener = new MyLocationListener();
		// ע�����λ�ñ仯����,10�����һ��
		lm.requestLocationUpdates(provider, 0, 0, listener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(listener);
		listener = null;
	}

	private class MyLocationListener implements LocationListener {
		// λ�÷����ı��ʱ�����
		@Override
		public void onLocationChanged(Location location) {
			// ��ȡ����
			double longitude = location.getLongitude();
			// ��ȡγ��
			double latitude = location.getLatitude();
			// ��ȡ��ȷ��
			float accuracy = location.getAccuracy();
			Log.i(TAG, "longitude:" + longitude);
			Log.i(TAG, "latitude:" + latitude);

			// ����γ�ȴ���config
			Editor editor = sp.edit();
			String data = "j:" + longitude + ",w:" + latitude + ",a:"
					+ accuracy;
			editor.putString("lastlocation", data);
			editor.commit();
		}

		// ����״̬�����仯��ʱ�����
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
