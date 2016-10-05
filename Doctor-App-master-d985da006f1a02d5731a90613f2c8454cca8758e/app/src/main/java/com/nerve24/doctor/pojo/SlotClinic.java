package com.nerve24.doctor.pojo;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class SlotClinic implements Serializable {
    public String slotSetupId, userName, clinicName, clinicId, locality;
    public HashMap<String,ArrayList<SlotDay>> timingSheduleMap=new HashMap<>();

    public SlotClinic(String slotSetupId, String userName, String clinicName, String clinicId
            , String locality, HashMap<String,ArrayList<SlotDay>> timingSheduleMap) {
        this.slotSetupId = slotSetupId;
        this.userName = userName;
        this.clinicName = clinicName;
        this.clinicId = clinicId;
        this.locality = locality;
        this.timingSheduleMap = timingSheduleMap;
    }

}
