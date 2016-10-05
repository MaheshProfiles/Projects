 package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RetrieveOrderDetailsResponseContainer extends ResponseContainer {
	@SerializedName("orderDetailsList")
	private List<OrderDetails> orderDetailsList;

	public List<OrderDetails> getOrderDetailsList() {
		return orderDetailsList;
	}

	public void setOrderDetailsList(List<OrderDetails> orderDetailsList) {
		this.orderDetailsList = orderDetailsList;
	}
	
	
}
