package com.snapbizz.snapbilling.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.domains.CustomerPayment;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class GetPaymentHistoryTask extends AsyncTask<Customer, Void, List<CustomerPayment>> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to retrieve Customer payment.";

	public GetPaymentHistoryTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected List<CustomerPayment> doInBackground(Customer... customer) {
		try {
		    if(customer[0]!=null)
		    return SnapBillingUtils.getHelper(context).getCustomerPaymentDao().queryForEq("customer_id", customer[0].getCustomerId());
		    else
		    return null;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<CustomerPayment> result) {
		if (result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
