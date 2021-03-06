package com.snapbizz.snaptoolkit.db.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table PRODUCT_PRICE.
 */
public class ProductPrice {

    private Long id;
    private Long pId;
    private Short isGdb;
    private Double salesPrice;

    public ProductPrice() {
    }

    public ProductPrice(Long id) {
        this.id = id;
    }

    public ProductPrice(Long id, Long pId, Short isGdb, Double salesPrice) {
        this.id = id;
        this.pId = pId;
        this.isGdb = isGdb;
        this.salesPrice = salesPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPId() {
        return pId;
    }

    public void setPId(Long pId) {
        this.pId = pId;
    }

    public Short getIsGdb() {
        return isGdb;
    }

    public void setIsGdb(Short isGdb) {
        this.isGdb = isGdb;
    }

    public Double getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(Double salesPrice) {
        this.salesPrice = salesPrice;
    }

}
