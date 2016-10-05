package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;

public class SearchProductSkuTask extends AsyncTask<String, Void, List<ProductSku>>{

    private Context context;
    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private final String errorMessage = "No Matching Products Found";
	private int shoppingCartId;


    public SearchProductSkuTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode, int shoppingCartId) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
		this.shoppingCartId = shoppingCartId;

    }

    @Override
    protected List<ProductSku> doInBackground(String... keyword) {
        try {
            SnapBizzDatabaseHelper databaseHelper = SnapBillingUtils.getHelper(context);
            QueryBuilder<ProductSku, Integer> productSkuQueryBuilder = databaseHelper.getProductSkuDao().queryBuilder().limit(40);
            keyword[0] = keyword[0].replaceAll(" ", "%");
            QueryBuilder<InventorySku, Integer> inventorySkuQueryBuilder = databaseHelper.getInventorySkuDao().queryBuilder();
            productSkuQueryBuilder.join(inventorySkuQueryBuilder);
            List<ProductSku> productSkuList = productSkuQueryBuilder.where().notIn("sku_id", context.getResources().getStringArray(R.array.quickadd_array)).and().like("sku_id","%"+keyword[0]+"%").and().not().like("sku_id", "snaplocalcampaign%").or().like("sku_name", "%"+keyword[0]+"%").query();
			if(!productSkuList.isEmpty() && this.shoppingCartId == SnapBillingConstants.LAST_SHOPPING_CART){
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
        if(result != null && result.size() != 0) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
        onQueryCompleteListener = null;
        context = null;
    }
   
}
