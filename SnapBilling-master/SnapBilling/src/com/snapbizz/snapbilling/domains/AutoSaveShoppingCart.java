package com.snapbizz.snapbilling.domains;

import java.util.ArrayList;

import android.content.Context;

import com.snapbizz.snaptoolkit.domains.ShoppingCart;

public class AutoSaveShoppingCart {
	private Context mCon = null;
	private ArrayList<ShoppingCart> shoppingCartList = null;
	private ShoppingCart shoppingcart = null;

	AutoSaveShoppingCart(ArrayList<ShoppingCart> shoppingList, Context con) {
		mCon = con;
		this.shoppingcart = shoppingcart;
		shoppingCartList = shoppingList;
	}

}
