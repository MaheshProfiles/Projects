package com.snapbizz.snaptoolkit.utils;


public enum ResponseCodes {

	OK(200),
	BLOCKED(375),
	CONNECTION_TIMEOUT(100),
	SERVER_NOT_FOUND(204),
	NOT_FOUND(404),
	INVALID_ACCESS_TOKEN(401),
	NOT_AUTHENTIC(406),
	DUPLICATE(301);
	
	private int value;

	private ResponseCodes (int value){
		this.value = value;
	}

	public int getResponseValue(){
		return this.value;
	}

	public static ResponseCodes getEnum(int val){
		for(ResponseCodes responseCode : ResponseCodes.values()){
			if(responseCode.value == val){
				return responseCode;
			}
		}
		return null;
	}
	
}
