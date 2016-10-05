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
import com.snapbizz.snapstock.domains.ProductBean;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;

public class OrderListAdapter extends ArrayAdapter<ProductBean>{

	private LayoutInflater layoutInflater;
	private Context context;
	private InventoryOrderEditListener inventoryOrderEditListener;
	private ProductBean expandableListViewChild;
	public View selectedView;

	public OrderListAdapter(Context context, List<ProductBean> distributorProductList, InventoryOrderEditListener inventoryOrderEditListener) {
		super(context, android.R.id.text1, distributorProductList);
		this.context = context;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.inventoryOrderEditListener = inventoryOrderEditListener;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		expandableListViewChild = getItem(position);
	    ProductWrapper productWrapper;

		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.distributor_product_sub_list_items, parent, false);
			productWrapper = new ProductWrapper();
			productWrapper.productImageView = (ImageView) convertView.findViewById(R.id.distributor_product_image_view);
			productWrapper.productCheckView = (ImageView) convertView.findViewById(R.id.product_order_checkView);
			productWrapper.productSkuName = (TextView) convertView.findViewById(R.id.product_sku_name_textView);
			productWrapper.productQty = (TextView) convertView.findViewById(R.id.product_in_stock_qty_textView);
			productWrapper.productMRP = (TextView) convertView.findViewById(R.id.product_MRP_textView);
			productWrapper.productPendingOrder = (TextView) convertView.findViewById(R.id.product_pending_order_textView);
			productWrapper.productToOrder = (TextView) convertView.findViewById(R.id.product_to_order_textView);			
			productWrapper.productWeeklyTrends = (TextView) convertView.findViewById(R.id.product_weekly_trend_textView);
			productWrapper.productSKUHighestMargin = (TextView) convertView.findViewById(R.id.product_sku_highest_margin);
			productWrapper.productSchemeType = (TextView) convertView.findViewById(R.id.product_scheme_type_textView);
			productWrapper.productSchemeValue = (TextView) convertView.findViewById(R.id.product_scheme_value_textView);
			productWrapper.productSchemeValidty = (TextView) convertView.findViewById(R.id.product_validity_period_textView);
			convertView.setTag(productWrapper);
		} 
		
		productWrapper = (ProductWrapper) convertView.getTag();		

		productWrapper.productImageView.setImageDrawable(SnapCommonUtils
				.getProductDrawable(expandableListViewChild.getImageUri(), context));

        if (expandableListViewChild.isSelected()) {
            productWrapper.productCheckView.setEnabled(false);
        } else {
            productWrapper.productCheckView.setEnabled(true);
        }

		productWrapper.productSkuName.setText(expandableListViewChild.getProductName());
		productWrapper.productQty.setText(String.valueOf(expandableListViewChild.getProductQty()));
		productWrapper.productMRP.setText(SnapToolkitTextFormatter.formatPriceText(expandableListViewChild.getProductPrice(),context));
		productWrapper.productPendingOrder.setText(String.valueOf(expandableListViewChild.getProductPendingOrder()));
		productWrapper.productToOrder.setText(String.valueOf(expandableListViewChild.getProductToOrder()));

		
//		productWrapper.productWeeklyTrends.setText("WT");
//		productWrapper.productSimilarStock.setText("SS");
//		  
//		  if (!child.isOffer()) 
//		  { productWrapper.productSchemeType.setText("NA");
//		  productWrapper.productSchemeValue.setText("NA");
//		  productWrapper.productSchemeValidty.setText("NA"); } else {
//		  productWrapper.productSchemeType.setText("");
//	      productWrapper.productSchemeValue.setText("");
//	      productWrapper.productSchemeValidty.setText(""); }
		 
		
		//setting tags on the onClickListeners
		productWrapper.productToOrder.setTag(position);
		productWrapper.productCheckView.setTag(position);
		productWrapper.productSkuName.setTag(position);
		productWrapper.productImageView.setTag(position);
		
		//setting on click listeners
		productWrapper.productToOrder.setOnClickListener(onProductRowClicked);
		productWrapper.productCheckView.setOnClickListener(onProductRowClicked);
		productWrapper.productSkuName.setOnClickListener(onProductRowClicked);
		productWrapper.productImageView.setOnClickListener(onProductRowClicked);

		return convertView;
	}

	View.OnClickListener onProductRowClicked = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			selectedView = v;			
			if (R.id.product_order_checkView == v.getId() ) {
				inventoryOrderEditListener.onCheckViewClicked((Integer) v.getTag());				
			} else if ( R.id.product_to_order_textView == v.getId() ) {
	            v.setEnabled(false);
				inventoryOrderEditListener.onStockQuantityEdit((Integer) v.getTag());
			} else if ( R.id.product_sku_name_textView == v.getId() ){
				inventoryOrderEditListener.onProductNameClicked((Integer) v.getTag());
			} else if ( R.id.distributor_product_image_view == v.getId() ){
				inventoryOrderEditListener.onProductImageClicked((Integer) v.getTag());				
			}
		}
	};

	public static class ProductWrapper {
		public ImageView productImageView;
		public ImageView productCheckView;
		public TextView productSkuName;
		public TextView productQty;
		public TextView productMRP;
		public TextView productToOrder;
		public TextView productPendingOrder;
		public TextView productSchemeType; 
		public TextView productSchemeValue; 
		public TextView productSchemeValidty; 
		public TextView productSKUHighestMargin; 
		public TextView productWeeklyTrends;
		 
	}

	public interface InventoryOrderEditListener {
		public void onStockQuantityEdit(int position);

		public void onCheckViewClicked(int position);

		public void onProductNameClicked(int position);
		
		public void onProductImageClicked(int position);
	}
}
