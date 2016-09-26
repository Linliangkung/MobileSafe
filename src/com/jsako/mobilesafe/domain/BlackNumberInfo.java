package com.jsako.mobilesafe.domain;

import java.io.Serializable;

public class BlackNumberInfo implements Serializable {
	private String number;
	private String mode;
	@Override
	public String toString() {
		return "BlackNumberInfo [number=" + number + ", mode=" + mode + "]";
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
}
