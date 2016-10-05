package com.snapbizz.snaptoolkit.domains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import com.snapbizz.snaptoolkit.db.ProductPacksHelper;
import com.snapbizz.snaptoolkit.db.dao.Customers;
import com.snapbizz.snaptoolkit.db.dao.ProductPacks;
import com.snapbizz.snaptoolkit.domainsV2.Models;
import com.snapbizz.snaptoolkit.domainsV2.Models.ProductInfo;
import com.snapbizz.snaptoolkit.domainsV2.Models.UOM;
import com.snapbizz.snaptoolkit.utils.SkuUnitType;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;

public class ShoppingCart {

	private static final String TAG = "[SnapBizz.ShoppingCart]";
	private final int shoppingCartId;
	private LongSparseArray<Models.BillItem> cartItems;			/*< HashMap of barcodes/gids to cartItems */
	private HashMap<String, Integer> productSkuMap;
	private HashMap<String, String> cartStartMap;
	private Customers customer;
	private Distributor distributor;
	public int totalCartValue;
	public int totalDiscount;
	public int totalSavings;
	public int cashPaid;
	public int totalVatAmount;
	public HashMap<String, Integer> vatRates;
	
	private int totalQty;
	private int lastSelectedItemPosition = -1;
	private int invoiceNumber;
	private boolean isInvoice;
	private boolean isVATCalculated;
	private boolean isFromBillingHistory;
	private String retriveSelection;
	private String receiveInvoiceNumber;
	private Context context;
	private boolean bReturnChange;
	private Date billingStartedAt = null;


	public Date getBillingStartedAt() {
		return billingStartedAt;
	}

	// TODO: Remove this
	public boolean isCustomerPayment() {
		return !bReturnChange;
	}
	
	public void setCustomerPayment(boolean isCustomerPayment) {
		this.bReturnChange = !isCustomerPayment;
	}
	
	public boolean isReturnChange() {
		return bReturnChange;
	}
	
	public void setReturnChange(boolean bReturnChange) {
		this.bReturnChange = bReturnChange;
	}

	public int netValue() {
		return totalCartValue - totalSavings - totalDiscount;
	}
	
	public int creditValue() {
		if(isCredit())
			return netValue() - cashPaid;
		return 0;
	}
	
	public boolean isCredit() {
		return cashPaid < netValue();
	}

	public boolean isFromBillingHistory() {
		return isFromBillingHistory;
	}

	public void setFromBillingHistory(boolean isFromBillingHistory) {
		this.isFromBillingHistory = isFromBillingHistory;
	}

	public int getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(int invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public int getLastSelectedItemPosition() {
		return lastSelectedItemPosition;
	}

	public void setLastSelectedItemPosition(int lastSelectedItemPosition) {
		this.lastSelectedItemPosition = lastSelectedItemPosition;
	}

	private boolean isCreditChanged;
	

	public float getTotalVatAmount() {
		return Math.abs(this.totalVatAmount / 100.0f);
	}

	public String getReceiveInvoiceNumber() {
		return receiveInvoiceNumber;
	}

	public void setReceiveInvoiceNumber(String receiveInvoiceNumber) {
		this.receiveInvoiceNumber = receiveInvoiceNumber;
	}

	public boolean isCreditChanged() {
		return isCreditChanged;
	}

	public void setCreditChanged(boolean isCreditChanged) {
		this.isCreditChanged = isCreditChanged;
	}

	public float getCashPaid() {
		return cashPaid / 100.0f;
	}

	public void setCashPaid(float cashPaid) {
		this.cashPaid = (int)(cashPaid * 100);
	}

	public float getTotalSavings() {
		return totalSavings / 100.0f;
	}

	public int getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(int totalQty) {
		this.totalQty = totalQty;
	}
	
	public Customers getCustomer() {
		return customer;
	}

	public void setCustomer(Customers customer) {
		this.customer = customer;
	}
	
	public double getExtraPaid() {
		return (totalCartValue - totalDiscount - totalSavings - cashPaid);
	}

	public int getTotalLineItems() {
		if (cartItems == null)
			return 0;
		else
			return cartItems.size();
	}

	public ShoppingCart(int id) {
		this.shoppingCartId = id;
		this.cartItems = new LongSparseArray<Models.BillItem>();
		
		this.productSkuMap = new HashMap<String, Integer>();
		this.cartStartMap = new HashMap<String, String>();
		this.vatRates = new HashMap<String, Integer>();
	}

	public HashMap<String, Integer> getProductSkuMap() {
		return productSkuMap;
	}

	public void setProductSkuMap(HashMap<String, Integer> productSkuMap) {
		this.productSkuMap = productSkuMap;
	}

	public HashMap<String, String> getCartStartMap() {
		return cartStartMap;
	}

	public void setCartStartMap(HashMap<String, String> cartStartMap) {
		this.cartStartMap = cartStartMap;
	}

	public float getTotalCartValue() {
		return totalCartValue / 100.0f;
	}

	public float getTotalDiscount() {
		return totalDiscount / 100.0f;
	}

	public void setTotalDiscount(float totalDiscount) {
		this.totalDiscount = (int)(totalDiscount * 100);
	}

	public int getShoppingCartId() {
		return shoppingCartId;
	}

	public final LongSparseArray<Models.BillItem> getCartItems() {
		return cartItems;
	}
	
	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}
	
	private void resetTotalAmounts() {
		totalVatAmount = 0;
		totalSavings = 0;
		totalCartValue = 0;
		cashPaid = 0;
	}
	
	public void recalculateTotalAndVat() {
		vatRates = new HashMap<String, Integer>();
		resetTotalAmounts();
		totalQty = 0;
		for(int i = 0; i < cartItems.size(); i++) {
			Models.BillItem billItem = cartItems.valueAt(i);
			double qty = billItem.quantity;
			if((billItem.uom == UOM.G || billItem.uom == UOM.ML)) {
	        	qty = qty / 1000.0f;
	        }

			totalSavings += (billItem.mrp - billItem.sellingPrice) * qty;
			totalCartValue += billItem.mrp * qty;
			billItem.vatAmount = SnapCommonUtils.getVatAmount(billItem.product.vatRate,
					   										  billItem.sellingPrice,
					   										  qty, false);
			totalVatAmount += billItem.vatAmount; 
			if (billItem.uom == Models.UOM.PC)
				totalQty = totalQty + (int)Math.abs(billItem.quantity);
			else
				totalQty = totalQty + 1;
			vatSplitCalculation(billItem, billItem.sellingPrice, qty,
								true, vatRates, false, false);
		}

		// First item added - billing started now.
		if(getTotalLineItems() == 1)
			billingStartedAt = new Date();
	}
	
	public void updateBillItemPrices(Models.BillItem billItem, int spIndex) {
		if (spIndex < 0 || billItem.sps == null || spIndex >= billItem.sps.length)
			return;
		billItem.sellingPrice = billItem.sps[spIndex];
		billItem.spIndex = spIndex;
	}
	
	public void updateAllBillItemPrices(int spIndex) {
		for(int i = 0; i < cartItems.size(); i++) {
			Models.BillItem billItem = cartItems.valueAt(i);
			updateBillItemPrices(billItem, spIndex);
		}
		recalculateTotalAndVat();
	}
	
	public void updateBillItemPrices(int position, int spIndex) {
		Models.BillItem billItem = cartItems.valueAt(position);
		if(billItem != null)
			updateBillItemPrices(billItem, spIndex);
	}

	public void deleteCartItem(int position) {
		cartItems.removeAt(position);

		recalculateTotalAndVat();
		
		if(totalQty == 0) {
			resetTotalAmounts();
			this.totalDiscount = 0;
		}
	}

	public void setLastItemQty(float qty, SkuUnitType skuUnitType) {
		if (lastSelectedItemPosition == -1 || lastSelectedItemPosition >= cartItems.size())
			return;
		UOM unitType = UOM.getFromOldUnits(skuUnitType);
		Models.BillItem billItem = cartItems.get(lastSelectedItemPosition);
		
		/*
		 * Check if the new quantity is of the same group as that of
		 * existing item. E.g. if the existing quantity is G, new quantity
		 * cannot be L/ML/PC, but can be KG
		 */
		if (billItem.product.uom == UOM.G || billItem.product.uom == UOM.KG) {
			if (unitType != UOM.G && unitType != UOM.G)
				return;
		} else if (billItem.product.uom == UOM.L || billItem.product.uom == UOM.ML) {
			if (unitType != UOM.ML && unitType != UOM.L)
				return;
		} else if (unitType != UOM.PC) {
			return;
		}

		// Adjust selling prices in case of non-pc items.
		// Selling prices are always tied to product's uom
		if (unitType != billItem.product.uom) {
			if(billItem.product.uom == UOM.G || billItem.product.uom == UOM.ML)
				qty = qty * 1000;
			else
				qty = qty / 1000;
		}

		billItem.quantity = (int)qty;
		recalculateTotalAndVat();
	}
	
	private void updateBillItemPackSizes(Models.BillItem billItem) {
		List<ProductPacks> packs = billItem.packs;
		if(packs == null || packs.size() <= 0)
			return;
		ProductPacks selectedPack = packs.get(0);	// Pick the first one to begin with
		for(ProductPacks pack : packs) {
			if(pack.getPackSize() < billItem.quantity)
				break;
			selectedPack = pack;
		}
		billItem.sps = ProductPacksHelper.getSellingPricesAsArray(selectedPack, billItem.mrp);
		if(!billItem.sellingPriceEdited)
			billItem.sellingPrice = billItem.sps[billItem.spIndex];
	}

	/**
	 * Helps with user's manually editing the item.
	 * 
	 * Called from BillCheckoutFragment.
	 * 
	 * @param position
	 * @param qty
	 * @param mrp
	 * @param sellingPrice
	 */
	public void editCartItem(int position, float qty, int mrp, int sellingPrice, UOM uom) {
		if (mrp < sellingPrice)
			sellingPrice = mrp;

		Log.d(TAG, "editCartItem --KeyBoar-- qty--->= " + qty);
		if(uom == UOM.L || uom == UOM.KG)
			qty = qty * 1000;
		Models.BillItem billItem = cartItems.valueAt(position);
		billItem.quantity = (int)qty;
		updateBillItemPackSizes(billItem);

		billItem.mrpEdited = (billItem.mrp != mrp);
		billItem.sellingPriceEdited = (billItem.sellingPrice != sellingPrice);

		billItem.mrp = mrp;
		billItem.sellingPrice = sellingPrice;
		
		recalculateTotalAndVat();
	}

	public void returnCartItem(int position) {
		Models.BillItem billItem = cartItems.valueAt(position);
		billItem.quantity = -billItem.quantity;
		
		recalculateTotalAndVat();

	}

	public void editProductName(int position, String productName) {
		Models.BillItem billItem = cartItems.valueAt(position);
		if(billItem != null) {
			billItem.name = productName;
			billItem.nameEdited = true;
		}
	}
	
	public void editReceiveItems(int position, String productName,
								 String categoryName, String subCategoryName,
								 float vat, ProductCategory productCategory) {
		// TODO
		/*billItemList.get(position).setProductSkuName(productName);
		billItemList.get(position).getProductSku().setProductCategory(productCategory);
		billItemList.get(position).getProductSku().setProductCategoryName(categoryName);
		billItemList.get(position).getProductSku().setProductSubCategoryName(subCategoryName);
		billItemList.get(position).getProductSku().setVAT(vat);*/
	}

	public void deleteCart() {
		this.lastSelectedItemPosition = -1;
		this.cartItems.clear();
		this.productSkuMap.clear();
		this.customer = null;
		this.distributor = null;
		this.receiveInvoiceNumber = null;
		this.totalCartValue = 0;
		this.totalDiscount = 0;
		this.totalQty = 0;
		this.totalVatAmount = 0;
		this.totalSavings = 0;
		this.cashPaid = 0;
		this.vatRates = new HashMap<String, Integer>();
	}
	
	/*private void updateCartValue(Models.BillItem billItem, int quantity) {
		this.totalCartValue += billItem.sellingPrice * quantity;
		int priceDifference = (billItem.mrp - billItem.sellingPrice) * quantity;
		if (priceDifference > 0)
			this.totalSavings += priceDifference;
		else
			this.totalCartValue -= priceDifference;
	}*/
	
	private Integer[] addAlternativeMrps(Integer[] mrps, Integer[] newmrps) {
		if(mrps == null)
			return newmrps;
		if(newmrps == null)
			return mrps;
		ArrayList<Integer> combined = new ArrayList<Integer>(Arrays.asList(mrps));
		for(Integer mrp : newmrps) {
			if(!combined.contains(mrp))
				combined.add(mrp);
		}
		return (Integer[])combined.toArray();
	}

	public void addItemsToCart(ProductInfo productSku, List<ProductPacks> packs) {
		Models.BillItem billItem = cartItems.get(productSku.productGid);

		if (billItem != null) {
			billItem.quantity += (billItem.uom == UOM.PC) ? 1 : 1000;
			if(productSku.alternateMrps != null && billItem.product.alternateMrps.length > 0)
				billItem.product.alternateMrps = addAlternativeMrps(billItem.product.alternateMrps, productSku.alternateMrps);
			updateBillItemPackSizes(billItem);
		} else {
			billItem = new Models.BillItem();
			billItem.product = productSku;
			billItem.packs = packs;
			/* 
			 * Packs are sorted by in the order of quantity.
			 * If pack is available use the least quantity's SP1 by default.
			 * If not use MRP.
			 */
			if(packs == null || packs.size() <= 0)
				billItem.sellingPrice = billItem.product.mrp;
			else
				billItem.sellingPrice = packs.get(0).getSalePrice1();
			billItem.uom = (billItem.product.isPc || billItem.product.uom == UOM.PC) ? UOM.PC :
							(billItem.product.uom == UOM.G || billItem.product.uom == UOM.KG) ? UOM.G : UOM.ML;
			billItem.quantity = billItem.uom == UOM.PC ? 1 : 1000;
			billItem.mrp = billItem.product.mrp;
			billItem.spIndex = 1;
			billItem.name = (billItem.product.localName != null && !billItem.product.localName.isEmpty()) ?
																    billItem.product.localName : billItem.product.name;
			cartItems.append(productSku.productGid, billItem);
		}
		
		// TODO: Check this.
		retriveSelection = SnapSharedUtils.getLastStoredSelection(SnapCommonUtils.getSnapContext(context));
		setProductSKUSalePrice(billItem);

		lastSelectedItemPosition = cartItems.indexOfKey(productSku.productGid);
		recalculateTotalAndVat();
	}

	public void addQuickAddItemToCart(ProductSku productSku, float quantity, SkuUnitType skuUnitType) {
		/*if(this.billItemList != null&&this.billItemList.isEmpty()){
			String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
			cartStartMap.put(getShoppingCartId()+"", date);
			vatRates = new HashMap<String, Float>();
			
		}
		if (skuUnitType.ordinal() == SkuUnitType.ML.ordinal()
				|| skuUnitType.ordinal() == SkuUnitType.GM.ordinal()) {
			quantity = quantity / 1000;
		}
		BillItem billItem = new BillItem(productSku, quantity,productSku.getProductSkuSalePrice());
		billItem.setBillItemUnitType(skuUnitType);
		this.billItemList.add(billItem);

		retriveSelection= SnapSharedUtils.getLastStoredSelection(SnapCommonUtils.getSnapContext(context));
		setProductSKUSalePrice(billItem);
		if (productSku.getProductSkuUnits().ordinal() != SkuUnitType.PC.ordinal())
			this.totalQty++;
		else
			this.totalQty += quantity;
		updateCartValue(billItem, quantity);
		lastSelectedItemPosition = billItemList.size() - 1;
		
		recalculateTotalAndVat();*/
	}

	public void addItemToCart(ProductSku productSku, float quantity, SkuUnitType skuUnitType) {
		/*if(this.billItemList != null&&this.billItemList.isEmpty()){
			String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
			cartStartMap.put(getShoppingCartId()+"", date);
			vatRates = new HashMap<String, Float>();
		}
		if (skuUnitType.ordinal() == SkuUnitType.ML.ordinal()|| skuUnitType.ordinal() == SkuUnitType.GM.ordinal()) {
			quantity = quantity / 1000;
		}
		
		if (this.billItemList == null) {
			this.billItemList = new ArrayList<BillItem>();
			this.productSkuMap = new HashMap<String, Integer>();
		}
		BillItem billItem = null;
		if (this.productSkuMap.containsKey(productSku.getProductSkuCode())) {
			billItem = this.billItemList.get(productSkuMap.get(productSku.getProductSkuCode()));
			billItem.addSkuQuantity(quantity);
			productSku = billItem.getProductSku();
			if (billItem.getBillItemUnitType().ordinal() == SkuUnitType.PC.ordinal())
				this.totalQty += quantity;
		} else {
			this.productSkuMap.put(productSku.getProductSkuCode(), this.billItemList.size());
			billItem = new BillItem(productSku, quantity);
			billItem.setBillItemUnitType(skuUnitType);
			this.billItemList.add(billItem);
			if (productSku.getProductSkuUnits().ordinal() == SkuUnitType.PC.ordinal())
				this.totalQty += quantity;
			else
				this.totalQty++;
		}
		retriveSelection= SnapSharedUtils.getLastStoredSelection(SnapCommonUtils.getSnapContext(context));
		setProductSKUSalePrice(billItem);
		updateVatAmounts(billItem, quantity);
			
		updateCartValue(billItem, quantity);
		lastSelectedItemPosition = productSkuMap.get(productSku.getProductSkuCode());
		
		recalculateTotalAndVat();*/
	}

	public void addItemToCart(ProductSku productSku, float quantity) {
		/*if(this.billItemList != null&&this.billItemList.isEmpty()){
			String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
			cartStartMap.put(getShoppingCartId()+"", date);
			vatRates = new HashMap<String, Float>();
			
		}
		if (this.billItemList == null) {
			this.billItemList = new ArrayList<BillItem>();
			this.productSkuMap = new HashMap<String, Integer>();
		}
		BillItem billItem = null;
		retriveSelection= SnapSharedUtils.getLastStoredSelection(SnapCommonUtils.getSnapContext(context));
		if (this.productSkuMap.containsKey(productSku.getProductSkuCode())) {
			billItem = this.billItemList.get(productSkuMap.get(productSku.getProductSkuCode()));
			billItem.addSkuQuantity(quantity);
			productSku = billItem.getProductSku();
			if (billItem.getBillItemUnitType().ordinal() == SkuUnitType.PC.ordinal())
				this.totalQty += quantity;
		} else {
			this.productSkuMap.put(productSku.getProductSkuCode(),this.billItemList.size());
			billItem = new BillItem(productSku, quantity);
			this.billItemList.add(billItem);
			if (productSku.getProductSkuUnits().ordinal() == SkuUnitType.PC.ordinal())
				this.totalQty += quantity;
			else
				this.totalQty++;
		}

		setProductSKUSalePrice(billItem);
		updateVatAmounts(billItem, quantity);
		updateCartValue(billItem, quantity);
		lastSelectedItemPosition = productSkuMap.get(productSku.getProductSkuCode());*/
		
		recalculateTotalAndVat();
	}

	public void setAltMrp(int position) {
		/*BillItem billItem = billItemList.get(position);
		ProductSku productSku = billItem.getProductSku();
		float salePrice = billItem.getProductSkuSalePrice();
		float mrp = productSku.getProductSkuMrp();
		float altSalePrice = productSku.getProductSkuAltSalePrice();
		float altMrp = productSku.getProductSkuAlternateMrp();
		
		updateVatAmounts(billItem, null);
			
		totalCartValue = totalCartValue
				- ((salePrice > mrp ? salePrice : mrp) * billItem.getProductSkuQuantity())
				+ ((altSalePrice > altMrp ? altSalePrice : altMrp) * billItem.getProductSkuQuantity());
		if (salePrice < mrp)
			totalSavings -= ((mrp - salePrice) * billItem.getProductSkuQuantity());
		if (altSalePrice < altMrp && altSalePrice > 0)
			totalSavings = totalSavings
					+ ((altMrp - altSalePrice) * billItem
							.getProductSkuQuantity());
		if (altSalePrice > 0) {
			billItem.setProductSkuSalePrice(altSalePrice);
		} else {
			billItem.setProductSkuSalePrice(altMrp);
		}
		
		updateVatAmounts(billItem, null);
		
		billItem.setProductSkuMrp(altMrp);
		billItem.setHasMultipleSp(false);

		recalculateTotalAndVat();*/
	}
	
	public void editSellingPrice(int position, float alternateSalePrice) {
		Models.BillItem billItem = cartItems.valueAt(position);
		billItem.sellingPrice = (int)(alternateSalePrice * 100);
		billItem.sellingPriceEdited = true;
		recalculateTotalAndVat();
	}

	public void resetVatRate() {
		int totalval = 0;
		vatRates = new HashMap<String, Integer>();

		for (int i = 0; i < cartItems.size(); i++) {
			Models.BillItem billItem = cartItems.valueAt(i);
			String key = String.valueOf(billItem.product.vatRate);
			int amount = SnapCommonUtils.getVatAmount(billItem.product.vatRate, billItem.sellingPrice, billItem.quantity);
			totalval += amount;

			if(vatRates.containsKey(key))
				amount += vatRates.get(key);

			vatRates.put(key, amount);					
		}
		// Set Total Vat
		totalVatAmount = totalval;
	}

	public boolean isInvoice() {
		return isInvoice;
	}

	public void setInvoice(boolean isInvoice) {
		this.isInvoice = isInvoice;
	}

	public boolean isVATCalculated() {
		return isVATCalculated;
	}

	public void setVATCalculated(boolean isVATCalculated) {
		this.isVATCalculated = isVATCalculated;
	}

	public HashMap<String, Integer> getVatRates() {
		return vatRates;
	}

	// TODO: Not used
	private void updateVatAmounts(Models.BillItem billItem, Integer quantity) {
		if(quantity == null)
			quantity = billItem.quantity;
		int amount = SnapCommonUtils.getVatAmount(billItem.product.vatRate, billItem.sellingPrice, quantity.intValue());
		this.totalVatAmount += amount;

		if(vatRates != null) {
			if(vatRates.containsKey(String.valueOf(billItem.product.vatRate)))
				amount += vatRates.get(String.valueOf(billItem.product.vatRate));
			vatRates.put(String.valueOf(billItem.product.vatRate), amount);
		}
	}
	
	public void vatSplitCalculation(Models.BillItem billItem, int sp, double qty,
            boolean bAddAmount, HashMap<String, Integer> vatRates, boolean bAbs) {
		vatSplitCalculation(billItem, sp, qty, bAddAmount, vatRates, false, bAbs);
	}
	
	public void vatSplitCalculation(Models.BillItem billItem, int sp, double qty,
            boolean bAddAmount, HashMap<String, Integer> vatRates) {
		vatSplitCalculation(billItem, sp, qty, bAddAmount, vatRates, false, true);
	}
	
	public void vatSplitCalculation(Models.BillItem billItem, int sp, double qty,
            boolean bAddAmount, HashMap<String, Integer> vatRates, boolean bIgnorePrev, boolean bAbs) {
        int amount = SnapCommonUtils.getVatAmount(billItem.product.vatRate, sp, qty, bAbs);
        String key = String.valueOf(billItem.product.vatRate);
        if(!bIgnorePrev && vatRates != null && vatRates.containsKey(key)) {
            int prevAmount = vatRates.get(key);
            if(bAddAmount)
                amount = prevAmount + amount;
            else
                amount = prevAmount - amount;
        }
        if(vatRates != null)
            vatRates.put(key, amount);
    }
	
	private void setProductSKUSalePrice(Models.BillItem billItem) {
		/*if(retriveSelection!=null) {
			if(retriveSelection.equals("MRP"))
				billItem.setProductSkuSalePrice(billItem.mrp);
			else if (retriveSelection.equals("sku_saleprice"))
				billItem.setProductSkuSalePrice(billItem.getProductSku().getProductSkuSalePrice() == 0 ? billItem.getProductSku().getProductSkuMrp() : billItem.getProductSku().getProductSkuSalePrice());
			else if (retriveSelection.equals("sku_saleprice_two"))
				billItem.setProductSkuSalePrice(billItem.getProductSku().getProductSkuSalePrice2());
			else if (retriveSelection.equals("sku_saleprice_three"))
				billItem.setProductSkuSalePrice(billItem.getProductSku().getProductSkuSalePrice3());
		}*/
	}
}
