package com.snapbizz.snapstock.asynctasks;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snapstock.domains.ProductBean;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class GetProductsToReceiveTask extends AsyncTask<Integer, Void, List<ProductBean>>{

	private Context context;
	private int taskCode;
	private String errorMessage = "Unable to get product(s)";
	private OnQueryCompleteListener onQueryCompleteListener;
	
	public GetProductsToReceiveTask(Context context, int taskCode, OnQueryCompleteListener onQueryCompleteListener){
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}
	
	@Override
	protected List<ProductBean> doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		try{
			GenericRawResults<String[]> rawResults = SnapInventoryUtils
					.getDatabaseHelper(context)
					.getProductSkuDao()
					.queryRaw(
							"SELECT product_sku.sku_id, product_sku.sku_name, product_sku.sku_mrp, product_sku.vat, inventory_sku.purchase_price, inventory_sku.is_offer, " +
							"inventory_sku.inventory_slno, product_sku.brand_id, product_sku.sku_subcategory_id, product_sku.product_imageurl, inventory_sku.inventory_sku_quantity," +
							" product_sku.is_gdb, brand.company_id, product_sku.sku_saleprice FROM product_sku INNER JOIN distributor_brand_map ON product_sku.brand_id = distributor_brand_map.brand_id "
									+ " INNER JOIN inventory_sku ON product_sku.sku_id = inventory_sku.inventory_sku_id INNER JOIN brand ON product_sku.brand_id = brand.brand_id WHERE distributor_brand_map.distributor_id = " + params[0] + " AND brand.brand_name != 'Others' GROUP BY product_sku.sku_id");
			List<ProductBean> productList = new ArrayList<ProductBean>();
			ProductBean productBean;
			for (String[] strings : rawResults) {
				productBean = new ProductBean();
				productBean.setProductSkuID(strings[0]);
				productBean.setProductName(strings[1]);
				productBean.setProductPrice(Float.parseFloat(strings[2]));
				productBean.setProductPurchasePrice(Float.parseFloat(strings[4]));
				productBean.setProductDiscount(0);
				productBean.setProductPendingOrder(0);
				productBean.setProductQty(Float.parseFloat(strings[10]));
				productBean.setOffer(Integer.parseInt(strings[5]) == 1 ? true : false);
				productBean.setProductBrandID(Integer.parseInt(strings[7]));
				productBean.setProductCategoryID(Integer.parseInt(strings[8]));
				productBean.setInventorySerialNumber(Integer.parseInt(strings[6]));
				productBean.setProductNetAmount(0);
				productBean.setProductTotalAmount(0);
				if (null != strings[3] && strings[3].length() > 0) {
					String input = SnapCommonUtils.StripPercentage(strings[3]);					
				    productBean.setVATRate(Float.parseFloat(input));
				} else {
				    productBean.setVATRate(0);
				}
				productBean.setVATAmount(0);
				productBean.setProductBilledQty(0);
				productBean.setProductToReceiveQty(0);
				productBean.setProductReceivedQty(0);
				productBean.setImageUri(strings[9]);
				productBean.setGDB(Integer.parseInt(strings[11]) == 1 ? true : false);
				productBean.setProductCompanyID(Integer.parseInt(strings[12]));
				productBean.setProductSalePrice(Float.parseFloat(strings[13]));
				productList.add(productBean);
			}
			
			GenericRawResults<String[]> rawResults2 = SnapInventoryUtils
					.getDatabaseHelper(context)
					.getProductSkuDao()
					.queryRaw(
							"SELECT product_sku.sku_id, product_sku.sku_name, product_sku.sku_mrp, product_sku.vat, inventory_sku.purchase_price, inventory_sku.is_offer, " +
							"inventory_sku.inventory_slno, product_sku.brand_id, product_sku.sku_subcategory_id, product_sku.product_imageurl, inventory_sku.inventory_sku_quantity," +
							" product_sku.is_gdb, brand.company_id, product_sku.sku_saleprice FROM product_sku INNER JOIN distributor_brand_map ON product_sku.brand_id = distributor_brand_map.brand_id "
							+ "INNER JOIN distributor_product_map ON distributor_product_map.sku_id = product_sku.sku_id "
									+ " INNER JOIN inventory_sku ON product_sku.sku_id = inventory_sku.inventory_sku_id INNER JOIN brand ON product_sku.brand_id = brand.brand_id WHERE distributor_product_map.distributor_id = " + params[0] + " AND brand.brand_name = 'Others' GROUP BY product_sku.sku_id");

			for (String[] strings : rawResults2) {
				productBean = new ProductBean();
				productBean.setProductSkuID(strings[0]);
				productBean.setProductName(strings[1]);
				productBean.setProductPrice(Float.parseFloat(strings[2]));
				productBean.setProductPurchasePrice(Float.parseFloat(strings[4]));
				productBean.setProductDiscount(0);
				productBean.setProductPendingOrder(0);
				productBean.setProductQty(Float.parseFloat(strings[10]));
				productBean.setOffer(Integer.parseInt(strings[5]) == 1 ? true : false);
				productBean.setProductBrandID(Integer.parseInt(strings[7]));
				productBean.setProductCategoryID(Integer.parseInt(strings[8]));
				productBean.setInventorySerialNumber(Integer.parseInt(strings[6]));
				productBean.setProductNetAmount(0);
				productBean.setProductTotalAmount(0);
				if (null != strings[3] && strings[3].length() > 0) {
					String input = SnapCommonUtils.StripPercentage(strings[3]);					
				    productBean.setVATRate(Float.parseFloat(input));
				} else {
				    productBean.setVATRate(0);
				}
				productBean.setVATAmount(0);
				productBean.setProductBilledQty(0);
				productBean.setProductToReceiveQty(0);
				productBean.setProductReceivedQty(0);
				productBean.setImageUri(strings[9]);
				productBean.setGDB(Integer.parseInt(strings[11]) == 1 ? true : false);
				productBean.setProductCompanyID(Integer.parseInt(strings[12]));
				productBean.setProductSalePrice(Float.parseFloat(strings[13]));
				productList.add(productBean);
			}
			
			return productList;
		}catch(java.sql.SQLException e){
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
