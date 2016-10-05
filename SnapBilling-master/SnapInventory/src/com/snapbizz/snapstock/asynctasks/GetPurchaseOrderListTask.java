package com.snapbizz.snapstock.asynctasks;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snapstock.domains.ProductBean;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class GetPurchaseOrderListTask extends AsyncTask<Integer, Void, List<ProductBean>>{
	
	private Context context;
	private int taskCode;
	private String errorMessage = "Unable to get product(s)";
	private OnQueryCompleteListener onQueryCompleteListener;
	
	public GetPurchaseOrderListTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode){
		this.context = context;
		this.taskCode = taskCode;
		this.onQueryCompleteListener = onQueryCompleteListener;
	}

	@Override
	protected List<ProductBean> doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		try{
			GenericRawResults<String[]> rawResults = SnapInventoryUtils.getDatabaseHelper(context).getOrderDao().queryRaw(
					"SELECT product_sku.sku_name, inventory_sku.inventory_sku_quantity, "
							+ "order_details.pending_qty, product_sku.sku_mrp, inventory_sku.purchase_price, order_details.product_discount,"
							+ " product_sku.sku_id, inventory_sku.is_offer, inventory_sku.inventory_slno, product_sku.brand_id,"
							+ "product_sku.sku_subcategory_id, product_sku.product_imageurl, product_sku.vat, "
							+ "order_details.billed_qty, order_details.to_receive_qty, order_details.received_qty, order_details.is_paid, product_sku.is_gdb, brand.company_id, product_sku.sku_saleprice "
							+ "FROM product_sku INNER JOIN order_details ON product_sku.sku_id = order_details.product_sku_id INNER JOIN brand ON product_sku.brand_id = brand.brand_id "
							+ "LEFT JOIN inventory_sku ON product_sku.sku_id = inventory_sku.inventory_sku_id WHERE order_details.order_no ="+ params[0] + " AND order_details.pending_qty > 0");
			List<ProductBean> receiveList = new ArrayList<ProductBean>();
			ProductBean productBean;
			for (String[] strings : rawResults) {
				productBean = new ProductBean();
				productBean.setProductName(strings[0]);
				if(null != strings[8]){
					productBean.setInventorySerialNumber(Integer.parseInt(strings[8]));
					productBean.setProductQty(Float.parseFloat(strings[1]));
					productBean.setProductPurchasePrice(Float.parseFloat(strings[4]));
					productBean.setOffer(Integer.parseInt(strings[7]) == 1 ? true : false);
				}else{
					productBean.setInventorySerialNumber(0);
					productBean.setProductQty(0);
					productBean.setProductPurchasePrice(0);
					productBean.setOffer(false);
				}				
				productBean.setProductPendingOrder(Integer.parseInt(strings[2]));
				productBean.setProductToOrder(Integer.parseInt(strings[2]));
				productBean.setProductPrice(Float.parseFloat(strings[3]));				
				productBean.setProductDiscount(Float.parseFloat(strings[5]));
				productBean.setProductSkuID(strings[6]);				
				productBean.setProductBrandID(Integer.parseInt(strings[9]));
				productBean.setProductCategoryID(Integer.parseInt(strings[10]));
				productBean.setImageUri(strings[11]);
				if (null != strings[12] && strings[12].length() > 0) {
					String input = SnapCommonUtils.StripPercentage(strings[12]);					
				    productBean.setVATRate(Float.parseFloat(input));
				} else {
				    productBean.setVATRate(0);
				}				
				productBean.setProductBilledQty(Integer.parseInt(strings[13]));
				productBean.setProductToReceiveQty(Integer.parseInt(strings[14]));
				productBean.setPaid(Integer.parseInt(strings[16]) == 1 ? true : false);
				productBean.setGDB(Integer.parseInt(strings[17]) == 1 ? true : false);
				productBean.setProductCompanyID(Integer.parseInt(strings[18]));
				productBean.setProductSalePrice(Float.parseFloat(strings[19]));
				if(Integer.parseInt(strings[15]) != 0){
					productBean.setProductReceivedQty(Integer.parseInt(strings[2]));
					productBean.setProductTotalReceivedQty(Integer.parseInt(strings[15]));
				}else{
					productBean.setProductReceivedQty(productBean.getProductBilledQty());
				}
				productBean.setVATAmount((productBean.getProductPurchasePrice() * productBean.getVATRate()) / (100 + productBean.getVATRate()) * productBean.getProductBilledQty());
		        productBean.setProductNetAmount((productBean.getProductPurchasePrice() * productBean.getProductBilledQty()) - productBean.getProductDiscount() - productBean.getVATAmount());
		        productBean.setProductTotalAmount(productBean.getProductNetAmount() + productBean.getVATAmount());
				receiveList.add(productBean);
			}
			return receiveList;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(List<ProductBean> result) {
		// TODO Auto-generated method stub
		if(null != result && result.size()>0){
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		}else{
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
}
