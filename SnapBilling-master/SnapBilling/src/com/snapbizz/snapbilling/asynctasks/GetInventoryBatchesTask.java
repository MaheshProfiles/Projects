package com.snapbizz.snapbilling.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;


public class GetInventoryBatchesTask extends AsyncTask<ProductSku, Void, List<InventoryBatch>> {

	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private String errorMessage = "Could not Get Inventory Batches";

	public GetInventoryBatchesTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected List<InventoryBatch> doInBackground(ProductSku... productSku) {
		try {
			SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
			GenericRawResults<InventoryBatch> rawResults = databaseHelper.getInventoryBatchDao().queryRaw("select a.* from inventory_batch a inner join (select distinct batch_id, sku_mrp, max(batch_id) as id from inventory_batch where sku_available_qty > 0 and sku_id = '"+productSku[0].getProductSkuCode()+"' group by sku_mrp) as b on a.batch_id = b.id and a.sku_mrp = b.sku_mrp where a.sku_available_qty > 0 order by a.batch_timestamp desc limit 2", databaseHelper.getInventoryBatchDao().getRawRowMapper());
			return rawResults.getResults();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<InventoryBatch> result) {
		if(result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
