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

import com.nerve24.doctor.Activities.ActivityFilterPatient;
import com.nerve24.doctor.Activities.ActivityMain;
import com.nerve24.doctor.Adapter.AdapterPatientHistory;
import com.nerve24.doctor.ApiTask.API_Get_Patient_History;
import com.nerve24.doctor.Listeners.Listener_Click_PatientHistory;
import com.nerve24.doctor.Listeners.Listener_Get_PatientHistory;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.PatientHistory;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class FragmentPatientHistory extends Fragment
        implements Listener_Get_PatientHistory, Listener_Click_PatientHistory, SearchView.OnQueryTextListener {

    private String TAG = "", MODULE = "FragmentPatientHistory";
    private Activity mActivity;
    private Utils utils;
    private Session session;

    private RecyclerView recyclerView;
    private static ArrayList<PatientHistory> patientHistoryArrayList = new ArrayList<>();
    private AdapterPatientHistory adapter = null;
    private TextView tvEmpty;
    private SearchView searchView;
    private MenuItem searchMenuItem;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_history, container, false);
        setHasOptionsMenu(true);
        initUI(rootView);
        return rootView;
    }

    private void initUI(View view) {

        TAG = "initUI";
        mActivity = getActivity();
        utils = new Utils(mActivity);
        session = new Session(mActivity);
        ((ActivityMain) getActivity()).setTitle("Patient History");

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        tvEmpty = (TextView) view.findViewById(R.id.tv_empty);
    }

    private void getValues() {
        try {
            String filter = session.getPatientFilters();
            patientHistoryArrayList.clear();
            JSONObject jsonObject = new JSONObject();
           /* {
                "clinicId" : 1,
                    "referredByDoctorNerve24Id" : "NV4T190003",
                    "doctorNerve24Id" : "NV4T190003",
                    "appointmentFromDate" : "10-08-2016",
                    "appointmentToDate" : "10-08-2016"
            }*/
            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
            String yesterday = Utils.getCurrentTimeStamp("dd-MM-yyyy");
            yesterday=Utils.addDay(yesterday,sdf,-1);
            String fromDate=Utils.addDay(yesterday,sdf,-13);
            if (filter.length() == 0) {
                jsonObject.put("doctorNerve24Id", session.getNerve24Id());
                jsonObject.put("appointmentFromDate", fromDate);
                jsonObject.put("appointmentToDate", yesterday);
            } else {
                jsonObject = new JSONObject(filter);
                if (jsonObject.getString("appointmentFromDate").length() == 0) {
                    jsonObject.put("appointmentFromDate", fromDate);
                    jsonObject.put("appointmentToDate", yesterday);
                }
            }
            utils.showProgress("","loading..");
            API_Get_Patient_History apiGetPatientHistory = new API_Get_Patient_History(jsonObject.toString(), FragmentPatientHistory.this, mActivity);
            apiGetPatientHistory.getPatients();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setList() {
        try {
            TAG = "setList";
            if (patientHistoryArrayList.size() == 0) {
                tvEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new AdapterPatientHistory(mActivity, patientHistoryArrayList, FragmentPatientHistory.this);
                recyclerView.setAdapter(adapter);
            }
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }


    @Override
    public void onGetPatientHistory(ArrayList<PatientHistory> historyArrayList) {
        utils.hideProgress();
        patientHistoryArrayList = historyArrayList;
        setList();
    }

    @Override
    public void onGetPatientHistoryError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }

    @Override
    public void onClickPatientHistory(PatientHistory object) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem filterCalendar = menu.findItem(R.id.filter);
        filterCalendar.setVisible(false);
        MenuItem filter = menu.findItem(R.id.filter_patient);
        filter.setVisible(true);


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
        if (adapter != null)
            adapter.getFilter().filter(newText);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_patient:
                goToFilter();
                break;
        }
        return true;
    }

    private void goToFilter() {
        Intent intent = new Intent(mActivity, ActivityFilterPatient.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        getValues();
        super.onResume();
    }


}