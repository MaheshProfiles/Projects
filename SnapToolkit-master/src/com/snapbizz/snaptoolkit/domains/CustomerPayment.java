package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.PaymentType;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName="customer_payment")
public class CustomerPayment {

    @DatabaseField(columnName="customer_payment_id", generatedId=true)
    private int customerPaymentId;
    @DatabaseField(columnName="customer_id", foreign=true, foreignColumnName="customer_id")
    private transient Customer customer;
    @DatabaseField(columnName="payment_amount")
    private float paymentAmount;
    @DatabaseField(columnName="lastmodified_timestamp", version=true, dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
    private Date lastModifiedTimestamp;
    @DatabaseField(columnName="payment_date", dataType = DataType.DATE_STRING, format=SnapToolkitConstants.STANDARD_DATEFORMAT)
    private Date paymentDate;
    @DatabaseField(columnName="payment_type")
    private PaymentType paymentType;
    @SerializedName("customerId")
    private int customerId;
  
    public CustomerPayment() {
        
    }
    
    public CustomerPayment(Customer customer, float paymentAmount) {
        this.customer = customer;
        this.paymentAmount = paymentAmount;
    }
    
    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
    
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getCustomerPaymentId() {
        return customerPaymentId;
    }
    public void setCustomerPaymentId(int customerPaymentId) {
        this.customerPaymentId = customerPaymentId;
    }
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public float getPaymentAmount() {
        return paymentAmount;
    }
    public void setPaymentAmount(float paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
    public Date getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }
    public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }
    
}
