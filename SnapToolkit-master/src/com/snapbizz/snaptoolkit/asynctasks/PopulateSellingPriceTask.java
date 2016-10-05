package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class PopulateSellingPriceTask extends AsyncTask<Void, Integer, Boolean>  {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private final String errorMessage = "Unable to populate Selling Price";
	private ProgressDialog pd = null;

	
	public PopulateSellingPriceTask(Context context,OnQueryCompleteListener onQueryCompleteListener, int taskCode){
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
	}
	

	@Override
	protected synchronized Boolean doInBackground(Void... arg0) {
		try {
			SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
			Log.d("Selling Price : ", "inside bgtask querying.....");
			if (isCancelled()) {
		        if (null != pd) {
		            pd.dismiss();
		        }
		        return false;
		    } else { 
		    	databaseHelper.getProductSkuDao().queryRaw("UPDATE product_sku SET sku_saleprice = sku_mrp WHERE sku_mrp < sku_saleprice;").close();
		    	Log.d("Selling Price : ", "updating vals succeeded....");
				return true;
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("Selling Price : ", "updating vals failed....");

			return false;
		} 
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		
		if (result) {
			Log.d("Selling Price : ", "TASK SUCCESS");
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
