package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snapbilling.utils.ChartFilterType;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.BarGraphDataPoint;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.ChartResolutionType;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class GetChartDataTask extends AsyncTask<Void, Void, List<BarGraphDataPoint>> {
	
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "Could not calculate chart data";
	private ChartResolutionType chartResolutionType;
	private ChartFilterType chartFilterType;
	
	public GetChartDataTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode, ChartResolutionType chartResolutionType, ChartFilterType chartFilterType) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.chartResolutionType = chartResolutionType;
		this.chartFilterType = chartFilterType;
	}

	@Override
	protected List<BarGraphDataPoint> doInBackground(Void... params) {
		try {
			String date = null;
			String limit = null;
			if(chartResolutionType.ordinal() == ChartResolutionType.DAY.ordinal()) {
				date = "SUBSTR(transactions.transaction_timestamp, 0, 11)";
				limit = "limit 15";
			} else if(chartResolutionType.ordinal() == ChartResolutionType.MONTH.ordinal()) {
				date = "SUBSTR(transactions.transaction_timestamp, 0, 8)";
				limit = "limit 3";
			} else if(chartResolutionType.ordinal() == ChartResolutionType.WEEK.ordinal()) {
				date = "SUBSTR(transactions.transaction_timestamp, 0, 5) || '/' || SUBSTR(transactions.transaction_timestamp, 12, 2)";
				limit = "limit 15";
			}
			GenericRawResults<String[]> rawResultRevenue = null;
			if(chartFilterType.ordinal() ==  ChartFilterType.REVENUE.ordinal())
				rawResultRevenue = SnapBillingUtils.getHelper(context).getTransactionDao().queryRaw("select SUM(transactions.total_amount - transactions.total_savings - transactions.total_discount), "+date+" AS transaction_date from transactions where transactions.is_visible = '1' group by transaction_date order by transaction_date DESC "+limit);
			else if(chartFilterType.ordinal() == ChartFilterType.BASKET_VALUE.ordinal())
				rawResultRevenue = SnapBillingUtils.getHelper(context).getTransactionDao().queryRaw("select SUM(transactions.total_amount - transactions.total_savings - transactions.total_discount) / Count(*), "+date+" AS transaction_date from transactions where transactions.is_visible = '1' group by transaction_date order by transaction_date DESC "+limit);			
			else if(chartFilterType.ordinal() == ChartFilterType.CUSTOMER_FOOTFALL.ordinal()) {
				rawResultRevenue = SnapBillingUtils.getHelper(context).getTransactionDao().queryRaw("select Count(*), "+date+" AS transaction_date from transactions where transactions.is_visible = '1' group by transaction_date order by transaction_date DESC "+limit);
			} else if(chartFilterType.ordinal() == ChartFilterType.RETURNED_PRODUCTS.ordinal()) {
				rawResultRevenue = SnapBillingUtils.getHelper(context).getTransactionDao().queryRaw("select SUM(transaction_details.sku_mrp * -transaction_details.sku_quantity), "+date+" AS transaction_date from transactions inner join transaction_details on transactions.transaction_id = transaction_details.transaction_id where transactions.is_visible = '1' and transaction_details.sku_quantity < 0 group by transaction_date order by transaction_date DESC "+limit);
			}
			//GenericRawResults<String[]> rawResultsProfit = SnapBillingUtils.getHelper(context).getTransactionDao().queryRaw("select SUM(inventory_sku.purchase_price * transaction_details.sku_quantity), "+date+" AS transaction_date from transactions inner join transaction_details on transactions.transaction_id = transaction_details.transaction_id inner join inventory_sku on transaction_details.sku_id = inventory_sku.inventory_sku_id group by transaction_date order by transaction_date ASC");
			List<BarGraphDataPoint> chartData = null;
			//List<String []> purchaseResultList = rawResultsProfit.getResults();
			for(String [] result: rawResultRevenue) {
				double revenue = Double.parseDouble(result[0]);
				String label = null;
				if(chartResolutionType.ordinal() == ChartResolutionType.DAY.ordinal())
					label = result[1].substring(8, 10) +" "+SnapCommonUtils.getMonthName(Integer.parseInt(result[1].substring(5, 7)));
				else if(chartResolutionType.ordinal() == ChartResolutionType.MONTH.ordinal()) {
					label = SnapCommonUtils.getMonthName(Integer.parseInt(result[1].substring(5, 7)));
				} else if(chartResolutionType.ordinal() == ChartResolutionType.WEEK.ordinal()) {
					label = "Week "+result[1].substring(5, 7);
				}
				//BarGraphDataPoint BarGraphDataPoint = new BarGraphDataPoint(label, revenue, revenue - Double.parseDouble(purchaseResultList.get(i)[0]));
				BarGraphDataPoint BarGraphDataPoint = new BarGraphDataPoint(label, revenue);
				if(chartData == null)
					chartData = new ArrayList<BarGraphDataPoint>();
				chartData.add(BarGraphDataPoint);
			}
			return chartData;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<BarGraphDataPoint> result) {
		if (result != null) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
