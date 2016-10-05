package com.snapbizz.snapbilling.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class QuickAddCategoryAdapter extends ArrayAdapter<ProductCategory> {

    private LayoutInflater layoutInflater;
    private Context context;

    public QuickAddCategoryAdapter(Context context, ArrayList<ProductCategory> productList) {
        super(context, android.R.id.text1, productList);
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) 
            convertView = layoutInflater.inflate(com.snapbizz.snapbilling.R.layout.griditem_quickadd, null);
        ProductCategory productCategory = getItem(position);
        ((ImageView) convertView.findViewById(R.id.product_imageview)).setImageDrawable(SnapCommonUtils.getProductCategoryDrawable(productCategory, context));
        ((TextView) convertView.findViewById(R.id.product_name_textview)).setText(productCategory.getCategoryName());
        return convertView;
    }

}
