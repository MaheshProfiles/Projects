package com.nerve24.doctor.pojo;


import java.io.Serializable;

public class SlotFee implements Serializable {

    public String slotId, userName, startTime, endTime, duration, fee, premium, clinicId, parentId, day, override, slotSetupId, checked;

    public SlotFee(String slotId, String userName, String startTime, String endTime, String duration, String fee, String premium,
                   String clinicId, String parentId, String day, String override, String slotSetupId, String checked) {
        this.slotId = slotId;
        this.userName = userName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.fee = fee;
        this.premium = premium;
        this.clinicId = clinicId;
        this.parentId = parentId;
        this.day = day;
        this.override = override;
        this.slotSetupId = slotSetupId;
        this.checked = checked;
    }

}
