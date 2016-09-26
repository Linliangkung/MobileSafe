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
	 * �����û�Ӧ�õļ���
	 */
	private List<AppInfo> userAppInfos;
	/**
	 * ����ϵͳӦ�õļ���
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
	 * ����
	 */
	private LinearLayout ll_share;
	/**
	 * ж��
	 */
	private LinearLayout ll_uninstall;
	/**
	 * ����
	 */
	private LinearLayout ll_start;

	private AppInfo info;

	// ����applock���ݵ�dao
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
		Log.i(TAG, "�ڲ��洢��·��:"
				+ Environment.getDataDirectory().getAbsolutePath());
		Log.i(TAG, "sd���洢��·��:"
				+ Environment.getExternalStorageDirectory().getAbsolutePath());
		long rom = getAvailSpace(Environment.getDataDirectory()
				.getAbsolutePath());
		long sd = getAvailSpace(Environment.getExternalStorageDirectory()
				.getAbsolutePath());

		tv_avail_rom.setText("�ڴ����:" + Formatter.formatFileSize(this, rom));
		tv_avail_sd.setText("sd������:" + Formatter.formatFileSize(this, sd));
		/**
		 * ����listview�����¼��ļ���
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
						tv_show.setText("ϵͳ����" + systemAppInfos.size() + "��");
					} else {
						tv_show.setText("�û�����" + userAppInfos.size() + "��");
					}
					dismissPopupWindow();
				}

			}
		});

		/**
		 * ����listview ��item��������¼��ļ���
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
				// �����Ĳ���ǰ����,��������б�����ɫ
				popu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				int[] location = new int[2];
				view.getLocationInWindow(location);

				// ��ȡappͼƬ�Ĵ�С
				ImageView iv_app_info = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				int width = iv_app_info.getWidth();
				popu.showAtLocation(parent, Gravity.TOP | Gravity.LEFT, width,
						location[1]);

				// ���ö���
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
							// ˵��û�����ù�����,�����Ի�����������
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
				// ��������
				Editor editor = sp.edit();
				editor.putString("applockpassword", Md5Utils.getMd5Password(psw));
				editor.commit();
				Toast.makeText(getApplicationContext(), "�������óɹ�",
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
				// ����
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo info : appInfos) {
					if (info.isUserApp()) {
						// ������û�Ӧ��
						userAppInfos.add(info);
					} else {
						// �����ϵͳӦ��
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
						tv_show.setText("�û�����" + userAppInfos.size() + "��");
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
				// ��ʾTextView,�����û��ж��ٸ��û�Ӧ��
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setText("�û�����" + userAppInfos.size() + "��");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position == (userAppInfos.size() + 1)) {
				// ��ʾTextView,�����û��ж��ٸ�ϵͳӦ��
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setText("ϵͳ����" + systemAppInfos.size() + "��");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			} else if (position <= userAppInfos.size()) {
				// ��ʾ�û�app
				info = userAppInfos.get(position - 1);
			} else if (position > userAppInfos.size()) {
				info = systemAppInfos.get(position - 1 - userAppInfos.size()
						- 1);
			}
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				// ��converView��Ϊ�յ�ʱ����converView ����Բ��ֵ���������ʱ��Ÿ���
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
				// �����װ���ֻ��ڴ�����
				holder.tv_app_location.setText("�ֻ��ڴ�");
			} else {
				// �����װ���ֻ�sd������
				holder.tv_app_location.setText("�ⲿ�洢");
			}
			if (dao.find(info.getPackageName())) {
				// ������Ѿ�����
				holder.iv_status.setImageResource(R.drawable.lock);
			} else {
				// ���û�м���
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
		// ��ȡ���õķ�������
		long count = statf.getAvailableBlocks();
		// ��ȡÿ�������Ĵ�С
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
		// ������activity��ʱ��,�ͷŵ����������Դ
		dismissPopupWindow();
	}

	private void dismissPopupWindow() {
		if (popu != null && popu.isShowing()) {
			// ���popu������null��������ʾ��
			popu.dismiss();
			popu = null;
		}
	}

	@Override
	public void onClick(View v) {
		dismissPopupWindow();
		switch (v.getId()) {
		case R.id.ll_uninstall:// ж��
			unInstallApplication();
			break;

		case R.id.ll_start:// ����
			startApplication();
			break;

		case R.id.ll_share:// ����
			shareApplication();
			break;
		}
	}

	private void shareApplication() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "�Ƽ���ʹ��һ�����,���ֽ�:" + info.getName());
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
			Toast.makeText(getApplicationContext(), "�޷�����", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void lockApp(View view) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (dao.find(info.getPackageName())) {
			// �Ѿ�����,������Ҫ����
			dao.delete(info.getPackageName());
			holder.iv_status.setImageResource(R.drawable.unlock);
		} else {
			dao.add(info.getPackageName());
			holder.iv_status.setImageResource(R.drawable.lock);
		}
	}
}
