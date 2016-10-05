package com.snapbizz.snapbilling.activity;

import java.util.ArrayList;
import java.util.Calendar;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.asynctaskV2.DownloadSyncTask;
import com.snapbizz.snapbilling.asynctaskV2.EditStoreDetailsTask;
import com.snapbizz.snapbilling.asynctaskV2.StoreOtpTask;
import com.snapbizz.snapbilling.interfaces.V2.OnLoadStoreDetailsListener;
import com.snapbizz.snaptoolkit.domains.Retailer;
import com.snapbizz.snaptoolkit.domains.Store;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.ValidationUtils;
import com.snapbizz.v2.sync.LocalSyncData.ApiDeviceRegistrationResponse;
import com.snapbizz.v2.sync.LocalSyncData.ApiDeviceRegistrationResponse.ApiEditStore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RetailerRegistrationActivity extends Activity implements OnQueryCompleteListener, OnLoadStoreDetailsListener {
	private final int OTP_GENERATE_TASKCODE = 1;
	private final int OTP_VERIFICATION_TASKCODE = 2;
	private int STORE_DOWNLOAD_SYNC_TASKCODE = 4;
	private int STORE_EDIT_DETAILS_TASKCODE = 3;
	private int MAX_LENGTH = 6;
	private ProgressDialog progressDialog;
	private Context commonContext;
	private Dialog dialog = null;
	private EditText storePhoneNoInput = null;
	private String storePhoneNo = "";
	private boolean isEdited = false;
	private ApiEditStore apiEditStoreDetails = null;
	private ArrayList<String> registrationDetailsList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.retailer_register_form);
		((RelativeLayout)findViewById(R.id.registration_parent_layout)).setVisibility(View.GONE);
		commonContext = SnapCommonUtils.getSnapContext(this);
		SnapSharedUtils.storeBackupDbName(commonContext);
		findViewById(R.id.btn_submit).setOnClickListener(
				onConditionClickListener);
		showStoreRegistrationOTPDialog(true);
	}

	private boolean registerRetailerValidation() {
		// Sales Person Phone Number validation
		if (!registrationDetailsList.get(0).isEmpty()
				&& !ValidationUtils.validateMobileNo(registrationDetailsList.get(0))) {
			CustomToast.showCustomToast(getApplicationContext(), getString(R.string.msg_invalid_sales_phone_no), 
					Toast.LENGTH_SHORT, CustomToast.ERROR);
			return false;
		}
		// store TIN number validation
		if (!ValidationUtils.validateTin(registrationDetailsList.get(1))) {
			CustomToast.showCustomToast(getApplicationContext(), getString(R.string.msg_invalid_tin_no), 
					Toast.LENGTH_SHORT, CustomToast.ERROR);
			return false;
		}
		// store ZipCode validation
		if (!ValidationUtils.validateZip(registrationDetailsList.get(2))) {
			CustomToast.showCustomToast(getApplicationContext(), getString(R.string.msg_invalid_zip_code),
					Toast.LENGTH_SHORT, CustomToast.ERROR);
			return false;
		}
		// store name validation
		if (registrationDetailsList.get(6).isEmpty() 
				|| !ValidationUtils.validateName(registrationDetailsList.get(6))) {
			CustomToast.showCustomToast(getApplicationContext(), getString(R.string.msg_invalid_store_name),
					Toast.LENGTH_SHORT, CustomToast.ERROR);
			return false;
		}
		// state name validation
		if (registrationDetailsList.get(3).isEmpty()) {
			CustomToast.showCustomToast(getApplicationContext(), getString(R.string.msg_invalid_store_state),
					Toast.LENGTH_SHORT, CustomToast.ERROR);
			return false;
		}
		// city name validation
		if (registrationDetailsList.get(4).isEmpty()) {
			CustomToast.showCustomToast(getApplicationContext(), getString(R.string.msg_invalid_store_city),
					Toast.LENGTH_SHORT, CustomToast.ERROR);
			return false;
		}
		// store address validation
		if (registrationDetailsList.get(7).isEmpty()) {
			CustomToast.showCustomToast(getApplicationContext(), getString(R.string.msg_invalid_store_address),
					Toast.LENGTH_SHORT, CustomToast.ERROR);
			return false;
		}
		return true;
	}
	
	public void onServiceFinished() {
		SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
		if (progressDialog != null && progressDialog.isShowing())
			progressDialog.cancel();
		Intent callingActivityIntent = new Intent();
		callingActivityIntent.setClassName(this, getIntent().getExtras()
				.getString(SnapToolkitConstants.ACTIVITY_KEY));
		startActivity(callingActivityIntent);
		finish();
	}
	    
	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		if (taskCode == 90 ) {
				onServiceFinished();
		} else if (OTP_GENERATE_TASKCODE == taskCode) {
				SnapCommonUtils.hideSoftKeyboard(RetailerRegistrationActivity.this,
						storePhoneNoInput.getWindowToken());
				showStoreRegistrationOTPDialog(false);
		} else if (STORE_DOWNLOAD_SYNC_TASKCODE == taskCode) {
			CustomToast.showCustomToast(getApplicationContext(), getResources().getString(R.string.download_sync_end_msg),
					Toast.LENGTH_SHORT, CustomToast.SUCCESS);
			onServiceFinished();
		} else if (STORE_EDIT_DETAILS_TASKCODE == taskCode) {
			ApiDeviceRegistrationResponse apiDeviceRegistrationResponse = new ApiDeviceRegistrationResponse();
			apiDeviceRegistrationResponse.store_details = apiEditStoreDetails;
			doSaveStoreDetails(apiDeviceRegistrationResponse, SnapSharedUtils.getStoreAuthKey(SnapCommonUtils.getSnapContext(this)));
			new DownloadSyncTask(RetailerRegistrationActivity.this, STORE_DOWNLOAD_SYNC_TASKCODE, 
					RetailerRegistrationActivity.this).execute();
		}
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel();
		} else if (STORE_EDIT_DETAILS_TASKCODE == taskCode) {
			onServiceFinished();
		} else {
			if (dialog != null && dialog.isShowing())
	            dialog.dismiss();
			dialog = null;
			CustomToast.showCustomToast(getApplicationContext(), errorMessage,
					Toast.LENGTH_SHORT, CustomToast.ERROR);
			finish();
		}
	}

	View.OnClickListener onConditionClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (isStoreDetailsModified())
				isEdited = true;
			String message = getString(R.string.condition_string);
			AlertDialog.Builder builder = new AlertDialog.Builder(RetailerRegistrationActivity.this)
			.setTitle(getResources().getString(R.string.submit))
			.setMessage(message)
			.setPositiveButton(R.string.accept, new Dialog.OnClickListener() {
				@Override
				public void onClick(
						DialogInterface dialogInterface, int i) {
					dialogInterface.dismiss();
					SnapToolkitConstants.SNAP_VERSION = 2;
					if (isEdited) {
						if (registerRetailerValidation()) {
							apiEditStoreDetails = getStoreDetailsAfterModification();
							new EditStoreDetailsTask(RetailerRegistrationActivity.this, STORE_EDIT_DETAILS_TASKCODE, 
									RetailerRegistrationActivity.this, apiEditStoreDetails).execute();
						}
					} else {
						new DownloadSyncTask(RetailerRegistrationActivity.this, STORE_DOWNLOAD_SYNC_TASKCODE,
								RetailerRegistrationActivity.this).execute();
					}
				}
			})
			.setNegativeButton(R.string.cancel,
					new Dialog.OnClickListener() {
				@Override
				public void onClick(
						DialogInterface dialogInterface, int i) {
					dialogInterface.dismiss();
				}
			});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			alertDialog.getWindow().setLayout(950, 650);
		}
	};
	
	private void showStoreRegistrationOTPDialog(final boolean isOTPGenerated) {
		if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
		}
		dialog = new Dialog(RetailerRegistrationActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_store_registraion_generate_otp);
		storePhoneNoInput = (EditText) dialog.findViewById(R.id.store_phone_no_edittext);
		storePhoneNoInput.setFocusable(true);
		Button btCancelDialog;
		Button btConfirmDialog;
		btCancelDialog = (Button) dialog.findViewById(R.id.cancel_button);
		btConfirmDialog = (Button) dialog.findViewById(R.id.ok_button);
		if (!isOTPGenerated) {
			storePhoneNoInput.setFilters(new InputFilter[] {new InputFilter.LengthFilter(MAX_LENGTH)});
			((TextView)dialog.findViewById(R.id.otp_textview)).setText("Enter Store OTP");
			btConfirmDialog.setText(getResources().getString(R.string.ok));
		}
		btCancelDialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btConfirmDialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isOTPGenerated) {
					if (!storePhoneNoInput.getText().toString().isEmpty()
							&& ValidationUtils.validateMobileNo(storePhoneNoInput.getText().toString())) {
						storePhoneNo = storePhoneNoInput.getText().toString();
						if (SnapCommonUtils.isNetworkAvailable(RetailerRegistrationActivity.this)) {
							new StoreOtpTask(RetailerRegistrationActivity.this, RetailerRegistrationActivity.this, 
									OTP_GENERATE_TASKCODE, Long.parseLong(storePhoneNo)).execute("");
						} else {
							 SnapCommonUtils.showErrorDialogAndFinish(RetailerRegistrationActivity.this,
                                     getApplicationContext().getResources().getString(R.string.no_network));
						}
					} else {
						CustomToast.showCustomToast(RetailerRegistrationActivity.this,
								getString(R.string.msg_invalid_store_phone_no), Toast.LENGTH_SHORT,
                                CustomToast.INFORMATION);
					}
				} else {
					if (!storePhoneNoInput.getText().toString().equalsIgnoreCase("")) {
                        if (SnapCommonUtils.isNetworkAvailable(RetailerRegistrationActivity.this)) {
                        	new StoreOtpTask(RetailerRegistrationActivity.this, RetailerRegistrationActivity.this,
                        			OTP_VERIFICATION_TASKCODE, Long.parseLong(storePhoneNo), 
                        			RetailerRegistrationActivity.this)
                        	.execute(storePhoneNoInput.getText().toString());
                        } else {
                            SnapCommonUtils.showErrorDialogAndFinish(RetailerRegistrationActivity.this,
                                                            getApplicationContext().getResources()
                                                            .getString(R.string.no_network));
                        }
                    } else {
                        CustomToast.showCustomToast(RetailerRegistrationActivity.this,
                                                    getResources().getString(R.string.enter_store_otp), 
                                                    Toast.LENGTH_SHORT, CustomToast.INFORMATION);
                    }
				}
			}
		});
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setCancelable(false);
	    dialog.show();
	}

	@Override
	public void loadAndSaveStoreDetails(ApiDeviceRegistrationResponse apiDeviceRegistrationResponse) {
		SnapCommonUtils.hideSoftKeyboard(RetailerRegistrationActivity.this,
				storePhoneNoInput.getWindowToken());
		if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
		}
		String storeAuth = apiDeviceRegistrationResponse.access_token != null ? apiDeviceRegistrationResponse.access_token : "";
		((RelativeLayout)findViewById(R.id.registration_parent_layout)).setVisibility(View.VISIBLE);
		populateStoreDetails(apiDeviceRegistrationResponse);
		doSaveStoreDetails(apiDeviceRegistrationResponse, storeAuth);
	}
	
	private void doSaveStoreDetails(ApiDeviceRegistrationResponse apiDeviceRegistrationResponse,
			String storeAuth) {
		if (apiDeviceRegistrationResponse.store_details != null) {
			Store store = new Store();
			store.setCity(apiDeviceRegistrationResponse.store_details.city);
			store.setState(apiDeviceRegistrationResponse.store_details.state);
			store.setStoreId(String.valueOf(apiDeviceRegistrationResponse.store_details.store_id));
			store.setStorePhoneNumber(String.valueOf(apiDeviceRegistrationResponse.store_details.salesperson_number));
			store.setStoreTinNumber(String.valueOf(apiDeviceRegistrationResponse.store_details.tin));
			store.setZipCode(String.valueOf(apiDeviceRegistrationResponse.store_details.pincode));
			store.setStoreAuthToken(storeAuth);
			store.setStoreName(apiDeviceRegistrationResponse.store_details.name);
			store.setStoreAddress(apiDeviceRegistrationResponse.store_details.address);
			Retailer retailer;
			if (apiDeviceRegistrationResponse.retailer_details != null) {
				retailer = new Retailer(apiDeviceRegistrationResponse.retailer_details.name, 
						String.valueOf(apiDeviceRegistrationResponse.store_details.phone), 
						apiDeviceRegistrationResponse.retailer_details.email, 
						String.valueOf(apiDeviceRegistrationResponse.store_details.salesperson_number), store);
				retailer.setRetailerId(String.valueOf(apiDeviceRegistrationResponse.retailer_details.retailer_id));
			} else {
				retailer = SnapSharedUtils.getRetailer(SnapCommonUtils.getSnapContext(this));
				if (retailer != null)
					retailer.setStore(store);
			}
			SnapSharedUtils.storeDeviceLastSyncTime(Calendar.getInstance().getTime(), commonContext);
			SnapSharedUtils.storeRetailer(SnapCommonUtils.getSnapContext(this), retailer);
			SnapSharedUtils.saveStoreType(SnapCommonUtils.getSnapContext(this),
					apiDeviceRegistrationResponse.store_details.store_type);
			SnapSharedUtils.saveStoreDisabled(SnapCommonUtils.getSnapContext(this),
					apiDeviceRegistrationResponse.store_details.is_disabled);
		}
	}
	
	private void populateStoreDetails(ApiDeviceRegistrationResponse apiDeviceRegistrationResponse) {
		if (apiDeviceRegistrationResponse.retailer_details != null) {
			((EditText) findViewById(R.id.et_owner_email)).setText(apiDeviceRegistrationResponse.retailer_details.email);
			((EditText) findViewById(R.id.et_owner_name)).setText(apiDeviceRegistrationResponse.retailer_details.name);
		}
		if (apiDeviceRegistrationResponse.store_details != null) {
			((EditText) findViewById(R.id.sales_person_phone_et)).setText(String.valueOf(apiDeviceRegistrationResponse.store_details.salesperson_number));
			((EditText) findViewById(R.id.et_tin_number)).setText(String.valueOf(apiDeviceRegistrationResponse.store_details.tin));
			((EditText) findViewById(R.id.et_store_zip)).setText(String.valueOf(apiDeviceRegistrationResponse.store_details.pincode));
			((EditText) findViewById(R.id.et_owner_phone)).setText(String.valueOf(apiDeviceRegistrationResponse.store_details.phone));
			((EditText) findViewById(R.id.et_store_state)).setText(apiDeviceRegistrationResponse.store_details.state);
			((EditText) findViewById(R.id.et_store_city)).setText(apiDeviceRegistrationResponse.store_details.city);
			((EditText) findViewById(R.id.et_owner_store_name)).setText(apiDeviceRegistrationResponse.store_details.name);
			((EditText) findViewById(R.id.et_address1)).setText(apiDeviceRegistrationResponse.store_details.address);
		}
	}
	
	private ApiEditStore getStoreDetailsAfterModification() {
		String salesPersonPhoneNo = registrationDetailsList.get(0);
		String tin = registrationDetailsList.get(1);
		String storeZipCode = registrationDetailsList.get(2);
		String phone = registrationDetailsList.get(5);
		String storeId = SnapSharedUtils.getStoreId(SnapCommonUtils.getSnapContext(this));
		String retailerId = SnapSharedUtils.getRetailerId(SnapCommonUtils.getSnapContext(this));
		ApiEditStore apiEditStore = new ApiEditStore();
		apiEditStore.store_id = storeId != null ? Long.parseLong(storeId) : 0;
		apiEditStore.phone = phone != null ? Long.parseLong(phone) : 0;
		apiEditStore.salesperson_number = salesPersonPhoneNo != null ? Long.parseLong(salesPersonPhoneNo) : 0;
		apiEditStore.state = registrationDetailsList.get(3);
		apiEditStore.city = registrationDetailsList.get(4);
		apiEditStore.tin = tin != null ? Long.parseLong(tin) : 0;
		apiEditStore.pincode = storeZipCode != null ? Integer.parseInt(storeZipCode) : 0;
		apiEditStore.store_type = SnapSharedUtils.getStoreType(SnapCommonUtils.getSnapContext(this));
		apiEditStore.is_disabled = SnapSharedUtils.isStoreDisabled(SnapCommonUtils.getSnapContext(this));
		apiEditStore.name = registrationDetailsList.get(6);
		apiEditStore.address = registrationDetailsList.get(7);
		apiEditStore.retailer_id = retailerId != null ? Long.parseLong(retailerId) : 0;
		return apiEditStore;
	}
	
	private boolean isStoreDetailsModified() {
		registrationDetailsList = getRegistrationInputDetails();
		Retailer retailer = SnapSharedUtils.getRetailer(SnapCommonUtils.getSnapContext(this));
		if (retailer.getStore().getStorePhoneNumber().equalsIgnoreCase(registrationDetailsList.get(0)) 
				&& retailer.getStore().getStoreTinNumber().equalsIgnoreCase(registrationDetailsList.get(1))
				&& retailer.getStore().getZipCode().equalsIgnoreCase(registrationDetailsList.get(2))
				&& retailer.getStore().getState().equalsIgnoreCase(registrationDetailsList.get(3))
				&& retailer.getStore().getCity().equalsIgnoreCase(registrationDetailsList.get(4))
				&& retailer.getStore().getStoreName().equalsIgnoreCase(registrationDetailsList.get(6))
				&& retailer.getStore().getStoreAddress().equalsIgnoreCase(registrationDetailsList.get(7)))
			return false;
		return true;
	}
	
	private ArrayList<String> getRegistrationInputDetails() {
		ArrayList<String> registrationDetailsList = new ArrayList<String>();
		String phone = ((EditText) findViewById(R.id.et_owner_phone)).getText().toString();
		String tin = ((EditText) findViewById(R.id.et_tin_number)).getText().toString();
		String storeZipCode = ((EditText) findViewById(R.id.et_store_zip)).getText().toString();
		String salesPersonPhoneNo = ((EditText) findViewById(R.id.sales_person_phone_et)).getText().toString();
		String state = ((EditText) findViewById(R.id.et_store_state)).getText().toString();
		String city = ((EditText) findViewById(R.id.et_store_city)).getText().toString();
		String storeName = ((EditText) findViewById(R.id.et_owner_store_name)).getText().toString();
		String storeAddress = ((EditText) findViewById(R.id.et_address1)).getText().toString();
		registrationDetailsList.add(salesPersonPhoneNo);
		registrationDetailsList.add(tin);
		registrationDetailsList.add(storeZipCode);
		registrationDetailsList.add(state);
		registrationDetailsList.add(city);
		registrationDetailsList.add(phone);
		registrationDetailsList.add(storeName);
		registrationDetailsList.add(storeAddress);
		return registrationDetailsList;
	}
}