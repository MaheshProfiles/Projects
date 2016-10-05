package com.snapbizz.snapbilling.fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jess.ui.TwoWayGridView;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.BillingMonitorAdapter;
import com.snapbizz.snapbilling.adapters.BillingMonitorAdapter.CartActionListener;
import com.snapbizz.snapbilling.adapters.BillingMonitorAdapter.CustomerActionListener;
import com.snapbizz.snapbilling.adapters.CustomerSearchListAdapter;
import com.snapbizz.snapbilling.adapters.DistributorSearchListAdapter;
import com.snapbizz.snapbilling.asynctasks.AddEditDistributorTask;
import com.snapbizz.snapbilling.asynctasks.QueryDistributorInfoTask;
import com.snapbizz.snapbilling.domains.BillingMonitorItem;
import com.snapbizz.snapbilling.interfaces.OnRecreateAdapterListener;
import com.snapbizz.snapbilling.utils.GoogleAnalyticsTracker;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.customviews.SoftInputEditText;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;
import com.snapbizz.snaptoolkit.db.dao.CustomerDetails;
import com.snapbizz.snaptoolkit.db.dao.Customers;
import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.ShoppingCart;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.ValidationUtils;

public class BillingMonitorFragment extends Fragment implements
OnQueryCompleteListener, CustomerActionListener {
	private final String TAG = "[BillingMonitorFragment]";
    private final int GET_DISTRIBUTORINFO_TASKCODE = 6;
    private final int ADD_DISTRIBUTORINFO_TASKCODE = 7;
    private final int BILLING_LIST_WIDTH = 435;

    private TwoWayGridView billingMonitorListView;
    private BillingMonitorAdapter billingMonitorAdapter;
    private DistributorSearchListAdapter distributorAdapter;
    private QueryDistributorInfoTask getDistributorInfoTask;
    private CustomerSearchListAdapter customerAdapter;
    private LinearLayout addCustomerLayout;
    private LinearLayout customerSearchLayoutContainer;
    private OnActionbarNavigationListener onActionbarNavigationListener;
    private OnRecreateAdapterListener onRecreateAdapterListener;

    InputMethodManager imm;	// TODO: Remove this
    private SnapbizzDB snapbizzDB;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_billing_monitor, null);
        billingMonitorListView = (TwoWayGridView) view.findViewById(R.id.billing_monitor_list);
        addCustomerLayout = (LinearLayout) view.findViewById(R.id.add_customer_layout);
        view.findViewById(R.id.save_button).setOnClickListener(customerAddEditListener);
        view.findViewById(R.id.edit_button).setOnClickListener(customerAddEditListener);
        view.findViewById(R.id.cancel_button).setOnClickListener(customerAddEditListener);
        
        addCustomerLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            
            public void onClick(View v) {
            	SnapCommonUtils.hideSoftKeyboard(getActivity(), v.getWindowToken());
                v.setVisibility(View.GONE);
                ((SoftInputEditText) getActivity()
                        .findViewById(R.id.customer_name_edittext)).setText("");
                        
               ((SoftInputEditText) getActivity()
                        .findViewById(R.id.customer_number_edittext))
                        .setText("");
              ((SoftInputEditText) getActivity()
                        .findViewById(R.id.customer_address_edittext))
                        .setText("");


            }
        });
        customerSearchLayoutContainer = (LinearLayout) view.findViewById(
                R.id.customersearch_linearlayout);
        return view;
    }
    
    public BillingMonitorAdapter getBillingMonitorAdapter() {
        if (billingMonitorAdapter==null) {
            onRecreateAdapterListener.onRecreateADapter();
        }
        return billingMonitorAdapter;
    }

    OnClickListener customerAddEditListener = new OnClickListener() {
    	

        @Override
        public void onClick(View v) {
        	Log.d("TAG", "showEditCustomerLayout customerAddEditListener");
            if (v.getId() == R.id.save_button || v.getId() == R.id.edit_button) {
                String customerName = ((SoftInputEditText) getActivity()
                        .findViewById(R.id.customer_name_edittext)).getText()
                        .toString();
                String customerNumber = ((SoftInputEditText) getActivity()
                        .findViewById(R.id.customer_number_edittext))
                        .getText().toString();
                String customerAddress = ((SoftInputEditText) getActivity()
                        .findViewById(R.id.customer_address_edittext))
                        .getText().toString();
                boolean isEdit = v.getId() == R.id.edit_button ? true : false;
                if (billingMonitorAdapter.cartPos == SnapBillingConstants.LAST_SHOPPING_CART && customerNumber.length() == 0) {
					CustomToast.showCustomToast(getActivity()
							.getApplicationContext(),
							getString(R.string.msg_validnumber), Toast.LENGTH_SHORT,
							CustomToast.WARNING);
				} else if (!ValidationUtils.validateMobileNo(customerNumber)) {
                    CustomToast.showCustomToast(getActivity().getApplicationContext(), getString(R.string.msg_validnumber), Toast.LENGTH_SHORT, CustomToast.WARNING);
                } else if (customerName.length() != 0 && !ValidationUtils.validateName(customerName)) {
                    CustomToast.showCustomToast(getActivity().getApplicationContext(), getString(R.string.msg_validname), Toast.LENGTH_SHORT, CustomToast.WARNING);
                } else {
                    int creditLimit = 0;
                    if (((SoftInputEditText) getActivity()
                            .findViewById(R.id.customer_creditlimit_edittext)).length() > 0) {
                        try {
                            creditLimit = Integer.parseInt(((SoftInputEditText) getActivity()
                                    .findViewById(R.id.customer_creditlimit_edittext))
                                    .getText().toString());
                        } catch(Exception e) {
                            CustomToast.showCustomToast(getActivity().getApplicationContext(), getString(R.string.msg_validcredit), Toast.LENGTH_SHORT, CustomToast.WARNING);
                            return;
                        }
                    }
                    Customers newCustomer = null;
                    Distributor distributor = null;
                    GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG + 
                    		" " + billingMonitorAdapter.cartPos);
                    if (billingMonitorAdapter.cartPos == SnapBillingConstants.LAST_SHOPPING_CART) {
						if (!isEdit) {
							distributor = new Distributor();
							distributor.setAgencyName(customerName);
							distributor.setPhoneNumber(customerNumber);
							distributor.setTinNumber(customerAddress);
						} else {
							distributor = (Distributor) v.getTag();
							distributor.setAgencyName(customerName);
							distributor.setPhoneNumber(customerNumber);
							distributor.setTinNumber(customerAddress);
							
						}
						new AddEditDistributorTask(getActivity()
								.getApplicationContext(),
								BillingMonitorFragment.this,
								ADD_DISTRIBUTORINFO_TASKCODE, isEdit)
								.execute(distributor);
						SnapCommonUtils.hideSoftKeyboard(getActivity(), customerSearchLayoutContainer.getWindowToken());
					} else {
						if (!isEdit) {
							if (snapbizzDB.getCustomerByPhoneNo(Long.parseLong(customerNumber)).size() > 0) {
			                    CustomToast.showCustomToast(getActivity(), getString(R.string.exc_customer_exists), Toast.LENGTH_SHORT, CustomToast.WARNING);
			                } else {
								snapbizzDB.insertCustomer(new Customers(Long.parseLong(customerNumber),
										customerName, customerAddress, null, 0, false, new Date(), new Date()));
								snapbizzDB.insertCustomerDetails(new CustomerDetails(Long.parseLong(customerNumber),
			                			0, 0, 0, 0, 0, 0, 0f, null, null, new Date(), new Date()));
								addEditCustomerSuccess(new Customers(Long.parseLong(customerNumber),
										customerName, customerAddress, null, 0, false, new Date(), new Date()));
			                }
						} else {
							newCustomer = (Customers) v.getTag();
							newCustomer.setName(customerName);
							newCustomer.setPhone(Long.parseLong(customerNumber));
							newCustomer.setAddress(customerAddress);
							newCustomer.setCreditLimit(creditLimit);
							newCustomer.setUpdatedAt(new Date());
							snapbizzDB.updateCustomerByPhoneNo(Long.parseLong(customerNumber),
									customerName, customerAddress, null, creditLimit);
							addEditCustomerSuccess(newCustomer);
						}
					}
                }
            } else {
                hideAddCustomerLayout();
                SnapCommonUtils.hideSoftKeyboard(getActivity(), customerSearchLayoutContainer.getWindowToken());
            }
        }
        private void addEditCustomerSuccess(Customers newCustomer) {
        	hideAddCustomerLayout();
    		billingMonitorAdapter.getItem(billingMonitorAdapter.cartPos).getShoppingCart().setCustomer(newCustomer);
    		billingMonitorAdapter.notifyDataSetChanged();
    		customerSearchLayoutContainer.setVisibility(View.GONE);
    		SnapCommonUtils.hideSoftKeyboard(getActivity(), customerSearchLayoutContainer.getWindowToken());
    		billingMonitorAdapter.getItem(billingMonitorAdapter.cartPos).setCustomerSearchString("");
        }
    };

    public void hideAddCustomerLayout() {
        if (addCustomerLayout != null && addCustomerLayout.getVisibility() == View.VISIBLE) {
            addCustomerLayout.setVisibility(View.GONE);
            ((EditText) getActivity()
                    .findViewById(R.id.customer_name_edittext)).setText("");
            ((EditText) getActivity()
                    .findViewById(R.id.customer_number_edittext)).setText("");
            ((EditText) getActivity()
                    .findViewById(R.id.customer_address_edittext)).setText("");
            ((EditText) getActivity()
                    .findViewById(R.id.customer_creditlimit_edittext)).setText("");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = getActivity().getActionBar();
        if (!actionBar.isShowing())
            actionBar.show();
        setHasOptionsMenu(true);
        if (actionBar.getCustomView().getId() != R.id.actionbar_layout)
            actionBar.setCustomView(R.layout.actionbar_layout);
        ((TextView)getActivity().findViewById(R.id.actionbar_header)).setText(getString(R.string.header_billing_monitor));
        snapbizzDB = SnapbizzDB.getInstance(getActivity(), false);
        if (billingMonitorAdapter != null)
            billingMonitorListView.setAdapter(billingMonitorAdapter);
        else {
            onRecreateAdapterListener.onRecreateADapter();
            billingMonitorListView.setAdapter(billingMonitorAdapter);
        }
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        ((ListView) getView().findViewById(R.id.customer_search_result_listview))
        .setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long arg3) {
                billingMonitorAdapter.getItem(billingMonitorAdapter.cartPos).setCustomerSearchString("");
                if (billingMonitorAdapter.cartPos == SnapBillingConstants.LAST_SHOPPING_CART) {
                	 billingMonitorAdapter
                     .getItem(billingMonitorAdapter.cartPos).getShoppingCart()
                     .setDistributor(distributorAdapter.getItem(pos));
                } else {
                	billingMonitorAdapter
                	.getItem(billingMonitorAdapter.cartPos).getShoppingCart().setCustomer(customerAdapter.getItem(pos));
                }
                billingMonitorAdapter.notifyDataSetChanged();
                SnapCommonUtils.hideSoftKeyboard(getActivity(), customerSearchLayoutContainer.getWindowToken());
                customerSearchLayoutContainer.setVisibility(View.GONE);
            }
        });
        customerSearchLayoutContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                customerSearchLayoutContainer.setVisibility(View.GONE);
            }
        });
        if (customerAdapter != null) {
            ((ListView) getView().findViewById(
                    R.id.customer_search_result_listview)).setAdapter(customerAdapter);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onActionbarNavigationListener = (OnActionbarNavigationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+getString(R.string.exc_implementnavigation));
        }
        try {
            onRecreateAdapterListener = (OnRecreateAdapterListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+"must implement OnRecreateAdapterListener");
        }
    }

    public void onAddCart(ShoppingCart shoppingCart, int position, Context context, CartActionListener cartActionListener) {
        if (billingMonitorAdapter == null) {
            billingMonitorAdapter = new BillingMonitorAdapter(context, android.R.id.text1, new ArrayList<BillingMonitorItem>(), cartActionListener, this);
        }
        billingMonitorAdapter.add(new BillingMonitorItem(shoppingCart));
        billingMonitorAdapter.notifyDataSetChanged();

    }

    public void getCustomer(String customerNameOrAddressOrPhone) {
    	List<Customers> customerListItems = snapbizzDB.getCustomerByNameOrAddressOrPhone(customerNameOrAddressOrPhone);
        if (customerListItems != null && !customerListItems.isEmpty()) {
        	hideAddCustomerLayout();
	        ListView customerListView = (ListView) getView().findViewById(
	                R.id.customer_search_result_listview);
	        LinearLayout.LayoutParams customerListLayoutParams = (LinearLayout.LayoutParams) getView()
	        		.findViewById(R.id.customersearch_listviewlayout).getLayoutParams();
            customerListLayoutParams.leftMargin =  billingMonitorAdapter.cartPos * BILLING_LIST_WIDTH + billingMonitorAdapter.cartPos * 2 + 1;
	        if (billingMonitorAdapter.cartPos == 3)
	            customerListLayoutParams.leftMargin =  SnapBillingConstants.BILLING_LIST_LEFT_MARGIN;
	        getView().findViewById(R.id.customersearch_listviewlayout).setLayoutParams(customerListLayoutParams);            
	        customerAdapter = new CustomerSearchListAdapter(getActivity().getApplicationContext(),android.R.id.text1, customerListItems);
	        customerListView.setAdapter(customerAdapter);
	        customerSearchLayoutContainer.setVisibility(View.VISIBLE);
        } else {
        	showAddCustomerLayout(getString(R.string.err_no_customer_found));
		}
	}
    
    @Override
    public void onTaskSuccess(Object responseList, int taskCode) {
        if (taskCode == GET_DISTRIBUTORINFO_TASKCODE) {
        	 ArrayList<Distributor> distributorListItems = (ArrayList<Distributor>) responseList;
             hideAddCustomerLayout();
             ListView customerListView = (ListView) getView().findViewById(
                     R.id.customer_search_result_listview);
             LinearLayout.LayoutParams customerListLayoutParams = (LinearLayout.LayoutParams) getView().findViewById(R.id.customersearch_listviewlayout)
                     .getLayoutParams();
             if (billingMonitorAdapter.cartPos == 3) {
                 customerListLayoutParams.leftMargin =  SnapBillingConstants.BILLING_LIST_LEFT_MARGIN;
             } else {
                 customerListLayoutParams.leftMargin =  billingMonitorAdapter.cartPos * BILLING_LIST_WIDTH + billingMonitorAdapter.cartPos * 2 + 1;
             }
      
            distributorAdapter = new DistributorSearchListAdapter(getActivity(),android.R.id.text1, distributorListItems);
            customerListView.setAdapter(distributorAdapter);
            customerSearchLayoutContainer.setVisibility(View.VISIBLE);
        } else if (taskCode == ADD_DISTRIBUTORINFO_TASKCODE) {
            hideAddCustomerLayout();
            billingMonitorAdapter.getItem(billingMonitorAdapter.cartPos).getShoppingCart().setDistributor((Distributor)responseList);
            billingMonitorAdapter.notifyDataSetChanged();
            customerSearchLayoutContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTaskError(String errorMessage, int taskCode) {
        if (taskCode == GET_DISTRIBUTORINFO_TASKCODE) {
			showAddCustomerLayout(errorMessage);
		}else {
            CustomToast.showCustomToast(getActivity().getApplicationContext(), errorMessage, Toast.LENGTH_LONG, CustomToast.ERROR);
        }
        
    }

    public void showAddCustomerLayout(String phoneNumber) {
    	Log.d("TAG", "showEditCustomerLayout showAddCustomerLayout");
        addCustomerLayout.findViewById(R.id.edit_button).setVisibility(View.INVISIBLE);
        addCustomerLayout.findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        addCustomerLayout.setVisibility(View.VISIBLE);
        if (ValidationUtils.validateNumber(phoneNumber)) {
            ((EditText)addCustomerLayout.findViewById(R.id.customer_number_edittext)).setText(phoneNumber); 
            ((EditText)addCustomerLayout.findViewById(R.id.customer_number_edittext)).setSelection(phoneNumber.length());
        }
        LinearLayout.LayoutParams addCustomerLayoutParams = (LinearLayout.LayoutParams) addCustomerLayout.findViewById(R.id.add_customer_relativelayout)
                .getLayoutParams();
        if (billingMonitorAdapter.cartPos == 3) {
            addCustomerLayoutParams.leftMargin = SnapBillingConstants.BILLING_LIST_LEFT_MARGIN - 1;
            setDistributorHint();
            
        } else {
            //billingMonitorListView.setSelection(0);
        	setCustomerHint();
            addCustomerLayoutParams.leftMargin = billingMonitorAdapter.cartPos * BILLING_LIST_WIDTH + billingMonitorAdapter.cartPos * 2;
        }
        addCustomerLayout.findViewById(R.id.add_customer_relativelayout).setLayoutParams(addCustomerLayoutParams);
        customerSearchLayoutContainer.setVisibility(View.GONE);
        ((EditText)addCustomerLayout.findViewById(R.id.customer_number_edittext)).requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        
    }

    public void showEditCustomerLayout(Customers customer) {
    	
    	Log.d("TAG", "showEditCustomerLayout showEditCustomerLayout");
    	
        addCustomerLayout.setVisibility(View.VISIBLE);
        addCustomerLayout.findViewById(R.id.edit_button).setVisibility(View.VISIBLE);
        addCustomerLayout.findViewById(R.id.edit_button).setTag(customer);
        addCustomerLayout.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
        setCustomerHint();
        ((EditText)addCustomerLayout.findViewById(R.id.customer_number_edittext)).setText(String.valueOf(customer.getPhone()));
        ((EditText)addCustomerLayout.findViewById(R.id.customer_address_edittext)).setText(customer.getAddress());
        ((EditText)addCustomerLayout.findViewById(R.id.customer_name_edittext)).setText(customer.getName());
        ((EditText)addCustomerLayout.findViewById(R.id.customer_creditlimit_edittext)).setText(customer.getCreditLimit()+"");
        LinearLayout.LayoutParams addCustomerLayoutParams = (LinearLayout.LayoutParams) addCustomerLayout.findViewById(R.id.add_customer_relativelayout)
                																						 .getLayoutParams();
        if (billingMonitorAdapter.cartPos == SnapBillingConstants.LAST_SHOPPING_CART) {
        	setDistributorHint();
            addCustomerLayoutParams.leftMargin = SnapBillingConstants.BILLING_LIST_LEFT_MARGIN - 1;
        } else {
            addCustomerLayoutParams.leftMargin = billingMonitorAdapter.cartPos * BILLING_LIST_WIDTH + billingMonitorAdapter.cartPos * 2;
        }
        addCustomerLayout.findViewById(R.id.add_customer_relativelayout).setLayoutParams(addCustomerLayoutParams);
        customerSearchLayoutContainer.setVisibility(View.GONE);
        ((EditText)addCustomerLayout.findViewById(R.id.customer_name_edittext)).requestFocus();
        ((EditText)addCustomerLayout.findViewById(R.id.customer_name_edittext)).setSelection(String.valueOf(customer.getName()).length());
    }
    
    public void showEditDistributorLayout(Distributor distributor) {
    	
    	Log.d("TAG", "showEditCustomerLayout showEditCustomerLayout");
    	
        addCustomerLayout.setVisibility(View.VISIBLE);
        addCustomerLayout.findViewById(R.id.edit_button).setVisibility(View.VISIBLE);
        addCustomerLayout.findViewById(R.id.edit_button).setTag(distributor);
        addCustomerLayout.findViewById(R.id.save_button).setVisibility(View.INVISIBLE); 
        setDistributorHint();
        ((EditText)addCustomerLayout.findViewById(R.id.customer_number_edittext)).setText(distributor.getPhoneNumber());
        ((EditText)addCustomerLayout.findViewById(R.id.customer_address_edittext)).setText(distributor.getTinNumber());
        ((EditText)addCustomerLayout.findViewById(R.id.customer_name_edittext)).setText(distributor.getAgencyName());
        LinearLayout.LayoutParams addCustomerLayoutParams = (LinearLayout.LayoutParams) addCustomerLayout.findViewById(R.id.add_customer_relativelayout)
                .getLayoutParams();
        if (billingMonitorAdapter.cartPos == 3) {
            //billingMonitorListView.setSelection(3);
            addCustomerLayoutParams.leftMargin = SnapBillingConstants.BILLING_LIST_LEFT_MARGIN - 1;
        } else {
            //billingMonitorListView.setSelection(0);
            addCustomerLayoutParams.leftMargin = billingMonitorAdapter.cartPos * BILLING_LIST_WIDTH + billingMonitorAdapter.cartPos * 2;
        }
        addCustomerLayout.findViewById(R.id.add_customer_relativelayout).setLayoutParams(addCustomerLayoutParams);
        customerSearchLayoutContainer.setVisibility(View.GONE);
        ((EditText)addCustomerLayout.findViewById(R.id.customer_number_edittext)).requestFocus();
        ((EditText)addCustomerLayout.findViewById(R.id.customer_number_edittext)).setSelection(distributor.getPhoneNumber().length());
    }

    @Override
    public void onCustomerSearch(String customerNumber, int position) {
    	Log.d("TAG", "showEditCustomerLayout onCustomerSearch");
        if (addCustomerLayout.getVisibility() == View.VISIBLE) {
            ((EditText) getActivity().findViewById(R.id.customer_number_edittext)).setText(customerNumber);
        }
        if (position == SnapBillingConstants.LAST_SHOPPING_CART) {
            if (customerNumber != null && customerNumber.length() == 0) {
                if (distributorAdapter != null) {
                	distributorAdapter.clear();
                	distributorAdapter.notifyDataSetChanged();
                }
                customerSearchLayoutContainer.setVisibility(View.GONE);
            } else {
                if (getDistributorInfoTask != null && !getDistributorInfoTask.isCancelled()) {
                	getDistributorInfoTask.cancel(true);
                }
                getDistributorInfoTask = new QueryDistributorInfoTask(getActivity(), this,
                		GET_DISTRIBUTORINFO_TASKCODE);
                getDistributorInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, customerNumber);
            }       	
	    } else {
	        if (customerNumber != null && customerNumber.length() == 0) {
	            if (customerAdapter != null) {
	                customerAdapter.clear();
	                customerAdapter.notifyDataSetChanged();
	            }
	            if (billingMonitorAdapter.cartPos == SnapBillingConstants.LAST_SHOPPING_CART) {
	            	if (distributorAdapter != null) {
	            		distributorAdapter.clear();
	            		distributorAdapter.notifyDataSetChanged();
	                }
	            }
	            customerSearchLayoutContainer.setVisibility(View.GONE);
	        } else {
	            if (getDistributorInfoTask != null && !getDistributorInfoTask.isCancelled()) {
	            	getDistributorInfoTask.cancel(true);
	            }
	            getCustomer(customerNumber);
	        }
	    }
        
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.dualdisplay_menuitem).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onActionbarNavigationListener.onActionbarNavigation(getTag(), menuItem.getItemId());
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        billingMonitorListView = null;
        addCustomerLayout = null;
        customerSearchLayoutContainer = null;
        if (null != billingMonitorAdapter) {
        	billingMonitorAdapter.clear();
        }
        billingMonitorAdapter = null;
    }
    
    @Override
    public void onCancelSearch(int position) {
        customerSearchLayoutContainer.setVisibility(View.GONE);
        hideAddCustomerLayout();
    }

    @Override
    public void onAddCustomerClick() {
    	billingMonitorAdapter.notifyDataSetChanged();
        showAddCustomerLayout("");
    }

    @Override
    public void onEditCustomer(Customers customer) {
        showEditCustomerLayout(customer);
    }
    
    @Override
    public void onEditDistributor(Distributor distributor) {
    	showEditDistributorLayout(distributor);
    }
    
    public void resetAdapter(int position) {
        if (billingMonitorListView != null) {
	        billingMonitorListView.setAdapter(null);
	        billingMonitorListView.setAdapter(billingMonitorAdapter);
	        
	        //if (position == SnapBillingConstants.LAST_SHOPPING_CART)
	        //	billingMonitorListView.setSelection(2);

            // Use the following code for moving the view to
            // a particular cart
	        /*if (position==0 || position==1)
	        	billingMonitorListView.setSelection(0);
	        else if (position==2 || position==3)
	        	billingMonitorListView.setSelection(2);*/
        }
    }
    
    public void setSelection(int position) {
    	if (billingMonitorListView != null)
    		billingMonitorListView.setSelection(position);
    }
    
    private void setDistributorHint() {
    	((EditText)addCustomerLayout.findViewById(R.id.customer_number_edittext)).setHint(R.string.distributor_number_hint);
        ((EditText)addCustomerLayout.findViewById(R.id.customer_name_edittext)).setHint(R.string.agency_name_hint);
        ((EditText)addCustomerLayout.findViewById(R.id.customer_address_edittext)).setHint(R.string.distributor_tin_hint);
        ((EditText)addCustomerLayout.findViewById(R.id.customer_address_edittext)).setInputType(InputType.TYPE_CLASS_NUMBER);
		((EditText)addCustomerLayout.findViewById(R.id.customer_address_edittext)).setFilters(SnapBillingUtils.setTextLength(SnapBillingConstants.TIN_MAX_LENGTH));
    }
    
    private void setCustomerHint() {
    	((EditText)addCustomerLayout.findViewById(R.id.customer_number_edittext)).setHint(R.string.customer_number_hint);
		((EditText)addCustomerLayout.findViewById(R.id.customer_name_edittext)).setHint(R.string.customer_name_hint);
		((EditText)addCustomerLayout.findViewById(R.id.customer_address_edittext)).setHint(R.string.customer_address_hint);
		((EditText)addCustomerLayout.findViewById(R.id.customer_address_edittext)).setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
		((EditText)addCustomerLayout.findViewById(R.id.customer_address_edittext)).setFilters(SnapBillingUtils.setTextLength(SnapBillingConstants.CUSTOMER_ADDR_MAX_LENGTH));
    }
    
}
