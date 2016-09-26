package com.jsako.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.jsako.mobilesafe.db.BlackNumberDBOpenHelper;
import com.jsako.mobilesafe.domain.BlackNumberInfo;

/**
 * 黑名单数据库增删改查的业务类
 * 
 * @author Administrator
 * 
 */
public class AntiVirusDao {
	private static String path = "/data/data/com.jsako.mobilesafe/files/antivirus.db";

	public static boolean isVirus(String md5) {
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		boolean result=false;
		if (database.isOpen()) {
			Cursor cursor = database.rawQuery(
					"select * from datable where md5=?", new String[] { md5 });
			if (cursor != null && cursor.getCount() > 0) {
				result = true;
			}
			cursor.close();
			database.close();
		}
		return result;
	}
}
