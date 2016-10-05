package com.sysfore.azure;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CoresCreatedFragment extends Fragment implements OnClickListener {

	
	TextView running,total;
	LinearLayout corecount;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ViewGroup root= (ViewGroup) inflater.inflate(R.layout.cores_created_framelayout, null);
		 setHasOptionsMenu(true);
		 running=(TextView) root.findViewById(R.id.running_cores);
		 total=(TextView) root.findViewById(R.id.total_cores);
		 corecount=(LinearLayout) root.findViewById(R.id.linear_counts);
		 corecount.setOnClickListener(this);
		 getActivity().getActionBar();
		
		 running.setText(""+this.getArguments().getInt("corecreated"));
		 total.setText(this.getArguments().getString("coretotal"));
		return root;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.linear_counts:
			//Toast.makeText(getActivity(), "Testing", Toast.LENGTH_SHORT).show();
			Intent resourcedetails = new Intent(getActivity(),Resource.class);
			startActivity(resourcedetails);
			
			break;

		default:
			break;
		}
		
	}
	

}
