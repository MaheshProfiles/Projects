package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class UpdateSalePriceProductSkuTask extends
		AsyncTask<List<ProductCategory>, Integer, Boolean> {
	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private ProgressDialog mProgressDialog;
	private final String errorMessage = "Unable to update ProductSku Saleprice.";
	private int productParentId = 0;
	private float spValue = 0;
	private String salePriceType = "";
	private int mProgressStatus =  0;
	private String message = "";
	
	 /** 
	 * Updating the productsku sale prices with the settings set
	 */
	public UpdateSalePriceProductSkuTask(Context context, int taskCode,
			OnQueryCompleteListener onQueryCompleteListener, Float Spvalue, String SpType, int categoryId,
			String categoryName) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.spValue = Spvalue;
		this.salePriceType = SpType;
		this.productParentId = categoryId;
		this.message = categoryName;
	}

	@Override
	protected void onPreExecute() {
		 mProgressDialog = new ProgressDialog(context);
		 mProgressDialog.setCancelable(false);
		 mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		 mProgressDialog.setProgressNumberFormat(null);
		 mProgressDialog.setProgressPercentFormat(null);
		 mProgressDialog.setIndeterminate(true);
		
	};
	@Override
	protected Boolean doInBackground(List<ProductCategory>... productCategoryList) {
		publishProgress(mProgressStatus);
		try {
			String date = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).format(Calendar.getInstance().getTime());
			float SpType = spValue/100 ;
			SnapCommonUtils.getDatabaseHelper(context).getProductSkuDao()
						   .queryRaw("UPDATE product_sku set " + salePriceType + " =(sku_mrp-(sku_mrp*" + SpType + ")), " +
								   	 "lastmodified_timestamp='" + date + "' where sku_subcategory_id ='" +
				                     productParentId + "' ").close();
		}  catch (SQLException e) {
			e.printStackTrace();
		}
		
		return true;	
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		mProgressDialog.setTitle("Updating "+message+" Sale Price");
		mProgressDialog.setMessage("Please Do Not Close Application");
		mProgressDialog.setProgress(mProgressStatus);
		mProgressDialog.show();
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		if (result) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
			Log.e("Result", result+"");
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
}
