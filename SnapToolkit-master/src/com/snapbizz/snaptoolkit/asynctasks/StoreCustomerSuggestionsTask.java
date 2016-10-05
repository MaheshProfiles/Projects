package com.snapbizz.snaptoolkit.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snaptoolkit.domains.CustomerSuggestions;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;

public class StoreCustomerSuggestionsTask  extends AsyncTask<List<CustomerSuggestions>, Void, Boolean> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private String timestamp;
	private String errorMessage = "Unable to store customer suggestions";
	
	public StoreCustomerSuggestionsTask(Context context,
	OnQueryCompleteListener onQueryCompleteListener, int taskCode, String timestamp) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.timestamp = timestamp;
	}
	
	@Override
	protected Boolean doInBackground(List<CustomerSuggestions>... customerSuggestionList) {
		// TODO Auto-generated method stub
		try {
		    SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
		    if (customerSuggestionList[0].size() == 0) {
		    	errorMessage = "No new customer suggestions found"; 
		    	return false;
		    }
		    for(CustomerSuggestions customerSuggestion : customerSuggestionList[0]) {
		        Log.d("","customer suggestion "+customerSuggestion.getCustomer().getCustomerId()+" prod "+customerSuggestion.getProductSku().getProductSkuCode());
		        databaseHelper.getCustomerSuggestionsDao().createOrUpdate(customerSuggestion);
		    }
		    SnapSharedUtils.storeLastCustomerSuggestionsSyncTimestamp(timestamp, SnapCommonUtils.getSnapContext(context));
		    return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
