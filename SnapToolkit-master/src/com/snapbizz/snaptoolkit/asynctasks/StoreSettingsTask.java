package com.snapbizz.snaptoolkit.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.UpdateBuilder;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.domains.Settings;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class StoreSettingsTask extends AsyncTask<List<Settings>, Void, Boolean> {
	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private SnapBizzDatabaseHelper databaseHelper;
	private final String errorMessage = "Unable to store records.";

	public StoreSettingsTask(Context context,
			int taskCode, OnQueryCompleteListener onQueryCompleteListener) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}
	  /** 
		 * Store the settings To Database and update
		 */
	@Override
	protected Boolean doInBackground(List<Settings>... settingsList) {
		 try {
	            if(settingsList[0] == null)
	                return false;
	            databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
	            for(Settings settings: settingsList[0]) {
	                Settings settingsDb = databaseHelper.getSettingsDao().queryForFirst(databaseHelper.getSettingsDao().queryBuilder().prepare());
	                if(settingsDb != null) {
//	                    databaseHelper.getSettingsDao().update(settings);
	                    UpdateBuilder<Settings, Integer> updateBuilder = databaseHelper.getSettingsDao().updateBuilder();
	    				updateBuilder.where().eq("settings_id", 1);
	    				updateBuilder.updateColumnValue("store_name", settings.getStoreName());
	    	    		updateBuilder.updateColumnValue("store_address", settings.getStoreAddress());
	    	    		updateBuilder.updateColumnValue("contact_number", settings.getContactNumber());
	    	    		updateBuilder.updateColumnValue("store_tin_number", settings.getStoreTinNumber());
	    	    		updateBuilder.updateColumnValue("store_city", settings.getStoreCity());
	    	    		updateBuilder.updateColumnValue("footer_1", settings.getFooter1());
	    	    		updateBuilder.updateColumnValue("footer_2", settings.getFooter2());
	    	    		updateBuilder.updateColumnValue("footer_3", settings.getFooter3());
	    	    		updateBuilder.updateColumnValue("show_savings", settings.isShowSavings());
	    	    		updateBuilder.updateColumnValue("sort_order", settings.isSortOrder());
	    	    		updateBuilder.updateColumnValue("roundoff_status", settings.isRoundOffStatus());
	    	    		updateBuilder.updateColumnValue("pin_number", settings.getPinNumber());
	    	    		updateBuilder.update();
	                } else {
	                    databaseHelper.getSettingsDao().create(settings);
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
        if (result) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }
	
}
