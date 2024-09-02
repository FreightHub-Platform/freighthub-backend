package com.freighthub.core.dto;

import com.freighthub.core.entity.RejectReason;
import com.freighthub.core.enums.Availability;
import com.freighthub.core.enums.VerifyStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link com.freighthub.core.entity.Vehicle}
 */
@Value
@Getter
@Setter
public class VehicleDto implements Serializable {
    Integer id;
    String licenseNo;
    String model;
    String make;
    String year;
    String color;
    Boolean craneFlag;
    Boolean refrigFlag;
    Availability availability;
    VerifyStatus verifyStatus;
    String registrationPic;
    String frontPic;
    String rearPic;
    String side1Pic;
    String side2Pic;
    String trailerImage;
    Integer completion;
    String revenueLicensePic;
    LocalDate licenseExpiry;
    String insurancePic;
    LocalDate insuranceExpiry;
    byte[] registrationDoc;
    Integer vTypeId;
    Integer fleetOwnerId;
    Integer driverId;
    Integer reviewBoardId;
    LocalDateTime verifyTime;
    Set<RejectReason> vehicleRejectReasons;
}