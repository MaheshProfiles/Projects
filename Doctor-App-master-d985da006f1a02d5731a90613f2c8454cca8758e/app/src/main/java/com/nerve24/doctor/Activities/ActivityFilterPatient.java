package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.ApiTask.API_Get_Patient_History;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.InstantAutoCompleteTextView;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.Clinic;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by mahesha on 10-08-16.
 */
public class ActivityFilterPatient extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TextView.OnEditorActionListener {
    private Button btnApply;
    private Button btnReset;
    private LinearLayout llClinics;
    private LinearLayout llReferral;
    private LinearLayout llDate;
    private RadioGroup llFilterClinics;
    private LinearLayout llFilterDate;
    private EditText fromDate, toDate;
    private Activity mActivity;
    private InstantAutoCompleteTextView atFilterPatientReferral;
    private TextInputLayout tilFilterPatientReferral;
    private Utils utils;
    private String filterClinic="";
    private String referredToMe = "";
    private Session session;
    private API_Get_Patient_History apiPatientHistoryFilter;
    private String validFrom = "", validTo = "";
    private boolean slotFromOrTo;
    private ArrayList<String> referredToMeList = new ArrayList<>();
    private String TAG = "", MODULE = "Filter By";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_patient);
        initUI();
        Fabric.with(this, new Crashlytics());
    }

    private void initUI() {
        try {
            TAG = "initUI";
            mActivity = ActivityFilterPatient.this;
            utils = new Utils(mActivity);
            session = new Session(mActivity);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.filter_by);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            btnApply = (Button) findViewById(R.id.btn_save);
            btnReset = (Button) findViewById(R.id.btn_cancel);
            btnApply.setText("Apply");
            btnReset.setText("Reset");
            llClinics = (LinearLayout) findViewById(R.id.ll_clinics);
            llReferral = (LinearLayout) findViewById(R.id.ll_referral);
            llDate = (LinearLayout) findViewById(R.id.ll_date);
            llFilterClinics = (RadioGroup) findViewById(R.id.ll_filter_clinics);
            llFilterDate = (LinearLayout) findViewById(R.id.ll_filter_date);
            atFilterPatientReferral = (InstantAutoCompleteTextView) findViewById(R.id.in_filter_patient);
            tilFilterPatientReferral = (TextInputLayout) findViewById(R.id.til_filter_patient);
            llClinics.setBackgroundResource(R.color.white);
            initListeners();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                savePatientFilterState();
                Log.e("Session", session.getPatientFilters());
                break;
            case R.id.btn_cancel:
                clearAllFilters();
                break;
            case R.id.ll_clinics:
                applyDefaultbackground();
                backgroundColor(llClinics);
                llFilterClinics.setVisibility(View.VISIBLE);
                setAllClinics();
                break;
            case R.id.ll_referral:
                applyDefaultbackground();
                backgroundColor(llReferral);
                atFilterPatientReferral.setVisibility(View.VISIBLE);
                tilFilterPatientReferral.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_date:
                applyDefaultbackground();
                backgroundColor(llDate);
                viewParentLayout(llFilterDate);
                setDate();
                break;
            case R.id.in_filter_patient:
                atFilterPatientReferral.showDropDown();
                break;
        }
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

    private void initListeners() {
        try {
            TAG = "initListeners";
            atFilterPatientReferral.setOnClickListener(this);
            atFilterPatientReferral.setOnEditorActionListener(this);
            btnApply.setOnClickListener(this);
            btnReset.setOnClickListener(this);
            llClinics.setOnClickListener(this);
            llReferral.setOnClickListener(this);
            llDate.setOnClickListener(this);
            atFilterPatientReferral.setThreshold(0);
            atFilterPatientReferral.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        atFilterPatientReferral.showDropDown();
                    }
                }
            });
            setValues();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void setValues() {
        try {

            referredToMeList.clear();
            referredToMeList.add("all");
            referredToMeList.add("referred by me");
            referredToMeList.add("referred to me");
            ArrayAdapter<String> adapterDoctor = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, referredToMeList);
            atFilterPatientReferral.setAdapter(adapterDoctor);
            String filter = session.getPatientFilters();
            // {"clinicId":0,"reference":"referred to me",
            // "doctorNerve24Id":"NV4T4A0000","appointmentFromDate":"12-09-2016",
            // "appointmentToDate":"25-09-2016"}
            if (filter.length() > 0) {
                JSONObject jsonObject = new JSONObject(filter);
                validFrom = jsonObject.getString("appointmentFromDate");
                validTo = jsonObject.getString("appointmentToDate");
                referredToMe = jsonObject.getString("reference");
                filterClinic = jsonObject.getString("clinicId");
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String yesterday = Utils.getCurrentTimeStamp("dd-MM-yyyy");
                yesterday = Utils.addDay(yesterday, sdf, -1);
                String fromDate = Utils.addDay(yesterday, sdf, -13);
                validFrom = fromDate;
                validTo = yesterday;
            }
            addDate();
            setAllClinics();
            fromDate.setText(validFrom);
            toDate.setText(validTo);
            atFilterPatientReferral.setText(referredToMe);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setAllClinics() {
        int childCount = llFilterClinics.getChildCount();
        if (childCount <= 0)
            addClinics();
        llFilterClinics.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = group.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(selectedId);
                if (radioButton != null) {
                    if (radioButton.getText().toString().equals("All"))
                        filterClinic = null;
                    else
                        filterClinic = String.valueOf(selectedId);
                }
            }
        });
    }

    private void addClinics() {
        List<Clinic> clinicsList = session.getClinics();
        llFilterClinics.removeAllViewsInLayout();
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View to_add = inflater.inflate(R.layout.item_filter_patient, llFilterClinics, false);
        final RadioButton allClinic = (RadioButton) to_add.findViewById(R.id.rb_filter);
        if (clinicsList != null && !clinicsList.isEmpty()) {
            allClinic.setId(clinicsList.size() + 1);
            allClinic.setText("All");
            allClinic.setChecked(true);
            llFilterClinics.addView(to_add);
            for (int i = 0; i < clinicsList.size(); i++) {
                inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                to_add = inflater.inflate(R.layout.item_filter_patient, llFilterClinics, false);
                final RadioButton clinic1 = (RadioButton) to_add.findViewById(R.id.rb_filter);
                final String clinicId = clinicsList.get(i).clinicId;
                Log.d("clinicId", clinicId);
                Log.d("filterClinic", filterClinic);
                if(clinicId.equals(filterClinic)) {
                    clinic1.setChecked(true);
                    allClinic.setChecked(false);
                }
                clinic1.setId(Integer.parseInt(clinicId));
                clinic1.setText(clinicsList.get(i).clinicName);
                llFilterClinics.addView(to_add);
            }
        }

    }


    private void backgroundColor(LinearLayout clickedLayout) {
        clickedLayout.setBackgroundResource(R.color.white);
    }

    private void applyDefaultbackground() {
        removeParentLayouts();
        llClinics.setBackgroundResource(R.color.grey_for_clendar_day);
        llReferral.setBackgroundResource(R.color.grey_for_clendar_day);
        llDate.setBackgroundResource(R.color.grey_for_clendar_day);
    }

    private void viewParentLayout(LinearLayout clickedLayout) {
        clickedLayout.setVisibility(View.VISIBLE);
    }

    private void savePatientFilterState() {
       /* {"clinicId":0,"reference":"referred to me","doctorNerve24Id":"NV4T4A0000"
                ,"appointmentFromDate":"12-09-2016","appointmentToDate":"25-09-2016"}*/
        if (validation()) {
            JSONObject objFilter = new JSONObject();
            try {
                if (filterClinic != null)
                    objFilter.put("clinicId", filterClinic);
                else
                    objFilter.put("clinicId", JSONObject.NULL);
                if (referredToMe.length() == 0)
                    objFilter.put("reference", JSONObject.NULL);
                else
                    objFilter.put("reference", referredToMe);
                objFilter.put("doctorNerve24Id", session.getNerve24Id());
                objFilter.put("appointmentFromDate", validFrom);
                objFilter.put("appointmentToDate", validTo);
                new Session(mActivity).savePatientFilters(objFilter.toString());
                setResult(RESULT_OK);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validation() {
        boolean flag = false;
        referredToMe = atFilterPatientReferral.getText().toString().trim();
        if (referredToMe.length() > 0 && !referredToMeList.contains(referredToMe)) {
            Utils.alertBox(mActivity, "Invalid Referred To Me");
        } else {
            flag = true;
        }
        return flag;
    }

    private void removeParentLayouts() {
        llFilterClinics.setVisibility(View.GONE);
        llFilterDate.setVisibility(View.GONE);
        atFilterPatientReferral.setVisibility(View.GONE);
        tilFilterPatientReferral.setVisibility(View.GONE);
    }

    private void setDate() {
        int childCount = llFilterDate.getChildCount();
        if (childCount <= 0)
            addDate();
        fromDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) selectDate(true);
            }
        });
        toDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) selectDate(false);
            }
        });
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate(true);
            }
        });
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validFrom != null && !validFrom.isEmpty()) {
                    selectDate(false);
                } else {
                    Utils.alertBox(mActivity, "Select the Start Date");
                }
            }
        });
    }

    private void addDate() {
        llFilterDate.removeAllViewsInLayout();
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View to_add = inflater.inflate(R.layout.item_time_slots, llFilterDate, false);
        fromDate = (EditText) to_add.findViewById(R.id.et_from);
        toDate = (EditText) to_add.findViewById(R.id.et_to);
        btnApply.setFocusableInTouchMode(true);
        btnApply.requestFocus();
        llFilterDate.addView(to_add);
    }

    private void selectDate(boolean slottype) {
        slotFromOrTo = slottype;
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        Calendar maxDate = Calendar.getInstance();
        dpd.setMaxDate(maxDate);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        if (validFrom != null && !validFrom.isEmpty()) {
            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(df.parse(validFrom));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (!slottype)
                dpd.setMinDate(cal);
        }
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void clearAllFilters() {
        try {
            addClinics();
            atFilterPatientReferral.setText("");
            fromDate.setText("");
            toDate.setText("");
            validTo = null;
            validFrom = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        try {
            monthOfYear++;
            if (slotFromOrTo) {
                fromDate.setText("" + dayOfMonth + "/" + monthOfYear + "/" + year);
            } else {
                toDate.setText("" + dayOfMonth + "/" + monthOfYear + "/" + year);
            }
            String day = "" + dayOfMonth;
            String month = "" + monthOfYear;
            String yearStr = "" + year;
            if (day.length() == 1) day = "0" + day;
            if (month.length() == 1) month = "0" + month;
            if (slotFromOrTo) {
                validFrom = day + "-" + month + "-" + yearStr;
            } else {
                validTo = day + "-" + month + "-" + yearStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        switch (textView.getId()) {
            case R.id.atv_search_patient:
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Utils.hideKeyboard(mActivity);
                    return true;
                }
                break;
        }
        return false;
    }
}
