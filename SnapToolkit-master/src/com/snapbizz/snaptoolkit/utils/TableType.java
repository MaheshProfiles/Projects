package com.snapbizz.snaptoolkit.utils;

public enum TableType {

    INVENTORY_BATCh("/remove_inventory_batches"),
	INVENTORY_SKU("/remove_inventories"), 
	PRODUCT_SKU("/remove_product_skus"), 
	BRAND("/remove_brands"),
    COMPANY("/remove_companies"),
    DISTRIBUTOR("/remove_distributors"),
    PAYMENTS("/remove_payments"),
	TRANSACTIONS("/remove_customer_transactions"),
	DISTRIBUTOR_BRAND_MAP("/remove_distributor_brand_maps"),
	CUSTOMER_PAYMENT("/remove_customer_payments"),
    DISTRIBUTOR_PRODUCT_MAP("/remove_distributor_product_map");

	private String tableType;

	private TableType(String tableType){
		this.tableType = tableType;
	}
	
	public String getTableType(){
		return this.tableType;
	}
	
	public static TableType getTableEnum(String tableValue){
		for (TableType tableType : TableType.values()) {
			if(tableType.tableType.equals(tableValue))
				return tableType;
		}
		return null;
	}
}
