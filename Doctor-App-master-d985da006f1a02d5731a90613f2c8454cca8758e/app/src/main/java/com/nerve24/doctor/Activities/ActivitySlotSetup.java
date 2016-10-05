package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.Adapter.AdapterSlotSetup;
import com.nerve24.doctor.ApiTask.API_Get_Slots;
import com.nerve24.doctor.Listeners.Listener_Click_SlotSetup;
import com.nerve24.doctor.Listeners.Listener_Get_Slots;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.SlotClinic;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class ActivitySlotSetup extends AppCompatActivity implements Listener_Get_Slots, Listener_Click_SlotSetup, SearchView.OnQueryTextListener {
    private String TAG = "", MODULE = "ActivitySlotSetup";
    private RecyclerView recyclerView;
    private Activity mActivity;
    private static ArrayList<SlotClinic> clinicList = new ArrayList<>();
    private AdapterSlotSetup adapter = null;
    private Utils utils;
    private Session session;
    private TextView tvEmpty;
    private SearchView searchView;
    private MenuItem searchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_setup);

        initUI();
        Fabric.with(this, new Crashlytics());
    }

    @Override
    protected void onResume() {
        getValues();
        super.onResume();
    }

    private void initUI() {
        try {
            TAG = "initUI";
            mActivity = ActivitySlotSetup.this;
            utils = new Utils(mActivity);
            session = new Session(mActivity);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Slot Setup");
            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            recyclerView.setHasFixedSize(true);
            tvEmpty = (TextView) findViewById(R.id.tv_empty);
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }

    }

    private void getValues() {
        try {
            TAG = "getValues";
            utils.showProgress("", "loading..");
            String nerve24Id = session.getNerve24Id();
            API_Get_Slots apiGetSlots = new API_Get_Slots(nerve24Id, ActivitySlotSetup.this, mActivity);
            apiGetSlots.getSlots();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void setList() {
        try {
            TAG="setList";
            if (clinicList.size() == 0) {
                tvEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
           /*     Collections.sort(clinicList, new Comparator<SlotClinic>() {
                    @Override public int compare(SlotClinic p1, SlotClinic p2) {
                        return p1.clinicName.compareTo(p2.clinicName);
                    }

                });*/
                tvEmpty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new AdapterSlotSetup(mActivity, clinicList, ActivitySlotSetup.this);
                recyclerView.setAdapter(adapter);
            }
        }
        catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }


    @Override
    public void onGetSlots(ArrayList<SlotClinic> slotClinics) {
        utils.hideProgress();
        clinicList = slotClinics;
        setList();
    }

    @Override
    public void onGetSlotsError(String res) {
        utils.hideProgress();
    }

    @Override
    public void onEditItem(SlotClinic object) {
        Log.e("onEditItem", "" + object.clinicId);
        Intent intent = new Intent(mActivity, ActivitySlotEdit.class);
        intent.putExtra("clinicId", object.clinicId);
        startActivity(intent);
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

    @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.search_menu, menu);

         SearchManager searchManager = (SearchManager)
                 getSystemService(Context.SEARCH_SERVICE);
         searchMenuItem = menu.findItem(R.id.search);
         searchView = (SearchView) searchMenuItem.getActionView();

         searchView.setSearchableInfo(searchManager.
                 getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
         searchView.setOnQueryTextListener(this);

         return true;
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
