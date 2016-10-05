package com.nerve24.doctor.Listeners;


import com.nerve24.doctor.pojo.Clinic;

import java.util.ArrayList;

public interface Listener_Get_Clinics
{
    public void onGetClinics(ArrayList<Clinic> clinicsList);
    public void onGetClinicsError(String res);
}
