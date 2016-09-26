package com.jsako.mobilesafe;

import com.jsako.mobilesafe.db.dao.NumberAddressQueryUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberAddressQueryActivity extends Activity {
	private static final String TAG = "NumberAddressQueryActivity";
	private EditText et_phone;
	private TextView tv_result;
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address_query);
		et_phone = (EditText) findViewById(R.id.et_phone);
		tv_result = (TextView) findViewById(R.id.tv_result);
		vibrator=(Vibrator) getSystemService(VIBRATOR_SERVICE);
		et_phone.addTextChangedListener(new TextWatcher() {
			/**
			 * ���ı������仯��ʱ��ص�
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String phone =  s.toString();
				if(phone.length()>2){
					String address = NumberAddressQueryUtils.getAddress(phone);
					tv_result.setText(address);
				}
			}

			/**
			 * ���ı������仯ǰ�ص�
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			/**
			 * ���ı������仯��ص�
			 */
			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	/**
	 * ��ѯ���������
	 * 
	 * @param view
	 */
	public void numberAddressQuery(View view) {
		String phone = et_phone.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "����Ϊ��", Toast.LENGTH_SHORT).show();
			Animation anim=AnimationUtils.loadAnimation(this,R.anim.shake);
			et_phone.startAnimation(anim);
			vibrator.vibrate(1000);
			return;
		}
		String address = NumberAddressQueryUtils.getAddress(phone);
		tv_result.setText(address);
	}
}
