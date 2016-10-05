package com.snapbizz.snapstock.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.DeletedRecords;
import com.snapbizz.snaptoolkit.domains.Payments;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.TableType;

public class DeletePaymentMadeTask extends AsyncTask<Void, Void, Boolean>{

	private Context context;
	private OnQueryCompleteListener onQueryCompleteListener;
	private int taskCode;
	private String errorMessage = "Unable to delete";
	private Payments payment;

	public DeletePaymentMadeTask( Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode, Payments payment){
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.payment = payment;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try{
			SnapCommonUtils.getDatabaseHelper(context).getPaymentsDao().delete(payment);
			DeletedRecords deletedRecord= new DeletedRecords();
            deletedRecord.setRecordId(payment.getPaymentID()+"");
            deletedRecord.setTableType(TableType.PAYMENTS);
            SnapCommonUtils.getDatabaseHelper(context).getDeletedRecordsDao().createOrUpdate(deletedRecord);
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
