package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ProductSkuSyncRequest extends Request {
	
	@SerializedName("productSkuList")
	private List<ProductSku> productSkuList;

	public List<ProductSku> getProductSkuList() {
		return productSkuList;
	}

	public void setProductSkuList(List<ProductSku> productSkuList) {
		this.productSkuList = productSkuList;
	}
	
}
