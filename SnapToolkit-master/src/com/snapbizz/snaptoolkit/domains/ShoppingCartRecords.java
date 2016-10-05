package com.snapbizz.snaptoolkit.domains;
import java.util.List;


public class ShoppingCartRecords {

	private List<ShoppingCart> shoppingCartList;

	public ShoppingCartRecords() {

	}

	public List<ShoppingCart> getBillItemList() {
		return shoppingCartList;
	}

	public void setBillItemList(List<ShoppingCart> shoppingList) {
		this.shoppingCartList = shoppingList;
	}

}
