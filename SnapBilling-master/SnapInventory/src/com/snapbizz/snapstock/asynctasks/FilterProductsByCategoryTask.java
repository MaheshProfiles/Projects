package com.snapbizz.snapstock.asynctasks;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.snapbizz.snapstock.domains.ProductBean;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class FilterProductsByCategoryTask extends AsyncTask<Integer, Void, List<ProductBean>>{

	private int taskCode;
	private String errorMessage = "No product(s) found";
	private OnQueryCompleteListener onQueryCompleteListener;
	private List<ProductBean> originalList;
	
	public FilterProductsByCategoryTask(int taskCode, OnQueryCompleteListener onQueryCompleteListener, List<ProductBean> listToBeFiltered){
		this.taskCode = taskCode;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.originalList = listToBeFiltered;
	}
	
	@Override
	protected List<ProductBean> doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		
		List<ProductBean> filteredList = new ArrayList<ProductBean>();
		for (ProductBean item : originalList) {
			if(item.getProductCategoryID() == params[0]){
				filteredList.add(item);
			}
		}
		return filteredList;
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
