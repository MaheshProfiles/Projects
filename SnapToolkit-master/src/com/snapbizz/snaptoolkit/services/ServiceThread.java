package com.snapbizz.snaptoolkit.services;

import java.util.ArrayList;

import org.apache.http.conn.ConnectTimeoutException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.interfaces.OnServiceCompleteListener;
import com.snapbizz.snaptoolkit.utils.RequestCodes;
import com.snapbizz.snaptoolkit.utils.ResponseCodes;

public class ServiceThread extends AsyncTask<ServiceRequest, ResponseContainer, ResponseContainer> {
	private final static String TAG = "[ServiceThread]";
	private ProgressDialog progressDialog;
	private boolean isProgressDialogShow = false;
	private OnServiceCompleteListener onServiceCompleteListener;
	private RestCall restCall;
	private Context context;
	private RequestCodes requestCode;
	private boolean isManualCancel = false;
	private Object object = null;
	ArrayList<ServiceRequest> allRequests = new ArrayList<ServiceRequest>();

	public ServiceThread(Context context, OnServiceCompleteListener onServiceCompleteListener, boolean isProgressDialogShow) {
		this.context = context;
		this.onServiceCompleteListener = onServiceCompleteListener;
		this.isProgressDialogShow = isProgressDialogShow;
		 if (isProgressDialogShow) {
			 progressDialog = new ProgressDialog(context);
			 progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			 //progressDialog.setContentView(R.layout.dialog_progress_layout);
			 //progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
			 progressDialog.setOnCancelListener(onDialogCancelListener);
			 progressDialog.setCancelable(true);
			 progressDialog.setCanceledOnTouchOutside(false);
		 }
	}

	DialogInterface.OnCancelListener onDialogCancelListener = new DialogInterface.OnCancelListener() {

		@Override
		public void onCancel(DialogInterface arg0) {
			// TODO Auto-generated method stub
			ServiceThread.this.cancel(true);
		}
	};
	
	public ServiceThread(Context context, OnServiceCompleteListener onServiceCompleteListener,Object obj) {
		this.context = context;
		this.onServiceCompleteListener = onServiceCompleteListener;
		this.object = obj;
	}

	public ServiceThread(Context context, OnServiceCompleteListener onServiceCompleteListener, boolean isProgressDialogShow, String message) {
		this.context = context;
		this.onServiceCompleteListener = onServiceCompleteListener;
		this.isProgressDialogShow = isProgressDialogShow;
		
		if (isProgressDialogShow) {
			progressDialog = new ProgressDialog(context);
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//			progressDialog.setContentView(R.layout.dialog_progress_layout);
//			((TextView)progressDialog.findViewById(R.id.progressdialog_textview)).setText(message);
//			progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);               
			progressDialog.setOnCancelListener(onDialogCancelListener);
			progressDialog.setCancelable(true);
			progressDialog.setCanceledOnTouchOutside(false);
		}
	}
	
	

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (isProgressDialogShow) {
			progressDialog.show();
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		try {
			abortThread();
		} catch(Exception e) {

		}

		if (isProgressDialogShow) {
			if(progressDialog.isShowing())
				progressDialog.cancel();
			isProgressDialogShow = false;
		}
	}

	public void abortThread() {
		if(restCall != null)
			restCall.abortRestCall();
	}
	
	public void queue(ServiceRequest serviceRequest) {
		allRequests.add(serviceRequest);
	}
	
	private ResponseContainer processRequest(ServiceRequest serviceRequest) {
		ResponseContainer responseContainer = new ResponseContainer();
		try{
			restCall = new RestCall();
			this.requestCode = serviceRequest.getRequestCode();
			Log.d(TAG, serviceRequest.getUrl()+serviceRequest.getPath()+serviceRequest.getMethod());
			Log.d(TAG, serviceRequest.getRequestString());

			return restCall.execute(serviceRequest, context);
		} catch (ConnectTimeoutException e) {
			responseContainer.setResponseCode(ResponseCodes.CONNECTION_TIMEOUT.getResponseValue());
			e.printStackTrace();
		} catch (Exception e) {
            responseContainer.setResponseCode(ResponseCodes.CONNECTION_TIMEOUT.getResponseValue());
			e.printStackTrace();
			
		}
		return responseContainer;	
	}
	
	private ResponseContainer processAll() {
		final int RETRY_COUNT = 10;
		for(ServiceRequest request : allRequests) {
			ResponseContainer response = null;
			for(int i=0;i<RETRY_COUNT;i++) {
				response = processRequest(request);
				if(!isManualCancel &&
						(response == null || response.getResponseCode() == ResponseCodes.CONNECTION_TIMEOUT.getResponseValue()) ||
						response.getResponseMessage() == null) {
					Log.d(TAG, "Retry:"+(i+2));
					continue;
				}
				break;
			}
			publishProgress(response);
		}
		return null;
	}

	@Override
	protected ResponseContainer doInBackground(ServiceRequest... serviceRequests) {
		if(serviceRequests == null || serviceRequests.length < 1 || serviceRequests[0] == null) {
			return processAll();
		}
		
		return processRequest(serviceRequests[0]);
	}

	@Override
	protected void onPostExecute(ResponseContainer result) {
		if(result == null)
			return;
		Log.e(TAG, result.getResponseMessage()+":"+result.getResponseCode());
		result.setReturnObj(this.object);
		try{
			if (isProgressDialogShow) {
				progressDialog.cancel();
				isProgressDialogShow = false;
			}
			super.onPostExecute(result);
			if(result == null || result.getResponseCode() == ResponseCodes.CONNECTION_TIMEOUT.getResponseValue()) {
				if(isManualCancel) {
					isManualCancel = false;
					onServiceCompleteListener.onError(result, RequestCodes.REQUEST_CODE_CANCEL);
				} else {
					onServiceCompleteListener.onError(result, requestCode);
				}
			} else if(result.getResponseCode() == ResponseCodes.OK.getResponseValue()){		
				onServiceCompleteListener.onSuccess(result);
			} else if(result.getRequestCode().equals(RequestCodes.REQUEST_CODE_TWENTYEIGHT)){		
				onServiceCompleteListener.onSuccess(result);
			} else
				onServiceCompleteListener.onError(result, requestCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onProgressUpdate(ResponseContainer... responses) {
		if(responses != null && responses.length > 0)
			onPostExecute(responses[0]);
	}

}
