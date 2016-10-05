package com.snapbizz.snapstock.tabs;

import java.util.HashMap;

import org.achartengine.ChartFactory;
import org.achartengine.renderer.DefaultRenderer;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.snapbizz.snapstock.R;
import com.snapbizz.snaptoolkit.customviews.AbstractSnapChart;
import com.snapbizz.snaptoolkit.domains.Request;
import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.domains.SnapMoneyPieData;
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

public class DashboardMoneyTab extends AbstractSnapChart implements OnServiceCompleteListener {

	private static final String TAG = DashboardMoneyTab.class.getSimpleName();
	private Context mContext;
	private View view;
	private LinearLayout chartLinearLayout;
	private Context commonContext;
	private String accessToken;
	private String storeId;
	private String deviceId;
	private boolean isLoaded;
	private ServiceThread serviceThread;

	public DashboardMoneyTab(Context context) {
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
		view.findViewById(R.id.refresh_money_button).setOnClickListener(onSnapMoneyRefreshClickListener);
	}

	public void loadMoneyTab() {
		if(!isLoaded) {
			SnapMoneyPieData snapMoneyPieData = SnapSharedUtils.getSnapMoney(mContext);
			if(snapMoneyPieData != null) {
				DefaultRenderer renderer = buildCategoryRenderer(snapMoneyPieData.getColors());
				renderer.setApplyBackgroundColor(true);
				//renderer.setBackgroundColor(Color.rgb(222, 222, 200));
				renderer.setLabelsColor(Color.GRAY);
				View chartView = ChartFactory.getPieChartView(mContext,
						buildCategoryDataset(snapMoneyPieData.getLabels(), snapMoneyPieData.getDataPoints()), renderer);
				if(chartLinearLayout == null)
					chartLinearLayout = ((LinearLayout)view.findViewById(R.id.dashboard_moneychart_linearlayout));
				else
					chartLinearLayout.removeAllViews();
				chartLinearLayout.addView(chartView);
			}
		} else {
			onSnapMoneyRefreshClickListener.onClick(null);
		}
	}

	View.OnClickListener onSnapMoneyRefreshClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Request snapMoneyRequest = new Request();
			snapMoneyRequest.setRequestFormat(RequestFormat.MAP);
			snapMoneyRequest.setRequestMethod(RequestMethod.GET);
			HashMap<String, String> requestParamMap = new HashMap<String, String>();
			requestParamMap.put(SnapToolkitConstants.STORE_ID, storeId);
			requestParamMap.put(SnapToolkitConstants.STORE_AUTH_KEY, accessToken);
			requestParamMap.put(SnapToolkitConstants.DEVICE_ID, deviceId);
			snapMoneyRequest.setRequestParams(requestParamMap);
			ServiceRequest serviceRequest = new ServiceRequest(snapMoneyRequest, mContext);
			serviceRequest.setMethod(SnapToolkitConstants.SNAP_MONEY_METHOD);
			serviceRequest.setPath(SnapToolkitConstants.STOCK_DASHBOARD_PATH);
			serviceRequest.setResponsibleClass(SnapMoneyPieData.class);
			serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_ONE);
			if(serviceThread != null && !serviceThread.isCancelled())
				serviceThread.cancel(true);
			serviceThread = new ServiceThread(mContext, DashboardMoneyTab.this, false);
			serviceThread.execute(serviceRequest);
		}
	};

	@Override
	public void onSuccess(ResponseContainer response) {
		// TODO Auto-generated method stub
		if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_ONE.ordinal()) {
			isLoaded = true;
			SnapMoneyPieData snapMoneyPieData = (SnapMoneyPieData) response;
			int [] colors = new int[snapMoneyPieData.getDataPoints().length];
			for(int i = 0; i < colors.length; i++) {
				colors[i] = SnapCommonUtils.getRandomColor();
			}
			snapMoneyPieData.setColors(colors);
			DefaultRenderer renderer = buildCategoryRenderer(snapMoneyPieData.getColors());
			renderer.setApplyBackgroundColor(true);
			//renderer.setBackgroundColor(Color.rgb(222, 222, 200));
			renderer.setLabelsColor(Color.GRAY);
			View chartView = ChartFactory.getPieChartView(mContext,
					buildCategoryDataset(snapMoneyPieData.getLabels(), snapMoneyPieData.getDataPoints()), renderer);
			if(chartLinearLayout == null)
				chartLinearLayout = ((LinearLayout)view.findViewById(R.id.dashboard_moneychart_linearlayout));
			else
				chartLinearLayout.removeAllViews();
			chartLinearLayout.addView(chartView);
			SnapSharedUtils.storeSnapMoney(snapMoneyPieData, mContext);
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
