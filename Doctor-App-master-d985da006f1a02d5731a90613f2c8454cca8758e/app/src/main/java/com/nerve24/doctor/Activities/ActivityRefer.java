package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.AppConfig.Constants;
import com.nerve24.doctor.Fragments.FragmentReferDoctor;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;

import io.fabric.sdk.android.Fabric;


public class ActivityRefer extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private String TAG = "", MODULE = "ActivityRefer";
    private Activity mActivity;
    private Utils utils;
    private Session session;
    private Spinner spRefer;
    private TextView tvRefer;
    private ImageView ivBack,ivRefer;
    private String currentReferType = "";
    private boolean spFlag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);

        initView();
        Fabric.with(this, new Crashlytics());
    }

    private void initView() {
        try {
            TAG = "initView";
            mActivity = ActivityRefer.this;
            utils = new Utils(mActivity);
            session = new Session(mActivity);

            ivBack = (ImageView) findViewById(R.id.iv_back);
            ivRefer=(ImageView) findViewById(R.id.iv_refer);
            spRefer = (Spinner) findViewById(R.id.sp_refer);
            tvRefer = (TextView) findViewById(R.id.tv_refer);

            initListeners();
            getValues();
            goToReferDoctor();

        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    public void setTitle(String title) {
        tvRefer.setText(title);
    }


    private void getValues() {
        try {
            String refer = getIntent().getStringExtra("refer_type");
            goToRefer(refer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListeners() {
        ivBack.setOnClickListener(this);
        ivRefer.setOnClickListener(this);
        tvRefer.setOnClickListener(this);
        spRefer.setOnItemSelectedListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_refer:
                spFlag=true;
                spRefer.performClick();
                break;
            case R.id.iv_refer:
                spFlag=true;
                spRefer.performClick();
                break;
        }
    }

    private void goToReferPharmacy() {

    }

    private void goToReferHospital() {

    }

    private void goToReferTest() {

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(  spFlag) {
            spFlag=false;
            String refer = adapterView.getItemAtPosition(i).toString();
            goToRefer(refer);
        }
    }

    private void goToRefer(String refer) {
        currentReferType = refer;
        Log.e("currentReferType",""+currentReferType);
        setTitle(currentReferType);
        String referType[] = getResources().getStringArray(R.array.array_refer);
        if (refer.equals(referType[0]))
            goToReferPharmacy();
        if (refer.equals(referType[1]))
            goToReferTest();
        if (refer.equals(referType[2]))
            goToReferDoctor();
        if (refer.equals(referType[3]))
            goToReferHospital();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void goToReferDoctor() {
        Fragment fragment = new FragmentReferDoctor();
        setFragment(fragment, Constants.FragmentReferDoctor);
    }


    private void setFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment,tag);
        fragmentTransaction.commit();
    }
}


