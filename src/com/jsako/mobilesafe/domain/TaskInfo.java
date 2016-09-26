package com.jsako.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class TaskInfo {
	private String name;
	private String packageName;
	private Drawable icon;
	private long memsize;
	private boolean checked;
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	/**
	 * 如果为true则说明为用户进程
	 */
	private boolean userTask;
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
	public long getMemsize() {
		return memsize;
	}
	public void setMemsize(long memsize) {
		this.memsize = memsize;
	}
	public boolean isUserTask() {
		return userTask;
	}
	public void setUserTask(boolean userTask) {
		this.userTask = userTask;
	}
	@Override
	public String toString() {
		return "TaskInfo [name=" + name + ", packageName=" + packageName
				+ ", icon=" + icon + ", memsize=" + memsize + ", userTask="
				+ userTask + "]";
	}
	
}
