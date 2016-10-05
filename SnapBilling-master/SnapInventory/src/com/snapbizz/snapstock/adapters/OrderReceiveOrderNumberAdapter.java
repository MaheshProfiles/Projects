package com.snapbizz.snapstock.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Order;

public class OrderReceiveOrderNumberAdapter extends ArrayAdapter<Order> {

	private Context context;
	private LayoutInflater layoutInflater;

	public OrderReceiveOrderNumberAdapter(Context context, List<Order> myList) {
		super(context, android.R.id.text1, myList);
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView purchaseOrderNumber;
		if (null == convertView) {
			convertView = layoutInflater.inflate(R.layout.po_sub_list, null);
			purchaseOrderNumber = (TextView) convertView
					.findViewById(R.id.purchase_order_textView);
			convertView.setTag(purchaseOrderNumber);
		} 
		purchaseOrderNumber = (TextView) convertView.getTag();		

		Order order = getItem(position);

		if(order.isSelected()){
			purchaseOrderNumber.setSelected(true);
		}else{
			purchaseOrderNumber.setSelected(false);
		}
		
		purchaseOrderNumber.setText(order.getOrderNumber()+" - " + 
		SnapInventoryUtils.convertDateFormat(order.getOrderDate(), "dd/MM/yyyy", "dd MMM yyyy"));

		return convertView;

	}
}
