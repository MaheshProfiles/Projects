package com.snapbizz.snapbilling.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class AddEditCustomerTask extends AsyncTask<Customer, Void, Customer> {
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private String errorMessage = "Customer could not be created";
	private boolean isEdit = false;
	
	public AddEditCustomerTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode, boolean isEdit) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.isEdit = isEdit;
	}

	@Override
	protected Customer doInBackground(Customer... customers) {
		try {
			Log.d(AddEditCustomerTask.class.getName(),
					customers[0].getCustomerName());
			customers[0].setCustomerName(SnapBillingTextFormatter.capitalseText(customers[0].getCustomerName()));
			if(isEdit) {
				SnapBillingUtils.getHelper(context).getCustomerDao().update(customers[0]);
				 
			} else {
			    SnapBillingUtils.getHelper(context).getCustomerDao().create(customers[0]);
			}
			return customers[0];
		} catch (Exception e) {
			errorMessage = context.getString(R.string.exc_customer_exists);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(Customer result) {
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
