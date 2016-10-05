package com.snapbizz.snapstock.asynctasks;

import net.sqlcipher.Cursor;
import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetBrandInventoryProductsTask extends
AsyncTask<Integer, Void, Cursor> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "";
	private int subcategoryId;
	
	public GetBrandInventoryProductsTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode,int subcategoryId) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.subcategoryId=subcategoryId;
	}

	@Override
	protected Cursor doInBackground(Integer... brand) {
		/*	SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils
					.getDatabaseHelper(context);
			QueryBuilder<Brand, Integer> brandQueryBuilder = databaseHelper.getBrandDao().queryBuilder();
			if(brand.length>0)
			brandQueryBuilder.where().in("brand_id", brand);
			
			QueryBuilder<ProductSku, Integer> productSkuQueryBuilder = databaseHelper.getProductSkuDao()
					.queryBuilder();*/
		SnapToolkitConstants.IS_REPORT = "0";

	    String brands= "";
        int size=brand.length;
        for(int i =0;i<size;i++){
            if(i!=size-1)
                brands = brands + brand[i]+", ";
            else
                brands = brands +brand[i];

        }
	           if(subcategoryId>0){
			  String sqlQuery = "select inventory_sku.inventory_sku_id,inventory_sku.inventory_sku_quantity,inventory_sku.is_offer,inventory_sku.purchase_price,product_sku.vat,inventory_sku.show_store,inventory_sku.inventory_slno," +
	                    "product_sku.sku_id,product_sku.sku_name,product_sku.sku_mrp,product_sku.sku_subcategory_id,product_sku.product_imageurl,product_sku.sku_saleprice,product_sku.sku_units,product_sku.brand_id,product_sku.lastmodified_timestamp,inventory_sku.lastmodified_timestamp,inventory_sku.inventory_slno,product_sku.category_name,product_sku.subcategory_name,inventory_sku._id,product_sku._id,product_sku.is_gdb,product_sku.sku_saleprice_two,product_sku.sku_saleprice_three from product_sku " +
	                    "join inventory_sku on inventory_sku.inventory_sku_id = product_sku.sku_id where product_sku.brand_id in ("+ brands +") and product_sku.sku_subcategory_id = "+ subcategoryId +"";
			  Cursor cursor=SnapCommonUtils.getDatabaseHelper(context).getReadableDatabase("sn@pb1zz@123").rawQuery(sqlQuery, null);
	            cursor.moveToFirst();
	            return cursor;
	           }else
	               return null;
		/*	productSkuQueryBuilder.where().eq("sku_subcategory_id", subcategoryId);
			productSkuQueryBuilder.join(brandQueryBuilder);
			Log.d(GetBrandInventoryProductsTask.class.getName(), "query "+databaseHelper
					.getInventorySkuDao().queryBuilder().join(productSkuQueryBuilder).prepareStatementString());
			QueryBuilder<InventorySku, Integer> invQueryBuilder = databaseHelper
					.getInventorySkuDao().queryBuilder().join(productSkuQueryBuilder);
			CloseableIterator<InventorySku> iterator = databaseHelper
                    .getInventorySkuDao().iterator(invQueryBuilder.prepare());
			try {
			   // get the raw results which can be cast under Android
			   AndroidDatabaseResults results =
			       (AndroidDatabaseResults)iterator.getRawResults();
			   Cursor cursor = results.getRawCursor();
			   return cursor;
			} finally {
		//	   iterator.closeQuietly();
			}*/
		} 
	

	@Override
	protected void onPostExecute(Cursor result) {
		// TODO Auto-generated method stub
		if (result != null) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
	}

}
