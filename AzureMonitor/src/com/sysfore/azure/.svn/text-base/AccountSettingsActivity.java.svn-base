package com.sysfore.azure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class AccountSettingsActivity extends FragmentActivity{
	
	
	ListView lv;


	public AccountSettingsActivity() {
	}

	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.account_settings);
	
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
		case R.id.subscription:
			Intent sub = new Intent(AccountSettingsActivity.this,SubscriptionActivity.class);
			startActivity(sub);
			return true;

		case R.id.monitormachine:
			Intent monitormachine = new Intent(AccountSettingsActivity.this,
					MachineMonitorActivity.class);
			startActivity(monitormachine);
			return true;
			
		case R.id.action_resource:
			Intent resource = new Intent(AccountSettingsActivity.this, Dashbaord.class);
			startActivity(resource);
			return true;
		
			

		case R.id.pricemenu:
			Intent price = new Intent(AccountSettingsActivity.this,
					Pricemenu.class);
			startActivity(price);
			return true;

	
		

	case R.id.account_settings:
			Intent accsettings = new Intent(AccountSettingsActivity.this,AccountSettingsActivity.class);
			startActivity(accsettings);
			return true;
			
		case R.id.action_logout:
			Intent home1 = new Intent(AccountSettingsActivity.this, LoginActivity.class);
			startActivity(home1);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}


	

}
