package com.snapbizz.snaptoolkit.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;

public class StoreCompanyTask extends AsyncTask<List<Company>, Void, Boolean> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to store records.";
	private SnapBizzDatabaseHelper databaseHelper;
	private String timestamp;

	public StoreCompanyTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, String timestamp, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.timestamp = timestamp;
	}

	@Override
	protected Boolean doInBackground(List<Company>... companyList) {
		// TODO Auto-generated method stub
		try {
			if(companyList[0] == null)
				return false;
			databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
			for(Company company : companyList[0]) {
				databaseHelper.getCompanyDao().create(company);
			}
            SnapSharedUtils.storeLastCompanyRetrievalTimestamp(timestamp, SnapCommonUtils.getSnapContext(context));
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
