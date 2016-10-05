package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DistributorBrandMapSyncRequest extends Request {

	@SerializedName("distributorBrandMapList")
	private List<DistributorBrandMap> distributorBrandMapList;

	public List<DistributorBrandMap> getDistributorBrandMapList() {
		return distributorBrandMapList;
	}

	public void setDistributorBrandMapList(
			List<DistributorBrandMap> distributorBrandMapList) {
		this.distributorBrandMapList = distributorBrandMapList;
	}

}
