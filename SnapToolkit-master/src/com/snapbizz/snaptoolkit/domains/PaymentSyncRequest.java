package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class PaymentSyncRequest extends Request {
	@SerializedName("paymentsList")
	private List<Payments> paymentsList;

	public List<Payments> getPaymentsList() {
		return paymentsList;
	}

	public void setPaymentsList(List<Payments> paymentsList) {
		this.paymentsList = paymentsList;
	}

}
