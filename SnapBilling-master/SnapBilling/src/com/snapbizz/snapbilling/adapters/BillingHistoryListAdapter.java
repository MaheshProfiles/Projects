package com.snapbizz.snapbilling.adapters;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.domains.Transaction;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;

public class BillingHistoryListAdapter extends ArrayAdapter<Transaction> {

	private Context context;
	private LayoutInflater layoutInflater;
	private int lastSelectedPosition = 0;

	public BillingHistoryListAdapter(Context context, int textViewResourceId,
			List<Transaction> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override																																																												
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) 
			convertView = layoutInflater.inflate(R.layout.listitem_billing_history, null);
			
		Transaction transaction = getItem(position);
		if (transaction.isSelected())
			convertView.setBackgroundResource(R.color.billitem_selected_color);
		else 
			convertView.setBackgroundResource(android.R.color.white);
		
		String [] dateSplitString = transaction.getTransactionTimeStamp().substring(0, transaction.getTransactionTimeStamp().indexOf(" "))																															.split("/");
		((TextView) convertView.findViewById(R.id.date_textview)).setText(dateSplitString[2] + "/" + dateSplitString[1] + "/" + dateSplitString[0].substring(2, 4));
		if (null != transaction.getCustomer()) {
			if (transaction.getCustomer().getCustomerName() == null || transaction.getCustomer().getCustomerName().trim().length() == 0) {
				((TextView) convertView.findViewById(R.id.customer_textview)).setText(transaction.getCustomer().getCustomerPhoneNumber());
			} else {
				((TextView) convertView.findViewById(R.id.customer_textview)).setText(transaction.getCustomer().getCustomerName() + "\n" + transaction.getCustomer()
																													.getCustomerPhoneNumber());
			}
		} else {
			((TextView) convertView.findViewById(R.id.customer_textview)).setText("");
		}
		((TextView) convertView.findViewById(R.id.total_textview)).setText(SnapToolkitTextFormatter.formatPriceText(transaction.getTotalAmount() - 
													transaction.getTotalDiscount() - transaction.getTotalSavings(), context));
		
		 float amount = 0;
			if (transaction.getTransactionType() == null) {
				double pendingPayment = Double.parseDouble(String.valueOf(transaction.getPendingPayment()));
				double totalDiscount = Double.parseDouble(String.valueOf(transaction.getTotalDiscount()));
				double totalSavings = Double.parseDouble(String.valueOf(transaction.getTotalSavings()));
				amount = (float) (transaction.getTotalAmount() - totalDiscount - totalSavings - pendingPayment);
			} else {
				amount = (float) (transaction.getTotalAmount() - transaction.getTotalDiscount() - transaction.getTotalSavings() - transaction.getPendingPayment());
			}
			if ((Math.round(transaction.getPendingPayment()) > 0) && amount > 0) 
				((TextView) convertView.findViewById(R.id.status_textview)).setText(context.getResources().getString(R.string.credit_cash_bill));
			else if (Math.round(transaction.getPendingPayment()) > 0) 
				((TextView) convertView.findViewById(R.id.status_textview)).setText(context.getResources().getString(R.string.credit));
			else 
				((TextView) convertView.findViewById(R.id.status_textview)).setText(transaction.isPaid() ? context.getResources().getString(R.string.paid) : context.getResources().getString(R.string.delivery));
		return convertView;
	}

	public void setLastSelectedPosition(int position) {
		this.lastSelectedPosition = position;
	}

	public int getLastSelectedPosition() {
		return lastSelectedPosition;
	}

	public Transaction getLastSelectedTransaction() {
		return getItem(lastSelectedPosition);
	}
}
