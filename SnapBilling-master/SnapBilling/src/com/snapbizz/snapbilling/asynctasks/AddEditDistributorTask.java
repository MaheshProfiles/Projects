package com.snapbizz.snapbilling.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class AddEditDistributorTask extends AsyncTask<Distributor, Void, Distributor> {
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private String errorMessage;
	private boolean isEdit = false;
	
	public AddEditDistributorTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode, boolean isEdit) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.isEdit = isEdit;
	}

	@Override
	protected Distributor doInBackground(Distributor... distributor) {
		try {
			Log.d(AddEditDistributorTask.class.getName(),
					distributor[0].getAgencyName());
			distributor[0].setAgencyName(SnapBillingTextFormatter.capitalseText(distributor[0].getAgencyName()));
			Log.d("TAG","AddEditDistributorTask-------isEdit-->"+isEdit);
			if(isEdit) {
				int x = SnapBillingUtils.getHelper(context).getDistributorDao().update(distributor[0]);
				Log.d("TAG","AddEditDistributorTask-------isEdit-x->"+x);
			} else {
				List<Distributor> distributorList=SnapBillingUtils.getHelper(context).getDistributorDao().queryBuilder().where().like("phone_number", "%" + distributor[0].getPhoneNumber() + "%").query();
				if(distributorList.isEmpty()){
					SnapBillingUtils.getHelper(context).getDistributorDao().create(distributor[0]);
				}else{
					 errorMessage = this.context.getResources().getString(R.string.distributor_phonenumber_exist);
					 return null;
				}
			}
			return distributor[0];
		} catch (Exception e) {
			errorMessage = this.context.getResources().getString(R.string.distributor_not_created);
			return null;
		}
	}

	@Override
	protected void onPostExecute(Distributor result) {
		if (result != null) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
		onQueryCompleteListener = null;
        context = null;
	}

}
