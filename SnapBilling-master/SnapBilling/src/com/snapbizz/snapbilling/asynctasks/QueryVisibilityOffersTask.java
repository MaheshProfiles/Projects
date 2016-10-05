package com.snapbizz.snapbilling.asynctasks;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snapbilling.domains.SmartStore;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;


public class QueryVisibilityOffersTask  extends AsyncTask<Integer, Void, List<List<InventorySku>>> {
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "No Inventory Found";
	private final int offersCount = SnapToolkitConstants.OFFER_LIMIT;

	public QueryVisibilityOffersTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected List<List<InventorySku>> doInBackground(Integer... params) {
		try {
			int size =0;
			int campaignSize =0;
			int i = 0;
			int offerLimit=0;
			List<List<InventorySku>> resultList = new ArrayList<List<InventorySku>>();
			SnapBizzDatabaseHelper databaseHelper = SnapBillingUtils.getDatabaseHelper(context);
		 	java.util.Date date= new java.util.Date();
			new Timestamp(date.getTime());
			Timestamp currentTimestamp = new Timestamp(date.getTime());
			String sqlCampaignsQuery = "select id,campaign_id,campaign_name,start_date,end_date,campaign_type,image_uid,campaign_code,company from campaigns where end_date >='"+currentTimestamp+"'";
			GenericRawResults<Campaigns> sqlCampaignsResult=databaseHelper.getCampaignsDao().queryRaw(sqlCampaignsQuery,databaseHelper.getCampaignsDao().getRawRowMapper());
			campaignSize=sqlCampaignsResult.getResults().size();
			List<SmartStore> smartStore = SnapBillingUtils.getSmartStoreList(context);
			if(smartStore != null && smartStore.size() > 0)
				campaignSize++;
			if (campaignSize < offersCount) {
				 offerLimit = offersCount-campaignSize;
				 offerLimit = offerLimit*2;
				 String sqlQuery=" select inventory_sku.* from inventory_sku inner join product_sku on product_sku.sku_id=inventory_sku.inventory_sku_id inner join brand on brand.brand_id=product_sku.brand_id where product_sku.has_image = 1 and (product_sku.sku_mrp > 0 AND (inventory_sku.is_offer = 1 OR inventory_sku.show_store = 1)) and product_sku.sku_saleprice > 0  order by inventory_sku.inventory_slno desc limit " + offerLimit;
					GenericRawResults<InventorySku> sqlQueryResult = databaseHelper.getInventorySkuDao().queryRaw(sqlQuery,databaseHelper.getInventorySkuDao().getRawRowMapper());
					List<InventorySku> inventorySkuList = sqlQueryResult.getResults();
					inventorySkuList=SnapBillingUtils.removeNoImageItems(inventorySkuList,SnapBillingUtils.getSnapContext(this.context));
					 Collections.sort(inventorySkuList, new Comparator<InventorySku>() {
			                @Override
			                public int compare(InventorySku lhs, InventorySku rhs) {
			                    return lhs.getProductSku().getProductCategory().getCategoryId() - rhs.getProductSku().getProductCategory().getCategoryId();
			                }
			            });
					 
					 if(size<=0){
							resultList.add(inventorySkuList);
					 }
						else{
							resultList.get(0).addAll(inventorySkuList);
						}
					
					 size = resultList.get(0).size();
		            if (size > offersCount) {
						for (int j = resultList.get(0).size() - 1; j >= offersCount; j--) {
							 resultList.get(0).remove(j);
						}
					}
		           
			   }			 			 						
				while (i < size) {
					 
					if (i != 0 && i % 2 == 0) {
						
						ArrayList<InventorySku> tempInventorySkuList = new ArrayList<InventorySku>();
						int limit;
						int ressize=resultList.get(0).size();
						if(ressize<=4){
						if(ressize%2==0){
							limit=4;
						}else{
							limit=(resultList.get(0).size()%2)+2;
						}
						}else{
							limit=4;
						}
						for (int j = 2; j < limit;i++) {
							if (resultList.get(0) != null
									&& resultList.get(0).size() > 2) {
								tempInventorySkuList.add(resultList.get(0).get((j)));
								resultList.get(0).remove(j);
								limit--;
							}else{
								j++;
							}
						}
						  resultList.add(tempInventorySkuList);
						
					} else {
						i++;
					}

				}
				
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); 
                Calendar cal = Calendar.getInstance();
                Calendar cal1 = Calendar.getInstance();
                cal.add(Calendar.DATE, +1);
                cal1.add(Calendar.DATE, -1);
                Date tomorrw = dateFormat.parse(dateFormat.format(cal.getTime()));
                Date yesterDay = dateFormat.parse(dateFormat.format(cal1.getTime())); 
                Timestamp tomorrwTimestamp = new Timestamp(tomorrw.getTime());
                Timestamp yesterdayTimestamp = new Timestamp(yesterDay.getTime());
                sqlCampaignsQuery=" select id,campaign_id,campaign_name,start_date,end_date,campaign_type,image_uid,campaign_code,company from campaigns where end_date >'"+yesterdayTimestamp+"' and start_date <'"+tomorrwTimestamp+"'";
				sqlCampaignsResult=databaseHelper.getCampaignsDao().queryRaw(sqlCampaignsQuery,databaseHelper.getCampaignsDao().getRawRowMapper());
				List<Campaigns> campaignsList = sqlCampaignsResult.getResults();
				if(smartStore != null && smartStore.size() > 0) {
					Log.d("TAG","getSmartStoreList------>"+smartStore.size());
					Campaigns campaigns= new Campaigns();
					campaigns.setCode("myscreen");
					campaignsList.add(campaigns);
				}
				Log.d("TAG","getSmartStoreList------>"+campaignsList.size());
				for(int x=0;x<campaignsList.size();x++){
					ArrayList<InventorySku> campaignSkuList = new ArrayList<InventorySku>();
					Campaigns campaigns=campaignsList.get(x);
					if(SnapCommonUtils.checkProductDrawable(SnapToolkitConstants.VISIBILITY_IMAGE_PATH+"campaign/"+campaigns.getImage_uid(), context)!=null){
					campaignSkuList.add(SnapBillingUtils.createVisibilityObject(campaigns));
					resultList.add(campaignSkuList);
					}else if (campaigns.getCode().toLowerCase().contains(SnapToolkitConstants.SNAP_MYSCREEN_PREFIX_KEY.toLowerCase())) {
						if(SnapCommonUtils.checkProductDrawable(SnapToolkitConstants.VISIBILITY_IMAGE_PATH+"mystore/storeimage.png", context)!=null){
							campaignSkuList.add(SnapBillingUtils.createVisibilityObject(campaigns));
							resultList.add(campaignSkuList);
						}
					}
					
				}
				
				
			return resultList;
		} catch (Exception e) {
			Log.e("Capmaign", "Capmaign--Error",e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<List<InventorySku>> result) {
		if (result != null && result.size() != 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
	

}
