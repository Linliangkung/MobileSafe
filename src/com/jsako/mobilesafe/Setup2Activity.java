package com.jsako.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.jsako.mobilesafe.ui.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {
	private TelephonyManager tm;
	private SettingItemView siv_bind_simcard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		siv_bind_simcard = (SettingItemView) findViewById(R.id.siv_bind_simcard);
		String sim=sp.getString("simserialnumber", null);
		//设置siv_bind_simcard最初状态,如果以前设置过绑定sim卡,初始化为绑定sim卡状态,如果没有设置过,则初始化为没有绑定sim卡状态
		siv_bind_simcard.setChecked(!TextUtils.isEmpty(sim));
		siv_bind_simcard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Editor editor = sp.edit();
//				if (siv_bind_simcard.isChecked()) {
//					// 如果已经绑定,则解除绑定
//					siv_bind_simcard.setChecked(false);
//					editor.putString("simserialnumber", null);
//				} else {
//					// 如果没有绑定,则绑定
//					siv_bind_simcard.setChecked(true);
//					// 获取sim卡唯一序列号
//					String sim = tm.getSimSerialNumber();
//					// 保存sim卡信息
//					editor.putString("simserialnumber", sim);
//				}
//				editor.commit();
				siv_bind_simcard.setChecked(!siv_bind_simcard.isChecked());
			}
		});
	}

	@Override
	protected void showNext() {
		if(!siv_bind_simcard.isChecked()){
			//如果用户没有绑定sim卡,提示用户绑定sim卡,否则不能进入下一个页面
			Toast.makeText(this, "请绑定sim卡", Toast.LENGTH_SHORT).show();
			return ;
		}
		Editor editor = sp.edit();
		String sim = tm.getSimSerialNumber();
		// 保存sim卡信息
		editor.putString("simserialnumber", sim);
		editor.commit();
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}

	@Override
	protected void showPre() {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
}
