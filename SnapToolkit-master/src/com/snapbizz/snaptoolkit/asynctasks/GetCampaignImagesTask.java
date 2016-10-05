package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import net.sqlcipher.database.SQLiteDatabase;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class GetCampaignImagesTask extends
		AsyncTask<Void, Void, List<Campaigns>> {
	
	private Context context;
	ProgressDialog progressDialog;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "No Updated Campaigns to download";

	public GetCampaignImagesTask(Context context, int taskCode,OnQueryCompleteListener onQueryCompleteListener) {
		Log.d("TAG", "Campaign Log-----  inside GetCampaignImagesTask--");
		this.context = context;
		this.taskCode=taskCode;
		this.onQueryCompleteListener = onQueryCompleteListener;
		Log.d("TAG", "Campaign Log-----  inside GetCampaignImagesTask--1");
	}

	@Override
	protected List<Campaigns> doInBackground(Void... params) {

		Log.d("TAG", "Campaign Log-----  inside doInBackground--");
		SQLiteDatabase.loadLibs(SnapCommonUtils.getSnapContext(this.context));
		SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(SnapCommonUtils.getSnapContext(this.context));
		List<Campaigns> campaignsList= new ArrayList<Campaigns>();
		 try {
         DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); 
         Calendar cal = Calendar.getInstance();
         Calendar cal1 = Calendar.getInstance();
         cal.add(Calendar.DATE, +1);
         cal1.add(Calendar.DATE, -1);
         Date tomorrw = dateFormat.parse(dateFormat.format(cal.getTime())); 
         Date yesterDay = dateFormat.parse(dateFormat.format(cal1.getTime())); 
             Timestamp tomorrwTimestamp = new Timestamp(tomorrw.getTime());
             Timestamp currentTimestamp = new Timestamp(yesterDay.getTime());
             String sqlCampaignsQuery="select id,campaign_id,campaign_name,start_date,end_date,campaign_type,image_uid,campaign_code,company,serverImageURL  from campaigns where end_date >'"+currentTimestamp+"' and start_date <'"+tomorrwTimestamp+"' and (has_image=0 or has_image=null) and (download_id!=8 or download_id=null) and (image_retry=0 or image_retry=null) LIMIT 1 ";
             GenericRawResults<Campaigns> sqlCampaignsResult=databaseHelper.getCampaignsDao().queryRaw(sqlCampaignsQuery,databaseHelper.getCampaignsDao().getRawRowMapper());
             campaignsList = sqlCampaignsResult.getResults();
             Log.d("TAG", "Campaign Log-----  inside GetCampaignImagesTask--doInBackground-image_retry false---campaignsList size="+campaignsList.size());
             if(campaignsList.size()==0 || campaignsList.isEmpty()){
            	 sqlCampaignsQuery="select id,campaign_id,campaign_name,start_date,end_date,campaign_type,image_uid,campaign_code,company,serverImageURL  from campaigns where end_date >'"+currentTimestamp+"' and start_date <'"+tomorrwTimestamp+"' and (has_image=0 or has_image=null) and (download_id!=8 or download_id=null) and (image_retry=1 or image_retry=null) LIMIT 1 ";
                 sqlCampaignsResult=databaseHelper.getCampaignsDao().queryRaw(sqlCampaignsQuery,databaseHelper.getCampaignsDao().getRawRowMapper());
                 campaignsList = sqlCampaignsResult.getResults();
                 Log.d("TAG", "Campaign Log-----  inside GetCampaignImagesTask--doInBackground-image_retry true---campaignsList size="+campaignsList.size());
             }
             
     } catch (SQLException e) {
             Log.e("", "Error",e);
     } catch (Exception e) {
             e.printStackTrace();
             Log.e("", "Error",e);
     }
	
	
		 return campaignsList;

	}
	@Override
	protected void onPostExecute(List<Campaigns> result) {
		Log.d("TAG", "Campaign Log-----  inside GetCampaignImagesTask--onPostExecute----result="+result.size());
		if (result != null) {
			Log.d("TAG","Going to download images campList--------result"+result.size());
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
