package com.sysfore.azure;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sysfore.azure.model.Subscription;

public class SubscriptionAdpter  extends BaseAdapter {
	
	 Context context;
	    List<Subscription> subscriptiondetails;

	public SubscriptionAdpter(Context context, List<Subscription> subscriptiondetails) {
		// TODO Auto-generated constructor stub
		 this.context = context;
	        this.subscriptiondetails = subscriptiondetails;
	        Log.d("subscriptiondetails", ""+subscriptiondetails);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return subscriptiondetails.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return subscriptiondetails.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		 return subscriptiondetails.indexOf(getItem(arg0));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
       if (convertView == null) {
           LayoutInflater mInflater = (LayoutInflater) context
                   .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
           convertView = mInflater.inflate(R.layout.subscription_list_row, null);
       }
       
     
      /* if ( position % 2 == 0)
       	convertView.setBackgroundResource(R.color.d_blue);
         else
       	  convertView.setBackgroundResource(R.color.l_blue);*/
      
       TextView subscriptions = (TextView) convertView.findViewById(R.id.subcription_name);
     
       

       Subscription row_pos = subscriptiondetails.get(position);
       // setting the image resource and title
       subscriptions.setText(row_pos.getSubcriptionName());
     
       
      

       return convertView;
	}
}
