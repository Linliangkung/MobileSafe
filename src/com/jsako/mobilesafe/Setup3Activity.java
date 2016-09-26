package com.jsako.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity {
	private EditText et_contact_set;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et_contact_set = (EditText) findViewById(R.id.et_contact_set);
		et_contact_set.setText(sp.getString("safenumber", ""));
	}

	@Override
	protected void showNext() {
		//�жϰ�ȫ�����������Ƿ�Ϊ��
		String safenumber=et_contact_set.getText().toString().trim();
		if(TextUtils.isEmpty(safenumber)){
			Toast.makeText(this,"�����ð�ȫ����", Toast.LENGTH_SHORT).show();
			return;
		}
		//�����ȫ���벻Ϊ��,�򱣴氲ȫ����
		Editor editor=sp.edit();
		editor.putString("safenumber", safenumber);
		editor.commit();
		Intent intent = new Intent(this, Setup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}

	@Override
	protected void showPre() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}

	public void selectContact(View view) {
		Intent intent = new Intent(this, SelectContactActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0:
			if (resultCode == 0) {
				et_contact_set.setText(data.getStringExtra("telephone").trim().replace("-", "").replace(" ",""));
			}
			break;
		}
	}
}
