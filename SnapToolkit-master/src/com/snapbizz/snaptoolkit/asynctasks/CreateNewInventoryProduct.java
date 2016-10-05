package com.snapbizz.snaptoolkit.asynctasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.CompanyDistributorMap;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.DistributorBrandMap;
import com.snapbizz.snaptoolkit.domains.DistributorProductMap;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class CreateNewInventoryProduct extends
AsyncTask<InventorySku, Void, InventorySku> {
    private Context context;
    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private String errorMessage = "Could not create new product, product may already exist";
    private boolean isEditMode;
    private Distributor taggedDistributor;
    private static final String TAG = "[CreateNewInventoryProduct]";

    public CreateNewInventoryProduct(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode,boolean isEditMode,Distributor taggedDistributor) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
        this.isEditMode=isEditMode;
        this.taggedDistributor=taggedDistributor;
    }

    @Override
    protected InventorySku doInBackground(InventorySku... inventorySkuArr) {
        // TODO Auto-generated method stub
        try {
            SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils
                    .getDatabaseHelper(context);
            
            Log.d("TAG", "doInBackground--isEditMode-->"+isEditMode);
           if(!isEditMode){
                List<ProductSku> tempProdList = databaseHelper
                        .getProductSkuDao()
                        .queryForEq("sku_id",
                                inventorySkuArr[0].getProductSku().getProductSkuCode());
                if (!tempProdList.isEmpty()) {
                    return null;
                }
            }
            if(inventorySkuArr[0].getProductSku().getProductSkuCode().startsWith(SnapToolkitConstants.SNAP_LOOSE_PREFIX_KEY)) {
                inventorySkuArr[0].getProductSku().setQuickAddProduct(true);
            }
            List<ProductSku> prodList = databaseHelper
                    .getProductSkuDao()
                    .queryForEq("sku_id",
                            inventorySkuArr[0].getProductSku().getProductSkuCode());
            if(inventorySkuArr[0].getProductSku().getProductSkuCode().equals(SnapToolkitConstants.SNAP_LOCAL_PREFIX_KEY)) {
                long rowId = Long.parseLong(databaseHelper.getProductSkuDao().queryRaw("SELECT ROWID from product_sku order by ROWID DESC limit 1").getFirstResult()[0]) + 1;
                ProductSku lastSku=databaseHelper.getProductSkuDao().queryBuilder().orderBy("sku_id", false).queryForFirst();
                if(!lastSku.getProductSkuCode().contains(SnapToolkitConstants.SNAP_LOCAL_CAMPAIGN_PREFIX_KEY)&&lastSku.getProductSkuCode().contains(SnapToolkitConstants.SNAP_LOCAL_PREFIX_KEY)){
                  long newRowId= Long.parseLong(lastSku.getProductSkuCode().substring( lastSku.getProductSkuCode().lastIndexOf("l")+1,  lastSku.getProductSkuCode().length()))+1;
                  inventorySkuArr[0].getProductSku().setProductSkuCode(SnapToolkitConstants.SNAP_LOCAL_PREFIX_KEY + newRowId);
                } else {
                	try {
                		String snapLocal = databaseHelper.getProductSkuDao()
                                                         .queryRaw("SELECT sku_id from product_sku where sku_id like('snaplocal%') "+
                                                                   "and sku_id not like('snaplocalcampaign%') order by sku_id DESC limit 1").getFirstResult()[0];
                		Log.d(TAG, "snapLocal---------"+snapLocal);
                		if(snapLocal != null && !snapLocal.trim().isEmpty())
                				rowId= Long.parseLong(snapLocal.substring( snapLocal.lastIndexOf("l")+1,  snapLocal.length()))+1;
                	} catch(Exception e) { }
                	Log.d(TAG, "snapLocal------rowId---"+rowId);
                	inventorySkuArr[0].getProductSku().setProductSkuCode(SnapToolkitConstants.SNAP_LOCAL_PREFIX_KEY + rowId);
                }
            }
            if(inventorySkuArr[0].getProductSku().getProductSkuBitmap() != null)
                inventorySkuArr[0].getProductSku().setImageUrl(SnapCommonUtils.storeImageBitmap(inventorySkuArr[0].getProductSku().getProductSkuBitmap(), inventorySkuArr[0].getProductSku()));
            if (prodList.isEmpty()&&!isEditMode) {
                databaseHelper.getProductSkuDao().create(
                        inventorySkuArr[0].getProductSku());
                Brand brand = databaseHelper.getBrandDao().queryForEq("brand_id", inventorySkuArr[0].getProductSku().getProductBrand().getBrandId()).get(0);
                if(!brand.isMyStore()) {
                    brand.setMyStore(true);
                    databaseHelper.getBrandDao().update(brand);
                }
                databaseHelper.getInventorySkuDao().create(inventorySkuArr[0]);
                databaseHelper.getInventoryBatchDao().create(new InventoryBatch(inventorySkuArr[0].getProductSku().getProductSkuCode(), inventorySkuArr[0].getProductSku().getProductSkuMrp(), inventorySkuArr[0].getProductSku().getProductSkuSalePrice(), inventorySkuArr[0].getPurchasePrice(), Calendar.getInstance().getTime(), null, inventorySkuArr[0].getQuantity(), -1, 0));
                if(taggedDistributor!=null){
                    DistributorProductMap distributorProductMap = new DistributorProductMap();
                    distributorProductMap.setDistributor(taggedDistributor);
                    distributorProductMap.setDistributorProductSku(inventorySkuArr[0].getProductSku());
                    databaseHelper.getDistributorProductMapDao().create(distributorProductMap);
                    Dao<DistributorBrandMap, Integer> distributorBrandMapDao = databaseHelper.getDistributorBrandMapDao();
                    DistributorBrandMap distributorBrandMap = new DistributorBrandMap();
                    distributorBrandMap.setDistributor(taggedDistributor);
                    distributorBrandMap.setDistributorBrand(inventorySkuArr[0].getProductSku().getProductBrand());
                    List<DistributorBrandMap> brandresultList = distributorBrandMapDao.queryBuilder().where().eq("brand_id", inventorySkuArr[0].getProductSku().getProductBrand().getBrandId()).and().eq("distributor_id", taggedDistributor.getDistributorId()).query();
                    if(brandresultList.size()==0)
                        distributorBrandMapDao.create(distributorBrandMap);
                    Dao<CompanyDistributorMap, Integer> distributorCompanyMapDao = databaseHelper.getCompanyDistributorDao();
                    CompanyDistributorMap distributorCompanyMap = new CompanyDistributorMap();
                    distributorCompanyMap.setDistributor(taggedDistributor);
                    distributorCompanyMap.setCompany(inventorySkuArr[0].getProductSku().getProductBrand().getCompany());
                    List<CompanyDistributorMap> companyResultList = distributorCompanyMapDao.queryBuilder().where().eq("company_id", inventorySkuArr[0].getProductSku().getProductBrand().getCompany().getCompanyId()).and().eq("distributor_id", taggedDistributor.getDistributorId()).query();
                    if(companyResultList.size()==0)
                        distributorCompanyMapDao.create(distributorCompanyMap);
                }
                InventorySku newSku =  databaseHelper.getInventorySkuDao().queryForEq("inventory_slno", inventorySkuArr[0].getSlNo()).get(0);
                return newSku;
            } else {
                InventorySku inventorySku = inventorySkuArr[0];
                databaseHelper.getProductSkuDao().update(inventorySku.getProductSku());
                InventorySku lastInventorySku = databaseHelper.getInventorySkuDao().queryForEq("inventory_sku_id", inventorySku.getProductSku().getProductSkuCode()).get(0);
                float qtyDifference = inventorySku.getQuantity() - lastInventorySku.getQuantity();
                float qty = inventorySku.getQuantity();
                inventorySku.setQuantity(lastInventorySku.getQuantity());
                Log.d("", "Updating inventory qty "+inventorySku.getQuantity());
                databaseHelper.getInventorySkuDao().update(inventorySku);
                inventorySku.setQuantity(qty);
                if(qtyDifference > 0)
                    databaseHelper.getInventoryBatchDao().executeRawNoArgs("Update inventory_batch set lastmodified_timestamp = '"+new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime())+"', sku_qty = sku_qty + '"+qtyDifference+"', sku_available_qty = sku_available_qty + '"+qtyDifference+"', sku_mrp='"+inventorySku.getProductSku().getProductSkuMrp()+"', sku_sales_price='"+inventorySku.getProductSku().getProductSkuSalePrice()+"', sku_purchase_price='"+inventorySku.getPurchasePrice()+"' where batch_id IN (select batch_id from inventory_batch where sku_id='"+inventorySku.getProductSku().getProductSkuCode()+"' order by batch_id desc limit 1)");
                else
                    databaseHelper.getInventoryBatchDao().executeRawNoArgs("Update inventory_batch set lastmodified_timestamp = '"+new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime())+"', sku_available_qty = sku_available_qty + '"+qtyDifference+"', sku_mrp='"+inventorySku.getProductSku().getProductSkuMrp()+"', sku_sales_price='"+inventorySku.getProductSku().getProductSkuSalePrice()+"', sku_purchase_price='"+inventorySku.getPurchasePrice()+"' where batch_id IN (select batch_id from inventory_batch where sku_id='"+inventorySku.getProductSku().getProductSkuCode()+"' order by batch_id desc limit 1)");

                if(taggedDistributor!=null){
                    DistributorProductMap distributorProductMap = new DistributorProductMap();
                    distributorProductMap.setDistributor(taggedDistributor);
                    distributorProductMap.setDistributorProductSku(inventorySkuArr[0].getProductSku());
                    databaseHelper.getDistributorProductMapDao().create(distributorProductMap);
                    Dao<DistributorBrandMap, Integer> distributorBrandMapDao = databaseHelper.getDistributorBrandMapDao();
                    DistributorBrandMap distributorBrandMap = new DistributorBrandMap();
                    distributorBrandMap.setDistributor(taggedDistributor);
                    distributorBrandMap.setDistributorBrand(inventorySkuArr[0].getProductSku().getProductBrand());
                    List<DistributorBrandMap> brandresultList = distributorBrandMapDao.queryBuilder().where().eq("brand_id", inventorySkuArr[0].getProductSku().getProductBrand().getBrandId()).and().eq("distributor_id", taggedDistributor.getDistributorId()).query();
                  /*  if( inventorySkuArr[0].getProductSku().getProductBrand().getBrandId()==SnapToolkitConstants.MISCELLANEOUS_BRAND_ID){
                        databaseHelper.getBrandDao().update(inventorySkuArr[0].getProductSku().getProductBrand());
                    }*/
                    if( brandresultList.size()==0)
                        distributorBrandMapDao.create(distributorBrandMap);
                    Dao<CompanyDistributorMap, Integer> distributorCompanyMapDao = databaseHelper.getCompanyDistributorDao();
                    CompanyDistributorMap distributorCompanyMap = new CompanyDistributorMap();
                    distributorCompanyMap.setDistributor(taggedDistributor);
                    distributorCompanyMap.setCompany(inventorySkuArr[0].getProductSku().getProductBrand().getCompany());
                    List<CompanyDistributorMap> companyResultList = distributorCompanyMapDao.queryBuilder().where().eq("company_id", inventorySkuArr[0].getProductSku().getProductBrand().getCompany().getCompanyId()).and().eq("distributor_id", taggedDistributor.getDistributorId()).query();
                    if( companyResultList.size()==0)
                        distributorCompanyMapDao.create(distributorCompanyMap);
                }

                InventorySku newSku =  databaseHelper.getInventorySkuDao().queryForEq("inventory_slno", inventorySkuArr[0].getSlNo()).get(0);
                return newSku;
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "Sorry Could not create new product";
            return null;
        }
    }

    @Override
    protected void onPostExecute(InventorySku result) {
        // TODO Auto-generated method stub
        if (result != null) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }
}
