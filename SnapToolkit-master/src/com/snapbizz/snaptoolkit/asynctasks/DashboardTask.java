package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sqlcipher.database.SQLiteDatabaseCorruptException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snaptoolkit.domains.BarGraphDataPoint;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.ChartType;
import com.snapbizz.snaptoolkit.utils.SnapBizzSyncDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class DashboardTask  extends AsyncTask<Void, Void, Boolean> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to calculate dashboards";
	
	public DashboardTask(Context context,
	OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}
	
	public List<BarGraphDataPoint> storeBarGraphDataPoint(GenericRawResults<String[]> rawResults) {
        List<BarGraphDataPoint> chartData = new ArrayList<BarGraphDataPoint>();
        for(String [] result : rawResults) {
            BarGraphDataPoint BarGraphDataPoint = new BarGraphDataPoint(result[0], Double.parseDouble(result[1]));
            chartData.add(BarGraphDataPoint);
        }
        return chartData;
    }

    public List<BarGraphDataPoint> storeBarGraphDataPoint(List<String[]> salesResults, List<String[]> stockResults, long noOfDays) {
        List<BarGraphDataPoint> chartData = new ArrayList<BarGraphDataPoint>();
        for(String [] result: salesResults) {
            if(stockResults.size() == 0)
                break;
            Double daysofStock = 0d;
            Double totalSales = Double.parseDouble(result[2]);
            if(totalSales != 0d) {
                for(int j = 0; j  < stockResults.size(); j++) {
                    if(result[0].equals(stockResults.get(j)[0])) {
                        daysofStock = Double.parseDouble(stockResults.get(j)[1]) * noOfDays / totalSales ;
                        break;
                    }
                }
            }

            BarGraphDataPoint BarGraphDataPoint = new BarGraphDataPoint(result[1], daysofStock);
            if(chartData == null)
                chartData = new ArrayList<BarGraphDataPoint>();
            chartData.add(BarGraphDataPoint);
        }
        if(chartData != null)
            Collections.sort(chartData, barGraphDataPointComparator);
        return chartData;
    }
    
    Comparator<BarGraphDataPoint> barGraphDataPointComparator = new Comparator<BarGraphDataPoint>() {

        @Override
        public int compare(BarGraphDataPoint lhs, BarGraphDataPoint rhs) {
            // TODO Auto-generated method stub
            return (int) (rhs.getDataPoint1() - lhs.getDataPoint1());
        }
    };
	
	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
        SnapBizzSyncDatabaseHelper databaseHelper = SnapCommonUtils.getSyncDatabaseHelper(SnapCommonUtils.getSnapContext(this.context));
        GenericRawResults<String[]> rawResults = null;
        List<String []> resultList = null;
        long noOfDays = 0;
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -SnapToolkitConstants.AVG_MONTH_SPAN);
            GenericRawResults<String[]> daysResult = databaseHelper.getBillItemDao().queryRaw("select * from transactions where transaction_timestamp > '"+new SimpleDateFormat(SnapToolkitConstants.STANDARD_DATEFORMAT).format(cal.getTime())+"' group by SUBSTR(transaction_timestamp, 0 ,11)");
            if(daysResult != null)
                noOfDays = daysResult.getResults().size();
            Log.d("","days "+noOfDays);
            //dist_rev
            rawResults = databaseHelper.getBillItemDao().queryRaw("select distributor.agency_name, SUM(transaction_details.sku_sale_price * transaction_details.sku_quantity) as value from transaction_details inner join product_sku on transaction_details.sku_id = product_sku.sku_id inner join distributor_brand_map on product_sku.brand_id = distributor_brand_map.brand_id inner join distributor on distributor_brand_map.distributor_id = distributor.distributor_id where product_sku.brand_id != '"+SnapToolkitConstants.MISCELLANEOUS_BRAND_ID+"' group by distributor.distributor_id order by value DESC");
            SnapSharedUtils.storeChartData(storeBarGraphDataPoint(rawResults), ChartType.REVENUE_DISTRIBUTOR, SnapCommonUtils.getSnapContext(context));
            //dist_Stock
            rawResults = databaseHelper.getBillItemDao().queryRaw("select distributor.agency_name, SUM(inventory_batch.sku_mrp * inventory_batch.sku_available_qty) as value from inventory_batch inner join product_sku on product_sku.sku_id = inventory_batch.sku_id inner join distributor_brand_map on product_sku.brand_id = distributor_brand_map.brand_id inner join distributor on distributor_brand_map.distributor_id = distributor.distributor_id where product_sku.brand_id != '"+SnapToolkitConstants.MISCELLANEOUS_BRAND_ID+"' group by distributor.distributor_id order by value DESC");                    
            SnapSharedUtils.storeChartData(storeBarGraphDataPoint(rawResults), ChartType.STOCK_VALUE_DISTRIBUTOR, SnapCommonUtils.getSnapContext(context));
            //dist_days_of_stock
            GenericRawResults<String[]> rawResultsStockValue = databaseHelper.getBillItemDao().queryRaw("select distributor.distributor_id, SUM(inventory_batch.sku_mrp * inventory_batch.sku_available_qty) from inventory_batch inner join product_sku on product_sku.sku_id = inventory_batch.sku_id inner join distributor_brand_map on product_sku.brand_id = distributor_brand_map.brand_id inner join distributor on distributor_brand_map.distributor_id = distributor.distributor_id where product_sku.brand_id != '"+SnapToolkitConstants.MISCELLANEOUS_BRAND_ID+"' group by distributor.distributor_id order by distributor.distributor_id");
            rawResults = databaseHelper.getBillItemDao().queryRaw("select distributor.distributor_id, distributor.agency_name, Sum(transaction_details.sku_sale_price * transaction_details.sku_quantity) from transaction_details inner join product_sku on product_sku.sku_id = transaction_details.sku_id inner join inventory_sku on inventory_sku.inventory_sku_id = product_sku.sku_id inner join distributor_brand_map on distributor_brand_map.brand_id = product_sku.brand_id inner join distributor on distributor.distributor_id = distributor_brand_map.distributor_id where product_sku.brand_id != '"+SnapToolkitConstants.MISCELLANEOUS_BRAND_ID+"' group by distributor.distributor_id order by distributor.distributor_id");                   
            resultList = rawResultsStockValue.getResults();
            SnapSharedUtils.storeChartData(storeBarGraphDataPoint(rawResults.getResults(), resultList, noOfDays), ChartType.DAYS_STOCK_DISTRIBUTOR, SnapCommonUtils.getSnapContext(context));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SQLiteDatabaseCorruptException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            //company_rev
            rawResults = databaseHelper.getBillItemDao().queryRaw("select company.company_name, SUM(transaction_details.sku_sale_price * transaction_details.sku_quantity) as value from transaction_details inner join product_sku on transaction_details.sku_id = product_sku.sku_id inner join brand on product_sku.brand_id = brand.brand_id inner join company on brand.company_id = company.company_id where product_sku.brand_id != '"+SnapToolkitConstants.MISCELLANEOUS_BRAND_ID+"' group by company.company_id order by value DESC");
            SnapSharedUtils.storeChartData(storeBarGraphDataPoint(rawResults), ChartType.REVENUE_COMPANY, SnapCommonUtils.getSnapContext(context));
            //company_stock
            rawResults = databaseHelper.getBillItemDao().queryRaw("select company.company_name, SUM(inventory_batch.sku_mrp * inventory_batch.sku_available_qty) as value from inventory_batch inner join product_sku on product_sku.sku_id = inventory_batch.sku_id inner join brand on product_sku.brand_id = brand.brand_id inner join company on company.company_id = brand.company_id where product_sku.brand_id != '"+SnapToolkitConstants.MISCELLANEOUS_BRAND_ID+"' group by company.company_id order by value DESC");                   
            
            SnapSharedUtils.storeChartData(storeBarGraphDataPoint(rawResults), ChartType.STOCK_VALUE_COMPANY, SnapCommonUtils.getSnapContext(context));
            //company_days_of_stock
            GenericRawResults<String[]> rawResultsStockValue = databaseHelper.getBillItemDao().queryRaw("select company.company_id, SUM(inventory_batch.sku_mrp * inventory_batch.sku_available_qty) as value from inventory_batch inner join product_sku on product_sku.sku_id = inventory_batch.sku_id inner join brand on product_sku.brand_id = brand.brand_id inner join company on company.company_id = brand.company_id where product_sku.brand_id != '"+SnapToolkitConstants.MISCELLANEOUS_BRAND_ID+"' group by company.company_id order by company.company_id");                               
            resultList = rawResultsStockValue.getResults();
            rawResults = databaseHelper.getBillItemDao().queryRaw("select company.company_id, company.company_name, Sum(transaction_details.sku_sale_price * transaction_details.sku_quantity) from transaction_details inner join product_sku on product_sku.sku_id = transaction_details.sku_id inner join inventory_sku on inventory_sku.inventory_sku_id = product_sku.sku_id inner join brand on brand.brand_id = product_sku.brand_id inner join company on company.company_id = brand.company_id where product_sku.brand_id != '"+SnapToolkitConstants.MISCELLANEOUS_BRAND_ID+"' group by company.company_id order by company.company_id");                  
            SnapSharedUtils.storeChartData(storeBarGraphDataPoint(rawResults.getResults(), resultList, noOfDays), ChartType.DAYS_STOCK_COMPANY, SnapCommonUtils.getSnapContext(context));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SQLiteDatabaseCorruptException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            //category_rev
            rawResults = databaseHelper.getBillItemDao().queryRaw("select product_category.product_category_name, SUM(transaction_details.sku_sale_price * transaction_details.sku_quantity) as value from transaction_details inner join product_sku on transaction_details.sku_id = product_sku.sku_id inner join product_category on product_sku.sku_subcategory_id = product_category.product_category_id where product_category.product_parentcategory_id > -1 and product_sku.brand_id != '"+SnapToolkitConstants.MISCELLANEOUS_BRAND_ID+"' group by product_category.product_category_id order by value DESC");
            SnapSharedUtils.storeChartData(storeBarGraphDataPoint(rawResults), ChartType.REVENUE_CATEGORY, SnapCommonUtils.getSnapContext(context));
            //category_stock
            rawResults = databaseHelper.getBillItemDao().queryRaw("select product_category.product_category_name, SUM(inventory_batch.sku_mrp * inventory_batch.sku_available_qty) as value from inventory_batch inner join product_sku on product_sku.sku_id = inventory_batch.sku_id inner join product_category on product_sku.sku_subcategory_id = product_category.product_category_id where product_category.product_parentcategory_id > -1 and product_sku.brand_id != '"+SnapToolkitConstants.MISCELLANEOUS_BRAND_ID+"' group by product_category.product_category_id order by value DESC");                    
            SnapSharedUtils.storeChartData(storeBarGraphDataPoint(rawResults), ChartType.STOCK_VALUE_CATEGORY, SnapCommonUtils.getSnapContext(context));
            //category_days_stock
            GenericRawResults<String[]> rawResultsStockValue = databaseHelper.getBillItemDao().queryRaw("select product_category.product_category_id, SUM(inventory_batch.sku_mrp * inventory_batch.sku_available_qty) as value from inventory_batch inner join product_sku on product_sku.sku_id = inventory_batch.sku_id inner join product_category on product_sku.sku_subcategory_id = product_category.product_category_id where product_category.product_parentcategory_id > -1 and product_sku.brand_id != '"+SnapToolkitConstants.MISCELLANEOUS_BRAND_ID+"' group by product_category.product_category_id order by product_category.product_category_id");
            rawResults = databaseHelper.getBillItemDao().queryRaw("select product_category.product_category_id, product_category.product_category_name, Sum(transaction_details.sku_sale_price * transaction_details.sku_quantity) from transaction_details inner join product_sku on product_sku.sku_id = transaction_details.sku_id inner join inventory_sku on inventory_sku.inventory_sku_id = product_sku.sku_id inner join product_category on product_sku.sku_subcategory_id = product_category.product_category_id and product_category.product_parentcategory_id > -1 where product_sku.brand_id != '"+SnapToolkitConstants.MISCELLANEOUS_BRAND_ID+"' group by product_category.product_category_id order by product_category.product_category_id");                   
            resultList = rawResultsStockValue.getResults();
            SnapSharedUtils.storeChartData(storeBarGraphDataPoint(rawResults.getResults(), resultList, noOfDays), ChartType.DAYS_STOCK_CATEGORY, SnapCommonUtils.getSnapContext(context));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SQLiteDatabaseCorruptException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		if (result) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
