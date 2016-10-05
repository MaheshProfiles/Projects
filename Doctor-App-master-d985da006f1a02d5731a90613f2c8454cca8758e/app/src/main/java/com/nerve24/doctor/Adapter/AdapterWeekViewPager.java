package com.nerve24.doctor.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.nerve24.doctor.Fragments.FragmentPagerCalendarWeekView;

import org.json.JSONArray;

import java.util.ArrayList;

public class AdapterWeekViewPager extends FragmentStatePagerAdapter {

    private ArrayList<String> startDaylist;
    private JSONArray clinicArray=new JSONArray();

    public AdapterWeekViewPager(FragmentManager fm,
                                ArrayList<String> startDaylist,JSONArray clinicArray) {
        super(fm);
        this.startDaylist = startDaylist;
        this.clinicArray=clinicArray;
    }


    @Override
    public Fragment getItem(int position) {

        Log.e("startDaylist",""+startDaylist.get(position));
        return FragmentPagerCalendarWeekView.newInstance(startDaylist.get(position),clinicArray);
    }

    public CharSequence getPageTitle(int position) {
        String title = "";
        return title;
    }

    private boolean doNotifyDataSetChangedOnce = false;
    @Override
    public int getCount() {
        if (doNotifyDataSetChangedOnce) {
            doNotifyDataSetChangedOnce = false;
            notifyDataSetChanged();
        }
        return startDaylist.size();
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }



}