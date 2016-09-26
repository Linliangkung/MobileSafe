package com.jsako.mobilesafe.ui;

import com.jsako.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * �Զ���һ���ռ�,��������TextVeiw��һ��CheakBox
 * 
 * @author JsAko
 * 
 */
public class SettingClickView extends RelativeLayout {
	private TextView tv_title;
	private TextView tv_desc;

	private void initView(Context context) {
		// �������ļ�ת����View����,�������ø�View�ĸ���ΪSettingItemView
		View.inflate(context, R.layout.setting_click_view, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
	}

	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		String itemtitle=attrs.getAttributeValue("http://schemas.android.com/apk/res/com.jsako.mobilesafe", "itemtitle");
		tv_title.setText(itemtitle);
	}

	public SettingClickView(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * �����Զ���Ͽؼ���������Ϣtextview��ǰ����Ϣ
	 * @param desc ������Ϣ
	 */
	public void setDesc(String desc) {
		tv_desc.setText(desc);
	}
}
