package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.ApiTask.API_Welcome_Register;
import com.nerve24.doctor.Listeners.Listener_Welcome_Register;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;
import com.satsuware.usefulviews.LabelledSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.util.Calendar;

import io.fabric.sdk.android.Fabric;

public class ActivityWelcome extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, Listener_Welcome_Register {
    private String TAG = "", MODULE = "ActivityWelcome";
    private Button btnSubmit;
    private AutoCompleteTextView etFname, etMname, etLname, etMobile, etEmail, etUserName, etPassword, etDOB, etNerve24Id;
    private Activity mActivity;
    private String fName, lName, mName, mobile, eMail, uName, password, salutation, dob, gender, nerve24Id;
    private Utils utils;
    private Session session;
    private String[] arraySalutation = {"Mr.", "Mrs.", "Ms.", "Shri", "Dr.", "CA", "Er.", "Prof."};
    private String[] arrayGender = {"Male", "Female", "Others"};

    private LabelledSpinner spinnerSalutation, spGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initUI();
        Fabric.with(this, new Crashlytics());
    }

    private void initUI() {
        try {
            TAG = "initUI";
            mActivity = ActivityWelcome.this;
            session = new Session(mActivity);
            utils = new Utils(mActivity);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_content_clear));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            etFname = (AutoCompleteTextView) findViewById(R.id.tv_fname);
            etMname = (AutoCompleteTextView) findViewById(R.id.tv_mname);
            etLname = (AutoCompleteTextView) findViewById(R.id.tv_lname);
            etMobile = (AutoCompleteTextView) findViewById(R.id.tv_mobile);
            etEmail = (AutoCompleteTextView) findViewById(R.id.tv_email);
            etUserName = (AutoCompleteTextView) findViewById(R.id.tv_uname);
            etPassword = (AutoCompleteTextView) findViewById(R.id.tv_password);
            etDOB = (AutoCompleteTextView) findViewById(R.id.tv_dob);
            etNerve24Id = (AutoCompleteTextView) findViewById(R.id.tv_nerve24_id);
            btnSubmit = (Button) findViewById(R.id.btn_submit);
            spinnerSalutation = (LabelledSpinner) findViewById(R.id.sp_salutation);
            spinnerSalutation.setItemsArray(arraySalutation);
            spGender = (LabelledSpinner) findViewById(R.id.sp_gender);
            spGender.setItemsArray(arrayGender);

            initListeners();
            getAndSetValues();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void getAndSetValues() {
        eMail = getIntent().getStringExtra("email");
        mobile = getIntent().getStringExtra("mobile");
        nerve24Id = session.getNerve24Id();
        etNerve24Id.setText(nerve24Id);
        etEmail.setText(eMail);
        etMobile.setText(mobile);

        if (eMail.length() > 0) etEmail.setFocusable(false);
        if (mobile.length() > 0) etMobile.setFocusable(false);

    }

    private void initListeners() {
        etDOB.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        etDOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    selectDOB();
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                submitProcess();
                break;
            case R.id.tv_dob:
                selectDOB();
                break;
        }
    }


    private void selectDOB() {

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                ActivityWelcome.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -18);
        dpd.setMaxDate(maxDate);
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -110);
        dpd.setMinDate(minDate);
        dpd.show(getFragmentManager(), "Datepickerdialog");

    }

    private void submitProcess() {
        try {
            if (validation()) {
                utils.showProgress("", "Loading...");
                JSONObject obj = new JSONObject();
                obj.put("firstName", fName);
                obj.put("lastName", lName);
                obj.put("middleName", mName);
                obj.put("email", eMail);
                obj.put("primaryphone", mobile);
                obj.put("username", uName);
                obj.put("password", password);
                obj.put("salutation", salutation);
                obj.put("dob", dob);
                obj.put("gender", gender);
                obj.put("userType", "DOCTOR");
                obj.put("nerve24Id", nerve24Id);

                byte[] bytes = obj.toString().getBytes("UTF-8");
                byte[] encodeValue = Base64.encode(bytes, Base64.NO_WRAP);
                String encodedText = new String(encodeValue, "UTF-8");

                API_Welcome_Register apiWelcomeRegister = new API_Welcome_Register(encodedText, ActivityWelcome.this, mActivity);
                apiWelcomeRegister.register();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private TextWatcher tw_Uname = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = etUserName.getText().toString().trim();
            if (s.length() == 0) {
                etUserName.setError("User name is empty!");
            } else {
                etUserName.setError(null);
            }
        }
    };

    private TextWatcher tw_Pwd = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = etPassword.getText().toString().trim();
            if (s.length() == 0) {
                etPassword.setError("Password is empty!");
            } else if (s.length() < 8) {
                etPassword.setError("Password minimum length is 8!");
            } else {
                etPassword.setError(null);
            }
        }
    };

    private TextWatcher tw_Fname = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = etFname.getText().toString().trim();
            if (s.length() == 0) {
                etFname.setError("First name is empty!");
            } else {
                etFname.setError(null);
            }
        }
    };

    private TextWatcher tw_Lname = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = etLname.getText().toString().trim();
            if (s.length() == 0) {
                etLname.setError("Last name is empty!");
            } else {
                etLname.setError(null);
            }
        }
    };

    private TextWatcher tw_Mobile = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = etMobile.getText().toString().trim();
            if (s.length() == 0) {
                etMobile.setError("Mobile is empty!");
            } else if (s.length() < 10) {
                etMobile.setError("Enter 10 digit mobile number!");
            } else {
                etMobile.setError(null);
            }
        }
    };

    private TextWatcher tw_Email = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = etEmail.getText().toString().trim();
            if (s.length() == 0) {
                etEmail.setError("Email is empty!");
            } else if (!Utils.checkEmail(s)) {
                etEmail.setError("Invalid email!");
            } else {
                etEmail.setError(null);
            }
        }
    };

    private TextWatcher tw_DOB = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = etDOB.getText().toString().trim();
            if (s.length() == 0) {
                etDOB.setError("Select DOB!");
            } else {
                etDOB.setError(null);
            }
        }
    };


    private boolean validation() {
        boolean flag = false;
        try {
            TAG = "validation";
            nerve24Id = etNerve24Id.getText().toString().trim();
            uName = etUserName.getText().toString().trim();
            password = etPassword.getText().toString().trim();
            salutation = spinnerSalutation.getSpinner().getSelectedItem().toString();
            fName = etFname.getText().toString().trim();
            mName = etMname.getText().toString().trim();
            lName = etLname.getText().toString().trim();
            mobile = etMobile.getText().toString().trim();
            eMail = etEmail.getText().toString().trim();
            dob = etDOB.getText().toString().trim();
            gender = spGender.getSpinner().getSelectedItem().toString();

            etUserName.addTextChangedListener(tw_Uname);
            etPassword.addTextChangedListener(tw_Pwd);
            etFname.addTextChangedListener(tw_Fname);
            etLname.addTextChangedListener(tw_Lname);
            etMobile.addTextChangedListener(tw_Mobile);
            etEmail.addTextChangedListener(tw_Email);
            etDOB.addTextChangedListener(tw_DOB);

            if (uName.length() == 0) {
                etUserName.setError("User name is empty!");
            } else {
                etUserName.setError(null);
            }
            if (password.length() == 0) {
                etPassword.setError("Password is empty!");
            } else if (password.length() < 8) {
                etPassword.setError("Password minimum length is 8!");
            } else {
                etPassword.setError(null);
            }

            if (fName.length() == 0) {
                etFname.setError("First name is empty!");
            } else {
                etFname.setError(null);
            }
            if (lName.length() == 0) {
                etLname.setError("Last name is empty!");
            } else {
                etLname.setError(null);
            }

            if (mobile.length() == 0) {
                etMobile.setError("Mobile is empty!");
            } else if (mobile.length() < 10) {
                etMobile.setError("Enter 10 digit mobile number!");
            } else {
                etMobile.setError(null);
            }

            if (eMail.length() == 0) {
                etEmail.setError("Email is empty!");
            } else if (!Utils.checkEmail(eMail)) {
                etEmail.setError("Invalid email!");
            } else {
                etEmail.setError(null);
            }

            if (dob.length() == 0) {
                etDOB.setError("Select DOB!");
            } else {
                etDOB.setError(null);
            }


            if (uName.length() == 0) {
            } else if (password.length() == 0) {
            } else if (password.length() < 8) {
            } else if (salutation.length() == 0) {
            } else if (fName.length() == 0) {
            } else if (lName.length() == 0) {
            } else if (mobile.length() == 0) {
            } else if (mobile.length() < 10) {
            } else if (eMail.length() == 0) {
            } else if (!Utils.checkEmail(eMail)) {
            } else if (dob.length() == 0) {
            } else if (gender.length() == 0) {
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
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        etDOB.setText("" + dayOfMonth + "-" + monthOfYear + "-" + year);
    }

    @Override
    public void onRegisterSuccess(String res) {
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
    public void onRegistrationError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }

}
