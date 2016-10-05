package com.nerve24.doctor.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.ApiTask.API_Register;
import com.nerve24.doctor.ApiTask.API_Register_ReSend_OTP;
import com.nerve24.doctor.ApiTask.API_Register_Send_OTP;
import com.nerve24.doctor.Listeners.Listener_OTP_Register_ResendOTP;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

public class ActivityRegister extends AppCompatActivity implements View.OnClickListener, Listener_OTP_Register_ResendOTP {
    private String TAG = "", MODULE = "ActivityRegister";
    private Activity mActivity;
    private Utils utils;
    private Session session;
    private BroadcastReceiver smsReceiver;
    private Button btnOTP_Register;
    private EditText etOTP, etMobile, etEmail;
    private CheckBox cbPrivacy;
    private TextView tvLogin, tvResendOTP;
    private RelativeLayout rlprivacy;
    private LinearLayout llCbPrivacy;
    private String mobile = "", email = "", otp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUI();
        Fabric.with(this, new Crashlytics());
    }

    private void initUI() {
        try {
            TAG = "initUI";
            mActivity = ActivityRegister.this;
            session = new Session(mActivity);
            utils = new Utils(mActivity);
            etMobile = (EditText) findViewById(R.id.et_mobile);
            etEmail = (EditText) findViewById(R.id.et_email);
            etOTP = (EditText) findViewById(R.id.otp);
            cbPrivacy = (CheckBox) findViewById(R.id.cb_privacy);
            btnOTP_Register = (Button) findViewById(R.id.btn_get_otp);
            tvResendOTP = (TextView) findViewById(R.id.tv_resend_otp);
            tvLogin = (TextView) findViewById(R.id.tv_member_login);
            rlprivacy = (RelativeLayout) findViewById(R.id.rl_privacy);
            llCbPrivacy = (LinearLayout) findViewById(R.id.ll_cb_privacy);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Register");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            tvLogin.setOnClickListener(this);
            tvResendOTP.setOnClickListener(this);
            btnOTP_Register.setOnClickListener(this);
            llCbPrivacy.setOnClickListener(this);
            etMobile.addTextChangedListener(twMobile);
            etEmail.addTextChangedListener(twEmail);
            disableBtn();
            initializeSMSReceiver();
            registerSMSReceiver();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void registerSMSReceiver() {
        IntentFilter intentFilter = new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    private void initializeSMSReceiver() {
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    for (int i = 0; i < pdus.length; i++) {
                        byte[] pdu = (byte[]) pdus[i];
                        SmsMessage message = SmsMessage.createFromPdu(pdu);
                        String text = message.getDisplayMessageBody();
                        String sender = message.getOriginatingAddress();
                        Log.e("text", "" + text);
                        if (text.contains("8 digit OTP is")) {
                            int is_Position = text.indexOf("is");
                            String otpString = text.substring(is_Position + 3, is_Position + 11);
                            Log.e("otpString", "" + otpString);
                            setMessage("" + otpString);
                        }
                    }
                }

            }
        };
    }

    public void setMessage(String mes) {
        tvResendOTP.setVisibility(View.GONE);
        etOTP.setText(mes);
        etOTP.setEnabled(false);
    }

    TextWatcher twEmail = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = etMobile.getText().toString();
            String str1 = etEmail.getText().toString();
            if (str.length() > 0 || str1.length() > 0) {
                enableBtn();
            } else {
                disableBtn();
            }
        }
    };

    TextWatcher twMobile = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = etMobile.getText().toString();
            String str1 = etEmail.getText().toString();
            if (str.length() > 0 || str1.length() > 0) {
                enableBtn();
            } else {
                disableBtn();
            }
        }
    };

    private void disableBtn() {
        if (btnOTP_Register.isEnabled()) {
            Log.e("disableBtn", "disableBtn");
            btnOTP_Register.setEnabled(false);
            btnOTP_Register.setAlpha(.5f);
        }
    }

    private void enableBtn() {
        if (!btnOTP_Register.isEnabled()) {
            Log.e("enableBtn", "enableBtn");
            btnOTP_Register.setEnabled(true);
            btnOTP_Register.setAlpha(1f);
        }
    }

    private void sendOTP_Register_ResendOTP(String action) {
        try {
            //  {"mobileNumber":"9999911111","userName":"ss@gmail.com","userOtp":"","sendOTP":true,"reSendOTP":false,"register":false}
            boolean sendOTP = false, reSendOTP = false, register = false;
            if (action.equals("sendotp")) sendOTP = true;
            if (action.equals("resendotp")) reSendOTP = true;
            if (action.equals("register")) register = true;


            JSONObject jsonBody = new JSONObject();
            jsonBody.put("mobileNumber", mobile);
            jsonBody.put("userName", email);
            jsonBody.put("userOtp", otp);
            jsonBody.put("sendOTP", sendOTP);
            jsonBody.put("reSendOTP", reSendOTP);
            jsonBody.put("register", register);

            utils.showProgress("", "loading..");
            if (sendOTP) {
                API_Register_Send_OTP apiSendOtp = new API_Register_Send_OTP(jsonBody, ActivityRegister.this, mActivity);
                apiSendOtp.sendOTP();
            } else if (reSendOTP) {
                API_Register_ReSend_OTP apiReSendOtp = new API_Register_ReSend_OTP(jsonBody, ActivityRegister.this, mActivity);
                apiReSendOtp.reSendOTP();
            } else if (register) {
                API_Register apiRegister = new API_Register(jsonBody, ActivityRegister.this, mActivity);
                apiRegister.register();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get_otp:
                getOTP_Register();
                break;
            case R.id.tv_member_login:
                finish();
                break;
            case R.id.tv_resend_otp:

                reSendOtp();
                break;

            case R.id.ll_cb_privacy:
                checkPrivacy();
                break;
        }
    }

    private void checkPrivacy() {
        if (cbPrivacy.isChecked()) {
            cbPrivacy.setChecked(false);
        } else {
            cbPrivacy.setChecked(true);
        }
    }

    private void getOTP_Register() {
        Log.e("getOTP_Register", "getOTP_Register");
        String btnText = btnOTP_Register.getText().toString().trim().toLowerCase();
        if (btnText.contains("otp")) {
            if (Utils.isMarshmallow()) {
                checkSMSPermission(mActivity, 1000);
            } else {
                getOTP();
            }
        } else {
            register();
        }
    }

    private void register() {
        if (validationRegister() && emailValidate() && mobileValidate()) {
            sendOTP_Register_ResendOTP("register");
        }
    }

    private void getOTP() {
        if (validationOTP()) {
            sendOTP_Register_ResendOTP("sendotp");
        }
    }

    private void reSendOtp() {
        if (validationOTP()) {
            if (Utils.isMarshmallow()) {
                checkSMSPermission(mActivity, 2000);
            } else {
                sendOTP_Register_ResendOTP("resendotp");
            }
        }
    }

    private boolean emailValidate() {
        boolean flag = false;
        if (etEmail.isEnabled()) {
            email = etEmail.getText().toString().trim();
            if (!Utils.checkEmail(email) && email.length() > 0) {
                Utils.alertBox(mActivity, "Enter valid email!");
            } else {
                flag = true;
            }
        } else {
            flag = true;
        }
        return flag;
    }

    private boolean mobileValidate() {
        boolean flag = false;
        mobile = etMobile.getText().toString().trim();
        if (etMobile.isEnabled() && mobile.length() > 0) {
            if (mobile.length() < 10) {
                Utils.alertBox(mActivity, "Enter 10 digit mobile!");
            } else {
                flag = true;
            }
        } else {
            flag = true;
        }
        return flag;
    }

    private boolean validationOTP() {
        boolean flag = false;
        mobile = etMobile.getText().toString().trim();
        email = etEmail.getText().toString().trim();

        if (mobile.length() > 0 && email.length() > 0) {
            if (mobile.length() < 10) {
                Utils.alertBox(mActivity, "Enter 10 digit mobile!");
            } else if (!Utils.checkEmail(email)) {
                Utils.alertBox(mActivity, "Enter valid email!");
            } else {
                flag = true;
            }
        } else if (mobile.length() > 0 && mobile.length() < 10) {
            Utils.alertBox(mActivity, "Enter 10 digit mobile!");
        } else if (email.length() > 0 && !Utils.checkEmail(email)) {
            Utils.alertBox(mActivity, "Enter valid email!");
        } else {
            flag = true;
        }

        return flag;
    }

    private boolean validationRegister() {
        boolean flag = false;
        otp = etOTP.getText().toString().trim();
        if (otp.length() == 0) {
            Utils.alertBox(mActivity, "Enter otp!");
        } else if (!cbPrivacy.isChecked()) {
            Utils.alertBox(mActivity, "Check privacy policy before registering!");
        } else {
            flag = true;
        }
        return flag;
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
    public void onGetOTP(String res) {
        utils.hideProgress();
        disableEnableButtons();
        String mes = "OTP has been sent to your registered Mobile/Email";
        btnOTP_Register.setText("REGISTER");
        rlprivacy.setVisibility(View.VISIBLE);
        Utils.alertBox(mActivity, mes);
    }

    private void disableEnableButtons() {
        if (email.length() > 0) {
            etEmail.setFocusable(false);
        }
        if (mobile.length() > 0) {
            etMobile.setFocusable(false);
        }
    }

    @Override
    public void onGetResendOTP(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }

    @Override
    public void onRegisterSuccess(String res) {
        utils.hideProgress();
        session.saveNerve24Id(res);
        Intent i = new Intent(getApplicationContext(),
                ActivityWelcome.class);
        i.putExtra("mobile", mobile);
        i.putExtra("email", email);
        finish();
        startActivity(i);
        //Utils.alertBox(mActivity,"Registered successfully!");
    }

    @Override
    public void onfailure(String res) {
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


    private void checkSMSPermission(Activity mActivity, int requestCode) {
        if (!isSMSPermissionEnabled()) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    requestCode);
        }
    }

    private boolean isSMSPermissionEnabled() {
        return ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.RECEIVE_SMS)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getOTP();
                } else {
                    //Utils.alertBox(mActivity, "You will get OTP in your inbox soon. Read and give here manually!");
                    getOTP();
                }
                return;
            }

            case 2000: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendOTP_Register_ResendOTP("resendotp");
                } else {
                    //Utils.alertBox(mActivity, "You will get OTP in your inbox soon. Read and give here manually!");
                    sendOTP_Register_ResendOTP("resendotp");
                }
                return;
            }

        }
    }

}


