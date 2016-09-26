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
 * @author Administrator
 *
 */
public class BlackNumberDao {
	private BlackNumberDBOpenHelper helper; 
	/**
	 * 构造方法,当创建BlackNumberDao的时候,同时创建出BlackNumberDBOpenHelper
	 * @param context
	 */
	public BlackNumberDao(Context context){
		helper=new BlackNumberDBOpenHelper(context);
	}
	/**
	 * 查询黑名单号码是否存在
	 * @param number 黑名单号码
	 * @return true 存在 false 不存在
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
	 * 添加黑名单号码
	 * @param number 黑名单号码
	 * @param mode 拦截模式 1.代表电话拦截,2.代表短信拦截,3.代表全部拦截
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
	 * 修改黑名单号码的拦截模式
	 * @param number 黑名单号码
	 * @param mode 新的拦截模式
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
	 * 删除黑名单号码
	 * @param number 黑名单号码
	 */
	public void delete(String number){
		SQLiteDatabase db=helper.getWritableDatabase();
		if(db.isOpen()){
			db.delete("blacknumber", "number=?", new String[]{number});
			db.close();
		}
	}
	/**
	 * 查询所有黑名单号码
	 * @return 所有黑名单号码组成的list集合
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
	 * 查询部分黑名单号码
	 * @return 所有黑名单号码组成的list集合
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
	 * 根据黑名单号码，查询拦截模式
	 * @param number 黑名单号码
	 * @return 拦截模式,如果没找到对应的黑名单号码，则返回空
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
