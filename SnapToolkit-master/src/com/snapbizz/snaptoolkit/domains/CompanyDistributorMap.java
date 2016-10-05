
package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="distributor_company_map")
public class CompanyDistributorMap {
    
	@DatabaseField(columnName="company_id", foreign=true, foreignColumnName="company_id")
	private Company company;
	@DatabaseField(columnName="distributor_id", foreign=true, foreignColumnName="distributor_id")
	private Distributor distributor;
    @SerializedName("tabletDbId")
	@DatabaseField(generatedId=true, columnName="slno", allowGeneratedIdInsert=true)
	private int slNo;
	
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}
	public Distributor getDistributor() {
		return distributor;
	}
	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}
	public int getSlNo() {
		return slNo;
	}
	public void setSlNo(int slNo) {
		this.slNo = slNo;
	}
	
	

}
