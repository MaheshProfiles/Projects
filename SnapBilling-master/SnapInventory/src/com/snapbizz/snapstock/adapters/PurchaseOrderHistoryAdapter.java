package com.snapbizz.snapstock.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snaptoolkit.domains.Order;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;

public class PurchaseOrderHistoryAdapter extends ArrayAdapter<Order> {
	
	private Context context;
	private LayoutInflater layoutInflater;
	private PurchaseOrderEditListener purchaseOrderEditListener;
	
	public PurchaseOrderHistoryAdapter(Context context, List<Order> orderList, PurchaseOrderEditListener purchaseOrderEditListener){
		super(context, android.R.id.text1, orderList);
		this.context = context;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.purchaseOrderEditListener = purchaseOrderEditListener;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
			Order order = getItem(position);
			POHistoryWrapper poHistoryWrapper;
			
			if(null == convertView){
				convertView = layoutInflater.inflate(R.layout.purchase_order_history_layout, null);
				poHistoryWrapper = new POHistoryWrapper();
				poHistoryWrapper.PONumberTextView = (TextView) convertView.findViewById(R.id.po_number_date_textView);
				poHistoryWrapper.POAmount = (TextView) convertView.findViewById(R.id.po_amount_textView);
				poHistoryWrapper.POEdit = (ImageButton) convertView.findViewById(R.id.purchase_order_edit_image_view);
				convertView.setTag(poHistoryWrapper);
			}
		
			poHistoryWrapper = (POHistoryWrapper) convertView.getTag();
			
			poHistoryWrapper.PONumberTextView.setText("PO Number:- " + order.getOrderNumber() + "\n" + order.getOrderDate());
			poHistoryWrapper.POAmount.setText(SnapToolkitTextFormatter.formatPriceText(order.getPaymentToMake(), context));
			poHistoryWrapper.POEdit.setOnClickListener(onEditClickListener);
			poHistoryWrapper.POEdit.setTag(position);
			
			return convertView;			
		}
	
	View.OnClickListener onEditClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(R.id.purchase_order_edit_image_view == v.getId()){
				purchaseOrderEditListener.onPurchaseOrderEditClicked((Integer) v.getTag());
			}
		}
	};
	
	public static class POHistoryWrapper{
		public TextView PONumberTextView;
		public TextView POAmount;
		public ImageButton POEdit;
	}
	
	public interface PurchaseOrderEditListener{
		public void onPurchaseOrderEditClicked(int position);
	}
}
