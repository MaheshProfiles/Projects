package com.snapbizz.snapbilling.adapters;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.gdb.dao.ProductCategories;


public class ProductCategoriesAdapter extends ArrayAdapter<ProductCategories> {
	private LayoutInflater inflater;
	private int layoutId;
	private Context context;
	private OnCategoryActionListener onCategoryActionListener;

	public ProductCategoriesAdapter(Context context, List<ProductCategories> objects, int layoutId, OnCategoryActionListener onCategoryActionListener) {
		super(context, android.R.id.text1, objects);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.layoutId = layoutId;
		this.onCategoryActionListener = onCategoryActionListener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String icon_Name;
		if (convertView == null)
			convertView = inflater.inflate(layoutId, null);
		ProductCategories productCategory = getItem(position);
		if (productCategory.getName().indexOf(" ")> 0) 
			icon_Name = (productCategory.getName().substring(0, productCategory.getName().indexOf(" ")).toLowerCase()).trim();
		else 
			icon_Name = (productCategory.getName().toLowerCase()).trim();
		((ImageView) convertView.findViewById(R.id.category_icon)).setBackgroundResource(SnapBillingUtils.getDrawable(icon_Name, context));
		((TextView) convertView.findViewById(R.id.category_name)).setText(productCategory.getName());
		convertView.setTag(position);
		convertView.setOnClickListener(categoryClickListener);
		return convertView;
	}
	
	OnClickListener categoryClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			v.setSelected(true);
			((TextView) v.findViewById(R.id.category_name)).setSelected(true);
			((ImageView) v.findViewById(R.id.category_icon)).setSelected(true);
			int position = (Integer) v.getTag();
			ProductCategories productCategory = getItem(position);
			onCategoryActionListener.onSelectItem(productCategory.getId());
		}
	};
	
	public interface OnCategoryActionListener {
			public void onSelectItem(long parentId);
	}
}
