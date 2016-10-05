package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class GetSubCategoryProductsTask extends AsyncTask<Integer, Void, List<ProductSku>>{
	
	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "";
	
	public GetSubCategoryProductsTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}
	
	@Override
	protected List<ProductSku> doInBackground(Integer... subcategoryId) {
		try {
			QueryBuilder<ProductSku, Integer> productSkuQueryBuilder = SnapBillingUtils.getHelper(context).getProductSkuDao().queryBuilder();
			productSkuQueryBuilder.where().eq("sku_subcategory_id", subcategoryId[0]);
			return productSkuQueryBuilder.join(SnapBillingUtils.getHelper(context).getInventorySkuDao().queryBuilder()).query();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(List<ProductSku> result) {
		if(result != null) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
	}
	
}
