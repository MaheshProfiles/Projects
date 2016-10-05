
package com.snapbizz.snapstock.asynctasks;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

public class AddProductToInventoryTask extends AsyncTask<InventorySku, Void, InventorySku> {

    private Context context;
    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private String errorMessage = "Could not add to Inventory";

    public AddProductToInventoryTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }

    @Override
    protected InventorySku doInBackground(InventorySku... inventorySku) {
        // TODO Auto-generated method stub
        try {
            SnapBizzDatabaseHelper databaseHelper = SnapInventoryUtils.getDatabaseHelper(context);
            List<InventorySku> prodList = databaseHelper.getInventorySkuDao().queryForEq("inventory_sku_id", inventorySku[0].getProductSku().getProductSkuCode());
            if(prodList.isEmpty()){
                ProductSku prodSku= databaseHelper.getProductSkuDao().queryForEq("sku_id", inventorySku[0].getProductSku().getProductSkuCode()).get(0);
                 inventorySku[0].setProductSku(prodSku);
                //InventorySku inventorySku = new InventorySku(productSku[0], Calendar.getInstance().getTime());
                inventorySku[0].setOffer(false);
                inventorySku[0].setProductSkuCode(inventorySku[0].getProductSku().getProductSkuCode());
                databaseHelper.getInventorySkuDao().create(inventorySku[0]);
                databaseHelper.getProductSkuDao().update(inventorySku[0].getProductSku());
                Brand brand = databaseHelper.getBrandDao().queryForEq("brand_id", inventorySku[0].getProductSku().getProductBrand().getBrandId()).get(0);
                if(!brand.isMyStore()) {
                    brand.setMyStore(true);
                    databaseHelper.getBrandDao().update(brand);
                }
                databaseHelper.getInventoryBatchDao().create(new InventoryBatch(inventorySku[0].getProductSku().getProductSkuCode(), inventorySku[0].getProductSku().getProductSkuMrp(), 
                        inventorySku[0].getProductSku().getProductSkuSalePrice(), 0, Calendar.getInstance().getTime(), null, inventorySku[0].getQuantity(), -1, 0));
                InventorySku inv= databaseHelper.getInventorySkuDao().queryForEq("inventory_sku_id", inventorySku[0].getProductSkuCode()).get(0);
                return inv;
            }
            else{
                return null;
            }
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
