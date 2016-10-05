package com.nerve24.doctor.Listeners;


import com.nerve24.doctor.pojo.Nerver24Member;

import java.util.ArrayList;
import java.util.Map;

public interface Listener_Patient_Search {
    public void onGetPatients(ArrayList<String> nameList, Map<String, String> idNameMap, Map<String, Nerver24Member> patientSearchMap);

    public void onGetPatientsError(String res);
}
