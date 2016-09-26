package com.jsako.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	// 1.定义一个手势识别器
	private GestureDetector detector;
	protected SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 2.实例化手势识别器
		detector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					/**
					 * 手滑动屏幕回调的方法
					 */
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						float x1 = e1.getRawX();
						float x2 = e2.getRawX();
						float y1 = e1.getRawY();
						float y2 = e2.getRawY();
						//屏蔽斜滑
						if(Math.abs(y1-y2)>100){
							Toast.makeText(getApplicationContext(),"滑动失败",Toast.LENGTH_SHORT).show();
							return true;
						}
						if(Math.abs(velocityX)<200){
							Toast.makeText(getApplicationContext(),"滑动太慢了", Toast.LENGTH_SHORT).show();
							return true;
						}
						// 从左往右移动,显示上一个页面
						if ((x2 - x1) > 200) {
							showPre();
						}
						// 从右往左移动,显示下一个页面
						if ((x1 - x2) > 200) {
							showNext();
						}
						return super.onFling(e1, e2, velocityX, velocityY);
					}

				});
	}

	protected abstract void showNext();

	protected abstract void showPre();

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 3.将触摸事件赋给手势识别器
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	public void next(View view) {
		showNext();
	}

	public void pre(View view) {
		showPre();
	}
}
