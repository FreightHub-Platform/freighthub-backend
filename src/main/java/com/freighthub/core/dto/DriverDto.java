package com.freighthub.core.dto;

import com.freighthub.core.enums.Availability;
import com.freighthub.core.enums.UserRole;
import com.freighthub.core.enums.VerifyStatus;
import lombok.Value;
import org.locationtech.jts.geom.Point;

import java.io.Serializable;

/**
 * DTO for {@link com.freighthub.core.entity.Driver}
 */
@Value
public class DriverDto implements Serializable {
    Integer id;
    String username;
    UserRole role;
    String nic;
    String licenseNumber;
    String fName;
    String lName;
    String contactNumber;
    String liFrontPic;
    String liRearPic;
    Point hometownLocation;
    String addressLine1;
    String addressLine2;
    String city;
    String province;
    String postalCode;
    VerifyStatus verifyStatus;
    Availability availability;
    Integer completion;
    Integer fleetOwner;
}