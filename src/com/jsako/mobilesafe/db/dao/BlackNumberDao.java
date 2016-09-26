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
 * ���������ݿ���ɾ�Ĳ��ҵ����
 * @author Administrator
 *
 */
public class BlackNumberDao {
	private BlackNumberDBOpenHelper helper; 
	/**
	 * ���췽��,������BlackNumberDao��ʱ��,ͬʱ������BlackNumberDBOpenHelper
	 * @param context
	 */
	public BlackNumberDao(Context context){
		helper=new BlackNumberDBOpenHelper(context);
	}
	/**
	 * ��ѯ�����������Ƿ����
	 * @param number ����������
	 * @return true ���� false ������
	 */
	public boolean find(String number){
		SQLiteDatabase db=helper.getReadableDatabase();
		boolean result=false;
		if(db.isOpen()){
			Cursor cursor=db.rawQuery("select * from blacknumber where number=?",new String[]{number});
			if(cursor!=null&&cursor.getCount()>0){
				result=true;
			}
			cursor.close();
			db.close();
		}
		return result;
	} 
	/**
	 * ��Ӻ���������
	 * @param number ����������
	 * @param mode ����ģʽ 1.����绰����,2.�����������,3.����ȫ������
	 */
	public void insert(String number,String mode){
		SQLiteDatabase db=helper.getWritableDatabase();
		if(db.isOpen()){
			ContentValues values=new ContentValues();
			values.put("number", number);
			values.put("mode", mode);
			db.insert("blacknumber",null,values);
			db.close();
		}
	}
	/**
	 * �޸ĺ��������������ģʽ
	 * @param number ����������
	 * @param mode �µ�����ģʽ
	 */
	public void update(String number,String mode){
		SQLiteDatabase db=helper.getWritableDatabase();
		if(db.isOpen()){
			ContentValues values=new ContentValues();
			values.put("mode", mode);
			db.update("blacknumber",values, "number =?", new String[]{number});
			db.close();
		}
	}
	/**
	 * ɾ������������
	 * @param number ����������
	 */
	public void delete(String number){
		SQLiteDatabase db=helper.getWritableDatabase();
		if(db.isOpen()){
			db.delete("blacknumber", "number=?", new String[]{number});
			db.close();
		}
	}
	/**
	 * ��ѯ���к���������
	 * @return ���к�����������ɵ�list����
	 */
	public List<BlackNumberInfo> findAll(){
		
		List<BlackNumberInfo> result =new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db=helper.getReadableDatabase();
		if(db.isOpen()){
			Cursor cursor=db.rawQuery("select number,mode from blacknumber order by _id desc",null);
			if(cursor!=null&&cursor.getColumnCount()>0){
				while(cursor.moveToNext()){
					BlackNumberInfo info=new BlackNumberInfo();
					info.setNumber(cursor.getString(0));
					info.setMode(cursor.getString(1));
					result.add(info);
				}
				cursor.close();
			}
			db.close();
		}
		return result;
	}
	
	/**
	 * ��ѯ���ֺ���������
	 * @return ���к�����������ɵ�list����
	 */
	public List<BlackNumberInfo> findPart(String offset,String maxnumber){
		
		List<BlackNumberInfo> result =new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db=helper.getReadableDatabase();
		if(db.isOpen()){
			Cursor cursor=db.rawQuery("select number,mode from blacknumber order by _id desc limit ? offset ?",new String[]{maxnumber,offset});
			if(cursor!=null&&cursor.getColumnCount()>0){
				while(cursor.moveToNext()){
					BlackNumberInfo info=new BlackNumberInfo();
					info.setNumber(cursor.getString(0));
					info.setMode(cursor.getString(1));
					result.add(info);
				}
				cursor.close();
			}
			db.close();
		}
		return result;
	}
	/**
	 * ���ݺ��������룬��ѯ����ģʽ
	 * @param number ����������
	 * @return ����ģʽ,���û�ҵ���Ӧ�ĺ��������룬�򷵻ؿ�
	 */
	public String findMode(String number){
		SQLiteDatabase db=helper.getReadableDatabase();
		String mode=null;
		if(db.isOpen()){
			Cursor cursor=db.rawQuery("select mode from blacknumber where number =?", new String[]{number});
			if(cursor!=null&&cursor.getCount()>0){
				cursor.moveToFirst();
				mode=cursor.getString(0);
				
			}
			cursor.close();
			db.close();
		}
		return mode;
	}
	
	public int getCount(){
		SQLiteDatabase db=helper.getReadableDatabase();
		int count=0;
		if(db.isOpen()){
			Cursor cursor=db.rawQuery("select count(*) from blacknumber", null);
			if(cursor!=null&&cursor.getCount()>0){
				cursor.moveToFirst();
				count=cursor.getInt(0);
			}
			cursor.close();
			db.close();
		}
		return count;
	}
}
