package com.jsako.mobilesafe.ui;

import com.jsako.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * �Զ���һ���ռ�,��������TextVeiw��һ��CheakBox
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
		// �������ļ�ת����View����,�������ø�View�ĸ���ΪSettingItemView
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
	 * �ж��Զ�����Ͽؼ���״̬�Ƿ�ѡ��,���checkbox��ѡ���򷵻�true,���򷵻�false
	 * @return �Զ�����Ͽؼ���cheackbox��״̬
	 */
	public boolean isChecked() {
		return cb_status.isChecked();
	}
	/**
	 * �����Զ�����Ͽؼ�ѡ��״̬,���Ϊtrue,��checkbox��ѡ��
	 * @param checked �ı��״̬
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
	 * �����Զ���Ͽؼ���������Ϣtextview��ǰ����Ϣ
	 * @param desc ������Ϣ
	 */
	public void setDesc(String desc) {
		tv_desc.setText(desc);
	}
}
