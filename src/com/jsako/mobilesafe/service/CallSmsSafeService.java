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
		// 当服务创建的时候,注册监听短信接收的广播
		receiver = new InnerSmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(1000);
		registerReceiver(receiver, filter);
		// 创建电话管理者对象
		manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 当服务销毁的时候,取消注册
		unregisterReceiver(receiver);
		receiver = null;
		// 取消电话的监听
		manager.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}

	private class InnerSmsReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "有短信到来");
			// 当接收到广播的时候
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				byte[] buf = (byte[]) obj;
				SmsMessage message = SmsMessage.createFromPdu(buf);
				// 获取发信人的号码
				String sender = message.getOriginatingAddress();
				Log.i(TAG, "发信人:" + sender);
				dao = new BlackNumberDao(CallSmsSafeService.this);
				String mode = dao.findMode(sender);
				// 当mode不等于null时说明这个电话被拦截了,当mode等于2或3的时候,说明这个电话需要进行短信拦截
				if ("2".equals(mode) || "3".equals(mode)) {
					Log.i(TAG, "短信拦截了。。。。");
					abortBroadcast();
				}
			}
		}

	}

	private class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 当电话响起的时候
				dao = new BlackNumberDao(CallSmsSafeService.this);
				String mode = dao.findMode(incomingNumber);
				if ("1".equals(mode) || "3".equals(mode)) {
					Log.i(TAG, "挂断电话");
					// 删除通话记录
					delCallInfo(incomingNumber);
					//注册一个内容观察者
					Uri uri = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(uri, true,new CallLogObserver(incomingNumber, new Handler()));
					// 挂断电话
					endCall();//另外一个进程执行的,异步执行,不是同步执行 
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:// 当电话处于空闲状态的时候
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 当电话接通的时候
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
			Log.i(TAG,"通话记录数据库发生改变,删除通话记录");
			//取消注册内容观察者
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
		// 呼叫记录uri路径
		Uri uri = Uri.parse("content://call_log/calls");// Uri路径可以用CallLog.CONTENT_URI;替换
		cr.delete(uri, "number=?", new String[] { incomingNumber });
	}
}
