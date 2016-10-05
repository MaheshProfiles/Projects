package com.nerve24.doctor.pojo;

import java.io.Serializable;
import java.util.ArrayList;

public class DailyWiseSlot implements Serializable {
    public String day;
    public ArrayList<DailyWiseSlotDay> dailyWiseSlotlist;

    public DailyWiseSlot(String day, ArrayList<DailyWiseSlotDay> dailyWiseSlotlist) {
        this.day = day;
        this.dailyWiseSlotlist = dailyWiseSlotlist;
    }
}
