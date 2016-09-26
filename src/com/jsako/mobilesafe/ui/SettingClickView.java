package com.jsako.mobilesafe.ui;

import com.jsako.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义一个空间,他有两个TextVeiw和一个CheakBox
 * 
 * @author JsAko
 * 
 */
public class SettingClickView extends RelativeLayout {
	private TextView tv_title;
	private TextView tv_desc;

	private void initView(Context context) {
		// 将布局文件转换成View对象,并且设置该View的父亲为SettingItemView
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
	 * 设置自定组合控件的描述信息textview当前的信息
	 * @param desc 描述信息
	 */
	public void setDesc(String desc) {
		tv_desc.setText(desc);
	}
}
