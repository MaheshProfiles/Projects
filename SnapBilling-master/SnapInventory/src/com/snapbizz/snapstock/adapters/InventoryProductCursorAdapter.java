package com.snapbizz.snapstock.adapters;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.adapters.InventoryProductsAdapter.OnInventoryActionListener;
import com.snapbizz.snapstock.fragments.InventoryUpdateFragment;
import com.snapbizz.snapstock.fragments.SkuReportFragment;
import com.snapbizz.snapstock.interfaces.GetResponseBackToUpdateFragment;
import com.snapbizz.snapstock.utils.SnapInventoryConstants;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.utils.SkuUnitType;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;

public class InventoryProductCursorAdapter extends CursorAdapter {

	private LayoutInflater inflater;
	private Context context;
	private OnInventoryActionListener inventoryActionListener;
	private int lastSelectedPos = -1;
	public View lastSelectedView;
	private String name = "";
	private String barCode = "";
	private GetResponseBackToUpdateFragment mResponseBackToUpdateFragment = null;
	private Menu menu = null;
	private String middle = "...";
	private String firstHalf, secondHalf;
	private String fullString;
	boolean click = false;
	Cursor currentCursor = null;
	SnapBizzDatabaseHelper databaseHelper;

	public InventoryProductCursorAdapter(Context context, Cursor c, boolean autoRequery, OnInventoryActionListener inventoryActionListener, 
			Menu menu, GetResponseBackToUpdateFragment getResponseBackToUpdateFragment) {
		super(context, c, autoRequery);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.menu = menu;
		databaseHelper = SnapInventoryUtils.getDatabaseHelper(context);
		mResponseBackToUpdateFragment = getResponseBackToUpdateFragment;
		this.inventoryActionListener = inventoryActionListener;

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		if (cursor != null) {
			InventoryProductsWrapper inventoryProductsWrapper = (InventoryProductsWrapper) view
					.getTag();
			InventorySku inventorySku = getSelectedInvSku(cursor);
			if (inventorySku.isStore()||(inventorySku.isOffer())) {
				inventoryProductsWrapper.productStoreSelectorImageView
						.setSelected(true);
			} else {
				inventoryProductsWrapper.productStoreSelectorImageView
						.setSelected(false);
			}
			inventoryProductsWrapper.selectedImageView.setSelected(cursor
					.getString(0) != null);

			if (cursor.getString(0) == null) {
				inventoryProductsWrapper.productRemoveButton.setEnabled(false);
				inventoryProductsWrapper.productEditButton.setEnabled(false);
				inventoryProductsWrapper.productBatchesButton.setEnabled(false);
			} else {
				inventoryProductsWrapper.productRemoveButton.setEnabled(true);
				inventoryProductsWrapper.productEditButton.setEnabled(true);
				inventoryProductsWrapper.productBatchesButton.setEnabled(true);
			}
			int position = cursor.getPosition();
			inventoryProductsWrapper.productMrpTextView.setTag(position);
			inventoryProductsWrapper.productImageView.setTag(position);
			inventoryProductsWrapper.productSubCategoryTextView
					.setTag(position);
			inventoryProductsWrapper.inventoryReportLayout.setTag(position);
			inventoryProductsWrapper.productCategoryTextView.setTag(position);
			inventoryProductsWrapper.productRemoveButton.setTag(position);
			inventoryProductsWrapper.productPurchasePriceTextView
					.setTag(position);
			inventoryProductsWrapper.productSalesPriceTextView.setTag(position);
			inventoryProductsWrapper.productSalesPriceTextTwoView.setTag(position);
			inventoryProductsWrapper.productSalesPriceTextThreeView.setTag(position);
			inventoryProductsWrapper.productQuantityTextView.setTag(position);
			inventoryProductsWrapper.productTaxTextView.setTag(position);
			inventoryProductsWrapper.productUnitTextView.setTag(position);
			inventoryProductsWrapper.productStoreSelectorImageView
					.setTag(position);
			inventoryProductsWrapper.productBatchesButton.setTag(position);
			inventoryProductsWrapper.productEditButton.setTag(position);
			inventoryProductsWrapper.selectedImageView.setTag(position);
			inventoryProductsWrapper.productNameTextView.setTag(position);
			inventoryProductsWrapper.inventory_prodname_layout.setTag(position);
			if (!cursor.isClosed()) {
				inventoryProductsWrapper.productImageView
						.setImageDrawable(SnapInventoryUtils
								.getProductDrawable(cursor.getString(11),
										context, false));
				if (cursor.getString(0) != null) {
					if (cursor.getString(0).length() >= SnapInventoryConstants.NORMAL_BARCODE_LENGTH) {
						firstHalf = cursor.getString(0).substring(0, 3);
						secondHalf = cursor.getString(0).substring(cursor.getString(0).length() - 5);
						fullString = firstHalf + middle + secondHalf;
					} else {
						fullString = cursor.getString(0);
					}
					if (firstHalf != null && firstHalf.equals("sna")) {
						inventoryProductsWrapper.productCodeTextView.setText(cursor.getString(0));
					} else {
						inventoryProductsWrapper.productCodeTextView.setText(fullString);
					}
				} else {
					if (cursor.getString(7).length() >= SnapInventoryConstants.NORMAL_BARCODE_LENGTH) {
						firstHalf = cursor.getString(7).substring(0, 3);
						secondHalf = cursor.getString(7).substring(cursor.getString(7).length() - 5);
						fullString = firstHalf + middle + secondHalf;
					} else {
						fullString = cursor.getString(7);
					}
					if (firstHalf != null && firstHalf.equals("sna")) {
						inventoryProductsWrapper.productCodeTextView.setText(cursor.getString(7));
					} else {
						inventoryProductsWrapper.productCodeTextView.setText(fullString);
					}
				}

				inventoryProductsWrapper.productQuantityTextView
						.setText(SnapInventoryUtils
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
				inventoryProductsWrapper.productSalesPriceTextTwoView
				.setText(SnapToolkitTextFormatter.formatPriceText(
						cursor.getFloat(23), context));
				inventoryProductsWrapper.productSalesPriceTextThreeView
				.setText(SnapToolkitTextFormatter.formatPriceText(
						cursor.getFloat(24), context));
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
		inventoryProductsWrapper.inventory_prodname_layout = (RelativeLayout) convertView
				.findViewById(R.id.inventory_prodname_trns_layout);
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
		inventoryProductsWrapper.productSalesPriceTextTwoView = (TextView) convertView
				.findViewById(R.id.inventory_salesprice_two_textview);
		inventoryProductsWrapper.productSalesPriceTextThreeView = (TextView) convertView
				.findViewById(R.id.inventory_salesprice_three_textview);
		inventoryProductsWrapper.productCategoryTextView = (TextView) convertView
				.findViewById(R.id.inventory_prod_cat_textview);
		inventoryProductsWrapper.productSubCategoryTextView = (TextView) convertView
				.findViewById(R.id.inventory_prod_subcat_textview);
		inventoryProductsWrapper.productRemoveButton = (Button) convertView
				.findViewById(R.id.inventory_delete_button);
		inventoryProductsWrapper.inventoryReportLayout = (RelativeLayout) convertView
				.findViewById(R.id.inventoryReportLayout);
		inventoryProductsWrapper.productStoreSelectorImageView = (ImageView) convertView
				.findViewById(R.id.inventory_store_select_image);
		inventoryProductsWrapper.selectedImageView = (ImageView) convertView
				.findViewById(R.id.update_global_prod_button_selected);
		inventoryProductsWrapper.productBatchesButton = (Button) convertView
				.findViewById(R.id.inventory_batch_button);
		inventoryProductsWrapper.productEditButton = (Button) convertView
				.findViewById(R.id.inventory_edit_prod_button);
		inventoryProductsWrapper.productQuantityTextView
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productTaxTextView
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productMrpTextView
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.inventory_prodname_layout
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productPurchasePriceTextView
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productSalesPriceTextView
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productSalesPriceTextTwoView
		.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productSalesPriceTextThreeView
		.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productBatchesButton
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productRemoveButton
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.inventoryReportLayout
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productNameTextView
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productEditButton
				.setOnClickListener(inventoryProdClickListener);
		inventoryProductsWrapper.productStoreSelectorImageView
				.setOnClickListener(inventoryProdClickListener);
		convertView.setTag(inventoryProductsWrapper);
		return convertView;
	}

	OnClickListener inventoryProdClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			lastSelectedView = v;

			lastSelectedPos = (Integer) v.getTag();
			SnapCommonUtils.hideSoftKeyboard(context, lastSelectedView.getWindowToken());
			currentCursor = (Cursor) getItem(lastSelectedPos);
			v.setSelected(true);
			if (v.getId() == R.id.inventoryReportLayout) {
				SnapToolkitConstants.SKU_REPORT_COUNT++;
				if (SnapToolkitConstants.SKU_REPORT_COUNT == 1) {
					SnapToolkitConstants.IS_REPORT = "1";
					if (menu != null) {
						try {
							menu.findItem(R.id.search_meuitem)
									.setVisible(false);
							menu.clear();
						} catch (NullPointerException e) {
							e.printStackTrace();
						}
					}

					SkuReportFragment skuReportFragment = new SkuReportFragment();
					skuReportFragment
							.setBackListener(mResponseBackToUpdateFragment);
					FragmentManager fragmentManager = ((Activity) context)
							.getFragmentManager();
					Bundle bundle = new Bundle();
					if (currentCursor.getString(0) != null) {
						bundle.putString("code", currentCursor.getString(0));
					} else {
						bundle.putString("code", currentCursor.getString(7));

					}
					if (currentCursor.getString(8) != null) {
						bundle.putString("name", currentCursor.getString(8));

					}
					if (currentCursor.getString(9) != null) {
						bundle.putString("mrp", currentCursor.getString(9));

					}

					if (currentCursor.getString(10) != null) {
						bundle.putString("sub_cat_id",
								currentCursor.getString(10));

					}
					if (String.valueOf(currentCursor.getFloat(1)) != null) {
						bundle.putString("stock_qty",
								String.valueOf(currentCursor.getFloat(1)));

					}

					FragmentTransaction ft = fragmentManager.beginTransaction();
					ft.add(R.id.content_framelayout, skuReportFragment,
							"SkuReport");
					ft.addToBackStack("SkuReport");
					skuReportFragment.setArguments(bundle);
					ft.commit();
					fragmentManager.executePendingTransactions();
				}
			} else {
				SnapToolkitConstants.IS_REPORT = "0";

				if (lastSelectedView != null
						&& v.getId() != R.id.inventory_batch_button)
					lastSelectedView.setSelected(false);
				if (v.getId() == R.id.inventory_store_select_image) {
					try {
						InventorySku inventorySku = getSelectedInvSku(currentCursor);

						databaseHelper = SnapInventoryUtils
								.getDatabaseHelper(context);

						UpdateBuilder<InventorySku, Integer> updateBuilder = databaseHelper
								.getInventorySkuDao().updateBuilder();
						updateBuilder.where().eq(
								"inventory_sku_id",
								inventorySku.getProductSku()
										.getProductSkuCode());

//						updateBuilder.updateColumnValue("show_store",
//								inventorySku.isStore() ? false : true);
//						updateBuilder.updateColumnValue("is_offer",
//								inventorySku.isOffer() ? false : true);
						
						
						if (!inventorySku.isStore()) {
							
							updateBuilder.updateColumnValue("show_store",
									inventorySku.isStore() ? false : true);
							
							
							updateBuilder.updateColumnValue("show_store", true);
							updateBuilder.updateColumnValue("is_offer", true);
//							inventoryActionListener.updateInventorySku(
//									inventorySku, "is_offer", true);
							inventorySku.setStore(true);

						} else {
							updateBuilder.updateColumnValue("show_store", false);
							updateBuilder.updateColumnValue("is_offer", false);
//							inventoryActionListener.updateInventorySku(
//									inventorySku, "show_store", false);
//							inventoryActionListener.updateInventorySku(
//									inventorySku, "is_offer", false);
							inventorySku.setStore(false);
						}

						try {
							updateBuilder
									.updateColumnValue(
											"lastmodified_timestamp",
											new SimpleDateFormat(
													SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
													.parse(new SimpleDateFormat(
															SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
															.format(Calendar
																	.getInstance()
																	.getTime())));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						updateBuilder.update();
						currentCursor.requery();

					} catch (SQLException e) {
						e.printStackTrace();
					}

				} 
				else if ((v.getId() == R.id.inventory_prodname_trns_layout)) {
					InventorySku invSku = getSelectedInvSku(currentCursor);
					if (invSku != null && !invSku.getProductSku().isSelected())
						inventoryActionListener.onAddItemToInventory(invSku);
					notifyDataSetChanged();
				}
				
				
				else if (v.getId() == R.id.inventory_prodquantity_textview) {
					inventoryActionListener.onProductValueEdit(
							currentCursor.getInt(1),
							InventoryUpdateFragment.STOCKQTY_CONTEXT);
				} else if (v.getId() == R.id.inventory_mrp_textview) {
					inventoryActionListener.onProductValueEdit(
							currentCursor.getFloat(9),
							InventoryUpdateFragment.MRP_CONTEXT);
				} else if (v.getId() == R.id.inventory_purchaseprice_textview) {
					inventoryActionListener.onProductValueEdit(
							currentCursor.getFloat(3),
							InventoryUpdateFragment.PURCHASE_PRICE_CONTEXT);
				} else if (v.getId() == R.id.inventory_salesprice_textview) {
					inventoryActionListener.onProductValueEdit(
							currentCursor.getFloat(12),
							InventoryUpdateFragment.SALES_PRICE_CONTEXT);
				}
				
				 else if (v.getId() == R.id.inventory_salesprice_two_textview) {
						inventoryActionListener.onProductValueEdit(
								currentCursor.getFloat(23),
								InventoryUpdateFragment.SALES_PRICE_TWO_CONTEXT);
					}
				 else if (v.getId() == R.id.inventory_salesprice_three_textview) {
						inventoryActionListener.onProductValueEdit(
								currentCursor.getFloat(24),
								InventoryUpdateFragment.SALES_PRICE_THREE_CONTEXT);
					}
				else if (v.getId() == R.id.inventory_delete_button) {
					inventoryActionListener
							.onRemoveItem(getSelectedInvSku(currentCursor));
				}

				else if (v.getId() == R.id.inventory_batch_button) {
					inventoryActionListener.onShowBatches(lastSelectedPos);
				} else if (v.getId() == R.id.inventory_edit_prod_button) {
					inventoryActionListener
							.onEditProduct(getSelectedInvSku(currentCursor));
				} 
			}

		}
	};

	@SuppressLint("SimpleDateFormat")
	private InventorySku getSelectedInvSku(Cursor cursor) {
		SkuUnitType unittype = SkuUnitType.valueOf(cursor.getString(13));
		Brand brand = new Brand();
		if(cursor.getString(14) != null ){
			brand.setBrandId(Integer.parseInt(cursor.getString(14)));
		}
		Date lastModifiedProdSkuDate = Calendar.getInstance().getTime();
		try {
			lastModifiedProdSkuDate = new SimpleDateFormat(
					SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
					.parse(cursor.getString(15));
		} catch (Exception e) {
			e.printStackTrace();
		}
		ProductCategory category = new ProductCategory();
		Log.d("TAG","getCategoryId---inventory-->"+category.getCategoryId());
		if(cursor.getString(10)!=null){
		  category.setCategoryId(Integer.parseInt(cursor.getString(10)));
		}
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
			if(cursor.getString(16) != null){
				lastModifiedInvSkuDate = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(cursor.getString(16));
			}
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
		// TODO Auto-generated method stub
		return super.runQueryOnBackgroundThread(constraint);

	}

	class InventoryProductsWrapper {
		private TextView productNameTextView;
		private ImageView productImageView;
		private TextView productMrpTextView;
		private TextView productQuantityTextView;
		private TextView productTaxTextView;
		private TextView productCodeTextView;
		private TextView productUnitTextView;
		private TextView productSalesPriceTextView;
		private TextView productSalesPriceTextTwoView;
		private TextView productSalesPriceTextThreeView;
		private TextView productPurchasePriceTextView;
		private TextView productCategoryTextView;
		private TextView productSubCategoryTextView;
		private Button productRemoveButton;
		private Button productBatchesButton;
		private Button productEditButton;
		private ImageView selectedImageView;
		private ImageView productStoreSelectorImageView;
		private RelativeLayout inventoryReportLayout;
		private RelativeLayout inventory_prodname_layout;
	}

}
