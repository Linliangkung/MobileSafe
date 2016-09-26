package com.jsako.mobilesafe;

import com.jsako.mobilesafe.utils.Md5Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EnterPSWActivity extends Activity{
	private TextView tv_name;
	private ImageView iv_icon;
	private EditText et_psw;
	private String packageName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_psw);
		tv_name=(TextView) findViewById(R.id.tv_name);
		iv_icon=(ImageView) findViewById(R.id.iv_icon);
		et_psw=(EditText) findViewById(R.id.et_psw);
		Intent intent=getIntent();
		packageName = intent.getStringExtra("packagename");
		PackageManager pm=getPackageManager();
		try {
			ApplicationInfo info=pm.getApplicationInfo(packageName,0);
			tv_name.setText(info.loadLabel(pm));
			iv_icon.setImageDrawable(info.loadIcon(pm));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void cancel(View view){
		backHome();
	}
	
	public void ok(View view){
		String password=et_psw.getText().toString().trim();
		SharedPreferences sp=getSharedPreferences("config",Context.MODE_PRIVATE);
		String configPsw=sp.getString("applockpassword", null);
		if(TextUtils.isEmpty(configPsw)){
			//说明尚未设置初始密码
			Toast.makeText(this,"初始密码尚未设置", Toast.LENGTH_SHORT).show();
			return ;
		}
		if(TextUtils.isEmpty(password)){
			Toast.makeText(this,"密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if(Md5Utils.getMd5Password(password).equals(configPsw)){
			//说明密码正确
			//发送一个广播,告诉看门狗暂时停止监控当前应用
			Intent intent =new Intent();
			intent.setAction("com.jsako.mobilesafe.tempstop");
			intent.putExtra("packagename",packageName);
			sendBroadcast(intent);
			finish();
		}else{
			//密码不正确
			Toast.makeText(this,"密码错误", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onBackPressed() {
		backHome();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}

	private void backHome() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
	}
}
