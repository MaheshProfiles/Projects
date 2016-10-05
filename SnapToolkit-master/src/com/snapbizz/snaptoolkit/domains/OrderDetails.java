package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName = "order_details")
public class OrderDetails {

    @SerializedName("tabletDbId")
	@DatabaseField(columnName = "order_detail_id", generatedId = true, allowGeneratedIdInsert=true)
	private int orderDetailID;
	@SerializedName("orderProductPendingQty")
	@DatabaseField(columnName = "pending_qty")
	private int orderProductPendingQty;
	@SerializedName("orderProductToOrderQty")
	@DatabaseField(columnName = "to_order_qty")
	private int orderProductToOrderQty;
	@SerializedName("productBilledQty")
	@DatabaseField(columnName = "billed_qty")
	private int productBilledQty;
	@SerializedName("productToReceiveQty")
	@DatabaseField(columnName = "to_receive_qty")
	private int productToReceiveQty;
	@SerializedName("productReceivedQty")
	@DatabaseField(columnName = "received_qty")
	private int productReceivedQty;
	@DatabaseField(columnName = "order_no", foreign = true, foreignColumnName = "order_no")
	private transient Order order;
	@SerializedName("orderId")
	private int orderId;
	@DatabaseField(columnName = "product_sku_id", foreign = true, foreignColumnName = "sku_id")
	private transient ProductSku productSkuID;
	@SerializedName("productSkuCode")
	private String productSkuCode;
	@SerializedName("productPurchasePrice")
	@DatabaseField(columnName = "product_purchase_price")
	private float productPurchasePrice;
	@SerializedName("productDiscount")
	@DatabaseField(columnName = "product_discount")
	private float productDiscount;
	@SerializedName("isPaid")
	@DatabaseField(columnName = "is_paid")
	private boolean isPaid;
    @DatabaseField(columnName="lastmodified_timestamp", version=true, dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
	private Date lastModifiedTimestamp;

	public int getOrderDetailID() {
		return orderDetailID;
	}

	public void setOrderDetailID(int orderDetailID) {
		this.orderDetailID = orderDetailID;
	}

	public int getOrderProductPendingQty() {
		return orderProductPendingQty;
	}

	public void setOrderProductPendingQty(int orderProductPendingQty) {
		this.orderProductPendingQty = orderProductPendingQty;
	}

	public int getOrderProductToOrderQty() {
		return orderProductToOrderQty;
	}

	public void setOrderProductToOrderQty(int orderProductToOrderQty) {
		this.orderProductToOrderQty = orderProductToOrderQty;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public ProductSku getProductSkuID() {
		return productSkuID;
	}

	public void setProductSkuID(ProductSku productSkuID) {
		this.productSkuID = productSkuID;
	}

	public float getProductPurchasePrice() {
		return productPurchasePrice;
	}

	public void setProductPurchasePrice(float productPurchasePrice) {
		this.productPurchasePrice = productPurchasePrice;
	}

	public float getProductDiscount() {
		return productDiscount;
	}

	public void setProductDiscount(float productDiscount) {
		this.productDiscount = productDiscount;
	}

	public int getProductBilledQty() {
		return productBilledQty;
	}

	public void setProductBilledQty(int productBilledQty) {
		this.productBilledQty = productBilledQty;
	}

	public int getProductToReceiveQty() {
		return productToReceiveQty;
	}

	public void setProductToReceiveQty(int productToReceiveQty) {
		this.productToReceiveQty = productToReceiveQty;
	}

	public int getProductReceivedQty() {
		return productReceivedQty;
	}

	public void setProductReceivedQty(int productReceivedQty) {
		this.productReceivedQty = productReceivedQty;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getProductSkuCode() {
		return productSkuCode;
	}

	public void setProductSkuCode(String productSkuCode) {
		this.productSkuCode = productSkuCode;
	}

	public boolean isPaid() {
		return isPaid;
	}

	public void setPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}
}
