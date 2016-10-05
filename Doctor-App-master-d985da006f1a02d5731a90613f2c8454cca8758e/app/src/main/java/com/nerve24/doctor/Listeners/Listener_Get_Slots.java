package com.nerve24.doctor.Listeners;


import com.nerve24.doctor.pojo.SlotClinic;

import java.util.ArrayList;

public interface Listener_Get_Slots
{
    public void onGetSlots(ArrayList<SlotClinic> slotClinics);
    public void onGetSlotsError(String res);
}
