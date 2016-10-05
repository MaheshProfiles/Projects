package com.snapbizz.snaptoolkit.domains;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

@DatabaseTable(tableName="distributor")
public class Distributor {

    @SerializedName("tabletDbId")
	@DatabaseField(columnName="distributor_id", generatedId=true, allowGeneratedIdInsert=true)
	private int distributorId;
	@SerializedName("agencyName")
	@DatabaseField(columnName="agency_name")
	private String agencyName;
	@SerializedName("salesmanName")
	@DatabaseField(columnName="salesman_name")
	private String salesmanName;
	@SerializedName("city")
	@DatabaseField(columnName="city")
	private String city;
	@SerializedName("address")
	@DatabaseField(columnName="address")
	private String address;
	@SerializedName("pincode")
	@DatabaseField(columnName="pincode")
	private String pincode;
	@SerializedName("tinNumber")
	@DatabaseField(columnName="tin_number")
	private String tinNumber;
	@SerializedName("phoneNumber")
	@DatabaseField(columnName="phone_number")
	private String phoneNumber;
	@SerializedName("creditLimit")
	@DatabaseField(columnName="credit_limit")
	private float creditLimit;
    @DatabaseField(columnName="lastmodified_timestamp", version=true, dataType = DataType.DATE_STRING, format=SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
	private Date lastModifiedTimestamp;
    private transient String header;
	private transient boolean isHeader;
	private transient String companyNames;
	
	public Distributor() {
		
	}
	
	public Distributor(String header) {
		this.header = header;
		isHeader = true;
	}
	
	public Distributor(String agencyName, String salesmanName, String city, String address, String pinCode, String tinNumber, String phoneNumber, float creditLimit) {
		this.agencyName = agencyName;
		this.salesmanName = salesmanName;
		this.city = city;
		this.address = address;
		this.pincode = pinCode;
		this.tinNumber = tinNumber;
		this.phoneNumber = phoneNumber;
		this.creditLimit = creditLimit;
	}

    public Date getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    public void setLastModifiedTimestamp(Date lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }
	public int getDistributorId() {
		return distributorId;
	}
	public void setDistributorId(int distributorId) {
		this.distributorId = distributorId;
	}
	public String getAgencyName() {
		return agencyName;
	}
	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}
	public String getSalesmanName() {
		return salesmanName;
	}
	public void setSalesmanName(String salesmanName) {
		this.salesmanName = salesmanName;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	public String getTinNumber() {
		return tinNumber;
	}
	public void setTinNumber(String tinNumber) {
		this.tinNumber = tinNumber;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public float getCreditLimit() {
		return creditLimit;
	}
	public void setCreditLimit(float creditLimit) {
		this.creditLimit = creditLimit;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public boolean getIsHeader() {
		return isHeader;
	}

	public void setIsHeader(boolean isHeader) {
		this.isHeader = isHeader;
	}

	public String getCompanyNames() {
		return companyNames;
	}

	public void setCompanyNames(String companyNames) {
		this.companyNames = companyNames;
	}
	
}
