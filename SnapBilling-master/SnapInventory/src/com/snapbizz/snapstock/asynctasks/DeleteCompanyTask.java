package com.snapbizz.snapstock.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.CompanyDistributorMap;
import com.snapbizz.snaptoolkit.domains.DeletedRecords;
import com.snapbizz.snaptoolkit.domains.DistributorBrandMap;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.TableType;

public class DeleteCompanyTask extends AsyncTask<Company, Void, Boolean>{

    private Context context;
    private OnQueryCompleteListener onQueryCompleteListener;
    private int taskCode;
    private String errorMessage = "Unable to delete company";

    public DeleteCompanyTask( Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode){
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }

    @Override
    protected Boolean doInBackground(Company... params) {
        // TODO Auto-generated method stub
        try{
            SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
            List<Brand> brandList = databaseHelper.getBrandDao().queryForEq("company_id", params[0].getCompanyId());
            Integer [] brandIds = new Integer[brandList.size()];
            for(int i = 0; i < brandList.size(); i++)
                brandIds[i] = brandList.get(i).getBrandId();
            List<ProductSku> productSkuList = databaseHelper.getProductSkuDao().queryBuilder().where().in("brand_id", brandIds).query();
            String [] skuIds = new String[productSkuList.size()];
            for(int i = 0; i < productSkuList.size(); i++) {
                skuIds[i] = productSkuList.get(i).getProductSkuCode();
            }
            DeleteBuilder<InventoryBatch, Integer> inventoryBatchDeleteBuilder = databaseHelper.getInventoryBatchDao().deleteBuilder();
            inventoryBatchDeleteBuilder.where().in("sku_id", skuIds);
            inventoryBatchDeleteBuilder.delete();
            DeleteBuilder<InventorySku, Integer> inventorySkuDeleteBuilder = databaseHelper.getInventorySkuDao().deleteBuilder();
            inventorySkuDeleteBuilder.where().in("inventory_sku_id", skuIds);
            inventorySkuDeleteBuilder.delete();
            DeleteBuilder<ProductSku, Integer> productSkuDeleteBuilder = databaseHelper.getProductSkuDao().deleteBuilder();
            productSkuDeleteBuilder.where().in("brand_id", brandIds);
            productSkuDeleteBuilder.delete();
            DeleteBuilder<DistributorBrandMap, Integer> distributorBrandDeleteBuilder = databaseHelper.getDistributorBrandMapDao().deleteBuilder();
            distributorBrandDeleteBuilder.where().in("brand_id", brandIds);
            distributorBrandDeleteBuilder.delete();
            DeleteBuilder<CompanyDistributorMap, Integer> distributorCompanyMapDeleteBuilder = databaseHelper.getCompanyDistributorDao().deleteBuilder();
            distributorCompanyMapDeleteBuilder.where().eq("company_id", params[0].getCompanyId());
            distributorCompanyMapDeleteBuilder.delete();
            DeleteBuilder<Brand, Integer> brandDeleteBuilder = databaseHelper.getBrandDao().deleteBuilder();
            brandDeleteBuilder.where().eq("company_id", params[0].getCompanyId());
            brandDeleteBuilder.delete();
            databaseHelper.getCompanyDao().delete(params[0]);
            DeletedRecords deletedRecord= new DeletedRecords();
            deletedRecord.setRecordId(params[0].getCompanyId()+"");
            deletedRecord.setTableType(TableType.COMPANY);
            databaseHelper.getDeletedRecordsDao().createOrUpdate(deletedRecord);
            for(int i=0;i<skuIds.length;i++) {
                DeletedRecords deletedInventoryskuRecord= new DeletedRecords();
                deletedInventoryskuRecord.setRecordId(skuIds[i]);
                deletedInventoryskuRecord.setTableType(TableType.INVENTORY_SKU);
                databaseHelper.getDeletedRecordsDao().createOrUpdate(deletedInventoryskuRecord);
            }
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        // TODO Auto-generated method stub
        if(result)
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        else
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
    }
}
