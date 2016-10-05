package com.sysfore.azure.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import android.os.StrictMode;
import android.util.Log;

public class AzureServiceCall {
	
	public  static InputStream getAzureData(String sUrl,InputStream keyInput,String pKeyPassword) {
		InputStream inputStream = null;
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy); 
			URL url = new URL( sUrl );
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod( "GET" );
			//con.setRequestProperty("x-ms-version", "2009-10-01");
			con.setRequestProperty("x-ms-version", "2014-05-01");
			con.setRequestProperty("content-Type", "application/xml");

			// Set up the user authentication portion of the handshake with the private key
			//File pKeyFile = new File(this.getAssets().open("AzureCert.pfx"));
			//String pKeyPassword = "azure";
			/*
			 * setKeyManagerAlgorithm(helperParameters.getFirstValue(
            "keyManagerAlgorithm", true, System.getProperty(
                    "ssl.KeyManagerFactory.algorithm", "SunX509")));
			 */
			
			
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			
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
			
			if (responseCode == HttpURLConnection.HTTP_OK) inputStream = con.getInputStream();
			else inputStream = con.getErrorStream();			
			return inputStream;
		}catch(Exception e) {
			Log.d("Exception in Web Return  ", e.toString());
		}
		
		return inputStream;
	}

}
