package com.snapbizz.snaptoolkit.domains;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="product")
public class Product {
	@DatabaseField(columnName="product_id", generatedId=true)
	private int produtId;
	@DatabaseField(columnName="product_name")
	private String productName;
	@DatabaseField(columnName="brand_id", foreign=true, foreignColumnName="brand_id")
	private Brand productBrand;
	@DatabaseField(columnName="product_subcategory_id",foreign=true, foreignColumnName="product_category_id")
	private ProductCategory productCategory;
	@DatabaseField(columnName="product_description")
	private String productDescription;
	
	public Brand getProductBrand() {
		return productBrand;
	}
	public void setProductBrand(Brand productBrand) {
		this.productBrand = productBrand;
	}
	public ProductCategory getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}
	public String getProductDescription() {
		return productDescription;
	}
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
}
