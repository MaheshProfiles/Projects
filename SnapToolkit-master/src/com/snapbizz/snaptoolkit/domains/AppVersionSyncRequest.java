package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;


public class AppVersionSyncRequest extends Request{

	@SerializedName("appVersionList")
	private List<AppVersion> appVersionList;

	public List<AppVersion> getAppVersionList() {
		return appVersionList;
	}

	public void setAppVersionList(List<AppVersion> appVersionList) {
		this.appVersionList = appVersionList;
	}
	
}
