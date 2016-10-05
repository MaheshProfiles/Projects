package com.nerve24.doctor.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.nerve24.doctor.Fragments.Fragment_Slot_Edit;

import java.util.ArrayList;

public class AdapterSlotEditPager extends FragmentStatePagerAdapter {

    private ArrayList<String> dayList;

    public AdapterSlotEditPager(FragmentManager fm,
                                ArrayList<String> dayList) {
        super(fm);
        this.dayList = dayList;
    }


    @Override
    public Fragment getItem(int position) {

        return Fragment_Slot_Edit.newInstance(dayList.get(position));
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
        return dayList.size();
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }



}