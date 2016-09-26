package com.jsako.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jsako.mobilesafe.db.AppLockDBOpenHelper;

/**
 * ���������ݿ������dao
 * 
 * @author Administrator
 * 
 */
public class AppLockDao {
	private AppLockDBOpenHelper helper;
	private Context context;
	
	public AppLockDao(Context context) {
		helper = new AppLockDBOpenHelper(context);
		this.context=context;
	}

	/**
	 * ���һ��Ҫ����Ӧ�ó���İ���
	 * 
	 * @param packageName
	 */
	public void add(String packageName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put("packagename", packageName);
			db.insert("applock", null, values);
			db.close();
		}
		sendDataChangeBroadcast();
	}
	
	private void sendDataChangeBroadcast() {
		Intent intent=new Intent();
		intent.setAction("com.jsako.mobilesafe.datachange");
		context.sendBroadcast(intent);
	}

	/**
	 * ɾ��һ��Ҫ����Ӧ�ó���İ���
	 * 
	 * @param packageName
	 */
	public void delete(String packageName) {
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete("applock", "packagename=?", new String[] { packageName });
			db.close();
		}
		sendDataChangeBroadcast();
	}

	/**
	 * ��ѯ��������¼�Ƿ����
	 * 
	 * @param packageName
	 * @return
	 */
	public boolean find(String packageName) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.query("applock", null, "packagename=?",
					new String[] { packageName }, null, null, null);
			if (cursor.getCount() > 0) {
				// ˵����ǰ�м�¼
				result = true;
			}
			cursor.close();
			db.close();
		}
		return result;
	}

	/**
	 * ��ѯ���г�������¼
	 * 
	 * @param packageName
	 * @return
	 */
	public List<String> findAll() {
		List<String> packageNameInfos = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.query("applock", new String[] { "packagename" },
					null, null, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					String packageName = cursor.getString(0);
					packageNameInfos.add(packageName);
				}
			}
			cursor.close();
			db.close();
		}
		return packageNameInfos;
	}
}
