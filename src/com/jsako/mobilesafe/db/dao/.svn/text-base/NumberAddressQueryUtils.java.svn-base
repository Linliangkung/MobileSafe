package com.jsako.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class NumberAddressQueryUtils {
	private static String path = "/data/data/com.jsako.mobilesafe/files/address.db";
	private static boolean flag = true;

	private NumberAddressQueryUtils() {
	}

	public static String getAddress(String number) {
		switch (number.length()) {
		case 3:
			return "特殊电话";
		case 5:
			return "客服电话";
		case 7:
		case 8:
			return "本地电话";
		}
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = null;
		try {
			if (number.matches("1[34568]\\d{9}")) {
				// 当匹配到号码是一个手机的时候
				cursor = database
						.rawQuery(
								"select location from data2 where id=(select outkey from data1 where id=?);",
								new String[] { number.substring(0, 7) });
				flag = true;
			}
			if (number.length() > 10 && number.startsWith("0")) {
				// 当匹配到号码时固话的时候
				if (number.length() == 11) {
					// 1.当号码是020这种三个字的情况时
					cursor = database.rawQuery(
							"select location from data2 where area=?",
							new String[] { number.substring(1, 3) });
				} else if (number.length() == 12) {
					// 2.当号码是0759这种四个字的情况时
					cursor = database.rawQuery(
							"select location from data2 where area=?",
							new String[] { number.substring(1, 4) });
				}
				flag = false;
			}
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				String address = cursor.getString(0);
				if (!flag) {
					// 说明是固话,需要截断最后的两个字
					address = address.substring(0, address.length() - 2);
				}
				cursor.close();
				return address;
			}
		} finally {
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			if (database != null)
				database.close();
		}
		return "没有查询到相应的结果";

	}
}
