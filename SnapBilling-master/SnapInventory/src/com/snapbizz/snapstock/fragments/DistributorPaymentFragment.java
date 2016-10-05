package com.snapbizz.snapstock.fragments;

import java.text.DateFormat;
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
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.adapters.DistributorPaymentHistory;
import com.snapbizz.snapstock.adapters.DistributorPaymentHistory.OnDistributorPaymentEditListener;
import com.snapbizz.snapstock.adapters.DistributorPaymentListAdapter;
import com.snapbizz.snapstock.asynctasks.AddEditDistributorTask;
import com.snapbizz.snapstock.asynctasks.QueryDistributorInfoTask;
import com.snapbizz.snapstock.utils.SnapBillingTextFormatter;
import com.snapbizz.snapstock.utils.SnapInventoryConstants;
import com.snapbizz.snaptoolkit.customviews.SoftInputEditText;
import com.snapbizz.snaptoolkit.domains.CustomerCreditPayment;
import com.snapbizz.snaptoolkit.domains.CustomerPayment;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.DistributorPayment;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.ValidationUtils;

public class DistributorPaymentFragment extends Fragment implements OnQueryCompleteListener,OnDistributorPaymentEditListener {

	
	private DistributorPaymentListAdapter distributorPaymentListAdapter;
    private int GET_DISTRIBUTOR_TASKCODE = 0;
    private int CREATE_DISTRIBUTORPAYMENT_TASKCODE = 1;
    private int EDIT_DISTRIBUTOR_TASKCODE = 2;
    private int GET_DISTRIBUTORPAYMENT_TASKCODE = 3;
    private int EDIT_DISTRIBUTORPAYMENT_TASKCODE = 4;
    private int FILTER_DATE_TASK_CODE = 5;
    private int FILTER_CREDIT_TASK_CODE = 6;
    private ListView customerListView;
    private OnActionbarNavigationListener onActionbarNavigationListener;
    private TextView distributorNameTextView;
    private TextView distributorAddressTextView;
    private TextView distributorNumberTextView;
    private TextView distributorDueTextView;
    private TextView distributorLastPurchaseTextView;
    private TextView distributorLastPaymentTextView;
    private TextView distributorLastPaymentAmountTextView;
    private TextView avgVisitsTextView;
    private TextView currentMonthText;
    private TextView avgPurchaseTextView;
    private View historyLayout;
    private View summaryLayout;
    private View historyTab;
    private View summaryTab;
    private EditText distributorSearchEditText;
    private EditText paymentEditText;
    private double totalDueValue = 0;
    private List<Distributor> distributorList;
    private DistributorPaymentHistory distributorPaymentAdapter;
    private Dialog paymentDialog;
    private Date now;
    private DateFormat dateFormat;
    private Date startDate;
	private Date endDate;
    private String monthPos = "";
    private DistributorPayment editCustomerPayment;
    private View customerDetailsView;
    int index,top;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
    	now = new Date();
        View view = inflater.inflate(R.layout.fragment_distributor_payment, null);
        distributorNameTextView = (TextView) view.findViewById(R.id.customer_name_textview);
        distributorNumberTextView = (TextView) view.findViewById(R.id.customer_number_textview);
        distributorAddressTextView = (TextView) view.findViewById(R.id.customer_address_textview);
        distributorDueTextView = (TextView) view.findViewById(R.id.customer_credit_textview);
        distributorLastPurchaseTextView = (TextView) view.findViewById(R.id.lastpurchasedate_textview);
        distributorLastPaymentTextView = (TextView) view.findViewById(R.id.lastpaymentdate_textview);
        distributorLastPaymentAmountTextView = (TextView) view.findViewById(R.id.lastpaymentdate_amount_textview);
        avgVisitsTextView = (TextView) view.findViewById(R.id.avg_visits_textview);
        avgPurchaseTextView = (TextView) view.findViewById(R.id.avg_purchase_textview);
        currentMonthText = (TextView) view.findViewById(R.id.current_month);
        historyLayout = view.findViewById(R.id.history_layout);
        summaryLayout = view.findViewById(R.id.summary_layout);
        historyTab = view.findViewById(R.id.history_tab);
        summaryTab = view.findViewById(R.id.summary_tab);
        distributorSearchEditText = (EditText) view.findViewById(R.id.customer_search_edittext);
        paymentEditText = (EditText) view.findViewById(R.id.payment_edittext);
        customerDetailsView = view.findViewById(R.id.customer_detail_layout);
        return view;
    }

    OnClickListener showListClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				PopupMenu popup = new PopupMenu(getActivity(), v);
				popup.getMenuInflater().inflate(R.menu.month, popup.getMenu());
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
					currentMonthText.setText(item.toString());
						switch (item.getItemId()) {
						case R.id.jan_menuitem:
							monthPos = "01";
							String prvYr = "";
							if (!(((Integer.parseInt(new SimpleDateFormat("MM")
									.format(now)) - 01) + "").length() > 1)) {
								prvYr = "0"
										+ (Integer
												.parseInt(new SimpleDateFormat(
														"yyyy").format(now)) - 01);
							} else {
								prvYr = ""
										+ (Integer
												.parseInt(new SimpleDateFormat(
														"yyyy").format(now)) - 01);

							}
//								if (getArguments().getString("code") != null) {
//									new GetMonthlyOpeningBalanceReportTask(getActivity(), CustomerPaymentFragment.this,
//											FILTER_MONTH_TASK_CODE,
//											getArguments().getString("code"), ""
//													+ monthPos + "",
//											new SimpleDateFormat("yyyy")
//													.format(now)).execute();
//
//									new GetMonthlyOpeningBalanceReportTask(getActivity(), CustomerPaymentFragment.this,
//											FILTER_MONTH_TASK_CODE,
//											getArguments().getString("code"), ""
//													+ "12", prvYr).execute();
//								}
							
							return true;

						case R.id.feb_menuitem:
//							GetMonthlyCreditReportMethod("02", "01");
							return true;
						case R.id.march_menuitem:

//							GetMonthlyCreditReportMethod("03", "02");

							return true;
						case R.id.april_menuitem:
//							GetMonthlyCreditReportMethod("04", "03");

							return true;
						case R.id.may_menuitem:
//							GetMonthlyCreditReportMethod("05", "04");

							return true;
						case R.id.june_menuitem:
//							GetMonthlyCreditReportMethod("06", "05");

							return true;
						case R.id.july_menuitem:
//							GetMonthlyCreditReportMethod("07", "06");

							return true;
						case R.id.aug_menuitem:
//							GetMonthlyCreditReportMethod("08", "07");

							return true;
						case R.id.sept_menuitem:
//							GetMonthlyCreditReportMethod("09", "08");

							return true;
						case R.id.oct_menuitem:
//							GetMonthlyCreditReportMethod("10", "09");

							return true;
						case R.id.nov_menuitem:
//							GetMonthlyCreditReportMethod("11", "10");

							return true;
						case R.id.dec_menuitem:
//							GetMonthlyCreditReportMethod("12", "11");
							return true;

						default:
							return false;

						}

					}
				});

				popup.show();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

		}
	};

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        totalDueValue = 0;
        customerListView = (ListView) getView().findViewById(R.id.customers_listview);
        getView().findViewById(R.id.payment_button).setOnClickListener(onPaymentClickListener);
        getView().findViewById(R.id.bills_button).setOnClickListener(onBillsClickListener);
        ActionBar actionBar = getActivity().getActionBar();
        if(!actionBar.isShowing())
            actionBar.show();
        setHasOptionsMenu(true);
        actionBar.setCustomView(R.layout.actionbar_layout);
        ((TextView)getActivity().findViewById(R.id.actionbar_header)).setText("Distributor Management");
        if(distributorPaymentListAdapter != null)
        customerListView.setAdapter(distributorPaymentListAdapter);
        customerListView.setOnItemClickListener(onCustomerItemClickListener);
//        try {
//			customerListView.setSelection(Integer.parseInt((SnapToolkitConstants.CUST_LIST_SELECTED_POS)));
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//		}
        getView().findViewById(R.id.edit_customer_button).setOnClickListener(onCustomerSaveEditClickListener);
        getView().findViewById(R.id.save_customer_button).setOnClickListener(onCustomerSaveEditClickListener);
        getView().findViewById(R.id.cancel_edit_button).setOnClickListener(onCustomerSaveEditClickListener);
    	/** 
		 * Distributor management layout invisible for summary tab- mahesh
		 */
        getView().findViewById(R.id.payment_button).setVisibility(View.GONE);
        getView().findViewById(R.id.bills_button).setVisibility(View.GONE);
        paymentEditText.setVisibility(View.GONE);
        getView().findViewById(R.id.payments_textview).setVisibility(View.GONE);
//        dateFormat = new SimpleDateFormat(SnapBillingConstants.STANDARD_DATEFORMAT, Locale.getDefault());
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.HOUR_OF_DAY, 24);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
//		this.endDate = calendar.getTime();
//		final String endDate = dateFormat.format(calendar.getTime());
//		calendar.add(Calendar.DAY_OF_MONTH, -1);
//		this.startDate = calendar.getTime();
//		final String startDate = dateFormat.format(calendar.getTime());
        summaryTab.setOnClickListener(onTabClickListener);
        historyTab.setOnClickListener(onTabClickListener);
        new QueryDistributorInfoTask(getActivity(), this, GET_DISTRIBUTOR_TASKCODE).execute("");
        distributorSearchEditText.addTextChangedListener(customerSearchTextWatcher);
        if(distributorPaymentListAdapter != null) {
            ((ListView)getView().findViewById(R.id.customer_payments_listview)).setAdapter(distributorPaymentListAdapter);
            distributorPaymentListAdapter.clear();
        }
        ((RelativeLayout) getActivity().findViewById(R.id.monthOptionTextLayout)).setOnClickListener(showListClickListener);
    	currentMonthText.setText(Calendar
				.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG,
						Locale.ENGLISH));
       
    }

    @Override
    public void onStop() {
        super.onStop();
        distributorSearchEditText.removeTextChangedListener(customerSearchTextWatcher);
        distributorSearchEditText.setText("");
        customerSearchKeyStrokeTimer.cancel();
    };

    CountDownTimer customerSearchKeyStrokeTimer = new CountDownTimer(SnapInventoryConstants.KEY_STROKE_TIMEOUT, SnapInventoryConstants.KEY_STROKE_TIMEOUT) {
        @Override
        public void onTick(long arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            if(distributorPaymentListAdapter != null) {
            	distributorPaymentListAdapter.clear();
                String searchString = distributorSearchEditText.getText().toString().toLowerCase();
                totalDueValue = 0;
                for(int i = 0; i < distributorList.size(); i++) {
                	Distributor customer = distributorList.get(i);
                    if(customer.getAgencyName().toLowerCase().contains(searchString) || customer.getPhoneNumber().contains(searchString)) {
                    	distributorPaymentListAdapter.add(customer);
//                        totalDueValue += customer.getAmountDue();
                    }
                }
//                if(customerListAdapter.getCount() > 0)
//                    customerListAdapter.setLastSelectedItemPosition(Integer.parseInt((SnapToolkitConstants.CUST_LIST_SELECTED_POS)));
//                else
                    try {
                    	distributorPaymentListAdapter.setLastSelectedItemPosition(Integer.parseInt((SnapToolkitConstants.CUST_LIST_SELECTED_POS)));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    distributorPaymentListAdapter.notifyDataSetChanged();
                if(distributorPaymentListAdapter.getCount() > 0) {
                    updateDistributorTextViews(distributorPaymentListAdapter.getLastSelectedDistributor());
                    if(customerDetailsView.getVisibility() != View.VISIBLE)
                        customerDetailsView.setVisibility(View.VISIBLE);
                } else {
                    if(customerDetailsView.getVisibility() != View.INVISIBLE)
                        customerDetailsView.setVisibility(View.INVISIBLE);
                }
                if(distributorPaymentAdapter != null)
                	distributorPaymentAdapter.getCursor().requery();
                if(!historyTab.isEnabled())
//                    new GetPaymentHistoryTask(getActivity(), DistributorPaymentFragment.this, GET_DISTRIBUTORPAYMENT_TASKCODE).execute(distributorPaymentListAdapter.getLastSelectedDistributor());
                ((TextView)getView().findViewById(R.id.total_customers_value_textview)).setText(SnapBillingTextFormatter.formatNumberText(distributorPaymentListAdapter.getCount()));
                ((TextView)getView().findViewById(R.id.total_credit_value_textview)).setText(SnapBillingTextFormatter.formatPriceText(totalDueValue, getActivity()));
            }
        }
    };

    TextWatcher customerSearchTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            customerSearchKeyStrokeTimer.cancel();
            customerSearchKeyStrokeTimer.start();

        }

        @Override
        public void beforeTextChanged(CharSequence arOg0, int arg1, int arg2,
                int arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable arg0) {
            // TODO Auto-generated method stub

        }
    };

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
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
            // TODO Auto-generated method stub
            try {
                float paymentAmount = Float.parseFloat(paymentEditText.getText().toString());
                DistributorPayment customerPayment = new DistributorPayment(distributorPaymentListAdapter.getLastSelectedDistributor(), paymentAmount);
//                new CreateCustomerPaymentTask(getActivity(), CustomerPaymentFragment.this, CREATE_DISTRIBUTORPAYMENT_TASKCODE).execute(customerPayment);
                SnapCommonUtils.hideSoftKeyboard(getActivity(), v.getWindowToken());
            } catch(NumberFormatException e) {
                CustomToast.showCustomToast(getActivity(), "Invalid Amount", Toast.LENGTH_SHORT, CustomToast.ERROR);
            }
        }
    };

    View.OnClickListener onCustomerSaveEditClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            // TODO Auto-generated method stub
            if(view.getId() == R.id.edit_customer_button) {
                getView().findViewById(R.id.customer_info_relativelayout).setVisibility(View.INVISIBLE);
                getView().findViewById(R.id.customer_infoedit_relativelayout).setVisibility(View.VISIBLE);
                Distributor selectedCustomer = distributorPaymentListAdapter.getLastSelectedDistributor();
                ((SoftInputEditText) getActivity().findViewById(R.id.customer_name_edittext)).setText(selectedCustomer.getAgencyName());
                ((SoftInputEditText) getActivity().findViewById(R.id.customer_number_edittext)).setText(selectedCustomer.getPhoneNumber());
                ((SoftInputEditText) getActivity().findViewById(R.id.customer_address_edittext)).setText(selectedCustomer.getAddress());
            } else if(view.getId() == R.id.save_customer_button) {
                String customerName = ((SoftInputEditText) getActivity()
                        .findViewById(R.id.customer_name_edittext)).getText()
                        .toString();
                String customerNumber = ((SoftInputEditText) getActivity()
                        .findViewById(R.id.customer_number_edittext))
                        .getText().toString();
                String customerAddress = ((SoftInputEditText) getActivity()
                        .findViewById(R.id.customer_address_edittext))
                        .getText().toString();

                if(!ValidationUtils.validateMobileNo(customerNumber)) {
                    CustomToast.showCustomToast(getActivity(), "Please Enter Correct Number", Toast.LENGTH_SHORT, CustomToast.WARNING);
                } else if(customerName.length() != 0 && !ValidationUtils.validateName(customerName)) {
                    CustomToast.showCustomToast(getActivity(), "Please Enter Correct Name", Toast.LENGTH_SHORT, CustomToast.WARNING);
                } else {

                	Distributor editCustomer = distributorPaymentListAdapter.getLastSelectedDistributor();
                    editCustomer.setAgencyName(customerName);
                    editCustomer.setPhoneNumber(customerNumber);
                    editCustomer.setAddress(customerAddress);
                    new AddEditDistributorTask(getActivity(), DistributorPaymentFragment.this,EDIT_DISTRIBUTOR_TASKCODE, true).execute(editCustomer);
                    SnapCommonUtils.hideSoftKeyboard(getActivity(), view.getWindowToken());
                }
            } else if(view.getId() == R.id.cancel_edit_button) {
                getView().findViewById(R.id.customer_info_relativelayout).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.customer_infoedit_relativelayout).setVisibility(View.INVISIBLE);
                SnapCommonUtils.hideSoftKeyboard(getActivity(), view.getWindowToken());
            }
        }
    };
    /** 
	 * Customer management  payment layout invisible for summary tab- mahesh
	 */
    View.OnClickListener onTabClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            v.setEnabled(false);
            if(v.getId() == R.id.summary_tab) {
                historyTab.setEnabled(true);
                summaryLayout.setVisibility(View.VISIBLE);
                historyLayout.setVisibility(View.GONE);
                getView().findViewById(R.id.payment_button).setVisibility(View.GONE);
                getView().findViewById(R.id.bills_button).setVisibility(View.GONE);
                paymentEditText.setVisibility(View.GONE);
                getView().findViewById(R.id.payments_textview).setVisibility(View.GONE);
            } else if(v.getId() == R.id.history_tab) {
                if(distributorPaymentListAdapter != null) {
                    if(distributorPaymentAdapter == null || distributorPaymentAdapter.getCount() == 0) {
//                        new GetPaymentHistoryTask(getActivity(), DistributorPaymentFragment.this, GET_DISTRIBUTORPAYMENT_TASKCODE).execute(customerListAdapter.getLastSelectedCustomer());
                    }
                }
                summaryTab.setEnabled(true);
                historyLayout.setVisibility(View.VISIBLE);
                summaryLayout.setVisibility(View.GONE);
                getView().findViewById(R.id.bills_button).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.payment_button).setVisibility(View.VISIBLE);
                paymentEditText.setVisibility(View.VISIBLE);
                getView().findViewById(R.id.payments_textview).setVisibility(View.VISIBLE);
            }
        }
    };

    View.OnClickListener onBillsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
//            onActionbarNavigationListener.onActionbarNavigation(getString(R.string.customerpaymentfragment_bills_tag)+":"+customerListAdapter.getLastSelectedCustomer().getCustomerPhoneNumber(), R.id.billhistory_menuitem);
        }
    };

    public void updateDistributorTextViews(Distributor selectedCustomer) {
    	distributorNameTextView.setText(selectedCustomer.getAgencyName());
    	distributorNumberTextView.setText(selectedCustomer.getPhoneNumber());
    	distributorAddressTextView.setText(selectedCustomer.getAddress());
    	distributorDueTextView.setText(SnapBillingTextFormatter.formatPriceText(selectedCustomer.getCreditLimit(), getActivity()));
        if(selectedCustomer.getLastModifiedTimestamp() != null) {
            int days = (int)((Calendar.getInstance().getTimeInMillis() - selectedCustomer.getLastModifiedTimestamp().getTime()) / 86400000);
            distributorLastPurchaseTextView.setText(SnapBillingTextFormatter.formatNumberText(days));
        } else
        	distributorLastPurchaseTextView.setText("NA");

        if(selectedCustomer.getLastModifiedTimestamp() != null) {
        	distributorLastPaymentTextView.setText("Last Paid on "+new SimpleDateFormat(SnapInventoryConstants.DATE_FORMAT_DD_MM_YYYY).format(selectedCustomer.getLastModifiedTimestamp()));
            distributorLastPaymentAmountTextView.setText(SnapBillingTextFormatter.formatPriceText(selectedCustomer.getCreditLimit(), getActivity()));
        } else {
        	distributorLastPaymentAmountTextView.setText("NA");
            distributorLastPaymentTextView.setText("Last Paid on NA");
        }
//        avgVisitsTextView.setText(SnapBillingTextFormatter.formatNumberText((int)selectedCustomer.getAvgVisits()));
//        avgPurchaseTextView.setText(SnapBillingTextFormatter.formatPriceText(selectedCustomer.getAvgPurchase(), getActivity()));
    }

    AdapterView.OnItemClickListener onCustomerItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position,
                long id) {
        	
            // TODO Auto-generated method stub
            if(position != distributorPaymentListAdapter.getLastSelectedItemPosition()) {
            	distributorPaymentListAdapter.setLastSelectedItemPosition(position);
            	distributorPaymentListAdapter.notifyDataSetChanged();
                if(distributorPaymentAdapter != null)
                	distributorPaymentAdapter.getCursor().requery();
                getView().findViewById(R.id.customer_info_relativelayout).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.customer_infoedit_relativelayout).setVisibility(View.INVISIBLE);
                updateDistributorTextViews(distributorPaymentListAdapter.getItem(position));
//                if(!historyTab.isEnabled())
//                    new GetPaymentHistoryTask(getActivity(), DistributorPaymentFragment.this, GET_DISTRIBUTORPAYMENT_TASKCODE).execute(distributorPaymentListAdapter.getLastSelectedDistributor());
            }
            SnapToolkitConstants.CUST_LIST_SELECTED_POS=String.valueOf(position);
        }
    };

	@Override
    public void onTaskSuccess(Object responseList, int taskCode) {
        // TODO Auto-generated method stub
        if(getView() == null || getActivity() == null)
            return;
        if(taskCode == GET_DISTRIBUTOR_TASKCODE) {
        	distributorList = (List<Distributor>) responseList;
            if(distributorPaymentListAdapter == null) {
            	distributorPaymentListAdapter = new DistributorPaymentListAdapter(getActivity(), R.layout.listitem_distributor_payment, new ArrayList<Distributor>());
                customerListView.setAdapter(distributorPaymentListAdapter);
            } else {
            	distributorPaymentListAdapter.clear();
            }
            distributorPaymentListAdapter.addAll(distributorList);
//            customerListAdapter.setLastSelectedItemPosition(0);
            distributorPaymentListAdapter.notifyDataSetChanged();
            for(int  i = 0; i < distributorPaymentListAdapter.getCount(); i++) {
                totalDueValue += distributorPaymentListAdapter.getItem(i).getCreditLimit();
            }
            customerDetailsView.setVisibility(View.VISIBLE);
            ((TextView)getView().findViewById(R.id.total_customers_value_textview)).setText(SnapBillingTextFormatter.formatNumberText(distributorPaymentListAdapter.getCount()));
            ((TextView)getView().findViewById(R.id.total_credit_value_textview)).setText(SnapBillingTextFormatter.formatPriceText(totalDueValue, getActivity()));
            updateDistributorTextViews(distributorPaymentListAdapter.getItem(0));
        
        } else if(taskCode == CREATE_DISTRIBUTORPAYMENT_TASKCODE) {
            CustomerPayment customerPayment = (CustomerPayment) responseList;
            totalDueValue -= customerPayment.getPaymentAmount();
            paymentEditText.setText("");
            ((TextView)getView().findViewById(R.id.total_credit_value_textview)).setText(SnapBillingTextFormatter.formatPriceText(totalDueValue, getActivity()));
            distributorPaymentListAdapter.notifyDataSetChanged();
            updateDistributorTextViews(distributorPaymentListAdapter.getLastSelectedDistributor());
            if(distributorPaymentAdapter == null) {
//            	distributorPaymentAdapter = new DistributorPaymentHistory(getActivity(), new ArrayList<Distributor>(), DistributorPaymentFragment.this);
                ((ListView)getView().findViewById(R.id.customer_payments_listview)).setAdapter(distributorPaymentAdapter);
            }
//            customerPaymentAdapter.add(customerPayment);
            distributorPaymentAdapter.notifyDataSetChanged();
        } 
        else if(taskCode == EDIT_DISTRIBUTOR_TASKCODE) {
            getView().findViewById(R.id.customer_info_relativelayout).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.customer_infoedit_relativelayout).setVisibility(View.INVISIBLE);
            updateDistributorTextViews((Distributor) responseList);
        } else if(taskCode == GET_DISTRIBUTORPAYMENT_TASKCODE) {
            if(distributorPaymentAdapter == null) {
//            	distributorPaymentAdapter = new DistributorPaymentHistory(getActivity(), (List<Distributor>) responseList, DistributorPaymentFragment.this);
//                ((ListView)getView().findViewById(R.id.customer_payments_listview)).setAdapter(distributorPaymentAdapter);
            } else {
//            	distributorPaymentAdapter.clear();
//            	distributorPaymentAdapter.addAll((Collection<? extends CustomerPayment>) responseList);
            	distributorPaymentAdapter.getCursor().requery();
            	distributorPaymentAdapter.notifyDataSetChanged();
            }
            boolean isOnlyCredit = true;
            Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 24);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			DistributorPaymentFragment.this.endDate = calendar.getTime();
			String endDate = dateFormat.format(calendar.getTime());
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			DistributorPaymentFragment.this.startDate = calendar.getTime();
			String startDate = dateFormat.format(calendar.getTime());
//			currentFilterAsyncTask1 = new GetCustomerCreditBillsTask(getActivity(),CustomerPaymentFragment.this, startDate,endDate,isOnlyCredit, FILTER_CREDIT_TASK_CODE, currentTransactionType);
//			currentFilterAsyncTask1.execute(customerListAdapter.getLastSelectedCustomer().getCustomerPhoneNumber());  
			
        } else if(taskCode == EDIT_DISTRIBUTORPAYMENT_TASKCODE) {
            CustomerPayment customerPayment = (CustomerPayment) responseList;
            totalDueValue -= customerPayment.getPaymentAmount();
            Distributor customer = distributorPaymentListAdapter.getLastSelectedDistributor();
            paymentEditText.setText("");
            ((TextView)getView().findViewById(R.id.total_credit_value_textview)).setText(SnapBillingTextFormatter.formatPriceText(totalDueValue, getActivity()));
            updateDistributorTextViews(customer);
            distributorPaymentListAdapter.notifyDataSetChanged();
            distributorPaymentAdapter.notifyDataSetChanged();
        } else if(taskCode == FILTER_CREDIT_TASK_CODE) {
//        	ArrayList<CustomerCreditPayment> creditist = new ArrayList<CustomerCreditPayment>();
//        	creditist = (ArrayList<CustomerCreditPayment>) responseList;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        menu.findItem(R.id.distributor_payment_menuitem).setVisible(false);
        menu.findItem(R.id.search_meuitem).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onActionbarNavigationListener.onActionbarNavigation(getTag(), menuItem.getItemId());
        return true;
    }

    @Override
    public void onTaskError(String errorMessage, int taskCode) {
        // TODO Auto-generated method stub
        if(getView() == null || getActivity() == null)
            return;
        if(taskCode == GET_DISTRIBUTOR_TASKCODE) {
            paymentEditText.setVisibility(View.INVISIBLE);
            customerDetailsView.setVisibility(View.INVISIBLE);
        } else if(taskCode == GET_DISTRIBUTORPAYMENT_TASKCODE) {
            if(distributorPaymentAdapter != null) {
            	distributorPaymentAdapter.getCursor().requery();
            	distributorPaymentAdapter.notifyDataSetChanged();
            }
        }
        CustomToast.showCustomToast(getActivity(), errorMessage, Toast.LENGTH_LONG, CustomToast.ERROR);
    }


    View.OnClickListener onPaymentDialogClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(v.getId() == R.id.positiveButton) {
                try {
                    EditText paymentEditText = (EditText)v.getTag();
                    float paymentAmount = Float.parseFloat(paymentEditText.getText().toString());
                    totalDueValue += editCustomerPayment.getPaymentAmount();
                    Distributor customer = distributorPaymentListAdapter.getLastSelectedDistributor();
//                    customer.setCreditLimit(customer.getAmountDue() + editCustomerPayment.getPaymentAmount());
//                    customer.setAmountPaid(customer.getAmountPaid() - editCustomerPayment.getPaymentAmount());
                    editCustomerPayment.setDistributor(customer);
                    editCustomerPayment.setPaymentAmount(paymentAmount);
//                    new CreateCustomerPaymentTask(getActivity(), CustomerPaymentFragment.this, EDIT_DISTRIBUTORPAYMENT_TASKCODE, editCustomerPayment.getCustomerPaymentId() == customerPaymentAdapter.getItem(customerPaymentAdapter.getCount() - 1).getCustomerPaymentId()).execute(editCustomerPayment);
                } catch(NumberFormatException e) {
                    CustomToast.showCustomToast(getActivity(), "Invalid Amount", Toast.LENGTH_SHORT, CustomToast.ERROR);
                    return;
                }
                distributorPaymentAdapter.notifyDataSetChanged();
            }
            SnapCommonUtils.hideSoftKeyboard(getActivity(), v.getWindowToken());
            paymentDialog.dismiss();
        }
    };

    
    private void GetCreditBillsMethod(String parseStartDate,String parseEndDate) {
    	if (distributorPaymentListAdapter.getLastSelectedDistributor().getDistributorId() != 0) {
//			currentFilterAsyncTask1 = new GetCustomerCreditBillsTask(getActivity(),CustomerPaymentFragment.this, parseStartDate,parseEndDate,customerListAdapter.getLastSelectedCustomer().getCustomerId(), GET_CUSTOMERPAYMENT_TASKCODE, currentTransactionType);
//			currentFilterAsyncTask1.execute(customerListAdapter.getLastSelectedCustomer().getCustomerPhoneNumber());    
		}
	}

    
    

	@Override
	public void onDistributorPaymentEdit(CustomerCreditPayment customerPayment) {
		// TODO Auto-generated method stub
		
	}
	
    }