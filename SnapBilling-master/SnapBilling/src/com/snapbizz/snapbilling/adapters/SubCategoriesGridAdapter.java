package com.snapbizz.snapbilling.adapters;

import java.util.List;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.gdb.dao.ProductCategories;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SubCategoriesGridAdapter extends BaseAdapter {
	private Context context;
	List<ProductCategories> subCategories;
	private OnSubCategoryActionListener onSubCategoryActionListener;
	
	public SubCategoriesGridAdapter(Context context, List<ProductCategories> subCategories, OnSubCategoryActionListener onSubCategoryActionListener) {
		this.context = context;
		this.subCategories = subCategories;
		this.onSubCategoryActionListener = onSubCategoryActionListener;
	}

	@Override
	public int getCount() {
		return subCategories.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ProductCategories productCategories = subCategories.get(position);
		if (convertView == null) 
			convertView = inflater.inflate(R.layout.subcategoriesgrid, null);
		TextView textView = (TextView) convertView.findViewById(R.id.grid_item_label);
		textView.setText(productCategories.getName());
		String icon_Name = productCategories.getName().replaceAll("[\\-\\+\\.\\//^:,&]","");
		icon_Name = icon_Name.replaceAll("\\s+", "").toLowerCase().trim();
		if (SnapBillingUtils.getDrawable(icon_Name, context)!= 0) 
			((ImageView) convertView.findViewById(R.id.grid_item_image)).setBackgroundResource(SnapBillingUtils.getDrawable(icon_Name, context));
		else 
			((ImageView) convertView.findViewById(R.id.grid_item_image)).setBackgroundResource(SnapBillingUtils.getDrawable("otcothers", context));
		convertView.setTag(position);
		convertView.setOnClickListener(subCategoryClickListener);
		return convertView;
	}
	
	OnClickListener subCategoryClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int selectedPos = (Integer) v.getTag();
			ProductCategories productCategories = subCategories.get(selectedPos);
			onSubCategoryActionListener.onSubCategorySelected(productCategories.getId());
		}
	};
	
	public interface OnSubCategoryActionListener {
		public void onSubCategorySelected(long categoryId);
	}
}
