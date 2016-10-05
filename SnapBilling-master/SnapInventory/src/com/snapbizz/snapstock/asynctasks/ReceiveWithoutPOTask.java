package com.snapbizz.snapstock.asynctasks;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snapstock.domains.ProductBean;
import com.snapbizz.snapstock.utils.SnapInventoryConstants;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.CompanyDistributorMap;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.DistributorBrandMap;
import com.snapbizz.snaptoolkit.domains.DistributorProductMap;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.Order;
import com.snapbizz.snaptoolkit.domains.OrderDetails;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.domains.ReceiveItems;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.OrderType;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class ReceiveWithoutPOTask extends AsyncTask<Void, Void, Boolean>{

    private final String TAG = ReceiveWithoutPOTask.class.getSimpleName();
	private Context context;
	private OnQueryCompleteListener onQueryCompleteListener;
	private int taskCode;
	private String errorMessage = "Unable to receive product(s)";
	private String currentDate;
	private String invoiceID;
	private HashMap<Integer, Brand> brandMap;
	private HashMap<Integer, Company> companyMap;
	private HashMap<String, DistributorProductMap> distributorProductHashMap;
	private List<ProductBean> selectedItem;
	private float totalDiscountGiven;
	private float modifiedOrderAmount;
	private float totalOrderAmountWithoutPO;
	private Distributor distributor;
	private Bitmap invoiceImageBitmap;
	private boolean isOrderReceivedCompletely = true;
	
	public ReceiveWithoutPOTask(Context context, int taskCode, OnQueryCompleteListener onQueryCompleteListener, List<ProductBean> selectedItem, String currentDate, float totalDiscountGiven, float modifiedOrderAmount, Distributor distributor, float totalOrderAmountWithoutPO, String invoiceID, Bitmap invoiceImageBitmap, HashMap<Integer, Brand> brandMap, HashMap<Integer, Company> companyMap, HashMap<String, DistributorProductMap> distributorProductHashMap){
		this.context = context;
		this.taskCode = taskCode;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.selectedItem = selectedItem;
		this.currentDate = currentDate;
		this.totalDiscountGiven = totalDiscountGiven;
		this.modifiedOrderAmount = modifiedOrderAmount;
		this.distributor = distributor;
		this.totalOrderAmountWithoutPO = totalOrderAmountWithoutPO;
		this.invoiceID = invoiceID;
		this.invoiceImageBitmap = invoiceImageBitmap;
		this.brandMap = brandMap;
		this.companyMap = companyMap;
		this.distributorProductHashMap = distributorProductHashMap;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		try {
			for (ProductBean child : selectedItem) {
				if(child.getProductBilledQty() > child.getProductReceivedQty()){
					isOrderReceivedCompletely = false;
				}
			}
			Order order = new Order();
			order.setDistributorID(distributor);
			order.setOrderDate(currentDate);
			order.setOrderTotalAmount(totalOrderAmountWithoutPO);
			order.setOrderTotalDiscount(totalDiscountGiven);
			order.setPaymentToMake(modifiedOrderAmount);
			order.setInvoiceID(invoiceID);
            if (null != invoiceImageBitmap) {
                order.setImage(SnapCommonUtils.storeImageBitmap(
                        invoiceImageBitmap, order));
            } else {
                order.setImage("");
            }
            order.setOrderNumber(0);
            if (isOrderReceivedCompletely) {
                order.setOrderStatus(OrderType.RECEIVED);
            } else {
                order.setOrderStatus(OrderType.PENDING);
            }
			SnapInventoryUtils.getDatabaseHelper(context).getOrderDao().create(order);
			ProductSku productSku;
			Brand brand;
			if(null != selectedItem){
				for (ProductBean child : selectedItem) {
					productSku = SnapInventoryUtils.getDatabaseHelper(context).getProductSkuDao().queryForEq("sku_id", child.getProductSkuID()).get(0);
					OrderDetails orderDetails = new OrderDetails();
					orderDetails.setOrder(order);
					orderDetails.setProductSkuID(productSku);
					if(child.getProductPurchasePrice()>0){
						orderDetails.setPaid(true);
					}					
					orderDetails.setProductReceivedQty(child.getProductReceivedQty());
					orderDetails.setProductBilledQty(child.getProductBilledQty());
					orderDetails.setProductDiscount(child.getProductDiscount());
					orderDetails.setProductToReceiveQty(child.getProductBilledQty());
					orderDetails.setProductPurchasePrice(child.getProductPurchasePrice());
					if((child.getProductBilledQty() - child.getProductReceivedQty()) > 0){
						orderDetails.setOrderProductPendingQty(child.getProductBilledQty() - child.getProductReceivedQty());
					}else{
						orderDetails.setOrderProductPendingQty(0);
					}				
					orderDetails.setOrderProductToOrderQty(child.getProductBilledQty());
					SnapInventoryUtils.getDatabaseHelper(context).getOrderDetailsDao().create(orderDetails);
					if (null != brandMap && !brandMap.containsKey(child.getProductBrandID())) {
                        DistributorBrandMap distributorBrandMap = new DistributorBrandMap();
                        brand = SnapInventoryUtils.getDatabaseHelper(context).getBrandDao().queryForEq("brand_id", productSku.getProductBrand().getBrandId()).get(0);
                        brand.setMyStore(true);
                        SnapInventoryUtils.getDatabaseHelper(context).getBrandDao().createOrUpdate(brand);
                        distributorBrandMap.setDistributorBrand(brand);
                        Log.d(TAG, "brand " + productSku.getProductBrand().getBrandName());
                        distributorBrandMap.setDistributor(distributor);
                        SnapInventoryUtils.getDatabaseHelper(context).getDistributorBrandMapDao().create(distributorBrandMap);
                    }
                    if (null != companyMap && !companyMap.containsKey(child.getProductCompanyID())) {
                        CompanyDistributorMap companyDistributorMap = new CompanyDistributorMap();
                        companyDistributorMap.setCompany(productSku.getProductBrand().getCompany());
                        Log.d(TAG, "company " + productSku.getProductBrand().getCompany().getCompanyName());
                        companyDistributorMap.setDistributor(distributor);
                        SnapInventoryUtils.getDatabaseHelper(context).getCompanyDistributorDao().create(companyDistributorMap);                        
                    }
                    if (null != distributorProductHashMap && !distributorProductHashMap.containsKey(child.getProductSkuID()) && child.getProductBrandID() == 1302) {
                        DistributorProductMap distributorProductMap = new DistributorProductMap();
                        distributorProductMap.setDistributor(distributor);
                        distributorProductMap.setDistributorProductSku(productSku);
                        Log.d(TAG, "product name " + productSku.getProductSkuName());
                        SnapInventoryUtils.getDatabaseHelper(context).getDistributorProductMapDao().create(distributorProductMap);
                    }
					InventorySku inventorySku = null;
					if(child.getInventorySerialNumber() == 0) {
						inventorySku = new InventorySku(Calendar.getInstance().getTime());
						brand = SnapInventoryUtils.getDatabaseHelper(context).getBrandDao().queryForEq("brand_id", productSku.getProductBrand().getBrandId()).get(0);
                        if(!brand.isMyStore()) {
                            brand.setMyStore(true);
                            SnapInventoryUtils.getDatabaseHelper(context).getBrandDao().update(brand);
                        }
					} else {
						inventorySku = SnapInventoryUtils.getDatabaseHelper(context).getInventorySkuDao().queryForEq("inventory_slno", child.getInventorySerialNumber()).get(0);
					}
					if (child.isMRPChanged()) {
                        productSku.setProductSkuAlternateMrp(productSku.getProductSkuMrp());
                        productSku.setProductSkuMrp(child.getProductPrice());
                        productSku.setProductSkuAltSalePrice(productSku.getProductSkuSalePrice());
                        productSku.setProductSkuSalePrice(productSku.getProductSkuMrp());
                        SnapInventoryUtils.getDatabaseHelper(context).getProductSkuDao().update(productSku);
                        if (child.getProductSalePrice() > child.getProductPrice()) {
                            Log.d("receive without PO", "offer set as false");
                            inventorySku.setOffer(false);
                        } else {
                            inventorySku.setOffer(child.isOffer());
                        }
                    }
					inventorySku.setPurchasePrice(child.getProductPurchasePrice());
					inventorySku.setTaxRate(child.getVATRate());
					inventorySku.setQuantity(child.getProductReceivedQty() + child.getProductQty());
					inventorySku.setProductSku(productSku);
					SnapInventoryUtils.getDatabaseHelper(context).getInventorySkuDao().createOrUpdate(inventorySku);
				
					SnapInventoryUtils.getDatabaseHelper(context).getInventoryBatchDao().create(new InventoryBatch(child.getProductSkuID(), 
							child.getProductPrice(), child.getProductPrice(), 
							child.getProductPurchasePrice(), Calendar.getInstance().getTime(), null, 
							child.getProductReceivedQty(), order.getOrderNumber(), child.getVATRate()));
					
					//received items details
                    ReceiveItems receiveItems = new ReceiveItems();
                    receiveItems.setInvoiceNumber(invoiceID);
                    receiveItems.setProductSkuID(productSku);
                    receiveItems.setProductSkuCode(productSku.getProductSkuCode());
                    receiveItems.setPurchasePrice(child.getProductPurchasePrice());
                    receiveItems.setReceiveDate((new SimpleDateFormat(SnapInventoryConstants.DATE_FORMAT_DD_MM_YYYY, Locale.US)).format(Calendar.getInstance().getTime()));
                    receiveItems.setReceivedQty(child.getProductReceivedQty());
                    receiveItems.setVatRate(child.getVATRate());
                    receiveItems.setVatAmount(child.getVATAmount());
                    SnapInventoryUtils.getDatabaseHelper(context).getReceiveItemsDao().create(receiveItems);
					}
				}	
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		if(result){
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		}else{
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
	
	
}
