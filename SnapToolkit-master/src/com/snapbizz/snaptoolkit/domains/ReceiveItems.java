package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName = "received_items")
public class ReceiveItems {
    
    @SerializedName("tabletDbId")
    @DatabaseField(columnName = "received_items_id", generatedId = true, allowGeneratedIdInsert = true)
    private int receiveItemsID;
    @SerializedName("receivedQty")
    @DatabaseField(columnName = "received_qty")
    private int receivedQty;
    @SerializedName("purchasePrice")
    @DatabaseField(columnName = "purchase_price")
    private float purchasePrice;
    @SerializedName("vatRate")
    @DatabaseField(columnName = "vat_rate")
    private float vatRate;
    @SerializedName("vatAmount")
    @DatabaseField(columnName = "vat_amount")
    private float vatAmount;
    @SerializedName("receiveDate")
    @DatabaseField(columnName = "receive_date")
    private String receiveDate;
    @DatabaseField(columnName="lastmodified_timestamp", version=true, dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
    private Date lastModifiedTimestamp;
    @SerializedName("invoiceNumber")
    @DatabaseField(columnName = "invoice_number")
    private String invoiceNumber;
    @DatabaseField(columnName = "sku_id", foreign = true, foreignColumnName = "sku_id")
    private transient ProductSku productSkuID;
    @SerializedName("productSkuCode")
    private String productSkuCode;
    
    public int getReceiveItemsID() {
        return receiveItemsID;
    }
    public void setReceiveItemsID(int receiveItemsID) {
        this.receiveItemsID = receiveItemsID;
    }
    public int getReceivedQty() {
        return receivedQty;
    }
    public void setReceivedQty(int receivedQty) {
        this.receivedQty = receivedQty;
    }
    public float getPurchasePrice() {
        return purchasePrice;
    }
    public void setPurchasePrice(float purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    public float getVatRate() {
        return vatRate;
    }
    public void setVatRate(float vatRate) {
        this.vatRate = vatRate;
    }
    public float getVatAmount() {
        return vatAmount;
    }
    public void setVatAmount(float vatAmount) {
        this.vatAmount = vatAmount;
    }
    public String getReceiveDate() {
        return receiveDate;
    }
    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }
    public String getInvoiceNumber() {
        return invoiceNumber;
    }
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
    public ProductSku getProductSkuID() {
        return productSkuID;
    }
    public void setProductSkuID(ProductSku productSkuID) {
        this.productSkuID = productSkuID;
    }
    public String getProductSkuCode() {
        return productSkuCode;
    }
    public void setProductSkuCode(String productSkuCode) {
        this.productSkuCode = productSkuCode;
    }

}
