package com.sysfore.azure;

import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sysfore.azure.modal.GMailSender;
import com.sysfore.azure.model.LoginMaster;
import com.sysfore.azure.sqlite.helper.DatabaseHelper;

public class ForgotPassword extends Activity {

	EditText username, email;
	Button submit;
	String mailid;
	GMailSender mailsender;
	DatabaseHelper db;
	String password;
	long passcount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password);
		email = (EditText) findViewById(R.id.forgot_security_answer);
		db = new DatabaseHelper(this);
		username = (EditText) findViewById(R.id.forgot_security_question);
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mailid = email.getText().toString();
				Log.d("MAilID+++++++++++", mailid);
				List<LoginMaster> pass = db.getforgetpassword(username.getText()
						.toString(), mailid);
				for (int i = 0; i < pass.size(); i++) {
					password = pass.get(i).getPassword();
				}
				passcount = db.getforgotpasswordCount(username.getText()
						.toString(), mailid);
				if (passcount > 0) {
					new sendmailgmail().execute();
					Toast.makeText(
							getApplicationContext(),
							"A Recovery Email is sent to you,See it for more details",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(
							getApplicationContext(),
							"Username or Mail-id Not Matching.Please Try Correctly",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	public class sendmailgmail extends AsyncTask<Void, String, Void> implements
			OnCancelListener {

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub

		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Intent reset = new Intent(ForgotPassword.this, LoginActivity.class);
			startActivity(reset);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			mailsender = new GMailSender("sysazurea@gmail.com", "Sys@2010");

			String[] toArr = { mailid };
			mailsender.set_to(toArr);
			mailsender.set_from("sysazurea@gmail.com");
			mailsender.set_subject("Reset Your Password.");
			mailsender.setBody("Hello,"+"\n"
					+ "\nThe Account has been sent with the password." + "\n"
					+ "\n" + "Please check below." + "\n" + "\n"
					+ "The Password is: " + password + "\n" + "\n"
					+ "Thank You For Using Azure Service");
			try {

				if (mailsender.send()) {
					Log.d("Email was sent successfully.",
							"Email was sent successfully.");
					// Toast.makeText(getApplicationContext(),
					// "Email was sent successfully.", Toast.LENGTH_LONG)
					// .show();
				} else {
					Log.d("Email was not sent.", "Email was not sent.");
					// Toast.makeText(getApplicationContext(),
					// "Email was not sent.", Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {

				Log.e("MailApp", "Could not send email", e);
			}
			return null;
		}
	}

}
