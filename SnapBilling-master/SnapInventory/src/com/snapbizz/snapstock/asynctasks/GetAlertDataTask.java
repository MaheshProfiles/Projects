package com.snapbizz.snapstock.asynctasks;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snapstock.utils.AlertFilterType;
import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.BarGraphDataPoint;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetAlertDataTask extends AsyncTask<Void, Void, List<BarGraphDataPoint>> {

	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "Could not calculate alert data";
	private SnapBizzDatabaseHelper databaseHelper;
	private AlertFilterType alertFilterType;

	public GetAlertDataTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode, AlertFilterType alertFilterType) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.alertFilterType = alertFilterType;
	}

	@Override
	protected List<BarGraphDataPoint> doInBackground(Void... params) {
		databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
		List<BarGraphDataPoint> chartData = null;
		GenericRawResults<String[]> rawResults = null;
		String quickaddString = "(";
		for(String quickaddCode : context.getResources().getStringArray(R.array.quickadd_array)) {
			quickaddString += "'"+quickaddCode+"',";
		}
		try {
			if(alertFilterType.ordinal() == AlertFilterType.LOW_STOCK.ordinal())
				rawResults = databaseHelper.getBillItemDao().queryRaw("select product_sku.sku_name, inventory_sku.inventory_sku_quantity * product_sku.sku_mrp as value from inventory_sku inner join product_sku on product_sku.sku_id = inventory_sku.inventory_sku_id where inventory_sku.inventory_sku_id NOT IN "+quickaddString.substring(0, quickaddString.length() - 1)+") and inventory_sku.inventory_sku_quantity < (select "+SnapToolkitConstants.MINIMUM_STOCK_DAYS+" * Sum(transaction_details.sku_quantity) / (select count(*) from transaction_details inner join transactions on transactions.transaction_id = transaction_details.transaction_id where transaction_details.sku_id = product_sku.sku_id group by SUBSTR(transactions.transaction_timestamp, 0, 11)) from transaction_details) order by value DESC limit 50");
			else if(alertFilterType.ordinal() == AlertFilterType.OUT_OF_STOCK.ordinal()) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_YEAR, -SnapToolkitConstants.ACTIVE_STOCK_DAYS);
				rawResults = databaseHelper.getBillItemDao().queryRaw("select product_sku.sku_name, sum(transaction_details.sku_quantity * transaction_details.sku_sale_price) as value from inventory_sku inner join transaction_details on transaction_details.sku_id = inventory_sku.inventory_sku_id inner join product_sku on product_sku.sku_id = transaction_details.sku_id inner join transactions on transactions.transaction_id = transaction_details.transaction_id where inventory_sku.inventory_sku_id NOT IN "+quickaddString.substring(0, quickaddString.length() - 1)+") and inventory_sku.inventory_sku_quantity = 0 and transactions.transaction_timestamp > '"+new SimpleDateFormat(SnapToolkitConstants.DAY_DATEFORMAT).format(calendar.getTime())+"' group by inventory_sku.inventory_sku_id order by value DESC limit 50");
			} else if(alertFilterType.ordinal() == AlertFilterType.SLOW_STOCK.ordinal()) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_YEAR, -SnapToolkitConstants.ACTIVE_STOCK_DAYS);
				Calendar cal = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_YEAR, -SnapToolkitConstants.SLOW_STOCK_DAYS);
				rawResults = databaseHelper.getBillItemDao().queryRaw("select product_sku.sku_name, inventory_sku.inventory_sku_quantity * product_sku.sku_mrp as value from inventory_sku inner join product_sku on inventory_sku.inventory_sku_id = product_sku.sku_id where product_sku.sku_id IN ( select product_sku.sku_id from product_sku inner join transaction_details on transaction_details.sku_id = product_sku.sku_id inner join transactions on transactions.transaction_id = transaction_details.transaction_id where product_sku.sku_id NOT IN "+quickaddString.substring(0, quickaddString.length() - 1)+") and transactions.transaction_timestamp > '"+new SimpleDateFormat(SnapToolkitConstants.DAY_DATEFORMAT).format(calendar.getTime())+"' and product_sku.sku_id NOT IN (select transaction_details.sku_id from transactions inner join transaction_details on transaction_details.transaction_id = transactions.transaction_id where transactions.transaction_timestamp > '"+new SimpleDateFormat(SnapToolkitConstants.DAY_DATEFORMAT).format(cal.getTime())+"')) order by value DESC limit 50");
			}
			if(rawResults != null) {
				for(String [] result: rawResults) {
					BarGraphDataPoint BarGraphDataPoint = new BarGraphDataPoint(result[0], Double.parseDouble(result[1]));
					if(chartData == null)
						chartData = new ArrayList<BarGraphDataPoint>();
					chartData.add(BarGraphDataPoint);
				}
			} else return null;
			return chartData;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(List<BarGraphDataPoint> result) {
		// TODO Auto-generated method stub
		if (result != null) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
