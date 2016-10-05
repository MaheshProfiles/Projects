package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RetrievePaymentResponseContainer extends ResponseContainer {

	@SerializedName("paymentsList")
	private List<Payments> paymentList;

	public List<Payments> getPaymentList() {
		return paymentList;
	}

	public void setPaymentList(List<Payments> paymentList) {
		this.paymentList = paymentList;
	}
	
	
}
