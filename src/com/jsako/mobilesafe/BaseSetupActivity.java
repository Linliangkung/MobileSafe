package com.jsako.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	// 1.����һ������ʶ����
	private GestureDetector detector;
	protected SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 2.ʵ��������ʶ����
		detector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					/**
					 * �ֻ�����Ļ�ص��ķ���
					 */
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						float x1 = e1.getRawX();
						float x2 = e2.getRawX();
						float y1 = e1.getRawY();
						float y2 = e2.getRawY();
						//����б��
						if(Math.abs(y1-y2)>100){
							Toast.makeText(getApplicationContext(),"����ʧ��",Toast.LENGTH_SHORT).show();
							return true;
						}
						if(Math.abs(velocityX)<200){
							Toast.makeText(getApplicationContext(),"����̫����", Toast.LENGTH_SHORT).show();
							return true;
						}
						// ���������ƶ�,��ʾ��һ��ҳ��
						if ((x2 - x1) > 200) {
							showPre();
						}
						// ���������ƶ�,��ʾ��һ��ҳ��
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
		// 3.�������¼���������ʶ����
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
