package com.sysfore.azurepricedetails.model;

public class AddNewRole {

	private String ID;
	private String rolename;
	private String accountinfo;
	private String resourcesummary;
	private String vmdetail;
	private String vmusagedetail;
	private String usagedetail;
	private String createdby;

	/**
	 * @param ID
	 */

	public String getID() {
		return ID;
	}

	/**
	 * @return ID
	 */
	public void setID(String iD) {
		ID = iD;
	}

	/**
	 * @param Rolename
	 */
	public String getRolename() {
		return rolename;
	}

	/**
	 * @return Rolename
	 */
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	/**
	 * @param Accountinfo
	 */
	public String Accountinfo() {
		return accountinfo;
	}

	/**
	 * @return Accountinfo
	 */
	public void setAccountinfo(String accountinfo) {
		this.accountinfo = accountinfo;
	}

	/**
	 * @param Resourcesummary
	 */
	public String getResourcesummary() {
		return resourcesummary;
	}

	/**
	 * @return Resourcesummary
	 */
	public void setResourcesummary(String resourcesummary) {
		this.resourcesummary = resourcesummary;
	}

	/**
	 * @param Vmdetail
	 */
	public String getVmdetail() {
		return vmdetail;
	}

	/**
	 * @return Vmdetail
	 */
	public void setVmdetail(String vmdetail) {
		this.vmdetail = vmdetail;
	}

	/**
	 * @param Vmusagedetail
	 */
	public String getVmusagedetail() {
		return vmusagedetail;
	}

	/**
	 * @return Vmusagedetail
	 */
	public void setVmusagedetail(String vmusagedetail) {
		this.vmusagedetail = vmusagedetail;
	}

	/**
	 * @param Usagedetail
	 */
	public String getUsagedetail() {
		return usagedetail;
	}

	/**
	 * @return Usagedetail
	 */
	public void setUsagedetail(String usagedetail) {
		this.usagedetail = usagedetail;
	}

	/**
	 * @param Createdby
	 */
	public String getCreatedby() {
		return createdby;
	}

	/**
	 * @return Createdby
	 */
	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	/**
	 * @return
	 * @param rolename
	 */
	public String toString() {
		return this.rolename;

	}

}
