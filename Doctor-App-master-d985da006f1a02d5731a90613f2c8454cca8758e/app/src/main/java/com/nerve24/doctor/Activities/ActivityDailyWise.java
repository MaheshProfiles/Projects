package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.Adapter.AdapterDailyWiseSlotPager;
import com.nerve24.doctor.ApiTask.API_Get_Daily_Wise_Slots;
import com.nerve24.doctor.Listeners.Listener_Get_Daily_Wise_Slots;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.SlidingTabLayout;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.DailyWiseSlot;

import org.json.JSONObject;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;


public class ActivityDailyWise extends ActionBarActivity implements OnClickListener, Listener_Get_Daily_Wise_Slots {
    private String TAG = "", MODULE = "ActivityDailyWise";
    private ViewPager pager;
    private SlidingTabLayout slidingTabLayout;
    private ArrayList<DailyWiseSlot> slotList = new ArrayList<>();
    private Activity mActivity;
    private Utils utils;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_wise);
        initUI();
        Fabric.with(this, new Crashlytics());
        getDailyWiseData();
    }

    private void getDailyWiseData() {
        try {
            TAG = "getDailyWiseData";
            utils.showProgress("", "loading..");
            String json = getIntent().getStringExtra("json");
            JSONObject jsonObject = new JSONObject(json);
            API_Get_Daily_Wise_Slots apiGetDailyWiseSlots = new API_Get_Daily_Wise_Slots(jsonObject, ActivityDailyWise.this, mActivity);
            apiGetDailyWiseSlots.get();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }

    }


    private void initUI() {
        try {
            TAG = "initUI";
            mActivity = ActivityDailyWise.this;
            utils = new Utils(mActivity);
            session = new Session(mActivity);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Day Wise");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            pager = (ViewPager) findViewById(R.id.viewpager);
            slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
            slidingTabLayout.setBackgroundColor(getResources().getColor(
                    R.color.colorPrimary));
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


    private void setTabs() {
        try {
            TAG = "setTabs";
            if (getSupportFragmentManager().getFragments() != null) {
                getSupportFragmentManager().getFragments().clear();
            }
            pager.setAdapter(new AdapterDailyWiseSlotPager(getSupportFragmentManager(),
                    slotList));
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


    }


    @Override
    public void onGetDailyWiseSlots(ArrayList<DailyWiseSlot> slotList) {
        utils.hideProgress();
        this.slotList = slotList;
        Log.e("slotList size", "-->" + slotList.size());
        setTabs();
    }

    @Override
    public void onGetDailyWiseSlotsError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }
}
