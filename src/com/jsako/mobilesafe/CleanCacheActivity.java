package com.jsako.mobilesafe;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jsako.mobilesafe.domain.CacheInfo;

@SuppressLint("NewApi") public class CleanCacheActivity extends Activity {
	public static final int SCANING = 1;
	public static final int FINISH = 2;
	public static final int SHOW_CACHE_DATA = 3;
	private Button btn_cleanAll;
	private ProgressBar pb_clean_cache;
	private TextView tv_clean_cache_status;
	private ListView lv_clean_cache;
	private PackageManager pm;
	private List<CacheInfo> cacheInfos;
	private CleanCacheAdapter adapter;
	private TextView tv_show_noinfo;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SCANING:
				// 当前正在扫描,修改TextView的显示
				String name = (String) msg.obj;
				tv_clean_cache_status.setText("正在扫描:" + name);
				break;
			case SHOW_CACHE_DATA:
				// 显示数据,将数据保存到集合
				CacheInfo cacheInfo = (CacheInfo) msg.obj;
				cacheInfos.add(0, cacheInfo);
				if (adapter == null) {
					adapter = new CleanCacheAdapter();
					lv_clean_cache.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
				tv_show_noinfo.setVisibility(View.INVISIBLE);
				break;
			case FINISH:
				// 扫描完成
				tv_clean_cache_status.setText("扫描完毕");
				btn_cleanAll.setEnabled(true);
			}
		};
	};
	// 定义当前系统的api版本
	private int apiLevel;
	private int userId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);
		btn_cleanAll = (Button) findViewById(R.id.btn_cleanAll);
		pb_clean_cache = (ProgressBar) findViewById(R.id.pb_clean_cache);
		tv_clean_cache_status = (TextView) findViewById(R.id.tv_clean_cache_status);
		lv_clean_cache = (ListView) findViewById(R.id.lv_clean_cache);
		tv_show_noinfo = (TextView) findViewById(R.id.tv_show_noinfo);
		pm = getPackageManager();
		apiLevel = Build.VERSION.SDK_INT;
		cacheInfos = new ArrayList<CacheInfo>();
		scanCache();
		
		if (apiLevel >= 17) {
			try{
			Method myUserIdMethod = UserHandle.class.getMethod(
					"myUserId", null);
			userId = (Integer) myUserIdMethod
					.invoke(null, null);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private void scanCache() {
		new Thread() {
			public void run() {
				Method getPackageSizeInfoMethod = null;
				Method[] methods = PackageManager.class.getMethods();
				for (Method method : methods) {
					if ("getPackageSizeInfo".equals(method.getName())) {
						getPackageSizeInfoMethod = method;
					}
				}

				List<ApplicationInfo> appInfos = pm.getInstalledApplications(0);
				pb_clean_cache.setMax(appInfos.size());
				int progress = 0;
				for (ApplicationInfo appInfo : appInfos) {
					String packageName = appInfo.packageName;
					try {
						if (apiLevel >= 17) {
							getPackageSizeInfoMethod.invoke(pm, packageName,
									userId, new MyDataObserver());
						} else {
							getPackageSizeInfoMethod.invoke(pm, packageName,
									new MyDataObserver());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					Message msg = Message.obtain();
					msg.what = SCANING;
					msg.obj = appInfo.loadLabel(pm).toString();
					handler.sendMessage(msg);
					progress++;
					pb_clean_cache.setProgress(progress);
					SystemClock.sleep(50);
				}

				Message msg = Message.obtain();
				msg.what = FINISH;
				handler.sendMessage(msg);
			}
		}.start();
	}

	private class MyDataObserver extends IPackageStatsObserver.Stub {

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			long cacheSize = pStats.cacheSize;
			String packageName = pStats.packageName;
			String name = null;
			try {
				name = pm.getApplicationInfo(pStats.packageName, 0)
						.loadLabel(pm).toString();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			if (cacheSize > 0) {
				CacheInfo cacheInfo = new CacheInfo();
				cacheInfo.setCacheSize(cacheSize);
				cacheInfo.setName(name);
				cacheInfo.setPackageName(packageName);
				Message msg = Message.obtain();
				msg.what = SHOW_CACHE_DATA;
				msg.obj = cacheInfo;
				handler.sendMessage(msg);
			}
		}

	}

	private class CleanCacheAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return cacheInfos.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView != null) {
				// 复用缓存
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				// 创建View
				view = View.inflate(CleanCacheActivity.this,
						R.layout.list_item_clean_cache, null);
				holder = new ViewHolder();
				holder.tv_app_name = (TextView) view
						.findViewById(R.id.tv_app_name);
				holder.tv_cache_size = (TextView) view
						.findViewById(R.id.tv_cache_size);
				holder.iv_clean = (ImageView) view.findViewById(R.id.iv_clean);
				view.setTag(holder);
			}
			final CacheInfo cacheInfo = cacheInfos.get(position);
			holder.tv_app_name.setText(cacheInfo.getName());
			holder.tv_cache_size.setText("缓存大小:"
					+ Formatter.formatFileSize(CleanCacheActivity.this,
							cacheInfo.CacheSize));
			holder.iv_clean.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 清除缓存
					System.out.println("清除缓存:" + cacheInfo.getName());
					try {
						Method method = PackageManager.class.getMethod(
								"deleteApplicationCacheFiles", String.class,
								IPackageDataObserver.class);
						method.invoke(pm, cacheInfo.getName(),
								new IPackageDataObserver.Stub() {
									@Override
									public void onRemoveCompleted(
											String packageName,
											boolean succeeded)
											throws RemoteException {
									}
								});
					} catch (Exception e) {
						e.printStackTrace();
						Builder builder = new Builder(CleanCacheActivity.this);
						builder.setTitle("提醒");
						builder.setMessage("将跳到系统设置页面,点击\"清除缓存\"清除软件缓存");
						builder.setNegativeButton("取消", null);
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										goToSystemSetting(cacheInfo);
									}
								});
						builder.show();
					}
				}
			});
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

	public class ViewHolder {
		public TextView tv_app_name;
		public TextView tv_cache_size;
		public ImageView iv_clean;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			startAgain();
			break;
		}
	}

	private void startAgain() {
		if (cacheInfos != null && adapter != null) {
			cacheInfos.clear();
			adapter.notifyDataSetChanged();
		}
		btn_cleanAll.setEnabled(false);
		tv_show_noinfo.setVisibility(View.VISIBLE);
		scanCache();
	}

	private void goToSystemSetting(final CacheInfo cacheInfo) {
		Intent intent = new Intent();
		System.out.println(apiLevel);
		if (apiLevel >= 9) {
			intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setData(Uri.parse("package:" + cacheInfo.getPackageName()));
		} else {
			intent.putExtra("pkg", cacheInfo.getPackageName());
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName("com.android.settings",
					"com.android.settings.InstalledAppDetails");
		}
		startActivityForResult(intent, 0);
	}

	public void cleanAll(View view) {
		if (cacheInfos.size() == 0) {
			Toast.makeText(getApplicationContext(), "您的手机已经很干净了,暂时无垃圾需要清理",
					Toast.LENGTH_SHORT).show();
			return;
		}
		Method[] methods = PackageManager.class.getMethods();
		for (Method method : methods) {
			if ("freeStorageAndNotify".equals(method.getName())) {
				try {
					method.invoke(pm, Integer.MAX_VALUE,
							new IPackageDataObserver.Stub() {
								@Override
								public void onRemoveCompleted(
										String packageName, boolean succeeded)
										throws RemoteException {
									System.out.println(succeeded);
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
				}
				Toast.makeText(getApplicationContext(), "清理成功,您的手机快得飞起了",
						Toast.LENGTH_SHORT).show();
				startAgain();
				return;
			}
		}
	}
}
