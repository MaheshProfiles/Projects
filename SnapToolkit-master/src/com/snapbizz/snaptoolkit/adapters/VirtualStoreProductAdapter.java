package com.snapbizz.snaptoolkit.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;

public class VirtualStoreProductAdapter extends ArrayAdapter<ProductSku> {

	private LayoutInflater layoutInflater;
	private Context context;

	public VirtualStoreProductAdapter(Context context, ArrayList<ProductSku> productList) {
		super(context, android.R.id.text1, productList);
		// TODO Auto-generated constructor stub
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ProductAdapterWrapper productAdapterWrapper;
		if(convertView == null) {
			convertView = layoutInflater.inflate(R.layout.griditem_store_product, null);
			convertView.findViewById(R.id.product_mrp_textview).setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.product_sp_textview).setVisibility(View.VISIBLE);
			productAdapterWrapper = new ProductAdapterWrapper();
			productAdapterWrapper.productImageView = (ImageView) convertView.findViewById(R.id.product_imageview);
			productAdapterWrapper.productNameTextView = (TextView) convertView.findViewById(R.id.product_name_textview);
			productAdapterWrapper.productMRPTextView = (TextView) convertView.findViewById(R.id.product_mrp_textview);
			productAdapterWrapper.productSPTextView = (TextView) convertView.findViewById(R.id.product_sp_textview);
			convertView.setTag(productAdapterWrapper);
		} else {
			productAdapterWrapper = (ProductAdapterWrapper) convertView.getTag();
		}
		productAdapterWrapper.productImageView.setImageDrawable(SnapCommonUtils.getProductDrawable(getItem(position), context));
		productAdapterWrapper.productNameTextView.setText(getItem(position).getProductSkuName());
		productAdapterWrapper.productMRPTextView.setText(SnapToolkitTextFormatter.formatPriceText(getItem(position).getProductSkuMrp(), context));
		productAdapterWrapper.productSPTextView.setText(SnapToolkitTextFormatter.formatPriceText(getItem(position).getProductSkuSalePrice(), context));
		if (getItem(position).getProductSkuMrp() - getItem(position).getProductSkuSalePrice() > 0) {
            productAdapterWrapper.productMRPTextView.setPaintFlags(productAdapterWrapper.productMRPTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            productAdapterWrapper.productMRPTextView.setPaintFlags(productAdapterWrapper.productMRPTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
		return convertView;
	}

	private static class ProductAdapterWrapper {
		public ImageView productImageView;
		public TextView productNameTextView;
		public TextView productMRPTextView;
		public TextView productSPTextView;
	}

}
