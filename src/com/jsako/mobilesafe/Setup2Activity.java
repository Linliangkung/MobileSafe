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
		//����siv_bind_simcard���״̬,�����ǰ���ù���sim��,��ʼ��Ϊ��sim��״̬,���û�����ù�,���ʼ��Ϊû�а�sim��״̬
		siv_bind_simcard.setChecked(!TextUtils.isEmpty(sim));
		siv_bind_simcard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Editor editor = sp.edit();
//				if (siv_bind_simcard.isChecked()) {
//					// ����Ѿ���,������
//					siv_bind_simcard.setChecked(false);
//					editor.putString("simserialnumber", null);
//				} else {
//					// ���û�а�,���
//					siv_bind_simcard.setChecked(true);
//					// ��ȡsim��Ψһ���к�
//					String sim = tm.getSimSerialNumber();
//					// ����sim����Ϣ
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
			//����û�û�а�sim��,��ʾ�û���sim��,�����ܽ�����һ��ҳ��
			Toast.makeText(this, "���sim��", Toast.LENGTH_SHORT).show();
			return ;
		}
		Editor editor = sp.edit();
		String sim = tm.getSimSerialNumber();
		// ����sim����Ϣ
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
