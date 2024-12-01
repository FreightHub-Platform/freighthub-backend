package com.freighthub.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LocationPoint implements Serializable {
    private double lat;
    private double lng;

    public LocationPoint() {
        // Default constructor required for Jackson
    }

    public LocationPoint(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
