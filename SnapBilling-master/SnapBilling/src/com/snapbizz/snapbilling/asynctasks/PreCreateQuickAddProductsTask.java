package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;

public class PreCreateQuickAddProductsTask extends AsyncTask<Void, Void, Hashtable<String,List<ProductSku>>>{

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to Create Quick Add products";
	
	public PreCreateQuickAddProductsTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}
	
	@Override
	protected Hashtable <String,List<ProductSku>> doInBackground(Void... arg0) {
		Hashtable<String,List<ProductSku>> resultList = new Hashtable<String,List<ProductSku>>();
		String sortOrder="sku_name";
		try {
			List<ProductCategory> productCategoryList=SnapBillingUtils.getHelper(context).getProductCategoryDao().queryBuilder().orderBy("product_category_name", true).where().eq("is_quickadd_category", true).query();
			if(SnapSharedUtils.getSortingCheckValue(SnapCommonUtils.getSnapContext(context))){
				sortOrder="sku_name";
			}else{
				sortOrder="sku_id";
			}
			
			for(int i=0;i<productCategoryList.size();i++){
				ProductCategory productCategory=productCategoryList.get(i);
				List<ProductSku> productSkuList=SnapBillingUtils.getHelper(context).getProductSkuDao().queryBuilder().orderBy(sortOrder, true).where().eq("sku_subcategory_id", productCategory.getCategoryId()).and().eq("is_quickadd_product", true).query();
				resultList.put(productCategory.getCategoryId()+"", productSkuList);
			}
			
		} catch (SQLException e) {
            e.printStackTrace();
		}
		return resultList;
	}
	
	@Override
	protected void onPostExecute(Hashtable <String,List<ProductSku>> result) {
		// TODO Auto-generated method stub
		if (result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
		onQueryCompleteListener = null;
		context = null;
	}

}
