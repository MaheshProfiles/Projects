package com.nerve24.doctor.Fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nerve24.doctor.Activities.ActivityAddNewAppointment;
import com.nerve24.doctor.Activities.ActivityFilterCalendar;
import com.nerve24.doctor.Activities.ActivityFilterPatient;
import com.nerve24.doctor.Activities.ActivityPatientAppointmentDetailedView;
import com.nerve24.doctor.ApiTask.API_Get_Slots_with_Appointments;
import com.nerve24.doctor.AppConfig.Constants;
import com.nerve24.doctor.Listeners.Listener_Calendar_DayView;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;


public class FragmentPagerCalendarDayView extends Fragment implements Listener_Get_Slots_with_Appointments, Listener_Calendar_DayView, SearchView.OnQueryTextListener {
    private String TAG = "", MODULE = "FragmentPagerCalendarDayView";

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("EEE dd MMMM");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_TO_SEND = new SimpleDateFormat("dd-MM-yyyy");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_TO_SEND1 = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh a");

    private static final String DATE_PICKER_DATE_KEY = "date_picker_date_key";
    private static final String DATE_PICKER_POSITION_KEY = "date_picker_position_key";
    private static final String CLINIC_ID_ARRAY = "clinic_id_array";


    private TextView tvDate;
    public long date;
    private JSONArray clinicsIdArray;

    private HashMap<Date, ArrayList<SlotWithAppointment>> itemListMorning = new HashMap<>();
    private HashMap<Date, ArrayList<SlotWithAppointment>> itemListNoon = new HashMap<>();
    private HashMap<Date, ArrayList<SlotWithAppointment>> itemListEve = new HashMap<>();

    private ArrayList<String> timeList = new ArrayList<>();
    private Activity mActivity;
    private Utils utils;
    private Session session;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private LinearLayout llMorning, llNoon, llEve;
    private int colorWhite, colorGrey, colorBlack, count;
    private FragmentCalendarDayView parentFragment;
    private int sNoCheckedIn = 0;

    public static FragmentPagerCalendarDayView newInstance(int position, long date, JSONArray clinicArray) {
        Bundle bundle = new Bundle();
        bundle.putInt(DATE_PICKER_POSITION_KEY, position);
        bundle.putLong(DATE_PICKER_DATE_KEY, date);
        bundle.putString(CLINIC_ID_ARRAY, clinicArray.toString());


        FragmentPagerCalendarDayView simplePageFragment = new FragmentPagerCalendarDayView();
        simplePageFragment.setArguments(bundle);

        return simplePageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //getArguments().getInt(DATE_PICKER_POSITION_KEY, -1);
            date = getArguments().getLong(DATE_PICKER_DATE_KEY, -1);
            String str = getArguments().getString(CLINIC_ID_ARRAY, "");
            clinicsIdArray = new JSONArray(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_pager_cal_dayview, container, false);
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
            session = new Session(mActivity);
            utils = new Utils(mActivity);
            parentFragment = (FragmentCalendarDayView) getFragmentManager().findFragmentByTag(Constants.FragmentCalendarDayView);
            tvDate = (TextView) view.findViewById(R.id.tv_date_label);
            tvDate.setText(SIMPLE_DATE_FORMAT.format(date));
            llEve = (LinearLayout) view.findViewById(R.id.ll_eve_slots);
            llMorning = (LinearLayout) view.findViewById(R.id.ll_morning_slots);
            llNoon = (LinearLayout) view.findViewById(R.id.ll_noon_slots);

            colorWhite = ContextCompat.getColor(mActivity, R.color.white);
            colorGrey = ContextCompat.getColor(mActivity, R.color.grey_txt);
            colorBlack = ContextCompat.getColor(mActivity, R.color.black);

            getAppointments();
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private void getAppointments() {
        try {
            String filter = session.getCalendarFilters();
            String fromToDate = SIMPLE_DATE_FORMAT_TO_SEND.format(date);
            JSONObject jsonObject = new JSONObject();
            String[] array = mActivity.getResources().getStringArray(R.array.array_time_12_am_12pm);
            timeList = new ArrayList<>(Arrays.asList(array));
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
            API_Get_Slots_with_Appointments appointments = new API_Get_Slots_with_Appointments(jsonObject.toString(), FragmentPagerCalendarDayView.this, mActivity);
            appointments.getSlots();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setList() {
        sNoCheckedIn = 0;
        if (llMorning.getChildCount() > 0)
            llMorning.removeAllViews();
        if (llNoon.getChildCount() > 0)
            llNoon.removeAllViews();
        if (llEve.getChildCount() > 0)
            llEve.removeAllViews();

        Log.e("llMorning", "" + itemListMorning.size());
        Log.e("itemListNoon", "" + itemListNoon.size());
        Log.e("itemListEve", "" + itemListEve.size());

        setSlots(llMorning, itemListMorning);
        setSlots(llNoon, itemListNoon);
        setSlots(llEve, itemListEve);
    }

    private synchronized void setSlots(LinearLayout llParent, HashMap<Date, ArrayList<SlotWithAppointment>> map) {
        try {
            SortedSet<Date> keys = new TreeSet<>(map.keySet());
            boolean isExpired = isDateExpired();

            for (Date key : keys) {
                ArrayList<SlotWithAppointment> slotsArrayList = map.get(key);
                LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.item_calendar_day_view, null);
                RelativeLayout rlAppointment = (RelativeLayout) view.findViewById(R.id.rl_appointment_list);
                TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
                String time = timeFormat.format(key);
                time=time.startsWith("0")?time.substring(1):time;
                tvTime.setText(time);
                LinearLayout llAppointments = (LinearLayout) view.findViewById(R.id.ll_appointments);
                count = 0;
                for (int j = 0; j < slotsArrayList.size(); j++) {
                    count++;
                    SlotWithAppointment slotWithAppointment = slotsArrayList.get(j);
                    String premium = slotWithAppointment.premium;

                    ArrayList<Appointment> appointmentsList = slotWithAppointment.appointmentsList;
                    if (appointmentsList.size() > 0) {
                        llAppointments.addView(addAppointmentList(appointmentsList, premium, isExpired));
                    }
                    if (!isFilterAvailable() && appointmentsList.size()==0) {
                        llAppointments.addView(addFreeSlot(slotWithAppointment, isExpired));
                    }

                    if (llAppointments.getChildCount() > 0)
                        rlAppointment.setVisibility(View.VISIBLE);
                    else
                        rlAppointment.setVisibility(View.GONE);
                }
                llParent.addView(view);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private boolean isDateExpired() {
        boolean flag;
        String selectedDate = SIMPLE_DATE_FORMAT_TO_SEND1.format(date);
        flag = Utils.isAfterToday(selectedDate, "dd/MM/yyyy");
        return flag;
    }

    private boolean isToday() {
        boolean flag = false;
        String today = Utils.getCurrentTimeStamp("dd/MM/yyyy");
        String selectedDate = SIMPLE_DATE_FORMAT_TO_SEND1.format(date);
        if (today.equals(selectedDate)) flag = true;
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

    private View addFreeSlot(final SlotWithAppointment slotWithAppointment, boolean isExpired) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        try {
            if (count % 2 == 0)
                view = inflater.inflate(R.layout.item_appointment_day_view_left, null);
            else
                view = inflater.inflate(R.layout.item_appointment_day_view_right, null);

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

            String time=Utils.convertTime(slotWithAppointment.startTime,"HH:mm:ss","HH:mm");
            tvName.setText(time);
            tvName.setTextColor(colorBlack);

            if (isExpired) {
                boolean isToday = isToday();
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
        Intent intent = new Intent(mActivity, ActivityAddNewAppointment.class);
        intent.putExtra("SlotWithAppointment", slotWithAppointment);
        intent.putExtra("from", MODULE);
        String d = SIMPLE_DATE_FORMAT_TO_SEND1.format(date);
        intent.putExtra("date", d);
        parentFragment.isFilterApplied = true;
        startActivity(intent);
    }

    private View addAppointmentList(ArrayList<Appointment> appointmentsList, String premium, boolean isExpired) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewParent = inflater.inflate(R.layout.item_appointment_day_view, null);
        try {
            LinearLayout llRoot = (LinearLayout) viewParent.findViewById(R.id.ll_root);

            for (int z = 0; z < appointmentsList.size(); z++) {
                final Appointment appointment = appointmentsList.get(z);
                View view = null;
                if (count % 2 == 0)
                    view = inflater.inflate(R.layout.item_appointment_day_view_left, null);
                else
                    view = inflater.inflate(R.layout.item_appointment_day_view_right, null);

                RelativeLayout rlAppointment = (RelativeLayout) view.findViewById(R.id.rl_appointment);
                CircularTextView tvNo = (CircularTextView) view.findViewById(R.id.tv_no);
                tvNo.setVisibility(View.GONE);
                TextView tvName = (TextView) view.findViewById(R.id.tv_name);
                ImageView ivStar = (ImageView) view.findViewById(R.id.iv_star);
                String appointmentStatus = "", name = "";
                appointmentStatus = appointment.appointmentStatus;
                name = appointment.firstName;
                String lName=appointment.lastName;
                name=lName!=null && !lName.equals("null")?name+" "+lName:name;
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

                    rlAppointment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onClickAppointment(appointment);
                        }
                    });

                   /* if (isExpired) {
                        boolean isToday = isToday();
                        if (isToday) {
                            boolean isTimeExpired = isTimeExpired(appointment.appointmentTime);
                            if (isTimeExpired) {

                            } else {
                                rlAppointment.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        onClickAppointment(appointment);
                                    }
                                });
                            }
                        }
                    } else {
                        rlAppointment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onClickAppointment(appointment);
                            }
                        });
                    }*/

                }
                llRoot.addView(view);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return viewParent;
    }

    @Override
    public void onClickAppointment(Appointment appointment) {
        //selectOption(appointment);
        goToChangeStatus(appointment);
    }

    private void goToChangeStatus(Appointment appointment) {
        refresh();
        Intent intent = new Intent(mActivity, ActivityPatientAppointmentDetailedView.class);
        intent.putExtra("Appointment", appointment);
        String currentDate = SIMPLE_DATE_FORMAT_TO_SEND.format(date);
        intent.putExtra("date", currentDate);
        startActivity(intent);
    }


    private void refresh() {
        FragmentCalendarDayView parentFragment = (FragmentCalendarDayView) getFragmentManager()
                .findFragmentByTag(Constants.FragmentCalendarDayView);
        parentFragment.isFilterApplied = true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem filter = menu.findItem(R.id.filter);
        filter.setVisible(true);

        SearchManager searchManager = (SearchManager)
                mActivity.getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(mActivity.getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        searchMenuItem.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                goToFilter();
                break;
            case R.id.filter_patient:
                goToPatientFilter();
                break;
        }
        return true;
    }

    private void goToFilter() {
        parentFragment.isFilterApplied = true;
        Intent intent = new Intent(mActivity, ActivityFilterCalendar.class);
        startActivity(intent);
    }

    private void goToPatientFilter() {
        Intent intent = new Intent(mActivity, ActivityFilterPatient.class);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return true;
    }

    @Override
    public void onGetSlots(ArrayList<SlotWithAppointment> slotWithAppointments) {
        filterAppointments(slotWithAppointments);
        utils.hideProgress();
    }

    @Override
    public void onGetSlots(HashMap<String, ArrayList<SlotWithAppointment>> slotWithAppointmentsMap) {

    }

    private void filterAppointments(ArrayList<SlotWithAppointment> appointments) {
        try {


            double fromTime = 0.00;
            for (int i = 0; i <= 23; i++) {
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

                Date time = timeFormat.parse(timeList.get(i));
                if (slotWithAppointments.size() > 0 && i < 12) {
                    itemListMorning.put(time, slotWithAppointments);
                }
                if (slotWithAppointments.size() > 0 && i >= 12 && i < 16) {
                    itemListNoon.put(time, slotWithAppointments);
                }

                if (slotWithAppointments.size() > 0 && i >= 16) {
                    itemListEve.put(time, slotWithAppointments);
                }

                fromTime = fromTime + 1.00;
            }

            setList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetSlotsError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }




    public String getDate() {
        return SIMPLE_DATE_FORMAT_TO_SEND.format(date);
    }
}
