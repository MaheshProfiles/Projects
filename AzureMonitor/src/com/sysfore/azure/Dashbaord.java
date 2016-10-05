package com.sysfore.azure;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sysfore.azure.modal.HostedServiceParser;
import com.sysfore.azure.modal.ResourceUsageServiceParser;
import com.sysfore.azure.modal.VMServiceParser;
import com.sysfore.azure.model.HostedService;
import com.sysfore.azure.model.LoginMaster;
import com.sysfore.azure.model.ResourceUsageService;
import com.sysfore.azure.model.Subscription;
import com.sysfore.azure.model.VMService;
import com.sysfore.azure.service.AzureServiceCall;
import com.sysfore.azure.sqlite.helper.DatabaseHelper;

public class Dashbaord extends FragmentActivity {

	public static final String MYPREFS = "mySharedPreferences";
	int mode = Activity.MODE_PRIVATE;
	String subcriptionID, certificateKey, pKeyPassword, rolename,
			retreivedmode;
	List<ResourceUsageService> resourcedetails;
	List<Subscription> subcription;
	List<HostedService> keycount;
	List<HostedService> hostedServiceList;
	DatabaseHelper db;
	HostedService hs;
	int login_id, rawCount, rawCount1;
	String coretotal, corecreated, cloudtotal, cloudcreated, storagetotal,
			storagecreated,subId;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.dashboard);

		resourcedetails = new ArrayList<ResourceUsageService>();
		keycount = new ArrayList<HostedService>();
		db = new DatabaseHelper(this);

		loadpreferences();
		getSubscriptionDetails();
		// getResourceUsage();
		rawCount = db.getResourceCount(Integer.toString(login_id),subId);
		rawCount1 = db.getVmusageCount(Integer.toString(login_id),subId);
		
		Log.d("rawCount", ""+rawCount);
		Log.d("rawCount1", ""+rawCount1);
		List<LoginMaster> userdetails = db.getUserDetails();
		for (int i = 0; i < userdetails.size(); i++) {
			retreivedmode = userdetails.get(i).getOfflinemode();
			//Log.d("retreivedmode", retreivedmode);
		}

		if (rawCount == 0 && rawCount1 == 0) {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();

			if (netInfo != null && netInfo.isConnectedOrConnecting()) {

				/*Toast.makeText(Dashbaord.this, "  Internet Connected",
						Toast.LENGTH_LONG).show();*/
				new ResourceUsageDownload().execute();
				
			}

			else {

				Toast.makeText(Dashbaord.this,
						"No internet connectivity Please try later",
						Toast.LENGTH_LONG).show();
			}

		} else {
			getDatafromDB();
			 Intent myIntent = new Intent(Dashbaord.this , WebService.class);     
		        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		       PendingIntent pendingIntent = PendingIntent.getService(Dashbaord.this, 0, myIntent, 0);

		        Calendar twelve = Calendar.getInstance();
		        twelve.set(Calendar.HOUR_OF_DAY, 4);
		        twelve.set(Calendar.MINUTE, 13);
		        twelve.set(Calendar.SECOND, 00);
		        twelve.set(Calendar.AM_PM, Calendar.PM);
		        
		      
		        

		       alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, twelve.getTimeInMillis(), twelve.getTimeInMillis(), pendingIntent);
		        
		//	new ResourceUsageDownload().execute();
		}
		/*
		 * else { if (retreivedmode.equalsIgnoreCase("Y")) {
		 * 
		 * Toast.makeText(Dashbaord.this, "  Data for user",
		 * Toast.LENGTH_LONG).show(); List<ResourceUsageService>
		 * resourcedetails=
		 * db.getAllResourceUsageDetails(Integer.toString(login_id));
		 * 
		 * 
		 * }else{
		 * 
		 * Toast.makeText(Dashbaord.this,
		 * "No internet connectivity Please try later",
		 * Toast.LENGTH_LONG).show(); }
		 * 
		 * }
		 */

		getActionBar();

		Bundle bundle = new Bundle();
		bundle.putString("coretotal", coretotal);
		bundle.putInt("corecreated", db.getVmusageStartedCount(Integer.toString(login_id),subcriptionID));
		bundle.putString("cloudtotal", cloudtotal);
		bundle.putString("cloudcreated", cloudcreated);
		bundle.putString("storagetotal", storagetotal);
		bundle.putString("storagecreated", storagecreated);
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		CoresCreatedFragment cores = new CoresCreatedFragment();
		CloudServiceCreatedFragment cloud = new CloudServiceCreatedFragment();
		StorageAccountCreatedFragment storage = new StorageAccountCreatedFragment();
		MonitorMachineStatusFragment monitor = new MonitorMachineStatusFragment();
		cores.setArguments(bundle);
		cloud.setArguments(bundle);
		storage.setArguments(bundle);
		// fragmentTransaction.setCustomAnimations(R.anim.slide_in_left,
		// R.anim.slide_in_right);

		fragmentTransaction.replace(R.id.My_Container_1_ID, cores);
		fragmentTransaction.replace(R.id.My_Container_2_ID, cloud);
		fragmentTransaction.replace(R.id.My_Container_3_ID, storage);
		fragmentTransaction.replace(R.id.My_Container_4_ID, monitor);

		fragmentTransaction.commit();

	}

	private void getDatafromDB() {
		// TODO Auto-generated method stub

		List<ResourceUsageService> resourcedetails = db.getAllResourceUsageDetails(Integer.toString(login_id),subId);
		for (int i = 0; i < resourcedetails.size(); i++) {
			coretotal = resourcedetails.get(i).getMaxCoreCount();
			corecreated = resourcedetails.get(i).getCurrentCoreCount();
			cloudtotal = resourcedetails.get(i).getMaxHostedServices();
			cloudcreated = resourcedetails.get(i).getCurrentHostedServices();
			storagetotal = resourcedetails.get(i).getMaxStorageAccounts();
			storagecreated = resourcedetails.get(i).getCurrentStorageAccounts();
		}
		// Log.d("vmusagedetail**********************", ""+resourcedetails);
		
		//	int startedCount =db.getVmusageStartedCount(Integer.toString(login_id),subcriptionID);
		//	Log.d("startedCount**********************", ""+startedCount);

	}

	private void loadpreferences() {
		// TODO Auto-generated method stub
		SharedPreferences mySharedPreferences = getSharedPreferences(MYPREFS,mode);
		login_id = mySharedPreferences.getInt("login_id", 0);
		Log.d("login_id", "" + login_id);
		subId = mySharedPreferences.getString("SubcriptionID", "");
		Log.d("SubcriptionID******************", "" + subId);

	}

	private void getSubscriptionDetails() {
		// TODO Auto-generated method stub
		subcription = db.getAllSubcription(Integer.toString(login_id),subId);
		for (int i = 0; i < subcription.size(); i++) {
			subcriptionID = subcription.get(i).getSubcriptionId();
			certificateKey = subcription.get(i).getCertificateKey();
			pKeyPassword = subcription.get(i).getPassword();
			

		}

		// certificateKey = "descfile.pfx";
	}

	public class ResourceUsageDownload extends AsyncTask<Void, String, Void>implements OnCancelListener {
		ProgressHUD mProgressHUD;

		@Override
		protected void onPreExecute() {
			mProgressHUD = ProgressHUD.show(Dashbaord.this, "Connecting", true,
					true, this);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				publishProgress("Connecting");
				Thread.sleep(2000);
				publishProgress("Processing");
				Thread.sleep(5000);
				getResourceUsage();
				vmusage();
				publishProgress("Done");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			mProgressHUD.setMessage(values[0]);
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {

			mProgressHUD.dismiss();
			super.onPostExecute(result);
						
			Bundle bundle = new Bundle();
			bundle.putString("coretotal", coretotal);
			bundle.putInt("corecreated", db.getVmusageStartedCount(Integer.toString(login_id),subId));
			bundle.putString("cloudtotal", cloudtotal);
			bundle.putString("cloudcreated", cloudcreated);
			bundle.putString("storagetotal", storagetotal);
			bundle.putString("storagecreated", storagecreated);
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			CoresCreatedFragment cores = new CoresCreatedFragment();
			CloudServiceCreatedFragment cloud = new CloudServiceCreatedFragment();
			StorageAccountCreatedFragment storage = new StorageAccountCreatedFragment();
			MonitorMachineStatusFragment monitor = new MonitorMachineStatusFragment();
			cores.setArguments(bundle);
			cloud.setArguments(bundle);
			storage.setArguments(bundle);
			
			
			// fragmentTransaction.setCustomAnimations(R.anim.slide_in_left,
			// R.anim.slide_in_right);
			

			fragmentTransaction.replace(R.id.My_Container_1_ID, cores);
			fragmentTransaction.replace(R.id.My_Container_2_ID, cloud);
			fragmentTransaction.replace(R.id.My_Container_3_ID, storage);
			fragmentTransaction.replace(R.id.My_Container_4_ID, monitor);

			fragmentTransaction.commit();
			
			
			
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			this.cancel(true);
			mProgressHUD.dismiss();
		}
	}

	public List<ResourceUsageService> getResourceUsage() {

		try {

			/*
			 * StrictMode.ThreadPolicy policy = new
			 * StrictMode.ThreadPolicy.Builder().permitAll().build();
			 * StrictMode.setThreadPolicy(policy);
			 */
			// Open a secure connection.
			// String subscription_id = "fc7b7fb1-2b20-4fdc-addb-42abd01f1317";

			// String sUrl =
			// "https://management.core.windows.net/"+subscription_id+"/services/hostedservices";
			// String sUrl =
			// String.format("https://management.core.windows.net/%s/locations",
			// subscription_id);
			// String sUrl =
			// String.format("https://management.core.windows.net/%s/services/images",
			// subscription_id);
			// String sUrl =
			// String.format("https://management.core.windows.net/%s/services/hostedservices ",
			// subscription_id);
			// String sUrl =
			// String.format("https://management.core.windows.net/%s/services/hostedservices/%s/deploymentslots/%s",
			// subscription_id, "AntuitWFSStg", "Production");
			// String sUrl =
			// String.format("https://management.core.windows.net/%s/services/hostedservices/%s/deployments/%s",
			// subscription_id, "AntuitWFSStg", "AntuitWFSStg");
			// usage service
			String sUrl = "https://management.core.windows.net/"+ subId;

			URL url = new URL(sUrl);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			// con.setRequestProperty("x-ms-version", "2009-10-01");
			con.setRequestProperty("x-ms-version", "2012-03-01");
			con.setRequestProperty("content-Type", "application/xml");

			// Set up the user authentication portion of the handshake with the
			// private key
			// File pKeyFile = new File(this.getAssets().open("AzureCert.pfx"));
			// String pKeyPassword = "azure";
			// Log.d("pKeyPassword  :", pKeyPassword);
			/*
			 * setKeyManagerAlgorithm(helperParameters.getFirstValue(
			 * "keyManagerAlgorithm", true, System.getProperty(
			 * "ssl.KeyManagerFactory.algorithm", "SunX509")));
			 */

			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance("X509");
			KeyStore keyStore = KeyStore.getInstance("PKCS12");

			// InputStream keyInput =
			// getActivity().getAssets().open("AzureCert.pfx");
			InputStream keyInput = openFileInput(certificateKey);

			/*
			 * try {
			 * 
			 * 
			 * keyInput = getActivity().openFileInput(certificateKey);
			 * 
			 * if (keyInput != null) { input = new BufferedReader(new
			 * InputStreamReader(keyInput)); while ((line = input.readLine()) !=
			 * null) { buffer.append(line + eol);
			 * 
			 * }
			 * 
			 * } catch (Exception e) {
			 * 
			 * }}
			 */

			keyStore.load(keyInput, pKeyPassword.toCharArray());
			keyInput.close();
			keyManagerFactory.init(keyStore, pKeyPassword.toCharArray());
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(keyManagerFactory.getKeyManagers(), null,
					new SecureRandom());
			SSLSocketFactory sockFact = context.getSocketFactory();
			con.setSSLSocketFactory(sockFact);

			con.connect();

			// Check for errors
			int responseCode = con.getResponseCode();
			InputStream inputStream;
			if (responseCode == HttpURLConnection.HTTP_OK)
				inputStream = con.getInputStream();
			else
				inputStream = con.getErrorStream();

			// Usage service parser starts hrere

			ResourceUsageServiceParser usageServiceParser = new ResourceUsageServiceParser();
			ResourceUsageService resourceUsageService = usageServiceParser
					.parse(inputStream);
			resourceUsageService.setSubcriptionId(subId);
			resourceUsageService.setCreatedby(Integer.toString(login_id));
			db.createResourceUsageDetail(resourceUsageService);

			Log.d("Usage  :", resourceUsageService.toString());

			// Usage Service parser ends here

			/*
			 * resourceUsageService.getCurrentCoreCount();
			 * Log.d("currentcorecount",
			 * ""+resourceUsageService.getCurrentCoreCount());
			 * resource.getCurrentHostedServices();
			 * resource.getCurrentStorageAccounts(); resource.getMaxCoreCount();
			 * resource.getMaxHostedServices();
			 * resource.getMaxStorageAccounts();
			 */

			/*
			 * ResourceUsageService resource = new ResourceUsageService();
			 * resource
			 * .setMaxStorageAccounts(resourceUsageService.getMaxStorageAccounts
			 * ()); Log.d("setMaxStorageAccounts  :",
			 * resourceUsageService.getMaxStorageAccounts());
			 * resource.setMaxHostedServices
			 * (resourceUsageService.getMaxHostedServices());
			 * resource.setCurrentCoreCount
			 * (resourceUsageService.getCurrentCoreCount());
			 * resource.setCurrentHostedServices
			 * (resourceUsageService.getCurrentHostedServices());
			 * resource.setCurrentStorageAccounts
			 * (resourceUsageService.getCurrentStorageAccounts());
			 * resource.setMaxCoreCount(resourceUsageService.getMaxCoreCount());
			 * resource.setSubcriptionId(subcriptionID);
			 * resource.setCreatedby(Integer.toString(login_id));
			 * db.createResourceUsageDetail(resource);
			 */

			resourcedetails.add(resourceUsageService);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return resourcedetails;
	}

	public List<HostedService> vmusage() {

		try {

			// Open a secure connection.
			// String subscription_id = "fc7b7fb1-2b20-4fdc-addb-42abd01f1317";

			InputStream keyInput = openFileInput(certificateKey);
			String sUrl = "https://management.core.windows.net/"
					+ subId + "/services/hostedservices";
			Log.d("Azure Webservices ", sUrl);
			InputStream inputStream = AzureServiceCall.getAzureData(sUrl,
					keyInput,pKeyPassword);

			// Usage service parser starts hrere

			/*
			 * UsageServiceParser usageServiceParser = new UsageServiceParser();
			 * UsageService usageService =
			 * usageServiceParser.parse(inputStream); Log.d("Usage  :",
			 * usageService.toString());
			 */

			// Usage Service parser ends here

			// -------------------------------------------------------------------------------------------------//

			// Hosted services will start here
			HostedServiceParser hostedServiceParser = new HostedServiceParser();
			hostedServiceList = hostedServiceParser.parse(inputStream);
			VMServiceParser vMServiceParser = new VMServiceParser();
			for (HostedService hostedService : hostedServiceList) {

				synchronized (this) {
					String subUrl = String
							.format("https://management.core.windows.net/%s/services/hostedservices/%s/deployments/%s",
									subId,
									hostedService.getServiceName(),
									hostedService.getServiceName());
					keyInput = openFileInput(certificateKey);
					InputStream inputStreamSub = AzureServiceCall.getAzureData(
							subUrl, keyInput,pKeyPassword);

					VMService vMService = vMServiceParser.parse(inputStreamSub);
					hostedService.setPowerState(vMService.getPowerState());
					hostedService.setRoleSize(vMService.getRoleSize());
				}

			}

			Map<String, List<HostedService>> summary = new HashMap<String, List<HostedService>>();
			List<HostedService> hostedServiceGroupList = null;
			for (HostedService hostedService : hostedServiceList) {

				if (summary.containsKey(hostedService.getRoleSize())) {
					hostedServiceGroupList = summary.get(hostedService.getRoleSize());
					hostedServiceGroupList.add(hostedService);
				} else {
					hostedServiceGroupList = new ArrayList<HostedService>();
					hostedServiceGroupList.add(hostedService);
					summary.put(hostedService.getRoleSize(),
							hostedServiceGroupList);
				}

				hostedService.setCreatedby(Integer.toString(login_id));
				hostedService.setSubcriptionId(subId);

				db.createVMUsageDetail(hostedService);

				Log.d("Hosted Service : ", hostedService.toString());
			}

			for (String key : summary.keySet()) {

				List<HostedService> hostedServie = summary.get(key);

				/*
				 * for (int i = 0; i < hostedServie.size(); i++) {
				 * 
				 * hs = new HostedService();
				 * hs.setServiceName(hostedServie.get(i).getServiceName());
				 * hs.setLocation(hostedServie.get(i).getLocation());
				 * hs.setPowerState(hostedServie.get(i).getPowerState());
				 * hs.setRoleSize(hostedServie.get(i).getRoleSize());
				 * hs.setKey(key);
				 * hs.setCount(Integer.toString(hostedServie.size()));
				 * hs.setSubcriptionId(subcriptionID);
				 * hs.setCreatedby(Integer.toString(login_id));
				 * db.createVMUsageDetail(hs); }
				 */
			}
			/*
			 * for(int i=0;i<hostedServie.size();i++){
			 * 
			 * 
			 * hs.setLocation(hostedServie.get(i).getLocation());
			 * Log.d("Location", ""+hostedServie.get(i).getLocation());
			 * hs.setRoleSize(hostedServie.get(i).getRoleSize());
			 * hs.setServiceName(hostedServie.get(i).getServiceName());
			 * hs.setPowerState(hostedServie.get(i).getPowerState());
			 * 
			 * }
			 */// keycount.add(hs);
				// Log.d("Key : "+key, " Size : "+hostedServie.size());

			// }

			/*
			 * for(int i=0;i<hostedServiceList.size();i++){ hostedlist= new
			 * ArrayList<HostedService>(); hosteddetails = new HostedService();
			 * hosteddetails
			 * .setLocation(hostedServiceList.get(i).getLocation());
			 * Log.d("Location", ""+hostedServiceList.get(i).getLocation());
			 * hosteddetails
			 * .setPowerState(hostedServiceList.get(i).getPowerState());
			 * Log.d("powerstate", ""+hostedServiceList.get(i).getPowerState());
			 * hosteddetails
			 * .setRoleSize(hostedServiceList.get(i).getRoleSize());
			 * Log.d("rolesize", ""+hostedServiceList.get(i).getRoleSize());
			 * hosteddetails
			 * .setServiceName(hostedServiceList.get(i).getServiceName());
			 * Log.d("servicename",
			 * ""+hostedServiceList.get(i).getServiceName());
			 * 
			 * hostedlist.add(hosteddetails);
			 * 
			 * }
			 */

			// Hosted Services Ends here

		} catch (Exception e) {
			e.printStackTrace();
		}
		return keycount;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.subscriptionmenu:
			Intent settings = new Intent(Dashbaord.this,SubscriptionActivity.class);
			startActivity(settings);
			return true;
			
		case R.id.switchsubscription:
			Intent switchsubs = new Intent(Dashbaord.this,SubscriptionList.class);
			startActivity(switchsubs);
			return true;
			
		case R.id.action_resource:
			Intent resource = new Intent(Dashbaord.this,Resource.class);
			startActivity(resource);
			return true;

		case R.id.monitormachine:
			Intent monitormachine = new Intent(Dashbaord.this,
					MachineMonitorActivity.class);
			startActivity(monitormachine);
			return true;

		case R.id.pricemenu:
			Intent pricemenu = new Intent(Dashbaord.this, Pricemenu.class);
			startActivity(pricemenu);
			return true;
		

	case R.id.account_settings:
			Intent accsettings = new Intent(Dashbaord.this,AccountSettingActivity.class);
			startActivity(accsettings);
			return true;
			
		case R.id.action_logout:
			Intent home1 = new Intent(Dashbaord.this, LoginActivity.class);
			startActivity(home1);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent login = new Intent(Dashbaord.this,Dashbaord.class);
		startActivity(login);
	}

}
