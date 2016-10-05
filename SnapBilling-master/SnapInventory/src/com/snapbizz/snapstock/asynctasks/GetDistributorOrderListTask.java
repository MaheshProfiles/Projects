package com.snapbizz.snapstock.asynctasks;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snapstock.domains.ProductBean;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class GetDistributorOrderListTask extends AsyncTask<Integer, Void, List<ProductBean>>{

	private Context context;
	private int taskCode;
	private String errorMessage = "Unable to get product(s)";
	private OnQueryCompleteListener onQueryCompleteListener;
	
	public GetDistributorOrderListTask(Context context, int taskCode, OnQueryCompleteListener onQueryCompleteListener){
		this.context = context;
		this.taskCode = taskCode;
		this.onQueryCompleteListener = onQueryCompleteListener;
	}
	
	@Override
	protected List<ProductBean> doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		try{
			GenericRawResults<String[]> rawResults = SnapInventoryUtils
					.getDatabaseHelper(context)
					.getProductSkuDao()
					.queryRaw(
							
							"SELECT"
									+ " product_sku.sku_name, product_sku.sku_mrp, inventory_sku.inventory_sku_quantity, "
									+ "product_sku.sku_subcategory_id, AVG ( order_details.to_order_qty ), SUM ( order_details.pending_qty ), "
									+ "product_sku.brand_id, product_sku.sku_id, inventory_sku.is_offer, product_sku.product_imageurl, product_sku.is_gdb, inventory_sku.inventory_slno, brand.company_id "
									+ "FROM product_sku INNER JOIN distributor_brand_map ON product_sku.brand_id = distributor_brand_map.brand_id "
									+ "INNER JOIN inventory_sku ON product_sku.sku_id = inventory_sku.inventory_sku_id "
									+ "INNER JOIN brand ON product_sku.brand_id = brand.brand_id "
									+ "LEFT JOIN order_details ON product_sku.sku_id = order_details.product_sku_id WHERE "
									+ "distributor_brand_map.distributor_id = '"
									+ params[0]
									+ "' AND brand.brand_name != 'Others' GROUP BY product_sku.sku_id");
			
			List<ProductBean> productList = new ArrayList<ProductBean>();
			ProductBean productBean;
			for (String[] strings : rawResults) {
				productBean = new ProductBean();
				productBean.setProductName(strings[0]);
				productBean.setProductPrice(Float.parseFloat(strings[1]));
				productBean.setProductQty(Float.parseFloat(strings[2]));
				productBean.setProductCategoryID(Integer.parseInt(strings[3]));
				if(null != strings[4]){
					productBean.setProductToOrder((int) Float.parseFloat(strings[4]));
					productBean.setProductPendingOrder(Integer.parseInt(strings[5]));
				}else{
					productBean.setProductToOrder(0);
					productBean.setProductPendingOrder(0);
				}
				productBean.setProductBrandID(Integer.parseInt(strings[6]));
				productBean.setProductSkuID(strings[7]);
				productBean.setOffer(Integer.parseInt(strings[8]) == 1 ? true : false);
				productBean.setImageUri(strings[9]);
				productBean.setGDB(Integer.parseInt(strings[10]) == 1 ? true : false);
				productBean.setInventorySerialNumber(Integer.parseInt(strings[11]));
				productBean.setProductCompanyID(Integer.parseInt(strings[12]));
				productList.add(productBean);
			}
			
			GenericRawResults<String[]> rawResults2 = SnapInventoryUtils.getDatabaseHelper(context).getProductSkuDao().queryRaw("SELECT"
					+ " product_sku.sku_name, product_sku.sku_mrp, inventory_sku.inventory_sku_quantity, "
					+ "product_sku.sku_subcategory_id, AVG ( order_details.to_order_qty ), SUM ( order_details.pending_qty ), "
					+ "product_sku.brand_id, product_sku.sku_id, inventory_sku.is_offer, product_sku.product_imageurl, product_sku.is_gdb, inventory_sku.inventory_slno, brand.company_id "
					+ "FROM product_sku INNER JOIN distributor_brand_map ON product_sku.brand_id = distributor_brand_map.brand_id "
					+ "INNER JOIN inventory_sku ON product_sku.sku_id = inventory_sku.inventory_sku_id "
					+ "INNER JOIN brand ON product_sku.brand_id = brand.brand_id INNER JOIN distributor_product_map ON product_sku.sku_id = distributor_product_map.sku_id "
					+ "LEFT JOIN order_details ON product_sku.sku_id = order_details.product_sku_id WHERE "
					+ "distributor_product_map.distributor_id = '"
					+ params[0]
					+ "' AND brand.brand_name = 'Others' GROUP BY product_sku.sku_id");
			
			for (String[] strings : rawResults2) {
				productBean = new ProductBean();
				productBean.setProductName(strings[0]);
				productBean.setProductPrice(Float.parseFloat(strings[1]));
				productBean.setProductQty(Float.parseFloat(strings[2]));
				productBean.setProductCategoryID(Integer.parseInt(strings[3]));
				if(null != strings[4]){
					productBean.setProductToOrder((int) Float.parseFloat(strings[4]));
					productBean.setProductPendingOrder(Integer.parseInt(strings[5]));
				}else{
					productBean.setProductToOrder(0);
					productBean.setProductPendingOrder(0);
				}
				productBean.setProductBrandID(Integer.parseInt(strings[6]));
				productBean.setProductSkuID(strings[7]);
				productBean.setOffer(Integer.parseInt(strings[8]) == 1 ? true : false);
				productBean.setImageUri(strings[9]);
				productBean.setGDB(Integer.parseInt(strings[10]) == 1 ? true : false);
				productBean.setInventorySerialNumber(Integer.parseInt(strings[11]));
				productBean.setProductCompanyID(Integer.parseInt(strings[12]));
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
