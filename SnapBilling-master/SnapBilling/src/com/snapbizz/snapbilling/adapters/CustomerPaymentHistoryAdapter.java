package com.snapbizz.snapbilling.adapters;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.domainsV2.CustomerInvoiceTranscationDetails;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;

public class CustomerPaymentHistoryAdapter extends ArrayAdapter<CustomerInvoiceTranscationDetails> {
    
    private LayoutInflater layoutInflater;
    private OnCustomerPaymentEditListener onCustomerPaymentEditListener;

    public CustomerPaymentHistoryAdapter(Context context, 
            List<CustomerInvoiceTranscationDetails> creditOpeningBalance, OnCustomerPaymentEditListener onCustomerPaymentEditListener, int openingBalance) {
        super(context, android.R.id.text1, creditOpeningBalance);
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onCustomerPaymentEditListener = onCustomerPaymentEditListener;
    } 

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) 
            convertView = layoutInflater.inflate(R.layout.listitem_history_credit, null);
        CustomerInvoiceTranscationDetails customerPayment = getItem(position);
        ((TextView) convertView.findViewById(R.id.payment_date_textview)).setText(new SimpleDateFormat(SnapBillingConstants.BILL_DATEFORMAT).format(customerPayment.invoiceOrTranscationDate));
        ((TextView) convertView.findViewById(R.id.payment_bill_no_textview)).setText("--");
        if (customerPayment.invoiceNo != 0)
        ((TextView) convertView.findViewById(R.id.payment_bill_no_textview)).setText(String.valueOf(customerPayment.invoiceNo));
       	((TextView) convertView.findViewById(R.id.payment_bill_amount_textview)).setText(SnapBillingTextFormatter
        			.formatPriceText(customerPayment.billAmount, getContext(), false));
        ((TextView) convertView.findViewById(R.id.payment_credit_given_textview)).setText(SnapBillingTextFormatter
        		.formatPriceText(customerPayment.credit, getContext(), false));
        ((TextView) convertView.findViewById(R.id.payment_payment_mode_textview)).setText(customerPayment.paymentMode);
        ((TextView) convertView.findViewById(R.id.payment_textview)).setText(SnapBillingTextFormatter
        		.formatPriceText(customerPayment.payment, getContext(), false));
        ((TextView) convertView.findViewById(R.id.payment_credit_due_textview)).setText(SnapBillingTextFormatter
        		.formatPriceText(customerPayment.totalCredit, getContext()));
        return convertView;
    }
    
    View.OnClickListener onCustomerPaymentEditClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onCustomerPaymentEditListener.onCustomerPaymentEdit(getItem(Integer.parseInt(v.getTag().toString())));
        }
    };

    public interface OnCustomerPaymentEditListener {
        public void onCustomerPaymentEdit(CustomerInvoiceTranscationDetails customerPayment);
    }

}
