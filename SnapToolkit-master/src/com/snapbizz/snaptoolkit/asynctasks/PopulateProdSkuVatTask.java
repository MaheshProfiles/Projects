package com.snapbizz.snaptoolkit.asynctasks;


import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class PopulateProdSkuVatTask extends AsyncTask<Void, Integer, Boolean>  {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to populate vat details";
	private ProgressDialog pd;
	private String stateId;
	private String timestamp;
	
	public PopulateProdSkuVatTask(Context context,OnQueryCompleteListener onQueryCompleteListener, int taskCode,String stateId,String timestamp){
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.stateId= stateId;
		this.timestamp=timestamp;
	}
	
	@Override
	protected void onPreExecute() {
			pd = new ProgressDialog(context);
			pd.setTitle("Updating VAT Rates");
			pd.setMessage("Please Wait...Do not close application..");
			pd.setCancelable(false);
			pd.show();
	};

	@Override
	protected synchronized Boolean doInBackground(Void... arg0) {
		try {
			SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
			Log.d("vat : ", "inside bgtask querying.....");
			if (timestamp == null) {
				timestamp=SnapSharedUtils.getLastProductSkuSyncTimestamp(this.context);
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault());
			Date lastModifiedTimestamp=dateFormat.parse(timestamp);
			if (isCancelled()) {
		        if (null != pd)
		            pd.dismiss();
		        return false;
		    } else { 
		    	databaseHelper.getProductSkuDao().queryRaw("UPDATE product_sku SET vat = (SELECT vat_value FROM vat WHERE vat.state_id = '" + stateId + "' AND vat.subcategory_id = product_sku.sku_subcategory_id) WHERE (vat is null or vat = 0.0) and lastmodified_timestamp >= '"+ new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(lastModifiedTimestamp)+"'").close();
		    	Log.d("vat : ", "updating vats succeeded....");
				return true;
		    }
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("vat : ", "updating vats failed....");

			return false;
		}
		
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			Log.d("vat : ", "TASK SUCCESS");
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
		pd.dismiss();
	}
}
