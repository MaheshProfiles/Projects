package com.snapbizz.snapbilling.asynctaskV2;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.v2.sync.LocalSyncAPI;
import com.snapbizz.v2.sync.LocalSyncData.ApiDeviceRegistrationResponse.ApiEditStore;
import com.snapbizz.v2.sync.LocalSyncData.ApiOtpGeneration;
import com.snapbizz.v2.sync.LocalSyncData.DefaultAPIResponse;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Window;
import android.widget.Toast;

public class EditStoreDetailsTask extends AsyncTask<Void, Void, Boolean> {
	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private ProgressDialog progressDialog;
	private ApiEditStore apiEditStore;
	
	public EditStoreDetailsTask(Context context, int taskCode, OnQueryCompleteListener onQueryCompleteListener, ApiEditStore apiEditStore) {
		this.context = context;
		progressDialog = new ProgressDialog(context);
		progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(context.getResources().getString(R.string.sending_store_details_msg));
		progressDialog.show();
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.apiEditStore = apiEditStore;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if (SnapCommonUtils.isNetworkAvailable(context)) {
			return editStoreDetailsAPICallingStatus();
		} else {
			Toast.makeText(context, context.getResources().getString(R.string.no_network),
					Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (progressDialog != null && progressDialog.isShowing())
			progressDialog.dismiss();
		if (result)
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		else
			onQueryCompleteListener.onTaskError(context.getResources().getString(R.string.error_store_details_saving), taskCode);
	}
	
    
    public Boolean editStoreDetailsAPICallingStatus() {
    	try {
			LocalSyncAPI localSyncAPI = new LocalSyncAPI(context);
			ApiOtpGeneration apiOtpGeneration = new ApiOtpGeneration(); 
			apiOtpGeneration.device_id = SnapCommonUtils.getDeviceID(SnapCommonUtils.getSnapContext(context));
			apiOtpGeneration.store_id = Long.parseLong(SnapSharedUtils.getStoreId(context));
			DefaultAPIResponse defaultAPIResponse = localSyncAPI.callEditStoreAPI(apiOtpGeneration, SnapSharedUtils.getStoreAuthKey(context), apiEditStore);
			if (defaultAPIResponse.status.equalsIgnoreCase("success"))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return false;
    }
}
