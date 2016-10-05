package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RetrieveCampaignResponseContainer extends ResponseContainer {

	// @SerializedName("campList")
	List<Campaigns> campaigns;
	// @SerializedName("timestamp")
	public String timestamp;
	// @SerializedName("image_base_url")
	public String image_base_url;

	public RetrieveCampaignResponseContainer() {

	}

	public List<Campaigns> getCampList() {
		return campaigns;
	}

	public void setCampList(List<Campaigns> campList) {
		this.campaigns = campList;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getImage_base_url() {
		return image_base_url;
	}

	public void setImage_base_url(String image_base_url) {
		this.image_base_url = image_base_url;
	}

	

}
