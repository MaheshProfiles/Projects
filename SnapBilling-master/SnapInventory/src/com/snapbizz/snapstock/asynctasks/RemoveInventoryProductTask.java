package com.snapbizz.snapstock.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.DeletedRecords;
import com.snapbizz.snaptoolkit.domains.DistributorProductMap;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.TableType;

public class RemoveInventoryProductTask extends
AsyncTask<InventorySku, Void, InventorySku> {

    private Context context;
    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private final String errorMessage = "Could not create new product, product may already exist";

    public RemoveInventoryProductTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }

    @Override
    protected InventorySku doInBackground(InventorySku... inventorySku) {
        try {
            SnapBizzDatabaseHelper databaseHelper = SnapInventoryUtils.getDatabaseHelper(context);
            if(inventorySku[0].getProductSku().isQuickAddProduct()) {
            	inventorySku[0].getProductSku().setQuickAddProduct(false);
            	databaseHelper.getProductSkuDao().update(inventorySku[0].getProductSku());
            }
            inventorySku[0].setTaxRate(0f);
            inventorySku[0].setQuantity(0);
            inventorySku[0].setPurchasePrice(0f);
            databaseHelper.getInventorySkuDao().delete(inventorySku[0]);
            List<String []> resultList = databaseHelper.getInventoryBatchDao().queryRaw("select * from inventory_sku inner join product_sku on inventory_sku.inventory_sku_id = product_sku.sku_id where product_sku.brand_id = '"+inventorySku[0].getProductSku().getProductBrand().getBrandId()+"'").getResults();
            if(resultList.size() == 0) {
                databaseHelper.getInventorySkuDao().executeRaw("update brand set is_mystore = 0 where brand_id = "+inventorySku[0].getProductSku().getProductBrand().getBrandId());
            }
            List<InventoryBatch> inventoryBatchList = databaseHelper.getInventoryBatchDao().queryForEq("sku_id", inventorySku[0].getProductSku().getProductSkuCode());
            for(InventoryBatch inventoryBatch : inventoryBatchList) {
                DeletedRecords deletedRecord = new DeletedRecords();
                deletedRecord.setRecordId(inventoryBatch.getSlNo()+"");
                deletedRecord.setTableType(TableType.INVENTORY_BATCh);
                databaseHelper.getDeletedRecordsDao().createOrUpdate(deletedRecord);
            }
            List<DistributorProductMap> distributorProductList = databaseHelper.getDistributorProductMapDao().queryForEq("sku_id", inventorySku[0].getProductSku().getProductSkuCode());
            for (DistributorProductMap distributorProductMap : distributorProductList) {
                DeletedRecords deletedRecords = new DeletedRecords();
                deletedRecords.setRecordId(String.valueOf(distributorProductMap.getDistributorProductSkuId()));
                deletedRecords.setTableType(TableType.DISTRIBUTOR_PRODUCT_MAP);
                databaseHelper.getDeletedRecordsDao().create(deletedRecords);
            }
            DeleteBuilder<DistributorProductMap, Integer> distributorProductDeleteBuilder = databaseHelper.getDistributorProductMapDao().deleteBuilder();
            distributorProductDeleteBuilder.where().eq("sku_id", inventorySku[0].getProductSku().getProductSkuCode());
            distributorProductDeleteBuilder.delete();
            DeleteBuilder<InventoryBatch, Integer> deleteBuilder = databaseHelper.getInventoryBatchDao().deleteBuilder();
            deleteBuilder.where().eq("sku_id", inventorySku[0].getProductSku().getProductSkuCode());
            deleteBuilder.delete();
            DeletedRecords deletedRecord = new DeletedRecords();
            deletedRecord.setRecordId(inventorySku[0].getSlNo()+"");
            deletedRecord.setTableType(TableType.INVENTORY_SKU);
            databaseHelper.getDeletedRecordsDao().createOrUpdate(deletedRecord);
            if(!inventorySku[0].getProductSku().isGDB()) {
                deletedRecord = new DeletedRecords();
                deletedRecord.setRecordId(inventorySku[0].getProductSku().getProductSkuCode());
                deletedRecord.setTableType(TableType.PRODUCT_SKU);
                databaseHelper.getDeletedRecordsDao().createOrUpdate(deletedRecord);
                databaseHelper.getProductSkuDao().delete(inventorySku[0].getProductSku());
            }
            return inventorySku[0];
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(InventorySku result) {
        // TODO Auto-generated method stub
        if (result != null ) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }
}
