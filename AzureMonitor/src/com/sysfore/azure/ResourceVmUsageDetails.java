package com.sysfore.azure;

import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.sysfore.azure.model.HostedService;
import com.sysfore.azure.model.ResourceUsageService;
import com.sysfore.azure.model.Subscription;
import com.sysfore.azure.sqlite.helper.DatabaseHelper;


public class ResourceVmUsageDetails extends Fragment {

	
	public static final String MYPREFS = "mySharedPreferences";
	int mode = Activity.MODE_PRIVATE;
	public OnLongClickListener longClickListner;
	LinearLayout panel1, panel2, panel3, panel4, panel5;
	TextView text1, text2, text3, text4, text5;
	View openLayout;
	ListView lv,lv1,lv2;
	Activity ctx;
	CoresAdapter adapter;
	
	ResourceUsageService resource;
	List<ResourceUsageService> resourcedetails;
	DatabaseHelper db;
	int login_id;
	List<Subscription> subcription;
	String subcriptionID,certificateKey,pKeyPassword,rolename,retreivedmode,subId;
	//String sUrl = "https://management.core.windows.net/"+subcriptionID;
	ProgressBar progressBar;
	NetworkInfo netInfo;
	ViewGroup root;


	
	/*public ResourceUsageDetailsActivity() {

	}
	
	public static Fragment newInstance(Context context) {
		ResourceUsageDetailsActivity f = new ResourceUsageDetailsActivity();	
		
		return f;
	}
	*/
	
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		root= (ViewGroup) inflater.inflate(R.layout.resource_vm_usage_details, null);
		 setHasOptionsMenu(true);
		/*panel1 = (LinearLayout) root.findViewById(R.id.panel1);
		panel2 = (LinearLayout) root.findViewById(R.id.panel2);
		panel3 = (LinearLayout) root.findViewById(R.id.panel3);

		
		text2 = (TextView) root.findViewById(R.id.text2);
		text3 = (TextView) root.findViewById(R.id.text3);
		
		
		text1.setOnClickListener(this);
		text2.setOnClickListener(this);
		text3.setOnClickListener(this);
		progressBar = (ProgressBar)root.findViewById(R.id.progressBar1);
		resourcedetails =new ArrayList<ResourceUsageService>();*/
		
		
		// text1 = (TextView) root.findViewById(R.id.text1);
		db=new DatabaseHelper(getActivity());
		loadpreferences();
		getSubscriptionDetails();
		lv=(ListView) root.findViewById(R.id.vm_list_item);
			adapter = new CoresAdapter(getActivity(), db.getAllvmUsageUsageDetails(Integer.toString(login_id), subcriptionID));
			lv.setAdapter(adapter);
			
		
			 
			
			/*for(int i=0;i<hostedservie.size();i++){
				
				TextView myText = new TextView(getActivity());
				
				myText.setText(hostedservie.get(i).getServiceName());
				
				regioncount.addView(myText);

			}*/
		//	fillCountryTable();
		//getResourceUsage();
		
		/*List<LoginMaster> userdetails =db.getUserDetails();
		for(int i=0;i<userdetails.size();i++){
		 retreivedmode=userdetails.get(i).getOfflinemode();
		 Log.d("retreivedmode", retreivedmode);
		}

		
		 ConnectivityManager cm =(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
          netInfo = cm.getActiveNetworkInfo();
        
         if (netInfo != null && netInfo.isConnectedOrConnecting()){
        	 
        	  Toast.makeText(getActivity(), "  Internet Connected", Toast.LENGTH_LONG).show();
        	   new ResourceUsageDownload().execute();
         }else  {
        	 if (retreivedmode.equalsIgnoreCase("Y")) {
            	 
  	      	   Toast.makeText(getActivity(), "  Data for user", Toast.LENGTH_LONG).show();
  	      	   progressBar.setVisibility(View.INVISIBLE);
  	    	   	lv=(ListView) root.findViewById(R.id.cores_list_item);
  				adapter = new CoresAdapter(getActivity(), db.getAllResourceUsageDetails(Integer.toString(login_id)));
  				lv.setAdapter(adapter);
  				
  				lv1=(ListView) root.findViewById(R.id.cloud_list_item);
  				adapter1 = new CloudAdapter(getActivity(), db.getAllResourceUsageDetails(Integer.toString(login_id)));
  				lv1.setAdapter(adapter);
  				
  				lv2=(ListView) root.findViewById(R.id.storage_list_item);
  				adapter2 = new StorageAdapter(getActivity(), db.getAllResourceUsageDetails(Integer.toString(login_id)));
  				lv2.setAdapter(adapter2);
  	    	
  	       }else{
  	    	 progressBar.setVisibility(View.INVISIBLE);
			 Toast.makeText(getActivity(), "No internet connectivity Please try later", Toast.LENGTH_LONG).show(); 
  	       }
        	 
         }*/
  
		
		getActivity().getActionBar();
		return root;
	}

	

	

	
/*
	private void fillCountryTable() {
		// TODO Auto-generated method stub
		TableRow row;
		TextView t1, t2;
		//Converting to dip unit
		int dip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				(float) 1, getResources().getDisplayMetrics());
		for (int current = 0; current < hostedservie.size(); current++) {
			row = new TableRow(getActivity());

			t1 = new TextView(getActivity());
			t1.setTextColor(getResources().getColor(R.color.l_blue));
			t2 = new TextView(getActivity());
			t2.setTextColor(getResources().getColor(R.color.d_blue));
			Log.d("Location******************************", ""+hostedservie.get(current).getLocation());
			t1.setText(hostedservie.get(current).getLocation());
			t2.setText(hostedservie.get(current).getLocation());

			t1.setTypeface(null, 1);
			t2.setTypeface(null, 1);

			t1.setTextSize(15);
			t2.setTextSize(15);

			t1.setWidth(50 * dip);
			t2.setWidth(150 * dip);
			t1.setPadding(20*dip, 0, 0, 0);
			row.addView(t1);
			row.addView(t2);

			country_table.addView(row, new TableLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		}
	}*/
		
	







	private void loadpreferences() {
		// TODO Auto-generated method stub
		SharedPreferences mySharedPreferences = getActivity().getSharedPreferences(MYPREFS,mode);
		login_id = mySharedPreferences.getInt("login_id", 0);
		Log.d("login_id", ""+login_id);
		subId = mySharedPreferences.getString("SubcriptionID", "");
	}


	private void getSubscriptionDetails() {
		// TODO Auto-generated method stub
		 subcription =  db.getAllSubcription(Integer.toString(login_id),subId);
		 for(int i=0;i<subcription.size();i++){
			 	subcriptionID =subcription.get(i).getSubcriptionId();
			 	certificateKey =subcription.get(i).getCertificateKey();
			 	pKeyPassword =subcription.get(i).getPassword();
			 	rolename =subcription.get(i).getRole();
			 	
		 }
		 
		// certificateKey = "descfile.pfx";
	}


	/*@Override
	public void onClick(View v) {
		hideOthers(v);
	}

	private void hideThemAll() {
		if (openLayout == null)
			return;
		if (openLayout == panel1)
			panel1.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,500, panel1, true));
		if (openLayout == panel2)
			panel2.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
					500, panel2, true));
		if (openLayout == panel3)
			panel3.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
					500, panel3, true));
		
	}

	private void hideOthers(View layoutView) {
		{
			int v;
			if (layoutView.getId() == R.id.text1) {
				v = panel1.getVisibility();
				if (v != View.VISIBLE) {
					panel1.setVisibility(View.VISIBLE);
					Log.v("CZ", "height..." + panel1.getHeight());
				}

				// panel1.setVisibility(View.GONE);
				// Log.v("CZ","again height..." + panel1.getHeight());
				hideThemAll();
				if (v != View.VISIBLE) {
					panel1.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel1, true));
				}
			} else if (layoutView.getId() == R.id.text2) {
				v = panel2.getVisibility();
				hideThemAll();
				if (v != View.VISIBLE) {
					panel2.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel2, true));
				}
			} else if (layoutView.getId() == R.id.text3) {
				v = panel3.getVisibility();
				hideThemAll();
				if (v != View.VISIBLE) {
					panel3.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel3, true));
				}
			}

		}
	}

	public class ScaleAnimToHide extends ScaleAnimation {

		private View mView;

		private LayoutParams mLayoutParams;

		private int mMarginBottomFromY, mMarginBottomToY;

		private boolean mVanishAfter = false;

		public ScaleAnimToHide(float fromX, float toX, float fromY, float toY,
				int duration, View view, boolean vanishAfter) {
			super(fromX, toX, fromY, toY);
			setDuration(duration);
			openLayout = null;
			mView = view;
			mVanishAfter = vanishAfter;
			mLayoutParams = (LayoutParams) view.getLayoutParams();
			int height = mView.getHeight();
			mMarginBottomFromY = (int) (height * fromY)
					+ mLayoutParams.bottomMargin - height;
			mMarginBottomToY = (int) (0 - ((height * toY) + mLayoutParams.bottomMargin))
					- height;

			Log.v("CZ", "height..." + height + " , mMarginBottomFromY...."
					+ mMarginBottomFromY + " , mMarginBottomToY.."
					+ mMarginBottomToY);
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 1.0f) {
				int newMarginBottom = mMarginBottomFromY
						+ (int) ((mMarginBottomToY - mMarginBottomFromY) * interpolatedTime);
				mLayoutParams.setMargins(mLayoutParams.leftMargin,
						mLayoutParams.topMargin, mLayoutParams.rightMargin,
						newMarginBottom);
				mView.getParent().requestLayout();
				// Log.v("CZ","newMarginBottom..." + newMarginBottom +
				// " , mLayoutParams.topMargin..." + mLayoutParams.topMargin);
			} else if (mVanishAfter) {
				mView.setVisibility(View.GONE);
			}
		}
	}

	public class ScaleAnimToShow extends ScaleAnimation {

		private View mView;

		private LayoutParams mLayoutParams;

		private int mMarginBottomFromY, mMarginBottomToY;

		@SuppressWarnings("unused")
		private boolean mVanishAfter = false;

		public ScaleAnimToShow(float toX, float fromX, float toY, float fromY,
				int duration, View view, boolean vanishAfter) {
			super(fromX, toX, fromY, toY);
			openLayout = view;
			setDuration(duration);
			mView = view;
			mVanishAfter = vanishAfter;
			mLayoutParams = (LayoutParams) view.getLayoutParams();
			mView.setVisibility(View.VISIBLE);
			int height = mView.getHeight();
			// mMarginBottomFromY = (int) (height * fromY) +
			// mLayoutParams.bottomMargin + height;
			// mMarginBottomToY = (int) (0 - ((height * toY) +
			// mLayoutParams.bottomMargin)) + height;

			mMarginBottomFromY = 0;
			mMarginBottomToY = height;

			// Log.v("CZ",".................height..." + height +
			// " , mMarginBottomFromY...." + mMarginBottomFromY +
			// " , mMarginBottomToY.." +mMarginBottomToY);
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 1.0f) {
				int newMarginBottom = (int) ((mMarginBottomToY - mMarginBottomFromY) * interpolatedTime)
						- mMarginBottomToY;
				mLayoutParams.setMargins(mLayoutParams.leftMargin,
						mLayoutParams.topMargin, mLayoutParams.rightMargin,
						newMarginBottom);
				mView.getParent().requestLayout();
				// Log.v("CZ","newMarginBottom..." + newMarginBottom +
				// " , mLayoutParams.topMargin..." + mLayoutParams.topMargin);
			}
		}
		

	}*/



	/*public List<ResourceUsageService> getResourceUsage(){
		
try {
	
	
		
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy);
			// Open a secure connection.
			//String subscription_id = "fc7b7fb1-2b20-4fdc-addb-42abd01f1317";
			
			//String sUrl = "https://management.core.windows.net/"+subscription_id+"/services/hostedservices";
			//String sUrl = String.format("https://management.core.windows.net/%s/locations", subscription_id);
			//String sUrl = String.format("https://management.core.windows.net/%s/services/images", subscription_id);
			//String sUrl = String.format("https://management.core.windows.net/%s/services/hostedservices ", subscription_id);
			//String sUrl = String.format("https://management.core.windows.net/%s/services/hostedservices/%s/deploymentslots/%s", subscription_id, "AntuitWFSStg", "Production");
			//String sUrl = String.format("https://management.core.windows.net/%s/services/hostedservices/%s/deployments/%s", subscription_id, "AntuitWFSStg", "AntuitWFSStg");
			//usage service
			String sUrl = "https://management.core.windows.net/"+subcriptionID;

			URL url = new URL( sUrl );
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod( "GET" );
			//con.setRequestProperty("x-ms-version", "2009-10-01");
			con.setRequestProperty("x-ms-version", "2012-03-01");
			con.setRequestProperty("content-Type", "application/xml");

			// Set up the user authentication portion of the handshake with the private key
			//File pKeyFile = new File(this.getAssets().open("AzureCert.pfx"));
			//String pKeyPassword = "azure";
			//Log.d("pKeyPassword  :", pKeyPassword);
			
			 * setKeyManagerAlgorithm(helperParameters.getFirstValue(
            "keyManagerAlgorithm", true, System.getProperty(
                    "ssl.KeyManagerFactory.algorithm", "SunX509")));
			 
			
			
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			
		//	InputStream keyInput = getActivity().getAssets().open("AzureCert.pfx");
			InputStream keyInput = getActivity().openFileInput(certificateKey);
			
			try {
				

				keyInput = getActivity().openFileInput(certificateKey);

				if (keyInput != null) {
					input = new BufferedReader(new InputStreamReader(keyInput));
					while ((line = input.readLine()) != null) {
						buffer.append(line + eol);

					}
				
			} catch (Exception e) {

			}}

			keyStore.load(keyInput, pKeyPassword.toCharArray());
			keyInput.close();
			keyManagerFactory.init(keyStore, pKeyPassword.toCharArray());
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());
			SSLSocketFactory sockFact = context.getSocketFactory();
			con.setSSLSocketFactory( sockFact );

			con.connect();

			// Check for errors
			int responseCode = con.getResponseCode();
			InputStream inputStream;
			if (responseCode == HttpURLConnection.HTTP_OK) inputStream = con.getInputStream();
			else inputStream = con.getErrorStream();

			//Usage service parser starts hrere
			
			ResourceUsageServiceParser usageServiceParser = new ResourceUsageServiceParser();
			ResourceUsageService resourceUsageService = usageServiceParser.parse(inputStream); 
				Log.d("Usage  :", resourceUsageService.toString());
				
				
		  //Usage Service parser ends here
				
				
			
				resourceUsageService.getCurrentCoreCount();
				Log.d("currentcorecount", ""+resourceUsageService.getCurrentCoreCount());
				resource.getCurrentHostedServices();
				resource.getCurrentStorageAccounts();
				resource.getMaxCoreCount();
				resource.getMaxHostedServices();
				resource.getMaxStorageAccounts();
				
				
				ResourceUsageService resource = new ResourceUsageService();
				resource.setMaxStorageAccounts(resourceUsageService.getMaxStorageAccounts());
				Log.d("setMaxStorageAccounts  :", resourceUsageService.getMaxStorageAccounts());
				resource.setMaxHostedServices(resourceUsageService.getMaxHostedServices());
				resource.setCurrentCoreCount(resourceUsageService.getCurrentCoreCount());
				resource.setCurrentHostedServices(resourceUsageService.getCurrentHostedServices());
				resource.setCurrentStorageAccounts(resourceUsageService.getCurrentStorageAccounts());
				resource.setMaxCoreCount(resourceUsageService.getMaxCoreCount());
				resource.setSubcriptionId(subcriptionID);
				resource.setCreatedby(Integer.toString(login_id));
				db.createResourceUsageDetail(resource);
	         
				resourcedetails.add(resourceUsageService);
				
			
				
				
		  
			
			} catch (Exception e) { e.printStackTrace(); }

			

		return resourcedetails;
	}
	
	
	private class ResourceUsageDownload extends AsyncTask<String, Integer, String> {
		 
		@Override
		protected void onPreExecute() {
			//textView.setText("Hello !!!");
			
			progressBar.setVisibility(View.VISIBLE);
			
			super.onPreExecute();
		}
 
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
 
		}
 
		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
					InputStream content = execute.getEntity().getContent();
 
					BufferedReader buffer = new BufferedReader(new InputStreamReader(
							content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}
 
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
			
			getResourceUsage();
			return "";
		}
 
		@Override
		protected void onPostExecute(String result) {
			progressBar.setVisibility(View.INVISIBLE);
			 lv=(ListView) root.findViewById(R.id.cores_list_item);
		 		adapter = new CoresAdapter(getActivity(), resourcedetails);
		 		lv.setAdapter(adapter);
		 		
		 		lv1=(ListView) root.findViewById(R.id.cloud_list_item);
		 		adapter1 = new CloudAdapter(getActivity(), resourcedetails);
		 		lv1.setAdapter(adapter);
		 		
		 		lv2=(ListView) root.findViewById(R.id.storage_list_item);
		 		adapter2 = new StorageAdapter(getActivity(), resourcedetails);
		 		lv2.setAdapter(adapter2);
				
			
		}
	}
	
	 public class ResourceUsageDownload extends AsyncTask<Void, String, Void> implements OnCancelListener {
	    	ProgressHUD mProgressHUD;    	

	    	@Override
	    	protected void onPreExecute() {
	        	mProgressHUD = ProgressHUD.show(getActivity(),"Connecting", true,true,this);
	    		super.onPreExecute();
	    	}
	    	
			@Override
			protected Void doInBackground(Void... params) {
				try {
					publishProgress("Connecting");
					Thread.sleep(2000);
					publishProgress("Downloading");
					Thread.sleep(5000);
					getResourceUsage();
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
				progressBar.setVisibility(View.INVISIBLE);
				mProgressHUD.dismiss();
				 lv=(ListView) root.findViewById(R.id.cores_list_item);
			 		adapter = new CoresAdapter(getActivity(), resourcedetails);
			 		lv.setAdapter(adapter);
			 		
			 		lv1=(ListView) root.findViewById(R.id.cloud_list_item);
			 		adapter1 = new CloudAdapter(getActivity(), resourcedetails);
			 		lv1.setAdapter(adapter);
			 		
			 		lv2=(ListView) root.findViewById(R.id.storage_list_item);
			 		adapter2 = new StorageAdapter(getActivity(), resourcedetails);
			 		lv2.setAdapter(adapter2);
				super.onPostExecute(result);
			}

			@Override
			public void onCancel(DialogInterface dialog) {
				this.cancel(true);
				mProgressHUD.dismiss();
			}		
	    }
	
	*/
	
	
	
}
