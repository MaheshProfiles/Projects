package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.SkuUnitType;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.VisibilityTag;

@DatabaseTable(tableName="transaction_details")
public class BillItem {
	
	@SerializedName("tabletDbId")
	@DatabaseField(columnName="transaction_detail_id", generatedId=true)
	private int transactionDetailId;
	@DatabaseField(columnName="transaction_id", foreign=true, foreignColumnName="transaction_id")
	private transient Transaction transaction;
	@DatabaseField(columnName="sku_id", foreign=true, foreignColumnName="sku_id", foreignAutoRefresh=true)
	private transient ProductSku productSku;
	@SerializedName("productSkuQuantity")
	@DatabaseField(columnName="sku_quantity")
	private float productSkuQuantity;
	@SerializedName("productSkuSalePrice")
	@DatabaseField(columnName="sku_sale_price")
	private float productSkuSalePrice;
	@SerializedName("skuDiscount")
	@DatabaseField(columnName="sku_discount")
	private float skuDiscount;
	@SerializedName("productSkuMrp")
	@DatabaseField(columnName="sku_mrp")
	private float productSkuMrp;
	@SerializedName("transactionId")
	private int transactionId;
	@SerializedName("productSkuCode")
	private String productSkuCode;
	@SerializedName("billItemUnitType")
	@DatabaseField(columnName="unit_type", defaultValue="PC")
	private SkuUnitType billItemUnitType;
	@DatabaseField(columnName="lastmodified_timestamp", version=true, dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
	private Date lastModifiedTimestamp;
	@DatabaseField(columnName="sku_name")
	private String productSkuName;
	@DatabaseField(columnName="visibility_tag")
	private VisibilityTag visibilityTag = VisibilityTag.NONE;
	@SerializedName("vatRate")
	@DatabaseField(columnName="vat_rate")
	private float vatRate;
	@SerializedName("vatAmount")
	@DatabaseField(columnName="vat_amount")
	private float vatAmount;
	private boolean hasMultipleSp;
	@SerializedName("currentStock")
	@DatabaseField(columnName = "current_stock")
	private float currentStock;
	private ProductSku mProductSku;
	public BillItem() { 
		
	}
	
	public BillItem(ProductSku productSku) {
		this.productSku = productSku;
		mProductSku=productSku;
		this.productSkuQuantity = 1;
		this.productSkuMrp = productSku.getProductSkuMrp();
		this.productSkuName = productSku.getProductSkuName();
		if(productSku.getProductSkuSalePrice() > 0)
			this.productSkuSalePrice = productSku.getProductSkuSalePrice();
		else
			this.productSkuSalePrice = productSku.getProductSkuMrp();
		this.billItemUnitType = productSku.getProductSkuUnits();
		this.hasMultipleSp = productSku.getProductSkuAlternateMrp() > 0 && (productSku.getProductSkuAlternateMrp() != productSku.getProductSkuMrp()) ? true : false;
		this.vatRate = productSku.getVAT();
	}

	public BillItem(ProductSku productSku, float quantity) {
		this.productSku = productSku;
		this.mProductSku=productSku;
		this.productSkuQuantity = quantity;
		this.productSkuMrp = productSku.getProductSkuMrp();
		this.productSkuName = productSku.getProductSkuName();
		if(productSku.getProductSkuSalePrice() > 0)
			this.productSkuSalePrice = productSku.getProductSkuSalePrice();
		else
			this.productSkuSalePrice = productSku.getProductSkuMrp();
		this.billItemUnitType = productSku.getProductSkuUnits();
        this.hasMultipleSp = productSku.getProductSkuAlternateMrp() > 0 && (productSku.getProductSkuAlternateMrp() != productSku.getProductSkuMrp()) ? true : false;
        this.vatRate = productSku.getVAT();
	}

	public BillItem(ProductSku productSku, float quantity, float salesPrice) {
		this.productSku = productSku;
		mProductSku=productSku;
		this.productSkuQuantity = quantity;
		this.productSkuSalePrice = salesPrice;
		this.productSkuMrp = productSku.getProductSkuMrp();
		this.billItemUnitType = productSku.getProductSkuUnits();
		this.productSkuName = productSku.getProductSkuName();
        this.hasMultipleSp = productSku.getProductSkuAlternateMrp() > 0 && (productSku.getProductSkuAlternateMrp() != productSku.getProductSkuMrp()) ? true : false;
        this.vatRate = productSku.getVAT();
	}
	
	public VisibilityTag getVisibilityTag() {
        return visibilityTag;
    }

    public void setVisibilityTag(VisibilityTag visibilityTag) {
        this.visibilityTag = visibilityTag;
    }

    public boolean isHasMultipleSp() {
        return false;
    }

    public void setHasMultipleSp(boolean hasMultipleSp) {
        this.hasMultipleSp = hasMultipleSp;
    }

    public String getProductSkuName() {
		return productSkuName;
	}

	public void setProductSkuName(String productSkuName) {
		this.productSkuName = productSkuName;
	}
	
	public int getTransactionDetailId() {
		return transactionDetailId;
	}

	public Date getLastModifiedTimestamp() {
		return lastModifiedTimestamp;
	}

	public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
		this.lastModifiedTimestamp = lastModifiedTimestamp;
	}
	
	public SkuUnitType getBillItemUnitType() {
		return billItemUnitType;
	}

	public void setBillItemUnitType(SkuUnitType billItemUnitType) {
		this.billItemUnitType = billItemUnitType;
	}
	
	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public String getProductSkuCode() {
		return productSkuCode;
	}

	public void setProductSkuCode(String productSkuCode) {
		this.productSkuCode = productSkuCode;
	}
	public float getSkuDiscount() {
		return skuDiscount;
	}

	public void setSkuDiscount(float skuDiscount) {
		this.skuDiscount = skuDiscount;
	}
	
	public float getProductSkuMrp() {
		return productSkuMrp;
	}

	public void setProductSkuMrp(float productSkuMrp) {
		this.productSkuMrp = productSkuMrp;
	}
	public Transaction getTransaction() {
		return transaction;
	}
	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	public ProductSku getProductSku() {
		return mProductSku;
	}
	public void setProductSku(ProductSku productSku) {
		this.productSku = productSku;
	}
	public float getProductSkuQuantity() {
		return productSkuQuantity;
	}
	public void setProductSkuQuantity(float productSkuQuantity) {
		this.productSkuQuantity = productSkuQuantity;
	}
	public float getProductSkuSalePrice() {
		return productSkuSalePrice;
	}
	public void setProductSkuSalePrice(float productSkuSalePrice) {
		this.productSkuSalePrice = productSkuSalePrice;
	}
	public void incrementSkuQuantity() {
		productSkuQuantity++;
	}
	public void decrementSkuQuantity() {
		productSkuQuantity--;
	}
	public void addSkuQuantity(int quantity) {
		this.productSkuQuantity += quantity;
	}
	public void addSkuQuantity(float quantity) {
        this.productSkuQuantity += quantity;
    }

    public float getVatRate() {
        return vatRate;
    }

    public void setVatRate(float billItemVatRate) {
        this.vatRate = billItemVatRate;
    }

    public float getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(float vatAmount) {
        this.vatAmount = vatAmount;
    }

	public float getCurrentStock() {
		return currentStock;
	}

	public void setCurrentStock(float currentStock) {
		this.currentStock = currentStock;
	}

	public ProductSku getmProductSku() {
		return mProductSku;
	}

	
    
    
	
}
