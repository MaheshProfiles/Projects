package com.sysfore.azurepricedetails;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sysfore.azurepricedetails.modal.AzureUtil;
import com.sysfore.azurepricedetails.model.ResultPojo;

public class PriceAdapter extends BaseAdapter {

	Context context;
	List<ResultPojo> pricedetail;
	

	public PriceAdapter(Context context, List<ResultPojo> pricedetail) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.pricedetail = pricedetail;

	}

	/**
	 * @param Count
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return pricedetail.size();
	}

	/**
	 * @param Item
	 */
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return pricedetail.get(arg0);
	}

	/**
	 * @param ItemId
	 */
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return pricedetail.indexOf(getItem(arg0));
	}

	/**
	 * Inserting values to the list via layout inflater
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.price_detail_list_row,
					null);
		}

		TextView product = (TextView) convertView.findViewById(R.id.product);
		TextView tariff = (TextView) convertView.findViewById(R.id.tariff);
		TextView unit = (TextView) convertView.findViewById(R.id.unit);
		TextView consumed = (TextView) convertView.findViewById(R.id.consumed);
		TextView total = (TextView) convertView.findViewById(R.id.total);

		ResultPojo row_pos = pricedetail.get(position);

		product.setText(AzureUtil.decrypt((row_pos.getProduct())));
		tariff.setText(row_pos.getPerUnitCommitted());
		unit.setText(row_pos.getUnitofMeasure());
		consumed.setText(row_pos.getUnitConsumed());
		total.setText(row_pos.getBillTotal());

		return convertView;
	}
}
