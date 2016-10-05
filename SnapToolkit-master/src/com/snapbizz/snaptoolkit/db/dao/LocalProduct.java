package com.snapbizz.snaptoolkit.db.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table LOCAL_PRODUCT.
 */
public class LocalProduct {

    private Long id;
    private Long barcode;
    private String name;
    private String description;
    private Integer brandId;
    private Integer subCategoryId;
    private String uom;
    private Double measure;
    private Double mrp;
    private Double vat;
    private Short isQuickaddProduct;
    private String transName;
    private String transDesc;
    /** Not-null value. */
    private java.util.Date createdAt;
    /** Not-null value. */
    private java.util.Date updatedAt;

    public LocalProduct() {
    }

    public LocalProduct(Long id) {
        this.id = id;
    }

    public LocalProduct(Long id, Long barcode, String name, String description, Integer brandId, Integer subCategoryId, String uom, Double measure, Double mrp, Double vat, Short isQuickaddProduct, String transName, String transDesc, java.util.Date createdAt, java.util.Date updatedAt) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
        this.description = description;
        this.brandId = brandId;
        this.subCategoryId = subCategoryId;
        this.uom = uom;
        this.measure = measure;
        this.mrp = mrp;
        this.vat = vat;
        this.isQuickaddProduct = isQuickaddProduct;
        this.transName = transName;
        this.transDesc = transDesc;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBarcode() {
        return barcode;
    }

    public void setBarcode(Long barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(Integer subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public Double getMeasure() {
        return measure;
    }

    public void setMeasure(Double measure) {
        this.measure = measure;
    }

    public Double getMrp() {
        return mrp;
    }

    public void setMrp(Double mrp) {
        this.mrp = mrp;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

    public Short getIsQuickaddProduct() {
        return isQuickaddProduct;
    }

    public void setIsQuickaddProduct(Short isQuickaddProduct) {
        this.isQuickaddProduct = isQuickaddProduct;
    }

    public String getTransName() {
        return transName;
    }

    public void setTransName(String transName) {
        this.transName = transName;
    }

    public String getTransDesc() {
        return transDesc;
    }

    public void setTransDesc(String transDesc) {
        this.transDesc = transDesc;
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