package com.snapbizz.snapstock.asynctasks;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetSummaryTask extends AsyncTask<Void, Void, Double []> {
	private static final String TAG = GetSummaryTask.class.getSimpleName();

	private static SnapBizzDatabaseHelper databaseHelper;
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
		Double [] summary = new Double[8];
		databaseHelper = SnapInventoryUtils.getDatabaseHelper(context);
		try {
			//Total Stock Value
			GenericRawResults<String[]> results = databaseHelper.getProductSkuDao().queryRaw("select SUM(inventory_batch.sku_mrp * inventory_batch.sku_available_qty) from inventory_batch");
			String[] values = results.getFirstResult();
			if (values != null && values[0] != null) {
				summary[0] = Double.parseDouble(values[0]);
			} else {
				summary[0] = 0d;
			}
		} catch(SQLException e) {
			e.printStackTrace();
			summary[0] = 0d;
		}

		try {
			// avg monthly revenue
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -SnapToolkitConstants.AVG_MONTH_SPAN);
			GenericRawResults<String[]> results = databaseHelper.getTransactionDao().queryRaw("select (Sum(transactions.total_amount) - Sum(transactions.total_savings) - Sum(transactions.total_discount)) / (select count(*) from (select * from transactions where transactions.transaction_timestamp > '"+new SimpleDateFormat(SnapToolkitConstants.DAY_DATEFORMAT).format(calendar.getTime()).substring(0,  7)+"' group by SUBSTR(transactions.transaction_timestamp, 0, 8))) from transactions where transactions.transaction_timestamp > '"+new SimpleDateFormat(SnapToolkitConstants.DAY_DATEFORMAT).format(calendar.getTime()).substring(0,  7)+"'");
			String[] values = results.getFirstResult();
			if (values != null && values[0] != null) {
				summary[1] = Double.parseDouble(values[0]);
			} else {
				summary[1] = 0d;
			}
		} catch(SQLException e) {
			e.printStackTrace();
			summary[1] = 0d;
		}

		try {
			//avg monthly profit
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -SnapToolkitConstants.AVG_MONTH_SPAN);
			GenericRawResults<String[]> results = databaseHelper.getTransactionDao().queryRaw("select Sum(transaction_details.sku_quantity * (select avg(inventory_batch.sku_purchase_price) from inventory_batch where inventory_batch.sku_id='transaction_details.sku_id' group by inventory_batch.sku_mrp)) / (select count(*) from (select * from transactions where transactions.transaction_timestamp > '"+new SimpleDateFormat(SnapToolkitConstants.DAY_DATEFORMAT).format(calendar.getTime()).substring(0,  7)+"' group by SUBSTR(transactions.transaction_timestamp, 0, 8))) from transaction_details inner join transactions on transactions.transaction_id = transaction_details.transaction_id where transactions.transaction_timestamp > '"+new SimpleDateFormat(SnapToolkitConstants.STANDARD_DATEFORMAT).format(calendar.getTime()).substring(0,  7)+"'");
			String[] values = results.getFirstResult();
			if (values != null && values[0] != null) {
				summary[2] = summary[1];
			} else {
				summary[2] = 0d;
			}
		} catch(SQLException e) {
			e.printStackTrace();
			summary[2] = 0d;
		}

		try {
			// total sku
			GenericRawResults<String[]> results = databaseHelper.getTransactionDao().queryRaw("select Count(*) from inventory_sku inner join product_sku on product_sku.sku_id=inventory_sku.inventory_sku_id");
			String[] values = results.getFirstResult();
			if (values != null && values[0] != null) {
				summary[3] = Double.parseDouble(values[0]);
			} else {
				summary[3] = 0d;
			}
		} catch(SQLException e) {
			e.printStackTrace();
			summary[3] = 0d;
		}

		try {
			// new sku
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, -SnapToolkitConstants.NEW_STOCK_DAYS);
			GenericRawResults<String[]> results = databaseHelper.getTransactionDao().queryRaw("select Count(*) from inventory_sku where inventory_sku.creation_timestamp > '"+new SimpleDateFormat(SnapToolkitConstants.DAY_DATEFORMAT).format(calendar.getTime())+"'");
			String[] values = results.getFirstResult();
			if (values != null && values[0] != null) {
				summary[4] = Double.parseDouble(values[0]);
			} else {
				summary[4] = 0d;
			}
		} catch(SQLException e) {
			e.printStackTrace();
			summary[4] = 0d;
		}

		try {
			// unpaid
			GenericRawResults<String[]> results = databaseHelper.getTransactionDao().queryRaw("select Sum(orders.payment_to_make) from orders");
			String[] values = results.getFirstResult();
			GenericRawResults<String[]> result1 = databaseHelper.getTransactionDao().queryRaw("select sum(amount) from payment");
            String[] value1 = result1.getFirstResult();
			if (values != null && values[0] != null) {
				summary[6] = Double.parseDouble(values[0]);
			} else {
				summary[6] = 0d;
			}
			if(value1 != null && value1[0]!= null) {
			    summary[6] -= Double.parseDouble(value1[0]);
            }
		} catch(SQLException e) {
			e.printStackTrace();
			summary[6] = 0d;
		}
		// paid
		summary[5] = summary[0] - summary[6];
		if(summary[5] < 0)
			summary[5] = 0d;

		try {
			//days of stock
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -SnapToolkitConstants.AVG_MONTH_SPAN);
			GenericRawResults<String[]> daysResult = databaseHelper.getBillItemDao().queryRaw("select count(*) from (select * from transactions where transaction_timestamp > '"+new SimpleDateFormat(SnapToolkitConstants.STANDARD_DATEFORMAT).format(calendar.getTime())+"' group by SUBSTR(transaction_timestamp, 0 ,11))");
            long noOfDays = 1;
			if(daysResult != null)
                noOfDays = Long.parseLong(daysResult.getFirstResult()[0]);
			GenericRawResults<String[]> results = databaseHelper.getTransactionDao().queryRaw("select Sum(transactions.total_amount) - Sum(transactions.total_savings) - Sum(transactions.total_discount) from transactions where transactions.transaction_timestamp > '"+new SimpleDateFormat(SnapToolkitConstants.DAY_DATEFORMAT).format(calendar.getTime()).substring(0,  7)+"'");
			String[] values = results.getFirstResult();
			if (values != null && values[0] != null) {
				summary[7] = summary[0] / Double.parseDouble(values[0]) * noOfDays;
			} else {
				summary[7] = 0d;
			}
		} catch(SQLException e) {
		    summary[7] = 0d;
		}
		return summary;
	}

	@Override
	protected void onPostExecute(Double [] result) {
		String errorMessage = "Failed to compute Summary data";
		if(result == null)
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		else
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
	}
}
