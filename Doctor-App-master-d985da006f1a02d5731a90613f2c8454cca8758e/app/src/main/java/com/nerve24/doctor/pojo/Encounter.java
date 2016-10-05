package com.nerve24.doctor.pojo;

import java.io.Serializable;

public class Encounter implements Serializable {
    public String id, groupName, value, description;

    public Encounter(String id, String groupName, String value, String description) {
        this.id = id;
        this.groupName = groupName;
        this.value = value;
        this.description = description;
    }
}
