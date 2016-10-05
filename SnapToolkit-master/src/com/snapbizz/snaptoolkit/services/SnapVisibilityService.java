package com.snapbizz.snaptoolkit.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.snapbizz.snaptoolkit.asynctasks.StoreCampaignTask;
import com.snapbizz.snaptoolkit.domains.Request;
import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.domains.RetrieveCampaignResponseContainer;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.interfaces.OnServiceCompleteListener;
import com.snapbizz.snaptoolkit.interfaces.OnSyncDateUpdatedListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.DeviceState;
import com.snapbizz.snaptoolkit.utils.RequestCodes;
import com.snapbizz.snaptoolkit.utils.RequestFormat;
import com.snapbizz.snaptoolkit.utils.RequestMethod;
import com.snapbizz.snaptoolkit.utils.ResponseCodes;
import com.snapbizz.snaptoolkit.utils.ResponseFormat;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class SnapVisibilityService extends IntentService implements OnServiceCompleteListener, OnQueryCompleteListener, OnSyncDateUpdatedListener {
    private final int SNAP_VISIBILITY_STORE_TASKCODE = 28;
    private Calendar calendar;
    private Date date;

    private static boolean isRunning;
    private  String timestamp;
    private String imageBaseUrl;
    private Context commonContext;

    public SnapVisibilityService() {
        super("");
    }
    

    @Override
    protected void onHandleIntent(Intent arg0) {
    	Log.e("service started", "service started");
    	 if(isRunning) {
	            stopSelf();
	            return;
	        }
	        try {
	        	Log.d("TAG", "Campaign Log-----  inside SnapVisibilityService---");
	        	commonContext = SnapCommonUtils.getSnapContext(this);
				Request request = new Request();
				request.setRequestFormat(RequestFormat.JSON);
				request.setRequestMethod(RequestMethod.GET);
				HashMap<String, String> requestParamMap = new HashMap<String, String>();
				String storeId = SnapSharedUtils.getStoreId(commonContext);
				String deviceId = SnapSharedUtils.getDeviceId(commonContext);
				String accessToken = SnapSharedUtils.getStoreAuthKey(commonContext);
				requestParamMap.put(SnapToolkitConstants.STORE_ID, storeId);
				Log.d("storeId", storeId);
				requestParamMap.put(SnapToolkitConstants.DEVICE_ID, deviceId);
				Log.d("deviceId", deviceId);
				requestParamMap.put(SnapToolkitConstants.STORE_AUTH_KEY, accessToken);
				Log.d("TAG", "Going to download images campList-----2-"+storeId);
				Log.d("TAG", "Going to download images campList-----2-"+deviceId);
				Log.e("service started", "AutoSyncCamp----------------------------------6");
				
				Log.d("TAG","CampaignSyncTimestamp---------->"+SnapSharedUtils.getLastCampaignRetrievalSyncTimestamp(commonContext));
				
				requestParamMap.put(SnapToolkitConstants.SYNC_TIMESTAMP_KEY, SnapSharedUtils.getLastCampaignRetrievalSyncTimestamp(commonContext));
				
				request.setRequestParams(requestParamMap);
				ServiceRequest serviceRequest = new ServiceRequest(request, this);
				serviceRequest.setMethod(SnapToolkitConstants.CAMPAIGN_SYNC_METHOD);
				serviceRequest.setPath(SnapToolkitConstants.SYNC_PATH);
				serviceRequest.setResponsibleClass(RetrieveCampaignResponseContainer.class);
				serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_TWENTYEIGHT);
				serviceRequest.setResponseFormat(ResponseFormat.JSON);
				ServiceThread serviceThread = new ServiceThread(this, this, false);
				serviceThread.execute(serviceRequest);
				Log.d("TAG", "Campaign Log-----  inside SnapVisibilityService---send request for campain details");
				
	        } catch(Exception e) {
	            stopSelf();
	            e.printStackTrace();
	        }       
    }

    @Override
    public void onSuccess(ResponseContainer response) {
    	Log.d("TAG", "Campaign Log-----  inside SnapVisibilityService--onSuccess-received campaign details");
    	RetrieveCampaignResponseContainer campaignResponseContainer=(RetrieveCampaignResponseContainer) response;

    	if(campaignResponseContainer != null && campaignResponseContainer.getCampList() != null && campaignResponseContainer.getCampList().size()>0){
    	this.timestamp=campaignResponseContainer.timestamp;
    	this.imageBaseUrl=campaignResponseContainer.image_base_url;
    	  Log.d("TAG", "Campaign Log-----  inside SnapVisibilityService--onSuccess-going to store details");
    	  //new StoreCampaignTask(this, this, SNAP_VISIBILITY_STORE_TASKCODE,this.timestamp,this.imageBaseUrl).execute(campaignResponseContainer.getCampList());
    	  new StoreCampaignTask(this, this, SNAP_VISIBILITY_STORE_TASKCODE,this.timestamp,this.imageBaseUrl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,campaignResponseContainer.getCampList());
    	  
    	}else{
    		if (SnapCommonUtils.isNetworkAvailable(this)) {
    		 Log.d("TAG", "Campaign Log-----  inside SnapVisibilityService--no data from server-invoking GetCampaignImageService");	
    		 Intent campaignImageServiceIntent = new Intent(commonContext,GetCampaignImageService.class);
    		 startService(campaignImageServiceIntent);
    		}else{
    			CustomToast.showCustomToast(this
						.getApplicationContext(), "No Network Connection",
						Toast.LENGTH_SHORT, CustomToast.ERROR);
    		}
    	}
    	
    }

    @Override
    public void onError(ResponseContainer response, RequestCodes requestCode) {
    	Log.e("service started", "AutoSyncCamp----------------------------------9");
    	Log.d("TAG", "Going to download images campList-----4-");
        if(response.getResponseCode() == ResponseCodes.BLOCKED.getResponseValue()) {
            SnapSharedUtils.storeDeviceState(DeviceState.DISABLED, commonContext);
        }
		SnapSharedUtils.storeDownloadingId(SnapCommonUtils.getSnapContext(this),0);
        SnapSharedUtils.setCampaignSyncStatus(false,commonContext);
        Intent i = new Intent("android.intent.action.taskcompleted");
		this.sendBroadcast(i);
    }

    @Override
    public void onTaskSuccess(Object responseList, int taskCode) {
    	
    	if (taskCode == SNAP_VISIBILITY_STORE_TASKCODE) {
    		if (SnapCommonUtils.isNetworkAvailable(this)) {
    			Log.d("TAG", "Campaign Log-----  inside SnapVisibilityService--onTaskSuccess-invoking GetCampaignImageService");
       		 	Intent campaignImageServiceIntent = new Intent(this,GetCampaignImageService.class);
       		 	startService(campaignImageServiceIntent);
       		}else{
       			CustomToast.showCustomToast(this
   						.getApplicationContext(), "No Network Connection",
   						Toast.LENGTH_SHORT, CustomToast.ERROR);
       		}
    		
    	}
    	
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        isRunning = false;
    }

    @Override
    public void onTaskError(String errorMessage, int taskCode) {
        // TODO Auto-generated method stub
        Log.d(SnapSyncService.class.getName(), errorMessage);
        Intent i = new Intent("android.intent.action.taskcompleted");
		this.sendBroadcast(i);
    }

    @Override
    public void updateDateValues(String startDate, String EndDate, int taskCode) {
        // TODO Auto-generated method stub
    }

    private String incByFiveDays(String currentDateString) {
        try {
            date = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).parse(currentDateString);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + SnapToolkitConstants.CHUNK_SIZE);
            return new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(calendar.getTime());
        } catch (Exception e) {
            Log.d(SnapSyncService.class.getName(), "inside inc month by one");
            e.printStackTrace();
            return currentDateString;
        }
    }

}

