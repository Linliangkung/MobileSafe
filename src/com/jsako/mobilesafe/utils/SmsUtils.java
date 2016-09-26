package com.jsako.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import com.jsako.mobilesafe.domain.SmsInfo;

/**
 * ���Ź�����
 * 
 * @author Administrator
 * 
 */
public class SmsUtils {
	private SmsUtils() {
	}

	public interface SmsCallBack {
		/**
		 * ����֮ǰ,���ý��������ֵ
		 * 
		 * @param max
		 *            ���������ֵ
		 */
		public void beforeBackup(int max);

		/**
		 * ������,���ý���������
		 * 
		 * @param progress
		 *            ����������
		 */
		public void onBackup(int progress);
	}

	/**
	 * ���ű��ݵķ���
	 * 
	 * @param context
	 *            ������
	 * @param callBack
	 *            �ص��ӿ�ʵ����Ķ���
	 * @throws Exception
	 */
	public static void backUpSms(Context context, SmsCallBack callBack)
			throws Exception {
		ContentResolver cr = context.getContentResolver();
		File file = new File(Environment.getExternalStorageDirectory(),
				"backup.xml");
		FileOutputStream fos = new FileOutputStream(file);
		// ��ȡxml���л���
		XmlSerializer xs = Xml.newSerializer();
		// ��ʼ��xml������
		xs.setOutput(fos, "utf-8");
		xs.startDocument("utf-8", true);
		xs.startTag(null, "smss");
		// �������ṩ�߶�ȡ������Ϣ,��������xml��sms�ڵ���
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = cr.query(uri, new String[] { "address", "date", "type",
				"body" }, null, null, null);
		int max = cursor.getCount();
		callBack.beforeBackup(max);
		xs.attribute(null, "max", String.valueOf(max));
		if (cursor != null && max > 0) {
			int count = 0;
			while (cursor.moveToNext()) {
				String address = cursor.getString(0);
				String date = cursor.getString(1);
				String type = cursor.getString(2);
				String body = cursor.getString(3);
				xs.startTag(null, "sms");

				xs.startTag(null, "address");
				xs.text(address);
				xs.endTag(null, "address");

				xs.startTag(null, "date");
				xs.text(date);
				xs.endTag(null, "date");

				xs.startTag(null, "type");
				xs.text(type);
				xs.endTag(null, "type");

				xs.startTag(null, "body");
				xs.text(body);
				xs.endTag(null, "body");
				xs.endTag(null, "sms");
				count++;
				callBack.onBackup(count);
			}
		}
		cursor.close();
		xs.endTag(null, "smss");
		xs.endDocument();
		fos.close();
	}

	/**
	 * ���Ż�ԭ
	 * 
	 * @param context
	 *            ������
	 * @param callBack
	 *            ���Żص��ӿ�ʵ����Ķ���
	 * @throws Exception
	 */
	public static void restoreSms(Context context,SmsCallBack callBack) throws Exception{
		//��ɾ��ԭ�м�¼
		ContentResolver cr=context.getContentResolver();
		Uri uri=Uri.parse("content://sms/");
		cr.delete(uri, null, null);
		//��ȡ����¼�� 
		File file =new File(Environment.getExternalStorageDirectory(),"backup.xml");
		FileInputStream fis=new FileInputStream(file);
		XmlPullParser parser=Xml.newPullParser();
		parser.setInput(fis,"utf-8");
		parser.next();
		String max=parser.getAttributeValue(null,"max");
		callBack.beforeBackup(Integer.parseInt(max));
		//��ȡxml�еĶ��ż�¼,ÿ��һ������һ����¼
		int type=parser.getEventType();
		SmsInfo info=null;
		int count=0;
		while(type!=XmlPullParser.END_DOCUMENT){
			switch(type){
			case XmlPullParser.START_TAG:
				if("sms".equals(parser.getName())){
					info=new SmsInfo();
				}else if("address".equals(parser.getName())){
					info.setAddress(parser.nextText());
				}else if("date".equals(parser.getName())){
					info.setDate(parser.nextText());
				}else if("type".equals(parser.getName())){
					info.setType(parser.nextText());
				}else if("body".equals(parser.getName())){
					info.setBody(parser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				if("sms".equals(parser.getName())){
					ContentValues values=new ContentValues();
					values.put("address",info.getAddress());
					values.put("type",info.getType());
					values.put("body",info.getBody());
					values.put("date",info.getDate());
					cr.insert(uri, values);
					count ++;
					callBack.onBackup(count);
				}
				break;
			}
			type=parser.next();
		}
	}
}
