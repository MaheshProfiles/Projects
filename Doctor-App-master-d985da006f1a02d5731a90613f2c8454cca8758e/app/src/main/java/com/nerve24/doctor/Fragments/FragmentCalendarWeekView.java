package com.nerve24.doctor.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nerve24.doctor.Activities.ActivityMain;
import com.nerve24.doctor.Adapter.AdapterWeekViewPager;
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

public class FragmentCalendarWeekView extends Fragment implements Listener_Get_Clinics {
    private SimpleDateFormat DATE_PICKER_DD_MM_YYYY_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    private String TAG = "", MODULE = "FragmentCalendarWeekView";
    private Utils utils;
    private Session session;
    private Activity mActivity;
    private String thisWeekSunday = "";
    private ArrayList<String> weekStartDateList = new ArrayList<>();
    private ViewPager pager;
    private JSONArray clinicArray = new JSONArray();


    public static FragmentCalendarWeekView newInstance() {
        return new FragmentCalendarWeekView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar_week_view, container, false);
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
            pager = (ViewPager) view.findViewById(R.id.viewpager);

            ((ActivityMain) getActivity()).setTitle("Calendar");
            getWeekList();
            initListeners();
            getClinics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getClinics() {
        try {
            TAG = "getValues";
            utils.showProgress("", "loading..");
            String nerve24Id = session.getNerve24Id();
            API_Get_Clinics apiGetClinics = new API_Get_Clinics(nerve24Id, FragmentCalendarWeekView.this, mActivity);
            apiGetClinics.getClinics();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }


    private void getWeekList() {
        try {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            thisWeekSunday = DATE_PICKER_DD_MM_YYYY_FORMAT.format(c.getTime());

            for (int i = -52; i < 52; i++) {
                int count = i * 7;
                String day = Utils.addDay(thisWeekSunday, DATE_PICKER_DD_MM_YYYY_FORMAT, count);
                weekStartDateList.add(day);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListeners() {

    }

    private void setTabs() {
        try {
            TAG = "setTabs";

            final AdapterWeekViewPager adapterWeekViewPager = new AdapterWeekViewPager(getFragmentManager(),
                    weekStartDateList,clinicArray);
            pager.setAdapter(adapterWeekViewPager);
            pager.setCurrentItem(52);
            pager.setOffscreenPageLimit(1);
            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    FragmentPagerCalendarWeekView fragment = (FragmentPagerCalendarWeekView) adapterWeekViewPager.instantiateItem(pager, position);
                    // fragment.setValues();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        } catch (Exception e) {
            Log.e(MODULE, TAG);
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
            setTabs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetClinicsError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }
}
