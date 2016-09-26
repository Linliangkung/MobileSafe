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
			return "����绰";
		case 5:
			return "�ͷ��绰";
		case 7:
		case 8:
			return "���ص绰";
		}
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = null;
		try {
			if (number.matches("1[34568]\\d{9}")) {
				// ��ƥ�䵽������һ���ֻ���ʱ��
				cursor = database
						.rawQuery(
								"select location from data2 where id=(select outkey from data1 where id=?);",
								new String[] { number.substring(0, 7) });
				flag = true;
			}
			if (number.length() > 10 && number.startsWith("0")) {
				// ��ƥ�䵽����ʱ�̻���ʱ��
				if (number.length() == 11) {
					// 1.��������020���������ֵ����ʱ
					cursor = database.rawQuery(
							"select location from data2 where area=?",
							new String[] { number.substring(1, 3) });
				} else if (number.length() == 12) {
					// 2.��������0759�����ĸ��ֵ����ʱ
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
					// ˵���ǹ̻�,��Ҫ�ض�����������
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
		return "û�в�ѯ����Ӧ�Ľ��";

	}
}
