package com.sysfore.azure.model;

public class AddNewRole  {
	
	private String ID;
	private String rolename;
	private String accountinfo;
	private String resourcesummary;
	private String vmdetail;
	private String vmusagedetail;
	private String usagedetail;
	private String createdby;
	
	
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getRolename() {
		return rolename;
	}
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
	public String getAccountinfo() {
		return accountinfo;
	}
	public void setAccountinfo(String accountinfo) {
		this.accountinfo = accountinfo;
	}
	public String getResourcesummary() {
		return resourcesummary;
	}
	public void setResourcesummary(String resourcesummary) {
		this.resourcesummary = resourcesummary;
	}
	public String getVmdetail() {
		return vmdetail;
	}
	public void setVmdetail(String vmdetail) {
		this.vmdetail = vmdetail;
	}
	public String getVmusagedetail() {
		return vmusagedetail;
	}
	public void setVmusagedetail(String vmusagedetail) {
		this.vmusagedetail = vmusagedetail;
	}
	public String getUsagedetail() {
		return usagedetail;
	}
	public void setUsagedetail(String usagedetail) {
		this.usagedetail = usagedetail;
	}
	
	public String getCreatedby() {
		return createdby;
	}
	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}
	
	
	public String toString() {
		return this.rolename;
		
	}
	
	

}
