package com.snapbizz.snapbilling.asynctasks;

import net.sqlcipher.Cursor;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;



public class SearchInventoryCursorTask extends AsyncTask<String, Void, Cursor> {

	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "No Product Found";
	private Context context;

	public SearchInventoryCursorTask(Context context,
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
			if (context == null)
				return null;
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
			int limit=SnapToolkitConstants.OFFER_LIMIT*2;
			String sqlQuery = "select inventory_sku.inventory_sku_id,inventory_sku.inventory_sku_quantity,inventory_sku.is_offer,inventory_sku.purchase_price,product_sku.vat,inventory_sku.show_store,inventory_sku.inventory_slno,"
					+ "product_sku.sku_id,product_sku.sku_name,product_sku.sku_mrp,product_sku.sku_subcategory_id,product_sku.product_imageurl,product_sku.sku_saleprice,product_sku.sku_units,product_sku.brand_id,product_sku.lastmodified_timestamp,inventory_sku.lastmodified_timestamp,inventory_sku.inventory_slno,product_sku.category_name,product_sku.subcategory_name,inventory_sku._id,product_sku._id,product_sku.is_gdb from product_sku "
					+ "left join inventory_sku on inventory_sku.inventory_sku_id=product_sku.sku_id where (inventory_sku.is_offer=1 and inventory_sku.show_store=1) or (inventory_sku.is_offer=1 or inventory_sku.show_store=1) and product_sku.sku_id not in ("
					+ excludedProducts
					+ ") order by inventory_sku.inventory_slno desc limit "+limit;

			Cursor cursor = SnapBillingUtils.getDatabaseHelper(context)
					.getReadableDatabase("sn@pb1zz@123")
					.rawQuery(sqlQuery, null);
			cursor.moveToFirst();
			limit=0;
			return cursor;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(Cursor result) {
		if (null != result) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
}
