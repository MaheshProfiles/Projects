package com.snapbizz.snapbilling.asynctasks;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Window;

import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class GetBillingItemsTask extends AsyncTask<Void, Void, List<BillItem>> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "Unable to get transaction details.";
	private Context context;
	private int taskCode;
	private int transactionId;
	private ProgressDialog progressDialog;

	public GetBillingItemsTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode,
			int transactionId) {
		this.context = context;
		this.taskCode = taskCode;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.transactionId = transactionId;
		progressDialog = new ProgressDialog(context);
	    progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    progressDialog.show();
	}


	@Override
	protected List<BillItem> doInBackground(Void... params) {
		try {
			List<BillItem> billItemList = SnapBillingUtils.getHelper(context).getBillItemDao().queryBuilder().where()
					.eq("transaction_id", transactionId).query();
			return billItemList;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			progressDialog.cancel();
			progressDialog.dismiss();
		}
	}

	@Override
	protected void onPostExecute(List<BillItem> result) {
		if (result != null && result.size() != 0) 
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		else
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
	}
}
