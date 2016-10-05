package com.snapbizz.snapbilling.fragments;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.BillListAdapter;
import com.snapbizz.snapbilling.adapters.BillingHistoryListAdapter;
import com.snapbizz.snapbilling.asynctasks.BillDeleteTask;
import com.snapbizz.snapbilling.asynctasks.BillStatusUpdateTask;
import com.snapbizz.snapbilling.asynctasks.ChangeToInvoiceTask;
import com.snapbizz.snapbilling.asynctasks.DeleteAllBillTask;
import com.snapbizz.snapbilling.asynctasks.GetBillingItemsTask;
import com.snapbizz.snapbilling.asynctasks.GetCustomerBillsTask;
import com.snapbizz.snapbilling.utils.GoogleAnalyticsTracker;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snaptoolkit.customviews.SoftInputEditText;
import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.domains.ShoppingCart;
import com.snapbizz.snaptoolkit.domains.Transaction;
import com.snapbizz.snaptoolkit.fragments.DatePickerFragment;
import com.snapbizz.snaptoolkit.fragments.DatePickerFragment.OnDateSelectedListener;
import com.snapbizz.snaptoolkit.fragments.NumKeypadFragment;
import com.snapbizz.snaptoolkit.fragments.NumKeypadFragment.NumberKeypadEnterListener;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.PrinterFactory;
import com.snapbizz.snaptoolkit.utils.PrinterManager;
import com.snapbizz.snaptoolkit.utils.PrinterType;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;
import com.snapbizz.snaptoolkit.utils.TransactionType;

public class BillingHistoryFragment extends Fragment implements
		OnQueryCompleteListener, NumberKeypadEnterListener,
		OnDateSelectedListener {

	private int FILTER_DATE_TASK_CODE = 3;
	private final int SUBLIST_TASK_CODE = 1;
	private final int DELETE_TASK_CODE = 12;
	private final int DELETE_ALL_TASK_CODE = 13;
	private final int PAY_BILL_TASK_CODE = 29;
	private final int DELIVER_BILL_TASK_CODE = 39;
	private final int EDIT_TOTALDISCOUNT_CONTEXT = 3;
	private final int INVOICE_TASKCODE = 99;
	private double totalSalesAmount = 0;
	private SoftInputEditText billSearchEditText;
	private final String TAG = "[BillingHistoryFragment]";
	private TextView totalAmountTextView;
	private TextView totalTextView;
	private TextView savingsTextView;
	private TextView totalSalesTextView;
	private TextView totalQtyTextView;
	private TextView durationTextView;
	private TextView dayTextView;
	private TextView billTotalTextView;
	private TextView vatTextView;
	private TextView billDateTextView;
	private Button totalDiscountButton;
	private ListView billingHistoryListView;
	private ListView billingItemListView;
	private TextView billCashTextView;
	private TextView billCreditTextView;
	private View lastSelectedFilterView = null;
	private View invoiceButtonView;
	private BillListAdapter billingItemListAdapter;
	private BillingHistoryListAdapter billingHistoryListAdapter;
	private Transaction selectedTransaction;
	private DateFormat dateFormat;
	private Date startDate;
	private Date endDate;
	private Date startDateTemp;
	private boolean isTaskRunning = false;
	private boolean showVat;
	private OnActionbarNavigationListener onActionbarNavigationListener;
	private GetCustomerBillsTask currentFilterAsyncTask;
	private GetBillingItemsTask currentBillingItemsTask;
	private NumKeypadFragment numKeypadFragment;
	private PrinterManager printerManager;
	private PrinterType printerType;
	private Dialog printerTypeDialog;
	private List<BillItem> billItemList;
	private String selectedCustomerNumber;
	private boolean isOnlyCredit;
	private ArrayList<TransactionType> transactionTypeList;
	private TransactionType currentTransactionType = TransactionType.BILL;
	private String allTransactionId = null;
	static int count = 1;
	private View dialogueView;

	public void setSelectedCustomerNumber(String selectedCustomerNumber) {
		this.selectedCustomerNumber = selectedCustomerNumber;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		GoogleAnalyticsTracker.getInstance(getActivity()).fragmentLoaded(getClass().getSimpleName(), getActivity());
		View view = inflater.inflate(R.layout.fragment_billling_history,
				container, false);
		billSearchEditText = (SoftInputEditText) view
				.findViewById(R.id.bill_search_edittext);
		billingHistoryListView = (ListView) view
				.findViewById(R.id.billlist_listview);
		billingHistoryListView.setOnItemClickListener(onBillClickedListener);
		billingItemListView = (ListView) view.findViewById(R.id.bill_listview);
		durationTextView = (TextView) view.findViewById(R.id.duration_textview);
		totalQtyTextView = (TextView) view
				.findViewById(R.id.bill_totalqty_textview);
		billTotalTextView = (TextView) view
				.findViewById(R.id.bill_totalprice_textview);
		vatTextView = (TextView) view.findViewById(R.id.vat_value_textview);
		totalAmountTextView = (TextView) view
				.findViewById(R.id.amount_value_textview);
		totalSalesTextView = (TextView) view
				.findViewById(R.id.total_sales_textview);
		totalTextView = (TextView) view.findViewById(R.id.total_value_textview);
		savingsTextView = (TextView) view
				.findViewById(R.id.savings_value_textview);
		billCashTextView = (TextView) view.findViewById(R.id.cash_value_button);
		billCreditTextView = (TextView) view
				.findViewById(R.id.change_value_textview);
		totalDiscountButton = (Button) view
				.findViewById(R.id.discount_value_button);
		dayTextView = (TextView) view.findViewById(R.id.days_textview);
		view.findViewById(R.id.pay_bill_button).setOnClickListener(
				onPayDeliverButtonClicked);
		view.findViewById(R.id.cancel_bill_button).setOnClickListener(
				onDeleteButtonClicked);
		view.findViewById(R.id.deleteall_bill_button).setOnClickListener(
				onDeleteAllButtonClicked);
		view.findViewById(R.id.delivery_bill_button).setOnClickListener(
				onPayDeliverButtonClicked);
		view.findViewById(R.id.cash_clear_button).setVisibility(View.INVISIBLE);
		view.findViewById(R.id.discount_clear_button).setVisibility(
				View.INVISIBLE);
		view.findViewById(R.id.return_value_textview).setVisibility(View.INVISIBLE);
		view.findViewById(R.id.change_textview).setVisibility(View.INVISIBLE);
		totalDiscountButton.setOnClickListener(onCashDiscountClickListener);
		billDateTextView = (TextView) view
				.findViewById(R.id.billcomplete_date_textview);
		view.findViewById(R.id.print_bill_button).setOnClickListener(
				onPrintClicKListener);
		view.findViewById(R.id.invoice_bill_button).setOnClickListener(
				onPrintClicKListener);
		view.findViewById(R.id.vat_bill_button).setOnClickListener(
				onPrintClicKListener);
		return view;
	}

	AdapterView.OnItemClickListener onPrinterSelectListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long id) {
			printerType = PrinterType
					.getPrinterEnum(SnapBillingConstants.PRINTER_TYPES[position]);
			SnapSharedUtils.savePrinter(printerType, getActivity());
			printerManager = PrinterFactory.createPrinterManager(printerType,
					getActivity());
			printerManager.connect();
			printerTypeDialog.dismiss();
		}
	};

	View.OnClickListener onPrintClicKListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (getActivity() != null) {
				if (R.id.print_bill_button == v.getId()) {
					if (printerType == null) {
						if (printerTypeDialog == null)
							printerTypeDialog = SnapCommonUtils
									.buildAlertListViewDialog(
											SnapBillingConstants.SELECT_PRINTER,
											SnapBillingConstants.PRINTER_TYPES,
											getActivity(),
											onPrinterSelectListener);
						printerTypeDialog.show();
						return;
					}
					if (printerManager == null)
						printerManager = PrinterFactory.createPrinterManager(
								printerType, getActivity());
					if (!printerManager.isPrinterConnected()) {
						printerManager.connect();
					}
					Date date;
					try {
						date = new SimpleDateFormat(
								SnapToolkitConstants.STANDARD_DATEFORMAT,
								Locale.getDefault()).parse(selectedTransaction
								.getTransactionTimeStamp());
					} catch (ParseException e) {
						e.printStackTrace();
						date = Calendar.getInstance().getTime();
					}
					GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
					// TODO: Check this
					/*if (printerManager.isPrinterConnected()) {
						ShoppingCart shoppingCart = new ShoppingCart(0);
						shoppingCart.setBillItemList(billItemList);
						shoppingCart.setTotalCartValue(selectedTransaction
								.getTotalAmount());
						shoppingCart.setTotalDiscount(selectedTransaction
								.getTotalDiscount());
						shoppingCart.setTotalSavings(selectedTransaction
								.getTotalSavings());
						shoppingCart.setCustomer(selectedTransaction
								.getCustomer());
						shoppingCart.setTotalQty(selectedTransaction
								.getTotalQty());
						shoppingCart.setTotalVatAmount(selectedTransaction
								.getVAT());
						shoppingCart.setVATCalculated(showVat);
						shoppingCart.setInvoiceNumber(selectedTransaction
								.getInvoiceNumber());
						shoppingCart
								.setInvoice(selectedTransaction.isInvoice());
						shoppingCart
								.setCashPaid((float) (shoppingCart
										.getTotalCartValue()
										- shoppingCart.getTotalDiscount()
										- shoppingCart.getTotalSavings() - selectedTransaction
										.getPendingPayment()));
						shoppingCart.setFromBillingHistory(true);
						if(billItemList == null)
							return;
                        for(int i = 0; i < billItemList.size(); i++) {
                            shoppingCart.vatSplitCalculation(billItemList.get(i), billItemList.get(i).getProductSkuSalePrice(),
                                                             Math.abs(billItemList.get(i).getProductSkuQuantity()),
                                                             true, shoppingCart.vatRates);
                        }
						if (!printerManager.printShoppingCart(shoppingCart, date))
							printerManager.connect();
					}*/
				} else if (R.id.invoice_bill_button == v.getId()) {
					invoiceButtonView = v;
					if (!v.isSelected()) {
						SnapCommonUtils.showAlert(getActivity(), getString(R.string.invoice),
								getString(R.string.msg_invoice), invoicePositiveClick,
								invoiceNegativeClick, false);
					}
				} else if (R.id.vat_bill_button == v.getId()) {
					v.setSelected(!v.isSelected());
					showVat = v.isSelected();
				}
			}
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onActionbarNavigationListener = (OnActionbarNavigationListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ getString(R.string.exc_implementnavigation));
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ActionBar actionBar = getActivity().getActionBar();
		if (!actionBar.isShowing())
			actionBar.show();
		setHasOptionsMenu(true);
		actionBar.setCustomView(R.layout.actionbar_billing_history);
		if (transactionTypeList == null) {
			transactionTypeList = new ArrayList<TransactionType>();
			transactionTypeList.add(TransactionType.BILL);
			transactionTypeList.add(TransactionType.MEMO);
			transactionTypeList.add(TransactionType.ALL);
		}
		View v = getActivity().findViewById(R.id.day_button);
		v.setOnClickListener(onFilterButtonClicked);
		lastSelectedFilterView = v;
		v.setEnabled(false);
		getActivity().findViewById(R.id.week_button).setOnClickListener(
				onFilterButtonClicked);
		getActivity().findViewById(R.id.month_button).setOnClickListener(
				onFilterButtonClicked);
		getActivity().findViewById(R.id.calendar_button).setOnClickListener(
				onFilterButtonClicked);
		durationTextView.setText("1");
		dateFormat = new SimpleDateFormat(
				SnapBillingConstants.STANDARD_DATEFORMAT, Locale.getDefault());

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 24);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		this.endDate = calendar.getTime();
		final String endDate = dateFormat.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		this.startDate = calendar.getTime();
		final String startDate = dateFormat.format(calendar.getTime());
		if (billingHistoryListAdapter != null) {
			billingHistoryListView.setAdapter(billingHistoryListAdapter);
		}
		if (currentFilterAsyncTask != null
				&& !currentFilterAsyncTask.isCancelled()) {
			currentFilterAsyncTask.cancel(true);
		}

		if (currentTransactionType == null)
			currentTransactionType = TransactionType.BILL;

		if (currentTransactionType.equals(TransactionType.ALL)) {
			((TextView) getActivity().findViewById(R.id.actionbar_header))
					.setText(getString(R.string.total_bills));
			((Button) getActivity().findViewById(R.id.deleteall_bill_button))
					.setVisibility(View.GONE);
			billSearchEditText.setHint(getResources().getString(R.string.bill_search_hint));
		} else if (currentTransactionType.equals(TransactionType.MEMO)) {
			((TextView) getActivity().findViewById(R.id.actionbar_header))
					.setText(getString(R.string.memo_bills));
			((Button) getActivity().findViewById(R.id.deleteall_bill_button))
					.setVisibility(View.VISIBLE);
			billSearchEditText.setHint(getResources().getString(R.string.bill_search_hint_memo));

		} else if (currentTransactionType.equals(TransactionType.BILL)) {
			((TextView) getActivity().findViewById(R.id.actionbar_header))
					.setText(getString(R.string.bills));
			((Button) getActivity().findViewById(R.id.deleteall_bill_button))
					.setVisibility(View.GONE);
			billSearchEditText.setHint(getResources().getString(R.string.bill_search_hint));
		}

		if (selectedCustomerNumber == null
				|| !selectedCustomerNumber
						.startsWith(getString(R.string.customerpaymentfragment_bills_tag))) {
			isOnlyCredit = false;
			currentFilterAsyncTask = new GetCustomerBillsTask(getActivity(),
					BillingHistoryFragment.this, startDate, endDate,
					isOnlyCredit, FILTER_DATE_TASK_CODE, currentTransactionType);
			currentFilterAsyncTask.execute(billSearchEditText.getText()
					.toString());
		} else {
			isOnlyCredit = true;
			currentFilterAsyncTask = new GetCustomerBillsTask(getActivity(),
					BillingHistoryFragment.this, startDate, endDate,
					isOnlyCredit, FILTER_DATE_TASK_CODE, currentTransactionType);
			currentFilterAsyncTask
					.execute(selectedCustomerNumber.split(":")[1]);
			((SoftInputEditText) getView().findViewById(
					R.id.bill_search_edittext)).setText(selectedCustomerNumber
					.split(":")[1]);
		}
		((TextView) getActivity().findViewById(R.id.actionbar_header))
				.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View arg0) {
						SnapToolkitConstants.BILL_HISTORY_SEL_POS=0;
						if (isTaskRunning) {
							if (currentFilterAsyncTask != null
									&& !currentFilterAsyncTask.isCancelled()) {
								currentFilterAsyncTask.cancel(true);
							}
							isTaskRunning = false;
						}
						isTaskRunning = true;
						currentTransactionType = transactionTypeList
								.get(count++);
						if (count >= transactionTypeList.size()) {
							count = 0;
						}
						if (currentTransactionType.equals(TransactionType.ALL)) {
							((TextView) getActivity().findViewById(
									R.id.actionbar_header)).setText(getString(R.string.total_bills));
							((Button) getActivity().findViewById(
									R.id.deleteall_bill_button))
									.setVisibility(View.GONE);
							billSearchEditText.setHint(getResources().getString(R.string.bill_search_hint));
						} else if (currentTransactionType
								.equals(TransactionType.MEMO)) {
							((TextView) getActivity().findViewById(
									R.id.actionbar_header)).setText(getString(R.string.memo_bills));
							((Button) getActivity().findViewById(
									R.id.deleteall_bill_button))
									.setVisibility(View.VISIBLE);
							billSearchEditText.setHint(getResources().getString(R.string.bill_search_hint_memo));
						} else if (currentTransactionType
								.equals(TransactionType.BILL)) {
							((TextView) getActivity().findViewById(
									R.id.actionbar_header)).setText(getString(R.string.bills));
							((Button) getActivity().findViewById(
									R.id.deleteall_bill_button))
									.setVisibility(View.GONE);
							billSearchEditText.setHint(getResources().getString(R.string.bill_search_hint));
						}
						if (BillingHistoryFragment.this.startDate == null
								&& startDateTemp != null) {
							BillingHistoryFragment.this.startDate = startDateTemp;
						} else if (BillingHistoryFragment.this.startDate == null
								&& startDateTemp == null) {
							BillingHistoryFragment.this.startDate = Calendar
									.getInstance().getTime();
						}
						if (selectedCustomerNumber == null || !selectedCustomerNumber.startsWith(getString(R.string.customerpaymentfragment_bills_tag))) {
							isOnlyCredit = false;
							if (lastSelectedFilterView.getId() == R.id.calendar_button) {
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(BillingHistoryFragment.this.startDate);
								calendar.add(Calendar.DAY_OF_YEAR, -1);
								currentFilterAsyncTask = new GetCustomerBillsTask(
										getActivity(),
										BillingHistoryFragment.this,
										dateFormat.format(calendar.getTime()),
										dateFormat
												.format(BillingHistoryFragment.this.endDate),
										isOnlyCredit, FILTER_DATE_TASK_CODE,
										currentTransactionType);
								currentFilterAsyncTask
										.execute(billSearchEditText.getText()
												.toString());
							} else {
								currentFilterAsyncTask = new GetCustomerBillsTask(getActivity(),BillingHistoryFragment.this,
										dateFormat
												.format(BillingHistoryFragment.this.startDate),
										dateFormat
												.format(BillingHistoryFragment.this.endDate),
										isOnlyCredit, FILTER_DATE_TASK_CODE,
										currentTransactionType);
								currentFilterAsyncTask
										.execute(billSearchEditText.getText()
												.toString());
							}
						} else {
							isOnlyCredit = true;
							if (lastSelectedFilterView.getId() == R.id.calendar_button) {
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(BillingHistoryFragment.this.startDate);
								calendar.add(Calendar.DAY_OF_YEAR, -1);
								currentFilterAsyncTask = new GetCustomerBillsTask(
										getActivity(),
										BillingHistoryFragment.this,
										dateFormat.format(calendar.getTime()),
										dateFormat
												.format(BillingHistoryFragment.this.endDate),
										isOnlyCredit, FILTER_DATE_TASK_CODE,
										currentTransactionType);
								currentFilterAsyncTask
										.execute(selectedCustomerNumber
												.split(":")[1]);
							} else {
								currentFilterAsyncTask = new GetCustomerBillsTask(
										getActivity(),
										BillingHistoryFragment.this,
										dateFormat
												.format(BillingHistoryFragment.this.startDate),
										dateFormat
												.format(BillingHistoryFragment.this.endDate),
										isOnlyCredit, FILTER_DATE_TASK_CODE,
										currentTransactionType);
								currentFilterAsyncTask
										.execute(selectedCustomerNumber
												.split(":")[1]);
							}
							((SoftInputEditText) getView().findViewById(
									R.id.bill_search_edittext))
									.setText(selectedCustomerNumber.split(":")[1]);

						}
						return false;
					}
				});
		if (billingItemListAdapter != null)
			billingItemListView.setAdapter(billingItemListAdapter);
		printerType = SnapSharedUtils.getSavedPrinterType(getActivity());
	}

	View.OnClickListener onCashDiscountClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if(!selectedTransaction.isPaid()){
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if (numKeypadFragment == null) {
				numKeypadFragment = new NumKeypadFragment();
				numKeypadFragment
						.setKeypadEnterListener(BillingHistoryFragment.this);
			}
			ft.replace(R.id.overlay_framelayout, numKeypadFragment);
			if (!numKeypadFragment.isAdded()) {
				ft.addToBackStack(null);
			}
			ft.commit();
			boolean isDiscount = v.getId() == R.id.discount_value_button ? true
					: false;
			numKeypadFragment.setContext(EDIT_TOTALDISCOUNT_CONTEXT);
			numKeypadFragment.setIsDiscount(isDiscount);
			numKeypadFragment.setValue(String.valueOf(selectedTransaction
					.getTotalDiscount()));
			numKeypadFragment.setTotalValue(selectedTransaction
					.getTotalAmount() - selectedTransaction.getTotalSavings());
			getView().findViewById(R.id.overlay_framelayout).setVisibility(
					View.VISIBLE);
			RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			rlParams.setMargins(
					getResources().getDimensionPixelOffset(
							R.dimen.billlist_width) * 2, isDiscount ? 50 : 75,
					0, 0);
			getActivity().findViewById(R.id.overlay_framelayout)
					.setLayoutParams(rlParams);
			}
		}
	};

	View.OnClickListener onFilterButtonClicked = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (lastSelectedFilterView.getId() == R.id.calendar_button)
				lastSelectedFilterView.setSelected(false);
			else if (v.getId() != R.id.calendar_button)
				lastSelectedFilterView.setEnabled(true);

			if (v.getId() != R.id.calendar_button) {
				v.setEnabled(false);
				lastSelectedFilterView = v;
			}

			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
			if (v.getId() == R.id.day_button) {
				durationTextView.setText("1");
				dayTextView.setText(getString(R.string.day));
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, 24);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				BillingHistoryFragment.this.endDate = calendar.getTime();
				String endDate = dateFormat.format(calendar.getTime());
				calendar.add(Calendar.DAY_OF_YEAR, -1);
				BillingHistoryFragment.this.startDate = calendar.getTime();
				String startDate = dateFormat.format(calendar.getTime());
				if (currentFilterAsyncTask != null
						&& !currentFilterAsyncTask.isCancelled()) {
					currentFilterAsyncTask.cancel(true);
				}
				currentFilterAsyncTask = new GetCustomerBillsTask(
						getActivity(), BillingHistoryFragment.this, startDate,
						endDate, isOnlyCredit, FILTER_DATE_TASK_CODE,
						currentTransactionType);
				currentFilterAsyncTask.execute(billSearchEditText.getText()
						.toString());
			} else if (v.getId() == R.id.week_button) {
				durationTextView.setText("7");
				dayTextView.setText(getString(R.string.days));
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, 24);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				BillingHistoryFragment.this.endDate = calendar.getTime();
				String endDate = dateFormat.format(calendar.getTime());
				calendar.add(Calendar.WEEK_OF_YEAR, -1);
				BillingHistoryFragment.this.startDate = calendar.getTime();
				String startDate = dateFormat.format(calendar.getTime());
				if (currentFilterAsyncTask != null
						&& !currentFilterAsyncTask.isCancelled()) {
					currentFilterAsyncTask.cancel(true);
				}
				currentFilterAsyncTask = new GetCustomerBillsTask(
						getActivity(), BillingHistoryFragment.this, startDate,
						endDate, isOnlyCredit, FILTER_DATE_TASK_CODE,
						currentTransactionType);
				currentFilterAsyncTask.execute(billSearchEditText.getText()
						.toString());
			} else if (v.getId() == R.id.month_button) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, 24);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				BillingHistoryFragment.this.endDate = calendar.getTime();
				String endDate = dateFormat.format(calendar.getTime());
				calendar.add(Calendar.MONTH, -1);
				BillingHistoryFragment.this.startDate = calendar.getTime();
				String startDate = dateFormat.format(calendar.getTime());
				durationTextView.setText(calendar
						.getActualMaximum(Calendar.DAY_OF_MONTH) + "");
				dayTextView.setText(getString(R.string.days));
				if (currentFilterAsyncTask != null
						&& !currentFilterAsyncTask.isCancelled()) {
					currentFilterAsyncTask.cancel(true);
				}
				currentFilterAsyncTask = new GetCustomerBillsTask(
						getActivity(), BillingHistoryFragment.this, startDate,
						endDate, isOnlyCredit, FILTER_DATE_TASK_CODE,
						currentTransactionType);
				currentFilterAsyncTask.execute(billSearchEditText.getText()
						.toString());
			} else {
				isTaskRunning = false;
				startDateTemp = startDate;
				startDate = null;
				DatePickerFragment datePickerFragment = new DatePickerFragment();
				datePickerFragment
						.setOnDateSelectedListener(BillingHistoryFragment.this);
				datePickerFragment.show(getFragmentManager(), "date picker");
			}
		}
	};

	View.OnClickListener onDeleteButtonClicked = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
			if (SnapSharedUtils.getLastStoredPin(SnapCommonUtils
					.getSnapContext(getActivity())) != null
					&& !SnapSharedUtils
							.getLastStoredPin(
									SnapCommonUtils
											.getSnapContext(getActivity()))
							.trim().equals("")) {
				dialogueView = SnapCommonUtils.showPinAlert(getActivity(),
						positivePinClickListener, negativePinClickListener,
						true);

			} else {
				SnapCommonUtils.showDeleteAlert(getActivity(), "",
						getString(R.string.msg_confirm_delete), positiveDeleteClickListener,
						negativeClickListener, false);

			}
		}
	};

	View.OnClickListener onDeleteAllButtonClicked = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (allTransactionId != null && !allTransactionId.isEmpty()) {
				GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
				if (SnapSharedUtils.getLastStoredPin(SnapCommonUtils
						.getSnapContext(getActivity())) != null
						&& !SnapSharedUtils
								.getLastStoredPin(
										SnapCommonUtils
												.getSnapContext(getActivity()))
								.trim().equals("")) {
					dialogueView = SnapCommonUtils.showPinAlert(getActivity(),
							positivePinAllClickListener,
							negativePinClickListener, true);
				} else {
					SnapCommonUtils
							.showDeleteAlert(
									getActivity(),
									"",
									getString(R.string.msg_deletecreditbills),
									positiveConfirmDeleteListener,
									negativeClickListener, false);

				}
			}
		}
	};

	OnClickListener negativePinClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			SnapCommonUtils.hideSoftKeyboard(getActivity(), v.getWindowToken());
			SnapCommonUtils.dismissAlert();
			dialogueView = null;

		}
	};

	View.OnClickListener onPayDeliverButtonClicked = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
			if (v.getId() == R.id.pay_bill_button) {
				if (!selectedTransaction.isPaid()) {
					SnapCommonUtils
							.showAlert(
									getActivity(),
									"",
									getString(R.string.msg_deliverycomplete),
									positivePayClickListener,
									negativePayClickListener, false);
				}
			} else {
				if (selectedTransaction.isPaid()) {
					SnapCommonUtils
							.showAlert(
									getActivity(),
									getString(R.string.confirm),
									getString(R.string.msg_deliveryconfirm),
									positiveClickListener,
									negativeClickListener, false);
				}
			}
		}

	};

	AdapterView.OnItemClickListener onBillClickedListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long id) {
			
			
			if (position != SnapToolkitConstants.BILL_HISTORY_SEL_POS) {
				selectedTransaction = billingHistoryListAdapter
						.getItem(position);
				getView().findViewById(R.id.invoice_bill_button).setSelected(
						selectedTransaction.isInvoice());
				getView().findViewById(R.id.cancel_bill_button).setVisibility(
						View.VISIBLE);

				if (selectedTransaction.isInvoice()) {
					getView().findViewById(R.id.cancel_bill_button)
							.setVisibility(View.GONE);
					((TextView) getView().findViewById(R.id.bill_id_textview))
							.setVisibility(View.VISIBLE);
					((TextView) getView().findViewById(R.id.bill_id_textview))
							.setText(getString(R.string.billno)
									+ selectedTransaction.getInvoiceNumber());
				} else {
					((TextView) getView().findViewById(R.id.bill_id_textview))
							.setVisibility(View.GONE);
					if (selectedTransaction.getPendingPayment() > 0) {
						getView().findViewById(R.id.cancel_bill_button)
								.setVisibility(View.GONE);
					} else {
						getView().findViewById(R.id.cancel_bill_button)
								.setVisibility(View.VISIBLE);
					}
				}

				String[] dateSplitString = selectedTransaction
						.getTransactionTimeStamp()
						.substring(
								0,
								selectedTransaction.getTransactionTimeStamp()
										.indexOf(" ")).split("/");
				billDateTextView.setText(dateSplitString[2] + "/"
						+ dateSplitString[1] + "/" + dateSplitString[0]);
				if (currentBillingItemsTask != null
						&& !currentBillingItemsTask.isCancelled())
					currentBillingItemsTask.cancel(true);
				currentBillingItemsTask = new GetBillingItemsTask(
						getActivity(), BillingHistoryFragment.this,
						SUBLIST_TASK_CODE,
						selectedTransaction.getTransactionId());
				currentBillingItemsTask.execute();
				billingHistoryListAdapter.getLastSelectedTransaction()
						.setSelected(false);
				billingHistoryListAdapter.setLastSelectedPosition(position);
				SnapToolkitConstants.BILL_HISTORY_SEL_POS=position;
				selectedTransaction.setSelected(true);
				billingHistoryListAdapter.notifyDataSetChanged();
				getView().findViewById(R.id.pay_bill_button).setEnabled(
						!selectedTransaction.isPaid());
				getView().findViewById(R.id.delivery_bill_button).setEnabled(
						selectedTransaction.isPaid());
				
			}
			if(getView().findViewById(R.id.overlay_framelayout).isShown()){
			getView().findViewById(R.id.overlay_framelayout).setVisibility(
					View.GONE);
			onActionbarNavigationListener.onActionbarNavigation("",
					android.R.id.home);
			}
		}
	};

	TextWatcher billSearchTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			keyStrokeTimer.cancel();
			if (s.length() == 0) {
				if (lastSelectedFilterView.getId() == R.id.day_button) {
					onFilterButtonClicked.onClick(lastSelectedFilterView);
				} else if (lastSelectedFilterView.getId() == R.id.week_button) {
					onFilterButtonClicked.onClick(lastSelectedFilterView);
				} else if (lastSelectedFilterView.getId() == R.id.month_button) {
					onFilterButtonClicked.onClick(lastSelectedFilterView);
				} else {
					startDate = startDateTemp;
					onDateSelected(endDate);
				}
			} else
				keyStrokeTimer.start();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	CountDownTimer keyStrokeTimer = new CountDownTimer(
			SnapBillingConstants.KEY_STROKE_TIMEOUT,
			SnapBillingConstants.KEY_STROKE_TIMEOUT) {

		@Override
		public void onTick(long arg0) {
		}

		@Override
		public void onFinish() {
			String customerName = billSearchEditText.getText().toString()
					.trim();
			if (customerName.length() > 0) {
				if (currentFilterAsyncTask != null
						&& !currentFilterAsyncTask.isCancelled()) {
					currentFilterAsyncTask.cancel(true);
				}
				if (customerName.contains("'")) {
					customerName = customerName.replace("'", "");
					billSearchEditText.setText(customerName);
					billSearchEditText.setSelection(customerName.length());
				}
				if (startDate == null)
					startDate = startDateTemp;
				currentFilterAsyncTask = new GetCustomerBillsTask(
						getActivity(), BillingHistoryFragment.this,
						dateFormat.format(startDate),
						dateFormat.format(endDate), isOnlyCredit,
						FILTER_DATE_TASK_CODE, currentTransactionType);
				currentFilterAsyncTask.execute(customerName);
			}
		}
	};

	@Override
	public void onStop() {
		super.onStop();
		if (currentBillingItemsTask != null)
			currentBillingItemsTask.cancel(true);
		if (currentFilterAsyncTask != null)
			currentFilterAsyncTask.cancel(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		billSearchEditText.addTextChangedListener(billSearchTextWatcher);
	}

	@Override
	public void onPause() {
		super.onPause();
		billSearchEditText.setText("");
		billSearchEditText.removeTextChangedListener(billSearchTextWatcher);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		if(getView() == null)
			return;
		Log.d("", "taskCode--onTaskSuccess---taskCode=" + taskCode);

		if (FILTER_DATE_TASK_CODE == taskCode) {
			if (billingItemListAdapter != null) {
				billingItemListAdapter.clear();
				billingItemListAdapter.notifyDataSetChanged();
			}
			if (getView() != null) {
				View v = getView().findViewById(R.id.bill_options_layout);
				if (v.getVisibility() != View.VISIBLE)
					v.setVisibility(View.VISIBLE);
			}
			ArrayList<Transaction> transactions = new ArrayList<Transaction>();
			isTaskRunning = false;
			transactions = (ArrayList<Transaction>) responseList;
			if (transactions.size() == 0) {
				if (billingHistoryListAdapter != null) {
					billingHistoryListAdapter.clear();
					billingHistoryListAdapter.notifyDataSetChanged();
				}
				if (getActivity() != null && getView() != null) {
					getView().findViewById(R.id.bill_options_layout)
							.setVisibility(View.GONE);
					billTotalTextView.setText(SnapBillingTextFormatter
							.formatPriceText(0, getActivity()));
					totalQtyTextView.setText("0");
					totalSalesTextView.setText(SnapBillingTextFormatter
							.formatPriceText(0, getActivity()));
				}
				allTransactionId = null;
				return;
			}
			if(SnapToolkitConstants.BILL_HISTORY_SEL_POS < transactions.size())
				this.selectedTransaction = transactions.get(SnapToolkitConstants.BILL_HISTORY_SEL_POS);
			else
				this.selectedTransaction = transactions.get(0);
			if (selectedTransaction.isInvoice()) {
				getView().findViewById(R.id.cancel_bill_button).setVisibility(
						View.GONE);
				((TextView) getView().findViewById(R.id.bill_id_textview))
						.setVisibility(View.VISIBLE);
				((TextView) getView().findViewById(R.id.bill_id_textview))
						.setText(getString(R.string.billno)
								+ selectedTransaction.getInvoiceNumber());
				getView().findViewById(R.id.invoice_bill_button).setSelected(
						true);
			} else {

				((TextView) getView().findViewById(R.id.bill_id_textview))
						.setVisibility(View.GONE);

				if (selectedTransaction.getPendingPayment() > 0) {
					getView().findViewById(R.id.cancel_bill_button)
							.setVisibility(View.GONE);
				} else {
					getView().findViewById(R.id.cancel_bill_button)
							.setVisibility(View.VISIBLE);
				}

				getView().findViewById(R.id.invoice_bill_button).setSelected(
						false);
			}
			getView().findViewById(R.id.pay_bill_button).setEnabled(
					!selectedTransaction.isPaid());
			getView().findViewById(R.id.delivery_bill_button).setEnabled(
					selectedTransaction.isPaid());
			String[] dateSplitString = selectedTransaction
					.getTransactionTimeStamp()
					.substring(
							0,
							selectedTransaction.getTransactionTimeStamp()
									.indexOf(" ")).split("/");
			billDateTextView.setText(dateSplitString[2] + "/"
					+ dateSplitString[1] + "/" + dateSplitString[0]);
			selectedTransaction.setSelected(true);

			if (currentBillingItemsTask != null
					&& !currentBillingItemsTask.isCancelled()) {
				currentBillingItemsTask.cancel(true);
			}
			currentBillingItemsTask = new GetBillingItemsTask(getActivity(),
					BillingHistoryFragment.this, SUBLIST_TASK_CODE,
					selectedTransaction.getTransactionId());
			currentBillingItemsTask.execute();
			if (null == billingHistoryListAdapter) {
				billingHistoryListAdapter = new BillingHistoryListAdapter(
						getActivity(), android.R.id.text1, transactions);
				billingHistoryListView.setAdapter(billingHistoryListAdapter);
			} else {
				if(SnapToolkitConstants.BILL_HISTORY_SEL_POS < transactions.size())
					billingHistoryListAdapter.setLastSelectedPosition(SnapToolkitConstants.BILL_HISTORY_SEL_POS);
				else
					billingHistoryListAdapter.setLastSelectedPosition(0);
				billingHistoryListAdapter.clear();
				billingHistoryListAdapter.addAll(transactions);
				billingHistoryListAdapter.notifyDataSetChanged();
			}
			totalSalesAmount = 0;
			allTransactionId = null;
			for (Transaction transaction : transactions) {
				if (transaction.getPendingPayment() <= 0) {
					if (allTransactionId == null) {
						allTransactionId = transaction.getTransactionId() + "";
					} else {
						allTransactionId = allTransactionId + ","
								+ transaction.getTransactionId();
					}
				}
				totalSalesAmount = totalSalesAmount
						+ transaction.getTotalAmount()
						- transaction.getTotalDiscount()
						- transaction.getTotalSavings();

			}
			totalSalesTextView.setText(SnapToolkitTextFormatter
					.formatPriceText(totalSalesAmount, getActivity()));
		} /*else if (SUBLIST_TASK_CODE == taskCode) {
			if (null == billingItemListAdapter) {
				billItemList = (ArrayList<BillItem>) responseList;
				ShoppingCart shoppingCart = new ShoppingCart(999);
				shoppingCart.setBillItemList(billItemList);
				billingItemListAdapter = new BillListAdapter(getActivity(),
						shoppingCart, null, R.layout.listitem_bill_monitor,
						true);
				billingItemListView.setAdapter(billingItemListAdapter);
			} else {
				billItemList.clear();
				billItemList.addAll((ArrayList<BillItem>) responseList);
				billingItemListAdapter.notifyDataSetChanged();
			}
			if (null != selectedTransaction) {
				billTotalTextView.setText(SnapToolkitTextFormatter.formatPriceText(
						selectedTransaction.getTotalAmount()
								- selectedTransaction.getTotalSavings(),
						getActivity()));
				vatTextView.setText(SnapToolkitTextFormatter.formatPriceText(
						selectedTransaction.getVAT(), getActivity()));
				totalQtyTextView.setText(selectedTransaction.getTotalQty() + "");
				totalAmountTextView.setText(SnapToolkitTextFormatter
						.formatPriceText(selectedTransaction.getTotalAmount(),
								getActivity()));
				totalTextView.setText(SnapToolkitTextFormatter.formatPriceText(
						selectedTransaction.getTotalAmount()
								- selectedTransaction.getTotalDiscount()
								- selectedTransaction.getTotalSavings(),
						getActivity()));
				savingsTextView.setText(SnapToolkitTextFormatter
						.formatRoundedPriceText(
								selectedTransaction.getTotalSavings(),
								getActivity()));
				totalDiscountButton.setText(SnapToolkitTextFormatter
						.formatRoundedPriceText(
								selectedTransaction.getTotalDiscount(),
								getActivity()));
				billCashTextView.setText(SnapBillingTextFormatter.formatPriceText(
						selectedTransaction.getTotalAmount()
								- selectedTransaction.getTotalDiscount()
								- selectedTransaction.getTotalSavings()
								- selectedTransaction.getPendingPayment(),
						getActivity()));
				if (selectedTransaction.getPendingPayment() > 0) {
					billCreditTextView.setText(SnapBillingTextFormatter
							.formatPriceText(
									(selectedTransaction.getPendingPayment()),
									getActivity()));
				} else {
					billCreditTextView.setText(SnapBillingTextFormatter
							.formatPriceText(0, getActivity()));
				}

				if (selectedTransaction.isPaid()) {
					totalDiscountButton.setEnabled(false);
				} else {
					totalDiscountButton.setEnabled(true);
				}
			}
		} */else if (DELETE_TASK_CODE == taskCode) {
			billingHistoryListAdapter.remove(selectedTransaction);
			billingHistoryListAdapter.notifyDataSetChanged();
			if (billingItemListAdapter != null) {
				billingItemListAdapter.clear();
				billingHistoryListAdapter.notifyDataSetChanged();
			}
			totalSalesAmount = totalSalesAmount
					- selectedTransaction.getTotalAmount()
					+ selectedTransaction.getTotalDiscount()
					+ selectedTransaction.getTotalSavings();
			totalSalesTextView.setText(SnapToolkitTextFormatter
					.formatPriceText(totalSalesAmount, getActivity()));
			if (billingHistoryListAdapter.getCount() > 0) {
				this.selectedTransaction = billingHistoryListAdapter.getItem(0);
				getView().findViewById(R.id.pay_bill_button).setEnabled(
						!selectedTransaction.isPaid());
				getView().findViewById(R.id.delivery_bill_button).setEnabled(
						selectedTransaction.isPaid());
				String[] dateSplitString = selectedTransaction
						.getTransactionTimeStamp()
						.substring(
								0,
								selectedTransaction.getTransactionTimeStamp()
										.indexOf(" ")).split("/");
				billDateTextView.setText(dateSplitString[2] + "/"
						+ dateSplitString[1] + "/" + dateSplitString[0]);
				if (selectedTransaction.getPendingPayment() > 0) {
					getView().findViewById(R.id.cancel_bill_button)
							.setVisibility(View.GONE);
				} else {
					getView().findViewById(R.id.cancel_bill_button)
							.setVisibility(View.VISIBLE);
				}
				selectedTransaction.setSelected(true);
				billingHistoryListAdapter.setLastSelectedPosition(SnapToolkitConstants.BILL_HISTORY_SEL_POS);
				billingHistoryListAdapter.notifyDataSetChanged();
				if (currentBillingItemsTask != null
						&& !currentBillingItemsTask.isCancelled()) {
					currentBillingItemsTask.cancel(true);
				}
				currentBillingItemsTask = new GetBillingItemsTask(
						getActivity(), BillingHistoryFragment.this,
						SUBLIST_TASK_CODE,
						selectedTransaction.getTransactionId());
				currentBillingItemsTask.execute();
			} else {
				getView().findViewById(R.id.bill_options_layout).setVisibility(
						View.GONE);
				selectedTransaction = null;
				totalQtyTextView.setText(0 + "");
				billTotalTextView.setText(SnapBillingTextFormatter
						.formatPriceText(0, getActivity()));
			}
		} else if (PAY_BILL_TASK_CODE == taskCode) {
			totalDiscountButton.setEnabled(false);
			billingHistoryListAdapter.notifyDataSetChanged();
			getView().findViewById(R.id.pay_bill_button).setEnabled(
					!selectedTransaction.isPaid());
			getView().findViewById(R.id.delivery_bill_button).setEnabled(
					selectedTransaction.isPaid());
			totalSalesTextView.setText(SnapBillingTextFormatter
					.formatPriceText(totalSalesAmount, getActivity()));
		} else if (DELIVER_BILL_TASK_CODE == taskCode) {
			totalDiscountButton.setEnabled(true);
			billingHistoryListAdapter.notifyDataSetChanged();
			getView().findViewById(R.id.pay_bill_button).setEnabled(
					!selectedTransaction.isPaid());
			getView().findViewById(R.id.delivery_bill_button).setEnabled(
					selectedTransaction.isPaid());
		} else if (INVOICE_TASKCODE == taskCode) {
			invoiceButtonView.setSelected(true);
			if (startDate == null)
				startDate = startDateTemp;
			String startDateString = dateFormat.format(startDate);
			String endDateString = dateFormat.format(endDate);
			currentFilterAsyncTask = new GetCustomerBillsTask(getActivity(),
					BillingHistoryFragment.this, startDateString,
					endDateString, isOnlyCredit, FILTER_DATE_TASK_CODE,
					currentTransactionType);
			currentFilterAsyncTask.execute(billSearchEditText.getText()
					.toString());
			refreshList();
		} else if (DELETE_ALL_TASK_CODE == taskCode) {
			getView().findViewById(R.id.bill_options_layout).setVisibility(
					View.GONE);
			selectedTransaction = null;
			totalQtyTextView.setText(0 + "");
			billTotalTextView.setText(SnapBillingTextFormatter.formatPriceText(
					0, getActivity()));
			if (billingItemListAdapter != null) {
				billingItemListAdapter.clear();
				billingHistoryListAdapter.clear();
				billingHistoryListAdapter.notifyDataSetChanged();
				totalSalesTextView.setText(0 + "");

			}
			refreshList();
		}
	}

	private void refreshList() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 24);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		this.endDate = calendar.getTime();
		final String endDate = dateFormat.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		this.startDate = calendar.getTime();
		final String startDate = dateFormat.format(calendar.getTime());
		currentFilterAsyncTask = new GetCustomerBillsTask(getActivity(),
				BillingHistoryFragment.this, startDate, endDate, isOnlyCredit,
				FILTER_DATE_TASK_CODE, currentTransactionType);
		currentFilterAsyncTask.execute(billSearchEditText.getText().toString());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		billSearchEditText = null;
		totalAmountTextView = null;
		totalTextView = null;
		savingsTextView = null;
		totalSalesTextView = null;
		totalQtyTextView = null;
		billCashTextView = null;
		billCreditTextView = null;
		durationTextView = null;
		dayTextView = null;
		billTotalTextView = null;
		billDateTextView = null;
		totalDiscountButton = null;
		billingHistoryListView = null;
		billingItemListView = null;
		lastSelectedFilterView = null;
		billingItemListAdapter = null;
		billingHistoryListAdapter = null;
		selectedTransaction = null;
		dateFormat = null;
		startDate = null;
		endDate = null;
		startDateTemp = null;
		onActionbarNavigationListener = null;
		currentFilterAsyncTask = null;
		currentBillingItemsTask = null;
		numKeypadFragment = null;
		printerManager = null;
		printerType = null;
		printerTypeDialog = null;
		billItemList = null;
		selectedCustomerNumber = null;
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {

		Log.d("", "taskCode--Error---taskCode=" + taskCode);
		if (taskCode == SUBLIST_TASK_CODE) {
			if (billingItemListAdapter != null) {
				billingItemListAdapter.clear();
				billingItemListAdapter.notifyDataSetChanged();
			}
		} else if (taskCode == FILTER_DATE_TASK_CODE) {
			if (billingHistoryListAdapter != null) {
				billingHistoryListAdapter.clear();
				billingHistoryListAdapter.notifyDataSetChanged();
			}
			if (billingItemListAdapter != null) {
				billingItemListAdapter.clear();
				billingItemListAdapter.notifyDataSetChanged();
			}
			if (getActivity() != null) {
				getView().findViewById(R.id.bill_options_layout).setVisibility(
						View.GONE);
				billTotalTextView.setText(SnapBillingTextFormatter
						.formatPriceText(0, getActivity()));
				totalQtyTextView.setText("0");
				totalSalesTextView.setText(SnapBillingTextFormatter
						.formatPriceText(0, getActivity()));
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.findItem(R.id.billhistory_menuitem).setVisible(false);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		onActionbarNavigationListener.onActionbarNavigation(getTag(),
				menuItem.getItemId());
		return true;
	}

	@Override
	public void onNumKeyPadEnter(String value, int context) {
		if (context == EDIT_TOTALDISCOUNT_CONTEXT) {
			totalSalesAmount = totalSalesAmount
					+ selectedTransaction.getTotalDiscount()
					- Float.parseFloat(value);
			selectedTransaction.setTotalDiscount(Float.parseFloat(value));
			totalDiscountButton.setText(SnapBillingTextFormatter
					.formatPriceText(Float.parseFloat(value), getActivity()));
			totalTextView.setText(SnapBillingTextFormatter.formatPriceText(
					selectedTransaction.getTotalAmount()
							- selectedTransaction.getTotalSavings()
							- Float.parseFloat(value), getActivity()));
			billCashTextView.setText(SnapBillingTextFormatter.formatPriceText(
					selectedTransaction.getTotalAmount()
							- selectedTransaction.getTotalDiscount()
							- selectedTransaction.getTotalSavings()
							- selectedTransaction.getPendingPayment(),
					getActivity()));
		}
		getView().findViewById(R.id.overlay_framelayout).setVisibility(
				View.GONE);
		onActionbarNavigationListener.onActionbarNavigation("",
				android.R.id.home);
	}

	@Override
	public void onDateSelected(Date selectedDate) {
		if (startDate == null && selectedDate != null) {
			startDate = selectedDate;
			startDateTemp = startDate;
			DatePickerFragment datePickerFragment = new DatePickerFragment();
			datePickerFragment
					.setOnDateSelectedListener(BillingHistoryFragment.this);
			datePickerFragment.show(getFragmentManager(), "date picker");
		} else if (selectedDate != null) {
			endDate = SnapCommonUtils.setTimeToEOD(selectedDate);
			lastSelectedFilterView.setEnabled(true);
			lastSelectedFilterView = getActivity().findViewById(
					R.id.calendar_button);
			lastSelectedFilterView.setSelected(true);
			if (!isTaskRunning) {
				isTaskRunning = true;
				if (currentFilterAsyncTask != null
						&& !currentFilterAsyncTask.isCancelled()) {
					currentFilterAsyncTask.cancel(true);
				}
				if (startDate.getTime() > endDate.getTime()) {
					Date temp = startDate;
					startDate = endDate;
					selectedDate = temp;
				}
				int days = (int) Math
						.abs((startDate.getTime() - endDate.getTime()) / 86400000);
				days = days + 1;
				durationTextView.setText(String.valueOf(days));
				if (days > 1)
					dayTextView.setText(getString(R.string.days));
				else
					dayTextView.setText(getString(R.string.day));
				currentFilterAsyncTask = new GetCustomerBillsTask(
						getActivity(), BillingHistoryFragment.this,
						dateFormat.format(startDate),
						dateFormat.format(endDate), isOnlyCredit,
						FILTER_DATE_TASK_CODE, currentTransactionType);
				currentFilterAsyncTask.execute(billSearchEditText.getText()
						.toString());
			}
		}
	}

	OnClickListener positivePayClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Log.d("tag", "positive pay click listener");
			// called when bill is changed from delivery to paid
			new BillStatusUpdateTask(getActivity(),
					BillingHistoryFragment.this, PAY_BILL_TASK_CODE, true)
					.execute(selectedTransaction);
			SnapCommonUtils.dismissAlert();
		}
	};
	OnClickListener negativePayClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Log.d("tag", "negative pay click listener");
			SnapCommonUtils.dismissAlert();
		}
	};
	OnClickListener positiveClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Log.d("tag", "positive click listener");
			// called when bill is changed from paid to delivery
			new BillStatusUpdateTask(getActivity(),
					BillingHistoryFragment.this, DELIVER_BILL_TASK_CODE, false)
					.execute(selectedTransaction);
			SnapCommonUtils.dismissAlert();
		}
	};
	OnClickListener negativeClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Log.d("tag", "negative click listener");
			SnapCommonUtils.dismissAlert();
		}
	};

	OnClickListener positiveConfirmDeleteListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			SnapCommonUtils.dismissAlert();
			SnapCommonUtils.showDeleteAlert(getActivity(), "", "Confirm ?",
					positiveDeleteAllClickListener, negativeClickListener,
					false);

		}
	};

	OnClickListener positiveDeleteClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			new BillDeleteTask(getActivity(), BillingHistoryFragment.this,
					DELETE_TASK_CODE).execute(selectedTransaction
					.getTransactionId());
			SnapCommonUtils.dismissAlert();
		}
	};

	OnClickListener positivePinAllClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			TextView alertErrorTextview = (TextView) dialogueView
					.findViewById(R.id.alert_error_textview);
			EditText pinEdittext = (EditText) dialogueView
					.findViewById(R.id.pin_edittext);
			String pin = pinEdittext.getText().toString().trim();
			Log.d("", "pin------>" + pin);
			if (pinEdittext
					.getText()
					.toString()
					.equals(SnapSharedUtils.getLastStoredPin(SnapCommonUtils
							.getSnapContext(getActivity())))) {

				SnapCommonUtils.dismissAlert();
				SnapCommonUtils
						.showDeleteAlert(
								getActivity(),
								"",
								getString(R.string.msg_deletecreditbills),
								positiveConfirmDeleteListener,
								negativeClickListener, false);

			} else {
				alertErrorTextview.setVisibility(View.VISIBLE);
			}
		}
	};

	OnClickListener positivePinClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			TextView alertErrorTextview = (TextView) dialogueView
					.findViewById(R.id.alert_error_textview);
			EditText pinEdittext = (EditText) dialogueView
					.findViewById(R.id.pin_edittext);
			String pin = pinEdittext.getText().toString().trim();
			Log.d("", "pin------>" + pin);
			if (pinEdittext
					.getText()
					.toString()
					.equals(SnapSharedUtils.getLastStoredPin(SnapCommonUtils
							.getSnapContext(getActivity())))) {
				SnapCommonUtils.dismissAlert();
				SnapCommonUtils.showDeleteAlert(getActivity(), "", getString(R.string.msg_confirm),
						positiveDeleteClickListener, negativeClickListener,
						false);
			} else {
				alertErrorTextview.setVisibility(View.VISIBLE);
			}
		}
	};

	OnClickListener positiveDeleteAllClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (allTransactionId != null && !allTransactionId.isEmpty()) {
				new DeleteAllBillTask(getActivity(),
						BillingHistoryFragment.this, DELETE_ALL_TASK_CODE)
						.execute(allTransactionId);
			}

			SnapCommonUtils.dismissAlert();

		}
	};

	OnClickListener invoicePositiveClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			new ChangeToInvoiceTask(getActivity(), BillingHistoryFragment.this,
					INVOICE_TASKCODE).execute(selectedTransaction);
			SnapCommonUtils.dismissAlert();
		}
	};

	OnClickListener invoiceNegativeClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			SnapCommonUtils.dismissAlert();
		}
	};
}
