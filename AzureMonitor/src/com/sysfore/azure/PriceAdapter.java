package com.sysfore.azure;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sysfore.azure.model.HostedService;
import com.sysfore.azure.model.ResultPojo;

public class PriceAdapter extends BaseAdapter {
	
	 Context context;
	    List<ResultPojo> pricedetail;

	public PriceAdapter(Context context, List<ResultPojo> pricedetail) {
		// TODO Auto-generated constructor stub
		 this.context = context;
	        this.pricedetail = pricedetail;
	        Log.d("pricedetail", ""+pricedetail);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return pricedetail.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return pricedetail.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		 return pricedetail.indexOf(getItem(arg0));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.price_detail_list_row, null);
        }
        
      
       /* if ( position % 2 == 0)
        	convertView.setBackgroundResource(R.color.d_blue);
          else
        	  convertView.setBackgroundResource(R.color.l_blue);*/
       
        TextView product = (TextView) convertView.findViewById(R.id.product);
      
        TextView tariff = (TextView) convertView.findViewById(R.id.tariff);
        TextView unit = (TextView) convertView.findViewById(R.id.unit);
        TextView consumed = (TextView) convertView.findViewById(R.id.consumed);
        TextView total = (TextView) convertView.findViewById(R.id.total);

        ResultPojo row_pos = pricedetail.get(position);
        // setting the image resource and title
        product.setText(row_pos.getProduct());
        tariff.setText(row_pos.getPerUnitCommitted());
       
        unit.setText( row_pos.getUnitofMeasure());
        consumed.setText(row_pos.getUnitConsumed());
        total.setText(row_pos.getBillTotal());
        
       

        return convertView;
	}
}
