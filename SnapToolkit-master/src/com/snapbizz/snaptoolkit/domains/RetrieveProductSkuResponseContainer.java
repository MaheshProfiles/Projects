package com.snapbizz.snaptoolkit.domains;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RetrieveProductSkuResponseContainer extends ResponseContainer {
	@SerializedName("productSkuList")
	private List<ProductSku> productSkuList;
	@SerializedName("timestamp")
	private String timestamp;

	public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<ProductSku> getProductSkuList() {
		return productSkuList;
	}

	public void setProductSkuList(List<ProductSku> productSkuList) {
		this.productSkuList = productSkuList;
	}
	
	
	
}
