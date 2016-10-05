package com.snapbizz.snaptoolkit.domains;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName = "inventory_sku")
public class InventorySku {
    @SerializedName("tabletDbId")
	@DatabaseField(columnName = "inventory_slno", generatedId = true, allowGeneratedIdInsert=true)
	private int slNo;
	@DatabaseField(columnName = "inventory_sku_id", foreign = true, foreignColumnName = "sku_id")
	private transient ProductSku productSku;
	@SerializedName("quantity")
	@DatabaseField(columnName = "inventory_sku_quantity")
	private float quantity;
	@SerializedName("isOffer")
	@DatabaseField(columnName = "is_offer")
	private boolean isOffer;
	@SerializedName("purchasePrice")
	@DatabaseField(columnName="purchase_price")
	private float purchasePrice;
	@SerializedName("taxRate")
	@DatabaseField(columnName="tax_rate")
	private float taxRate;
	@SerializedName("timestamp")
	@DatabaseField(columnName="creation_timestamp")
	private String timestamp;
	@SerializedName("productSkuCode")
	private String productSkuCode;
    @DatabaseField(columnName="lastmodified_timestamp", version=true, dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
    private Date lastModifiedTimestamp;
    @SerializedName("isStore")
	@DatabaseField(columnName = "show_store")
	private boolean isStore;
    @DatabaseField(columnName = "_id")    
    private String _id;
	private transient boolean isLargeSize;
	private transient boolean isForgotten;
	
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getProductSkuCode() {
		return productSkuCode;
	}

	public void setProductSkuCode(String productSkuCode) {
		this.productSkuCode = productSkuCode;
	}

	public float getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(float taxRate) {
		this.taxRate = taxRate;
	}

	public float getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(float purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	
	public InventorySku(ProductSku produtSku, Date timeStamp) {
		this.productSku = produtSku;
		this.timestamp = new SimpleDateFormat(SnapToolkitConstants.STANDARD_DATEFORMAT).format(timeStamp);
	}
	
	public InventorySku(ProductSku produtSku, float quantity, Date timeStamp,String prodSkuCode,float purchasePrice,float taxRate) {
		this.productSku = produtSku;
		this.quantity = quantity;
		this.timestamp = new SimpleDateFormat(SnapToolkitConstants.STANDARD_DATEFORMAT).format(timeStamp);
		this.productSkuCode=prodSkuCode;
		this.purchasePrice= purchasePrice;
		this.taxRate=taxRate;
	}
	
	public InventorySku(Date timeStamp) {
		this.timestamp = new SimpleDateFormat(SnapToolkitConstants.STANDARD_DATEFORMAT).format(timeStamp);
	}
	
	public InventorySku() {
		
	}

	public boolean isOffer() {
		return isOffer;
	}

	public void setOffer(boolean isOffer) {
		this.isOffer = isOffer;
	}
	
	public int getSlNo() {
		return slNo;
	}
	
	public boolean isForgotten() {
        return isForgotten;
    }

    public void setForgotten(boolean isForgotten) {
        this.isForgotten = isForgotten;
    }

    public void setSlNo(int slNo) {
		this.slNo = slNo;
	}

	public ProductSku getProductSku() {
		return productSku;
	}

	public void setProductSku(ProductSku productSku) {
		this.productSku = productSku;
	}

	public float getQuantity() {
		return quantity;
	}

	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}

	public void incrementSkuQuantity() {
		quantity++;
	}

	public void decrementSkuQuantity() {
		quantity--;
	}

	public void addSkuQuantity(int quantity) {
		this.quantity += quantity;
	}

	public boolean isLargeSize() {
		return isLargeSize;
	}

	public void setLargeSize(boolean isLargeSize) {
		this.isLargeSize = isLargeSize;
	}

	public boolean isStore() {
		return isStore;
	}

	public void setStore(boolean isStore) {
		this.isStore = isStore;
	}

	public Date getLastModifiedTimestamp() {
		return lastModifiedTimestamp;
	}

	public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
		this.lastModifiedTimestamp = lastModifiedTimestamp;
	}
}
