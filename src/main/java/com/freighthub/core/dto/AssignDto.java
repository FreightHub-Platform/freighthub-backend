package com.freighthub.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AssignDto implements Serializable {
    private int driverId;
    private int vehicleId;

    // No-argument constructor needed for deserialization
    public AssignDto() {}

    // Constructor with ID for convenience
    public AssignDto(int driverId, int vehicleId) {
        this.driverId = vehicleId;
        this.vehicleId = vehicleId;
    }
}
