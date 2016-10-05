package com.nerve24.doctor.Fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nerve24.doctor.Activities.ActivityTimeFee_SlotFee;
import com.nerve24.doctor.Adapter.AdapterDailyWise;
import com.nerve24.doctor.Listeners.Listener_Daily_Wise_Slot;
import com.nerve24.doctor.R;
import com.nerve24.doctor.pojo.DailyWiseSlot;
import com.nerve24.doctor.pojo.DailyWiseSlotDay;

import java.util.ArrayList;

public class Fragment_Daily_Wise_slot extends Fragment implements Listener_Daily_Wise_Slot,SearchView.OnQueryTextListener {
    private String TAG = "", MODULE = "Fragment_Daily_Wise_slot";

    private DailyWiseSlot object;
    private Activity mActivity;
    private RecyclerView recyclerView;
    private ArrayList<DailyWiseSlotDay> objList;
    private AdapterDailyWise adapter;
    private TextView tvEmpty;
    private SearchView searchView;
    private MenuItem searchMenuItem;

    public Fragment_Daily_Wise_slot() {
        // Required empty public constructor
    }

    public static Fragment_Daily_Wise_slot newInstance(DailyWiseSlot object) {
        Fragment_Daily_Wise_slot fragment = new Fragment_Daily_Wise_slot();
        Bundle args = new Bundle();
        args.putSerializable("DailyWiseSlot", object);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            object = (DailyWiseSlot) getArguments().getSerializable("DailyWiseSlot");
            Log.e("objList size", "" + object.dailyWiseSlotlist.size());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_wise_slot, container, false);
        initUI(view);
        Log.e("onCreateView", "onCreateView");
        setHasOptionsMenu(true);
        return view;
    }

    private void initUI(View view) {
        try {
            TAG="initUI";
            mActivity = getActivity();
            tvEmpty = (TextView) view.findViewById(R.id.tv_empty);
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            objList = object.dailyWiseSlotlist;

            if (objList.size() == 0) {
                tvEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                setList();
            }
        }
        catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void setList() {
        recyclerView.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        adapter = new AdapterDailyWise(mActivity, objList, Fragment_Daily_Wise_slot.this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onClickDailyWiseItem(DailyWiseSlotDay dailyWiseSlotDay) {
        Log.e("dailyWiseSlotDay", "" + dailyWiseSlotDay.slotsId);
        Intent intent = new Intent(mActivity, ActivityTimeFee_SlotFee.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("DailyWiseSlotDay", dailyWiseSlotDay);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager)
                mActivity.getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(mActivity.getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);


    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(adapter!=null)
        adapter.getFilter().filter(newText);
        return true;
    }
}
