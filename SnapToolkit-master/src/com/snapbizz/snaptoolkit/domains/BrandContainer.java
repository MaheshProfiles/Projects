package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class BrandContainer extends ResponseContainer{
	@SerializedName("response")
	private List<Brand> brandList;
	
	public void setProductSkuList (List<Brand> brandList) {
		this.brandList = brandList;
	}
	
	public List<Brand> getBrandList() {
		return this.brandList;
	}

}
