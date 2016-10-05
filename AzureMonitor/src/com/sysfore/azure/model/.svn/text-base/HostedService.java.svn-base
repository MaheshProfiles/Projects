package com.sysfore.azure.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HostedService implements Parcelable {

	private String serviceName = "";
	private String location = "";
	private String powerState = "";
	private String roleSize = "";
	private String key = "";
	private String count = "";
	private String subcriptionId;
	private String createdby = "";
	private String mode = "";
	private boolean selected;

	public String getSubcriptionId() {
		return subcriptionId;
	}

	public void setSubcriptionId(String subcriptionId) {
		this.subcriptionId = subcriptionId;
	}

	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setPowerState(String powerState) {
		this.powerState = powerState;
	}

	public void setRoleSize(String roleSize) {
		this.roleSize = roleSize;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getLocation() {
		return location;
	}

	public String getPowerState() {
		return powerState;
	}

	public String getRoleSize() {
		return roleSize;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String toString() {
		return "ServiceName : " + serviceName + "\nLocation : " + location
				+ "\nPowerState : " + powerState + "\nRoleSize : " + roleSize
				+ "\nkey: " + key + "\ncount: " + count + "\nsubcriptionId: "
				+ subcriptionId + "\ncreatedby: " + createdby;

	}

	public HostedService(Parcel in) {
		super();
		readFromParcel(in);
	}

	public HostedService() {
		// TODO Auto-generated constructor stub
		super();
	}

	public static final Parcelable.Creator<HostedService> CREATOR = new Parcelable.Creator<HostedService>() {
		public HostedService createFromParcel(Parcel in) {
			return new HostedService(in);
		}

		public HostedService[] newArray(int size) {

			return new HostedService[size];
		}

	};

	public void readFromParcel(Parcel in) {

		serviceName = in.readString();
		location = in.readString();
		powerState = in.readString();
		roleSize = in.readString();
		key = in.readString();
		count = in.readString();

	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(serviceName);
		dest.writeString(location);
		dest.writeString(powerState);
		dest.writeString(roleSize);
		dest.writeString(key);
		dest.writeString(count);
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
