package com.snapbizz.snaptoolkit.asynctasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.Payments;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class StorePaymentTask  extends AsyncTask<List<Payments>, Void, Boolean>{

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to store records.";
	private SnapBizzDatabaseHelper databaseHelper;

	public StorePaymentTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected Boolean doInBackground(List<Payments>... paymentsList) {
		try {
			if(paymentsList[0] == null)
				return false;
			databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
			for(Payments payments : paymentsList[0]) {
				Distributor distributor = new Distributor();
				distributor.setDistributorId(payments.getDistributorId());
				payments.setDistributor(distributor);
				databaseHelper.getPaymentsDao().create(payments);
			}
			String timestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime());
            SnapSharedUtils.storeLastPaymentSyncTimestamp(timestamp, SnapCommonUtils.getSnapContext(context));
			return true;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		if (result) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
