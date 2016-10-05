package com.snapbizz.snapbilling.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.db.dao.Customers;

public class CustomerSearchListAdapter extends ArrayAdapter<Customers> {

	private LayoutInflater layoutInflater;
	private int layoutId = R.layout.listitem_customer_search;

	public CustomerSearchListAdapter(Context context, int textViewResourceId,
			List<Customers> objects) {
		super(context, textViewResourceId, objects);
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public CustomerSearchListAdapter(Context context, int textViewResourceId,
			List<Customers> objects, int layoutId) {
		super(context, textViewResourceId, objects);
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layoutId = layoutId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) 
			convertView = layoutInflater.inflate(layoutId, null);
		Customers customer = getItem(position);
		((TextView) convertView.findViewById(R.id.search_customername_textview)).setText(customer.getName());
		((TextView) convertView.findViewById(R.id.search_customernumber_textview)).setText(String.valueOf(customer.getPhone()));
		((TextView) convertView.findViewById(R.id.search_customeraddress_textview)).setText(customer.getAddress());
		return convertView;
	}
}
