package com.snapbizz.snaptoolkit.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.util.Log;

import com.snapbizz.snaptoolkit.domains.HttpDeleteWithBody;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class RestClient {

	private final String TAG = getClass().getName();
	private InputStream reponseStream;
	private ServiceRequest request;
	private HttpParams httpParameters;
	private AbortableHttpRequest httpRequest;
	private Context context;

	public RestClient(ServiceRequest request, Context context) {
		this.context = context;
		if(request.isSecureConnectionRequest()) {
			request.setUrl(SnapToolkitConstants.BASE_SECURE_URL);
		}
	
		this.request=request;
		httpParameters = new BasicHttpParams();
		httpParameters.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, SnapToolkitConstants.MAX_CONNECTIONS);
		httpParameters.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE,new ConnPerRouteBean(SnapToolkitConstants.MAX_CONNECTIONS_PERROUTE));
		httpParameters.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE,false);
		httpParameters.setLongParameter(ConnManagerParams.TIMEOUT, request.getConnectionTImeout());
		HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParameters, "utf8");
		HttpConnectionParams.setConnectionTimeout(httpParameters, request.getConnectionTImeout());
		HttpConnectionParams.setSoTimeout(httpParameters, request.getConnectionTImeout()+20000);
        HttpConnectionParams.setSocketBufferSize(httpParameters, 16384);
		ConnManagerParams.setTimeout(httpParameters, request.getConnectionTImeout());
	}

	public void execute() throws ConnectTimeoutException {
		switch (this.request.getRequestMethod()) {
		case POST:
			HttpEntityEnclosingRequestBase httpRequest = new HttpPost(this.request.getUrl()+this.request.getPath()+this.request.getMethod()+this.request.getRequestString());
			prepareCall(httpRequest);
			break;
		case DELETE:
			HttpDeleteWithBody httpDeleteRequest = new HttpDeleteWithBody(this.request.getUrl()+this.request.getPath()+this.request.getMethod()+this.request.getRequestString());
			prepareCall(httpDeleteRequest);
			break;
		case PUT:
			HttpEntityEnclosingRequestBase httpPutRequest = new HttpPut(this.request.getUrl()+this.request.getPath()+this.request.getMethod()+this.request.getRequestString());
			prepareCall(httpPutRequest);
			break;
		case GET:
			HttpGet httpGetRequest = new HttpGet(this.request.getUrl()+this.request.getPath()+this.request.getMethod()+this.request.getRequestString());
			prepareCall(httpGetRequest);
			break;
		default:
			break;
		}
	}

	private void prepareCall(HttpEntityEnclosingRequestBase httpRequest) throws ConnectTimeoutException {
		try {
			this.httpRequest = httpRequest;
			if(this.request.getMultiPartEntity() != null) {
				httpRequest.setEntity(this.request.getMultiPartEntity());
			} else if(this.request.getUrlEncodedEntity() != null) {
				httpRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
				httpRequest.setEntity(this.request.getUrlEncodedEntity());
			} else {
				StringEntity string = new StringEntity(this.request.getPayload(), "utf-8");
				httpRequest.addHeader("Content-Type", "application/json");
				httpRequest.setEntity(string);
			}
			HttpClient httpClient = getSecuredHttpClient();
			((AbstractHttpClient) httpClient).setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			setInputStream(httpResponse);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(ConnectTimeoutException e) {
			Log.d("timeout", "timeout occured");
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void prepareCall(HttpGet httpRequest) throws ConnectTimeoutException {
		try {
			this.httpRequest = httpRequest;
			httpRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
			HttpClient httpClient = getSecuredHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			setInputStream(httpResponse);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(ConnectTimeoutException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	private void setInputStream(HttpResponse httpResponse) {
		HttpEntity entity = httpResponse.getEntity();
		if (entity != null) {
			InputStream instream;
			try {
				instream = entity.getContent();
				reponseStream = instream;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}

	public void abortHttpClient() {
		if(httpRequest != null)
			httpRequest.abort();
	}

	private HttpClient getSecuredHttpClient() {
			return new  DefaultHttpClient(httpParameters);
	}

	public InputStream getInputStream() {
		return reponseStream;
	}
}
