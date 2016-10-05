package com.nerve24.doctor.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nerve24.doctor.Activities.ActivityTimeFee_SlotFee;
import com.nerve24.doctor.Adapter.AdapterSlotFee;
import com.nerve24.doctor.ApiTask.API_Get_Daily_Wise_Slots;
import com.nerve24.doctor.ApiTask.API_Get_Slot_Fee;
import com.nerve24.doctor.ApiTask.API_Save_Premium_Slots;
import com.nerve24.doctor.Listeners.Listener_Fragment;
import com.nerve24.doctor.Listeners.Listener_Get_Daily_Wise_Slots;
import com.nerve24.doctor.Listeners.Listener_Get_Slot_Fee;
import com.nerve24.doctor.Listeners.Listener_Save_Premium_Slots;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.*;
import com.nerve24.doctor.pojo.SlotFee;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_Slot_Fee extends Fragment implements Listener_Fragment, Listener_Get_Daily_Wise_Slots, Listener_Get_Slot_Fee, View.OnClickListener, Listener_Save_Premium_Slots {
    private String TAG = "", MODULE = "Fragment_Slot_Fee";
    private Activity mActivity;
    private Utils utils;
    private Session session;
    private RecyclerView recyclerView;
    private ArrayList<SlotFee> objList;
    private DailyWiseSlotDay object;
    private AdapterSlotFee adapter;
    private TextView tvEmpty;
    private Button btnSave, btnCancel;
    private JSONArray jsonArray = new JSONArray();
    private String response = "";

    public Fragment_Slot_Fee() {
        // Required empty public constructor
    }

    public static Fragment_Slot_Fee newInstance(DailyWiseSlotDay object) {
        Fragment_Slot_Fee fragment = new Fragment_Slot_Fee();
        Bundle args = new Bundle();
        args.putSerializable("DailyWiseSlotDay", object);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            object = (DailyWiseSlotDay) getArguments().getSerializable("DailyWiseSlotDay");
           // Log.e("slots id", "" + object.slotsId);
        }
    }

    public void refresh() {
        if (adapter != null) {
            Log.e("refresh", "refresh");
            setAdapter();
        }
    }

    private void setAdapter() {
        String fromTime = ((ActivityTimeFee_SlotFee) getActivity()).fromTime;
        String toTime = ((ActivityTimeFee_SlotFee) getActivity()).toTime;
        String fee = ((ActivityTimeFee_SlotFee) getActivity()).fee;
        recyclerView.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        adapter = new AdapterSlotFee(mActivity, objList, fromTime, toTime, fee);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slot_fee, container, false);
        initUI(view);
        Log.e("onCreateView", "onCreateView");
        return view;
    }

    private void initUI(View view) {
        mActivity = getActivity();
        utils = new Utils(mActivity);
        session = new Session(mActivity);

        tvEmpty = (TextView) view.findViewById(R.id.tv_empty);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        btnSave = (Button) view.findViewById(R.id.btn_save);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        getSlots();
    }

    private void getSlots() {
        try {
            TAG = "getSlots";
            utils.showProgress("", "loading..");
            String parentSlotId = object.slotsId;
            String userName = session.getNerve24Id();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("parentSlotId", parentSlotId);
            jsonObject.put("userName", userName);

            API_Get_Slot_Fee apiGetSlotFee = new API_Get_Slot_Fee(jsonObject, Fragment_Slot_Fee.this, mActivity);
            apiGetSlotFee.get();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void setList() {
        if (objList.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {

            setAdapter();
        }
    }


    @Override
    public void onGetSlotFee(ArrayList<SlotFee> slotFeesList, JSONArray jsonArray) {
        utils.hideProgress();
        objList = slotFeesList;
        this.jsonArray = jsonArray;
        setList();
    }

    @Override
    public void onGetSlotFeeError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                saveProcess();
                break;
            case R.id.btn_cancel:
                mActivity.finish();
                break;
        }
    }

    @Override
    public void onStop() {
        utils.hideProgress();
        super.onStop();
    }

    private void saveProcess() {
        try {
            TAG = "saveProcess";
            String fee_common = ((ActivityTimeFee_SlotFee) getActivity()).fee;

            if (adapter.view_LastFocused != null) {
                adapter.feeProcess(adapter.view_LastFocused);
            }
            HashMap<String, String> mapFee = adapter.getFeeUpdate();
            HashMap<String, String> mapStar = adapter.getStarUpdate();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String slotId = jsonObject.getString("slotId");
                if (mapFee.containsKey(slotId)) {
                    String fee = mapFee.get(slotId);
                    jsonObject.put("override", adapter.override);
                    jsonObject.put("fee", fee);

                }
                if (mapStar.containsKey(slotId)) {
                    String pre = mapStar.get(slotId);
                    boolean premium = Boolean.parseBoolean(pre);
                    jsonObject.put("premium", premium);
                }
            }


            utils.showProgress("", "loading..");
            API_Save_Premium_Slots apiSavePremiumSlots = new API_Save_Premium_Slots(jsonArray, Fragment_Slot_Fee.this, mActivity);
            apiSavePremiumSlots.save();

        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }

    }

    @Override
    public void onSlotsSaved(String res) {
        response = res;
        refreshDailyWise();
    }

    private void refreshDailyWise() {
        try {
            TAG = "refreshDailyWise";
            utils.showProgress("", "loading..");
            String json = session.getDailyWiseParams();
            JSONObject jsonObject = new JSONObject(json);
            API_Get_Daily_Wise_Slots apiGetDailyWiseSlots = new API_Get_Daily_Wise_Slots(jsonObject, Fragment_Slot_Fee.this, mActivity);
            apiGetDailyWiseSlots.get();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }

    }

    @Override
    public void onSlotsSavedError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }

    private void successAlert(String res) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mActivity);
        builder.title("Success");
        builder.content(res);
        builder.positiveText("Ok");
        builder.show();
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                mActivity.finish();
            }
        });

    }

    @Override
    public void onGetDailyWiseSlots(ArrayList<DailyWiseSlot> slotList) {
        utils.hideProgress();
        successAlert(response);
    }

    @Override
    public void onGetDailyWiseSlotsError(String res) {
    }

    @Override
    public void onVisible() {
        refresh();
    }

    @Override
    public void onDestroy() {
        utils.hideProgress();
        super.onDestroy();
    }
}
