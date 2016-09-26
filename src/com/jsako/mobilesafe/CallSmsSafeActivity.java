package com.jsako.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jsako.mobilesafe.db.dao.BlackNumberDao;
import com.jsako.mobilesafe.domain.BlackNumberInfo;

public class CallSmsSafeActivity extends Activity {
	final private static String TAG = "CallSmsSafeActivity";
	private ListView lv_callsms_safe;
	private List<BlackNumberInfo> result;
	private MyAdapter adapter;
	private BlackNumberDao dao;
	private LinearLayout ll_load;
	private TextView tv_show_noinfo;
	private int offset=0;
	private int maxnumber=20;
	private int dbcount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
		ll_load=(LinearLayout) findViewById(R.id.ll_load);
		tv_show_noinfo=(TextView) findViewById(R.id.tv_show_noinfo);
		dao = new BlackNumberDao(this);
		dbcount=dao.getCount();
		fillData();
		
		//设置listview滚动事件的监听
		lv_callsms_safe.setOnScrollListener(new OnScrollListener() {
			//当listview状态发生改变的时候调用
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch(scrollState){
				case OnScrollListener.SCROLL_STATE_FLING://当惯性滑动的状态
					break;
				case OnScrollListener.SCROLL_STATE_IDLE://当处于空闲的状态
					int lastPosition=lv_callsms_safe.getLastVisiblePosition();
					if(lastPosition==result.size()-1){
						Log.i(TAG,"lastPosition:"+lastPosition);
						if(offset<dbcount){
							offset+=maxnumber;
							fillData();
						}else{
							Toast.makeText(getApplicationContext(),"已经没有更多数据啦亲", Toast.LENGTH_SHORT).show();
						}
					}
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://当手部触摸的状态
					break;
				}
			}
			//当listview 正在滚动的时候调用
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
		
		lv_callsms_safe.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				Builder builder = new Builder(CallSmsSafeActivity.this);
				final AlertDialog dialog = builder.create();
				View contentView = View.inflate(CallSmsSafeActivity.this,
						R.layout.dialog_add_blacknumber, null);
				TextView tv_title = (TextView) contentView
						.findViewById(R.id.tv_title);
				tv_title.setText("修改黑名单号码信息");
				final EditText et_blacknumber = (EditText) contentView
						.findViewById(R.id.et_blacknumber);
				final CheckBox cb_number = (CheckBox) contentView
						.findViewById(R.id.cb_number);
				final CheckBox cb_sms = (CheckBox) contentView
						.findViewById(R.id.cb_sms);
				final Button ok = (Button) contentView.findViewById(R.id.ok);
				final Button cancel = (Button) contentView
						.findViewById(R.id.cancel);
				// 设置输入框不可被编辑
				et_blacknumber.setEnabled(false);
				// 会显示数据
				final String number = result.get(position).getNumber();
				final String mode = result.get(position).getMode();

				et_blacknumber.setText(number);
				switch (Integer.parseInt(mode)) {
				case 1:// 电话拦截
					cb_number.setChecked(true);
					break;
				case 2:// 短信拦截
					cb_sms.setChecked(true);
					break;
				case 3:// 全部拦截
					cb_number.setChecked(true);
					cb_sms.setChecked(true);
					break;
				}
				ok.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 修改数据
						// 判断checkbox的选择情况
						String mode1;
						if (cb_number.isChecked() && cb_sms.isChecked()) {
							// 两个checkbox都被选择
							mode1 = "3";
						} else if (cb_number.isChecked()) {
							// 只有电话拦截被选择
							mode1 = "1";
						} else if (cb_sms.isChecked()) {
							// 只有短信拦截被选则
							mode1 = "2";
						} else {
							// 都没有被选择
							Toast.makeText(getApplicationContext(), "请选择拦截模式",
									Toast.LENGTH_SHORT).show();
							return;
						}
						// 将数据存入数据库中
						// 通知listview 数据发生了改变
						if (!mode1.equals(mode)) {
							// 说明数据发生了改变
							dao.update(number, mode1);
							result.get(position).setMode(mode1);
							adapter.notifyDataSetChanged();
						}
						Toast.makeText(getApplicationContext(), "修改成功",
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				});
				cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 当点击取消的时候
						dialog.dismiss();
					}
				});
				dialog.setView(contentView, 0, 0, 0, 0);
				dialog.show();
			}
		});
	}

	private void fillData() {
		ll_load.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				if(result==null){
				result = dao.findPart(String.valueOf(offset), String.valueOf(maxnumber));
				}else{
					result.addAll( dao.findPart(String.valueOf(offset), String.valueOf(maxnumber)));
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(result.size()==0){
							//说明当前没有数据
							tv_show_noinfo.setVisibility(View.VISIBLE);
						}
						ll_load.setVisibility(View.INVISIBLE);
						if(adapter==null){
						adapter = new MyAdapter();
						lv_callsms_safe.setAdapter(adapter);
						}
						else{
						adapter.notifyDataSetChanged();
						}
					}
				});
			};
		}.start();
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return result.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view;
			ViewHolder holder;
			// 这样子可以减少view对象的创建,节约内存,提高效率
			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(CallSmsSafeActivity.this,
						R.layout.list_item_callsms, null);
				// 当孩子生下来的时候,将它的地址信息写入户口本ViewHolder,再放入父亲的口袋中,减少子孩子的查询个数
				holder = new ViewHolder();
				holder.tv_mode = (TextView) view
						.findViewById(R.id.tv_black_mode);
				holder.tv_number = (TextView) view
						.findViewById(R.id.tv_black_number);
				holder.iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);
				view.setTag(holder);
			}
			String mode = null;
			switch (Integer.parseInt(result.get(position).getMode())) {
			case 1:
				mode = "拦截电话";
				break;
			case 2:
				mode = "拦截短信";
				break;
			case 3:
				mode = "全部拦截";
				break;
			}
			String number = result.get(position).getNumber();
			holder.tv_mode.setText(mode);
			holder.tv_number.setText(number);
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Builder builder = new Builder(CallSmsSafeActivity.this);
					builder.setTitle("警告提醒");
					builder.setMessage("你确定要删除改黑名单号码?");
					builder.setNegativeButton("取消", null);
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String number = result.get(position)
											.getNumber();
									// 删除数据库的数据
									dao.delete(number);
									// 删除集合的数据
									result.remove(position);
									// 通知listview更新了数据
									if(result.size()==0){
										tv_show_noinfo.setVisibility(View.VISIBLE);
									}
									adapter.notifyDataSetChanged();
									dialog.dismiss();
								}

							});
					builder.show();
				}
			});
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
		TextView tv_number;
		TextView tv_mode;
		ImageView iv_delete;
	}

	public void addBlackNumber(View view) {
		Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View contentView = View.inflate(this, R.layout.dialog_add_blacknumber,
				null);
		final EditText et_blacknumber = (EditText) contentView
				.findViewById(R.id.et_blacknumber);
		final CheckBox cb_number = (CheckBox) contentView
				.findViewById(R.id.cb_number);
		final CheckBox cb_sms = (CheckBox) contentView
				.findViewById(R.id.cb_sms);
		final Button ok = (Button) contentView.findViewById(R.id.ok);
		final Button cancel = (Button) contentView.findViewById(R.id.cancel);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 当点击确定的时候
				String number = et_blacknumber.getText().toString().trim();
				if (TextUtils.isEmpty(number)) {
					Toast.makeText(getApplicationContext(), "黑名单号码为空",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// 判断checkbox的选择情况
				String mode;
				if (cb_number.isChecked() && cb_sms.isChecked()) {
					// 两个checkbox都被选择
					mode = "3";
				} else if (cb_number.isChecked()) {
					// 只有电话拦截被选择
					mode = "1";
				} else if (cb_sms.isChecked()) {
					// 只有短信拦截被选则
					mode = "2";
				} else {
					// 都没有被选择
					Toast.makeText(getApplicationContext(), "请选择拦截模式",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// 将数据存入数据库中
				dao.insert(number, mode);
				// 将数据存入集合当中
				BlackNumberInfo info = new BlackNumberInfo();
				info.setNumber(number);
				info.setMode(mode);
				result.add(0, info);
				if(tv_show_noinfo.isShown()){
					//如果显示无数据的空间是显示的
					tv_show_noinfo.setVisibility(View.INVISIBLE);
				}
				// 通知listview 数据发生了改变
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 当点击取消的时候
				dialog.dismiss();
			}
		});
		dialog.setView(contentView, 0, 0, 0, 0);
		dialog.show();
	}
}
