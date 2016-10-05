package com.snapbizz.snaptoolkit.services;

import java.io.InputStream;

import org.apache.http.conn.ConnectTimeoutException;

import android.content.Context;
import android.content.Intent;

import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.domains.StreamResponseContainer;
import com.snapbizz.snaptoolkit.utils.JsonParser;
import com.snapbizz.snaptoolkit.utils.ResponseCodes;
import com.snapbizz.snaptoolkit.utils.ResponseFormat;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;


public class RestCall{

	RestClient restClient;

	public ResponseContainer execute(ServiceRequest serviceRequest, Context context) throws ConnectTimeoutException, Exception {
		ResponseContainer responseContainer = null;
		try {
			restClient = new RestClient(serviceRequest, context);
			restClient.execute();
			InputStream is = restClient.getInputStream();
			if(serviceRequest.getResponseFormat().ordinal() == ResponseFormat.JSON.ordinal()) {
				responseContainer = JsonParser.parseJson(is, serviceRequest.getResponsibleClass());
				if(responseContainer == null)
					responseContainer = new ResponseContainer();
			} else {
				StreamResponseContainer streamResponseContainer = new StreamResponseContainer();
				streamResponseContainer.setInputStream(is);
				streamResponseContainer.setResponseCode(ResponseCodes.OK.getResponseValue());
				responseContainer = streamResponseContainer;
			}
			responseContainer.setRequestCode(serviceRequest.getRequestCode());
		} catch(ConnectTimeoutException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return responseContainer;
	}

	public void abortRestCall() {
		if(restClient != null)
			restClient.abortHttpClient();
	}

}
