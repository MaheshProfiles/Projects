package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snaptoolkit.domains.VAT;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class GetDetailedSalesReportTask extends AsyncTask<Void, Void, List<String[]>>{

	private OnQueryCompleteListener mOnQueryCompleteListener;
	private int mTaskCode;
	private String mErrorMessage = "Unable to get detailed VAT sales report";
	private Context mContext;
	private List<VAT> mVatList;
	private String mStartDate;
	private String mEndDate;
	
	public GetDetailedSalesReportTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode, String startDate, String endDate, List<VAT> vatList) {
		this.mContext = context;
		this.mOnQueryCompleteListener = onQueryCompleteListener;
		this.mTaskCode = taskCode;
		this.mStartDate = startDate;
		this.mEndDate = endDate;
		this.mVatList = vatList;
	}
	
	@Override
	protected List<String[]> doInBackground(Void... params) {
		try {
			String inVatListString = "";
        	for (VAT vat : mVatList) {
				inVatListString += vat.getVat() + ", ";
			}
        	inVatListString = inVatListString.substring(0, inVatListString.length() - 2);
			GenericRawResults<String[]> rawResults = SnapCommonUtils.getDatabaseHelper(mContext).getTransactionDao().queryRaw("SELECT substr(invoice.transaction_timestamp, 0, 11) as rangeDate, invoice.invoice_id, sum((transaction_details.sku_sale_price) * transaction_details.sku_quantity), transaction_details.vat_rate, sum(((transaction_details.sku_sale_price * transaction_details.vat_rate) / (transaction_details.vat_rate + 100)) * (transaction_details.sku_quantity)) FROM invoice INNER JOIN transaction_details ON invoice.transaction_id = transaction_details.transaction_id WHERE (invoice.transaction_timestamp >= '" + mStartDate + "' AND invoice.transaction_timestamp <= '" + mEndDate + "') AND transaction_details.vat_rate IN ( " + inVatListString + " ) GROUP BY rangeDate, invoice.invoice_id, transaction_details.vat_rate");
			return rawResults.getResults();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(List<String[]> result) {
		if (null != result && result.size() >0) {
			mOnQueryCompleteListener.onTaskSuccess(result, mTaskCode);
		} else {
			mOnQueryCompleteListener.onTaskError(mErrorMessage, mTaskCode);
		}
	}
 }
