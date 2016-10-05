package com.snapbizz.snapstock.asynctasks;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.snapbizz.snapstock.domains.ProductBean;
import com.snapbizz.snapstock.utils.SnapInventoryConstants;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.Order;
import com.snapbizz.snaptoolkit.domains.OrderDetails;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.domains.ReceiveItems;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.OrderType;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class UpdatePurchaseOrderReceiveTask extends
AsyncTask<Void, Void, Boolean> {

	private Context context;
	private int taskCode;
	private String errorMessage = "Unable to update the purchase order";
	private String invoiceID;
	private OnQueryCompleteListener onQueryCompleteListener;
	private int purchaseOrderNo;
	private List<ProductBean> ReceiveOrderList;
	private float totalOrderDiscount;
	private float totalDiscountGiven;
	private float paymentAmount;
	private float paymentToMake;
	private Bitmap invoiceImageBitmap;

	public UpdatePurchaseOrderReceiveTask(Context context, int taskCode,
			OnQueryCompleteListener onQueryCompleteListener,
			int purchaseOrderNo,
			List<ProductBean> ReceiveOrderList,
			float totalOrderDiscount, float paymentAmount, float totalDiscountGiven, float paymentToMake, String invoice, Bitmap invoiceImageBitmap) {
		this.context = context;
		this.taskCode = taskCode;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.ReceiveOrderList = ReceiveOrderList;
		this.purchaseOrderNo = purchaseOrderNo;
		this.totalOrderDiscount = totalOrderDiscount;
		this.paymentAmount = paymentAmount;
		this.totalDiscountGiven = totalDiscountGiven;
		this.paymentToMake = paymentToMake;
		this.invoiceID = invoice;
		this.invoiceImageBitmap = invoiceImageBitmap;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub

        int pendingProductCounter = 0;
        try {
            SnapBizzDatabaseHelper databaseHelper = SnapInventoryUtils.getDatabaseHelper(context);
            UpdateBuilder<OrderDetails, Integer> orderDetailsUB = databaseHelper.getOrderDetailsDao().updateBuilder();
            UpdateBuilder<Order, Integer> orderUB = SnapInventoryUtils.getDatabaseHelper(context).getOrderDao().updateBuilder();
            orderUB.updateColumnValue("payment_to_make", paymentAmount + paymentToMake);
            orderUB.updateColumnValue("order_total_discount", totalOrderDiscount + totalDiscountGiven);
            orderUB.updateColumnValue("invoice_number", invoiceID);
            if(null != invoiceImageBitmap){
                Order order = new Order();
                order.setOrderNumber(purchaseOrderNo);
                orderUB.updateColumnValue("invoice_imageurl", SnapCommonUtils.storeImageBitmap(invoiceImageBitmap, order));
            }else{
                orderUB.updateColumnValue("invoice_imageurl", "");
            }
            String date = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime());
            orderUB.updateColumnValue("lastmodified_timestamp", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).parse(date));
            orderUB.where().eq("order_no", purchaseOrderNo);
            orderUB.update();
            
            if(null != ReceiveOrderList){
                for (ProductBean distributorProductsChild: ReceiveOrderList) {
                    if (distributorProductsChild.getProductBilledQty() > distributorProductsChild.getProductReceivedQty()) {
                        orderDetailsUB.updateColumnValue(
                                "pending_qty",
                                distributorProductsChild.getProductBilledQty()
                                - distributorProductsChild.getProductReceivedQty() - distributorProductsChild.getProductTotalReceivedQty());
                    } else {
                        orderDetailsUB.updateColumnValue("pending_qty", 0);
                    }

                    orderDetailsUB.updateColumnValue("product_purchase_price",
                            distributorProductsChild.getProductPurchasePrice());
                    orderDetailsUB.updateColumnValue("product_discount",
                            distributorProductsChild.getProductDiscount());
                    orderDetailsUB.updateColumnValue("billed_qty",
                            distributorProductsChild.getProductBilledQty());
                    if(distributorProductsChild.getProductPurchasePrice()>0){
                        orderDetailsUB.updateColumnValue("is_paid", true);
                    }                   
                    orderDetailsUB.updateColumnValue("received_qty", distributorProductsChild.getProductReceivedQty() + distributorProductsChild.getProductTotalReceivedQty());
                    date = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime());
                    orderDetailsUB.updateColumnValue("lastmodified_timestamp", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).parse(date));
                    orderDetailsUB.where().eq("order_no", purchaseOrderNo).and()
                    .eq("product_sku_id", distributorProductsChild.getProductSkuID());
                    orderDetailsUB.update();
                    
                    ProductSku productSku = databaseHelper.getProductSkuDao().queryForEq("sku_id", distributorProductsChild.getProductSkuID()).get(0);              
                    InventorySku inventorySku = null;
                    if(distributorProductsChild.getInventorySerialNumber() == 0) {
                        inventorySku = new InventorySku(Calendar.getInstance().getTime());
                        Brand brand = databaseHelper.getBrandDao().queryForEq("brand_id", productSku.getProductBrand().getBrandId()).get(0);
                        if(!brand.isMyStore()) {
                            brand.setMyStore(true);
                            databaseHelper.getBrandDao().update(brand);
                        }
                    } else {
                        inventorySku = databaseHelper.getInventorySkuDao().queryForEq("inventory_slno", distributorProductsChild.getInventorySerialNumber()).get(0);
                    }
                    if(distributorProductsChild.isMRPChanged()) {
                        productSku.setProductSkuAlternateMrp(productSku.getProductSkuMrp());
                        productSku.setProductSkuMrp(distributorProductsChild.getProductPrice());
                        productSku.setProductSkuAltSalePrice(productSku.getProductSkuSalePrice());
                        productSku.setProductSkuSalePrice(productSku.getProductSkuMrp());
                        databaseHelper.getProductSkuDao().update(productSku);
                        if (distributorProductsChild.getProductSalePrice() > distributorProductsChild.getProductPrice()) {
                            Log.d("update purchase order receive", "offer set as false");
                            inventorySku.setOffer(false);
                        } else {
                            inventorySku.setOffer(distributorProductsChild.isOffer());
                        }
                    }
					inventorySku.setQuantity((distributorProductsChild.getProductReceivedQty() + distributorProductsChild.getProductQty()));
					inventorySku.setPurchasePrice(distributorProductsChild.getProductPurchasePrice());
					inventorySku.setTaxRate(distributorProductsChild.getVATRate());
					inventorySku.setProductSku(productSku);
					databaseHelper.getInventorySkuDao().createOrUpdate(inventorySku);

					Log.d(""," received "+distributorProductsChild.getProductReceivedQty());
					databaseHelper.getInventoryBatchDao().create(new InventoryBatch(distributorProductsChild.getProductSkuID(), 
							distributorProductsChild.getProductPrice(), distributorProductsChild.getProductPrice(), 
							distributorProductsChild.getProductPurchasePrice(), Calendar.getInstance().getTime(), null, 
							distributorProductsChild.getProductReceivedQty(), purchaseOrderNo, distributorProductsChild.getVATRate()));
					
					//received items details
					ReceiveItems receiveItems = new ReceiveItems();
					receiveItems.setInvoiceNumber(invoiceID);
					receiveItems.setProductSkuID(productSku);
					receiveItems.setProductSkuCode(productSku.getProductSkuCode());
					receiveItems.setPurchasePrice(distributorProductsChild.getProductPurchasePrice());
					receiveItems.setReceiveDate((new SimpleDateFormat(SnapInventoryConstants.DATE_FORMAT_DD_MM_YYYY, Locale.US)).format(Calendar.getInstance().getTime()));
					receiveItems.setReceivedQty(distributorProductsChild.getProductReceivedQty());
					receiveItems.setVatRate(distributorProductsChild.getVATRate());
					receiveItems.setVatAmount(distributorProductsChild.getVATAmount());
					databaseHelper.getReceiveItemsDao().create(receiveItems);
				}
			}			


			GenericRawResults<String[]> rawResults = databaseHelper.getOrderDetailsDao().queryRaw(
					"select count(*) from order_details where pending_qty > 0 and order_no="
							+ purchaseOrderNo + "");

			for (String[] strings : rawResults) {
				pendingProductCounter = Integer.parseInt(strings[0].toString());
			}

			if (pendingProductCounter == 0) {				
				orderUB.updateColumnValue("order_status", OrderType.getOrderEnum(OrderType.RECEIVED.getOrderType()));
				orderUB.where().eq("order_no", purchaseOrderNo);
				orderUB.update();

			} 

			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
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
