package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;

public class AppUpdateResponseContainer extends ResponseContainer{

	@SerializedName("response")
	private AppUpdateResponse updateResponse;

	public AppUpdateResponse getUpdateResponse() {
		return updateResponse;
	}

	public void setUpdateResponse(AppUpdateResponse updateResponse) {
		this.updateResponse = updateResponse;
	}
	
}
