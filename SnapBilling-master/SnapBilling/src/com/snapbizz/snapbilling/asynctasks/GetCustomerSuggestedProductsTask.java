package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.CustomerSuggestions;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

public class GetCustomerSuggestedProductsTask extends AsyncTask<Integer, Void, List<ProductSku>> {
    
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "Could not get Suggested Products";
	private SnapBizzDatabaseHelper databaseHelper;
	
	public GetCustomerSuggestedProductsTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected List<ProductSku> doInBackground(Integer... params) {
		try {
			databaseHelper = SnapBillingUtils.getHelper(context);
			Log.d(""," searching customer id "+params[0]);
			QueryBuilder<CustomerSuggestions, Integer> customerSuggestionsQueryBuilder = databaseHelper.getCustomerSuggestionsDao().queryBuilder();
			customerSuggestionsQueryBuilder.where().eq("customer_id", params[0]);
			Log.d(""," query "+databaseHelper.getProductSkuDao().queryBuilder().join(customerSuggestionsQueryBuilder).prepareStatementString());
			Log.d("","size "+databaseHelper.getProductSkuDao().queryBuilder().join(customerSuggestionsQueryBuilder).query().size());
			return databaseHelper.getProductSkuDao().queryBuilder().join(customerSuggestionsQueryBuilder).query();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<ProductSku> result) {
		if (result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
		onQueryCompleteListener = null;
        context = null;
	}

}
