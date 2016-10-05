package com.nerve24.doctor.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nerve24.doctor.Activities.ActivityMain;
import com.nerve24.doctor.ApiTask.API_Get_Slots_with_Appointments_weekly;
import com.nerve24.doctor.Listeners.Listener_Get_Slots_with_Appointments;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.CircularTextView;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.Appointment;
import com.nerve24.doctor.pojo.SlotWithAppointment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class FragmentPagerCalendarWeekView extends Fragment implements Listener_Get_Slots_with_Appointments {
    private SimpleDateFormat DATE_PICKER_DD_MM_YYYY_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private String format = "dd-MM-yyyy";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_TO_SEND1 = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_TO_SEND = new SimpleDateFormat("dd-MM-yyyy");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh a");

    private String TAG = "", MODULE = "FragmentPagerCalendarWeekView";
    private Utils utils;
    private Session session;
    private Activity mActivity;
    private String weekStartDate = "";
    private static final String KEY = "weekStartDate";
    private static final String CLINIC_ID_ARRAY = "clinic_id_array";

    private TextView tvSun, tvMon, tvTue, tvWed, tvThu, tvFri, tvSat;
    private LinearLayout llROOT;
    private String sun, mon, tue, wed, thu, fri, sat;
    private int colorWhite, colorGrey, colorBlack, count, sNoCheckedIn = 0;
    private HashMap<String, ArrayList<SlotWithAppointment>> slotWithAppointmentsMap = new HashMap<>();
    private HashMap<String, ArrayList<SlotWithAppointment>> itemListSunday = new HashMap<>();
    private HashMap<String, ArrayList<SlotWithAppointment>> itemListMonday = new HashMap<>();
    private HashMap<String, ArrayList<SlotWithAppointment>> itemListTuesday = new HashMap<>();
    private HashMap<String, ArrayList<SlotWithAppointment>> itemListWednesday = new HashMap<>();
    private HashMap<String, ArrayList<SlotWithAppointment>> itemListThursday = new HashMap<>();
    private HashMap<String, ArrayList<SlotWithAppointment>> itemListFriday = new HashMap<>();
    private HashMap<String, ArrayList<SlotWithAppointment>> itemListSaturday = new HashMap<>();
    private JSONArray clinicsIdArray = new JSONArray();

    public static FragmentPagerCalendarWeekView newInstance(String weekStartDate, JSONArray clinicArray) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY, weekStartDate);
        bundle.putString(CLINIC_ID_ARRAY, clinicArray.toString());
        FragmentPagerCalendarWeekView simplePageFragment = new FragmentPagerCalendarWeekView();
        simplePageFragment.setArguments(bundle);
        return simplePageFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            weekStartDate = getArguments().getString(KEY);
            String str = getArguments().getString(CLINIC_ID_ARRAY, "");
            clinicsIdArray = new JSONArray(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pager_calendar_week_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
    }

    private void initUI(View view) {
        try {
            TAG = "initUI";
            mActivity = getActivity();
            utils = new Utils(mActivity);
            session = new Session(mActivity);
            colorWhite = ContextCompat.getColor(mActivity, R.color.white);
            colorGrey = ContextCompat.getColor(mActivity, R.color.grey_txt);
            colorBlack = ContextCompat.getColor(mActivity, R.color.black);

            tvSun = (TextView) view.findViewById(R.id.tv_sun);
            tvMon = (TextView) view.findViewById(R.id.tv_mon);
            tvTue = (TextView) view.findViewById(R.id.tv_tue);
            tvWed = (TextView) view.findViewById(R.id.tv_wed);
            tvThu = (TextView) view.findViewById(R.id.tv_thur);
            tvFri = (TextView) view.findViewById(R.id.tv_fri);
            tvSat = (TextView) view.findViewById(R.id.tv_sat);
            llROOT = (LinearLayout) view.findViewById(R.id.ll_parent);

            ((ActivityMain) getActivity()).setTitle("Calendar");
            getDays();
            getAppointments();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAppointments() {
        try {
            String filter = session.getCalendarFilters();
            String fromToDate = weekStartDate;
            JSONObject jsonObject = new JSONObject();
            String[] array = mActivity.getResources().getStringArray(R.array.array_time_12_am_12pm);
            if (filter.length() == 0) {
                //  {"fromAppointmentDate":"1-8-2016","toAppointmentDate":"1-8-2016","doctorNerve24Id":"NV4SUQ0000","clinicIdList":[1]}
                jsonObject.put("clinicIdList", clinicsIdArray);
                jsonObject.put("fromAppointmentDate", fromToDate);
                jsonObject.put("toAppointmentDate", fromToDate);
                String nerve24Id = session.getNerve24Id();
                jsonObject.put("doctorNerve24Id", nerve24Id);
            } else {
                jsonObject = new JSONObject(filter);
                jsonObject.put("fromAppointmentDate", fromToDate);
                jsonObject.put("toAppointmentDate", fromToDate);
            }
            utils.showProgress("", "loading...");
            API_Get_Slots_with_Appointments_weekly appointments = new API_Get_Slots_with_Appointments_weekly(jsonObject.toString(), FragmentPagerCalendarWeekView.this, mActivity);
            appointments.getSlots();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getDays() {
        try {
            sun = weekStartDate;
            mon = Utils.addDay(weekStartDate, DATE_PICKER_DD_MM_YYYY_FORMAT, 1);
            tue = Utils.addDay(weekStartDate, DATE_PICKER_DD_MM_YYYY_FORMAT, 2);
            wed = Utils.addDay(weekStartDate, DATE_PICKER_DD_MM_YYYY_FORMAT, 3);
            thu = Utils.addDay(weekStartDate, DATE_PICKER_DD_MM_YYYY_FORMAT, 4);
            fri = Utils.addDay(weekStartDate, DATE_PICKER_DD_MM_YYYY_FORMAT, 5);
            sat = Utils.addDay(weekStartDate, DATE_PICKER_DD_MM_YYYY_FORMAT, 6);
            setDays();
            settextColor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void settextColor() {
        try {
            setColor(sun, tvSun);
            setColor(mon, tvMon);
            setColor(tue, tvTue);
            setColor(wed, tvWed);
            setColor(thu, tvThu);
            setColor(fri, tvFri);
            setColor(sat, tvSat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setColor(String date, TextView tv) {
        int blue = getResources().getColor(R.color.blue);
        int black = getResources().getColor(R.color.black);
        int grey = getResources().getColor(R.color.gra);

        if (isToday(date))
            tv.setTextColor(blue);
        else if (!Utils.isAfterToday(date, format))
            tv.setTextColor(black);
        else
            tv.setTextColor(grey);
    }

    private void setDays() {
        String day1 = Utils.convertTime(sun, "dd-MM-yyyy", "dd");
        String day2 = Utils.convertTime(mon, "dd-MM-yyyy", "dd");
        String day3 = Utils.convertTime(tue, "dd-MM-yyyy", "dd");
        String day4 = Utils.convertTime(wed, "dd-MM-yyyy", "dd");
        String day5 = Utils.convertTime(thu, "dd-MM-yyyy", "dd");
        String day6 = Utils.convertTime(fri, "dd-MM-yyyy", "dd");
        String day7 = Utils.convertTime(sat, "dd-MM-yyyy", "dd");

        tvSun.setText(getSpanString(day1, "Sun"));
        tvMon.setText(getSpanString(day2, "Mon"));
        tvTue.setText(getSpanString(day3, "Tue"));
        tvWed.setText(getSpanString(day4, "Wed"));
        tvThu.setText(getSpanString(day5, "Thu"));
        tvFri.setText(getSpanString(day6, "Fri"));
        tvSat.setText(getSpanString(day7, "Sat"));
    }

    private CharSequence getSpanString(String str1, String str2) {
        SpannableString span1 = new SpannableString(str1);
        SpannableString span2 = new SpannableString("\n" + str2);
        span1.setSpan(new RelativeSizeSpan(1.3f), 0, 2, 0);
        return TextUtils.concat(span1, " ", span2);
    }


    private boolean isToday(String date) {
        boolean flag = false;
        String today = Utils.getCurrentTimeStamp("dd-MM-yyyy");
        if (today.equals(date)) flag = true;
        return flag;
    }


    @Override
    public void onGetSlots(ArrayList<SlotWithAppointment> slotWithAppointments) {

    }

    @Override
    public void onGetSlots(HashMap<String, ArrayList<SlotWithAppointment>> slotWithAppointmentsMap) {
        utils.hideProgress();
        this.slotWithAppointmentsMap.clear();
        this.slotWithAppointmentsMap = slotWithAppointmentsMap;
        if(slotWithAppointmentsMap.size()>0)
         filterAppointments();
    }

    private void filterAppointments() {
        try {
            sNoCheckedIn = 0;
            String[] daysArray = getResources().getStringArray(R.array.array_days_full_name_lowercase);
            String day = daysArray[0];
            ArrayList<SlotWithAppointment> appointments = slotWithAppointmentsMap.get(day);
            addAppointments(appointments, itemListMonday);

            day = daysArray[1];
            appointments = slotWithAppointmentsMap.get(day);
            addAppointments(appointments, itemListTuesday);

            day = daysArray[2];
            appointments = slotWithAppointmentsMap.get(day);
            addAppointments(appointments, itemListWednesday);

            day = daysArray[3];
            appointments = slotWithAppointmentsMap.get(day);
            addAppointments(appointments, itemListThursday);

            day = daysArray[4];
            appointments = slotWithAppointmentsMap.get(day);
            addAppointments(appointments, itemListFriday);

            day = daysArray[5];
            appointments = slotWithAppointmentsMap.get(day);
            addAppointments(appointments, itemListSaturday);

            day = daysArray[6];
            appointments = slotWithAppointmentsMap.get(day);
            addAppointments(appointments, itemListSunday);

            setList();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setList() {
        try {
            double fromTime = 0.00;
            for (int i = 0; i < 23; i++) {
                ArrayList<SlotWithAppointment> sundayAppointmentList = new ArrayList<>();
                ArrayList<SlotWithAppointment> mondayAppointmentList = new ArrayList<>();
                ArrayList<SlotWithAppointment> tuesdayAppointmentList = new ArrayList<>();
                ArrayList<SlotWithAppointment> wednesdayAppointmentList = new ArrayList<>();
                ArrayList<SlotWithAppointment> thursdayAppointmentList = new ArrayList<>();
                ArrayList<SlotWithAppointment> fridayAppointmentList = new ArrayList<>();
                ArrayList<SlotWithAppointment> saturdayAppointmentList = new ArrayList<>();

                String key=""+fromTime;
                if (itemListSunday.containsKey(key))
                    sundayAppointmentList = itemListSunday.get(key);

                if (itemListMonday.containsKey(key))
                    mondayAppointmentList = itemListMonday.get(key);

                if (itemListTuesday.containsKey(key))
                    tuesdayAppointmentList = itemListTuesday.get(key);

                if (itemListWednesday.containsKey(key))
                    wednesdayAppointmentList = itemListWednesday.get(key);

                if (itemListThursday.containsKey(key))
                    thursdayAppointmentList = itemListThursday.get(key);

                if (itemListFriday.containsKey(key))
                    fridayAppointmentList = itemListFriday.get(key);

                if (itemListSaturday.containsKey(key))
                    saturdayAppointmentList = itemListSaturday.get(key);

                createRow(sundayAppointmentList, mondayAppointmentList,
                        tuesdayAppointmentList, wednesdayAppointmentList,
                        thursdayAppointmentList, fridayAppointmentList,
                        saturdayAppointmentList,""+fromTime);

                fromTime = fromTime + 1.00;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createRow(ArrayList<SlotWithAppointment> sundayAppointmentList,
                           ArrayList<SlotWithAppointment> mondayAppointmentList,
                           ArrayList<SlotWithAppointment> tuesdayAppointmentList,
                           ArrayList<SlotWithAppointment> wednesdayAppointmentList,
                           ArrayList<SlotWithAppointment> thursdayAppointmentList,
                           ArrayList<SlotWithAppointment> fridayAppointmentList,
                           ArrayList<SlotWithAppointment> saturdayAppointmentList,String time) {
        try {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.item_calendar_week_row, null);
            TextView tvTime = (TextView) row.findViewById(R.id.tv_time);
            LinearLayout llSunday = (LinearLayout) row.findViewById(R.id.ll_sunday);
            LinearLayout llMonday = (LinearLayout) row.findViewById(R.id.ll_monday);
            LinearLayout llTuesday = (LinearLayout) row.findViewById(R.id.ll_tuesday);
            LinearLayout llWednesday = (LinearLayout) row.findViewById(R.id.ll_wednesday);
            LinearLayout llThursday = (LinearLayout) row.findViewById(R.id.ll_thursday);
            LinearLayout llFriday = (LinearLayout) row.findViewById(R.id.ll_friday);
            LinearLayout llSaturday = (LinearLayout) row.findViewById(R.id.ll_saturday);
            String tTime=Utils.convertTime(time,"HH.mm","hh a");
            tvTime.setText(tTime);

            if (sundayAppointmentList.size() > 0) {
                addSlots(sundayAppointmentList, llSunday, sun);
            }

            if (mondayAppointmentList.size() > 0) {
                addSlots(mondayAppointmentList, llMonday, mon);
            }

            if (tuesdayAppointmentList.size() > 0) {
                addSlots(tuesdayAppointmentList, llTuesday, tue);
            }

            if (wednesdayAppointmentList.size() > 0) {
                addSlots(wednesdayAppointmentList, llWednesday, wed);
            }

            if (thursdayAppointmentList.size() > 0) {
                addSlots(thursdayAppointmentList, llThursday, thu);
            }

            if (fridayAppointmentList.size() > 0) {
                addSlots(fridayAppointmentList, llFriday, fri);
            }

            if (saturdayAppointmentList.size() > 0) {
                addSlots(saturdayAppointmentList, llSaturday, sat);
            }

            llROOT.addView(row);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View addSlots(ArrayList<SlotWithAppointment> slotsArrayList, LinearLayout llColumn, String date) {
        try {
            count = 0;
            boolean isExpired = isDateExpired(date);

            for (int i = 0; i < slotsArrayList.size(); i++) {
                count++;
                SlotWithAppointment slotWithAppointment = slotsArrayList.get(i);
                String premium = slotWithAppointment.premium;

                ArrayList<Appointment> appointmentsList = slotWithAppointment.appointmentsList;
                if (appointmentsList.size() > 0) {
                    llColumn.addView(addAppointmentList(appointmentsList, premium, isExpired, date));
                }
                if (!isFilterAvailable() && appointmentsList.size() == 0) {
                    llColumn.addView(addFreeSlot(slotWithAppointment, isExpired, date));
                }

                if (llColumn.getChildCount() > 0)
                    llColumn.setVisibility(View.VISIBLE);
                else
                    llColumn.setVisibility(View.GONE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isFilterAvailable() {
        boolean flag = false;
        try {
            String filter = session.getCalendarFilters();
            if (filter.length() > 0) {
                JSONObject jsonObject = new JSONObject(filter);
                if (jsonObject instanceof JSONObject) {
                    JSONObject filterObj = jsonObject.getJSONObject("appointmentFilterDTO");
                    if (!filterObj.isNull("patientNerve24Id"))
                        flag = true;
                    else if (!filterObj.isNull("referredByDoctorNerve24Id"))
                        flag = true;
                    else if (!filterObj.isNull("appointmentType"))
                        flag = true;
                    else if (!filterObj.isNull("appointmentFor"))
                        flag = true;
                    else if (!filterObj.isNull("status"))
                        flag = true;
                    else if (!filterObj.isNull("slotType"))
                        flag = true;
                    else if (!filterObj.isNull("bookingSource"))
                        flag = true;
                    else if (!filterObj.isNull("referral"))
                        flag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    private boolean isDateExpired(String date) {
        boolean flag;
        flag = Utils.isAfterToday(date, "dd-MM-yyyy");
        return flag;
    }


    private boolean isTimeExpired(String startTime) {
        boolean flag = false;
        startTime = startTime.substring(0, 4);
        startTime = startTime.replace(":", ".");
        float sTime = Float.parseFloat(startTime);
        float currentTime = Float.parseFloat(Utils.getCurrentTimeStamp("HH.mm"));
        flag = sTime < currentTime;
        return flag;
    }

    private View addFreeSlot(final SlotWithAppointment slotWithAppointment, boolean isExpired, String date) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        try {
            if (count % 2 == 0)
                view = inflater.inflate(R.layout.item_appointment_week_view_left, null);
            else
                view = inflater.inflate(R.layout.item_appointment_week_view_right, null);

            RelativeLayout rlAppointment = (RelativeLayout) view.findViewById(R.id.rl_appointment);
            CircularTextView tvNo = (CircularTextView) view.findViewById(R.id.tv_no);
            tvNo.setVisibility(View.GONE);
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            ImageView ivStar = (ImageView) view.findViewById(R.id.iv_star);

            if (slotWithAppointment.premium.equals("true")) {
                ivStar.setVisibility(View.VISIBLE);
                ivStar.setBackgroundResource(R.drawable.ic_star_black);
            } else
                ivStar.setVisibility(View.GONE);


            tvName.setText(slotWithAppointment.startTime);
            tvName.setTextColor(colorBlack);

            if (isExpired) {
                boolean isToday = isToday(date);
                if (isToday) {
                    boolean isTimeExpired = isTimeExpired(slotWithAppointment.startTime);
                    if (isTimeExpired) {
                        rlAppointment.setBackgroundResource(R.drawable.round_rect_grey);
                    } else {
                        rlAppointment.setBackgroundResource(R.drawable.round_rect_white);
                        rlAppointment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                goToNewAppointment(slotWithAppointment);
                            }
                        });
                    }
                } else {
                    rlAppointment.setBackgroundResource(R.drawable.round_rect_grey);
                }
            } else {
                rlAppointment.setBackgroundResource(R.drawable.round_rect_white);
                rlAppointment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToNewAppointment(slotWithAppointment);
                    }
                });

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void goToNewAppointment(SlotWithAppointment slotWithAppointment) {
       /* Intent intent = new Intent(mActivity, ActivityAddNewAppointment.class);
        intent.putExtra("SlotWithAppointment", slotWithAppointment);
        intent.putExtra("from", MODULE);
        String d = SIMPLE_DATE_FORMAT_TO_SEND1.format(date);
        intent.putExtra("date", d);
        parentFragment.isFilterApplied = true;
        startActivity(intent);*/
    }

    private View addAppointmentList(ArrayList<Appointment> appointmentsList, String premium, boolean isExpired, String date) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewParent = inflater.inflate(R.layout.item_appointment_day_view, null);
        try {
            LinearLayout llRoot = (LinearLayout) viewParent.findViewById(R.id.ll_root);

            for (int z = 0; z < appointmentsList.size(); z++) {
                final Appointment appointment = appointmentsList.get(z);
                View view = null;
                if (count % 2 == 0)
                    view = inflater.inflate(R.layout.item_appointment_week_view_left, null);
                else
                    view = inflater.inflate(R.layout.item_appointment_week_view_right, null);

                RelativeLayout rlAppointment = (RelativeLayout) view.findViewById(R.id.rl_appointment);
                CircularTextView tvNo = (CircularTextView) view.findViewById(R.id.tv_no);
                tvNo.setVisibility(View.GONE);
                TextView tvName = (TextView) view.findViewById(R.id.tv_name);
                ImageView ivStar = (ImageView) view.findViewById(R.id.iv_star);
                String appointmentStatus = "", name = "";
                appointmentStatus = appointment.appointmentStatus;
                name = appointment.firstName;
                tvName.setText(name);
                if (premium.equals("true"))
                    ivStar.setVisibility(View.VISIBLE);
                else
                    ivStar.setVisibility(View.GONE);

                rlAppointment.setBackgroundResource(R.drawable.round_rect_red);
                if (appointmentStatus.equals("Missed") || appointmentStatus.equals("Completed")) {
                    rlAppointment.setBackgroundResource(R.drawable.round_rect_light_grey);
                    tvName.setText(name);
                    tvName.setTextColor(colorGrey);
                    ivStar.setBackgroundResource(R.drawable.ic_star);
                } else if (appointmentStatus.equals("In Consultation")) {
                    rlAppointment.setBackgroundResource(R.drawable.round_rect_green);
                    tvName.setText(name);
                    tvName.setTextColor(colorWhite);
                    ivStar.setBackgroundResource(R.drawable.ic_star_white);
                } else if (appointmentStatus.equals("Checked In")) {
                    sNoCheckedIn++;
                    tvNo.setVisibility(View.VISIBLE);
                    tvNo.setText("" + sNoCheckedIn);
                    rlAppointment.setBackgroundResource(R.drawable.round_rect_red);
                    tvName.setText(name);
                    tvName.setTextColor(colorWhite);
                    ivStar.setBackgroundResource(R.drawable.ic_star_white);
                } else if (appointmentStatus.equals("Booked")) {
                    rlAppointment.setBackgroundResource(R.drawable.round_rect_light_red);
                    tvName.setText(name);
                    tvName.setTextColor(colorBlack);
                    ivStar.setBackgroundResource(R.drawable.ic_star_black);
                }
                tvName.setTypeface(null, Typeface.NORMAL);

                if (appointmentStatus.equals("Missed") || appointmentStatus.equals("Completed")) {
                } else {

                    if (isExpired) {
                        boolean isToday = isToday(date);
                        if (isToday) {
                            boolean isTimeExpired = isTimeExpired(appointment.appointmentTime);
                            if (isTimeExpired) {

                            } else {
                                rlAppointment.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //onClickAppointment(appointment);
                                    }
                                });
                            }
                        }
                    } else {
                        rlAppointment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // onClickAppointment(appointment);
                            }
                        });
                    }

                }
                llRoot.addView(view);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return viewParent;
    }

    private void addAppointments(ArrayList<SlotWithAppointment> appointments, HashMap<String, ArrayList<SlotWithAppointment>> itemList) {
        try {

            double fromTime = 0.00;
            for (int i = 0; i < 23; i++) {
                double firstTime = fromTime;
                double secTime = fromTime + 0.59;

                ArrayList<SlotWithAppointment> slotWithAppointments = new ArrayList<>();
                for (int k = 0; k < appointments.size(); k++) {
                    SlotWithAppointment slotWithAppointment = appointments.get(k);
                    String time = slotWithAppointment.startTime;
                    time = time.substring(0, time.lastIndexOf(":"));
                    time = time.replace(":", ".");
                    double dTime = Double.parseDouble(time);


                    if (firstTime <= dTime && secTime >= dTime) {
                        slotWithAppointments.add(slotWithAppointment);
                    }
                }

                if (slotWithAppointments.size() > 0) {
                    itemList.put("" + firstTime, slotWithAppointments);
                }
                fromTime = fromTime + 1.00;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onGetSlotsError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }
}
