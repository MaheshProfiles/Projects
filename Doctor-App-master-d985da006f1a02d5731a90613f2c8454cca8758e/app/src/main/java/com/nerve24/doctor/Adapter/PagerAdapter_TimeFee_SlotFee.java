package com.nerve24.doctor.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nerve24.doctor.Fragments.Fragment_Slot_Fee;
import com.nerve24.doctor.Fragments.Fragment_Time_Fee;
import com.nerve24.doctor.pojo.DailyWiseSlotDay;

public class PagerAdapter_TimeFee_SlotFee extends FragmentPagerAdapter {

    private DailyWiseSlotDay dailyWiseSlotDay;

    public PagerAdapter_TimeFee_SlotFee(FragmentManager fm,
                                        DailyWiseSlotDay dailyWiseSlotDay) {
        super(fm);
        this.dailyWiseSlotDay = dailyWiseSlotDay;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        if (position == 0) {
            fragment = Fragment_Time_Fee.newInstance(dailyWiseSlotDay);
        } else if (position == 1) {
            fragment = Fragment_Slot_Fee.newInstance(dailyWiseSlotDay);
        }
        return fragment;
    }

    public CharSequence getPageTitle(int position) {
        String title = "";
        if (position == 0) {
            title = "TIME & FEE";
        } else if (position == 1) {
            title = "SLOT & FEE";
        }

        return title;
    }

    @Override
    public int getCount() {
        return 2;
    }

}