package com.snapbizz.snapstock.asynctasks;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseIntArray;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snaptoolkit.domains.StockReport;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.StockReportFilterType;

public class GetStockReportTask extends AsyncTask<Void, Void, List<StockReport>> {

	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "Could not generate stock report";
	private SnapBizzDatabaseHelper databaseHelper;
	private StockReportFilterType stockReportFilterType;

	public GetStockReportTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode, StockReportFilterType alertFilterType) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.stockReportFilterType = alertFilterType;
	}

	@Override
	protected List<StockReport> doInBackground(Void... params) {
		databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
		List<StockReport> stockReportList = null;
		GenericRawResults<String[]> rawResults = null;
		SparseIntArray stockReportMap = new SparseIntArray();
		try {
			if(stockReportFilterType.ordinal() == StockReportFilterType.DISTRIBUTOR.ordinal()) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MONTH, -SnapToolkitConstants.AVG_MONTH_SPAN);
				GenericRawResults<String []> noOfDaysResult = databaseHelper.getBillItemDao().queryRaw("select count(*) from transactions where transactions.transaction_timestamp > '"+new SimpleDateFormat(SnapToolkitConstants.DAY_DATEFORMAT).format(calendar.getTime())+"' group by SUBSTR(transactions.transaction_timestamp, 0, 11)");
				int noOfDays = 1;
				if(noOfDaysResult != null) {
					String [] result = noOfDaysResult.getFirstResult();
					if(result != null)
						noOfDays = Integer.parseInt(result[0]);
				}
				rawResults = databaseHelper.getBillItemDao().queryRaw("select distributor.agency_name, Sum(inventory_batch.sku_mrp * inventory_batch.sku_available_qty), Sum(inventory_batch.sku_available_qty), distributor.distributor_id from inventory_batch inner join orders on orders.order_no = inventory_batch.order_no inner join distributor on orders.distributor_id = distributor.distributor_id group by distributor.distributor_id");
				if(rawResults == null)
					return null;
				if(stockReportList == null)
					stockReportList = new ArrayList<StockReport>();
				for(String [] result: rawResults) {
					StockReport stockReport = new StockReport();
					stockReport.setFilterName(result[0]);
					stockReport.setAllStockValue(result[1]);
					stockReport.setAllStockSku(result[2]);
					stockReportList.add(stockReport);
					stockReportMap.put(Integer.parseInt(result[3]), stockReportList.size() - 1);
				}
				rawResults = databaseHelper.getBillItemDao().queryRaw("select SUM(transaction_details.sku_quantity) / "+noOfDays+", Sum(transaction_details.sku_sale_price), distributor.distributor_id from transaction_details inner join transactions on transactions.transaction_id = transaction_details.transaction_id inner join product_sku on product_sku.sku_id = transaction_details.sku_id inner join distributor_brand_map on product_sku.brand_id = distributor_brand_map.brand_id inner join distributor on distributor.distributor_id = distributor_brand_map.distributor_id where transactions.transaction_timestamp > '"+new SimpleDateFormat(SnapToolkitConstants.DAY_DATEFORMAT).format(calendar.getTime())+"' group by distributor.distributor_id");
				if(rawResults != null) {
					for(String [] result: rawResults) {
						StockReport stockReport = stockReportList.get(stockReportMap.get(Integer.parseInt(result[2])));
						stockReport.setAllDaysofStock(""+(Double.parseDouble(stockReport.getAllStockSku()) / Double.parseDouble(result[0])));
						stockReport.setAllStockRevenue(result[1]);
					}
				}
				rawResults = databaseHelper.getBillItemDao().queryRaw("select Sum(inventory_batch.sku_mrp * inventory_batch.sku_available_qty), Sum(inventory_batch.sku_available_qty) from inventory_batch ");
			} else if(stockReportFilterType.ordinal() == StockReportFilterType.CATEGORY.ordinal()) {
//				Calendar calendar = Calendar.getInstance();
//				calendar.add(Calendar.DAY_OF_YEAR, -SnapToolkitConstants.ACTIVE_STOCK_DAYS);
//				rawResults = databaseHelper.getBillItemDao().queryRaw("select product_sku.sku_name, inventory_sku.inventory_sku_quantity * transaction_details.sku_mrp as value from inventory_sku inner join transaction_details on transaction_details.sku_id = inventory_sku.inventory_sku_id inner join product_sku on product_sku.sku_id = transaction_details.sku_id inner join transactions on transactions.transaction_id = transaction_details.transaction_id where inventory_sku.inventory_sku_quantity > 0 and transactions.transaction_timestamp > '"+new SimpleDateFormat(SnapToolkitConstants.DAY_DATEFORMAT).format(calendar.getTime())+"' order by value limit 50");
			}

			
			return stockReportList;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(List<StockReport> result) {
		// TODO Auto-generated method stub
		if (result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
