package com.snapbizz.snaptoolkit.asynctasks;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sqlcipher.Cursor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetMonthlyProductSkuReportTask extends
		AsyncTask<Void, Void, List<BillItem>> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	String mProdNm = "";
	private String month="";
	private String year="";
	private final String errorMessage = "Unable to retrieve billitems.";

	public GetMonthlyProductSkuReportTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode,
			String productName,String month,String year) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		mProdNm = productName;
		this.month=month;
		this.year=year;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected List<BillItem> doInBackground(Void... params) {
		SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils
				.getDatabaseHelper(context);

		try {
			String excludedProducts = "";
			if (context == null)
				return null;
//			String sqlQuery = "select lastmodified_timestamp,SUM(sku_quantity),SUM(sku_sale_price*sku_quantity),unit_type,current_stock from transaction_details  where  strftime('%m',lastmodified_timestamp)='"+month+"' and strftime('%Y','now')='"+year+"'and sku_id ='"
//					+mProdNm+ "' GROUP BY substr(lastmodified_timestamp, 1, 11)";
			String sqlQuery = "select lastmodified_timestamp,SUM(sku_quantity),SUM(sku_sale_price*sku_quantity),unit_type,current_stock from transaction_details  where  strftime('%m',lastmodified_timestamp)='"+month+"' and strftime('%Y',lastmodified_timestamp)='"+year+"'and sku_id ='"
					+mProdNm+ "' GROUP BY substr(lastmodified_timestamp, 1, 11)";
			Cursor cursor = SnapCommonUtils.getDatabaseHelper(context)
					.getReadableDatabase("sn@pb1zz@123")
					.rawQuery(sqlQuery, null);
			cursor.moveToFirst();
			ArrayList<BillItem> billItemList = new ArrayList<BillItem>();
			if (cursor.getCount() > 0) {
				for (int i = 0; i <cursor.getCount(); i++) {
					if(cursor.moveToPosition(i)){
					BillItem billItem = new BillItem();
					String trimDate[] = null;
					if (cursor.getString(0)
							.contains(" ")) {
						trimDate = cursor.getString(0).split("\\s+");
					}
					if (trimDate != null && trimDate.length > 0){
						 DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
						    Date startDate = null;
						    try {
						        startDate = df.parse(trimDate[0]);
						        String newDateString = df.format(startDate);
						    } catch (ParseException e) {
						        e.printStackTrace();
						    }
						billItem.setLastModifiedTimestamp(startDate);
					}
					if(cursor.getString(1)!=null){
						billItem.setProductSkuQuantity(Float.parseFloat(cursor
								.getString(1)));
					}
					if(cursor.getString(2)!=null){
						billItem.setProductSkuMrp(Float.parseFloat(cursor
								.getString(2)));
					}
					if(cursor.getString(4)!=null){
						billItem.setCurrentStock(Float.parseFloat(cursor
								.getString(4)));
					}
					
					
					billItemList.add(billItem);
					}
				}
			}
			cursor.close();
			return billItemList;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<BillItem> result) {
		if (null != result) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
}
