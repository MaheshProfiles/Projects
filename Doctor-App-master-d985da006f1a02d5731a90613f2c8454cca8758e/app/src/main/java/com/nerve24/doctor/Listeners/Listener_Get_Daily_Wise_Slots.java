package com.nerve24.doctor.Listeners;


import com.nerve24.doctor.pojo.DailyWiseSlot;

import java.util.ArrayList;

public interface Listener_Get_Daily_Wise_Slots
{
    public void onGetDailyWiseSlots(ArrayList<DailyWiseSlot> slotList);
    public void onGetDailyWiseSlotsError(String res);
}
