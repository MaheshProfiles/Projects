package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.OrderDetails;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetSyncOrderDetailsTask extends
		AsyncTask<String, Integer, List<OrderDetails>> {
	
	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to retrieve orderdetails.";

	public GetSyncOrderDetailsTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}

	@Override
	protected List<OrderDetails> doInBackground(String... timestamp) {
		// TODO Auto-generated method stub
		try {
			List<OrderDetails> orderDetailsList = SnapCommonUtils.getSyncDatabaseHelper(context).getOrderDetailsDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(timestamp[0])).query();
            for(OrderDetails orderDetails : orderDetailsList) {
                if(orderDetails.getOrder() != null)
                    orderDetails.setOrderId(orderDetails.getOrder().getOrderNumber());
                if(orderDetails.getProductSkuID() != null)
                    orderDetails.setProductSkuCode(orderDetails.getProductSkuID().getProductSkuCode());
                else
                    orderDetails.setProductSkuCode(SnapCommonUtils.getSyncDatabaseHelper(context).getOrderDetailsDao().queryRaw("select product_sku_id from order_details where order_detail_id = '"+orderDetails.getOrderDetailID()+"'").getFirstResult()[0]);
            }
            return orderDetailsList;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
	}
	
	@Override
	protected void onPostExecute(List<OrderDetails> result) {
		// TODO Auto-generated method stub
		if (result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
