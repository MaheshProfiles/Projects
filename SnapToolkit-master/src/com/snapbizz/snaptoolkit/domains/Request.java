package com.snapbizz.snaptoolkit.domains;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.snapbizz.snaptoolkit.interfaces.Transformable;
import com.snapbizz.snaptoolkit.utils.RequestFormat;
import com.snapbizz.snaptoolkit.utils.RequestMethod;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class Request implements Transformable, Parcelable {

	@SerializedName(SnapToolkitConstants.STORE_AUTH_KEY)
	private String accessToken;
	@SerializedName(SnapToolkitConstants.DEVICE_ID)
	private String deviceId;
	@SerializedName(SnapToolkitConstants.STORE_ID)
	private String storeId;
	private Bitmap bitmap;
	
	private transient HashMap<String, String> requestParams;

	private transient RequestMethod requestMethod = RequestMethod.GET;

	private transient RequestFormat requestFormat = RequestFormat.MAP;

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public RequestFormat getRequestFormat() {
		return requestFormat;
	}

	public void setRequestFormat(RequestFormat requestFormat) {
		this.requestFormat = requestFormat;
	}

	public void setRequestMethod(RequestMethod requestMethod) {
		this.requestMethod = requestMethod;
	}

	public RequestMethod getRequestMethod() {
		return requestMethod;
	}

	public HashMap<String, String> getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(HashMap<String, String> requestParams) {
		if(this.requestParams == null)
			this.requestParams = new HashMap<String, String>();
		else
			this.requestParams.clear();
		this.requestParams.putAll(requestParams);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int parcelableFlags) {
		final int N = requestParams.size();
		dest.writeInt(N);
		if (N > 0) {
			for (Map.Entry<String, String> entry : requestParams.entrySet()) {
				dest.writeString(entry.getKey());
				dest.writeString(entry.getValue());
			}
		}
		dest.writeSerializable(requestFormat);
	}

	public static final Creator<Request> CREATOR = new Creator<Request>() {
		public Request createFromParcel(Parcel source) {
			return new Request(source);
		}
		public Request[] newArray(int size) {
			return new Request[size];
		}
	};

	private Request(Parcel source) {
		requestParams = new HashMap<String, String>();
		final int N = source.readInt();

		for (int i = 0; i < N; i++) {
			String key = source.readString();
			String val = source.readString();
			requestParams.put(key, val);
		}
		requestFormat = (RequestFormat) source.readSerializable();
	}

	public Request() {
		// TODO Auto-generated constructor stub
	}

}
