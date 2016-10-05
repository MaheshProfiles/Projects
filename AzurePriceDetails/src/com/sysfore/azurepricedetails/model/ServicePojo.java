package com.sysfore.azurepricedetails.model;

public class ServicePojo {
	private String service;
	private String unitOfMeasure;
	private String commitmentUnitPrice;
	private String commitmentDiscount;

	/**
	 * @param service
	 */
	public String getService() {
		return service;
	}

	/**
	 * @return service
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * @param unitOfMeasure
	 */
	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	/**
	 * @return unitOfMeasure
	 */
	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	/**
	 * @param CommitmentUnitPrice
	 */
	public String getCommitmentUnitPrice() {
		return commitmentUnitPrice;
	}

	/**
	 * @return CommitmentUnitPrice
	 */
	public void setCommitmentUnitPrice(String commitmentUnitPrice) {
		this.commitmentUnitPrice = commitmentUnitPrice;
	}

	/**
	 * @param CommitmentDiscount
	 */
	public String getCommitmentDiscount() {
		return commitmentDiscount;
	}

	/**
	 * @return CommitmentDiscount
	 */
	public void setCommitmentDiscount(String commitmentDiscount) {
		this.commitmentDiscount = commitmentDiscount;
	}

	/**
	 * @return
	 * @param CommitmentDiscount
	 * @param CommitmentUnitPrice
	 * @param unitOfMeasure
	 */
	public String toString() {
		return "Service : " + service + " Unit Of Measure : " + unitOfMeasure
				+ " Commitment Unit Price : " + commitmentUnitPrice
				+ " Commitment Discount : " + commitmentDiscount;

	}

}
