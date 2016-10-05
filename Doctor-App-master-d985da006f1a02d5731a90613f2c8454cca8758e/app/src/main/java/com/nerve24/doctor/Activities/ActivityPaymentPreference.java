package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.ApiTask.API_Get_payment;
import com.nerve24.doctor.ApiTask.API_Save_Payment;
import com.nerve24.doctor.Listeners.Listener_Get_Payment;
import com.nerve24.doctor.Listeners.Listener_Save_Payment;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.InstantAutoCompleteTextView;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.PaymentPref;

import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

public class ActivityPaymentPreference extends AppCompatActivity implements View.OnClickListener, Listener_Save_Payment, Listener_Get_Payment {
    private String TAG = "", MODULE = "ActivityPaymentPreference";
    private Activity mActivity;
    private Utils utils;
    private Session session;

    private EditText et_acc_holder_name, et_acc_no, et_bank_name, et_branch_name, et_ifsc;
    private Spinner spAccountType;
    private InstantAutoCompleteTextView atvAccountType;
    private Button btncancel, btnSave;

    private String[] arrayAccountType = {"Current account", "Savings account"};
    private String accountHolderName = "", accNo = "", bankName = "", branchName = "", ifsc = "", accType = "", bankDetailId = "";
    private boolean spinnerFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_pref);
        initUI();
        Fabric.with(this, new Crashlytics());
    }

    private void initUI() {
        TAG = "initUI";
        mActivity = ActivityPaymentPreference.this;
        session = new Session(mActivity);
        utils = new Utils(mActivity);
        spAccountType = (Spinner) findViewById(R.id.sp_acc_type);
        atvAccountType = (InstantAutoCompleteTextView) findViewById(R.id.atv_acc_type);

        btnSave = (Button) findViewById(R.id.btn_save);
        btncancel = (Button) findViewById(R.id.btn_cancel);
        et_acc_holder_name = (EditText) findViewById(R.id.et_account_holder_name);
        et_acc_no = (EditText) findViewById(R.id.et_acc_no);
        et_bank_name = (EditText) findViewById(R.id.et_bank_name);
        et_branch_name = (EditText) findViewById(R.id.et_bank_branch);
        et_ifsc = (EditText) findViewById(R.id.et_ifsc);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Payment Preferences");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initListeners();
        getPayment();
    }

    private void getPayment() {
        try {
            String nerve24Id = session.getNerve24Id();
            utils.showProgress("", "loading..");
            API_Get_payment apiGetPayment = new API_Get_payment(nerve24Id, ActivityPaymentPreference.this, mActivity);
            apiGetPayment.getPayment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListeners() {
        btncancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        et_acc_holder_name.addTextChangedListener(twAccHolderName);
        et_bank_name.addTextChangedListener(twBankName);
        et_branch_name.addTextChangedListener(twBranchName);
        et_acc_no.addTextChangedListener(twAccNo);
        et_ifsc.addTextChangedListener(twIfsc);
        atvAccountType.addTextChangedListener(tvAccType);

        et_acc_holder_name.setFilters(Utils.getAlphabetWithSpaceFilter());
        et_bank_name.setFilters(Utils.getAlphabetWithSpaceFilter());
        et_branch_name.setFilters(Utils.getAlphabetWithSpaceFilter());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, arrayAccountType);
        spAccountType.setAdapter(adapter);

        atvAccountType.setInputType(InputType.TYPE_NULL);
        atvAccountType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    spinnerFlag = true;
                    spAccountType.performClick();
                }
            }
        });
        atvAccountType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerFlag = true;
                spAccountType.performClick();
            }
        });

        spAccountType.setSelection(1);
        spAccountType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerFlag) {
                    spinnerFlag = false;
                    String item = adapterView.getItemAtPosition(i).toString();
                    atvAccountType.setText(item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    TextWatcher twAccHolderName = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = et_acc_holder_name.getText().toString();
            if (str.length() == 0) {
                et_acc_holder_name.setError("Account Holder Name is required field!");
            } else {
                et_acc_holder_name.setError(null);
            }
        }
    };

    TextWatcher twBankName = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = et_bank_name.getText().toString();
            if (str.length() == 0) {
                et_bank_name.setError("Bank Name is required field!");
            } else {
                et_bank_name.setError(null);
            }
        }
    };

    TextWatcher twBranchName = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = et_branch_name.getText().toString();
            if (str.length() == 0) {
                et_branch_name.setError("Bank Branch is required field!");
            } else {
                et_branch_name.setError(null);
            }
        }
    };

    TextWatcher twAccNo = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = et_acc_no.getText().toString();
            if (str.length() == 0) {
                et_acc_no.setError("Account number is required field!");
            } else if (str.length() < 10) {
                et_acc_no.setError("Account number minimum length is 10!");
            } else {
                et_acc_no.setError(null);
            }
        }
    };

    TextWatcher twIfsc = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = et_ifsc.getText().toString();
            if (str.length() == 0) {
                et_ifsc.setError("IFSC Code is required field!");
            } else {
                et_ifsc.setError(null);
            }
        }
    };

    TextWatcher tvAccType = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = atvAccountType.getText().toString();
            if (str.length() == 0) {
                atvAccountType.setError("Account type is required field!");
            } else {
                atvAccountType.setError(null);
            }
        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                save();
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }

    }


    private void save() {
        if (validate()) {
            utils.showProgress("", "loading..");
            try {
                // {"accountHolderNumber":1213,"accountType":{"id":2,"accountType":"Savings"},"bankName":"jhjh","bankBranch":"fdvvf","ifscCode":"3ccdf","userName":"NV4T1A000D"}
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("accountHolderName", accountHolderName);
                jsonObject.put("accountHolderNumber", Long.parseLong(accNo));
                JSONObject jsonAccount = new JSONObject();
                int id = accType.equals(arrayAccountType[0]) ? 1 : 2;
                jsonAccount.put("id", id);
                jsonAccount.put("accountType", accType);
                jsonObject.put("accountType", jsonAccount);
                jsonObject.put("bankName", bankName);
                jsonObject.put("bankBranch", branchName);
                jsonObject.put("ifscCode", ifsc);
                jsonObject.put("userName", session.getNerve24Id());
                if (bankDetailId.length() > 0)
                    jsonObject.put("bankDetailId", bankDetailId);


                API_Save_Payment apiSavePayment = new API_Save_Payment(jsonObject, ActivityPaymentPreference.this, ActivityPaymentPreference.this);
                apiSavePayment.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validate() {
        boolean flag = false;
        accountHolderName = et_acc_holder_name.getText().toString().trim();
        accNo = et_acc_no.getText().toString().trim();
        bankName = et_bank_name.getText().toString().trim();
        branchName = et_branch_name.getText().toString().trim();
        ifsc = et_ifsc.getText().toString().trim();
        accType = atvAccountType.getText().toString().trim();

        if (accountHolderName.length() == 0) {
            et_acc_holder_name.setError("Account Holder Name is required field!");
        } else if (accNo.length() == 0) {
            et_acc_no.setError("Account number is required field!");
        } else if (accNo.length() < 10) {
            et_acc_no.setError("Account number minimum length is 10!");
        } else if (accType.length() == 0) {
            atvAccountType.setError("Account type is required field!");
        } else if (bankName.length() == 0) {
            et_bank_name.setError("Bank Name is required field!");
        } else if (branchName.length() == 0) {
            et_branch_name.setError("Bank Branch is required field!");
        } else if (ifsc.length() == 0) {
            et_ifsc.setError("IFSC Code is required field!");
        } else {
            flag = true;
        }

        return flag;
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

    @Override
    public void onPaymentSaved(String res) {
        utils.hideProgress();
        successAlert("Payment preference saved successfully");
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
    public void onPaymentSavedError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }

    @Override
    public void onGetPayment(PaymentPref paymentPref) {
        utils.hideProgress();
        if (paymentPref != null) {
            displayPayment(paymentPref);
        }
    }

    private void displayPayment(PaymentPref paymentPref) {
        et_acc_holder_name.setText(paymentPref.accountHolderName);
        et_acc_no.setText(paymentPref.accountHolderNumber);
        et_ifsc.setText(paymentPref.ifscCode);
        int pos = Integer.parseInt(paymentPref.id) - 1;
        spAccountType.setSelection(pos);
        et_branch_name.setText(paymentPref.bankBranch);
        et_bank_name.setText(paymentPref.bankName);
        bankDetailId = paymentPref.bankDetailId;

    }

    @Override
    public void onGetPaymentError(String res) {
        utils.hideProgress();
    }
}