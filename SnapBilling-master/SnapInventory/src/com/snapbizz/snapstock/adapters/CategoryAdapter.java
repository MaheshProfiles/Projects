package com.snapbizz.snapstock.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snaptoolkit.domains.ProductCategory;

public class CategoryAdapter extends ArrayAdapter<ProductCategory> {

    private LayoutInflater inflater;
    private int layoutId;
    private int lastSelectedPosition = -1;
    private boolean showAllCategories;

    public CategoryAdapter(Context context, List<ProductCategory> objects, int layoutId, boolean showAllCategories) {
        super(context, android.R.id.text1, objects);
        // TODO Auto-generated constructor stub
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
        this.showAllCategories = showAllCategories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        CategoryAdapterWrapper categoryAdapterWrapper;
        if (convertView == null) {
            categoryAdapterWrapper = new CategoryAdapterWrapper();
            convertView = inflater.inflate(layoutId, null);
            categoryAdapterWrapper.categoryNameTextView = (TextView) convertView.findViewById(R.id.category_name_textview);
            convertView.setTag(categoryAdapterWrapper);
        } else {
            categoryAdapterWrapper = (CategoryAdapterWrapper) convertView.getTag();
        }
        ProductCategory productCategory = getItem(position);
        if(layoutId == R.layout.listitem_category) {
            if(lastSelectedPosition == position) {
                convertView.setBackgroundResource(android.R.color.holo_blue_light);
                categoryAdapterWrapper.categoryNameTextView.setSelected(true);
            } else {
                convertView.setBackgroundResource(R.drawable.listitem_bg_selector);
                categoryAdapterWrapper.categoryNameTextView.setSelected(false);
            }
        }
        if(productCategory != null)
            categoryAdapterWrapper.categoryNameTextView.setText(getItem(position).getCategoryName());
        else
            categoryAdapterWrapper.categoryNameTextView.setText("All Categories");
        return convertView;
    }

    private static class CategoryAdapterWrapper {
        public TextView categoryNameTextView;
    }

    public ProductCategory getLastSelectedItem() {
        if(lastSelectedPosition == -1)
            return null;
        else
            return getItem(lastSelectedPosition);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if(layoutId == R.layout.listitem_category && showAllCategories)
            return super.getCount()+1;
        else
            return super.getCount();
    }

    @Override
    public ProductCategory getItem(int position) {
        // TODO Auto-generated method stub
        if(layoutId == R.layout.listitem_category && showAllCategories) {
            if(position == 0)
                return null;
            else
                return super.getItem(position - 1);
        } else
            return super.getItem(position);
    }

    public void setLastSelectedPosition(int lastSelectedPosition) {
        this.lastSelectedPosition = lastSelectedPosition;
    }

}
