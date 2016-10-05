package com.snapbizz.snapbilling.domains;


public class VisibilityItem {

    private String productName;
	private float productMrp;
	private String imageUrl;
	private String productCode;
	private float productSalePrice;
    private boolean isOffer;
	private boolean isExpanded;
	public final static int SIZE_BIG = 1;
	
	public VisibilityItem(String prodCode, String productName, String imageUrl, float productMrp, float productSalePrice, boolean isOffer) {
		this.productName = productName;
		this.imageUrl = imageUrl;
		this.productCode = prodCode;
		this.productMrp = productMrp;
		this.productSalePrice = productSalePrice;
		this.isOffer = isOffer;
	}

    public float getProductMrp() {
        return productMrp;
    }

    public float getProductSalePrice() {
        return productSalePrice;
    }
    
	public boolean isOffer() {
        return isOffer;
    }

    public void setOffer(boolean isOffer) {
        this.isOffer = isOffer;
    }

    public void setProductMrp(float productMrp) {
        this.productMrp = productMrp;
    }

    public void setProductSalePrice(float productSalePrice) {
        this.productSalePrice = productSalePrice;
    }

    public boolean isExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean isBig) {
		this.isExpanded = isBig;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

}
