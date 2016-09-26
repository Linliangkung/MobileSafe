package com.jsako.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jsako.mobilesafe.domain.TrafficInfo;
import com.jsako.mobilesafe.engine.TrafficInfoProvider;

public class TrafficManagerActivity extends Activity {
	private RelativeLayout rl_show_info;
	private TextView tv_total_tx;
	private TextView tv_total_rx;
	private LinearLayout ll_load_traffic;
	private ListView lv_traffic_manager;

	private long totalTx;// 上传总流量
	private long totalRx;// 下载总流量
	private List<TrafficInfo> trafficInfos;
	private TrafficManagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic_manager);

		rl_show_info = (RelativeLayout) findViewById(R.id.rl_show_info);
		tv_total_tx = (TextView) findViewById(R.id.tv_total_tx);
		tv_total_rx = (TextView) findViewById(R.id.tv_total_rx);
		ll_load_traffic = (LinearLayout) findViewById(R.id.ll_load_traffic);
		lv_traffic_manager = (ListView) findViewById(R.id.lv_traffic_manager);
	}

	private void fillData() {
		ll_load_traffic.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				totalTx = TrafficStats.getTotalTxBytes();
				totalRx = TrafficStats.getTotalRxBytes();
				trafficInfos = TrafficInfoProvider
						.getTrafficInfos(TrafficManagerActivity.this);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tv_total_tx.setText("上传总流量:"
								+ Formatter.formatFileSize(
										TrafficManagerActivity.this, totalTx));
						tv_total_rx.setText("下载总流量:"
								+ Formatter.formatFileSize(
										TrafficManagerActivity.this, totalRx));
						rl_show_info.setVisibility(View.VISIBLE);
						ll_load_traffic.setVisibility(View.INVISIBLE);
						lv_traffic_manager.setVisibility(View.VISIBLE);
						if (adapter == null) {
							adapter = new TrafficManagerAdapter();
							lv_traffic_manager.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}

					}
				});
			}
		}.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		rl_show_info.setVisibility(View.INVISIBLE);
		lv_traffic_manager.setVisibility(View.INVISIBLE);
	}

	private class TrafficManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return trafficInfos.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView != null) {
				// 复用缓存
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				// 创建View对象
				view = View.inflate(TrafficManagerActivity.this,
						R.layout.list_item_trafficinfo, null);
				holder = new ViewHolder();
				holder.iv_app_icon = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				holder.tv_app_name = (TextView) view
						.findViewById(R.id.tv_app_name);
				holder.tv_total = (TextView) view.findViewById(R.id.tv_total);
				holder.tv_rx=(TextView) view.findViewById(R.id.tv_rx);
				holder.tv_tx = (TextView) view.findViewById(R.id.tv_tx);
				view.setTag(holder);
			}
			TrafficInfo trafficInfo = trafficInfos.get(position);
			holder.iv_app_icon.setImageDrawable(trafficInfo.getIcon());
			holder.tv_app_name.setText(trafficInfo.getName());
			long rx = trafficInfo.getRx();
			long tx = trafficInfo.getTx();
			long total = rx + tx;
			holder.tv_total.setText(Formatter.formatFileSize(
					TrafficManagerActivity.this, total));
			holder.tv_tx
					.setText("上传  "
							+ Formatter.formatFileSize(
									TrafficManagerActivity.this, tx));
			holder.tv_rx.setText("下载"+Formatter.formatFileSize(
					TrafficManagerActivity.this, rx));
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
		public ImageView iv_app_icon;
		public TextView tv_app_name;
		public TextView tv_total;
		public TextView tv_tx;
		public TextView tv_rx;
	}
}
