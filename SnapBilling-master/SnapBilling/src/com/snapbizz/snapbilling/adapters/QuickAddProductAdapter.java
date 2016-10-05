package com.snapbizz.snapbilling.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.fragments.BillCheckoutFragment.OnCreateEditQuickAddProductClickListener;
import com.snapbizz.snapbilling.utils.GoogleAnalyticsTracker;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snaptoolkit.domains.ProductSku;

public class QuickAddProductAdapter extends ArrayAdapter<ProductSku> {

    private LayoutInflater layoutInflater;
    private Context context;
    private OnCreateEditQuickAddProductClickListener onQuickAddEditListener;
    private int lastSelectedItemPosition = -1;
	private static final String TAG = "[QuickAddProductAdapter]";

    public QuickAddProductAdapter(Context context, ArrayList<ProductSku> productList, OnCreateEditQuickAddProductClickListener onQuickAddEditListener) {
        super(context, android.R.id.text1, productList);
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.onQuickAddEditListener = onQuickAddEditListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) 
            convertView = layoutInflater.inflate(R.layout.listitem_quickadd_product, null);
        ((ImageButton) convertView.findViewById(R.id.edit_quickadd_product_imagebutton)).setOnClickListener(onQuickAddEditClickListener); 
        ProductSku productSku = getItem(position);
        if (position == lastSelectedItemPosition) {
            (convertView.findViewById(R.id.product_container_layout)).setBackgroundResource(android.R.color.holo_blue_light);
            (convertView.findViewById(R.id.product_container_layout)).setSelected(true);
        } else {
            (convertView.findViewById(R.id.product_container_layout)).setSelected(false);
            (convertView.findViewById(R.id.product_container_layout)).setBackgroundResource(R.drawable.listitem_bg_selector);
        }
        
        if (productSku == null) {
            ((TextView) convertView.findViewById(R.id.quickadd_productname_textview)).setText("");
            ((ImageButton) convertView.findViewById(R.id.edit_quickadd_product_imagebutton)).setVisibility(View.GONE);
            ((TextView) convertView.findViewById(R.id.quickadd_productsaleprice_textview)).setText("");
            ((ImageView) convertView.findViewById(R.id.quickadd_product_imageview)).setImageResource(R.drawable.icon_add_new_item);
            ((ImageView) convertView.findViewById(R.id.quickadd_product_imageview)).setVisibility(View.VISIBLE);
        } else {
            ((ImageButton) convertView.findViewById(R.id.edit_quickadd_product_imagebutton)).setVisibility(View.VISIBLE);
            ((ImageView) convertView.findViewById(R.id.quickadd_product_imageview)).setVisibility(View.GONE);
            ((TextView) convertView.findViewById(R.id.quickadd_productname_textview)).setText(productSku.getProductSkuName());
            ((ImageButton) convertView.findViewById(R.id.edit_quickadd_product_imagebutton)).setTag(position);
            ((TextView) convertView.findViewById(R.id.quickadd_productsaleprice_textview)).setText(SnapBillingTextFormatter.formatPriceText(productSku.getProductSkuSalePrice(), context));
        }
        return convertView;
    }
    
    public void setLastSelectedItemPosition(int position) {
        this.lastSelectedItemPosition = position;
    }
    
    View.OnClickListener onQuickAddEditClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onQuickAddEditListener.onEditQuickAddClicked(getItem((Integer) v.getTag()));
            GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(context, v, TAG);
        }
    };
    
    @Override
    public ProductSku getItem(int position) {
        if (position == 0)
            return null;
        else
            return super.getItem(position - 1);
    };
    
    @Override
    public int getCount() {
        return super.getCount() + 1;
    };
}
