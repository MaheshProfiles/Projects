package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.domains.Request;
import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.domains.StreamResponseContainer;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.interfaces.OnServiceCompleteListener;
import com.snapbizz.snaptoolkit.services.ServiceRequest;
import com.snapbizz.snaptoolkit.services.ServiceThread;
import com.snapbizz.snaptoolkit.utils.RequestCodes;
import com.snapbizz.snaptoolkit.utils.RequestMethod;
import com.snapbizz.snaptoolkit.utils.ResponseFormat;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetImageDownloadAckmentTask extends AsyncTask<Void, Void, Boolean>
		implements OnServiceCompleteListener,OnQueryCompleteListener {
	private Context context;
	private String campID="";
	private String imgDwnldTimestamp = "";
	private OnQueryCompleteListener onQueryCompleteListener;
	private int taskCode;
	private final String errorMessage = "Acknowledgement failed";
	
	public GetImageDownloadAckmentTask(Context context,OnQueryCompleteListener onQueryCompleteListener,
			String mCampID,int pos,int taskCode) {
		this.context = context;
		campID = mCampID;
		this.onQueryCompleteListener=onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected Boolean doInBackground(Void... timestamp) {
		try {
			Log.d("TAG", "Campaign Log-----  inside GetImageDownloadAckmentTask-campID="+campID);
			Date dt = new Date();
			dt.setTime(dt.getTime() + 1000);
			imgDwnldTimestamp = new SimpleDateFormat(
					SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,
					Locale.getDefault()).format(dt);
			Request imageRequest = new Request();
			imageRequest.setRequestMethod(RequestMethod.GET);
			HashMap<String, String> requestParamMap = new HashMap<String, String>();
			requestParamMap.put(SnapToolkitConstants.STORE_ID, SnapSharedUtils
					.getStoreId(SnapCommonUtils.getSnapContext(context)));
			requestParamMap.put(SnapToolkitConstants.DEVICE_ID, SnapSharedUtils
					.getDeviceId(SnapCommonUtils.getSnapContext(context)));
			requestParamMap.put(SnapToolkitConstants.STORE_AUTH_KEY,
					SnapSharedUtils.getStoreAuthKey(SnapCommonUtils
							.getSnapContext(context)));
            Log.e("Request_Time_dwonload_time", "campID = "+campID);

				requestParamMap.put(SnapToolkitConstants.CAMPAIGN_ID,campID);
				requestParamMap.put("downloaded_time", imgDwnldTimestamp);
				imageRequest.setRequestParams(requestParamMap);
				ServiceRequest serviceRequest = new ServiceRequest(
						imageRequest, context);
				serviceRequest
						.setMethod(SnapToolkitConstants.DOWNLOAD_TIME_METHOD);
				serviceRequest.setPath(SnapToolkitConstants.CAMPAIGN_PATH);
				serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_THIRTY);
				serviceRequest
						.setResponsibleClass(StreamResponseContainer.class);
				serviceRequest.setResponseFormat(ResponseFormat.FILE);
				ServiceThread serviceThread = new ServiceThread(context, this,
						false);
				serviceThread.execute(serviceRequest);
				
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}
	
	
	@Override
	protected void onPostExecute(Boolean result) {
		Log.d("TAG", "Campaign Log-----  inside GetImageDownloadAckmentTask-result="+result);
		if (result) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

	@Override
	public void onSuccess(ResponseContainer response) {
		Log.e("Ack", "campaignsList---onSuccess--"+response.getResponseMessage());
		if (response.getRequestCode() == RequestCodes.REQUEST_CODE_THIRTY) {
		}
	
	}

	@Override
	public void onError(ResponseContainer response, RequestCodes requestCode) {
		Log.e("Ack", "campaignsList---onError--"+response.getResponseMessage());
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		Log.e("Ack", responseList.toString());
		Log.e("Ack", "campaignsList---onTaskSuccess--"+responseList.toString());
		
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		// TODO Auto-generated method stub
		
	}

}
