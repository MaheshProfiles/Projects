package com.snapbizz.snapstock.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.utils.SnapBillingTextFormatter;
import com.snapbizz.snapstock.utils.SnapInventoryConstants;
import com.snapbizz.snaptoolkit.domains.CustomerCreditPayment;

public class DistributorPaymentHistory extends CursorAdapter {

	
	private LayoutInflater layoutInflater;
	private OnDistributorPaymentEditListener onCustomerPaymentEditListener;
	private float discount;
	private int lastSelectedPos = -1;
	private float savings;
	private float openingBalance;
	private float pendingpayment;
	private SimpleDateFormat dateFormat;
	private String paymentdate;
	float creditDue=openingBalance;

	public DistributorPaymentHistory(Context context, Cursor c,
			boolean autoRequery,
			OnDistributorPaymentEditListener onCustomerPaymentEditListener,float openingBalance) {
		super(context, c, autoRequery);
		// TODO Auto-generated constructor stub
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.onCustomerPaymentEditListener = onCustomerPaymentEditListener;
		this.openingBalance=openingBalance;
		 dateFormat = new SimpleDateFormat(SnapInventoryConstants.DATE_FORMAT_DD_MM_YYYY);
	}
	public DistributorPaymentHistory(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		if (cursor != null) {
			CustomerPaymentWrapper customerPaymentWrapper = (CustomerPaymentWrapper) view
					.getTag();
//			CustomerCreditPayment customerCredit = getSelectedPayment(cursor);
			CustomerCreditPayment customerCredit = new CustomerCreditPayment();
			
//			if(creditDue<0){
//				creditDue=creditDue+(cursor.getFloat(3)-cursor.getFloat(7));
//			}
			creditDue=creditDue+(cursor.getFloat(7)-cursor.getFloat(3));
			customerCredit.setCreditDue(creditDue);
			int position = cursor.getPosition();
			customerPaymentWrapper.customerPaymentDateTextView.setTag(position);
			customerPaymentWrapper.customerBillNoTextView.setTag(position);	
			customerPaymentWrapper.customerBillAmtTextView.setTag(position);
			customerPaymentWrapper.customerCreditGivenTextView.setTag(position);
			customerPaymentWrapper.customerCashPaidTextView.setTag(position);
			customerPaymentWrapper.customerCreditDueTextView.setTag(position);
			if (!cursor.isClosed()) {
				if (cursor.getInt(1) == 0) {
					customerPaymentWrapper.customerBillNoTextView.setText("--");
				}else{
				customerPaymentWrapper.customerBillNoTextView.setText(String.valueOf(cursor.getInt(1)));
				}
				
				if (cursor.getFloat(2) == 0) {
					customerPaymentWrapper.customerBillAmtTextView.setText("--");
				}else{
				customerPaymentWrapper.customerBillAmtTextView.setText(SnapBillingTextFormatter.formatPriceText(cursor.getFloat(2),context));
				}
				if (cursor.getFloat(3) == 0) {
					customerPaymentWrapper.customerCashPaidTextView.setText(SnapBillingTextFormatter.formatPriceText(cursor.getFloat(2)-cursor.getFloat(4)-cursor.getFloat(5)-cursor.getFloat(7),context));
				
				}else{
				customerPaymentWrapper.customerCashPaidTextView.setText(SnapBillingTextFormatter.formatPriceText(cursor.getFloat(3),context));

				}
				if (cursor.getFloat(7) == 0) {
					customerPaymentWrapper.customerCreditGivenTextView.setText("--");
				}else{
				customerPaymentWrapper.customerCreditGivenTextView.setText(SnapBillingTextFormatter.formatPriceText(cursor.getFloat(7),context));
				}
				if (cursor.getString(6) != null) {
					String payment = (cursor.getString(6));
					Date creditdate;
					try {
						creditdate = dateFormat.parse(payment);
						paymentdate=dateFormat.format(creditdate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				paymentdate = cursor.getString(6);
				customerPaymentWrapper.customerPaymentDateTextView.setText(paymentdate);
				if(customerCredit.getCreditDue()!=0){
				customerPaymentWrapper.customerCreditDueTextView.setText(SnapBillingTextFormatter.formatPriceText(customerCredit.getCreditDue(),context));
				}
				
			}
		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return super.getView(position, convertView, parent);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup view) {
		// TODO Auto-generated method stub
		View convertView = layoutInflater.inflate(
				R.layout.listitem_history_credit, null);
		CustomerPaymentWrapper customerPaymentWrapper;
		customerPaymentWrapper = new CustomerPaymentWrapper();
		customerPaymentWrapper.customerPaymentDateTextView = (TextView) convertView
				.findViewById(R.id.payment_date_textview);
		customerPaymentWrapper.customerBillNoTextView = (TextView) convertView
				.findViewById(R.id.payment_bill_no_textview);
		customerPaymentWrapper.customerBillAmtTextView = (TextView) convertView
				.findViewById(R.id.payment_bill_amount_textview);
		customerPaymentWrapper.customerCreditGivenTextView = (TextView) convertView
				.findViewById(R.id.payment_credit_given_textview);
		customerPaymentWrapper.customerCashPaidTextView = (TextView) convertView
				.findViewById(R.id.payment_cash_paid_textview);
		customerPaymentWrapper.customerCreditDueTextView = (TextView) convertView
				.findViewById(R.id.payment_credit_due_textview);
		convertView.setTag(customerPaymentWrapper);
		return convertView;
	}

	public CustomerCreditPayment getLastSelectedItem() {
		if (lastSelectedPos == -1)
			return null;
		else if (lastSelectedPos >= getCount())
			return null;
		else {
			Cursor currentCursor = (Cursor) getItem(lastSelectedPos);
			return getSelectedPayment(currentCursor);
		}
	}
	
	private CustomerCreditPayment getSelectedPayment(Cursor currentCursor) {
		// TODO Auto-generated method stub
		CustomerCreditPayment cuspayobj=new CustomerCreditPayment();
//		creditDue=creditDue+currentCursor.getFloat(3)-currentCursor.getFloat(7);
//		cuspayobj.setCreditDue(creditDue);
//		
		return cuspayobj;
	}

	public int getLastSelectedPos() {
		return lastSelectedPos;
	}

	public void setLastSelectedPos(int lastSelectedPos) {
		this.lastSelectedPos = lastSelectedPos;
	}
	public interface OnDistributorPaymentEditListener {
		public void onDistributorPaymentEdit(CustomerCreditPayment customerPayment);
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		return super.runQueryOnBackgroundThread(constraint);

	}
	private static class CustomerPaymentWrapper {
		public TextView customerPaymentDateTextView;
		public TextView customerBillNoTextView;
		public TextView customerBillAmtTextView;
		public TextView customerCreditGivenTextView;
		public TextView customerCashPaidTextView;
		public TextView customerCreditDueTextView;

	}

}
