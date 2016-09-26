package com.jsako.mobilesafe.ui;

import com.jsako.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义一个空间,他有两个TextVeiw和一个CheakBox
 * 
 * @author JsAko
 * 
 */
public class SettingItemView extends RelativeLayout {
	private CheckBox cb_status;
	private TextView tv_title;
	private TextView tv_desc;
	private String desc_on;
	private String desc_off;

	private void initView(Context context) {
		// 将布局文件转换成View对象,并且设置该View的父亲为SettingItemView
		View.inflate(context, R.layout.setting_item_view, this);
		cb_status = (CheckBox) findViewById(R.id.cb_status);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		String itemtitle=attrs.getAttributeValue("http://schemas.android.com/apk/res/com.jsako.mobilesafe", "itemtitle");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.jsako.mobilesafe", "desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.jsako.mobilesafe", "desc_off");
		tv_title.setText(itemtitle);
		setDesc(desc_off);
	}

	public SettingItemView(Context context) {
		super(context);
		initView(context);
	}
	/**
	 * 判断自定义组合控件的状态是否被选中,如果checkbox被选中则返回true,否则返回false
	 * @return 自定义组合控件中cheackbox的状态
	 */
	public boolean isChecked() {
		return cb_status.isChecked();
	}
	/**
	 * 设置自定义组合控件选中状态,如果为true,则checkbox被选中
	 * @param checked 改变的状态
	 */
	public void setChecked(boolean checked) {
		cb_status.setChecked(checked);
		if(checked){
			setDesc(desc_on);
		}else{
			setDesc(desc_off);
		}
	}
	/**
	 * 设置自定组合控件的描述信息textview当前的信息
	 * @param desc 描述信息
	 */
	public void setDesc(String desc) {
		tv_desc.setText(desc);
	}
}
