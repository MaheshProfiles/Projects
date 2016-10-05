package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.TransactionType;

@DatabaseTable(tableName = "invoice")
public class Invoice {

    @SerializedName("transactionId")
    @DatabaseField(columnName = "transaction_id")
    private int transactionId;
    @SerializedName("totalAmount")
    @DatabaseField(columnName = "total_amount")
    private double totalAmount;
    @SerializedName("totalDiscount")
    @DatabaseField(columnName = "total_discount")
    private float totalDiscount;
    @SerializedName("totalSavings")
    @DatabaseField(columnName = "total_savings")
    private float totalSavings;
    @SerializedName("totalQty")
    @DatabaseField(columnName = "total_qty")
    private int totalQty;
    @DatabaseField(columnName = "customer_id", foreign = true, foreignColumnName = "customer_id")
    private transient Customer customer;
    @SerializedName("transactionTimeStamp")
    @DatabaseField(columnName = "transaction_timestamp")
    private String transactionTimeStamp;
    @SerializedName("transactionType")
    @DatabaseField(columnName = "transaction_type", dataType = DataType.ENUM_STRING)
    private TransactionType transactionType;
    @SerializedName("isPaid")
    @DatabaseField(columnName = "is_paid")
    private boolean isPaid;
    @SerializedName("customerId")
    private int customerId;
    @DatabaseField(columnName="lastmodified_timestamp", version=true, dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
    private Date lastModifiedTimestamp;
    @DatabaseField(columnName = "is_visible")
    private boolean isVisible = true;
    @DatabaseField(columnName = "pending_payment")
    private float pendingPayment;
    @SerializedName("vat")
    @DatabaseField(columnName="vat")
    private float VAT;
    @SerializedName("tabletDbId")
    @DatabaseField(columnName = "invoice_id", generatedId = true, allowGeneratedIdInsert = true)
    private int invoiceID;
    
    public Invoice () {
        
    }
    
    public Invoice(double totalAmount, float totalDiscount, float totalSaving, int totalQty, boolean isPaid, float pendingPayment, float vat, TransactionType transactionType, Customer customer, int transactionID, String transactionTimestamp, Integer invoiceId) {
        this.totalAmount = totalAmount;
        this.totalDiscount = totalDiscount;
        this.totalSavings = totalSaving;
        this.totalQty = totalQty;
        this.isPaid = isPaid;
        this.pendingPayment = pendingPayment;
        this.VAT = vat;
        this.transactionType = transactionType;
        this.customer = customer;
        this.transactionId = transactionID;
        this.transactionTimeStamp = transactionTimestamp;
        if(invoiceId != null)
        	this.invoiceID = invoiceId;
    }
    
    public int getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
    public double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public float getTotalDiscount() {
        return totalDiscount;
    }
    public void setTotalDiscount(float totalDiscount) {
        this.totalDiscount = totalDiscount;
    }
    public float getTotalSavings() {
        return totalSavings;
    }
    public void setTotalSavings(float totalSavings) {
        this.totalSavings = totalSavings;
    }
    public int getTotalQty() {
        return totalQty;
    }
    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public String getTransactionTimeStamp() {
        return transactionTimeStamp;
    }
    public void setTransactionTimeStamp(String transactionTimeStamp) {
        this.transactionTimeStamp = transactionTimeStamp;
    }
    public TransactionType getTransactionType() {
        return transactionType;
    }
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    public boolean isPaid() {
        return isPaid;
    }
    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }
    public int getCustomerId() {
        return customerId;
    }
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    public Date getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }
    public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }
    public boolean isVisible() {
        return isVisible;
    }
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
    public float getPendingPayment() {
        return pendingPayment;
    }
    public void setPendingPayment(float pendingPayment) {
        this.pendingPayment = pendingPayment;
    }
    public float getVAT() {
        return VAT;
    }
    public void setVAT(float vAT) {
        VAT = vAT;
    }
    public int getInvoiceID() {
        return invoiceID;
    }
    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }
    
}
