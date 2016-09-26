package com.jsako.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.jsako.mobilesafe.db.dao.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallSmsSafeService extends Service {
	public static final String TAG = "CallSmsSafeService";
	private InnerSmsReceiver receiver;
	private TelephonyManager manager;
	private MyPhoneStateListener listener;
	private BlackNumberDao dao;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// �����񴴽���ʱ��,ע��������Ž��յĹ㲥
		receiver = new InnerSmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(1000);
		registerReceiver(receiver, filter);
		// �����绰�����߶���
		manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// ���������ٵ�ʱ��,ȡ��ע��
		unregisterReceiver(receiver);
		receiver = null;
		// ȡ���绰�ļ���
		manager.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}

	private class InnerSmsReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "�ж��ŵ���");
			// �����յ��㲥��ʱ��
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				byte[] buf = (byte[]) obj;
				SmsMessage message = SmsMessage.createFromPdu(buf);
				// ��ȡ�����˵ĺ���
				String sender = message.getOriginatingAddress();
				Log.i(TAG, "������:" + sender);
				dao = new BlackNumberDao(CallSmsSafeService.this);
				String mode = dao.findMode(sender);
				// ��mode������nullʱ˵������绰��������,��mode����2��3��ʱ��,˵������绰��Ҫ���ж�������
				if ("2".equals(mode) || "3".equals(mode)) {
					Log.i(TAG, "���������ˡ�������");
					abortBroadcast();
				}
			}
		}

	}

	private class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// ���绰�����ʱ��
				dao = new BlackNumberDao(CallSmsSafeService.this);
				String mode = dao.findMode(incomingNumber);
				if ("1".equals(mode) || "3".equals(mode)) {
					Log.i(TAG, "�Ҷϵ绰");
					// ɾ��ͨ����¼
					delCallInfo(incomingNumber);
					//ע��һ�����ݹ۲���
					Uri uri = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(uri, true,new CallLogObserver(incomingNumber, new Handler()));
					// �Ҷϵ绰
					endCall();//����һ������ִ�е�,�첽ִ��,����ͬ��ִ�� 
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:// ���绰���ڿ���״̬��ʱ��
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// ���绰��ͨ��ʱ��
				break;
			}
		}

	}
	private class CallLogObserver extends ContentObserver{
		private String number;
		public CallLogObserver(String number,Handler handler) {
			super(handler);
			this.number=number;
		}
		
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i(TAG,"ͨ����¼���ݿⷢ���ı�,ɾ��ͨ����¼");
			//ȡ��ע�����ݹ۲���
			getContentResolver().unregisterContentObserver(this);
			delCallInfo(number);
		}
	}
	private void endCall() {
		try {
			Class clazz = CallSmsSafeService.class.getClassLoader().loadClass(
					"android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony.Stub.asInterface(iBinder).endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void delCallInfo(String incomingNumber) {
		ContentResolver cr = getContentResolver();
		// ���м�¼uri·��
		Uri uri = Uri.parse("content://call_log/calls");// Uri·��������CallLog.CONTENT_URI;�滻
		cr.delete(uri, "number=?", new String[] { incomingNumber });
	}
}
