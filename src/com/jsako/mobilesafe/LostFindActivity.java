package com.jsako.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {
	private static final String TAG = "LostFindActivity";
	private SharedPreferences sp;
	private TextView tv_safenum;
	private ImageView iv_protecting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean isSetUp = sp.getBoolean("configed", false);
		if (isSetUp) {
			// �Ѿ�����������,�����ֻ�����ҳ��
			setContentView(R.layout.activity_lost_find);
		} else {
			// û����������,���������򵼽���
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			finish();
			return;
		}
		tv_safenum = (TextView) findViewById(R.id.tv_safenum);
		iv_protecting = (ImageView) findViewById(R.id.iv_protecting);
		tv_safenum.setText(sp.getString("safenumber", "δ����"));
		boolean protecting = sp.getBoolean("protecting", false);
		if (protecting)
			iv_protecting.setImageResource(R.drawable.lock);
		else
			iv_protecting.setImageResource(R.drawable.unlock);
	}

	public void reEnterSetup(View view) {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
	}
}
