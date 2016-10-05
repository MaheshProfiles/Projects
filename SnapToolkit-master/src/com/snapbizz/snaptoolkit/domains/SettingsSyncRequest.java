package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SettingsSyncRequest extends Request{
	@SerializedName("settingList")
	private List<Settings> settingList;

	public List<Settings> getSettingsList() {
		return settingList;
	}

	public void setSettingsList(List<Settings> settingList) {
		this.settingList = settingList;
	}
}
