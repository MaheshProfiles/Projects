package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;

public class RegistraionResponse extends ResponseContainer {
	
	@SerializedName("response")
	private Response response;
	
	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
	
	public String getApiKey() {
		return response.apiKey;
	}

	public void setApiKey(String apiKey) {
		response.apiKey = apiKey;
	}

	public String getStoreId() {
		return response.storeId;
	}

	public void setStoreId(String storeId) {
		response.storeId = storeId;
	}

	public String getRetailerId() {
		return response.retailerId;
	}

	public void setRetailerId(String retailerId) {
		response.retailerId = retailerId;
	}
	
	public void setAccessToken(String accessToken) {
		response.accessToken = accessToken;
	}
	
	public String getAccessToken() {
		return response.accessToken;
	}
	
	private class Response {
		@SerializedName("api_key")
		private String apiKey;
		@SerializedName("store_id")
		private String storeId;
		@SerializedName("retailer_id")
		private String retailerId;
		@SerializedName("access_token")
		private String accessToken;
	}

}