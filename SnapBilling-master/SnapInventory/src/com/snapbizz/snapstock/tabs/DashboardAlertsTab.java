package com.snapbizz.snapstock.tabs;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.asynctasks.GetAlertDataTask;
import com.snapbizz.snapstock.utils.AlertFilterType;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.adapters.BarchartHorizontalAdapter;
import com.snapbizz.snaptoolkit.asynctasks.SaveViewToFileTask;
import com.snapbizz.snaptoolkit.domains.BarGraphDataPoint;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;

public class DashboardAlertsTab implements OnQueryCompleteListener {

	private static final String TAG = DashboardAlertsTab.class.getSimpleName();
	private Context mContext;
	private final int GET_CHARTDATA_TASKCODE = 0;
	private final int SAVE_IMAGE_TASKCODE = 1;
	private ListView chartListView;
	private BarchartHorizontalAdapter adapter;
	private AlertFilterType chartFilterType = AlertFilterType.LOW_STOCK;
	private GetAlertDataTask currentAsyncTask;
	private View view;
	private Bitmap bitmap;

	public DashboardAlertsTab(Context context) {
		mContext = context;
	}

	public void setView(View view) {
		this.view = view;
		chartListView = (ListView) view.findViewById(R.id.charts);
		((RadioGroup)view.findViewById(R.id.chart_filters)).setOnCheckedChangeListener(onChartFilterChangeListener);
	}

	RadioGroup.OnCheckedChangeListener onChartFilterChangeListener = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if(checkedId == R.id.lowstock_button) {
				chartFilterType = AlertFilterType.LOW_STOCK;
			} else if(checkedId == R.id.outofstock_button) {
				chartFilterType = AlertFilterType.OUT_OF_STOCK;
			} else if(checkedId == R.id.slowstock_button) {
				chartFilterType = AlertFilterType.SLOW_STOCK;
			} else if(checkedId == R.id.expiringstock_button) {
				chartFilterType = AlertFilterType.EXPIRING_STOCK;
			} else if(checkedId == R.id.expiredstock_button) {
				chartFilterType = AlertFilterType.EXPIRED_STOCK;
			}
			if(!currentAsyncTask.isCancelled())
				currentAsyncTask.cancel(true);
			currentAsyncTask = new GetAlertDataTask(mContext, DashboardAlertsTab.this, GET_CHARTDATA_TASKCODE, chartFilterType);
			currentAsyncTask.execute();
		}
	};

	public void loadAlertsTab() {
		currentAsyncTask = new GetAlertDataTask(mContext, this, GET_CHARTDATA_TASKCODE, chartFilterType);
		currentAsyncTask.execute();
		if(adapter != null)
			chartListView.setAdapter(adapter);
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		// TODO Auto-generated method stub
		if(taskCode == GET_CHARTDATA_TASKCODE) {
			List<BarGraphDataPoint> barGraphDataPointList = (List<BarGraphDataPoint>) responseList;
			if(adapter == null) {
				adapter = new BarchartHorizontalAdapter(mContext, barGraphDataPointList);
				adapter.setLayoutId(R.layout.listitem_alert_barchart);
				adapter.adjustBarWidths((int)(chartListView.getWidth() * 0.8));
				chartListView.setAdapter(adapter);
			} else {
				adapter.clear();
				adapter.addAll((List<BarGraphDataPoint>) responseList);
				adapter.adjustBarWidths((int)(chartListView.getWidth() * 0.8));
				adapter.notifyDataSetChanged();
			}
			view.setDrawingCacheBackgroundColor(Color.WHITE);
			view.setDrawingCacheEnabled(true);
			view.buildDrawingCache(true);
			this.bitmap = view.getDrawingCache(true);
			Handler h = new Handler();
			h.postDelayed(r, 1000);
		}
	}

	Runnable r = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			new SaveViewToFileTask(DashboardAlertsTab.this , SAVE_IMAGE_TASKCODE, SnapInventoryUtils.getAlertWidgetImagePath(mContext), bitmap).executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR);
		}
	};

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		// TODO Auto-generated method stub
		if(taskCode == GET_CHARTDATA_TASKCODE) {
			if(adapter != null) {
				adapter.clear();
				adapter.notifyDataSetChanged();
			}
		}
		CustomToast.showCustomToast(mContext, errorMessage, Toast.LENGTH_SHORT, CustomToast.ERROR);
	}
}
