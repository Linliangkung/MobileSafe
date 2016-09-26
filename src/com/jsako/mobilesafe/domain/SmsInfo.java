package com.jsako.mobilesafe.domain;

import java.io.Serializable;

public class SmsInfo implements Serializable {
	private String address;
	private String type;
	private String date;
	private String body;
	@Override
	public String toString() {
		return "SmsInfo [address=" + address + ", type=" + type + ", date="
				+ date + ", body=" + body + "]";
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
}
