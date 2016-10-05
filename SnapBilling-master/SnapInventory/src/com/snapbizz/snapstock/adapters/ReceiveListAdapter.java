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

public class ReceiveListAdapter extends ArrayAdapter<ProductBean> {

	private Context context;
	private LayoutInflater layoutInflater;
	private OrderReceiveEditListener orderReceiveEditListener;
	private ProductBean expandableListViewChild;
	public View selectedView;

	public ReceiveListAdapter(Context context,List<ProductBean> purchaseOrderReceiveList,OrderReceiveEditListener orderReceiveEditListener) {
		super(context, android.R.id.text1, purchaseOrderReceiveList);
		this.context = context;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.orderReceiveEditListener = orderReceiveEditListener;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		expandableListViewChild = (ProductBean) getItem(position);
		ChildWrapper childWrapper;

		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.purchase_receive_sublist_items, parent, false);

			childWrapper = new ChildWrapper();
			childWrapper.productImage = (ImageView) convertView.findViewById(R.id.purchase_order_receive_image_view);
			childWrapper.imageCheckBox = (ImageView) convertView.findViewById(R.id.purchase_order_receive_checkView);
			childWrapper.productSkuName = (TextView) convertView.findViewById(R.id.purchase_order_receive_product_name_textView);
			childWrapper.productPendingQty = (TextView) convertView.findViewById(R.id.purchase_order_receive_product_pending_qty_textView);
			childWrapper.productBilledQty = (TextView) convertView.findViewById(R.id.purchase_order_receive_product_billed_qty_textView);
			childWrapper.productReceivedQty = (TextView) convertView.findViewById(R.id.purchase_order_receive_product_received_qty_textView);
			childWrapper.productMRP = (TextView) convertView.findViewById(R.id.purchase_order_receive_product_MRP_textView);
			childWrapper.productTaxRate = (TextView) convertView.findViewById(R.id.purchase_order_receive_product_tax_rate_textView);
			childWrapper.productPurchasePrice = (TextView) convertView.findViewById(R.id.purchase_order_receive_product_purchase_price_textView);
			childWrapper.productDiscount = (TextView) convertView.findViewById(R.id.purchase_order_receive_product_discount_textView);
			childWrapper.productNetAmount = (TextView) convertView.findViewById(R.id.purchase_order_receive_product_net_amount_textView);
			childWrapper.productTaxAmount = (TextView) convertView.findViewById(R.id.purchase_order_receive_product_tax_amount_textView);
			childWrapper.productTotal = (TextView) convertView.findViewById(R.id.purchase_order_receive_product_total_textView);
			// orderReceiveSubListAdapterWrapper.productSchemeValue = (TextView)
			// convertView.findViewById(R.id.purchase_order_receive_product_scheme_value_textView);

			convertView.setTag(childWrapper);
		}
		childWrapper = (ChildWrapper) convertView.getTag();
		
		if(expandableListViewChild.isSelected()){
			childWrapper.imageCheckBox.setEnabled(false);
		}else{
			childWrapper.imageCheckBox.setEnabled(true);
		}

		childWrapper.productImage.setImageDrawable(SnapCommonUtils.getProductDrawable(expandableListViewChild.getImageUri(), context));
		childWrapper.productSkuName.setText(expandableListViewChild.getProductName());
		childWrapper.productBilledQty.setText(String.valueOf(expandableListViewChild.getProductBilledQty()));
		childWrapper.productReceivedQty.setText(String.valueOf(expandableListViewChild.getProductPendingOrder()));
		childWrapper.productMRP.setText(SnapToolkitTextFormatter.formatPriceText(expandableListViewChild.getProductPrice(), context));
		childWrapper.productTaxRate.setText(expandableListViewChild.getVATRate() + "%");
		childWrapper.productPurchasePrice.setText(SnapToolkitTextFormatter.formatPriceText(expandableListViewChild.getProductPurchasePrice(), context));
		childWrapper.productDiscount.setText(SnapToolkitTextFormatter.formatPriceText(expandableListViewChild.getProductDiscount(), context));
		childWrapper.productNetAmount.setText(SnapToolkitTextFormatter.formatPriceText(expandableListViewChild.getProductNetAmount(), context));	
		childWrapper.productTaxAmount.setText(SnapToolkitTextFormatter.formatPriceText(expandableListViewChild.getVATAmount(), context));
		childWrapper.productTotal.setText(SnapToolkitTextFormatter.formatPriceText(expandableListViewChild.getProductTotalAmount(), context));
        if (expandableListViewChild.getProductBilledQty() > expandableListViewChild
                .getProductReceivedQty()) {
            if (expandableListViewChild.getProductTotalReceivedQty() != 0) {
                childWrapper.productPendingQty.setText(String.valueOf(expandableListViewChild.getProductBilledQty() - expandableListViewChild.getProductTotalReceivedQty() - expandableListViewChild.getProductPendingOrder()));
            } else {
                childWrapper.productPendingQty.setText(String.valueOf(expandableListViewChild.getProductBilledQty() - expandableListViewChild.getProductReceivedQty()));
            }
        } else {
            childWrapper.productPendingQty.setText(String.valueOf(0));
        }
		
		//setting on click listeners
		childWrapper.productBilledQty.setOnClickListener(onClickListener);
		childWrapper.productReceivedQty.setOnClickListener(onClickListener);
		childWrapper.productPurchasePrice.setOnClickListener(onClickListener);
		childWrapper.productDiscount.setOnClickListener(onClickListener);
		childWrapper.productTotal.setOnClickListener(onClickListener);
		childWrapper.imageCheckBox.setOnClickListener(onClickListener);
		childWrapper.productImage.setOnClickListener(onClickListener);
		childWrapper.productSkuName.setOnClickListener(onClickListener);
		childWrapper.productMRP.setOnClickListener(onClickListener);
		
		//setting tags on click listeners
		childWrapper.productBilledQty.setTag(position);
		childWrapper.productReceivedQty.setTag(position);
		childWrapper.productTaxRate.setTag(position);
		childWrapper.productPurchasePrice.setTag(position);
		childWrapper.productDiscount.setTag(position);
		childWrapper.productTotal.setTag(position);
		childWrapper.imageCheckBox.setTag(position);
		childWrapper.productSkuName.setTag(position);
		childWrapper.productImage.setTag(position);
		childWrapper.productMRP.setTag(position);

		return convertView;
	}

	public static class ChildWrapper {
		public ImageView productImage;
		public ImageView imageCheckBox;
		public TextView productSkuName;
		public TextView productPendingQty;
		public TextView productBilledQty;
		public TextView productReceivedQty;
		public TextView productMRP;
		public TextView productTaxRate;
		public TextView productPurchasePrice;
		public TextView productDiscount;
		public TextView productNetAmount;
		public TextView productTaxAmount;
		public TextView productTotal;
		public TextView productSchemeValue;
	}

	public interface OrderReceiveEditListener {
		public void onOrderReceivedEditQty(int position);

		public void onOrderReceiveCheckBoxChecked(int position);

		public void onProductPurchasePriceClicked(int position);

		public void onProductDiscountClicked(int position);

		public void onProductBilledQtyClicked(int position);

		public void onTotalClicked(int position);
		
		public void onMRPClicked(int position);
		
		public void onProductNameClicked(int position);
		
		public void onProductImageClicked(int position);
	}
	
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			selectedView = v;
			
			if( R.id.purchase_order_receive_checkView == v.getId() ){
				
				orderReceiveEditListener.onOrderReceiveCheckBoxChecked((Integer) v.getTag());
				
			}else if( R.id.purchase_order_receive_product_billed_qty_textView == v.getId() ){
				
			    v.setEnabled(false);
				orderReceiveEditListener.onProductBilledQtyClicked((Integer) v.getTag());
				
			}else if( R.id.purchase_order_receive_product_received_qty_textView == v.getId() ){
				
			    v.setEnabled(false);
				orderReceiveEditListener.onOrderReceivedEditQty((Integer) v.getTag());
				
			}else if( R.id.purchase_order_receive_product_purchase_price_textView == v.getId() ){
				
			    v.setEnabled(false);
				orderReceiveEditListener.onProductPurchasePriceClicked((Integer) v.getTag());
				
			}else if( R.id.purchase_order_receive_product_discount_textView == v.getId() ){
				
			    v.setEnabled(false);
				orderReceiveEditListener.onProductDiscountClicked((Integer) v.getTag());
				
			}else if( R.id.purchase_order_receive_product_total_textView == v.getId() ){
				
			    v.setEnabled(false);
				orderReceiveEditListener.onTotalClicked((Integer) v.getTag());
				
			} else if ( R.id.purchase_order_receive_product_name_textView == v.getId() ){
				
				orderReceiveEditListener.onProductNameClicked((Integer) v.getTag());
				
			} else if ( R.id.purchase_order_receive_image_view == v.getId() ){
				
				orderReceiveEditListener.onProductImageClicked((Integer) v.getTag());
				
			}else if ( R.id.purchase_order_receive_product_MRP_textView == v.getId() ){
				
			    v.setEnabled(false);
				orderReceiveEditListener.onMRPClicked((Integer) v.getTag());
				
			}
		}
	};

}
