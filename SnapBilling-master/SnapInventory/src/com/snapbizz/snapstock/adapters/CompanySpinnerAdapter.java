package com.snapbizz.snapstock.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snaptoolkit.domains.Company;

public class CompanySpinnerAdapter extends ArrayAdapter<Company> {

    private LayoutInflater inflater;
    private Context context;

    public CompanySpinnerAdapter(Context context, List<Company> companyList) {
        super(context, android.R.id.text1, companyList);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView companyNameTextView;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_company_spinner, null);
            companyNameTextView = (TextView) convertView.findViewById(R.id.companyname_textview);
            convertView.setTag(companyNameTextView);
        } else {
            companyNameTextView = (TextView) convertView.getTag();
        }
        Company company = getItem(position);
        companyNameTextView.setText(company.getCompanyName());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        TextView companyNameTextView;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_company_spinner, null);
            companyNameTextView = (TextView) convertView.findViewById(R.id.companyname_textview);
            convertView.setTag(companyNameTextView);
        } else {
            companyNameTextView = (TextView) convertView.getTag();
        }
        Company company = getItem(position);
        companyNameTextView.setText(company.getCompanyName());
        return convertView;
    }


}
