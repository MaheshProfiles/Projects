package com.snapbizz.snapbilling.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.domains.HotProduct;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;

public class HotproductsListAdapter extends ArrayAdapter<HotProduct> {

	private Context context;
	private LayoutInflater layoutInflater;

	public HotproductsListAdapter(Context context, int textViewResourceId,
			List<HotProduct> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) 
			convertView = layoutInflater.inflate(R.layout.listitem_hotproducts, null);
			
		HotProduct hotProduct = getItem(position);
		((TextView) convertView.findViewById(R.id.total_customers_textview)).setText(hotProduct.getTotalCustomers() + " " +  this.context.getResources().getString(R.string.customers));
		((ImageView) convertView.findViewById(R.id.product_imageview)).setImageDrawable(SnapBillingUtils.getProductDrawable(hotProduct.getImageUrl(), context));
		((TextView) convertView.findViewById(R.id.product_name_textview)).setText(hotProduct.getProductName());
		((TextView) convertView.findViewById(R.id.total_qtysold_textview)).setText(hotProduct.getTotalQuantity() + " " +  this.context.getResources().getString(R.string.pieces));
		((TextView) convertView.findViewById(R.id.total_sales_textview)).setText(SnapBillingTextFormatter.formatPriceText(hotProduct.getTotalSales(), context));
		return convertView;
	}
}
