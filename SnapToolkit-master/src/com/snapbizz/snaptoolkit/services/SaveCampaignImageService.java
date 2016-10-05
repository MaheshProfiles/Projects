package com.snapbizz.snaptoolkit.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.snapbizz.snaptoolkit.asynctasks.GetImageDownloadAckmentTask;
import com.snapbizz.snaptoolkit.asynctasks.StoreCampaignImageBitmapTask;
import com.snapbizz.snaptoolkit.asynctasks.UpdateCampaignDetailsTask;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class SaveCampaignImageService extends IntentService implements OnQueryCompleteListener{
	
	private final int STORE_CAMPAIGN_IMAGES_TASKCODE = 30;
	private final int CAMPAIGN_ACKNOWLEDGEMENT_TASKCODE = 31;
	private final int UPDATE_CAMPAIGN_TASKCODE = 32;
	
	private static boolean isRunning;
	private FileInputStream fileInputStream;
	private int status;
	private Campaigns currentDownloadedCampaign;
	
	public SaveCampaignImageService() {
		super("");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		if (isRunning) {
			stopSelf();
			return;
		}
		Log.d("TAG", "Campaign Log-----  inside SaveCampaignImageService SP download Id="+SnapSharedUtils.getDownloadingId(SnapCommonUtils.getSnapContext(this)));
		if(SnapSharedUtils.getDownloadingId(SnapCommonUtils.getSnapContext(this))!=0){
			isRunning=true;
			DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
			DownloadManager.Query query = new DownloadManager.Query();
			query.setFilterById(SnapSharedUtils.getDownloadingId(SnapCommonUtils.getSnapContext(this)));
			android.database.Cursor cursor = downloadManager.query(query);
			if (cursor.moveToFirst()) {
				int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
				status = cursor.getInt(columnIndex);
				Log.d("TAG", "Campaign Log-----  inside SaveCampaignImageService-status="+status);
				if (status == DownloadManager.STATUS_SUCCESSFUL) {
					ParcelFileDescriptor file;
					try {
						file = downloadManager.openDownloadedFile(SnapSharedUtils.getDownloadingId(SnapCommonUtils.getSnapContext(this)));
						fileInputStream= new ParcelFileDescriptor.AutoCloseInputStream(file);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					currentDownloadedCampaign=SnapSharedUtils.getCurrentCampaign(SnapCommonUtils.getSnapContext(this));
					new StoreCampaignImageBitmapTask(this, this,fileInputStream,STORE_CAMPAIGN_IMAGES_TASKCODE,currentDownloadedCampaign.getImage_uid()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
		cursor.close();
		}else{
			Intent i = new Intent("android.intent.action.taskcompleted");
	    	this.sendBroadcast(i);
		}
		
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		if(taskCode==STORE_CAMPAIGN_IMAGES_TASKCODE){
			if(SnapCommonUtils.checkProductDrawable(SnapToolkitConstants.VISIBILITY_IMAGE_PATH+"campaign/"+currentDownloadedCampaign.getImage_uid(), SnapCommonUtils.getSnapContext(this))!=null){
				Log.d("TAG", "Campaign Log-----  inside SaveCampaignImageService---onTaskSuccess--sending Ackment");
				new GetImageDownloadAckmentTask(this,this,currentDownloadedCampaign.getId()+"",0,CAMPAIGN_ACKNOWLEDGEMENT_TASKCODE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
		}else if(taskCode==CAMPAIGN_ACKNOWLEDGEMENT_TASKCODE){
			Log.d("TAG", "Campaign Log-----  inside SaveCampaignImageService---onTaskSuccess--updateing DB");
			new UpdateCampaignDetailsTask(this,this,currentDownloadedCampaign.getId()+"",UPDATE_CAMPAIGN_TASKCODE,this.status,true,false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}else if(taskCode==UPDATE_CAMPAIGN_TASKCODE){
			Log.d("TAG", "Campaign Log-----  inside SaveCampaignImageService---onTaskSuccess--updateCampaignUI=");
			updateCampaignUI();
			SnapSharedUtils.storeDownloadingId(SnapCommonUtils.getSnapContext(this),0);
			Log.d("TAG", "Campaign Log-----  inside SaveCampaignImageService SP download Id="+SnapSharedUtils.getDownloadingId(SnapCommonUtils.getSnapContext(this)));
			Intent campaignImageServiceIntent = new Intent(this,GetCampaignImageService.class);
			startService(campaignImageServiceIntent);	
		}
		
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		Log.d("TAG", "Campaign Log-----  inside SaveCampaignImageService-onTaskError");
		new UpdateCampaignDetailsTask(this,this,currentDownloadedCampaign.getId()+"",UPDATE_CAMPAIGN_TASKCODE,0,false,true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		isRunning = false;
	}
	
	private void updateCampaignUI(){
		Intent i = new Intent("android.intent.action.downloadcompleted");
   	 	this.sendBroadcast(i);
	}

}
