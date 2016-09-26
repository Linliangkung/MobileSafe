package com.jsako.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Utils {
	private Md5Utils() {
	}

	public static String getMd5Password(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] bs = digest.digest(password.getBytes());
			StringBuffer buffer = new StringBuffer();
			for (byte b : bs) {
				int num = b & 0xff;
				String str = Integer.toHexString(num);
				if (str.length() == 1) {
					buffer.append(0);
				}
				buffer.append(str);
			}
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getFileMd5(String path) {
		try {
			File file = new File(path);
			if (file.exists()) {
				// 如果文件存在
				MessageDigest digest = MessageDigest.getInstance("md5");
				FileInputStream fis=new FileInputStream(file);
				byte[] buf=new byte[1024];
				int len=0;
				while((len=fis.read(buf))!=-1){
					digest.update(buf, 0, len);
				}
				byte[] bs = digest.digest();
				StringBuffer buffer = new StringBuffer();
				for (byte b : bs) {
					int num = b & 0xff;
					String str = Integer.toHexString(num);
					if (str.length() == 1) {
						buffer.append(0);
					}
					buffer.append(str);
				}
				return buffer.toString();	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
