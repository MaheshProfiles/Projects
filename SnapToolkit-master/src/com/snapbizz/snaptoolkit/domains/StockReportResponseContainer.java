
package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class StockReportResponseContainer extends ResponseContainer {
	
	@SerializedName("stockReportList")
	List<StockReport> stockReportList;

	public List<StockReport> getStockReportList() {
		return stockReportList;
	}

	public void setStockReportList(List<StockReport> stockReportList) {
		this.stockReportList = stockReportList;
	}
	
}
