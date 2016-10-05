package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.ApiTask.API_Edit_Price_Clinic;
import com.nerve24.doctor.Listeners.Listener_Edit_Price_Clinic;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;


public class ActivityEditClinicPrice extends AppCompatActivity implements View.OnClickListener, Listener_Edit_Price_Clinic {
    private String TAG = "", MODULE = "ActivityEditClinicPrice";
    private Activity mActivity;
    private Utils utils;
    private Session session;
    private EditText etClinicName, etClinicAddress, etFee, etDoctorDiscount, etnerve24Discount;
    private CheckBox cbCreditCard, cbDebitCard, cbCashOnly, cbNetBanking;
    private Button btnCancel, btnSave;
    private String clinicName = "", clinicId = "", locality = "", fee = "", oldFee = "", doctorDiscount = "", nerve24Discount = "";
    private JSONObject jsonObjectPriceSetup = new JSONObject();
    private JSONArray jsonArrayPriceSetup = new JSONArray();
    private int position = -1;
    private boolean overridden;

    private HashMap<String, String> paymentMethodMasterMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_clinic_price);
        initUI();
        Fabric.with(this, new Crashlytics());
    }

    private void initUI() {
        try {
            TAG = "initUI";
            mActivity = ActivityEditClinicPrice.this;
            utils = new Utils(mActivity);
            session = new Session(mActivity);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Edit");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            etClinicName = (EditText) findViewById(R.id.et_clinic_name);
            etClinicAddress = (EditText) findViewById(R.id.et_location);
            etFee = (EditText) findViewById(R.id.et_fee);
            etDoctorDiscount = (EditText) findViewById(R.id.et_doctor_discount);
            etnerve24Discount = (EditText) findViewById(R.id.et_nerve24_discount);
            cbCreditCard = (CheckBox) findViewById(R.id.cb_credit_card);
            cbDebitCard = (CheckBox) findViewById(R.id.cb_dedit_card);
            cbCashOnly = (CheckBox) findViewById(R.id.cb_cash_only);
            cbNetBanking = (CheckBox) findViewById(R.id.cb_net_banking);
            btnCancel = (Button) findViewById(R.id.btn_cancel);
            btnSave = (Button) findViewById(R.id.btn_save);
            initListeners();
            getValues();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void getValues() {
        try {
            TAG = "getValues";
            clinicId = getIntent().getStringExtra("clinicId");
            String response = session.getPriceSetup();
            jsonArrayPriceSetup = new JSONArray(response);
            for (int i = 0; i < jsonArrayPriceSetup.length(); i++) {
                JSONObject jsonObject = jsonArrayPriceSetup.getJSONObject(i);
                String cId = jsonObject.getString("clinicId");
                if (cId.equals(clinicId)) {
                    jsonObjectPriceSetup = jsonObject;
                    position = i;
                    break;
                }
            }

            if (jsonObjectPriceSetup.has("clinicId")) {
                setValues();
            }

        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void setValues() {
        try {

            if (jsonObjectPriceSetup.has("overridden"))
                overridden = jsonObjectPriceSetup.getBoolean("overridden");

            if (jsonObjectPriceSetup.has("clinicName"))
                clinicName = jsonObjectPriceSetup.getString("clinicName");
            if (jsonObjectPriceSetup.has("clinicId"))
                clinicId = jsonObjectPriceSetup.getString("clinicId");
            if (jsonObjectPriceSetup.has("locality"))
                locality = jsonObjectPriceSetup.getString("locality");
            if (jsonObjectPriceSetup.has("fee")) {
                fee = jsonObjectPriceSetup.getString("fee");
                oldFee = jsonObjectPriceSetup.getString("fee");
                BigDecimal b=new BigDecimal(fee);
                fee=b.toString();
                oldFee=b.toString();
            }
            if (jsonObjectPriceSetup.has("doctorDiscount"))
                doctorDiscount = jsonObjectPriceSetup.getString("doctorDiscount");
            if (jsonObjectPriceSetup.has("nerve24Discount"))
                nerve24Discount = jsonObjectPriceSetup.getString("nerve24Discount");
            if (jsonObjectPriceSetup.has("paymentMethodList")) {
                if (jsonObjectPriceSetup.isNull("paymentMethodList")) {
                } else {
                    JSONArray array=jsonObjectPriceSetup.getJSONArray("paymentMethodList");
                    for(int i=0;i<array.length();i++) {
                        JSONObject jsonObject1 = array.getJSONObject(i);
                        String paymentMethodId = jsonObject1.getString("paymentMethodId");
                        String paymentMethod = jsonObject1.getString("paymentMethod");
                        paymentMethodMasterMap.put(paymentMethodId, paymentMethod);
                    }

                }
            }

            etClinicName.setText(clinicName);
            etClinicAddress.setText(locality);
            etFee.setText(fee);
            etDoctorDiscount.setText(doctorDiscount);
            etnerve24Discount.setText(nerve24Discount);

            if (paymentMethodMasterMap.containsKey("1"))
                cbCreditCard.setChecked(true);
            if (paymentMethodMasterMap.containsKey("2"))
                cbDebitCard.setChecked(true);
            if (paymentMethodMasterMap.containsKey("3"))
                cbCashOnly.setChecked(true);
            if (paymentMethodMasterMap.containsKey("4"))
                cbNetBanking.setChecked(true);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListeners() {
        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                cancel();
                break;
            case R.id.btn_save:
                pickOption();
                break;
        }
    }

    private void cancel() {
        finish();
    }

    private void pickOption() {
        if (validation()) {
            try {
                boolean hasOverriddenSlots = jsonObjectPriceSetup.getBoolean("hasOverriddenSlots");
                if (hasOverriddenSlots) {
                    final String[] array = mActivity.getResources().getStringArray(R.array.array_pick_apply_for_all);
                    new MaterialDialog.Builder(mActivity)
                            .title("How do you want to apply the change?")
                            .items(array)
                            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    if (which >= 0) {
                                        String selectedItem = text.toString();
                                        if (selectedItem.equals(array[0])) {
                                            save(0);
                                        } else if (selectedItem.equals(array[1])) {
                                            save(1);
                                        } else if (selectedItem.equals(array[2])) {
                                            save(2);
                                        }
                                    }
                                    return true;
                                }

                            })
                            .positiveText("Ok")
                            .show();
                } else {
                    save(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void save(int flag) {
        try {
            TAG = "save";
            JSONObject jsonObject = jsonArrayPriceSetup.getJSONObject(position);
            jsonObject.put("fee", Double.parseDouble(fee));
            jsonObject.put("doctorDiscount", doctorDiscount);
            jsonObject.put("nerve24Discount", nerve24Discount);
            jsonObject.put("overridden", overridden);
            jsonObject.put("updateStatus", flag);


            JSONArray array = new JSONArray();
            for (Map.Entry<String, String> entry : paymentMethodMasterMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                JSONObject jsonObject1 =new JSONObject() ;
                jsonObject1.put("paymentMethodId", key);
                jsonObject1.put("paymentMethod", value);
                array.put(jsonObject1);
            }

            jsonObject.put("paymentMethodList",array);


            Log.e("save", "" + jsonArrayPriceSetup);
            utils.showProgress("", "loading..");
            API_Edit_Price_Clinic apiEditPriceClinic = new API_Edit_Price_Clinic(jsonObject, ActivityEditClinicPrice.this, mActivity);
            apiEditPriceClinic.edit();

        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private boolean validation() {
        boolean flag = false;
        try {
            TAG = "validation";
            fee = etFee.getText().toString().trim();
            doctorDiscount = etDoctorDiscount.getText().toString().trim();
            nerve24Discount = etnerve24Discount.getText().toString().trim();
            if (fee.length() == 0) {
                Utils.alertBox(mActivity, "Fee is empty!");
            } else if (doctorDiscount.length() == 0) {
                Utils.alertBox(mActivity, "Doctor discount is empty!");
            } else if (Double.parseDouble(doctorDiscount) > 100) {
                Utils.alertBox(mActivity, "Doctor discount is not valid!");
            } else if (nerve24Discount.length() == 0) {
                Utils.alertBox(mActivity, "Nerve24 discount is empty!");
            } else if (Double.parseDouble(nerve24Discount) > 100) {
                Utils.alertBox(mActivity, "Nerve24 discount is not valid!");
            } else if (!cbNetBanking.isChecked() && !cbCashOnly.isChecked() && !cbCreditCard.isChecked() && !cbDebitCard.isChecked()) {
                Utils.alertBox(mActivity, "Select payment method!");
            } else {
                if (oldFee.equals(fee)) {
                } else {
                    overridden = true;
                }
                getPaymentId();
                flag = true;
            }
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public void onPriceEdited(String res) {
        utils.hideProgress();
        successAlert(res);
    }

    @Override
    public void onPriceEditedError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
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

    private void getPaymentId() {
        paymentMethodMasterMap.clear();
        if (cbCreditCard.isChecked())
            paymentMethodMasterMap.put("1", "Credit card");

        if (cbDebitCard.isChecked())
            paymentMethodMasterMap.put("2", "Debit card");

        if (cbCashOnly.isChecked())
            paymentMethodMasterMap.put("3", "Cash Payment");

        if (cbNetBanking.isChecked())
            paymentMethodMasterMap.put("4", "Net Banking");

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
