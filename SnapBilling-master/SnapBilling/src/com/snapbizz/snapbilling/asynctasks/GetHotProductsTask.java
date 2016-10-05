package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snapbilling.domains.HotProduct;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetHotProductsTask extends AsyncTask<Void, Void, List<HotProduct>> {
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "Could not get Hot Products";
	
	public GetHotProductsTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected List<HotProduct> doInBackground(Void... params) {
		try {
			String quickaddString = "(";
			for(String quickaddCode : context.getResources().getStringArray(R.array.quickadd_array)) {
				quickaddString += "'"+quickaddCode+"',";
			}
			GenericRawResults<String[]> rawResults = SnapBillingUtils.getHelper(context).getBillItemDao().queryRaw("select product_sku.sku_id, product_sku.sku_name, Sum(transaction_details.sku_sale_price * transaction_details.sku_quantity), Sum(transaction_details.sku_quantity), Count(*) as basket_count, product_sku.product_imageurl from transaction_details inner join product_sku on product_sku.sku_id = transaction_details.sku_id inner join transactions on transactions.transaction_id = transaction_details.transaction_id where transactions.transaction_timestamp like('"+new SimpleDateFormat(SnapToolkitConstants.DAY_DATEFORMAT).format(Calendar.getInstance().getTime())+"%') and transaction_details.sku_id NOT IN "+quickaddString.substring(0, quickaddString.length() - 1)+") and transaction_details.sku_quantity > 0 group by transaction_details.sku_id order by basket_count DESC limit 10");
			
			List<HotProduct> hotProductList = null;
			for(String [] results : rawResults) {
				HotProduct hotProduct = new HotProduct();
				hotProduct.setSkuCode(results[0]);
				hotProduct.setProductName(results[1]);
				hotProduct.setTotalSales(Float.parseFloat(results[2]));
				hotProduct.setTotalQuantity(Float.parseFloat(results[3]));
				hotProduct.setTotalCustomers(Integer.parseInt(results[4]));
				hotProduct.setImageUrl(results[5]);
				if(hotProductList == null)
					hotProductList = new ArrayList<HotProduct>();
				hotProductList.add(hotProduct);
			}
			return hotProductList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<HotProduct> result) {
		if (result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
	
}
