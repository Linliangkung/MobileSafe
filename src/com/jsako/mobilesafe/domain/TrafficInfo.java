package com.jsako.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class TrafficInfo {
	private long rx;//当前应用下载流量
	private long tx;//当前应用上传流量
	private String name;//当前应用名字
	private Drawable icon;//当前应用图标
	public long getRx() {
		return rx;
	}
	public void setRx(long rx) {
		this.rx = rx;
	}
	public long getTx() {
		return tx;
	}
	public void setTx(long tx) {
		this.tx = tx;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	@Override
	public String toString() {
		return "TrafficInfo [rx=" + rx + ", tx=" + tx + ", name=" + name
				+ ", icon=" + icon + "]";
	}
	
	
}
