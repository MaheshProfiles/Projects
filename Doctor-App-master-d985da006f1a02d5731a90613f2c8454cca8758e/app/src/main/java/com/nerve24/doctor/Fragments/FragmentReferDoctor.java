package com.nerve24.doctor.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nerve24.doctor.Activities.ActivityRefer;
import com.nerve24.doctor.ApiTask.API_Get_Doctor;
import com.nerve24.doctor.ApiTask.API_Get_DoctorLocality;
import com.nerve24.doctor.ApiTask.API_Refer_Doctor;
import com.nerve24.doctor.Listeners.Listener_Doctor_Locality;
import com.nerve24.doctor.Listeners.Listener_Doctor_Search;
import com.nerve24.doctor.Listeners.Listener_Refer_Doctor;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.InstantAutoCompleteTextView;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.DoctorLocality;
import com.nerve24.doctor.pojo.Nerver24Member;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FragmentReferDoctor extends Fragment implements View.OnClickListener, Listener_Refer_Doctor,
        Listener_Doctor_Search, Listener_Doctor_Locality {
    private String TAG = "", MODULE = "FragmentReferDoctor";
    private Activity mActivity;
    private Utils utils;
    private Session session;
    private TextView tvTemplate;
    private InstantAutoCompleteTextView actDoctor, actLocation;
    private EditText etNote;
    private Button btnCancel, btnRefer;
    private List<Nerver24Member> doctorList;
    private ArrayList<String> referedDoctorList = new ArrayList<>();
    private Map<String, String> referedDoctorMap = new HashMap();
    private ArrayList<String> locationList = new ArrayList<>();
    private Map<String, String> locationMap = new HashMap();
    private String locationID = "", doctorId = "", note = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_refer_doctor, container, false);
        initUI(rootView);
        return rootView;
    }

    private void initUI(View view) {

        TAG = "initUI";
        mActivity = getActivity();
        utils = new Utils(mActivity);
        session = new Session(mActivity);
        ((ActivityRefer) getActivity()).setTitle("Refer to doctor");

        tvTemplate = (TextView) view.findViewById(R.id.tv_template);
        actDoctor = (InstantAutoCompleteTextView) view.findViewById(R.id.atv_search_by_doctor);
        actLocation = (InstantAutoCompleteTextView) view.findViewById(R.id.atv_filter_by_location);
        etNote = (EditText) view.findViewById(R.id.et_note);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnRefer = (Button) view.findViewById(R.id.btn_save);

        initListeners();
        getValues();
    }


    private void getValues() {
        try {
            doctorList = session.getDoctorList();
            if (doctorList.size() == 0) {
                utils.showProgress("", "loading..");
                new API_Get_Doctor(mActivity).getDoctors();
            } else {
                setDoctorList();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDoctorList() {
        for (Nerver24Member obj :
                doctorList) {
            referedDoctorList.add(obj.fullName);
            referedDoctorMap.put(obj.fullName, obj.nerve24Id);
        }
        ArrayAdapter<String> adapterDoctor = new ArrayAdapter<>(
                mActivity, android.R.layout.simple_dropdown_item_1line, referedDoctorList);
        actDoctor.setAdapter(adapterDoctor);
    }


    private void initListeners() {
        btnCancel.setText("Cancel");
        btnRefer.setText("Refer");
        tvTemplate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnRefer.setOnClickListener(this);
        actDoctor.addTextChangedListener(twDoctor);
        actLocation.addTextChangedListener(twLocality);
        tvTemplate.setFocusableInTouchMode(true);
        tvTemplate.requestFocus();

        actDoctor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    actDoctor.showDropDown();
                }
            }
        });

        actLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    actLocation.showDropDown();
                }
            }
        });

        actDoctor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                doctorId = referedDoctorMap.get(selection);
                getLocation();
            }
        });

        actLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                locationID = locationMap.get(selection);
            }
        });

    }

    private void getLocation() {
         try
         {
             utils.showProgress("","loading..");
             API_Get_DoctorLocality getDoctorLocality=new API_Get_DoctorLocality(doctorId,FragmentReferDoctor.this,mActivity);
             getDoctorLocality.getLocality();
         }
         catch (Exception e)
         {
             e.printStackTrace();
         }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_template:
                selectTemplate();
                break;
            case R.id.btn_save:
                referProcess();
                break;
            case R.id.btn_cancel:

                break;

        }
    }

    private void selectTemplate() {

    }

    @Override
    public void onGetDoctors(ArrayList<Nerver24Member> doctorList) {
        utils.hideProgress();
        this.doctorList = doctorList;
        setDoctorList();
        actDoctor.performClick();
    }

    @Override
    public void onGetDoctors(ArrayList<String> nameList, Map<String, String> idNameMap, Map<String, Nerver24Member> patientSearchMap) {

    }

    @Override
    public void onGetDoctorsError(String res) {
        utils.hideProgress();
    }

    @Override
    public void onGetDoctorsLocalityError(String res) {
        utils.hideProgress();
    }

    @Override
    public void onGetDoctorsLocality(ArrayList<DoctorLocality> list) {
        utils.hideProgress();
        locationList.clear();
        locationMap.clear();

        for (DoctorLocality doctorLocality : list) {
            locationList.add(doctorLocality.addressBlock);
            locationMap.put(doctorLocality.addressBlock, doctorLocality.locality);
        }
        setLocation();
    }

    private void setLocation() {
        ArrayAdapter<String> adapterDoctor = new ArrayAdapter<>(
                mActivity, android.R.layout.simple_dropdown_item_1line, locationList);
        actLocation.setAdapter(adapterDoctor);
    }

    private boolean validation() {
        boolean flag = false;
        note = etNote.getText().toString().trim();
        if (locationID.length() == 0)
            actLocation.setError("Select location!");
        else if (doctorId.length() == 0)
            actDoctor.setError("Select doctor!");
        else if (note.length() == 0)
            etNote.setError("Note is empty!");
        else
            flag = true;

        return flag;
    }

    private void referProcess() {
        try {
            if (validation()) {
              /*  "doctorMediportID":"NV4SU3000B",
                        "location":1,
                        "noteToDoctor":"abcd efgh ",
                        "eopdID":12*/
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("noteToDoctor", note);
                jsonObject.put("location", Integer.parseInt(locationID));
                jsonObject.put("doctorMediportID", doctorId);
                jsonObject.put("eopdID", 12);

                utils.showProgress("", "loading..");
                API_Refer_Doctor apiReferDoctor = new API_Refer_Doctor(jsonObject, FragmentReferDoctor.this, mActivity);
                apiReferDoctor.refer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    TextWatcher twLocality = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = actLocation.getText().toString().trim();
            if (str.length() == 0) {
                locationID = "";
                actLocation.setError("Select location!");
            } else {
                actLocation.setError(null);
            }
        }
    };

    TextWatcher twDoctor = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String str = actDoctor.getText().toString().trim();
            if (str.length() == 0) {
                doctorId = "";
                actDoctor.setError("Select doctor!");
            } else {
                actLocation.setError(null);
            }
        }
    };


    @Override
    public void onReferred(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }

    @Override
    public void onReferredFailure(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }
}


