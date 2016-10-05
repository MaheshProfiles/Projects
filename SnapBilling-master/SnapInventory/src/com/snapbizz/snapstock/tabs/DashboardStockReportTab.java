package com.snapbizz.snapstock.tabs;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.adapters.StockReportAdapter;
import com.snapbizz.snaptoolkit.domains.Request;
import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.domains.StockReport;
import com.snapbizz.snaptoolkit.domains.StockReportResponseContainer;
import com.snapbizz.snaptoolkit.interfaces.OnServiceCompleteListener;
import com.snapbizz.snaptoolkit.services.ServiceRequest;
import com.snapbizz.snaptoolkit.services.ServiceThread;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.RequestCodes;
import com.snapbizz.snaptoolkit.utils.RequestFormat;
import com.snapbizz.snaptoolkit.utils.RequestMethod;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.StockReportFilterType;

public class DashboardStockReportTab implements OnServiceCompleteListener {

	private static final String TAG = DashboardStockReportTab.class.getSimpleName();
	private Context mContext;
	private ListView stockReportListView;
	private StockReportAdapter adapter;
	private StockReportFilterType stockReportFilterType = StockReportFilterType.DISTRIBUTOR;
	private View view;
	private Context commonContext;
	private String storeId;
	private String accessToken;
	private String deviceId;
	private boolean isLoaded;
	private ServiceThread serviceThread;

	public DashboardStockReportTab(Context context) {
		mContext = context;
		commonContext = SnapCommonUtils.getSnapContext(mContext);
		if(storeId == null) {
			storeId = SnapSharedUtils.getStoreId(commonContext);
			deviceId = SnapSharedUtils.getDeviceId(commonContext);
			accessToken = SnapSharedUtils.getStoreAuthKey(commonContext);
		}
	}

	public void setView(View view) {
		this.view = view;
		isLoaded = false;
		stockReportListView = (ListView) view.findViewById(R.id.stockreport_listview);
		stockReportListView.addHeaderView(((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.header_stock_report, null));
		((RadioGroup)view.findViewById(R.id.stock_filter_radiogroup)).setOnCheckedChangeListener(onStockReportFilterChangeListener);
		if(adapter != null) {
			stockReportListView.setAdapter(adapter);
		}
		view.findViewById(R.id.refresh_stockreport_button).setOnClickListener(onStockReportRefreshClickListener);
	}

	RadioGroup.OnCheckedChangeListener onStockReportFilterChangeListener = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if(checkedId == R.id.filter_distributor_button) {
				stockReportFilterType = StockReportFilterType.DISTRIBUTOR;
			} else if(checkedId == R.id.filter_category_button) {
				stockReportFilterType = StockReportFilterType.CATEGORY;
			}
			((TextView)view.findViewById(R.id.filter_headername_textview)).setText(stockReportFilterType.getStockReportFilterTypeValue());
			List<StockReport> stockReportList = SnapSharedUtils.getStockReportList(stockReportFilterType, mContext);
			Log.d("", " "+stockReportFilterType.getStockReportFilterTypeValue()+"  "+stockReportList);
			if(stockReportList != null) {
				if(adapter == null) {
					adapter = new StockReportAdapter(mContext, stockReportList);
					stockReportListView.setAdapter(adapter);
				} else {
					adapter.clear();
					adapter.addAll(stockReportList);
					adapter.notifyDataSetChanged();
				}
			} else {
				if(adapter != null) {
					adapter.clear();
					adapter.notifyDataSetChanged();
				}
				onStockReportRefreshClickListener.onClick(null);
			}
		}
	};

	public void loadStockReportTab() {
		if(!isLoaded) {
			List<StockReport> stockReportList = SnapSharedUtils.getStockReportList(stockReportFilterType, mContext);
			if(stockReportList != null) {
				isLoaded = true;
				if(adapter == null) {
					adapter = new StockReportAdapter(mContext, stockReportList);
					stockReportListView.setAdapter(adapter);
				} else {
					adapter.clear();
					adapter.addAll(stockReportList);
					adapter.notifyDataSetChanged();
				}
			} else {
				if(adapter != null) {
					adapter.clear();
					adapter.notifyDataSetChanged();
				}
				onStockReportRefreshClickListener.onClick(null);
			}
			((TextView)view.findViewById(R.id.filter_headername_textview)).setText(stockReportFilterType.getStockReportFilterTypeValue());
		}
	}

	View.OnClickListener onStockReportRefreshClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Request stockReportSyncRequest = new Request();
			stockReportSyncRequest.setRequestFormat(RequestFormat.MAP);
			stockReportSyncRequest.setRequestMethod(RequestMethod.GET);
			HashMap<String, String> requestParamMap = new HashMap<String, String>();
			requestParamMap.put(SnapToolkitConstants.STORE_ID, storeId);
			requestParamMap.put(SnapToolkitConstants.STORE_AUTH_KEY, accessToken);
			requestParamMap.put(SnapToolkitConstants.DEVICE_ID, deviceId);
			requestParamMap.put(SnapToolkitConstants.STOCK_REPORT_KEY, stockReportFilterType.getStockReportFilterTypeValue());
			stockReportSyncRequest.setRequestParams(requestParamMap);
			ServiceRequest serviceRequest = new ServiceRequest(stockReportSyncRequest, mContext);
			serviceRequest.setMethod(SnapToolkitConstants.STOCK_REPORT_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.STOCK_DASHBOARD_PATH);
			serviceRequest.setResponsibleClass(StockReportResponseContainer.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_ONE);
			if(serviceThread != null && !serviceThread.isCancelled())
				serviceThread.cancel(true);
			serviceThread = new ServiceThread(mContext, DashboardStockReportTab.this, false);
			serviceThread.execute(serviceRequest);
		}
	};

	@Override
	public void onSuccess(ResponseContainer response) {
		// TODO Auto-generated method stub
		if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_ONE.ordinal()) {
			isLoaded = true;
			StockReportResponseContainer stockRequestResponseContainer = (StockReportResponseContainer) response;
			if(adapter == null) {
				adapter = new StockReportAdapter(mContext, stockRequestResponseContainer.getStockReportList());
				stockReportListView.setAdapter(adapter);
			} else {
				adapter.clear();
				adapter.addAll(stockRequestResponseContainer.getStockReportList());
				adapter.notifyDataSetChanged();
			}
			SnapSharedUtils.storeStockReportList(stockRequestResponseContainer.getStockReportList(), stockReportFilterType, mContext);
		}
	}

	@Override
	public void onError(ResponseContainer response, RequestCodes requestCode) {
		// TODO Auto-generated method stub
	    if(response == null) {
            CustomToast.showCustomToast(mContext, "No Network Connection", Toast.LENGTH_SHORT, CustomToast.ERROR);
        } else {
            CustomToast.showCustomToast(mContext, response.getResponseMessage(), Toast.LENGTH_SHORT, CustomToast.ERROR);
        }
	}
}
