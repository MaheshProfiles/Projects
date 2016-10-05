package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName = "company")
public class Company {

    @SerializedName("companyName")
	@DatabaseField(columnName = "company_name")
	private String companyName;
    @SerializedName("tabletDbId")
	@DatabaseField(columnName = "company_id", generatedId=true, allowGeneratedIdInsert=true)
	private int companyId;
	@SerializedName("companyImageUrl")
	@DatabaseField(columnName = "company_imageurl")
	private String companyImageUrl;
    @DatabaseField(columnName="lastmodified_timestamp", version=true, dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
	private Date lastModifiedTimestamp;
    @DatabaseField(columnName="has_image")
    private boolean hasImage;
    @DatabaseField(columnName="is_gdb")
    private boolean isGDB;
    
    private transient boolean isSelected;
	private transient boolean isHeader;
	private transient String headerValue;
	private transient String companyBrandData;

	public Company(){
		
	}

    public boolean isGDB() {
        return isGDB;
    }

    public void setGDB(boolean isGDB) {
        this.isGDB = isGDB;
    }
    
    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }
    
	public Date getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }
	
	public Company(String header) {
		this.headerValue = header;
		isHeader = true;
	}

	public String getCompanyBrandData() {
		return companyBrandData;
	}

	public void setCompanyBrandData(String companyBrandData) {
		this.companyBrandData = companyBrandData;
	}
	
	public String getCompanyImageUrl() {
		return companyImageUrl;
	}

	public void setCompanyImageUrl(String companyImageUrl) {
		this.companyImageUrl = companyImageUrl;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public boolean isHeader() {
		return isHeader;
	}

	public void setHeader(boolean isHeader) {
		this.isHeader = isHeader;
	}

	public String getHeaderValue() {
		return headerValue;
	}

	public void setHeaderValue(String headerValue) {
		this.headerValue = headerValue;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return companyName;
	}
}
