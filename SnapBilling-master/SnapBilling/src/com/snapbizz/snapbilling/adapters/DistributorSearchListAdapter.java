package com.snapbizz.snapbilling.adapters;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.domains.Distributor;
public class DistributorSearchListAdapter extends ArrayAdapter<Distributor> {

	private LayoutInflater layoutInflater;
	private int layoutId = R.layout.listitem_customer_search;

	public DistributorSearchListAdapter(Context context, int textViewResourceId,
			List<Distributor> objects) {
		super(context, textViewResourceId, objects);
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public DistributorSearchListAdapter(Context context, int textViewResourceId,
			List<Distributor> objects, int layoutId) {
		super(context, textViewResourceId, objects);
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layoutId = layoutId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) 
			convertView = layoutInflater.inflate(layoutId, null);
		Distributor distributor=getItem(position);
		((TextView) convertView.findViewById(R.id.search_customername_textview)).setText(distributor.getAgencyName());
		((TextView) convertView.findViewById(R.id.search_customernumber_textview)).setText(distributor.getPhoneNumber());
		((TextView) convertView.findViewById(R.id.search_customeraddress_textview)).setText(distributor.getTinNumber());
		return convertView;
	}
}
