package com.snapbizz.snaptoolkit.asynctasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.Order;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class StoreOrderTask extends AsyncTask<List<Order>, Void, Boolean>{

	public static final String PURCHASE_ORDER_NUMBER_SHARED_PREF_STRING = "purchase_order_number_shared_pref";
	public static final String PURCHASE_ORDER_NUMBER_KEY = "new_purchase_order_number";
	private SharedPreferences sharedPreferences;
	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to store records.";
	private SnapBizzDatabaseHelper databaseHelper;

	public StoreOrderTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected Boolean doInBackground(List<Order>... orderList) {
		try {
			int PONumber = 0;
			if(orderList[0] == null)
				return false;
			databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
			for(Order order : orderList[0]) {
				Distributor distributor = new Distributor();
				distributor.setDistributorId(order.getDistributorsId());
				order.setDistributorID(distributor);
				PONumber = order.getOrderNumber();
				databaseHelper.getOrderDao().create(order);
			}
			String timestamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime());
            SnapSharedUtils.storeLastOrderSyncTimestamp(timestamp, SnapCommonUtils.getSnapContext(context));
            sharedPreferences = context.getSharedPreferences(PURCHASE_ORDER_NUMBER_SHARED_PREF_STRING, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt(PURCHASE_ORDER_NUMBER_KEY, PONumber);
			editor.commit();
			return true;
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
			return false;
		}
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
