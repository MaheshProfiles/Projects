package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DistributorSyncRequest extends Request {
	@SerializedName("distributorList")
	private List<Distributor> distributorList;

	public List<Distributor> getDistributorList() {
		return distributorList;
	}

	public void setDistributorList(List<Distributor> distributorList) {
		this.distributorList = distributorList;
	}

	
}
