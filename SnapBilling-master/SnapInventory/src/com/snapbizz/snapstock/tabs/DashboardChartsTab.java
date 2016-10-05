package com.snapbizz.snapstock.tabs;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.asynctasks.GetChartDataTask;
import com.snapbizz.snapstock.utils.ChartFilterType;
import com.snapbizz.snapstock.utils.ChartFilterValueType;
import com.snapbizz.snaptoolkit.adapters.BarchartHorizontalAdapter;
import com.snapbizz.snaptoolkit.domains.BarGraphDataPoint;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;

public class DashboardChartsTab implements OnQueryCompleteListener {

	private static final String TAG = DashboardChartsTab.class.getSimpleName();
	private Context mContext;
	private final int GET_CHARTDATA_TASKCODE = 0;
	private ListView chartListView;
	private BarchartHorizontalAdapter adapter;
	private ChartFilterValueType chartFilterValueType = ChartFilterValueType.REVENUE;
	private ChartFilterType chartFilterType = ChartFilterType.DISTRIBUTOR;
	private GetChartDataTask currentAsyncTask;
	private View view;

	public DashboardChartsTab(Context context) {
		mContext = context;
	}

	public void setView(View view) {
		this.view = view;
		chartListView = (ListView) view.findViewById(R.id.charts);
		((RadioGroup)view.findViewById(R.id.chart_value_filters)).setOnCheckedChangeListener(onChartFilterValueChangeListener);
		((RadioGroup)view.findViewById(R.id.chart_filters)).setOnCheckedChangeListener(onChartFilterChangeListener);
		((RadioButton) view.findViewById(R.id.revenue_button)).setChecked(true);
		((RadioButton) view.findViewById(R.id.distributor_button)).setChecked(true);
	}

	RadioGroup.OnCheckedChangeListener onChartFilterValueChangeListener = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if(checkedId == R.id.revenue_button) {
				chartFilterValueType = ChartFilterValueType.REVENUE;
			} else if(checkedId == R.id.stock_value_button) {
				chartFilterValueType = ChartFilterValueType.STOCK_VALUE;
			} else if(checkedId == R.id.days_stock_button) {
				chartFilterValueType = ChartFilterValueType.DAYS_STOCK;
			}
			if(!currentAsyncTask.isCancelled())
				currentAsyncTask.cancel(true);
			currentAsyncTask = new GetChartDataTask(mContext, DashboardChartsTab.this, GET_CHARTDATA_TASKCODE, chartFilterValueType, chartFilterType);
			currentAsyncTask.execute();
		}
	};

	RadioGroup.OnCheckedChangeListener onChartFilterChangeListener = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if(checkedId == R.id.distributor_button) {
				chartFilterType = ChartFilterType.DISTRIBUTOR;
			} else if(checkedId == R.id.company_button) {
				chartFilterType = ChartFilterType.COMPANY;
			} else if(checkedId == R.id.category_button) {
				chartFilterType = ChartFilterType.CATEGORY;
			}
			if(!currentAsyncTask.isCancelled())
				currentAsyncTask.cancel(true);
			currentAsyncTask = new GetChartDataTask(mContext, DashboardChartsTab.this, GET_CHARTDATA_TASKCODE, chartFilterValueType, chartFilterType);
			currentAsyncTask.execute();
		}
	};

	public void loadChartsTab() {
		currentAsyncTask = new GetChartDataTask(mContext, this, GET_CHARTDATA_TASKCODE, chartFilterValueType, chartFilterType);
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
				adapter.adjustBarWidths((int)(chartListView.getWidth() * 0.8));
				if(chartFilterValueType.ordinal() == ChartFilterValueType.DAYS_STOCK.ordinal())
					adapter.setPriceChart(false);
				else
					adapter.setPriceChart(true);
				chartListView.setAdapter(adapter);
			} else {
				adapter.clear();
				adapter.addAll((List<BarGraphDataPoint>) responseList);
				adapter.adjustBarWidths((int)(chartListView.getWidth() * 0.8));
				if(chartFilterValueType.ordinal() == ChartFilterValueType.DAYS_STOCK.ordinal())
					adapter.setPriceChart(false);
				else
					adapter.setPriceChart(true);
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		// TODO Auto-generated method stub
		if(adapter != null) {
			adapter.clear();
			adapter.notifyDataSetChanged();
		}
		CustomToast.showCustomToast(mContext, errorMessage, Toast.LENGTH_SHORT, CustomToast.ERROR);
	}
}
