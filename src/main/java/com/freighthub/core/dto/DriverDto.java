package com.freighthub.core.dto;

import com.freighthub.core.enums.Availability;
import com.freighthub.core.enums.UserRole;
import com.freighthub.core.enums.VehicleOwnership;
import com.freighthub.core.enums.VerifyStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.locationtech.jts.geom.Point;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.freighthub.core.entity.Driver}
 */
@Value
@Getter
@Setter
public class DriverDto implements Serializable {
    Integer id;
    String username;
    UserRole role;
    String nic;
    String nicFrontPic;
    String nicRearPic;
    String licenseNumber;
    boolean hasExpire;
    LocalDate licenseExpiry;
    String fName;
    String lName;
    String contactNumber;
    String profilePic;
    String liFrontPic;
    String liRearPic;
    Point hometownLocation;
    String addressLine1;
    String addressLine2;
    String street;
    String city;
    String province;
    String postalCode;
    String billingProof;
    VehicleOwnership ownership;
    VerifyStatus verifyStatus;
    Availability availability;
    Integer completion;
    Integer fleetOwnerId;
    Integer vTypeId;
    Integer reviewBoardId;
    LocalDateTime verifyTime;
}