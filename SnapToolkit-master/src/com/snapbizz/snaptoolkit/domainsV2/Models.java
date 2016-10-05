package com.snapbizz.snaptoolkit.domainsV2;

import java.util.List;

import com.snapbizz.snaptoolkit.db.dao.ProductPacks;
import com.snapbizz.snaptoolkit.domainsV2.Models.UOM;
import com.snapbizz.snaptoolkit.utils.SkuUnitType;

public class Models {
	public static enum UOM {
		PC,
		G,
		ML,
		KG,
		L;
		
		public static UOM getFromOldUnits(SkuUnitType skuUnitType) {
			switch(skuUnitType) {
				case GM:
					return UOM.G;
				case KG:
					return UOM.KG;
				case LTR:
					return UOM.L;
				case ML:
					return UOM.ML;
				default:
				case PC:
					return UOM.PC;
			}
		}
		
		public static SkuUnitType convertToOldUnits(UOM uom) {
			switch(uom) {
				case G:
					return SkuUnitType.GM;
				case KG:
					return SkuUnitType.KG;
				case L:
					return SkuUnitType.LTR;
				case ML:
					return SkuUnitType.ML;
				case PC:
				default:
					return SkuUnitType.PC;
			}
		}
	}

	// TODO: Requires proguard protect, along with ldbProduct and gdbProduct
	public static class ProductInfo {
		public long barcode;
		public long productGid;
	    public String name;
	    public String localName = null;
	    public int mrp = 0;
	    public int measure = 1;
	    public int brandId;
	    public int categoryId;
	    public String image;
	    public boolean isPc;
	    
	    // From LDB
	    public float vatRate;
	    
	    // Manually set
	    public Integer alternateMrps[];
	    public UOM uom;
	    public boolean bNew = false;						/*< if true, update LDB */
	}
	
	public static class BillItem {
		// Supporting data
		public ProductInfo product;							/*< Product info */
		public List<ProductPacks> packs;					/*< Pack info, TODO: Populate this */

		// Line item related data
		public UOM uom;										/*< G / ML / PC only */
		public int quantity;								/*< quantity in pc/gms/ml */
	    public int packSize = 1;							/*< Pack Size, multiplier - unused currently */

		public int mrp;										/*< Current MRP selection */
		public boolean mrpEdited = false;					/*< If MRP is manually edited */

		public int sellingPrice;							/*< Selling Price */
		public int spIndex;									/*< Is this 0, 1, 2 */
		public boolean sellingPriceEdited = false;			/*< Requires updating of sellingPrice */
		public Integer[] sps;								/*< The mrp, three sale prices */
		
		public int vatAmount = 0;							/*< VAT Amount */

		public String name;									/*< Name of the product - shown on screen and printable */
		public boolean nameEdited = false;					/*< Requires changing of the name in LDB */
		
		
		// ---------------------------------------------------- Helper methods
		/**
		 * Convert quantity in the required UOM format.
		 *
		 * @param uom
		 * @return quantity
		 */
		public float getQuantity(UOM uom) {
			if(uom == UOM.PC || uom == UOM.G || uom == UOM.ML)
				return quantity;
			return quantity / 1000.0f;
		}

		/**
		 * Get UOM for display.
		 *
		 * @return UOM
		 */
		public UOM getDisplayUom() {
			if(uom == UOM.ML)
				return (quantity >= 1000) ? UOM.L : UOM.ML;
			if(uom == UOM.G)
				return (quantity >= 1000) ? UOM.KG : UOM.G;
			return UOM.PC;
		}

		/**
		 * Gets the total in Rupees / Piasa
		 * 
		 * @param bRupee
		 * @return total of the line item
		 */
		public double getTotal(boolean bRupee) {
			return Math.abs(quantity) * sellingPrice / (bRupee ? 100.0 : 1) / (uom == UOM.PC ? 1 : 1000.0);
		}
		
		public double getPriceDifference(boolean bRupee) {
			return (mrp - sellingPrice) / (bRupee ? 100.0 : 1);
		}

		/**
		 * 
		 * @param bRupee
		 * @return
		 */
		public double getItemSavings(boolean bRupee) {
			return (getPriceDifference(bRupee) * Math.abs(quantity) / (bRupee ? 100.0 : 1) / (uom == UOM.PC ? 1 : 1000.0));
		}
	}
}
