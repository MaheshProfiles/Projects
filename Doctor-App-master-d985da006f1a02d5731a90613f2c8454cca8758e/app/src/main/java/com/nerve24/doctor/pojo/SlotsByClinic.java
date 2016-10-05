package com.nerve24.doctor.pojo;

import java.io.Serializable;

/**
 * Created by selva on 7/25/2016.
 */
public class SlotsByClinic implements Serializable {
    public String slotId, startTime;

    public SlotsByClinic(String slotId, String startTime) {
        this.slotId = slotId;
        this.startTime = startTime;
    }
}
