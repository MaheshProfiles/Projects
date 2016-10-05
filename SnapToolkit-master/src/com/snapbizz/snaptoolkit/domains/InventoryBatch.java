package com.snapbizz.snaptoolkit.domains;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName="inventory_batch")
public class InventoryBatch {
    @SerializedName("tabletDbId")
    @DatabaseField(generatedId=true, columnName="batch_id", allowGeneratedIdInsert=true)
    private int slNo;
    @DatabaseField(columnName="sku_id", foreign=true, foreignColumnName="sku_id")
    private transient ProductSku productSku;
    @SerializedName("batchMrp")
    @DatabaseField(columnName="sku_mrp")
    private float batchMrp;
    @SerializedName("batchSalesPrice")
    @DatabaseField(columnName="sku_sales_price")
    private float batchSalesPrice;
    @SerializedName("batchPurchasePrice")
    @DatabaseField(columnName="sku_purchase_price")
    private float batchPurchasePrice;
    @SerializedName("batchTimeStamp")
    @DatabaseField(columnName="batch_timestamp")
    private String batchTimeStamp;
    @SerializedName("batchExpiryDate")
    @DatabaseField(columnName="sku_exp_date")
    private String batchExpiryDate;
    @SerializedName("batchQty")
    @DatabaseField(columnName="sku_qty")
    private float batchQty;
    @SerializedName("batchAvailableQty")
    @DatabaseField(columnName="sku_available_qty")
    private float batchAvailableQty;
    @DatabaseField(columnName="order_no", foreignColumnName="order_no", foreign=true)
    private transient Order batchOrder;
    @SerializedName("orderId")
    private int orderId;
    @SerializedName("productSkuCode")
    private String productSkuCode;
    @DatabaseField(columnName="lastmodified_timestamp", version=true, dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
    private Date lastModifiedTimestamp;
    @DatabaseField(columnName="tax_rate")
    private float taxRate;

    public InventoryBatch() {

    }

    public Date getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    public InventoryBatch(String skuCode, float mrp, float salesPrice, float purchasePrice, Date batchTimeStamp, Date batchExpDate, float batchQty, int batchOrderNumber, float taxRate) {
        DateFormat dateFormat = new SimpleDateFormat(
                SnapToolkitConstants.STANDARD_DATEFORMAT);
        productSku = new ProductSku();
        productSku.setProductSkuCode(skuCode);
        this.batchSalesPrice = salesPrice;
        this.batchMrp = mrp;
        this.batchPurchasePrice = purchasePrice;
        this.batchQty = batchQty;
        if(batchTimeStamp != null)
            this.batchTimeStamp = dateFormat.format(batchTimeStamp);
        if(batchExpDate != null)
            this.batchExpiryDate = dateFormat.format(batchExpDate);
        this.batchAvailableQty = batchQty;
        batchOrder = new Order();
        batchOrder.setOrderNumber(batchOrderNumber);
        this.taxRate = taxRate;
    }

    public float getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(float taxRate) {
        this.taxRate = taxRate;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public float getBatchAvailableQty() {
        return batchAvailableQty;
    }

    public void setBatchAvailableQty(float batchAvailableQty) {
        this.batchAvailableQty = batchAvailableQty;
    }
    public int getSlNo() {
        return slNo;
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
    public float getBatchMrp() {
        return batchMrp;
    }
    public void setBatchMrp(float batchMrp) {
        this.batchMrp = batchMrp;
    }
    public float getBatchSalesPrice() {
        return batchSalesPrice;
    }
    public void setBatchSalesPrice(float batchSalesPrice) {
        this.batchSalesPrice = batchSalesPrice;
    }
    public float getBatchPurchasePrice() {
        return batchPurchasePrice;
    }
    public void setBatchPurchasePrice(float batchPurchasePrice) {
        this.batchPurchasePrice = batchPurchasePrice;
    }
    public String getBatchTimeStamp() {
        return batchTimeStamp;
    }
    public void setBatchTimeStamp(String batchTimeStamp) {
        this.batchTimeStamp = batchTimeStamp;
    }
    public String getBatchExpiryDate() {
        return batchExpiryDate;
    }
    public void setBatchExpiryDate(String batchExpiryDate) {
        this.batchExpiryDate = batchExpiryDate;
    }
    public float getBatchQty() {
        return batchQty;
    }
    public void setBatchQty(float batchQty) {
        this.batchQty = batchQty;
    }
    public Order getBatchOrder() {
        return batchOrder;
    }
    public void setBatchOrder(Order batchOrder) {
        this.batchOrder = batchOrder;
    }

    public String getProductSkuCode() {
        return productSkuCode;
    }

    public void setProductSkuCode(String productSkuCode) {
        this.productSkuCode = productSkuCode;
    }




}
