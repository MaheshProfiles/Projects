package com.snapbizz.snapbilling.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class QueryCustomerInfoTask extends
AsyncTask<String, Void, List<Customer>> {
    private Context context;
    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private String errorMessage = "No Customer Found";
    private boolean isCustomerSort = false;

    public QueryCustomerInfoTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
    	this.context = context;
    	this.onQueryCompleteListener = onQueryCompleteListener;
    	this.taskCode = taskCode;
    }
    public QueryCustomerInfoTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode, boolean isCustomerSort) {
    	this(context, onQueryCompleteListener, taskCode);
    	this.isCustomerSort = isCustomerSort;
    }

    @Override
    protected List<Customer> doInBackground(String... params) {
		try {
			if (!params[0].equals(""))
				return SnapBillingUtils.getHelper(context).getCustomerDao()
						.queryBuilder().where()
						.like("customer_phone", "%" + params[0] + "%").or()
						.like("customer_name", "%" + params[0] + "%").or()
						.like("customer_address", "%" + params[0] + "%")
						.query();
			else if (isCustomerSort)
				return SnapBillingUtils.getHelper(context).getCustomerDao()
						.queryBuilder().orderBy("amount_due", false).query();
			else
				return SnapBillingUtils.getHelper(context).getCustomerDao()
						.queryBuilder().orderBy("customer_name", true).query();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }

    @Override
    protected void onPostExecute(List<Customer> result) {
        if (result != null && result.size() > 0) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
        onQueryCompleteListener = null;
        context = null;
    }

}
