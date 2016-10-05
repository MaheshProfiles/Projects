package com.sysfore.azure.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import android.util.Log;

import com.sysfore.azure.model.BillingPojo;

public class BillingService {
	
	public Map<String,List<BillingPojo>> getBillDetails(String enrollment,String token,String month,String year) {
		
		Map<String, List<BillingPojo>> billingMap = new HashMap<String, List<BillingPojo>>();
		
		try {
			System.out.println("-----------------------------------------------------------------");
			System.out.println("-----------------------------------------------------------------");
			//String subscription_id = "fc7b7fb1-2b20-4fdc-addb-42abd01f1317";
			//String enrollment = "79593733";
			//String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Il92ZHZieDJYRUkyb2l5N09abVpVelZGelM2TSJ9.eyJpc3MiOiJlYS5taWNyb3NvZnRhenVyZS5jb20iLCJhdWQiOiJjbGllbnQuZWEubWljcm9zb2Z0YXp1cmUuY29tIiwibmJmIjoxNDEwMzMyMDA2LCJleHAiOjE0MjU5NzA0MDYsIkVucm9sbG1lbnROdW1iZXIiOiI3OTU5MzczMyIsIklkIjoiYjFhNzg2ZWQtNDRjMS00YTgzLWE3OTgtZmZlNzM4ZjRiYTIxIiwiUmVwb3J0VmlldyI6IkluZGlyZWN0RW50ZXJwcmlzZSIsIlBhcnRuZXJJZCI6IiJ9.KWbjky_ekLicwn8_zvMjagNxCao8yGfs9jaSOPnaQtJMRQRFFOaPptCyb0bCIfpDe-9-M1VaUtx2LUOq3jvr9obdBmmaHOdaCr_-ITDHqhYYHPROI3-lTx3L8v4aDu2tbLMQRbIXxH_ZroZpt08QgJOBmob56AFUtuqM4Qo-wvfg91OqP9Waf9Dl_fezX4dUViqAnZxiQi-DuVs3ggiE2GyeMs_o8GmINidJQSREznqWnT3oVDSsOD7zPJdcho3Dvi8SlejhiBi7MUAB6GpJbiWACtHLUmHS0Cwwi6i8iyprDnPVrmaGJ3Yu9SpYEyGq-K7MfeRvGVBEWvGqkrKHqw";
			String sUrl = String.format("https://ea.windowsazure.com/rest/%s/usage-report?month=%s&type=%s",enrollment, month+"-"+year, "detail");
			URL url = new URL( sUrl );
			Log.d("billing URL:", ""+sUrl);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod( "GET" );
			con.setRequestProperty("authorization", "bearer " + token);
			con.setRequestProperty("api-version", "1.0");



			con.connect();


			int responseCode = con.getResponseCode();
			InputStream inputStream;
			
			if (responseCode == HttpURLConnection.HTTP_OK) inputStream = con.getInputStream();
			else inputStream = con.getErrorStream();


			BufferedReader reader;
			String csvSplitBy = ",";
			String line = null;
			//PrintWriter printWriter = new PrintWriter ("D:/projects/file/file.csv");
			reader = new BufferedReader(new InputStreamReader(inputStream));
			while((line = reader.readLine()) != null){ 
			   BillingPojo billingPojo = new BillingPojo();	
			   String lineString[] = line.split(csvSplitBy);
			   //printWriter.println(line);
			   if(lineString.length > 10) {
				   billingPojo.setSubscriptionGUID(lineString[4]);
				   String productFormatter = lineString[10].replace("\"", "");
				   billingPojo.setProduct(productFormatter);
				   billingPojo.setServiceResource(lineString[15]);
				   
				   String formatted = lineString[16].replace("\"", "");
				   if(formatted.contains("00s")) {
					   formatted = lineString[17].replace("\"", "");
					   billingPojo.setResourceQtyConsumed(formatted);
					   billingPojo.setServiceSubRegion(lineString[18]);
					   billingPojo.setComponent(lineString[20]);
					   billingPojo.setServiceInfo(lineString[19]);
				   }else{ 
					   billingPojo.setResourceQtyConsumed(formatted);
					   billingPojo.setServiceSubRegion(lineString[17]);
					   billingPojo.setComponent(lineString[19]);
					   billingPojo.setServiceInfo(lineString[18]);
				   }
				   
				   //System.out.println(billingPojo.getResourceQtyConsumed());
				   
				   
				   List<BillingPojo> extractList = billingMap.get(lineString[4]);
				   if(extractList != null && extractList.size() != 0) {
					   extractList.add(billingPojo);
					   
				   }else {
					   if(!lineString[4].equals("\"SubscriptionGuid\"")) {
						   extractList = new ArrayList<BillingPojo>();
						   extractList.add(billingPojo);
						   billingMap.put(lineString[4],extractList);
					   }
				   }
				   
				   //billingList.add(billingPojo);
				   
			   }
				
			}
			
			inputStream.close();
			//printWriter.close();
			} catch (Exception e) { e.printStackTrace(); }
			
		
		return billingMap;
	}

}
