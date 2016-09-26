package com.jsako.mobilesafe;

import com.jsako.mobilesafe.utils.Md5Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	private static final String TAG = "HomeActivity";
	private GridView list_home;
	private MyAdapter adapter;
	private SharedPreferences sp;
	private AlertDialog setupDialog;
	private AlertDialog enterDialog;
	private String[] names = { "�ֻ�����", "ͨѶ��ʿ", "�������", "���̹���", "����ͳ��", "�ֻ�ɱ��",
			"��������", "�߼�����", "��������" };
	private int[] ids = { R.drawable.safe, R.drawable.callmsgsafe,
			R.drawable.app, R.drawable.taskmanager, R.drawable.netmanager,
			R.drawable.trojan, R.drawable.sysoptimize, R.drawable.atools,
			R.drawable.settings };
	private TextView et_setup_psw;
	private TextView et_setup_confirm;
	private Button ok;
	private Button cancel;
	private TextView et_enter_psw;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		list_home = (GridView) findViewById(R.id.list_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		adapter = new MyAdapter();
		list_home.setAdapter(adapter);
		list_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent;
				switch (position) {
				case 8:// ��ǰ���������������
					intent = new Intent(HomeActivity.this,
							SettingActivity.class);
					startActivity(intent);
					break;
				case 0:// ��ǰ��������ֻ�����
					showLostFindDialog();
					break;
				case 7:// ����߼�����
					intent = new Intent(HomeActivity.this, AtoolsActivity.class);
					startActivity(intent);
					break;
				case 1://����ͨѶ��ʿ
					intent=new Intent(HomeActivity.this,CallSmsSafeActivity.class);
					startActivity(intent);
					break;
				case 2://�������������ҳ��
					intent=new Intent(HomeActivity.this,AppManagerActivity.class);
					startActivity(intent);
					break;
				case 3://������̹������
					intent=new Intent(HomeActivity.this,TaskManagerActivity.class);
					startActivity(intent);
					break;
				case 4://��������ͳ�ƽ���
					intent=new Intent(HomeActivity.this,TrafficManagerActivity.class);
					startActivity(intent);
					break;
				case 5://�����ֻ�ɱ������
					intent=new Intent(HomeActivity.this,AntiVirusActivity.class);
					startActivity(intent);
					break;
				case 6://���뻺���������
					intent=new Intent(HomeActivity.this,CleanCacheActivity.class);
					startActivity(intent);
					break;
				default:
					break;
				}
			}

		});
	}

	protected void showLostFindDialog() {
		// �ж��Ƿ����ù�����
		if (isSetupPsw()) {
			// ���ù�����,�������������
			Log.i(TAG, "���ù�����,�������������");
			showEnterPswDialog();
		} else {
			// û�����ù�����,�����������������
			Log.i(TAG, "û�����ù�����,�����������������");
			showSetupPswDialog();
		}
	}

	/**
	 * ��ʾ�������������
	 */
	private void showSetupPswDialog() {
		Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dialog_setup_password, null);
		et_setup_psw = (TextView) view.findViewById(R.id.et_setup_psw);
		et_setup_confirm = (TextView) view.findViewById(R.id.et_setup_confirm);
		ok = (Button) view.findViewById(R.id.ok);
		cancel = (Button) view.findViewById(R.id.cancel);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String psw = et_setup_psw.getText().toString().trim();
				String pswConfirm = et_setup_confirm.getText().toString()
						.trim();
				if (TextUtils.isEmpty(psw) || TextUtils.isEmpty(pswConfirm)) {
					// ��ǰ�����Ϊ��
					Toast.makeText(getApplicationContext(), "���벻��Ϊ��",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (!psw.equals(pswConfirm)) {
					// �������벻һ��
					Toast.makeText(getApplicationContext(), "���벻һ��",
							Toast.LENGTH_SHORT).show();
					et_setup_psw.setText("");
					et_setup_confirm.setText("");
					return;
				}
				// ������˵�����벻Ϊ��,����������������ȷ,�洢����
				Editor editor = sp.edit();
				editor.putString("password", Md5Utils.getMd5Password(psw));
				editor.commit();
				// ���ٶԻ���
				Toast.makeText(getApplicationContext(), "�������óɹ�",
						Toast.LENGTH_SHORT).show();
				setupDialog.dismiss();
				Intent intent = new Intent(HomeActivity.this,
						LostFindActivity.class);
				startActivity(intent);
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setupDialog.dismiss();
			}
		});
		setupDialog = builder.create();
		setupDialog.setView(view, 0, 0, 0, 0);
		setupDialog.show();
	}

	/**
	 * ��ʾ���������
	 */
	private void showEnterPswDialog() {
		Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dialog_enter_password, null);
		et_enter_psw = (TextView) view.findViewById(R.id.et_enter_psw);
		ok = (Button) view.findViewById(R.id.ok);
		cancel = (Button) view.findViewById(R.id.cancel);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = et_enter_psw.getText().toString().trim();
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(HomeActivity.this, "���벻��Ϊ��",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (Md5Utils.getMd5Password(password).equals(
						HomeActivity.this.password)) {
					// ˵��������֤��ȷ�����ֻ�����,�����ٶԻ���
					enterDialog.dismiss();
					Log.i(TAG, "������֤��ȷ,�����ֻ���������");
					Intent intent = new Intent(HomeActivity.this,
							LostFindActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(HomeActivity.this, "�������",
							Toast.LENGTH_SHORT).show();
					et_enter_psw.setText("");
				}
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				enterDialog.dismiss();
			}
		});
		enterDialog = builder.create();
		enterDialog.setView(view, 0, 0, 0, 0);
		enterDialog.show();
	}

	/**
	 * �ж��Ƿ����ù�����,������ù����뷵��true,���߷���false
	 * 
	 * @return true�������ù�����, false����û�����ù�����
	 */
	private boolean isSetupPsw() {
		password = sp.getString("password", null);
		return !TextUtils.isEmpty(password);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return names.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(HomeActivity.this,
					R.layout.list_item_home, null);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			tv_item.setText(names[position]);
			iv_item.setImageResource(ids[position]);
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}
}
