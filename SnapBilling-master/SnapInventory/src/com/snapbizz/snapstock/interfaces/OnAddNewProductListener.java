package com.snapbizz.snapstock.interfaces;

import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.InventorySku;

public interface OnAddNewProductListener {
	public void showAddProductLayout(String barcode);
	public void showAddProductLayout(InventorySku inventorySku);
	public void  showAddProductLayout(String barcode,Distributor distributor);
}