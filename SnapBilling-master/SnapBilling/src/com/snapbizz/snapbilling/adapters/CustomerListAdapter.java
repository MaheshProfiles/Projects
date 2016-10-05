package com.snapbizz.snapbilling.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;
import com.snapbizz.snaptoolkit.db.dao.Customers;

public class CustomerListAdapter extends ArrayAdapter<Customers> {

    private LayoutInflater layoutInflater;
    private int lastSelectedItemPosition = -1;
    private SnapbizzDB snapbizzDB;

    public CustomerListAdapter(Context context, int textViewResourceId,
            List<Customers> objects) {
        super(context, textViewResourceId, objects);
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lastSelectedItemPosition = 0;
        snapbizzDB = SnapbizzDB.getInstance(context, true);
    } 

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) 
            convertView = layoutInflater.inflate(R.layout.listitem_customer, parent, false);
        Customers customer = getItem(position);
        if (lastSelectedItemPosition == position) 
            convertView.setBackgroundResource(R.color.billitem_selected_color);
        else 
            convertView.setBackgroundResource(android.R.color.white);
        ((TextView) convertView.findViewById(R.id.customer_name_textview)).setText(customer.getName());
        ((TextView) convertView.findViewById(R.id.customer_number_textview)).setText(String.valueOf(customer.getPhone()));
        ((TextView) convertView.findViewById(R.id.search_customeraddress_textview)).setText(customer.getAddress());
        ((TextView) convertView.findViewById(R.id.customer_credit_textview)).setText(SnapBillingTextFormatter.formatPriceText(snapbizzDB.getCustomerDuePhoneNo(customer.getPhone()), getContext()));
        return convertView;
    }

    public int getLastSelectedItemPosition() {
        return lastSelectedItemPosition;
    }

    public void setLastSelectedItemPosition(int position) {
        this.lastSelectedItemPosition = position;
    }

    public Customers getLastSelectedCustomer() {
        if (lastSelectedItemPosition == -1)
            return null;
        else
            return getItem(lastSelectedItemPosition);
    }

}
