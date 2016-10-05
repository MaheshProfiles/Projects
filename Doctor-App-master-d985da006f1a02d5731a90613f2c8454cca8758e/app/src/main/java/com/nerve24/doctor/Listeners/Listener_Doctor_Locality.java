package com.nerve24.doctor.Listeners;

import com.nerve24.doctor.pojo.DoctorLocality;

import java.util.ArrayList;

/**
 * Created by mahesha on 12-08-16.
 */
public interface Listener_Doctor_Locality {

    public void onGetDoctorsLocalityError(String res);

    public void onGetDoctorsLocality(ArrayList<DoctorLocality> localityList);
}
