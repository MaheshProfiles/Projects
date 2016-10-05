package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.domains.Transaction;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetSummaryTask extends AsyncTask<Void, Void, Double []> {
	private static final String TAG = GetSummaryTask.class.getSimpleName();

	private Context context;
	private OnQueryCompleteListener onQueryCompleteListener;
	private int taskCode;

	public GetSummaryTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode){
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected Double [] doInBackground(Void... params) {
		Double [] summary = new Double[5];
		
		try {
			QueryBuilder<Transaction, Integer> qb = SnapBillingUtils.getHelper(context).getTransactionDao()
					.queryBuilder();
			qb.selectRaw("SUM(total_amount) - SUM(total_discount) - SUM(total_savings)");
			qb.where().like("transaction_timestamp",  new SimpleDateFormat(SnapBillingConstants.DAY_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime())+"%").and().eq("is_visible", true);
			GenericRawResults<String[]> results = SnapBillingUtils.getHelper(context).getTransactionDao().queryRaw(qb
					.prepareStatementString());
			String[] values = results.getFirstResult();
			if (values != null && values[0] != null) {
				summary[0] = Double.parseDouble(values[0]);
			} else {
				summary[0] = 0d;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			summary[0] = 0d;
		}
		try {
			QueryBuilder<Transaction, Integer> qb = SnapBillingUtils.getHelper(context).getTransactionDao()
					.queryBuilder();
			qb.where().like("transaction_timestamp",  new SimpleDateFormat(SnapBillingConstants.DAY_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime())+"%").and().eq("is_visible", true);
			summary[1] = (double) qb.countOf();
		} catch (SQLException e) {
			e.printStackTrace();
			summary[1] = 0d;
		}
		try{
			QueryBuilder<Transaction, Integer> qb = SnapBillingUtils.getHelper(context).getTransactionDao()
					.queryBuilder();
			qb.where().like("transaction_timestamp",  new SimpleDateFormat(SnapBillingConstants.DAY_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime())+"%").and().eq("is_visible", true);
			QueryBuilder<BillItem, Integer> billItemQuerYBuilder = SnapBillingUtils.getHelper(context).getBillItemDao().queryBuilder().distinct();
			summary[2] = (double) billItemQuerYBuilder.join(qb).countOf();
		}catch (SQLException e) {
			e.printStackTrace();
			summary[2] = 0d;
		}
		try{
			QueryBuilder<Transaction, Integer> qb = SnapBillingUtils.getHelper(context).getTransactionDao()
					.queryBuilder();
			qb.where().like("transaction_timestamp",  new SimpleDateFormat(SnapBillingConstants.DAY_DATEFORMAT, Locale.getDefault()).format(Calendar.getInstance().getTime())+"%").and().eq("is_visible", true);
			QueryBuilder<BillItem, Integer> billItemQuerYBuilder = SnapBillingUtils.getHelper(context)
					.getBillItemDao().queryBuilder();
			summary[3] = (double) billItemQuerYBuilder.join(qb).where().lt("sku_quantity", 0).countOf();
		}catch (java.sql.SQLException e) {
			Log.e(TAG, "Exception " + e.getMessage(), e);
			summary[3] = 0d;
		}
		try{
			QueryBuilder<Transaction, Integer> transactionBuilder = SnapBillingUtils.getHelper(context).getTransactionDao().queryBuilder();
			SimpleDateFormat dateFormat = new SimpleDateFormat(SnapToolkitConstants.STANDARD_DATEFORMAT, Locale.getDefault());
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MINUTE, 00);
			calendar.set(Calendar.HOUR_OF_DAY, 00);
			calendar.set(Calendar.SECOND, 00);
			String startTime = dateFormat.format(calendar.getTime());
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.SECOND, 59);
			String endTime = dateFormat.format(calendar.getTime());
			transactionBuilder.selectRaw("SUM(pending_payment)").where().ge("transaction_timestamp", startTime).and().le("transaction_timestamp", endTime);
			GenericRawResults<String[]> results = SnapBillingUtils.getHelper(context).getTransactionDao().queryRaw(transactionBuilder.prepareStatementString());
			String[] values = results.getFirstResult();
			if (values != null && values[0] != null) {
				summary[4] = Double.parseDouble(values[0]);
			}else{
				summary[4] = 0d;
			}
		}catch (java.sql.SQLException e) {
			Log.e(TAG, "Exception " + e.getMessage(), e);
			summary[4] = 0d;
		}

		return summary;
	}

	@Override
	protected void onPostExecute(Double [] result) {
		String errorMessage = "Failed to compute summary data";
		if(result == null)
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		else
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
	}

}
