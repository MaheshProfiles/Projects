package com.snapbizz.snapstock.fragments;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.adapters.PaymentsMadeAdapter;
import com.snapbizz.snapstock.adapters.PurchaseOrderHistoryAdapter;
import com.snapbizz.snapstock.adapters.PaymentsMadeAdapter.PaymentEditListener;
import com.snapbizz.snapstock.adapters.PurchaseOrderHistoryAdapter.PurchaseOrderEditListener;
import com.snapbizz.snapstock.asynctasks.AddPaymentTask;
import com.snapbizz.snapstock.asynctasks.DeletePaymentMadeTask;
import com.snapbizz.snapstock.asynctasks.GetDistributorPOHistoryTask;
import com.snapbizz.snapstock.asynctasks.GetPaymentHistoryTask;
import com.snapbizz.snapstock.asynctasks.GetStockSummaryTask;
import com.snapbizz.snapstock.domains.ProductBean;
import com.snapbizz.snapstock.utils.SnapInventoryConstants;
import com.snapbizz.snaptoolkit.customviews.SoftInputEditText;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.Order;
import com.snapbizz.snaptoolkit.domains.Payments;
import com.snapbizz.snaptoolkit.fragments.DatePickerFragment;
import com.snapbizz.snaptoolkit.fragments.DatePickerFragment.OnDateSelectedListener;
import com.snapbizz.snaptoolkit.fragments.NumKeypadFragment;
import com.snapbizz.snaptoolkit.fragments.NumKeypadFragment.NumberKeypadEnterListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;

public class SummaryFragment extends Fragment implements OnQueryCompleteListener, NumberKeypadEnterListener, PaymentEditListener, OnDateSelectedListener, PurchaseOrderEditListener {

	private OrderSummaryButtonListener orderSummaryButtonListener;
	
	private PurchaseOrderHistoryAdapter purchaseOrderHistoryAdapter;
	private PaymentsMadeAdapter paymentsMadeAdapter;
	
	private GetStockSummaryTask getStockSummaryTask;
	private GetDistributorPOHistoryTask getDistributorPOHistoryTask;
	private GetPaymentHistoryTask getPaymentHistoryTask;
	private DeletePaymentMadeTask deletePaymentMadeTask;
	private AddPaymentTask addPaymentTask;
	
	private Distributor distributor;
	private Order order;
	private Payments payment;
	private NumKeypadFragment keypadFragment;
	
	private List<ProductBean> productList;
	private List<Payments> paymentList;
	private List<Float> stockSummaryNumbers;
	private List<Order> orderHistoryArray;
	
	private ListView orderHistoryListView;
	private ListView paymentMadeListView;
	
	private final int DISTRIBUTOR_PO_HISTORY_TASK = 1;
	private final int STOCK_SUMMARY_TASK = 2;
	private final int DELETE_PAYMENTS_MADE_TASKCODE = 3;
	private final int PAYMENT_HISTORY_TASKCODE = 5;
	private final int ADD_PAYMENT_TASKCODE = 6;
	private final int PAYMENT_AMOUNT_TASKCODE = 7;
	private final int KEYPAD_FRAGMENT_DISCOUNT_TASK_CODE = 8;
	private final int PAYMENT_CHEQUE_NO_TASKCODE = 9;
	private final int EDIT_TOTAL_AMOUNT = 10;
	private final int CAPTURE_IMAGE_TASK_CODE = 13;
	
	private Bitmap invoiceImageBitmap;
	private SimpleDateFormat dateFormat;
	private int totalQuantity = 0;
	private float totalOrderAmount = 0;
	private float totalOrderAmountWithoutPO = 0;
	private float totalDiscountGiven = 0;
	private float modifiedOrderAmount = 0;
	private float paymentAmount = 0;
	private float totalPaymentDue = 0;
	private float totalPaymentMade = 0;
	private float totalAmountToPay = 0;
	private boolean isToOrder;
	private String amountDueString = "Total Amount Due: ";
	private String chequeNo;
	private String currentDate;
	private String paymentModeType;
	private Date paymentDate;
	private Button summaryAddBtn;
	private Button addPaymentBtn;
	private Button deletePaymentBtn;
	private Button confirmBtn;
	private Button cancelBtn;
	private Button deleteInvoiceImageBtn;
	private Button recaptureInvoiceImageBtn;
	private Button invoiceImageDoneBtn;
	private SoftInputEditText invoiceEditText;
	private TextView dTotalValueTextView;
    private TextView sTotalValueTextView;
    private TextView dTotalQty;
    private TextView sTotalQty;
    private TextView dDailySales;
    private TextView sDailySales;
    private TextView dDaysStock;
    private TextView sDaysStock;
	private TextView poHistoryTotalAmountTextView;
	private TextView paymentTotalAmountTextView;
	private TextView discountTextView;
	private TextView totalOrderAmountTextView;
	private TextView addPaymentAmountTextView;
	private TextView addPaymentChequeNoTextView;
	private TextView addPaymentDateTextView;
	private TextView totalPaymentDueTextView;
	private ImageView invoiceImageViewThumbnail;
	private ImageView invoiceImageView;
	private RelativeLayout transparentOverlay;
	private RelativeLayout paymentLayout;
	private RelativeLayout invoiceImageLayout;
	private Spinner paymentModeSpinner;

	public void setSummaryData(Distributor distributor,
			List<ProductBean> productList, boolean isToOrder, String currentDate,
			OrderSummaryButtonListener orderSummaryButtonListener) {
		this.distributor = distributor;
		this.productList = productList;
		this.isToOrder = isToOrder;
		this.currentDate = currentDate;
		this.orderSummaryButtonListener = orderSummaryButtonListener;
	}
	
	public void setSummaryData(Distributor distributor,
			List<ProductBean> productList, boolean isToOrder, Order order,
			OrderSummaryButtonListener orderSummaryButtonListener) {
		this.distributor = distributor;
		this.productList = productList;
		this.isToOrder = isToOrder;
		this.order = order;
		this.orderSummaryButtonListener = orderSummaryButtonListener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(
				R.layout.inventory_distributor_information_layout, null);
		
		orderHistoryListView = (ListView) view.findViewById(R.id.po_history_listView);
		orderHistoryListView.addFooterView(inflater.inflate(R.layout.summary_total_layout, null));
		poHistoryTotalAmountTextView = (TextView) orderHistoryListView.findViewById(R.id.summary_total_amount_textView);
		paymentMadeListView = (ListView) view.findViewById(R.id.po_payment_listView);
		paymentMadeListView.addFooterView(inflater.inflate(R.layout.summary_total_layout, null));
		paymentTotalAmountTextView = (TextView) paymentMadeListView.findViewById(R.id.summary_total_amount_textView);
		
		 dTotalValueTextView = (TextView) view.findViewById(R.id.distributor_total_value_textView);
	     sTotalValueTextView = (TextView) view.findViewById(R.id.store_total_value_textView);
	     dTotalQty = (TextView) view.findViewById(R.id.distributor_total_qty_textView);
	     sTotalQty = (TextView) view.findViewById(R.id.store_total_qty_textView);
	     dDailySales = (TextView) view.findViewById(R.id.distributor_average_daily_sales_textView);
	     sDailySales = (TextView) view.findViewById(R.id.store_average_daily_sales_textView);
	     dDaysStock = (TextView) view.findViewById(R.id.distributor_days_of_stock_textView);
	     sDaysStock = (TextView) view.findViewById(R.id.store_days_of_stock_textView);

		return view;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		confirmBtn = ((Button) getView().findViewById(R.id.btn_product_order_confirm));
				confirmBtn.setOnClickListener(onButtonClicked);

		cancelBtn = ((Button) getView().findViewById(R.id.btn_product_order_cancel));
				cancelBtn.setOnClickListener(onButtonClicked);
		
		((Button) getView().findViewById(R.id.update_payment_made_button))
		.setOnClickListener(onButtonClicked);
		
		paymentLayout = (RelativeLayout) getView().findViewById(R.id.payment_modify_layout);
		
		transparentOverlay = (RelativeLayout) getView().findViewById(R.id.summary_keypad_overlay);
		transparentOverlay.setOnClickListener(onButtonClicked);
		
		invoiceImageLayout = (RelativeLayout) getView().findViewById(R.id.summary_invoice_image_layout);
		
		if (!isToOrder) {
			invoiceImageViewThumbnail = (ImageView) getView().findViewById(R.id.order_receive_cameraView);
			invoiceImageViewThumbnail.setVisibility(View.VISIBLE);
			invoiceImageViewThumbnail.setOnClickListener(onButtonClicked);
			invoiceImageView = (ImageView) getView().findViewById(R.id.invoice_image_view);
			deleteInvoiceImageBtn = (Button) getView().findViewById(R.id.invoice_image_delete_button);
			deleteInvoiceImageBtn.setOnClickListener(onButtonClicked);
			recaptureInvoiceImageBtn = (Button) getView().findViewById(R.id.invoice_image_recapture_button);
			recaptureInvoiceImageBtn.setOnClickListener(onButtonClicked);
			invoiceImageDoneBtn = (Button) getView().findViewById(R.id.invoice_image_done_button);
			invoiceImageDoneBtn.setOnClickListener(onButtonClicked);
			invoiceEditText = (SoftInputEditText) getView().findViewById(R.id.invoice_id_textView);
			invoiceEditText.setVisibility(View.VISIBLE);
			LayoutParams param = confirmBtn.getLayoutParams();
			param.width = 155;
			confirmBtn.setLayoutParams(param);
			param = cancelBtn.getLayoutParams();
			param.width = 150;
			cancelBtn.setLayoutParams(param);
			discountTextView = ((TextView) getView().findViewById(
					R.id.product_order_disount_textView));
			discountTextView.setVisibility(View.VISIBLE);
			discountTextView.setOnClickListener(onButtonClicked);
			((RelativeLayout) getView().findViewById(R.id.stock_summary)).setBackground(getActivity().getResources().getDrawable(R.drawable.receive_group_gradient));
			if(null != productList && productList.size() > 0){
				for (ProductBean currentList : productList) {
					if(!currentList.isPaid()){
						totalOrderAmount += currentList.getProductTotalAmount();
					}
					totalOrderAmountWithoutPO += currentList.getProductPrice() * currentList.getProductBilledQty();
					totalQuantity = totalQuantity + currentList.getProductBilledQty();
				}
			}	
			modifiedOrderAmount = totalOrderAmount;
			discountTextView.setText("Discount :" + SnapToolkitTextFormatter.formatPriceText(0,getActivity()));
			if(null != order){				
			
				((TextView) getView().findViewById(R.id.distributor_PO_number_textView))
					.setText("PO No: " + (order.getOrderNumber()));
				((TextView) getView().findViewById(R.id.order_date_textView)).setText(order.getOrderDate());
				invoiceEditText.setText(order.getInvoiceID());
				try {
					if(null != order.getImage()){
						invoiceImageBitmap = new BitmapDrawable(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(order.getImage()))).getBitmap();	
					}else{
						invoiceImageBitmap = null;
					}					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					invoiceImageBitmap = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					invoiceImageBitmap = null;
					e.printStackTrace();
				}
				if(null != invoiceImageBitmap){
					invoiceImageViewThumbnail.setImageBitmap(invoiceImageBitmap);
				}
					
			}else{
				
				//((TextView) getView().findViewById(R.id.distributor_PO_number_textView))
				//.setText("PO No: " + (purchaseOrderNumber + 1));				
				
				dateFormat = new SimpleDateFormat(SnapInventoryConstants.DATE_FORMAT_DD_MM_YYYY, Locale.US);
				currentDate = dateFormat.format(Calendar.getInstance().getTime());
				((TextView) getView().findViewById(R.id.order_date_textView)).setText(currentDate);
			}			
		}else{
			((RelativeLayout) getView().findViewById(R.id.stock_summary)).setBackground(getActivity().getResources().getDrawable(R.drawable.order_group_gradient));
			if(null != productList && productList.size() > 0){
				for (ProductBean currentList : productList) {
					totalOrderAmount = totalOrderAmount
						+ (currentList.getProductPrice() * currentList
								.getProductToOrder());
					totalQuantity = totalQuantity + currentList.getProductToOrder();
				}
			}
			
		//	((TextView) getView().findViewById(R.id.distributor_PO_number_textView))
		//	.setText("PO No: " + (purchaseOrderNumber + 1));
			((TextView) getView().findViewById(R.id.order_date_textView))
			.setText(currentDate);
			LayoutParams param = confirmBtn.getLayoutParams();
			param.width = 180;
			confirmBtn.setLayoutParams(param);
			param = cancelBtn.getLayoutParams();
			param.width = 180;
			cancelBtn.setLayoutParams(param);
		}
		
		summaryAddBtn = (Button) getView().findViewById(R.id.stock_summary_add_button);
		summaryAddBtn.setOnClickListener(onButtonClicked);
		
		((TextView) getView().findViewById(
				R.id.product_order_total_quantity_textView)).setText(String
				.valueOf(totalQuantity));
		totalOrderAmountTextView = ((TextView) getView().findViewById(
				R.id.product_order_total_amount_textView));
		totalOrderAmountTextView.setText(SnapToolkitTextFormatter.formatPriceText(
				totalOrderAmount, getActivity()));
		
		totalPaymentDueTextView = (TextView) getView().findViewById(R.id.payment_due_textView);
				
		if(!isToOrder){
			totalOrderAmountTextView.setOnClickListener(onButtonClicked);
		}
		((TextView) getView().findViewById(R.id.agency_name_textView))
				.setText(distributor.getAgencyName());		
		((TextView) getView().findViewById(R.id.agency_name_stock_summary_textView))
		.setText(distributor.getAgencyName());
		((TextView) getView().findViewById(
				R.id.distributor_salesman_name_textView)).setText(distributor
				.getSalesmanName());
		((TextView) getView().findViewById(
				R.id.distributor_contact_number_textView)).setText(distributor
				.getPhoneNumber());
		((TextView) getView().findViewById(R.id.agency_name_textView))
				.setText(distributor.getAgencyName());
		
		paymentModeSpinner = (Spinner) getView().findViewById(R.id.add_payment_mode_spinner);
		paymentModeSpinner.setOnItemSelectedListener(onSpinnerItemSelected);
		
		getStockSummaryTask = new GetStockSummaryTask(getActivity(), STOCK_SUMMARY_TASK, SummaryFragment.this);
		getStockSummaryTask.execute(distributor.getDistributorId());
		
		getDistributorPOHistoryTask = new GetDistributorPOHistoryTask(getActivity(), DISTRIBUTOR_PO_HISTORY_TASK	, SummaryFragment.this);
		getDistributorPOHistoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, distributor.getDistributorId());
		
		getPaymentHistoryTask = new GetPaymentHistoryTask(getActivity(), SummaryFragment.this, PAYMENT_HISTORY_TASKCODE);
		getPaymentHistoryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, distributor.getDistributorId());
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@SuppressLint("NewApi")
	View.OnClickListener onButtonClicked = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			if ( R.id.product_order_disount_textView == v.getId() ){
								
				addKeypadFragment();
				
				keypadFragment.setValue("00");
				keypadFragment.setContext(KEYPAD_FRAGMENT_DISCOUNT_TASK_CODE);
				RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				rlParams.setMargins(400, 300, 0, 0);
				getActivity().findViewById(
						R.id.stock_summary_keypad_layout)
						.setLayoutParams(rlParams);
			
			} else if (v.getId() == R.id.btn_product_order_confirm) {
				
				if(isToOrder){
					orderSummaryButtonListener.onOrderConfirmButtonClicked(totalOrderAmount, ((TextView) getView().findViewById(
							R.id.order_date_textView)).getText().toString());
					totalOrderAmount = 0;
					totalQuantity = 0;
				}else{
					String invoiceText;
					if(null != invoiceEditText){
						invoiceText = invoiceEditText.getText().toString().trim();
					}else{
						invoiceText = "";
					}
					if(null != order){
						orderSummaryButtonListener.onReceiveConfirmButtonClicked(totalDiscountGiven, modifiedOrderAmount, invoiceText, invoiceImageBitmap);
					}else{
						orderSummaryButtonListener.onReceiveConfirmWithoutPO(totalDiscountGiven, modifiedOrderAmount, currentDate, totalOrderAmountWithoutPO, invoiceText, invoiceImageBitmap);
					}
					totalOrderAmount = 0;
					totalQuantity = 0;	
					invoiceText = "";
				}
				
			} else if (v.getId() == R.id.btn_product_order_cancel) {	
				
				totalQuantity = 0;
				totalOrderAmount = 0;
				orderSummaryButtonListener.onCancelButtonClicked();
				
			} 
			else if( R.id.delete_payment_made_button == v.getId()){
				
				SnapCommonUtils.showDeleteAlert(getActivity(), "Payment", "Delete payment made?", deletePaymentPositiveClickListener, deletePaymentNegativeClickListener, false);			
			
			}
			else if( R.id.stock_summary_add_button == v.getId() ){
				
				payment = null;
				if(null != paymentModeType && paymentModeType.equalsIgnoreCase("Cheque")){
					paymentModeSpinner.setSelection(1);
					showChequeNoField();
				}
				showAddPaymentOverlay();
								
			}else if ( R.id.summary_keypad_overlay == v.getId() ){
				if ( null != keypadFragment && keypadFragment.isAdded() ){
					FragmentManager fm = getFragmentManager();
					fm.popBackStack();
					keypadFragment = null;
					getView().findViewById(R.id.stock_summary_keypad_layout)
						.setVisibility(View.GONE);
					transparentOverlay.setVisibility(View.GONE);
				}
			}else if( R.id.payment_to_make_textView == v.getId() ){
				
				addKeypadFragment();
				
				keypadFragment.setValue("00");
				keypadFragment.setContext(PAYMENT_AMOUNT_TASKCODE);
				RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				rlParams.setMargins(300, 300, 0, 0);
				getActivity().findViewById(
						R.id.stock_summary_keypad_layout)
						.setLayoutParams(rlParams);
			
			}else if ( R.id.payment_cheque_no_textView == v.getId() ){
			
				addKeypadFragment();
				
				keypadFragment.setValue("00");
				keypadFragment.setContext(PAYMENT_CHEQUE_NO_TASKCODE);
				RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				rlParams.setMargins(600, 300, 0, 0);
				getActivity().findViewById(
						R.id.stock_summary_keypad_layout)
						.setLayoutParams(rlParams);
				
			}else if ( R.id.update_payment_made_button == v.getId() ){
				
				if( null == payment ){
					payment = new Payments();
					payment.setDistributor(distributor);
					payment.setPaymentAmount(paymentAmount);
					payment.setPaymentDate(paymentDate);
					payment.setPaymentType(paymentModeType);
					payment.setPaymentChequeNo(chequeNo);
				}else{
					payment.setPaymentAmount(paymentAmount);
					payment.setPaymentDate(paymentDate);
					payment.setPaymentType(paymentModeType);
					payment.setPaymentChequeNo(chequeNo);
				}
				
				if (null == paymentDate){
					Toast.makeText(getActivity().getApplicationContext(), "Invalid Date", Toast.LENGTH_SHORT).show();
				}else if(paymentAmount == 0){
					Toast.makeText(getActivity().getApplicationContext(), "Invalid payment amount", Toast.LENGTH_SHORT).show();
				}else if(paymentModeType.equalsIgnoreCase("Cheque") && ((null == chequeNo || chequeNo.length() != 6))){
					Toast.makeText(getActivity().getApplicationContext(), "Invalid cheque number", Toast.LENGTH_SHORT).show();
				}else{
					addPaymentTask = new AddPaymentTask(getActivity(), SummaryFragment.this, ADD_PAYMENT_TASKCODE, payment);
					addPaymentTask.execute();
				}
				
			}else if ( R.id.add_payment_date_textView == v.getId() ){
				
				DatePickerFragment datePicker = new DatePickerFragment();
				datePicker.setOnDateSelectedListener(SummaryFragment.this);
				datePicker.show(getFragmentManager(), "date picker");
			
			}else if ( R.id.product_order_total_amount_textView == v.getId() ){
				
				addKeypadFragment();
				
				keypadFragment.setValue(String.valueOf(modifiedOrderAmount));
				keypadFragment.setContext(EDIT_TOTAL_AMOUNT);
				RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				rlParams.setMargins(400, 300, 0, 0);
				getActivity().findViewById(
						R.id.stock_summary_keypad_layout)
						.setLayoutParams(rlParams);
				
			}else if(R.id.payment_modify_layout == v.getId()){
				hideAddPaymentOverlay();
			}else if(R.id.order_receive_cameraView == v.getId()){
					
					if(null != invoiceImageBitmap){
						invoiceImageLayout.setVisibility(View.VISIBLE);
						invoiceImageView.setImageBitmap(invoiceImageBitmap);
					}else{
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						startActivityForResult(intent, CAPTURE_IMAGE_TASK_CODE);
					}
					
			}else if(R.id.invoice_image_delete_button == v.getId()){
				SnapCommonUtils.showDeleteAlert(getActivity(), "Invoice Image", "Delete Invoice Image?", invoiceImagePositiveClickListener, invoiceImageNegativeClickListener, true);
			}else if(R.id.invoice_image_recapture_button == v.getId()){
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, CAPTURE_IMAGE_TASK_CODE);
			}else if(R.id.invoice_image_done_button == v.getId()){
				if(null != invoiceImageLayout){
					invoiceImageLayout.setVisibility(View.GONE);
				}
			}
		}
	};
	
	View.OnClickListener deletePaymentPositiveClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if( null != payment ){
				SnapCommonUtils.dismissAlert();
				deletePaymentMadeTask = new DeletePaymentMadeTask(getActivity(), SummaryFragment.this, DELETE_PAYMENTS_MADE_TASKCODE, payment);
				deletePaymentMadeTask.execute();
			}
			else{
				Toast.makeText(getActivity().getApplicationContext(), "unable to delete", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	View.OnClickListener deletePaymentNegativeClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SnapCommonUtils.dismissAlert();
		}
	};
	
	View.OnClickListener invoiceImagePositiveClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {SnapCommonUtils.dismissAlert();
			// TODO Auto-generated method stub
			invoiceImageBitmap = null;
			invoiceImageViewThumbnail.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_camera));
			SnapCommonUtils.dismissAlert();
			invoiceImageLayout.setVisibility(View.GONE);
		}
	};
	
	View.OnClickListener invoiceImageNegativeClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SnapCommonUtils.dismissAlert();
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		try {
			super.onActivityResult(requestCode, resultCode, data);

			if (requestCode == CAPTURE_IMAGE_TASK_CODE) {
				if (resultCode == Activity.RESULT_OK) {
					invoiceImageBitmap = (Bitmap) data.getExtras().get("data");
					invoiceImageViewThumbnail.setImageBitmap(invoiceImageBitmap);
					if(null != invoiceImageLayout){
						invoiceImageLayout.setVisibility(View.GONE);
					}
				}
			} else {
				Toast.makeText(getActivity().getApplicationContext(),
						getString(R.string.msg_capture_image), Toast.LENGTH_SHORT).show();
			}
		} catch (NullPointerException e) {
			Toast.makeText(getActivity().getApplicationContext(),
					getString(R.string.msg_image_processed), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("Summary fragment", "inside on destroy");
		totalOrderAmount = 0;
		totalQuantity = 0;
		totalDiscountGiven = 0;
		totalOrderAmountWithoutPO = 0;
		if( null != purchaseOrderHistoryAdapter ){
			purchaseOrderHistoryAdapter = null;
		}
		if( null != stockSummaryNumbers && stockSummaryNumbers.size() > 0 ){
			stockSummaryNumbers.clear();
		}
		if( null != paymentsMadeAdapter ){
			paymentsMadeAdapter = null;
		}
		if( null != transparentOverlay ){
			transparentOverlay.setVisibility(View.GONE);
		}
		if(null != getStockSummaryTask && !getStockSummaryTask.isCancelled()){
			getStockSummaryTask.cancel(true);
		}
		if(null != getDistributorPOHistoryTask && !getDistributorPOHistoryTask.isCancelled()){
			getDistributorPOHistoryTask.cancel(true);
		}
		if(null != getPaymentHistoryTask && !getPaymentHistoryTask.isCancelled()){
			getPaymentHistoryTask.cancel(true);
		}
		if(null != deletePaymentMadeTask && !deletePaymentMadeTask.isCancelled()){
			deletePaymentMadeTask.cancel(true);
		}
		if(null != addPaymentTask && !addPaymentTask.isCancelled()){
			addPaymentTask.cancel(true);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		// TODO Auto-generated method stub
		
		if( DISTRIBUTOR_PO_HISTORY_TASK == taskCode){
			
			orderHistoryArray =  (List<Order>) responseList;
			totalAmountToPay = 0;
			for (Order orderItem : orderHistoryArray) {
				totalAmountToPay += orderItem.getPaymentToMake();
			}
				totalPaymentDue = totalAmountToPay - totalPaymentMade;
				poHistoryTotalAmountTextView.setText(SnapToolkitTextFormatter.formatPriceText(totalAmountToPay, getActivity()));
				totalPaymentDueTextView.setText(amountDueString + SnapToolkitTextFormatter.formatPriceText(totalPaymentDue, getActivity()));
			if( null == purchaseOrderHistoryAdapter ){
				purchaseOrderHistoryAdapter = new PurchaseOrderHistoryAdapter(getActivity(), orderHistoryArray, SummaryFragment.this);
				orderHistoryListView.setAdapter(purchaseOrderHistoryAdapter);
			}else{
				purchaseOrderHistoryAdapter.clear();
				purchaseOrderHistoryAdapter.addAll(orderHistoryArray);
				purchaseOrderHistoryAdapter.notifyDataSetChanged();
			}
			
		} else if ( STOCK_SUMMARY_TASK == taskCode ){
			
			stockSummaryNumbers = (List<Float>) responseList;						
			setTextViewValues();
			
		} else if ( DELETE_PAYMENTS_MADE_TASKCODE == taskCode ){
		
			hideAddPaymentOverlay();
			Toast.makeText(getActivity().getApplicationContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();
			paymentDate = null;
			paymentAmount = 0;
			chequeNo = "";
			getPaymentHistoryTask = new GetPaymentHistoryTask(getActivity(), SummaryFragment.this, PAYMENT_HISTORY_TASKCODE);
			getPaymentHistoryTask.execute(distributor.getDistributorId());

		} else if ( PAYMENT_HISTORY_TASKCODE == taskCode ){
			
			paymentList = (List<Payments>) responseList;
			totalPaymentMade = 0;
			for (Payments pay : paymentList) {
				totalPaymentMade += pay.getPaymentAmount();
			}
				totalPaymentDue = totalAmountToPay - totalPaymentMade;
				paymentTotalAmountTextView.setText(SnapToolkitTextFormatter.formatPriceText(totalPaymentMade, getActivity()));
				totalPaymentDueTextView.setText(amountDueString + SnapToolkitTextFormatter.formatPriceText(totalPaymentDue, getActivity()));
	
			if( null == paymentsMadeAdapter ){
				paymentsMadeAdapter = new PaymentsMadeAdapter(getActivity(), paymentList, SummaryFragment.this);
				paymentMadeListView.setAdapter(paymentsMadeAdapter);
			}else{
				paymentsMadeAdapter.clear();
				paymentsMadeAdapter.addAll(paymentList);
				paymentsMadeAdapter.notifyDataSetChanged();
			}
			
		} else if ( ADD_PAYMENT_TASKCODE == taskCode ){
			
			hideAddPaymentOverlay();
			Toast.makeText(getActivity().getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
			paymentDate = null;
			paymentAmount = 0;
			chequeNo = "";
			getPaymentHistoryTask = new GetPaymentHistoryTask(getActivity(), SummaryFragment.this, PAYMENT_HISTORY_TASKCODE);
			getPaymentHistoryTask.execute(distributor.getDistributorId());
						
		}
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		// TODO Auto-generated method stub
		
		if( PAYMENT_HISTORY_TASKCODE == taskCode ){
			if(null != paymentsMadeAdapter){
				paymentsMadeAdapter.clear();
				paymentsMadeAdapter.notifyDataSetChanged();
			}
			totalPaymentMade = 0;
			paymentTotalAmountTextView.setText(SnapToolkitTextFormatter.formatPriceText(totalPaymentMade, getActivity()));
			totalPaymentDue = totalAmountToPay - totalPaymentMade;
			totalPaymentDueTextView.setText(amountDueString + SnapToolkitTextFormatter.formatPriceText(totalPaymentDue, getActivity()));
		}
		
		Toast.makeText(getActivity().getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
	}
	
	AdapterView.OnItemSelectedListener onSpinnerItemSelected = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			// TODO Auto-generated method stub
			paymentModeType = (String) parent.getItemAtPosition(pos);
			if( paymentModeType.equalsIgnoreCase("Cheque")){
				showChequeNoField();
			}else{
				chequeNo = null;
				hideChequeNoField();	
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	};

	@Override
	public void onNumKeyPadEnter(String value, int context) {
		// TODO Auto-generated method stub
		
		if( KEYPAD_FRAGMENT_DISCOUNT_TASK_CODE == context ){
			
			modifiedOrderAmount = totalOrderAmount - Float.valueOf(value);
			totalDiscountGiven = Float.valueOf(value);
			((TextView) getView().findViewById(
					R.id.product_order_total_amount_textView))
					.setText(SnapToolkitTextFormatter.formatPriceText(
							modifiedOrderAmount, getActivity()));
			discountTextView.setText("Discount :"
					+ SnapToolkitTextFormatter.formatPriceText(Double.parseDouble(value),
							getActivity()));
			
		}else if( PAYMENT_AMOUNT_TASKCODE == context ){
		
			if( Integer.parseInt(value) > 0 ){
				addPaymentAmountTextView.setText(SnapToolkitTextFormatter.formatPriceText(Double.parseDouble(value), getActivity()));
				paymentAmount = Float.parseFloat(value);
			}else
				Toast.makeText(getActivity().getApplicationContext(), "Enter valid amount", Toast.LENGTH_SHORT).show();
		
		}else if ( PAYMENT_CHEQUE_NO_TASKCODE == context ) {
			chequeNo = value+"";
			chequeNo = chequeNo.substring(0, chequeNo.lastIndexOf("."));
			if(chequeNo.length() == 6)
				addPaymentChequeNoTextView.setText(String.valueOf(chequeNo));
			else
				Toast.makeText(getActivity().getApplicationContext(), "Enter valid cheque number", Toast.LENGTH_SHORT).show();
			
		}else if ( EDIT_TOTAL_AMOUNT == context ){
			
			totalOrderAmount = Float.parseFloat(value) - totalDiscountGiven;
			modifiedOrderAmount = Float.parseFloat(value) - totalDiscountGiven;
			totalOrderAmountTextView.setText(SnapToolkitTextFormatter.formatPriceText(modifiedOrderAmount, getActivity()));
			
		}
		
		popKeypadFragment();
		
	}
	
	public boolean isMakePaymentVisible(){
			if( View.VISIBLE == paymentLayout.getVisibility() ){
				return true;
			}
		return false;
	}
	
	public boolean isNumKeypadVisible(){
		if( null != keypadFragment && keypadFragment.isAdded() ){
			return true;
		}else{
			return false;
		}
	}
	
	private void addKeypadFragment(){
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (keypadFragment == null) {
			keypadFragment = new NumKeypadFragment();
			keypadFragment
					.setKeypadEnterListener(SummaryFragment.this);
			ft.add(R.id.stock_summary_keypad_layout, keypadFragment,
					"keypad_receive_fragment");
		} else
			ft.replace(R.id.stock_summary_keypad_layout,
					keypadFragment);

		if (!keypadFragment.isAdded()) {
			ft.addToBackStack(keypadFragment.getTag());
		}
		ft.commit();

		getView().findViewById(R.id.stock_summary_keypad_layout)
				.setVisibility(View.VISIBLE);
		transparentOverlay.setVisibility(View.VISIBLE);
		
	}
	
	private void showAddPaymentOverlay(){
		paymentLayout.setVisibility(View.VISIBLE);
		paymentLayout.setOnClickListener(onButtonClicked);
		paymentModeSpinner.setOnItemSelectedListener(onSpinnerItemSelected);
		addPaymentAmountTextView = (TextView) getView().findViewById(R.id.payment_to_make_textView);
		addPaymentAmountTextView.setText("");
		addPaymentAmountTextView.setOnClickListener(onButtonClicked);
		addPaymentDateTextView = (TextView) getView().findViewById(R.id.add_payment_date_textView);
		addPaymentDateTextView.setOnClickListener(onButtonClicked);
		addPaymentDateTextView.setText("");
		addPaymentBtn = (Button) getView().findViewById(R.id.update_payment_made_button);
		addPaymentBtn.setOnClickListener(onButtonClicked);
		deletePaymentBtn = (Button) getView().findViewById(R.id.delete_payment_made_button);
		deletePaymentBtn.setOnClickListener(onButtonClicked);
	}

	private void showChequeNoField(){
		addPaymentChequeNoTextView = (TextView) getView().findViewById(R.id.payment_cheque_no_textView);
		addPaymentChequeNoTextView.setVisibility(View.VISIBLE);
		addPaymentChequeNoTextView.setOnClickListener(onButtonClicked);
	}
	
	private void hideChequeNoField(){
		if(null != addPaymentChequeNoTextView){
			addPaymentChequeNoTextView.setVisibility(View.GONE);
			addPaymentChequeNoTextView.setText("");
			chequeNo = "";
		}
	}
	
	public void hideAddPaymentOverlay(){
		paymentLayout.setVisibility(View.GONE);
		if( null != addPaymentChequeNoTextView ){
			addPaymentChequeNoTextView.setText("");
			chequeNo = null;
			hideChequeNoField();
		}	
	}
	
	public void popKeypadFragment(){
		FragmentManager fm = getFragmentManager();
		fm.popBackStack();
		keypadFragment = null;
		getView().findViewById(R.id.stock_summary_keypad_layout)
		.setVisibility(View.GONE);
		if( null != transparentOverlay ){
			transparentOverlay.setVisibility(View.GONE);
		}	
	}
	
	@Override
	public void onPaymentEditClicked(int position) {
		// TODO Auto-generated method stub
		
		payment = paymentsMadeAdapter.getItem(position);
		paymentDate = payment.getPaymentDate();
		paymentAmount = payment.getPaymentAmount();
		chequeNo = payment.getPaymentChequeNo();
		paymentModeType = payment.getPaymentType();
		if ( payment.getPaymentType().equalsIgnoreCase("Cash") ){
			hideChequeNoField();
			paymentModeSpinner.setSelection(0);
		}else if ( payment.getPaymentType().equalsIgnoreCase("Credit Note")){
			hideChequeNoField();
			paymentModeSpinner.setSelection(2);
		}else if( payment.getPaymentChequeNo() != null && !payment.getPaymentChequeNo().isEmpty()) {
			paymentModeSpinner.setSelection(1);
			showChequeNoField();
			addPaymentChequeNoTextView.setText(String.valueOf(chequeNo));
		}
		showAddPaymentOverlay();
		addPaymentAmountTextView.setText(SnapToolkitTextFormatter.formatPriceText(paymentAmount, getActivity()));
		SimpleDateFormat dateFormat = new SimpleDateFormat(SnapInventoryConstants.DATE_FORMAT_DD_MM_YYYY, Locale.US);
		addPaymentDateTextView.setText(dateFormat.format(paymentDate));
		
	}
	
	@Override
	public void onPurchaseOrderEditClicked(int position) {
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onDateSelected(Date selectedDate) {
		// TODO Auto-generated method stub
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(selectedDate);
		cal.add(Calendar.DATE, -1);
		selectedDate = cal.getTime();
		paymentDate = selectedDate;
		SimpleDateFormat dateFormat = new SimpleDateFormat(SnapInventoryConstants.DATE_FORMAT_DD_MM_YYYY, Locale.US);
		addPaymentDateTextView.setText(dateFormat.format(selectedDate));
	}
	
	public void setTextViewValues(){

	     dTotalValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(stockSummaryNumbers.get(0), getActivity())+ " (" + stockSummaryNumbers.get(8) + "%)");
	     sTotalValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(stockSummaryNumbers.get(1), getActivity()));
	     dTotalQty.setText(String.valueOf(stockSummaryNumbers.get(2))+ " (" + stockSummaryNumbers.get(9) + "%)");
	     sTotalQty.setText(String.valueOf(stockSummaryNumbers.get(3)));
	     dDailySales.setText(SnapToolkitTextFormatter.formatPriceText(stockSummaryNumbers.get(4), getActivity()));
	     sDailySales.setText(SnapToolkitTextFormatter.formatPriceText(stockSummaryNumbers.get(5), getActivity()));
	     dDaysStock.setText(String.valueOf(stockSummaryNumbers.get(6)));
	     sDaysStock.setText(String.valueOf(stockSummaryNumbers.get(7)));
		
	}
	
	public interface OrderSummaryButtonListener {
		public void onOrderConfirmButtonClicked(float totalOrderAmount, String currentDate);

		public void onCancelButtonClicked();
		
		public void onReceiveConfirmButtonClicked(float totalDiscountGiven, float modifiedOrderAmount, String invoiceID, Bitmap invoiceImageBitmap);
		
		public void onReceiveConfirmWithoutPO(float totalDiscountGiven, float modifiedOrderAmount, String currentDate, float totalOrderAmountWithoutPO, String invoiceID, Bitmap invoiceImageBitmap);
	}
}