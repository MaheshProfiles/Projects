package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class GetProductCategoriesNameTask extends
		AsyncTask<ArrayList<Integer>, Void, List<ProductCategory>> {
	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "";

	public GetProductCategoriesNameTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected List<ProductCategory> doInBackground(
			ArrayList<Integer>... subcategoryId) {
		try {
			QueryBuilder<ProductCategory, Integer> productCartegoryQueryBuilder = SnapBillingUtils.getHelper(
					context).getProductCategoryDao().queryBuilder();
			List<ProductCategory> products = new ArrayList<ProductCategory>();
			for (int i = 0; i < subcategoryId[0].size(); i++) {
				System.out.println(" length " + subcategoryId.length);
				productCartegoryQueryBuilder.where().eq("product_category_id",
						subcategoryId[0].get(i));
				products.add(productCartegoryQueryBuilder.query().get(0));
			}
			return products;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<ProductCategory> result) {
		if (result != null) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
	}

}
