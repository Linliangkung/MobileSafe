package com.jsako.mobilesafe;

import android.content.Intent;
import android.os.Bundle;

public class Setup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}

	protected void showPre() {

	}

	protected void showNext() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		// �˷���������finish��startactivity֮�����
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}
}
