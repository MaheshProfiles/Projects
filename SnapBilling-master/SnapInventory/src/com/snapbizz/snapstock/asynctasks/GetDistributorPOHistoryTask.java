package com.snapbizz.snapstock.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Order;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class GetDistributorPOHistoryTask extends AsyncTask<Integer, Void, List<Order>>{
	
	private Context context;
	private int taskCode;
	private String errorMessage = "No purchase order history found";
	private OnQueryCompleteListener onQueryCompleteListener;
	
	public GetDistributorPOHistoryTask(Context context, int taskCode, OnQueryCompleteListener onQueryCompleteListener){
		this.context = context;
		this.taskCode = taskCode;
		this.onQueryCompleteListener = onQueryCompleteListener;
	}

	@Override
	protected List<Order> doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		
		try {

			QueryBuilder<Order, Integer> orderQueryBuilder = SnapInventoryUtils.getDatabaseHelper(context).getOrderDao().queryBuilder();
			
			return orderQueryBuilder.where().eq("distributor_id", params[0]).query();

		} catch (java.sql.SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	@Override
	protected void onPostExecute(List<Order> result) {
		// TODO Auto-generated method stub
		if (null != result && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
