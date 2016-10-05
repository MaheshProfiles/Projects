package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class OrderSyncRequest extends Request {
	@SerializedName("orderList")
	private List<Order> orderList;

	
	public List<Order> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<Order> orderList) {
		this.orderList = orderList;
	}
	
}
