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
		
		//����listview�����¼��ļ���
		lv_callsms_safe.setOnScrollListener(new OnScrollListener() {
			//��listview״̬�����ı��ʱ�����
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch(scrollState){
				case OnScrollListener.SCROLL_STATE_FLING://�����Ի�����״̬
					break;
				case OnScrollListener.SCROLL_STATE_IDLE://�����ڿ��е�״̬
					int lastPosition=lv_callsms_safe.getLastVisiblePosition();
					if(lastPosition==result.size()-1){
						Log.i(TAG,"lastPosition:"+lastPosition);
						if(offset<dbcount){
							offset+=maxnumber;
							fillData();
						}else{
							Toast.makeText(getApplicationContext(),"�Ѿ�û�и�����������", Toast.LENGTH_SHORT).show();
						}
					}
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://���ֲ�������״̬
					break;
				}
			}
			//��listview ���ڹ�����ʱ�����
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
				tv_title.setText("�޸ĺ�����������Ϣ");
				final EditText et_blacknumber = (EditText) contentView
						.findViewById(R.id.et_blacknumber);
				final CheckBox cb_number = (CheckBox) contentView
						.findViewById(R.id.cb_number);
				final CheckBox cb_sms = (CheckBox) contentView
						.findViewById(R.id.cb_sms);
				final Button ok = (Button) contentView.findViewById(R.id.ok);
				final Button cancel = (Button) contentView
						.findViewById(R.id.cancel);
				// ��������򲻿ɱ��༭
				et_blacknumber.setEnabled(false);
				// ����ʾ����
				final String number = result.get(position).getNumber();
				final String mode = result.get(position).getMode();

				et_blacknumber.setText(number);
				switch (Integer.parseInt(mode)) {
				case 1:// �绰����
					cb_number.setChecked(true);
					break;
				case 2:// ��������
					cb_sms.setChecked(true);
					break;
				case 3:// ȫ������
					cb_number.setChecked(true);
					cb_sms.setChecked(true);
					break;
				}
				ok.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// �޸�����
						// �ж�checkbox��ѡ�����
						String mode1;
						if (cb_number.isChecked() && cb_sms.isChecked()) {
							// ����checkbox����ѡ��
							mode1 = "3";
						} else if (cb_number.isChecked()) {
							// ֻ�е绰���ر�ѡ��
							mode1 = "1";
						} else if (cb_sms.isChecked()) {
							// ֻ�ж������ر�ѡ��
							mode1 = "2";
						} else {
							// ��û�б�ѡ��
							Toast.makeText(getApplicationContext(), "��ѡ������ģʽ",
									Toast.LENGTH_SHORT).show();
							return;
						}
						// �����ݴ������ݿ���
						// ֪ͨlistview ���ݷ����˸ı�
						if (!mode1.equals(mode)) {
							// ˵�����ݷ����˸ı�
							dao.update(number, mode1);
							result.get(position).setMode(mode1);
							adapter.notifyDataSetChanged();
						}
						Toast.makeText(getApplicationContext(), "�޸ĳɹ�",
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				});
				cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// �����ȡ����ʱ��
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
							//˵����ǰû������
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
			// �����ӿ��Լ���view����Ĵ���,��Լ�ڴ�,���Ч��
			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(CallSmsSafeActivity.this,
						R.layout.list_item_callsms, null);
				// ��������������ʱ��,�����ĵ�ַ��Ϣд�뻧�ڱ�ViewHolder,�ٷ��븸�׵Ŀڴ���,�����Ӻ��ӵĲ�ѯ����
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
				mode = "���ص绰";
				break;
			case 2:
				mode = "���ض���";
				break;
			case 3:
				mode = "ȫ������";
				break;
			}
			String number = result.get(position).getNumber();
			holder.tv_mode.setText(mode);
			holder.tv_number.setText(number);
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Builder builder = new Builder(CallSmsSafeActivity.this);
					builder.setTitle("��������");
					builder.setMessage("��ȷ��Ҫɾ���ĺ���������?");
					builder.setNegativeButton("ȡ��", null);
					builder.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String number = result.get(position)
											.getNumber();
									// ɾ�����ݿ������
									dao.delete(number);
									// ɾ�����ϵ�����
									result.remove(position);
									// ֪ͨlistview����������
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
				// �����ȷ����ʱ��
				String number = et_blacknumber.getText().toString().trim();
				if (TextUtils.isEmpty(number)) {
					Toast.makeText(getApplicationContext(), "����������Ϊ��",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// �ж�checkbox��ѡ�����
				String mode;
				if (cb_number.isChecked() && cb_sms.isChecked()) {
					// ����checkbox����ѡ��
					mode = "3";
				} else if (cb_number.isChecked()) {
					// ֻ�е绰���ر�ѡ��
					mode = "1";
				} else if (cb_sms.isChecked()) {
					// ֻ�ж������ر�ѡ��
					mode = "2";
				} else {
					// ��û�б�ѡ��
					Toast.makeText(getApplicationContext(), "��ѡ������ģʽ",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// �����ݴ������ݿ���
				dao.insert(number, mode);
				// �����ݴ��뼯�ϵ���
				BlackNumberInfo info = new BlackNumberInfo();
				info.setNumber(number);
				info.setMode(mode);
				result.add(0, info);
				if(tv_show_noinfo.isShown()){
					//�����ʾ�����ݵĿռ�����ʾ��
					tv_show_noinfo.setVisibility(View.INVISIBLE);
				}
				// ֪ͨlistview ���ݷ����˸ı�
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// �����ȡ����ʱ��
				dialog.dismiss();
			}
		});
		dialog.setView(contentView, 0, 0, 0, 0);
		dialog.show();
	}
}
