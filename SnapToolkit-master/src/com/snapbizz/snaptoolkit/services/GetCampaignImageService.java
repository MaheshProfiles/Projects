package com.snapbizz.snaptoolkit.services;

import java.util.ArrayList;
import java.util.List;
import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.snapbizz.snaptoolkit.asynctasks.GetCampaignImagesTask;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetCampaignImageService extends IntentService implements OnQueryCompleteListener {
	
	private final int GET_CAMPAIGN_IMAGES_TASKCODE = 29;
	private List<Campaigns> campList;
	private static boolean isRunning;
	public GetCampaignImageService() {
		super("");
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		if (isRunning) {
			stopSelf();
			return;
		}
		Bundle bundleObject = intent.getExtras();
		if(bundleObject!=null){
			
			Log.d("TAG", "Campaign Log----- image click request ");
			campList = (ArrayList<Campaigns>) bundleObject.getSerializable("key");
			Log.d("TAG","Going to download images onReceive--static-campList="+campList);
			if(campList!=null&&campList.size()!=0){
				downloadCampaignImage();
			}else{
				dismissProgessBar();
			}
		}else if(SnapSharedUtils.getDownloadingId(SnapCommonUtils.getSnapContext(this))==0){
			Log.d("TAG", "Campaign Log----- invoke GetCampaignImagesTask ");
			new GetCampaignImagesTask(SnapCommonUtils.getSnapContext(this),GET_CAMPAIGN_IMAGES_TASKCODE,this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		SnapSharedUtils.setCampaignSyncStatus(false,SnapCommonUtils.getSnapContext(this));
		if(taskCode==GET_CAMPAIGN_IMAGES_TASKCODE){
			campList = (List<Campaigns>) responseList;
			Log.d("TAG", "Campaign Log----- got campList= "+campList.size());
			if(campList.size()!=0){
				Log.d("TAG", "Going to download images campList-----1---->");
				downloadCampaignImage();
			}else{
				Log.d("TAG", "Campaign Log-----  campList= "+campList.size()+"Stoping progress");
				dismissProgessBar();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isRunning = false;
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		
		Log.e("error send broadcast","Going to download--error send broadcast");
		dismissProgessBar();
	}
	
	private void downloadCampaignImage(){
		Log.d("TAG", "Campaign Log-----  inside downloadCampaignImage");
		DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		Campaigns camp = campList.get(0);
		Log.d("TAG", "Campaign Log-----  inside downloadCampaignImage downloadCampaignImage-"+camp.getServerImageURL());
		if(camp.getServerImageURL()==null||camp.getServerImageURL().trim().equals("")){
			camp.setServerImageURL(SnapToolkitConstants.BASE_CAMPAIGN_URL+camp.getImage_uid());
		}
		Uri Download_Uri = Uri.parse(camp.getServerImageURL());
		DownloadManager.Request request = new DownloadManager.Request(
			Download_Uri);
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
			| DownloadManager.Request.NETWORK_MOBILE);
		request.setAllowedOverRoaming(true);
		long downloadReference = downloadManager.enqueue(request);
		Log.d("TAG", "Campaign Log-----  inside downloadCampaignImage downloadReference="+downloadReference);
		SnapSharedUtils.storeDownloadingId(SnapCommonUtils.getSnapContext(this),downloadReference);
		SnapSharedUtils.storeCurrentCampaign(SnapCommonUtils.getSnapContext(this),camp);
	}
	
	private void dismissProgessBar(){
		SnapSharedUtils.setCampaignSyncStatus(false,SnapCommonUtils.getSnapContext(this));
		SnapSharedUtils.storeDownloadingId(SnapCommonUtils.getSnapContext(this),0);
    	Intent i = new Intent("android.intent.action.taskcompleted");
		this.sendBroadcast(i);
	}

}
