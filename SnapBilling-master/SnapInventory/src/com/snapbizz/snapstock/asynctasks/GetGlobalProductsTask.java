package com.snapbizz.snapstock.asynctasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snapstock.domains.ProductBean;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class GetGlobalProductsTask extends AsyncTask<String, Void, List<ProductBean>>{

	private Context context;
	private int taskCode;
	private String errorMessage = "No product(s) found";
	private OnQueryCompleteListener onQueryCompleteListener;
	private HashMap<String, ProductBean> productBeanMap;
	
	public GetGlobalProductsTask(Context context, int taskCode, OnQueryCompleteListener onQueryCompleteListener, HashMap<String, ProductBean> productBeanMap){
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.productBeanMap = productBeanMap;
	}
	
	@Override
	protected List<ProductBean> doInBackground(String... params) {
		// TODO Auto-generated method stub
		try{
			params[0] = params[0].replaceAll(" ", "%");
			GenericRawResults<String[]> rawResults = SnapInventoryUtils
					.getDatabaseHelper(context)
					.getProductSkuDao()
					.queryRaw("SELECT product_sku.sku_id, product_sku.sku_name, product_sku.sku_mrp, product_sku.sku_subcategory_id, brand.brand_id, product_sku.product_imageurl, " +
							"product_sku.is_gdb, brand.company_id, inventory_sku.inventory_sku_quantity, inventory_sku.is_offer, inventory_sku.inventory_slno, product_sku.vat"
							+ " FROM product_sku INNER JOIN brand ON product_sku.brand_id = brand.brand_id LEFT JOIN inventory_sku ON " +
							"product_sku.sku_id = inventory_sku.inventory_sku_id WHERE sku_name LIKE '%" + params[0] + "%' or product_sku.sku_id like '%"+params[0]+"%' LIMIT 100");
			List<ProductBean> productList = new ArrayList<ProductBean>();
			ProductBean productBean;
			for (String[] strings : rawResults) {
				productBean = new ProductBean();
				if (productBeanMap.containsKey(strings[0])) {
					productBean = productBeanMap.get(strings[0]);
				} else {
					productBean.setProductSkuID(strings[0]);
					productBean.setProductName(strings[1]);
					productBean.setProductPrice(Float.parseFloat(strings[2]));
					productBean.setProductCategoryID(Integer.parseInt(strings[3]));
					productBean.setProductBrandID(Integer.parseInt(strings[4]));
					productBean.setImageUri(strings[5]);
					productBean.setGDB((Integer.parseInt(strings[6]) == 1 ? true : false));
					productBean.setProductCompanyID(Integer.parseInt(strings[7]));
					if(null != strings[11] && strings[11].length() > 0) {
						String input = SnapCommonUtils.StripPercentage(strings[11]);
	                    productBean.setVATRate(Float.parseFloat(input));
					} else {
					    productBean.setVATRate(0f);
					}
					if(null != strings[10]){
						productBean.setInventorySerialNumber(Integer.parseInt(strings[10]));
						productBean.setProductQty(Float.parseFloat(strings[8]));
						productBean.setOffer(Integer.parseInt(strings[9]) == 1 ? true : false);
					} else {
                        productBean.setInventorySerialNumber(0);
                        productBean.setProductQty(0);
                        productBean.setOffer(false);
                    }

					productBean.setProductPendingOrder(0);
					productBean.setProductToOrder(0);
				}				
				
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
