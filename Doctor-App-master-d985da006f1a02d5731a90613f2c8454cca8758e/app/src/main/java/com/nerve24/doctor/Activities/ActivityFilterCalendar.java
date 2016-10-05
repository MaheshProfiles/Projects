package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.ApiTask.API_Get_Patients_Search;
import com.nerve24.doctor.ApiTask.API_SearchByType;
import com.nerve24.doctor.Listeners.Listener_Doctor_Search;
import com.nerve24.doctor.Listeners.Listener_Patient_Search;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.InstantAutoCompleteTextView;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.AppointmentFor;
import com.nerve24.doctor.pojo.AppointmentStatus;
import com.nerve24.doctor.pojo.AppointmentType;
import com.nerve24.doctor.pojo.Clinic;
import com.nerve24.doctor.pojo.Nerver24Member;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

/**
 * Created by mahesha on 09-08-16.
 */
public class ActivityFilterCalendar extends AppCompatActivity implements Listener_Patient_Search, Listener_Doctor_Search, View.OnClickListener {
    private Button btnApply;
    private Button btnReset;
    private LinearLayout btnAllClinics;
    private LinearLayout btnAppointmentType;
    private LinearLayout btnAppointmentFor;
    private LinearLayout btnSlotType;
    private LinearLayout btnReferral;
    private LinearLayout btnSearchReferrals;
    private LinearLayout btnStatus;
    private LinearLayout btnMore;
    private LinearLayout btnBookingSource;
    private LinearLayout btnSearchPatient;
    private LinearLayout btnMinimize;
    private LinearLayout llFilterAllClinics;
    private RadioGroup llFilterAppointmentType;
    private RadioGroup llFilterAppointmentFor;
    private RadioGroup llFilterStatus;
    private TextView txtClinic;
    private TextView txtAppointmentType;
    private TextView txtAppointmentFor;
    private TextView txtStatus;
    private TextView txtSlotType;
    private TextView txtReferral;
    private TextView txtSearchReferrals;
    private TextView txtBookingSource;
    private TextView txtSearchPatient;
    private InstantAutoCompleteTextView atFilterSearchPatient;
    private RadioGroup llFilterSlotType;
    private RadioGroup llFilterReferral;
    private InstantAutoCompleteTextView atFilterSearchReferrals;
    private InstantAutoCompleteTextView atFilterBookingSource;
    private TextInputLayout tilFilterSearchReferral;
    private TextInputLayout tilFilterPatient;
    private TextInputLayout tilFilterBookingSource;

    private Activity mActivity;
    private Utils utils;
    private Session session;
    private String TAG = "", MODULE = "Filter By";
    private String sPatientNerve24Id = null;
    private String sReferredByDoctorNerve24Id = null;
    private String sAppointmentType = null;
    private String sAppointmentFor = null;
    private String sStatus = null;
    private String sReferral = null;
    private String sSlotType = null;
    private String sBookingSource = null;
    private String sPatientSearched;
    private String sReferralSearched;
    private ArrayList<String> filterAppointmentClinics;
    private API_SearchByType apiSearchByType;
    TextWatcher twSearchReferrals = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            sReferralSearched = atFilterSearchReferrals.getText().toString().trim();
            if (sReferralSearched.length() > 0) {
                searchDoctor(sReferralSearched);
            }
            if (sReferralSearched.length() == 0) {
                atFilterSearchReferrals.setError("Select Doctor!");
            } else {
                atFilterSearchReferrals.setError(null);
            }
        }
    };
    private Map<String, Nerver24Member> patientSearchMap = new HashMap<>();
    private Map<String, String> idNameMap = new HashMap<>();
    private ArrayList<String> nameList = new ArrayList<>();
    private Nerver24Member patientSearch;
    private API_Get_Patients_Search apiGetPatientsSearch;
    TextWatcher twSearchPatient = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            sPatientSearched = atFilterSearchPatient.getText().toString().trim();
            if (sPatientSearched.length() > 0) {
                searchPatient(sPatientSearched);
            }
            if (sPatientSearched.length() == 0) {
                atFilterSearchPatient.setError("Select patient!");
            } else {
                atFilterSearchPatient.setError(null);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_calendar);
        initUI();
        Fabric.with(this, new Crashlytics());
    }

    private void initUI() {
        try {
            TAG = "initUI";
            mActivity = ActivityFilterCalendar.this;
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
            btnSlotType = (LinearLayout) findViewById(R.id.ll_slot_type);
            btnBookingSource = (LinearLayout) findViewById(R.id.ll_booking_source);
            btnReferral = (LinearLayout) findViewById(R.id.ll_referral);
            btnSearchReferrals = (LinearLayout) findViewById(R.id.ll_search_referrals);
            btnAllClinics = (LinearLayout) findViewById(R.id.ll_allclinics);
            btnMore = (LinearLayout) findViewById(R.id.ll_more);
            btnSearchPatient = (LinearLayout) findViewById(R.id.ll_search_patient);
            btnAppointmentFor = (LinearLayout) findViewById(R.id.ll_appointment_for);
            btnAppointmentType = (LinearLayout) findViewById(R.id.ll_appointment_type);
            btnStatus = (LinearLayout) findViewById(R.id.ll_status);
            btnMinimize = (LinearLayout) findViewById(R.id.ll_minimize);
            llFilterAllClinics = (LinearLayout) findViewById(R.id.ll_filter_allclinics);
            llFilterAppointmentType = (RadioGroup) findViewById(R.id.ll_filter_appointment_type);
            llFilterAppointmentFor = (RadioGroup) findViewById(R.id.ll_filter_appointment_for);
            llFilterStatus = (RadioGroup) findViewById(R.id.ll_filter_status);
            atFilterSearchPatient = (InstantAutoCompleteTextView) findViewById(R.id.et_filter_search_patient);
            llFilterSlotType = (RadioGroup) findViewById(R.id.ll_filter_slot_type);
            llFilterReferral = (RadioGroup) findViewById(R.id.ll_filter_referral);
            atFilterSearchReferrals = (InstantAutoCompleteTextView) findViewById(R.id.et_filter_search_referral);
            tilFilterPatient = (TextInputLayout) findViewById(R.id.til_patient);
            tilFilterSearchReferral = (TextInputLayout) findViewById(R.id.til_search_referral);
            tilFilterBookingSource = (TextInputLayout) findViewById(R.id.til_booking_source);
            atFilterBookingSource = (InstantAutoCompleteTextView) findViewById(R.id.et_booking_source);
            btnAllClinics.setBackgroundResource(R.color.white);
            txtClinic = (TextView) findViewById(R.id.tv_allclinics);
            txtAppointmentType = (TextView) findViewById(R.id.tv_appointment_type);
            txtAppointmentFor = (TextView) findViewById(R.id.tv_appointment_for);
            txtStatus = (TextView) findViewById(R.id.tv_status);
            txtSearchPatient = (TextView) findViewById(R.id.tv_search_patient);
            txtSlotType = (TextView) findViewById(R.id.tv_slot_type);
            txtBookingSource = (TextView) findViewById(R.id.tv_booking_source);
            txtReferral = (TextView) findViewById(R.id.tv_referral);
            txtSearchReferrals = (TextView) findViewById(R.id.tv_search_referrals);
            txtClinic.setTextColor(R.color.colorPrimary);
            initListeners();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void initListeners() {
        try {
            TAG = "initListeners";
            btnApply.setOnClickListener(this);
            btnReset.setOnClickListener(this);
            btnAllClinics.setOnClickListener(this);
            btnAppointmentType.setOnClickListener(this);
            btnAppointmentFor.setOnClickListener(this);
            btnStatus.setOnClickListener(this);
            btnSearchPatient.setOnClickListener(this);
            btnMore.setOnClickListener(this);
            btnSlotType.setOnClickListener(this);
            btnReferral.setOnClickListener(this);
            btnBookingSource.setOnClickListener(this);
            btnSearchReferrals.setOnClickListener(this);
            btnMinimize.setOnClickListener(this);
            atFilterSearchPatient.setThreshold(0);
            atFilterSearchPatient.addTextChangedListener(twSearchPatient);
            atFilterSearchPatient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                    String selection = (String) parent.getItemAtPosition(position);
                    Log.e("idNameMap.get(selection", "" + idNameMap.get(selection));
                    patientSearch = patientSearchMap.get(idNameMap.get(selection));
                    sPatientNerve24Id = patientSearch.nerve24Id;
                }
            });
            atFilterSearchReferrals.setThreshold(0);
            atFilterSearchReferrals.addTextChangedListener(twSearchReferrals);
            atFilterSearchReferrals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                    String selection = (String) parent.getItemAtPosition(position);
                    Log.e("idNameMap.get(selection", "" + idNameMap.get(selection));
                    patientSearch = patientSearchMap.get(idNameMap.get(selection));
                    sReferredByDoctorNerve24Id = patientSearch.nerve24Id;
                }
            });
            atFilterSearchPatient.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchPatient(sPatientSearched);
                        return true;
                    }
                    return false;
                }
            });
            atFilterSearchReferrals.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchDoctor(sReferralSearched);
                        return true;
                    }
                    return false;
                }
            });
            setValues();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void setValues() {
        setAllClinics();
        String jsonSession = session.getCalendarFilters();
        Log.d("json CalenderFilter", jsonSession.toString());
        if (jsonSession != "" && !jsonSession.isEmpty()) {
            setSelectedValues();
        }
    }

    private void setSelectedValues() {
        try {
            int childCount = 0;
            String jsonSession = session.getCalendarFilters();
            JSONObject jsonObj = new JSONObject(jsonSession);
            JSONArray clinicIdList = jsonObj.getJSONArray("clinicIdList");
            ArrayList<String> listdata = new ArrayList<String>();
            System.out.println("The clinicIdList name is: " + clinicIdList);
            for (int i = 0; i < clinicIdList.length(); i++) {
                listdata.add(clinicIdList.get(i).toString());
            }
            childCount = llFilterAllClinics.getChildCount();
            if (childCount > 1) {
                for (int i = 0; i < listdata.size(); i++) {
                    listdata.get(i).toString();
                    System.out.println("The clinicList name is: " + listdata.get(i).toString());
                    List<Clinic> clinicsList = session.getClinics();
                    for (int j = 0; j < clinicsList.size(); j++) {
                        if (Integer.parseInt(clinicsList.get(j).clinicId) == Integer.parseInt(listdata.get(i).toString())) {
                            for (int k = 0; k < childCount; k++) {
                                LinearLayout childView = (LinearLayout) llFilterAllClinics.getChildAt(i);
                                CheckBox check1 = (CheckBox) childView.findViewById(R.id.cb_filter);
                                if (check1.getText().toString().equals(clinicsList.get(j).clinicName))
                                    check1.setChecked(true);
                            }

                        }
                    }
                }
            }
            JSONObject appointmentFilterDTO = jsonObj.getJSONObject("appointmentFilterDTO");
            System.out.println("The appointmentFilterDTO is: " + appointmentFilterDTO);
            String appointmentType = appointmentFilterDTO.getString("appointmentType");
            System.out.println("The appointmentType is: " + appointmentType);
            String appointmentFor = appointmentFilterDTO.getString("appointmentFor");
            System.out.println("The appointmentFor is: " + appointmentFor);
            String status = appointmentFilterDTO.getString("status");
            System.out.println("The status is: " + status);
            String slotType = appointmentFilterDTO.getString("slotType");
            System.out.println("The slotType is: " + slotType);
            String referral = appointmentFilterDTO.getString("referral");
            System.out.println("The referral is: " + referral);

        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                saveFilterState();
                Log.d("QueryLang", session.getCalendarFilters());
                break;
            case R.id.btn_cancel:
                clearAllFilters();
                break;
            case R.id.ll_more:
                displayMoreOptions();
                break;
            case R.id.ll_minimize:
                displayMoreOptions();
                break;
            case R.id.ll_allclinics:
                applyDefaultBackground();
                backgroundColor(btnAllClinics);
                backgroundTextColor(txtClinic);
                viewParentLayout(llFilterAllClinics);
                setAllClinics();
                break;
            case R.id.ll_appointment_type:
                applyDefaultBackground();
                backgroundColor(btnAppointmentType);
                backgroundTextColor(txtAppointmentType);
                viewParentLayout(llFilterAppointmentType);
                setAppointmentType();
                break;
            case R.id.ll_appointment_for:
                applyDefaultBackground();
                backgroundColor(btnAppointmentFor);
                backgroundTextColor(txtAppointmentFor);
                viewParentLayout(llFilterAppointmentFor);
                setAppointmentFor();
                break;
            case R.id.ll_status:
                applyDefaultBackground();
                backgroundColor(btnStatus);
                backgroundTextColor(txtStatus);
                viewParentLayout(llFilterStatus);
                setStatus();
                break;
            case R.id.ll_search_patient:
                applyDefaultBackground();
                backgroundColor(btnSearchPatient);
                backgroundTextColor(txtSearchPatient);
                viewParentEditTextLayout(atFilterSearchPatient);
                tilFilterPatient.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_slot_type:
                applyDefaultBackground();
                backgroundColor(btnSlotType);
                backgroundTextColor(txtSlotType);
                viewParentLayout(llFilterSlotType);
                setSlotType();
                break;
            case R.id.ll_booking_source:
                applyDefaultBackground();
                backgroundColor(btnBookingSource);
                backgroundTextColor(txtBookingSource);
                tilFilterBookingSource.setVisibility(View.VISIBLE);
                viewParentEditTextLayout(atFilterBookingSource);
                break;
            case R.id.ll_referral:
                applyDefaultBackground();
                backgroundColor(btnReferral);
                backgroundTextColor(txtReferral);
                viewParentLayout(llFilterReferral);
                setReferral();
                break;
            case R.id.ll_search_referrals:
                applyDefaultBackground();
                backgroundColor(btnSearchReferrals);
                backgroundTextColor(txtSearchReferrals);
                tilFilterSearchReferral.setVisibility(View.VISIBLE);
                viewParentEditTextLayout(atFilterSearchReferrals);
                break;
        }
    }

    private void displayMoreOptions() {
        if (btnSearchReferrals.getVisibility() == View.GONE) {
            btnSearchReferrals.setVisibility(View.VISIBLE);
            btnSlotType.setVisibility(View.VISIBLE);
            btnBookingSource.setVisibility(View.VISIBLE);
            btnReferral.setVisibility(View.VISIBLE);
            btnMinimize.setVisibility(View.VISIBLE);
            btnMore.setVisibility(View.GONE);
        } else {
            btnSearchReferrals.setVisibility(View.GONE);
            btnSlotType.setVisibility(View.GONE);
            btnReferral.setVisibility(View.GONE);
            btnMinimize.setVisibility(View.GONE);
            btnBookingSource.setVisibility(View.GONE);
            btnMore.setVisibility(View.VISIBLE);
        }
    }

    private void setAllClinics() {
        int childCount = llFilterAllClinics.getChildCount();
        if (childCount < 1)
            addClinicsView();
        String jsonSession = session.getCalendarFilters();
        Log.d("json CalenderFilter", jsonSession.toString());
        if (jsonSession != "" && !jsonSession.isEmpty()) {
            setSelectedValues();
        }
    }

    private void addClinicsView() {
        List<Clinic> clinicsList = session.getClinics();
        llFilterAllClinics.removeAllViewsInLayout();
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View to_add = inflater.inflate(R.layout.item_filter_content, llFilterAllClinics, false);
        final CheckBox allClinic = (CheckBox) to_add.findViewById(R.id.cb_filter);
        allClinic.setText("All");
        allClinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allClinic.isChecked())
                    getClinicsChecked(true);
                else
                    getClinicsChecked(false);
            }
        });
        llFilterAllClinics.addView(to_add);
        if (clinicsList != null) {
            for (int i = 0; i < clinicsList.size(); i++) {
                final String clinicId = clinicsList.get(i).clinicId;
                filterAppointmentClinics = new ArrayList<>();
                inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                to_add = inflater.inflate(R.layout.item_filter_content, llFilterAllClinics, false);
                final CheckBox clinic1 = (CheckBox) to_add.findViewById(R.id.cb_filter);
                clinic1.setText(clinicsList.get(i).clinicName);
                llFilterAllClinics.addView(to_add);
                clinic1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            filterAppointmentClinics.add(clinicId);
                        } else {
                            setClinicUnchecked();
                            filterAppointmentClinics.remove(clinicId);
                        }
                    }

                });
            }
        }
    }

    private void getClinicsChecked(boolean isChecked) {
        int childCount = llFilterAllClinics.getChildCount();
        for (int i = 0; i < childCount; i++) {
            LinearLayout childView = (LinearLayout) llFilterAllClinics.getChildAt(i);
            CheckBox check1 = (CheckBox) childView.findViewById(R.id.cb_filter);
            check1.setChecked(isChecked);
        }
    }

    private void setClinicUnchecked() {
        LinearLayout childView = (LinearLayout) llFilterAllClinics.getChildAt(0);
        CheckBox removeAllChecked = (CheckBox) childView.findViewById(R.id.cb_filter);
        removeAllChecked.setChecked(false);
    }

    private void setAppointmentType() {
        final List<AppointmentType> appointmentType = session.getAppointmentType();
        int childCount = llFilterAppointmentType.getChildCount();
        if (childCount < 1)
            addAppointmentTypeViews();
        llFilterAppointmentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = group.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(selectedId);
                if (radioButton.getText().toString().startsWith("All"))
                    sAppointmentType = null;
                else
                    sAppointmentType = radioButton.getText().toString();
            }
        });
    }

    private void searchPatient(String key) {
        try {
            if (apiGetPatientsSearch != null) {
                apiGetPatientsSearch.stopSearch();
            }
            apiGetPatientsSearch = new API_Get_Patients_Search(key, ActivityFilterCalendar.this, mActivity);
            apiGetPatientsSearch.getPatients();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchDoctor(String doctor) {
        try {
            if (apiSearchByType != null) {
                apiSearchByType.stopSearch();
            }
            JSONObject obj = new JSONObject();
            obj.put("username", doctor);
            obj.put("userType", "DOCTOR");
            apiSearchByType = new API_SearchByType(obj, ActivityFilterCalendar.this, mActivity);
            apiSearchByType.getSearchType();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addAppointmentTypeViews() {
        final List<AppointmentType> appointmentType = session.getAppointmentType();
        llFilterAppointmentType.removeAllViewsInLayout();
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View to_add = inflater.inflate(R.layout.item_filter_patient, llFilterAppointmentType, false);
        final RadioButton allClinic = (RadioButton) to_add.findViewById(R.id.rb_filter);
        allClinic.setText("All");
        allClinic.setId(Integer.parseInt("10" + appointmentType.size()) + 1);
        llFilterAppointmentType.addView(to_add);
        for (int i = 0; i < appointmentType.size(); i++) {
            inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            to_add = inflater.inflate(R.layout.item_filter_patient, llFilterAppointmentType, false);
            final RadioButton clinic1 = (RadioButton) to_add.findViewById(R.id.rb_filter);

            clinic1.setId(Integer.parseInt("1" + i));
            clinic1.setText(appointmentType.get(i).value);
            llFilterAppointmentType.addView(to_add);
        }
    }

    private void setAppointmentFor() {
        final List<AppointmentFor> appointmentFor = session.getAppointmentFor();
        int childCount = llFilterAppointmentFor.getChildCount();
        if (childCount < 1)
            addAppointmentForViews();
        llFilterAppointmentFor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = group.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(selectedId);
                if (selectedId > (Integer.parseInt("2" + appointmentFor.size())))
                    sAppointmentFor = null;
                else
                    sAppointmentFor = radioButton.getText().toString();
            }
        });

    }

    private void addAppointmentForViews() {
        final List<AppointmentFor> appointmentFor = session.getAppointmentFor();
        llFilterAppointmentFor.removeAllViewsInLayout();
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View to_add = inflater.inflate(R.layout.item_filter_patient, llFilterAppointmentFor, false);
        final RadioButton allClinic = (RadioButton) to_add.findViewById(R.id.rb_filter);
        allClinic.setId(Integer.parseInt("20" + appointmentFor.size()) + 1);
        allClinic.setText("All");
        llFilterAppointmentFor.addView(to_add);
        for (int i = 0; i < appointmentFor.size(); i++) {
            inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            to_add = inflater.inflate(R.layout.item_filter_patient, llFilterAppointmentFor, false);
            final RadioButton clinic1 = (RadioButton) to_add.findViewById(R.id.rb_filter);
            clinic1.setId(Integer.parseInt("2" + i));
            clinic1.setText(appointmentFor.get(i).value);
            llFilterAppointmentFor.addView(to_add);
        }
    }

    private void setStatus() {
        final List<AppointmentStatus> appointmentStatus = session.getAppointmentStatus();
        int childCount = llFilterStatus.getChildCount();
        if (childCount < 1)
            addStatusViews();
        llFilterStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = group.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(selectedId);
                if (selectedId > (Integer.parseInt("3" + appointmentStatus.size())))
                    sStatus = null;
                else
                    sStatus = radioButton.getText().toString();
            }
        });
    }

    private void addStatusViews() {
        final List<AppointmentStatus> appointmentStatus = session.getAppointmentStatus();
        llFilterStatus.removeAllViewsInLayout();
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View to_add = inflater.inflate(R.layout.item_filter_patient, llFilterStatus, false);
        final RadioButton allClinic = (RadioButton) to_add.findViewById(R.id.rb_filter);
        allClinic.setId(Integer.parseInt("30" + appointmentStatus.size()) + 1);
        allClinic.setText("All");
        llFilterStatus.addView(to_add);
        for (int i = 0; i < appointmentStatus.size(); i++) {
            inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            to_add = inflater.inflate(R.layout.item_filter_patient, llFilterStatus, false);
            final RadioButton clinic1 = (RadioButton) to_add.findViewById(R.id.rb_filter);
            if (!appointmentStatus.get(i).value.equals("Cancelled")) {
                clinic1.setId(Integer.parseInt("3" + i));
                clinic1.setText(appointmentStatus.get(i).value);
                llFilterStatus.addView(to_add);
            }
        }
    }

    private void setSlotType() {
        ArrayList<String> slotType = new ArrayList<String>() {{
            add("All");
            add("Premium");
            add("Non premium");
        }};
        int childCount = llFilterSlotType.getChildCount();
        if (childCount < 1)
            addSlotTypeViews();
        llFilterSlotType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = group.getCheckedRadioButtonId();
                if (selectedId == 40)
                    sSlotType = null;
                else if (selectedId == 41)
                    sSlotType = "true";
                else
                    sSlotType = "false";
            }

        });
    }

    private void addSlotTypeViews() {
        ArrayList<String> slotType = new ArrayList<String>() {{
            add("All");
            add("Premium");
            add("Non premium");
        }};
        llFilterSlotType.removeAllViewsInLayout();
        for (int i = 0; i < slotType.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View to_add = inflater.inflate(R.layout.item_filter_patient, llFilterSlotType, false);
            final RadioButton clinic1 = (RadioButton) to_add.findViewById(R.id.rb_filter);
            clinic1.setId(Integer.parseInt("4" + i));
            clinic1.setText(slotType.get(i).toString());
            llFilterSlotType.addView(to_add);
        }
    }

    private void setReferral() {
        final ArrayList<String> referral = new ArrayList<String>() {{
            add("All");
            add("Only Referral");
        }};
        int childCount = llFilterReferral.getChildCount();
        if (childCount < 1)
            addReferralViews();
        llFilterReferral.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = group.getCheckedRadioButtonId();
                if (selectedId == 50)
                    sReferral = "false";
                else
                    sReferral = "true";
            }

        });
    }

    private void addReferralViews() {
        final ArrayList<String> referral = new ArrayList<String>() {{
            add("All");
            add("Only Referral");
        }};
        llFilterReferral.removeAllViewsInLayout();
        for (int i = 0; i < referral.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View to_add = inflater.inflate(R.layout.item_filter_patient, llFilterReferral, false);
            final RadioButton clinic1 = (RadioButton) to_add.findViewById(R.id.rb_filter);
            clinic1.setId(Integer.parseInt("5" + i));
            clinic1.setText(referral.get(i).toString());
            llFilterReferral.addView(to_add);
        }
    }

    private void backgroundColor(LinearLayout clickedLayout) {
        clickedLayout.setBackgroundResource(R.color.white);
    }

    private void applyDefaultBackground() {
        removeParentLayouts();
        btnAllClinics.setBackgroundResource(R.color.grey_for_clendar_day);
        btnAppointmentType.setBackgroundResource(R.color.grey_for_clendar_day);
        btnAppointmentFor.setBackgroundResource(R.color.grey_for_clendar_day);
        btnStatus.setBackgroundResource(R.color.grey_for_clendar_day);
        btnSearchPatient.setBackgroundResource(R.color.grey_for_clendar_day);
        btnSlotType.setBackgroundResource(R.color.grey_for_clendar_day);
        btnBookingSource.setBackgroundResource(R.color.grey_for_clendar_day);
        btnReferral.setBackgroundResource(R.color.grey_for_clendar_day);
        btnSearchReferrals.setBackgroundResource(R.color.grey_for_clendar_day);
        applyDefaultTextColors();
    }


    private void backgroundTextColor(TextView clickedLayout) {
        clickedLayout.setTextColor(R.color.colorPrimary);
    }

    private void applyDefaultTextColors() {
        txtClinic.setTextColor(R.color.colorPrimary);
        txtAppointmentType.setTextColor(R.color.colorPrimary);
        txtAppointmentFor.setTextColor(R.color.colorPrimary);
        txtStatus.setTextColor(R.color.colorPrimary);
        txtSearchPatient.setTextColor(R.color.colorPrimary);
        txtSlotType.setTextColor(R.color.colorPrimary);
        txtBookingSource.setTextColor(R.color.colorPrimary);
        txtReferral.setTextColor(R.color.colorPrimary);
        txtSearchReferrals.setTextColor(R.color.colorPrimary);
    }

    private void viewParentLayout(LinearLayout clickedLayout) {
        clickedLayout.setVisibility(View.VISIBLE);
    }

    private void viewParentEditTextLayout(EditText clickedLayout) {
        clickedLayout.setVisibility(View.VISIBLE);
    }

    private void removeParentLayouts() {
        llFilterAllClinics.setVisibility(View.GONE);
        llFilterAppointmentType.setVisibility(View.GONE);
        llFilterAppointmentFor.setVisibility(View.GONE);
        llFilterStatus.setVisibility(View.GONE);
        atFilterSearchPatient.setVisibility(View.GONE);
        llFilterSlotType.setVisibility(View.GONE);
        llFilterReferral.setVisibility(View.GONE);
        tilFilterBookingSource.setVisibility(View.GONE);
        atFilterBookingSource.setVisibility(View.GONE);
        atFilterSearchReferrals.setVisibility(View.GONE);
        tilFilterSearchReferral.setVisibility(View.GONE);
        tilFilterPatient.setVisibility(View.GONE);
    }

    private void saveFilterState() {
        if (validateChangePassword()) {
            JSONObject objAppointmentFilterDTO = new JSONObject();
            JSONObject objFilter = new JSONObject();
            try {
                if (sPatientNerve24Id != null)
                    objAppointmentFilterDTO.put("patientNerve24Id", sPatientNerve24Id);
                else
                    objAppointmentFilterDTO.put("patientNerve24Id", JSONObject.NULL);
                if (sReferredByDoctorNerve24Id != null)
                    objAppointmentFilterDTO.put("referredByDoctorNerve24Id", sReferredByDoctorNerve24Id);
                else
                    objAppointmentFilterDTO.put("referredByDoctorNerve24Id", JSONObject.NULL);
                if (sAppointmentType != null)
                    objAppointmentFilterDTO.put("appointmentType", sAppointmentType);
                else
                    objAppointmentFilterDTO.put("appointmentType", JSONObject.NULL);
                if (sAppointmentFor != null)
                    objAppointmentFilterDTO.put("appointmentFor", sAppointmentFor);
                else
                    objAppointmentFilterDTO.put("appointmentFor", JSONObject.NULL);
                if (sStatus != null)
                    objAppointmentFilterDTO.put("status", sStatus);
                else
                    objAppointmentFilterDTO.put("status", JSONObject.NULL);
                if (sSlotType != null)
                    objAppointmentFilterDTO.put("slotType", sSlotType);
                else
                    objAppointmentFilterDTO.put("slotType", JSONObject.NULL);
                if (sBookingSource != null)
                    objAppointmentFilterDTO.put("bookingSource", sBookingSource);
                else
                    objAppointmentFilterDTO.put("bookingSource", JSONObject.NULL);
                if (sReferral != null)
                    objAppointmentFilterDTO.put("referral", sReferral);
                else
                    objAppointmentFilterDTO.put("referral", JSONObject.NULL);
                objFilter.put("fromAppointmentDate", Utils.getCurrentTimeStamp("dd-MM-yyyy"));
                objFilter.put("toAppointmentDate", Utils.getCurrentTimeStamp("dd-MM-yyyy"));
                objFilter.put("doctorNerve24Id", session.getNerve24Id());
                objFilter.put("clinicIdList", new JSONArray(filterAppointmentClinics));
                objFilter.put("appointmentFilterDTO", objAppointmentFilterDTO);
                new Session(mActivity).saveCalendarFilters(objFilter.toString());
                Log.d("filters", session.getCalendarFilters());
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearAllFilters() {
        addClinicsView();
        addAppointmentTypeViews();
        addAppointmentForViews();
        addStatusViews();
        addSlotTypeViews();
        addReferralViews();
        atFilterSearchPatient.setText("");
        atFilterSearchReferrals.setText("");
    }

    private boolean validateChangePassword() {
        boolean flag = false;
        if (filterAppointmentClinics == null || filterAppointmentClinics.isEmpty()) {
            Utils.alertBox(mActivity, "Select the Clinics");
        } else {
            flag = true;
        }
        return flag;
    }

    @Override
    public void onGetPatients(ArrayList<String> nameList, Map<String, String> idNameMap, Map<String, Nerver24Member> patientSearchMap) {
        if (nameList.size() > 0) {
            atFilterSearchPatient.setError(null);
            this.nameList = nameList;
            this.idNameMap = idNameMap;
            this.patientSearchMap = patientSearchMap;

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, nameList);
            atFilterSearchPatient.setAdapter(adapter);
            atFilterSearchPatient.showDropDown();

        } else {
            if ((atFilterSearchPatient.getAdapter().isEmpty()))
                atFilterSearchPatient.setError("No Such Patient With " + sPatientSearched);
        }
    }

    @Override
    public void onGetPatientsError(String res) {
        try {
            String sPatientSearched = atFilterSearchPatient.getText().toString().trim();
            if ((atFilterSearchPatient.getAdapter().isEmpty()))
                atFilterSearchPatient.setError("No such patient with " + sPatientSearched);
        } catch (Exception e) {

        }
    }

    @Override
    public void onGetDoctors(ArrayList<String> nameList, Map<String, String> idNameMap, Map<String, Nerver24Member> patientSearchMap) {
        if (nameList.size() > 0) {
            atFilterSearchReferrals.setError(null);
            this.nameList = nameList;
            this.idNameMap = idNameMap;
            this.patientSearchMap = patientSearchMap;

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, nameList);
            atFilterSearchReferrals.setAdapter(adapter);
            atFilterSearchReferrals.showDropDown();
        } else {
            if ((atFilterSearchReferrals.getAdapter().isEmpty()))
                atFilterSearchReferrals.setError("No Such Doctor With " + sReferralSearched);
        }
    }

    @Override
    public void onGetDoctorsError(String res) {
    }

    @Override
    public void onGetDoctors(ArrayList<Nerver24Member> doctorList) {

    }
}
