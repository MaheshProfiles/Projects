package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;

public class Retailer {
	
	private String retailerId;
	@SerializedName("name")
	private String retailerName;
	@SerializedName("phone")
	private String retailerPhoneNumber;
	@SerializedName("email")
	private String retailerEmailId;
	@SerializedName("store_attributes")
	private Store store;
	@SerializedName("salesperson_number")
	private String salesPersonNumber;
	
	public Retailer(String retailerName, String retailerPhoneNumber, String retailerEmailId, String salesmanPhonerNumber, Store store) {
		this.retailerName = retailerName;
		this.retailerEmailId = retailerEmailId;
		this.retailerPhoneNumber = retailerPhoneNumber;
		this.salesPersonNumber = salesmanPhonerNumber;
		this.store = store;
	}

	public String getRetailerId() {
		return retailerId;
	}

	public void setRetailerId(String retailerId) {
		this.retailerId = retailerId;
	}
	public String getSalesPersonNumber() {
		return salesPersonNumber;
	}

	public void setSalesPersonNumber(String salesPersonNumber) {
		this.salesPersonNumber = salesPersonNumber;
	}
	public String getRetailerName() {
		return retailerName;
	}
	public void setRetailerName(String retailerName) {
		this.retailerName = retailerName;
	}
	public String getRetailerPhoneNumber() {
		return retailerPhoneNumber;
	}
	public void setRetailerPhoneNumber(String retailerPhoneNumber) {
		this.retailerPhoneNumber = retailerPhoneNumber;
	}
	public String getRetailerEmailId() {
		return retailerEmailId;
	}
	public void setRetailerEmailId(String retailerEmailId) {
		this.retailerEmailId = retailerEmailId;
	}
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}

	

}
