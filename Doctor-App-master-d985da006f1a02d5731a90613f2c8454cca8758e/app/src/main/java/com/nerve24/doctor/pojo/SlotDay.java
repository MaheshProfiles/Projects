package com.nerve24.doctor.pojo;


import java.io.Serializable;

public class SlotDay implements Serializable {
    public String slotId, startTime, endTime, duration, fee, premium, checked, parentId,startEndTime;

    public SlotDay(String slotId, String startTime, String endTime, String duration
            , String fee, String premium, String checked, String parentId, String startEndTime) {
        this.slotId = slotId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.fee = fee;
        this.premium = premium;
        this.checked=checked;
        this.parentId = parentId;
        this.startEndTime=startEndTime;
    }

}
