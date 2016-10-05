package com.snapbizz.snaptoolkit.asynctasks;

import java.io.IOException;

import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.ToolkitV2;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Window;

public class OtpGenerateTask extends AsyncTask<String, Void, Integer>{
	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private int otp = 0;
    private ProgressDialog progressDialog;
	
	public OtpGenerateTask(Context context,OnQueryCompleteListener onQueryCompleteListener, int taskCode){
		this.context = SnapCommonUtils.getSnapContext(context);
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
        progressDialog = new ProgressDialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.show();

	}
	
	@Override
	protected Integer doInBackground(String... params) {
		final Retrofit retrofit = ToolkitV2.buildRetrofit(SnapToolkitConstants.BASE_URL + "/stores/");
		OTPResponse response = null;
		
	    try {
	    	if(params[0] != null && !params[0].trim().isEmpty()) {
				otp = Integer.parseInt(params[0]);
				response = verifyOTP(retrofit);
	    	}
	    	else {
	    		response = generateOTP(retrofit);
	    	}
	    	if(response != null && response.responseCode == 200)
				return 1;
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
			    onQueryCompleteListener.onTaskError(context.getResources().getString(R.string.error_generate_otp), taskCode);
    			break;
	    	case 1:
		    	onQueryCompleteListener.onTaskSuccess("Sucess", taskCode);
			    break;
		}
	};
	
	private static class OTPResponse {
		public int responseCode = 0;
		public String reponseMessage = null;
	}
	
	private static interface OTPAPI {
		@GET("generate_otp")
		Call<OTPResponse> generate(@Query("store_id") String storeId,
				@Query("device_id") String deviceId , @Query("access_token") String accessToken);
		
		@POST("verify_otp")
		Call<OTPResponse> verify(@Body VerifyOtp verifyOtp);
	
	}
	
	private OTPResponse generateOTP(Retrofit retrofit) throws IOException {
		OTPAPI otpAPI = retrofit.create(OTPAPI.class);
		Call<OTPResponse> call = otpAPI.generate(SnapSharedUtils.getStoreId(context),
				SnapSharedUtils.getDeviceId(context), SnapSharedUtils.getStoreAuthKey(context));
		return call.execute().body();
	}
	
	private OTPResponse verifyOTP(Retrofit retrofit) throws IOException {
		OTPAPI verifyOTPAPI = retrofit.create(OTPAPI.class);
		VerifyOtp verifyOtp = new VerifyOtp(otp , Integer.parseInt(SnapSharedUtils.getStoreId(context)),
				SnapSharedUtils.getDeviceId(context) , SnapSharedUtils.getStoreAuthKey(context));
		Call<OTPResponse> call = verifyOTPAPI.verify(verifyOtp);
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
