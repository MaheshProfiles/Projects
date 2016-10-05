package com.sysfore.azurepricedetails;

import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sysfore.azurepricedetails.model.LoginMaster;
import com.sysfore.azurepricedetails.sqlite.helper.DatabaseHelper;

public class Registration extends Activity implements OnClickListener,
		OnItemSelectedListener, TextWatcher {
	private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,4})$";
	private static final String EMAIL_MSG = "invalid email";
	private static final String REQUIRED_MSG = "Valid Email";
	TextView signup;
	Spinner question;
	EditText username, email, password, confirmPassword, securityAnswer;
	Button register;
	ArrayAdapter<CharSequence> securityQuestion;
	DatabaseHelper db;
	LoginMaster master;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		db = new DatabaseHelper(this);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		confirmPassword = (EditText) findViewById(R.id.confirm_password);
		securityAnswer = (EditText) findViewById(R.id.securit_answer);
		signup = (TextView) findViewById(R.id.signupscreen);
		signup.setOnClickListener(this);
		email = (EditText) findViewById(R.id.email);
		email.addTextChangedListener(this);
		question = (Spinner) findViewById(R.id.security_question);
		question.setOnItemSelectedListener(this);
		register = (Button) findViewById(R.id.register);
		register.setOnClickListener(this);
		loadSpinner();
	}

	/**
	 * Validating Email
	 */
	public static boolean isEmailAddress(EditText editText, boolean required) {
		return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required);
	}

	/**
	 * Set error if email not in format
	 */
	// return true if the input field is valid, based on the parameter passed
	public static boolean isValid(EditText editText, String regex,
			String errMsg, boolean required) {

		String text = editText.getText().toString().trim();
		// clearing the error, if it was previously set by some other values
		editText.setError(null);

		// text required and editText is blank, so return false
		if (required && !hasText(editText))
			return false;

		// pattern doesn't match so returning false
		if (required && !Pattern.matches(regex, text)) {
			editText.setError(Html
					.fromHtml("<font color='red'>Invalid email</font>"));
			return false;
		}
		;

		return true;
	}

	public static boolean hasText(EditText editText) {

		String text = editText.getText().toString().trim();
		editText.setError(null);

		// length 0 means there is no text
		if (text.length() == 0) {
			editText.setError(REQUIRED_MSG);
			return false;
		}

		return true;
	}

	/**
	 * Insert Questions to drop down
	 */
	private void loadSpinner() {
		// TODO Auto-generated method stub
		securityQuestion = ArrayAdapter.createFromResource(this,
				R.array.options, R.layout.spinner_text_layout2);
		question.setAdapter(securityQuestion);

	}

	/**
	 * Button On click
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.signupscreen:
			Intent ra = new Intent(Registration.this, LoginActivity.class);
			startActivity(ra);
			break;

		case R.id.register:

			String userNam = db.getAllEntry(username.getText().toString());
			if (username.getText().toString().length() == 0
					|| email.getText().toString().length() == 0
					|| password.getText().toString().length() == 0
					|| confirmPassword.getText().toString().length() == 0
					|| securityAnswer.getText().toString().length() == 0) {
				Toast.makeText(this, "Please Fill all Data", Toast.LENGTH_SHORT)
						.show();
				return;
			} else if (username.getText().toString().equals(userNam)) {
				Toast.makeText(getApplicationContext(),
						"UserName Already Exists", Toast.LENGTH_LONG).show();
				return;
			}

			// check if both password matches
			else if (!password.getText().toString()
					.equals(confirmPassword.getText().toString())) {
				Toast.makeText(getApplicationContext(),
						"Password does not match", Toast.LENGTH_LONG).show();
				return;
			} else {
				// Save the Data in Database

				Toast.makeText(getApplicationContext(),
						"Account Successfully Created ", Toast.LENGTH_LONG)
						.show();
				master = new LoginMaster();
				master.setLoginName(username.getText().toString());
				master.setPassword(password.getText().toString());
				master.setEmail(email.getText().toString());
				master.setOfflinemode("N");
				db.createLoginUser(master);
				Intent login = new Intent(Registration.this,
						LoginActivity.class);
				startActivity(login);
				break;

			}

		default:
			break;
		}

	}

	/**
	 * Spinner Selected
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

		switch (parent.getId()) {
		case R.id.security_question:
			String securityQuestion = question.getSelectedItem().toString();
			Log.d("optionDetails", securityQuestion);
			if (!securityQuestion
					.equalsIgnoreCase("Select Your Security Question")) {
				securityAnswer.setVisibility(View.VISIBLE);
			} else {
				securityAnswer.setVisibility(View.GONE);
			}

		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * Checking the email format after entered
	 */
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		isEmailAddress(email, true);

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

}
