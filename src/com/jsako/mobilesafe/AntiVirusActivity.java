package com.jsako.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jsako.mobilesafe.db.dao.AntiVirusDao;
import com.jsako.mobilesafe.domain.ScanInfo;
import com.jsako.mobilesafe.utils.Md5Utils;

public class AntiVirusActivity extends Activity {
	protected static final int SCANING = 1;
	protected static final int FINISH = 2;
	private ImageView iv_scan;
	private ProgressBar anti_virus_pb;
	private PackageManager pm;
	private TextView tv_scan_status;
	private LinearLayout ll_result;
	private List<ScanInfo> scanInfos;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCANING:
				// 正在扫描
				ScanInfo scanInfo = (ScanInfo) msg.obj;
				tv_scan_status.setText("正在扫描:" + scanInfo.getName());
				TextView tv = new TextView(AntiVirusActivity.this);
				if (scanInfo.isVirus()) {
					// 是病毒
					tv.setTextColor(Color.RED);
					tv.setText("发现病毒:" + scanInfo.getName());
				} else {
					// 不是病毒
					tv.setTextColor(Color.BLACK);
					tv.setText("扫描安全:" + scanInfo.getName());
				}
				ll_result.addView(tv, 0);
				break;
			case FINISH:
				// 扫描完成
				iv_scan.clearAnimation();
				int size = scanInfos.size();
				if (size == 0) {
					tv_scan_status.setText("扫描完毕,未发现病毒");
					Toast.makeText(AntiVirusActivity.this, "你的手机已经很安全,请放心使用",
							Toast.LENGTH_SHORT).show();
				} else {
					tv_scan_status.setText("扫描完毕,发现病毒" + size + "个病毒");
					// 当发现病毒后删除病毒
					Builder builder = new Builder(AntiVirusActivity.this);
					builder.setTitle("警告");
					builder.setMessage("发现严重病毒,您的手机评分为0,请立刻查杀病毒");
					builder.setNegativeButton("下次再说", null);
					builder.setPositiveButton("立即查杀", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							for(ScanInfo scanInfo:scanInfos)
							{
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_VIEW);
								intent.setAction(Intent.ACTION_DELETE);
								intent.addCategory(Intent.CATEGORY_DEFAULT);
								intent.setData(Uri.parse("package:"
										+ scanInfo.getPackageName()));
								startActivityForResult(intent, 0);
								dialog.dismiss();
							}
						}
					});
					builder.show();
				}
				break;
			}
		}
	};
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case 0:
			anti_virus_pb.setProgress(0);
			startAnimation();
			ll_result.removeAllViews();
			scanVirus();
			break;
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		anti_virus_pb = (ProgressBar) findViewById(R.id.anti_virus_pb);
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		ll_result = (LinearLayout) findViewById(R.id.ll_result);
		pm = getPackageManager();
		startAnimation();
		scanVirus();
	}

	private void scanVirus() {
		tv_scan_status.setText("正在初始化杀毒引擎");
		new Thread() {
			public void run() {
				List<ApplicationInfo> appInfos = pm.getInstalledApplications(0);
				SystemClock.sleep(500);
				int size = appInfos.size();
				anti_virus_pb.setMax(size);
				int progress = 0;
				ScanInfo scanInfo;
				scanInfos = new ArrayList<ScanInfo>();
				for (ApplicationInfo appInfo : appInfos) {
					scanInfo = new ScanInfo();
					String sourceDir = appInfo.sourceDir;
					String md5 = Md5Utils.getFileMd5(sourceDir);
					String name = appInfo.loadLabel(pm).toString();
					String packageName = appInfo.packageName;
					scanInfo.setName(name);
					scanInfo.setPackageName(packageName);
					if (AntiVirusDao.isVirus(md5)) {
						// 是病毒
						scanInfo.setVirus(true);
						scanInfos.add(scanInfo);
					} else {
						// 不是病毒
						scanInfo.setVirus(false);
					}
					Message msg = Message.obtain();
					msg.what = SCANING;
					msg.obj = scanInfo;
					handler.sendMessage(msg);
					progress++;
					anti_virus_pb.setProgress(progress);
				}
				Message msg = Message.obtain();
				msg.what = FINISH;
				handler.sendMessage(msg);
			};
		}.start();
	}

	private void startAnimation() {
		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setRepeatCount(Animation.INFINITE);
		ra.setDuration(1000);
		ra.setRepeatMode(Animation.REVERSE);
		iv_scan.startAnimation(ra);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();	
	}
}
