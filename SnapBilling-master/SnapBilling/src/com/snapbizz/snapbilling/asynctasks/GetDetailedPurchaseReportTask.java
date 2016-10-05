package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetDetailedPurchaseReportTask extends AsyncTask<Void, Void, List<String[]>> {
	
	private OnQueryCompleteListener mOnQueryCompleteListener;
	private int mTaskCode;
	private Context mContext;
	private String mStartDate;
	private String mEndDate;
	
	public GetDetailedPurchaseReportTask(Context context, OnQueryCompleteListener onQueryCompleteListener, int taskCode, String startDate, String endDate) {
		this.mContext = context;
		this.mOnQueryCompleteListener = onQueryCompleteListener;
		this.mTaskCode = taskCode;
		this.mStartDate = startDate;
		this.mEndDate = endDate;
	}

	@Override
	protected List<String[]> doInBackground(Void... arg0) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(SnapToolkitConstants.STANDARD_DATEFORMAT, Locale.getDefault());
        	Date startDate = dateFormat.parse(mStartDate);
        	Date endDate = dateFormat.parse(mEndDate);
        	dateFormat = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault());
        	mStartDate = dateFormat.format(startDate);
        	mEndDate = dateFormat.format(endDate);
			GenericRawResults<String[]> rawResults = SnapCommonUtils.getDatabaseHelper(mContext)
					.getTransactionDao().queryRaw("SELECT orders.order_date,"
							+ " orders.invoice_number, sum((order_details.product_purchase_price) * order_details.to_order_qty), product_sku.vat, "
							+ "sum(((order_details.product_purchase_price * product_sku.vat) /"
							+ " (product_sku.vat + 100)) * (order_details.to_order_qty)) "
							+ "FROM orders INNER JOIN order_details ON orders.order_no = "
							+ "order_details.order_no INNER JOIN product_sku "
							+ "ON order_details.product_sku_id = product_sku.sku_id "
							+ "WHERE (orders.lastmodified_timestamp >= '" + mStartDate
							+ "' AND orders.lastmodified_timestamp <= '" + mEndDate + "') "
							+ " GROUP BY orders.order_date, orders.invoice_number, product_sku.vat");
			return rawResults.getResults();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(List<String[]> result) {
		if (null != result && result.size() > 0)
			mOnQueryCompleteListener.onTaskSuccess(result, mTaskCode);
		else
			mOnQueryCompleteListener.onTaskError(mContext.getString(R.string.error_unable_creating_purchase_detail_xl), mTaskCode);
	}
}
