package com.nerve24.doctor.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nerve24.doctor.Activities.ActivityAddNewAppointment;
import com.nerve24.doctor.Activities.ActivityGroupCancellation;
import com.nerve24.doctor.Activities.ActivityMain;
import com.nerve24.doctor.Adapter.AdapterCustomDate;
import com.nerve24.doctor.ApiTask.API_Get_Clinics;
import com.nerve24.doctor.Listeners.Listener_Get_Clinics;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.Clinic;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pl.rspective.pagerdatepicker.PagerDatePickerDateFormat;
import pl.rspective.pagerdatepicker.adapter.DatePagerFragmentAdapter;
import pl.rspective.pagerdatepicker.view.DateRecyclerView;

public class FragmentCalendarDayView extends Fragment implements Listener_Get_Clinics, View.OnClickListener {
    private String TAG = "", MODULE = "FragmentCalendarDayView";
    private Utils utils;
    private Session session;
    private Activity mActivity;

    private DateRecyclerView dateList;
    private ViewPager pager;
    private FloatingActionButton fbCalendar, fbNew, fbGroupCancel;
    private LinearLayout llFloatingButtons;
    private SimpleDateFormat DATE_PICKER_DD_MM_YYYY_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private JSONArray clinicArray = new JSONArray();

    private final static int INTERVAL = 1000 * 60 * 1;
    private Handler mHandler = new Handler();
    private DatePagerFragmentAdapter fragmentAdapter;
    private String currentSlidedate = "";
    public static boolean isFilterApplied = false;


    public static FragmentCalendarDayView newInstance() {
        return new FragmentCalendarDayView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar_day_view, container, false);
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
            pager = (ViewPager) view.findViewById(R.id.pager);
            dateList = (DateRecyclerView) view.findViewById(R.id.date_list);
            fbCalendar = (FloatingActionButton) view.findViewById(R.id.fb_calendar);
            fbNew = (FloatingActionButton) view.findViewById(R.id.fb_new);
            fbGroupCancel = (FloatingActionButton) view.findViewById(R.id.fb_group_cancel);
            llFloatingButtons = (LinearLayout) view.findViewById(R.id.ll_fb);
            ((ActivityMain) getActivity()).setTitle("Calendar");

            initListeners();

            getClinics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListeners() {
        fbCalendar.setOnClickListener(this);
        fbNew.setOnClickListener(this);
        fbGroupCancel.setOnClickListener(this);
    }

    private void getClinics() {
        try {
            TAG = "getValues";
            utils.showProgress("", "loading..");
            String nerve24Id = session.getNerve24Id();
            API_Get_Clinics apiGetClinics = new API_Get_Clinics(nerve24Id, FragmentCalendarDayView.this, mActivity);
            apiGetClinics.getClinics();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    public void configCalendar(String date) {
        try {
            Date start = null;
            Date end = null;
            Date defaultDate = null;

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, 1);
            String maxDate = DATE_PICKER_DD_MM_YYYY_FORMAT.format(cal.getTime());

            start = PagerDatePickerDateFormat.DATE_PICKER_DD_MM_YYYY_FORMAT.parse("01-01-2015");
            end = PagerDatePickerDateFormat.DATE_PICKER_DD_MM_YYYY_FORMAT.parse(maxDate);

            defaultDate = PagerDatePickerDateFormat.DATE_PICKER_DD_MM_YYYY_FORMAT.parse(date);
            currentSlidedate = DATE_PICKER_DD_MM_YYYY_FORMAT.format(defaultDate);

            final AdapterCustomDate dateAdapter = new AdapterCustomDate(start, end, defaultDate);
            dateList.setAdapter(dateAdapter);

            fragmentAdapter = new DatePagerFragmentAdapter(getFragmentManager(), dateList.getDateAdapter()) {
                @Override
                protected Fragment getFragment(int position, long date) {
                    return FragmentPagerCalendarDayView.newInstance(position, date, clinicArray);
                }
            };

            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    Date date = dateAdapter.getItem(position).getDate();
                    currentSlidedate = PagerDatePickerDateFormat.DATE_PICKER_DD_MM_YYYY_FORMAT.format(date);
                    stopRepeatingTask();
                    startRepeatingTask();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            pager.setAdapter(fragmentAdapter);
            pager.setOffscreenPageLimit(1);
            dateList.setPager(pager);

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

            Calendar cal1 = Calendar.getInstance();
            String today = DATE_PICKER_DD_MM_YYYY_FORMAT.format(cal1.getTime());
            configCalendar(today);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fb_calendar:
                openFloatingButtons();
                break;
            case R.id.fb_group_cancel:
                goToGroupCancel();
                break;
            case R.id.fb_new:
                goToNewAppointment();
                break;

        }
    }

    private void goToGroupCancel() {
        isFilterApplied = true;
        Intent intent = new Intent(mActivity, ActivityGroupCancellation.class);
        startActivity(intent);
    }

    private void goToNewAppointment() {
        isFilterApplied = true;
        Intent intent = new Intent(mActivity, ActivityAddNewAppointment.class);
        startActivity(intent);
    }


    private void openFloatingButtons() {
        if (llFloatingButtons.getVisibility() == View.GONE) {
            llFloatingButtons.setVisibility(View.VISIBLE);
        } else {
            llFloatingButtons.setVisibility(View.GONE);
        }
    }

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            configCalendar(currentSlidedate);
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    void startRepeatingTask() {
        mHandler.postDelayed(mHandlerTask, INTERVAL);
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mHandlerTask);
    }

    @Override
    public void onResume() {
        Log.e("onResume", "onResume");
        Log.e("isFilterApplied", "" + isFilterApplied);
        if (isFilterApplied) {
            isFilterApplied = false;
            configCalendar(currentSlidedate);
        }

        startRepeatingTask();
        super.onResume();
    }

    @Override
    public void onStop() {
        stopRepeatingTask();
        super.onStop();
    }
}
