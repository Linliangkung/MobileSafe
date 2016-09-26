package com.jsako.mobilesafe.test;


import java.util.List;
import java.util.Random;

import android.test.AndroidTestCase;

import com.jsako.mobilesafe.db.BlackNumberDBOpenHelper;
import com.jsako.mobilesafe.db.dao.BlackNumberDao;
import com.jsako.mobilesafe.domain.BlackNumberInfo;

public class TestBlackNumberDB extends AndroidTestCase {
	public void TestCreateDB() throws Exception {
		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(
				getContext());
		helper.getWritableDatabase();
	}
	public void testAdd() throws Exception {
		BlackNumberDao dao=new BlackNumberDao(getContext());
		long number=13500000000l;
		Random random=new Random();
		for(int i=0;i<100;i++){
			dao.insert(String.valueOf(number+i),String.valueOf(random.nextInt(3)+1));
		}
	}
	
	public void testDelete() throws Exception {
		BlackNumberDao dao=new BlackNumberDao(getContext());
		dao.delete("13143350142");
	}
	
	public void testUpdate() throws Exception {
		BlackNumberDao dao=new BlackNumberDao(getContext());
		dao.update("13143350142", "3");
	}
	
	public void testFind() throws Exception {
		BlackNumberDao dao=new BlackNumberDao(getContext());
		boolean result=dao.find("13143350142");
		assertEquals(true, result);
	}
	public void testFindAll() throws Exception{
		BlackNumberDao dao=new BlackNumberDao(getContext());
		List<BlackNumberInfo> list=dao.findAll();
		for(BlackNumberInfo info:list){
			System.out.println(info);
		}
	}
	public void testFindMode() throws Exception{
		BlackNumberDao dao=new BlackNumberDao(getContext());
		String mode=dao.findMode("13143350142");
		if(mode==null){
			System.out.println("mode=null");
			return;
		}
		System.out.println(mode);
	}
	public void testCount() throws Exception{
		BlackNumberDao dao=new BlackNumberDao(getContext());
		int count=dao.getCount();
		System.out.println(count);
	}
}
