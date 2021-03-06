package com.snapbizz.snaptoolkit.db.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "ITEMS".
 */
public class Items {

    private Long id;
    private long invoiceId;
    /** Not-null value. */
    private String name;
    private long productCode;
    /** Not-null value. */
    private String uom;
    private int measure;
    private int quantity;
    private int mrp;
    private int salePrice;
    private float vatRate;
    private int vatAmount;
    private int savings;
    private int totalAmount;
    private int packSize;
    /** Not-null value. */
    private java.util.Date createdAt;
    /** Not-null value. */
    private java.util.Date updatedAt;

    public Items() {
    }

    public Items(Long id) {
        this.id = id;
    }

    public Items(Long id, long invoiceId, String name, long productCode, String uom, int measure, int quantity, int mrp, int salePrice, float vatRate, int vatAmount, int savings, int totalAmount, int packSize, java.util.Date createdAt, java.util.Date updatedAt) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.name = name;
        this.productCode = productCode;
        this.uom = uom;
        this.measure = measure;
        this.quantity = quantity;
        this.mrp = mrp;
        this.salePrice = salePrice;
        this.vatRate = vatRate;
        this.vatAmount = vatAmount;
        this.savings = savings;
        this.totalAmount = totalAmount;
        this.packSize = packSize;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(long invoiceId) {
        this.invoiceId = invoiceId;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    public long getProductCode() {
        return productCode;
    }

    public void setProductCode(long productCode) {
        this.productCode = productCode;
    }

    /** Not-null value. */
    public String getUom() {
        return uom;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUom(String uom) {
        this.uom = uom;
    }

    public int getMeasure() {
        return measure;
    }

    public void setMeasure(int measure) {
        this.measure = measure;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMrp() {
        return mrp;
    }

    public void setMrp(int mrp) {
        this.mrp = mrp;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(int salePrice) {
        this.salePrice = salePrice;
    }

    public float getVatRate() {
        return vatRate;
    }

    public void setVatRate(float vatRate) {
        this.vatRate = vatRate;
    }

    public int getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(int vatAmount) {
        this.vatAmount = vatAmount;
    }

    public int getSavings() {
        return savings;
    }

    public void setSavings(int savings) {
        this.savings = savings;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getPackSize() {
        return packSize;
    }

    public void setPackSize(int packSize) {
        this.packSize = packSize;
    }

    /** Not-null value. */
    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }

    /** Not-null value. */
    public java.util.Date getUpdatedAt() {
        return updatedAt;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUpdatedAt(java.util.Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}
