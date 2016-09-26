package com.jsako.mobilesafe.test;

import java.util.List;

import com.jsako.mobilesafe.domain.TaskInfo;
import com.jsako.mobilesafe.engine.TaskInfoProvider;

import android.test.AndroidTestCase;

public class TestTaskInfoProvider extends AndroidTestCase {
	public void testGetTaskInfos() throws Exception {
		List<TaskInfo> list=TaskInfoProvider.getTaskInfos(getContext());
		for(TaskInfo info:list){
			System.out.println(info);
		}
	}
}
