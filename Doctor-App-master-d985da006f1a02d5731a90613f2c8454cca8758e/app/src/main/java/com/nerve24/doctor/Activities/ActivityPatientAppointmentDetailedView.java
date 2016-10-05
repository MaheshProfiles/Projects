package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.ApiTask.API_ChangeStatus;
import com.nerve24.doctor.Listeners.Listener_ChangeAppointmentStatus;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.Appointment;
import com.nerve24.doctor.pojo.AppointmentStatus;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class ActivityPatientAppointmentDetailedView extends AppCompatActivity implements View.OnClickListener, Listener_ChangeAppointmentStatus {
    private String TAG = "", MODULE = "ActivityPatientAppointmentDetailedView";
    private Activity mActivity;
    private Utils utils;
    private Session session;

    private Button btnReschedule;
    private ImageView ivPremium;
    private TextView tvStatus, tvName, tvAge, tvAppointmentType, tvAppointmentFor, tvEpisodeName, tvEncounterName, tvTotalPayment, tvPaymentMode, tvReferredBy;
    private ArrayList<String> statusList = new ArrayList<>();
    private String id = "", date = "", value = "";
    private Appointment appointment=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointment_detailed_view);
        initUI();
        Fabric.with(this, new Crashlytics());
    }

    private void initUI() {
        TAG = "initUI";
        mActivity = ActivityPatientAppointmentDetailedView.this;
        session = new Session(mActivity);
        utils = new Utils(mActivity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnReschedule = (Button) findViewById(R.id.btn_reschedule);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvAppointmentType = (TextView) findViewById(R.id.tv_appointment_type);
        tvAppointmentFor = (TextView) findViewById(R.id.tv_appointment_for);
        tvEpisodeName = (TextView) findViewById(R.id.tv_episode_name);
        tvEncounterName = (TextView) findViewById(R.id.tv_encounter_name);
        tvTotalPayment = (TextView) findViewById(R.id.tv_total_payment);
        tvPaymentMode = (TextView) findViewById(R.id.tv_payment_mode);
        tvReferredBy = (TextView) findViewById(R.id.tv_referred_by);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvAge = (TextView) findViewById(R.id.tv_age);
        ivPremium = (ImageView) findViewById(R.id.iv_premium);

        initListeners();
        getValues();
    }


    private void getValues() {
        try {
            date = getIntent().getStringExtra("date");
            String currentDate = Utils.getCurrentTimeStamp("dd-MM-yyyy");

            if (!currentDate.equals(date)) {
                statusList.add("Booked");
                statusList.add("Cancelled");
            } else {
                List<AppointmentStatus> list = session.getAppointmentStatus();
                for (AppointmentStatus obj :
                        list) {
                    statusList.add(obj.value);
                }
            }


            appointment = (Appointment) getIntent().getSerializableExtra("Appointment");
            String fName, lname, dob, age = "", gender, picture, appointmentType, appointmentFor, episodeName, encounterName, totalPayment, paymentMode, referredBy, status;
            appointmentType = getResources().getString(R.string.appointment_type);
            appointmentFor = getResources().getString(R.string.appointment_for_with_colon);
            episodeName = getResources().getString(R.string.episode_name);
            encounterName = getResources().getString(R.string.encounter_name);
            totalPayment = getResources().getString(R.string.total_payment) + getResources().getString(R.string.Rs);
            paymentMode = getResources().getString(R.string.payment_mode);
            appointmentType = appointmentType + " " + appointment.appointmentType;
            appointmentFor = appointmentFor + " " + appointment.appointmentFor;
            episodeName = episodeName + " " + appointment.episode;
            encounterName = encounterName + " " + appointment.encounter;
            totalPayment = totalPayment + " " + "0";
            paymentMode = paymentMode + " " + "Online Transfer";
            referredBy = appointment.refer;
            if (!appointment.salutationRefer.equals("null"))
                referredBy = appointment.salutationRefer + " " + appointment.refer;
            status = appointment.appointmentStatus;
            dob = appointment.dob;
            if (!dob.equals("null"))
                age = dob.length() > 0 ? "Age " + Utils.calculateAge("yyyy-MM-dd", dob) : "";
            id = appointment.id;
            fName = appointment.firstName;
            lname = appointment.lastName;
            lname=lname.equals("null")?"":lname;
            gender = appointment.gender.equals("null") ? "" : " , " + appointment.gender;
            tvName.setText(fName + " " + lname);
            if (appointment.premium.equals("true"))
                ivPremium.setVisibility(View.VISIBLE);
            else
                ivPremium.setVisibility(View.GONE);
            tvAppointmentType.setText(appointmentType);
            tvAppointmentFor.setText(appointmentFor);
            tvEpisodeName.setText(episodeName);
            tvEncounterName.setText(encounterName);
            tvTotalPayment.setText(totalPayment);
            tvPaymentMode.setText(paymentMode);
            tvReferredBy.setText(referredBy);
            tvAge.setText(age + gender);
            tvStatus.setText(status);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initListeners() {
        btnReschedule.setOnClickListener(this);
        tvStatus.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reschedule:
                goToReschedule();
                break;
            case R.id.tv_status:
                pickOption();
                break;
        }

    }

    private void goToReschedule() {
        Intent intent = new Intent(mActivity, ActivityRescheduleAppointment.class);
        intent.putExtra("Appointment", appointment);
        finish();
        startActivity(intent);
    }

    private void pickOption() {
        final String[] array = statusList.toArray(new String[0]);
        value = "";
        new MaterialDialog.Builder(mActivity)
                .title("Select the status and confirm")
                .items(array)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which >= 0) {
                            value = text.toString();
                            if (value.equals("Cancelled")) {
                                cancelAppointment();
                            } else {
                                changeStatus();
                            }
                        }
                        return true;
                    }

                })
                .positiveText("Confirm")
                .negativeText("Close")
                .show();


    }

    private void changeStatus() {
        try {
            // {"id":126,"appointmentStatus":"Booked"}
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("appointmentStatus", value);

            utils.showProgress("", "loading..");
            API_ChangeStatus apiChangeStatus = new API_ChangeStatus(jsonObject.toString(), ActivityPatientAppointmentDetailedView.this, mActivity);
            apiChangeStatus.changeStatus();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelAppointment() {

        new MaterialDialog.Builder(mActivity)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .title("Cancel Appointment")
                .backgroundColorRes(R.color.white)
                .positiveColorRes(R.color.colorPrimaryDark)
                .negativeColorRes(R.color.colorPrimaryDark)
                .negativeText("Close")
                .positiveText("Cancel")
                .input("Reason for cancellation *", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input.toString().length() > 0) {
                            changeStatus();
                        } else {
                            validationAlert("Enter reason for cancellation!");
                        }
                    }
                }).show();

    }

    private void validationAlert(String res) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mActivity);
        builder.title("Alert!");
        builder.content(res);
        builder.positiveText("Ok");
        builder.show();
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                cancelAppointment();
            }
        });

    }


    @Override
    public void onChangedStatus(String res) {
        utils.hideProgress();
        tvStatus.setText(value);
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
                if(value.equals("Cancelled")) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

    }

    @Override
    public void onChangedStatusFailure(String res) {
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
        return true;
    }
}