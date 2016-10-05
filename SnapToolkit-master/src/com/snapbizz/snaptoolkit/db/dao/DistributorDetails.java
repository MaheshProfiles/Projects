package com.snapbizz.snaptoolkit.db.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "DISTRIBUTOR_DETAILS".
 */
public class DistributorDetails {

    private long phone;
    private int amountDue;
    private int amountPaid;
    private java.util.Date lastPurchaseDate;
    private java.util.Date lastPaymentDate;
    private Integer lastPurchaseAmount;
    private Integer lastPaymentAmount;
    /** Not-null value. */
    private java.util.Date createdAt;
    /** Not-null value. */
    private java.util.Date updatedAt;

    public DistributorDetails() {
    }

    public DistributorDetails(long phone) {
        this.phone = phone;
    }

    public DistributorDetails(long phone, int amountDue, int amountPaid, java.util.Date lastPurchaseDate, java.util.Date lastPaymentDate, Integer lastPurchaseAmount, Integer lastPaymentAmount, java.util.Date createdAt, java.util.Date updatedAt) {
        this.phone = phone;
        this.amountDue = amountDue;
        this.amountPaid = amountPaid;
        this.lastPurchaseDate = lastPurchaseDate;
        this.lastPaymentDate = lastPaymentDate;
        this.lastPurchaseAmount = lastPurchaseAmount;
        this.lastPaymentAmount = lastPaymentAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public int getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(int amountDue) {
        this.amountDue = amountDue;
    }

    public int getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(int amountPaid) {
        this.amountPaid = amountPaid;
    }

    public java.util.Date getLastPurchaseDate() {
        return lastPurchaseDate;
    }

    public void setLastPurchaseDate(java.util.Date lastPurchaseDate) {
        this.lastPurchaseDate = lastPurchaseDate;
    }

    public java.util.Date getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(java.util.Date lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public Integer getLastPurchaseAmount() {
        return lastPurchaseAmount;
    }

    public void setLastPurchaseAmount(Integer lastPurchaseAmount) {
        this.lastPurchaseAmount = lastPurchaseAmount;
    }

    public Integer getLastPaymentAmount() {
        return lastPaymentAmount;
    }

    public void setLastPaymentAmount(Integer lastPaymentAmount) {
        this.lastPaymentAmount = lastPaymentAmount;
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