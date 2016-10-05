package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName = "distributor_brand_map")
public class DistributorBrandMap {
    @SerializedName("tabletDbId")
	@DatabaseField(columnName = "distributor_brand_id", generatedId = true, allowGeneratedIdInsert=true)
	private int distributorBrandId;
	@DatabaseField(columnName = "distributor_id", foreign = true, foreignColumnName = "distributor_id")
	private transient Distributor distributor;
	@SerializedName("distributorId")
	private int distributorId;
	@DatabaseField(columnName = "brand_id", foreign = true, foreignColumnName = "brand_id")
	private transient Brand distributorBrand;
	@SerializedName("brandId")
	private int brandId;
    @DatabaseField(columnName="lastmodified_timestamp", version=true, dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
	private Date lastModifiedTimestamp;

	public int getDistributorBrandId() {
		return distributorBrandId;
	}

	public void setDistributorBrandId(int distributorBrandId) {
		this.distributorBrandId = distributorBrandId;
	}
	public int getDistributorId() {
		return distributorId;
	}

	public void setDistributorId(int distributorId) {
		this.distributorId = distributorId;
	}

	public int getBrandId() {
		return brandId;
	}

	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}
	
	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}

	public Brand getDistributorBrand() {
		return distributorBrand;
	}

	public void setDistributorBrand(Brand distributorBrand) {
		this.distributorBrand = distributorBrand;
	}

}
