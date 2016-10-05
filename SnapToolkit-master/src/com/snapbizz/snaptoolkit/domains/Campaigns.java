package com.snapbizz.snaptoolkit.domains;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
@DatabaseTable(tableName="campaigns")

public class Campaigns implements Serializable{
	
	@SerializedName("campaign_id")
	@DatabaseField(columnName = "campaign_id", generatedId = true)
	 private int campaign_id;
	
	@SerializedName("id")
	@DatabaseField(columnName = "id")
	 private int id;
	

	
	@SerializedName("name")
	@DatabaseField(columnName="campaign_name")

	private String name;
	@SerializedName("start_date")
	@DatabaseField(columnName="start_date")

	private String start_date;
	@SerializedName("end_date")
	@DatabaseField(columnName="end_date")

	private String end_date;
	@SerializedName("campaign_type")
	@DatabaseField(columnName="campaign_type")

	private String campaign_type;
	@SerializedName("image_uid")
	@DatabaseField(columnName="image_uid")

	private String image_uid;
	@SerializedName("code")
	@DatabaseField(columnName="campaign_code")

	private String code;
	@SerializedName("company")
	@DatabaseField(columnName="company")

	private String company;
	
	@SerializedName("hasImage")
	@DatabaseField(columnName="has_image")

	private boolean hasImage;
	
	@SerializedName("imageRetry")
	@DatabaseField(columnName="image_retry")

	private boolean imageRetry;
	
	@SerializedName("localImageURL")
	@DatabaseField(columnName="localImageURL")
	private String localImageURL;
	
	@SerializedName("serverImageURL")
	@DatabaseField(columnName="serverImageURL")

	private String serverImageURL;
	
	@SerializedName("downloadId")
	@DatabaseField(columnName="download_id")

	private int downloadId;
	
	public int getCampaign_id() {
		return campaign_id;
	}
	public void setCampaign_id(int campaign_id) {
		this.campaign_id = campaign_id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getCampaign_type() {
		return campaign_type;
	}
	public void setCampaign_type(String campaign_type) {
		this.campaign_type = campaign_type;
	}
	public String getImage_uid() {
		return image_uid;
	}
	public void setImage_uid(String image_uid) {
		this.image_uid = image_uid;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public boolean isHasImage() {
		return hasImage;
	}
	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}
	public String getLocalImageURL() {
		return localImageURL;
	}
	public void setLocalImageURL(String localImageURL) {
		this.localImageURL = localImageURL;
	}
	public String getServerImageURL() {
		return serverImageURL;
	}
	public void setServerImageURL(String serverImageURL) {
		this.serverImageURL = serverImageURL;
	}
	public int getDownloadId() {
		return downloadId;
	}
	public void setDownloadId(int downloadId) {
		this.downloadId = downloadId;
	}
	public boolean isImageRetry() {
		return imageRetry;
	}
	public void setImageRetry(boolean imageRetry) {
		this.imageRetry = imageRetry;
	}
	
  
}
