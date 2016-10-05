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

public class CoresAdapter extends BaseAdapter {
	
	 Context context;
	    List<HostedService> resourcedetails;

	public CoresAdapter(Context context, List<HostedService> resourcedetails) {
		// TODO Auto-generated constructor stub
		 this.context = context;
	        this.resourcedetails = resourcedetails;
	        Log.d("HostedService", ""+resourcedetails);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return resourcedetails.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return resourcedetails.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		 return resourcedetails.indexOf(getItem(arg0));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.cores_created, null);
        }
        
      
       /* if ( position % 2 == 0)
        	convertView.setBackgroundResource(R.color.d_blue);
          else
        	  convertView.setBackgroundResource(R.color.l_blue);*/
       
        TextView machinename = (TextView) convertView.findViewById(R.id.machine);
      
        TextView region = (TextView) convertView.findViewById(R.id.region);
        TextView resourcetype = (TextView) convertView.findViewById(R.id.resourcetype);
        TextView powerstatus = (TextView) convertView.findViewById(R.id.powerstatus);

        HostedService row_pos = resourcedetails.get(position);
        // setting the image resource and title
        machinename.setText(row_pos.getServiceName());
        region.setText(row_pos.getLocation());
      
        resourcetype.setText(row_pos.getRoleSize());
        powerstatus.setText(row_pos.getPowerState());
        
       

        return convertView;
	}
}
