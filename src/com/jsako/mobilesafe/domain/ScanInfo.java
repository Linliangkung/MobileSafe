package com.jsako.mobilesafe.domain;

import java.io.Serializable;

public class ScanInfo implements Serializable{
	private String packageName;
	private String name;
	boolean isVirus;
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isVirus() {
		return isVirus;
	}
	public void setVirus(boolean isVirus) {
		this.isVirus = isVirus;
	}
}
