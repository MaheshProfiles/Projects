package com.nerve24.doctor.pojo;

import java.io.Serializable;

/**
 * Created by selva on 7/7/2016.
 */
public class DailyWiseSlotDay implements Serializable {
    public String clinicName, clinicID, slotsId, startTime, endTime, day;

    public DailyWiseSlotDay(String clinicName, String clinicID, String slotsId, String startTime, String endTime, String day) {
        this.clinicName = clinicName;
        this.clinicID = clinicID;
        this.slotsId = slotsId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
    }
}
