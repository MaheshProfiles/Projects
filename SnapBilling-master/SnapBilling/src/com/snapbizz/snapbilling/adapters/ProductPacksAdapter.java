package com.snapbizz.snapbilling.adapters;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.InventoryProductAdapter.OnInventoryActionListener;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.db.GlobalDB;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;
import com.snapbizz.snaptoolkit.db.dao.ProductPacks;
import com.snapbizz.snaptoolkit.gdb.dao.Products;

public class ProductPacksAdapter extends ArrayAdapter<ProductPacks> {

	public static final int STOCKQTY_CONTEXT = 1;
	public static final int SALES_PRICE_ONE_CONTEXT = 2;
	public static final int SALES_PRICE_TWO_CONTEXT = 3;
	public static final int SALES_PRICE_THREE_CONTEXT = 4;
	
	private List <ProductPacks> productPacksList; 
	private LayoutInflater inflater;
	private String productName; 
	private long groupId; 
	private Context context;
	private OnInventoryActionListener onInventoryActionListener;	
	private ProductPacks selectedProductPack;
	
	public ProductPacksAdapter(Context context, List<ProductPacks> productPacksList, String productName, long groupId, OnInventoryActionListener onInventoryActionListener) {
		super(context, android.R.id.text1, productPacksList);
		this.context = context;
		this.productPacksList = productPacksList;
		this.groupId = groupId;
		this.onInventoryActionListener = onInventoryActionListener;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.productName = productName;
	}
	
	@Override
	public int getCount() {
		return productPacksList.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ProductPacks productPack = (ProductPacks) getItem(position);
		if (convertView == null) 
			 convertView = inflater.inflate(R.layout.listitem_product_packs, null);
		((TextView) convertView.findViewById(R.id.product_pack_name)).setText(this.productName);
		((TextView) convertView.findViewById(R.id.product_pack_qty)).setText(String.valueOf(productPack.getPackSize()));
		((TextView) convertView.findViewById(R.id.product_pack_sp1)).setText(String.valueOf(productPack.getSalePrice1() / 100));
		((TextView) convertView.findViewById(R.id.product_pack_sp2)).setText(String.valueOf(productPack.getSalePrice2() / 100));
		((TextView) convertView.findViewById(R.id.product_pack_sp3)).setText(String.valueOf(productPack.getSalePrice3() / 100));
		((TextView) convertView.findViewById(R.id.edit_product_pack_qty)).setText(String.valueOf(productPack.getPackSize()));
		((TextView) convertView.findViewById(R.id.edit_product_pack_sp1)).setText(String.valueOf(productPack.getSalePrice1() / 100));
		((TextView) convertView.findViewById(R.id.edit_product_pack_sp2)).setText(String.valueOf(productPack.getSalePrice2() / 100));
		((TextView) convertView.findViewById(R.id.edit_product_pack_sp3)).setText(String.valueOf(productPack.getSalePrice3() / 100));
		((ImageView) convertView.findViewById(R.id.inventory_prod_add)).setTag(productPack);
		((ImageView) convertView.findViewById(R.id.inventory_prod_delete)).setTag(productPack);
		((TextView) convertView.findViewById(R.id.product_pack_qty)).setTag(position);
		((TextView) convertView.findViewById(R.id.product_pack_sp1)).setTag(position);
		((TextView) convertView.findViewById(R.id.product_pack_sp2)).setTag(position);
		((TextView) convertView.findViewById(R.id.product_pack_sp3)).setTag(position);
		((ImageView) convertView.findViewById(R.id.inventory_prod_delete)).setOnClickListener(deletePackClickListener);
		((ImageView) convertView.findViewById(R.id.icon_prod_details)).setOnClickListener(prodDetailsClickListener);
		((ImageView) convertView.findViewById(R.id.inventory_prod_add)).setOnClickListener(addPackClickListener);
		((TextView) convertView.findViewById(R.id.product_pack_qty)).setOnClickListener(inventoryProdPackClickListener);
		((TextView) convertView.findViewById(R.id.product_pack_sp1)).setOnClickListener(inventoryProdPackClickListener);
		((TextView) convertView.findViewById(R.id.product_pack_sp2)).setOnClickListener(inventoryProdPackClickListener);
		((TextView) convertView.findViewById(R.id.product_pack_sp3)).setOnClickListener(inventoryProdPackClickListener);
		return convertView;
	}
	
	OnClickListener inventoryProdPackClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int selectedPos = (Integer) v.getTag();
			selectedProductPack = (ProductPacks) getItem(selectedPos);
			View convertView = v.getRootView();
			showFields(convertView);
			if (v.getId() == R.id.product_pack_qty) {
				  openNumberPad((EditText) convertView.findViewById(R.id.edit_product_pack_qty), ((TextView) convertView.findViewById
						  																							(R.id.product_pack_qty)));
			} else if (v.getId() == R.id.product_pack_sp1) {
				  openNumberPad((EditText) convertView.findViewById(R.id.edit_product_pack_sp1), ((TextView) convertView.findViewById
						  																							(R.id.product_pack_sp1)));
			} else if (v.getId() == R.id.product_pack_sp2) {
				  openNumberPad((EditText) convertView.findViewById(R.id.edit_product_pack_sp2), ((TextView) convertView.findViewById
						  																							(R.id.product_pack_sp2)));
			} else if (v.getId() == R.id.product_pack_sp3) {
				  openNumberPad((EditText) convertView.findViewById(R.id.edit_product_pack_sp3), ((TextView) convertView.findViewById
						  																								(R.id.product_pack_sp3)));
			} 
		}
	};
	
	OnEditorActionListener keyDoneClickListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
				Log.d("TAG", "v.getText()----"+v.getText());
				if (v.getId() == R.id.edit_product_pack_qty) {
					Log.d("TAG", "v.getText()----"+v.getText());
					selectedProductPack.setPackSize((Integer.parseInt(v.getText().toString())));
					onInventoryActionListener.onProdPackValueEdit(selectedProductPack);
				} else if (v.getId() == R.id.edit_product_pack_sp1) {
					selectedProductPack.setSalePrice1((Integer.parseInt(v.getText().toString())));
					onInventoryActionListener.onProdPackValueEdit(selectedProductPack);
					Log.d("TAG", "v.getText()----"+selectedProductPack.getSalePrice1());
				} else if (v.getId() == R.id.edit_product_pack_sp2) {
					selectedProductPack.setSalePrice2(Integer.parseInt(v.getText().toString()));
					onInventoryActionListener.onProdPackValueEdit(selectedProductPack);
				} else if (v.getId() == R.id.edit_product_pack_sp3) {
					selectedProductPack.setSalePrice3(Integer.parseInt(v.getText().toString()));
					onInventoryActionListener.onProdPackValueEdit(selectedProductPack);
				}
			}
			return false;
		}
	};
	
	OnClickListener prodDetailsClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			List<Products> groupDetails = GlobalDB.getInstance(context, true).getProductByGroupId(groupId) ;
			if (groupDetails != null && groupDetails.size() > 0)
				SnapBillingUtils.showGroupIdDetails(context, groupDetails);
		}
	};
	
	OnClickListener deletePackClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ProductPacks productPack = (ProductPacks) v.getTag();
			if (productPacksList.size() > 1) {
				SnapbizzDB snapbizzDB = SnapbizzDB.getInstance(context, false);
				snapbizzDB.packsHelper.deleteProductPack(productPack.getProductCode());
			} else {
				onInventoryActionListener.onRemoveItem(productPack.getProductCode());
			}
		}
	};
	
	OnClickListener addPackClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ProductPacks productPack = (ProductPacks) v.getTag();
			ProductPacks newProductPack = new ProductPacks();
			newProductPack.setProductCode(productPack.getProductCode() + 1);
			newProductPack.setPackSize(productPack.getPackSize() + 1);
			newProductPack.setSalePrice1(productPack.getSalePrice1());
			newProductPack.setSalePrice2(productPack.getSalePrice2());
			newProductPack.setSalePrice3(productPack.getSalePrice3());
			newProductPack.setIsDefault(true);
			newProductPack.setUpdatedAt(new Date());
			newProductPack.setCreatedAt(new Date());
			SnapbizzDB snapbizzDB = SnapbizzDB.getInstance(context, false);
			snapbizzDB.packsHelper.insertOrReplaceProductPack(newProductPack);
			productPacksList.add(newProductPack);
			notifyDataSetChanged();
		}
	};
	
	private void openNumberPad(EditText editText , TextView textView) {
		textView.setVisibility(View.GONE);
		editText.setVisibility(View.VISIBLE);
		editText.requestFocus();
		editText.selectAll();
		editText.setOnEditorActionListener(keyDoneClickListener);
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}
	
	private void showFields (View convertView) {
		 ((TextView) convertView.findViewById(R.id.edit_product_pack_sp1)).setVisibility(View.GONE);
		 ((TextView) convertView.findViewById(R.id.edit_product_pack_sp2)).setVisibility(View.GONE);
		 ((TextView) convertView.findViewById(R.id.edit_product_pack_sp3)).setVisibility(View.GONE);
		 ((TextView) convertView.findViewById(R.id.edit_product_pack_qty)).setVisibility(View.GONE);
		 ((TextView) convertView.findViewById(R.id.product_pack_qty)).setVisibility(View.VISIBLE);
		 ((TextView) convertView.findViewById(R.id.product_pack_sp1)).setVisibility(View.VISIBLE);
		 ((TextView) convertView.findViewById(R.id.product_pack_sp2)).setVisibility(View.VISIBLE);
		 ((TextView) convertView.findViewById(R.id.product_pack_sp3)).setVisibility(View.VISIBLE);
	}
}
