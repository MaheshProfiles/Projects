package com.snapbizz.snaptoolkit.asynctasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public  class PendingSyncTask extends AsyncTask<Void ,Void, HashMap<String, Integer>>
{
	private Context context;
    private OnQueryCompleteListener onQueryCompleteListener;
    private int taskCode;
    private ProgressDialog mProgressDialog;
    private String errorMessage = "Unable to retrieve pending items";
    
	ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String,String>>();
	HashMap<String, Integer> pendingMap ;
	
	public PendingSyncTask(Context context,OnQueryCompleteListener onquerycompletelistner,int taskcode)
	{
		this.context=context;
		this.taskCode=taskcode;
		this.onQueryCompleteListener=onquerycompletelistner;
		
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		mProgressDialog = new ProgressDialog(context);
     	mProgressDialog.setTitle("Fetching Summary");
//		mProgressDialog.setMessage("Please Wait...Do not close application..");
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}
	@Override
	
	protected HashMap<String, Integer> doInBackground(Void... params) 
	{
		
		try {
			pendingMap = new HashMap<String, Integer>();
			Context activityContext = SnapCommonUtils.getSnapContext(context);
		    int productPending = SnapCommonUtils.getDatabaseHelper(context).getProductSkuDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,Locale.getDefault()).parse(SnapSharedUtils.getLastProductSkuSyncTimestamp(activityContext))).query().size();
		    int myStoreProductsPending = SnapCommonUtils.getDatabaseHelper(context).getInventorySkuDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,Locale.getDefault()).parse(SnapSharedUtils.getLastInventorySkuSyncTimestamp(activityContext))).query().size();
		    int customerPending = SnapCommonUtils.getDatabaseHelper(context).getCustomerDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,Locale.getDefault()).parse(SnapSharedUtils.getLastCustomerSyncTimestamp(activityContext))).query().size();
		    int customerPaymentsPending = SnapCommonUtils.getDatabaseHelper(context).getCustomerPaymentDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,Locale.getDefault()).parse(SnapSharedUtils.getLastCustomerPaymentSyncTimestamp(activityContext))).query().size();
		    int memoBillsPending = SnapCommonUtils.getDatabaseHelper(context).getTransactionDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,Locale.getDefault()).parse(SnapSharedUtils.getLastTransactionSyncTimestamp(activityContext))).and().eq("is_invoice", false).query().size();
		    int invoiceBillsPending = SnapCommonUtils.getDatabaseHelper(context).getInvoiceDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,Locale.getDefault()).parse(SnapSharedUtils.getLastInvoiceRetrievalSyncTimestamp(activityContext))).query().size();
		    int billItemsPending = SnapCommonUtils.getDatabaseHelper(context).getBillItemDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,Locale.getDefault()).parse(SnapSharedUtils.getLastBillItemSyncTimestamp(activityContext))).query().size();
//		    int distributorPending = SnapCommonUtils.getDatabaseHelper(context).getDistributorProductMapDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,Locale.getDefault()).parse(SnapSharedUtils.getLastDistributorSyncTimestamp(activityContext))).query().size();
            int distributorPending = SnapCommonUtils.getDatabaseHelper(context).getDistributorDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,Locale.getDefault()).parse(SnapSharedUtils.getLastDistributorSyncTimestamp(activityContext))).query().size();
//		    int distributorPaymentsPending = SnapCommonUtils.getDatabaseHelper(context).getDistributorDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,Locale.getDefault()).parse(SnapSharedUtils.getLastPaymentsSyncTimestamp(activityContext))).query().size();
		    int distributorPaymentsPending = SnapCommonUtils.getDatabaseHelper(context).getPaymentsDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,Locale.getDefault()).parse(SnapSharedUtils.getLastPaymentsSyncTimestamp(activityContext))).query().size();
		    int ordersPending = SnapCommonUtils.getDatabaseHelper(context).getOrderDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,Locale.getDefault()).parse(SnapSharedUtils.getLastOrderSyncTimestamp(activityContext))).query().size();
		    int ordersItemsPending = SnapCommonUtils.getDatabaseHelper(context).getOrderDetailsDao().queryBuilder().where().rawComparison("lastmodified_timestamp",">", new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT,Locale.getDefault()).parse(SnapSharedUtils.getLastOrderDetailsSyncTimestamp(activityContext))).query().size();
					    
		    pendingMap.put("products",productPending);
			pendingMap.put("myStoreProducts",myStoreProductsPending);
			pendingMap.put("customer",customerPending);
			pendingMap.put("customerPayments",customerPaymentsPending);
			pendingMap.put("memoBills",memoBillsPending);
			pendingMap.put("invoiceBills",invoiceBillsPending);
			pendingMap.put("billItems",billItemsPending);
			pendingMap.put("distributor",distributorPending);
			pendingMap.put("distributorPayments",distributorPaymentsPending);
			pendingMap.put("orders",ordersPending);
     		pendingMap.put("orderItems",ordersItemsPending);
			return pendingMap;
		} catch (Exception e) {
            return null;
        }
	}

	@Override
	protected void onPostExecute(HashMap<String, Integer> result) {
		// TODO Auto-generated method stub
		if (null != mProgressDialog) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		if (result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
		  onQueryCompleteListener = null;
	        context = null;
	}
	
}


