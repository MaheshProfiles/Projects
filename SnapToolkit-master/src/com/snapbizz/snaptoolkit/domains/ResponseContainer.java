package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;
import com.snapbizz.snaptoolkit.utils.RequestCodes;

public class ResponseContainer {
	
	@SerializedName("responseCode")
	private int responseCode;
	@SerializedName("responseMessage")
	private String responseMessage;
	protected transient RequestCodes requestCode;
	private Object returnObj;
	
	public RequestCodes getRequestCode() {
		return requestCode;
	}
	public void setRequestCode(RequestCodes requestCode) {
		this.requestCode = requestCode;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public Object getReturnObj() {
		return returnObj;
	}
	public void setReturnObj(Object returnObj) {
		this.returnObj = returnObj;
	}
	
	
}
