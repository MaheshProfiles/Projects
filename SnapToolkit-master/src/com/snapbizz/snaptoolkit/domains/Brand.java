package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName="brand")
public class Brand {
    
    @SerializedName("tabletDbId")
	@DatabaseField(columnName="brand_id", generatedId=true, allowGeneratedIdInsert=true)
	private int brandId;
	@SerializedName("brandName")
	@DatabaseField(columnName="brand_name", unique=true)
	private String brandName;
	@SerializedName("brandImageUrl")
	@DatabaseField(columnName="brand_imageurl")
	private String brandImageUrl;
	private transient boolean isSelected = false;
    @DatabaseField(columnName="lastmodified_timestamp", version=true, dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
	private Date lastModifiedTimestamp;
    @DatabaseField(columnName="has_image")
    private boolean hasImage;
    @DatabaseField(columnName="company_id", foreign=true)
    private transient Company company;
    @SerializedName("company_id")
    private int companyId;
    @DatabaseField(columnName="is_gdb")
    private boolean isGDB;
    @DatabaseField(columnName="is_mystore")
    private boolean isMyStore;

    public boolean isMyStore() {
        return isMyStore;
    }

    public void setMyStore(boolean isMyStore) {
        this.isMyStore = isMyStore;
    }

    public boolean isGDB() {
        return isGDB;
    }

    public void setGDB(boolean isGDB) {
        this.isGDB = isGDB;
    }
    
    public Date getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }
    
	public int getCompnayId() {
        return companyId;
    }

    public void setCompnayId(int compnayId) {
        this.companyId = compnayId;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public int getBrandId() {
		return brandId;
	}

	public String getBrandImageUrl() {
		return brandImageUrl;
	}

	public void setBrandImageUrl(String brandImageUrl) {
		this.brandImageUrl = brandImageUrl;
	}

	public void setBrandId(int brandId) {
		this.brandId = brandId;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
	return brandName;
	}	
	
}
