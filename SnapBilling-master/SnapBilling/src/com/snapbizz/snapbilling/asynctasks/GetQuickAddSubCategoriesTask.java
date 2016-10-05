package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class GetQuickAddSubCategoriesTask extends AsyncTask<Void, Void, List<ProductCategory>> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to Get Quick Add Categories";

	public GetQuickAddSubCategoriesTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}
	
	@Override
	protected List<ProductCategory> doInBackground(Void... params) {
		try {
			return SnapBillingUtils.getHelper(context).getProductCategoryDao().queryBuilder().orderBy("product_category_name", true).where().eq("is_quickadd_category", true).query();
		} catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
	}

	@Override
	protected void onPostExecute(List<ProductCategory> result) {
		if (result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
		onQueryCompleteListener = null;
		context = null;
	}
}
