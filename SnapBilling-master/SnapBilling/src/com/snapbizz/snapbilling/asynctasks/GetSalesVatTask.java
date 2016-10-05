package com.snapbizz.snapbilling.asynctasks;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snaptoolkit.domains.VAT;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class GetSalesVatTask extends AsyncTask<String, Void, HashMap<Float, String[]>> {
    private Context mContext;
    private int mTaskCode;
    private List<VAT> mVatList;
    private OnQueryCompleteListener mOnQueryCompleteListener;
    private String mErrorMessage = "No VAT Rates Found";
    private String mStartDate;
    private String mEndDate;

    public GetSalesVatTask(Context context,OnQueryCompleteListener onQueryCompleteListener, int taskCode, String startDate, String endDate, List<VAT> vatList) {
        this.mContext = context;
        this.mOnQueryCompleteListener = onQueryCompleteListener;
        this.mTaskCode = taskCode;
        this.mStartDate = startDate;
        this.mEndDate = endDate;
        this.mVatList = vatList;
    }

    @Override
    protected HashMap<Float, String[]> doInBackground(String... params) {
        try {
        	String inVatListString = "";
        	for (VAT vat : mVatList) {
				inVatListString += vat.getVat() + ", ";
			}
        	inVatListString = inVatListString.substring(0, inVatListString.length() - 2);
        	HashMap<Float, String[]> result = new HashMap<Float, String[]>();
        	GenericRawResults<String[]> rawResults = SnapCommonUtils
					.getDatabaseHelper(mContext)
					.getTransactionDao()
					.queryRaw("SELECT sum((transaction_details.sku_sale_price) * transaction_details.sku_quantity), "
								+ "sum((transaction_details.sku_sale_price * transaction_details.vat_rate/(100 + transaction_details.vat_rate)) * transaction_details.sku_quantity) , "
								+ "transaction_details.vat_rate "
							+ "FROM invoice "
							+ "INNER JOIN transaction_details ON invoice.transaction_id = transaction_details.transaction_id "
							+ "INNER JOIN product_sku ON product_sku.sku_id = transaction_details.sku_id "
							+ "WHERE (invoice.transaction_timestamp >= '" + mStartDate + "' AND invoice.transaction_timestamp <= '" + mEndDate + "') AND "
							+ "transaction_details.vat_rate IN ( " + inVatListString + " ) "
							+ "GROUP BY transaction_details.vat_rate "
							+ "ORDER BY transaction_details.vat_rate");
        	String[] resultString; 
        	for (String[] strings : rawResults) {
        		resultString = new String[2];
				resultString[1] = strings[1];
				resultString[0] = String.valueOf(Float.parseFloat(strings[0]) - Float.parseFloat(strings[1]));
				result.put(Float.parseFloat(strings[2]), resultString);
			}
        	return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(HashMap<Float, String[]> result) {
        if (result != null && result.size() > 0) {
        	mOnQueryCompleteListener.onTaskSuccess(result, mTaskCode);
        } else {
        	mOnQueryCompleteListener.onTaskError(mErrorMessage, mTaskCode);
        }
        mOnQueryCompleteListener = null;
        mContext = null;
    }

}
