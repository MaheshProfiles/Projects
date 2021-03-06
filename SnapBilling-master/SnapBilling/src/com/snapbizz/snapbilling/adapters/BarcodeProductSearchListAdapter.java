package com.snapbizz.snapbilling.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;

public class BarcodeProductSearchListAdapter extends ArrayAdapter<ProductSku> {
	private LayoutInflater layoutInflater;
	private int layoutId = R.layout.barcode_product_row;
	private Context context;
	public BarcodeProductSearchListAdapter(Context context, int textViewResourceId,
			List<ProductSku> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public BarcodeProductSearchListAdapter(Context context, int textViewResourceId,
			List<ProductSku> objects, int layoutId) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layoutId = layoutId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) 
			convertView = layoutInflater.inflate(layoutId, null);
		ProductSku prod = getItem(position);
		((TextView) convertView.findViewById(R.id.barcode_productname_textview)).setText(prod.getProductSkuName());
		((TextView) convertView.findViewById(R.id.barcode_productmrp_textview)).setText(SnapToolkitTextFormatter.formatPriceText(prod.getProductSkuMrp(), context));
		((TextView) convertView.findViewById(R.id.barcode_productrate_textview)).setText(SnapToolkitTextFormatter.formatPriceText(prod.getProductSkuSalePrice(), context));
		((TextView) convertView.findViewById(R.id.barcode_productmrp_textview)).setPaintFlags(((TextView) convertView.findViewById(R.id.barcode_productmrp_textview)).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		return convertView;
	}
}
