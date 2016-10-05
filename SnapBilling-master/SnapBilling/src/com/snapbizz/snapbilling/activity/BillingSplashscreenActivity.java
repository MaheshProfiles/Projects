package com.snapbizz.snapbilling.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.asynctasks.UpgradeAppTask;
import com.snapbizz.snapbilling.domains.StateRequest;
import com.snapbizz.snaptoolkit.activity.SplashscreenActivity;
import com.snapbizz.snaptoolkit.asynctasks.GetStatesTask;
import com.snapbizz.snaptoolkit.asynctasks.PopulateProdSkuVatTask;
import com.snapbizz.snaptoolkit.asynctasks.PopulateSellingPriceTask;
import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.domains.State;
import com.snapbizz.snaptoolkit.services.ServiceRequest;
import com.snapbizz.snaptoolkit.services.ServiceThread;
import com.snapbizz.snaptoolkit.utils.RequestCodes;
import com.snapbizz.snaptoolkit.utils.RequestFormat;
import com.snapbizz.snaptoolkit.utils.RequestMethod;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class BillingSplashscreenActivity extends SplashscreenActivity {

	private Spinner storeStateSpinner;
	private ArrayAdapter<State> stateSpinnerAdapter;
	private int GET_STATES_TASKCODE = 20;
	private int POPULATE_VAT_TASKCODE = 90;
	private int POPULATE_MARP_TASKCODE = 95;
	boolean isPopulateVat=false;
	boolean isPopulateSellingPrice=false;
	boolean upgradeEight= false;
	boolean isAppUpgrade=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		startingActivity = BillingActivity.class;
		isBillingApp = true;
		crittercismId = SnapToolkitConstants.BILLING_CRITTERCISM_ID;
		super.onCreate(savedInstanceState);
	}

	@Override
	public void upgradeVersion(int oldVersion, int newVersion) {
		if(oldVersion==8){
			upgradeEight=true;
			new GetStatesTask(this, this, GET_STATES_TASKCODE).execute();
			openCustomDialog(oldVersion, newVersion);
		}else{
			new UpgradeAppTask(BillingSplashscreenActivity.this,BillingSplashscreenActivity.this, oldVersion, newVersion, UPGRADE_APP_TASKCODE).execute();
		}
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		super.onTaskSuccess(responseList, taskCode);
		if(taskCode == UPGRADE_APP_TASKCODE){
			isAppUpgrade=true;
			if(!upgradeEight)
				startActivity();
		}
		if(taskCode == GET_STATES_TASKCODE){
			ArrayList<State> stateList = (ArrayList<State>)responseList;  
			//	        State state = new State();
			//	        state.setName("--- Choose State ---");
			//	        stateList.add(0,state);
			//storeStateSpinner = (Spinner) findViewById(R.id.et_store_state);
			stateSpinnerAdapter = new ArrayAdapter<State>(this,
					android.R.layout.simple_dropdown_item_1line, stateList); 
			// storeStateSpinner.setAdapter(stateSpinnerAdapter);

		}else if(taskCode == POPULATE_VAT_TASKCODE){
			isPopulateVat=true;
			if(isAppUpgrade)
				startActivity();
		}else if(taskCode == POPULATE_MARP_TASKCODE){
			isPopulateSellingPrice=true;
			if(isAppUpgrade)
				startActivity();
		}
	}


	public boolean openCustomDialog(final int oldVersion,final int newVersion){
		AlertDialog.Builder customDialog= new AlertDialog.Builder(this);
		customDialog.setTitle(getString(R.string.confirm_state));
		customDialog.setCancelable(false);
		LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=layoutInflater.inflate(R.layout.dialog_state_spinner,null);
		storeStateSpinner = (Spinner)view.findViewById(R.id.et_store_state);
		storeStateSpinner.setAdapter(stateSpinnerAdapter);

		customDialog.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog,int which) {
			    if (storeStateSpinner==null || storeStateSpinner.getAdapter()==null || storeStateSpinner.getAdapter().getCount()==0) {
			        openCustomDialog(oldVersion, newVersion);
			        return;
			    }
				State state =((State)storeStateSpinner.getSelectedItem());
				storeId = SnapSharedUtils.getStoreId(commonContext);
				SnapSharedUtils.storeStateInfo(((State)storeStateSpinner.getSelectedItem()).getName(), ((State)storeStateSpinner.getSelectedItem()).getStateID()+"", commonContext);
				StateRequest request = new StateRequest();
				request.setStateDetails(state);
				request.setStoreId(storeId);
				request.setAccessToken(SnapSharedUtils
						.getStoreAuthKey(commonContext));
				request.setDeviceId(SnapSharedUtils
						.getDeviceId(commonContext));
				request.setRequestFormat(RequestFormat.JSON);
				request.setRequestMethod(RequestMethod.POST);
				ServiceRequest serviceRequest = new ServiceRequest(request,BillingSplashscreenActivity.this);
				serviceRequest.setMethod(SnapToolkitConstants.UPDATE_STATE_METHOD);
				serviceRequest.setPath(SnapToolkitConstants.STORES_PATH);
				serviceRequest.setResponsibleClass(ResponseContainer.class);
				serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_TWENTYSEVEN);
				ServiceThread serviceThread = new ServiceThread(BillingSplashscreenActivity.this, BillingSplashscreenActivity.this, false);
				serviceThread.execute(serviceRequest);
				new PopulateSellingPriceTask (BillingSplashscreenActivity.this,BillingSplashscreenActivity.this, POPULATE_VAT_TASKCODE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				new PopulateProdSkuVatTask(BillingSplashscreenActivity.this,BillingSplashscreenActivity.this, POPULATE_VAT_TASKCODE,((State)storeStateSpinner.getSelectedItem()).getStateID()+"", null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				new UpgradeAppTask(BillingSplashscreenActivity.this,BillingSplashscreenActivity.this, oldVersion, newVersion, UPGRADE_APP_TASKCODE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}});

		customDialog.setNegativeButton(getString(R.string.fetch_state), new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog,int which) {
				new GetStatesTask(BillingSplashscreenActivity.this, BillingSplashscreenActivity.this, GET_STATES_TASKCODE).execute();
				openCustomDialog(oldVersion, newVersion);

			}});

		customDialog.setView(view);
		customDialog.show();     
		return true;
	}


}
