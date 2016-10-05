package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class GetCompositeVatTask extends AsyncTask<Void, Void, Double[]>{

	private Context mContext;
	private OnQueryCompleteListener mOnQueryCompleteListener;
	private int mTaskCode;
	private String mErrorMessage = "Something went wrong while calculating Composite VAT";
	private String mStartDate;
	private String mEndDate;
	private double mCompositeVatEditTextValue;
	private EditText mCompositeVatEditText;
	private double mCompositeRate;
	
	public GetCompositeVatTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode, String startDate, String endDate,double mCompositeRate) {
		this.mOnQueryCompleteListener = onQueryCompleteListener;
		this.mStartDate = startDate;
		this.mEndDate = endDate;
		this.mTaskCode = taskCode;
		this.mContext = context;
		this.mCompositeRate=mCompositeRate;
	}
	

	@Override
	protected Double[] doInBackground(Void... params) {
		
		Double[] doubleValues = new Double[2];
		Double compositeVatValue = null;
		try {
			GenericRawResults<String[]> rawResults = SnapBillingUtils.getHelper(mContext).getTransactionDao().queryRaw("SELECT SUM(total_amount) - SUM(total_discount) - SUM(total_savings) FROM invoice WHERE (invoice.transaction_timestamp >= '" + mStartDate + "' AND invoice.transaction_timestamp <= '" + mEndDate + "')");
			for (String[] strings : rawResults) {
				if (null != strings[0]) {
					compositeVatValue = Double.parseDouble(strings[0]);
					doubleValues[0] = compositeVatValue;
					compositeVatValue = (compositeVatValue *mCompositeRate)/ 100;
					doubleValues[1] = compositeVatValue;
				}
			}
			return doubleValues;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(Double[] result) {
		if (null != result && null != result[0] && null != result[1]) {
			mOnQueryCompleteListener.onTaskSuccess(result, mTaskCode);
		} else {
			mOnQueryCompleteListener.onTaskError(mErrorMessage, mTaskCode);
		}
	}
}
