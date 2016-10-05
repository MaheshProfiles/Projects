package com.snapbizz.snaptoolkit.asynctasks;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetSyncCategoryTask extends
		AsyncTask<String, Void, List<ProductCategory>> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to retrieve Multiple sale prices.";

	public GetSyncCategoryTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected List<ProductCategory> doInBackground(String... timestamp) {
		// TODO Auto-generated method stub

		    Log.d("TAG", " productCategoryList Multiple sale price timestamp "+timestamp[0]);
		    try {
		    	 //String sqlCampaignsQuery="SELECT * FROM product_category where lastmodified_timestamp > '"+timestamp[0]+"'";
		    	 
		    	 return SnapCommonUtils.getSyncDatabaseHelper(context).getProductCategoryDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(timestamp[0])).query();
		    	 
		    	 
		    	/* Log.d("TAG", "ProductCategory------productCategoryList-sqlCampaignsQuery->"+sqlCampaignsQuery);
	             GenericRawResults<ProductCategory> sqlProductCategoryResult=SnapCommonUtils.getSyncDatabaseHelper(context).getProductCategoryDao().queryRaw(sqlCampaignsQuery,SnapCommonUtils.getSyncDatabaseHelper(context).getProductCategoryDao().getRawRowMapper());
	             List<ProductCategory> list= sqlProductCategoryResult.getResults();
		    	
		    	Log.d("TAG", "ProductCategory------productCategoryList-list->"+list);
		    	Log.d("TAG", "ProductCategory------productCategoryList-list->"+list.size());
		    	return list;*/
		    } catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		
	}

	@Override
	protected void onPostExecute(List<ProductCategory> result) {
		// TODO Auto-generated method stub
		 Log.d("TAG", "ProductCategory------productCategoryList-result->"+result);
		if (result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
