package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.PaymentType;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName = "distributor_payment")
public class DistributorPayment {
	@DatabaseField(columnName = "distributor_payment_id", generatedId = true)
	private int distributorPaymentId;
	@DatabaseField(columnName = "distributor_id", foreign = true, foreignColumnName = "distributor_id")
	private transient Distributor distributor;
	@DatabaseField(columnName = "distributor_payment_amount")
	private float paymentAmount;
	@DatabaseField(columnName = "lastmodified_timestamp", version = true, dataType = DataType.DATE_STRING, format = SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
	private Date lastModifiedTimestamp;
	@DatabaseField(columnName = "distributor_payment_date", dataType = DataType.DATE_STRING, format = SnapToolkitConstants.STANDARD_DATEFORMAT)
	private Date paymentDate;
	@DatabaseField(columnName = "distributor_payment_type")
	private PaymentType paymentType;
	@SerializedName("distributorId")
	private int distributorId;

	public int getDistributorPaymentId() {
		return distributorPaymentId;
	}

	public void setDistributorPaymentId(int distributorPaymentId) {
		this.distributorPaymentId = distributorPaymentId;
	}

	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
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

	public int getDistributorId() {
		return distributorId;
	}

	public void setDistributorId(int distributorId) {
		this.distributorId = distributorId;
	}

	public DistributorPayment() {

	}

	public DistributorPayment(Distributor distributor, float paymentAmount) {
		this.distributor = distributor;
		this.paymentAmount = paymentAmount;
	}

	
}
