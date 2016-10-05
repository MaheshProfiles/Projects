package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;

public class AppVersion {
	
	@SerializedName("billingVersion")
	private String billingVersion;
	@SerializedName("inventoryVersion")
	private String inventoryVersion;
	@SerializedName("dashboardVersion")
	private String dashboardVersion;
	@SerializedName("visibilityVersion")
	private String visibilityVersion;
	@SerializedName("pushOffersVersion")
	private String pushOffersVersion;
	@SerializedName("tabletDbId")
	private String tabletDbId;
	public String getBillingVersion() {
		return billingVersion;
	}
	public void setBillingVersion(String billingVersion) {
		this.billingVersion = billingVersion;
	}
	public String getInventoryVersion() {
		return inventoryVersion;
	}
	public void setInventoryVersion(String inventoryVersion) {
		this.inventoryVersion = inventoryVersion;
	}
	public String getDashboardVersion() {
		return dashboardVersion;
	}
	public void setDashboardVersion(String dashboardVersion) {
		this.dashboardVersion = dashboardVersion;
	}
	public String getVisibilityVersion() {
		return visibilityVersion;
	}
	public void setVisibilityVersion(String visibilityVersion) {
		this.visibilityVersion = visibilityVersion;
	}
	public String getPushOffersVersion() {
		return pushOffersVersion;
	}
	public void setPushOffersVersion(String pushOffersVersion) {
		this.pushOffersVersion = pushOffersVersion;
	}
	public String getTabletDbId() {
		return tabletDbId;
	}
	public void setTabletDbId(String tabletDbId) {
		this.tabletDbId = tabletDbId;
	}
	
	
	

}
