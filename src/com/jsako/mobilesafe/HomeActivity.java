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
	private String[] names = { "手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计", "手机杀毒",
			"缓存清理", "高级工具", "设置中心" };
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
				case 8:// 当前点击的是设置中心
					intent = new Intent(HomeActivity.this,
							SettingActivity.class);
					startActivity(intent);
					break;
				case 0:// 当前点击的是手机防盗
					showLostFindDialog();
					break;
				case 7:// 进入高级工具
					intent = new Intent(HomeActivity.this, AtoolsActivity.class);
					startActivity(intent);
					break;
				case 1://进入通讯卫士
					intent=new Intent(HomeActivity.this,CallSmsSafeActivity.class);
					startActivity(intent);
					break;
				case 2://进入软件管理器页面
					intent=new Intent(HomeActivity.this,AppManagerActivity.class);
					startActivity(intent);
					break;
				case 3://进入进程管理界面
					intent=new Intent(HomeActivity.this,TaskManagerActivity.class);
					startActivity(intent);
					break;
				case 4://进入流量统计界面
					intent=new Intent(HomeActivity.this,TrafficManagerActivity.class);
					startActivity(intent);
					break;
				case 5://进入手机杀毒界面
					intent=new Intent(HomeActivity.this,AntiVirusActivity.class);
					startActivity(intent);
					break;
				case 6://进入缓存清理界面
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
		// 判断是否设置过密码
		if (isSetupPsw()) {
			// 设置过密码,弹出密码输入框
			Log.i(TAG, "设置过密码,弹出密码输入框");
			showEnterPswDialog();
		} else {
			// 没有设置过密码,弹出密码设置输入框
			Log.i(TAG, "没有设置过密码,弹出密码设置输入框");
			showSetupPswDialog();
		}
	}

	/**
	 * 显示密码设置输入框
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
					// 当前输入框为空
					Toast.makeText(getApplicationContext(), "密码不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (!psw.equals(pswConfirm)) {
					// 两次密码不一致
					Toast.makeText(getApplicationContext(), "密码不一致",
							Toast.LENGTH_SHORT).show();
					et_setup_psw.setText("");
					et_setup_confirm.setText("");
					return;
				}
				// 到这里说明密码不为空,且两次密码输入正确,存储密码
				Editor editor = sp.edit();
				editor.putString("password", Md5Utils.getMd5Password(psw));
				editor.commit();
				// 销毁对话框
				Toast.makeText(getApplicationContext(), "密码设置成功",
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
	 * 显示密码输入框
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
					Toast.makeText(HomeActivity.this, "密码不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (Md5Utils.getMd5Password(password).equals(
						HomeActivity.this.password)) {
					// 说明密码验证正确进入手机防盗,并销毁对话框
					enterDialog.dismiss();
					Log.i(TAG, "密码验证正确,进入手机防盗中心");
					Intent intent = new Intent(HomeActivity.this,
							LostFindActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(HomeActivity.this, "密码错误",
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
	 * 判断是否设置过密码,如果设置过密码返回true,否者返回false
	 * 
	 * @return true代表设置过密码, false代表没有设置过密码
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
