package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CompanyContainer extends ResponseContainer{

	@SerializedName("response")
	private List<Company> companyList;
	
	public void setProductSkuList (List<Company> companyList) {
		this.companyList = companyList;
	}
	
	public List<Company> getCompanyList() {
		return this.companyList;
	}

}
