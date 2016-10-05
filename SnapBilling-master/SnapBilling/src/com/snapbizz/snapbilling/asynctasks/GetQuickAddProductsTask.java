package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetQuickAddProductsTask extends AsyncTask<ProductCategory, Void, List<ProductSku>> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to Get Quick Add products";
	private int shoppingCartId;

	public GetQuickAddProductsTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode,int shoppingCartId) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.shoppingCartId = shoppingCartId;
	}

	@Override
	protected List<ProductSku> doInBackground(ProductCategory... params) {
		try {
			List<ProductSku> productSkuList = null;
			String order_by = SnapSharedUtils.getSortingCheckValue(SnapCommonUtils.getSnapContext(context)) ? "sku_name" : "sku_id";
			if(params[0].getCategoryId() == SnapToolkitConstants.XTRA_PRODUCTS_CAT_ID) {
				productSkuList = SnapBillingUtils.getHelper(context).getProductSkuDao().queryBuilder().orderBy(order_by, true)
												 .where().eq("is_quickadd_product", true).and()
												 .notIn("subcategory_name", (Object[])context.getResources().getStringArray(R.array.quickaddCategoryArray))
												 .query();
			} else {
				productSkuList = SnapBillingUtils.getHelper(context).getProductSkuDao().queryBuilder().orderBy(order_by, true)
												 .where().eq("sku_subcategory_id", params[0].getCategoryId()).and()
												 .eq("is_quickadd_product", true).query();
			}
			if(!productSkuList.isEmpty() && this.shoppingCartId == SnapBillingConstants.LAST_SHOPPING_CART) {
				for(int i = 0; i <productSkuList.size(); i++) {
					List<InventorySku> inventorySkuSkuList = SnapBillingUtils.getHelper(context).getInventorySkuDao()
																			 .queryForEq("inventory_sku_id", productSkuList.get(i).getProductSkuCode());
					if(inventorySkuSkuList != null && !inventorySkuSkuList.isEmpty())
						productSkuList.get(i).setProductSkuSalePrice(inventorySkuSkuList.get(0).getPurchasePrice());
				}
			}
			return productSkuList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
	    return null;
	}

	@Override
	protected void onPostExecute(List<ProductSku> result) {
		if (result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
		onQueryCompleteListener = null;
		context = null;
	}

}
