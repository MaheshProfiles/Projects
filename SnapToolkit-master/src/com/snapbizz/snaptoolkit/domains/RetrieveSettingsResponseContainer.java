package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RetrieveSettingsResponseContainer extends  ResponseContainer {

	@SerializedName("settingList")
	List<Settings> settings;
	public RetrieveSettingsResponseContainer(){
		
	}
	public List<Settings> getSettingsList() {
		return settings;
	}
	public void setSettingsList(List<Settings> settingList) {
		this.settings = settingList;
	}
}
