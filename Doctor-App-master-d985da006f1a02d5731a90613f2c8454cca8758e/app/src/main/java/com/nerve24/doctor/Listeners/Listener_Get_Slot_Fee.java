package com.nerve24.doctor.Listeners;


import com.nerve24.doctor.pojo.SlotFee;

import org.json.JSONArray;

import java.util.ArrayList;

public interface Listener_Get_Slot_Fee
{
    public void onGetSlotFee(ArrayList<SlotFee> slotFeesList, JSONArray jsonArray);
    public void onGetSlotFeeError(String res);
}
