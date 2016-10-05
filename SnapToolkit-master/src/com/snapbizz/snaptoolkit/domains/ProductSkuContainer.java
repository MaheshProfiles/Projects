package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ProductSkuContainer extends ResponseContainer{

	@SerializedName("response")
	List<ProductSkuOld> productSkuList;
	
	public void setProductSkuList (List<ProductSkuOld> productSkuList) {
		this.productSkuList = productSkuList;
	}
	
	public List<ProductSkuOld> getProductSkuList() {
		return this.productSkuList;
	}
	
}
