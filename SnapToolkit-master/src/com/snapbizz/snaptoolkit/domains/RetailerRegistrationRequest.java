package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;


public class RetailerRegistrationRequest  extends Request {

	@SerializedName("retailer")
	private Retailer retailer;

	public RetailerRegistrationRequest(Retailer retailer) {
		this.retailer = retailer;
	}
	
	public Retailer getRetailer() {
		return retailer;
	}

	public void setRetailer(Retailer retailer) {
		this.retailer = retailer;
	}



}
