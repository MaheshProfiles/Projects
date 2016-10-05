package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.ApiTask.API_Get_Episodes;
import com.nerve24.doctor.ApiTask.API_Get_Patients_Search;
import com.nerve24.doctor.ApiTask.API_Get_Slots_By_Clinic;
import com.nerve24.doctor.ApiTask.API_Save_Appointment;
import com.nerve24.doctor.Listeners.Listener_Get_Episodes;
import com.nerve24.doctor.Listeners.Listener_Get_Slots_By_Clinic;
import com.nerve24.doctor.Listeners.Listener_Patient_Search;
import com.nerve24.doctor.Listeners.Listener_Save_Appointment;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.InstantAutoCompleteTextView;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.AppointmentFor;
import com.nerve24.doctor.pojo.AppointmentType;
import com.nerve24.doctor.pojo.Clinic;
import com.nerve24.doctor.pojo.Encounter;
import com.nerve24.doctor.pojo.Episode;
import com.nerve24.doctor.pojo.Gender;
import com.nerve24.doctor.pojo.Nerver24Member;
import com.nerve24.doctor.pojo.Salutation;
import com.nerve24.doctor.pojo.SlotWithAppointment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class ActivityAddNewAppointment extends AppCompatActivity implements Listener_Save_Appointment,
        Listener_Get_Slots_By_Clinic, Listener_Patient_Search, View.OnClickListener, Listener_Get_Episodes, TextView.OnEditorActionListener {
    private String TAG = "", MODULE = "ActivityAddNewAppointment";
    private Activity mActivity;
    private Utils utils;
    private Session session;

    private TextInputLayout tilSearchPatient, tilSalutation, tilGender, tilReferredBy, tilClinic, tilType, tilTime, tilAppointmentFor, tilEpisode, tilEncounter;
    private TextInputLayout tilFirstName, tilMiddleName, tilLastname, tilMobile, tilEmail, tilDob, tilDate;
    private InstantAutoCompleteTextView atvSearchPatient, atvSalutation, atvGender, atvReferredBy, atvClinic, atvType, atvTime, atvAppointmentFor, atvEpisode, atvEncounter;
    private EditText etFirstName, etMiddleName, etLastname, etMobile, etEmail, etDob, etDate;
    private Button btnCancel, btnAdd;
    private DatePickerDialog.OnDateSetListener dobListener, dateListener;
    private String doctorNerve24Id = "", patientNerve24Id = "", referredByDoctorNerve24Id = "",
            salutation = "", referredBy = "", clinic = "", clinicId = "", slotId = "",
            appointmentType = "", appointmentFor = "", episodeType = "",
            encounter = "", fName = "", mName = "", lName = "", mobile = "",
            email = "", dob = "", appointmentDate = "", appointmentTime = "", gender;
    private API_Get_Patients_Search apiGetPatientsSearch;
    private Map<String, Nerver24Member> patientSearchMap = new HashMap<>();
    private Map<String, String> idNameMap = new HashMap<>();
    private ArrayList<String> nameList = new ArrayList<>();
    private Nerver24Member patientSearch;
    private ArrayList<String> salutationList = new ArrayList<>();
    private ArrayList<String> clinicList = new ArrayList<>();
    private Map<String, String> clinicMap = new HashMap();
    private ArrayList<String> timeList = new ArrayList<>();
    private Map<String, String> timeMap = new HashMap();

    ArrayList<String> episodeList = new ArrayList<>();
    private ArrayList<String> encounterList = new ArrayList<>();
    private ArrayList<String> genderList = new ArrayList<>();
    private ArrayList<String> appointmentTypeList = new ArrayList<>();
    private ArrayList<String> appointmentForList = new ArrayList<>();
    private ArrayList<String> referedDoctorList = new ArrayList<>();
    private Map<String, String> referedDoctorMap = new HashMap();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_appointment);
        initUI();
        Fabric.with(this, new Crashlytics());
    }

    private void initUI() {
        TAG = "initUI";
        mActivity = ActivityAddNewAppointment.this;
        session = new Session(mActivity);
        utils = new Utils(mActivity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add New Appointment");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        atvSearchPatient = (InstantAutoCompleteTextView) findViewById(R.id.atv_search_patient);
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

        tilSearchPatient = (TextInputLayout) findViewById(R.id.til_search_patient);
        tilSalutation = (TextInputLayout) findViewById(R.id.til_salutation);
        tilReferredBy = (TextInputLayout) findViewById(R.id.til_referred_by);
        tilClinic = (TextInputLayout) findViewById(R.id.til_clinic);
        tilType = (TextInputLayout) findViewById(R.id.til_type);
        tilTime = (TextInputLayout) findViewById(R.id.til_time);
        tilAppointmentFor = (TextInputLayout) findViewById(R.id.til_appointment_for);
        tilEpisode = (TextInputLayout) findViewById(R.id.til_select_episode);
        tilEncounter = (TextInputLayout) findViewById(R.id.til_select_encounter);
        tilGender = (TextInputLayout) findViewById(R.id.til_gender);

        tilFirstName = (TextInputLayout) findViewById(R.id.til_first_name);
        tilMiddleName = (TextInputLayout) findViewById(R.id.til_middle_name);
        tilLastname = (TextInputLayout) findViewById(R.id.til_last_name);
        tilMobile = (TextInputLayout) findViewById(R.id.til_mobile);
        tilEmail = (TextInputLayout) findViewById(R.id.til_email);
        tilDob = (TextInputLayout) findViewById(R.id.til_dob);
        tilDate = (TextInputLayout) findViewById(R.id.til_date);

        btnAdd = (Button) findViewById(R.id.btn_save);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        initListeners();
        getValues();
    }


    private void getValues() {
        try {
            doctorNerve24Id = session.getNerve24Id();
            List<Salutation> objSalutationList = session.getSalutation();
            for (Salutation obj : objSalutationList) {
                salutationList.add(obj.salutation);
            }
            ArrayAdapter<String> adapterSalutation = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, salutationList);
            atvSalutation.setAdapter(adapterSalutation);

            List<Nerver24Member> objNerver24Members = session.getDoctorList();
            for (Nerver24Member obj :
                    objNerver24Members) {
                String name = obj.salutation + " " + obj.fullName;
                referedDoctorList.add(name);
                referedDoctorMap.put(name, obj.nerve24Id);
            }
            ArrayAdapter<String> adapterDoctor = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, referedDoctorList);
            atvReferredBy.setAdapter(adapterDoctor);

            List<Encounter> objEncounterList = session.getEncounter();
            for (Encounter obj :
                    objEncounterList) {
                encounterList.add(obj.value);
            }
            ArrayAdapter<String> adapterEncounter = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, encounterList);
            atvEncounter.setAdapter(adapterEncounter);

            List<Gender> objGenderList = session.getgender();
            for (Gender obj :
                    objGenderList) {
                genderList.add(obj.description);
            }
            ArrayAdapter<String> adapterGender = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, genderList);
            atvGender.setAdapter(adapterGender);


            List<AppointmentType> objAppointmentTypeList = session.getAppointmentType();
            for (AppointmentType obj :
                    objAppointmentTypeList) {
                appointmentTypeList.add(obj.value);
            }

            ArrayAdapter<String> adapterAppointmentType = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, appointmentTypeList);
            atvType.setAdapter(adapterAppointmentType);


            List<AppointmentFor> objAppointmentForList = session.getAppointmentFor();
            for (AppointmentFor obj :
                    objAppointmentForList) {
                appointmentForList.add(obj.value);
            }

            ArrayAdapter<String> adapterAppointmentFor = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, appointmentForList);
            atvAppointmentFor.setAdapter(adapterAppointmentFor);

            List<Clinic> objClinicList = session.getClinics();
            for (Clinic obj :
                    objClinicList) {
                clinicList.add(obj.clinicName);
                clinicMap.put(obj.clinicName, obj.clinicId);
            }

            ArrayAdapter<String> adapterClinic = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, clinicList);
            atvClinic.setAdapter(adapterClinic);

            if (getIntent().hasExtra("from")) {
                String from = getIntent().getStringExtra("from");
                if (from.equals("FragmentPagerCalendarDayView")) {
                    SlotWithAppointment slotWithAppointment = (SlotWithAppointment) getIntent().getSerializableExtra("SlotWithAppointment");
                    String slotDate = getIntent().getStringExtra("date");
                    etDate.setText(slotDate);
                    appointmentDate = slotDate;
                    String time = slotWithAppointment.startTime;
                    time = Utils.convertTime(time, "HH:mm:ss", "hh:mm a");
                    atvTime.setText(time);
                    slotId = slotWithAppointment.slotId;
                    clinic = slotWithAppointment.clinicName;
                    atvClinic.setText(clinic);
                    clinicId = slotWithAppointment.clinicId;
                    getSlots();
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validation() {
        boolean flag = false;
        String referedDoctor = atvReferredBy.getText().toString().trim();
        referredByDoctorNerve24Id = referedDoctorMap.get(referedDoctor);
        episodeType = atvEpisode.getText().toString().trim();
        encounter = atvEncounter.getText().toString().trim();
        appointmentType = atvType.getText().toString().trim();
        appointmentFor = atvAppointmentFor.getText().toString().trim();
        appointmentDate = etDate.getText().toString().trim();
        appointmentTime = atvTime.getText().toString().trim();
        slotId = timeMap.get(appointmentTime);

        salutation = atvSalutation.getText().toString();
        fName = etFirstName.getText().toString().trim();
        mName = etMiddleName.getText().toString().trim();
        lName = etLastname.getText().toString().trim();
        mobile = etMobile.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        gender = atvGender.getText().toString().trim();


        if (patientNerve24Id.length() == 0) {
            if (salutationList.contains(salutation))
                tilSalutation.setError("Salutation is not valid!");
            else
                tilSalutation.setError(null);

            if (fName.length() == 0)
                tilFirstName.setError("First name must be filled!");
            else
                tilFirstName.setError(null);

            if (mobile.length() == 0)
                tilMobile.setError("Mobile Number must be filled!");
            else if (mobile.length() < 10)
                tilMobile.setError("Enter 10 digit mobile!");
            else
                tilMobile.setError(null);


            if (email.length() > 0 && !Utils.checkEmail(email))
                tilEmail.setError("Email is not valid!");
            else
                tilEmail.setError(null);


            if (gender.length() > 0 && !genderList.contains(gender))
                tilGender.setError("Gender is not valid!");
            else
                tilGender.setError(null);


        } else
            tilSearchPatient.setError(null);


        if (episodeType.length() == 0)
            tilEpisode.setError("Episode is empty!");
        else
            tilEpisode.setError(null);

        if (appointmentFor.length() == 0)
            tilAppointmentFor.setError("Select appointmentFor!");
        else if(!appointmentForList.contains(appointmentFor))
            tilAppointmentFor.setError("Invalid appointmentFor!");
        else
            tilAppointmentFor.setError(null);

        if (appointmentType.length() == 0)
            tilType.setError("Select type!");
        else if(!appointmentTypeList.contains(appointmentType))
            tilType.setError("Invalid type!");
        else
            tilType.setError(null);

        if (appointmentTime.length() == 0)
            tilTime.setError("Select slot!");
        else if (!timeList.contains(appointmentTime))
            tilTime.setError("Invalid time!");
        else
            tilTime.setError(null);

        if (encounter.length() == 0)
            tilEncounter.setError("Select encounter!");
        else if (!encounterList.contains(encounter))
            tilEncounter.setError("Invalid encounter!");
        else
            tilEncounter.setError(null);

        clinic = atvClinic.getText().toString().trim();
        if (clinic.length() == 0)
            tilClinic.setError("Select clinic!");
        else if(!clinicList.contains(clinic))
            tilClinic.setError("Invalid clinic!");
        else
            tilClinic.setError(null);

        if (appointmentDate.length() == 0)
            tilDate.setError("Select date!");
        else
            tilDate.setError(null);

        if (patientNerve24Id.length() == 0) {
            if (salutation.length()>0 && !salutationList.contains(salutation)) {
            } else if (gender.length() > 0 && !genderList.contains(gender)) {
            } else if (fName.length() == 0) {
            } else if (mobile.length() == 0) {
            } else if (mobile.length() < 10) {
            } else if (email.length() > 0 && !Utils.checkEmail(email)) {
            } else if (episodeType.length() == 0) {
            } else if (appointmentFor.length() == 0 && !appointmentForList.contains(appointmentFor)) {
            } else if (appointmentType.length() == 0 && !appointmentTypeList.contains(appointmentType)) {
            } else if (appointmentTime.length() == 0 && !timeList.contains(appointmentTime)) {
            } else if (encounter.length() == 0 && !encounterList.contains(encounter)) {
            } else if (clinic.length() == 0 && !clinicList.contains(clinic)) {
            } else if (appointmentDate.length() == 0) {
            } else {
                flag = true;
            }
        } else {
            if (episodeType.length() == 0) {
            } else if (appointmentFor.length() == 0 && !appointmentForList.contains(appointmentFor)) {
            } else if (appointmentType.length() == 0 && !appointmentTypeList.contains(appointmentType)) {
            } else if (appointmentTime.length() == 0 && !timeList.contains(appointmentTime)) {
            } else if (encounter.length() == 0 && !encounterList.contains(encounter)) {
            } else if (clinic.length() == 0 && !clinicList.contains(clinic)) {
            } else if (appointmentDate.length() == 0) {
            } else {
                flag = true;
            }
        }

        return flag;
    }


    private void saveAppointment() {
        try {

            if (validation()) {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("id", "");
                if (patientNerve24Id.length() == 0) {
                    jsonObject.put("patientSalutation", salutation);
                    jsonObject.put("patientFirstName", fName);
                    jsonObject.put("patientMiddleName", mName);
                    jsonObject.put("patientMobileNumber", mobile);
                    jsonObject.put("patientDob", dob);
                    jsonObject.put("patientEmail", email);
                    jsonObject.put("patientGender", gender);
                } else {
                    jsonObject.put("patientNerve24Id", patientNerve24Id);
                }
                jsonObject.put("doctorNerve24Id", doctorNerve24Id);
                jsonObject.put("referredByDoctorNerve24Id", referredByDoctorNerve24Id);
                JSONObject epiObj = new JSONObject();
                epiObj.put("episodeType", episodeType);
                jsonObject.put("episode", epiObj);
                jsonObject.put("encounter", encounter);
                JSONObject clinicObj = new JSONObject();
                clinicObj.put("clinicId", Integer.parseInt(clinicId));
                jsonObject.put("clinic", clinicObj);
                JSONObject slotObj = new JSONObject();
                slotObj.put("slotId", Integer.parseInt(slotId));
                jsonObject.put("slot", slotObj);

                String appointmentNewDate = appointmentDate.replace("/", "-");
                String appointmentNewTime = Utils.convertTime(appointmentTime, "hh:mm a", "HH:mm:ss");

                jsonObject.put("appointmentStatus", "Booked");
                jsonObject.put("appointmentType", appointmentType);
                jsonObject.put("appointmentFor", appointmentFor);
                jsonObject.put("appointmentDate", appointmentNewDate);
                jsonObject.put("appointmentTime", appointmentNewTime);
                jsonObject.put("appointmentStringTime", appointmentNewTime);
                jsonObject.put("rescheduled", false);

                Log.e("json", "" + jsonObject);

                utils.showProgress("", "loading..");
                API_Save_Appointment apiSaveSlots = new API_Save_Appointment(jsonObject, ActivityAddNewAppointment.this, mActivity);
                apiSaveSlots.save();


            /*{
                "id":"",
                    "patientNerve24Id":"NV4SUQ0007",
                    "doctorNerve24Id":"NV4SUQ0000",
                    "referredByDoctorNerve24Id":"NV4SUQ000S",
                    "episode":{
                "episodeType":"dsf"
            },
                "encounter":"New",
                    "clinic":{
                "clinicId":6
            },
                "slot":{
                "slotId":39157
            },
                "appointmentStatus":"Booked",
                    "appointmentType":"Procedure",
                    "appointmentFor":"consultation",
                    "appointmentDate":"5-8-2016",
                    "appointmentTime":"05:00:00",
                    "appointmentStringTime":"05:00:00",
                    "rescheduled":false
            }
                  */
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListeners() {
        btnAdd.setText("Add");
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        atvSalutation.setOnClickListener(this);
        etDob.setOnClickListener(this);
        etDate.setOnClickListener(this);
        atvTime.setOnClickListener(this);
        atvGender.setOnClickListener(this);
        atvAppointmentFor.setOnClickListener(this);
        atvEpisode.setOnClickListener(this);
        atvEncounter.setOnClickListener(this);
        atvType.setOnClickListener(this);
        atvSearchPatient.setOnEditorActionListener(this);

        atvGender.setThreshold(0);
        atvSalutation.setThreshold(0);
        atvClinic.setThreshold(0);
        atvEncounter.setThreshold(0);
        atvReferredBy.setThreshold(0);
        atvType.setThreshold(0);
        atvTime.setThreshold(0);
        atvAppointmentFor.setThreshold(0);
        atvEpisode.setThreshold(0);
        atvSearchPatient.setThreshold(0);
        atvSearchPatient.addTextChangedListener(twSearchPatient);
        etDate.addTextChangedListener(twDate);
        atvClinic.addTextChangedListener(twClinic);
        atvEncounter.addTextChangedListener(twEncounter);
        atvType.addTextChangedListener(twAppointmentType);
        atvTime.addTextChangedListener(twSlot);
        atvAppointmentFor.addTextChangedListener(twAppointmentFor);
        atvEpisode.addTextChangedListener(twEpisode);
        etFirstName.addTextChangedListener(twFirstName);
        etMobile.addTextChangedListener(twMobile);
        atvReferredBy.addTextChangedListener(twSearchRefer);


        atvGender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    atvGender.showDropDown();
                }
            }
        });

        atvSalutation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    Utils.showTimeDropDown(atvSalutation);
                    Utils.hideKeyboard(mActivity);
                }
            }
        });

        atvClinic.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    atvClinic.showDropDown();
                }
            }
        });

        atvEncounter.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    atvEncounter.showDropDown();
                }
            }
        });


        atvType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    atvType.showDropDown();
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

        atvAppointmentFor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    atvAppointmentFor.showDropDown();
                }
            }
        });


        atvEpisode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    atvEpisode.showDropDown();
                }
            }
        });


        etDob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    selectDOB();
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


        dobListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear++;
                String month = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;
                String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                etDob.setText(day + "/" + month + "/" + year);
                dob = day + "-" + month + "-" + year;
            }
        };

        dateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear++;
                String month = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;
                String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                etDate.setText(day + "/" + month + "/" + year);
                appointmentDate = day + "/" + month + "/" + year;
                atvTime.setText("");
                getSlots();
            }
        };


        atvSearchPatient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                patientSearch = patientSearchMap.get(idNameMap.get(selection));
                setValues();
            }
        });

        atvClinic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                atvTime.setText("");
                clinic = atvClinic.getText().toString().trim();
                clinicId = clinicMap.get(clinic);
                getSlots();
            }
        });
    }

    private void getSlots() {
        try {
            resetTime();
            // {"clinicList":[6],"selectedDate":"5/8/2016","doctorNerve24Id":"NV4SUQ0000"}
            Log.e("appointmentDate", "" + appointmentDate);
            clinic = atvClinic.getText().toString().trim();
            if (clinic.length() > 0) {
                clinicId = clinicMap.get(clinic);
                Log.e("clinicId", "" + clinicId);
            }
            if (clinic.length() > 0 && appointmentDate.length() > 0) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(Integer.parseInt(clinicId));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clinicList", jsonArray);
                jsonObject.put("selectedDate", appointmentDate);
                jsonObject.put("doctorNerve24Id", doctorNerve24Id);
                utils.showProgress("", "Loading slots..");
                API_Get_Slots_By_Clinic apiGetSlotsByClinic = new API_Get_Slots_By_Clinic(jsonObject.toString(), ActivityAddNewAppointment.this, mActivity);
                apiGetSlotsByClinic.getSlots();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setValues() {
        try {
            patientNerve24Id = patientSearch.nerve24Id;
            email = patientSearch.email;
            mobile = patientSearch.primaryphone;
            fName = patientSearch.firstName;
            mName = patientSearch.middleName;
            lName = patientSearch.lastName;
            salutation = patientSearch.salutation;
            dob = patientSearch.dob;
            gender = patientSearch.gender;

            etEmail.setText(email);
            etMobile.setText(mobile);
            etFirstName.setText(fName);
            etMiddleName.setText(mName);
            etLastname.setText(lName);
            atvSalutation.setText(salutation);
            etDob.setText(dob);
            atvGender.setText(gender);

            etEmail.setEnabled(false);
            etMobile.setEnabled(false);
            etFirstName.setEnabled(false);
            etMiddleName.setEnabled(false);
            etLastname.setEnabled(false);
            atvSalutation.setEnabled(false);
            etDob.setEnabled(false);
            atvGender.setEnabled(false);

            getEpisodes();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getEpisodes() {
        try {
            utils.showProgress("", "loading..");
            API_Get_Episodes apiGetEpisodes = new API_Get_Episodes(patientNerve24Id, ActivityAddNewAppointment.this, mActivity);
            apiGetEpisodes.getEpisodes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableAll() {
        Log.e("enableAll", "enableAll");
        if (!etEmail.isEnabled()) {

            atvGender.setEnabled(true);
            etEmail.setEnabled(true);
            etMobile.setEnabled(true);
            etFirstName.setEnabled(true);
            etMiddleName.setEnabled(true);
            etLastname.setEnabled(true);
            atvSalutation.setEnabled(true);
            etDob.setEnabled(true);

            atvGender.setText("");
            etEmail.setText("");
            etMobile.setText("");
            etFirstName.setText("");
            etMiddleName.setText("");
            etLastname.setText("");
            atvSalutation.setText("");
            etDob.setText("");

            patientNerve24Id = "";
            email = "";
            mobile = "";
            fName = "";
            mName = "";
            lName = "";
            salutation = "";
            dob = "";
            gender = "";
        }
    }


    TextWatcher twMobile = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = etMobile.getText().toString().trim();
            if (str.length() == 0) {
            } else {
                Log.e("afterTextChanged","afterTextChanged");
                tilMobile.setError(null);
            }
        }
    };

    TextWatcher twFirstName = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = etFirstName.getText().toString().trim();
            if (str.length() == 0) {
            } else {
                tilFirstName.setError(null);
            }
        }
    };

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
            } else {
                tilDate.setError(null);
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
            } else {
                tilClinic.setError(null);
                if(!clinicList.contains(str))
                {
                    resetTime();
                }
            }
        }
    };


    TextWatcher twAppointmentFor = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            final String str = atvAppointmentFor.getText().toString().trim();
            if (str.length() == 0) {
            } else {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (atvAppointmentFor.isPopupShowing()) {
                            tilAppointmentFor.setError(null);
                        } else {
                            if (!appointmentForList.contains(str))
                                tilAppointmentFor.setError("No Appointment For matching " + str + " were found");
                        }
                    }
                }, 100);

            }
            if (appointmentForList.contains(str)) tilAppointmentFor.setError(null);

        }
    };


    TextWatcher twAppointmentType = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            final String str = atvType.getText().toString().trim();
            if (str.length() == 0) {
            } else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (atvType.isPopupShowing()) {
                            tilType.setError(null);
                        } else {
                            if (!appointmentTypeList.contains(str))
                               tilType.setError("No Appointment Type matching " + str + " were found");
                        }
                    }
                }, 100);
            }

            if (appointmentTypeList.contains(str)) tilType.setError(null);
        }
    };

    TextWatcher twSlot = new TextWatcher() {
        int count = 0;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            this.count = count;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            Log.e("editable.length()", "" + editable.length());
            if (timeList.size() > 0) {
                String str = atvTime.getText().toString().trim();
                if (str.length() == 0 && count == 1) {
                    Utils.showTimeDropDown(atvTime);
                } else {
                    tilTime.setError(null);
                }
            } else {
                tilTime.setError("No slots available");
            }
        }
    };

    TextWatcher twEncounter = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            final String str = atvEncounter.getText().toString().trim();
            if (str.length() == 0) {
            } else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (atvEncounter.isPopupShowing()) {
                            tilEncounter.setError(null);
                        } else {
                            if (!encounterList.contains(str))
                                tilEncounter.setError("No encounters matching " + str + " were found");
                        }
                    }
                }, 100);
            }
            if (encounterList.contains(str)) tilEncounter.setError(null);
        }
    };

    TextWatcher twEpisode = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            final String str = atvEpisode.getText().toString().trim();
            if (str.length() == 0) {
            } else {
                tilEpisode.setError(null);
            }
        }
    };

    TextWatcher twSearchPatient = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (after == 0) {
                enableAll();
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = atvSearchPatient.getText().toString().trim();
            if (str.length() > 0) {
                serachPatient(str);
            } else {
                patientNerve24Id = "";
                atvEpisode.setAdapter(null);
                atvEpisode.setText("");
            }
        }
    };

    TextWatcher twSearchRefer = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String str = atvReferredBy.getText().toString().trim();
                    if (str.length() > 1 && doctorNerve24Id.length() == 0) {
                        if (atvReferredBy.isPopupShowing()) {
                            tilReferredBy.setError(null);
                        } else {
                            tilReferredBy.setError("No such doctor with " + str);
                        }
                    } else {
                        tilReferredBy.setError(null);
                    }
                }
            }, 100);

        }
    };


    private void serachPatient(String key) {
        try {
            if (apiGetPatientsSearch != null) {
                apiGetPatientsSearch.stopSearch();
            }
            apiGetPatientsSearch = new API_Get_Patients_Search(key, ActivityAddNewAppointment.this, mActivity);
            apiGetPatientsSearch.getPatients();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void selectDOB() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                dobListener,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, 0);
        dpd.setMaxDate(maxDate);
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -110);
        dpd.setMinDate(minDate);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

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
            case R.id.et_dob:
                selectDOB();
                break;
            case R.id.et_date:
                selectDate();
                break;

            case R.id.atv_time:
                Utils.showTimeDropDown(atvTime);
                break;

            case R.id.atv_gender:
                Utils.showTimeDropDown(atvGender);
                break;

            case R.id.atv_appointment_for:
                Utils.showTimeDropDown(atvAppointmentFor);
                break;

            case R.id.atv_type:
                Utils.showTimeDropDown(atvType);
                break;

            case R.id.atv_select_episode:
                Utils.showTimeDropDown(atvEpisode);
                break;

            case R.id.atv_select_encounter:
                Utils.showTimeDropDown(atvEncounter);
                break;

            case R.id.atv_salutation:
                Utils.showTimeDropDown(atvSalutation);
                Utils.hideKeyboard(mActivity);
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
    public void onGetPatients(ArrayList<String> nameList, Map<String, String> idNameMap, Map<String, Nerver24Member> patientSearchMap) {

        if (nameList.size() > 0) {
            tilSearchPatient.setError(null);
            this.nameList = nameList;
            this.idNameMap = idNameMap;
            this.patientSearchMap = patientSearchMap;

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, nameList);
            atvSearchPatient.setAdapter(adapter);

            atvSearchPatient.showDropDown();

        } else {
            String sPatientSearched = atvSearchPatient.getText().toString().trim();
            if ((atvSearchPatient.getAdapter().isEmpty()))
                tilSearchPatient.setError("No Such Patient With " + sPatientSearched);
        }
    }

    @Override
    public void onGetPatientsError(String res) {
        try {
            String sPatientSearched = atvSearchPatient.getText().toString().trim();
            if ((atvSearchPatient.getAdapter().isEmpty()))
               tilSearchPatient.setError("No such patient with " + sPatientSearched);
        } catch (Exception e) {

        }
    }

    @Override
    public void onGetSlots(ArrayList<String> slots, Map<String, String> map) {
        utils.hideProgress();
        if (slots.size() > 0) {
            tilTime.setError(null);
            timeList = slots;
            timeMap = map;
            ArrayAdapter<String> adapterTime = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, timeList);
            atvTime.setAdapter(adapterTime);
        } else {
            atvTime.setAdapter(null);
            tilTime.setError("No slots available");
            resetTime();
        }
    }

    @Override
    public void onGetSlotsError(String res) {
        atvTime.setAdapter(null);
        tilTime.setError("No slots available");
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
    public void onAppointmentSavedError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }


    @Override
    public void onGetEpisodes(ArrayList<Episode> episodeArrayList) {
        utils.hideProgress();
        episodeList.clear();
        if (episodeArrayList.size() > 0) {

            for (Episode obj : episodeArrayList) {
                episodeList.add(obj.episodeType);
            }
            ArrayAdapter<String> adapterSalutation = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, episodeList);
            atvEpisode.setAdapter(adapterSalutation);
        } else {
            atvEpisode.setAdapter(null);
        }
    }

    @Override
    public void onGetEpisodesError(String res) {
        utils.hideProgress();
        atvEpisode.setAdapter(null);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        switch (textView.getId()) {
            case R.id.atv_search_patient:
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getAndDisplayPatient();
                    return true;
                }
                break;
        }
        return false;
    }

    private void getAndDisplayPatient() {
        Log.e("getAndDisplayPatient", "getAndDisplayPatient");
        String selection = atvSearchPatient.getText().toString().trim();
        if (selection.length() > 0) {
            if (idNameMap.containsKey(selection)) {
                patientSearch = patientSearchMap.get(idNameMap.get(selection));
                setValues();
            }
        }
    }
}