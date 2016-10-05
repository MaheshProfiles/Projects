package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snaptoolkit.domains.VAT;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class GetStateVatRatesTask extends AsyncTask<String, Void, List<VAT>>{

	private int mTaskCode;
	private Context mContext;
	private OnQueryCompleteListener mOnQueryCompleteListener;
	private String mErrorMessage = "No VAT rates found";
	private ProgressDialog mProgressDialog;
	
	public GetStateVatRatesTask (Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.mTaskCode = taskCode;
		this.mOnQueryCompleteListener = onQueryCompleteListener;
		this.mContext = context;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setTitle("Fetching state VAT rates");
		mProgressDialog.setMessage("Please Wait...Do not close application..");
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}
	
	@Override
	protected List<VAT> doInBackground(String... params) {
		// TODO Auto-generated method stub
		QueryBuilder<VAT, Integer> vatQueryBuilder;
		try {
			vatQueryBuilder = SnapCommonUtils.getDatabaseHelper(mContext).getVatDao().queryBuilder();
			vatQueryBuilder.distinct().selectColumns("vat_value").where().eq("state_id", params[0]);
			vatQueryBuilder.orderBy("vat_value", true);
			return vatQueryBuilder.query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(List<VAT> result) {
		// TODO Auto-generated method stub
		if (null != mProgressDialog) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		if (null != result && result.size() > 0) {
			mOnQueryCompleteListener.onTaskSuccess(result, mTaskCode);
		} else {
			mOnQueryCompleteListener.onTaskError(mErrorMessage, mTaskCode);
		}
	}
}
