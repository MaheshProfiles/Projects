package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RetrieveCategoryResponseContainer extends ResponseContainer {

	@SerializedName("productCategoryList")
	List<ProductCategory> productCategory;

	@SerializedName("timestamp")
	private String timestamp;

	public RetrieveCategoryResponseContainer() {

	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public List<ProductCategory> getProductCategoryList() {
		return productCategory;
	}

	public void setProductCategoryList(List<ProductCategory> productCategoryList) {
		this.productCategory = productCategoryList;
	}

}
