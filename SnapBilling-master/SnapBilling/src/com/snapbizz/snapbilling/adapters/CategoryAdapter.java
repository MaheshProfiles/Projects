package com.snapbizz.snapbilling.adapters;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.domains.ProductCategory;


public class CategoryAdapter extends ArrayAdapter<ProductCategory> {

    private LayoutInflater inflater;
    private int layoutId;
    private int lastSelectedPosition = -1;
    private boolean showAllCategories;
    private Context context;

    public CategoryAdapter(Context context, List<ProductCategory> objects, int layoutId, boolean showAllCategories) {
        super(context, android.R.id.text1, objects);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutId = layoutId;
        this.showAllCategories = showAllCategories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) 
            convertView = inflater.inflate(layoutId, null);
        ProductCategory productCategory = getItem(position);
        if (layoutId == R.layout.listitem_category) {
            if (lastSelectedPosition == position) {
                convertView.setBackgroundResource(android.R.color.holo_blue_light);
                ((TextView) convertView.findViewById(R.id.category_name_textview)).setSelected(true);
            } else {
                convertView.setBackgroundResource(R.drawable.listitem_bg_selector);
                ((TextView) convertView.findViewById(R.id.category_name_textview)).setSelected(false);
            }
        }
        if (productCategory != null)
            ((TextView) convertView.findViewById(R.id.category_name_textview)).setText(getItem(position).getCategoryName());
        else
            ((TextView) convertView.findViewById(R.id.category_name_textview)).setText(this.context.getResources().getString(R.string.all_category));
        return convertView;
    }

    public ProductCategory getLastSelectedItem() {
        if (lastSelectedPosition == -1)
            return null;
        else
            return getItem(lastSelectedPosition);
    }

    @Override
    public int getCount() {
        if (layoutId == R.layout.listitem_category && showAllCategories)
            return super.getCount() + 1;
        else
            return super.getCount();
    }

    @Override
    public ProductCategory getItem(int position) {
        if (layoutId == R.layout.listitem_category && showAllCategories) {
            if (position == 0)
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
