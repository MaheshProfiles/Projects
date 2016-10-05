package com.snapbizz.snaptoolkit.db.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ORDERS.
 */
public class Orders {

    private Integer poNumber;
    private Long distributorPhone;
    private Integer totalAmount;
    private Integer distributorInvoiceNo;
    private Integer totalDiscount;
    private Integer pendingAmount;
    private Integer totalQuantity;
    private Integer totalItems;
    private Integer totalVat;
    private Boolean isDeleted;
    private Boolean isMemo;
    private String status;
    /** Not-null value. */
    private java.util.Date orderDate;
    /** Not-null value. */
    private java.util.Date createdAt;
    /** Not-null value. */
    private java.util.Date updatedAt;

    public Orders() {
    }

    public Orders(Integer poNumber) {
        this.poNumber = poNumber;
    }

    public Orders(Integer poNumber, Long distributorPhone, Integer totalAmount, Integer distributorInvoiceNo, Integer totalDiscount, Integer pendingAmount, Integer totalQuantity, Integer totalItems, Integer totalVat, Boolean isDeleted, Boolean isMemo, String status, java.util.Date orderDate, java.util.Date createdAt, java.util.Date updatedAt) {
        this.poNumber = poNumber;
        this.distributorPhone = distributorPhone;
        this.totalAmount = totalAmount;
        this.distributorInvoiceNo = distributorInvoiceNo;
        this.totalDiscount = totalDiscount;
        this.pendingAmount = pendingAmount;
        this.totalQuantity = totalQuantity;
        this.totalItems = totalItems;
        this.totalVat = totalVat;
        this.isDeleted = isDeleted;
        this.isMemo = isMemo;
        this.status = status;
        this.orderDate = orderDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(Integer poNumber) {
        this.poNumber = poNumber;
    }

    public Long getDistributorPhone() {
        return distributorPhone;
    }

    public void setDistributorPhone(Long distributorPhone) {
        this.distributorPhone = distributorPhone;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getDistributorInvoiceNo() {
        return distributorInvoiceNo;
    }

    public void setDistributorInvoiceNo(Integer distributorInvoiceNo) {
        this.distributorInvoiceNo = distributorInvoiceNo;
    }

    public Integer getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(Integer totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public Integer getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(Integer pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Integer getTotalVat() {
        return totalVat;
    }

    public void setTotalVat(Integer totalVat) {
        this.totalVat = totalVat;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Boolean getIsMemo() {
        return isMemo;
    }

    public void setIsMemo(Boolean isMemo) {
        this.isMemo = isMemo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /** Not-null value. */
    public java.util.Date getOrderDate() {
        return orderDate;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setOrderDate(java.util.Date orderDate) {
        this.orderDate = orderDate;
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
