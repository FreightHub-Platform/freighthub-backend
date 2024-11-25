package com.freighthub.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LocationPoint implements Serializable {
    private double lat;
    private double lng;

    public LocationPoint(Double latitude, Double longitude) {
        this.lat = latitude;
        this.lng = longitude;
    }
}
