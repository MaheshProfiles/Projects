package com.sysfore.azure;

import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sysfore.azure.model.HostedService;
import com.sysfore.azure.sqlite.helper.DatabaseHelper;

public class MonitorMachineStatusFragment extends Fragment {
	TextView machine1, machine2, machine3;
	ImageView Status1, Status2, Status3;
	DatabaseHelper db;
	List<HostedService> datalist;
	HostedService hs;
	String count1, count2, count3, machine, machineone, machinetwo,
			machinethree, status, status1, status2, status3, check;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ViewGroup root = (ViewGroup) inflater.inflate(
				R.layout.monitor_machine_framelayout, null);
		setHasOptionsMenu(true);
		machine1 = (TextView) root.findViewById(R.id.machine01);
		machine2 = (TextView) root.findViewById(R.id.machine02);
		machine3 = (TextView) root.findViewById(R.id.machine03);
		Status1 = (ImageView) root.findViewById(R.id.button01);
		Status2 = (ImageView) root.findViewById(R.id.button02);
		Status3 = (ImageView) root.findViewById(R.id.button03);

		getActivity().getActionBar();

		db = new DatabaseHelper(getActivity());

		datalist = db.getAllDashboardDetails();
	
		for (int i = 0; i < datalist.size(); i++) {
			if (i == 0) {
				machine1.setText(datalist.get(i).getServiceName());
				// check=datalist.get(i).getPowerState();
				status1 = datalist.get(i).getPowerState();
				if (status1.equals("Started")) {
					Status1.setImageResource(R.drawable.indicator_green);

				} else {
					Status1.setImageResource(R.drawable.indicator_red);

				}
			} else if (i == 1) {

				machine2.setText(datalist.get(i).getServiceName());
				status2 = datalist.get(i).getPowerState();
				if (status2.equals("Started")) {
					Status2.setImageResource(R.drawable.indicator_green);

				} else {
					Status2.setImageResource(R.drawable.indicator_red);

				}

			} else if (i == 2) {

				machine3.setText(datalist.get(i).getServiceName());
				status3 = datalist.get(i).getPowerState();
				if (status3.equals("Started")) {
					Status3.setImageResource(R.drawable.indicator_green);

				} else {
					Status3.setImageResource(R.drawable.indicator_red);

				}
			}	

		}

		return root;
	}

}
