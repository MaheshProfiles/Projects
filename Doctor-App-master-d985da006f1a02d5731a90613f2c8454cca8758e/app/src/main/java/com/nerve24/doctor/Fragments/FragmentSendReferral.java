package com.nerve24.doctor.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nerve24.doctor.Activities.ActivityMain;
import com.nerve24.doctor.Activities.ActivityRefer;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;


public class FragmentSendReferral extends Fragment implements View.OnClickListener {
    private String TAG = "", MODULE = "FragmentSendReferral";
    private Activity mActivity;
    private Utils utils;
    private Session session;
    private TextView tvReferPharmacy, tvReferTest, tvReferDoctor, tvReferHospital;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_send_referral, container, false);
        initUI(rootView);
        return rootView;
    }

    private void initUI(View view) {

        TAG = "initUI";
        mActivity = getActivity();
        utils = new Utils(mActivity);
        session = new Session(mActivity);
        ((ActivityMain) getActivity()).setTitle("Send Referral");

        tvReferPharmacy = (TextView) view.findViewById(R.id.tv_refer_to_pharmacy);
        tvReferTest = (TextView) view.findViewById(R.id.tv_refer_test);
        tvReferDoctor = (TextView) view.findViewById(R.id.tv_refer_to_doctor);
        tvReferHospital = (TextView) view.findViewById(R.id.tv_refer_to_hospital);

        initListeners();
    }


    private void initListeners() {
        tvReferPharmacy.setOnClickListener(this);
        tvReferTest.setOnClickListener(this);
        tvReferDoctor.setOnClickListener(this);
        tvReferHospital.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        String referType[] = getResources().getStringArray(R.array.array_refer);
        switch (view.getId()) {
            case R.id.tv_refer_to_pharmacy:
               // goToRefer(referType[0]);
                break;
            case R.id.tv_refer_test:
               // goToRefer(referType[1]);
                break;
            case R.id.tv_refer_to_doctor:
                goToRefer(referType[2]);
                break;
            case R.id.tv_refer_to_hospital:
               // goToRefer(referType[3]);
                break;

        }
    }

    private void goToRefer(String refer) {
        Intent intent = new Intent(mActivity, ActivityRefer.class);
        intent.putExtra("refer_type", refer);
        startActivity(intent);
    }


}


