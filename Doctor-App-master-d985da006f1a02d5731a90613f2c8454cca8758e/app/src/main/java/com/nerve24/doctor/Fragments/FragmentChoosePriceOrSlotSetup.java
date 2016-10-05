package com.nerve24.doctor.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nerve24.doctor.Activities.ActivityMain;
import com.nerve24.doctor.Activities.ActivityPriceandslot;
import com.nerve24.doctor.Activities.ActivitySlotSetup;
import com.nerve24.doctor.R;


public class FragmentChoosePriceOrSlotSetup extends Fragment
        implements View.OnClickListener {

    private CardView cvSlotSetup, cvPricingAndSlot;
    private Activity mActivity;
    private ImageView iv_slot_setup, iv_pricing_slot;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_choose_price_or_slot_setup, container, false);
        initUI(rootView);
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initUI(View view) {
        mActivity = getActivity();

        cvSlotSetup = (CardView) view.findViewById(R.id.card_view_clinic_wise);
        cvPricingAndSlot = (CardView) view.findViewById(R.id.card_pricing_slot);
        iv_pricing_slot = (ImageView) view.findViewById(R.id.iv_pricing_slot);
        iv_slot_setup = (ImageView) view.findViewById(R.id.iv_slot_setup);
        ((ActivityMain) getActivity()).setTitle("Onboarding");

        initListeners();
    }

    private void initListeners() {
        cvSlotSetup.setOnClickListener(this);
        cvPricingAndSlot.setOnClickListener(this);
        iv_pricing_slot.setOnClickListener(this);
        iv_slot_setup.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.card_view_clinic_wise:
                goToSlotSetup();
                break;
            case R.id.card_pricing_slot:
                goToPricingSlot();
                break;
            case R.id.iv_slot_setup:
                goToSlotSetup();
                break;
            case R.id.iv_pricing_slot:
                goToPricingSlot();
                break;
        }
    }

    private void goToPricingSlot() {
        Intent intent = new Intent(mActivity, ActivityPriceandslot.class);
        startActivity(intent);
    }

    private void goToSlotSetup() {
        Intent intent = new Intent(mActivity, ActivitySlotSetup.class);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }


}