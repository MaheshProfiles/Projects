package com.snapbizz.snapbilling.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.domains.BillingMonitorItem;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;
import com.snapbizz.snaptoolkit.db.dao.Customers;
import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;

public class BillingMonitorAdapter extends ArrayAdapter<BillingMonitorItem> {

    private LayoutInflater layoutInflater;
    private Context context;
    private CartActionListener cartActionListener;
    private CustomerActionListener customerActionListener;
    private ArrayList<BillingMonitorItem> billingMonitorItemList;
    public int cartPos = 0;
    public int activeCart = 0;

    public BillingMonitorAdapter(Context context, int textViewResourceId,
            ArrayList<BillingMonitorItem> objects,
            CartActionListener cartActionListener,
            CustomerActionListener customerActionListerner) {
        super(context.getApplicationContext(), textViewResourceId, objects);
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.billingMonitorItemList = objects;
        this.cartActionListener = cartActionListener;
        this.customerActionListener = customerActionListerner;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        position = position % 4;
        BillingMonitorAdapterWrapper billingMonitorAdapterWrapper;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_bill, null);
            billingMonitorAdapterWrapper = new BillingMonitorAdapterWrapper();

            billingMonitorAdapterWrapper.totalPriceText = (TextView) convertView
                    .findViewById(R.id.bill_totalprice_textview);
            billingMonitorAdapterWrapper.totalQtyTextView = (TextView) convertView
                    .findViewById(R.id.bill_totalqty_textview);
            billingMonitorAdapterWrapper.customerSearchLayout = convertView
                    .findViewById(R.id.customer_search_layout);
            billingMonitorAdapterWrapper.searchCustomerEditText = (EditText) convertView
                    .findViewById(R.id.customer_search_edittext);
            billingMonitorAdapterWrapper.clearCustomersButton = (ImageButton) convertView
                    .findViewById(R.id.clear_customer_button);
            billingMonitorAdapterWrapper.clearCustomersButton.setOnClickListener(onSearchClearListener);
            billingMonitorAdapterWrapper.addCustomerButton = (Button) convertView
                    .findViewById(R.id.add_customer_button);
            billingMonitorAdapterWrapper.customerInformationLayout = convertView
                    .findViewById(R.id.customer_info_layout);
            billingMonitorAdapterWrapper.customerDetailsLayout = convertView
                    .findViewById(R.id.customer_details_relativelayout);
            billingMonitorAdapterWrapper.removeCustomerButton = (Button) convertView
                    .findViewById(R.id.remove_customer_button);
            billingMonitorAdapterWrapper.customerNameTextView = (TextView) convertView
                    .findViewById(R.id.customername_textivew);
            billingMonitorAdapterWrapper.customerDueTextView = (TextView) convertView
                    .findViewById(R.id.search_customerDue_textview);
            billingMonitorAdapterWrapper.customerNumberTextView = (TextView) convertView
                    .findViewById(R.id.customernumber_textivew);
            billingMonitorAdapterWrapper.customerEditButton = (Button) convertView
                    .findViewById(R.id.editcustomer_info_button);
            billingMonitorAdapterWrapper.customerCreditLimitTextView = (TextView) convertView
                    .findViewById(R.id.customer_creditlimit_value_textview);
            billingMonitorAdapterWrapper.customerDueAmountTextView = (TextView) convertView
                    .findViewById(R.id.customer_duevalue_textview);
            billingMonitorAdapterWrapper.customerMembershipDateTextView = (TextView) convertView
                    .findViewById(R.id.customer_memberdate_textview);
            billingMonitorAdapterWrapper.customerAddresssTextView = (TextView) convertView
                    .findViewById(R.id.customer_address_textview);
            billingMonitorAdapterWrapper.expandCustomerDetailsButton = (ImageButton) convertView
                    .findViewById(R.id.expand_customerdetails_button);
            billingMonitorAdapterWrapper.billFooterLayout = convertView
                    .findViewById(R.id.bill_footer_layout);
            billingMonitorAdapterWrapper.billingListView = (ListView) convertView
                    .findViewById(R.id.bill_listview);
            billingMonitorAdapterWrapper.billOptionsLinearLayout = convertView
                    .findViewById(R.id.billoptions_linearlayout);
            billingMonitorAdapterWrapper.billOffersButton = (Button) billingMonitorAdapterWrapper.billOptionsLinearLayout
                    .findViewById(R.id.offers_button);
            billingMonitorAdapterWrapper.billQuickAddButton = (Button) billingMonitorAdapterWrapper.billOptionsLinearLayout
                    .findViewById(R.id.quickadd_button);
            billingMonitorAdapterWrapper.billSearchButton = (Button) billingMonitorAdapterWrapper.billOptionsLinearLayout
                    .findViewById(R.id.search_button);
            billingMonitorAdapterWrapper.productSearchEditText = (EditText) convertView
                    .findViewById(R.id.product_search_edittext);
            billingMonitorAdapterWrapper.expandCustomerDetailsButton
            .setOnClickListener(onExpandCustomerDetailsClickListener);
            billingMonitorAdapterWrapper.searchCustomerEditText
            .addTextChangedListener(textChangeWatcher);
            billingMonitorAdapterWrapper.searchCustomerEditText
            .setOnFocusChangeListener(customerSearchEditTextFocusChangeListener);
            billingMonitorAdapterWrapper.billFooterLayout
            .setOnClickListener(onBillNavigationClickListener);
            billingMonitorAdapterWrapper.removeCustomerButton
            .setOnClickListener(onAddEditDelCustomerClickListener);
            billingMonitorAdapterWrapper.addCustomerButton
            .setOnClickListener(onAddEditDelCustomerClickListener);
            billingMonitorAdapterWrapper.customerEditButton
            .setOnClickListener(onAddEditDelCustomerClickListener);
            billingMonitorAdapterWrapper.billOffersButton.setOnClickListener(onBillNavigationClickListener);
            billingMonitorAdapterWrapper.billQuickAddButton.setOnClickListener(onBillNavigationClickListener);
            billingMonitorAdapterWrapper.billSearchButton.setOnClickListener(onBillNavigationClickListener);
            // convertView.setOnClickListener(onAddCartClickListener);
            convertView.setTag(R.string.wrapper_tag, billingMonitorAdapterWrapper);
        } else {
            billingMonitorAdapterWrapper = (BillingMonitorAdapterWrapper) convertView.getTag(R.string.wrapper_tag);
        }
        if ( position == SnapBillingConstants.LAST_SHOPPING_CART ) {
        	billingMonitorAdapterWrapper.billOptionsLinearLayout.findViewById(R.id.offers_button).setVisibility(View.INVISIBLE);
        	((TextView) convertView.findViewById(R.id.rate_textview)).setText(R.string.pp);
        } else {
        	billingMonitorAdapterWrapper.billOptionsLinearLayout.findViewById(R.id.offers_button).setVisibility(View.VISIBLE);
        	((TextView) convertView.findViewById(R.id.rate_textview)).setText(R.string.rate);
        }

        if (position == activeCart && position != SnapBillingConstants.LAST_SHOPPING_CART) {
        	convertView.setBackgroundResource(R.drawable.active_cart_bg);
        	convertView.setSelected(true);
        } else if (position == SnapBillingConstants.LAST_SHOPPING_CART) {
        	convertView.setPadding(3, 3, 3, 3);
        	convertView.setBackgroundResource(R.drawable.receive_bill_border_shape);
        } else {
        	convertView.setBackgroundResource(R.drawable.bill_border_shape);
        	convertView.setPadding(0, 0, 0, 0);
        }
        final int[] footerColors = { R.color.bill1_footer_color, R.color.bill2_footer_color,
        							 R.color.bill3_footer_color, R.color.bill4_footer_color };
        billingMonitorAdapterWrapper.billFooterLayout.setBackgroundResource(footerColors[position]);
        billingMonitorAdapterWrapper.addCustomerButton.setTag(
                R.string.position_tag, position);
        billingMonitorAdapterWrapper.searchCustomerEditText.setTag(position);
        convertView.setTag(R.string.position_tag, position);
        billingMonitorAdapterWrapper.expandCustomerDetailsButton
        .setTag(position);
        billingMonitorAdapterWrapper.removeCustomerButton.setTag(
                R.string.position_tag, position);
        billingMonitorAdapterWrapper.billFooterLayout.setTag(position);
        billingMonitorAdapterWrapper.billOffersButton.setTag(position);
        billingMonitorAdapterWrapper.billQuickAddButton.setTag(position);
        billingMonitorAdapterWrapper.billSearchButton.setTag(position);
        billingMonitorAdapterWrapper.clearCustomersButton.setTag(position);
        BillingMonitorItem billingMonitorItem = null;
        if (position < billingMonitorItemList.size())
            billingMonitorItem = getItem(position);
        if (billingMonitorItem == null) {
            billingMonitorAdapterWrapper.billingListView
            .setVisibility(View.INVISIBLE);
            billingMonitorAdapterWrapper.customerSearchLayout
            .setVisibility(View.INVISIBLE);
            billingMonitorAdapterWrapper.customerInformationLayout
            .setVisibility(View.INVISIBLE);
            billingMonitorAdapterWrapper.billFooterLayout
            .setVisibility(View.INVISIBLE);
            billingMonitorAdapterWrapper.billOptionsLinearLayout
            .setVisibility(View.INVISIBLE);
            if (billingMonitorItem == null)
                return convertView;
        } else {
            billingMonitorAdapterWrapper.billingListView
            .setVisibility(View.VISIBLE);
            billingMonitorAdapterWrapper.customerSearchLayout
            .setVisibility(View.VISIBLE);
            billingMonitorAdapterWrapper.billOptionsLinearLayout
            .setVisibility(View.VISIBLE);
            billingMonitorAdapterWrapper.billFooterLayout
            .setVisibility(View.VISIBLE);
            if (billingMonitorItem.getBillingMonitorBillListAdapter() == null
                    && billingMonitorItem.getShoppingCart().getCartItems() != null) {
                billingMonitorItem
                .setBillingMonitorBillListAdapter(new BillListAdapter(
                        context, billingMonitorItem.getShoppingCart() , null,
                        R.layout.listitem_bill_monitor));
            }
            billingMonitorAdapterWrapper.billingListView
            .setAdapter(billingMonitorItem
                    .getBillingMonitorBillListAdapter());
            billingMonitorAdapterWrapper.billingListView
            .setSelection(billingMonitorItem
                    .getBillingMonitorBillListAdapter().getCount() - 1);
            billingMonitorAdapterWrapper.totalPriceText
            .setText(SnapBillingTextFormatter.formatPriceText(
                    billingMonitorItem.getShoppingCart().getTotalCartValue() - billingMonitorItem.getShoppingCart()
                    .getTotalSavings(), context));
            billingMonitorAdapterWrapper.totalQtyTextView
            .setText(billingMonitorItem.getShoppingCart().getTotalQty()
                    + "");
            if (position == cartPos && !billingMonitorAdapterWrapper.searchCustomerEditText.hasFocus()) {
                billingMonitorAdapterWrapper.searchCustomerEditText.requestFocus();
        		SnapSharedUtils.storeLastStoredSelection(SnapCommonUtils.getSnapContext(context),null);
            }
            if (billingMonitorItem.getShoppingCart().getDistributor() != null) {
            	 Distributor distributor = billingMonitorItem.getShoppingCart().getDistributor();
                 billingMonitorAdapterWrapper.customerInformationLayout.setVisibility(View.VISIBLE);
                 billingMonitorAdapterWrapper.customerNumberTextView.setText(distributor.getPhoneNumber());
                 billingMonitorAdapterWrapper.customerNameTextView.setText(distributor.getAgencyName());
                 billingMonitorAdapterWrapper.expandCustomerDetailsButton.setSelected(billingMonitorItem.isCustomerExpanded());
                 billingMonitorAdapterWrapper.customerEditButton.setTag(R.string.position_tag, position);
                 billingMonitorAdapterWrapper.customerEditButton.setTag(R.string.customer_tag, distributor);
                 if (billingMonitorItem.isCustomerExpanded()) {
                     billingMonitorAdapterWrapper.customerAddresssTextView.setText(distributor.getTinNumber());
                     billingMonitorAdapterWrapper.customerDetailsLayout.setVisibility(View.VISIBLE);                    
                 } else {
                	 billingMonitorAdapterWrapper.customerDetailsLayout.setVisibility(View.GONE);
                 }
            } else if (billingMonitorItem.getShoppingCart().getCustomer() != null) {
            	SnapbizzDB snapbizzDB = SnapbizzDB.getInstance(context, true);
                Customers customer = billingMonitorItem.getShoppingCart().getCustomer();
                billingMonitorAdapterWrapper.customerInformationLayout.setVisibility(View.VISIBLE);
                billingMonitorAdapterWrapper.customerNumberTextView.setText(String.valueOf(customer.getPhone()));
                billingMonitorAdapterWrapper.customerNameTextView.setText(customer.getName());
                if (cartPos == SnapBillingConstants.LAST_SHOPPING_CART) {
                	billingMonitorAdapterWrapper.customerDueTextView.setText("");
                	billingMonitorAdapterWrapper.customerDueTextView.setVisibility(View.GONE);
                } else {
                	billingMonitorAdapterWrapper.customerDueTextView.setVisibility(View.VISIBLE);
                	billingMonitorAdapterWrapper.customerDueTextView.setText(context.getString(R.string.display_due) + SnapBillingTextFormatter.formatPriceText(snapbizzDB.getCustomerDuePhoneNo(customer.getPhone()), context));
                }
                
                billingMonitorAdapterWrapper.expandCustomerDetailsButton.setSelected(billingMonitorItem.isCustomerExpanded());
                billingMonitorAdapterWrapper.customerEditButton.setTag(R.string.position_tag, position);
                billingMonitorAdapterWrapper.customerEditButton.setTag(R.string.customer_tag, customer);
                if (billingMonitorItem.isCustomerExpanded()) {
                    billingMonitorAdapterWrapper.customerAddresssTextView.setText(customer.getAddress());
                    billingMonitorAdapterWrapper.customerCreditLimitTextView.setText(SnapBillingTextFormatter.formatPriceText(customer.getCreditLimit() != null ? customer.getCreditLimit() : 0, context));
                    billingMonitorAdapterWrapper.customerDueAmountTextView.setText(SnapBillingTextFormatter.formatPriceText(snapbizzDB.getCustomerDuePhoneNo(customer.getPhone()), context));
                    billingMonitorAdapterWrapper.customerMembershipDateTextView.setText(SnapBillingTextFormatter.formatCustomerMembershipDate(customer.getCreatedAt()));
                    billingMonitorAdapterWrapper.customerDetailsLayout.setVisibility(View.VISIBLE);
                } else {
                    billingMonitorAdapterWrapper.customerDetailsLayout.setVisibility(View.GONE);
                }
            } else {
                billingMonitorAdapterWrapper.searchCustomerEditText.removeTextChangedListener(textChangeWatcher);
                billingMonitorAdapterWrapper.searchCustomerEditText.setText(billingMonitorItem.getCustomerSearchString());
                billingMonitorAdapterWrapper.searchCustomerEditText.addTextChangedListener(textChangeWatcher);
                billingMonitorAdapterWrapper.customerInformationLayout.setVisibility(View.GONE);
            }
        }

        return convertView;
    }
    
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for(int i = 0 ; i < billingMonitorItemList.size(); i++) {
            if (billingMonitorItemList.get(i).getBillingMonitorBillListAdapter() != null)
                billingMonitorItemList.get(i).getBillingMonitorBillListAdapter().notifyDataSetChanged();
        }
    };
    
    View.OnClickListener onExpandCustomerDetailsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            getItem((Integer) v.getTag()).setCustomerExpanded(!v.isSelected());
            notifyDataSetChanged();
        }
    };

    View.OnClickListener onSearchClearListener =new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	Log.d("TAG", "showEditCustomerLayout onSearchClearListener");
            getItem((Integer)v.getTag()).setCustomerSearchString("");
            notifyDataSetChanged();
        }
    };
    View.OnClickListener onAddEditDelCustomerClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	Log.d("TAG", "showEditCustomerLayout onAddEditDelCustomerClickListener");
            cartPos = (Integer) v.getTag(R.string.position_tag);
            if (v.getId() == R.id.add_customer_button) {
                customerActionListener.onAddCustomerClick();
            } else if (v.getId() == R.id.editcustomer_info_button) {
                getItem(cartPos).setCustomerExpanded(false);
                notifyDataSetChanged();
                customerActionListener.onEditCustomer((Customers) v.getTag(R.string.customer_tag));
                if (cartPos == SnapBillingConstants.LAST_SHOPPING_CART)
                	customerActionListener.onEditDistributor((Distributor) v.getTag(R.string.customer_tag));
                
            } else {
            	getItem(cartPos).getShoppingCart().setCustomer(null);
            	if (cartPos == SnapBillingConstants.LAST_SHOPPING_CART)
            		 getItem(cartPos).getShoppingCart().setDistributor(null);	
                notifyDataSetChanged();
            }
        }
    };

    View.OnClickListener onBillNavigationClickListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
        	loadCheckoutScreen(v);
        }
    };
    
    private void loadCheckoutScreen(View v) {
    	if (v.getId() == R.id.bill_footer_layout) {
    		cartActionListener.onOpenCart((Integer) v.getTag());
       } else if (v.getId() == R.id.offers_button) {
           cartActionListener.onShowOffers((Integer) v.getTag());
       } else if (v.getId() == R.id.quickadd_button) {
           cartActionListener.onShowQuickAdd((Integer) v.getTag());
       } else if (v.getId() == R.id.search_button) {
           cartActionListener.onShowSearchProducts((Integer) v.getTag());
       }
    }

    CountDownTimer keyStrokeTimer = new CountDownTimer(300, 300) {
        @Override
        public void onTick(long arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onFinish() {
            customerActionListener.onCustomerSearch(getItem(cartPos)
                    .getCustomerSearchString(), cartPos);
        }
    };

    OnFocusChangeListener customerSearchEditTextFocusChangeListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                cartPos = (Integer) v.getTag();
            }
        }
    };

    TextWatcher textChangeWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            keyStrokeTimer.cancel();
            getItem(cartPos).setCustomerSearchString(s.toString());
            if (s.length() == 0) {
                customerActionListener.onCancelSearch(cartPos);
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

    @Override
    public int getCount() {
        if (billingMonitorItemList.size() < 4)
            return billingMonitorItemList.size() + 1;
        else
            return billingMonitorItemList.size();
    };
    
    public void setActiveCart(int activeCartPos) {
    	activeCart = activeCartPos;
    	notifyDataSetChanged();
    }
    
    private static class BillingMonitorAdapterWrapper {
        public ListView billingListView;
        public EditText searchCustomerEditText;
        public EditText productSearchEditText;
        public View customerSearchLayout;
        public View customerInformationLayout;
        public View customerDetailsLayout;
        public View billOptionsLinearLayout;
        public View billFooterLayout;
        public TextView totalPriceText;
        public TextView totalQtyTextView;
        public TextView customerNameTextView;
        public TextView customerDueTextView;
        public TextView customerNumberTextView;
        public TextView customerMembershipDateTextView;
        public TextView customerCreditLimitTextView;
        public TextView customerDueAmountTextView;
        public TextView customerAddresssTextView;
        public Button addCustomerButton;
        public Button removeCustomerButton;
        public ImageButton expandCustomerDetailsButton;
        public Button customerEditButton;
        public Button billOffersButton;
        public Button billQuickAddButton;
        public Button billSearchButton;
        private ImageButton clearCustomersButton;
    }
    
    public BillListAdapter getBillingAdapter(int position) {
        return billingMonitorItemList.get(position).getBillingMonitorBillListAdapter();
    }
    
    public interface CartActionListener {
        public void onOpenCart(int shoppingCartId);

        public void onShowOffers(int shoppingCartId);

        public void onShowQuickAdd(int shoppingCartId);

        public void onShowSearchProducts(int shoppingCartId);
    }

    public interface CustomerActionListener {
        public void onCustomerSearch(String customerNumber, int position);

        public void onAddCustomerClick();

        public void onCancelSearch(int position);

        public void onEditCustomer(Customers customer);
        public void onEditDistributor(Distributor distributor);
      
    }

}
