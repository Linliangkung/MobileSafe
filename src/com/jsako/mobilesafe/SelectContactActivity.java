package com.jsako.mobilesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectContactActivity extends Activity {
	private static final String TAG = "SelectContactActivity";
	private ListView list_select_contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		final List<Map<String, String>> data = getContactInfo();
		// for (Map<String, String> map : data) {
		// for (Map.Entry<String, String> entry : map.entrySet()) {
		// String key = entry.getKey();
		// String value = entry.getValue();
		// Log.i(TAG, "key:" + key);
		// Log.i(TAG, "value:" + value);
		// }
		// }
		Log.i(TAG, "联系人个数:" + data.size());
		list_select_contact = (ListView) findViewById(R.id.list_select_contact);
		list_select_contact.setAdapter(new SimpleAdapter(this, data,
				R.layout.contact_item_view,
				new String[] { "name", "telephone" }, new int[] {
						R.id.tv_contact_name, R.id.tv_contact_telephone }));
		list_select_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent=new Intent();
				Map<String,String> map=data.get(position);
				intent.putExtra("telephone", map.get("telephone"));
				setResult(0, intent);
				finish();
			}
		});
	}

	private List<Map<String, String>> getContactInfo() {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		// 获取一个内容提供者解析器
		ContentResolver resolver = getContentResolver();
		Uri contactUri = Uri
				.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");
		Cursor contactCursor = resolver.query(contactUri,
				new String[] { "contact_id" }, null, null, null);
		// 定义一个map用来保存,每个联系人的信息
		Map<String, String> contact;
		if (contactCursor != null && contactCursor.getCount() > 0) {
			while (contactCursor.moveToNext()) {
				// 获取到每个联系人的contact id
				String contact_id = contactCursor.getString(0);
				if (!TextUtils.isEmpty(contact_id)) {
					// 如果获取的当前联系人id部位空的话,查询data表
					Cursor dataCursor = resolver.query(dataUri, new String[] {
							"data1", "mimetype" }, "contact_id=?",
							new String[] { contact_id }, null);
					if (dataCursor != null && dataCursor.getCount() > 0) {
						contact = new HashMap<String, String>();
						while (dataCursor.moveToNext()) {
							String data1 = dataCursor.getString(0);
							String mimetype = dataCursor.getString(1);
							if ("vnd.android.cursor.item/name".equals(mimetype)) {
								// 如果mimetype是vnd.android.cursor.item/name的话,说明data1是一个名字
								contact.put("name", data1);
							} else if ("vnd.android.cursor.item/phone_v2"
									.equals(mimetype)) {
								// 如果mimetype是vnd.android.cursor.item/phone_v2的话,说明data1是一个电话号码
								contact.put("telephone", data1);
							}
						}
						data.add(contact);
						dataCursor.close();
					}
				}
			}
			contactCursor.close();
		}
		return data;
	}
	@Override
	public void onBackPressed() {
		Intent intent=new Intent();
		intent.putExtra("telephone","");
		setResult(0, intent);
		super.onBackPressed();
	}
}
