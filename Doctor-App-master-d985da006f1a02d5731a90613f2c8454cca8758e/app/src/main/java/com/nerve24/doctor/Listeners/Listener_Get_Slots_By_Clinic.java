package com.nerve24.doctor.Listeners;


import java.util.ArrayList;
import java.util.Map;

public interface Listener_Get_Slots_By_Clinic
{
    public void onGetSlots(ArrayList<String> timeList, Map<String,String> map);
    public void onGetSlotsError(String res);
}
