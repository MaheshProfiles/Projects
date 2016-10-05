package com.snapbizz.snaptoolkit.utils;

public enum OrderType {

	PENDING("pending"), 
	COMPLETED("completed"), 
	RECEIVED("received");
	
	private String orderType;

	private OrderType(String orderType){
		this.orderType = orderType;
	}
	
	public String getOrderType(){
		return this.orderType;
	}
	
	public static OrderType getOrderEnum(String OrderValue){
		for (OrderType orderType : OrderType.values()) {
			if(orderType.orderType.equals(OrderValue))
				return orderType;
		}
		return null;
	}
}
