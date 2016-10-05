package com.snapbizz.snapstock.asynctasks;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snapstock.domains.ProductBean;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.CompanyDistributorMap;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.DistributorBrandMap;
import com.snapbizz.snaptoolkit.domains.DistributorProductMap;
import com.snapbizz.snaptoolkit.domains.Order;
import com.snapbizz.snaptoolkit.domains.OrderDetails;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.OrderType;

public class SaveInventoryDistributorOrder extends AsyncTask<Void, Void, Boolean> {

    private final String TAG = SaveInventoryDistributorOrder.class.getSimpleName();
	private Context context;
	private OnQueryCompleteListener onQueryCompleteListener;
	private int taskCode;
	private String errorMessage = "Unable to place the order";
	private float totalOrderAmount;
	private List<ProductBean> distributorOrderList;
	private HashMap<Integer, Brand> brandMap;
	private HashMap<Integer, Company> companyMap;
	private HashMap<String, DistributorProductMap> distributorProductHashMap;
	private String orderDate;
	private Distributor distributor;

	public SaveInventoryDistributorOrder(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode,
			float totalOrderAmount, String orderDate,
			List<ProductBean> distributorOrderList,
			Distributor distributor, HashMap<Integer, Brand> brandMap, HashMap<Integer, Company> companyMap, HashMap<String, DistributorProductMap> distributorProductHashMap) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.totalOrderAmount = totalOrderAmount;
		this.distributorOrderList = distributorOrderList;
		this.orderDate = orderDate;
		this.distributor = distributor;
		this.brandMap = brandMap;
		this.companyMap = companyMap;
		this.distributorProductHashMap = distributorProductHashMap;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try {

			if (null != distributorOrderList && distributorOrderList.size() > 0) {

				Order order = new Order();
				order.setDistributorID(distributor);
				order.setOrderStatus(OrderType.PENDING);
				order.setOrderDate(orderDate);
				order.setOrderTotalAmount(totalOrderAmount);
				order.setOrderTotalDiscount(00);
				SnapInventoryUtils.getDatabaseHelper(context).getOrderDao().create(order);
				Brand brand;
				ProductSku productSku;
				for (ProductBean item : distributorOrderList) {
					productSku = SnapInventoryUtils.getDatabaseHelper(context).getProductSkuDao().queryForEq("sku_id", item.getProductSkuID()).get(0);
					if (null != brandMap && !brandMap.containsKey(item.getProductBrandID())) {
					    DistributorBrandMap distributorBrandMap = new DistributorBrandMap();
                        brand = SnapInventoryUtils.getDatabaseHelper(context).getBrandDao().queryForEq("brand_id", productSku.getProductBrand().getBrandId()).get(0);
                        brand.setMyStore(true);
                        SnapInventoryUtils.getDatabaseHelper(context).getBrandDao().createOrUpdate(brand);
                        distributorBrandMap.setDistributorBrand(brand);
                        Log.d(TAG, "Brand " + productSku.getProductBrand().getBrandName());
                        distributorBrandMap.setDistributor(distributor);
                        SnapInventoryUtils.getDatabaseHelper(context).getDistributorBrandMapDao().create(distributorBrandMap);
                    }
					if (null != companyMap && !companyMap.containsKey(item.getProductCompanyID())) {
                        CompanyDistributorMap companyDistributorMap = new CompanyDistributorMap();
                        companyDistributorMap.setCompany(productSku.getProductBrand().getCompany());
                        Log.d(TAG, "company " + productSku.getProductBrand().getCompany().getCompanyName());
                        companyDistributorMap.setDistributor(distributor);
                        SnapInventoryUtils.getDatabaseHelper(context).getCompanyDistributorDao().create(companyDistributorMap);                        
                    }
					if (null != distributorProductHashMap && !distributorProductHashMap.containsKey(item.getProductSkuID()) && item.getProductBrandID() == 1302) {
					    DistributorProductMap distributorProductMap = new DistributorProductMap();
                        distributorProductMap.setDistributor(distributor);
                        distributorProductMap.setDistributorProductSku(productSku);
                        Log.d(TAG, "product name " + productSku.getProductSkuName());
                        SnapInventoryUtils.getDatabaseHelper(context).getDistributorProductMapDao().create(distributorProductMap);
                    }
					OrderDetails orderDetails = new OrderDetails();
					orderDetails.setOrder(order);
					orderDetails.setProductSkuID(productSku);
					orderDetails.setPaid(false);
					orderDetails.setOrderProductPendingQty(item.getProductToOrder());
					orderDetails.setOrderProductToOrderQty(item.getProductToOrder());
					orderDetails.setProductBilledQty(item.getProductToOrder());
					orderDetails.setProductToReceiveQty(item.getProductToOrder());
					orderDetails.setProductReceivedQty(00);
					SnapInventoryUtils.getDatabaseHelper(context).getOrderDetailsDao().create(orderDetails);
				}

				return true;
			} else {
				errorMessage = "No item in the list";
				return false;
			}
		} catch (Exception e) {
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
