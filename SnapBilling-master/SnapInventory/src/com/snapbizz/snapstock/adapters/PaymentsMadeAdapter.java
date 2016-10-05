package com.snapbizz.snapstock.adapters;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.utils.SnapInventoryConstants;
import com.snapbizz.snaptoolkit.domains.Payments;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;

public class PaymentsMadeAdapter extends ArrayAdapter<Payments>{
	
	private Context context;
	private LayoutInflater layoutInflater;
	private PaymentEditListener paymentEditListener;
	private SimpleDateFormat dateFormat;
	
	public PaymentsMadeAdapter(Context context, List<Payments> paymentsList, PaymentEditListener paymentEditListener){
		super(context, android.R.id.text1, paymentsList);
		this.context = context;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.paymentEditListener = paymentEditListener;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
			Payments payments = getItem(position);
			PaymentWrapper paymentWrapper;
		
			if(null == convertView){
				
				convertView = layoutInflater.inflate(R.layout.payment_history_layout, null);
				paymentWrapper = new PaymentWrapper();
				
				paymentWrapper.paymentDateMode = (TextView) convertView.findViewById(R.id.payment_date_mode_textView);
				paymentWrapper.paymentAmount = (TextView) convertView.findViewById(R.id.payment_amount_textView);
				paymentWrapper.paymentEdit = (ImageButton) convertView.findViewById(R.id.payment_edit_image_view);
				
				convertView.setTag(paymentWrapper);
			}
			
			paymentWrapper = (PaymentWrapper) convertView.getTag();
			dateFormat = new SimpleDateFormat(SnapInventoryConstants.DATE_FORMAT_DD_MM_YYYY, Locale.US);
			if(payments.getPaymentChequeNo() != null && !payments.getPaymentChequeNo().isEmpty()){
				paymentWrapper.paymentDateMode.setText(dateFormat.format(payments.getPaymentDate()) + "\n" + payments.getPaymentType() + " - " + payments.getPaymentChequeNo());
			}else{
				paymentWrapper.paymentDateMode.setText(dateFormat.format(payments.getPaymentDate()) + "\n" + payments.getPaymentType());
			}
			paymentWrapper.paymentAmount.setText(SnapToolkitTextFormatter.formatPriceText(payments.getPaymentAmount(), context));
			paymentWrapper.paymentEdit.setOnClickListener(onEditClicked);
			paymentWrapper.paymentEdit.setTag(position);
		
			return convertView;
	}
	
	View.OnClickListener onEditClicked = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if( R.id.payment_edit_image_view == v.getId() ){				
				paymentEditListener.onPaymentEditClicked((Integer) v.getTag());				
			}			
		}
	};
	
	public static class PaymentWrapper{
		public TextView paymentDateMode;
		public TextView paymentAmount;
		public ImageButton paymentEdit;
	}
	
	public interface PaymentEditListener{
		public void onPaymentEditClicked(int position);
	}
	
}
