package com.nerve24.doctor.Listeners;


import com.nerve24.doctor.pojo.SlotWithAppointment;

import java.util.ArrayList;
import java.util.HashMap;

public interface Listener_Get_Slots_with_Appointments
{
    public void onGetSlots(ArrayList<SlotWithAppointment> slotWithAppointments);
    public void onGetSlots(HashMap<String,ArrayList<SlotWithAppointment>> slotWithAppointmentsMap);
    public void onGetSlotsError(String res);
}
