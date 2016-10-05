package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RetrieveTransactionDetailsResponseContainer extends ResponseContainer {
	@SerializedName("billItemList")
	private List<BillItem> billItemList;

	public List<BillItem> getBillItemList() {
		return billItemList;
	}

	public void setBillItemList(List<BillItem> billItemList) {
		this.billItemList = billItemList;
	}

}
