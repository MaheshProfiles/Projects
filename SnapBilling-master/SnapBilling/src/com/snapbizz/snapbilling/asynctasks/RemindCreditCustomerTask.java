package com.snapbizz.snapbilling.asynctasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.ToolkitV2;

public class RemindCreditCustomerTask extends AsyncTask<Void, Void, Integer> {

	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private float customerCredit;
	private String customerName;
	private String customerNumber;
	
	public RemindCreditCustomerTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode, String customerName,
			float customerCredit, String customerNumber) {
		this.context = context;
		this.taskCode = taskCode;
		this.customerCredit = customerCredit;
		this.customerName = customerName;
		this.customerNumber = customerNumber;
		this.onQueryCompleteListener = onQueryCompleteListener;	
	}
	
	@Override
	protected Integer doInBackground(Void... params) {
		CustomerRemindResponse response = null;
		Retrofit retrofit = ToolkitV2.buildRetrofit(SnapToolkitConstants.BASE_URL + "/");
		try {
			response = remindCreditCustomers(retrofit);
			if(response != null && response.responseCode == 200)
				return 1;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
	@Override
	protected void onPostExecute(Integer result) {
		switch (result) {
		case 0:
			onQueryCompleteListener.onTaskError(context.getString(R.string.remind_failure_message), taskCode);
			break;
		case 1:
			onQueryCompleteListener.onTaskSuccess("Success", taskCode);
			break;
		}
	}

	private static class CustomerRemindResponse {
		public int responseCode = 0;
		public String reponseMessage = null;
	}
	
	private static interface CustomerRemindAPI {
		@POST("remind_sms")
		Call<CustomerRemindResponse> sendSms(@Body CustomerRemind customerRemind);
	}
	
	private CustomerRemindResponse remindCreditCustomers(Retrofit retrofit)
			throws IOException {
		CustomerRemindAPI customerRemindAPI = retrofit.create(CustomerRemindAPI.class);
		CustomerDetails customer = new CustomerDetails(
				(context.getString(R.string.customer_Remind_message,
						customerName, customerCredit, SnapSharedUtils.getStoreDetails(context).getStoreName(),
						SnapSharedUtils.getRetailerPhoneNumber(context))), customerNumber);
		List<CustomerDetails> customerList = new ArrayList<CustomerDetails>();
		customerList.add(customer);
		CustomerRemind remindCustomer = new CustomerRemind(customerList,
				Integer.parseInt(SnapSharedUtils.getStoreId(context)),
				SnapSharedUtils.getDeviceId(context),
				SnapSharedUtils.getStoreAuthKey(context));
		Call<CustomerRemindResponse> call = customerRemindAPI.sendSms(remindCustomer);
		return call.execute().body();
	}

	public class CustomerRemind {
		List<CustomerDetails> customers;
		int store_id;
		String device_id;
		String access_token;

		public CustomerRemind(List<CustomerDetails> customerDetails,
				int storeId, String deviceId, String accessToken) {
			this.customers = customerDetails;
			store_id = storeId;
			device_id = deviceId;
			access_token = accessToken;
		}
	}

	public class CustomerDetails {
		String message;
		String phone;

		public CustomerDetails(String message, String phone) {
			this.message = message;
			this.phone = phone;
		}
	}
}
