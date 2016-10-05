package com.snapbizz.snapbilling.adapters;

import java.util.List;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.db.GlobalDB;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;
import com.snapbizz.snaptoolkit.gdb.dao.ProductCategories;
import com.snapbizz.snaptoolkit.gdb.dao.Products;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProductGroupIdAdapter extends ArrayAdapter<Products> {

	private Context context;
	private List <Products> groupDetails;
    private LayoutInflater inflater;
    private GlobalDB globalDB;
    public ProductGroupIdAdapter(Context context, List <Products> groupDetails) {
		super(context, android.R.id.text1, groupDetails);
		this.context = context;
		this.groupDetails = groupDetails;
	    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    globalDB = GlobalDB.getInstance(this.context, true);
	}

	@Override
	public int getCount() {
		return groupDetails.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Products products = (Products) getItem(position);
		ProductCategories productCategories = globalDB.getCategory((long) products.getCategoryId());
	   	 if (convertView == null)
	   		  convertView = inflater.inflate(R.layout.groupid_list_item, null);
	   	 ((TextView) convertView.findViewById(R.id.group_item_name_text)).setText(products.getName());
	   	 ((TextView) convertView.findViewById(R.id.group_item_mrp_text)).setText(String.valueOf(products.getMrp() / 100));
	   	 ((TextView) convertView.findViewById(R.id.group_item_stock_text)).setText(String.valueOf(products.getMeasure()));
	   	 ((TextView) convertView.findViewById(R.id.group_item_vat_text)).setText(String.valueOf(globalDB.getVatRate(products.getVatId())));
	   	 ((TextView) convertView.findViewById(R.id.group_item_unit_text)).setText(products.getUom());
	   	 ((TextView) convertView.findViewById(R.id.group_item_category_text)).setText(globalDB.getCategory((long)productCategories.getParentId()).getName());
	   	 ((TextView) convertView.findViewById(R.id.group_item_subcategory_text)).setText(productCategories.getName());
	   	 return convertView;
	}
}
