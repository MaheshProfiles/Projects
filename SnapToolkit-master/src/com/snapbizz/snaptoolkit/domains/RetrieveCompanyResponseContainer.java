package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class RetrieveCompanyResponseContainer extends ResponseContainer {
	
	@SerializedName("companyList")
	private List<Company> companyList;
	@SerializedName("timestamp")
	private String timestamp;

	public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<Company> getCompanyList() {
    	Log.d("","companyList-------->"+companyList.size());
		return companyList;
	}

	public void setCompanyList(List<Company> companyList) {
		this.companyList = companyList;
	}
	

}
