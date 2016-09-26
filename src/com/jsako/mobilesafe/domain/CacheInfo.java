package com.jsako.mobilesafe.domain;

import java.io.Serializable;

public class CacheInfo implements Serializable {
	public String name;
	public String packageName;
	public long CacheSize;
	
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
	public long getCacheSize() {
		return CacheSize;
	}
	public void setCacheSize(long cacheSize) {
		CacheSize = cacheSize;
	}
	@Override
	public String toString() {
		return "CacheInfo [name=" + name + ", CacheSize=" + CacheSize + "]";
	}
}
