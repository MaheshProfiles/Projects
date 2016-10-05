package com.nerve24.doctor.pojo;

import java.io.Serializable;

public class Episode implements Serializable {

    public String episodeType, id;

    public Episode(String episodeType, String id) {
        this.episodeType = episodeType;
        this.id = id;
    }
}
