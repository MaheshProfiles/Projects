package com.snapbizz.snapbilling.asynctaskV2;

import java.io.IOException;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.interfaces.V2.OnLoadStoreDetailsListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.ToolkitV2;
import com.snapbizz.v2.sync.LocalSyncData.ApiDeviceRegistrationInput;
import com.snapbizz.v2.sync.LocalSyncData.ApiDeviceRegistrationResponse;
import com.snapbizz.v2.sync.LocalSyncData.GenerateStoreOTPAPIResponse;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Window;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class StoreOtpTask extends AsyncTask<String, Void, Integer>{
	private OnQueryCompleteListener onQueryCompleteListener;
	private OnLoadStoreDetailsListener onLoadStoreDetailsListener;
	private Context context;
	private int taskCode;
	private int otp = 0;
    private ProgressDialog progressDialog;
    private long storePhNo;
    private ApiDeviceRegistrationResponse apiDeviceRegistrationResponse = null;
    private GenerateStoreOTPAPIResponse response = null;
	
	public StoreOtpTask(Context context,OnQueryCompleteListener onQueryCompleteListener, int taskCode, long storePhoneNo) {
		this.context = SnapCommonUtils.getSnapContext(context);
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		storePhNo = storePhoneNo;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getResources().getString(R.string.generate_store_otp));
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.show();
	}
	
	public StoreOtpTask(Context context,OnQueryCompleteListener onQueryCompleteListener, int taskCode, 
			long storePhoneNo, OnLoadStoreDetailsListener onLoadStoreDetailsListener) {
		this.context = SnapCommonUtils.getSnapContext(context);
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.onLoadStoreDetailsListener = onLoadStoreDetailsListener;
		this.taskCode = taskCode;
		storePhNo = storePhoneNo;
        progressDialog = new ProgressDialog(context);
    	progressDialog.setMessage(context.getResources().getString(R.string.verify_store_otp));
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.show();
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		final Retrofit retrofit = ToolkitV2.buildRetrofit(SnapToolkitConstants.BASE_URL_STORE_OTP_APK);
	    try {
	    	if (params[0] != null && !params[0].trim().isEmpty()) {
				otp = Integer.parseInt(params[0]);
				apiDeviceRegistrationResponse = verifyStoreOTP(retrofit);
				if (apiDeviceRegistrationResponse == null)
					return 0;
				if (apiDeviceRegistrationResponse.access_token != null && apiDeviceRegistrationResponse.store_details != null)
					return 1;
	    	}
	    	else {
	    		response = generateStoreOTP(retrofit);
	    		if (response.status != null && response.status.equalsIgnoreCase("success"))
					return 1;	    			
	    	}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	@Override
	protected void onPostExecute(Integer result) {
        progressDialog.dismiss();
		switch (result) {
		    case 0:
		    	if (taskCode == 1) {
		    		if (response != null && response.status != null)
			    		onQueryCompleteListener.onTaskError(response.status, taskCode);
		    		else
			    		onQueryCompleteListener.onTaskError(context.getResources().getString(R.string.error_generate_otp), taskCode);
		    	}
		    	else {
		    		onQueryCompleteListener.onTaskError(context.getResources().getString(R.string.verify_store_otp_error), taskCode);
		    	}
    			break;
	    	case 1:
	    		if (apiDeviceRegistrationResponse != null)
	    			onLoadStoreDetailsListener.loadAndSaveStoreDetails(apiDeviceRegistrationResponse);
	    		else
	    			onQueryCompleteListener.onTaskSuccess("Sucess", taskCode);
			    break;
		}
	};
	
	public class ApiOtpGenerationInput {
		String device_id;
		long store_phone;
	}
	
	private static class OTPResponse {
		public int responseCode = 0;
		public String reponseMessage = null;
	}
	
	private static interface StoreOTPAPI {
		@POST("otp_generation")
		Call<GenerateStoreOTPAPIResponse> generate(@Body ApiOtpGenerationInput apiOtpGenerationInput);
		
		@POST("device_registration")
		Call<ApiDeviceRegistrationResponse> verify(@Body ApiDeviceRegistrationInput verifyOtp);
	}
	
	private GenerateStoreOTPAPIResponse generateStoreOTP(Retrofit retrofit) throws IOException {
		StoreOTPAPI otpAPI = retrofit.create(StoreOTPAPI.class);
		ApiOtpGenerationInput apiOtpGenerationInput = new ApiOtpGenerationInput();
		apiOtpGenerationInput.device_id = SnapCommonUtils.getDeviceID(SnapCommonUtils.getSnapContext(context));
		apiOtpGenerationInput.store_phone = storePhNo;
		Call<GenerateStoreOTPAPIResponse> call = otpAPI.generate(apiOtpGenerationInput);
		return call.execute().body();
	}
	
	private ApiDeviceRegistrationResponse verifyStoreOTP(Retrofit retrofit) throws IOException {
		StoreOTPAPI verifyOTPAPI = retrofit.create(StoreOTPAPI.class);
		ApiDeviceRegistrationInput apiDeviceRegistrationInput = new ApiDeviceRegistrationInput();
		apiDeviceRegistrationInput .device_id = SnapCommonUtils.getDeviceID(SnapCommonUtils.getSnapContext(context));
		apiDeviceRegistrationInput.store_phone = storePhNo;
		apiDeviceRegistrationInput.otp = otp;
		Call<ApiDeviceRegistrationResponse> call = verifyOTPAPI.verify(apiDeviceRegistrationInput);
		return call.execute().body();
	}
	
	public class VerifyOtp {
		private int otp;
	    private int store_id;
	    private String device_id;
	    private String access_token;
	    public VerifyOtp(int otp, int storeId, String deviceId, String accessToken) {
	    	this.otp = otp;
	    	store_id = storeId;
	    	device_id = deviceId;
	    	access_token = accessToken;
	    }
	}

}
