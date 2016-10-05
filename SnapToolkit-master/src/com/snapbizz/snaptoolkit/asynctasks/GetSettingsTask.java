package com.snapbizz.snaptoolkit.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.Settings;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetSettingsTask extends AsyncTask<Settings, Void, List<Settings>> {
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "Unable to get Settings details.";
	private Context context;
	private int taskCode;
	 /** 
	 * Retrieve the settings From Database and update
	 */
	public GetSettingsTask(Context context,
			int taskCode, OnQueryCompleteListener onQueryCompleteListener) {
		this.context = context;
		this.taskCode = taskCode;
		this.onQueryCompleteListener = onQueryCompleteListener;
	}

	@Override
	protected List<Settings> doInBackground(Settings... params) {
		// TODO Auto-generated method stub
		SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
		try {
			List<Settings> settingsList =databaseHelper.getSettingsDao().queryBuilder().query();
			return settingsList;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	@Override
	protected void onPostExecute( List<Settings> result) {
		// TODO Auto-generated method stub
		if (result != null && result.size() != 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
			if(SnapSharedUtils.getSettingsNameValue(context)==false){
				SnapSharedUtils.storePrinterStoreName(result.get(0).getStoreName(),context);
				SnapSharedUtils.storePrinterStoreAddress(result.get(0).getStoreAddress(),context);
				SnapSharedUtils.storePrinterStoreNumber(result.get(0).getContactNumber(),context);
				SnapSharedUtils.storePrinterStoreTinNumber(result.get(0).getStoreTinNumber(),context);
				SnapSharedUtils.storePrinterStoreCity(result.get(0).getStoreCity(),context);
				SnapSharedUtils.storePrinterFooter1(result.get(0).getFooter1(),context);
				SnapSharedUtils.storePrinterFooter2(result.get(0).getFooter2(),context);
				SnapSharedUtils.storePrinterFooter3(result.get(0).getFooter3(),context);
				SnapSharedUtils.setSettingsNameValue(true,context);
			}
			
		} else{
//			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}


}
