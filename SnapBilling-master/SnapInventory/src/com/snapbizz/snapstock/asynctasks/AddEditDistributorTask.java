package com.snapbizz.snapstock.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snapstock.utils.SnapBillingTextFormatter;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class AddEditDistributorTask extends AsyncTask<Distributor, Void, Distributor> {
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "Distributor could not be created";
	private boolean isEdit = false;
	
	public AddEditDistributorTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode, boolean isEdit) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.isEdit = isEdit;
	}

	@Override
	protected Distributor doInBackground(Distributor... customers) {
		try {
			Log.d(AddEditDistributorTask.class.getName(),
					customers[0].getAgencyName());
			customers[0].setAgencyName(SnapBillingTextFormatter.capitalseText(customers[0].getAgencyName()));
			if(isEdit) {
				com.snapbizz.snapstock.utils.SnapBillingUtils.getHelper(context).getDistributorDao().update(customers[0]);
				 
			} else {
				com.snapbizz.snapstock.utils.SnapBillingUtils.getHelper(context).getDistributorDao().create(customers[0]);
			}
			return customers[0];
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(Distributor result) {
		// TODO Auto-generated method stub
		if (result != null) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
		onQueryCompleteListener = null;
        context = null;
	}

}
