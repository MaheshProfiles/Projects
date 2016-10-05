package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snaptoolkit.domains.CustomerPayment;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetSyncCustomerPaymentTask  extends AsyncTask<String, Void, List<CustomerPayment>>{

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to retrieve customer payment.";
	
	public GetSyncCustomerPaymentTask(Context context,
	OnQueryCompleteListener onQueryCompleteListener, int taskCode){
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}
	@Override
	protected List<CustomerPayment> doInBackground(String... timestamp) {
		// TODO Auto-generated method stub
		try {
		    Log.d("", "customer payment timestamp "+timestamp[0]);
			List<CustomerPayment> customerPaymentList = SnapCommonUtils.getSyncDatabaseHelper(context).getCustomerPaymentDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(timestamp[0])).query();
			for(CustomerPayment customerPayment : customerPaymentList) {
			    if(customerPayment != null && customerPayment.getCustomer() != null)
			        customerPayment.setCustomerId(customerPayment.getCustomer().getCustomerId());
			}
			return customerPaymentList;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
	}
	
	@Override
	protected void onPostExecute(List<CustomerPayment> result) {
		// TODO Auto-generated method stub
		if (result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
