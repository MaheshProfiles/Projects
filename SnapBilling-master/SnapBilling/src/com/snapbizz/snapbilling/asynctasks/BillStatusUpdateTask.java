package com.snapbizz.snapbilling.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.UpdateBuilder;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.Invoice;
import com.snapbizz.snaptoolkit.domains.Transaction;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.TransactionType;

public class BillStatusUpdateTask extends AsyncTask<Transaction, Void, Boolean> {
	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to update the record";
	private boolean isPaid;
	private TransactionType mTransactionType;
	
	public BillStatusUpdateTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode, boolean isPaid) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.isPaid = isPaid;
	}

	@Override
	protected Boolean doInBackground(Transaction... transactions) {
				
		try {
			if (isPaid) {
				if (transactions[0].isInvoice()) {
					mTransactionType = TransactionType.BILL;
				} else {
					mTransactionType = TransactionType.MEMO;
				}
			} else {
				if (transactions[0].isInvoice()) {
					mTransactionType = TransactionType.DELIVERY_INVOICE;
				} else {
					mTransactionType = TransactionType.DELIVERY_MEMO;
				}
			}
			transactions[0].setIsPaid(isPaid);
			transactions[0].setTransactionType(mTransactionType);
			SnapBillingUtils.getHelper(context).getTransactionDao().update(transactions[0]);
			if(transactions[0].isInvoice()){
				UpdateBuilder<Invoice, Integer> updateBuilder = SnapBillingUtils.getHelper(context).getInvoiceDao().updateBuilder();
				updateBuilder.where().eq("transaction_id", transactions[0].getTransactionId());
				updateBuilder.updateColumnValue("transaction_type", mTransactionType);
				updateBuilder.updateColumnValue("is_paid", isPaid);
				updateBuilder.update();
//				UpdateBuilder<Transaction, Integer> updateTransactionBuilder = SnapBillingUtils.getHelper(context).getTransactionDao().updateBuilder();
//				updateTransactionBuilder.where().eq("transaction_id", transactions[0].getTransactionId());
//				updateTransactionBuilder.updateColumnValue("is_paid", isPaid);
//				updateTransactionBuilder.update();
			}
			System.out.println(transactions[0]);
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
