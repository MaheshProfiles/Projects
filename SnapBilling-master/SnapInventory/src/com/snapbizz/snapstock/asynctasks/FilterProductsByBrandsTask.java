package com.snapbizz.snapstock.asynctasks;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.snapbizz.snapstock.domains.ProductBean;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class FilterProductsByBrandsTask extends AsyncTask<Void, Void, List<ProductBean>> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private String errorMessage = "No products found for the particular brand(s)";
	private int taskCode;
	private List<Brand> filterBrandList;
	private List<ProductBean> productList;

	public FilterProductsByBrandsTask(OnQueryCompleteListener onQueryCompleteListener, int taskCode,	List<Brand> filterBrandList, List<ProductBean> listToBeFiltered) {
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.filterBrandList = filterBrandList;
		this.productList = listToBeFiltered;
	}

	@Override
	protected List<ProductBean> doInBackground(
			Void... params) {
		// TODO Auto-generated method stub
		List<ProductBean> filteredList = new ArrayList<ProductBean>();
		for (Brand brand : filterBrandList) {
			for (ProductBean item : productList) {
				if(item.getProductBrandID() == brand.getBrandId()) {
					filteredList.add(item);
				}
			}
		}
		return filteredList;
	}

	@Override
	protected void onPostExecute(List<ProductBean> result) {
		// TODO Auto-generated method stub
		if (null != result && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
