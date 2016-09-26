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
	// apk�����ص�ַ
	private String apkurl;
	private String versionName;
	private SharedPreferences sp;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ENTER_HOME:// ����������
				enterHome();
				break;
			case SHOW_UPDATE_DIALOG:// ��ʾ�����Ի���
				Log.i(TAG, description);
				showUpdateDialog();
				break;
			case CODE_ERROR:// �������
			case NETWORK_ERROR:
				enterHome();
				Toast.makeText(getApplicationContext(), "�������,��������",
						Toast.LENGTH_SHORT).show();
				break;
			case URL_ERROR:// URL����
				enterHome();
				Toast.makeText(getApplicationContext(), "URL����",
						Toast.LENGTH_SHORT).show();
				break;
			case JSON_ERROR:// JSON��������
				enterHome();
				Toast.makeText(getApplicationContext(), "JSON��������",
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
		builder.setTitle("��������");
		builder.setMessage(description);
		// builder.setCancelable(false);�û����鲻̫�ý��鲻ʹ��
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// ����������
				enterHome();
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("��������", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "��������");
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// sd���ڿ���
					// apkurl======http://10.50.6.121/MobileSafeServer/mobilesafe2.0.apk"
					String apkFileName = apkurl.substring(apkurl
							.lastIndexOf('/'));
					FinalHttp finalHttp = new FinalHttp();
					finalHttp.download(apkurl, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ apkFileName, new AjaxCallBack<File>() {
						// ���سɹ��ص��ĺ���
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

						// ����ʧ�ܻص��ĺ���
						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							Toast.makeText(getApplicationContext(), "����ʧ��",
									Toast.LENGTH_SHORT).show();
							enterHome();
							t.printStackTrace();
						}

						// �������ػص��ĺ���
						@Override
						public void onLoading(long count, long current) {
							super.onLoading(count, current);
							int progress = (int) (current * 100 / count);
							tv_splash_progress.setText("���ؽ���" + progress + "%");
						}
					});
				} else {
					// sdж����
					Toast.makeText(getApplicationContext(), "����ʧ��,sd��������",
							Toast.LENGTH_LONG).show();
					enterHome();
					return;
				}
			}
		});
		builder.setNegativeButton("�´���˵", new OnClickListener() {
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
			tv_splash_version.setText("�汾��:" + versionName);
		}
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean checked = sp.getBoolean("update", false);
		//�������ͼ��
		installShortcut();
		// ��������������ݿ⵼��/data/data/����/files��Ŀ¼��
		copyDb("address.db");
		copyDb("antivirus.db");
		if (checked) {
			// ����û������˿����Զ�����
			// �������
			checkUpdate();
		} else {
			// �û��ر����Զ�����
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					enterHome();
				}
			}, 2000);
		}
		// ��������Ч���Ķ���
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(1000);
		findViewById(R.id.rl_root_splash).startAnimation(aa);

		// ��ȡ��ʾ���ȵ�TextView
		tv_splash_progress = (TextView) findViewById(R.id.tv_splash_progress);
	}

	private void installShortcut() {
		boolean shortcut=sp.getBoolean("shortcut", false);
		if(shortcut){
			//˵���Ѿ���������
			return;
		}
		Intent intent=new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		//��Ҫ������ ͼ��  ��ʲô����
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher));
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"�ֻ�С��ʿ");
		Intent shortcutIntent=new Intent();
		shortcutIntent.setClassName(getPackageName(),"com.jsako.mobilesafe.SplashActivity");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		sendBroadcast(intent);
		Editor editor=sp.edit();
		editor.putBoolean("shortcut",true);
		editor.commit();
	}

	private void copyDb(String filename) {
		 //�ж�address.db�Ƿ�������м�¼
		 File address=new File(getFilesDir(),filename);
		 if(address.exists()&&address.length()>0){
		 //�������
		 Log.i(TAG,"���ݿ��Ѿ�������,����Ҫ������");
		 Log.i(TAG,getClass().getClassLoader().getResource("assets/"+filename).getPath());
		 //������д��getClass().getResource("/assets/address.db").getPath()��getClass().getClassLoader().getResource("assets/address.db").getPath()
		 }else{
		 //���������,�������ݿ�
		 try {
		//Ϊ�˼��ݵͰ汾,����������д��������ʽ,������getAssets().open(filename)������ʽ
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
	 * ����Ƿ����°汾,����о�����
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
					// ����������Ӧ��ʱʱ��Ϊ10��
					conn.setConnectTimeout(10000);
					// ���ö�ȡ��ʱʱ��Ϊ5��
					conn.setReadTimeout(5000);
					// ��������ķ���ΪGET����
					conn.setRequestMethod("GET");
					// ��ȡ������Ӧ��(���и÷���Ĭ��ִ��conn.connection())
					int code = conn.getResponseCode();
					if (code == 200) {
						// ����ɹ�
						InputStream in = conn.getInputStream();
						String data = StreamTools.readFromStream(in);
						Log.i(TAG, "�����ɹ�:" + data);
						// json����
						JSONObject dataJson = new JSONObject(data);
						// �������İ汾��Ϣ
						String version = dataJson.getString("version");
						description = dataJson.getString("description");
						apkurl = dataJson.getString("apkurl");
						// У���Ƿ����°汾
						if (versionName != null && versionName.equals(version)) {
							// �汾һ��,û���°汾
							msg.what = ENTER_HOME;
						} else {
							// ���°汾Ҫ������,����һ�������Ի���
							msg.what = SHOW_UPDATE_DIALOG;
						}
					} else {
						Log.i(TAG, "��Ӧ��:" + code);
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
					// ִ�����������õ�ʱ��
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
	 * ������ȡ��ǰapp�İ汾��Ϣ
	 * 
	 * @return �汾��Ϣ
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
