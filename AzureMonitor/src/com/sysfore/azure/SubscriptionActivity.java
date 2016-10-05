package com.sysfore.azure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
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

import com.sysfore.azure.model.Subscription;
import com.sysfore.azure.sqlite.helper.DatabaseHelper;

public class SubscriptionActivity extends FragmentActivity implements
		OnClickListener, OnItemSelectedListener {

	private static final int REQUEST_PATH = 1;
	public static final String MYPREF = "mySharedPreferences";
	public static final String MYPREFS = "mySharedPreferences";
	int mode = Activity.MODE_PRIVATE;
	EditText subscriptionName, subscriptionID, password, token, enrollmentID;
	Spinner role, versions;
	Button clear, cancel, add, certificateKey;
	ArrayAdapter<CharSequence> securityQuestion;
	DatabaseHelper db;
	Subscription subscription;
	String selectedrole, roles, formattedDate, targetedfile, version,
			selectedmode;
	int login_id;
	long upstatus = 0;
	String curFileName;
	Calendar c;
	private static final int REQUEST_PICK_FILE = 1;
	private File selectedFile;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.subcription_detail);
		db = new DatabaseHelper(this);
		subscriptionName = (EditText) findViewById(R.id.subscriptionname);
		subscriptionID = (EditText) findViewById(R.id.subscription);
		// subscriptionID.setText("61017d56-bc79-4bae-a431-52bd0aa2facc");
		subscriptionID.setText("fc7b7fb1-2b20-4fdc-addb-42abd01f1317");
		certificateKey = (Button) findViewById(R.id.certificate);
		certificateKey.setOnClickListener(this);
		password = (EditText) findViewById(R.id.password);
		token = (EditText) findViewById(R.id.token);
		// token.setText("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Il92ZHZieDJYRUkyb2l5N09abVpVelZGelM2TSJ9.eyJpc3MiOiJlYS5taWNyb3NvZnRhenVyZS5jb20iLCJhdWQiOiJjbGllbnQuZWEubWljcm9zb2Z0YXp1cmUuY29tIiwibmJmIjoxNDEyOTI3Mzg3LCJleHAiOjE0Mjg2NTIxODcsIkVucm9sbG1lbnROdW1iZXIiOiI3ODMxMjk5IiwiSWQiOiI2ODI0ODZiOC0xYzdiLTQwYzktODljMi0yOTI4MzEyZDBkZTAiLCJSZXBvcnRWaWV3IjoiSW5kaXJlY3RFbnRlcnByaXNlIiwiUGFydG5lcklkIjoiIn0.ZLMiknPv_TTkZH5tpUHTZsiIJGAyuZ0hfMoo6iKyMicAiUFfPIl5Qh6lPrPmN9z9rqQVrJ87P-8p2qslxTXL3g6rkw-TilXrNeyq7Y0iJhL72rdZu0asD6He2NnySJQkaLGfz1NhqInrMiauHDugVo2uxFwIIT8BRX3ilF9ODntVVW-0MaaEAmn8aO-1sGABNdBoBIOvV8MfmZOPujh50L9Ahdt09nqyLec_dqJloeAA0oXNj88XbLuwj7dijV3REeOp_azMBYjyRwkpUwr3Y2tM4z966wJ_7iQLYr0ukZq7R3Yw6iaM2glBZW_gaxNk9iUYRhisLsmAQLFbAeBZ6Q");
		token.setText("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Il92ZHZieDJYRUkyb2l5N09abVpVelZGelM2TSJ9.eyJpc3MiOiJlYS5taWNyb3NvZnRhenVyZS5jb20iLCJhdWQiOiJjbGllbnQuZWEubWljcm9zb2Z0YXp1cmUuY29tIiwibmJmIjoxNDEwMzMyMDA2LCJleHAiOjE0MjU5NzA0MDYsIkVucm9sbG1lbnROdW1iZXIiOiI3OTU5MzczMyIsIklkIjoiYjFhNzg2ZWQtNDRjMS00YTgzLWE3OTgtZmZlNzM4ZjRiYTIxIiwiUmVwb3J0VmlldyI6IkluZGlyZWN0RW50ZXJwcmlzZSIsIlBhcnRuZXJJZCI6IiJ9.KWbjky_ekLicwn8_zvMjagNxCao8yGfs9jaSOPnaQtJMRQRFFOaPptCyb0bCIfpDe-9-M1VaUtx2LUOq3jvr9obdBmmaHOdaCr_-ITDHqhYYHPROI3-lTx3L8v4aDu2tbLMQRbIXxH_ZroZpt08QgJOBmob56AFUtuqM4Qo-wvfg91OqP9Waf9Dl_fezX4dUViqAnZxiQi-DuVs3ggiE2GyeMs_o8GmINidJQSREznqWnT3oVDSsOD7zPJdcho3Dvi8SlejhiBi7MUAB6GpJbiWACtHLUmHS0Cwwi6i8iyprDnPVrmaGJ3Yu9SpYEyGq-K7MfeRvGVBEWvGqkrKHqw");

		enrollmentID = (EditText) findViewById(R.id.enrolmentID);
		// enrollmentID.setText("7831299");
		enrollmentID.setText("79593733");

		versions = (Spinner) findViewById(R.id.versions);
		versions.setOnItemSelectedListener(this);
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

		loadpreferences();
		loadspinner();
	}

	private void loadpreferences() {
		// TODO Auto-generated method stub

		SharedPreferences mySharedPreferences = getSharedPreferences(MYPREFS,
				mode);
		login_id = mySharedPreferences.getInt("login_id", 0);
		Log.d("login_id", "" + login_id);
	}

	private void loadspinner() {
		// TODO Auto-generated method stub
		/*
		 * List<AddNewRole> roles =db.getRoleName(Integer.toString(login_id));
		 * Log.d("roles", ""+roles); ArrayAdapter<AddNewRole> adapter = new
		 * ArrayAdapter<AddNewRole>(getActivity(),
		 * R.layout.spinner_text_layout2, roles); role.setAdapter(adapter);
		 */

		securityQuestion = ArrayAdapter.createFromResource(
				SubscriptionActivity.this, R.array.versions,
				R.layout.spinner_text_layout2);
		versions.setAdapter(securityQuestion);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		
		
		case R.id.switchsubscription:
			Intent switchsub = new Intent(this, SubscriptionList.class);
			startActivity(switchsub);
			return true;

		case R.id.subscription:
			Intent sub = new Intent(this, SubscriptionFragment.class);
			startActivity(sub);
			return true;

		case R.id.monitormachine:
			Intent monitormachine = new Intent(this,
					MachineMonitorActivity.class);
			startActivity(monitormachine);
			return true;

		case R.id.action_resource:
			Intent resource = new Intent(this, Resource.class);
			startActivity(resource);
			return true;

		case R.id.pricemenu:
			Intent price = new Intent(this, Pricemenu.class);
			startActivity(price);
			return true;

		case R.id.home:

			Intent home = new Intent(this, Dashbaord.class);
			startActivity(home);
			return true;

		case R.id.account_settings:
			Intent sign = new Intent(this, AccountSettingActivity.class);
			startActivity(sign);
			return true;
		case R.id.action_logout:
			Intent home1 = new Intent(this, LoginActivity.class);
			startActivity(home1);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.clear:
			Toast.makeText(SubscriptionActivity.this, "clear",
					Toast.LENGTH_SHORT).show();

			break;
		case R.id.cancel:
			Toast.makeText(SubscriptionActivity.this, "cancel",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.add:

			if (version.equalsIgnoreCase("Select")) {
				Toast.makeText(SubscriptionActivity.this,
						"please select mode from drop down", Toast.LENGTH_SHORT)
						.show();
				break;
			}

			if (version.equalsIgnoreCase("EA")
					&& token.getText().toString().length() == 0
					&& enrollmentID.getText().toString().length() == 0) {
				Toast.makeText(SubscriptionActivity.this,
						"please scroll to fill Token and EnrollmentID fields ",
						Toast.LENGTH_SHORT).show();
				break;
			}

			if (subscriptionID.getText().toString().length() == 0
					|| password.getText().toString().length() == 0
					|| subscriptionName.getText().toString().length() == 0) {
				Toast.makeText(SubscriptionActivity.this,
						"Please Fill all Data", Toast.LENGTH_SHORT).show();
				break;
			}

			File sourceFile = new File(certificateKey.getText().toString());
			try {
				copyDirectoryOneLocationToAnotherLocation(sourceFile,
						targetedfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			subscription = new Subscription();
			subscription.setSubcriptionName(subscriptionName.getText()
					.toString());
			subscription.setSubcriptionId(subscriptionID.getText().toString());
			subscription.setCertificateKey(targetedfile);
			subscription.setPassword(password.getText().toString());

			subscription.setSelection(selectedmode);
			subscription.setToken(token.getText().toString());
			subscription.setEmplomentID(enrollmentID.getText().toString());
			subscription.setIsDefault(1);
			subscription.setCreatedby(Integer.toString(login_id));
			upstatus = db.createSubcription(subscription);

			SharedPreferences mySharedPreferences = getSharedPreferences(
					MYPREF, mode);
			SharedPreferences.Editor editor = mySharedPreferences.edit();
			editor.putString("SubcriptionID", subscriptionID.getText()
					.toString());

			editor.commit();

			if (upstatus == 0) {
				Toast.makeText(SubscriptionActivity.this, "Update fail",
						Toast.LENGTH_SHORT).show();
			} else {

				Toast.makeText(SubscriptionActivity.this,
						"Updated Successfully", Toast.LENGTH_SHORT).show();

			}
			Intent dashboard = new Intent(SubscriptionActivity.this,
					Dashbaord.class);

			startActivity(dashboard);

			break;

		case R.id.certificate:
			Intent intent1 = new Intent(SubscriptionActivity.this,
					Filepicker.class);
			startActivityForResult(intent1, REQUEST_PATH);

			break;

		default:
			break;
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch (parent.getId()) {

		case R.id.versions:
			version = versions.getSelectedItem().toString();

			if (version.equalsIgnoreCase("EA")) {
				selectedmode = versions.getSelectedItem().toString();
				token.setVisibility(View.VISIBLE);
				enrollmentID.setVisibility(View.VISIBLE);

			} else {
				selectedmode = versions.getSelectedItem().toString();
				token.setVisibility(View.GONE);
				enrollmentID.setVisibility(View.GONE);
			}

		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	public void copyDirectoryOneLocationToAnotherLocation(File sourceLocation,
			String targetFile) throws IOException {

		/*
		 * if (sourceLocation.isDirectory()) { if (!targetLocation.exists()) {
		 * targetLocation.mkdir(); }
		 * 
		 * String[] children = sourceLocation.list(); for (int i = 0; i <
		 * sourceLocation.listFiles().length; i++) {
		 * 
		 * copyDirectoryOneLocationToAnotherLocation(new File(sourceLocation,
		 * children[i]), new File(targetLocation, children[i])); } } else {
		 */

		try {
			InputStream in = new FileInputStream(sourceLocation);

			FileOutputStream fos;
			fos = openFileOutput(targetFile, Context.MODE_PRIVATE);
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

	// }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {

			switch (requestCode) {

			case REQUEST_PICK_FILE:

				if (data.hasExtra(Filepicker.EXTRA_FILE_PATH)) {

					selectedFile = new File(
							data.getStringExtra(Filepicker.EXTRA_FILE_PATH));
					certificateKey.setText(selectedFile.getPath());
				}
				break;
			}
		}
	}

}
