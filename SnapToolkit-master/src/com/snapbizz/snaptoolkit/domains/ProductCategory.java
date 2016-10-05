package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName="product_category")
public class ProductCategory extends ResponseContainer {
	
	@SerializedName("tabletDbId")
	@DatabaseField(columnName="product_category_id", generatedId=true)
	private int categoryId;
	@SerializedName("categoryName")
	@DatabaseField(columnName="product_category_name")
	private String categoryName;
	@DatabaseField(columnName="product_parentcategory_id", foreign=true, foreignColumnName="product_category_id")
	private ProductCategory parenCategory;
	@DatabaseField(columnName="is_quickadd_category")
	private boolean isQuickAddCategory;
	@SerializedName("productCategorySalePrice")
	@DatabaseField(columnName="sku_saleprice")
	private float productCategorySalePrice;
	@SerializedName("productCategorySalePriceTwo")
	@DatabaseField(columnName="sku_saleprice_two")
	private float productCategorySalePriceTwo;
	@SerializedName("productCategorySalePriceThree")
	@DatabaseField(columnName="sku_saleprice_three")
	private float productCategorySalePriceThree;
	boolean isSelected;
	boolean isClicked;
	@SerializedName("profitMargin")
	@DatabaseField(columnName="profit_margin")
	private int profitMargin;
	@DatabaseField(columnName="lastmodified_timestamp",version=true,dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
	private Date lastModifiedTimestamp;
	@SerializedName("parentId")
//	@DatabaseField(columnName="product_parentcategory_id")
	private int parentId;
	
	public Date getLastModifiedTimestamp() {
		return lastModifiedTimestamp;
	}
	public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
		this.lastModifiedTimestamp = lastModifiedTimestamp;
	}
	public int getProfitMargin() {
		return profitMargin;
	}
	public void setProfitMargin(int profitMargin) {
		this.profitMargin = profitMargin;
	}
	public boolean isClicked() {
		return isClicked;
	}
	public void setClicked(boolean isClicked) {
		this.isClicked = isClicked;
	}
	public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    public boolean isQuickAddCategory() {
        return isQuickAddCategory;
    }
    public void setQuickAddCategory(boolean isQuickAddCategory) {
        this.isQuickAddCategory = isQuickAddCategory;
    }
    public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public ProductCategory getParenCategory() {
		return parenCategory;
	}
	public void setParenCategory(ProductCategory parenCategory) {
		this.parenCategory = parenCategory;
	}
	public float getProductCategorySalePrice() {
		return productCategorySalePrice;
	}
	public void setProductCategorySalePrice(float productCategorySalePrice) {
		this.productCategorySalePrice = productCategorySalePrice;
	}
	public float getProductCategorySalePriceTwo() {
		return productCategorySalePriceTwo;
	}
	public void setProductCategorySalePriceTwo(float productCategorySalePriceTwo) {
		this.productCategorySalePriceTwo = productCategorySalePriceTwo;
	}
	public float getProductCategorySalePriceThree() {
		return productCategorySalePriceThree;
	}
	public void setProductCategorySalePriceThree(float productCategorySalePriceThree) {
		this.productCategorySalePriceThree = productCategorySalePriceThree;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return categoryName;
	}
	
	
}
