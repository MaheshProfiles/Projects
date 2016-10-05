package com.snapbizz.snapstock.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;

import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

public class UpdateInventoryBatchTask extends AsyncTask<List<InventoryBatch>, Void, InventorySku> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "";
	private ProgressDialog progressDialog;
	private SnapBizzDatabaseHelper databaseHelper;
	
	public UpdateInventoryBatchTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		progressDialog = new ProgressDialog(context);
		progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		progressDialog.show();
//		progressDialog.setContentView(R.layout.dialog_progress_layout);
//		progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
//		progressDialog.setOnCancelListener(onDialogCancelListener);
//		progressDialog.setCancelable(true);
//		progressDialog.setCanceledOnTouchOutside(false);
	}
	
	@Override
	protected InventorySku doInBackground(List<InventoryBatch>... params) {
		try {
			databaseHelper = SnapInventoryUtils.getDatabaseHelper(context);
			InventorySku inventorySku = null;
			for(InventoryBatch inventoryBatch: params[0]) {
				databaseHelper.getInventoryBatchDao().update(inventoryBatch);
				Log.d(""," batch "+inventoryBatch.getSlNo()+" sales price "+inventoryBatch.getBatchSalesPrice()+" pp "+inventoryBatch.getBatchPurchasePrice());
				List<InventoryBatch> inventoryBatchList = databaseHelper.getInventoryBatchDao().queryBuilder().orderBy("batch_timestamp", false).where().eq("sku_id", inventoryBatch.getProductSku().getProductSkuCode()).query();
				List<InventorySku> inventorySkuList = databaseHelper.getInventorySkuDao().queryForEq("inventory_sku_id", inventoryBatch.getProductSku().getProductSkuCode());
				inventorySku = inventorySkuList.get(0);
				if(inventoryBatchList.get(0).getSlNo() == inventoryBatch.getSlNo()) {
					inventorySku.setPurchasePrice(inventoryBatch.getBatchPurchasePrice());
					ProductSku productSku = inventorySku.getProductSku();
					productSku.setProductSkuMrp(inventoryBatch.getBatchMrp());
					productSku.setProductSkuSalePrice(inventoryBatch.getBatchSalesPrice());
					if(productSku.getProductSkuMrp() == productSku.getProductSkuAlternateMrp()) {
					    productSku.setProductSkuAlternateMrp(0);
					    productSku.setProductSkuAltSalePrice(0);
					}
					databaseHelper.getProductSkuDao().update(productSku);
					databaseHelper.getInventorySkuDao().update(inventorySku);
				} else if(inventoryBatchList.get(1).getSlNo() == inventoryBatch.getSlNo()) {
					ProductSku productSku = inventorySku.getProductSku();
					productSku.setProductSkuAlternateMrp(inventoryBatch.getBatchMrp());
					productSku.setProductSkuAltSalePrice(inventoryBatch.getBatchSalesPrice());
					if(productSku.getProductSkuMrp() == productSku.getProductSkuAlternateMrp()) {
                        productSku.setProductSkuAlternateMrp(0);
                        productSku.setProductSkuAltSalePrice(0);
                    }
					databaseHelper.getProductSkuDao().update(productSku);
				}
			}
			return inventorySku;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(InventorySku result) {
		// TODO Auto-generated method stub
		progressDialog.cancel();
		if(result != null) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
	}

}
