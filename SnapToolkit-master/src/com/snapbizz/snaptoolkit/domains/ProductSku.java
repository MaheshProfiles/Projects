package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.SkuUnitType;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName = "product_sku")
public class ProductSku extends ResponseContainer {

	@SerializedName("tabletDbId")
	@DatabaseField(columnName = "sku_id", dataType = DataType.STRING, id = true, index = true, indexName = "sku_index")
	private String productSkuCode;
	@SerializedName("productSkuName")
	@DatabaseField(columnName = "sku_name")
	private String productSkuName;
	@SerializedName("productSkuDescription")
	@DatabaseField(columnName = "sku_description")
	private String productSkuDescription;
	@SerializedName("produtSkuMrp")
	@DatabaseField(columnName = "sku_mrp")
	private float productSkuMrp;
	@SerializedName("productSkuUnits")
	@DatabaseField(columnName = "sku_units", defaultValue = "PC")
	private SkuUnitType productSkuUnits = SkuUnitType.PC;
	@DatabaseField(columnName = "product_id", foreign = true, foreignColumnName = "product_id")
	private Product skuProduct;
	@DatabaseField(columnName = "sku_subcategory_id", foreign = true, foreignColumnName = "product_category_id")
	private transient ProductCategory productCategory;
	@DatabaseField(columnName = "brand_id", foreignColumnName = "brand_id", foreign = true)
	private transient Brand productBrand;
	@SerializedName("imageUrl")
	@DatabaseField(columnName = "product_imageurl")
	private String imageUrl;
	@SerializedName("productSkuAlternateMrp")
	@DatabaseField(columnName = "alternate_mrp")
	private float productSkuAlternateMrp;
	@SerializedName("productSkuSalePrice")
	@DatabaseField(columnName = "sku_saleprice")
	private float productSkuSalePrice;
	@SerializedName("productSkuSalePrice2")
	@DatabaseField(columnName = "sku_saleprice_two")
	private float productSkuSalePrice2;
	@SerializedName("productSkuSalePrice3")
	@DatabaseField(columnName = "sku_saleprice_three")
	private float productSkuSalePrice3;
	@SerializedName("productSkuAltSalePrice")
	@DatabaseField(columnName = "sku_alternate_saleprice")
	private float productSkuAltSalePrice;
	private transient boolean isSelected;
	private transient boolean isAnonymous;
	@SerializedName("skuSubCategoryId")
	private int subcategoryId;
	@SerializedName("skuBrandId")
	private int brandId;
	@SerializedName("lastModifiedTimestamp")
	@DatabaseField(columnName = "lastmodified_timestamp", version = true, dataType = DataType.DATE_STRING, format = SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
	private Date lastModifiedTimestamp;
	@DatabaseField(columnName = "has_image")
	private boolean hasImage;
	@DatabaseField(columnName = "is_gdb")
	private boolean isGDB;
	@DatabaseField(columnName = "is_quickadd_product")
	private boolean isQuickAddProduct;
	@DatabaseField(columnName = "category_name")
	private String productCategoryName;
	@DatabaseField(columnName = "subcategory_name")
	private String productSubCategoryName;
	@DatabaseField(columnName = "vat")
	private float VAT;
	private Bitmap productSkuBitmap;


	public Bitmap getProductSkuBitmap() {
		return productSkuBitmap;
	}

	public void setProductSkuBitmap(Bitmap productSkuBitmap) {
		this.productSkuBitmap = productSkuBitmap;
	}

	public String getProductCategoryName() {
		return productCategoryName;
	}

	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}

	public String getProductSubCategoryName() {
		return productSubCategoryName;
	}

	public void setProductSubCategoryName(String productSubCategoryName) {
		this.productSubCategoryName = productSubCategoryName;
	}

	public boolean isQuickAddProduct() {
		return isQuickAddProduct;
	}

	public void setQuickAddProduct(boolean isQuickAddProduct) {
		this.isQuickAddProduct = isQuickAddProduct;
	}

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

	public int getSubcategoryId() {
		return subcategoryId;
	}

	public void setSubcategoryId(int subcategoryId) {
		this.subcategoryId = subcategoryId;
	}

	public int getBrandId() {
		return brandId;
	}

	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}

	public float getProductSkuSalePrice() {
		return productSkuSalePrice;
	}

	public void setProductSkuSalePrice(float productSkuSalePrice) {
		this.productSkuSalePrice = productSkuSalePrice;
	}

	public float getProductSkuAltSalePrice() {
		return productSkuAltSalePrice;
	}

	public void setProductSkuAltSalePrice(float productSkuAltSalePrice) {
		this.productSkuAltSalePrice = productSkuAltSalePrice;
	}

	public float getProductSkuAlternateMrp() {
		return productSkuAlternateMrp;
	}

	public void setProductSkuAlternateMrp(float productSkuAlternateMrp) {
		this.productSkuAlternateMrp = productSkuAlternateMrp;
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

	public ProductSku() {

	}

	public ProductSku(String name, float mrp, String skuCode, String imageUrl,
			SkuUnitType unit, boolean isSelected, Brand brand,
			ProductCategory prodCategory, Date lastModifiedTimestamp,
			boolean isGdb, float ProductSkuSalePrice) {
		this.productSkuName = name;
		this.productSkuMrp = mrp;
		this.productSkuCode = skuCode;
		this.imageUrl = imageUrl;
		this.isSelected = isSelected;
		this.productBrand = brand;
		this.productSkuUnits = unit;
		this.productSkuSalePrice = ProductSkuSalePrice;
		this.productCategory = prodCategory;
		this.lastModifiedTimestamp = lastModifiedTimestamp;
		this.isGDB = isGdb;
		this.productSubCategoryName = productCategory.getCategoryName();
		this.productCategoryName = productCategory.getParenCategory()
				.getCategoryName();
	}

	public ProductSku(String name, float mrp, String skuCode, String imageUrl,
			SkuUnitType unit, boolean isSelected, Brand brand,
			ProductCategory prodCategory, Date lastModifiedTimestamp,
			boolean isGdb) {
		this.productSkuName = name;
		this.productSkuMrp = mrp;
		this.productSkuCode = skuCode;
		this.imageUrl = imageUrl;
		this.isSelected = isSelected;
		this.productBrand = brand;
		this.productSkuUnits = unit;
		this.productSkuSalePrice = productSkuMrp;
		this.productCategory = prodCategory;
		this.lastModifiedTimestamp = lastModifiedTimestamp;
		this.isGDB = isGdb;
		this.productSubCategoryName = productCategory.getCategoryName();
		this.productCategoryName = productCategory.getParenCategory()
				.getCategoryName();
	}

	public ProductSku(String name, float mrp, String skuCode, String imageUrl,
			boolean isSelected, float salePrice,float salePriceTwo,float salePriceThree, SkuUnitType unit) {
		this.productSkuName = name;
		this.productSkuMrp = mrp;
		this.productSkuCode = skuCode;
		this.imageUrl = imageUrl;
		this.isSelected = isSelected;
		this.productSkuSalePrice = productSkuMrp;
		if (salePrice > mrp) {
			this.productSkuSalePrice = mrp;
		} else {
			this.productSkuSalePrice = salePrice;
		}
		if (salePriceTwo > mrp) {
			this.productSkuSalePrice2 = mrp;
		} else {
			this.productSkuSalePrice2 = salePriceTwo;
		}
		if (salePriceThree > mrp) {
			this.productSkuSalePrice3 = mrp;
		} else {
			this.productSkuSalePrice3 = salePriceThree;
		}
		this.productSkuUnits = unit;
	}

	public ProductSku(String name, float mrp, String imageUrl,
			boolean isSelected, float salePrice,float salePriceTwo,float salePriceThree, SkuUnitType unit) {
		this.productSkuName = name;
		this.productSkuMrp = mrp;
		this.imageUrl = imageUrl;
		this.isSelected = isSelected;
		this.productSkuSalePrice = productSkuMrp;
		if (salePrice > mrp) {
			this.productSkuSalePrice = mrp;
		} else {
			this.productSkuSalePrice = salePrice;
		}
		if (salePriceTwo > mrp) {
			this.productSkuSalePrice2 = mrp;
		} else {
			this.productSkuSalePrice2 = salePriceTwo;
		}
		if (salePriceThree > mrp) {
			this.productSkuSalePrice3 = mrp;
		} else {
			this.productSkuSalePrice3 = salePriceThree;
		}
		this.productSkuUnits = unit;
	}

	public ProductSku(String productSkuCode, String productSkuName) {
		this.productSkuCode = productSkuCode;
		this.productSkuName = productSkuName;
		this.isAnonymous = true;
	}

	public ProductSku(ProductSku productSku) {
		this.productSkuName = productSku.getProductSkuName();
		this.productCategory = productSku.getProductCategory();
		this.productSkuCode = productSku.getProductSkuCode();
		this.productSkuDescription = productSku.getProductSkuDescription();
		this.productSkuMrp = productSku.getProductSkuMrp();
		this.productSkuUnits = productSku.getProductSkuUnits();
		this.skuProduct = productSku.getSkuProduct();
		this.imageUrl = productSku.getImageUrl();
		this.productSkuAlternateMrp = productSku.getProductSkuAlternateMrp();
		this.productSkuAltSalePrice = productSku.getProductSkuAltSalePrice();
		this.productSkuSalePrice = productSku.getProductSkuSalePrice();
		this.productSkuSalePrice2 = productSku.getProductSkuSalePrice2();
        this.productSkuSalePrice3 = productSku.getProductSkuSalePrice3();
		this.productSubCategoryName = productSku.getProductSubCategoryName();
		this.productCategoryName = productSku.getProductCategoryName();
		this.VAT=productSku.getVAT();
	}

	// public String getProductBrandName() {
	// return brandName;
	// }

	public Brand getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(Brand productBrand) {
		this.productBrand = productBrand;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
        if(productCategory == null)
            return;
		this.productCategory = productCategory;
		this.productSubCategoryName = productCategory.getCategoryName();
		if (productCategory.getParenCategory() != null)
			this.productCategoryName = productCategory.getParenCategory()
					.getCategoryName();
	}

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

	public SkuUnitType getProductSkuUnits() {
		return productSkuUnits;
	}

	public void setProductSkuUnits(SkuUnitType productSkuUnits) {
		this.productSkuUnits = productSkuUnits;
	}

	public Date getLastModifiedTimestamp() {
		return lastModifiedTimestamp;
	}

	public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
		this.lastModifiedTimestamp = lastModifiedTimestamp;
	}

	public float getVAT() {
		return VAT;
	}

	public void setVAT(float productVAT) {
		this.VAT = productVAT;
	}
	public float getProductSkuSalePrice2() {
		return productSkuSalePrice2;
	}

	public void setProductSkuSalePrice2(float productSkuSalePrice2) {
		this.productSkuSalePrice2 = productSkuSalePrice2;
	}

	public float getProductSkuSalePrice3() {
		return productSkuSalePrice3;
	}

	public void setProductSkuSalePrice3(float productSkuSalePrice3) {
		this.productSkuSalePrice3 = productSkuSalePrice3;
	}
	
}
