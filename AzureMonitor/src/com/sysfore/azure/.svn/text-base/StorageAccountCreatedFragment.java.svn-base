package com.sysfore.azure;

import android.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StorageAccountCreatedFragment extends Fragment {

	TextView running,total;
	
	
	
	



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ViewGroup root= (ViewGroup) inflater.inflate(R.layout.storage_acc_created_framelayout, null);
		 setHasOptionsMenu(true);
		 running=(TextView) root.findViewById(R.id.storage_running_cores);
		 total=(TextView) root.findViewById(R.id.storage_total_cores);
		 running.setText(this.getArguments().getString("storagecreated"));
		 total.setText(this.getArguments().getString("storagetotal"));
		getActivity().getActionBar();
		return root;
	}
	

}
