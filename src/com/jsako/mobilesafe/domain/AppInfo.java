package com.jsako.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * �û��洢�ֻ�app��Ϣ��ҵ��bean
 * @author Administrator
 *
 */
public class AppInfo {
	private String name;
	private String packageName;
	private Drawable icon;
	//������û�Ӧ����Ϊtrue,ϵͳӦ��Ϊfalse
	private boolean userApp;
	//����ǰ�װ���ֻ��ڴ���Ϊtrue,��װ��sd��Ϊfalse
	private boolean isRom;
	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", packageName=" + packageName
				+ ", icon=" + icon + ", userApp=" + userApp + ", isRom="
				+ isRom + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public boolean isUserApp() {
		return userApp;
	}
	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}
	public boolean isRom() {
		return isRom;
	}
	public void setRom(boolean isRom) {
		this.isRom = isRom;
	}
	
	
}
