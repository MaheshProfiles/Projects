package com.snapbizz.snapstock.asynctasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Payments;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class GetPaymentHistoryTask extends AsyncTask<Integer, Void, List<Payments>>{
	
	private Context context;
	private OnQueryCompleteListener onQueryCompleteListener;
	private int taskCode;
	private String errorMessage = "No payment history found";
	
	public GetPaymentHistoryTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode){
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected List<Payments> doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		try{
			
			QueryBuilder<Payments, Integer> paymentQueryBuilder = SnapInventoryUtils.getDatabaseHelper(context).getPaymentsDao().queryBuilder();
			
			return paymentQueryBuilder.where().eq("distributor_id", params[0]).query();
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(List<Payments> result) {
		// TODO Auto-generated method stub
		if( null != result && result.size() > 0 )
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		else
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
	}
}
