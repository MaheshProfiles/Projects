package com.snapbizz.snapstock.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.utils.SnapBillingTextFormatter;
import com.snapbizz.snaptoolkit.domains.Distributor;

public class DistributorPaymentListAdapter extends ArrayAdapter<Distributor> {
	private LayoutInflater layoutInflater;
	private int lastSelectedItemPosition = -1;

	public DistributorPaymentListAdapter(Context context,
			int textViewResourceId, List<Distributor> objects) {
		super(context, textViewResourceId, objects);
		this.layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		lastSelectedItemPosition = 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DistributorWrapper distributorWrapper;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.listitem_distributor_payment,
					parent, false);
			distributorWrapper = new DistributorWrapper();
			distributorWrapper.customerNameTextView = (TextView) convertView.findViewById(R.id.customer_name_textview);
			distributorWrapper.customerNumberTextView = (TextView) convertView.findViewById(R.id.customer_number_textview);
			distributorWrapper.searchcustomerAddressTextView = (TextView) convertView.findViewById(R.id.search_customeraddress_textview);
			distributorWrapper.customerAmountDueTextView = (TextView) convertView.findViewById(R.id.customer_credit_textview);
			convertView.setTag(distributorWrapper);
		} else {
			distributorWrapper = (DistributorWrapper) convertView.getTag();
		}
		Distributor distributor = getItem(position);
		if (lastSelectedItemPosition == position) {
			convertView.setBackgroundResource(R.color.distributor_selected_color);
		} else {
			convertView.setBackgroundResource(android.R.color.white);
		}
		distributorWrapper.customerNameTextView.setText(distributor.getAgencyName());
		distributorWrapper.customerNumberTextView.setText(distributor.getPhoneNumber());
		distributorWrapper.searchcustomerAddressTextView.setText(distributor.getAddress());
		distributorWrapper.customerAmountDueTextView.setText(SnapBillingTextFormatter.formatPriceText(distributor.getCreditLimit(), getContext()));
		return convertView;
	}

	public int getLastSelectedItemPosition() {
		return lastSelectedItemPosition;
	}

	public void setLastSelectedItemPosition(int position) {
		this.lastSelectedItemPosition = position;
	}

	public Distributor getLastSelectedDistributor() {
		if (lastSelectedItemPosition == -1)
			return null;
		else
			return getItem(lastSelectedItemPosition);
	}

	private static class DistributorWrapper {
		public TextView customerNameTextView;
		public TextView customerNumberTextView;
		public TextView customerAmountDueTextView;
		public TextView searchcustomerAddressTextView;

	}
}
