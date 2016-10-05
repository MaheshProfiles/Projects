package com.snapbizz.snapbilling.db;

import java.util.Locale;

import com.snapbizz.snaptoolkit.db.GlobalDB;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;
import com.snapbizz.snaptoolkit.domainsV2.Models;
import com.snapbizz.snaptoolkit.domainsV2.Models.ProductInfo;
import com.snapbizz.snaptoolkit.domainsV2.Models.UOM;
import com.snapbizz.snaptoolkit.db.dao.Products;
import com.snapbizz.snaptoolkit.utils.ToolkitV2;

public class ProductsHelper {

	public static Integer[] convertStringPrices(String prices) {
		if(prices == null || prices.isEmpty())
			return null;
		String[] splitPrices = prices.split(",");
		Integer[] values = new Integer[splitPrices.length];
		for(int i = 0; i < splitPrices.length; i++) {
			String price = splitPrices[i];
			int val = 0;
			try {
				val = Integer.parseInt(price);
			} catch(Exception e) { }
			values[i] = val;
		}
		return values;
	}
	
	public static ProductInfo getProductInfo(long barcode) {
		GlobalDB gdb = GlobalDB.getInstance(null, false);
		SnapbizzDB db = SnapbizzDB.getInstance(null, false);

		com.snapbizz.snaptoolkit.gdb.dao.Products gdbProduct = null;
		Products ldbProduct = null;
		ProductInfo product = new ProductInfo();
		Long pid = null;
		if(gdb != null) {
			gdbProduct = gdb.getProductByBarcode(barcode);
			if(gdbProduct != null) {
				// Offset the gid to work with local db
				pid = gdbProduct.getProductGid() + SnapbizzDB.PRODUCT_GID_OFFSET;
				ToolkitV2.copyProperties(gdbProduct, product);
				product.uom = UOM.valueOf(gdbProduct.getUom().toUpperCase(Locale.ENGLISH));
				product.alternateMrps = convertStringPrices(gdbProduct.getAlternateMrp());
				product.vatRate = gdb.getVatRate(gdbProduct.getVatId() != null ?
														gdbProduct.getVatId() :
														gdb.getCategory(gdbProduct.getCategoryId()).getVatId());
				// Update GID
				product.productGid = pid;
			}
		}
		if(db != null) {
			if(pid != null)
				ldbProduct = db.getProductByPId(pid);
			else
				ldbProduct = db.getProductByPId(barcode);
			if(ldbProduct != null) {
				ToolkitV2.copyProperties(ldbProduct, product);
				product.uom = UOM.valueOf(ldbProduct.getUom());
				product.productGid = ldbProduct.getProductCode();
			}
		}
		// No product found
		if(gdbProduct == null && ldbProduct == null) {
			product.productGid = barcode;
			product.barcode = barcode;
			product.name = String.valueOf(barcode);
			product.vatRate = 0.0f;
			product.bNew = true;
			// Assuming scanned products are PC
			product.isPc = true;
			product.uom = UOM.PC;
			// Category ID, Brand ID
			product.categoryId = 0;
			product.brandId = 0;
		}
		
		return product;
	}
}
