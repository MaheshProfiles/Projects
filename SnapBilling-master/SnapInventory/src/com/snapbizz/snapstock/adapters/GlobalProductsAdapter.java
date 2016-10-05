package com.snapbizz.snapstock.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.ProductSku;

public class GlobalProductsAdapter extends ArrayAdapter<ProductSku> {

	private Context context;
	private LayoutInflater inflater;
	public int lastSelecteditem=-1;
	
	public GlobalProductsAdapter(Context context,
			List<ProductSku> globalProducts) {
		super(context, android.R.id.text1, globalProducts);
		// TODO Auto-generated constructor stub
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		GlobalProductsWrapper globalProductsWrapper;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listitem_global_update,
					null);
			globalProductsWrapper = new GlobalProductsWrapper();
			globalProductsWrapper.productImage = (ImageView) convertView
					.findViewById(R.id.update_global_prod_image);
			globalProductsWrapper.productNameText = (TextView) convertView
					.findViewById(R.id.update_global_prod_name);
			globalProductsWrapper.productPriceText = (TextView) convertView
					.findViewById(R.id.update_global_prod_price);
			globalProductsWrapper.selectedImageView = (ImageView) convertView
					.findViewById(R.id.update_global_prod_button_selected);
			convertView.setTag(globalProductsWrapper);
		} else {
			globalProductsWrapper = (GlobalProductsWrapper) convertView
					.getTag();
		}
		ProductSku prod = getItem(position);
		globalProductsWrapper.productImage.setImageDrawable(SnapInventoryUtils
				.getProductDrawable(prod, context));
		globalProductsWrapper.productNameText.setText(prod.getProductSkuName());
		globalProductsWrapper.productPriceText.setText(prod.getProductSkuMrp()
				+ "");
		globalProductsWrapper.selectedImageView.setSelected(prod.isSelected());
		return convertView;
	}
	
	@Override
	public void clear() {
	    // TODO Auto-generated method stub
	    super.clear();
	    lastSelecteditem=-1;
	}
	
	@Override
	public ProductSku getItem(int position) {
	    // TODO Auto-generated method stub
	    if(position==-1)
	        return null;
	    else
	    return super.getItem(position);
	}

	private class GlobalProductsWrapper {
		private TextView productNameText;
		private ImageView productImage;
		private ImageView selectedImageView;
		private TextView productPriceText;
	}

}
