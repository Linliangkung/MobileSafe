package com.jsako.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamTools {
	//˽�л����캯��
	private StreamTools(){}
	/**
	 * ��һ��������ת����һ���ַ���
	 * @param in ����������
	 * @return   ת������ַ���
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
