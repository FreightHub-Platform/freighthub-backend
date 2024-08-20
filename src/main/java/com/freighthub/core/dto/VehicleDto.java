package com.freighthub.core.dto;

import com.freighthub.core.enums.Availability;
import com.freighthub.core.enums.VerifyStatus;
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
    Availability availability;
    VerifyStatus verifyStatus;
    String registrationPic;
    String vehiclePic1;
    String vehiclePic2;
    String vehiclePic3;
    String vehiclePic4;
    Integer vTypeId;
    Integer fleetOwnerId;
    Integer driverId;
}