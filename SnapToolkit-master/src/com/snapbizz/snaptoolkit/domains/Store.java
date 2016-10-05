package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;

public class Store {
	
	@SerializedName("name")
	private String storeName;
	@SerializedName("tin")
	private String storeTinNumber;
	@SerializedName("address1")
	private String storeAddress;
	@SerializedName("phone")
	private String storePhoneNumber;
	@SerializedName("city")
	private String city;
	@SerializedName("state")
	private String state;
	@SerializedName("zip")
	private String zipCode;
	@SerializedName("storeId")
	private String storeId;
	@SerializedName("storeAuthToken")
	private String storeAuthToken;
	@SerializedName("storeApiKey")
	private String storeApiKey;
	private String storeStateId;

	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getStoreTinNumber() {
		return storeTinNumber;
	}
	public void setStoreTinNumber(String storeTinNumber) {
		this.storeTinNumber = storeTinNumber;
	}
	public String getStoreAddress() {
		return storeAddress;
	}
	public void setStoreAddress(String storeAddress) {
		this.storeAddress = storeAddress;
	}
	public String getStorePhoneNumber() {
		return storePhoneNumber;
	}
	public void setStorePhoneNumber(String storePhoneNumber) {
		this.storePhoneNumber = storePhoneNumber;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getStoreAuthToken() {
		return storeAuthToken;
	}
	public void setStoreAuthToken(String storeAuthToken) {
		this.storeAuthToken = storeAuthToken;
	}
	public String getStoreApiKey() {
		return storeAuthToken;
	}
	public void setStoreApiKey(String storeApiKey) {
		this.storeApiKey = storeApiKey;
	}
	public String getStoreStateId() {
		return storeStateId;
	}
	public void setStoreStateId(String storeStateId) {
		this.storeStateId = storeStateId;
	}           
	
	
	
}
