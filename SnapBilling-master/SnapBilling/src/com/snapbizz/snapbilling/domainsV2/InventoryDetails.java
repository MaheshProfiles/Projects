package com.snapbizz.snapbilling.domainsV2;

import java.util.List;

import com.snapbizz.snaptoolkit.db.dao.ProductPacks;

import android.graphics.Bitmap;

public class InventoryDetails {
	private Long productSkuCode;
	private Long productGid;
	private String productSkuName;
	private Integer productSkuMrp;
	private String uom;
	private String imageUrl;
	private boolean isQuickAddProduct;
	private String productCategoryName;
	private String productSubCategoryName;
	private float VAT;  // TODO: Should be all lowercase
	private Bitmap productSkuBitmap;
	private String localName;
	private List<ProductPacks> productPacksList;
	 
	public Long getProductSkuCode() {
		return productSkuCode;
	}
	public void setProductSkuCode(Long productSkuCode) {
		this.productSkuCode = productSkuCode;
	}
	
	public Long getProductGid() {
		return productGid;
	}
	
	public void setProductGid(Long productGid) {
		this.productGid = productGid;
	}
	
	public String getProductSkuName() {
		return productSkuName;
	}
	
	public void setProductSkuName(String productSkuName) {
		this.productSkuName = productSkuName;
	}
	
	public Integer getProductSkuMrp() {
		return productSkuMrp;
	}
	
	public void setProductSkuMrp(Integer productSkuMrp) {
		this.productSkuMrp = productSkuMrp;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public boolean isQuickAddProduct() {
		return isQuickAddProduct;
	}
	
	public void setQuickAddProduct(boolean isQuickAddProduct) {
		this.isQuickAddProduct = isQuickAddProduct;
	}
	
	public String getProductSubCategoryName() {
		return productSubCategoryName;
	}
	
	public void setProductSubCategoryName(String productSubCategoryName) {
		this.productSubCategoryName = productSubCategoryName;
	}
	
	public float getVAT() {
		return VAT;
	}
	
	public void setVAT(float VAT) {
		this.VAT = VAT;
	}
	
	public Bitmap getProductSkuBitmap() {
		return productSkuBitmap;
	}
	
	public void setProductSkuBitmap(Bitmap productSkuBitmap) {
		this.productSkuBitmap = productSkuBitmap;
	}
	
	public String getLocalName() {
		return localName;
	}
	
	public void setLocalName(String localName) {
		this.localName = localName;
	}
	
	public String getProductCategoryName() {
		return productCategoryName;
	}
	
	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}
	
	public String getUom() {
		return uom;
	}
	
	public void setUom(String uom) {
		this.uom = uom;
	}
	
	public List<ProductPacks> getProductPacksList() {
		return productPacksList;
	}
	
	public void setProductPacksList(List<ProductPacks> productPacksList) {
		this.productPacksList = productPacksList;
	}
}
