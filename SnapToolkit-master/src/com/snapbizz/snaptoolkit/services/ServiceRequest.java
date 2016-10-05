package com.snapbizz.snaptoolkit.services;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.snapbizz.snaptoolkit.domains.Request;
import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.utils.RequestCodes;
import com.snapbizz.snaptoolkit.utils.RequestFormat;
import com.snapbizz.snaptoolkit.utils.RequestMethod;
import com.snapbizz.snaptoolkit.utils.ResponseFormat;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.TransformableFactory;
import com.snapbizz.snaptoolkit.utils.TransformableFactory.GsonType;

public class ServiceRequest {

	private String requestString = "";
	private String payload;
	private UrlEncodedFormEntity urlEncodedEntity;
	private MultipartEntity multiPartEntity;
	private String url;
	private Class<? extends ResponseContainer> responsibleClass;
	private RequestMethod requestMethod;
	private RequestCodes requestCode;
	private boolean isSecureConnectionRequest;
	private String path="";
	private String method="";
	private int ConnectionTimeout = SnapToolkitConstants.MAX_CONNECTION_TIMEOUT;
	private ResponseFormat responseFormat = ResponseFormat.JSON;

	public ServiceRequest(Request request, Context context) {		
		url = SnapToolkitConstants.BASE_URL;
		requestMethod = request.getRequestMethod();

		//		HashMap<String, String> requestParamMap = request.getRequestParams();
		//		String appVersion = "";
		//		try {
		//			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		//			appVersion = pInfo.versionCode+"";
		//		} catch (NameNotFoundException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		if(appVersion != null)
		//			requestParamMap.put("appVersion", appVersion);
		//		requestParamMap.put("deviceId", SnapCommonUtils.getDeviceID(context));
		//		requestParamMap.put("osVersion", android.os.Build.VERSION.SDK_INT+"");

		if(requestMethod == RequestMethod.GET) {
            requestString = SnapCommonUtils.buildEncodedQueryString(request.getRequestParams());
            Log.e("Request_Time_dwonload_time", "request = "+requestString);
        } else {
			if(request.getRequestFormat().equals(RequestFormat.JSON)) {
				try {
					payload = TransformableFactory.tranformObjectToJson(request, GsonType.DEFAULT);
					Log.d("payload", payload);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(request.getRequestFormat().equals(RequestFormat.MAP)) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				Iterator it = request.getRequestParams().entrySet().iterator();
				String requestString = "";
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry)it.next();
					nameValuePairs.add(new BasicNameValuePair(pairs.getKey().toString(), pairs.getValue().toString()));
					requestString += pairs.getKey()+"="+pairs.getValue()+"&";
					it.remove();
				}
				try {
					urlEncodedEntity = new UrlEncodedFormEntity(nameValuePairs);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("Request", requestString);
			} else if(request.getRequestFormat().equals(RequestFormat.MULTI_PART)) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				request.getBitmap().compress(CompressFormat.JPEG, 100, bos);
				byte[] data = bos.toByteArray();
				ByteArrayBody bab = new ByteArrayBody(data, "image");
				multiPartEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				multiPartEntity.addPart("image", bab);
				Iterator it = request.getRequestParams().entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry)it.next();
					try {
						multiPartEntity.addPart(pairs.getKey().toString(), new StringBody(pairs.getValue().toString()));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					it.remove();
				}
				Log.d("Request", " image "+bab.getContentLength());
			}
		} 
	}

	public ResponseFormat getResponseFormat() {
		return responseFormat;
	}
	
	public void setResponseFormat(ResponseFormat responseFormat) {
		this.responseFormat = responseFormat;
	}
	
	public MultipartEntity getMultiPartEntity() {
		return multiPartEntity;
	}

	public void setMultiPartEntity(MultipartEntity multiPartEntity) {
		this.multiPartEntity = multiPartEntity;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	public int getConnectionTImeout() {
		return ConnectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		ConnectionTimeout = connectionTimeout;
	}

	public UrlEncodedFormEntity getUrlEncodedEntity() {
		return urlEncodedEntity;
	}

	public void setUrlEncodedEntity(UrlEncodedFormEntity urlEncodedEntity) {
		this.urlEncodedEntity = urlEncodedEntity;
	}

	public boolean isSecureConnectionRequest() {
		return isSecureConnectionRequest;
	}

	public void setSecureConnectionRequest(boolean isSecureConnectionRequest) {
		this.isSecureConnectionRequest = isSecureConnectionRequest;
	}

	public String getRequestString() {
		return requestString;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public void setRequestString(String requestString) {
		this.requestString = requestString;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Class<? extends ResponseContainer> getResponsibleClass() {
		return responsibleClass;
	}

	public void setResponsibleClass(Class<? extends ResponseContainer> responsibleClass) {
		this.responsibleClass = responsibleClass;
	}

	public RequestMethod getRequestMethod() {
		return requestMethod;
	}

	public RequestCodes getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(RequestCodes requestCode) {
		this.requestCode = requestCode;
	}

}
