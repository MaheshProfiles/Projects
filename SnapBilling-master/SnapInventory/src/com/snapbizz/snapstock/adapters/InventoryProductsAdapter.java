package com.snapbizz.snapstock.adapters;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.stmt.UpdateBuilder;
import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.fragments.InventoryUpdateFragment;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;

public class InventoryProductsAdapter extends ArrayAdapter<InventorySku> {

    private Context context;
    private LayoutInflater inflater;
    private OnInventoryActionListener inventoryActionListener;
    private int lastSelectedPos = -1;
    public View lastSelectedView;
    private String middle="...";
    private String firstHalf,secondHalf;
    private String FullString;

	

	public InventoryProductsAdapter(Context context,
			List<InventorySku> inventoryProducts,
			OnInventoryActionListener inventoryActionListener) {
		super(context, android.R.id.text1, inventoryProducts);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.inventoryActionListener = inventoryActionListener;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		InventoryProductsWrapper inventoryProductsWrapper;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listitem_inventory_update,
					parent, false);
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
			inventoryProductsWrapper.productRemoveButton = (Button) convertView
					.findViewById(R.id.inventory_delete_button);
			// inventoryProductsWrapper.productOfferSelectorImageView =
			// (ImageView) convertView
			// .findViewById(R.id.inventory_offer_select_image);
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
			inventoryProductsWrapper.productPurchasePriceTextView
					.setOnClickListener(inventoryProdClickListener);
			inventoryProductsWrapper.productSalesPriceTextView
					.setOnClickListener(inventoryProdClickListener);
			inventoryProductsWrapper.productBatchesButton
					.setOnClickListener(inventoryProdClickListener);
			inventoryProductsWrapper.productRemoveButton
					.setOnClickListener(inventoryProdClickListener);
			/*
			 * inventoryProductsWrapper.selectedImageView
			 * .setOnClickListener(inventoryProdClickListener);
			 */
			inventoryProductsWrapper.productNameTextView
					.setOnClickListener(inventoryProdClickListener);
			inventoryProductsWrapper.productEditButton
					.setOnClickListener(inventoryProdClickListener);
			// inventoryProductsWrapper.productUnitTextView
			// .setOnClickListener(inventoryProdClickListener);


			// inventoryProductsWrapper.productOfferSelectorImageView
			// .setOnClickListener(inventoryProdClickListener);
			inventoryProductsWrapper.productStoreSelectorImageView
					.setOnClickListener(inventoryProdClickListener);

			convertView.setTag(inventoryProductsWrapper);
		} else {
			inventoryProductsWrapper = (InventoryProductsWrapper) convertView
					.getTag();
		}
		InventorySku inventorySku = getItem(position);
		ProductSku prod = inventorySku.getProductSku();
		if (!inventorySku.getProductSku().isSelected()) {
			inventoryProductsWrapper.productRemoveButton.setEnabled(false);
			inventoryProductsWrapper.productEditButton.setEnabled(false);
			inventoryProductsWrapper.productBatchesButton.setEnabled(false);
			// inventoryProductsWrapper.productOfferSelectorImageView.setEnabled(false);
			inventoryProductsWrapper.productStoreSelectorImageView
					.setEnabled(false);
		} else {
			inventoryProductsWrapper.productRemoveButton.setEnabled(true);
			inventoryProductsWrapper.productEditButton.setEnabled(true);
			inventoryProductsWrapper.productBatchesButton.setEnabled(true);
			// inventoryProductsWrapper.productOfferSelectorImageView.setEnabled(true);
			inventoryProductsWrapper.productStoreSelectorImageView
					.setEnabled(true);
		}
		inventoryProductsWrapper.productMrpTextView.setTag(position);
		inventoryProductsWrapper.productImageView.setTag(position);
		inventoryProductsWrapper.productSubCategoryTextView.setTag(position);
		inventoryProductsWrapper.productCategoryTextView.setTag(position);
		inventoryProductsWrapper.productRemoveButton.setTag(position);
		inventoryProductsWrapper.productPurchasePriceTextView.setTag(position);
		inventoryProductsWrapper.productSalesPriceTextView.setTag(position);
		inventoryProductsWrapper.productQuantityTextView.setTag(position);
		inventoryProductsWrapper.productTaxTextView.setTag(position);
		inventoryProductsWrapper.productUnitTextView.setTag(position);
		// inventoryProductsWrapper.productOfferSelectorImageView.setTag(position);
		inventoryProductsWrapper.productStoreSelectorImageView.setTag(position);
		inventoryProductsWrapper.productBatchesButton.setTag(position);
		inventoryProductsWrapper.productEditButton.setTag(position);
		inventoryProductsWrapper.selectedImageView.setTag(position);
		inventoryProductsWrapper.productNameTextView.setTag(position);
		inventoryProductsWrapper.productImageView
				.setImageDrawable(SnapInventoryUtils.getProductDrawable(prod,
						context, false));
		inventoryProductsWrapper.productNameTextView.setText(prod
				.getProductSkuName());
		inventoryProductsWrapper.productMrpTextView
				.setText(SnapToolkitTextFormatter.formatPriceText(
						prod.getProductSkuMrp(), context));
		inventoryProductsWrapper.productQuantityTextView
				.setText(SnapInventoryUtils.roundOffDecimalPoints(inventorySku
						.getQuantity()) + "");
		
		firstHalf=prod.getProductSkuCode().substring(0,3);
        secondHalf=prod.getProductSkuCode().substring(prod.getProductSkuCode().length()-5);
   	 	FullString=firstHalf+middle+secondHalf;
   	 	if(firstHalf.equals("sna")){
   		 inventoryProductsWrapper.productCodeTextView.setText(prod.getProductSkuCode());
   	 	}else {
   		 inventoryProductsWrapper.productCodeTextView.setText(FullString);
   		 }
		inventoryProductsWrapper.productUnitTextView.setText(prod
				.getProductSkuUnits().getUnitValue());
		inventoryProductsWrapper.productTaxTextView.setText(inventorySku
				.getTaxRate() + "%");
		inventoryProductsWrapper.productPurchasePriceTextView
				.setText(SnapToolkitTextFormatter.formatPriceText(
						inventorySku.getPurchasePrice(), context));
		inventoryProductsWrapper.productSalesPriceTextView
				.setText(SnapToolkitTextFormatter.formatPriceText(inventorySku
						.getProductSku().getProductSkuSalePrice(), context));
		inventoryProductsWrapper.productCategoryTextView.setText(prod
				.getProductCategory().getParenCategory().getCategoryName());
		inventoryProductsWrapper.productSubCategoryTextView.setText(prod
				.getProductCategory().getCategoryName());
		// inventoryProductsWrapper.productOfferSelectorImageView.setSelected(inventorySku.isOffer());
		inventoryProductsWrapper.productStoreSelectorImageView
				.setSelected(inventorySku.isOffer());
		inventoryProductsWrapper.productStoreSelectorImageView
				.setSelected(inventorySku.isStore());
		inventoryProductsWrapper.selectedImageView.setSelected(inventorySku
				.getProductSku().isSelected());
		return convertView;
	}

	public InventorySku getLastSelectedItem() {
		if (lastSelectedPos == -1)
			return null;
		else if (lastSelectedPos >= getCount())
			return null;
		else
			return getItem(lastSelectedPos);
	}

	public int getLastSelectedPos() {
		return lastSelectedPos;
	}

	public void setLastSelectedPos(int lastSelectedPos) {
		this.lastSelectedPos = lastSelectedPos;
	}

	OnClickListener inventoryProdClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			v.setSelected(true);
			if (lastSelectedView != null
					&& v.getId() != R.id.inventory_batch_button)
				lastSelectedView.setSelected(false);
			lastSelectedView = v;
			lastSelectedPos = (Integer) v.getTag();

			if (v.getId() == R.id.inventory_prodquantity_textview) {
				inventoryActionListener.onProductValueEdit(
						getItem(lastSelectedPos).getQuantity(),
						InventoryUpdateFragment.STOCKQTY_CONTEXT);
			} else if (v.getId() == R.id.inventory_mrp_textview) {
				inventoryActionListener.onProductValueEdit(
						getItem(lastSelectedPos).getProductSku()
								.getProductSkuMrp(),
						InventoryUpdateFragment.MRP_CONTEXT);
			} else if (v.getId() == R.id.inventory_purchaseprice_textview) {
				inventoryActionListener.onProductValueEdit(
						getItem(lastSelectedPos).getPurchasePrice(),
						InventoryUpdateFragment.PURCHASE_PRICE_CONTEXT);
			} else if (v.getId() == R.id.inventory_salesprice_textview) {
				inventoryActionListener.onProductValueEdit(
						getItem(lastSelectedPos).getProductSku()
								.getProductSkuSalePrice(),
						InventoryUpdateFragment.SALES_PRICE_CONTEXT);
			} else if (v.getId() == R.id.inventory_delete_button) {
				inventoryActionListener.onRemoveItem(getItem(lastSelectedPos));
			}

			else if (v.getId() == R.id.inventory_store_select_image) {

				InventorySku inventorySku = getItem(lastSelectedPos);
				if (!inventorySku.isStore()) {
					inventoryActionListener.updateInventorySku(inventorySku,
							"show_store", true);
					inventoryActionListener.updateInventorySku(inventorySku,
							"is_offer", true);
					inventorySku.setStore(true);

				} else {
					inventoryActionListener.updateInventorySku(inventorySku,
							"show_store", false);
					inventoryActionListener.updateInventorySku(inventorySku,
							"is_offer", false);
					inventorySku.setStore(false);
				}

				notifyDataSetChanged();

			}
			// else if(v.getId()==R.id.inventory_store_select_image) {
			// InventorySku inventorySku = getItem(lastSelectedPos);
			// if(inventorySku.isStore()){
			// inventorySku.setStore(!inventorySku.isStore());
			// notifyDataSetChanged();
			// inventoryActionListener.updateInventorySku(inventorySku,"show_store",true);
			// inventoryActionListener.updateInventorySku(inventorySku,"is_offer",true);
			// }else if(checkOfferStatus()){
			// inventorySku.setStore(!inventorySku.isStore());
			// notifyDataSetChanged();
			// inventoryActionListener.updateInventorySku(inventorySku,"show_store",inventorySku.isStore());
			// inventoryActionListener.updateInventorySku(inventorySku,"is_offer",inventorySku.isStore());
			// }else{
			// v.setSelected(false);
			// Toast.makeText(context,
			// "You have exceed allowed limit of images ",
			// Toast.LENGTH_SHORT).show();
			// }
			// }
			else if (v.getId() == R.id.inventory_batch_button) {
				inventoryActionListener.onShowBatches(lastSelectedPos);
			} else if (v.getId() == R.id.inventory_edit_prod_button) {
				inventoryActionListener.onEditProduct(getItem(lastSelectedPos));
			} else if (v.getId() == R.id.inventory_prodname_textview) {
				if (getLastSelectedItem() != null
						&& !getLastSelectedItem().getProductSku().isSelected())
					inventoryActionListener
							.onAddItemToInventory(getLastSelectedItem());
			}
		}
	};

	private class InventoryProductsWrapper {
		private TextView productNameTextView;
		private ImageView productImageView;
		// private ImageView productOfferSelectorImageView;
		private TextView productMrpTextView;
		private TextView productQuantityTextView;
		private TextView productTaxTextView;
		private TextView productCodeTextView;
		private TextView productUnitTextView;
		private TextView productSalesPriceTextView;
		private TextView productPurchasePriceTextView;
		private TextView productCategoryTextView;
		private TextView productSubCategoryTextView;
		private Button productRemoveButton;
		private Button productBatchesButton;
		private Button productEditButton;
		private ImageView selectedImageView;
		private ImageView productStoreSelectorImageView;
	}

	public interface OnInventoryActionListener {
		public void onProductValueEdit(float value, int context);

		public void onRemoveItem(InventorySku product);

		public void onShowBatches(int position);

		public void updateInventorySku(InventorySku inventorySku,
				String fieldName, boolean value);

		public void onEditProduct(InventorySku inventorySku);

		public void onAddItemToInventory(InventorySku inventorySku);
	}

	public boolean checkOfferStatus() {
		SnapBizzDatabaseHelper databaseHelper = SnapInventoryUtils
				.getDatabaseHelper(context);
		try {
			long numRows = databaseHelper.getInventorySkuDao().queryBuilder()
					.where().eq("is_offer", true).and().eq("show_store", true)
					.countOf();
			int limit = SnapToolkitConstants.OFFER_LIMIT * 2;
			if (numRows < limit) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

}
