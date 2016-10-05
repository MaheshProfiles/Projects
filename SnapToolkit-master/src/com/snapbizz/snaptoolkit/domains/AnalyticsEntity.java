package com.snapbizz.snaptoolkit.domains;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "AnalyticsTable")
public class AnalyticsEntity {
	@DatabaseField(id = true)
	public int id;
	@DatabaseField
	public int campaignId;
	@DatabaseField
	public int imageId;
	@DatabaseField
	public long timestamp;
	@DatabaseField
	public boolean uploadtoserver;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(int campaignId) {
		this.campaignId = campaignId;
	}
	public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public boolean isUploadtoserver() {
		return uploadtoserver;
	}
	public void setUploadtoserver(boolean uploadtoserver) {
		this.uploadtoserver = uploadtoserver;
	}
	
	

}