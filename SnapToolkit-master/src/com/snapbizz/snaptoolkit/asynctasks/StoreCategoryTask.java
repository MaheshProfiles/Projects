package com.snapbizz.snaptoolkit.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;

public class StoreCategoryTask extends AsyncTask<List<ProductCategory>, Void, Boolean> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to store records.";
	private SnapBizzDatabaseHelper databaseHelper;
	private String timestamp;
	
	public StoreCategoryTask(Context context,int taskCode,OnQueryCompleteListener onQueryCompleteListener,String timestamp) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.timestamp=timestamp;
	}
	
	@Override
	protected Boolean doInBackground(List<ProductCategory>...categoryList) {
		// TODO Auto-generated method stub
		try {
			if(categoryList[0] == null)
				return false;
			//databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
			for(ProductCategory productCategory : categoryList[0]) {
				//databaseHelper.getProductCategoryDao().update(productCategory);
			}
            SnapSharedUtils.storeLastCompanyRetrievalTimestamp(timestamp, SnapCommonUtils.getSnapContext(context));
			return true;
		} catch (Exception e) {
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
