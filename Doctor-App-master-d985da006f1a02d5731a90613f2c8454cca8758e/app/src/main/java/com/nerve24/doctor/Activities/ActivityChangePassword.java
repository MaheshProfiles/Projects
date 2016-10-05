package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.ApiTask.API_Change_Password;
import com.nerve24.doctor.Listeners.Listener_Change_password;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;

import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

public class ActivityChangePassword extends AppCompatActivity implements Listener_Change_password, View.OnClickListener {
    private String TAG = "", MODULE = "ActivityChangePassword";
    private Activity mActivity;
    private Utils utils;
    private Session session;

    private EditText et_old_pwd, et_pwd_again, et_pwd;
    private Button btnSave, btnCancel;
    private CheckBox cbShowCureentPwd, cbShowNewPwd;
    private String oldPassword = "", newPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        initUI();
        Fabric.with(this, new Crashlytics());
    }

    private void initUI() {
        TAG = "initUI";
        mActivity = ActivityChangePassword.this;
        session = new Session(mActivity);
        utils = new Utils(mActivity);
        et_old_pwd = (EditText) findViewById(R.id.et_current_pwd);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_pwd_again = (EditText) findViewById(R.id.et_pwd_again);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        cbShowCureentPwd = (CheckBox) findViewById(R.id.cb_show_current_pwd);
        cbShowNewPwd = (CheckBox) findViewById(R.id.cb_show_new_pwd);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Change Password");
        setSupportActionBar(toolbar);

        initListeners();
    }

    private void initListeners() {
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        cbShowNewPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    et_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_pwd_again.setTransformationMethod(PasswordTransformationMethod.getInstance());

                } else {
                    et_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    et_pwd_again.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
        cbShowCureentPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    et_old_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    et_old_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                changePassword();
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }

    }


    private boolean validateChangePassword() {
        boolean flag = false;
        oldPassword = et_old_pwd.getText().toString().trim();
        newPassword = et_pwd.getText().toString().trim();
        String pwdAgain = et_pwd_again.getText().toString().trim();
        if (oldPassword.length() == 0) {
            Utils.alertBox(mActivity, "Enter current password!");
        } else if (oldPassword.length() < 8) {
            Utils.alertBox(mActivity, "Password minimum length is 8!");
        } else if (newPassword.length() == 0) {
            Utils.alertBox(mActivity, "Enter new password!");
        } else if (newPassword.length() < 8) {
            Utils.alertBox(mActivity, "Password minimum length is 8!");
        } else if (pwdAgain.length() == 0) {
            Utils.alertBox(mActivity, "Enter confirm password!");
        } else if (pwdAgain.length() < 8) {
            Utils.alertBox(mActivity, "Password minimum length is 8!");
        } else if (!newPassword.equals(pwdAgain)) {
            Utils.alertBox(mActivity, "Re-entered password must match New password!");
        } else {
            flag = true;
        }
        return flag;
    }


    private void changePassword() {
        if (validateChangePassword()) {
            utils.showProgress("", "loading..");
            try {
                //   {"oldPassword":"12345678","newPassword":"qwertyui","confirmPassword":"qwertyui","userName":"selva"}

                JSONObject obj = new JSONObject();
                obj.put("oldPassword", oldPassword);
                obj.put("newPassword", newPassword);
                obj.put("confirmPassword", newPassword);
                obj.put("userName", session.getUserName());

                byte[] bytes = obj.toString().getBytes("UTF-8");
                byte[] encodeValue = Base64.encode(bytes, Base64.NO_WRAP);
                String encodedText = new String(encodeValue, "UTF-8");

                API_Change_Password apiChangePassword = new API_Change_Password(encodedText, ActivityChangePassword.this, mActivity);
                apiChangePassword.process();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPasswordChanged(String res) {
        utils.hideProgress();
        successAlert( res);
        reset();
    }

    private void successAlert(String res) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mActivity);
        builder.title("Success");
        builder.content(res);
        builder.positiveText("Ok");
        builder.show();
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                session.clearAll();
                Intent intent=new Intent(mActivity,ActivityLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }


    private void reset() {
        et_old_pwd.setText("");
        et_pwd.setText("");
        et_pwd_again.setText("");
    }

    @Override
    public void onPasswordChangedError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }
}