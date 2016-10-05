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
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class BrandAdapter extends ArrayAdapter<Brand> {

	private LayoutInflater inflater;
	private Context context;
	private int layoutId = R.layout.listitem_brand;
	public int lastSelectedPos=-1;

	public BrandAdapter(Context context, List<Brand> objects) {
		super(context, android.R.id.text1, objects);
		// TODO Auto-generated constructor stub
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		BrandAdapterWrapper brandAdapterWrapper;
		if (convertView == null) {
			brandAdapterWrapper = new BrandAdapterWrapper();
			convertView = inflater.inflate(layoutId, null);
			brandAdapterWrapper.brandImageView = (ImageView) convertView
					.findViewById(R.id.brand_imageview);
			brandAdapterWrapper.brandSelectorImageView = (ImageView) convertView
					.findViewById(R.id.brand_selected_imagebutton);

			convertView.setTag(brandAdapterWrapper);
		} else {
			brandAdapterWrapper = (BrandAdapterWrapper) convertView.getTag();
		}
		brandAdapterWrapper.brandImageView.setImageDrawable(SnapCommonUtils
				.getBrandDrawable(getItem(position), context));
		brandAdapterWrapper.brandSelectorImageView.setSelected(getItem(position).isSelected());
		return convertView;
	}

	private static class BrandAdapterWrapper {
		public ImageView brandSelectorImageView;
		public ImageView brandImageView;
	}

}
