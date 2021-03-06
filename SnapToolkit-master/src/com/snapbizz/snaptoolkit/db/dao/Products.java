package com.snapbizz.snaptoolkit.db.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "PRODUCTS".
 */
public class Products {

    private Long productCode;
    /** Not-null value. */
    private String name;
    private int mrp;
    /** Not-null value. */
    private String uom;
    private int measure;
    private float vatRate;
    private String image;
    private boolean isGdb;
    /** Not-null value. */
    private java.util.Date createdAt;
    /** Not-null value. */
    private java.util.Date updatedAt;

    public Products() {
    }

    public Products(Long productCode) {
        this.productCode = productCode;
    }

    public Products(Long productCode, String name, int mrp, String uom, int measure, float vatRate, String image, boolean isGdb, java.util.Date createdAt, java.util.Date updatedAt) {
        this.productCode = productCode;
        this.name = name;
        this.mrp = mrp;
        this.uom = uom;
        this.measure = measure;
        this.vatRate = vatRate;
        this.image = image;
        this.isGdb = isGdb;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getProductCode() {
        return productCode;
    }

    public void setProductCode(Long productCode) {
        this.productCode = productCode;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    public int getMrp() {
        return mrp;
    }

    public void setMrp(int mrp) {
        this.mrp = mrp;
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

    public float getVatRate() {
        return vatRate;
    }

    public void setVatRate(float vatRate) {
        this.vatRate = vatRate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean getIsGdb() {
        return isGdb;
    }

    public void setIsGdb(boolean isGdb) {
        this.isGdb = isGdb;
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
