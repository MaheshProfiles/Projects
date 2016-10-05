package com.snapbizz.snapstock.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snaptoolkit.domains.Distributor;

public class DistributorAdapter extends ArrayAdapter<Distributor> {

    private LayoutInflater layoutInflater;
    private Context context;

    public DistributorAdapter(Context context, List<Distributor> distributorList) {
        super(context, android.R.id.text1, distributorList);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        DistributorAdapterWrapper distributorAdapterWrapper;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listitem_distributor,
                    null);
            distributorAdapterWrapper = new DistributorAdapterWrapper();
            distributorAdapterWrapper.distributorNameTextView = (TextView) convertView.findViewById(R.id.distributor_list_text);
            distributorAdapterWrapper.distributorHeaderTextView = (TextView) convertView.findViewById(R.id.distributor_header_text);
            convertView.setTag(distributorAdapterWrapper);
        } else {
            distributorAdapterWrapper = (DistributorAdapterWrapper) convertView
                    .getTag();
        }
        Distributor distributorData = getItem(position);

        convertView.setBackgroundResource(android.R.color.white);
        distributorAdapterWrapper.distributorNameTextView.setText(distributorData
                .getAgencyName());
        distributorAdapterWrapper.distributorNameTextView.setVisibility(View.VISIBLE);
        distributorAdapterWrapper.distributorHeaderTextView.setVisibility(View.INVISIBLE);

        return convertView;

    }

    private static class DistributorAdapterWrapper {
        TextView distributorNameTextView;
        TextView distributorHeaderTextView;
    }

}
