package com.nerve24.doctor.Fragments;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.nerve24.doctor.Activities.ActivityTimeFee_SlotFee;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.DailyWiseSlotDay;

import java.util.Calendar;

public class Fragment_Time_Fee extends Fragment implements View.OnClickListener {
    private Activity mActivity;
    private Utils utils;
    private Session session;
    private TextView tvFromTime, tvToTime;
    private RelativeLayout rlFromTime, rlToTime;
    private EditText etFee;
    private Button btnSave, btnCancel;
    private DailyWiseSlotDay object;
    private String startTime = "", endTime = "", fee = "";

    public Fragment_Time_Fee() {
        // Required empty public constructor
    }

    public static Fragment_Time_Fee newInstance(DailyWiseSlotDay object) {
        Fragment_Time_Fee fragment = new Fragment_Time_Fee();
        Bundle args = new Bundle();
        args.putSerializable("DailyWiseSlotDay", object);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            object = (DailyWiseSlotDay) getArguments().getSerializable("DailyWiseSlotDay");
            Log.e("slots id", "" + object.slotsId);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_fee, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        mActivity = getActivity();
        utils = new Utils(mActivity);
        session = new Session(mActivity);

        rlFromTime = (RelativeLayout) view.findViewById(R.id.rl_from_time);
        rlToTime = (RelativeLayout) view.findViewById(R.id.rl_to_time);
        tvFromTime = (TextView) view.findViewById(R.id.tv_from_time);
        tvToTime = (TextView) view.findViewById(R.id.tv_to_time);
        etFee = (EditText) view.findViewById(R.id.et_fee);
        btnSave = (Button) view.findViewById(R.id.btn_save);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnSave.setText("Apply");

        initListeners();

    }

    private void initListeners() {
        rlFromTime.setOnClickListener(this);
        rlToTime.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_from_time:
                pickTime("from");
                break;
            case R.id.rl_to_time:
                pickTime("to");
                break;
            case R.id.btn_save:
                save();
                break;
            case R.id.btn_cancel:
                mActivity.finish();
                break;
        }
    }

    private void save() {
        if (validation()) {
            ((ActivityTimeFee_SlotFee) getActivity()).fromTime = startTime;
            ((ActivityTimeFee_SlotFee) getActivity()).toTime = endTime;
            ((ActivityTimeFee_SlotFee) getActivity()).fee = fee;
            ((ActivityTimeFee_SlotFee) getActivity()).goToSlotFee();
        }
    }

    private boolean validation() {
        boolean flag = false;
        startTime = tvFromTime.getText().toString().trim();
        endTime = tvToTime.getText().toString().trim();
        fee = etFee.getText().toString().trim();
        if (startTime.length() == 0) {
            Utils.alertBox(mActivity, "From time is empty!");
        } else if (endTime.length() == 0) {
            Utils.alertBox(mActivity, "To time is empty!");
        } else if (fee.length() == 0) {
            Utils.alertBox(mActivity, "Fee is empty!");
        } else if (!isValidTime()) {
            Utils.alertBox(mActivity, "Enter valid time!");
        } else {
            flag = true;
        }
        return flag;
    }

    private boolean isValidTime() {
        boolean flag = false;
        String startTime1 = ((ActivityTimeFee_SlotFee) mActivity).fromTime;
        String endTime1 = ((ActivityTimeFee_SlotFee) mActivity).toTime;
        boolean option1 = Utils.isFromLaysForPricing(startTime1, endTime1, startTime);
        boolean option2 = Utils.isFromLaysForPricing(startTime1, endTime1, endTime);
        boolean option3 = Utils.isFromLaysForPricing(startTime, endTime, startTime1);
        boolean option4 = Utils.isFromLaysForPricing(startTime, endTime, endTime1);
        if (option1 || option2 || option3 || option4) {
            flag = true;
        }

        return flag;
    }

    private void pickTime(final String tv) {
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


                        if (tv.equals("from")) {
                            tvFromTime.setText(time);
                            tvToTime.setText("");
                        } else if (tv.equals("to")) {
                            String t = tvFromTime.getText().toString();
                            if (Utils.isGreaterThan24(t, time)) {
                                Utils.alertBox(mActivity, "Time should be less than 24 hours!");
                            } else {
                                tvToTime.setText(time);
                            }
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

}
