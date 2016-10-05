package com.sysfore.azure;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

public class Resource extends FragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.resource);
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		// ResourceUsageDetailsActivity cores = new
		// ResourceUsageDetailsActivity();
		ResourceVmUsageDetails vmusage = new ResourceVmUsageDetails();

		// fragmentTransaction.replace(R.id.My_Container_ID, cores);
		fragmentTransaction.replace(R.id.My_Container_ID1, vmusage);

		fragmentTransaction.commit();
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

		case R.id.subscriptionmenu:
			Intent subscriptionmenu = new Intent(Resource.this,
					SubscriptionActivity.class);
			startActivity(subscriptionmenu);
			return true;

		case R.id.home:
			Intent home = new Intent(Resource.this, Dashbaord.class);
			startActivity(home);
			return true;

		case R.id.subscription:
			Intent sub = new Intent(Resource.this, SubscriptionFragment.class);
			startActivity(sub);
			return true;

		case R.id.monitormachine:
			Intent monitormachine = new Intent(Resource.this,
					MachineMonitorActivity.class);
			startActivity(monitormachine);
			return true;

		case R.id.action_resource:
			Intent resource = new Intent(Resource.this, Resource.class);
			startActivity(resource);
			return true;

		case R.id.pricemenu:
			Intent price = new Intent(Resource.this, Pricemenu.class);
			startActivity(price);
			return true;

		case R.id.account_settings:
			Intent accsettings = new Intent(Resource.this,
					AccountSettingActivity.class);
			startActivity(accsettings);
			return true;

		case R.id.action_logout:
			Intent home1 = new Intent(Resource.this, LoginActivity.class);
			startActivity(home1);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
