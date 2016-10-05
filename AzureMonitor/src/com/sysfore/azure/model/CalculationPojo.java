package com.sysfore.azure.model;

public class CalculationPojo {
	private String serviceResource;
	private String product;
	private double sum;
	public String getServiceResource() {
		return serviceResource;
	}
	public void setServiceResource(String serviceResource) {
		this.serviceResource = serviceResource;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public double getSum() {
		return sum;
	}
	public void setSum(double sum) {
		this.sum = sum;
	}
    
	public String toString()  {
		return "Service resources : "+serviceResource+" Product : "+product+
				" Sum : "+sum;				
		}
}
