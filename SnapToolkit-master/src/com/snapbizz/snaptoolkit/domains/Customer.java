package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName="customer")
public class Customer {
    @SerializedName("tabletDbId")
	@DatabaseField(columnName="customer_id", generatedId=true, allowGeneratedIdInsert=true)
	private int customerId;
	@SerializedName("customerName")
	@DatabaseField(columnName="customer_name")
	private String customerName;
	@SerializedName("customerPhoneNumber")
	@DatabaseField(columnName="customer_phone", unique=true)
	private String customerPhoneNumber;
	@SerializedName("customerAddress")
	@DatabaseField(columnName="customer_address")
	private String customerAddress;
	@SerializedName("state")
	@DatabaseField(columnName="state")
	private String state;
	@SerializedName("city")
	@DatabaseField(columnName="city")
	private String city;
	@SerializedName("pinCode")
	@DatabaseField(columnName="pincode")
	private String pinCode;
	@SerializedName("emailId")
	@DatabaseField(columnName="customer_email")
	private String emailId;
	@DatabaseField(columnName="membership_date", dataType=DataType.DATE_STRING, format=SnapToolkitConstants.STANDARD_DATEFORMAT)
	private transient Date customerMembershipDate;
	@SerializedName("creditLimit")
	@DatabaseField(columnName="credit_limit")
	private float creditLimit;
	@SerializedName("amount_due")
	@DatabaseField(columnName="amount_due")
	private float amountDue;
    @DatabaseField(columnName="lastmodified_timestamp", version=true, dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
	private Date lastModifiedTimestamp;
    @DatabaseField(columnName="last_payment_date", dataType = DataType.DATE_STRING, format=SnapToolkitConstants.STANDARD_DATEFORMAT)
    private Date lastPaymentDate;
    @DatabaseField(columnName="last_purchase_date", dataType = DataType.DATE_STRING, format=SnapToolkitConstants.STANDARD_DATEFORMAT)
    private Date lastPurchaseDate;
    @DatabaseField(columnName="last_payment_amount")
    private float lastPaymentAmount;
    @DatabaseField(columnName="last_purchase_amount")
    private float lastPurchaseAmount;
    @DatabaseField(columnName="amount_paid")
    private float amountPaid;
    @DatabaseField(columnName="avg_visits")
    private float avgVisits;
    @DatabaseField(columnName="avg_purchase")
    private float avgPurchase;
    
	public float getAvgVisits() {
        return avgVisits;
    }
    public void setAvgVisits(float avgVisits) {
        this.avgVisits = avgVisits;
    }
    public float getAvgPurchase() {
        return avgPurchase;
    }
    public void setAvgPurchase(float avgPurchase) {
        this.avgPurchase = avgPurchase;
    }
    public Date getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }
    public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }
    public Date getLastPaymentDate() {
        return lastPaymentDate;
    }
    public void setLastPaymentDate(Date lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }
    public Date getLastPurchaseDate() {
        return lastPurchaseDate;
    }
    public void setLastPurchaseDate(Date lastPurchaseDate) {
        this.lastPurchaseDate = lastPurchaseDate;
    }
    public float getLastPaymentAmount() {
        return lastPaymentAmount;
    }
    public void setLastPaymentAmount(float lastPaymentAmount) {
        this.lastPaymentAmount = lastPaymentAmount;
    }
    public float getLastPurchaseAmount() {
        return lastPurchaseAmount;
    }
    public void setLastPurchaseAmount(float lastPurchaseAmount) {
        this.lastPurchaseAmount = lastPurchaseAmount;
    }
    public float getAmountPaid() {
        return amountPaid;
    }
    public void setAmountPaid(float amountPaid) {
        this.amountPaid = amountPaid;
    }
    public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public float getCreditLimit() {
		return creditLimit;
	}
	public void setCreditLimit(float creditLimit) {
		this.creditLimit = creditLimit;
	}
	public float getAmountDue() {
		return amountDue;
	}
	public void setAmountDue(float amountDue) {
		this.amountDue = amountDue;
	}
	public Date getCustomerMembershipDate() {
		return customerMembershipDate;
	}
	public void setCustomerMembershipDate(Date customerMembershipDate) {
		this.customerMembershipDate = customerMembershipDate;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerPhoneNumber() {
		return customerPhoneNumber;
	}
	public void setCustomerPhoneNumber(String customerPhoneNumber) {
		this.customerPhoneNumber = customerPhoneNumber;
	}
	public String getCustomerAddress() {
		return customerAddress;
	}
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPinCode() {
		return pinCode;
	}
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}
	
}
