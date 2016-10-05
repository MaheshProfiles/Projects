package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.ApiTask.API_ApplyFeeForAll;
import com.nerve24.doctor.ApiTask.API_Get_Clinics;
import com.nerve24.doctor.ApiTask.API_Get_Price_Setup;
import com.nerve24.doctor.Listeners.Listener_ApplyFeeForAll;
import com.nerve24.doctor.Listeners.Listener_Get_Clinics;
import com.nerve24.doctor.Listeners.Listener_Get_Price_Setup;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.InstantAutoCompleteTextView;
import com.nerve24.doctor.Utility.MultiSelectionSpinner;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.Clinic;
import com.nerve24.doctor.pojo.PaymentMethod;
import com.nerve24.doctor.pojo.PriceSetup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;


public class ActivityPriceandslot extends AppCompatActivity implements Listener_Get_Price_Setup, Listener_Get_Clinics, View.OnClickListener, Listener_ApplyFeeForAll {
    private String TAG = "", MODULE = "ActivityPriceandslot";
    private CardView cvClinicWise, cvDailyWise, cvPaymentPref;
    private ImageButton ibClinicWise, ibDailyWise, ibPaymentPref;
    private EditText etFee;
    public static InstantAutoCompleteTextView atvPayment_method;
    private MultiSelectionSpinner spinnerPayment;
    private Button btnApplyForAll;
    private String fee,userName;
    private Activity mActivity;
    private Utils utils;
    private Session session;
    private JSONArray clinicArray;
    private ArrayList<String> paymentList = new ArrayList<>();
    private Map<String, String> paymentMap = new HashMap();
    private boolean overridden = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priceandslot);

        initView();
        Fabric.with(this, new Crashlytics());
    }

    private void initView() {
        try {
            TAG = "initView";
            mActivity = ActivityPriceandslot.this;
            utils = new Utils(mActivity);
            session = new Session(mActivity);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Pricing & Slot");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            cvClinicWise = (CardView) findViewById(R.id.card_view_clinic_wise);
            cvDailyWise = (CardView) findViewById(R.id.card_day);
            cvPaymentPref = (CardView) findViewById(R.id.card_payment_pref);
            ibClinicWise = (ImageButton) findViewById(R.id.ib_clinic_wise);
            ibDailyWise = (ImageButton) findViewById(R.id.ib_daily_wise);
            ibPaymentPref = (ImageButton) findViewById(R.id.ib_payment_pref);
            etFee = (EditText) findViewById(R.id.et_fee);
            atvPayment_method = (InstantAutoCompleteTextView) findViewById(R.id.atv_payment_method);
            btnApplyForAll = (Button) findViewById(R.id.btn_apply_for_all);
            spinnerPayment = (MultiSelectionSpinner) findViewById(R.id.spinner_payment_method);

            initListeners();

        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void getValues() {
        try {
            if (paymentList.size() > 0) paymentList.clear();
            if (paymentMap.size() > 0) paymentMap.clear();

            List<PaymentMethod> objList = session.getPaymentMethod();
            for (PaymentMethod obj :
                    objList) {
                paymentList.add(obj.paymentMethod);
                paymentMap.put(obj.paymentMethod, obj.paymentMethodId);
            }
            spinnerPayment.setItems(paymentList);

            utils.showProgress("", "loading..");
            String nerve24Id = session.getNerve24Id();
            API_Get_Clinics apiGetClinics = new API_Get_Clinics(nerve24Id, ActivityPriceandslot.this, mActivity);
            apiGetClinics.getClinics();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPriceSetup() {
        utils.showProgress("", "loading..");
        userName = session.getNerve24Id();
        API_Get_Price_Setup apiGetPriceSetup = new API_Get_Price_Setup(userName, ActivityPriceandslot.this, mActivity);
        apiGetPriceSetup.getPriceSetup();
    }

    private void initListeners() {
        cvClinicWise.setOnClickListener(this);
        cvDailyWise.setOnClickListener(this);
        btnApplyForAll.setOnClickListener(this);
        cvPaymentPref.setOnClickListener(this);
        ibClinicWise.setOnClickListener(this);
        ibDailyWise.setOnClickListener(this);
        ibPaymentPref.setOnClickListener(this);

        atvPayment_method.setInputType(InputType.TYPE_NULL);
        atvPayment_method.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    spinnerPayment.performClick();
                }
            }
        });
        atvPayment_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerPayment.performClick();
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.card_view_clinic_wise:
                moveToClinicWise();
                break;
            case R.id.card_day:
                moveToDailyWise();
                break;
            case R.id.card_payment_pref:
                moveToPaymentPref();
                break;

            case R.id.ib_clinic_wise:
                moveToClinicWise();
                break;
            case R.id.ib_daily_wise:
                moveToDailyWise();
                break;
            case R.id.ib_payment_pref:
                moveToPaymentPref();
                break;
            case R.id.btn_apply_for_all:
                getPriceSetup();
                break;
        }
    }


    private void pickOption() {

        if (validation()) {
            Log.e("overridden", "" + overridden);
            if (overridden) {
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
                                        applyForAll(0);
                                    } else if (selectedItem.equals(array[1])) {
                                        applyForAll(1);
                                    } else if (selectedItem.equals(array[2])) {
                                        applyForAll(2);
                                    }
                                }
                                return true;
                            }

                        })
                        .positiveText("Ok")
                        .show();
            } else {
                applyForAll(0);
            }
        }

    }

    private void moveToPaymentPref() {
        Intent i1 = new Intent(getApplicationContext(), ActivityPaymentPreference.class);
        startActivity(i1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void applyForAll(int flag) {
        try {
            utils.showProgress("", "loading..");
           /* {
                "fee": 100,
                    "paymentMethodList": [{
                "paymentMethodId": 1,
                        "paymentMethod": "Credit card"
            }, {
                "paymentMethodId": 2,
                        "paymentMethod": "Debit card"
            }],
                "userName": "NV4T4A0000",
                    "applyForAllType": "0"
            }*/
            JSONObject obj = new JSONObject();
            obj.put("fee", Double.parseDouble(fee));
            JSONArray array = new JSONArray();

            List<String> paymentList=spinnerPayment.getSelectedStrings();
            for(int i=0;i<paymentList.size();i++) {
                JSONObject jsonObject=new JSONObject();
                String name=paymentList.get(i);
                String id=paymentMap.get(name);
                jsonObject.put("paymentMethodId", id);
                jsonObject.put("paymentMethod", name);
                array.put(i,jsonObject);
            }
            obj.put("paymentMethodList",array);
            obj.put("userName", userName);
            obj.put("applyForAllType", flag);
            API_ApplyFeeForAll apiApplyFeeForAll = new API_ApplyFeeForAll(obj, ActivityPriceandslot.this, mActivity);
            apiApplyFeeForAll.applyFee();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private boolean validation() {
        boolean flag = false;
        try {
            TAG = "validation";
            userName = session.getNerve24Id();
            fee = etFee.getText().toString().trim();
            String paymentMethod = atvPayment_method.getText().toString().trim();

            if (clinicArray.length() == 0) {
                Utils.alertBox(mActivity, "There is no clinics to apply!");
            } else if (fee.length() == 0) {
                Utils.alertBox(mActivity, "Fee is empty!");
            } else if (paymentMethod.length() == 0) {
                Utils.alertBox(mActivity, "Select payment method!");
            }  else {
                flag = true;
            }
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
        return flag;
    }


    private void moveToDailyWise() {
        try {
            TAG = "moveToDailyWise";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("clinics", clinicArray);
            jsonObject.put("userName", session.getNerve24Id());

            Intent in = new Intent(getApplicationContext(), ActivityDailyWise.class);
            in.putExtra("json", jsonObject.toString());
            startActivity(in);
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void moveToClinicWise() {
        Intent i1 = new Intent(getApplicationContext(), ActivityClinicwise.class);
        startActivity(i1);
    }

    @Override
    public void onFeeAppliedSuccess(String res) {
        utils.hideProgress();
        reset();
        Utils.alertBox(mActivity, "Applied successfully!");
    }

    private void reset() {
        fee = "";
        etFee.setText("");
        atvPayment_method.setText("");
    }

    @Override
    public void onFeeAppliedFailure(String res) {
        Utils.alertBox(mActivity, res);
        utils.hideProgress();
    }

    @Override
    public void onResume() {
        getValues();
        super.onResume();
    }


    @Override
    public void onGetClinics(ArrayList<Clinic> clinicsList) {
        try {
            clinicArray = new JSONArray();
            for (int i = 0; i < clinicsList.size(); i++) {
                String clinicId = clinicsList.get(i).clinicId;
                clinicArray.put(clinicId);
            }
            utils.hideProgress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetClinicsError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }


    @Override
    public void onGetPriceSetup(ArrayList<PriceSetup> priceSetupList) {
        try {
            TAG = "onGetPriceSetup";
            utils.hideProgress();
            if (priceSetupList.size() > 1) {
                for (int i = 0; i < priceSetupList.size(); i++) {
                    Log.e("i", "" + i);
                    PriceSetup priceSetup = priceSetupList.get(i);
                    if (priceSetup.overridden) {
                        overridden = true;
                        break;
                    }
                }
            }
            pickOption();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    @Override
    public void onGetPriceSetupError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }
}


