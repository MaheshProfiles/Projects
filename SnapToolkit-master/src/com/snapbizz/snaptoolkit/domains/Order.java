package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.OrderType;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName = "orders")
public class Order {
    @SerializedName("tabletDbId")
	@DatabaseField(columnName = "order_no", generatedId = true, allowGeneratedIdInsert=true)
	private int orderNumber;
	@SerializedName("orderDate")
	@DatabaseField(columnName = "order_date")
	private String orderDate;
	@SerializedName("orderTotalAmount")
	@DatabaseField(columnName = "order_total_amount")
	private float orderTotalAmount;
	@SerializedName("orderStatus")
	@DatabaseField(columnName = "order_status", dataType = DataType.ENUM_STRING)
	private OrderType orderStatus;
	@DatabaseField(columnName = "distributor_id", foreign = true, foreignColumnName = "distributor_id")
	private transient Distributor distributorID;
	@SerializedName("orderTotalDiscount")
	@DatabaseField(columnName = "order_total_discount")
	private float orderTotalDiscount;
	@SerializedName("paymentToMake")
	@DatabaseField( columnName = "payment_to_make")
	private float paymentToMake;
	private transient boolean isSelected;
	@SerializedName("distributorId")
	private int distributorId;
    @DatabaseField(columnName="lastmodified_timestamp", version=true, dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
	private Date lastModifiedTimestamp;
    @SerializedName("invoice_imageurl")
    @DatabaseField(columnName = "invoice_imageurl")
    private String image;
    @SerializedName("invoice_number")
    @DatabaseField(columnName = "invoice_number")
    private String invoiceID;
	
	public int getOrderNumber() {
		return orderNumber;
	}	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public float getOrderTotalAmount() {
		return orderTotalAmount;
	}
	public void setOrderTotalAmount(float orderTotalAmount) {
		this.orderTotalAmount = orderTotalAmount;
	}
	public OrderType getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(OrderType orderStatus) {
		this.orderStatus = orderStatus;
	}
	public Distributor getDistributorID() {
		return distributorID;
	}
	public void setDistributorID(Distributor distributorID) {
		this.distributorID = distributorID;
	}
	public float getOrderTotalDiscount() {
		return orderTotalDiscount;
	}
	public void setOrderTotalDiscount(float orderTotalDiscount) {
		this.orderTotalDiscount = orderTotalDiscount;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public float getPaymentToMake() {
		return paymentToMake;
	}
	public void setPaymentToMake(float paymentToMake) {
		this.paymentToMake = paymentToMake;
	}
	public int getDistributorsId() {
		return distributorId;
	}
	public void setDistributorsId(int distributorsId) {
		this.distributorId = distributorsId;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getInvoiceID() {
		return invoiceID;
	}
	public void setInvoiceID(String invoiceID) {
		this.invoiceID = invoiceID;
	}
}
