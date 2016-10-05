package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RetrieveInventoryResponseContainer  extends ResponseContainer{

	@SerializedName("inventoryList")
	private List<InventorySku> inventoryList;

	public List<InventorySku> getInventoryList() {
		return inventoryList;
	}

	public void setInventoryList(List<InventorySku> inventoryList) {
		this.inventoryList = inventoryList;
	}
	
}
