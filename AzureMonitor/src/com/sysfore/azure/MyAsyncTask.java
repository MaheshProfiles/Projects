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
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class MyAsyncTask extends  AsyncTask<Void, String, Void> {
	
	
	public static final String MYPREFS = "mySharedPreferences";
	int mode = Activity.MODE_PRIVATE;
	   String tag="TestService";
	   DatabaseHelper db;
	   List<HostedService> keycount;
	   int login_id;
	   String subcriptionID, certificateKey, pKeyPassword, rolename,
		retreivedmode,subId;
	   List<ResourceUsageService> resourcedetails;
		List<Subscription> subcription;
	
		List<HostedService> hostedServiceList;
		private Context context; 

		public MyAsyncTask(Context context) {
		    this.context = context;
		}


	@Override
	protected Void doInBackground(Void... params) {
		
		Log.d("Entering in Asynctask************************", "Aynsctask clas");
		resourcedetails = new ArrayList<ResourceUsageService>();
		keycount = new ArrayList<HostedService>();
		
		db = new DatabaseHelper(context);
		loadpreferences();
		getSubscriptionDetails();
		getResourceUsage();
		vmusage();
		return null;
	}
	
	
	  private void loadpreferences() {
			// TODO Auto-generated method stub
			SharedPreferences mySharedPreferences = context.getSharedPreferences(MYPREFS,mode);
			login_id = mySharedPreferences.getInt("login_id", 0);
			
			Log.d("Asynctask", "" + login_id);
			subId = mySharedPreferences.getString("SubcriptionID", "");
			Log.d("SubcriptionID inASYNTASK******************", "" + subId);

		}

		private void getSubscriptionDetails() {
			// TODO Auto-generated method stub
			subcription = db.getAllSubcription(Integer.toString(login_id),subId);
			for (int i = 0; i < subcription.size(); i++) {
				subcriptionID = subcription.get(i).getSubcriptionId();
				Log.d("SubcriptionID inASYNTASK******************", "" + subcriptionID);
				certificateKey = subcription.get(i).getCertificateKey();
				Log.d("certificateKey inASYNTASK******************", "" + certificateKey);
				pKeyPassword = subcription.get(i).getPassword();
				Log.d("pKeyPassword inASYNTASK******************", "" + pKeyPassword);
				
				

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
			InputStream keyInput = context.openFileInput(certificateKey);

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

			InputStream keyInput = context.openFileInput(certificateKey);
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
					keyInput = context.openFileInput(certificateKey);
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
					hostedServiceGroupList = summary.get(hostedService
							.getRoleSize());
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
	
	
}