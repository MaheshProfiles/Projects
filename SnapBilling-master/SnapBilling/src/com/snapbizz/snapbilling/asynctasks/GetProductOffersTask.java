package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.ColumnArg;
import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class GetProductOffersTask extends
		AsyncTask<Void, Void, List<ProductSku>> {

	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "No Products Found";
	private int shoppingCartId;

	public GetProductOffersTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode, int shoppingCartId) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.shoppingCartId = shoppingCartId;

	}

	@Override
	protected List<ProductSku> doInBackground(Void... params) {
		try {
			QueryBuilder<InventorySku, Integer> inventoryQueryBuilder = SnapBillingUtils.getHelper(
					context).getInventorySkuDao().queryBuilder();
			inventoryQueryBuilder.where().eq("is_offer", true).or().eq("show_store", true);
			QueryBuilder <ProductSku, Integer> productQueryBuilder=SnapBillingUtils.getHelper(context).getProductSkuDao().queryBuilder();
			productQueryBuilder.where().lt("sku_saleprice",new ColumnArg("sku_mrp")).and().not().like("sku_id", "snaplocalcampaign%");
			 List<ProductSku> productSkuList = productQueryBuilder.join(inventoryQueryBuilder).query();
			 if(!productSkuList.isEmpty() && this.shoppingCartId == SnapBillingConstants.LAST_SHOPPING_CART) {
					for(int i = 0; i < productSkuList.size(); i++) {
						List<InventorySku> inventorySkuSkuList = SnapBillingUtils.getHelper(context).getInventorySkuDao().queryForEq("inventory_sku_id", productSkuList.get(i).getProductSkuCode());	
						if(inventorySkuSkuList != null && !inventorySkuSkuList.isEmpty()) {
							productSkuList.get(i).setProductSkuSalePrice(inventorySkuSkuList.get(0).getPurchasePrice());
						}
					}
				}
	            return productSkuList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<ProductSku> result) {
		if (result != null && result.size() != 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
		onQueryCompleteListener = null;
		context = null;
	}

}
