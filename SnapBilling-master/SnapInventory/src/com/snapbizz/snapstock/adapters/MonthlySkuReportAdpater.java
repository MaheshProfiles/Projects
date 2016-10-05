package com.snapbizz.snapstock.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class MonthlySkuReportAdpater extends ArrayAdapter<BillItem> {

	private LayoutInflater inflater;
	private Context context;
	private int layoutId = R.layout.monthly_sku_report_inflate;
	public int lastSelectedPos = -1;
	private List<BillItem> mbBillList = null;

	public MonthlySkuReportAdpater(Context context, List<BillItem> billList) {
		super(context, android.R.id.text1, billList);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		mbBillList = billList;
	}

	@Override
	public int getCount() {
		return mbBillList.size();
	}

	@Override
	public BillItem getItem(int position) {
		return mbBillList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BillAdapterWrapper billAdapterWrapper;
		if (convertView == null) {
			billAdapterWrapper = new BillAdapterWrapper();
			convertView = inflater.inflate(layoutId, null);
			billAdapterWrapper.date_value_txtvw = (TextView) convertView
					.findViewById(R.id.date_value_txtvw);
			billAdapterWrapper.qty_sold = (TextView) convertView
					.findViewById(R.id.qty_sold);
			billAdapterWrapper.amt_txtvw = (TextView) convertView
					.findViewById(R.id.amt_txtvw);
			billAdapterWrapper.closing_stock_txt = (TextView) convertView
					.findViewById(R.id.closing_stock_txt);

			convertView.setTag(billAdapterWrapper);
		} else {
			billAdapterWrapper = (BillAdapterWrapper) convertView.getTag();
		}
		if (getItem(position).getLastModifiedTimestamp().toString() != null) {

			billAdapterWrapper.date_value_txtvw.setText(new SimpleDateFormat(
					"yyyy-MM-dd").format(getItem(position)
					.getLastModifiedTimestamp()));

		}
		if (String.valueOf(getItem(position).getProductSkuQuantity()) != null) {
			billAdapterWrapper.qty_sold.setText(String
					.valueOf(getItem(position).getProductSkuQuantity()));

		}
		if (String.valueOf(getItem(position).getProductSkuMrp()) != null) {
			billAdapterWrapper.amt_txtvw.setText(String.valueOf(getItem(
					position).getProductSkuMrp()));

		}
		if (String.valueOf(getItem(position).getCurrentStock()) != null) {
			billAdapterWrapper.closing_stock_txt.setText(String.valueOf(getItem(
					position).getCurrentStock()));

		}
		return convertView;
	}

	private static class BillAdapterWrapper {
		public TextView date_value_txtvw;
		public TextView qty_sold;
		public TextView amt_txtvw;
		public TextView closing_stock_txt;
	}

}
