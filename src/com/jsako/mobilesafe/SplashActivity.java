package com.jsako.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.jsako.mobilesafe.utils.StreamTools;

public class SplashActivity extends Activity {

	private static final String TAG = "SplashActivity";
	protected static final int ENTER_HOME = 0;
	protected static final int SHOW_UPDATE_DIALOG = 1;
	protected static final int CODE_ERROR = 2;
	protected static final int URL_ERROR = 3;
	protected static final int NETWORK_ERROR = 4;
	protected static final int JSON_ERROR = 5;
	private TextView tv_splash_version;
	private String description;
	private TextView tv_splash_progress;
	// apk的下载地址
	private String apkurl;
	private String versionName;
	private SharedPreferences sp;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ENTER_HOME:// 进入主界面
				enterHome();
				break;
			case SHOW_UPDATE_DIALOG:// 显示升级对话框
				Log.i(TAG, description);
				showUpdateDialog();
				break;
			case CODE_ERROR:// 网络错误
			case NETWORK_ERROR:
				enterHome();
				Toast.makeText(getApplicationContext(), "网络出错,请检查网络",
						Toast.LENGTH_SHORT).show();
				break;
			case URL_ERROR:// URL错误
				enterHome();
				Toast.makeText(getApplicationContext(), "URL错误",
						Toast.LENGTH_SHORT).show();
				break;
			case JSON_ERROR:// JSON解析错误
				enterHome();
				Toast.makeText(getApplicationContext(), "JSON解析错误",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	};

	private void showUpdateDialog() {
		Builder builder = new Builder(this);
		builder.setTitle("提醒升级");
		builder.setMessage(description);
		// builder.setCancelable(false);用户体验不太好建议不使用
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// 进入主界面
				enterHome();
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("立刻升级", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "立刻升级");
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// sd存在可用
					// apkurl======http://10.50.6.121/MobileSafeServer/mobilesafe2.0.apk"
					String apkFileName = apkurl.substring(apkurl
							.lastIndexOf('/'));
					FinalHttp finalHttp = new FinalHttp();
					finalHttp.download(apkurl, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ apkFileName, new AjaxCallBack<File>() {
						// 下载成功回调的函数
						@Override
						public void onSuccess(File t) {
							super.onSuccess(t);
							intallApk(t);
							finish();
						}

						private void intallApk(File t) {
							Intent intent = new Intent();
							intent.setAction(Intent.ACTION_VIEW);
							intent.addCategory(Intent.CATEGORY_DEFAULT);
							intent.setDataAndType(Uri.fromFile(t),
									"application/vnd.android.package-archive");
							startActivity(intent);
						}

						// 下载失败回调的函数
						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							Toast.makeText(getApplicationContext(), "下载失败",
									Toast.LENGTH_SHORT).show();
							enterHome();
							t.printStackTrace();
						}

						// 正在下载回调的函数
						@Override
						public void onLoading(long count, long current) {
							super.onLoading(count, current);
							int progress = (int) (current * 100 / count);
							tv_splash_progress.setText("下载进度" + progress + "%");
						}
					});
				} else {
					// sd卸载了
					Toast.makeText(getApplicationContext(), "下载失败,sd卡不存在",
							Toast.LENGTH_LONG).show();
					enterHome();
					return;
				}
			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterHome();
			}
		});
		builder.create().show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		versionName = getVersionName();
		if (!TextUtils.isEmpty(versionName)) {
			tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
			tv_splash_version.setText("版本号:" + versionName);
		}
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean checked = sp.getBoolean("update", false);
		//创建快捷图标
		installShortcut();
		// 将号码归属地数据库导入/data/data/报名/files的目录下
		copyDb("address.db");
		copyDb("antivirus.db");
		if (checked) {
			// 如果用户设置了开启自动升级
			// 检查升级
			checkUpdate();
		} else {
			// 用户关闭了自动升级
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					enterHome();
				}
			}, 2000);
		}
		// 设置朦胧效果的动画
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(1000);
		findViewById(R.id.rl_root_splash).startAnimation(aa);

		// 获取显示进度的TextView
		tv_splash_progress = (TextView) findViewById(R.id.tv_splash_progress);
	}

	private void installShortcut() {
		boolean shortcut=sp.getBoolean("shortcut", false);
		if(shortcut){
			//说明已经创建过了
			return;
		}
		Intent intent=new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		//需要有名称 图标  干什么事情
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher));
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"手机小卫士");
		Intent shortcutIntent=new Intent();
		shortcutIntent.setClassName(getPackageName(),"com.jsako.mobilesafe.SplashActivity");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		sendBroadcast(intent);
		Editor editor=sp.edit();
		editor.putBoolean("shortcut",true);
		editor.commit();
	}

	private void copyDb(String filename) {
		 //判断address.db是否存在且有记录
		 File address=new File(getFilesDir(),filename);
		 if(address.exists()&&address.length()>0){
		 //如果存在
		 Log.i(TAG,"数据库已经存在了,不需要拷贝了");
		 Log.i(TAG,getClass().getClassLoader().getResource("assets/"+filename).getPath());
		 //有两种写法getClass().getResource("/assets/address.db").getPath()和getClass().getClassLoader().getResource("assets/address.db").getPath()
		 }else{
		 //如果不存在,拷贝数据库
		 try {
		//为了兼容低版本,输入流必须写成这种形式,而不是getAssets().open(filename)这种形式
		 InputStream in=getClass().getResourceAsStream("/assets/"+filename);
		 FileOutputStream fos=new FileOutputStream(address);
		 int len=0;
		 byte[] buf=new byte[1024];
				while ((len = in.read(buf)) != -1) {
					fos.write(buf, 0, len);
				}
		
		 in.close();
		 fos.close();
		 } catch (IOException e) {
		 e.printStackTrace();
		 }
		 }
	}

	/**
	 * 检查是否有新版本,如果有就升级
	 */
	private void checkUpdate() {
		new Thread() {
			public void run() {
				Long startTime = System.currentTimeMillis();
				Message msg = Message.obtain();
				try {
					// URL=http://10.50.6.121/MobileSafeServer/updateinfo.json
					URL url = new URL(getString(R.string.serverurl));
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					// 设置连接响应超时时间为10秒
					conn.setConnectTimeout(10000);
					// 设置读取超时时间为5秒
					conn.setReadTimeout(5000);
					// 设置请求的方法为GET请求
					conn.setRequestMethod("GET");
					// 获取请求响应码(其中该方法默认执行conn.connection())
					int code = conn.getResponseCode();
					if (code == 200) {
						// 请求成功
						InputStream in = conn.getInputStream();
						String data = StreamTools.readFromStream(in);
						Log.i(TAG, "联网成功:" + data);
						// json解析
						JSONObject dataJson = new JSONObject(data);
						// 服务器的版本信息
						String version = dataJson.getString("version");
						description = dataJson.getString("description");
						apkurl = dataJson.getString("apkurl");
						// 校验是否有新版本
						if (versionName != null && versionName.equals(version)) {
							// 版本一致,没有新版本
							msg.what = ENTER_HOME;
						} else {
							// 有新版本要下载了,弹出一个升级对话框
							msg.what = SHOW_UPDATE_DIALOG;
						}
					} else {
						Log.i(TAG, "响应码:" + code);
						msg.what = CODE_ERROR;
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					msg.what = URL_ERROR;
				} catch (ProtocolException e) {
					e.printStackTrace();
					msg.what = NETWORK_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					msg.what = NETWORK_ERROR;
				} catch (JSONException e) {
					e.printStackTrace();
					msg.what = JSON_ERROR;
				} finally {
					Long endTime = System.currentTimeMillis();
					// 执行网络请求用的时间
					Long dTime = endTime - startTime;
					if (dTime < 3000) {
						SystemClock.sleep(3000 - dTime);
					}
					handler.sendMessage(msg);
				}
			};
		}.start();
	}

	/**
	 * 用来获取当前app的版本信息
	 * 
	 * @return 版本信息
	 */
	private String getVersionName() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
