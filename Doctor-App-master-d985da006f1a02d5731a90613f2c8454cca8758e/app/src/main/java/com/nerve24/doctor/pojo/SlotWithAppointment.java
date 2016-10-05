package com.nerve24.doctor.pojo;

import java.io.Serializable;
import java.util.ArrayList;

public class SlotWithAppointment implements Serializable {
    public String startTime, endTime, premium, checked, slotId, parentId,clinicId,clinicName;
    public ArrayList<Appointment> appointmentsList;

    public SlotWithAppointment(String startTime, String endTime, String premium,
                               String checked, String slotId,
                               String parentId,ArrayList<Appointment> appointmentsList,
                               String clinicId,String clinicName) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.premium = premium;
        this.checked = checked;
        this.slotId = slotId;
        this.parentId = parentId;
        this.appointmentsList=appointmentsList;
        this.clinicId=clinicId;
        this.clinicName=clinicName;
    }

}
