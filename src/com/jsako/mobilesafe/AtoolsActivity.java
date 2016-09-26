package com.jsako.mobilesafe;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.jsako.mobilesafe.utils.SmsUtils;
import com.jsako.mobilesafe.utils.SmsUtils.SmsCallBack;

public class AtoolsActivity extends Activity {

	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}

	/**
	 * 号码归属地查询，进入该页面
	 * 
	 * @param view
	 */
	public void numberQuery(View view) {
		Intent intent = new Intent(this, NumberAddressQueryActivity.class);
		startActivity(intent);
	}

	/**
	 * 短信备份
	 */
	public void smsBackup(View view) {
		dialog = new ProgressDialog(this);
		dialog.setMessage("正在备份短信");
		// 水平样式
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.show();
		new Thread() {
			public void run() {
				try {
					SmsUtils.backUpSms(AtoolsActivity.this, new SmsCallBack() {

						@Override
						public void onBackup(int progress) {
							dialog.setProgress(progress);
						}

						@Override
						public void beforeBackup(int max) {
							dialog.setMax(max);
						}
					});
					// 调用此方法使ui更新在主线程上执行,适用于简单的ui更新
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "备份成功",
									Toast.LENGTH_SHORT).show();
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "备份失败",
									Toast.LENGTH_SHORT).show();
						}
					});
				} finally {
					dialog.dismiss();
				}
			};
		}.start();
	}

	/**
	 * 短信的还原
	 */
	public void smsRestore(View view) {
		// 先判断备份文件是否存在,如果不存在提示用户先去备份
		File file = new File(Environment.getExternalStorageDirectory(),
				"backup.xml");
		if (!file.exists() || file.length() <= 0) {
			Toast.makeText(this, "你尚未备份短信", Toast.LENGTH_SHORT).show();
			return;
		}
		// 对话框提醒用户,还原短信会删除当前的短信
		Builder builder = new Builder(this);
		builder.setTitle("警告");
		builder.setMessage("还原短信,删除当前短信的记录,是否继续");
		builder.setPositiveButton("继续", new OnClickListener() {
			@Override
			public void onClick(DialogInterface d, int which) {
				dialog = new ProgressDialog(AtoolsActivity.this);
				dialog.setMessage("正在还原短信");
				// 水平样式
				dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				dialog.show();
				new Thread() {
					public void run() {
						try {
							SmsUtils.restoreSms(AtoolsActivity.this,
									new SmsCallBack() {
										@Override
										public void onBackup(int progress) {
											dialog.setProgress(progress);
										}

										@Override
										public void beforeBackup(int max) {
											dialog.setMax(max);
										}
									});
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(getApplicationContext(),
											"还原成功", Toast.LENGTH_SHORT).show();
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(getApplicationContext(),
											"还原失败", Toast.LENGTH_SHORT).show();
								}
							});
						} finally {
							dialog.dismiss();
						}
					};
				}.start();
			}
		});

		builder.setNegativeButton("取消", null);
		builder.show();

	}
}
