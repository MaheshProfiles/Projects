package com.sysfore.azurepricedetails.model;

public class CalculationPojo {
	private String serviceResource;
	private String product;
	private double sum;
	private String component = "";

	/**
	 * @param ServiceResource
	 */
	public String getServiceResource() {
		return serviceResource;
	}

	/**
	 * @return ServiceResource
	 */
	public void setServiceResource(String serviceResource) {
		this.serviceResource = serviceResource;
	}

	/**
	 * @param Product
	 */
	public String getProduct() {
		return product;
	}

	/**
	 * @return Product
	 */
	public void setProduct(String product) {
		this.product = product;
	}

	/**
	 * @param Sum
	 */
	public double getSum() {
		return sum;
	}

	/**
	 * @return Sum
	 */
	public void setSum(double sum) {
		this.sum = sum;
	}
	

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	/**
	 * @return
	 * @param product
	 * @param serviceResource
	 */
	public String toString()  {
		return "Service resources : "+serviceResource+" Product : "+product+
				" Sum : "+sum+" Component "+component;				
		}
}
