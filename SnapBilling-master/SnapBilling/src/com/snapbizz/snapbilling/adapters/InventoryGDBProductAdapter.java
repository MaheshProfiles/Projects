package com.snapbizz.snapbilling.adapters;

import java.util.List;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.domainsV2.InventoryDetails;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.db.GlobalDB;
import com.snapbizz.snaptoolkit.gdb.dao.Products;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InventoryGDBProductAdapter extends ArrayAdapter<InventoryDetails> {
	
	private Context context;
    private List <InventoryDetails> inventoryDetailsList; 
    private LayoutInflater inflater;
    private OnGdbProductSelectListener onGdbProductSelectListener;
	
	public InventoryGDBProductAdapter(Context context, List<InventoryDetails> inventoryDetailsList, 
																						OnGdbProductSelectListener onGdbProductSelectListener) {
		super(context, android.R.id.text1, inventoryDetailsList);
		this.context = context;
		this.inventoryDetailsList = inventoryDetailsList;
	    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    this.onGdbProductSelectListener = onGdbProductSelectListener;
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
			convertView = inflater.inflate(R.layout.listitem_inventory_gdb, null);
		String prodcode = String.valueOf(inventoryDetails.getProductSkuCode());
		if (prodcode.length() >= 8)
			prodcode = prodcode.substring(0, 3) + "..." + prodcode.substring(prodcode.length() - 5);
		((TextView) convertView.findViewById(R.id.inventory_prodcode_textview)).setText(prodcode);
   	 	((TextView) convertView.findViewById(R.id.inventory_prodname_textview)).setText(inventoryDetails.getProductSkuName());
   	 	((TextView) convertView.findViewById(R.id.inventory_mrp_textview)).setText(String.valueOf(inventoryDetails.getProductSkuMrp() / 100));
   	 	((TextView) convertView.findViewById(R.id.inventory_prod_cat_textview)).setText(inventoryDetails.getProductCategoryName());
   	 	((TextView) convertView.findViewById(R.id.inventory_prod_subcat_textview)).setText(inventoryDetails.getProductSubCategoryName());
   	 	((TextView) convertView.findViewById(R.id.inventory_prodtax_textview)).setText(inventoryDetails.getVAT() + "%");
   	 	((TextView) convertView.findViewById(R.id.inventory_produnit_textview)).setText(inventoryDetails.getUom());
   	 	((TextView) convertView.findViewById(R.id.inventory_prodname_textview)).setTag(position);
   	 	((ImageView) convertView.findViewById(R.id.group_id_details)).setTag(position);
   	    ((ImageView) convertView.findViewById(R.id.group_id_details)).setOnClickListener(groupIdDetailsClickListener);
   	 	((TextView) convertView.findViewById(R.id.inventory_prodname_textview)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InventoryDetails itemDetails = (InventoryDetails) getItem((Integer)v.getTag());
				onGdbProductSelectListener.onProductSelectedToInventory(itemDetails);
			}
		});
		return convertView;
	}
	
	OnClickListener groupIdDetailsClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int selectedPos = (Integer) v.getTag();
			InventoryDetails inventoryDetails = (InventoryDetails) getItem(selectedPos);
			List<Products> groupDetails = GlobalDB.getInstance(context, true).getProductByGroupId(inventoryDetails.getProductGid());
			if (groupDetails != null && groupDetails.size() > 0)
				SnapBillingUtils.showGroupIdDetails(context, groupDetails);
		}
	};
	
	public interface OnGdbProductSelectListener {
			public void onProductSelectedToInventory(InventoryDetails inventoryDetails);
	}
}