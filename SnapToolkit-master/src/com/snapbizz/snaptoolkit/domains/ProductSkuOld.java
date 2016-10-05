package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName = "product_sku")
public class ProductSkuOld extends ResponseContainer {
	@SerializedName("barCode")
	@DatabaseField(columnName="sku_id", unique=true,id=true, dataType=DataType.STRING)
	private String productSkuCode;
	@SerializedName("productName")
	@DatabaseField(columnName="sku_name")
	private String productSkuName;
	@DatabaseField(columnName = "sku_description")
	private String productSkuDescription;
	@SerializedName("mrp")
	@DatabaseField(columnName="sku_mrp")
	private float productSkuMrp;
	@SerializedName("unitType")
	@DatabaseField(columnName = "sku_units", defaultValue = "PC")
	private String productSkuUnits;
	@DatabaseField(columnName = "product_id", foreign = true, foreignColumnName = "product_id")
	private Product skuProduct;
//	@DatabaseField(columnName = "sku_subcategory_id", foreign = true, foreignColumnName = "product_category_id")
//	private ProductCategory productCategory;
//	@DatabaseField(columnName="brand_id", foreignColumnName = "brand_id", foreign=true)
//	private Brand productBrand;
	@DatabaseField(columnName="product_imageurl")
	private String imageUrl;
	private boolean isSelected;
	private boolean isAnonymous;
//	@SerializedName("subCategory")
//	public String subCategory;
	@DatabaseField(columnName="sku_subcategory_id")
	public int subcategoryId;
    @DatabaseField(columnName="category_name")
    public String categoryName;
    @DatabaseField(columnName="subcategory_name")
    public String subCategoryName;
	@SerializedName("brandId")
	@DatabaseField(columnName="brand_id")
	public int brandId;
	@DatabaseField(columnName="sku_saleprice")
	private float salesPrice;
    @DatabaseField(columnName="lastmodified_timestamp", version=true, dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
    private Date lastModifiedTimestamp;
    @DatabaseField(columnName="has_image")
    private boolean hasImage;
    @DatabaseField(columnName="is_gdb")
    private boolean isGDB;
//    @DatabaseField(columnName="thumbnail_image")
//    private String thumbnailImageUrl;
//    
//    public String getThumbnailImageUrl() {
//        return thumbnailImageUrl;
//    }
//
//    public void setThumbnailImageUrl(String thumbnailImageUrl) {
//        this.thumbnailImageUrl = thumbnailImageUrl;
//    }

    public boolean isGDB() {
        return isGDB;
    }

    public void setGDB(boolean isGDB) {
        this.isGDB = isGDB;
    }
    
	public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public float getSalesPrice() {
		return salesPrice;
	}

	public void setSalesPrice(float salesPrice) {
		this.salesPrice = salesPrice;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public boolean isAnonymous() {
		return isAnonymous;
	}

	public void setAnonymous(boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

	public ProductSkuOld() {

	}
	
	public ProductSkuOld(String name, float mrp, String skuCode, boolean isSelected) {
		this.productSkuName = name;
		this.productSkuMrp = mrp;
		this.productSkuCode = skuCode;
		this.isSelected = isSelected;
	}
	
	public ProductSkuOld(String productSkuCode, String productSkuName) {
		this.productSkuCode = productSkuCode;
		this.productSkuName = productSkuName;
		this.isAnonymous = true;
	}
	
	public ProductSkuOld(ProductSkuOld productSku) {
		this.productSkuName = productSku.getProductSkuName();
		//this.productCategory = productSku.getProductCategory();
		//this.productSkuBaseQuantity = productSku.getProductSkuBaseQuantity();
		this.productSkuCode = productSku.getProductSkuCode();
		this.productSkuDescription = productSku.getProductSkuDescription();
		this.productSkuMrp = productSku.getProductSkuMrp();
		this.productSkuUnits = productSku.getProductSkuUnits();
		this.skuProduct = productSku.getSkuProduct();
	}
	
//	public String getProductBrandName() {
//		return brandName;
//	}
	
//	public Brand getProductBrand() {
//		return productBrand;
//	}
//	
//	public void setProductBrand(Brand productBrand) {
//		this.productBrand = productBrand;
//	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
//	public ProductCategory getProductCategory() {
//		return productCategory;
//	}
//	
//	public void setProductCategory(ProductCategory productCategory) {
//		this.productCategory = productCategory;
//	}

	public Product getSkuProduct() {
		return skuProduct;
	}

	public void setSkuProduct(Product skuProduct) {
		this.skuProduct = skuProduct;
	}

	public String getProductSkuCode() {
		return productSkuCode;
	}

	public void setProductSkuCode(String productSkuCode) {
		this.productSkuCode = productSkuCode;
	}

	public String getProductSkuName() {
		return productSkuName;
	}

	public void setProductSkuName(String productSkuName) {
		this.productSkuName = productSkuName;
	}

	public String getProductSkuDescription() {
		return productSkuDescription;
	}

	public void setProductSkuDescription(String productSkuDescription) {
		this.productSkuDescription = productSkuDescription;
	}

	public float getProductSkuMrp() {
		return productSkuMrp;
	}

	public void setProductSkuMrp(float productSkuMrp) {
		this.productSkuMrp = productSkuMrp;
	}

	public String getProductSkuUnits() {
		return productSkuUnits;
	}

	public void setProductSkuUnits(String productSkuUnits) {
		this.productSkuUnits = productSkuUnits;
	}

}