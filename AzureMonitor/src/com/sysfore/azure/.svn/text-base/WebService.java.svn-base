package com.sysfore.azure;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import com.sysfore.azure.Dashbaord.ResourceUsageDownload;
import com.sysfore.azure.modal.HostedServiceParser;
import com.sysfore.azure.modal.ResourceUsageServiceParser;
import com.sysfore.azure.modal.VMServiceParser;
import com.sysfore.azure.model.HostedService;
import com.sysfore.azure.model.ResourceUsageService;
import com.sysfore.azure.model.Subscription;
import com.sysfore.azure.model.VMService;
import com.sysfore.azure.service.AzureServiceCall;
import com.sysfore.azure.sqlite.helper.DatabaseHelper;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WebService extends Service {

	
	   String tag="TestService";
	
	   
	   @Override
	   public void onCreate() {
	       super.onCreate();
	     //  Toast.makeText(this, "Service created...", Toast.LENGTH_LONG).show();   
	      
	       Log.i(tag, "Service created...");
	       
	   }

	   @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
	       super.onStart(intent, startId);  
	       ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();

			if (netInfo != null && netInfo.isConnectedOrConnecting()) {

				/*Toast.makeText(WebService.this, "  Internet Connected",
						Toast.LENGTH_LONG).show();*/
				
				 new MyAsyncTask(this).execute();
				
				//new ResourceUsageDownload().execute();
				
			}

			else {

				Toast.makeText(WebService.this,
						"No internet connectivity Please try later",
						Toast.LENGTH_LONG).show();
			}

	       Log.i(tag, "Service started...");
	       return 0;
	   }
	   @Override
	   public void onDestroy() {
	       super.onDestroy();
	       Toast.makeText(this, "Service destroyed...", Toast.LENGTH_LONG).show();
	   }

	   @Override
	   public IBinder onBind(Intent intent) {
	       return null;

	   }
	   
	

		
}