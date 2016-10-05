package com.snapbizz.snaptoolkit.domains;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.snapbizz.snaptoolkit.utils.PaymentType;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.TransactionType;

public class CustomerCreditPayment {

	private int transactionId;
	private double totalAmount;
	private float totalDiscount;
	private float totalSavings;
	private int totalQty;
	private TransactionType transactionType;
	private boolean isPaid;
	private float pendingPayment;
	private String transactionTimeStamp;
	private float creditDue;
	private float openingBalance;
	private List<CustomerCreditPayment> creditDueList;
	private int invoiceNumber;
	@DatabaseField(columnName = "customer_payment_id", generatedId = true)
	private int customerPaymentId;
	@DatabaseField(columnName = "customer_id", foreign = true, foreignColumnName = "customer_id")
	private transient Customer customer;
	@DatabaseField(columnName = "payment_amount")
	private float paymentAmount;
	@DatabaseField(columnName = "lastmodified_timestamp", version = true, dataType = DataType.DATE_STRING, format = SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
	private Date lastModifiedTimestamp;
	@DatabaseField(columnName = "payment_date", dataType = DataType.DATE_STRING, format = SnapToolkitConstants.STANDARD_DATEFORMAT)
	private Date paymentDate;
	@DatabaseField(columnName = "payment_type")
	private PaymentType paymentType;
	@SerializedName("customerId")
	private int customerId;

	public CustomerCreditPayment() {

	}

	public CustomerCreditPayment(Customer customer, float paymentAmount) {
		this.customer = customer;
		this.paymentAmount = paymentAmount;
	}

	public int getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(int invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	
	public float getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(float openingBalance) {
		this.openingBalance = openingBalance;
	}

	public void setPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getTransactionTimeStamp() {
		return transactionTimeStamp;
	}

	public void setTransactionTimeStamp(String transactionTimeStamp) {
		this.transactionTimeStamp = transactionTimeStamp;
	}

	public float getCreditDue() {
		return creditDue;
	}

	public void setCreditDue(float creditDue) {
		this.creditDue = creditDue;
	}
	public int getCustomerPaymentId() {
		return customerPaymentId;
	}

	public void setCustomerPaymentId(int customerPaymentId) {
		this.customerPaymentId = customerPaymentId;
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

	public float getPendingPayment() {
		return pendingPayment;
	}

	public void setPendingPayment(float pendingPayment) {
		this.pendingPayment = pendingPayment;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
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

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public boolean isPaid() {
		return isPaid;
	}

	public void setIsPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public List<CustomerCreditPayment> getCreditDueList() {
		return creditDueList;
	}

	public void setCreditDueList(List<CustomerCreditPayment> creditDueList) {
		this.creditDueList = creditDueList;
	}

}
