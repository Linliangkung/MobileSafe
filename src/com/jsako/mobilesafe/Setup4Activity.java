package com.jsako.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {
	private CheckBox cb_proteting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		cb_proteting=(CheckBox) findViewById(R.id.cb_proteting);
		boolean protecting=sp.getBoolean("protecting", false);
		cb_proteting.setChecked(protecting);
		cb_proteting.setText(protecting?"您已经开启防盗保护":"您没有开启防盗保护");
		cb_proteting.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				cb_proteting.setText(isChecked?"您已经开启防盗保护":"您没有开启防盗保护");
				//保存是否勾选开启手机防盗功能
				Editor editor=sp.edit();
				editor.putBoolean("protecting", isChecked);
				editor.commit();
			}
		});
	}

	@Override
	protected void showNext() {
		Editor editor=sp.edit();
		editor.putBoolean("configed",true);
		editor.commit();
		Intent intent=new Intent(this,LostFindActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_next_in,R.anim.tran_next_out );
	}

	@Override
	protected void showPre() {
		Intent intent=new Intent(this,Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in,R.anim.tran_pre_out );
	}
}
