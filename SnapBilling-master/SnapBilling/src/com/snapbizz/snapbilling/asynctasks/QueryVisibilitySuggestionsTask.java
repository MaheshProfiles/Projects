package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class QueryVisibilitySuggestionsTask extends
		AsyncTask<ArrayList<Integer>, Void, List<List<InventorySku>>> {
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "No Inventory Found";

	public QueryVisibilitySuggestionsTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected List<List<InventorySku>> doInBackground(
			ArrayList<Integer>... params) {
		try {
			/*
			 * return SnapBillingUtils.getHelper(context).getInventorySkuDao().queryBuilder()
			 * .offset(Integer.parseInt(params[0])).limit(22).query();
			 */
			/*
			 * QueryBuilder<ProductSku, Integer> inventoryQueryBuilder =
			 * SnapBillingUtils.getHelper(context).getProductSkuDao().queryBuilder();
			 * inventoryQueryBuilder.where().eq("sku_subcategory_id",params );
			 */
			QueryBuilder<ProductSku, Integer> productSkuQueryBuilder = SnapBillingUtils.getHelper(
					context).getProductSkuDao().queryBuilder();
			List<List<InventorySku>> resultList = new ArrayList<List<InventorySku>>();
			ArrayList<InventorySku> intermitentList = new ArrayList<InventorySku>();
			int j = 0;
			for (int i = 0; i < params[0].size(); i++, j++) {
				if (j != 0 && j % 11 == 0) {
					resultList.add(intermitentList);
					intermitentList = new ArrayList<InventorySku>();
				}
				productSkuQueryBuilder.orderBy("sku_mrp", false).where()
						.eq("sku_subcategory_id", params[0].get(j));
				intermitentList.addAll(SnapBillingUtils.getHelper(context).getInventorySkuDao()
						.queryBuilder().join(productSkuQueryBuilder).limit(2)
						.query());
			}
			if (params[0].size() < 11) {
				resultList.add(intermitentList);
			}
			
			return resultList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<List<InventorySku>> result) {
		if (result != null && result.size() != 0) {
			System.out.println("got result success");
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
		//	System.out.println(" result.size() " + result.size());
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
