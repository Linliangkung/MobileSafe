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
	 * ��������ز�ѯ�������ҳ��
	 * 
	 * @param view
	 */
	public void numberQuery(View view) {
		Intent intent = new Intent(this, NumberAddressQueryActivity.class);
		startActivity(intent);
	}

	/**
	 * ���ű���
	 */
	public void smsBackup(View view) {
		dialog = new ProgressDialog(this);
		dialog.setMessage("���ڱ��ݶ���");
		// ˮƽ��ʽ
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
					// ���ô˷���ʹui���������߳���ִ��,�����ڼ򵥵�ui����
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "���ݳɹ�",
									Toast.LENGTH_SHORT).show();
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "����ʧ��",
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
	 * ���ŵĻ�ԭ
	 */
	public void smsRestore(View view) {
		// ���жϱ����ļ��Ƿ����,�����������ʾ�û���ȥ����
		File file = new File(Environment.getExternalStorageDirectory(),
				"backup.xml");
		if (!file.exists() || file.length() <= 0) {
			Toast.makeText(this, "����δ���ݶ���", Toast.LENGTH_SHORT).show();
			return;
		}
		// �Ի��������û�,��ԭ���Ż�ɾ����ǰ�Ķ���
		Builder builder = new Builder(this);
		builder.setTitle("����");
		builder.setMessage("��ԭ����,ɾ����ǰ���ŵļ�¼,�Ƿ����");
		builder.setPositiveButton("����", new OnClickListener() {
			@Override
			public void onClick(DialogInterface d, int which) {
				dialog = new ProgressDialog(AtoolsActivity.this);
				dialog.setMessage("���ڻ�ԭ����");
				// ˮƽ��ʽ
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
											"��ԭ�ɹ�", Toast.LENGTH_SHORT).show();
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(getApplicationContext(),
											"��ԭʧ��", Toast.LENGTH_SHORT).show();
								}
							});
						} finally {
							dialog.dismiss();
						}
					};
				}.start();
			}
		});

		builder.setNegativeButton("ȡ��", null);
		builder.show();

	}
}
