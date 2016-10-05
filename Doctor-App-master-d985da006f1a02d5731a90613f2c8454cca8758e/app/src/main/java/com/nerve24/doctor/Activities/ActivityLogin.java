package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.ApiTask.API_Login;
import com.nerve24.doctor.Listeners.Listener_Login;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Font;
import com.nerve24.doctor.Utility.Utils;

import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener, Listener_Login, CompoundButton.OnCheckedChangeListener {
    private String TAG = "", MODULE = "ActivityLogin";
    private Activity mActivity;
    private Utils utils;
    private Session session;
    private Button btnLogin;
    private TextView tvRegister, tvTitle, textforgetpassword;
    private TextInputLayout inputLayoutuser, inputLayoutpass;
    private EditText etUser, etPwd;
    private CheckBox cb_remember_me;
    private String email = "", password = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
        initUI();
        Fabric.with(this, new Crashlytics());
    }

    private void checkSession() {
        if (session.getSession().equals("1")) {
            Intent i = new Intent(getApplicationContext(),
                    ActivityMain.class);
            finish();
            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        setLogin();
        super.onResume();
    }

    private void initUI() {
        try {
            TAG = "initUI";
            mActivity = ActivityLogin.this;
            session = new Session(mActivity);
            checkSession();
            utils = new Utils(mActivity);
            cb_remember_me = (CheckBox) findViewById(R.id.cb_remember_me);
            etUser = (EditText) findViewById(R.id.et_mobile);
            textforgetpassword = (TextView) findViewById(R.id.tv_forget_password);
            inputLayoutuser = (TextInputLayout) findViewById(R.id.inputuser);
            inputLayoutpass = (TextInputLayout) findViewById(R.id.inputpassword);
            etPwd = (EditText) findViewById(R.id.password);
            tvTitle = (TextView) findViewById(R.id.tv_login_from_social_account);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            tvRegister = (TextView) findViewById(R.id.tv_register);
            btnLogin = (Button) findViewById(R.id.btn_get_otp);
            inputLayoutuser.setErrorEnabled(true);
            etUser.setTypeface(Font.getRobotMedium(mActivity), Typeface.NORMAL);
            setSupportActionBar(toolbar);
            textforgetpassword.setOnClickListener(this);
            tvRegister.setOnClickListener(this);
            btnLogin.setOnClickListener(this);
            cb_remember_me.setOnCheckedChangeListener(this);

        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            saveLogin();
    }

    private void setLogin() {
        try {
            TAG = "setLogin";
            email = session.getLoginId();
            password = session.getPassword();
            if (email.length() !=0) {
                cb_remember_me.setChecked(true);
            }
            etUser.setText(email);
            etPwd.setText(password);
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_forget_password:
                goToForgetPassword();
                break;
            case R.id.tv_register:
                goToRegister();
                break;
            case R.id.btn_get_otp:
                login();
                break;
        }
    }

    private void goToRegister() {
        reset();
        Intent intent = new Intent(getApplicationContext(), ActivityRegister.class);
        startActivity(intent);
    }

    private void goToForgetPassword() {
        reset();
        Intent intent = new Intent(getApplicationContext(), ActivityForgetpassword.class);
        startActivity(intent);
    }

    private void login() {
        if (validation()) {
            try {
                utils.showProgress("", "loading..");
                JSONObject obj = new JSONObject();
                obj.put("username", email);
                obj.put("password", password);
                API_Login apiLogin = new API_Login(obj, ActivityLogin.this, mActivity);
                apiLogin.login();
            } catch (Exception e) {
                Log.e(MODULE, TAG);
                e.printStackTrace();
            }
        }
    }

    private boolean validation() {
        boolean flag = false;
        try {
            TAG = "validation";
            email = etUser.getText().toString().trim();
            password = etPwd.getText().toString().trim();
            if (email.length() == 0) {
                Utils.alertBox(mActivity, "Enter user name!");
            } else if (password.length() == 0) {
                Utils.alertBox(mActivity, "Enter password!");
            } else {
                flag = true;
            }
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public void onLoginSuccess(String res) {
        utils.hideProgress();
        saveLogin();
        Intent i = new Intent(getApplicationContext(),
                ActivityMain.class);
        finish();
        startActivity(i);
    }

    private void saveLogin() {
        try {
            TAG = "saveLogin";
            if (cb_remember_me.isChecked()) {
                Log.e(MODULE, TAG);
                session.saveLoginId(email);
                session.savePassword(password);
            } else {
                session.saveLoginId("");
                session.savePassword("");
            }
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    @Override
    public void onLoginError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }

    private void reset() {
        etUser.setText("");
        etPwd.setText("");
    }


}






