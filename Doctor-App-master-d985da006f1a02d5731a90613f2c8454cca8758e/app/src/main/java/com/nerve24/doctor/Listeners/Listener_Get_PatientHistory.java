package com.nerve24.doctor.Listeners;


import com.nerve24.doctor.pojo.PatientHistory;

import java.util.ArrayList;

public interface Listener_Get_PatientHistory
{
    public void onGetPatientHistory(ArrayList<PatientHistory> historyArrayList);
    public void onGetPatientHistoryError(String res);

}
