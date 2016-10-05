package com.snapbizz.snapstock.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.CompanyDistributorMap;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.DistributorBrandMap;
import com.snapbizz.snaptoolkit.domains.DistributorProductMap;
import com.snapbizz.snaptoolkit.domains.Order;
import com.snapbizz.snaptoolkit.domains.OrderDetails;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class AddProductToExistingPO extends AsyncTask<Void, Void, Boolean>{

    private final String TAG = AddProductToExistingPO.class.getSimpleName();
	private Context context;
	private int taskCode;
	private String errorMessage = "Unable to add product to the PO";
    private String productSkuCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private ProductSku productSku;
	private Order order;
	private Distributor distributor;
	private boolean isBrandTagged;
	private boolean isCompanyTagged;
	private boolean isProductTagged;
	
	public AddProductToExistingPO(Context context, int taskCode, OnQueryCompleteListener onQueryCompleteListener, String productSkuCode, Order order, Distributor distributor, boolean isBrandTagged, boolean isCompanyTagged, boolean isProductTagged){
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.productSkuCode = productSkuCode;
		this.order = order;
		this.distributor = distributor;
		this.isBrandTagged = isBrandTagged;
		this.isCompanyTagged = isCompanyTagged;
		this.isProductTagged = isProductTagged;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try{
			
			OrderDetails orderDetails = new OrderDetails();
			orderDetails.setOrder(order);
			orderDetails.setOrderProductPendingQty(1);
			orderDetails.setProductBilledQty(1);
			productSku = SnapCommonUtils.getDatabaseHelper(context).getProductSkuDao().queryForEq("sku_id", productSkuCode).get(0);
			orderDetails.setProductSkuID(productSku);
			SnapCommonUtils.getDatabaseHelper(context).getOrderDetailsDao().create(orderDetails);
			Brand brand;
			if (!isBrandTagged) {
	            DistributorBrandMap distributorBrandMap = new DistributorBrandMap();
	            distributorBrandMap.setDistributor(distributor);
	            brand = SnapInventoryUtils.getDatabaseHelper(context).getBrandDao().queryForEq("brand_id", productSku.getProductBrand().getBrandId()).get(0);
	            brand.setMyStore(true);
	            SnapInventoryUtils.getDatabaseHelper(context).getBrandDao().createOrUpdate(brand);
	            distributorBrandMap.setDistributorBrand(brand);
	            SnapInventoryUtils.getDatabaseHelper(context).getDistributorBrandMapDao().create(distributorBrandMap);
            }
			if (!isCompanyTagged) {
                CompanyDistributorMap companyDistributorMap = new CompanyDistributorMap();
                companyDistributorMap.setDistributor(distributor);
                companyDistributorMap.setCompany(productSku.getProductBrand().getCompany());
                SnapInventoryUtils.getDatabaseHelper(context).getCompanyDistributorDao().create(companyDistributorMap);
            }
			if (!isProductTagged && productSku.getBrandId() == 1302) {
                DistributorProductMap distributorProductMap = new DistributorProductMap();
                distributorProductMap.setDistributor(distributor);
                distributorProductMap.setDistributorProductSku(productSku);
                Log.d(TAG, "product name " + productSku.getProductSkuName());
                SnapInventoryUtils.getDatabaseHelper(context).getDistributorProductMapDao().create(distributorProductMap);
            }
			return true;
			
		}catch(Exception e){
			e.printStackTrace();
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
