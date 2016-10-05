package com.sysfore.azurepricedetails.modal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sysfore.azurepricedetails.model.BillingPojo;
import com.sysfore.azurepricedetails.model.CalculationPojo;

public class DataProcess {
	
	public Map<String, List<BillingPojo>> processServiceResources(List<BillingPojo> billingPojoList) {
		
		Map<String, List<BillingPojo>> billingPojoMap = new HashMap<String, List<BillingPojo>>();
		for(BillingPojo billingPojo: billingPojoList) {
			List<BillingPojo> resorceList = billingPojoMap.get(billingPojo.getServiceResource());
			if(resorceList != null && resorceList.size() != 0) {
				resorceList.add(billingPojo);
			} else {
				resorceList = new ArrayList<BillingPojo>();
				resorceList.add(billingPojo);
				billingPojoMap.put(billingPojo.getServiceResource(), resorceList);
			}
		}
		
		return billingPojoMap;
	}
	
	/**
	 * Calculating the total price of usage 
	 */
	public Map<String, List<CalculationPojo>> calculateAzureUsage(Map<String, List<BillingPojo>> billingMap) {
		
		Map<String, List<CalculationPojo>> usageSummary = new HashMap<String, List<CalculationPojo>>();
		
		for(String key : billingMap.keySet()) {
			List<BillingPojo> resourceGroupList = billingMap.get(key);
			for(BillingPojo billingPojo : resourceGroupList) {
				
				List<CalculationPojo> calculationList =  usageSummary.get(key);
				if(calculationList != null && calculationList.size()> 0) {
					boolean verifier = false;
					for(CalculationPojo calculationPojo : calculationList) {
						if(calculationPojo.getProduct().equals(billingPojo.getProduct())) {
							double value = this.parseToDoubleValue(billingPojo.getResourceQtyConsumed());
							//System.out.println("Value  "+value);
							calculationPojo.setSum((calculationPojo.getSum()+value));
							verifier = true;
							//break;
						}
					}
					
					if(verifier == false) {
						CalculationPojo cp= new CalculationPojo();
						cp.setServiceResource(billingPojo.getServiceResource());
						cp.setProduct(billingPojo.getProduct());
						double value = this.parseToDoubleValue(billingPojo.getResourceQtyConsumed());
						cp.setSum(value);
						calculationList.add(cp);
						
					}
					
					verifier = false;
					
				}else {
					CalculationPojo cp= new CalculationPojo();
					cp.setServiceResource(billingPojo.getServiceResource());
					cp.setProduct(billingPojo.getProduct());
					double value = this.parseToDoubleValue(billingPojo.getResourceQtyConsumed());
					cp.setSum(value);
					List<CalculationPojo> calculationPojoList = new ArrayList<CalculationPojo>();
					calculationPojoList.add(cp);
					usageSummary.put(billingPojo.getServiceResource(), calculationPojoList);
				}
											
			}
		}
		
		return usageSummary;
		
	}
	
	
	public Map<String, List<CalculationPojo>> calculateAzureComponentUsage(Map<String, List<BillingPojo>> billingMap) {
		
		Map<String, List<CalculationPojo>> usageSummary = new HashMap<String, List<CalculationPojo>>();
		
		List<BillingPojo>  list = new ArrayList<BillingPojo>();
		for(String key : billingMap.keySet()) {
			list = billingMap.get(key);
			for(BillingPojo billingPojo : list) {
				if(usageSummary.size() > 0) {
					if(usageSummary.containsKey(billingPojo.getProduct())) {
						
						double value = this.parseToDoubleValue(billingPojo.getResourceQtyConsumed());
						//System.out.println("Value  "+value);
						List<CalculationPojo> calcPojoList = usageSummary.get(billingPojo.getProduct());
						CalculationPojo cp = calcPojoList.get(0);
						cp.setSum((cp.getSum()+value));
						usageSummary.remove(billingPojo.getProduct());
						List<CalculationPojo> calculationPojoList = new ArrayList<CalculationPojo>();
						calculationPojoList.add(cp);
						usageSummary.put(billingPojo.getProduct(), calculationPojoList);
						
					}else {
						
						CalculationPojo cp = new CalculationPojo();
						cp.setProduct(billingPojo.getProduct());
						cp.setComponent(key);
						cp.setServiceResource(billingPojo.getServiceResource());
						double value = this.parseToDoubleValue(billingPojo.getResourceQtyConsumed());
						cp.setSum(value);
						List<CalculationPojo> calculationPojoList = new ArrayList<CalculationPojo>();
						calculationPojoList.add(cp);
						usageSummary.put(billingPojo.getProduct(), calculationPojoList);
						
					}
					
				}else {
					CalculationPojo cp = new CalculationPojo();
					cp.setProduct(billingPojo.getProduct());
					cp.setComponent(key);
					cp.setServiceResource(billingPojo.getServiceResource());
					double value = this.parseToDoubleValue(billingPojo.getResourceQtyConsumed());
					cp.setSum(value);
					List<CalculationPojo> calculationPojoList = new ArrayList<CalculationPojo>();
					calculationPojoList.add(cp);
					usageSummary.put(billingPojo.getProduct(), calculationPojoList);
					
				}
			
		
			}
		}
		
		
		return usageSummary;
		
	}
	
	/**
	 * Make the cost in double variable
	 */
	private double parseToDoubleValue(String value) {
		double parseValue = 0.0;
		try {
			parseValue = Double.parseDouble(value);
		}catch(NumberFormatException e) {
			parseValue = 0.00;
		}
		
		return parseValue;
	}

}
