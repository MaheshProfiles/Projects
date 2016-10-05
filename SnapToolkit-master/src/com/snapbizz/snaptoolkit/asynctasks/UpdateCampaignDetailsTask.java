package com.snapbizz.snaptoolkit.asynctasks;

import net.sqlcipher.database.SQLiteDatabase;

import com.j256.ormlite.stmt.UpdateBuilder;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class UpdateCampaignDetailsTask extends AsyncTask<Void, Void, Boolean>{
	private OnQueryCompleteListener onQueryCompleteListener;
	private int taskCode;
	private final String errorMessage = "Unable to Update Campaign Details";
	private String campID="";
	private int status;
	private boolean hasImage;
	private boolean imageRetry;
	private Context context;
	
	public UpdateCampaignDetailsTask(Context context,OnQueryCompleteListener onQueryCompleteListener,
			String mCampID,int taskCode,int status,boolean hasImage,boolean imageRetry) {
		this.context = context;
		this.campID = mCampID;
		this.onQueryCompleteListener=onQueryCompleteListener;
		this.taskCode = taskCode;
		this.status=status;
		this.hasImage=hasImage;
		this.imageRetry=imageRetry;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			Log.d("TAG", "Campaign Log-----  inside UpdateCampaignDetailsTask-");

			SQLiteDatabase.loadLibs(SnapCommonUtils.getSnapContext(this.context));
			SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(SnapCommonUtils.getSnapContext(this.context));
			UpdateBuilder<Campaigns, Integer> updateBuilder = databaseHelper.getCampaignsDao().updateBuilder();
			updateBuilder.where().eq("id", campID);
			updateBuilder.updateColumnValue("has_image", this.hasImage);
			updateBuilder.updateColumnValue("download_id", this.status);
			updateBuilder.updateColumnValue("image_retry", this.imageRetry);
			updateBuilder.update();
		}catch(Exception e){
			return false;
		}
		return true;
	}
	@Override
	protected void onPostExecute(Boolean result) {
		Log.d("TAG", "Campaign Log-----  inside UpdateCampaignDetailsTask-result="+result);
		if (result) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
}
