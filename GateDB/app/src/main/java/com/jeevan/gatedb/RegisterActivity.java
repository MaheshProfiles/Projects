package com.jeevan.gatedb;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.jeevan.GateModel.OTPMaster;
import com.jeevan.database.DBHelper;
import com.jeevan.provider.OTP_Generation;
import com.jeevan.provider.UtilsProvider;
import com.jeevan.sender.GMailSender;

import java.util.Date;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button otp, register;
    private TextInputLayout inputLayoutEntercode;
    private TextInputLayout inputLayoutEnterEmail;
    private TextInputLayout inputLayoutEnterMobileNo;
    private TextInputLayout inputLayoutEnterOtp;

    private EditText inputEntercode;
    private UtilsProvider utils;
    private Activity mActivity;
    private EditText inputEnterEmail;
    private EditText inputEnterMobileNo;
    private EditText inputEnterOtp;
    private DBHelper db;
    private CheckBox checkTermsConditions;
    private String emailid;
    private String sOtp;
    private GMailSender mailsender;
    String tempOtp;
    String email1;
    String mobileno;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return false;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mActivity = RegisterActivity.this;
        utils = new UtilsProvider(mActivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Register");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DBHelper(mActivity);
        db = new DBHelper(RegisterActivity.this);
//
        inputLayoutEnterEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputEnterEmail = (EditText) findViewById(R.id.input_email);
        inputLayoutEnterMobileNo = (TextInputLayout) findViewById(R.id.input_layout_mob1);
        inputEnterMobileNo = (EditText) findViewById(R.id.input_mob1);
        inputLayoutEnterOtp = (TextInputLayout) findViewById(R.id.input_layout_otp);
        inputEnterOtp = (EditText) findViewById(R.id.input_otp);
        otp = (Button) findViewById(R.id.otp);
        checkTermsConditions = (CheckBox) findViewById(R.id.checkTc);
        register = (Button) findViewById(R.id.reg);
        otp.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.otp:
                emailid = inputEnterEmail.getText().toString();
                String vUser = db.getValidUser(inputEnterEmail.getText().toString(), inputEnterMobileNo.getText().toString());
                if (!emailid.isEmpty() && emailid != null) {
                    email1 = inputEnterEmail.getText().toString();
                    mobileno = inputEnterMobileNo.getText().toString();
                    new sendmailgmail().execute();
                } else if (!(vUser.equals(email1) || vUser.equals(mobileno))) {
                    Snackbar.make(view, "User already exists", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                Snackbar.make(view, "Please Enter Valid Email/MobileNo", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
            case R.id.reg:
                if (inputEnterEmail.getText().toString().isEmpty()
                        && inputEnterMobileNo.getText().toString().isEmpty()
                        && inputEnterOtp.getText().toString().isEmpty()) {
                    Snackbar.make(view, "Fields Cannot be empty", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else if (inputEnterOtp.getText().toString().length() < 6
                        || (inputEnterOtp.getText().toString().length() < 6)) {
                    Snackbar.make(view, "Should enter 6 digit otp which received in mail", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                if (!checkTermsConditions.isChecked()) {
                    Snackbar.make(view, "Please Agree Terms & Conditions", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    sOtp = inputEnterOtp.getText().toString();
                    String maill = inputEnterEmail.getText().toString();
                    String mobb = inputEnterMobileNo.getText().toString();
                    if (!sOtp.isEmpty() && sOtp != null) {
                        String sentOTP = db.getMyOTP(inputEnterEmail.getText().toString(), inputEnterMobileNo.getText().toString());
                        if (!sentOTP.equals(sOtp)) {
                            Snackbar.make(view, "Please Enter Received OTP", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        } else {
                            Intent i3 = new Intent(RegisterActivity.this, ProfileActivity.class);
                            i3.putExtra("email",emailid);
                            i3.putExtra("mobile",mobileno);
                            startActivity(i3);
                        }
                    } else {
                        Snackbar.make(view, "Please Enter OTP", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
                break;
        }
    }

    public class sendmailgmail extends AsyncTask<Void, String, Void> implements DialogInterface.OnCancelListener {

        @Override
        public void onCancel(DialogInterface dialog) {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            utils.showProgress("", "Sending OTP..");
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            utils.hideProgress();
            register.setVisibility(View.VISIBLE);
            checkTermsConditions.setVisibility(View.VISIBLE);
            inputLayoutEnterOtp.setVisibility(View.VISIBLE);
            otp.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            mailsender = new GMailSender("gate4ru@gmail.com", "gate123$");
            String[] toArr = {emailid};
            tempOtp = OTP_Generation.generateOTP(6);
            mailsender.set_to(toArr);
            mailsender.set_from("gate4ru@gmail.com");
            mailsender.set_subject("OTP Validation");
            mailsender.setBody("Hello," + "\n"
                    + "\nThe Account has been sent with the OTP." + "\n"
                    + "\n" + "Please check below." + "\n" + "\n"
                    + "The OTP is: " + tempOtp + "\n" + "\n"
                    + "Thank You For Using Gate Service");
            try {
                if (mailsender.send()) {
                    Log.e("after execute method ", "Coming here");
                    Log.e(">>>>>>>>>> tempOtp", tempOtp + "coming here");
                    OTPMaster master1 = new OTPMaster();
                    master1.setOtp(tempOtp);
                    master1.setEmail(email1);
                    master1.setMobile(mobileno);
                    master1.setCreatedAt(new Date());
                    Log.e("values", master1 + "");
                    db.createOTP(master1);
//            finish();
                    Log.e("OTPMaster object", master1.toString());
                    Log.d("Emailsent successfully.",
                            "Email was sent successfully.");
                } else {
                    utils.hideProgress();
                    UtilsProvider.alertBox(mActivity, "Error");
                    Log.d("Email was not sent.", "Email was not sent.");
                }
            } catch (Exception e) {
                Log.e("MailApp", "Could not send email", e);
            }
            return null;
        }
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Registration Process not completed,Do you want to Exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RegisterActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
