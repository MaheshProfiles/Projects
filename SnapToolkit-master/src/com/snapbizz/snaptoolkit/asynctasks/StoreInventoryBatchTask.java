package com.snapbizz.snaptoolkit.asynctasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sqlcipher.database.SQLiteDatabase;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.Order;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class StoreInventoryBatchTask extends AsyncTask<List<InventoryBatch>, Void, Boolean>{

    private OnQueryCompleteListener onQueryCompleteListener;
    private Context context;
    private int taskCode;
    private final String errorMessage = "Unable to store records.";
    private SnapBizzDatabaseHelper databaseHelper;

    public StoreInventoryBatchTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }

    public void updateInventoryBatch(SnapBizzDatabaseHelper databaseHelper) {
        try {
            GenericRawResults<InventorySku> inventorySkuList =  databaseHelper.getInventorySkuDao().queryRaw("select inventory_sku.* from inventory_sku left outer join inventory_batch on inventory_sku.inventory_sku_id = inventory_batch.sku_id where inventory_batch.sku_id IS NULL", databaseHelper.getInventorySkuDao().getRawRowMapper());
            for(InventorySku inventorySku : inventorySkuList) {
                InventoryBatch inventoryBatch;
                Date timestamp = null;
                try {
                    timestamp = new SimpleDateFormat(SnapToolkitConstants.STANDARD_DATEFORMAT).parse(inventorySku.getTimestamp());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    timestamp = Calendar.getInstance().getTime();
                    e.printStackTrace();
                }
                if(inventorySku.getProductSku() != null) {
                    inventoryBatch = new InventoryBatch(inventorySku.getProductSku().getProductSkuCode(), inventorySku.getProductSku().getProductSkuMrp(), inventorySku.getProductSku().getProductSkuSalePrice(), inventorySku.getPurchasePrice(), timestamp, null, inventorySku.getQuantity(), 0, inventorySku.getTaxRate());
                    databaseHelper.getInventoryBatchDao().create(inventoryBatch);
                    Log.d("","created batch for "+inventorySku.getProductSku().getProductSkuCode());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected Boolean doInBackground(List<InventoryBatch>... inventoryBatchList) {
        try {
        	SQLiteDatabase.loadLibs(SnapCommonUtils.getSnapContext(this.context));
            databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
            if(inventoryBatchList[0] == null) {
                updateInventoryBatch(databaseHelper);
                return false;
            }
            for(InventoryBatch inventoryBatch : inventoryBatchList[0]) {
                ProductSku productSku = new ProductSku();
                productSku.setProductSkuCode(inventoryBatch.getProductSkuCode());
                inventoryBatch.setProductSku(productSku);
                Order order = new Order();
                order.setOrderNumber(inventoryBatch.getOrderId());
                inventoryBatch.setBatchOrder(order);
                databaseHelper.getInventoryBatchDao().create(inventoryBatch);
            }
            String timestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime());
            SnapSharedUtils.storeLastInventoryBatchSyncTimestamp(timestamp, SnapCommonUtils.getSnapContext(context));
            updateInventoryBatch(databaseHelper);
            return true;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            updateInventoryBatch(databaseHelper);
            return false;
        }
    }
    @Override
    protected void onPostExecute(Boolean result) {
        // TODO Auto-generated method stub
        if (result) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }


}
