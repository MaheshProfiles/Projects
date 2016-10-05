package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class QueryProductCategories extends AsyncTask<Void, Void, List<ProductCategory>>{

	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "No Categories Found";
//	private final String query = "SELECT `a`.* FROM `product_category` 'a' INNER JOIN `product_category` 'b' ON `a`.`product_category_id` = `b`.`product_parentcategory_id` INNER JOIN `product_sku` ON `b`.`product_category_id` = `product_sku`.`sku_subcategory_id` INNER JOIN `inventory_sku` ON `product_sku`.`sku_id` = `inventory_sku`.`inventory_sku_id` WHERE `a`.`product_parentcategory_id` = -1";

	public QueryProductCategories(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected List<ProductCategory> doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try {
//			GenericRawResults<ProductCategory> rawResults = getHelper(context).getProductCategoryDao().queryRaw(query, getHelper(context).getProductCategoryDao().getRawRowMapper());
//			List<ProductCategory> productCategoryList = new ArrayList<ProductCategory>();
//			for(ProductCategory productCategory : rawResults) {
//				productCategoryList.add(productCategory);
//			}
//			return productCategoryList;
		//	return getHelper(context).getProductCategoryDao().queryForEq("product_parentcategory_id", -1);
			
			return SnapCommonUtils.getDatabaseHelper(context).getProductCategoryDao().queryForEq("product_parentcategory_id", -1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<ProductCategory> result) {
		// TODO Auto-generated method stub
		if(result != null && result.size() != 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
	}

}
