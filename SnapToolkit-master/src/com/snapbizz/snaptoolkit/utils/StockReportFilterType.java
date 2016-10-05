package com.snapbizz.snaptoolkit.utils;


public enum StockReportFilterType {

	DISTRIBUTOR("Distributor"), 
	CATEGORY("Category");
	
	private String stockReportFilterType;

	private StockReportFilterType (String stockReportFilterType){
		this.stockReportFilterType = stockReportFilterType;
	}

	public String getStockReportFilterTypeValue(){
		return this.stockReportFilterType;
	}

	public static StockReportFilterType getEnum(String val) {
		for(StockReportFilterType stockReportFilterType : StockReportFilterType.values()) {
			if(stockReportFilterType.stockReportFilterType.equals(val)){
				return stockReportFilterType;
			}
		}
		return null;
	}
	
}
