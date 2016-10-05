package com.sysfore.azurepricedetails.model;

import java.util.ArrayList;
import java.util.List;

public class ResultPojo {

	public String product;
	public String unitofMeasure;
	public String unitConsumed;
	public String perUnitCommitted;
	public String billTotal;
	public String month;
	public String year;
	public String subcriptionId;
	public String subcriptionName;
	public String createdby;
	public String component = "";
	private List<ResultPojo> childtList = new ArrayList<ResultPojo>();
//	private List<ResultPojo> HeaderList = new ArrayList<ResultPojo>();

/*	public ResultPojo(List<ResultPojo> HeaderList,
			List<ResultPojo> childtList) {
		// TODO Auto-generated constructor stub
			super();
		  this.HeaderList = HeaderList;
		  this.childtList = childtList;
	}

	
	public ResultPojo() {
		// TODO Auto-generated constructor stub
	}
*/

	/**
	 * @param product
	 */
	public String getProduct() {
		return product;
	}

	/**
	 * @return product
	 */

	public void setProduct(String product) {
		this.product = product;
	}

	/**
	 * @param unitofMeasure
	 */

	public String getUnitofMeasure() {
		return unitofMeasure;
	}

	/**
	 * @return unitofMeasure
	 */

	public void setUnitofMeasure(String unitofMeasure) {
		this.unitofMeasure = unitofMeasure;
	}

	/**
	 * @param unitConsumed
	 */
	public String getUnitConsumed() {
		return unitConsumed;
	}

	/**
	 * @return unitConsumed
	 */
	public void setUnitConsumed(String unitConsumed) {
		this.unitConsumed = unitConsumed;
	}

	/**
	 * @param perUnitCommitted
	 */

	public String getPerUnitCommitted() {
		return perUnitCommitted;
	}

	/**
	 * @return perUnitCommitted
	 */
	public void setPerUnitCommitted(String perUnitCommitted) {
		this.perUnitCommitted = perUnitCommitted;
	}

	/**
	 * @param billTotal
	 */

	public String getBillTotal() {
		return billTotal;
	}

	/**
	 * @return billTotal
	 */
	public void setBillTotal(String billTotal) {
		this.billTotal = billTotal;
	}

	/**
	 * @param month
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * @return month
	 */
	public void setMonth(String month) {
		this.month = month;
	}

	/**
	 * @param year
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @return year
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * @param subcriptionId
	 */
	public String getSubcriptionId() {
		return subcriptionId;
	}

	/**
	 * @return subcriptionId
	 */

	public void setSubcriptionId(String subcriptionId) {
		this.subcriptionId = subcriptionId;
	}

	/**
	 * @param createdby
	 */
	public String getCreatedby() {
		return createdby;
	}

	/**
	 * @return createdby
	 */
	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	

	/**
	 * @param subcriptionName
	 */
	public String getSubcriptionName() {
		return subcriptionName;
	}

	/**
	 * @return subcriptionName
	 */
	public void setSubcriptionName(String subcriptionName) {
		this.subcriptionName = subcriptionName;
	}

	/**
	 * @param Component
	 */
	public String getComponent() {
		return component;
	}

	/**
	 * @return Component
	 */
	public void setComponent(String component) {
		this.component = component;
	}
	
	/*public List<ResultPojo> getChildtList() {
		return childtList;
	}


	public void setChildtList(List<ResultPojo> childtList) {
		this.childtList = childtList;
	}


	public List<ResultPojo> getHeaderList() {
		return HeaderList;
	}


	public void setHeaderList(List<ResultPojo> headerList) {
		HeaderList = headerList;
	}*/

	
	 /*public String toString() { return
	  "Component : "+component+" Product : "+product
	  +" Unit Of Measure : "+unitofMeasure+
	  " Commitment Unit Price : "+perUnitCommitted
	  +" Unit Consumed : "+unitConsumed+ " Bill Total : "+billTotal;
	  
	  }*/
	
	/*@Override
	public String toString() {
		return "ResultPojo [product=" + product + ", unitofMeasure="
				+ unitofMeasure + ", unitConsumed=" + unitConsumed
				+ ", perUnitCommitted=" + perUnitCommitted + ", billTotal="
				+ billTotal + ", month=" + month + ", year=" + year
				+ ", subcriptionId=" + subcriptionId + ", subcriptionName="
				+ subcriptionName + ", createdby=" + createdby + ", component="
				+ component + "]";
	}
	*/
	
	/*public String toString() { return
	  "Component : "+component+" Product : "+product
	  + " Bill Total : "+billTotal;
	  
	  }*/
	

	public String toString() {
		return this.subcriptionName;
		
	}
	
	 public String toString1() {
	    	return "product "+product+ " unitofMeasure "+unitofMeasure +" unitConsumed "+unitConsumed+"perUnitCommitted"+perUnitCommitted;
	    }

	public List<ResultPojo> getChildtList() {
		return childtList;
	}

	public void setChildtList(List<ResultPojo> childtList) {
		this.childtList = childtList;
	}

	
	

	 

}
