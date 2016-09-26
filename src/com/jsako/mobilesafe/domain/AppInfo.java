package com.jsako.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * 用户存储手机app信息的业务bean
 * @author Administrator
 *
 */
public class AppInfo {
	private String name;
	private String packageName;
	private Drawable icon;
	//如果是用户应用则为true,系统应用为false
	private boolean userApp;
	//如果是安装在手机内存则为true,安装在sd卡为false
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
