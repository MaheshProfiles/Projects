package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.ApiTask.API_Get_Slots_By_Clinic;
import com.nerve24.doctor.ApiTask.API_Save_Appointment;
import com.nerve24.doctor.Listeners.Listener_Get_Slots_By_Clinic;
import com.nerve24.doctor.Listeners.Listener_Save_Appointment;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.InstantAutoCompleteTextView;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.Appointment;
import com.nerve24.doctor.pojo.Clinic;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class ActivityRescheduleAppointment extends AppCompatActivity implements Listener_Save_Appointment, Listener_Get_Slots_By_Clinic, View.OnClickListener {
    private String TAG = "", MODULE = "ActivityRescheduleAppointment";
    private Activity mActivity;
    private Utils utils;
    private Session session;

    private InstantAutoCompleteTextView atvSalutation, atvReferredBy, atvGender, atvClinic, atvType, atvTime, atvAppointmentFor, atvEpisode, atvEncounter;
    private EditText etFirstName, etMiddleName, etLastname, etMobile, etEmail, etDob, etDate;
    private Button btnCancel, btnAdd;
    private DatePickerDialog.OnDateSetListener dateListener;
    private String id, salutation = "", referredBy = "", clinic = "", clinicId = "", slotId = "", appointmentType = "", appointmentFor = "", episodeType = "", encounter = "", fName = "", mName = "", lName = "", mobile = "", email = "", dob = "", appointmentDate = "", appointmentTime = "", gender;
    private ArrayList<String> clinicList = new ArrayList<>();
    private Map<String, String> clinicMap = new HashMap();
    private ArrayList<String> timeList = new ArrayList<>();
    private Map<String, String> timeMap = new HashMap();
    private ArrayList<String> genderList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_appointment);
        initUI();
        Fabric.with(this, new Crashlytics());
    }

    private void initUI() {
        TAG = "initUI";
        mActivity = ActivityRescheduleAppointment.this;
        session = new Session(mActivity);
        utils = new Utils(mActivity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Reschedule Appointment");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextInputLayout til_patient = (TextInputLayout) findViewById(R.id.til_patient);
        til_patient.setVisibility(View.GONE);
        atvSalutation = (InstantAutoCompleteTextView) findViewById(R.id.atv_salutation);
        atvReferredBy = (InstantAutoCompleteTextView) findViewById(R.id.atv_referred_by);
        atvClinic = (InstantAutoCompleteTextView) findViewById(R.id.atv_clinic);
        atvType = (InstantAutoCompleteTextView) findViewById(R.id.atv_type);
        atvTime = (InstantAutoCompleteTextView) findViewById(R.id.atv_time);
        atvAppointmentFor = (InstantAutoCompleteTextView) findViewById(R.id.atv_appointment_for);
        atvEpisode = (InstantAutoCompleteTextView) findViewById(R.id.atv_select_episode);
        atvEncounter = (InstantAutoCompleteTextView) findViewById(R.id.atv_select_encounter);
        atvGender = (InstantAutoCompleteTextView) findViewById(R.id.atv_gender);

        etFirstName = (EditText) findViewById(R.id.et_first_name);
        etMiddleName = (EditText) findViewById(R.id.et_middle_name);
        etLastname = (EditText) findViewById(R.id.et_last_name);
        etMobile = (EditText) findViewById(R.id.et_mobile);
        etEmail = (EditText) findViewById(R.id.et_email);
        etDob = (EditText) findViewById(R.id.et_dob);
        etDate = (EditText) findViewById(R.id.et_date);

        btnAdd = (Button) findViewById(R.id.btn_save);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnAdd.setText("Add");

        initListeners();
        getValues();
    }


    private void getValues() {
        try {

            List<Clinic> objClinicList = session.getClinics();
            for (Clinic obj :
                    objClinicList) {
                clinicList.add(obj.clinicName);
                clinicMap.put(obj.clinicName, obj.clinicId);
            }

            ArrayAdapter<String> adapterClinic = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, clinicList);
            atvClinic.setAdapter(adapterClinic);

            setValues();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validation() {
        boolean flag = false;
        appointmentDate = etDate.getText().toString().trim();
        appointmentTime = atvTime.getText().toString().trim();
        slotId = timeMap.get(appointmentTime);

        if (appointmentTime.length() == 0) {
            atvTime.setError("Select slot!");
        } else {
            atvTime.setError(null);
        }

        clinic = atvClinic.getText().toString().trim();
        if (clinic.length() == 0) {
            atvClinic.setError("Select clinic!");
        } else {
            atvClinic.setError(null);
        }

        if (appointmentDate.length() == 0) {
            etDate.setError("Select date!");
        } else {
            etDate.setError(null);
        }

        if (appointmentTime.length() == 0) {
        } else if (clinic.length() == 0) {
        } else if (appointmentDate.length() == 0) {
        } else {
            flag = true;
        }


        return flag;
    }


    private void saveAppointment() {
        try {

            if (validation()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", Integer.parseInt(id));
                JSONObject clinicObj = new JSONObject();
                clinicObj.put("clinicId", Integer.parseInt(clinicId));
                jsonObject.put("clinic", clinicObj);
                JSONObject slotObj = new JSONObject();
                slotObj.put("slotId", Integer.parseInt(slotId));
                jsonObject.put("slot", slotObj);

                String appointmentDateNew = Utils.convertTime(appointmentDate, "yyyy-MM-dd", "dd-MM-yyyy");
                String appointmentNewTime = Utils.convertTime(appointmentTime, "hh:mm a", "HH:mm:ss");

                jsonObject.put("appointmentDate", appointmentDateNew);
                jsonObject.put("appointmentStringTime", appointmentNewTime);
                jsonObject.put("rescheduled", true);

                Log.e("json", "" + jsonObject);

                utils.showProgress("", "loading..");
                API_Save_Appointment apiSaveSlots = new API_Save_Appointment(jsonObject, ActivityRescheduleAppointment.this, mActivity);
                apiSaveSlots.save();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListeners() {
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        etDate.setOnClickListener(this);
        atvTime.setOnClickListener(this);
        atvTime.setThreshold(0);
        atvClinic.setThreshold(0);


        atvSalutation.setEnabled(false);
        atvEncounter.setEnabled(false);
        atvReferredBy.setEnabled(false);
        atvType.setEnabled(false);
        atvAppointmentFor.setEnabled(false);
        atvEpisode.setEnabled(false);
        etEmail.setEnabled(false);
        etMobile.setEnabled(false);
        etFirstName.setEnabled(false);
        etMiddleName.setEnabled(false);
        etLastname.setEnabled(false);
        atvSalutation.setEnabled(false);
        etDob.setEnabled(false);
        atvGender.setEnabled(false);


        etDate.addTextChangedListener(twDate);
        atvClinic.addTextChangedListener(twClinic);
        atvTime.addTextChangedListener(twSlot);

        atvClinic.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    atvClinic.showDropDown();
                }

            }
        });


        atvTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    atvTime.showDropDown();
                }
            }
        });

        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    selectDate();
                }
            }
        });

        dateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear++;
                String month = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;
                String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                etDate.setText(year + "-" + month + "-" + day);
                atvTime.setText("");
                getSlots();
            }
        };

        atvClinic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                atvTime.setText("");
                clinic = atvClinic.getText().toString().trim();
                clinicId = clinicMap.get(clinic);
                getSlots();
            }
        });

        atvTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                clinic = atvClinic.getText().toString().trim();
                if (clinic.length() > 0) {
                    clinicId = clinicMap.get(clinic);
                    Log.e("clinicId", "" + clinicId);
                }
            }
        });
    }

    private void getSlots() {
        try {
            resetTime();
            // {"clinicList":[6],"selectedDate":"5/8/2016","doctorNerve24Id":"NV4SUQ0000"}
            appointmentDate = etDate.getText().toString();
            appointmentDate = Utils.convertTime(appointmentDate, "yyyy-MM-dd", "dd/MM/yyyy");
            clinic = atvClinic.getText().toString().trim();
            if (clinic.length() > 0 && appointmentDate.length() > 0) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(Integer.parseInt(clinicId));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clinicList", jsonArray);
                jsonObject.put("selectedDate", appointmentDate);
                jsonObject.put("doctorNerve24Id", session.getNerve24Id());
                utils.showProgress("", "Loading slots..");
                API_Get_Slots_By_Clinic apiGetSlotsByClinic = new API_Get_Slots_By_Clinic(jsonObject.toString(), ActivityRescheduleAppointment.this, mActivity);
                apiGetSlotsByClinic.getSlots();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setValues() {
        try {
            Appointment patientSearch = (Appointment) getIntent().getSerializableExtra("Appointment");
            id = patientSearch.id;
            email = patientSearch.email;
            email = email.equals("null") ? "" : email;
            mobile = patientSearch.primaryphone;
            mobile = mobile.equals("null") ? "" : mobile;
            fName = patientSearch.firstName;
            fName = fName.equals("null") ? "" : fName;
            mName = patientSearch.middleName;
            mName = mName.equals("null") ? "" : mName;
            lName = patientSearch.lastName;
            lName = lName.equals("null") ? "" : lName;
            salutation = patientSearch.salutation;
            salutation = salutation.equals("null") ? "" : salutation;
            dob = patientSearch.dob;
            dob = dob.equals("null") ? "" : dob;
            gender = patientSearch.gender;
            gender = gender.equals("null") ? "" : gender;
            clinicId = patientSearch.clinicId;
            atvReferredBy.setText(patientSearch.salutationRefer + " " + patientSearch.refer);

            atvAppointmentFor.setText(patientSearch.appointmentFor);
            atvType.setText(patientSearch.appointmentType);
            atvClinic.setText(patientSearch.clinicDisplayName);
            atvEncounter.setText(patientSearch.encounter);
            atvEpisode.setText(patientSearch.episode);
            etDate.setText(patientSearch.appointmentDate);
            String time = Utils.convertTime(patientSearch.appointmentTime, "HH:mm:ss", "hh:mm a");
            atvTime.setText(time);

            etEmail.setText(email);
            etMobile.setText(mobile);
            etFirstName.setText(fName);
            etMiddleName.setText(mName);
            etLastname.setText(lName);
            atvSalutation.setText(salutation);
            etDob.setText(dob);
            atvGender.setText(gender);

            getSlots();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    TextWatcher twDate = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = etDate.getText().toString().trim();
            if (str.length() == 0) {
                etDate.setError("Select date!");
            } else {
                etDate.setError(null);
            }
        }
    };

    TextWatcher twClinic = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = atvClinic.getText().toString().trim();
            if (str.length() == 0) {
                atvClinic.setError("Select clinic!");
            } else {
                atvClinic.setError(null);
                if (!clinicList.contains(str)) {
                    resetTime();
                }
            }
        }
    };

    TextWatcher twSlot = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = atvTime.getText().toString().trim();
            if (str.length() == 0) {
                atvTime.setError("Select slot!");
                Utils.showTimeDropDown(atvTime);
            } else {
                atvTime.setError(null);
            }
        }
    };


    private void selectDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                dateListener,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, 1);
        dpd.setMaxDate(maxDate);
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, 0);
        dpd.setMinDate(minDate);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                saveAppointment();
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.et_date:
                selectDate();
                break;
            case R.id.atv_time:
                Utils.showTimeDropDown(atvTime);
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


    @Override
    public void onGetSlots(ArrayList<String> slots, Map<String, String> map) {
        utils.hideProgress();
        if (slots.size() > 0) {
            timeList = slots;
            timeMap = map;
            Log.e("Time list", "" + timeList.size());
            Log.e("timeMap", "" + timeMap.size());
            ArrayAdapter<String> adapterTime = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, timeList);
            atvTime.setAdapter(adapterTime);
        } else {
            Utils.alertBox(mActivity, "Slots are not available for this date!");
            resetTime();
        }
    }

    @Override
    public void onGetSlotsError(String res) {
        Utils.alertBox(mActivity, "Slots are not available for this date!");
        utils.hideProgress();
        resetTime();
    }

    private void resetTime() {
        timeList.clear();
        timeMap.clear();
        atvTime.setText("");
        atvTime.setAdapter(null);
    }

    @Override
    public void onAppointmentSaved(String res) {
        utils.hideProgress();
        successAlert("Appointment modified successfully!");
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
                setResult(RESULT_OK);
                finish();
            }
        });

    }


    @Override
    public void onAppointmentSavedError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }


}