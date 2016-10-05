package com.nerve24.doctor.Fragments;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.nerve24.doctor.Activities.ActivitySlotEdit;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Fragment_Slot_Edit extends Fragment implements View.OnClickListener {
    private String TAG = "", MODULE = "Fragment_Slot_Edit";
    private Activity mActivity;
    private Utils utils;
    private Session session;

    private String selectedDay, firstSlot = "First slot", secondSlot = "Second slot",
            firstSlotFromTime = "firstSlotFromTime", firstSlotToTime = "firstSlotToTime",
            secSlotFromTime = "secSlotFromTime", secSlotToTime = "secSlotToTime";
    private CheckBox cbSameForAllDays, cbAvailable;
    private Spinner spDuration, spDuration1;
    private TextView tvSplitTimeSlot, tvHideSplitTimeSlot, tvFromTime1, tvToTime1, tvFromTime2, tvToTime2;
    private EditText etDuration1, etDuration2;
    private RelativeLayout rlFromTime, rlToTime, rlFromTime1, rlToTime1;
    private LinearLayout llSplit2;
    public ArrayList<String> daysList = new ArrayList<>();
    private boolean spFlag = false, isTouched = false;

    public Fragment_Slot_Edit() {
        // Required empty public constructor
    }

    public static Fragment_Slot_Edit newInstance(String selectedDay) {
        Fragment_Slot_Edit fragment = new Fragment_Slot_Edit();
        Bundle args = new Bundle();
        args.putSerializable("selectedDay", selectedDay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedDay = getArguments().getString("selectedDay");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager_slot_edit, container, false);

        return view;
    }

    private void initUI(View view) {
        try {
            TAG = "initUI";
            mActivity = getActivity();
            utils = new Utils(mActivity);
            session = new Session(mActivity);

            cbSameForAllDays = (CheckBox) view.findViewById(R.id.cb_same_for_all_days);
            cbAvailable = (CheckBox) view.findViewById(R.id.cb_available);
            tvSplitTimeSlot = (TextView) view.findViewById(R.id.tv_split_time_slot);
            tvHideSplitTimeSlot = (TextView) view.findViewById(R.id.tv_hide_split_time_slot1);
            tvFromTime1 = (TextView) view.findViewById(R.id.tv_from_time);
            tvToTime1 = (TextView) view.findViewById(R.id.tv_to_time);
            tvFromTime2 = (TextView) view.findViewById(R.id.tv_from_time1);
            tvToTime2 = (TextView) view.findViewById(R.id.tv_to_time1);
            etDuration1 = (EditText) view.findViewById(R.id.tv_duration);
            etDuration2 = (EditText) view.findViewById(R.id.tv_duration1);
            rlFromTime = (RelativeLayout) view.findViewById(R.id.rl_from_time);
            rlToTime = (RelativeLayout) view.findViewById(R.id.rl_to_time);
            rlFromTime1 = (RelativeLayout) view.findViewById(R.id.rl_from_time1);
            rlToTime1 = (RelativeLayout) view.findViewById(R.id.rl_to_time1);
            llSplit2 = (LinearLayout) view.findViewById(R.id.ll_split_time1);
            spDuration = (Spinner) view.findViewById(R.id.sp_duration);
            spDuration1 = (Spinner) view.findViewById(R.id.sp_duration1);


            initListeners();
            setValues();

        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
    }

    public void setValues() {
        try {

            String[] durArray = mActivity.getResources().getStringArray(R.array.array_duration);
            ArrayList<String> durList = new ArrayList<>(Arrays.asList(durArray));
            String[] dayArray = mActivity.getResources().getStringArray(R.array.array_days_full_name_lowercase);
            daysList = new ArrayList<>(Arrays.asList(dayArray));

            JSONObject jsonObjectSlot = ((ActivitySlotEdit) mActivity).jsonObjectSlot;

            if (selectedDay.equals(daysList.get(0))) {
                cbSameForAllDays.setVisibility(View.VISIBLE);
                if (jsonObjectSlot.has("sameForAllDays")) {
                    if (jsonObjectSlot.getBoolean("sameForAllDays"))
                        cbSameForAllDays.setChecked(true);
                    else
                        cbSameForAllDays.setChecked(false);
                } else
                    cbSameForAllDays.setChecked(false);
            }

            JSONArray selectedDayArray = jsonObjectSlot.getJSONObject("timingShedule").getJSONArray(selectedDay);


            if (selectedDayArray.length() == 0) {
                cbAvailable.setChecked(false);
                availableProcess();
            } else {

                JSONObject slot1_Obj = selectedDayArray.getJSONObject(0);
                rlFromTime.setEnabled(true);
                rlToTime.setEnabled(true);
                tvFromTime1.setText(slot1_Obj.getString("startTime"));
                tvToTime1.setText(slot1_Obj.getString("endTime"));
                cbAvailable.setSaveEnabled(false);
                cbAvailable.setChecked(slot1_Obj.getBoolean("checked"));
                etDuration1.setText(slot1_Obj.getString("duration"));
                etDuration1.setEnabled(true);

                if (selectedDayArray.length() == 2) {
                    rlFromTime1.setEnabled(true);
                    rlToTime1.setEnabled(true);
                    llSplit2.setVisibility(View.VISIBLE);
                    JSONObject slot2_Obj = selectedDayArray.getJSONObject(1);
                    tvFromTime2.setText(slot2_Obj.getString("startTime"));
                    tvToTime2.setText(slot2_Obj.getString("endTime"));
                    etDuration2.setText(slot2_Obj.getString("duration"));
                    etDuration2.setEnabled(true);
                } else {
                    llSplit2.setVisibility(View.GONE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initListeners() {
        try {
            TAG = "initListeners";
            rlFromTime.setOnClickListener(this);
            rlToTime.setOnClickListener(this);
            rlFromTime1.setOnClickListener(this);
            rlToTime1.setOnClickListener(this);
            tvSplitTimeSlot.setOnClickListener(this);
            cbAvailable.setOnClickListener(this);
            cbSameForAllDays.setOnClickListener(this);
            tvHideSplitTimeSlot.setOnClickListener(this);
            etDuration1.setOnClickListener(this);
            etDuration2.setOnClickListener(this);

            etDuration1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    isTouched = true;
                    return false;
                }
            });

            etDuration2.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    isTouched = true;
                    return false;
                }
            });

            etDuration1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b && isTouched) {
                        isTouched = false;
                        selectDuration(etDuration1);
                    }
                }
            });

            etDuration2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b && isTouched) {
                        isTouched = false;
                        selectDuration(etDuration2);
                    }
                }
            });


            spDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (spFlag) {
                        spFlag = false;
                        if(i>0) {
                            String value = adapterView.getItemAtPosition(i).toString();
                            if (value.length() > 0) {
                                etDuration1.setText(value);
                                ((ActivitySlotEdit) mActivity).changeArray(selectedDay, 0, "duration", Integer.parseInt(value));
                            }
                        }

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spDuration1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (spFlag) {
                        spFlag = false;
                        if(i>0) {
                            String value = adapterView.getItemAtPosition(i).toString();
                            if (value.length() > 0) {
                                etDuration2.setText(value);
                                ((ActivitySlotEdit) mActivity).changeArray(selectedDay, 1, "duration", Integer.parseInt(value));
                            }
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private boolean isLessThanDuration(String t, String time, String d) {
        boolean flag = false;
        int betweenTime = Utils.getBetweenTimeinMin(t, time, "hh:mm a");
        int duration = Integer.parseInt(d);

        if (betweenTime < duration) {
            flag = true;
        }
        if (betweenTime == duration) {
            flag = false;
        }
        return flag;
    }

    private void selectDuration(final EditText et) {
        spFlag = true;
        if (et.getId() == R.id.tv_duration) {
            spDuration.performClick();
        } else {
            spDuration1.performClick();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_from_time:
                selectFromTime();
                break;
            case R.id.rl_from_time1:
                selectFromTime1();
                break;
            case R.id.rl_to_time:
                if (tvFromTime1.getText().toString().trim().length() == 0) {
                    Utils.alertBox(mActivity, "Select from time!");
                } else if (tvFromTime2.getText().toString().trim().length() > 0
                        && tvToTime2.getText().toString().trim().length() == 0
                        && llSplit2.getVisibility() == View.VISIBLE) {
                    Utils.alertBox(mActivity, "Select to time for second slot!");
                } else {
                    pickTime(firstSlotToTime);
                }
                break;
            case R.id.rl_to_time1:
                if (tvFromTime2.getText().toString().trim().length() == 0) {
                    Utils.alertBox(mActivity, "Select from time!");
                } else if (tvFromTime1.getText().toString().trim().length() > 0
                        && tvToTime1.getText().toString().trim().length() == 0) {
                    Utils.alertBox(mActivity, "Select to time for first slot!");
                } else {
                    pickTime(secSlotToTime);
                }
                break;

            case R.id.tv_split_time_slot:
                showSplitLayout();
                break;
            case R.id.tv_hide_split_time_slot1:
                hideSplitLayout();
                break;
            case R.id.cb_available:
                availableProcess();
                break;
            case R.id.cb_same_for_all_days:
                sameForAllDays();
                break;

            case R.id.tv_duration:
                selectDuration(etDuration1);
                break;
            case R.id.tv_duration1:
                selectDuration(etDuration2);
                break;
        }
    }

    private void selectFromTime() {
        if (etDuration1.getText().toString().length() == 0) {
            Utils.alertBox(mActivity, "Select duration!");
        } else if (llSplit2.getVisibility() == View.VISIBLE && tvFromTime2.getText().toString().length() > 0
                && tvToTime2.getText().toString().length() == 0) {
            Utils.alertBox(mActivity, "Select to time for second slot!");
        } else {
            pickTime(firstSlotFromTime);
        }
    }

    private void selectFromTime1() {
        if (tvFromTime1.getText().toString().length() == 0) {
            Utils.alertBox(mActivity, "Select from time for first slot!");
        } else if (tvToTime1.getText().toString().length() == 0) {
            Utils.alertBox(mActivity, "Select to time for first slot!");
        } else if (etDuration2.getText().toString().length() == 0) {
            Utils.alertBox(mActivity, "Select duration!");
        } else {
            pickTime(secSlotFromTime);
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
                            if (view.isShown()) {
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
            String duration = etDuration1.getText().toString();
            String duration1 = etDuration2.getText().toString();

            if (tv.equals(firstSlotFromTime)) {
                if (!isFromLays(firstSlot, time)) {
                    tvFromTime1.setText(time);
                    tvToTime1.setText("");
                    ((ActivitySlotEdit) mActivity).changeArray(selectedDay, 0, "startTime", time);
                    ((ActivitySlotEdit) mActivity).changeArray(selectedDay, 0, "endTime", "");
                }
            } else if (tv.equals(firstSlotToTime)) {

                String t = tvFromTime1.getText().toString();
                if (Utils.isGreaterThan24(t, time)) {
                    Utils.alertBox(mActivity, "Time should be less than 24 hours!");
                } else {
                    if (isLessThanDuration(t, time, duration)) {
                        Utils.alertBox(mActivity, "Select slot time greater than duration! ");
                    } else {
                        if (!isToLays(firstSlot, time)) {
                            tvToTime1.setText(time);
                            ((ActivitySlotEdit) mActivity).changeArray(selectedDay, 0, "endTime", time);
                        }
                    }
                }
            } else if (tv.equals(secSlotFromTime)) {
                if (!isFromLays(secondSlot, time)) {
                    tvFromTime2.setText(time);
                    tvToTime2.setText("");
                    ((ActivitySlotEdit) mActivity).changeArray(selectedDay, 1, "startTime", time);
                    ((ActivitySlotEdit) mActivity).changeArray(selectedDay, 1, "endTime", "");
                }
            } else if (tv.equals(secSlotToTime)) {
                String t = tvFromTime2.getText().toString();
                if (Utils.isGreaterThan24(t, time)) {
                    Utils.alertBox(mActivity, "Time should be less than 24 hours!");
                } else {
                    if (isLessThanDuration(t, time, duration1)) {
                        Utils.alertBox(mActivity, "Select slot time greater than duration! ");
                    } else {
                        if (!isToLays(secondSlot, time)) {
                            tvToTime2.setText(time);
                            ((ActivitySlotEdit) mActivity).changeArray(selectedDay, 1, "endTime", time);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideSplitLayout() {
        llSplit2.setVisibility(View.GONE);
        ((ActivitySlotEdit) mActivity).removeJSONobject(selectedDay, 1);
    }

    private void showSplitLayout() {
        try {
            if (tvFromTime1.getText().toString().length() > 0 && tvToTime1.getText().toString().length() > 0) {
                TAG = "showSplitLayout";
                tvFromTime2.setText("");
                tvToTime2.setText("");
                etDuration2.setText("");
                llSplit2.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void availableProcess() {
        boolean b = cbAvailable.isChecked();
        if (b) {
            ((ActivitySlotEdit) mActivity).changeArray(selectedDay, 0, "checked", true);
            rlFromTime.setEnabled(true);
            rlFromTime1.setEnabled(true);
            rlToTime.setEnabled(true);
            rlToTime1.setEnabled(true);
            etDuration1.setEnabled(true);
            etDuration2.setEnabled(true);
        } else {
            cbSameForAllDays.setChecked(false);
            ((ActivitySlotEdit) mActivity).sameForAllDays(false);
            resetAll();
            ((ActivitySlotEdit) mActivity).removeAll(selectedDay);
            rlFromTime.setEnabled(false);
            rlFromTime1.setEnabled(false);
            rlToTime.setEnabled(false);
            rlToTime1.setEnabled(false);
            etDuration1.setEnabled(false);
            etDuration2.setEnabled(false);
            llSplit2.setVisibility(View.GONE);
        }
    }

    private void resetAll() {
        tvFromTime1.setText("");
        tvFromTime2.setText("");
        tvToTime1.setText("");
        tvToTime2.setText("");
        etDuration1.setText("");
        etDuration2.setText("");
    }

    private void sameForAllDays() {
        boolean b = cbSameForAllDays.isChecked();
        if (b) {
            String fromTime = tvFromTime1.getText().toString().trim();
            String toTime = tvToTime1.getText().toString().trim();
            if (fromTime.length() == 0) {
                cbSameForAllDays.setChecked(false);
                Utils.alertBox(mActivity, "Enter from time!");
            } else if (toTime.length() == 0) {
                cbSameForAllDays.setChecked(false);
                Utils.alertBox(mActivity, "Enter to time!");
            } else {
                ((ActivitySlotEdit) mActivity).sameForAllDays(b);
            }
        } else {
            ((ActivitySlotEdit) mActivity).sameForAllDays(b);
        }
    }


    private boolean isFromLays(String slot, String time) {
        boolean flag = false;
        if (slot.equals(firstSlot) && llSplit2.getVisibility() == View.VISIBLE) {
            String startTime = tvFromTime2.getText().toString().trim();
            String endTime = tvToTime2.getText().toString().trim();

            if (startTime.length() > 0 && endTime.length() > 0) {
                flag = Utils.isFromLays(startTime, endTime, time);
                if (flag) {
                    Utils.alertBox(mActivity, "Selected time overlap with second slot!");
                }
            }


        } else if (slot.equals(secondSlot)) {
            String startTime = tvFromTime1.getText().toString().trim();
            String endTime = tvToTime1.getText().toString().trim();
            if (startTime.length() > 0 && endTime.length() > 0) {
                flag = Utils.isFromLays(startTime, endTime, time);
                if (flag) {
                    Utils.alertBox(mActivity, "Selected time overlap with first slot!");
                }
            }
        }
        return flag;
    }

    private boolean isToLays(String slot, String time) {
        boolean flag = false;
        if (slot.equals(firstSlot) && llSplit2.getVisibility() == View.VISIBLE) {
            String startTime = tvFromTime2.getText().toString().trim();
            String endTime = tvToTime2.getText().toString().trim();
            String startTime1 = tvFromTime1.getText().toString().trim();
            String endTime1 = time;
            if (startTime.length() > 0 && endTime.length() > 0) {
                flag = Utils.isToLays(startTime, endTime, time);
                if (flag) {
                    Utils.alertBox(mActivity, "Selected time overlap with first slot!");
                }
                if (!flag) {
                    flag = Utils.isToLays(startTime1, endTime1, startTime);
                    if (flag) {
                        Utils.alertBox(mActivity, "Selected time overlap with first slot!");
                    }
                }
                if (!flag) {
                    flag = Utils.isToLays(startTime1, endTime1, endTime);
                    if (flag) {
                        Utils.alertBox(mActivity, "Selected time overlap with first slot!");
                    }
                }
            }
        } else if (slot.equals(secondSlot)) {
            String startTime = tvFromTime1.getText().toString().trim();
            String endTime = tvToTime1.getText().toString().trim();
            String startTime1 = tvFromTime2.getText().toString().trim();
            String endTime1 = time;
            if (startTime.length() > 0 && endTime.length() > 0) {
                flag = Utils.isToLays(startTime, endTime, time);
                if (flag) {
                    Utils.alertBox(mActivity, "Selected time overlap with first slot!");
                }
                if (!flag) {
                    flag = Utils.isToLays(startTime1, endTime1, startTime);
                    if (flag) {
                        Utils.alertBox(mActivity, "Selected time overlap with first slot!");
                    }
                }
                if (!flag) {
                    flag = Utils.isToLays(startTime1, endTime1, endTime);
                    if (flag) {
                        Utils.alertBox(mActivity, "Selected time overlap with first slot!");
                    }
                }
            }
        }
        return flag;
    }


}
