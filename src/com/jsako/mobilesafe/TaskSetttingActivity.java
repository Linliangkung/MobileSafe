package com.jsako.mobilesafe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.jsako.mobilesafe.service.AutoCleanService;
import com.jsako.mobilesafe.ui.SettingItemView;
import com.jsako.mobilesafe.utils.ServiceUtils;

public class TaskSetttingActivity extends Activity {
	private SettingItemView siv_showsystem;
	private SettingItemView siv_killprocess_ontime;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_setting);
		sp=getSharedPreferences("config", Context.MODE_PRIVATE);
		

		//显示系统进程的设置
		siv_showsystem=(SettingItemView) findViewById(R.id.siv_showsystem);
		//回显数据
		siv_showsystem.setChecked(sp.getBoolean("showsystem", false));
		siv_showsystem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor=sp.edit();
				boolean checked=siv_showsystem.isChecked();
				siv_showsystem.setChecked(!checked);
				editor.putBoolean("showsystem", !checked);
				editor.commit();
			}
		});
		
		siv_killprocess_ontime=(SettingItemView) findViewById(R.id.siv_killprocess_ontime);
		//这句可以省略
		siv_killprocess_ontime.setChecked(ServiceUtils.isRunningService(this,"com.jsako.mobilesafe.service.AutoCleanService"));
		siv_killprocess_ontime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(TaskSetttingActivity.this,AutoCleanService.class);
				if(siv_killprocess_ontime.isChecked()){
					//已经开启锁屏清理
					siv_killprocess_ontime.setChecked(false);
					stopService(intent);
				}else{
					//没有开启锁屏清理
					siv_killprocess_ontime.setChecked(true);
					startService(intent);
				}
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		siv_killprocess_ontime.setChecked(ServiceUtils.isRunningService(this,"com.jsako.mobilesafe.service.AutoCleanService"));
	}
}
