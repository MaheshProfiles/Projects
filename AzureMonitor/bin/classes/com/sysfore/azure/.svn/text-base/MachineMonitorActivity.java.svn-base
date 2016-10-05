package com.sysfore.azure;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.sysfore.azure.model.HostedService;
import com.sysfore.azure.model.Subscription;
import com.sysfore.azure.sqlite.helper.DatabaseHelper;

public class MachineMonitorActivity extends FragmentActivity implements
		OnClickListener {

	public static final String MYPREFS = "mySharedPreferences";
	int mode = Activity.MODE_PRIVATE;
	ListView monitorlist;
	DatabaseHelper db;
	MonitorMachineAdapter MonitorAdapter;
	List<HostedService> Datalist;
	Button commit;
	int login_id;
	HostedService hs;
	List<Subscription> subcription;
	String subcriptionID, certificateKey, pKeyPassword, rolename,
			retreivedmode, subId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.machine_monitor);
		db = new DatabaseHelper(this);
		MonitorAdapter = new MonitorMachineAdapter(this, Datalist);
		monitorlist = (ListView) findViewById(R.id.monitorstatuslist);

		LayoutInflater inflater = getLayoutInflater();
		ViewGroup footer = (ViewGroup) inflater.inflate(
				R.layout.monitor_machine_footer, monitorlist, false);
		monitorlist.addFooterView(footer, null, false);

		commit = (Button) findViewById(R.id.monitorCommit);
		commit.setOnClickListener(this);
		loadpreferences();
		getSubscriptionDetails();
		MonitorAdapter = new MonitorMachineAdapter(this,
				db.getAllmonitormachineDetails(Integer.toString(login_id),
						subcriptionID));
		monitorlist.setAdapter(MonitorAdapter);
	}

	private void loadpreferences() {
		// TODO Auto-generated method stub
		SharedPreferences mySharedPreferences = this.getSharedPreferences(
				MYPREFS, mode);
		login_id = mySharedPreferences.getInt("login_id", 0);
		Log.d("login_id", "" + login_id);
		subId = mySharedPreferences.getString("SubcriptionID", "");

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.monitorCommit:

			List<HostedService> getmachinedetails = MonitorAdapter
					.getMachinedetails();
			int count = 0;

			for (int j = 0; j < getmachinedetails.size(); j++) {

				if (getmachinedetails.get(j).isSelected() == true) {
					count++;
					// Log.d("j", "" + j);

				}

			}

			if (count == 0) {
				Toast.makeText(getApplicationContext(),
						"Please Select a machine to continue",
						Toast.LENGTH_SHORT).show();

			} else {
				db.cleardata();
				for (int i = 0; i < getmachinedetails.size(); i++) {
					hs = new HostedService();
					hs.setServiceName(getmachinedetails.get(i).getServiceName());
					hs.setLocation(getmachinedetails.get(i).getLocation());
					hs.setPowerState(getmachinedetails.get(i).getPowerState());
					hs.setRoleSize(getmachinedetails.get(i).getRoleSize());
					hs.setSubcriptionId(subcriptionID);
					hs.setCreatedby(Integer.toString(login_id));
					if (getmachinedetails.get(i).isSelected() == true) {
						hs.setMode("Y");
					} else {
						hs.setMode("N");
					}

					db.createMonitorMachine(hs);
					Log.d("hs", "" + hs);
					Intent settings = new Intent(MachineMonitorActivity.this,
							Dashbaord.class);

					startActivity(settings);

				}
				Toast.makeText(getApplicationContext(),
						"Details Successfull entered", Toast.LENGTH_SHORT)
						.show();
			}

			break;

		default:
			break;
		}
	}

	private void getSubscriptionDetails() {
		// TODO Auto-generated method stub
		subcription = db.getAllSubcription(Integer.toString(login_id), subId);
		for (int i = 0; i < subcription.size(); i++) {
			subcriptionID = subcription.get(i).getSubcriptionId();
			certificateKey = subcription.get(i).getCertificateKey();
			pKeyPassword = subcription.get(i).getPassword();
			rolename = subcription.get(i).getRole();

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.home:
			Intent home = new Intent(this, Dashbaord.class);
			startActivity(home);
			return true;

		case R.id.subscriptionmenu:
			Intent subscriptionmenu = new Intent(this,
					SubscriptionActivity.class);
			startActivity(subscriptionmenu);
			return true;

		case R.id.switchsubscription:
			Intent switchsubs = new Intent(this, SubscriptionList.class);
			startActivity(switchsubs);
			return true;

		case R.id.action_resource:
			Intent resource = new Intent(this, Resource.class);
			startActivity(resource);
			return true;

		case R.id.monitormachine:
			Intent monitormachine = new Intent(this,
					MachineMonitorActivity.class);
			startActivity(monitormachine);
			return true;

		case R.id.pricemenu:
			Intent pricemenu = new Intent(this, Pricemenu.class);
			startActivity(pricemenu);
			return true;

		case R.id.account_settings:
			Intent accsettings = new Intent(this, AccountSettingActivity.class);
			startActivity(accsettings);
			return true;

		case R.id.action_logout:
			Intent home1 = new Intent(this, LoginActivity.class);
			startActivity(home1);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
