package com.snapbizz.snapbilling.adapters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.InventoryProductsAdapter.OnInventoryActionListener;
import com.snapbizz.snapbilling.fragments.OfferAndStoreFragment;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.utils.SkuUnitType;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;


public class InventoryProductCursorAdapter extends CursorAdapter {

	private LayoutInflater inflater;
	private OnInventoryActionListener inventoryActionListener;
	private int lastSelectedPos = -1;
	public View lastSelectedView;
	private String middle = "...";
	private String firstHalf,secondHalf;
	private String FullString;

	public InventoryProductCursorAdapter(Context context, Cursor c,
			boolean autoRequery,
			OnInventoryActionListener inventoryActionListener) {
		super(context, c, autoRequery);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.inventoryActionListener = inventoryActionListener;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (cursor != null) {
			InventoryProductsWrapper inventoryProductsWrapper = (InventoryProductsWrapper) view.getTag();
			if (cursor.getString(0) == null) {
				inventoryProductsWrapper.productStoreSelectorImageView
						.setEnabled(false);
			}
			if(cursor.getString(0) != null) {
				inventoryProductsWrapper.productStoreSelectorImageView
						.setEnabled(true);
			}
			int position = cursor.getPosition();
			inventoryProductsWrapper.productMrpTextView.setTag(position);
			inventoryProductsWrapper.productImageView.setTag(position);
			inventoryProductsWrapper.productSubCategoryTextView
					.setTag(position);
			inventoryProductsWrapper.productCategoryTextView.setTag(position);
			inventoryProductsWrapper.productPurchasePriceTextView
					.setTag(position);
			inventoryProductsWrapper.productSalesPriceTextView.setTag(position);
			inventoryProductsWrapper.productQuantityTextView.setTag(position);
			inventoryProductsWrapper.productTaxTextView.setTag(position);
			inventoryProductsWrapper.productUnitTextView.setTag(position);
			inventoryProductsWrapper.productStoreSelectorImageView
					.setTag(position);
			inventoryProductsWrapper.selectedImageView.setTag(position);
			inventoryProductsWrapper.productNameTextView.setTag(position);
			if (!cursor.isClosed()) {
				inventoryProductsWrapper.productImageView
						.setImageDrawable(SnapCommonUtils
								.getProductDrawable(cursor.getString(11),
										context));
				if(cursor.getString(0)!=null){
	            	 firstHalf=cursor.getString(0).substring(0,3);
	            	 secondHalf=cursor.getString(0).substring(cursor.getString(0).length()-5);
	            	 FullString=firstHalf+middle+secondHalf;
	            	 if(firstHalf.equals("sna")){
	            		 inventoryProductsWrapper.productCodeTextView.setText(cursor.getString(0));
	            	 }else {
	            		 inventoryProductsWrapper.productCodeTextView.setText(FullString);
	            		 }
					}
	            else{
	            	firstHalf=cursor.getString(7).substring(0,3);
	       	 		secondHalf=cursor.getString(7).substring(cursor.getString(7).length()-5);
	       	 		FullString=firstHalf+middle+secondHalf;
	       	 		if(firstHalf.equals("sna")){
	        		 inventoryProductsWrapper.productCodeTextView.setText(cursor.getString(7));
	       	 		}
	       	 	else {
	       		 inventoryProductsWrapper.productCodeTextView.setText(FullString);
	       		 }
	            }

				inventoryProductsWrapper.productQuantityTextView
						.setText(SnapCommonUtils
								.roundOffDecimalPoints(cursor.getFloat(1)) + "");
				inventoryProductsWrapper.productUnitTextView.setText(cursor
						.getString(13));
				inventoryProductsWrapper.productTaxTextView.setText(cursor
						.getFloat(4) + "%");
				inventoryProductsWrapper.productPurchasePriceTextView
						.setText(SnapToolkitTextFormatter.formatPriceText(
								cursor.getFloat(3), context));
				inventoryProductsWrapper.productSalesPriceTextView
						.setText(SnapToolkitTextFormatter.formatPriceText(
								cursor.getFloat(12), context));
				inventoryProductsWrapper.productNameTextView.setText(cursor
						.getString(8));
				inventoryProductsWrapper.productMrpTextView
						.setText(SnapToolkitTextFormatter.formatPriceText(
								cursor.getFloat(9), context));
				if (cursor.getString(18) != null)
					inventoryProductsWrapper.productCategoryTextView
							.setText(cursor.getString(18));
				else
					inventoryProductsWrapper.productCategoryTextView
							.setText("");

				if (cursor.getString(19) != null)
					inventoryProductsWrapper.productSubCategoryTextView
							.setText(cursor.getString(19));
				else
					inventoryProductsWrapper.productSubCategoryTextView
							.setText("");

				if ((cursor.getString(2) != null)||(cursor.getString(5) != null)){
					
					inventoryProductsWrapper.productStoreSelectorImageView
							.setSelected((cursor.getString(2).equals("1") ? true
									: false)||(cursor.getString(5).equals("1") ? true
									: false));
				}


				inventoryProductsWrapper.selectedImageView.setSelected(cursor
						.getString(0) != null);
			}
		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View convertView = inflater.inflate(R.layout.listitem_inventory_update,
				null);
		InventoryProductsWrapper inventoryProductsWrapper;
		inventoryProductsWrapper = new InventoryProductsWrapper();
		inventoryProductsWrapper.productImageView = (ImageView) convertView
				.findViewById(R.id.inventory_prod_imageview);
		inventoryProductsWrapper.productNameTextView = (TextView) convertView
				.findViewById(R.id.inventory_prodname_textview);
		inventoryProductsWrapper.productMrpTextView = (TextView) convertView
				.findViewById(R.id.inventory_mrp_textview);
		inventoryProductsWrapper.productQuantityTextView = (TextView) convertView
				.findViewById(R.id.inventory_prodquantity_textview);
		inventoryProductsWrapper.productTaxTextView = (TextView) convertView
				.findViewById(R.id.inventory_prodtax_textview);
		inventoryProductsWrapper.productCodeTextView = (TextView) convertView
				.findViewById(R.id.inventory_prodcode_textview);
		inventoryProductsWrapper.productUnitTextView = (TextView) convertView
				.findViewById(R.id.inventory_produnit_spinner);
		inventoryProductsWrapper.productPurchasePriceTextView = (TextView) convertView
				.findViewById(R.id.inventory_purchaseprice_textview);
		inventoryProductsWrapper.productSalesPriceTextView = (TextView) convertView
				.findViewById(R.id.inventory_salesprice_textview);
		inventoryProductsWrapper.productCategoryTextView = (TextView) convertView
				.findViewById(R.id.inventory_prod_cat_textview);
		inventoryProductsWrapper.productSubCategoryTextView = (TextView) convertView
				.findViewById(R.id.inventory_prod_subcat_textview);
		inventoryProductsWrapper.productStoreSelectorImageView = (ImageView) convertView
				.findViewById(R.id.inventory_store_select_image);
		inventoryProductsWrapper.selectedImageView = (ImageView) convertView
				.findViewById(R.id.update_global_prod_button_selected);
		inventoryProductsWrapper.productQuantityTextView
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productTaxTextView
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productMrpTextView
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productPurchasePriceTextView
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productSalesPriceTextView
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productNameTextView
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productStoreSelectorImageView
				.setOnClickListener(inventoryProdClickListener);
		convertView.setTag(inventoryProductsWrapper);
		return convertView;
	}

	OnClickListener inventoryProdClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			v.setSelected(true);
			lastSelectedView = v;
			lastSelectedPos = (Integer) v.getTag();
			Cursor currentCursor = (Cursor) getItem(lastSelectedPos);

			if (v.getId() == R.id.inventory_prodquantity_textview) {
				inventoryActionListener.onProductValueEdit(
						currentCursor.getInt(1),
						OfferAndStoreFragment.STOCKQTY_CONTEXT);
			} else if (v.getId() == R.id.inventory_prodtax_textview) {
				inventoryActionListener.onProductValueEdit(
						currentCursor.getFloat(4),
						OfferAndStoreFragment.TAX_CONTEXT);
			} else if (v.getId() == R.id.inventory_mrp_textview) {
				inventoryActionListener.onProductValueEdit(
						currentCursor.getFloat(9),
						OfferAndStoreFragment.MRP_CONTEXT);
			} else if (v.getId() == R.id.inventory_purchaseprice_textview) {
				inventoryActionListener.onProductValueEdit(
						currentCursor.getFloat(3),
						OfferAndStoreFragment.PURCHASE_PRICE_CONTEXT);
			} else if (v.getId() == R.id.inventory_salesprice_textview) {
				inventoryActionListener.onProductValueEdit(
						currentCursor.getFloat(12),
						OfferAndStoreFragment.SALES_PRICE_CONTEXT);
			}
			else if (v.getId() == R.id.inventory_store_select_image) {
				InventorySku inventorySku = getSelectedInvSku(currentCursor);
					inventoryActionListener.updateInventorySku(inventorySku,
							v.getContext().getString(R.string.show_store), false);
					inventoryActionListener.updateInventorySku(inventorySku,
							v.getContext().getString(R.string.is_offer), false);
				inventorySku.setStore(false);
				notifyDataSetChanged();
				
				
			} else if (v.getId() == R.id.inventory_prodname_textview) {
				InventorySku invSku = getSelectedInvSku(currentCursor);
				if (invSku != null && !invSku.getProductSku().isSelected())
					inventoryActionListener.onAddItemToInventory(invSku);
			}
		}
	};

	@SuppressLint("SimpleDateFormat")
	private InventorySku getSelectedInvSku(Cursor cursor) {
		SkuUnitType unittype = SkuUnitType.valueOf(cursor.getString(13));
		Brand brand = new Brand();
		brand.setBrandId(Integer.parseInt(cursor.getString(14)));
		Date lastModifiedProdSkuDate = Calendar.getInstance().getTime();
		try {
			lastModifiedProdSkuDate = new SimpleDateFormat(
					SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
					.parse(cursor.getString(15));
		} catch (Exception e) {
			e.printStackTrace();
		}
		ProductCategory category = new ProductCategory();
		category.setCategoryId(Integer.parseInt(cursor.getString(10)));
		ProductCategory parentCategory = new ProductCategory();
		parentCategory.setCategoryId(1);
		category.setParenCategory(parentCategory);
		float mrp = 0;
		float salePrice = 0;
		try {
			mrp = Float.parseFloat(cursor.getString(9));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			salePrice = Float.parseFloat(cursor.getString(12));
		} catch (Exception e) {
			e.printStackTrace();
		}
		ProductSku prodObj = new ProductSku(cursor.getString(8), mrp,
				cursor.getString(7), cursor.getString(11), unittype,
				cursor.getString(0) == null, brand, category,
				lastModifiedProdSkuDate,
				cursor.getString(22).equals("1") ? true : false, salePrice);
		Date lastModifiedInvSkuDate = Calendar.getInstance().getTime();
		try {
			lastModifiedInvSkuDate = new SimpleDateFormat(
					SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
					.parse(cursor.getString(16));
		} catch (Exception e) {
			lastModifiedInvSkuDate = Calendar.getInstance().getTime();
			e.printStackTrace();
		}
		float taxRate = 0;
		if (cursor.getString(4) != null && !cursor.getString(4).isEmpty()) {
			String tempVariable = cursor.getString(4);
			tempVariable = SnapCommonUtils.StripPercentage(tempVariable);
			taxRate = Float.parseFloat(tempVariable);
		}

		float purchasePrice = 0;
		if (cursor.getString(3) != null)
			purchasePrice = Float.parseFloat(cursor.getString(3));

		float quantity = 0;
		if (cursor.getString(1) != null)
			quantity = Float.parseFloat(cursor.getString(1));

		InventorySku invObj = new InventorySku(prodObj, quantity, Calendar
				.getInstance().getTime(), prodObj.getProductSkuCode(),
				purchasePrice, taxRate);
		invObj.getProductSku().setVAT(taxRate);
		invObj.setLastModifiedTimestamp(lastModifiedInvSkuDate);
		invObj.setSlNo(cursor.getInt(17));
		invObj.getProductSku().setSelected(cursor.getString(0) != null);
		if (cursor.getString(2) != null)
			invObj.setOffer(cursor.getString(2).equals("1") ? true : false);
		if (cursor.getString(5) != null)
			invObj.setStore(cursor.getString(5).equals("1") ? true : false);
		invObj.getProductSku().setProductCategoryName(cursor.getString(18));
		invObj.getProductSku().setProductSubCategoryName(cursor.getString(19));
		return invObj;
	}

	public InventorySku getLastSelectedItem() {
		if (lastSelectedPos == -1)
			return null;
		else if (lastSelectedPos >= getCount())
			return null;
		else {
			Cursor currentCursor = (Cursor) getItem(lastSelectedPos);
			return getSelectedInvSku(currentCursor);
		}
	}

	public int getLastSelectedPos() {
		return lastSelectedPos;
	}

	public void setLastSelectedPos(int lastSelectedPos) {
		this.lastSelectedPos = lastSelectedPos;
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		return super.runQueryOnBackgroundThread(constraint);

	}

	private class InventoryProductsWrapper {
		private TextView productNameTextView;
		private ImageView productImageView;
		private TextView productMrpTextView;
		private TextView productQuantityTextView;
		private TextView productTaxTextView;
		private TextView productCodeTextView;
		private TextView productUnitTextView;
		private TextView productSalesPriceTextView;
		private TextView productPurchasePriceTextView;
		private TextView productCategoryTextView;
		private TextView productSubCategoryTextView;
		private ImageView selectedImageView;
		private ImageView productStoreSelectorImageView;
	}

}
