package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.ApiTask.API_CancelAppointments;
import com.nerve24.doctor.ApiTask.API_Get_Clinics;
import com.nerve24.doctor.Listeners.Listener_CancelAppointments;
import com.nerve24.doctor.Listeners.Listener_Get_Clinics;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.Clinic;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by mahesha on 04-08-16.
 */
public class ActivityGroupCancellation extends AppCompatActivity implements Listener_Get_Clinics, Listener_CancelAppointments, View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public Button btnDone;
    public Button btnCancel;
    public JSONArray jsonArraySlot;
    private String TAG = "", MODULE = "Group Cancellation";
    private Activity mActivity;
    private Utils utils;
    private Session session;
    private EditText etdate;
    private EditText etFrom;
    private RadioButton rbCancelAll, rbCancelByTimings;
    private LinearLayout llClinicDetails;
    private EditText etTo;
    private TextView tvSelectClinic;
    private String validFrom = "";
    private String jsonDate = "";
    private String firstSlotFromTime = "firstSlotFromTime", firstSlotToTime = "firstSlotToTime", selectedDay;
    private JSONArray clinicArray;
    private ArrayList<String> cancelAppointmentClinics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_cancellation);
        initUI();
        Fabric.with(this, new Crashlytics());
    }

    private void initUI() {
        try {
            TAG = "initUI";
            mActivity = ActivityGroupCancellation.this;
            utils = new Utils(mActivity);
            session = new Session(mActivity);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.group_cancellation);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            tvSelectClinic = (TextView) findViewById(R.id.tv_select_clinic);
            tvSelectClinic.setFocusableInTouchMode(true);
            tvSelectClinic.requestFocus();
            btnDone = (Button) findViewById(R.id.btn_save);
            btnCancel = (Button) findViewById(R.id.btn_cancel);
            etdate = (EditText) findViewById(R.id.et_date);
            etFrom = (EditText) findViewById(R.id.et_from);
            etTo = (EditText) findViewById(R.id.et_to);
            llClinicDetails = (LinearLayout) findViewById(R.id.ll_clinics);
            rbCancelAll = (RadioButton) findViewById(R.id.rb_cancel_all);
            rbCancelByTimings = (RadioButton) findViewById(R.id.rb_cancel_by_timings);
            initListeners();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void initListeners() {
        try {
            TAG = "initListeners";
            btnDone.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            etdate.setOnClickListener(this);
            rbCancelAll.setChecked(true);
            onRadioButtonClicked(rbCancelAll);
            etdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        selectDate();
                    }
                }
            });
            etFrom.setOnClickListener(this);
            etTo.setOnClickListener(this);
            etFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        pickTime(firstSlotFromTime);
                    }
                }
            });

            etTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        pickTime(firstSlotToTime);
                    }
                }
            });
            setValues();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
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
                cancelAppointments();
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.et_date:
                selectDate();
                break;
            case R.id.et_from:
                pickTime(firstSlotFromTime);
                break;
            case R.id.et_to:
                pickTime(firstSlotToTime);
                break;
        }

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        try {
            monthOfYear++;
            jsonDate = "" + dayOfMonth + "/" + monthOfYear + "/" + year;
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MONTH, monthOfYear -1);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            cal.set(Calendar.YEAR, year);
            String converted = utils.convertTime(jsonDate,"dd/MM/yyyy","ddth MMM yyyy");
            Log.d("dateFormat", converted);
            etdate.setText(jsonDate);
            String day = "" + dayOfMonth;
            String month = "" + monthOfYear;
            String yearStr = "" + year;
            if (day.length() == 1) day = "0" + day;
            if (month.length() == 1) month = "0" + month;
            validFrom = day + "-" + month + "-" + yearStr;
            changeValidFrom();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void onCancelAppointmentSuccess(String res) {
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
    public void onCancelAppointmentError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }

    private void selectDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(this,
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

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rb_cancel_all:
                if (checked) {
                    etTo.setText("");
                    etFrom.setText("");
                    etTo.setEnabled(false);
                    etTo.setFocusable(false);
                    etFrom.setFocusable(false);
                    etFrom.setEnabled(false);
                }
                break;
            case R.id.rb_cancel_by_timings:
                if (checked) {
                    etTo.setFocusable(true);
                    etFrom.setFocusable(true);
                    etTo.setEnabled(true);
                    etFrom.setEnabled(true);
                }
                break;
        }
    }

    private void cancelAppointments() {
        if (validateChangePassword()) {
            try {
                utils.showProgress("", "loading..");
                JSONObject obj = new JSONObject();
                obj.put("doctorNerve24Id", session.getNerve24Id());
                obj.put("date", etdate.getText().toString());
                obj.put("clinics", new JSONArray(cancelAppointmentClinics));
                if (rbCancelByTimings.isChecked()) {
                    obj.put("fromTime", etFrom.getText().toString());
                    obj.put("toTime", etTo.getText().toString());
                }
                obj.put("cancelAll", rbCancelAll.isChecked());
                API_CancelAppointments apiLogin = new API_CancelAppointments(obj, ActivityGroupCancellation.this, mActivity);
                apiLogin.cancelAppointment();
            } catch (Exception e) {
                Log.e(MODULE, TAG);
                e.printStackTrace();
            }
        }
    }

    private boolean validateChangePassword() {
        boolean flag = false;
        if (etdate.length() == 0) {
            Utils.alertBox(mActivity, "Select The Date");
        } else if (cancelAppointmentClinics == null || cancelAppointmentClinics.isEmpty()) {
            Utils.alertBox(mActivity, "Select the Clinics");
        } else if (!rbCancelAll.isChecked() && etFrom.length() == 0) {
            Utils.alertBox(mActivity, "Select the Start Time");
        } else if (!rbCancelAll.isChecked() && etTo.length() == 0) {
            Utils.alertBox(mActivity, "Select the End Time");
        } else {
            flag = true;
        }
        return flag;
    }


    public void setValues() {
        getClinics();
        List<Clinic> clinicsList = session.getClinics();
        for (int i = 0; i < clinicsList.size(); i++) {
            final String clinicId = clinicsList.get(i).clinicId;
            cancelAppointmentClinics = new ArrayList<String>();
            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View to_add = inflater.inflate(R.layout.item_clinic_name, llClinicDetails, false);
            final CheckBox clinic1 = (CheckBox) to_add.findViewById(R.id.cb_clinic1);
            final CheckBox clinic2 = (CheckBox) to_add.findViewById(R.id.cb_clinic2);
            if (i == 0) {
                clinic1.setText("All");
                clinic1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clinic1.isChecked())
                            getClinicsChecked(true);
                        else
                            getClinicsChecked(false);
                    }
                });
            } else {
                clinic1.setText(clinicsList.get(i).clinicName);
                clinic1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            cancelAppointmentClinics.add(clinicId);
                        } else {
                            setClinicUnchecked();
                            cancelAppointmentClinics.remove(clinicId);
                        }
                    }
                });
            }
            if (i == 0) {
                clinic2.setTag(clinicId);
                clinic2.setVisibility(View.VISIBLE);
                clinic2.setText(clinicsList.get(0).clinicName);
                clinic2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            cancelAppointmentClinics.add(clinicId);
                        } else {
                            setClinicUnchecked();
                            cancelAppointmentClinics.remove(clinicId);
                        }
                    }
                });
            } else {
                if ((i + 1) < clinicsList.size()) {
                    final String clinicsId = clinicsList.get(i + 1).clinicId;
                    clinic2.setVisibility(View.VISIBLE);
                    clinic2.setText(clinicsList.get(i + 1).clinicName);
                    clinic2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                cancelAppointmentClinics.add(clinicsId);
                            } else {
                                setClinicUnchecked();
                                cancelAppointmentClinics.remove(clinicsId);
                            }
                        }

                    });
                    i = i + 1;
                } else {
                    clinic2.setVisibility(View.GONE);
                }
            }
            llClinicDetails.addView(to_add);
        }
    }

    private void getClinicsChecked(boolean isChecked) {
        int childCount = llClinicDetails.getChildCount();
        for (int i = 0; i < childCount; i++) {
            LinearLayout childView = (LinearLayout) llClinicDetails.getChildAt(i);
            CheckBox check1 = (CheckBox) childView.findViewById(R.id.cb_clinic1);
            CheckBox check2 = (CheckBox) childView.findViewById(R.id.cb_clinic2);
            check1.setChecked(isChecked);
            check2.setChecked(isChecked);
        }
    }

    private void setClinicUnchecked() {
        LinearLayout childView = (LinearLayout) llClinicDetails.getChildAt(0);
        CheckBox removeAllChecked = (CheckBox) childView.findViewById(R.id.cb_clinic1);
        removeAllChecked.setChecked(false);
    }

    private void getClinics() {
        try {
            TAG = "getValues";
            utils.showProgress("", "loading..");
            String nerve24Id = session.getNerve24Id();
            API_Get_Clinics apiGetClinics = new API_Get_Clinics(nerve24Id, ActivityGroupCancellation.this, mActivity);
            apiGetClinics.getClinics();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void pickTime(final String tv) {
        try {
            TAG = "pickTime";
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            String AM_PM = "";
                            Calendar datetime = Calendar.getInstance();
                            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            datetime.set(Calendar.MINUTE, minute);

                            if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                                AM_PM = "AM";
                            else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                                AM_PM = "PM";
                            String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
                            if (strHrsToShow.length() == 1) strHrsToShow = "0" + strHrsToShow;
                            String minute1 = "" + datetime.get(Calendar.MINUTE);
                            if (minute1.length() == 1) minute1 = "0" + minute1;
                            String time = strHrsToShow + ":" + minute1 + " " + AM_PM;
                            setTime(tv, time);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void setTime(String tv, String time) {
        try {
            if (tv.equals(firstSlotFromTime)) {
                etFrom.setText(time);
                etTo.setText("");
                ((ActivitySlotEdit) mActivity).changeArray(selectedDay, 0, "startTime", time);
                ((ActivitySlotEdit) mActivity).changeArray(selectedDay, 0, "endTime", "");
            } else if (tv.equals(firstSlotToTime)) {
                String t = etFrom.getText().toString();
                if (Utils.isGreaterThan24(t, time)) {
                    Utils.alertBox(mActivity, "Time should be less than 24 hous!");
                } else {
                    etTo.setText(time);
                    ((ActivitySlotEdit) mActivity).changeArray(selectedDay, 0, "endTime", time);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeValidFrom() {
        try {
            for (int i = 0; i < jsonArraySlot.length(); i++) {
                jsonArraySlot.getJSONObject(i).put("validFrom", validFrom);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

