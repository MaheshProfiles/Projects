package com.nerve24.doctor.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nerve24.doctor.Fragments.Fragment_Daily_Wise_slot;
import com.nerve24.doctor.pojo.DailyWiseSlot;

import java.util.ArrayList;

public class AdapterDailyWiseSlotPager extends FragmentPagerAdapter {

    private ArrayList<DailyWiseSlot> slotList;

    public AdapterDailyWiseSlotPager(FragmentManager fm,
                                     ArrayList<DailyWiseSlot> slotList) {
        super(fm);
        this.slotList = slotList;
    }

    @Override
    public Fragment getItem(int position) {

        return Fragment_Daily_Wise_slot.newInstance(slotList.get(position));
    }

    public CharSequence getPageTitle(int position) {
        String title = "";
        if (position == 0) {
            title="MON";
        } else if (position == 1) {
            title="TUE";
        } else if (position == 2) {
            title="WED";
        } else if (position == 3) {
            title="THU";
        } else if (position == 4) {
            title="FRI";
        } else if (position == 5) {
            title="SAT";
        } else if (position == 6) {
            title="SUN";
        }

        return title;
    }

    @Override
    public int getCount() {
        return slotList.size();
    }

}