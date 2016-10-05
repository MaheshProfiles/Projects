package com.snapbizz.snapbilling.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.DeletedRecords;
import com.snapbizz.snaptoolkit.domains.Transaction;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.TableType;

public class BillDeleteTask extends AsyncTask<Integer, Void, Boolean> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to delete the record.";

	public BillDeleteTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected Boolean doInBackground(Integer... params) {
		try {
		    Transaction transaction = SnapBillingUtils.getHelper(context).getTransactionDao().queryForEq("transaction_id", params[0]).get(0);
		    transaction.setVisible(false);
		    DeletedRecords deletedRecord = new DeletedRecords();
		    deletedRecord.setRecordId(transaction.getTransactionId()+"");
		    deletedRecord.setTableType(TableType.TRANSACTIONS);
		    SnapBillingUtils.getHelper(context).getTransactionDao().update(transaction);
		    SnapBillingUtils.getHelper(context).getDeletedRecordsDao().create(deletedRecord);
			return true;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
