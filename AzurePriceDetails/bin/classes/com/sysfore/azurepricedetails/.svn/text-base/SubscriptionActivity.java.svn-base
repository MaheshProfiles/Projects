package com.sysfore.azurepricedetails;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.sysfore.azurepricedetails.modal.AzureUtil;
import com.sysfore.azurepricedetails.model.Subscription;
import com.sysfore.azurepricedetails.sqlite.helper.DatabaseHelper;

public class SubscriptionActivity extends FragmentActivity implements
		OnClickListener, OnItemSelectedListener {

	public static final String MYPREFS = "mySharedPreferences";
	int mode = Activity.MODE_PRIVATE;
	EditText accountName, subscriptionID, password, token, enrollmentID;
	Spinner role, versions;
	Button clear, cancel, add, certificateKey;
	ArrayAdapter<CharSequence> securityQuestion;
	DatabaseHelper db;
	List<Subscription> subcription;
	Subscription subscription;
	String selectedrole, roles, formattedDate, targetedfile, version,
			selectedmode, subcriptionID, subcriptionname;
	int login_id;
	long upstatus = 0;
	String curFileName;
	Calendar c;

	public SubscriptionActivity() {

	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		setContentView(R.layout.subcription_detail);
		db = new DatabaseHelper(this);
		accountName = (EditText) findViewById(R.id.subscriptionname);
		token = (EditText) findViewById(R.id.token);
		token.setText("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Il92ZHZieDJYRUkyb2l5N09abVpVelZGelM2TSJ9.eyJpc3MiOiJlYS5taWNyb3NvZnRhenVyZS5jb20iLCJhdWQiOiJjbGllbnQuZWEubWljcm9zb2Z0YXp1cmUuY29tIiwibmJmIjoxNDEwMzMyMDA2LCJleHAiOjE0MjU5NzA0MDYsIkVucm9sbG1lbnROdW1iZXIiOiI3OTU5MzczMyIsIklkIjoiYjFhNzg2ZWQtNDRjMS00YTgzLWE3OTgtZmZlNzM4ZjRiYTIxIiwiUmVwb3J0VmlldyI6IkluZGlyZWN0RW50ZXJwcmlzZSIsIlBhcnRuZXJJZCI6IiJ9.KWbjky_ekLicwn8_zvMjagNxCao8yGfs9jaSOPnaQtJMRQRFFOaPptCyb0bCIfpDe-9-M1VaUtx2LUOq3jvr9obdBmmaHOdaCr_-ITDHqhYYHPROI3-lTx3L8v4aDu2tbLMQRbIXxH_ZroZpt08QgJOBmob56AFUtuqM4Qo-wvfg91OqP9Waf9Dl_fezX4dUViqAnZxiQi-DuVs3ggiE2GyeMs_o8GmINidJQSREznqWnT3oVDSsOD7zPJdcho3Dvi8SlejhiBi7MUAB6GpJbiWACtHLUmHS0Cwwi6i8iyprDnPVrmaGJ3Yu9SpYEyGq-K7MfeRvGVBEWvGqkrKHqw");
		enrollmentID = (EditText) findViewById(R.id.enrolmentID);
		enrollmentID.setText("79593733");
		clear = (Button) findViewById(R.id.clear);
		clear.setOnClickListener(this);
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		add = (Button) findViewById(R.id.add);
		add.setOnClickListener(this);
		c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		formattedDate = df.format(c.getTime());
		targetedfile = "Azure " + formattedDate + ".pfx";
		Log.d("targetedfile", "" + targetedfile);
		securityQuestion = ArrayAdapter.createFromResource(this,
				R.array.versions, R.layout.spinner_text_layout2);
		// versions.setAdapter(securityQuestion);
		loadpreferences();
		getSubscriptionDetails();

	}

	/**
	 * Retrieving Login id Preferences
	 */
	private void loadpreferences() {
		// TODO Auto-generated method stub

		SharedPreferences mySharedPreferences = getSharedPreferences(MYPREFS,
				mode);
		login_id = mySharedPreferences.getInt("login_id", 0);
		Log.d("login_id", "" + login_id);
	}

	/**
	 * Getting SubscriptionDetails
	 */
	private void getSubscriptionDetails() {
		// TODO Auto-generated method stub
		subcription = db.getAllSubcription(Integer.toString(login_id));
		for (int i = 0; i < subcription.size(); i++) {
			subcriptionID = subcription.get(i).getSubcriptionId();
			subcriptionname = subcription.get(i).getSubcriptionName();
		}
		long subsname = db.getSubscriptionCount(Integer.toString(login_id));
		if (subsname >= 0) {

			accountName.setText(AzureUtil.decrypt(subcriptionname));
		} else {
			accountName.setText("");
		}
	}

	/**
	 * Menu selected
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.action_home:

			Intent home = new Intent();
			startActivity(home);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Button On click
	 */
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.clear:
			Toast.makeText(this.getApplicationContext(), "clear",
					Toast.LENGTH_SHORT).show();
			accountName.setText("");
			// token.setText("");
			// enrollmentID.setText("");

			break;
		case R.id.cancel:
			Toast.makeText(getApplicationContext(), "cancel",
					Toast.LENGTH_SHORT).show();

			break;
		case R.id.add:

			if (accountName.getText().toString().length() == 0
					&& token.getText().toString().length() == 0
					&& enrollmentID.getText().toString().length() == 0) {
				Toast.makeText(getApplicationContext(), "Please Fill All Data",
						Toast.LENGTH_SHORT).show();
				break;
			}

			subscription = new Subscription();

			subscription.setSubcriptionName(AzureUtil.encrypt(accountName
					.getText().toString()));
			subscription
					.setToken(AzureUtil.encrypt(token.getText().toString()));
			subscription.setEmplomentID(AzureUtil.encrypt(enrollmentID
					.getText().toString()));
			subscription.setCreatedby(Integer.toString(login_id));
			upstatus = db.createSubcription(subscription);

			Intent dashboard = new Intent(this, Pricemenu.class);

			startActivity(dashboard);

			break;

		default:

			break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Writing file
	 */
	public void copyDirectoryOneLocationToAnotherLocation(File sourceLocation,
			String targetFile) throws IOException {

		try {
			InputStream in = new FileInputStream(sourceLocation);

			FileOutputStream fos;
			fos = getApplicationContext().openFileOutput(targetFile,
					Context.MODE_PRIVATE);
			// openFileOutput(targetFile, Context.MODE_PRIVATE);
			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				fos.write(buf, 0, len);
			}
			in.close();
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	// }

}
