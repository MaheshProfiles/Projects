package com.snapbizz.snaptoolkit.asynctasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import net.sqlcipher.database.SQLiteDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.stmt.UpdateBuilder;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class UpdateSalePriceTask extends AsyncTask<List<ProductCategory>, Void, Boolean> {
	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to update Sale Price.";
	private SnapBizzDatabaseHelper databaseHelper;
	private int categoryId=0;
	private float spValue=0;
	private String salePriceType="";
	/** 
	 * Updating the product category table with sale prices with the settings set
	 */
	public UpdateSalePriceTask(Context context,int taskCode,OnQueryCompleteListener onQueryCompleteListener,
	         Float Spvalue, String SpType, int categoryId) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.spValue = Spvalue;
		this.salePriceType = SpType;
		this.categoryId = categoryId;
	}
	
	public UpdateSalePriceTask(Context context,int taskCode,OnQueryCompleteListener onQueryCompleteListener) {
       this.context = context;
       this.onQueryCompleteListener = onQueryCompleteListener;
       this.taskCode = taskCode;
   }
	
	@Override
	protected Boolean doInBackground(List<ProductCategory>... productCategoryList) {
		try {
			databaseHelper = SnapCommonUtils.getDatabaseHelper(this.context);

			if(productCategoryList != null &&
					productCategoryList.length > 0 && productCategoryList[0] != null) {
				for(ProductCategory productCat : productCategoryList[0]) {
						UpdateBuilder<ProductCategory, Integer> updateBuilder = databaseHelper.getProductCategoryDao().updateBuilder();
						updateBuilder.where().eq("product_category_id", productCat.getCategoryId());
						updateBuilder.updateColumnValue("sku_saleprice", productCat.getProductCategorySalePrice());
			    		updateBuilder.updateColumnValue("sku_saleprice_two", productCat.getProductCategorySalePriceTwo());
			    		updateBuilder.updateColumnValue("sku_saleprice_three", productCat.getProductCategorySalePriceThree());
			    		if(productCat.isClicked() == true) {
			    			String date = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime());
			    			updateBuilder.updateColumnValue("lastmodified_timestamp", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(date));
			    		}
			    		updateBuilder.update();
				}
			} else {
				UpdateBuilder<ProductCategory, Integer> updateBuilder = databaseHelper.getProductCategoryDao().updateBuilder();
				updateBuilder.where().eq("product_category_id", categoryId);
				updateBuilder.updateColumnValue(salePriceType, spValue);

				String date = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime());
				updateBuilder.updateColumnValue("lastmodified_timestamp", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(date));
				updateBuilder.update();
			}
		} catch (Exception e) {
			Log.e(" Updating SalePrice",e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
			Log.e("Result", result+"");
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
}
