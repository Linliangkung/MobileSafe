package com.jsako.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.jsako.mobilesafe.R;
import com.jsako.mobilesafe.db.dao.NumberAddressQueryUtils;

public class AddressService extends Service {
	protected static final String TAG = "AddressService";
	private TelephonyManager tm;
	private MyPhoneStateListener listener;
	private OutCallingReceiver ocr;

	// ���崰�������,������ʾ�Զ���Toast
	private WindowManager wm;
	private View view;
	private SharedPreferences sp;
	private WindowManager.LayoutParams params;
	private long[] counts=new long[2];

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		listener = new MyPhoneStateListener();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		// ʵ�����㲥������
		ocr = new OutCallingReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(1000);
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(ocr, filter);
		// ʵ�������������
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// ȡ������
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		// ȡ��ע��㲥������
		unregisterReceiver(ocr);
	}

	private class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				// ���绰�����ʱ��
				String address = NumberAddressQueryUtils
						.getAddress(incomingNumber);
				showToast(address);
				System.out.println("�绰������");
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				// ���绰���ڿ���״̬��ʱ�����
				if (view != null) {
					wm.removeView(view);
				}
			}
		}

	}

	private class OutCallingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// ��ȡȥ��ĵ绰
			String phone = getResultData();
			// ��ȡ���������
			String address = NumberAddressQueryUtils.getAddress(phone);
			showToast(address);
		}

	}

	private void showToast(String address) {
		
		view = View.inflate(this, R.layout.address_show, null);
		//����һ������¼�
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.arraycopy(counts,1, counts, 0, counts.length-1);
				counts[counts.length-1]=SystemClock.uptimeMillis();
				if((SystemClock.uptimeMillis()-counts[0])<=500){
					//˵�����Ǹ�˫���¼�
					//ʹ���view������ʾ
					params.x=wm.getDefaultDisplay().getWidth()/2-view.getWidth()/2;
					params.y=wm.getDefaultDisplay().getHeight()/2-view.getHeight()/2;
					wm.updateViewLayout(view, params);
					saveXY();
				}
			}
		});
		//����һ�������¼�
		view.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						//����ָ���¿ؼ� getRawX()�ǻ��������Ļ��λ��,��getX()�ǻ����Կؼ���λ��
						 startX=(int) event.getRawX();
						 startY=(int) event.getRawY();
						Log.i(TAG,"��ʼλ��Ϊ:x:"+startX+",y:"+startY);
						break;
					case MotionEvent.ACTION_MOVE:
						//����ָ�ڿؼ����ƶ�
						int nowX=(int) event.getRawX();
						int nowY=(int) event.getRawY();
					
						int dx=nowX-startX;
						int dy=nowY-startY;
						//�ı俪ʼ�����
						startX=nowX;
						startY=nowY;
		
						params.x+=dx;
						params.y+=dy;
						
						if(params.x<0)
						params.x=0;
						if(params.y<0)
						params.y=0;
						if(params.x>(wm.getDefaultDisplay().getWidth()-view.getWidth())){
							params.x=wm.getDefaultDisplay().getWidth()-view.getWidth();
						}
						if(params.y>(wm.getDefaultDisplay().getHeight()-view.getHeight())){
							params.y=wm.getDefaultDisplay().getHeight()-view.getHeight();
						}
						wm.updateViewLayout(view, params);
						break;
					case MotionEvent.ACTION_UP:
						//����ָ�뿪�ؼ�
						Log.i(TAG,"��ָ�뿪�ؼ�");
						//����ؼ�����λ��,�����´γ�ʼ��ʱ,����ԭ����λ��
						saveXY();
						break;
				}
				return false;//˵���¼�������������(�нض��¼�������),��Ҫ�ø��ؼ����߸�����ȥ��Ӧ�����¼�
			}
		});
		TextView tv_address = (TextView) view.findViewById(R.id.tv_address);
		tv_address.setText(address);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		int which = sp.getInt("which", 0);
		int[] bgs = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };
		
		view.setBackgroundResource(bgs[which]);
		params = new WindowManager.LayoutParams();
		params.gravity=Gravity.TOP+Gravity.LEFT;
		params.x=sp.getInt("lastx", 0);
		params.y=sp.getInt("lasty", 0);
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;// androidϵͳ������е绰���ȼ���һ�ִ������ͣ��ǵ����Ȩ�ޡ�
		wm.addView(view, params);
		
		System.out.println("�����ߵ�������");
	}
	private void saveXY(){
		Editor editor=sp.edit();
		editor.putInt("lastx", params.x);
		editor.putInt("lasty", params.y);
		editor.commit();
	}
}
