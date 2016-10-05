package com.sysfore.azurepricedetails;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sysfore.azurepricedetails.model.LoginMaster;
import com.sysfore.azurepricedetails.sqlite.helper.DatabaseHelper;

public class LoginActivity extends Activity implements OnClickListener {

	public static final String MYPREFS = "mySharedPreferences";
	int mode = Activity.MODE_PRIVATE;
	Button login;
	EditText txtname, txtpassword;
	TextView registeruser, forgotpassword;
	DatabaseHelper db;
	int counter = 0;
	long subcriptionCount, roleCount;
	String login_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		if (getIntent().getBooleanExtra("EXIT", false)) {
			finish();
		}
		db = new DatabaseHelper(this);
		txtname = (EditText) findViewById(R.id.username);
		txtpassword = (EditText) findViewById(R.id.password);
		registeruser = (TextView) findViewById(R.id.register);
		registeruser.setOnClickListener(this);
		forgotpassword = (TextView) findViewById(R.id.forgotpassword);
		forgotpassword.setOnClickListener(this);
		login = (Button) findViewById(R.id.login);
		login.setOnClickListener(this);

	}

	/**
	 * Checking the user and password
	 */
	private boolean checkuser(String username, String password) {
		String name = "";
		String pass = "";

		List<LoginMaster> getdetails = db.getUserList(username, password);
		boolean ckuser = false;
		if (getdetails.size() > 0) {
			int i = 0;
			while ((ckuser == false) && i <= getdetails.size()) {
				login_id = getdetails.get(i).getLoginId();
				name = getdetails.get(i).getLoginName();
				pass = getdetails.get(i).getPassword();
				SharedPreferences mySharedPreferences = getSharedPreferences(
						MYPREFS, mode);
				SharedPreferences.Editor editor = mySharedPreferences.edit();
				editor.putInt("login_id", Integer.parseInt(login_id));
				editor.commit();

				if (username.equals(name) && password.equals(pass)) {
					ckuser = true;
				}
				i++;
			}
		}

		return ckuser;
	}

	/**
	 * Button Click
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login:
			String username = txtname.getText().toString();
			String password = txtpassword.getText().toString();

			if (username.length() == 0 && password.length() == 0) {
				Toast.makeText(LoginActivity.this,
						"Please fill username and password", Toast.LENGTH_LONG)
						.show();
			} else {
				boolean ck = checkuser(username, password);
				subcriptionCount = db.getSubscriptionCount(login_id);
				Log.d("subcriptionCount", "" + subcriptionCount);

				if (ck && subcriptionCount == 0) {

					Intent i = new Intent(LoginActivity.this,
							SubscriptionActivity.class);
					startActivity(i);
				} else if (ck && subcriptionCount > 0) {
					Intent i = new Intent(LoginActivity.this, Pricemenu.class);
					startActivity(i);
				} else {
					Toast.makeText(LoginActivity.this,
							"Username or Password entered is incorrect!",
							Toast.LENGTH_LONG).show();
					counter = counter + 1;
					Log.d("Counter", "" + counter);
					if (counter == 2) {
						Toast.makeText(
								LoginActivity.this,
								"Last attempt Application will be inactive for 2 hours",
								Toast.LENGTH_LONG).show();
					}
				}

			}
			break;

		case R.id.register:
			Intent ra = new Intent(LoginActivity.this, Registration.class);
			startActivity(ra);
			break;
		case R.id.forgotpassword:
			Intent fp = new Intent(LoginActivity.this, ForgotPassword.class);
			startActivity(fp);
			break;

		}

	}

	/**
	 * Back Pressed
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("EXIT", true);
		startActivity(intent);

	}

}
