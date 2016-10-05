package com.snapbizz.snapbilling.asynctasks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetPurchaseVatTask extends AsyncTask<String, Void, HashMap<Float, String[]>> {
    private Context mContext;
    private int mTaskCode;
    private OnQueryCompleteListener mOnQueryCompleteListener;
    private String mStartDate;
    private String mEndDate;

    public GetPurchaseVatTask(Context context,OnQueryCompleteListener onQueryCompleteListener, int taskCode, String startDate, String endDate) {
        this.mContext = context;
        this.mOnQueryCompleteListener = onQueryCompleteListener;
        this.mTaskCode = taskCode;
        this.mStartDate = startDate;
        this.mEndDate = endDate;
    }

    @Override
    protected HashMap<Float, String[]> doInBackground(String... params) {
        try {
        	SimpleDateFormat dateFormat = new SimpleDateFormat(SnapToolkitConstants.STANDARD_DATEFORMAT, Locale.getDefault());
        	Date startDate = dateFormat.parse(mStartDate);
        	Date endDate = dateFormat.parse(mEndDate);
        	dateFormat = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault());
        	mStartDate = dateFormat.format(startDate);
        	mEndDate = dateFormat.format(endDate);
        	HashMap<Float, String[]> result = new HashMap<Float, String[]>();
        	GenericRawResults<String[]> rawResults = SnapCommonUtils
					.getDatabaseHelper(mContext)
					.getTransactionDao()
					.queryRaw("SELECT sum(order_details.product_purchase_price * order_details.to_order_qty), "
							+ "sum((order_details.product_purchase_price * product_sku.vat / (100 + product_sku.vat)) * order_details.to_order_qty) , product_sku.vat "
							+ "FROM orders INNER JOIN order_details ON orders.order_no = order_details.order_no INNER JOIN product_sku ON order_details.product_sku_id = product_sku.sku_id " +
							"WHERE orders.lastmodified_timestamp >= '" + mStartDate + "' AND orders.lastmodified_timestamp <= '" + mEndDate + "'"
							+ "GROUP BY product_sku.vat "
							+ "ORDER BY product_sku.vat");
        	String[] resultString;
        	for (String[] strings : rawResults) {
				resultString = new String[2];
				resultString[0] = strings[0];
				resultString[1] = strings[1];
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
		mOnQueryCompleteListener.onTaskSuccess(result, mTaskCode);
	}

}
