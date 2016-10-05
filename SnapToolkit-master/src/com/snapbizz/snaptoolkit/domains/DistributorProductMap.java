package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName = "distributor_product_map")
public class DistributorProductMap {
    @SerializedName("tabletDbId")
	@DatabaseField(columnName = "distributor_product_slno", generatedId = true, allowGeneratedIdInsert=true)
	private int distributorProductId;
	@DatabaseField(columnName = "distributor_id", foreign = true, foreignColumnName = "distributor_id")
	private transient Distributor distributor;
	@SerializedName("distributorId")
	private int distributorId;
	@DatabaseField(columnName = "sku_id", foreign = true, foreignColumnName = "sku_id")
	private transient ProductSku distributorProductSku;
	@SerializedName("skuId")
	private String skuId;
    @DatabaseField(columnName="lastmodified_timestamp", version=true, dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
	private Date lastModifiedTimestamp;

	public int getDistributorProductSkuId() {
		return distributorProductId;
	}

	public void setDistributorProductSkuId(int distributorProductSkuId) {
		this.distributorProductId = distributorProductSkuId;
	}
	public int getDistributorId() {
		return distributorId;
	}

	public void setDistributorId(int distributorId) {
		this.distributorId = distributorId;
	}

	public String getProductSkuId() {
		return skuId;
	}

	public void setProductSkuId(String productSkuId) {
		this.skuId = productSkuId;
	}
	
	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}

	public ProductSku getDistributorProductSku() {
		return distributorProductSku;
	}

	public void setDistributorProductSku(ProductSku distributorProductSku) {
		this.distributorProductSku = distributorProductSku;
	}

}
