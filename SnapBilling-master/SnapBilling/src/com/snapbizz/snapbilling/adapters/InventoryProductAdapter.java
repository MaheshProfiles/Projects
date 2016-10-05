package com.snapbizz.snapbilling.adapters;

import java.util.List;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.domainsV2.InventoryDetails;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.db.GlobalDB;
import com.snapbizz.snaptoolkit.db.dao.ProductPacks;
import com.snapbizz.snaptoolkit.gdb.dao.Products;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InventoryProductAdapter extends ArrayAdapter<InventoryDetails> {

	private Context context;
	private List<InventoryDetails> inventoryDetailsList;
	private OnInventoryActionListener onInventoryActionListener;
	private LayoutInflater inflater;

	public InventoryProductAdapter(Context context, List<InventoryDetails> inventoryDetailsList, 
														OnInventoryActionListener onInventoryActionListener) {
		super(context, android.R.id.text1, inventoryDetailsList);
		this.context = context;
		this.inventoryDetailsList = inventoryDetailsList;
		this.onInventoryActionListener = onInventoryActionListener;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return inventoryDetailsList.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		InventoryDetails inventoryDetails = (InventoryDetails) getItem(position);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.listitem_inventory, null);
		((ImageView) convertView.findViewById(R.id.update_global_prod_button_selected)).setSelected(true);
		((TextView) convertView.findViewById(R.id.inventory_prodname_textview)).setText(inventoryDetails.getProductSkuName());
		String groupId = String.valueOf(inventoryDetails.getProductSkuCode());
		if (groupId.length() >= 8)
			groupId = groupId.substring(0, 3) + "..." + groupId.substring(groupId.length() - 5);
		((TextView) convertView.findViewById(R.id.inventory_prodcode_textview)).setText(groupId);
		((TextView) convertView.findViewById(R.id.inventory_mrp_textview)).setText(inventoryDetails.getProductSkuMrp() / 100 + "");
		((TextView) convertView.findViewById(R.id.inventory_prod_cat_textview)).setText(inventoryDetails.getProductCategoryName());
		((TextView) convertView.findViewById(R.id.inventory_prod_subcat_textview)).setText(inventoryDetails.getProductSubCategoryName());
		((TextView) convertView.findViewById(R.id.inventory_prodtax_textview)).setText(inventoryDetails.getVAT() + "%");
		((TextView) convertView.findViewById(R.id.inventory_produnit_textview)).setText(inventoryDetails.getUom());
		((Button) convertView.findViewById(R.id.inventory_delete_button)).setEnabled(true);
		((Button) convertView.findViewById(R.id.inventory_edit_prod_button)).setEnabled(true);
		((Button) convertView.findViewById(R.id.inventory_delete_button)).setTag(position);
		((Button) convertView.findViewById(R.id.inventory_edit_prod_button)).setTag(position);
		((TextView) convertView.findViewById(R.id.inventory_prodname_textview)).setTag(position);
		((Button) convertView.findViewById(R.id.inventory_delete_button)).setOnClickListener(inventoryProdClickListener);
		((Button) convertView.findViewById(R.id.inventory_edit_prod_button)).setOnClickListener(inventoryProdClickListener);
		ProductPacksAdapter productPacksAdapter = new ProductPacksAdapter(context, inventoryDetails.getProductPacksList(), 
										inventoryDetails.getProductSkuName(), inventoryDetails.getProductGid(), onInventoryActionListener);
		((ListView) convertView.findViewById(R.id.product_packs_list)).setAdapter(productPacksAdapter);
		((TextView) convertView.findViewById(R.id.inventory_prodname_textview)).setTag(convertView);
		((ImageView) convertView.findViewById(R.id.inventory_expand)).setTag(convertView);
		((TextView) convertView.findViewById(R.id.inventory_prodname_textview)).setOnClickListener(inventoryProdNameClickListener);
		((ImageView) convertView.findViewById(R.id.inventory_expand)).setOnClickListener(inventoryProdNameClickListener);
		((ImageView) convertView.findViewById(R.id.group_id_details)).setTag(position);
		((ImageView) convertView.findViewById(R.id.group_id_details)).setOnClickListener(groupIdDetailsClickListener);
		((TextView) convertView.findViewById(R.id.inventory_mrp_textview)).setVisibility(SnapBillingUtils.getVisibleStatus
																								(SnapSharedUtils.isMRPHeader(context), SnapBillingConstants.FIRST_COLUMN, context));
		//((TextView) convertView.findViewById(R.id.pp_header)).setVisibility(SnapBillingUtils.getVisibleStatus(SnapSharedUtils.isPurchasePriceHeader(context)));
		((TextView) convertView.findViewById(R.id.inventory_prodquantity_textview)).setVisibility(SnapBillingUtils.getVisibleStatus
																								(SnapSharedUtils.isQtyHeader(context), SnapBillingConstants.SECOND_COLUMN, context));
		((TextView) convertView.findViewById(R.id.inventory_prodtax_textview)).setVisibility(SnapBillingUtils.getVisibleStatus
																								(SnapSharedUtils.isVatRateHeader(context), SnapBillingConstants.THIRD_COLUMN,context));
		((TextView) convertView.findViewById(R.id.inventory_produnit_textview)).setVisibility(SnapBillingUtils.getVisibleStatus
																								(SnapSharedUtils.isUnitTypeHeader(context), SnapBillingConstants.FOURTH_COLUMN,context));
		((TextView) convertView.findViewById(R.id.inventory_prod_cat_textview)).setVisibility(SnapBillingUtils.getVisibleStatus
																								(SnapSharedUtils.isCategoryHeader(context), SnapBillingConstants.FIFTH_COLUMN, context));
		((TextView) convertView.findViewById(R.id.inventory_prod_subcat_textview)).setVisibility(SnapBillingUtils.getVisibleStatus
																								(SnapSharedUtils.isSubCategoryHeader(context), SnapBillingConstants.SIXTH_COLUMN, context));
		((RelativeLayout) convertView.findViewById(R.id.inventory_action_layout)).setVisibility(SnapBillingUtils.getVisibleStatus
																								(SnapSharedUtils.isActionsHeader(context), SnapBillingConstants.SEVENTH_COLUMN, context));
		return convertView;
	}

	OnClickListener groupIdDetailsClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int selectedPos = (Integer) v.getTag();
			InventoryDetails inventoryDetails = (InventoryDetails) getItem(selectedPos);
			List<Products> groupDetails = GlobalDB.getInstance(context, true).getProductByGroupId(inventoryDetails.getProductGid()) ;
			if (groupDetails != null && groupDetails.size() > 0)
				SnapBillingUtils.showGroupIdDetails(context, groupDetails);
		}
	};

	OnClickListener inventoryProdNameClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			View currentView = (View) v.getTag();
			if (((LinearLayout) currentView
					.findViewById(R.id.product_packs_layout)).getVisibility() == View.VISIBLE) {
				((LinearLayout) currentView
						.findViewById(R.id.product_packs_layout))
						.setVisibility(View.GONE);
				((ImageView) currentView.findViewById(R.id.inventory_expand))
						.setImageResource(R.drawable.inventory_down);
			} else {
				((LinearLayout) currentView
						.findViewById(R.id.product_packs_layout))
						.setVisibility(View.VISIBLE);
				((ImageView) currentView.findViewById(R.id.inventory_expand))
						.setImageResource(R.drawable.inventory_up);
			}
		}
	};

	OnClickListener inventoryProdClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int selectedPos = (Integer) v.getTag();
			InventoryDetails inventoryDetails = (InventoryDetails) getItem(selectedPos);
			if (v.getId() == R.id.inventory_delete_button)
				onInventoryActionListener.onRemoveItem(inventoryDetails.getProductSkuCode());
			else if (v.getId() == R.id.inventory_edit_prod_button)
				onInventoryActionListener.onEditProduct(inventoryDetails.getProductSkuCode());
		}
	};

	public interface OnInventoryActionListener {
		public void onRemoveItem(Long productSkuCode);
		public void onEditProduct(Long productSkuCode);
		public void onProdPackValueEdit(ProductPacks selectedProductPack);
	}
}
