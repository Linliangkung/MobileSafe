package com.jsako.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamTools {
	//私有化构造函数
	private StreamTools(){}
	/**
	 * 将一个输入流转换成一个字符串
	 * @param in 输入流对象
	 * @return   转换后的字符串
	 * @throws IOException 
	 */
	public static String readFromStream(InputStream in) throws IOException{
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		int len=0;
		byte[] buf=new byte[1024];
		while((len=in.read(buf))!=-1){
			bos.write(buf,0,len);
		}
		String data=bos.toString();
		in.close();
		bos.close();
		return data;
	}
}
