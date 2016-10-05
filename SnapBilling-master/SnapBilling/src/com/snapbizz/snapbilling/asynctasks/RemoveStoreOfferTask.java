package com.snapbizz.snapbilling.asynctasks;

import net.sqlcipher.Cursor;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class RemoveStoreOfferTask extends AsyncTask<String, Void, Cursor> {

	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "No Product Found";
	private Context context;

	public RemoveStoreOfferTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.context = context;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected Cursor doInBackground(String... params) {
		try {
			String excludedProducts = "";
			int size = context.getResources().getStringArray(
					R.array.quickadd_array).length;
			for (int i = 0; i < size; i++) {
				if (i != size - 1)
					excludedProducts = excludedProducts
							+ "'"
							+ context.getResources().getStringArray(
									R.array.quickadd_array)[i] + "', ";
				else
					excludedProducts = excludedProducts
							+ "'"
							+ context.getResources().getStringArray(
									R.array.quickadd_array)[i] + "'";

			}
			/*String sqlQuery = "select inventory_sku.inventory_sku_id from product_sku "
					+ "left join inventory_sku on inventory_sku.inventory_sku_id=product_sku.sku_id where (inventory_sku.is_offer=1 and inventory_sku.show_store=1) or (inventory_sku.is_offer=1 or inventory_sku.show_store=1) and product_sku.sku_id not in ("
					+ excludedProducts
					+ ") order by inventory_sku.inventory_slno desc ";*/
			
			String sqlQuery=" select inventory_sku.* from inventory_sku inner join product_sku on product_sku.sku_id=inventory_sku.inventory_sku_id inner join brand on brand.brand_id=product_sku.brand_id where product_sku.has_image = 1 and (product_sku.sku_mrp > 0 AND (inventory_sku.is_offer = 1 OR inventory_sku.show_store = 1)) and product_sku.sku_saleprice > 0  " ;

			Cursor cursor = SnapBillingUtils.getDatabaseHelper(context)
					.getReadableDatabase("sn@pb1zz@123")
					.rawQuery(sqlQuery, null);
			cursor.moveToFirst();
			Log.d("deleateLimit", "deleateLimit-----getCount--->"+cursor.getCount());
			int deleateLimit=cursor.getCount()-(SnapToolkitConstants.OFFER_LIMIT*2);
			Log.d("deleateLimit", "deleateLimit-------->"+deleateLimit);
			if(deleateLimit>0){
				String query = "Update inventory_sku set is_offer=0,show_store=0 where inventory_sku_id IN (select inventory_sku.inventory_sku_id from inventory_sku inner join product_sku on product_sku.sku_id=inventory_sku.inventory_sku_id inner join brand on brand.brand_id=product_sku.brand_id where product_sku.has_image = 1 and (product_sku.sku_mrp > 0 AND (inventory_sku.is_offer = 1 OR inventory_sku.show_store = 1)) and product_sku.sku_saleprice > 0   limit "+deleateLimit+")";
				 cursor = SnapBillingUtils.getDatabaseHelper(context)
						.getReadableDatabase("sn@pb1zz@123")
						.rawQuery(query, null);
				cursor.moveToFirst();
				return cursor;
			}else{
				return null;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(Cursor result) {
		Log.d("deleateLimit", "deleateLimit-----result--->"+result);
		if (null != result) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
