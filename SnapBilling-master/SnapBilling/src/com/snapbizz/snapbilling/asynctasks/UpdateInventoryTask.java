package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Window;


import com.j256.ormlite.stmt.UpdateBuilder;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class UpdateInventoryTask extends AsyncTask<String, Void, Boolean> {

    private OnQueryCompleteListener onQueryCompleteListener;
    private Context context;
    private int taskCode;
    private final String errorMessage = "";
    private ProgressDialog progressDialog;
    private SnapBizzDatabaseHelper databaseHelper;
    private String fieldName;
    private String skuId;

    public UpdateInventoryTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode,String fieldName,String skuID) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
        this.fieldName=fieldName;
        this.skuId = skuID;
        progressDialog = new ProgressDialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        //		progressDialog.setContentView(R.layout.dialog_progress_layout);
        //		progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        //		progressDialog.setOnCancelListener(onDialogCancelListener);
        //		progressDialog.setCancelable(true);
        //		progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected synchronized Boolean doInBackground(String... params) {
        try {
            databaseHelper = SnapBillingUtils.getDatabaseHelper(context);
            if(fieldName.equals("vat")){
                UpdateBuilder<ProductSku, Integer> updateBuilder = databaseHelper.getProductSkuDao().updateBuilder();
                // set the criteria like you would a QueryBuilder
                updateBuilder.where().eq("sku_id", skuId);
                // update the value of your field(s)
                updateBuilder.updateColumnValue(fieldName, params[0]);
                String date = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime());
                updateBuilder.updateColumnValue("lastmodified_timestamp", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(date));
                updateBuilder.update();
            }
            else if(fieldName.equals("purchase_price")){
                UpdateBuilder<InventorySku, Integer> updateBuilder = databaseHelper.getInventorySkuDao().updateBuilder();
                // set the criteria like you would a QueryBuilder
                updateBuilder.where().eq("inventory_sku_id", skuId);
                // update the value of your field(s)
                updateBuilder.updateColumnValue(fieldName, params[0]);
                String date = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime());
                updateBuilder.updateColumnValue("lastmodified_timestamp", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(date));
                updateBuilder.update();

                databaseHelper.getInventoryBatchDao().executeRawNoArgs("Update inventory_batch set lastmodified_timestamp = '"+new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime())+"', sku_purchase_price='"+params[0]+"' where batch_id IN (select batch_id from inventory_batch where sku_id='"+skuId+"' order by batch_id desc limit 1)");

                /*UpdateBuilder<InventoryBatch, Integer> updateBatchBuilder = databaseHelper.getInventoryBatchDao().updateBuilder();
                updateBatchBuilder.where().eq("sku_id", skuId);
                updateBatchBuilder.updateColumnValue("sku_purchase_price", params[0]);
                updateBatchBuilder.update();
                 */
            }
            else if(fieldName.equals("sku_mrp")) {
                UpdateBuilder<ProductSku, Integer> updateBuilder = databaseHelper.getProductSkuDao().updateBuilder();
                updateBuilder.where().eq("sku_id", skuId);
                updateBuilder.updateColumnValue(fieldName, params[0]);
                String date = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime());
                updateBuilder.updateColumnValue("lastmodified_timestamp", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(date));
                updateBuilder.update();
                databaseHelper.getInventoryBatchDao().executeRawNoArgs("Update inventory_batch set lastmodified_timestamp = '"+new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime())+"', sku_mrp='"+params[0]+"' where batch_id IN (select batch_id from inventory_batch where sku_id='"+skuId+"' order by batch_id desc limit 1)");

            }
            else if(fieldName.equals("sku_saleprice")){
                UpdateBuilder<ProductSku, Integer> updateBuilder = databaseHelper.getProductSkuDao().updateBuilder();
                updateBuilder.where().eq("sku_id", skuId);
                updateBuilder.updateColumnValue(fieldName, params[0]);
                String date = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime());
                updateBuilder.updateColumnValue("lastmodified_timestamp", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(date));
                updateBuilder.update();
                databaseHelper.getInventoryBatchDao().executeRawNoArgs("Update inventory_batch set lastmodified_timestamp = '"+new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime())+"', sku_sales_price='"+params[0]+"' where batch_id IN (select batch_id from inventory_batch where sku_id='"+skuId+"' order by batch_id desc limit 1)");

                /*  UpdateBuilder<InventoryBatch, Integer> updateBatchBuilder = databaseHelper.getInventoryBatchDao().updateBuilder();
                updateBatchBuilder.where().eq("sku_id", skuId);
                updateBatchBuilder.updateColumnValue("sku_sales_price", params[0]);
                updateBatchBuilder.update();*/
            }
            else if( fieldName.equals("is_offer")||fieldName.equals("show_store")){
                UpdateBuilder<InventorySku, Integer> updateBuilder = databaseHelper.getInventorySkuDao().updateBuilder();
                updateBuilder.where().eq("inventory_sku_id", skuId);
                if(params[0].equals("true"))
                    updateBuilder.updateColumnValue(fieldName,true);
                else
                    updateBuilder.updateColumnValue(fieldName,false);
                
                String date = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime());
                updateBuilder.updateColumnValue("lastmodified_timestamp", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(date));
                updateBuilder.update();
               // databaseHelper.getInventoryBatchDao().executeRawNoArgs("Update inventory_batch set lastmodified_timestamp = '"+new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime())+"' where batch_id IN (select batch_id from inventory_batch where sku_id='"+skuId+"' order by batch_id desc limit 1)");
                wait(50);
            } else if(fieldName.equals("inventory_sku_quantity")){
                InventorySku lastInventorySku = databaseHelper.getInventorySkuDao().queryForEq("inventory_sku_id", skuId).get(0);
                InventoryBatch batchObj= databaseHelper.getInventoryBatchDao().queryBuilder().orderBy("batch_id", false).where().eq("sku_id", skuId).queryForFirst();
                float qtyDifference = Float.parseFloat(params[0]) - lastInventorySku.getQuantity();
                if(qtyDifference > 0){
                    databaseHelper.getInventoryBatchDao().executeRawNoArgs("Update inventory_batch set lastmodified_timestamp = '"+new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime())+"', sku_qty='"+(batchObj.getBatchQty() +qtyDifference)+"', sku_available_qty='"+(batchObj.getBatchAvailableQty() + qtyDifference)+"' where batch_id IN (select batch_id from inventory_batch where sku_id='"+skuId+"' order by batch_id desc limit 1)");
                }else{
                    databaseHelper.getInventoryBatchDao().executeRawNoArgs("Update inventory_batch set lastmodified_timestamp = '"+new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime())+"', sku_available_qty='"+(batchObj.getBatchAvailableQty() + qtyDifference)+"' where batch_id IN (select batch_id from inventory_batch where sku_id='"+skuId+"' order by batch_id desc limit 1)");
                }
            }
            return true; 
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        progressDialog.cancel();
        if(result != null) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
    }

}
