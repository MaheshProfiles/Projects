package com.snapbizz.snaptoolkit.asynctasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.domains.CustomerPayment;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class StoreCustomerPaymentsTask  extends AsyncTask<List<CustomerPayment>, Void, Boolean>{
	
	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to store records.";
	private SnapBizzDatabaseHelper databaseHelper;

	public StoreCustomerPaymentsTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected Boolean doInBackground(List<CustomerPayment>... customerList) {
		try {
			if(customerList[0] == null)
				return false;
			databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
            Customer customer = new Customer();
			for(CustomerPayment customerPayment : customerList[0]) {
			    customer.setCustomerId(customerPayment.getCustomerId());
			    customerPayment.setCustomer(customer);
				databaseHelper.getCustomerPaymentDao().create(customerPayment);
			}
			String timestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime());
            SnapSharedUtils.storeLastCustomerPaymentSyncTimestamp(timestamp, SnapCommonUtils.getSnapContext(context));
			return true;
		} catch (java.sql.SQLException e) {
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
