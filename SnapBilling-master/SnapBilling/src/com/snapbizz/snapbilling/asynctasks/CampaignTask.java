package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

import com.j256.ormlite.dao.GenericRawResults;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class CampaignTask extends AsyncTask<String, Void, List<Campaigns>>{
	private static final String TAG=CampaignTask.class.getSimpleName();
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "No Campaign Found";
	private Context context;
	
	public CampaignTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.context = context;
	}
	
	@Override
	protected List<Campaigns> doInBackground(String... params) {
		SnapBizzDatabaseHelper databaseHelper = SnapBillingUtils.getDatabaseHelper(context);
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
             String sqlCampaignsQuery=" select id,campaign_id,campaign_name,start_date,end_date,campaign_type,image_uid,campaign_code,company,has_image,serverImageURL from campaigns where end_date >'"+currentTimestamp+"' and start_date <'"+tomorrwTimestamp+"'";
             GenericRawResults<Campaigns> sqlCampaignsResult=databaseHelper.getCampaignsDao().queryRaw(sqlCampaignsQuery,databaseHelper.getCampaignsDao().getRawRowMapper());
             campaignsList = sqlCampaignsResult.getResults();
     } catch (SQLException e) {
             Log.e(TAG, "Error",e);
     } catch (Exception e) {
             e.printStackTrace();
     }
     
			Log.e("campaignsList_last_show", "campaignsList_last_show");

		return campaignsList;
	}
	@Override
	protected void onPostExecute(List<Campaigns> result) {
		if (result != null && result.size() != 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
}
