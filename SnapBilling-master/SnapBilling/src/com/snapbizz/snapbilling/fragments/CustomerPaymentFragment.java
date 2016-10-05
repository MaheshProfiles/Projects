package com.snapbizz.snapbilling.fragments;


import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.CustomerListAdapter;
import com.snapbizz.snapbilling.adapters.CustomerPaymentHistoryAdapter;
import com.snapbizz.snapbilling.adapters.CustomerPaymentHistoryAdapter.OnCustomerPaymentEditListener;
import com.snapbizz.snapbilling.asynctasks.RemindCreditCustomerTask;
import com.snapbizz.snapbilling.db.CustomerInvoicesHelper;
import com.snapbizz.snapbilling.domainsV2.CustomerInvoiceTranscationDetails;
import com.snapbizz.snapbilling.utils.GoogleAnalyticsTracker;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snaptoolkit.customviews.SoftInputEditText;
import com.snapbizz.snaptoolkit.db.InvoiceHelper;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;
import com.snapbizz.snaptoolkit.db.dao.CustomerDetails;
import com.snapbizz.snaptoolkit.db.dao.Customers;
import com.snapbizz.snaptoolkit.db.dao.Invoices;
import com.snapbizz.snaptoolkit.db.dao.Transactions;
import com.snapbizz.snaptoolkit.domains.CustomerCreditPayment;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.PrinterFactory;
import com.snapbizz.snaptoolkit.utils.PrinterManager;
import com.snapbizz.snaptoolkit.utils.PrinterType;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.ValidationUtils;

public class CustomerPaymentFragment extends Fragment implements OnQueryCompleteListener, OnCustomerPaymentEditListener {

    private CustomerListAdapter customerListAdapter;
    private int REMIND_CUSTOMER_CREDIT_TASKCODE = 1;
    private ListView customerListView;
    private OnActionbarNavigationListener onActionbarNavigationListener;
    private TextView customerNameTextView;
    private TextView customerAddressTextView;
    private TextView customerNumberTextView;
    private TextView customerDueTextView;
    private TextView customerLastPurchaseTextView;
    private TextView customerLastPaymentTextView;
    private TextView customerLastPaymentAmountTextView;
    private TextView avgVisitsTextView;
    private TextView avgPurchaseTextView;
    private ListView customerPaymentListView;
    private View historyLayout;
    private View summaryLayout;
    private View historyTab;
    private View summaryTab;
    private View footerView;
    private EditText customerSearchEditText;
    private EditText paymentEditText;
    private double totalDueValue = 0;
    private List<Customers> customerList;
	private final String TAG = "[CustomerPaymentFragment]";
    private CustomerPaymentHistoryAdapter customerPaymentAdapter;
    private Dialog paymentDialog;
    private View customerDetailsView;
    private View newCustomerLayout;
    DateFormat dateFormat;
    private TextView tvCurrentMonthText;
    private Date transcationEndDate;
    private Date openingBalanceStartDate;
    private Date openingBalanceEndDate;
    private int  creditbalance = 0;
    private PrinterManager printerManager;
    private PrinterType printerType;
    private Dialog printerTypeDialog;
    private List<CustomerCreditPayment> customerCreditPayment;
    private DatePicker datePicker;
    private SnapbizzDB snapbizzDB;
    int month;
    int year;
    private static int amount = 0;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		GoogleAnalyticsTracker.getInstance(getActivity()).fragmentLoaded(getClass().getSimpleName(), getActivity());
        View view = inflater.inflate(R.layout.fragment_customer_payment, null);
        customerNameTextView = (TextView) view.findViewById(R.id.customer_name_textview);
        customerNumberTextView = (TextView) view.findViewById(R.id.customer_number_textview);
        customerAddressTextView = (TextView) view.findViewById(R.id.customer_address_textview);
        customerDueTextView = (TextView) view.findViewById(R.id.customer_credit_textview);
        customerLastPurchaseTextView = (TextView) view.findViewById(R.id.lastpurchasedate_textview);
        customerLastPaymentTextView = (TextView) view.findViewById(R.id.lastpaymentdate_textview);
        customerLastPaymentAmountTextView = (TextView) view.findViewById(R.id.lastpaymentdate_amount_textview);
        avgVisitsTextView = (TextView) view.findViewById(R.id.avg_visits_textview);
        avgPurchaseTextView = (TextView) view.findViewById(R.id.avg_purchase_textview);
        tvCurrentMonthText = (TextView) view.findViewById(R.id.current_month);
        historyLayout = view.findViewById(R.id.history_layout);
        summaryLayout = view.findViewById(R.id.summary_layout);
        historyTab = view.findViewById(R.id.history_tab);
        summaryTab = view.findViewById(R.id.summary_tab);
        customerSearchEditText = (EditText) view.findViewById(R.id.customer_search_edittext);
        paymentEditText = (EditText) view.findViewById(R.id.payment_edittext);
        customerDetailsView = view.findViewById(R.id.customer_detail_layout);
        newCustomerLayout = view.findViewById(R.id.new_customer_relativelayout);
        datePicker = (DatePicker) view.findViewById(R.id.datepicker);
        return view;
    }
    
    OnClickListener showListClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
        	createDialogWithoutDateField().show();
        	SnapCommonUtils.hideSoftKeyboard(getActivity(),v.getWindowToken());
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        totalDueValue = 0;
        snapbizzDB = SnapbizzDB.getInstance(getActivity(), false);
        printerType = SnapSharedUtils.getSavedPrinterType(getActivity());
        customerListView = (ListView) getView().findViewById(R.id.customers_listview);
        getView().findViewById(R.id.payment_button).setOnClickListener(onPaymentClickListener);
        getView().findViewById(R.id.bills_button).setOnClickListener(onBillsClickListener);
        ActionBar actionBar = getActivity().getActionBar();
        if (!actionBar.isShowing())
            actionBar.show();
        setHasOptionsMenu(true);
        actionBar.setCustomView(R.layout.actionbar_layout);
        ((TextView)getActivity().findViewById(R.id.actionbar_header)).setText(getString(R.string.header_customer_management));
        if (customerListAdapter != null)
        	customerListView.setAdapter(customerListAdapter);
        customerListView.setOnItemClickListener(onCustomerItemClickListener);
        getView().findViewById(R.id.edit_customer_button).setOnClickListener(onCustomerSaveEditClickListener);
        getView().findViewById(R.id.save_customer_button).setOnClickListener(onCustomerSaveEditClickListener);
        getView().findViewById(R.id.cancel_edit_button).setOnClickListener(onCustomerSaveEditClickListener);
        getView().findViewById(R.id.new_save_customer_button).setOnClickListener(onCustomerSaveEditClickListener);
        getView().findViewById(R.id.new_cancel_edit_button).setOnClickListener(onCustomerSaveEditClickListener);
        getView().findViewById(R.id.llcustomerdue).setOnClickListener(onCustomerSortClickListener);
        getView().findViewById(R.id.llcustomername).setOnClickListener(onCustomerSortClickListener);
        getView().findViewById(R.id.remind_customer_button).setOnClickListener(onCustomerRemindListener);

        /*** Customer management layout invisible for summary tab */
        customerPaymentListView=(ListView) getView().findViewById(R.id.customer_payments_listview);
        footerView = View.inflate(getActivity(), R.layout.listitem_customer_footer, null);
        //customerPaymentListView.addFooterView(footerView);
        
        dateFormat = new SimpleDateFormat(SnapBillingConstants.STANDARD_DATEFORMAT, Locale.getDefault());
        month = Calendar.getInstance().get(Calendar.MONTH);
        year = Calendar.getInstance().get(Calendar.YEAR);
        summaryTab.setOnClickListener(onTabClickListener);
        historyTab.setOnClickListener(onTabClickListener);
        loadAllCustomers();
        customerSearchEditText.addTextChangedListener(customerSearchTextWatcher);
        if (customerPaymentAdapter != null) {
            ((ListView)getView().findViewById(R.id.customer_payments_listview)).setAdapter(customerPaymentAdapter);
            customerPaymentAdapter.clear();
        }
        ((RelativeLayout) getActivity()
                .findViewById(R.id.monthOptionTextLayout))
                .setOnClickListener(showListClickListener);
        tvCurrentMonthText.setText(Calendar.getInstance()
        		.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) +" " + Calendar.getInstance().get(Calendar.YEAR));
       
    }

    @Override
    public void onStop() {
        super.onStop();
        customerSearchEditText.removeTextChangedListener(customerSearchTextWatcher);
        customerSearchEditText.setText("");
        customerSearchKeyStrokeTimer.cancel();
    };

    CountDownTimer customerSearchKeyStrokeTimer = new CountDownTimer(SnapBillingConstants.KEY_STROKE_TIMEOUT, SnapBillingConstants.KEY_STROKE_TIMEOUT) {
        @Override
        public void onTick(long arg0) {
        }

        @Override
        public void onFinish() {
            if (customerListAdapter != null) {
                customerListAdapter.clear();
                String searchString = customerSearchEditText.getText().toString().toLowerCase();
                totalDueValue = 0;
                for (int i = 0; i < customerList.size(); i++) {
                    Customers customer = customerList.get(i);
                    if (customer.getName().toLowerCase().contains(searchString)
                    		|| String.valueOf(customer.getPhone()).contains(searchString)
                    		|| customer.getAddress().contains(searchString)) {
                        customerListAdapter.add(customer);
                        totalDueValue += snapbizzDB.getCustomerDuePhoneNo(customer.getPhone());
                    }
                }
                if (customerListAdapter.getCount() > Integer.parseInt((SnapToolkitConstants.CUST_LIST_SELECTED_POS))){
                    customerListAdapter.setLastSelectedItemPosition(Integer.parseInt((SnapToolkitConstants.CUST_LIST_SELECTED_POS)));
                }
                else{
                    customerListAdapter.setLastSelectedItemPosition(0);
                }
                customerListAdapter.notifyDataSetChanged();
                getView().findViewById(R.id.customer_infoedit_relativelayout).setVisibility(View.INVISIBLE);
                getView().findViewById(R.id.customer_info_relativelayout).setVisibility(View.VISIBLE);
                if (customerListAdapter.getCount() > 0) {
                    updateCustomerTextViews(customerListAdapter.getLastSelectedCustomer());
                    if (customerDetailsView.getVisibility() != View.VISIBLE)
                        customerDetailsView.setVisibility(View.VISIBLE);
                    newCustomerLayout.setVisibility(View.GONE);
                } else {
                    if (customerDetailsView.getVisibility() != View.INVISIBLE)
                    	customerDetailsView.setVisibility(View.INVISIBLE);
                    newCustomerLayout.setVisibility(View.VISIBLE);
                    newCustomerLayout.setBackground(getResources().getDrawable(R.drawable.customer_edit_layout_border));
                    ((SoftInputEditText) getActivity().findViewById(R.id.new_customer_name_edittext)).setText("");
                    ((SoftInputEditText) getActivity().findViewById(R.id.new_customer_number_edittext)).setText("");
                    ((SoftInputEditText) getActivity().findViewById(R.id.new_customer_address_edittext)).setText("");
                }
                if (customerPaymentAdapter != null)
                	((TextView)getView().findViewById(R.id.total_customers_value_textview)).setText(SnapBillingTextFormatter.formatNumberText(customerListAdapter.getCount()));
                ((TextView)getView().findViewById(R.id.total_credit_value_textview)).setText(SnapBillingTextFormatter.formatPriceText(totalDueValue, getActivity()));
            }
        }
    };

    TextWatcher customerSearchTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            customerSearchKeyStrokeTimer.cancel();
            customerSearchKeyStrokeTimer.start();

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                int arg3) {

        }

        @Override
        public void afterTextChanged(Editable arg0) {

        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onActionbarNavigationListener = (OnActionbarNavigationListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()+" must implement OnActionbarNavigation Listener");
        }
    }

    View.OnClickListener onPaymentClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
            if (paymentEditText.getText().toString().equals(".")) {
                CustomToast.showCustomToast(getActivity(),getString(R.string.msg_invalid_amount), Toast.LENGTH_SHORT,
                        CustomToast.ERROR);
                return;
            } else {
            	amount = (Integer.parseInt(paymentEditText.getText().toString()));
            	updateTranscation();
            	long phone = customerListAdapter.getLastSelectedCustomer().getPhone(); 
            	int amountDue = (snapbizzDB.getCustomerDuePhoneNo(phone) - Integer.parseInt(paymentEditText.getText().toString()));
            	int purchaseValue = 0;
            	int amountSaved = 0;
            	Integer lastPurchaseAmount = 0;
            	Integer lastPaymentAmount = Integer.parseInt(paymentEditText.getText().toString());
            	Integer avgPurchasePerVisit = 0;
            	Float avgVisitsPerMonth = 0f;
            	Date lastPurchaseDate = null;
            	Date lastPaymentDate = new Date();
            	snapbizzDB.updateCustomerDetails(phone, amountDue, purchaseValue, amountSaved, lastPurchaseAmount, 
            			lastPaymentAmount, avgPurchasePerVisit, avgVisitsPerMonth, lastPurchaseDate, lastPaymentDate);
            	paymentEditText.setText("");
            	SnapCommonUtils.hideSoftKeyboard(getActivity(), paymentEditText.getWindowToken());
            	customerListAdapter.notifyDataSetChanged();
            	updateCustomerTextViews(customerListAdapter.getLastSelectedCustomer());
            	updateCustomerSummaryTextViews(snapbizzDB.getCustomerDetailsByPhoneNo(phone));
            }
        }
    };
    
    View.OnClickListener onCustomerSaveEditClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.edit_customer_button) {
                getView().findViewById(R.id.customer_info_relativelayout).setVisibility(View.INVISIBLE);
                getView().findViewById(R.id.customer_infoedit_relativelayout).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.customer_infoedit_relativelayout).setBackground(getResources().getDrawable(R.drawable.customer_edit_layout_border));
                Customers selectedCustomer = customerListAdapter.getLastSelectedCustomer();
                ((SoftInputEditText) getActivity()
                        .findViewById(R.id.customer_name_edittext)).setText(selectedCustomer.getName());
                ((SoftInputEditText) getActivity()
                        .findViewById(R.id.customer_number_edittext)).setText(String.valueOf(selectedCustomer.getPhone()));
                ((SoftInputEditText) getActivity()
                        .findViewById(R.id.customer_address_edittext)).setText(selectedCustomer.getAddress());
            } else if (view.getId() == R.id.save_customer_button) {
            	GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), view, TAG);
                String customerName = ((SoftInputEditText) getActivity()
                        .findViewById(R.id.customer_name_edittext)).getText()
                        .toString();
                String customerNumber = ((SoftInputEditText) getActivity()
                        .findViewById(R.id.customer_number_edittext))
                        .getText().toString();
                String customerAddress = ((SoftInputEditText) getActivity()
                        .findViewById(R.id.customer_address_edittext))
                        .getText().toString();

                if (!ValidationUtils.validateMobileNo(customerNumber)) {
                    CustomToast.showCustomToast(getActivity(), getString(R.string.msg_validnumber), Toast.LENGTH_SHORT, CustomToast.WARNING);
                } else if (customerName.length() != 0 && !ValidationUtils.validateName(customerName)) {
                    CustomToast.showCustomToast(getActivity(), getString(R.string.msg_validname), Toast.LENGTH_SHORT, CustomToast.WARNING);
                } else {
                    Customers editCustomer = customerListAdapter.getLastSelectedCustomer();
                    editCustomer.setName(customerName);
                    editCustomer.setPhone(Long.parseLong(customerNumber));
                    editCustomer.setAddress(customerAddress);
                    editCustomer.setCreditLimit(0);
                    snapbizzDB.updateCustomerByPhoneNo(Long.parseLong(customerNumber),
							customerName, customerAddress, null, 0);
                    SnapCommonUtils.hideSoftKeyboard(getActivity(), view.getWindowToken());
                    getView().findViewById(R.id.customer_info_relativelayout).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.customer_infoedit_relativelayout).setVisibility(View.INVISIBLE);
                    updateCustomerTextViews(editCustomer);
                    customerListAdapter.notifyDataSetChanged();
                }
            } else if (view.getId() == R.id.cancel_edit_button) {
                getView().findViewById(R.id.customer_info_relativelayout).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.customer_infoedit_relativelayout).setVisibility(View.INVISIBLE);
                SnapCommonUtils.hideSoftKeyboard(getActivity(), view.getWindowToken());
            } else if (view.getId() == R.id.new_save_customer_button) {
            	GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), view, getClass().getName());
                String customerName = ((SoftInputEditText) getActivity()
                        .findViewById(R.id.new_customer_name_edittext)).getText()
                        .toString();
                String customerNumber = ((SoftInputEditText) getActivity()
                        .findViewById(R.id.new_customer_number_edittext))
                        .getText().toString();
                String customerAddress = ((SoftInputEditText) getActivity()
                        .findViewById(R.id.new_customer_address_edittext))
                        .getText().toString();
                if (!ValidationUtils.validateMobileNo(customerNumber)) {
                    CustomToast.showCustomToast(getActivity(), getString(R.string.msg_validnumber), Toast.LENGTH_SHORT, CustomToast.WARNING);
                } else if (customerName.length() != 0 && !ValidationUtils.validateName(customerName)) {
                    CustomToast.showCustomToast(getActivity(), getString(R.string.msg_validname), Toast.LENGTH_SHORT, CustomToast.WARNING);
                } else if (snapbizzDB.getCustomerByPhoneNo(Long.parseLong(customerNumber)).size() > 0) {
                    CustomToast.showCustomToast(getActivity(), getString(R.string.exc_customer_exists), Toast.LENGTH_SHORT, CustomToast.WARNING);
                } else {
                	showAddCustomerLayout();
                	SnapCommonUtils.hideSoftKeyboard(getActivity(), view.getWindowToken());
                	snapbizzDB.insertCustomer(new Customers(Long.parseLong(customerNumber),
							customerName, customerAddress, null, 0, false, new Date(), new Date()));
                	snapbizzDB.insertCustomerDetails(new CustomerDetails(Long.parseLong(customerNumber),
                			0, 0, 0, 0, 0, 0, 0f, null, null, new Date(), new Date()));
                	loadAllCustomers();
                	SnapCommonUtils.hideSoftKeyboard(getActivity(), view.getWindowToken());
                    CustomToast.showCustomToast(getActivity(), getString(R.string.customer_created), Toast.LENGTH_SHORT, CustomToast.SUCCESS);
                }
            } else if (view.getId() == R.id.new_cancel_edit_button) {
            	showAddCustomerLayout();
            	SnapCommonUtils.hideSoftKeyboard(getActivity(), view.getWindowToken());
            }
        }
    };

    View.OnClickListener onTabClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            if (v.getId() == R.id.summary_tab) {
            	GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
                historyTab.setEnabled(true);
                summaryLayout.setVisibility(View.VISIBLE);
                historyLayout.setVisibility(View.GONE);
                getView().findViewById(R.id.print_bill_button).setVisibility(View.GONE);
            } else if (v.getId() == R.id.history_tab) {
            	GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
                if (customerListAdapter != null) {
                    historyTab.setEnabled(false);
                    getMonthSelected(month, year);
                    summaryTab.setEnabled(true);
                    historyLayout.setVisibility(View.VISIBLE);
                    summaryLayout.setVisibility(View.GONE);
                    getView().findViewById(R.id.print_bill_button).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.print_bill_button)
                            .setOnClickListener(onPrintMsgMailClickListener);
                }
            }
        }
    };
    
    View.OnClickListener onCustomerRemindListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
        	new RemindCreditCustomerTask(getActivity(),
                    CustomerPaymentFragment.this,
                    REMIND_CUSTOMER_CREDIT_TASKCODE,
                    customerListAdapter.getLastSelectedCustomer().getName(),
                    snapbizzDB.getCustomerDuePhoneNo(customerListAdapter.getLastSelectedCustomer().getPhone()),
                    String.valueOf(customerListAdapter.getLastSelectedCustomer().getPhone())).execute();
        }
    };
    
    AdapterView.OnItemClickListener onPrinterSelectListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view,
                int position, long id) {
            printerType = PrinterType
                    .getPrinterEnum(SnapBillingConstants.PRINTER_TYPES[position]);
            SnapSharedUtils.savePrinter(printerType, getActivity());
            printerManager = PrinterFactory.createPrinterManager(printerType,getActivity());
            printerTypeDialog.dismiss();
            printerManager.connect();
        }
    };
    
    View.OnClickListener onPrintMsgMailClickListener = new View.OnClickListener() {

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
                    Calendar calendar = Calendar.getInstance();
                    try {
                        date = new SimpleDateFormat(
                                SnapToolkitConstants.STANDARD_DATEFORMAT,
                                Locale.getDefault()).parse(dateFormat
                                .format(calendar.getTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        date = Calendar.getInstance().getTime();
                    }
                    Customers customer = customerListAdapter.getLastSelectedCustomer();
                    float amountDue = snapbizzDB.getCustomerDuePhoneNo(customerListAdapter.getLastSelectedCustomer().getPhone());
                    if (printerManager.isPrinterConnected()) {
                        CustomerCreditPayment customerCreditPayments = new CustomerCreditPayment();
                        customerCreditPayments.setCreditDueList(customerCreditPayment);
//                        customerCreditPayments.setCustomers(customer);
                        customerCreditPayments.getCustomer().setAmountDue(amountDue);
                        customerCreditPayments.setOpeningBalance(creditbalance);
                        if (customerCreditPayment != null&& !customerCreditPayment.isEmpty()) {
                            if (!printerManager.printCustomerCreditPayment(customerCreditPayments, date))
                                printerManager.connect();
                            GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
                        } else {
                            CustomToast.showCustomToast(getActivity(),getString(R.string.msg_nobills), Toast.LENGTH_LONG,
                                    CustomToast.ERROR);
                        }
                    }
                }
            }
        }
    };

    View.OnClickListener onBillsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
            onActionbarNavigationListener.onActionbarNavigation(getString(R.string.customerpaymentfragment_bills_tag) + ":"
            		+ customerListAdapter.getLastSelectedCustomer().getPhone(), R.id.billhistory_menuitem);
        }
    };

    // TODO: Check this
    public void updateTranscation() {
    	List<Invoices> invoices = snapbizzDB.invoiceHelper.getCustomerCreditBillByPhone(customerListAdapter
    													  .getLastSelectedCustomer().getPhone());
    	Long invoiceId;
    	InvoiceHelper.PaymentType paymentType;
    	InvoiceHelper.PaymentMode paymentMode;
    	String paymentReference = null;
    	int remainingAmount;
    	Long customerPhone = (customerListAdapter.getLastSelectedCustomer().getPhone());
    	Long parentTransactionId = 0L;
    	Transactions customerPayment;
    	if (invoices != null && !invoices.isEmpty()) {
    		invoiceId = invoices.get(0).getId();
        	remainingAmount = amount;
        	if ((amount > invoices.get(0).getPendingAmount()) && (invoices.get(0).getPendingAmount() != 0)) {
        		paymentType = InvoiceHelper.PaymentType.CREDIT;
        		paymentMode = InvoiceHelper.PaymentMode.CASH;
        		remainingAmount = 0;
        	} else {
        		paymentType = InvoiceHelper.PaymentType.ADVANCE;
        		paymentMode = InvoiceHelper.PaymentMode.CASH;
        		remainingAmount = amount;
        	}
        	
        	customerPayment = new Transactions(null, invoiceId, paymentType.name(),
        			paymentMode.name(), paymentReference, amount, remainingAmount,
        			customerPhone, parentTransactionId, false, new Date(), new Date());
        	if (amount > invoices.get(0).getPendingAmount()) {
        		snapbizzDB.invoiceHelper.insertTransaction(customerPayment);
        		amount = amount - invoices.get(0).getPendingAmount();
        		updateTranscation();
			} else {
				snapbizzDB.invoiceHelper.insertTransaction(customerPayment);
			}
    	} else {
    		paymentType = InvoiceHelper.PaymentType.ADVANCE;
    		paymentMode = InvoiceHelper.PaymentMode.CASH;
    		remainingAmount = amount;
    		customerPayment = new Transactions(null, 0L, paymentType.name(),
        			paymentMode.name(), paymentReference, amount, remainingAmount,
        			customerPhone, parentTransactionId, false, new Date(), new Date());
			snapbizzDB.invoiceHelper.insertTransaction(customerPayment);
		}
    }
    
    public void updateCustomerTextViews(Customers selectedCustomer) {
    	remindCustomerButtonDisplay(snapbizzDB.getCustomerDuePhoneNo(customerListAdapter.getLastSelectedCustomer().getPhone()));
        customerNameTextView.setText(selectedCustomer.getName());
        customerNumberTextView.setText(String.valueOf(selectedCustomer.getPhone()));
        customerAddressTextView.setText(selectedCustomer.getAddress());
        customerDueTextView.setText(SnapBillingTextFormatter.formatPriceText(snapbizzDB.getCustomerDuePhoneNo(selectedCustomer.getPhone()), getActivity()));
    }
    
    public void updateCustomerSummaryTextViews(List<CustomerDetails> customerDetailsList) {
    	if (!customerDetailsList.isEmpty()) {
    		if (customerDetailsList.get(0).getLastPurchaseDate() != null) {
    			int secondsInDay = 86400000;
    			int days = (int) ((Calendar.getInstance().getTimeInMillis() - customerDetailsList.get(0).getLastPurchaseDate().getTime()) / secondsInDay);
    			customerLastPurchaseTextView.setText(SnapBillingTextFormatter.formatNumberText(days));
    		} else {
    			customerLastPurchaseTextView.setText(getString(R.string.msg_na));
    		}
    		if (customerDetailsList.get(0).getLastPaymentDate() != null) {
    			customerLastPaymentTextView.setText(getString(R.string.msg_last_paid) + new SimpleDateFormat(SnapBillingConstants.BILL_DATEFORMAT).format(customerDetailsList.get(0).getLastPaymentDate()));
    			customerLastPaymentAmountTextView.setText(SnapBillingTextFormatter.formatPriceText(customerDetailsList.get(0).getLastPaymentAmount(), getActivity()));
    		} else {
    			customerLastPaymentAmountTextView.setText(getString(R.string.msg_na));
    			customerLastPaymentTextView.setText(getString(R.string.msg_last_paid_na));
    		}
    		if (!historyTab.isEnabled()) {
    			getView().findViewById(R.id.print_bill_button).setSelected(false);
    			creditbalance = 0;
    			getMonthlyCreditReport(openingBalanceStartDate, openingBalanceEndDate);
    			getCreditBills(openingBalanceEndDate, transcationEndDate);
    		}
    		remindCustomerButtonDisplay(snapbizzDB.getCustomerDuePhoneNo(customerDetailsList.get(0).getPhone()));
			avgPurchaseTextView.setText(SnapBillingTextFormatter.formatPriceText(0, getActivity()));
			avgVisitsTextView.setText(SnapBillingTextFormatter.formatNumberText(0));
    		if (customerDetailsList.get(0).getAvgVisitsPerMonth() != null)
    			avgVisitsTextView.setText(SnapBillingTextFormatter.formatNumberText(customerDetailsList.get(0).getAvgVisitsPerMonth()));
    		if (customerDetailsList.get(0).getAvgPurchasePerVisit() != null)
    			avgPurchaseTextView.setText(SnapBillingTextFormatter.formatPriceText(customerDetailsList.get(0).getAvgPurchasePerVisit(), getActivity()));
    	} else {
    		customerLastPurchaseTextView.setText(getString(R.string.msg_na));
    		customerLastPaymentAmountTextView.setText(getString(R.string.msg_na));
			customerLastPaymentTextView.setText(getString(R.string.msg_last_paid_na));
			avgVisitsTextView.setText(SnapBillingTextFormatter.formatNumberText(0));
    		avgPurchaseTextView.setText(SnapBillingTextFormatter.formatPriceText(0, getActivity()));
		}
    }

    public void showAddCustomerLayout() {
    	customerDetailsView.setVisibility(View.VISIBLE);
    	customerSearchEditText.setText("");
    	getView().findViewById(R.id.customer_info_relativelayout).setVisibility(View.VISIBLE);
    	getView().findViewById(R.id.new_customer_relativelayout).setVisibility(View.GONE);
    	getView().findViewById(R.id.customer_infoedit_relativelayout).setVisibility(View.INVISIBLE);
	}
    
    public void loadAllCustomers() {
    	customerList = snapbizzDB.getAllCustomers();
    	totalDueValue = 0;
		if (customerListAdapter == null) {
			customerListAdapter = new CustomerListAdapter(getActivity(),
					R.layout.listitem_customer, new ArrayList<Customers>());
			customerListView.setAdapter(customerListAdapter);
		} else {
			customerListAdapter.clear();
		}
		customerListAdapter.addAll(customerList);
		customerListAdapter.notifyDataSetChanged();
		for (int i = 0; i < customerListAdapter.getCount(); i++)
			totalDueValue += snapbizzDB.getCustomerDuePhoneNo(customerListAdapter.getItem(i).getPhone());
		customerDetailsView.setVisibility(View.VISIBLE);
		if (customerListAdapter != null && customerListAdapter.getCount() > 0) {
			customerListAdapter.setLastSelectedItemPosition(0);
			updateCustomerTextViews(customerListAdapter.getItem(0));
			updateCustomerSummaryTextViews(snapbizzDB.getCustomerDetailsByPhoneNo(customerListAdapter.getItem(0).getPhone()));
		} else {
			paymentEditText.setVisibility(View.INVISIBLE);
			customerDetailsView.setVisibility(View.INVISIBLE);
		}
		((TextView) getView().findViewById(R.id.total_customers_value_textview))
				.setText(SnapBillingTextFormatter
						.formatNumberText(customerListAdapter.getCount()));
		((TextView) getView().findViewById(R.id.total_credit_value_textview))
				.setText(SnapBillingTextFormatter.formatPriceText(
						totalDueValue, getActivity()));
	}
   
    AdapterView.OnItemClickListener onCustomerItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position,
                long id) {
            if (position != customerListAdapter.getLastSelectedItemPosition()) {
                customerListAdapter.setLastSelectedItemPosition(position);
                customerListAdapter.notifyDataSetChanged();
                if (customerPaymentAdapter != null)
                    customerPaymentAdapter.clear();
                getView().findViewById(R.id.customer_info_relativelayout).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.customer_infoedit_relativelayout).setVisibility(View.INVISIBLE);
                updateCustomerTextViews(customerListAdapter.getItem(position));
        		updateCustomerSummaryTextViews(snapbizzDB.getCustomerDetailsByPhoneNo(customerListAdapter.getItem(position).getPhone()));
                if (!historyTab.isEnabled()) {
                    getView().findViewById(R.id.print_bill_button).setSelected(false);
                    creditbalance = 0;
                    getMonthlyCreditReport(openingBalanceStartDate, openingBalanceEndDate);
                    getCreditBills(openingBalanceEndDate, transcationEndDate);
                }
            }
            SnapToolkitConstants.CUST_LIST_SELECTED_POS = String.valueOf(position);
        }
    };
    
    @Override
    public void onTaskSuccess(Object responseList, int taskCode) {
        if (getView() == null || getActivity() == null)
            return;
        if (taskCode == REMIND_CUSTOMER_CREDIT_TASKCODE) {
        	CustomToast.showCustomToast(getActivity(), getActivity().getString(R.string.success_message),
        									Toast.LENGTH_SHORT, CustomToast.SUCCESS);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.customer_payment_menuitem).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onActionbarNavigationListener.onActionbarNavigation(getTag(), menuItem.getItemId());
        return true;
    }

    @Override
    public void onTaskError(String errorMessage, int taskCode) {
        if (getView() == null || getActivity() == null)
            return;
        CustomToast.showCustomToast(getActivity(), errorMessage, Toast.LENGTH_LONG, CustomToast.ERROR);
    }
    
    private void getMonthlyCreditReport(Date selectedStartDate, Date selectedEndDate) {
        ((TextView) getView().findViewById(R.id.opening_balance)).setText(getString(R.string.opening_balanace)
                + SnapBillingTextFormatter.formatPriceText(creditbalance, getActivity()));
        ((TextView) footerView.findViewById(R.id.footerCloseingBalance)).setVisibility(View.GONE);
    }
    
    private void getMonthSelected(int month, int year) {
    	
    	// Declaring the start date for the opening balance which is constant
    	Calendar monthending = Calendar.getInstance();
    	monthending.set(Calendar.DAY_OF_MONTH, 1);
    	monthending.set(Calendar.MONTH, 0);
    	monthending.set(Calendar.YEAR, 2014);
    	monthending.set(Calendar.HOUR_OF_DAY, 0);
    	monthending.set(Calendar.MINUTE, 0);
    	monthending.set(Calendar.SECOND, 0);
        openingBalanceStartDate = new Date(monthending.getTimeInMillis());
        Calendar selectedMonth = Calendar.getInstance();
        /*** Customer Credit End date for opening balance and start date for transaction Details*/
        selectedMonth.set(Calendar.MONTH, month);
        selectedMonth.set(Calendar.YEAR, year);
        selectedMonth.set(Calendar.DAY_OF_MONTH, 1);
        selectedMonth.set(Calendar.HOUR_OF_DAY, 0);
        selectedMonth.set(Calendar.SECOND, 0);
        selectedMonth.set(Calendar.MINUTE, 0);
        openingBalanceEndDate = new Date(selectedMonth.getTimeInMillis());
        getMonthlyCreditReport(openingBalanceStartDate, openingBalanceEndDate);
        /*** Customer Credit Start and end date for transaction Details */
        selectedMonth.set(Calendar.MONTH, month + 1);
        if (month == 11) {
            selectedMonth.set(Calendar.MONTH, 0);
            selectedMonth.set(Calendar.YEAR, year + 1);
        }
        transcationEndDate = new Date(selectedMonth.getTimeInMillis());
        if (!historyTab.isEnabled())
        	getCreditBills(openingBalanceEndDate, transcationEndDate);
    }

    private void getCreditBills(Date startDate, Date endDate) {
    	List<CustomerInvoiceTranscationDetails> invoicesList = CustomerInvoicesHelper.addInvoiceToInvoiceTranscationDetails(getActivity() ,getInvoiceDetails(startDate, endDate));
    	List<CustomerInvoiceTranscationDetails> transactionsList = CustomerInvoicesHelper.addTranscationToInvoiceTranscationDetails(getTransactionsDetails(startDate, endDate));
    	invoicesList.addAll(transactionsList);
    	Collections.sort(invoicesList, new SortAccoridngDate());
    	float  fOpeningBalance = 0;
    	float  fClosingBalance=0;
    	if (invoicesList.size() > 0) {
    		fOpeningBalance = creditbalance;
    		for (int i = 0; i < invoicesList.size(); i++) {
    			CustomerInvoiceTranscationDetails invoice = invoicesList.get(i);
    			fOpeningBalance = (float) (fOpeningBalance + (invoice.payment));
    		}
    		fClosingBalance += fOpeningBalance;
    	}
    	int totalCredit = 0;
    	((TextView) footerView.findViewById(R.id.footerCloseingBalance)).setText(getString(R.string.closing_balanace) + SnapBillingTextFormatter.formatPriceText(fClosingBalance, getActivity()));
    	customerPaymentListView.setVisibility(View.GONE);
    	if (invoicesList != null && !invoicesList.isEmpty()) {
    		for (int i = 0; invoicesList != null && i < invoicesList.size(); i++) {
        		totalCredit += invoicesList.get(i).billAmount - invoicesList.get(i).payment;
        		invoicesList.get(i).totalCredit = totalCredit;
        	}
    		((TextView) footerView.findViewById(R.id.footerCloseingBalance)).setVisibility(View.VISIBLE);
    		customerPaymentListView.setVisibility(View.VISIBLE);
    		if (customerPaymentAdapter == null) {
    			customerPaymentAdapter = new CustomerPaymentHistoryAdapter(
    					getActivity(), invoicesList,
    					CustomerPaymentFragment.this, creditbalance);
    			((ListView) getView().findViewById(R.id.customer_payments_listview)).setAdapter(customerPaymentAdapter);
    		} else {
    			customerPaymentAdapter.clear();
    			customerPaymentAdapter = new CustomerPaymentHistoryAdapter(
    					getActivity(), invoicesList,
    					CustomerPaymentFragment.this, creditbalance);
    			((ListView) getView().findViewById(R.id.customer_payments_listview)).setAdapter(customerPaymentAdapter);
    			customerPaymentAdapter.notifyDataSetChanged();
    		}
    	} else if (customerPaymentAdapter != null) {
    		customerPaymentAdapter.clear();
    		customerPaymentAdapter.notifyDataSetChanged();
    	}
    }
    
    private List<Invoices> getInvoiceDetails(Date startDate, Date endDate) {
		return snapbizzDB.invoiceHelper.getCustomerInvoicesByPhone(customerListAdapter.getLastSelectedCustomer().getPhone(), startDate, endDate);
	}
    
    private List<Transactions> getTransactionsDetails(Date startDate, Date endDate) {
		return snapbizzDB.invoiceHelper.getCustomerTransactionsByPhone(customerListAdapter.getLastSelectedCustomer().getPhone(), startDate, endDate);
    }
    
    @Override
    public void onCustomerPaymentEdit(CustomerInvoiceTranscationDetails customerPayment) {
        if (paymentDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.msg_enter_payment));
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_edit_payment, null);
            view.findViewById(R.id.positiveButton).setOnClickListener(onPaymentDialogClickListener);
            view.findViewById(R.id.positiveButton).setTag(view.findViewById(R.id.payment_edittext));
            view.findViewById(R.id.negativeButton).setOnClickListener(onPaymentDialogClickListener);
            builder.setView(view);
            paymentDialog = builder.create();
        }
        paymentDialog.show();
        ((EditText)paymentDialog.findViewById(R.id.payment_edittext)).setText("");
    }

    View.OnClickListener onPaymentDialogClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.positiveButton) {
                customerPaymentAdapter.notifyDataSetChanged();
            }
            SnapCommonUtils.hideSoftKeyboard(getActivity(), v.getWindowToken());
            paymentDialog.dismiss();
        }
    };
    
    private Dialog createDialogWithoutDateField() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), datePickerListener, year, month, 1);
        try {
            Field[] datePickerDialogFields = datePickerDialog.getClass().getDeclaredFields();
            for (Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals(getString(R.string.datepicker_dialog_spinner))) {
                    datePickerDialogField.setAccessible(true);
                    datePicker = (DatePicker) datePickerDialogField.get(datePickerDialog);
                    Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (Field datePickerField : datePickerFields) {
                        if (getString(R.string.datepicker_spinner).equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
        }
		datePickerDialog.getDatePicker().setMinDate(openingBalanceStartDate.getTime());
		datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
		datePickerDialog.getDatePicker().setCalendarViewShown(false);
		datePickerDialog.setTitle(getString(R.string.msg_select_month));
        return datePickerDialog;
    }
    
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
    	public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
    		SnapCommonUtils.hideSoftKeyboard(getActivity(), view.getWindowToken());
    		year = selectedYear;
    		month = selectedMonth;
    		getMonthSelected(selectedMonth, selectedYear);
    		tvCurrentMonthText.setText(new DateFormatSymbols().getMonths()[selectedMonth] + " " + selectedYear);
    		datePicker.init(selectedYear, selectedMonth, 1, null);
    	}
    };
    
    public void doCustomerSort(boolean isSortDue) {
    	List<Customers> sortedCustomers = new ArrayList<Customers>();
    	sortedCustomers = snapbizzDB.sortCustomer(isSortDue);
    	totalDueValue = 0;
        if (customerListAdapter == null) {
            customerListAdapter = new CustomerListAdapter(getActivity(), R.layout.listitem_customer, new ArrayList<Customers>());
            customerListView.setAdapter(customerListAdapter);
        } else {
            customerListAdapter.clear();
        }
        customerListAdapter.addAll(sortedCustomers);
        customerListAdapter.notifyDataSetChanged();
        for (int  i = 0; i < customerListAdapter.getCount(); i++) {
            totalDueValue += snapbizzDB.getCustomerDuePhoneNo(customerListAdapter.getItem(i).getPhone());
        }
        customerDetailsView.setVisibility(View.VISIBLE);
        if (customerListAdapter != null && customerListAdapter.getCount() > 0) {
        	customerListAdapter.setLastSelectedItemPosition(0);
        	updateCustomerTextViews(customerListAdapter.getItem(0));
        }
        ((TextView)getView().findViewById(R.id.total_customers_value_textview)).setText(SnapBillingTextFormatter.formatNumberText(customerListAdapter.getCount()));
        ((TextView)getView().findViewById(R.id.total_credit_value_textview)).setText(SnapBillingTextFormatter.formatPriceText(totalDueValue, getActivity()));
    }

    private void remindCustomerButtonDisplay(float customerDue) {
    	if (customerDue > 0)
    		getView().findViewById(R.id.remind_customer_button).setVisibility(View.VISIBLE);
    	else
    		getView().findViewById(R.id.remind_customer_button).setVisibility(View.GONE);
    };
    
	View.OnClickListener onCustomerSortClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			getView().findViewById(R.id.customer_infoedit_relativelayout).setVisibility(View.INVISIBLE);
			getView().findViewById(R.id.customer_info_relativelayout).setVisibility(View.VISIBLE);
			switch (view.getId()) {
			case R.id.llcustomerdue:
					doCustomerSort(true);
					GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), view.getId(), TAG);
				break;
			case R.id.llcustomername:
					doCustomerSort(false);
					GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), view.getId(), TAG);
				break;
			}
		}
	};
}

class SortAccoridngDate implements Comparator<CustomerInvoiceTranscationDetails> {
	 
    @Override
    public int compare(CustomerInvoiceTranscationDetails InvoiceTranscationDetails1, CustomerInvoiceTranscationDetails InvoiceTranscationDetails2) {
        if (InvoiceTranscationDetails1.invoiceOrTranscationDate.getTime() > InvoiceTranscationDetails2.invoiceOrTranscationDate.getTime())
            return 1;
        else
            return -1;
    }
}
