package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "settings")
public class Settings {
	
	
	@DatabaseField(columnName="settings_id", generatedId = true)
    @SerializedName("settingsid")
    private int settingsID;
	@SerializedName("store_name")
	@DatabaseField(columnName = "store_name")
	private String storeName;
	@SerializedName("store_address")
	@DatabaseField(columnName = "store_address")
	private String storeAddress;
	@SerializedName("contact_number")
	@DatabaseField(columnName = "contact_number")
	private String contactNumber;
	@SerializedName("store_tin_number")
	@DatabaseField(columnName = "store_tin_number")
	private String storeTinNumber;
	@SerializedName("store_city")
	@DatabaseField(columnName = "store_city")
	private String storeCity;
	@SerializedName("footer_1")
	@DatabaseField(columnName = "footer_1")
	private String footer1;
	@SerializedName("footer_2")
	@DatabaseField(columnName = "footer_2")
	private String footer2;
	@SerializedName("footer_3")
	@DatabaseField(columnName = "footer_3")
	private String footer3;
	@SerializedName("show_savings")
	@DatabaseField(columnName = "show_savings")
	private boolean showSavings;
	@SerializedName("sort_order")
	@DatabaseField(columnName = "sort_order")
	private boolean sortOrder;
	@SerializedName("roundoff_status")
	@DatabaseField(columnName = "roundoff_status")
	private boolean roundOffStatus;
	@SerializedName("pin_number")
	@DatabaseField(columnName = "pin_number")
	private String pinNumber;
	
	
	public Settings(){}


	public String getStoreName() {
		return storeName;
	}


	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public int getSettingsID() {
		return settingsID;
	}


	public void setSettingsID(int settingsID) {
		this.settingsID = settingsID;
	}
	
	public String getStoreAddress() {
		return storeAddress;
	}


	public void setStoreAddress(String storeAddress) {
		this.storeAddress = storeAddress;
	}


	public String getContactNumber() {
		return contactNumber;
	}


	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}


	public String getStoreTinNumber() {
		return storeTinNumber;
	}


	public void setStoreTinNumber(String storeTinNumber) {
		this.storeTinNumber = storeTinNumber;
	}


	public String getStoreCity() {
		return storeCity;
	}


	public void setStoreCity(String storeCity) {
		this.storeCity = storeCity;
	}


	public String getFooter1() {
		return footer1;
	}


	public void setFooter1(String footer1) {
		this.footer1 = footer1;
	}


	public String getFooter2() {
		return footer2;
	}


	public void setFooter2(String footer2) {
		this.footer2 = footer2;
	}


	public String getFooter3() {
		return footer3;
	}


	public void setFooter3(String footer3) {
		this.footer3 = footer3;
	}


	public boolean isShowSavings() {
		return showSavings;
	}


	public void setShowSavings(boolean showSavings) {
		this.showSavings = showSavings;
	}


	public boolean isSortOrder() {
		return sortOrder;
	}


	public void setSortOrder(boolean sortOrder) {
		this.sortOrder = sortOrder;
	}


	public boolean isRoundOffStatus() {
		return roundOffStatus;
	}


	public void setRoundOffStatus(boolean roundOffStatus) {
		this.roundOffStatus = roundOffStatus;
	}


	public String getPinNumber() {
		return pinNumber;
	}


	public void setPinNumber(String pinNumber) {
		this.pinNumber = pinNumber;
	}
	
	
	

}
