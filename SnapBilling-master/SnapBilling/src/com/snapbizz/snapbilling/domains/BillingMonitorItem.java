package com.snapbizz.snapbilling.domains;

import com.snapbizz.snapbilling.adapters.BillListAdapter;
import com.snapbizz.snaptoolkit.domains.ShoppingCart;

public class BillingMonitorItem {

	private ShoppingCart billingMonitorShoppingCart;
	private BillListAdapter billingMonitorBillListAdapter;
	private boolean isCustomerExpanded = false;
	private String customerSearchString;
	
	public String getCustomerSearchString() {
		return customerSearchString;
	}

	public void setCustomerSearchString(String customerSearchString) {
		this.customerSearchString = customerSearchString;
	}

	public boolean isCustomerExpanded() {
		return isCustomerExpanded;
	}

	public void setCustomerExpanded(boolean isCustomerExpanded) {
		this.isCustomerExpanded = isCustomerExpanded;
	}

	public BillingMonitorItem(ShoppingCart shoppingCart) {
		this.billingMonitorShoppingCart = shoppingCart;
	}
	
	public BillListAdapter getBillingMonitorBillListAdapter() {
		return billingMonitorBillListAdapter;
	}

	public void setBillingMonitorBillListAdapter(
			BillListAdapter billingMonitorBillListAdapter) {
		this.billingMonitorBillListAdapter = billingMonitorBillListAdapter;
	}

	public ShoppingCart getShoppingCart() {
		return billingMonitorShoppingCart;
	}
	
	public void setShoppingCart(ShoppingCart shoppingCart) {
		this.billingMonitorShoppingCart = shoppingCart;
	}
	
}
