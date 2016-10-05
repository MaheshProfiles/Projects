package com.snapbizz.snapstock.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.RevenueAnalysisType;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

public class GetRevenueAnalysisTask extends AsyncTask<Void, Void, List<String[]>> {

	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private String errorMessage = "Could not get Revenue Analysis";
	private RevenueAnalysisType revenueAnalysisType;
	
	public GetRevenueAnalysisTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode, RevenueAnalysisType revenueAnalysisType) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.revenueAnalysisType = revenueAnalysisType;
	}

	@Override
	protected List<String[]> doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try {
			SnapBizzDatabaseHelper databaseHelper = SnapInventoryUtils.getDatabaseHelper(context);
			String query = null;
			if(revenueAnalysisType.ordinal() == RevenueAnalysisType.BRAND.ordinal()) {
				query = "select SUM(transaction_details.sku_sale_price), brand.brand_name from transaction_details inner join product_sku on transaction_details.sku_id = product_sku.sku_id inner join brand on brand.brand_id = product_sku.brand_id group by product_sku.brand_id ";
			} else if(revenueAnalysisType.ordinal() == RevenueAnalysisType.CATEGORY.ordinal()) {
				query = "select SUM(transaction_details.sku_sale_price), parent_cat.product_category_name from transaction_details inner join product_sku on transaction_details.sku_id = product_sku.sku_id inner join product_category cat on cat.product_category_id = product_sku.sku_subcategory_id inner join product_category parent_cat on parent_cat.product_category_id = cat.product_parentcategory_id group by cat.product_parentcategory_id";
			} else if(revenueAnalysisType.ordinal() == RevenueAnalysisType.SUBCATEGORY.ordinal()) {
				query = "select SUM(transaction_details.sku_sale_price), product_category.product_category_name from transaction_details inner join product_sku on transaction_details.sku_id = product_sku.sku_id inner join product_category on product_category.product_category_id = product_sku.sku_subcategory_id group by product_category.product_category_id ";				
			} else if(revenueAnalysisType.ordinal() == RevenueAnalysisType.SKU.ordinal()) {
				query = "select SUM(transaction_details.sku_sale_price), product_sku.sku_name from transaction_details inner join product_sku on transaction_details.sku_id = product_sku.sku_id group by product_sku.sku_id ";
			}
			GenericRawResults<String []> rawResults = databaseHelper.getInventorySkuDao().queryRaw(query);
			return rawResults.getResults();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<String[]> result) {
		// TODO Auto-generated method stub
		if(result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
