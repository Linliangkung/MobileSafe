package com.jsako.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jsako.mobilesafe.db.dao.AppLockDao;
import com.jsako.mobilesafe.domain.AppInfo;
import com.jsako.mobilesafe.engine.AppInfoProvider;
import com.jsako.mobilesafe.utils.Md5Utils;

public class AppManagerActivity extends Activity implements OnClickListener {
	private static final String TAG = "AppManagerActivity";
	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private List<AppInfo> appInfos;
	/**
	 * 保存用户应用的集合
	 */
	private List<AppInfo> userAppInfos;
	/**
	 * 保存系统应用的集合
	 */
	private List<AppInfo> systemAppInfos;
	private PopupWindow popu;
	private ListView lv_app_manager;
	private LinearLayout ll_load_app;
	private MyAdapter adapter;
	private TextView tv_show;
	private SharedPreferences sp;
	private AlertDialog setupDialog;

	/**
	 * 分享
	 */
	private LinearLayout ll_share;
	/**
	 * 卸载
	 */
	private LinearLayout ll_uninstall;
	/**
	 * 启动
	 */
	private LinearLayout ll_start;

	private AppInfo info;

	// 操作applock数据的dao
	private AppLockDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		dao = new AppLockDao(this);
		tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
		tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
		ll_load_app = (LinearLayout) findViewById(R.id.ll_load_app);
		tv_show = (TextView) findViewById(R.id.tv_show);
		Log.i(TAG, "内部存储的路径:"
				+ Environment.getDataDirectory().getAbsolutePath());
		Log.i(TAG, "sd卡存储的路径:"
				+ Environment.getExternalStorageDirectory().getAbsolutePath());
		long rom = getAvailSpace(Environment.getDataDirectory()
				.getAbsolutePath());
		long sd = getAvailSpace(Environment.getExternalStorageDirectory()
				.getAbsolutePath());

		tv_avail_rom.setText("内存可用:" + Formatter.formatFileSize(this, rom));
		tv_avail_sd.setText("sd卡可用:" + Formatter.formatFileSize(this, sd));
		/**
		 * 设置listview滚动事件的监听
		 */
		lv_app_manager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size()) {
						tv_show.setText("系统程序" + systemAppInfos.size() + "个");
					} else {
						tv_show.setText("用户程序" + userAppInfos.size() + "个");
					}
					dismissPopupWindow();
				}

			}
		});

		/**
		 * 设置listview 的item被点击的事件的监听
		 */

		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					return;
				} else if (position == userAppInfos.size() + 1) {
					return;
				} else if (position <= userAppInfos.size()) {
					info = userAppInfos.get(position - 1);
				} else if (position > userAppInfos.size()) {
					info = systemAppInfos.get(position - 1
							- userAppInfos.size() - 1);
				}
				dismissPopupWindow();
				View contentView = View.inflate(AppManagerActivity.this,
						R.layout.popup_app_item, null);
				ll_uninstall = (LinearLayout) contentView
						.findViewById(R.id.ll_uninstall);
				ll_start = (LinearLayout) contentView
						.findViewById(R.id.ll_start);
				ll_share = (LinearLayout) contentView
						.findViewById(R.id.ll_share);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);
				ll_start.setOnClickListener(AppManagerActivity.this);
				ll_share.setOnClickListener(AppManagerActivity.this);
				popu = new PopupWindow(contentView, -2, -2);
				// 动画的播放前提是,窗体必须有背景颜色
				popu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				int[] location = new int[2];
				view.getLocationInWindow(location);

				// 获取app图片的大小
				ImageView iv_app_info = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				int width = iv_app_info.getWidth();
				popu.showAtLocation(parent, Gravity.TOP | Gravity.LEFT, width,
						location[1]);

				// 设置动画
				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(300);
				AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
				aa.setDuration(300);

				AnimationSet set = new AnimationSet(false);
				set.addAnimation(sa);
				set.addAnimation(aa);

				contentView.startAnimation(set);

			}
		});

		lv_app_manager
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						if (position == 0) {
							return true;
						} else if (position == userAppInfos.size() + 1) {
							return true;
						} else if (position <= userAppInfos.size()) {
							info = userAppInfos.get(position - 1);
						} else if (position > userAppInfos.size()) {
							info = systemAppInfos.get(position - 1
									- userAppInfos.size() - 1);
						}
						sp = AppManagerActivity.this.getSharedPreferences(
								"config", Context.MODE_PRIVATE);
						String applock = sp.getString("applockpassword", null);
						if (TextUtils.isEmpty(applock)) {
							// 说明没有设置过密码,弹出对话框设置密码
							showSetupPSWDialog(view);
							return true;
						}
						lockApp(view);
						return true;
					}

				});
	}

	protected void showSetupPSWDialog(final View view1) {
		Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dialog_setup_password, null);
		final EditText et_setup_psw = (EditText) view
				.findViewById(R.id.et_setup_psw);
		final EditText et_setup_confirm = (EditText) view
				.findViewById(R.id.et_setup_confirm);
		Button ok = (Button) view.findViewById(R.id.ok);
		Button cancel = (Button) view.findViewById(R.id.cancel);

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
				// 保存密码
				Editor editor = sp.edit();
				editor.putString("applockpassword", Md5Utils.getMd5Password(psw));
				editor.commit();
				Toast.makeText(getApplicationContext(), "密码设置成功",
						Toast.LENGTH_SHORT).show();
				lockApp(view1);
				setupDialog.dismiss();
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

	private void fillData() {
		ll_load_app.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				// 分组
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo info : appInfos) {
					if (info.isUserApp()) {
						// 如果是用户应用
						userAppInfos.add(info);
					} else {
						// 如果是系统应用
						systemAppInfos.add(info);
					}
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (adapter == null) {
							adapter = new MyAdapter();
							lv_app_manager.setAdapter(adapter);
							tv_show.setVisibility(View.VISIBLE);
						} else {
							adapter.notifyDataSetChanged();
						}
						tv_show.setText("用户程序" + userAppInfos.size() + "个");
						ll_load_app.setVisibility(View.INVISIBLE);
					}
				});
			};
		}.start();
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo info = null;
			if (position == 0) {
				// 显示TextView,告诉用户有多少个用户应用
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setText("用户程序" + userAppInfos.size() + "个");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position == (userAppInfos.size() + 1)) {
				// 显示TextView,告诉用户有多少个系统应用
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setText("系统程序" + systemAppInfos.size() + "个");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position <= userAppInfos.size()) {
				// 显示用户app
				info = userAppInfos.get(position - 1);
			} else if (position > userAppInfos.size()) {
				info = systemAppInfos.get(position - 1 - userAppInfos.size()
						- 1);
			}
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				// 当converView不为空的时候且converView 是相对布局的子类对象的时候才复用
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(AppManagerActivity.this,
						R.layout.list_item_appinfo, null);
				holder = new ViewHolder();
				holder.tv_app_name = (TextView) view
						.findViewById(R.id.tv_app_name);
				holder.tv_app_location = (TextView) view
						.findViewById(R.id.tv_app_location);
				holder.iv_app_icon = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				holder.iv_status = (ImageView) view
						.findViewById(R.id.iv_status);
				view.setTag(holder);
			}
			holder.tv_app_name.setText(info.getName());
			holder.iv_app_icon.setImageDrawable(info.getIcon());
			if (info.isRom()) {
				// 如果是装在手机内存里面
				holder.tv_app_location.setText("手机内存");
			} else {
				// 如果是装在手机sd卡上面
				holder.tv_app_location.setText("外部存储");
			}
			if (dao.find(info.getPackageName())) {
				// 如果是已经加锁
				holder.iv_status.setImageResource(R.drawable.lock);
			} else {
				// 如果没有加锁
				holder.iv_status.setImageResource(R.drawable.unlock);
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	private long getAvailSpace(String path) {
		StatFs statf = new StatFs(path);
		// 获取可用的分区个数
		long count = statf.getAvailableBlocks();
		// 获取每个分区的大小
		long size = statf.getBlockSize();
		return count * size;
	}

	private class ViewHolder {
		public TextView tv_app_name;
		public TextView tv_app_location;
		public ImageView iv_app_icon;
		public ImageView iv_status;
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 当销毁activity的时候,释放弹出窗体的资源
		dismissPopupWindow();
	}

	private void dismissPopupWindow() {
		if (popu != null && popu.isShowing()) {
			// 如果popu不等于null且正在显示中
			popu.dismiss();
			popu = null;
		}
	}

	@Override
	public void onClick(View v) {
		dismissPopupWindow();
		switch (v.getId()) {
		case R.id.ll_uninstall:// 卸载
			unInstallApplication();
			break;

		case R.id.ll_start:// 启动
			startApplication();
			break;

		case R.id.ll_share:// 分享
			shareApplication();
			break;
		}
	}

	private void shareApplication() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "推荐您使用一款软件,名字叫:" + info.getName());
		startActivity(intent);
	}

	private void unInstallApplication() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setAction(Intent.ACTION_DELETE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setData(Uri.parse("package:" + info.getPackageName()));
		startActivity(intent);
	}

	private void startApplication() {
		PackageManager pm = getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(info.getPackageName());
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(), "无法启动", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void lockApp(View view) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (dao.find(info.getPackageName())) {
			// 已经加锁,现在需要解锁
			dao.delete(info.getPackageName());
			holder.iv_status.setImageResource(R.drawable.unlock);
		} else {
			dao.add(info.getPackageName());
			holder.iv_status.setImageResource(R.drawable.lock);
		}
	}
}
