package com.jsako.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jsako.mobilesafe.domain.TaskInfo;
import com.jsako.mobilesafe.engine.TaskInfoProvider;
import com.jsako.mobilesafe.utils.SystemInfoUtils;

public class TaskManagerActivity extends Activity {
	private static final String TAG = "TaskManagerActivity";
	private TextView tv_process_count;
	private TextView tv_memory_info;
	private LinearLayout ll_load_app;
	private RelativeLayout rl_show_info;
	private TextView tv_show;
	private ListView lv_task_manager;
	private List<TaskInfo> taskInfos;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> systemTaskInfos;
	private TaskManagerAdapter adapter;
	private LinearLayout ll_task_manager_button;
	private int processCount;
	private long totalMem;
	private long availMem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);
		ll_load_app = (LinearLayout) findViewById(R.id.ll_load_app);
		rl_show_info = (RelativeLayout) findViewById(R.id.rl_show_info);
		tv_show = (TextView) findViewById(R.id.tv_show);
		lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);
		ll_task_manager_button = (LinearLayout) findViewById(R.id.ll_task_manager_button);

		lv_task_manager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (userTaskInfos != null && systemTaskInfos != null) {
					if (firstVisibleItem > userTaskInfos.size()) {
						// ϵͳ����
						tv_show.setText("ϵͳ����:" + systemTaskInfos.size() + "��");
					} else {
						// �û�����
						tv_show.setText("�û�����:" + userTaskInfos.size() + "��");
					}

				}

			}
		});

		lv_task_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskInfo info;
				if (position == 0) {
					return;
				} else if (position == userTaskInfos.size() + 1) {
					return;
				} else if (position <= userTaskInfos.size()) {
					info = userTaskInfos.get(position - 1);
				} else {
					info = systemTaskInfos.get(position - 1
							- userTaskInfos.size() - 1);
				}

				if (getPackageName().equals(info.getPackageName())) {
					return;
				}
				ViewHolder holder = (ViewHolder) view.getTag();
				if (info.isChecked()) {
					// �����ѡ��
					holder.cb_status.setChecked(false);
					info.setChecked(false);
				} else {
					// ���û�й�ѡ
					holder.cb_status.setChecked(true);
					info.setChecked(true);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		setInvisible();
	}

	private void setInvisible() {
		tv_show.setVisibility(View.INVISIBLE);
		rl_show_info.setVisibility(View.INVISIBLE);
		ll_task_manager_button.setVisibility(View.INVISIBLE);
		lv_task_manager.setVisibility(View.INVISIBLE);
	}

	private void setVisible() {
		tv_show.setVisibility(View.VISIBLE);
		rl_show_info.setVisibility(View.VISIBLE);
		ll_task_manager_button.setVisibility(View.VISIBLE);
		lv_task_manager.setVisibility(View.VISIBLE);
	}

	private void fillData() {
		ll_load_app.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				// ��ȡϵͳ��������
				taskInfos = TaskInfoProvider
						.getTaskInfos(TaskManagerActivity.this);
				userTaskInfos = new ArrayList<TaskInfo>();
				systemTaskInfos = new ArrayList<TaskInfo>();
				for (TaskInfo info : taskInfos) {
					if (info.isUserTask()) {
						// ������û�����
						userTaskInfos.add(info);
					} else {
						// �����ϵͳ����
						systemTaskInfos.add(info);
					}
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setTitle();
						setVisible();
						tv_show.setText("�û�����:" + userTaskInfos.size() + "��");
						ll_load_app.setVisibility(View.INVISIBLE);
						if (adapter == null) {
							adapter = new TaskManagerAdapter();
							lv_task_manager.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
					}
				});

			};
		}.start();
	}

	private class TaskManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			SharedPreferences sp=getSharedPreferences("config",Context.MODE_PRIVATE);
			boolean checked=sp.getBoolean("showsystem", false);
			if(checked)
			return userTaskInfos.size() + 1 + systemTaskInfos.size() + 1;
			else
			return userTaskInfos.size() + 1 ;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TaskInfo info;
			if (position == 0) {
				// ��ʾ�û�����С��Ŀ
				TextView tv = new TextView(TaskManagerActivity.this);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("�û�����:" + userTaskInfos.size() + "��");
				tv.setTextColor(Color.WHITE);
				return tv;
			} else if (position == userTaskInfos.size() + 1) {
				// ��ʾϵͳ����С��Ŀ
				TextView tv = new TextView(TaskManagerActivity.this);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("ϵͳ����:" + systemTaskInfos.size() + "��");
				tv.setTextColor(Color.WHITE);
				return tv;
			} else if (position <= userTaskInfos.size()) {
				// ��ʾ�û�����
				info = userTaskInfos.get(position - 1);
			} else {
				// ��ʾϵͳ����
				info = systemTaskInfos.get(position - 1 - userTaskInfos.size()
						- 1);
			}
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(TaskManagerActivity.this,
						R.layout.list_item_taskinfo, null);
				holder = new ViewHolder();
				holder.iv_task_icon = (ImageView) view
						.findViewById(R.id.iv_task_icon);
				holder.tv_task_name = (TextView) view
						.findViewById(R.id.tv_task_name);
				holder.tv_task_memsize = (TextView) view
						.findViewById(R.id.tv_task_memsize);
				holder.cb_status = (CheckBox) view.findViewById(R.id.cb_status);
				view.setTag(holder);
			}
			holder.iv_task_icon.setImageDrawable(info.getIcon());
			holder.tv_task_name.setText(info.getName());
			holder.tv_task_memsize.setText("�ڴ�ռ��:"
					+ Formatter.formatFileSize(getApplicationContext(),
							info.getMemsize()));
			holder.cb_status.setChecked(info.isChecked());
			if (getPackageName().equals(info.getPackageName())) {
				// ˵���ǵ�ǰӦ��,����checkBox������
				holder.cb_status.setVisibility(View.INVISIBLE);
			} else {
				holder.cb_status.setVisibility(View.VISIBLE);
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}

	private class ViewHolder {
		public ImageView iv_task_icon;
		public TextView tv_task_name;
		public TextView tv_task_memsize;
		public CheckBox cb_status;
	}

	public void selectAll(View view) {
		// for(TaskInfo info:userTaskInfos){
		// info.setChecked(true);
		// }
		// for(TaskInfo info:systemTaskInfos){
		// info.setChecked(true);
		// }
		for (TaskInfo info : taskInfos) {
			if (getPackageName().equals(info.getPackageName())) {
				continue;
			}
			info.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}

	public void selectOppo(View view) {
		// for(TaskInfo info:userTaskInfos){
		// info.setChecked(!info.isChecked());
		// }
		// for(TaskInfo info:systemTaskInfos){
		// info.setChecked(!info.isChecked());
		// }
		for (TaskInfo info : taskInfos) {
			if (getPackageName().equals(info.getPackageName())) {
				continue;
			}
			info.setChecked(!info.isChecked());
		}
		adapter.notifyDataSetChanged();
	}

	public void killAll(View view) {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<TaskInfo> killProcessList = new ArrayList<TaskInfo>();
		int count = 0;
		long saveMem = 0;
		for (TaskInfo info : taskInfos) {
			if (info.isChecked()) {
				// �����ѡ�о�ɱ���ý���
				am.killBackgroundProcesses(info.getPackageName());
				killProcessList.add(info);
				if (info.isUserTask()) {
					// ������û�����
					userTaskInfos.remove(info);
				} else {
					// �����ϵͳ����
					systemTaskInfos.remove(info);
				}
				count++;
				saveMem += info.getMemsize();
			}
		}
		adapter.notifyDataSetChanged();
		taskInfos.removeAll(killProcessList);
		Toast.makeText(
				this,
				"ɱ��" + count + "������,�ͷ���"
						+ Formatter.formatFileSize(this, saveMem) + "�ڴ�ռ�",
				Toast.LENGTH_SHORT).show();
		processCount -= count;
		availMem += saveMem;
		tv_process_count.setText("�����еĽ���:" + processCount + "��");
		tv_memory_info.setText("ʣ��/���ڴ�:"
				+ Formatter.formatFileSize(TaskManagerActivity.this, availMem)
				+ "/"
				+ Formatter.formatFileSize(TaskManagerActivity.this, totalMem));
	}

	public void enterSetting(View view) {
		Intent intent=new Intent(this,TaskSetttingActivity.class);
		startActivity(intent);
	}

	private void setTitle() {
		processCount = SystemInfoUtils
				.getRuningProcessCount(TaskManagerActivity.this);
		totalMem = SystemInfoUtils.getTotalMem(TaskManagerActivity.this);
		availMem = SystemInfoUtils.getAvailMem(TaskManagerActivity.this);
		tv_process_count.setText("�����еĽ���:" + processCount + "��");
		tv_memory_info.setText("ʣ��/���ڴ�:"
				+ Formatter.formatFileSize(TaskManagerActivity.this, availMem)
				+ "/"
				+ Formatter.formatFileSize(TaskManagerActivity.this, totalMem));
	}
}
