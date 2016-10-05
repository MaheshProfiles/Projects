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
import android.widget.Toast;

import com.sysfore.azurepricedetails.model.LoginMaster;
import com.sysfore.azurepricedetails.sqlite.helper.DatabaseHelper;

public class ChangePassword extends Activity implements OnClickListener {
	public static final String MYPREFS = "mySharedPreferences";
	int mode = Activity.MODE_PRIVATE;
	EditText oldpassword, newpassword, confirmpassword;
	Button passwordsubmit;
	DatabaseHelper db;
	String login_id,subId;
	int loginId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		db = new DatabaseHelper(this);
		setContentView(R.layout.changepassword);
		oldpassword = (EditText) findViewById(R.id.oldpassword);
		newpassword = (EditText) findViewById(R.id.newpassword);
		confirmpassword = (EditText) findViewById(R.id.confirmpassword);
		passwordsubmit = (Button) findViewById(R.id.passowrd_submit);
		passwordsubmit.setOnClickListener(this);
		loadpreferences();
	}
	/**
	 * Login Preferences
	 */
	private void loadpreferences() {
		// TODO Auto-generated method stub
		SharedPreferences mySharedPreferences = getSharedPreferences(MYPREFS,mode);
		loginId = mySharedPreferences.getInt("login_id", 0);
		Log.d("AccountseetingloginId", "" + login_id);
		subId = mySharedPreferences.getString("SubcriptionID", "");

	}
	/**
	 * Button On click
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.passowrd_submit:
			String oldpass = oldpassword.getText().toString();
			String pass = newpassword.getText().toString();
			String confirmpass = confirmpassword.getText().toString();

			if (oldpass.length() == 0 || pass.length() == 0
					|| confirmpass.length() == 0) {
				Toast.makeText(this, "please fill All fields",
						Toast.LENGTH_LONG).show();
			} else {
				boolean ck = checkuser(oldpass);
				if (ck) {
					if (pass.equals(confirmpass)) {
						LoginMaster userList = new LoginMaster();
						userList.setLoginId(Integer.toString(loginId));
						userList.setPassword(pass);

						db.updateLoginUser(userList);
						Toast.makeText(
								this,
								"Password Has been changed.Login Again!",
								Toast.LENGTH_LONG).show();
						  Intent login = new Intent(this,LoginActivity.class); 
						  startActivity(login);
						 

					} else {
						Toast.makeText(
								this,
								"New password and Confirm pasword doesn't match",
								Toast.LENGTH_LONG).show();
					}

				} else {
					Toast.makeText(this,
							"please Enter correct password", Toast.LENGTH_LONG)
							.show();
				}

			}

			break;

		default:
			break;
		}
	}

	/**
	 * Validate old password
	 */
	private boolean checkuser(String password) {

		String pass = "";

		List<LoginMaster> getdetails = db.getchangePassword(password);
		boolean ckuser = false;
		if (getdetails.size() > 0) {
			int i = 0;
			while ((ckuser == false) && i <= getdetails.size()) {

				login_id = getdetails.get(i).getLoginId();
				pass = getdetails.get(i).getPassword();

				if (password.equals(pass)) {
					ckuser = true;
				}
				i++;
			}
		}

		return ckuser;
	}
}
