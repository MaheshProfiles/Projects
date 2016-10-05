package com.snapbizz.snapstock.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Payments;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class AddPaymentTask extends AsyncTask<Void, Void, Boolean>{
	
	private int taskCode;
	private Context context;
	private OnQueryCompleteListener onQueryCompleteListener;
	private String errorMessage = "Unable to add payment";
	private Payments payment;
	
	public AddPaymentTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode, Payments payment){
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.payment = payment;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try{
			SnapInventoryUtils.getDatabaseHelper(context).getPaymentsDao().createOrUpdate(payment);
			return true;
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		if(result)
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		else
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
	}
}
