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
import com.nerve24.doctor.Adapter.AdapterClinicWisePrice;
import com.nerve24.doctor.ApiTask.API_Get_Price_Setup;
import com.nerve24.doctor.Listeners.Listener_ClinicWisePriceList;
import com.nerve24.doctor.Listeners.Listener_Get_Price_Setup;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.PriceSetup;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class ActivityClinicwise extends AppCompatActivity implements Listener_ClinicWisePriceList,
        Listener_Get_Price_Setup,SearchView.OnQueryTextListener {

    private String TAG = "", MODULE = "ActivityClinicwise";
    private RecyclerView recyclerView;
    private static ArrayList<PriceSetup> clinicList = new ArrayList<>();
    private AdapterClinicWisePrice adapter = null;
    private Activity mActivity;
    private Utils utils;
    private Session session;
    private String userName;
    private TextView tvEmpty;
    private SearchView searchView;
    private MenuItem searchMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinicwise);
        initUI();
        Fabric.with(this, new Crashlytics());
    }

    private void initUI() {
        try {
            TAG = "initUI";
            mActivity = ActivityClinicwise.this;
            utils = new Utils(mActivity);
            session = new Session(mActivity);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Clinic Wise");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            tvEmpty = (TextView) findViewById(R.id.tv_empty);
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }


    private void setList() {
        try {
            TAG = "setList";
            if (clinicList.size() == 0) {
                tvEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new AdapterClinicWisePrice(this, clinicList, ActivityClinicwise.this);
                recyclerView.setAdapter(adapter);
            }
        }
        catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        getValues();
        super.onResume();
    }

    private void getValues() {
        try {
            TAG = "getValues";
            utils.showProgress("", "loading..");
            userName = session.getNerve24Id();
            API_Get_Price_Setup apiGetPriceSetup = new API_Get_Price_Setup(userName, ActivityClinicwise.this, mActivity);
            apiGetPriceSetup.getPriceSetup();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }

    }


    @Override
    public void onClickClinicWiseItem(PriceSetup PriceSetup) {
        Intent intent = new Intent(mActivity, ActivityEditClinicPrice.class);
        intent.putExtra("clinicId", PriceSetup.clinicId);
        startActivity(intent);
    }

    @Override
    public void onDeleteItem(PriceSetup PriceSetup) {

    }

    @Override
    public void oneditItem(PriceSetup PriceSetup) {
        Log.e("oneditItem", "" + PriceSetup.clinicId);
        Intent intent = new Intent(mActivity, ActivityEditClinicPrice.class);
        intent.putExtra("clinicId", PriceSetup.clinicId);
        startActivity(intent);
    }

    @Override
    public void onGetPriceSetup(ArrayList<PriceSetup> priceSetupList) {
        try {
            TAG = "onGetPriceSetup";
            utils.hideProgress();
            clinicList = priceSetupList;
            setList();
        }
        catch (Exception e)
        {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    @Override
    public void onGetPriceSetupError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
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
