package com.nerve24.doctor.pojo;

import java.io.Serializable;

public class AppointmentFor implements Serializable {
    public String id, groupName, value, description;

    public AppointmentFor(String id, String groupName, String value, String description) {
        this.id = id;
        this.groupName = groupName;
        this.value = value;
        this.description = description;
    }
}
