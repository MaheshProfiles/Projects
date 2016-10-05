package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.Adapter.PagerAdapter_TimeFee_SlotFee;
import com.nerve24.doctor.Listeners.Listener_Fragment;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.SlidingTabLayout;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.DailyWiseSlot;
import com.nerve24.doctor.pojo.DailyWiseSlotDay;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import io.fabric.sdk.android.Fabric;


public class ActivityTimeFee_SlotFee extends ActionBarActivity implements OnClickListener, ViewPager.OnPageChangeListener {
    private String TAG = "", MODULE = "ActivityTimeFee_SlotFee";
    private ViewPager pager;
    private SlidingTabLayout slidingTabLayout;
    private ArrayList<DailyWiseSlot> slotList = new ArrayList<>();
    private Spinner spinnerDay;
    private RelativeLayout rlSpinner;
    private ImageView ivBack;
    private TextView tvTitle, tvDay;
    private Activity mActivity;
    private Utils utils;
    private Session session;
    private DailyWiseSlotDay dailyWiseSlot;
    private String selectedDay = "", clinicName = "";
    private ArrayList<String> daysList;
    private int selectedpos = 0;
    public String fromTime = "", toTime = "", fee = "";
    private PagerAdapter_TimeFee_SlotFee pagerAdapterTimeFeeSlotFee;
    private boolean isSpinnerClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timefee_slotfee);
        initUI();
        Fabric.with(this, new Crashlytics());
    }


    private void initUI() {
        try {
            TAG = "initUI";
            mActivity = ActivityTimeFee_SlotFee.this;
            utils = new Utils(mActivity);
            session = new Session(mActivity);
            Bundle bundle = getIntent().getExtras();
            dailyWiseSlot = (DailyWiseSlotDay) bundle.getSerializable("DailyWiseSlotDay");

            tvTitle = (TextView) findViewById(R.id.tv_title);
            ivBack = (ImageView) findViewById(R.id.iv_back);
            spinnerDay = (Spinner) findViewById(R.id.sp_days);
            rlSpinner = (RelativeLayout) findViewById(R.id.rl_spinner);
            tvDay = (TextView) findViewById(R.id.tv_days);
            pager = (ViewPager) findViewById(R.id.viewpager);
            slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);

            initListeners();
            setValues();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void setValues() {
        try {
            TAG = "setValues";
            String[] daysArray = getResources().getStringArray(R.array.days_full_name);
            daysList = new ArrayList<>(Arrays.asList(daysArray));
            Log.e("dayslist", "" + daysList);

            ArrayAdapter adapter = new ArrayAdapter(this,
                    R.layout.spinner_item, daysList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDay.setAdapter(adapter);
            clinicName = dailyWiseSlot.clinicName;
            fromTime = dailyWiseSlot.startTime;
            toTime = dailyWiseSlot.endTime;
            tvTitle.setText(clinicName);

            String day = dailyWiseSlot.day;
            for (int i = 0; i < daysList.size(); i++) {
                if (day.equalsIgnoreCase(daysList.get(i))) {
                    selectedDay = daysList.get(i);
                    selectedpos = i;
                    tvDay.setText(selectedDay);
                }
            }

            slidingTabLayout.setBackgroundColor(getResources().getColor(
                    R.color.colorPrimary));
            slidingTabLayout.setDistributeEvenly(true);

            setTabs();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void initListeners() {
        ivBack.setOnClickListener(this);
        pager.addOnPageChangeListener(this);
        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isSpinnerClicked) {
                    isSpinnerClicked = false;
                    String day = adapterView.getItemAtPosition(i).toString();
                    tvDay.setText(day);
                    getSlotByDay(day);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tvDay.setOnClickListener(this);
        rlSpinner.setOnClickListener(this);
    }

    public void goToSlotFee() {
        pager.setCurrentItem(1);
    }

    private void getSlotByDay(String day) {
        try {
            TAG = "getSlotByDay";
            String res = session.getDailyWise();
            JSONObject jsonObject = new JSONObject(res);
            DailyWiseSlotDay dailyWiseSlotDay = Utils.parseData(jsonObject, clinicName, day);
            if (dailyWiseSlotDay == null) {
                Utils.alertBox(mActivity, "No slots available on " + day);
                tvDay.setText(selectedDay);
            } else {
                dailyWiseSlot = dailyWiseSlotDay;
                setTabs();
            }
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }


    private void setTabs() {
        try {
            TAG = "setTabs";
            if (getSupportFragmentManager().getFragments() != null) {
                getSupportFragmentManager().getFragments().clear();
            }
            pagerAdapterTimeFeeSlotFee = new PagerAdapter_TimeFee_SlotFee(getSupportFragmentManager(),
                    dailyWiseSlot);
            pager.setAdapter(pagerAdapterTimeFeeSlotFee);
            slidingTabLayout.setVisibility(View.VISIBLE);
            slidingTabLayout.setViewPager(pager);
            slidingTabLayout
                    .setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                        @Override
                        public int getIndicatorColor(int position) {
                            return getResources().getColor(
                                    R.color.white);
                        }
                    });
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.tv_days: {
                isSpinnerClicked = true;
                spinnerDay.performClick();
            }

            case R.id.rl_spinner: {
                isSpinnerClicked = true;
                spinnerDay.performClick();
            }
            break;
        }

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.e("onPageSelected", "" + position);
        if (position == 1) {
            Listener_Fragment fragment = (Listener_Fragment) pagerAdapterTimeFeeSlotFee.instantiateItem(pager, position);
            fragment.onVisible();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
