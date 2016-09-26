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

	// 定义窗体管理者,用来显示自定义Toast
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
		// 实例化广播接受者
		ocr = new OutCallingReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(1000);
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(ocr, filter);
		// 实例化窗体管理者
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 取消监听
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		// 取消注册广播接受者
		unregisterReceiver(ocr);
	}

	private class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				// 当电话响铃的时候
				String address = NumberAddressQueryUtils
						.getAddress(incomingNumber);
				showToast(address);
				System.out.println("电话响铃了");
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				// 当电话处于空闲状态的时候调用
				if (view != null) {
					wm.removeView(view);
				}
			}
		}

	}

	private class OutCallingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 获取去电的电话
			String phone = getResultData();
			// 获取号码归属地
			String address = NumberAddressQueryUtils.getAddress(phone);
			showToast(address);
		}

	}

	private void showToast(String address) {
		
		view = View.inflate(this, R.layout.address_show, null);
		//设置一个点击事件
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.arraycopy(counts,1, counts, 0, counts.length-1);
				counts[counts.length-1]=SystemClock.uptimeMillis();
				if((SystemClock.uptimeMillis()-counts[0])<=500){
					//说明这是个双击事件
					//使这个view居中显示
					params.x=wm.getDefaultDisplay().getWidth()/2-view.getWidth()/2;
					params.y=wm.getDefaultDisplay().getHeight()/2-view.getHeight()/2;
					wm.updateViewLayout(view, params);
					saveXY();
				}
			}
		});
		//设置一个触摸事件
		view.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						//当手指按下控件 getRawX()是获得整个屏幕的位置,而getX()是获得相对控件的位置
						 startX=(int) event.getRawX();
						 startY=(int) event.getRawY();
						Log.i(TAG,"开始位置为:x:"+startX+",y:"+startY);
						break;
					case MotionEvent.ACTION_MOVE:
						//当手指在控件上移动
						int nowX=(int) event.getRawX();
						int nowY=(int) event.getRawY();
					
						int dx=nowX-startX;
						int dy=nowY-startY;
						//改变开始的左边
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
						//当手指离开控件
						Log.i(TAG,"手指离开控件");
						//保存控件最后的位置,利于下次初始化时,保留原来的位置
						saveXY();
						break;
				}
				return false;//说明事件到这里就完成了(有截断事件的意义),不要让父控件或者父布局去响应触摸事件
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
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;// android系统里面具有电话优先级的一种窗体类型，记得添加权限。
		wm.addView(view, params);
		
		System.out.println("方法走到这里了");
	}
	private void saveXY(){
		Editor editor=sp.edit();
		editor.putInt("lastx", params.x);
		editor.putInt("lasty", params.y);
		editor.commit();
	}
}
