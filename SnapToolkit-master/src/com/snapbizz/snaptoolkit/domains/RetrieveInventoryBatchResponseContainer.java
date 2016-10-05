package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RetrieveInventoryBatchResponseContainer extends ResponseContainer {
	
	@SerializedName("inventoryBatchList")
	private List<InventoryBatch>inventoryBatchList;

	public List<InventoryBatch> getInventoryBatchList() {
		return inventoryBatchList;
	}

	public void setInventoryBatchList(List<InventoryBatch> inventoryBatchList) {
		this.inventoryBatchList = inventoryBatchList;
	}

}
