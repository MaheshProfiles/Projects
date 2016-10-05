package com.snapbizz.snapstock.asynctasks;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

public class AddScannedProductToInventoryTask extends AsyncTask<String, Void, InventorySku> {

	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private String errorMessage = "No Product Found";

	public AddScannedProductToInventoryTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected InventorySku doInBackground(String... barcode) {
		// TODO Auto-generated method stub
		try {
			errorMessage = barcode[0];
			Log.d(AddScannedProductToInventoryTask.class.getName(), barcode[0]);
			SnapBizzDatabaseHelper databaseHelper = SnapInventoryUtils.getDatabaseHelper(context);
			QueryBuilder<ProductSku, Integer> productSkuQueryBuilder = databaseHelper.getProductSkuDao().queryBuilder();
			List<ProductSku> productSkuList = productSkuQueryBuilder.where().eq("sku_id", barcode[0]).query();
			if(productSkuList.size() > 0) {
				List<InventorySku> inventorySkuList = databaseHelper.getInventorySkuDao().queryBuilder().join(productSkuQueryBuilder).query();
				if(inventorySkuList.size() > 0)
					return inventorySkuList.get(0);
				else {
					InventorySku inventorySku = new InventorySku(productSkuList.get(0), Calendar.getInstance().getTime());
					databaseHelper.getInventoryBatchDao().create(new InventoryBatch(inventorySku.getProductSku().getProductSkuCode(), inventorySku.getProductSku().getProductSkuMrp(), 
		                    inventorySku.getProductSku().getProductSkuSalePrice(), 0, Calendar.getInstance().getTime(), null, inventorySku.getQuantity(), -1, 0));
			        inventorySku.setProductSkuCode(inventorySku.getProductSku().getProductSkuCode());
			        databaseHelper.getProductSkuDao().update(inventorySku.getProductSku());
	                Brand brand = databaseHelper.getBrandDao().queryForEq("brand_id", inventorySku.getProductSku().getProductBrand().getBrandId()).get(0);
	                if(!brand.isMyStore()) {
	                    brand.setMyStore(true);
	                    databaseHelper.getBrandDao().update(brand);
	                }
					databaseHelper.getInventorySkuDao().create(inventorySku);	
					databaseHelper.getInventoryBatchDao().create(new InventoryBatch(inventorySku.getProductSku().getProductSkuCode(), inventorySku.getProductSku().getProductSkuMrp(), 
			        inventorySku.getProductSku().getProductSkuSalePrice(), 0, Calendar.getInstance().getTime(), null, inventorySku.getQuantity(), -1, 0));
			        InventorySku inv= databaseHelper.getInventorySkuDao().queryForEq("inventory_sku_id", inventorySku.getProductSkuCode()).get(0);
					return inv;
				}
			} else 
				return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(InventorySku result) {
		// TODO Auto-generated method stub
		if(result!=null) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
