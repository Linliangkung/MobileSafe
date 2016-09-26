package com.jsako.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.jsako.mobilesafe.service.AddressService;
import com.jsako.mobilesafe.service.CallSmsSafeService;
import com.jsako.mobilesafe.service.WatchDogService;
import com.jsako.mobilesafe.ui.SettingClickView;
import com.jsako.mobilesafe.ui.SettingItemView;
import com.jsako.mobilesafe.utils.ServiceUtils;

public class SettingActivity extends Activity {
	private SettingItemView siv_update;
	private SharedPreferences sp;
	private SettingItemView siv_show_address;
	private SettingClickView scv_choose_addresshow_background;
	private SettingItemView siv_callsms_safe;
	private SettingItemView siv_watchdog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		/**
		 * 设置是否开启自动更新
		 */
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 读取config配置参数,初始化给自定义控件
		boolean checked = sp.getBoolean("update", false);
		siv_update.setChecked(checked);
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (siv_update.isChecked()) {
					// 自动更新开启
					siv_update.setChecked(false);
					editor.putBoolean("update", false);
				} else {
					// 自动更新关闭
					siv_update.setChecked(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		/**
		 * 设置是否开启来电归属地显示
		 */
		// 初始化状态自定义控件勾选的状态
		siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
		siv_show_address.setChecked(ServiceUtils.isRunningService(this,
				"com.jsako.mobilesafe.service.AddressService"));
		siv_show_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,
						AddressService.class);
				if (siv_show_address.isChecked()) {
					// 显示来电归属地关闭
					siv_show_address.setChecked(false);
					// 关闭服务
					stopService(intent);
				} else {
					// 显示来电归属地开启
					siv_show_address.setChecked(true);
					// 开启服务
					startService(intent);
				}
			}
		});
		// 设置归属地提示框风格选择
		scv_choose_addresshow_background = (SettingClickView) findViewById(R.id.scv_choose_addresshow_background);
		final String[] items = { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
		// 初始化组合控件的一些信息
		int which = sp.getInt("which", 0);
		scv_choose_addresshow_background.setDesc(items[which]);
		scv_choose_addresshow_background
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int which=sp.getInt("which",0);
						// 弹出单选对话框
						Builder builder = new Builder(SettingActivity.this);
						builder.setSingleChoiceItems(items, which,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										//将所选的背景保存到config.xml里面
										Editor editor=sp.edit();
										editor.putInt("which",which);
										editor.commit();
										scv_choose_addresshow_background.setDesc(items[which]);
										//关闭对话框
										dialog.dismiss();
									}
								});
						builder.setNegativeButton("取消",null);
						builder.show();
					}
				});
		siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
		siv_callsms_safe.setChecked(ServiceUtils.isRunningService(this,
				"com.jsako.mobilesafe.service.CallSmsSafeService"));
		siv_callsms_safe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,
						CallSmsSafeService.class);
				if (siv_callsms_safe.isChecked()) {
					// 显示来电归属地关闭
					siv_callsms_safe.setChecked(false);
					// 关闭服务
					stopService(intent);
				} else {
					// 显示来电归属地开启
					siv_callsms_safe.setChecked(true);
					// 开启服务
					startService(intent);
				}
			}
		});
		
		//看门狗服务开启的设置
		siv_watchdog=(SettingItemView) findViewById(R.id.siv_watchdog);
		siv_watchdog.setChecked(ServiceUtils.isRunningService(this,
				"com.jsako.mobilesafe.service.WatchDogService"));
		siv_watchdog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,
						WatchDogService.class);
				if (siv_watchdog.isChecked()) {
					siv_watchdog.setChecked(false);
					// 关闭服务
					stopService(intent);
				} else {
					siv_watchdog.setChecked(true);
					// 开启服务
					startService(intent);
				}
			}
		});
	}

	/**
	 * 重新获得焦点的时候
	 */
	@Override
	protected void onResume() {
		super.onResume();
		siv_show_address.setChecked(ServiceUtils.isRunningService(this,
				"com.jsako.mobilesafe.service.AddressService"));
		siv_callsms_safe.setChecked(ServiceUtils.isRunningService(this,
				"com.jsako.mobilesafe.service.CallSmsSafeService"));
		siv_watchdog.setChecked(ServiceUtils.isRunningService(this,
				"com.jsako.mobilesafe.service.WatchDogService"));
	}
}
