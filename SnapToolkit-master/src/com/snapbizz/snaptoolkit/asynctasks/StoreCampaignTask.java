package com.snapbizz.snaptoolkit.asynctasks;

import java.util.List;

import net.sqlcipher.database.SQLiteDatabase;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.j256.ormlite.stmt.UpdateBuilder;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;

public class StoreCampaignTask extends AsyncTask<List<Campaigns>, Void, Boolean> {
	private final int GET_CAMPAIGN_IMAGES_TASKCODE = 29;
	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to store records.";
	private SnapBizzDatabaseHelper databaseHelper;
	private List<Campaigns> campList;
	private String timestamp;
	private String imageBaseUrl;
	
	public StoreCampaignTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode,String timestamp,String imageBaseUrl) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.timestamp=timestamp;
		this.imageBaseUrl=imageBaseUrl;
	
	}
	
	

	@Override
	protected Boolean doInBackground(List<Campaigns>... campList) {
		
		Log.d("TAG", "Campaign Log-----  inside StoreCampaignTask--doInBackground-going to store details");
		try {
		if(campList[0] == null)
			return false;
		this.campList=campList[0];
		SQLiteDatabase.loadLibs(SnapCommonUtils.getSnapContext(this.context));
		databaseHelper = SnapCommonUtils.getDatabaseHelper(SnapCommonUtils.getSnapContext(this.context));
		for(Campaigns camp : campList[0]) {
			String name = camp.getName().replace("'","''");
			String company = camp.getCompany().replace("'","''");
			camp.setName(name);
			camp.setCompany(company);
			camp.setServerImageURL(imageBaseUrl+camp.getImage_uid());
			if(databaseHelper.getCampaignsDao().queryForEq("id",camp.getId()).isEmpty()){
				camp.setHasImage(false);
				camp.setDownloadId(0);
				camp.setImageRetry(false);
			    databaseHelper.getCampaignsDao().create(camp);
				Log.e("New Camp ID Storing",""+ camp.getId());

			}else{
				UpdateBuilder<Campaigns, Integer> updateBuilder = databaseHelper.getCampaignsDao().updateBuilder();
				updateBuilder.where().eq("id", camp.getId());
				updateBuilder.updateColumnValue("start_date", camp.getStart_date());
	    		updateBuilder.updateColumnValue("end_date", camp.getEnd_date());
	    		updateBuilder.updateColumnValue("campaign_name", camp.getName());
	    		Log.e(" Adding New Camp","Campaign Log------Image_uid-->"+camp.getImage_uid());
	    		updateBuilder.updateColumnValue("image_uid", camp.getImage_uid());
	    		updateBuilder.updateColumnValue("campaign_type", camp.getCampaign_type());
	    		updateBuilder.updateColumnValue("company", camp.getCompany());
	    		updateBuilder.updateColumnValue("campaign_code", camp.getCode());
	    		updateBuilder.updateColumnValue("has_image", false);
	    		updateBuilder.updateColumnValue("image_retry", false);
	    		updateBuilder.updateColumnValue("serverImageURL", camp.getServerImageURL());
	    		updateBuilder.updateColumnValue("download_id", 0);
	    		
	    		updateBuilder.update();
	    		Log.e("Camp ID Updating",""+ camp.getId());
			}
			
		}

		return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		Log.d("TAG", "Campaign Log-----  inside StoreCampaignTask--onPostExecute-result="+result);
		if (result) {
			SnapSharedUtils.storeLastCampaignSyncTimestamp(this.timestamp, SnapCommonUtils.getSnapContext(SnapCommonUtils.getSnapContext(this.context)));
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
			Log.e("Result", result+"");
		} else {

			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
	
}

