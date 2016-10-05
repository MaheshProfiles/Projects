package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;

public class AppUpdateResponse {
	
	@SerializedName("billing")
	private BillingUpdateResponse billingResponse;
	
	@SerializedName("stock")
	private StockUpdateResponse stockResponse;
	
	@SerializedName("push_offers")
	private PushOfferUpdateResponse pushOfferResponse;

	public BillingUpdateResponse getBillingResponse() {
		return billingResponse;
	}

	public void setBillingResponse(BillingUpdateResponse billingResponse) {
		this.billingResponse = billingResponse;
	}

	public StockUpdateResponse getStockResponse() {
		return stockResponse;
	}

	public void setStockResponse(StockUpdateResponse stockResponse) {
		this.stockResponse = stockResponse;
	}

	public PushOfferUpdateResponse getPushOfferResponse() {
		return pushOfferResponse;
	}

	public void setPushOfferResponse(PushOfferUpdateResponse pushOfferResponse) {
		this.pushOfferResponse = pushOfferResponse;
	}
	
	

}
