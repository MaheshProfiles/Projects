package com.snapbizz.snaptoolkit.asynctasks;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;

import android.os.AsyncTask;

import android.util.Log;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.snapbizz.snaptoolkit.domains.DeletedRecords;
import com.snapbizz.snaptoolkit.domains.DistributorProductMap;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.TableType;

public class DeleteUnsoldProductTask extends AsyncTask<Void, Void, Void> {
	private Context context;

	public DeleteUnsoldProductTask(Context context, int taskCode) {
		this.context = context;

	}

	@Override
	protected Void doInBackground(Void... arg0) {

		SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils
				.getDatabaseHelper(context);
		List<InventorySku> prodList = new ArrayList<InventorySku>();
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Calendar cal1 = Calendar.getInstance();
			cal1.add(Calendar.DATE, -90);
			Date yesterDay = dateFormat
					.parse(dateFormat.format(cal1.getTime()));
			Timestamp currentTimestamp = new Timestamp(yesterDay.getTime());
//			String sqlQuery = "select * from inventory_sku where inventory_sku.lastmodified_timestamp <'"
//					+ currentTimestamp
//					+ "' and inventory_sku.inventory_sku_quantity=0 limit 1";
			String sqlQuery = "select * from inventory_sku where inventory_sku.lastmodified_timestamp <'"
					+ currentTimestamp
					+ "' and inventory_sku.inventory_sku_quantity=0 limit'"+ SnapToolkitConstants.UNSOLD_PROD_DELETE_LIMIT+ "'";
			
 			
			GenericRawResults<InventorySku> sqlInventorySkuResult = databaseHelper
					.getInventorySkuDao().queryRaw(sqlQuery,
							databaseHelper.getInventorySkuDao().getRawRowMapper());
			
			prodList = sqlInventorySkuResult.getResults();
			Log.d("prod sku","DeleteUnsoldProductTask  unsold prodList size ----"+prodList.size());
			if(prodList!=null && prodList.size()>0 ){
				if(prodList.get(0).getProductSku()!=null ){
					for(int i=0;i<prodList.size();i++){
//						try {
//							
//							String sqlQuery1 = "delete from inventory_sku where inventory_sku.inventory_sku_id ='"
//									+ prodList.get(i).getProductSku().getProductSkuCode()+ "'";
//							GenericRawResults<InventorySku> sqlCampaignsResult1 = databaseHelper
//									.getInventorySkuDao().queryRaw(sqlQuery1,
//											databaseHelper.getInventorySkuDao().getRawRowMapper());
//							
//						} catch (java.sql.SQLException e) {
//							e.printStackTrace();
//						}
						
						
						
						
						try {
							prodList.get(i).setTaxRate(0f);
							prodList.get(i).setQuantity(0);
							prodList.get(i).setPurchasePrice(0f);
				            databaseHelper.getInventorySkuDao().delete(prodList.get(i));
				            List<String []> resultList = databaseHelper.getInventoryBatchDao().queryRaw("select * from inventory_sku inner join product_sku on inventory_sku.inventory_sku_id = product_sku.sku_id where product_sku.brand_id = '"+prodList.get(i).getProductSku().getProductBrand().getBrandId()+"'").getResults();
				            if(resultList.size() == 0) {
				                databaseHelper.getInventorySkuDao().executeRaw("update brand set is_mystore = 0 where brand_id = "+prodList.get(i).getProductSku().getProductBrand().getBrandId());
				            }
				            List<InventoryBatch> inventoryBatchList = databaseHelper.getInventoryBatchDao().queryForEq("sku_id", prodList.get(i).getProductSku().getProductSkuCode());
				            for(InventoryBatch inventoryBatch : inventoryBatchList) {
				                DeletedRecords deletedRecord = new DeletedRecords();
				                deletedRecord.setRecordId(inventoryBatch.getSlNo()+"");
				                deletedRecord.setTableType(TableType.INVENTORY_BATCh);
				                databaseHelper.getDeletedRecordsDao().createOrUpdate(deletedRecord);
				            }
				            List<DistributorProductMap> distributorProductList = databaseHelper.getDistributorProductMapDao().queryForEq("sku_id", prodList.get(i).getProductSku().getProductSkuCode());
				            for (DistributorProductMap distributorProductMap : distributorProductList) {
				                DeletedRecords deletedRecords = new DeletedRecords();
				                deletedRecords.setRecordId(String.valueOf(distributorProductMap.getDistributorProductSkuId()));
				                deletedRecords.setTableType(TableType.DISTRIBUTOR_PRODUCT_MAP);
				                databaseHelper.getDeletedRecordsDao().create(deletedRecords);
				            }
				            DeleteBuilder<DistributorProductMap, Integer> distributorProductDeleteBuilder = databaseHelper.getDistributorProductMapDao().deleteBuilder();
				            distributorProductDeleteBuilder.where().eq("sku_id", prodList.get(i).getProductSku().getProductSkuCode());
				            distributorProductDeleteBuilder.delete();
				            DeleteBuilder<InventoryBatch, Integer> deleteBuilder = databaseHelper.getInventoryBatchDao().deleteBuilder();
				            deleteBuilder.where().eq("sku_id", prodList.get(i).getProductSku().getProductSkuCode());
				            deleteBuilder.delete();
				            DeletedRecords deletedRecord = new DeletedRecords();
				            deletedRecord.setRecordId(prodList.get(i).getSlNo()+"");
				            deletedRecord.setTableType(TableType.INVENTORY_SKU);
				            databaseHelper.getDeletedRecordsDao().createOrUpdate(deletedRecord);
				            if(!prodList.get(i).getProductSku().isGDB()) {
				                deletedRecord = new DeletedRecords();
				                deletedRecord.setRecordId(prodList.get(i).getProductSku().getProductSkuCode());
				                deletedRecord.setTableType(TableType.PRODUCT_SKU);
				                databaseHelper.getDeletedRecordsDao().createOrUpdate(deletedRecord);
				                databaseHelper.getProductSkuDao().delete(prodList.get(i).getProductSku());
				            }
				            else{
				            	try {
//									
									String sqlQuery1 = "delete from inventory_sku where inventory_sku.inventory_sku_id ='"
											+ prodList.get(i).getProductSku().getProductSkuCode()+ "'";
									GenericRawResults<InventorySku> sqlCampaignsResult1 = databaseHelper
											.getInventorySkuDao().queryRaw(sqlQuery1,
													databaseHelper.getInventorySkuDao().getRawRowMapper());
									
								} catch (java.sql.SQLException e) {
									e.printStackTrace();
								}
				            }
				        } catch (Exception e) {
				            // TODO Auto-generated catch block
				            e.printStackTrace();
				            return null;
				        }
						
					}
				}
				
			}
			
			
			
		} catch (SQLException e) {
			Log.e("", "Error", e);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
