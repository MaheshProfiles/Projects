package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DeleteRecordsRequest extends Request {

	@SerializedName("tabletDbIds")
	private List<String> deleteRecordsList;

	public List<String> getDeleteRecordsList() {
		return deleteRecordsList;
	}

	public void setDeleteRecordsList(List<String> deleteRecordsList) {
		this.deleteRecordsList = deleteRecordsList;
	}
	
}
