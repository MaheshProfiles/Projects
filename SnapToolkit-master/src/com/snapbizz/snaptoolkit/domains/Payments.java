package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName = "payment")
public class Payments {

    @SerializedName("tabletDbId")
	@DatabaseField(columnName = "payment_id", generatedId = true, allowGeneratedIdInsert=true)
	private int paymentID;
	@SerializedName("paymentAmount")
	@DatabaseField(columnName = "amount")
	private float paymentAmount;
	@DatabaseField(columnName = "distributor_id", foreign = true, foreignColumnName = "distributor_id")
	private transient Distributor distributor;
	@SerializedName("distributorId")
	private int distributorId;
	@SerializedName("paymentDate")
	@DatabaseField(columnName = "payment_timestamp", dataType=DataType.DATE_STRING, format=SnapToolkitConstants.STANDARD_DATEFORMAT)
	private Date paymentDate;
	@SerializedName("paymentChequeNo")
	@DatabaseField(columnName = "cheque_no")
	private String paymentChequeNo;
	@SerializedName("paymentType")
	@DatabaseField(columnName = "payment_mode")
	private String paymentType;
    @DatabaseField(columnName="lastmodified_timestamp", version=true, dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
	private Date lastModifiedTimestamp;

	public int getPaymentID() {
		return paymentID;
	}

	public void setPaymentID(int paymentID) {
		this.paymentID = paymentID;
	}

	public float getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(float paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getPaymentChequeNo() {
		return paymentChequeNo;
	}

	public void setPaymentChequeNo(String paymentChequeNo) {
		this.paymentChequeNo = paymentChequeNo;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public int getDistributorId() {
		return distributorId;
	}

	public void setDistributorId(int distributorId) {
		this.distributorId = distributorId;
	}

}