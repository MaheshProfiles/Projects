package com.sysfore.azure;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sysfore.azure.model.Subscription;
import com.sysfore.azure.sqlite.helper.DatabaseHelper;

public class DeleteAdapter extends BaseAdapter {
	
	 Context context;
	    List<Subscription> subcriptiondetails;

	public DeleteAdapter(Context context, List<Subscription> subcriptiondetails) {
		// TODO Auto-generated constructor stub
		 this.context = context;
	        this.subcriptiondetails = subcriptiondetails;
	        Log.d("subcriptiondetails", ""+subcriptiondetails);
	}
	
	public void updateSubscriptionList(List<Subscription> subcriptiondetails) {
		subcriptiondetails.clear();
		subcriptiondetails.addAll(subcriptiondetails);
	    this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return subcriptiondetails.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return subcriptiondetails.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		 return subcriptiondetails.indexOf(getItem(arg0));
	}

	@Override
	public View getView( final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
       if (convertView == null) {
           LayoutInflater mInflater = (LayoutInflater) context
                   .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
           convertView = mInflater.inflate(R.layout.delete_account_listrow, null);
       }

      
       TextView txtTitle = (TextView) convertView.findViewById(R.id.subcriptionnamerow);
       ImageView del = (ImageView) convertView.findViewById(R.id.deleteimage);
      

       Subscription row_pos = subcriptiondetails.get(position);
       // setting the image resource and title
       del.setTag(row_pos.get_ID());
      
       txtTitle.setText(row_pos.getSubcriptionName());
       
       
       del.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 AlertDialog.Builder adb = new AlertDialog.Builder(context);
			    adb.setTitle("Delete?");
			    adb.setMessage("Are you sure you want to delete ?");
			    final int user_id = Integer.parseInt(v.getTag().toString());
			    adb.setNegativeButton("Cancel", null);
			    adb.setPositiveButton("Ok",
				    new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
						int which) {
					    // MyDataObject.remove(positionToRemove);
					    DatabaseHelper dBHandler = new DatabaseHelper(context.getApplicationContext());
					    Log.d("Resource usage subcriptionid********", ""+subcriptiondetails.get(position).getSubcriptionId());
					    Log.d("Resource usage loginid********", ""+subcriptiondetails.get(position).getCreatedby());
					    dBHandler.clearResourceUsage(subcriptiondetails.get(position).getSubcriptionId(),subcriptiondetails.get(position).getCreatedby());
					  
					    dBHandler.Delete_Contact(user_id);
					   
					  

					}
				    });
			    adb.show();
			
			
		}
	});
       

       return convertView;
	}
}