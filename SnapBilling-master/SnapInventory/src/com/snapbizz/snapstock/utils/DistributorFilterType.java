package com.snapbizz.snapstock.utils;

public enum DistributorFilterType {
	
	DISTRIBUTOR_FILTER("agency_name"), 
	COMPANY_FILTER("company_name"), 
	BRAND_FILTER("brand_name");
	
	private String distributorFilterType;

	private DistributorFilterType (String distributorFilterType){
		this.distributorFilterType = distributorFilterType;
	}

	public String getDistributorFilterTypeValue(){
		return this.distributorFilterType;
	}

	public static DistributorFilterType getEnum(String val) {
		for(DistributorFilterType distributorFilterType : DistributorFilterType.values()) {
			if(distributorFilterType.distributorFilterType.equals(val)){
				return distributorFilterType;
			}
		}
		return null;
	}
	
}
