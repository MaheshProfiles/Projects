package com.snapbizz.snapstock.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

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
		// TODO Auto-generated method stub
		try {
			SnapBizzDatabaseHelper databaseHelper = SnapInventoryUtils.getDatabaseHelper(context);
			GenericRawResults<InventoryBatch> rawResults = databaseHelper.getInventoryBatchDao().queryRaw("select a.* from inventory_batch a inner join (select distinct batch_id, sku_mrp, max(batch_id) as id from inventory_batch where sku_available_qty > 0 and sku_id = '"+productSku[0].getProductSkuCode()+"' group by sku_mrp) as b on a.batch_id = b.id and a.sku_mrp = b.sku_mrp where a.sku_available_qty > 0 order by a.batch_timestamp desc limit 2", databaseHelper.getInventoryBatchDao().getRawRowMapper());
			return rawResults.getResults();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<InventoryBatch> result) {
		// TODO Auto-generated method stub
		if(result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
