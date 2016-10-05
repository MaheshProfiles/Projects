package com.snapbizz.snaptoolkit.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snapbizz.snaptoolkit.domains.ResponseContainer;


public class JsonParser {

	public static ResponseContainer parseJson(InputStream responseStream, Class<? extends ResponseContainer> responsibleClass) throws IOException {
		if(responseStream == null)
			return null;
		ResponseContainer mResponse = new ResponseContainer();
		try {
			String responseString = SnapCommonUtils.convertStreamToString(responseStream);
			if(responseString == null || responseString.trim().startsWith("<!DOCTYPE") ||
				responseString.trim().startsWith("<html>")) {
				if(responseString != null)
					Log.e("Response", responseString);
				return null;
			}
			Log.e("Response", responseString);
			//TestFlight.log("Response "+responseString);
			Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
			mResponse = gson.fromJson(responseString, responsibleClass);
			responseStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return mResponse;
	}

}
