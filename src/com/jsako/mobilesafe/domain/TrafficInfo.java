package com.jsako.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class TrafficInfo {
	private long rx;//��ǰӦ����������
	private long tx;//��ǰӦ���ϴ�����
	private String name;//��ǰӦ������
	private Drawable icon;//��ǰӦ��ͼ��
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
