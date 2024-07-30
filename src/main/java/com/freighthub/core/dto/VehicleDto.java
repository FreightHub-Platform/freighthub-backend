package com.freighthub.core.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.freighthub.core.entity.Vehicle}
 */
@Value
public class VehicleDto implements Serializable {
    Integer id;
    String licenseNo;
    String model;
    String make;
    Boolean availability;
    Boolean verifyStatus;
    String registrationPic;
    String vehiclePic1;
    String vehiclePic2;
    String vehiclePic3;
    String vehiclePic4;
    int vTypeId;
    int fleetOwnerId;
    int driverId;
}