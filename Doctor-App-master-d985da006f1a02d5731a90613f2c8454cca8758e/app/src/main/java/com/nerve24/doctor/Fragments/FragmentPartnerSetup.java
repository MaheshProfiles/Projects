package com.nerve24.doctor.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nerve24.doctor.Activities.ActivityMain;
import com.nerve24.doctor.R;


public class FragmentPartnerSetup extends Fragment
        implements View.OnClickListener {

    private Activity mActivity;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_partner_setup, container, false);
        initUI(rootView);
        return rootView;
    }

    private void initUI(View view) {
        mActivity = getActivity();

        ((ActivityMain) getActivity()).setTitle("Parnet setup");

        initListeners();
    }

    private void initListeners() {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }


}