package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.ApiTask.API_Forget_Password;
import com.nerve24.doctor.ApiTask.API_Forget_Password_Send_OTP;
import com.nerve24.doctor.ApiTask.API_Forget_Password_Validate_OTP;
import com.nerve24.doctor.Listeners.Listener_Forget_password;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;

import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

public class ActivityForgetpassword extends AppCompatActivity implements Listener_Forget_password, View.OnClickListener {
    private String TAG = "", MODULE = "ActivityForgetpassword";
    private Activity mActivity;
    private Utils utils;
    private Session session;

    private EditText et_username, et_otp, et_pwd_again, et_pwd;
    private Button btnGetOTP, btnValidateOTP, btnSubmit;
    private TextView resend;
    private String uName = "", OTP = "", password = "";
    private RelativeLayout rlOTP, rlPwd;
    private JSONObject userObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        initUI();
        Fabric.with(this, new Crashlytics());
    }

    private void initUI() {
        TAG = "initUI";
        mActivity = ActivityForgetpassword.this;
        session = new Session(mActivity);
        utils = new Utils(mActivity);
        et_username = (EditText) findViewById(R.id.et_username);
        et_otp = (EditText) findViewById(R.id.et_otp);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_pwd_again = (EditText) findViewById(R.id.et_pwd_again);
        btnGetOTP = (Button) findViewById(R.id.btn_get_otp);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        resend = (TextView) findViewById(R.id.tv_resend_otp);
        btnValidateOTP = (Button) findViewById(R.id.btn_validate_otp);
        rlOTP = (RelativeLayout) findViewById(R.id.rl_otp);
        rlPwd = (RelativeLayout) findViewById(R.id.rl_pwd);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Forgot Password");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initListeners();
    }

    private void initListeners() {
        btnGetOTP.setOnClickListener(this);
        resend.setOnClickListener(this);
        btnValidateOTP.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        et_username.addTextChangedListener(twUsername);
        et_otp.addTextChangedListener(twOTP);
        et_pwd.addTextChangedListener(twPwd);
        et_pwd_again.addTextChangedListener(twPwd);

        disableBtn(btnGetOTP);
        disableBtn(btnValidateOTP);
        disableBtn(btnSubmit);
    }


    TextWatcher twUsername = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = et_username.getText().toString();
            if (str.length() > 0) {
                enableBtn(btnGetOTP);
            } else {
                disableBtn(btnGetOTP);
            }
        }
    };
    TextWatcher twOTP = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = et_otp.getText().toString();
            if (str.length() > 0) {
                enableBtn(btnValidateOTP);
            } else {
                disableBtn(btnValidateOTP);
            }
        }
    };
    TextWatcher twPwd = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = et_pwd.getText().toString();
            String str1 = et_pwd_again.getText().toString();
            if (str.length() > 0 && str1.length() > 0) {
                enableBtn(btnSubmit);
            } else {
                disableBtn(btnSubmit);
            }
        }
    };


    private void disableBtn(Button btn) {
        if (btn.isEnabled()) {
            btn.setEnabled(false);
            btn.setAlpha(.5f);
        }
    }

    private void enableBtn(Button btn) {
        if (!btn.isEnabled()) {
            btn.setEnabled(true);
            btn.setAlpha(1f);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get_otp:
                getOTP();
                break;
            case R.id.btn_validate_otp:
                submitOTP();
                break;
            case R.id.tv_resend_otp:
                resendOTP();
                break;
            case R.id.btn_submit:
                changePassword();
                break;
        }

    }

    private void resendOTP() {
        if (validateGetOTP()) {
            utils.showProgress("", "loading..");
            try {
                JSONObject obj = new JSONObject();
                obj.put("enteredUserName", uName);
                obj.put("otpResent", true);
                // {"enteredUserName":"selva","otpResent":true}

                API_Forget_Password_Send_OTP apiForgetPasswordSendOtp = new API_Forget_Password_Send_OTP(obj, ActivityForgetpassword.this, ActivityForgetpassword.this);
                apiForgetPasswordSendOtp.sendOTP();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getOTP() {
        if (validateGetOTP()) {
            utils.showProgress("", "loading..");
            try {
                JSONObject obj = new JSONObject();
                obj.put("enteredUserName", uName);
                // {"enteredUserName":"selva"}

                API_Forget_Password_Send_OTP apiForgetPasswordSendOtp = new API_Forget_Password_Send_OTP(obj, ActivityForgetpassword.this, ActivityForgetpassword.this);
                apiForgetPasswordSendOtp.sendOTP();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void submitOTP() {
        if (validateSubmitOTP()) {
            utils.showProgress("", "loading..");
            try {
                // {"enteredUserName":"selva","otpResent":false,"otpEntered":true,"otp":"05122878"}
                JSONObject obj = new JSONObject();
                obj.put("enteredUserName", uName);
                obj.put("otpResent", false);
                obj.put("otpEntered", true);
                obj.put("otp", OTP);

                API_Forget_Password_Validate_OTP apiForgetPasswordValidateOtp = new API_Forget_Password_Validate_OTP(obj, ActivityForgetpassword.this, ActivityForgetpassword.this);
                apiForgetPasswordValidateOtp.validate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validateGetOTP() {
        boolean flag = false;
        uName = et_username.getText().toString().trim();
        if (uName.length() == 0) {
            Utils.alertBox(mActivity, "Enter username/email/mobile!");
        } else {
            flag = true;
        }
        return flag;
    }


    private boolean validateSubmitOTP() {
        boolean flag = false;
        OTP = et_otp.getText().toString().trim();
        if (OTP.length() == 0) {
            Utils.alertBox(mActivity, "Enter OTP!");
        } else {
            flag = true;
        }
        return flag;
    }

    private boolean validateChangePassword() {
        boolean flag = false;
        password = et_pwd.getText().toString().trim();
        String pwdAgain = et_pwd_again.getText().toString().trim();
        if (password.length() == 0) {
            Utils.alertBox(mActivity, "Enter new password!");
        } else if (password.length() < 8) {
            Utils.alertBox(mActivity, "Password minimum length is 8!");
        } else if (pwdAgain.length() == 0) {
            Utils.alertBox(mActivity, "Enter confirm password!");
        } else if (pwdAgain.length() < 8) {
            Utils.alertBox(mActivity, "Password minimum length is 8!");
        } else if (!password.equals(pwdAgain)) {
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
                userObj.put("confirmPassword", password);
                userObj.put("newPassword", password);

                byte[] bytes = userObj.toString().getBytes("UTF-8");
                byte[] encodeValue = Base64.encode(bytes, Base64.NO_WRAP);
                String encodedText = new String(encodeValue, "UTF-8");

                API_Forget_Password api_forget_password = new API_Forget_Password(encodedText, ActivityForgetpassword.this, ActivityForgetpassword.this);
                api_forget_password.process();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onOTPsent(String res) {
        et_username.setEnabled(false);
        rlOTP.setVisibility(View.VISIBLE);
        btnGetOTP.setVisibility(View.GONE);
        btnValidateOTP.setVisibility(View.VISIBLE);
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }

    @Override
    public void onOTPsentError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }

    @Override
    public void onPasswordChange(String res) {
        utils.hideProgress();
        successAlert(res);
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
                finish();
            }
        });

    }


    @Override
    public void onPasswordChangeError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }

    @Override
    public void onOTP_Validated(String res, JSONObject obj) {
        rlOTP.setVisibility(View.GONE);
        rlPwd.setVisibility(View.VISIBLE);
        btnValidateOTP.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.VISIBLE);
        userObj = obj;
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }

    @Override
    public void onOTP_ValidatedError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }

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
}