package com.snapbizz.snapstock.asynctasks;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetStockSummaryTask extends AsyncTask<Integer, Void, List<Float>>{
	
	private Context context;
	private int taskCode;
	private String errorMessage = "Unable to get stock data";
	private OnQueryCompleteListener onQueryCompleteListener;
	private List<Float> stockSummaryNumbers;
	
	public GetStockSummaryTask(Context context, int taskCode, OnQueryCompleteListener onQueryCompleteListener){
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode; 
	}

	@Override
	protected List<Float> doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		
		try {
			
			stockSummaryNumbers = new ArrayList<Float>();
			GenericRawResults<String[]> rawResults = SnapInventoryUtils
					.getDatabaseHelper(context)
					.getProductSkuDao()
					.queryRaw("SELECT sku_available_qty, product_sku.sku_mrp FROM inventory_batch " +
							"INNER JOIN product_sku ON inventory_batch.sku_id = product_sku.sku_id INNER JOIN orders ON " +
							"inventory_batch.order_no = orders.order_no INNER JOIN distributor ON " +
							"orders.distributor_id = distributor.distributor_id AND distributor.distributor_id = '" + params[0] + "'");
			 
			float totalCount = 0;
			float distributorTotalCount = 0;
			float distributorPercentValue = 0;
			float distributorPercentCount = 0;
			float distributorTotalValue = 0;
			float totalValue = 0;
			float storeDaysOfStock = 0;
			float distributorAverageDailySales = 0;
			float storeAverageDailySales = 0;
			float distributorDaysOfStock = 0;
			for (String[] strings : rawResults) {
					distributorTotalCount = distributorTotalCount + Float.parseFloat(strings[0]);
					distributorTotalValue = distributorTotalValue + ( Float.parseFloat(strings[1]) * Float.parseFloat(strings[0]));
			}
			
			GenericRawResults<String[]> rawResultsStore = SnapInventoryUtils
					.getDatabaseHelper(context)
					.getProductSkuDao()
					.queryRaw("SELECT inventory_sku.inventory_sku_quantity, product_sku.sku_mrp FROM inventory_sku " +
							"INNER JOIN product_sku ON product_sku.sku_id = inventory_sku.inventory_sku_id");
			
			for (String[] strings : rawResultsStore) {
				totalCount = totalCount + Float.parseFloat(strings[0]);
				totalValue = totalValue + (Float.parseFloat(strings[1]) * Float.parseFloat(strings[0]));
			}
			
			stockSummaryNumbers.add(distributorTotalValue);
			stockSummaryNumbers.add(totalValue);
			stockSummaryNumbers.add(distributorTotalCount);
			stockSummaryNumbers.add(totalCount);
			
			if(distributorTotalCount != 0){
				distributorPercentValue = SnapInventoryUtils.roundOffDecimalPoints((distributorTotalValue/totalValue)*100);
				distributorPercentCount = SnapInventoryUtils.roundOffDecimalPoints(((float) distributorTotalCount/totalCount)*100);
			}else{
				distributorPercentCount = 0;
				distributorPercentValue = 0;
			}
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -SnapToolkitConstants.AVG_MONTH_SPAN);
			
			GenericRawResults<String[]> rawResultSalesDistributor = SnapInventoryUtils.getDatabaseHelper(context).getProductSkuDao()
					.queryRaw("SELECT SUM (product_sku.sku_mrp * transaction_details.sku_quantity)/(select count(*) from (select * from transactions where transaction_timestamp > '"+new SimpleDateFormat(SnapToolkitConstants.STANDARD_DATEFORMAT).format(cal.getTime())+"' group by SUBSTR(transaction_timestamp, 0 ,11))) FROM product_sku INNER JOIN transaction_details ON product_sku.sku_id = transaction_details.sku_id INNER JOIN distributor_brand_map ON product_sku.brand_id = distributor_brand_map.brand_id WHERE distributor_brand_map.distributor_id = '" + params[0] + "'");			
			
			for (String[] strings : rawResultSalesDistributor) {
				if(null != strings[0]){
					distributorAverageDailySales += Float.parseFloat(strings[0]);
				}		
			}
			stockSummaryNumbers.add(distributorAverageDailySales);
			
			GenericRawResults<String[]> rawResultSales = SnapInventoryUtils.getDatabaseHelper(context).getProductSkuDao()
					.queryRaw("SELECT SUM (product_sku.sku_mrp * transaction_details.sku_quantity)/(select count(*) from (select * from transactions where transaction_timestamp > '"+new SimpleDateFormat(SnapToolkitConstants.STANDARD_DATEFORMAT).format(cal.getTime())+"' group by SUBSTR(transaction_timestamp, 0 ,11))) FROM product_sku INNER JOIN transaction_details ON product_sku.sku_id = transaction_details.sku_id");			
			
			for (String[] strings : rawResultSales) {
				if(null != strings[0]){
					storeAverageDailySales += Float.parseFloat(strings[0]);
				}
			}
			stockSummaryNumbers.add(storeAverageDailySales);

			if( distributorAverageDailySales != 0 ){
				distributorDaysOfStock = distributorTotalValue / distributorAverageDailySales;
				distributorDaysOfStock = SnapInventoryUtils.roundOffDecimalPoints(distributorDaysOfStock);
			}				
			else
				distributorDaysOfStock = 0;
			
			if( storeAverageDailySales != 0 ){
				storeDaysOfStock = totalValue / storeAverageDailySales;
				storeDaysOfStock = SnapInventoryUtils.roundOffDecimalPoints(storeDaysOfStock);
			}				
			else
				storeDaysOfStock = 0;
			
			stockSummaryNumbers.add(distributorDaysOfStock);
			stockSummaryNumbers.add(storeDaysOfStock);
			stockSummaryNumbers.add(distributorPercentValue);
			stockSummaryNumbers.add(distributorPercentCount);
			
		return stockSummaryNumbers;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(List<Float> result) {
		// TODO Auto-generated method stub
		if( null != result && result.size() > 0 ){
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		}else{
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
}
