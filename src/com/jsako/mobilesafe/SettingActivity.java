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
		 * �����Ƿ����Զ�����
		 */
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// ��ȡconfig���ò���,��ʼ�����Զ���ؼ�
		boolean checked = sp.getBoolean("update", false);
		siv_update.setChecked(checked);
		siv_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (siv_update.isChecked()) {
					// �Զ����¿���
					siv_update.setChecked(false);
					editor.putBoolean("update", false);
				} else {
					// �Զ����¹ر�
					siv_update.setChecked(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		/**
		 * �����Ƿ��������������ʾ
		 */
		// ��ʼ��״̬�Զ���ؼ���ѡ��״̬
		siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
		siv_show_address.setChecked(ServiceUtils.isRunningService(this,
				"com.jsako.mobilesafe.service.AddressService"));
		siv_show_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,
						AddressService.class);
				if (siv_show_address.isChecked()) {
					// ��ʾ��������عر�
					siv_show_address.setChecked(false);
					// �رշ���
					stopService(intent);
				} else {
					// ��ʾ��������ؿ���
					siv_show_address.setChecked(true);
					// ��������
					startService(intent);
				}
			}
		});
		// ���ù�������ʾ����ѡ��
		scv_choose_addresshow_background = (SettingClickView) findViewById(R.id.scv_choose_addresshow_background);
		final String[] items = { "��͸��", "������", "��ʿ��", "������", "ƻ����" };
		// ��ʼ����Ͽؼ���һЩ��Ϣ
		int which = sp.getInt("which", 0);
		scv_choose_addresshow_background.setDesc(items[which]);
		scv_choose_addresshow_background
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int which=sp.getInt("which",0);
						// ������ѡ�Ի���
						Builder builder = new Builder(SettingActivity.this);
						builder.setSingleChoiceItems(items, which,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										//����ѡ�ı������浽config.xml����
										Editor editor=sp.edit();
										editor.putInt("which",which);
										editor.commit();
										scv_choose_addresshow_background.setDesc(items[which]);
										//�رնԻ���
										dialog.dismiss();
									}
								});
						builder.setNegativeButton("ȡ��",null);
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
					// ��ʾ��������عر�
					siv_callsms_safe.setChecked(false);
					// �رշ���
					stopService(intent);
				} else {
					// ��ʾ��������ؿ���
					siv_callsms_safe.setChecked(true);
					// ��������
					startService(intent);
				}
			}
		});
		
		//���Ź�������������
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
					// �رշ���
					stopService(intent);
				} else {
					siv_watchdog.setChecked(true);
					// ��������
					startService(intent);
				}
			}
		});
	}

	/**
	 * ���»�ý����ʱ��
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
