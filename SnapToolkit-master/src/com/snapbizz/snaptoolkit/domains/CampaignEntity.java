package com.snapbizz.snaptoolkit.domains;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "CampaignTable")
public class CampaignEntity {

	/*
	 * @DatabaseField(uniqueIndexName = "unique_store_group_and_item_ids") int
	 * store_group_id;
	 * 
	 * @DatabaseField(uniqueIndexName = "unique_store_group_and_item_ids") int
	 * store_item_id;
	 */
	// @DatabaseField(id = true, generatedId = false) public String storeId;
	@DatabaseField(id = true, generatedId = false)
	public int image_id;

	@DatabaseField
	public String storeId;

	/*
	 * @DatabaseField public int category;
	 */

	@DatabaseField
	public String serverImageURL;
	@DatabaseField
	public String type;

	@DatabaseField
	public String campaignName;
	@DatabaseField
	public String campaignDescription;
	@DatabaseField
	public String startDate;
	@DatabaseField
	public String endDate;

	@DatabaseField
	public int sequence;
	@DatabaseField
	public String offerText;
	@DatabaseField
	public int createdDate;

	/*
	 * @DatabaseField public int modifiedDate;
	 */

	@DatabaseField
	public String override;
	@DatabaseField
	public String localImageURL;
	@DatabaseField
	public int template_id;
	@DatabaseField
	public int template_position;

	@DatabaseField
	public int id;

	public int getImage_id() {
		return image_id;
	}

	public void setImage_id(int image_id) {
		this.image_id = image_id;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getServerImageURL() {
		return serverImageURL;
	}

	public void setServerImageURL(String serverImageURL) {
		this.serverImageURL = serverImageURL;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getCampaignDescription() {
		return campaignDescription;
	}

	public void setCampaignDescription(String campaignDescription) {
		this.campaignDescription = campaignDescription;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getOfferText() {
		return offerText;
	}

	public void setOfferText(String offerText) {
		this.offerText = offerText;
	}

	public int getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(int createdDate) {
		this.createdDate = createdDate;
	}

	public String getOverride() {
		return override;
	}

	public void setOverride(String override) {
		this.override = override;
	}

	public String getLocalImageURL() {
		return localImageURL;
	}

	public void setLocalImageURL(String localImageURL) {
		this.localImageURL = localImageURL;
	}

	public int getTemplate_id() {
		return template_id;
	}

	public void setTemplate_id(int template_id) {
		this.template_id = template_id;
	}

	public int getTemplate_position() {
		return template_position;
	}

	public void setTemplate_position(int template_position) {
		this.template_position = template_position;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CampaignEntity() {/* OrmLite need a default constructor */
	}
}
