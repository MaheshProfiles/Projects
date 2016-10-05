package com.sysfore.azure;

import java.util.List;

import com.sysfore.azure.model.Subscription;
import com.sysfore.azure.sqlite.helper.DatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class SubscriptionList extends FragmentActivity implements OnItemClickListener{
	
	public static final String MYPREFS = "mySharedPreferences";
	public static final String MYPREF = "mySharedPreferences";
	int mode = Activity.MODE_PRIVATE;
	ListView lv;
	DatabaseHelper db;
	SubscriptionAdpter adapter;
	List<Subscription> sublist;
	int login_id;
	String subId;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.subscription_list);
		db = new DatabaseHelper(this);
		loadpreferences();
		lv=(ListView) findViewById(R.id.subscription_list_item);
		lv.setOnItemClickListener(this);
		sublist=db.getListofSubcription(Integer.toString(login_id));
		adapter = new SubscriptionAdpter(SubscriptionList.this, db.getListofSubcription(Integer.toString(login_id)));
		lv.setAdapter(adapter);
		
	}

	private void loadpreferences() {
		// TODO Auto-generated method stub
		SharedPreferences mySharedPreferences = getSharedPreferences(MYPREFS,mode);
		login_id = mySharedPreferences.getInt("login_id", 0);
		Log.d("login_id", ""+login_id);
		subId = mySharedPreferences.getString("SubcriptionID", "");
		
		
	}

	

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		// TODO Auto-generated method stub
		
		db.updateSetSubscrition(sublist.get(pos).get_ID());
		SharedPreferences mySharedPreferences = getSharedPreferences(MYPREF, mode);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("SubcriptionID", sublist.get(pos).getSubcriptionId());
		
		editor.commit();
		
		Intent dashboard = new Intent(SubscriptionList.this,Dashbaord.class);
		startActivity(dashboard);
		
		
	}
	
	

}
