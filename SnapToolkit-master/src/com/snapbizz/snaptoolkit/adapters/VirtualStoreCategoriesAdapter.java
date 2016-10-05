package com.snapbizz.snaptoolkit.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.ProductCategory;

public class VirtualStoreCategoriesAdapter extends ArrayAdapter<ProductCategory> {

	private LayoutInflater layoutInflater;
	public int lastSelectedPos =- 1;
	private int layoutId = R.layout.griditem_store_category;
	private List<ProductCategory> productCategoryList;
	
	public VirtualStoreCategoriesAdapter(Context context, List<ProductCategory> productCategoryList, int layoutId) {
		super(context, android.R.id.text1, productCategoryList);
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layoutId = layoutId;
		this.productCategoryList = productCategoryList;
	}
	
	public VirtualStoreCategoriesAdapter(Context context, List<ProductCategory> productCategoryList) {
		super(context, android.R.id.text1, productCategoryList);
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.productCategoryList = productCategoryList;
	}

	public List<ProductCategory> getProductCategoryList() {
		return this.productCategoryList;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView categoryTextView;
		if(convertView == null) {
			convertView = layoutInflater.inflate(layoutId, null);
			categoryTextView = (TextView) convertView.findViewById(R.id.category_textview);
			convertView.setTag(categoryTextView);
		} else {
			categoryTextView = (TextView) convertView.getTag();
		}
		
		if(position == lastSelectedPos)
			categoryTextView.setSelected(true);
		else
			categoryTextView.setSelected(false);
		categoryTextView.setText(getItem(position).getCategoryName());
		return convertView;
	}
	
	public ProductCategory getLastSelectedItem() {
		if(lastSelectedPos==-1 || lastSelectedPos >= getCount())
			return null;
		return getItem(lastSelectedPos);
	}

}
