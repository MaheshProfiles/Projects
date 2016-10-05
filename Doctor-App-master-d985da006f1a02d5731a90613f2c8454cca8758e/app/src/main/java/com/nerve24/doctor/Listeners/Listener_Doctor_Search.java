package com.nerve24.doctor.Listeners;

import com.nerve24.doctor.pojo.Nerver24Member;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by mahesha on 12-08-16.
 */
public interface Listener_Doctor_Search {
    public void onGetDoctors(ArrayList<String> nameList, Map<String, String> idNameMap, Map<String, Nerver24Member> patientSearchMap);

    public void onGetDoctorsError(String res);

    public void onGetDoctors(ArrayList<Nerver24Member> doctorList);
}
